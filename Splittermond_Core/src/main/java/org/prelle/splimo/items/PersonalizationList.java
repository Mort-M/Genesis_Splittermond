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
@Root(name="personalizations")
@ElementList(entry="personalization",type=Personalization.class,inline=true)
public class PersonalizationList extends ArrayList<Personalization> {

	private static final long serialVersionUID = 2295522569612512624L;

	//-------------------------------------------------------------------
	/**
	 */
	public PersonalizationList() {
	}

	//-------------------------------------------------------------------
	/**
	 * @param c
	 */
	public PersonalizationList(Collection<? extends Personalization> c) {
		super(c);
	}

}
