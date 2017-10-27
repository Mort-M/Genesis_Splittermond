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
@Root(name="resources")
@ElementList(entry="resource",type=Resource.class,inline=true)
public class ResourceList extends ArrayList<Resource> {

	private static final long serialVersionUID = 1L;

	//-------------------------------------------------------------------
	/**
	 */
	public ResourceList() {
	}

	//-------------------------------------------------------------------
	/**
	 * @param c
	 */
	public ResourceList(Collection<? extends Resource> c) {
		super(c);
	}

	//-------------------------------------------------------------------
	public List<Resource> getResources() {
		return this;
	}
}
