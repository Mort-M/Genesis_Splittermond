/**
 * 
 */
package org.prelle.splimo.levelling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.prelle.splimo.Power;
import org.prelle.splimo.Power.SelectionType;
import org.prelle.splimo.PowerReference;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.PowerController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.modifications.AttributeModification;
import org.prelle.splimo.modifications.PowerModification;
import org.prelle.splimo.modifications.SkillModification;
import org.prelle.splimo.requirements.Requirement;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class PowerLeveller implements PowerController, GenerationEventListener {
	
	private static Logger logger = Logger.getLogger("splittermond.level.resource");

	private List<Modification> undoList;
	private Map<PowerReference, Stack<PowerModification>> powerUndoStack;

	private SpliMoCharacter model;
	private CharacterLeveller charGen;
	/** up to date list of available powers */
	private List<Power> available;
	
	//--------------------------------------------------------------------
	/**
	 * @param undoList Undo list for all controllers
	 */
	public PowerLeveller(SpliMoCharacter model, List<Modification> undoList, CharacterLeveller lvler) {
		this.model = model;
		this.undoList = undoList;
		this.charGen  = lvler;
		available = new ArrayList<Power>();

		powerUndoStack = new HashMap<PowerReference, Stack<PowerModification>>();
		for (PowerReference key : model.getPowers())
			powerUndoStack.put(key, new Stack<PowerModification>());
		
		updateAvailable();
		
		GenerationEventDispatcher.addListener(this);
	}
	
	//--------------------------------------------------------------------
	/**
	 * By default all powers are available. Sort out those that
	 * - are already selected and
	 *   - can only be selected once
	 *   - can be selected multiple times but not yet
	 * - the user does not have the points to pay
	 */
//	private void updateAvailable() {
//		available.clear();
//		for (Power power : SplitterMondCore.getPowers()) {
//			if (canBeSelected(power))
//				available.add(power);
//		}
//		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.POWER_AVAILABLE_ADDED, ref));
//	}

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
		
		Collections.sort(available);
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
		if (power==null)
			return false;
		// Can character afford power?
		if (model.getExperienceFree() < power.getCost()*7)
			return false;
		// Can the power be selected while leveling
		if (power.getSelectable()==SelectionType.GENERATION)
			return false;
		
		// Check if already selected
		if (model.hasPower(power)) {
			// Already selected
			PowerReference exist = model.getPower(power);
			SelectionType type = power.getSelectable();
			if (type==null)
				return false;
			switch (type) {
			case LEVEL:
				if (exist.getCount()>=model.getLevel())
					return false;
				break;
			case MAX3:
				if (exist.getCount()>=3)
					return false;
				break;
			case ALWAYS:
				// Already selected
				return false;
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
		switch (ref.getPower().getSelectable()) {
		case ALWAYS:
		case GENERATION:
			return powerUndoStack.containsKey(ref) && !powerUndoStack.get(ref).isEmpty();
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

	//-------------------------------------------------------------------
	private void applyModification(Power power) {
		logger.info("Distribute modifications attached to the power "+power);
		if (!power.getModifications().isEmpty()) {
			charGen.apply(power.getModifications());
		}
	}

	//-------------------------------------------------------------------
	private void undoModification(Power power) {
		logger.debug("Undo modifications attached to the power "+power);
		if (!power.getModifications().isEmpty()) {
			logger.error("TODO: undo modifications of power: "+power.getModifications());
			charGen.undo(power.getModifications());
		}
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.PowerController#select(org.prelle.splimo.Power)
	 */
	@Override
	public PowerReference select(Power power) {
		int expNeeded = 7 * power.getCost();
		if (model.getExperienceFree()<expNeeded) 
			return null;
		
		Power.SelectionType type = power.getSelectable();
		if (model.hasPower(power)) {
			// Already selected
			PowerReference ref = model.getPower(power);
			if (!canBeIncreased(ref))
				return null;
			increase(ref);
			return ref;
		} else {
			// Power not selected yet
			if (type==SelectionType.GENERATION)
				return null;
		}
		
		PowerReference ref = new PowerReference(power);
		ref.setCount(1);
		
		model.addPower(ref);
		model.setExperienceFree(model.getExperienceFree()-expNeeded);
		model.setExperienceInvested(model.getExperienceInvested()+expNeeded);
		
		logger.info("Selected power "+power);
		
		// make it undoable
		PowerModification mod = new PowerModification(power);
		mod.setExpCost(expNeeded);
		powerUndoStack.put(ref, new Stack<PowerModification>());
		powerUndoStack.get(ref).push(mod);
		undoList.add(mod);
		updateAvailable();

		// Apply modifications attached to power
		applyModification(power);
//		ref.getModifications().addAll(power.getModifications());
		for (Modification mod2 : power.getModifications()) {
			if (mod2 instanceof AttributeModification) {
				Modification clone = ((AttributeModification) mod2).clone();
				ref.getModifications().add(clone);
				clone.setSource(ref);
			} else if (mod2 instanceof SkillModification) { 
				Modification clone = ((SkillModification) mod2).clone();
				ref.getModifications().add(clone);
				clone.setSource(ref);
			} else {
				ref.getModifications().add(mod);
			}
		}
		
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.POWER_ADDED, ref));
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.EXPERIENCE_CHANGED, null, new int[]{
				model.getExperienceFree(),
				model.getExperienceInvested()
		}));
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.UNDO_LIST_CHANGED, undoList));
		
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
			PowerModification mod = powerUndoStack.get(ref).pop();
			logger.info("Deselect "+ref);
			ref.setCount(0);
			model.removePower(ref);
			model.setExperienceFree(model.getExperienceFree()+mod.getExpCost());
			model.setExperienceInvested(model.getExperienceInvested()-mod.getExpCost());
			undoList.remove(mod);
			updateAvailable();
			
			// Undo modifications attched to power
			undoModification(ref.getPower());
			
			// Inform listener
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.POWER_REMOVED, ref));
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.EXPERIENCE_CHANGED, null, new int[]{
					model.getExperienceFree(),
					model.getExperienceInvested()
			}));
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.UNDO_LIST_CHANGED, undoList));
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
		SelectionType type = ref.getPower().getSelectable();
		if (type==null)
			return false;

		int expNeeded = 7 * ref.getPower().getCost();
		if (expNeeded>model.getExperienceFree())
			return false;
				
		switch (type) {
		case GENERATION:
			return false;
		case MULTIPLE:
			return true;
		case LEVEL:
			return ref.getCount()<model.getLevel();
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
		switch (ref.getPower().getSelectable()) {
		case ALWAYS:
		case GENERATION:
			return false;
		default:
			return !powerUndoStack.get(ref).isEmpty();
		}
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.PowerController#increase(org.prelle.splimo.PowerReference)
	 */
	@Override
	public boolean increase(PowerReference ref) {
		int newVal = ref.getCount()+1;
		
		// Determine how much exp is needed
		int expNeeded = 7 * ref.getPower().getCost();

		if (canBeIncreased(ref)) {
			// Update model
			logger.info("Increase "+ref+" from "+ref.getCount()+" to "+newVal);
			ref.setCount(newVal);
			model.setExperienceFree(model.getExperienceFree()-expNeeded);
			model.setExperienceInvested(model.getExperienceInvested()+expNeeded);
			// Add to undo list
			PowerModification mod = new PowerModification(ref.getPower());
			mod.setExpCost(expNeeded);
			undoList.add(mod);
			powerUndoStack.get(ref).push(mod);
			updateAvailable();

			// Apply modifications attached to power
			logger.info("Increase modifications: "+ref.getModifications());
			for (Modification oldMod : ref.getModifications()) {
				if (oldMod instanceof AttributeModification) {
					AttributeModification aMod = (AttributeModification)oldMod;
					int perLevelVal = aMod.getValue() / (ref.getCount()-1);
					logger.info("  increase from "+aMod.getValue()+"   per Level="+perLevelVal+"  newVal="+newVal);
					aMod.setValue(perLevelVal * newVal);
					GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.ATTRIBUTE_CHANGED, aMod.getAttribute(),model.getAttribute(aMod.getAttribute())));
				}
			}
//			applyModification(ref.getPower());

			// Inform listener
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.POWER_CHANGED, ref));
//			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.POWER_ADDED, ref));
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.EXPERIENCE_CHANGED, null, new int[]{
					model.getExperienceFree(),
					model.getExperienceInvested()
			}));
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.UNDO_LIST_CHANGED, undoList));
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
			PowerModification mod = powerUndoStack.get(ref).pop();
			logger.info("Decrease "+ref+" from "+ref.getCount()+" to "+newVal);
			ref.setCount(newVal);
			if (newVal==0)
				model.removePower(ref);
			
			model.setExperienceFree(model.getExperienceFree()+mod.getExpCost());
			model.setExperienceInvested(model.getExperienceInvested()-mod.getExpCost());
			undoList.remove(mod);

			updateAvailable();

			// Undo modifications attached to power
			if (newVal>0) {
				logger.info("decrease modifications");
				for (Modification oldMod : ref.getModifications()) {
					if (oldMod instanceof AttributeModification) {
						AttributeModification aMod = (AttributeModification)oldMod;
						int perLevelVal = aMod.getValue() / (ref.getCount()+1);
						logger.info("  decrease from "+aMod.getValue()+"   per Level="+perLevelVal+"  newVal="+newVal);
						aMod.setValue(perLevelVal * newVal);
						GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.ATTRIBUTE_CHANGED, aMod.getAttribute(),model.getAttribute(aMod.getAttribute())));
					}
				}
			} else {
				// Undo modifications attched to power
				undoModification(ref.getPower());
			}
			
			// Inform listener
			if (newVal==0)
				GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.POWER_REMOVED, ref));
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.EXPERIENCE_CHANGED, null, new int[]{
					model.getExperienceFree(),
					model.getExperienceInvested()
			}));
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.UNDO_LIST_CHANGED, undoList));
			return true;
		}
		return false;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		switch (event.getType()) {
		case EXPERIENCE_CHANGED:
			logger.debug("Experience changed to "+Arrays.toString((int[])event.getValue())+" - update available");
			updateAvailable();
			break;
		default:
		}
		
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.PowerController#getPointsLeft()
	 */
	@Override
	public int getPointsLeft() {
		return 0;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.Controller#getToDos()
	 */
	@Override
	public List<String> getToDos() {
		return new ArrayList<>();
	}

}
