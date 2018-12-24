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
public class ModifyDerivedValuesByLevelProcessor implements SpliMoCharacterProcessor {

	private static Logger logger = Logger.getLogger("splittermond.chargen.level");

	//-------------------------------------------------------------------
	public ModifyDerivedValuesByLevelProcessor() {
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.processor.SpliMoCharacterProcessor#process(org.prelle.splimo.SpliMoCharacter, java.util.List)
	 */
	@Override
	public List<Modification> process(SpliMoCharacter data, List<Modification> previous) {
		List<Modification> unprocessed = new ArrayList<>(previous);

		logger.trace("START: process");
		try {
			if (data.getLevel()>1) {
				logger.debug("  add moonsign points and resistances bonus depending on level");
				int mult = data.getLevel()-1;
				unprocessed.add( new AttributeModification(Attribute.SPLINTER  , 1*mult, "Level") );
				unprocessed.add( new AttributeModification(Attribute.BODYRESIST, 2*mult, "Level") );
				unprocessed.add( new AttributeModification(Attribute.MINDRESIST, 2*mult, "Level") );
				unprocessed.add( new AttributeModification(Attribute.DEFENSE   , 2*mult, "Level") );
			}
		} finally {
			logger.trace("STOP : process() ends with "+unprocessed.size()+" modifications still to process");
		}
		return unprocessed;
	}

}
