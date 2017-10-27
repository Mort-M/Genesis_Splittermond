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
@Root(name="holypowers")
@ElementList(entry="holypower",type=HolyPower.class)
public class HolyPowerList extends ArrayList<HolyPower> {

	private static final long serialVersionUID = 1L;

	//-------------------------------------------------------------------
	public HolyPowerList() {
	}

	//-------------------------------------------------------------------
	public HolyPowerList(Collection<? extends HolyPower> c) {
		super(c);
	}

}
