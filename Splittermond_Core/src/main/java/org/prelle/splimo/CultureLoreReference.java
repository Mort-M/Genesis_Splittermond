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
public class CultureLoreReference implements Comparable<CultureLoreReference> {

	
	@Attribute(name="ref")
	@AttribConvert(CultureLoreConverter.class)
	private CultureLore data;

	//-------------------------------------------------------------------
	public CultureLoreReference() {
	}

	//-------------------------------------------------------------------
	public CultureLoreReference(CultureLore data) {
		this.data = data;
	}

	//-------------------------------------------------------------------
	public String toString() {
			return String.valueOf(data);
	}

	//-------------------------------------------------------------------
	public boolean equals(Object o) {
		if (o instanceof CultureLoreReference) {
			CultureLoreReference other = (CultureLoreReference)o;
			if (data!=null) return data.equals(other.getCultureLore());
			return false;
		}
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the skill
	 */
	public CultureLore getCultureLore() {
		return data;
	}

	//--------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(CultureLoreReference other) {
		return toString().compareTo(other.toString());
	}

}
