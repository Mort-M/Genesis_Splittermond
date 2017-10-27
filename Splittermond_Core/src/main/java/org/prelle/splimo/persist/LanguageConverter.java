package org.prelle.splimo.persist;

import org.prelle.simplepersist.StringValueConverter;
import org.prelle.splimo.Language;
import org.prelle.splimo.SplitterMondCore;

public class LanguageConverter implements StringValueConverter<Language> {

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(org.prelle.simplepersist.Persister.ParseNode, javax.xml.stream.events.StartElement)
	 */
	@Override
	public Language read(String v) throws Exception {
		Language data = SplitterMondCore.getLanguage(v);
		if (data==null) {
			System.err.println("No such language: "+v);
			throw new IllegalArgumentException("No such language: "+v);
		}
		return data;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(org.prelle.simplepersist.XmlNode, java.lang.Object)
	 */
	@Override
	public String write(Language v) throws Exception {
		return v.getKey();
	}
	
}