package org.prelle.splimo.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.log4j.Logger;
import org.prelle.splimo.Spell;

public class SpellDifficultyAdapter extends XmlAdapter<String, Integer> {
	
	private final static Logger logger = Logger.getLogger("splimo.jaxb");

	@Override
	public Integer unmarshal(String v) throws Exception {
		try {
			return Integer.parseInt(v);
		} catch (NumberFormatException nfe) {
			if (v.equalsIgnoreCase("VTD") || v.equalsIgnoreCase("DEF"))
				return Spell.DIFF_DEFENSE;
			if (v.equalsIgnoreCase("GW") || v.equalsIgnoreCase("MR"))
				return Spell.DIFF_MINDRESIST;
			if (v.equalsIgnoreCase("KW") || v.equalsIgnoreCase("BR"))
				return Spell.DIFF_BODYRESIST;
			System.err.println("Unknown spell difficulty: "+v);
			logger.error("Unknown spell difficulty: "+v);
			throw new IllegalArgumentException("Unknown spell difficulty: "+v);
		}
	}

	//--------------------------------------------------------------------
	/**
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public String marshal(Integer v) throws Exception {
		if (v==null)
			return null;
		if (v==Spell.DIFF_DEFENSE)	return "VTD";
		if (v==Spell.DIFF_MINDRESIST)	return "GW";
		if (v==Spell.DIFF_BODYRESIST)	return "KW";
		return String.valueOf(v);
	}
	
}