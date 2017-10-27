package org.prelle.splimo.persist;

import org.apache.log4j.Logger;
import org.prelle.simplepersist.StringValueConverter;
import org.prelle.splimo.Resource;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.persist.ReferenceException.ReferenceType;

public class ResourceConverter implements StringValueConverter<Resource> {
	
	private final static Logger logger = Logger.getLogger("splimo.persist");

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(org.prelle.simplepersist.Persister.ParseNode, javax.xml.stream.events.StartElement)
	 */
	@Override
	public Resource read(String v) throws Exception {
		Resource data = SplitterMondCore.getResource(v);
		if (data==null) {
			logger.error("No such Resource: "+v);
			throw new ReferenceException(ReferenceType.RESOURCE, v);
		}
		return data;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(org.prelle.simplepersist.XmlNode, java.lang.Object)
	 */
	@Override
	public String write(Resource v) throws Exception {
		return v.getId();
	}

}