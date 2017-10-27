package org.prelle.splimo.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.prelle.splimo.Education;
import org.prelle.splimo.SplitterMondCore;

public class EducationAdapter extends XmlAdapter<String, Education> {
	@Override
	public Education unmarshal(String v) throws Exception {
		Education data = SplitterMondCore.getEducation(v);
		if (data==null) {
			System.err.println("No such education: "+v);
			throw new IllegalArgumentException("No such education: "+v);
		}
		return data;
	}

	@Override
	public String marshal(Education v) throws Exception {
		return v.getKey();
	}
}