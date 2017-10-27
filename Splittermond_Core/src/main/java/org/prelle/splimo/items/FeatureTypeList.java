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
@Root(name="featuretypes")
@ElementList(entry="featuretype",type=FeatureType.class)
public class FeatureTypeList extends ArrayList<FeatureType> {

	private static final long serialVersionUID = 1L;

	//-------------------------------------------------------------------
	/**
	 */
	public FeatureTypeList() {
	}

	//-------------------------------------------------------------------
	/**
	 * @param c
	 */
	public FeatureTypeList(Collection<? extends FeatureType> c) {
		super(c);
	}

}
