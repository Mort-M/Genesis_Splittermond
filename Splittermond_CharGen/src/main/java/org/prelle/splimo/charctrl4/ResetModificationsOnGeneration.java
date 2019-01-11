/**
 * 
 */
package org.prelle.splimo.charctrl4;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.AttributeValue;
import org.prelle.splimo.PowerReference;
import org.prelle.splimo.ResourceReference;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.processor.SpliMoCharacterProcessor;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class ResetModificationsOnGeneration implements SpliMoCharacterProcessor {

	private static Logger logger = LogManager.getLogger("splittermond.chargen.race");

	//-------------------------------------------------------------------
	private void clearCharacter(SpliMoCharacter model) {
		model.setExperienceFree(15);
		model.setExperienceInvested(0);
		for (Modification mod : model.getHistory()) {
			model.removeFromHistory(mod);
		}
		// Reset resources
		for (ResourceReference ref : new ArrayList<>(model.getResources())) {
			ref.clearModifications();
//			if (!NewResourceGenerator.BASE_RESOURCES.contains(ref.getResource()))
//				model.removeResource(ref);
		}
		
		// Reset powers
		for (PowerReference ref : new ArrayList<>(model.getPowers())) {
			ref.clearModifications();
			if (ref.getCount()==0)
				model.removePower(ref);
//			if (!NewResourceGenerator.BASE_RESOURCES.contains(ref.getResource()))
//				model.removeResource(ref);
		}
		
		// Reset attributes
		for (Attribute key : Attribute.values()) {
			AttributeValue val = model.getAttribute(key);
			if (val!=null) {
				val.clearModifications();
			}
		}
//		for (Attribute key : new Attribute[] {Attribute.INITIATIVE_DICE_PHYSICAL, Attribute.INITIATIVE_DICE_ASTRAL, Attribute.INITIATIVE_DICE_MATRIX})
//			model.getAttribute(key).setPoints(1);
//		
//		// Reset skills
//		for (SkillValue val : model.getSkillValues(false)) {
//			if (val!=null) {
//				val.clearModifications();
//			}
//		}
//		
//		// Reset skillgroup values
//		for (SkillGroupValue val : model.getSkillGroupValues()) {
//			if (val!=null) {
//				val.clearModifications();
//			}
//		}
//		
//		// Reset qualities
//		for (QualityValue ref : model.getSystemSelectedQualities()) {
//			model.removeRacialQuality(ref);
//		}
//		
//		// Remove SINs given by modifications
//		for (SIN ref : new ArrayList<SIN>(model.getSINs())) {
//			if (ref.isCriminal() || ref.getQuality()==SIN.Quality.REAL_SIN) {
//				if (ref.getUniqueId()!=null)
//					realSINUUID = ref.getUniqueId();
//				model.removeSIN(ref);
//			}
//		}
	}

	//-------------------------------------------------------------------
	@Override
	public List<Modification> process(SpliMoCharacter model, List<Modification> previous) {
		List<Modification> unprocessed = new ArrayList<>(previous);

		logger.trace("START: process");
		try {
			// Set character back to zero
			clearCharacter(model);
		} finally {
			logger.trace("STOP : process() ends with "+unprocessed.size()+" modifications still to process");
		}
		return unprocessed;
	}
	
}
