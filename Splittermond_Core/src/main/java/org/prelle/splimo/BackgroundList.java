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
@Root(name="backgrounds")
@ElementList(entry="background",type=Background.class)
public class BackgroundList extends ArrayList<Background> {

	private static final long serialVersionUID = 1L;

	//-------------------------------------------------------------------
	/**
	 */
	public BackgroundList() {
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @param c
	 */
	public BackgroundList(Collection<? extends Background> c) {
		super(c);
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	public List<Background> getData() {
		return this;
	}
}
