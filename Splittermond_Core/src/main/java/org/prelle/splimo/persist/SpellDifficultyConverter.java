package org.prelle.splimo.persist;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.simplepersist.StringValueConverter;
import org.prelle.splimo.Spell;

public class SpellDifficultyConverter implements StringValueConverter<Integer> {

	private final static Logger logger = LogManager.getLogger("splittermond.persist");

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(org.prelle.simplepersist.Persister.ParseNode, javax.xml.stream.events.StartElement)
	 */
	@Override
	public Integer read(String v) throws Exception {
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
	@Override
	public String write(Integer v) throws Exception {
		if (v==null)
			return null;
		switch (v) {
		case Spell.DIFF_DEFENSE   : return "VTD";
		case Spell.DIFF_MINDRESIST: return "GW";
		case Spell.DIFF_BODYRESIST: return "KW";
		default:
			return String.valueOf(v);
		}
	}
	
}