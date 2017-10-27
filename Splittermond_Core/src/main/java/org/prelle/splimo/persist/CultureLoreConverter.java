package org.prelle.splimo.persist;

import org.prelle.simplepersist.StringValueConverter;
import org.prelle.splimo.CultureLore;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.persist.ReferenceException.ReferenceType;

public class CultureLoreConverter implements StringValueConverter<CultureLore> {

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(org.prelle.simplepersist.Persister.ParseNode, javax.xml.stream.events.StartElement)
	 */
	@Override
	public CultureLore read(String v) throws Exception {
		CultureLore data = SplitterMondCore.getCultureLore(v);
		if (data==null) {
			System.err.println("No such culture lore: "+v);
			throw new ReferenceException(ReferenceType.CULTURELORE,v);
		}
		return data;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(org.prelle.simplepersist.XmlNode, java.lang.Object)
	 */
	@Override
	public String write(CultureLore v) throws Exception {
		return v.getKey();
	}
	
}