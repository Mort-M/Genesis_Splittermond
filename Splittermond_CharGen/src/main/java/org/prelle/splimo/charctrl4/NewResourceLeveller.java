/**
 * 
 */
package org.prelle.splimo.charctrl4;

import java.util.Date;

import org.prelle.splimo.ResourceReference;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.modifications.ResourceModification;

/**
 * @author prelle
 *
 */
public class NewResourceLeveller extends NewResourceGenerator {

	//-------------------------------------------------------------------
	public NewResourceLeveller(SplitterEngineCharacterGenerator charGen, int toSpend) {
		super(charGen, toSpend, false);
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	protected int getMaxValue() {
//		switch (parent.getMode()) {
//		case FINISHED:
			return 4 + model.getLevel()*2;
//		default:
//			return super.getMaxValue();
//		}
	}

	//-------------------------------------------------------------------
	protected int getMinValue() {
//		switch (parent.getMode()) {
//		case FINISHED:
			return 4 + model.getLevel()*2;
//		default:
//			return super.getMinValue();
//		}
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.ResourceController#canBeIncreased(org.prelle.splimo.ResourceReference)
	 */
	@Override
	public boolean canBeIncreased(ResourceReference ref) {
		logger.debug("canBeIncreased("+ref+")  "+getPointsLeft());
		// Prevent increasing above the maximum
		if (ref.getValue()>=getMaxValue())
			return false;
		
		// Only allow when there is exp left
		return model.getExperienceFree()>=7;
	}

	//-------------------------------------------------------------------
	@Override
	public boolean increase(ResourceReference ref) {
		logger.debug("increase "+ref);
		if (!canBeIncreased(ref))
			return false;
		
		// Increase
		ref.setValue( ref.getValue()+1 );
		logger.info("increased resource "+ref.getResource()+" to "+ref.getValue());
		
		// Pay
		int exp = 7;
		model.setExperienceInvested( model.getExperienceInvested() + exp);
		model.setExperienceFree    ( model.getExperienceFree    () - exp);
		
		// Log it
		ResourceModification mod = new ResourceModification(ref.getResource(), ref.getValue());
		mod.setExpCost(exp);
		mod.setDate(new Date());
		mod.setComment(ref.getDescription());
		model.addToHistory(mod);
		
		// Inform listener
		parent.runProcessors();
		return true;
	}

}
