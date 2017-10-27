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
@Root(name = "educations")
@ElementList(entry="education",type=Education.class)
public class EducationList extends ArrayList<Education> {

	private static final long serialVersionUID = 1L;

	//-------------------------------------------------------------------
	/**
	 */
	public EducationList() {
	}

	//-------------------------------------------------------------------
	/**
	 * @param c
	 */
	public EducationList(Collection<? extends Education> c) {
		super(c);
	}

	//-------------------------------------------------------------------
	public List<Education> getData() {
		return this;
	}
}
