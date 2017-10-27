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
@Root(name = "races")
@ElementList(entry="race",type=Race.class,inline=true)
public class RaceList extends ArrayList<Race> {

	private static final long serialVersionUID = 1L;

	//-------------------------------------------------------------------
	/**
	 */
	public RaceList() {
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @param c
	 */
	public RaceList(Collection<? extends Race> c) {
		super(c);
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	public List<Race> getRaces() {
		return this;
	}
}
