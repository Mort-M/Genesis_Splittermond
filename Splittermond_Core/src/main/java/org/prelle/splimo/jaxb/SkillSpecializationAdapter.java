package org.prelle.splimo.jaxb;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.log4j.Logger;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SkillSpecialization;
import org.prelle.splimo.SkillSpecializationValue;
import org.prelle.splimo.SpellType;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.jaxb.ReferenceException.ReferenceType;

public class SkillSpecializationAdapter extends XmlAdapter<String, SkillSpecializationValue> {

	private final static Logger logger = Logger.getLogger("splimo.jaxb");

	@Override
	public SkillSpecializationValue unmarshal(String v) throws Exception {
		StringTokenizer tok = new StringTokenizer(v, "/- ");
		SkillSpecialization special = null;
		try {
			String skillID   = tok.nextToken();
			Skill skill = SplitterMondCore.getSkill(skillID);
			if (skill==null) {
				System.err.println("No such skill: "+v);
				throw new IllegalArgumentException("No such skill: "+v);
			}
			String specialID = tok.nextToken();
			if (skill.getType()==SkillType.MAGIC) {
				try {
					SpellType.valueOf(specialID.toUpperCase());
					special = skill.getSpecialization(specialID.toUpperCase());
				} catch (Exception e) {
					System.err.println("No such spell type in mastership: "+v);
					throw new IllegalArgumentException("No such spell type in mastership: "+v);
				}
			} else {
				special = skill.getSpecialization(specialID);
				if (special==null) {
					System.err.println("No such mastership: "+v);
					throw new IllegalArgumentException("No such mastership: "+v);
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

	@Override
	public String marshal(SkillSpecializationValue v) throws Exception {
		if (v==null)
			return null;
		if (v.getSpecial()==null)
			throw new NullPointerException("No skill specialization set in "+v);
		if (v.getSpecial().getSkill()==null)
			throw new NullPointerException("No skill set in specialization "+v);
		return v.getSpecial().getSkill().getId()+"/"+v.getSpecial().getId()+"/"+v.getLevel();
	}
}