package org.prelle.splimo.persist;

import org.prelle.simplepersist.StringValueConverter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.items.Enhancement;
import org.prelle.splimo.persist.ReferenceException.ReferenceType;

public class EnhancementConverter implements StringValueConverter<Enhancement> {

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(org.prelle.simplepersist.Persister.ParseNode, javax.xml.stream.events.StartElement)
	 */
	@Override
	public Enhancement read(String v) throws Exception {
		Enhancement data = SplitterMondCore.getEnhancement(v);
		if (data==null) {
			System.err.println("No such enhancement: "+v);
			throw new ReferenceException(ReferenceType.ENHANCEMENT,v);
		}
		return data;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(org.prelle.simplepersist.XmlNode, java.lang.Object)
	 */
	@Override
	public String write(Enhancement v) throws Exception {
		return v.getId();
	}
	
}