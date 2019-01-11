package org.prelle.splimo.persist;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.simplepersist.StringValueConverter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.creature.CreatureType;
import org.prelle.splimo.persist.ReferenceException.ReferenceType;

public class CreatureTypeConverter implements StringValueConverter<CreatureType> {

	private final static Logger logger = LogManager.getLogger("splittermond.persist");

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(org.prelle.simplepersist.Persister.ParseNode, javax.xml.stream.events.StartElement)
	 */
	@Override
	public CreatureType read(String v) throws Exception {
		CreatureType item = SplitterMondCore.getCreatureType(v);
		if (item==null) {
			logger.error("Unknown item reference: '"+v+"'");
			throw new ReferenceException(ReferenceType.CREATURE_TYPE, v);
		}
		return item;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(org.prelle.simplepersist.XmlNode, java.lang.Object)
	 */
	@Override
	public String write(CreatureType v) throws Exception {
		return v.getKey();
	}
	
}