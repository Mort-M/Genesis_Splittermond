/**
 * 
 */
package org.prelle.splimo.processor;

import java.util.List;

import org.apache.log4j.Logger;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.Skill;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class ClearAllModificationsProcessor implements SpliMoCharacterProcessor {

	private static Logger logger = Logger.getLogger("splittermond.chargen.level");

	//-------------------------------------------------------------------
	public ClearAllModificationsProcessor() {
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.processor.SpliMoCharacterProcessor#process(org.prelle.splimo.SpliMoCharacter, java.util.List)
	 */
	@Override
	public List<Modification> process(SpliMoCharacter model, List<Modification> unprocessed) {
		logger.trace("START: process");
		try {
			for (Attribute key : Attribute.values()) {
				model.getAttribute(key).clearModifications();
			}
			for (Skill key : SplitterMondCore.getSkills()) {
				SkillValue val = model.getSkillValue(key);
				if (val!=null)
					val.clearModifications();
			}
			
		} finally {
			logger.trace("STOP : process() ends with "+unprocessed.size()+" modifications still to process");
		}
		return unprocessed;
	}

}
