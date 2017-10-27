package org.prelle.splimo.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.prelle.splimo.Resource;
import org.prelle.splimo.SplitterMondCore;

public class ResourceAdapter extends XmlAdapter<String, Resource> {
	@Override
	public Resource unmarshal(String v) throws Exception {
		Resource data = SplitterMondCore.getResource(v);
		if (data==null) {
			System.err.println("No such Resource: "+v);
			throw new IllegalArgumentException("No such Resource: "+v);
		}
		return data;
	}

	@Override
	public String marshal(Resource v) throws Exception {
		return v.getId();
	}
}