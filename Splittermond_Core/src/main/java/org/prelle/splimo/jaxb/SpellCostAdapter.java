package org.prelle.splimo.jaxb;

import java.util.StringTokenizer;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.log4j.Logger;
import org.prelle.splimo.SpellCost;

public class SpellCostAdapter extends XmlAdapter<String, SpellCost> {
	
	private final static Logger logger = Logger.getLogger("splimo.jaxb");

	@Override
	public SpellCost unmarshal(String v) throws Exception {
		StringTokenizer tok = new StringTokenizer(v, "KV");
		int num1 = Integer.parseInt(tok.nextToken());
		if (tok.hasMoreTokens()) {
			int consumed = Integer.parseInt(tok.nextToken());
			if (v.startsWith("K"))
				return new SpellCost(num1, 0, consumed);
			else if (Character.isDigit(v.charAt(0)))
				return new SpellCost(0, num1, consumed);
			else {
				System.err.println("Unknown spell cost: "+v);
				logger.error("Unknown spell cost: "+v);
				throw new IllegalArgumentException("Unknown spell cost: "+v);
			}
		} else {
			if (v.startsWith("K"))
				return new SpellCost(num1, 0, 0);
			else if (Character.isDigit(v.charAt(0)))
				return new SpellCost(0, num1, 0);
			else {
				System.err.println("Unknown spell cost: "+v);
				logger.error("Unknown spell cost: "+v);
				throw new IllegalArgumentException("Unknown spell cost: "+v);
			}
		}
	}

	//--------------------------------------------------------------------
	/**
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public String marshal(SpellCost v) throws Exception {
		StringBuffer buf = new StringBuffer();
		if (v.getChannelled()>0)
			buf.append("K"+v.getChannelled());
		else
			buf.append(String.valueOf(v.getExhausted()));
		
		if (v.getConsumed()>0)
			buf.append("V"+v.getConsumed());
		
		return buf.toString();
	}
	
}