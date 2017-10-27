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
@Root(name="creaturemodules")
@ElementList(entry="creaturemodule",type=CreatureModule.class)
public class CreatureModuleList extends ArrayList<CreatureModule> {

	private static final long serialVersionUID = 1L;

	//-------------------------------------------------------------------
	public CreatureModuleList() {
	}

	//-------------------------------------------------------------------
	/**
	 * @param c
	 */
	public CreatureModuleList(Collection<? extends CreatureModule> c) {
		super(c);
	}

}
