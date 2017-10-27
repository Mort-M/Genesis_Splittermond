/**
 * 
 */
package org.prelle.splimo.creature;

import java.util.ArrayList;
import java.util.Collection;

import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.Root;

/**
 * @author prelle
 *
 */
@Root(name="creaturetypes")
@ElementList(entry="creaturetype",type=CreatureType.class)
public class CreatureTypeList extends ArrayList<CreatureType> {

	private static final long serialVersionUID = 1L;

	//-------------------------------------------------------------------
	/**
	 */
	public CreatureTypeList() {
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @param c
	 */
	public CreatureTypeList(Collection<? extends CreatureType> c) {
		super(c);
		// TODO Auto-generated constructor stub
	}

}
