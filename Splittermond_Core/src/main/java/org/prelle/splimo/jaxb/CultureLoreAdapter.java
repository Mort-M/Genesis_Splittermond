package org.prelle.splimo.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.prelle.splimo.CultureLore;
import org.prelle.splimo.SplitterMondCore;

public class CultureLoreAdapter extends XmlAdapter<String, CultureLore> {
	@Override
	public CultureLore unmarshal(String v) throws Exception {
		CultureLore data = SplitterMondCore.getCultureLore(v);
		if (data==null) {
			System.err.println("No such culture lore: "+v);
			throw new IllegalArgumentException("No such culture lore: "+v);
		}
		return data;
	}

	@Override
	public String marshal(CultureLore v) throws Exception {
		return v.getKey();
	}
}