/**
 * 
 */
package org.prelle.splimo.charctrl4;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.AttributeValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.charctrl.CharGenConstants;
import org.prelle.splimo.modifications.AttributeModification;
import org.prelle.splimo.processor.SpliMoCharacterProcessor;

import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.modification.DecisionToMake;
import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class NewAttributeGenerator implements AttributeController, Generator, SpliMoCharacterProcessor {
	
	private static Logger logger = LogManager.getLogger("splittermond.chargen.attr");

	private final static ResourceBundle RES = CharGenConstants.RES;

	private SplitterEngineCharacterGenerator parent;
	private SpliMoCharacter model;
	private int pointsForAttributes;
	private int pointsLeft;
	private List<ToDoElement> todos;
	private List<DecisionToMake> decisions;

	//-------------------------------------------------------------------
	/**
	 */
	public NewAttributeGenerator(SplitterEngineCharacterGenerator parent, int points) {
		this.parent = parent;
		this.model = parent.getModel();
		pointsForAttributes = points;
		todos = new ArrayList<>();
		decisions = new ArrayList<>();
		pointsLeft = points;

		for (Attribute attr : Attribute.primaryValues()) {		
			// Value should have average of 2
			AttributeValue val = model.getAttribute(attr);
			val.setDistributed(1);
		}

		// Splinter points
		set(Attribute.SPLINTER, 3);
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.AttributeController#increase(org.prelle.splimo.Attribute)
	 */
	@Override
	public boolean increase(Attribute attrib) {
		if (!canBeIncreased(attrib)) {
			logger.error("Trying to increase "+attrib+" which cannot be increased");
			return false;
		}

		AttributeValue val = model.getAttribute(attrib);
		
		// Change model
		if (pointsLeft>0 && val.getDistributed()<3) {
			val.setDistributed(val.getDistributed()+1);
			logger.info("INCREASE "+attrib+ " with free points");
		} else if (model.getExperienceFree()>=10) {
			val.setDistributed(val.getDistributed()+1);
			logger.info("INCREASE "+attrib+ " with 10 exp");
		} else {
			logger.error("canBeIncreased is true, but don't know what to do");
			return false;
		}

		
		parent.runProcessors();

		return true;
	}

	//-------------------------------------------------------------------
	public boolean decrease(Attribute attrib) {
		if (!canBeDecreased(attrib)) {
			logger.warn("Trying to decrease "+attrib+" which already is at minimum");
			return false;
		}

		AttributeValue val = model.getAttribute(attrib);

		// Modify attribute. 
		logger.info("DECREASE "+attrib);
		val.setDistributed(val.getDistributed()-1);

		parent.runProcessors();

		return true;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.AttributeController#canBeDecreased(org.prelle.splimo.Attribute)
	 */
	@Override
	public boolean canBeDecreased(Attribute key) {
		return key.isPrimary() && model.getAttribute(key).getDistributed()>1;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.AttributeController#canBeIncreased(org.prelle.splimo.Attribute)
	 */
	@Override
	public boolean canBeIncreased(Attribute key) {
		return key.isPrimary() 
				&& (
				(pointsLeft>0 && model.getAttribute(key).getDistributed()<3) || model.getExperienceFree()>=10 
				);
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.Generator#getPointsLeft()
	 */
	@Override
	public int getPointsLeft() {
		return pointsLeft;
	}

	//-------------------------------------------------------------------
	private void set(Attribute attr, int val) {
		AttributeValue data = model.getAttribute(attr);
		data.setDistributed(val);
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.AttributeController#getIncreaseCost(org.prelle.splimo.Attribute)
	 */
	@Override
	public int getIncreaseCost(Attribute key) {
		return 1;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.Controller#getToDos()
	 */
	@Override
	public List<ToDoElement> getToDos() {
		return todos;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl4.Controller#decide(de.rpgframework.genericrpg.modification.DecisionToMake, java.util.List)
	 */
	@Override
	public void decide(DecisionToMake choice, List<Modification> choosen) {
		// Find decision
		if (!decisions.contains(choice))
			throw new IllegalArgumentException("Unknown option: "+choice);

		choice.setDecision(choosen);
		// Recalculate
		parent.runProcessors();
	}

	//-------------------------------------------------------------------
	@Override
	public List<Modification> process(SpliMoCharacter model, List<Modification> previous) {
		List<Modification> unprocessed = new ArrayList<>();

		logger.trace("START: process");
		try {

			todos.clear();
			
			/*
			 * Clear all modifications from previous runs
			 */
			for (Attribute key : Attribute.primaryValues()) {
				model.getAttribute(key).clearModifications();
			}
			model.getAttribute(Attribute.SPLINTER).clearModifications();
			
			/*
			 * Apply modifications
			 */
			for (Modification mod : previous) {
				if (mod instanceof AttributeModification) {
					AttributeModification amod = (AttributeModification)mod;
					if (amod.getAttribute()!=null) {
						logger.debug("  Apply modification "+amod+" from "+mod.getSource());
						model.getAttribute(amod.getAttribute()).addModification(amod);
					} else {
						unprocessed.add(mod);
					}
				} else
					unprocessed.add(mod);
			}

			/*
			 * Calculate points left to spend
			 */
			pointsLeft = pointsForAttributes;
			for (Attribute key : Attribute.primaryValues()) {
				AttributeValue val = model.getAttribute(key);
				int maxWithPoints = Math.min(3, pointsLeft);
				
				if (val.getDistributed()<= maxWithPoints) {
					// Normal case - everything is payed with free points
					pointsLeft -= val.getDistributed();
					val.setStart(val.getValue());
					logger.debug("Invest "+val.getDistributed()+" points in "+key);
				} else if (val.getDistributed() == (maxWithPoints+1)) {
					// One point more invested than possible - pay with exp
					val.setStart((val.getDistributed()-1) + val.getModifier());
					logger.debug("Invest "+(val.getDistributed()-1)+" points and 10 exp in "+key);
					pointsLeft -= val.getDistributed()-1;
					model.setExperienceFree(model.getExperienceFree() - 10);
					model.setExperienceInvested(model.getExperienceInvested() + 10);
					todos.add(new ToDoElement(Severity.INFO, String.format(RES.getString("attrgen.todo.expinvested"), 10, key.getName())));
				} else {
					 // Distributed points more than 1 higher than available points allow - reduce them
					val.setDistributed(pointsLeft+1);
					logger.warn("Corrected "+key+" to "+val.getDistributed()+" points");
					val.setStart(pointsLeft + val.getModifier());
					pointsLeft = 0;
					model.setExperienceFree(model.getExperienceFree() - 10);
					model.setExperienceInvested(model.getExperienceInvested() + 10);
				}
				logger.trace("  Start value of "+key+" is "+val.getStart());
				
			}
			logger.debug("From "+pointsForAttributes+" points have been "+(pointsForAttributes-pointsLeft)+" invested and are "+pointsLeft+" left");
			
			/*
			 * Calculate start value
			 */
			
			if (pointsLeft>0) {
				todos.add(new ToDoElement(Severity.STOPPER, String.format(RES.getString("attrgen.todo"), pointsLeft)));
			} else if (pointsLeft<0) {
				todos.add(new ToDoElement(Severity.STOPPER, String.format(RES.getString("attrgen.todo2"), pointsLeft)));
			}


		} finally {
			logger.trace("STOP : process() ends with "+unprocessed.size()+" modifications still to process");
		}
		return unprocessed;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl4.Controller#getDecisionsToMake()
	 */
	@Override
	public List<DecisionToMake> getDecisionsToMake() {
		return decisions;
	}

}
