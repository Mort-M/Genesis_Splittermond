/**
 * 
 */
package org.prelle.splimo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.Root;

/**
 * @author prelle
 *
 */
@Root(name="powers")
@ElementList(entry="power",type=Power.class,inline=true)
public class PowerList extends ArrayList<Power> {

	private static final long serialVersionUID = 3532129761175646703L;

	//-------------------------------------------------------------------
	/**
	 */
	public PowerList() {
	}

	//-------------------------------------------------------------------
	/**
	 * @param c
	 */
	public PowerList(Collection<? extends Power> c) {
		super(c);
	}

	//-------------------------------------------------------------------
	public List<Power> getPowers() {
		return this;
	}
}
