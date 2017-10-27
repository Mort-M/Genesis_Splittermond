package org.prelle.splimo.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.log4j.Logger;
import org.prelle.splimo.Spell;

public class SpellRangeAdapter extends XmlAdapter<String, Integer> {
	
	private final static Logger logger = Logger.getLogger("splimo.jaxb");

	@Override
	public Integer unmarshal(String v) throws Exception {
		try {
			return Integer.parseInt(v);
		} catch (NumberFormatException nfe) {
			if (v.equalsIgnoreCase("CASTER"))
				return Spell.RANGE_CASTER;
			if (v.equalsIgnoreCase("TOUCH"))
				return Spell.RANGE_TOUCH;
			if (v.endsWith("m"))
				return Integer.parseInt(v.substring(0, v.indexOf("m")));
			System.err.println("Unknown spell range: "+v);
			logger.error("Unknown spell range: "+v);
			throw new IllegalArgumentException("Unknown spell range: "+v);
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
		if (v==Spell.RANGE_CASTER)	return "CASTER";
		if (v==Spell.RANGE_TOUCH)	return "TOUCH";
		return String.valueOf(v)+"m";
	}
	
}