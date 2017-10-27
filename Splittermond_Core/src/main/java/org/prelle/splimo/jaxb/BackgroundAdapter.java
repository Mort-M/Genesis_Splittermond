package org.prelle.splimo.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.prelle.splimo.Background;
import org.prelle.splimo.SplitterMondCore;

public class BackgroundAdapter extends XmlAdapter<String, Background> {
	@Override
	public Background unmarshal(String v) throws Exception {
		Background data = SplitterMondCore.getBackground(v);
		if (data==null) {
			System.err.println("No such background: "+v);
			throw new IllegalArgumentException("No such background: "+v);
		}
		return data;
	}

	@Override
	public String marshal(Background v) throws Exception {
		return v.getKey();
	}
}