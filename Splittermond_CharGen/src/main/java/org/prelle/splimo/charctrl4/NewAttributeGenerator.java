/**
 * 
 */
package org.prelle.splimo.charctrl4;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.AttributeValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventType;
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

	private static Logger logger = Logger.getLogger("splittermond.chargen.attr");

	private final static ResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splittermond/chargen");

	private SplitterEngineCharacterGenerator parent;
	private SpliMoCharacter model;
	private int pointsForAttributes;
	private int pointsLeft;
	private Map<Attribute, Collection<AttributeModification>> modifications;
	private List<ToDoElement> todos;
	private List<DecisionToMake> decisions;

	//-------------------------------------------------------------------
	/**
	 */
	public NewAttributeGenerator(SplitterEngineCharacterGenerator parent, int points) {
		this.parent = parent;
		this.model = ((NewSpliMoCharacterGenerator)parent).getModel();
		pointsForAttributes = points;
		modifications = new HashMap<>();
		todos = new ArrayList<>();
		decisions = new ArrayList<>();

		for (Attribute attr : Attribute.primaryValues()) {		
			// Value should have average of 2
			AttributeValue val = model.getAttribute(attr);
			val.setDistributed(1);
		}

		// Splinter points
		set(Attribute.SPLINTER, 3);

		calculateDerived();
	}

	//-------------------------------------------------------------------
	private void calculateDerived() {
		set(Attribute.SPEED     , get(Attribute.SIZE) + get(Attribute.AGILITY));
		set(Attribute.INITIATIVE,                  10 - get(Attribute.INTUITION));
		set(Attribute.LIFE      , get(Attribute.SIZE) + get(Attribute.CONSTITUTION));
		set(Attribute.FOCUS     , 2*(get(Attribute.MYSTIC) + get(Attribute.WILLPOWER)) );
		set(Attribute.DEFENSE   , 12 + get(Attribute.AGILITY) + get(Attribute.STRENGTH));
		set(Attribute.MINDRESIST, 12 + get(Attribute.MIND) + get(Attribute.WILLPOWER));
		set(Attribute.BODYRESIST, 12 + get(Attribute.CONSTITUTION) + get(Attribute.WILLPOWER));
	}

	//-------------------------------------------------------------------
	public void addModification(AttributeModification mod) {
		logger.debug("addModification");
		Collection<AttributeModification> modList = modifications.get(mod.getAttribute());		
		modList.add(mod);
		model.getAttribute(mod.getAttribute()).addModification(mod);
		GenerationEvent event = new GenerationEvent(GenerationEventType.ATTRIBUTE_CHANGED, mod.getAttribute(), model.getAttribute(mod.getAttribute()));
		GenerationEventDispatcher.fireEvent(event);

		calculateDerived();
	}

	//-------------------------------------------------------------------
	public void removeModification(AttributeModification mod) {
		logger.debug("remove Modification");
		Collection<AttributeModification> modList = modifications.get(mod.getAttribute());		
		modList.remove(mod);
		model.getAttribute(mod.getAttribute()).removeModification(mod);
		GenerationEvent event = new GenerationEvent(GenerationEventType.ATTRIBUTE_CHANGED, mod.getAttribute(), model.getAttribute(mod.getAttribute()));
		GenerationEventDispatcher.fireEvent(event);

		calculateDerived();
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.AttributeController#increase(org.prelle.splimo.Attribute)
	 */
	@Override
	public boolean increase(Attribute attrib) {
		if (!attrib.isPrimary()) {
			logger.debug(attrib+" is not primary");
			return true;
		}

		AttributeValue val = model.getAttribute(attrib);
		if (val.getDistributed()>=3)
			return true;
		if (pointsForAttributes==0)
			return false;

		logger.info("INCREASE "+attrib);

		// Modify attribute. This also fires an event
		set(attrib, val.getDistributed()+1);

		// Update derived values. This also fires eventually events
		calculateDerived();

		parent.runProcessors();

		//		GenerationEvent event = new GenerationEvent(GenerationEventType.POINTS_LEFT_ATTRIBUTES, null, pointsForAttributes);
		//		GenerationEventDispatcher.fireEvent(event);

		return true;
	}

	//	//-------------------------------------------------------------------
	//	private void checkIncreaseButtons() {
	//		if (attribCallback==null) return;
	//		
	//		for (Attribute attr : Attribute.primaryValues()) {
	//			PerAttribute perAttrib = values.get(attr);
	//			attribCallback.setIncreaseButton(attr, (pointsForAttributes>0) && (perAttrib.getDistributed()<3));
	//		}
	//	}

	//-------------------------------------------------------------------
	public boolean decrease(Attribute attrib) {
		if (!attrib.isPrimary())
			return false;

		AttributeValue val = model.getAttribute(attrib);
		if (val.getDistributed()<=0)
			return false;

		logger.info("DECREASE "+attrib);
		// Modify attribute. This also fires an event
		set(attrib, val.getDistributed()-1);

		// Update derived values. This also fires eventually events
		calculateDerived();

		//		GenerationEvent event = new GenerationEvent(GenerationEventType.POINTS_LEFT_ATTRIBUTES, null, pointsForAttributes);
		//		GenerationEventDispatcher.fireEvent(event);

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
		return key.isPrimary() && pointsForAttributes>0 && model.getAttribute(key).getDistributed()<3;
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
	public int get(Attribute attr) {
		return model.getAttribute(attr).getValue();
	}

	//-------------------------------------------------------------------
	void set(Attribute attr, int val) {
		AttributeValue data = model.getAttribute(attr);

		//		int oldVal = data.getValue();
		//		int oldDist= data.getDistributed();

		data.setDistributed(val);
		//		
		//		// Fire event if changed
		//		if (oldDist!=val || oldVal!=data.getValue()) {
		//			logger.debug("Attribute "+attr+" changed");
		//			GenerationEvent event = new GenerationEvent(GenerationEventType.ATTRIBUTE_CHANGED, attr, data);
		//			GenerationEventDispatcher.fireEvent(event);
		//		}
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
	private DecisionToMake findDecision(Modification mod) {
		for (DecisionToMake tmp : decisions) {
			if (tmp.getChoice()==mod)
				return tmp;
		}
		return null;
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
			 * Calculate points left to spend
			 */
			pointsLeft = pointsForAttributes;
			for (Attribute key : Attribute.primaryValues()) {
				AttributeValue val = model.getAttribute(key);
				logger.trace("  "+val.getDistributed()+" for "+key);
				pointsLeft -= val.getDistributed();
				if (val.getDistributed()>3) {
					logger.warn("Too many points distributed in "+key.toString());
				}
			}
			logger.debug("From "+pointsForAttributes+" points have been "+(pointsForAttributes-pointsLeft)+" invested and are "+pointsLeft+" left");
			if (pointsLeft>0) {
				todos.add(new ToDoElement(Severity.STOPPER, String.format(RES.getString("attrgen.todo"), pointsLeft)));
			} else if (pointsLeft<0) {
				todos.add(new ToDoElement(Severity.STOPPER, String.format(RES.getString("attrgen.todo2"), pointsLeft)));
			}

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
