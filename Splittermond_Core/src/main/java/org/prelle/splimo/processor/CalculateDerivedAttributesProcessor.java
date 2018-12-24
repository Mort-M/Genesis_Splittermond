/**
 * 
 */
package org.prelle.splimo.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterTools;
import org.prelle.splimo.modifications.AttributeModification;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class CalculateDerivedAttributesProcessor implements SpliMoCharacterProcessor {

	private static Logger logger = Logger.getLogger("splittermond.chargen.level");

	//-------------------------------------------------------------------
	public CalculateDerivedAttributesProcessor() {
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.processor.SpliMoCharacterProcessor#process(org.prelle.splimo.SpliMoCharacter, java.util.List)
	 */
	@Override
	public List<Modification> process(SpliMoCharacter model, List<Modification> previous) {
		List<Modification> unprocessed = new ArrayList<>(previous);

		logger.trace("START: process");
		try {
			model.setAttribute(Attribute.SPEED     , model.getAttribute(Attribute.SIZE).getValue() + model.getAttribute(Attribute.AGILITY).getValue());
			model.setAttribute(Attribute.INITIATIVE,                  10 - model.getAttribute(Attribute.INTUITION).getValue());
			model.setAttribute(Attribute.LIFE      , model.getAttribute(Attribute.SIZE).getValue() + model.getAttribute(Attribute.CONSTITUTION).getValue());
			model.setAttribute(Attribute.FOCUS     , 2*(model.getAttribute(Attribute.MYSTIC).getValue() + model.getAttribute(Attribute.WILLPOWER).getValue()) );
			model.setAttribute(Attribute.DEFENSE   , 12 + model.getAttribute(Attribute.AGILITY).getValue() + model.getAttribute(Attribute.STRENGTH).getValue());
			model.setAttribute(Attribute.MINDRESIST, 12 + model.getAttribute(Attribute.MIND).getValue() + model.getAttribute(Attribute.WILLPOWER).getValue());
			model.setAttribute(Attribute.BODYRESIST, 12 + model.getAttribute(Attribute.CONSTITUTION).getValue() + model.getAttribute(Attribute.WILLPOWER).getValue());
		} finally {
			logger.trace("STOP : process() ends with "+unprocessed.size()+" modifications still to process");
		}
		return unprocessed;
	}

}
