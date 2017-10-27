package org.prelle.splimo.persist;

import org.apache.log4j.Logger;
import org.prelle.simplepersist.StringValueConverter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.items.FeatureType;
import org.prelle.splimo.persist.ReferenceException.ReferenceType;

public class FeatureTypeConverter implements StringValueConverter<FeatureType> {
	
	private final static Logger logger = Logger.getLogger("splittermond.persist");

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(org.prelle.simplepersist.Persister.ParseNode, javax.xml.stream.events.StartElement)
	 */
	@Override
	public FeatureType read(String v) throws Exception {
		FeatureType item = SplitterMondCore.getFeatureType(v);
		if (item==null) {
			logger.error("Unknown item reference: '"+v+"'");
			throw new ReferenceException(ReferenceType.FEATURE_TYPE, v);
		}
		return item;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(org.prelle.simplepersist.XmlNode, java.lang.Object)
	 */
	@Override
	public String write(FeatureType v) throws Exception {
		return v.getId();
	}
	
}