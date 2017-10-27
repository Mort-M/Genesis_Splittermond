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
@Root(name="culturelores")
@ElementList(entry="culturelore",type=CultureLore.class)
public class CultureLoreList extends ArrayList<CultureLore> {

	private static final long serialVersionUID = 1L;

	//-------------------------------------------------------------------
	/**
	 */
	public CultureLoreList() {
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @param c
	 */
	public CultureLoreList(Collection<? extends CultureLore> c) {
		super(c);
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	public List<CultureLore> getData() {
		return this;
	}
}
