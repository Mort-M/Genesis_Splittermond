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
public class CalculateLevelProcessor implements SpliMoCharacterProcessor {

	private static Logger logger = Logger.getLogger("splittermond.chargen.level");

	//-------------------------------------------------------------------
	public CalculateLevelProcessor() {
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.processor.SpliMoCharacterProcessor#process(org.prelle.splimo.SpliMoCharacter, java.util.List)
	 */
	@Override
	public List<Modification> process(SpliMoCharacter data, List<Modification> unprocessed) {
		logger.trace("START: process");
		try {
			/*
			 * Calculate level and add resistances depending on it
			 */
	        double factor = Preferences.userRoot().node("/de/rpgframework/plugins/splittermond").getDouble("exp_factor", 1.0);
			// Level 2
			if (data.getExperienceInvested()>=(int)(100*factor)) {
				data.setLevel(2);
			}
			// Level 3
			if (data.getExperienceInvested()>=(int)(300*factor)) {
				data.setLevel(3);
			}
			// Level 4
			if (data.getExperienceInvested()>=(int)(600*factor)) {
				data.setLevel(4);
			}
		} finally {
			logger.trace("STOP : process() ends with "+unprocessed.size()+" modifications still to process");
		}
		return unprocessed;
	}

}
