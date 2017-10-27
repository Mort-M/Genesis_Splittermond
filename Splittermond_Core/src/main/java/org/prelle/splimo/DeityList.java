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
@Root(name="deities")
@ElementList(entry="deity",type=Deity.class,inline=true)
public class DeityList extends ArrayList<Deity> {

	private static final long serialVersionUID = 1L;

	//-------------------------------------------------------------------
	/**
	 */
	public DeityList() {
	}

	//-------------------------------------------------------------------
	/**
	 * @param c
	 */
	public DeityList(Collection<? extends Deity> c) {
		super(c);
	}

}
