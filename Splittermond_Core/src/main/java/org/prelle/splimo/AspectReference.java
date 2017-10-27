/**
 * 
 */
package org.prelle.splimo;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.splimo.persist.CultureLoreConverter;

/**
 * @author prelle
 *
 */
public class AspectReference implements Comparable<AspectReference> {

	
	@Attribute(name="ref")
	@AttribConvert(CultureLoreConverter.class)
	private Aspect data;

	//-------------------------------------------------------------------
	public AspectReference() {
	}

	//-------------------------------------------------------------------
	public AspectReference(Aspect data) {
		this.data = data;
	}

	//-------------------------------------------------------------------
	public String toString() {
			return String.valueOf(data);
	}

	//-------------------------------------------------------------------
	public boolean equals(Object o) {
		if (o instanceof AspectReference) {
			AspectReference other = (AspectReference)o;
			if (data!=null) return data.equals(other.getAspect());
			return false;
		}
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the skill
	 */
	public Aspect getAspect() {
		return data;
	}

	//--------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(AspectReference other) {
		return toString().compareTo(other.toString());
	}

}
