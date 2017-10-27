package org.prelle.splimo.persist;

import org.prelle.simplepersist.StringValueConverter;
import org.prelle.splimo.items.Enhancement;

public class EnhancementMaxConverter implements StringValueConverter<Integer> {

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(org.prelle.simplepersist.Persister.ParseNode, javax.xml.stream.events.StartElement)
	 */
	@Override
	public Integer read(String v) throws Exception {
		if ("ONCEPERLOAD".equals(v))
			return Enhancement.MAX_ONCEPERLOAD;
		if ("HALFLOAD".equals(v))
			return Enhancement.MAX_HALFLOAD;
		return Integer.parseInt(v);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(org.prelle.simplepersist.XmlNode, java.lang.Object)
	 */
	@Override
	public String write(Integer v) throws Exception {
		if (v==Enhancement.MAX_HALFLOAD) return "HALFLOAD";
		if (v==Enhancement.MAX_ONCEPERLOAD) return "ONCEPERLOAD";
		return String.valueOf(v);
	}
	
}