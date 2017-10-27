/**
 * 
 */
package org.prelle.splimo.modifications;

import java.util.PropertyResourceBundle;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.CultureLore;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.persist.CultureLoreConverter;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
@Root(name = "cultureloremod")
public class CultureLoreModification extends ModificationImpl {
	
	private static PropertyResourceBundle res = SplitterMondCore.getI18nResources();
	
	@Attribute
	@AttribConvert(CultureLoreConverter.class)
	private CultureLore ref;

	//-------------------------------------------------------------------
	/**
	 */
	public CultureLoreModification() {
	}

	//-------------------------------------------------------------------
	/**
	 */
	public CultureLoreModification(CultureLore ref) {
		this.ref = ref;
	}

	//-------------------------------------------------------------------
	public String toString() {
		return res.getString("label.culturelore")+" "+ref;
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Modification other) {
		if (other instanceof CultureLoreModification)
			return ref.compareTo(((CultureLoreModification)other).getData());
		return 0;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the ref
	 */
	public CultureLore getData() {
		return ref;
	}

}
