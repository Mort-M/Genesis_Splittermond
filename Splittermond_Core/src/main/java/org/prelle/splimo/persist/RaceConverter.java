package org.prelle.splimo.persist;

import org.apache.log4j.Logger;
import org.prelle.simplepersist.StringValueConverter;
import org.prelle.splimo.Race;
import org.prelle.splimo.SplitterMondCore;

public class RaceConverter implements StringValueConverter<Race> {
	
	private final static Logger logger = Logger.getLogger("splimo.persist");

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(org.prelle.simplepersist.Persister.ParseNode, javax.xml.stream.events.StartElement)
	 */
	@Override
	public Race read(String v) throws Exception {
		Race data = SplitterMondCore.getRace(v);
		if (data==null) {
			logger.error("No such Race: "+v);
			throw new IllegalArgumentException("No such Race: "+v);
		}
		return data;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(org.prelle.simplepersist.XmlNode, java.lang.Object)
	 */
	@Override
	public String write(Race v) throws Exception {
		return v.getKey();
	}
	
}