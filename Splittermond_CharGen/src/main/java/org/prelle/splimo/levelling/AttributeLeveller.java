/**
 * 
 */
package org.prelle.splimo.levelling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.AttributeValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.charctrl.AttributeController;
import org.prelle.splimo.charctrl4.SpliMoCharGenConstants;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.modifications.AttributeModification;
import org.prelle.splimo.modifications.ModificationValueType;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class AttributeLeveller implements AttributeController {
	
	private static Logger logger = LogManager.getLogger("splittermond.level");

	private final static ResourceBundle RES = SpliMoCharGenConstants.RES;

	private List<Modification> undoList;
	private Map<Attribute, Stack<AttributeModification>> attributeUndoStack;

	private SpliMoCharacter model;

	//--------------------------------------------------------------------
	/**
	 * @param undoList Undo list for all controllers
	 */
	public AttributeLeveller(SpliMoCharacter model, List<Modification> undoList) {
		this.model = model;
		this.undoList = undoList;

		attributeUndoStack = new HashMap<Attribute, Stack<AttributeModification>>();
		for (Attribute key : Attribute.values())
			attributeUndoStack.put(key, new Stack<AttributeModification>());
		
		calculateDerived();
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.AttributeController#canBeDecreased(org.prelle.splimo.Attribute)
	 */
	@Override
	public boolean canBeDecreased(Attribute attrib) {
		return !attributeUndoStack.get(attrib).isEmpty();
 	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.AttributeController#canBeIncreased(org.prelle.splimo.Attribute)
	 */
	@Override
	public boolean canBeIncreased(Attribute attrib) {
		AttributeValue set = model.getAttribute(attrib);
		
		// Determine new target value
		int newVal = set.getValue()+1;
		
		// Ensure that it cannot be increased above maximum
		int max = set.getStart() + model.getLevel();
		if (newVal>max)
			return false;
		
		// Determine how often the attribute has been increased then
		int times = newVal - set.getStart();

		// Determine how much exp is needed
		int expNeeded = 5 + times*5;
		return expNeeded<=model.getExperienceFree();
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.AttributeController#getIncreaseCost(org.prelle.splimo.Attribute)
	 */
	@Override
	public int getIncreaseCost(Attribute key) {
		AttributeValue set = model.getAttribute(key);
		
		// Determine new target value
		int newVal = set.getValue()+1;
		// Determine how often the attribute has been increased then
		int times = newVal - set.getStart();

		// Determine how much exp is needed
		return 5 + times*5;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.AttributeController#increase(org.prelle.splimo.Attribute)
	 */
	@Override
	public boolean increase(Attribute attrib) {
		AttributeValue set = model.getAttribute(attrib);
		
		int newVal = set.getDistributed()+1;
		int times = newVal - set.getStart();
		int expNeeded = 5 + times*5;

		if (canBeIncreased(attrib)) {
			// Update model
			logger.info("Increase "+attrib+" from "+set.getValue()+" to "+newVal);
			set.setDistributed(newVal);
			model.setExperienceFree(model.getExperienceFree()-expNeeded);
			model.setExperienceInvested(model.getExperienceInvested()+expNeeded);
			// Add to undo list
			AttributeModification mod = new AttributeModification(ModificationValueType.ABSOLUT, attrib, newVal);
			mod.setExpCost(expNeeded);
			undoList.add(mod);
			attributeUndoStack.get(attrib).push(mod);
			// Inform listener
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.ATTRIBUTE_CHANGED, attrib, set));
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.EXPERIENCE_CHANGED, null, new int[]{
					model.getExperienceFree(),
					model.getExperienceInvested()
			}));
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.UNDO_LIST_CHANGED, undoList));
			
			// Update derived attributes
			calculateDerived();
			return true;
		}
		return false;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.AttributeController#decrease(org.prelle.splimo.Attribute)
	 */
	@Override
	public boolean decrease(Attribute attrib) {
		AttributeValue set = model.getAttribute(attrib);
		int newVal = set.getDistributed()-1;

		if (canBeDecreased(attrib)) {
			// Update model
			AttributeModification mod = attributeUndoStack.get(attrib).pop();
			logger.info("Decrease "+attrib+" from "+set.getValue()+" to "+newVal);
			set.setDistributed(newVal);
			model.setExperienceFree(model.getExperienceFree()+mod.getExpCost());
			model.setExperienceInvested(model.getExperienceInvested()-mod.getExpCost());
			logger.info("Undo = "+undoList.remove(mod));

			// Inform listener
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.ATTRIBUTE_CHANGED, attrib, set));
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.EXPERIENCE_CHANGED, null, new int[]{
					model.getExperienceFree(),
					model.getExperienceInvested()
			}));
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.UNDO_LIST_CHANGED, undoList));

			// Update derived attributes
			calculateDerived();
			return true;
		}
		return false;
	}

	//-------------------------------------------------------------------
	int get(Attribute attr) {
		return model.getAttribute(attr).getValue();
	}

	//-------------------------------------------------------------------
	void set(Attribute attr, int derivedValue) {
		AttributeValue val = model.getAttribute(attr);
		int oldVal = val.getValue();
		
		val.setDistributed(derivedValue);
		
		if (oldVal!=val.getValue())
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.ATTRIBUTE_CHANGED, attr, val));
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

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.AttributeController#getPointsLeft()
	 */
	@Override
	public int getPointsLeft() {
		return 0;
	}

	//-------------------------------------------------------------------
	void addModification(AttributeModification mod) {
		logger.debug("addModification "+mod);
		if (mod.getType()==ModificationValueType.RELATIVE) {
			AttributeValue value = model.getAttribute(mod.getAttribute());
			value.addModification(mod);
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.ATTRIBUTE_CHANGED, mod.getAttribute(), value));
		} else
			logger.warn("Don't know how to deal with non-relative attribute mods");
	}

	//-------------------------------------------------------------------
	void removeModification(AttributeModification mod) {
		logger.debug("remove Modification");
		if (mod.getType()==ModificationValueType.RELATIVE) {
			AttributeValue value = model.getAttribute(mod.getAttribute());
			value.removeModification(mod);
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.ATTRIBUTE_CHANGED, mod.getAttribute(), value));
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.Controller#getToDos()
	 */
	@Override
	public List<String> getToDos() {
		return new ArrayList<>();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#canBeIncreased(de.rpgframework.genericrpg.SelectedValue)
	 */
	@Override
	public boolean canBeIncreased(AttributeValue value) {
		return canBeIncreased(value.getModifyable());
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#canBeDecreased(de.rpgframework.genericrpg.SelectedValue)
	 */
	@Override
	public boolean canBeDecreased(AttributeValue value) {
		return canBeDecreased(value.getModifyable());
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#increase(de.rpgframework.genericrpg.SelectedValue)
	 */
	@Override
	public boolean increase(AttributeValue value) {
		return increase(value.getModifyable());
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#decrease(de.rpgframework.genericrpg.SelectedValue)
	 */
	@Override
	public boolean decrease(AttributeValue value) {
		return decrease(value.getModifyable());
	}

}
