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
@Root(name="deitytypes")
@ElementList(entry="deitytype",type=DeityType.class)
public class DeityTypeList extends ArrayList<DeityType> {

	private static final long serialVersionUID = 1L;

	//-------------------------------------------------------------------
	public DeityTypeList() {
	}

	//-------------------------------------------------------------------
	public DeityTypeList(Collection<? extends DeityType> c) {
		super(c);
	}

}
