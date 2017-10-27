/**
 * 
 */
package org.prelle.splimo;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.splimo.persist.LanguageConverter;

/**
 * @author prelle
 *
 */
public class LanguageReference implements Comparable<LanguageReference> {

	
	@Attribute(name="ref")
	@AttribConvert(LanguageConverter.class)
	private Language data;

	//-------------------------------------------------------------------
	public LanguageReference() {
	}

	//-------------------------------------------------------------------
	public LanguageReference(Language data) {
		this.data = data;
	}

	//-------------------------------------------------------------------
	public String toString() {
			return String.valueOf(data);
	}

	//-------------------------------------------------------------------
	public boolean equals(Object o) {
		if (o instanceof LanguageReference) {
			LanguageReference other = (LanguageReference)o;
			if (data!=null) return data.equals(other.getLanguage());
			return false;
		}
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the skill
	 */
	public Language getLanguage() {
		return data;
	}

	//--------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(LanguageReference other) {
		return toString().compareTo(other.toString());
	}

}
