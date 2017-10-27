package org.prelle.splimo.jaxb;

import java.util.StringTokenizer;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.prelle.splimo.SpellCost;
import org.prelle.splimo.SpellEnhancement;

public class SpellEnhancementAdapter extends XmlAdapter<String, SpellEnhancement> {
	
	private final static SpellCostAdapter COST = new SpellCostAdapter();

	@Override
	public SpellEnhancement unmarshal(String v) throws Exception {
		StringTokenizer tok = new StringTokenizer(v, "/+");
		int eg = Integer.parseInt(tok.nextToken());
		SpellCost add = COST.unmarshal(tok.nextToken());
		
		return new SpellEnhancement(eg, add);
	}

	//--------------------------------------------------------------------
	/**
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public String marshal(SpellEnhancement v) throws Exception {
		StringBuffer buf = new StringBuffer(v.getSuccessGrades()+"/");
		buf.append(v.getExtraCost().toString());
		
		return buf.toString();
	}
	
}