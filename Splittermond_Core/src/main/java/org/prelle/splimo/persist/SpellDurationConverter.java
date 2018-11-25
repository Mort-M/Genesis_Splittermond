package org.prelle.splimo.persist;

import org.apache.log4j.Logger;
import org.prelle.simplepersist.StringValueConverter;
import org.prelle.splimo.Spell;

public class SpellDurationConverter implements StringValueConverter<Integer> {
	
	private final static Logger logger = Logger.getLogger("splimo.persist");

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(java.lang.String)
	 */
	@Override
	public Integer read(String v) throws Exception {
		String count_s  = v.substring(0, v.length()-1);
		String suffix = v.substring(v.length()-1);
		int count = 0;
		if (count_s.length()>0)
			count = Integer.parseInt(count_s)*10;
		
		if (suffix.equals("T"))
			count += Spell.DURATION_TICK;
		else if (suffix.equals("s"))
			count += Spell.DURATION_SECOND;
		else if (suffix.equals("m"))
			count += Spell.DURATION_MINUTE;
		else if (suffix.equals("h"))
			count += Spell.DURATION_HOUR;
		else if (suffix.equals("D"))
			count += Spell.DURATION_DAY;
		else if (suffix.equals("W"))
			count += Spell.DURATION_WEEK;
		else if (suffix.equals("M"))
			count += Spell.DURATION_MONTH;
		else if (suffix.equals("Y"))
			count += Spell.DURATION_YEAR;
		else if (suffix.equals("K"))
			count += Spell.DURATION_CHANNELLED;
		else {
			System.err.println("Unknown spell cast duration: "+v);
			logger.error("Unknown spell cast duration: "+v);
			throw new IllegalArgumentException("Unknown spell cast duration: "+v);
		}
		
		return count;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(java.lang.Object)
	 */
	@Override
	public String write( Integer v) throws Exception {
		if (v==null)
			return null;
		int type = v%10;
		int count = v/10;
		String val = "?"+v+"?";
		switch (type) {
		case Spell.DURATION_TICK  : val=count+"T"; break;
		case Spell.DURATION_SECOND: val=count+"s"; break;
		case Spell.DURATION_MINUTE: val=count+"m"; break;
		case Spell.DURATION_HOUR  : val=count+"h"; break;
		case Spell.DURATION_DAY   : val=count+"D"; break;
		case Spell.DURATION_WEEK  : val=count+"W"; break;
		case Spell.DURATION_MONTH : val=count+"M"; break;
		case Spell.DURATION_YEAR  : val=count+">"; break;
		case Spell.DURATION_CHANNELLED: val="K"; break;
		}
		return val;
	}
	
}