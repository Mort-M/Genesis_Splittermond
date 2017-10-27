/**
 * 
 */
package org.prelle.splimo.items;

import java.util.ArrayList;
import java.util.Collection;

import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.Root;

/**
 * @author prelle
 *
 */
@Root(name = "enhancements")
@ElementList(entry="enhancement",type=Enhancement.class,inline=true)
public class EnhancementList extends ArrayList<Enhancement> {

	private static final long serialVersionUID = 1L;

	//-------------------------------------------------------------------
	public EnhancementList() {
	}

	//-------------------------------------------------------------------
	public EnhancementList(Collection<? extends Enhancement> c) {
		super(c);
	}

}
