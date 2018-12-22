/**
 * 
 */
package org.prelle.splimo.charctrl4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.prelle.splimo.Power;
import org.prelle.splimo.Power.SelectionType;
import org.prelle.splimo.PowerReference;
import org.prelle.splimo.Race;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.modifications.AttributeModification;
import org.prelle.splimo.modifications.PowerModification;
import org.prelle.splimo.modifications.SkillModification;
import org.prelle.splimo.processor.SpliMoCharacterProcessor;
import org.prelle.splimo.requirements.Requirement;

import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.modification.DecisionToMake;
import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class NewPowerGenerator implements PowerController, Generator, SpliMoCharacterProcessor {

	private static Logger logger = Logger.getLogger("splittermond.chargen.power");

	private final static ResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splittermond/chargen");

	private Map<PowerReference, Stack<PowerModification>> powerUndoStack;

	private SpliMoCharacter model;
	private SplitterEngineCharacterGenerator charGen;
	/** up to date list of available powers */
	private List<Power> available;
	private List<ToDoElement> todos;
	private List<DecisionToMake> decisions;

	private int pointsMax;
	private int pointsLeft;

	//--------------------------------------------------------------------
	/**
	 * @param undoList Undo list for all controllers
	 */
	public NewPowerGenerator(SplitterEngineCharacterGenerator charGen, int pointsToSpend) {
		this.model      = charGen.getModel();
		this.pointsMax  = pointsToSpend;
		this.charGen    = charGen;
		available       = new ArrayList<Power>();
		todos = new ArrayList<>();
		decisions = new ArrayList<>();

		powerUndoStack = new HashMap<PowerReference, Stack<PowerModification>>();
		for (PowerReference key : model.getPowers())
			powerUndoStack.put(key, new Stack<PowerModification>());

		updateAvailable();
	}

	//--------------------------------------------------------------------
	/**
	 * By default all powers are available. Sort out those that
	 * - are already selected and
	 *   - can only be selected once
	 *   - can be selected multiple times but not yet
	 * - the user does not have the points to pay
	 */
	private void updateAvailable() {
		logger.debug("Update list of available powers");
		
		List<Power> added = new ArrayList<Power>();
		List<Power> removed = new ArrayList<Power>();
		/*
		 * Walk through all previous available powers and test if
		 * they are still selectable
		 */
		for (Power power : new ArrayList<Power>(available)) {
			if (!canBeSelected(power)) {
				if (logger.isTraceEnabled())
					logger.trace(power+" is not available anymore");
				available.remove(power);
				removed.add(power);
			}
		}
		
		/* 
		 * Now walk through all powers and test if one is now
		 * selectable
		 */
		for (Power power : SplitterMondCore.getPowers()) {
			// Don't list culture lore here  #1901
			// Use CultureLoreController for that
			if (power.getId().equals("loreculture"))
				continue;
			if (!available.contains(power) && canBeSelected(power)) {
				if (logger.isTraceEnabled())
					logger.trace(power+" is available anymore");
				available.add(power);
				added.add(power);
			}
		}
		
		if (!removed.isEmpty())
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.POWER_AVAILABLE_REMOVED, removed));
		if (!added.isEmpty())
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.POWER_AVAILABLE_ADDED, added));
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.PowerController#canBeSelected(org.prelle.splimo.Power)
	 */
	@Override
	public boolean canBeSelected(Power power) {
		// Can character afford power?
		if (getPointsLeft() < power.getCost())
			return false;

//		logger.debug("canBeSelected("+power+")  hasPower="+model.hasPower(power));
		// Check if already selected
		if (model.hasPower(power)) {
			// Already selected
			PowerReference exist = model.getPower(power);
			SelectionType type = power.getSelectable();
			if (type==null)
				return false;
			switch (type) {
			case GENERATION:
			case ALWAYS:
			case LEVEL:
				return false;
			case MAX3:
				if (exist.getCount()>=3)
					return false;
				break;
			default:
			}
		}
		
		/*
		 * Check requirements
		 */
		for (Requirement req : power.getRequirement()) {
			if (!model.meetsRequirement(req)) {
				logger.trace("  requirement "+req+" not met");
				return false;
			}
		}
		
		return true;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.PowerController#canBeDeselected(org.prelle.splimo.PowerReference)
	 */
	@Override
	public boolean canBeDeselected(PowerReference ref) {
		Stack<PowerModification> stack = powerUndoStack.get(ref);
		
		switch (ref.getPower().getSelectable()) {
		case ALWAYS:
		case GENERATION:
			if (stack==null || stack.isEmpty())
				return false;
			for (PowerModification pMod : stack) {
				if (pMod.getSource()==null || !(pMod.getSource() instanceof Race) )
					return true;
			}
			return false;
		default:
			return false;
			//			return ref.getCount()<=1 && canBeDecreased(ref);
		}
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.PowerController#getAvailablePowers()
	 */
	@Override
	public List<Power> getAvailablePowers() {
		return available;
	}

	//--------------------------------------------------------------------
	private void correctReferenceModifications(PowerReference ref) {
		ref.getModifications().clear();
		
		Power power = ref.getPower();
		// Copy modifications from power to power reference. This allows to stack them
		for (Modification mod : power.getModifications()) {
			if (mod instanceof AttributeModification) {
				AttributeModification aMod = ((AttributeModification)mod).clone();
				aMod.setValue(aMod.getValue()*ref.getCount());
				ref.getModifications().add(aMod );
				aMod.setSource(ref);
			} else if (mod instanceof SkillModification) {
				SkillModification sMod = ((SkillModification)mod).clone();
				sMod.setValue(sMod.getValue()*ref.getCount());
				ref.getModifications().add(sMod );
				sMod.setSource(ref);
			} else {
				ref.getModifications().add(mod); 
			}
		}
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.PowerController#select(org.prelle.splimo.Power)
	 */
	@Override
	public PowerReference select(Power power) {
		int expNeeded = power.getCost();
		if (getPointsLeft()<expNeeded) 
			return null;

		if (model.hasPower(power)) {
			// Already selected
			PowerReference ref = model.getPower(power);
			if (!canBeIncreased(ref))
				return null;
			increase(ref);
			return ref;
			//		} else {
			//			// Power not selected yet
			//			if (type==SelectionType.GENERATION)
			//				return null;
		}

		PowerReference ref = new PowerReference(power);
		ref.setCount(1);
		correctReferenceModifications(ref);


		model.addPower(ref);
		logger.info("Selected power "+power);

		// make it undoable
		PowerModification mod = new PowerModification(power);
		mod.setExpCost(power.getCost());
		powerUndoStack.put(ref, new Stack<PowerModification>());
		powerUndoStack.get(ref).push(mod);

		updateAvailable();

		charGen.runProcessors();
//		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.POWER_ADDED, ref));
//		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.POINTS_LEFT_POWERS, null, getPointsLeft()));
//
//		applyModification(ref);
		
		return ref;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.PowerController#deselect(org.prelle.splimo.PowerReference)
	 */
	@Override
	public boolean deselect(PowerReference ref) {
		if (canBeDeselected(ref)) {
			// Update model
			powerUndoStack.get(ref).pop();
			logger.info("Deselect "+ref);
			ref.setCount(0);
			model.removePower(ref);

			updateAvailable();

			// Inform listener
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.POWER_REMOVED, ref));
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.POINTS_LEFT_POWERS, null, getPointsLeft()));

			charGen.runProcessors();
		return true;
		}
		return false;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.PowerController#canBeIncreased(org.prelle.splimo.PowerReference)
	 */
	@Override
	public boolean canBeIncreased(PowerReference ref) {
		// Only can increase those that already exist
		if (!model.hasPower(ref.getPower()))
			return false;
		
		SelectionType type = ref.getPower().getSelectable();
		if (type==null)
			return false;

		int expNeeded = ref.getPower().getCost();
		if (expNeeded>getPointsLeft())
			return false;

		switch (type) {
		case GENERATION:
			return false;
		case MULTIPLE:
			return true;
		case LEVEL:
			return false;
		case MAX3:
			return ref.getCount()<3;
		default:
			return false;
		}
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.PowerController#canBeDecreased(org.prelle.splimo.PowerReference)
	 */
	@Override
	public boolean canBeDecreased(PowerReference ref) {
		logger.info("canBeDecreased("+ref+")");

		Stack<PowerModification> stack = powerUndoStack.get(ref);

		//		switch (ref.getPower().getSelectable()) {
//		case ALWAYS:
//		case GENERATION:
//			return false;
//		default:
//			logger.debug(" powerUndoStack  = "+powerUndoStack);
//			logger.debug(" powerUndoStack2 = "+powerUndoStack.containsKey(ref));
//			logger.debug(" powerUndoStack3 = "+powerUndoStack.get(ref));
			if (stack==null || stack.isEmpty())
				return false;
			for (PowerModification pMod : stack) {
				if (pMod.getSource()==null || !(pMod.getSource() instanceof Race) )
					return ref.getCount()>0;
			}
			return ref.getCount()>1;
//		}
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.PowerController#increase(org.prelle.splimo.PowerReference)
	 */
	@Override
	public boolean increase(PowerReference ref) {
		int newVal = ref.getCount()+1;

		// Determine how much exp is needed
		int expNeeded = ref.getPower().getCost();

		if (canBeIncreased(ref)) {
			// Update model
			logger.info("Increase "+ref+" from "+ref.getCount()+" to "+newVal);
			ref.setCount(newVal);
			correctReferenceModifications(ref);
			// Add to undo list
			PowerModification mod = new PowerModification(ref.getPower());
			mod.setExpCost(expNeeded);
			if (!powerUndoStack.containsKey(ref))
				powerUndoStack.put(ref, new Stack<PowerModification>());
			powerUndoStack.get(ref).push(mod);
			updateAvailable();

			// Inform listener
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.POWER_CHANGED, ref));
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.POINTS_LEFT_POWERS, getPointsLeft()));

			charGen.runProcessors();
			return true;
		}
		return false;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.PowerController#decrease(org.prelle.splimo.PowerReference)
	 */
	@Override
	public boolean decrease(PowerReference ref) {
		int newVal = ref.getCount()-1;

		if (canBeDecreased(ref)) {
			// Update model
			powerUndoStack.get(ref).pop();
			logger.info("Decrease "+ref+" from "+ref.getCount()+" to "+newVal);
			ref.setCount(newVal);
			if (newVal==0) {
				model.removePower(ref);
			} else {
				correctReferenceModifications(ref);
			}
			updateAvailable();
			charGen.runProcessors();

			// Inform listener
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.POWER_CHANGED, ref));
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.POINTS_LEFT_POWERS, null, getPointsLeft()));
			
			return true;
		}
		return false;
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
	void addModification(PowerModification pMod) {
		logger.debug("Add modification "+pMod+" // src="+pMod.getSource());

		Power power = pMod.getPower();
		pMod.setExpCost(power.getCost());
		// Check if there already is a PowerReference
		PowerReference ref = model.getPower(pMod.getPower());
		if (ref==null) {
			ref = new PowerReference(pMod.getPower());				
			// Copy modifications from power to power reference. This allows to stack them
			for (Modification mod : power.getModifications()) {
				if (mod instanceof AttributeModification) {
					Modification clone = ((AttributeModification)mod).clone();
					ref.getModifications().add(clone);
					clone.setSource(ref);
				} else if (mod instanceof SkillModification) { 
					Modification clone = ((SkillModification)mod).clone();
					ref.getModifications().add(clone);
					clone.setSource(ref);
				} else {
					ref.getModifications().add(mod);
				}
			}
			
			model.addPower(ref);
			logger.info("Added by modification: "+ref);
			Stack<PowerModification> stack = powerUndoStack.get(ref);
			if (ref!=null) {
				stack = new Stack<PowerModification>();
				powerUndoStack.put(ref, stack);
			}
			stack.push(pMod);
			
			GenerationEvent event = new GenerationEvent(GenerationEventType.POWER_ADDED, ref);
			GenerationEventDispatcher.fireEvent(event);
		} else {

			/*
			 * Power has already been selected. Further actions depend
			 * how often the power can be selected
			 */

			GenerationEvent event = new GenerationEvent(GenerationEventType.POWER_CHANGED, ref);
			switch (power.getSelectable()) {
			case MAX3:
				/*
				 * Allow increasing if maximum is not reached yet.
				 * Otherwise continue fo further cases where one
				 * of the user selections is converted to a system selection
				 */
				if (ref.getCount()<3) {
					ref.setCount(ref.getCount()+1);
					GenerationEventDispatcher.fireEvent(event);
//					// Now distribute modifications attached to the power
//					applyModification(pMod.getPower());
					break;
				}
			case GENERATION:
			case ALWAYS:
			case LEVEL:
//				/* 
//				 * May be only selected once, so reimburse the points spent here.
//				 * For powers that have been user selected, remove them from
//				 * being undoable.
//				 */
//				pointsFree += power.getCost();
//				if (canBeDeselected(ref)) {
//					// Update model
//					powerUndoStack.get(ref).pop();
//					logger.info("Convert user selection to system selection of "+power);
//				}
//				GenerationEventDispatcher.fireEvent(event);
//				GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.POINTS_LEFT_POWERS, pointsFree));
				break;
			case MULTIPLE:			
				ref.setCount(ref.getCount()+1);
				GenerationEventDispatcher.fireEvent(event);
//				// Now distribute modifications attached to the power
//				applyModification(pMod.getPower());
				break;
			}
		}

		// Update list of available selections
		updateAvailable();
		charGen.runProcessors();
	}

	//-------------------------------------------------------------------
	void removeModification(PowerModification pMod) {
		logger.debug("remove modification "+pMod);

		if (!model.hasPower(pMod.getPower())) {
			logger.warn("Remove a power that the model does not have");
			return;
		}

		PowerReference ref = model.getPower(pMod.getPower());
		switch (pMod.getPower().getSelectable()) {
		case MAX3:
		case MULTIPLE:
		case LEVEL:
			if (ref.getCount()>1) {
				ref.setCount(ref.getCount()-1);
				logger.info("Reduced count on power '"+pMod.getPower()+"' to "+ref.getCount());
				GenerationEvent event = new GenerationEvent(GenerationEventType.POWER_CHANGED, ref);
				GenerationEventDispatcher.fireEvent(event);
//				// Now undo modifications attached to the power
//				undoModification(pMod.getPower());
				return;
			}
			// New count would be 0 - same as remove operation below
		default:
			model.removePower(ref);
			GenerationEvent event = new GenerationEvent(GenerationEventType.POWER_REMOVED, ref);
			GenerationEventDispatcher.fireEvent(event);
		}


		//		modifications.remove(pMod);
		//		model.removePower(new PowerReference(pMod.getPower()));
		//		fireChange(pMod.getPower(), false);
		charGen.runProcessors();
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
	 * @see org.prelle.splimo.charctrl4.Controller#getDecisionsToMake()
	 */
	@Override
	public List<DecisionToMake> getDecisionsToMake() {
		return decisions;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl4.Controller#decide(de.rpgframework.genericrpg.modification.DecisionToMake, java.util.List)
	 */
	@Override
	public void decide(DecisionToMake choice, List<Modification> choosen) {
		// Find decision
		if (!decisions.contains(choice))
			throw new IllegalArgumentException("Unknown option: "+choice+"   (origin was "+choice.getChoice().getSource()+")");

		for (Modification mod : choosen)
			mod.setSource(choice.getChoice().getSource());
		logger.info("User decided: "+choice.getChoice()+" => "+choosen);
		choice.setDecision(choosen);
		// Recalculate
		charGen.runProcessors();
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
	 * @see org.prelle.splimo.processor.SpliMoCharacterProcessor#process(org.prelle.splimo.SpliMoCharacter, java.util.List)
	 */
	@Override
	public List<Modification> process(SpliMoCharacter model, List<Modification> previous) {
		List<Modification> unprocessed = new ArrayList<>();

		logger.trace("START: process");
		try {
			todos.clear();
			// All backgrounds are available - remove if not recommended
			available = new ArrayList<>(SplitterMondCore.getPowers());

			/*
			 * Process incoming modifications
			 */
			for (Modification mod : previous) {
				if (mod instanceof PowerModification) {
					PowerModification pMod = (PowerModification)mod;
					Power power = pMod.getPower();
					// If model already has that resource, increase it - otherwise ad
					boolean found = model.hasPower(power);
					if (found) {
						PowerReference ref = model.getPower(power);
						if (power.canBeUsedMultipleTimes() && ref.getModifiedCount()>0)
							logger.debug(" * increase power '"+power.getId()+" +1' from "+pMod.getSource());
						else
							logger.debug(" * add power '"+power.getId()+"' from "+pMod.getSource());
						ref.addModification(pMod);
					} else {
						PowerReference toAdd = new PowerReference(power, 0);
						logger.debug(" * Add power '"+toAdd+"  from "+pMod.getSource());
						toAdd.addModification(pMod);
						model.addPower(toAdd);
					}

				} else {
					unprocessed.add(mod);
				}
			}
			
			/*
			 * Check points spent in powers
			 */
			pointsLeft = pointsMax;
			for (PowerReference ref : model.getPowers()) {
				int cost = ref.getPower().getCost()* ref.getModifiedCount();
				logger.debug(" Invest "+cost+" points for "+ref);
				pointsLeft -= cost;
			}
			logger.debug("  Invested "+(pointsMax-pointsLeft)+" of "+pointsMax+" points for powers");
			
			/*
			 * Check all points are spent
			 */
			if (pointsLeft>0)
				todos.add(new ToDoElement(Severity.STOPPER, String.format(RES.getString("powergen.todo"), getPointsLeft())));
			
			/*
			 * Insert modifications attached to power
			 */
			for (PowerReference ref : model.getPowers()) {
				for (Modification mod : ref.getPower().getModifications()) {
					if (mod instanceof AttributeModification) {
						AttributeModification amod = (AttributeModification)mod;
						AttributeModification toAdd = new AttributeModification(amod.getAttribute(), amod.getValue()*ref.getModifiedCount() );
						toAdd.setModificationSource(amod.getModificationSource());
						toAdd.setSource(ref.getPower());
						unprocessed.add(toAdd);
					} else
						unprocessed.add(mod);
				}
			}
		} finally {
			logger.trace("STOP : process");
		}
		return unprocessed;
	}

}
