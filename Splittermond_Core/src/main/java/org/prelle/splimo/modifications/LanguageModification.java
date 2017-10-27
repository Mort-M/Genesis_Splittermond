/**
 * 
 */
package org.prelle.splimo.modifications;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.Language;
import org.prelle.splimo.persist.LanguageConverter;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
@Root(name = "languagemod")
public class LanguageModification extends ModificationImpl {
	
	@Attribute
	@AttribConvert(LanguageConverter.class)
	private Language ref;

	//-------------------------------------------------------------------
	/**
	 */
	public LanguageModification() {
	}

	//-------------------------------------------------------------------
	/**
	 */
	public LanguageModification(Language ref) {
		this.ref = ref;
	}

	//-------------------------------------------------------------------
	public String toString() {
		if (ref==null)
			return "REFERENCE_MISSING";
		return ref.getName();
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Modification other) {
		if (other instanceof LanguageModification)
			return ref.compareTo(((LanguageModification)other).getData());
		return 0;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the ref
	 */
	public Language getData() {
		return ref;
	}

}
