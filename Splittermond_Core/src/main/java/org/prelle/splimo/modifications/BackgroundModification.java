/**
 * 
 */
package org.prelle.splimo.modifications;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.Background;
import org.prelle.splimo.persist.BackgroundConverter;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
@Root(name = "backmod")
public class BackgroundModification extends ModificationImpl {
	
	@Attribute
	@AttribConvert(BackgroundConverter.class)
	private Background ref;

	//-------------------------------------------------------------------
	/**
	 */
	public BackgroundModification() {
	}

	//-------------------------------------------------------------------
	/**
	 */
	public BackgroundModification(Background race) {
		this.ref = race;
	}

	//-------------------------------------------------------------------
	public String toString() {
		return "background="+ref;
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Modification other) {
		if (other instanceof BackgroundModification)
			return ref.compareTo(((BackgroundModification)other).getBackground());
		return 0;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the ref
	 */
	public Background getBackground() {
		return ref;
	}

}
