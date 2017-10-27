package org.prelle.splimo.jaxb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.prelle.splimo.items.ItemType;

public class ItemTypeAdapter extends XmlAdapter<String, Collection<ItemType>> {
	@Override
	public Collection<ItemType> unmarshal(String v) throws Exception {
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

	@Override
	public String marshal(Collection<ItemType> v) throws Exception {
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