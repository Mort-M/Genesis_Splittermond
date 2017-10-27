package org.prelle.splimo.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.log4j.Logger;
import org.prelle.splimo.Spell;

public class SpellDurationAdapter extends XmlAdapter<String, Integer> {
	
	private final static Logger logger = Logger.getLogger("splimo.jaxb");

	@Override
	public Integer unmarshal(String v) throws Exception {
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

	//--------------------------------------------------------------------
	/**
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public String marshal(Integer v) throws Exception {
		if (v==null)
			return null;
		int type = v%10;
		int count = v/10;
		switch (type) {
		case Spell.DURATION_TICK  : return count+"T";
		case Spell.DURATION_SECOND: return count+"s";
		case Spell.DURATION_MINUTE: return count+"m";
		case Spell.DURATION_HOUR  : return count+"h";
		case Spell.DURATION_DAY   : return count+"D";
		case Spell.DURATION_MONTH : return count+"M";
		case Spell.DURATION_YEAR  : return count+">";
		case Spell.DURATION_CHANNELLED: return "K";
		}
		return "?"+v+"?";
	}
	
}