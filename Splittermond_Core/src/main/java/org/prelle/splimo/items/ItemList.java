/**
 * 
 */
package org.prelle.splimo.items;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.Root;

/**
 * @author prelle
 *
 */
@Root(name = "items")
@ElementList(entry="item",type=ItemTemplate.class,inline=true)
public class ItemList extends ArrayList<ItemTemplate> {

	private static final long serialVersionUID = 1L;

	//-------------------------------------------------------------------
	public ItemList() {
	}

	//-------------------------------------------------------------------
	public ItemList(Collection<? extends ItemTemplate> c) {
		super(c);
	}

	//-------------------------------------------------------------------
	public List<ItemTemplate> getData() {
		return this;
	}
}
