/**
 * 
 */
package org.prelle.splimo.npc;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.AttributeValue;
import org.prelle.splimo.charctrl.AttributeController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.creature.Creature;

/**
 * @author Stefan
 *
 */
public class NPCAttributeGenerator implements AttributeController {
	
	private static Logger logger = Logger.getLogger("splittermond.npcgen");

	private Creature model;
	
	//--------------------------------------------------------------------
	/**
	 */
	public NPCAttributeGenerator(Creature model) {
		this.model = model;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.Generator#getPointsLeft()
	 */
	@Override
	public int getPointsLeft() {
		return 0;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.Controller#getToDos()
	 */
	@Override
	public List<String> getToDos() {
		return new ArrayList<String>();
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.AttributeController#canBeDecreased(org.prelle.splimo.Attribute)
	 */
	@Override
	public boolean canBeDecreased(Attribute key) {
		return model.getAttribute(key).getDistributed()>0;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.AttributeController#canBeIncreased(org.prelle.splimo.Attribute)
	 */
	@Override
	public boolean canBeIncreased(Attribute key) {
		return true;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.AttributeController#getIncreaseCost(org.prelle.splimo.Attribute)
	 */
	@Override
	public int getIncreaseCost(Attribute key) {
		return 0;
	}

	//-------------------------------------------------------------------
	public int get(Attribute attr) {
		return model.getAttribute(attr).getValue();
	}

	//-------------------------------------------------------------------
	void set(Attribute attr, int val) {
		logger.debug("  set "+attr+"="+val);
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

	//-------------------------------------------------------------------
	public void calculateDerived() {
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
	 * @see org.prelle.splimo.charctrl.AttributeController#increase(org.prelle.splimo.Attribute)
	 */
	@Override
	public boolean increase(Attribute key) {
		int old = model.getAttribute(key).getDistributed();
		set(key, old+1);
		calculateDerived();
		return true;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.AttributeController#decrease(org.prelle.splimo.Attribute)
	 */
	@Override
	public boolean decrease(Attribute key) {
		if (!canBeDecreased(key))
			return false;
		int old = model.getAttribute(key).getDistributed();
		set(key, old-1);
		calculateDerived();
		return true;
	}

}
