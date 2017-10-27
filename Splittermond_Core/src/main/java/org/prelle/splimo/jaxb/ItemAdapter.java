package org.prelle.splimo.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.log4j.Logger;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.items.ItemTemplate;
import org.prelle.splimo.jaxb.ReferenceException.ReferenceType;

public class ItemAdapter extends XmlAdapter<String, ItemTemplate> {
	
	private final static Logger logger = Logger.getLogger("splimo.jaxb");
	
	//--------------------------------------------------------------------
	/**
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public ItemTemplate unmarshal(String v) throws Exception {
		ItemTemplate item = SplitterMondCore.getItem(v);
		if (item==null) {
			logger.error("Unknown item reference: '"+v+"'");
			throw new ReferenceException(ReferenceType.ITEM, v);
		}
		return item;
	}

	//--------------------------------------------------------------------
	/**
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public String marshal(ItemTemplate v) throws Exception {
		if (v==null)
			return null;
		return v.getID();
	}
}