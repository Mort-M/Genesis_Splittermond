package org.prelle.splimo.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.log4j.Logger;
import org.prelle.splimo.Skill;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.jaxb.ReferenceException.ReferenceType;

public class SkillAdapter extends XmlAdapter<String, Skill> {
	
	private final static Logger logger = Logger.getLogger("splimo.jaxb");
	
	@Override
	public Skill unmarshal(String v) throws Exception {
			Skill skill = SplitterMondCore.getSkill(v);
			if (skill==null) {
				logger.debug("No such skill '"+v+"'");
				throw new ReferenceException(ReferenceType.SKILL, v);
			}
		return skill;
	}

	@Override
	public String marshal(Skill v) throws Exception {
		if (v==null)
			return null;
		return v.getId();
	}
}