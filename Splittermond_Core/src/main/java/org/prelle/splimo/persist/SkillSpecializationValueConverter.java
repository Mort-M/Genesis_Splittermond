package org.prelle.splimo.persist;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.simplepersist.StringValueConverter;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SkillSpecialization;
import org.prelle.splimo.SkillSpecializationValue;
import org.prelle.splimo.SpellType;
import org.prelle.splimo.SplitterDataMigration;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.persist.ReferenceException.ReferenceType;

public class SkillSpecializationValueConverter implements StringValueConverter<SkillSpecializationValue> {

	private final static Logger logger = LogManager.getLogger("splittermond.persist");

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(org.prelle.simplepersist.Persister.ParseNode, javax.xml.stream.events.StartElement)
	 */
	@Override
	public SkillSpecializationValue read(String v) throws Exception {
		StringTokenizer tok = new StringTokenizer(v, "/ ");
		SkillSpecialization special = null;
//		logger.warn("Decode "+v);
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
					specialID = SplitterDataMigration.getMastershipId(specialID);
					special = skill.getSpecialization(specialID);
				}
				if (special == null) {
					logger.error("No such specialization: "+v);
					throw new IllegalArgumentException("No such specialization: "+v);
				}
			}
			if (tok.hasMoreTokens()) {
				String level_s   = tok.nextToken();
				int level = Integer.parseInt(level_s);
				return new SkillSpecializationValue(special, level);
			}
			return new SkillSpecializationValue(special, 1);
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
	public String write(SkillSpecializationValue v) throws Exception {
		if (v==null)
			return null;
		if (v.getSpecial()==null)
			throw new NullPointerException("No skill specialization set in "+v);
		if (v.getSpecial().getSkill()==null)
			throw new NullPointerException("No skill set in specialization "+v);
		String id = v.getSpecial().getSkill().getId()+"/"+v.getSpecial().getId()+"/"+v.getLevel();
		return id;
	}
}