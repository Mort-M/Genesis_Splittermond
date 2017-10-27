package org.prelle.splimo.persist;

import org.prelle.simplepersist.StringValueConverter;
import org.prelle.splimo.Culture;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.persist.ReferenceException.ReferenceType;

public class CultureConverter implements StringValueConverter<Culture> {

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(org.prelle.simplepersist.Persister.ParseNode, javax.xml.stream.events.StartElement)
	 */
	@Override
	public Culture read(String v) throws Exception {
		Culture data = SplitterMondCore.getCulture(v);
		if (data==null) {
			throw new ReferenceException(ReferenceType.CULTURE,v);
		}
		return data;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(org.prelle.simplepersist.XmlNode, java.lang.Object)
	 */
	@Override
	public String write(Culture v) throws Exception {
		return v.getKey();
	}

}