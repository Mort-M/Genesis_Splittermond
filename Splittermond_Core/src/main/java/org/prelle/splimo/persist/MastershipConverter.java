/**
 *
 */
package org.prelle.splimo.persist;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.prelle.simplepersist.StringValueConverter;
import org.prelle.splimo.Mastership;
import org.prelle.splimo.Skill;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.jaxb.ReferenceException;
import org.prelle.splimo.jaxb.ReferenceException.ReferenceType;

/**
 * @author prelle
 *
 */
public class MastershipConverter implements StringValueConverter<Mastership> {

	private final static Logger logger = Logger.getLogger("splittermond.persist");

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(java.lang.String)
	 */
	public Mastership read(String v) {
		StringTokenizer tok = new StringTokenizer(v, "/- ");
		Mastership master;
		try {
			String skillID = tok.nextToken();
			String masterID = tok.nextToken();
			Skill skill = SplitterMondCore.getSkill(skillID);
			if (skill==null) {
				logger.error("No such skill: "+skillID);
				throw new ReferenceException(ReferenceType.SKILL, skillID);
			}
			master = skill.getMastership(masterID);
			if (master==null) {
				logger.error("No such mastership: "+masterID+" in skill "+skill);
				logger.error("Valid values are "+skill.getMasterships());
//				try {
					throw new ReferenceException(ReferenceType.MASTERSHIP, v);
//				} catch (Exception e) {
//					StringWriter out = new StringWriter();
//					e.printStackTrace(new PrintWriter(out));
//					BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 1, "No such mastership: "+masterID+" in skill "+skill+"\n\n"+out);
//				}
			}
		} catch (NoSuchElementException e) {
			logger.error("Invalid mastership reference: "+v);
			throw new ReferenceException(ReferenceType.MASTERSHIP, v);
		}
		return master;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(org.prelle.simplepersist.XmlNode, java.lang.Object)
	 */
	@Override
	public String write(Mastership v) throws Exception {
		if (v.getSkill()==null)
			throw new NullPointerException("No skill set in "+v);
		return v.getSkill().getId()+"/"+v.getKey();
	}

}
