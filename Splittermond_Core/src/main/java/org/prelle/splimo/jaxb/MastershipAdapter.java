package org.prelle.splimo.jaxb;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.log4j.Logger;
import org.prelle.splimo.Mastership;
import org.prelle.splimo.Skill;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.jaxb.ReferenceException.ReferenceType;

public class MastershipAdapter extends XmlAdapter<String, Mastership> {
	
	private final static Logger logger = Logger.getLogger("splimo.jaxb");
	
	@Override
	public Mastership unmarshal(String v) throws Exception {
			StringTokenizer tok = new StringTokenizer(v, "/- ");
			Mastership master;
			try {
				String skillID = tok.nextToken();
				String masterID = tok.nextToken();
				Skill skill = SplitterMondCore.getSkill(skillID);
				if (skill==null) {
					System.err.println("No such skill: "+skillID);
					logger.warn("No such skill: "+skillID);
					throw new ReferenceException(ReferenceType.SKILL, skillID);
				}
				master = skill.getMastership(masterID);
				if (master==null) {
					System.err.println("No such mastership: "+v);
					logger.error("No such mastership: "+v);
					throw new ReferenceException(ReferenceType.MASTERSHIP, v);
				}
			} catch (NoSuchElementException e) {
				logger.error("Invalid mastership reference: "+v);
				throw new ReferenceException(ReferenceType.MASTERSHIP, v);
			}
		return master;
	}

	@Override
	public String marshal(Mastership v) throws Exception {
		if (v==null)
			return null;
		if (v.getSkill()==null)
			throw new NullPointerException("No skill set in "+v);
		return v.getSkill().getId()+"/"+v.getKey();
	}
}