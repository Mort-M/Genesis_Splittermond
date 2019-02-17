/**
 *
 */
package org.prelle.splimo.charctrl4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

	private final static ResourceBundle RES = SpliMoCharGenConstants.RES;

	private static Logger logger = LogManager.getLogger("splittermond.chargen.power");

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
		pointsLeft      = pointsToSpend;

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
				boolean success = available.remove(power);
				logger.debug("Remove from available: "+power+" was successful = "+success);
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
					logger.trace(power+" is available now");
				available.add(power);
				added.add(power);
			}
		}
		logger.info("Available size is "+available.size());
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

		// Can character afford power?
		if (getPointsLeft() < power.getCost() && model.getExperienceFree()<(power.getCost()*7))
			return false;


		return true;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.PowerController#canBeDeselected(org.prelle.splimo.PowerReference)
	 */
	@Override
	public boolean canBeDeselected(PowerReference ref) {
		if (!model.getPowers().contains(ref))
			return false;

		// Cannot deselect powers from race selection
		return !ref.isFixed();
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
		if (!canBeSelected(power)) {
			logger.warn("Trying to select power that cannot be selected: "+power);
			return null;
		}
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

		if (!model.getPowers().contains(ref))
			return false;

		return ref.getCount()>0;
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
	/**
	 * @see org.prelle.splimo.processor.SpliMoCharacterProcessor#process(org.prelle.splimo.SpliMoCharacter, java.util.List)
	 */
	@Override
	public List<Modification> process(SpliMoCharacter model, List<Modification> previous) {
		List<Modification> unprocessed = new ArrayList<>();

		logger.trace("START: process");
		try {
			todos.clear();

			/*
			 * Clear all powers that are not user selected
			 */
			for (PowerReference ref : new ArrayList<>(model.getPowers())) {
				if (ref.isSystemAssigned()) {
					if (ref.getCount()==0) {
						logger.trace("  clear system assigned power "+ref);
						model.removePower(ref);
					} else if (!ref.getPower().canBeUsedMultipleTimes()){
						ref.clearModifications();
						logger.trace("   remove all modifications from user selected "+ref);
					}
				}
			}

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
						if (power.canBeUsedMultipleTimes() && ref.getModifiedCount()>0) {
							logger.debug(" * increase power '"+power.getId()+" +1' from "+pMod.getSource());
						} else {
							logger.debug(" * Ignore "+pMod+" from "+pMod.getSource()+" because it cannot be selected multiple times");
						}
						ref.setSystemAssigned(true);
						if (pMod.getSource()!=null && pMod.getSource() instanceof Race)
							ref.setFixed(true);
					} else {
						PowerReference toAdd = new PowerReference(power, 0);
						if (power.canBeUsedMultipleTimes())
							toAdd.setCount(1);
						if (pMod.getSource()!=null && pMod.getSource() instanceof Race)
							toAdd.setFixed(true);
						logger.debug(" * Add power '"+toAdd+"  from "+pMod.getSource());
						toAdd.setSystemAssigned(true);
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
			List<PowerReference> powers = model.getPowers();
			logger.trace("  Powers unsorted = "+powers);
			// Sort power in a way that expensive powers are first, allowing
			// them to be payed with GP
			Collections.sort(powers, new Comparator<PowerReference>() {
				public int compare(PowerReference o1, PowerReference o2) {
					int cmp = (Integer.valueOf(o1.getPower().getCost()*o1.getModifiedCount())).compareTo(o2.getPower().getCost()*o2.getModifiedCount());
					if (cmp!=0) return cmp;
					return o1.getPower().getName().compareTo(o2.getPower().getName());
				}
			});
			logger.trace("  Powers sorted   = "+powers);
			int expInvested = 0;
			for (PowerReference ref : powers) {
				logger.debug("* "+ref);
				int cost = ref.getPower().getCost();
				if (ref.getModifiedCount()>0)
					cost *= ref.getModifiedCount();
				int expCost = cost*7;
				if (cost<=pointsLeft) {
					logger.debug(" Invest "+cost+" points for "+ref);
					pointsLeft -= cost;
				} else if (expCost<model.getExperienceFree()) {
					logger.debug(" Invest "+expCost+" EP for "+ref);
					model.setExperienceFree( model.getExperienceFree() - expCost );
					model.setExperienceInvested( model.getExperienceInvested() + expCost );
					PowerModification mod = new PowerModification(ref.getPower());
					mod.setExpCost(expCost);
					model.addToHistory(mod);
					expInvested += expCost;
				} else {
					logger.error("There are neither "+cost+" GP nor "+expCost+" EP left to pay the power "+ref+" - removing it");
					model.removePower(ref);
				}
			}
			// Calculate needed exp

			logger.debug("  Invested "+(pointsMax-pointsLeft)+" GP and "+expInvested+" EP for powers");

			/*
			 * Check all points are spent
			 */
			if (pointsLeft>0)
				todos.add(new ToDoElement(Severity.STOPPER, String.format(RES.getString("powergen.todo"), getPointsLeft())));
			if (expInvested>0)
				todos.add(new ToDoElement(Severity.INFO, String.format(RES.getString("powergen.todo.experience"), expInvested)));

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
