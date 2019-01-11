package org.prelle.splimo.persist;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.simplepersist.StringValueConverter;
import org.prelle.splimo.SplitterDataMigration;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.SplittermondCustomDataCore;
import org.prelle.splimo.items.ItemTemplate;
import org.prelle.splimo.persist.ReferenceException.ReferenceType;

public class ItemConverter implements StringValueConverter<ItemTemplate> {

	private final static Logger logger = LogManager.getLogger("splittermond.persist");

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(org.prelle.simplepersist.Persister.ParseNode, javax.xml.stream.events.StartElement)
	 */
	@Override
	public ItemTemplate read(String v) throws Exception {
		ItemTemplate item = SplitterMondCore.getItem(v);
		if (item==null) {
			item = SplittermondCustomDataCore.getItem(v);
		}
		if (item == null){
			String id = SplitterDataMigration.getItemId(v);
			if (id != null) {
				return read(id);
			}
		}
		if (item==null) {
			logger.error("Unknown item reference: '"+v+"'");
			throw new ReferenceException(ReferenceType.ITEM, v);
		}
		return item;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(org.prelle.simplepersist.XmlNode, java.lang.Object)
	 */
	@Override
	public String write(ItemTemplate v) throws Exception {
		return v.getID();
	}
	
}