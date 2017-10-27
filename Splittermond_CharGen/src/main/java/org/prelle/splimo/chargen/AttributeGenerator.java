/**
 * 
 */
package org.prelle.splimo.chargen;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.prelle.splimo.charctrl.AttributeController;
import org.prelle.splimo.charctrl.Generator;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.modifications.AttributeModification;

/**
 * @author prelle
 *
 */
public class AttributeGenerator implements AttributeController, Generator {
	
	private static Logger logger = Logger.getLogger("splittermond.chargen");

	private final static ResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splittermond/chargen");
	
	private SpliMoCharacter model;
	private int pointsForAttributes;
	private Map<Attribute, Collection<AttributeModification>> modifications;

	//-------------------------------------------------------------------
	/**
	 */
	public AttributeGenerator(SpliMoCharacter model) {
		this.model = model;
		pointsForAttributes = 18;
		modifications = new HashMap<>();
		
		modifications.put(Attribute.SPLINTER, new ArrayList<AttributeModification>());
		for (Attribute attr : Attribute.primaryValues()) {		
			modifications.put(attr, new ArrayList<AttributeModification>());
			// Value should have average of 2
			AttributeValue val = model.getAttribute(attr);
			val.setDistributed(2);
			pointsForAttributes -= 2;
			// Inform listener
//			GenerationEventDispatcher.fireEvent(new GenerationEvent(
//					GenerationEventType.ATTRIBUTE_CHANGED, attr, val));
		}
		pointsForAttributes = 2;
		
		// Prepare secondary attributes
		for (Attribute attr : Attribute.secondaryValues()) {
			modifications.put(attr, new ArrayList<AttributeModification>());
			// Inform listener
//			GenerationEventDispatcher.fireEvent(new GenerationEvent(
//					GenerationEventType.ATTRIBUTE_CHANGED, attr, model.getAttribute(attr)));
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

		// Modify attribute. This also fires an event
		set(attrib, val.getDistributed()+1);
//		val.setDistributed(val.getDistributed()+1);
		pointsForAttributes--;
		
		// Update derived values. This also fires eventually events
		calculateDerived();
		
		
		GenerationEvent event = new GenerationEvent(GenerationEventType.POINTS_LEFT_ATTRIBUTES, null, pointsForAttributes);
		GenerationEventDispatcher.fireEvent(event);
		
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

		// Modify attribute. This also fires an event
		set(attrib, val.getDistributed()-1);
		pointsForAttributes++;
		
		// Update derived values. This also fires eventually events
		calculateDerived();
		
		GenerationEvent event = new GenerationEvent(GenerationEventType.POINTS_LEFT_ATTRIBUTES, null, pointsForAttributes);
		GenerationEventDispatcher.fireEvent(event);
		
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
		return pointsForAttributes;
	}

	//-------------------------------------------------------------------
	public int get(Attribute attr) {
		return model.getAttribute(attr).getValue();
	}

	//-------------------------------------------------------------------
	void set(Attribute attr, int val) {
		AttributeValue data = model.getAttribute(attr);
		
		int oldVal = data.getValue();
		int oldDist= data.getDistributed();
		
		data.setDistributed(val);
		
		// Fire event if changed
		if (oldDist!=val || oldVal!=data.getValue()) {
			logger.debug("Attribute "+attr+" changed");
			GenerationEvent event = new GenerationEvent(GenerationEventType.ATTRIBUTE_CHANGED, attr, data);
			GenerationEventDispatcher.fireEvent(event);
		}
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
	public List<String> getToDos() {
		if (getPointsLeft()>0)
			return Arrays.asList(String.format(RES.getString("attrgen.todo"), getPointsLeft()));
		return new ArrayList<>();
	}

}
