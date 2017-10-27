package org.prelle.splimo.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.prelle.splimo.Language;
import org.prelle.splimo.SplitterMondCore;

public class LanguageAdapter extends XmlAdapter<String, Language> {
	@Override
	public Language unmarshal(String v) throws Exception {
		Language data = SplitterMondCore.getLanguage(v);
		if (data==null) {
			System.err.println("No such language: "+v);
			throw new IllegalArgumentException("No such language: "+v);
		}
		return data;
	}

	@Override
	public String marshal(Language v) throws Exception {
		return v.getKey();
	}
}