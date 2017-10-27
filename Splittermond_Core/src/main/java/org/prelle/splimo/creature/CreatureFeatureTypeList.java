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
@Root(name="creaturefeaturetypes")
@ElementList(entry="creaturefeaturetype",type=CreatureFeatureType.class)
public class CreatureFeatureTypeList extends ArrayList<CreatureFeatureType> {

	private static final long serialVersionUID = 1L;

	//-------------------------------------------------------------------
	public CreatureFeatureTypeList() {
	}

	//-------------------------------------------------------------------
	/**
	 * @param c
	 */
	public CreatureFeatureTypeList(Collection<? extends CreatureFeatureType> c) {
		super(c);
	}

}
