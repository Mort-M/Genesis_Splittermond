/**
 * 
 */
package org.prelle.splimo.modifications;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.Power;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.persist.PowerConverter;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
@Root(name = "powermod")
public class PowerModification extends ModificationImpl {
	
	@Attribute
	@AttribConvert(PowerConverter.class)
	private Power ref;

	//-------------------------------------------------------------------
	/**
	 */
	public PowerModification() {
	}

	//-------------------------------------------------------------------
	/**
	 */
	public PowerModification(Power race) {
		this.ref = race;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.modifications.ModificationImpl#clone()
	 */
	@Override
	public PowerModification clone() {
		PowerModification ret = new PowerModification(ref);
    	ret.cloneAdd(this);
    	return ret;
	}

	//-------------------------------------------------------------------
	public String toString() {
		if (ref!=null)
			return SplitterMondCore.getI18nResources().getString("label.power")+" "+ref.getName();
		return "power="+ref;
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Modification other) {
		if (other instanceof PowerModification)
			return ref.compareTo(((PowerModification)other).getPower());
		return 0;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the ref
	 */
	public Power getPower() {
		return ref;
	}

}
