package org.prelle.splimo.persist;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.simplepersist.StringValueConverter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.creature.CreatureFeatureType;
import org.prelle.splimo.persist.ReferenceException.ReferenceType;

public class CreatureFeatureTypeConverter implements StringValueConverter<CreatureFeatureType> {

	private final static Logger logger = LogManager.getLogger("splittermond.persist");

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(org.prelle.simplepersist.Persister.ParseNode, javax.xml.stream.events.StartElement)
	 */
	@Override
	public CreatureFeatureType read(String v) throws Exception {
		CreatureFeatureType item = SplitterMondCore.getCreatureFeatureType(v);
		if (item==null) {
			logger.error("Unknown creature feature reference: '"+v+"'");
			throw new ReferenceException(ReferenceType.CREATURE_FEATURE_TYPE, v);
		}
		return item;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(org.prelle.simplepersist.XmlNode, java.lang.Object)
	 */
	@Override
	public String write(CreatureFeatureType v) throws Exception {
		return v.getId();
	}
	
}