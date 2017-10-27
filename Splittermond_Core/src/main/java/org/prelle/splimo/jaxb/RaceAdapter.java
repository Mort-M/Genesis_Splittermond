package org.prelle.splimo.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.prelle.splimo.Race;
import org.prelle.splimo.SplitterMondCore;

public class RaceAdapter extends XmlAdapter<String, Race> {
	@Override
	public Race unmarshal(String v) throws Exception {
		Race data = SplitterMondCore.getRace(v);
		if (data==null) {
			System.err.println("No such Race: "+v);
			throw new IllegalArgumentException("No such Race: "+v);
		}
		return data;
	}

	@Override
	public String marshal(Race v) throws Exception {
		return v.getKey();
	}
}