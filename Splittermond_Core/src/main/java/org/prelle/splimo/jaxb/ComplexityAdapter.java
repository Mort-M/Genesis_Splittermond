package org.prelle.splimo.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.prelle.splimo.items.Complexity;

public class ComplexityAdapter extends XmlAdapter<String, Complexity> {
		@Override
	public Complexity unmarshal(String v) throws Exception {
		return Complexity.getByID(v);
	}

	@Override
	public String marshal(Complexity v) throws Exception {
		if (v==null)
			return null;
		return v.getID();
	}
}