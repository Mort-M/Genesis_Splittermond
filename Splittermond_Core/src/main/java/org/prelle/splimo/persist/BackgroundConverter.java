package org.prelle.splimo.persist;

import org.prelle.simplepersist.StringValueConverter;
import org.prelle.splimo.Background;
import org.prelle.splimo.SplitterMondCore;

public class BackgroundConverter implements StringValueConverter<Background> {

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(org.prelle.simplepersist.XmlNode, java.lang.Object)
	 */
	@Override
	public String write(Background v) throws Exception {
		return v.getKey();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(org.prelle.simplepersist.Persister.ParseNode, javax.xml.stream.events.StartElement)
	 */
	@Override
	public Background read(String v) throws Exception {
		Background data = SplitterMondCore.getBackground(v);
		if (data==null) {
			throw new IllegalArgumentException("No such background: "+v);
		}
		return data;
	}

}