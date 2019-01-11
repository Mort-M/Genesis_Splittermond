package org.prelle.splimo.persist;

import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.simplepersist.StringValueConverter;
import org.prelle.splimo.SpellCost;

public class SpellCostConverter implements StringValueConverter<SpellCost> {

	private final static Logger logger = LogManager.getLogger("splittermond.persist");

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(java.lang.String)
	 */
	@Override
	public SpellCost read(String v) {
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

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(java.lang.Object)
	 */
	@Override
	public String write(SpellCost v) throws Exception {
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