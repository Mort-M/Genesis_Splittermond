/**
 * 
 */
package org.prelle.splimo;

import java.util.ArrayList;
import java.util.Collection;

import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.Root;

/**
 * @author prelle
 *
 */
@Root(name="towns")
@ElementList(entry="town",type=Town.class,inline=true)
public class TownList extends ArrayList<Town> {

	private static final long serialVersionUID = 1L;

	//-------------------------------------------------------------------
	/**
	 */
	public TownList() {
	}

	//-------------------------------------------------------------------
	/**
	 * @param c
	 */
	public TownList(Collection<? extends Town> c) {
		super(c);
	}

}
