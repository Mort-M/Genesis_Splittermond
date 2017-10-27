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
@Root(name="aspects")
@ElementList(entry="aspect",type=Aspect.class,inline=true)
public class AspectList extends ArrayList<Aspect> {

	private static final long serialVersionUID = 1L;

	//-------------------------------------------------------------------
	/**
	 */
	public AspectList() {
	}

	//-------------------------------------------------------------------
	/**
	 * @param c
	 */
	public AspectList(Collection<? extends Aspect> c) {
		super(c);
	}

}
