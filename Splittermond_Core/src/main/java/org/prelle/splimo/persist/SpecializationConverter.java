package org.prelle.splimo.persist;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.simplepersist.StringValueConverter;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SkillSpecialization;
import org.prelle.splimo.SpellType;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.persist.ReferenceException.ReferenceType;

public class SpecializationConverter implements StringValueConverter<SkillSpecialization> {

	private final static Logger logger = LogManager.getLogger("splittermond.persist");

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(org.prelle.simplepersist.Persister.ParseNode, javax.xml.stream.events.StartElement)
	 */
	@Override
	public SkillSpecialization read(String v) {
		StringTokenizer tok = new StringTokenizer(v, "/- ");
		SkillSpecialization special = null;
		try {
			String skillID   = tok.nextToken();
			Skill skill = SplitterMondCore.getSkill(skillID);
			if (skill==null) {
				logger.error("No such skill: "+v);
				throw new IllegalArgumentException("No such skill: "+v);
			}
			String specialID = tok.nextToken();
			if (skill.getType()==SkillType.MAGIC) {
				try {
					SpellType.valueOf(specialID.toUpperCase());
					special = skill.getSpecialization(specialID.toUpperCase());
					if (special==null) {
						logger.error("No such spell type in mastership: "+specialID.toUpperCase());
						throw new ReferenceException(ReferenceType.SPELLTYPE_SPECIAL, specialID.toUpperCase(), skill);
					}
				} catch (NoSuchElementException e) {
					logger.error("No such spell type in mastership: "+v);
					throw new IllegalArgumentException("No such spell type in mastership: "+v);
				}
			} else {
				special = skill.getSpecialization(specialID);
				if (special==null) {
					logger.error("No such mastership: "+v);
					throw new IllegalArgumentException("No such mastership: "+v);
				}
			}
			return special;
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