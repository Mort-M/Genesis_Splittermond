package org.prelle.splimo.persist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.prelle.simplepersist.StringValueConverter;
import org.prelle.splimo.items.ItemType;

public class ItemTypeConverter implements StringValueConverter<Collection<ItemType>> {

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(org.prelle.simplepersist.Persister.ParseNode, javax.xml.stream.events.StartElement)
	 */
	@Override
	public Collection<ItemType> read(String v) throws Exception {
		List<ItemType> ret = new ArrayList<ItemType>();
		StringTokenizer tok = new StringTokenizer(v," ,");
		try {
			while (tok.hasMoreTokens()) {
				ret.add(ItemType.valueOf(tok.nextToken()));
			}
		} catch (Exception e) {
			System.err.println("Failed converting to item type list: "+v);
			throw new IllegalArgumentException("Failed converting to item type list: "+v);
		}
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(org.prelle.simplepersist.XmlNode, java.lang.Object)
	 */
	@Override
	public String write(Collection<ItemType> v) throws Exception {
		StringBuffer buf = new StringBuffer();
		Iterator<ItemType> it = v.iterator();
		while (it.hasNext()) {
			buf.append(it.next().name());
			if (it.hasNext())
				buf.append(",");
		}
		return buf.toString();
	}
}