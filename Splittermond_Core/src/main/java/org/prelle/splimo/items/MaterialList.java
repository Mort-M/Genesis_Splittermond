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
@Root(name="materials")
@ElementList(entry="material",type=Material.class,inline=true)
public class MaterialList extends ArrayList<Material> {

	private static final long serialVersionUID = 7067425577754420182L;

	//-------------------------------------------------------------------
	/**
	 */
	public MaterialList() {
	}

	//-------------------------------------------------------------------
	/**
	 * @param c
	 */
	public MaterialList(Collection<? extends Material> c) {
		super(c);
	}

	//-------------------------------------------------------------------
	public List<Material> getMaterials() {
		return this;
	}
}
