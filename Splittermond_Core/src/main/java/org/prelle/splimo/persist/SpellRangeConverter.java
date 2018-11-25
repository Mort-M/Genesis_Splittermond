package org.prelle.splimo.persist;

import org.apache.log4j.Logger;
import org.prelle.simplepersist.StringValueConverter;
import org.prelle.splimo.Spell;

public class SpellRangeConverter implements StringValueConverter<Integer> {
	
	private final static Logger logger = Logger.getLogger("splimo.persist");

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(org.prelle.simplepersist.Persister.ParseNode, javax.xml.stream.events.StartElement)
	 */
	@Override
	public Integer read(String v) throws Exception {
		try {
			return Integer.parseInt(v);
		} catch (NumberFormatException nfe) {
			if (v.equalsIgnoreCase("CASTER"))
				return Spell.RANGE_CASTER;
			if (v.equalsIgnoreCase("TOUCH"))
				return Spell.RANGE_TOUCH;
			if (v.endsWith("km"))
				return Integer.parseInt(v.substring(0, v.indexOf("km")))*1000;
			if (v.endsWith("m"))
				return Integer.parseInt(v.substring(0, v.indexOf("m")));
			System.err.println("Unknown spell range: "+v);
			logger.error("Unknown spell range: "+v);
			throw new IllegalArgumentException("Unknown spell range: "+v);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(java.lang.Object)
	 */
	@Override
	public String write(Integer v) throws Exception {
		if (v==null)
			return null;
		switch (v) {
		case Spell.RANGE_CASTER: return "CASTER"; 
		case Spell.RANGE_TOUCH: return "TOUCH"; 
		default:
			if (v>1000)
				return String.valueOf(v/1000)+"km";
			return String.valueOf(v)+"m";
		}
	}
	
}