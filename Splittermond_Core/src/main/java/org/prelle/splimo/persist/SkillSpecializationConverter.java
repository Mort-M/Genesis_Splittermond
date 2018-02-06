package org.prelle.splimo.persist;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.prelle.simplepersist.StringValueConverter;
import org.prelle.splimo.Skill;
import org.prelle.splimo.SkillSpecialization;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.persist.ReferenceException;
import org.prelle.splimo.persist.ReferenceException.ReferenceType;

public class SkillSpecializationConverter implements StringValueConverter<SkillSpecialization> {

	private final static Logger logger = Logger.getLogger("splittermond.persist");

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(org.prelle.simplepersist.Persister.ParseNode, javax.xml.stream.events.StartElement)
	 */
	@Override
	public SkillSpecialization read(String v) throws Exception {
		StringTokenizer tok = new StringTokenizer(v, "/ ");
		try {
			String skillID   = tok.nextToken();
			Skill skill = SplitterMondCore.getSkill(skillID);
			if (skill==null) {
				logger.error("No such skill: "+v);
				throw new IllegalArgumentException("No such skill: "+v);
			}
			String specialID = tok.nextToken();
			return skill.getSpecialization(specialID);
		} catch (NoSuchElementException nse) {
			logger.error("Invalid skill specialization reference: "+v);
			throw new ReferenceException(ReferenceType.SKILL_SPECIAL, v);			
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(org.prelle.simplepersist.XmlNode, java.lang.Object)
	 */
	@Override
	public String write(SkillSpecialization v) throws Exception {
		if (v==null)
			return null;
		if (v.getSkill()==null)
			throw new NullPointerException("No skill set in specialization "+v);
		String id = v.getSkill().getId()+"/"+v.getId();
		return id;
	}
}