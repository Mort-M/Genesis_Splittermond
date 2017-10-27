package org.prelle.splimo.persist;

import java.util.StringTokenizer;

import org.prelle.simplepersist.StringValueConverter;
import org.prelle.splimo.SpellCost;
import org.prelle.splimo.SpellEnhancement;

public class SpellEnhancementConverter implements StringValueConverter<SpellEnhancement> {
	
	private final static SpellCostConverter COST = new SpellCostConverter();

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(org.prelle.simplepersist.Persister.ParseNode, javax.xml.stream.events.StartElement)
	 */
	@Override
	public SpellEnhancement read(String v) throws Exception {
		StringTokenizer tok = new StringTokenizer(v, "/+");
		int eg = Integer.parseInt(tok.nextToken());
		SpellCost add = COST.read(tok.nextToken());
		
		return new SpellEnhancement(eg, add);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(java.lang.Object)
	 */
	@Override
	public String write(SpellEnhancement v) throws Exception {
		StringBuffer buf = new StringBuffer(v.getSuccessGrades()+"/");
		buf.append(v.getExtraCost().toString());
		
		return buf.toString();
	}
	
}