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
@Root(name="cultures")
@ElementList(entry="culture",type=Culture.class,inline=true)
public class CultureList extends ArrayList<Culture> {

	private static final long serialVersionUID = 1L;

	//-------------------------------------------------------------------
	/**
	 */
	public CultureList() {
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @param c
	 */
	public CultureList(Collection<? extends Culture> c) {
		super(c);
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	public List<Culture> getData() {
		return this;
	}
}
