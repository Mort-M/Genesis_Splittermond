/**
 * 
 */
package org.prelle.splimo.modifications;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.DamageType;
import org.prelle.splimo.persist.WeaponDamageConverter;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
@Root(name = "damagemod")
public class DamageModification extends ModificationImpl {

	@Attribute
	private boolean substract;
	@Attribute
	@AttribConvert(WeaponDamageConverter.class)
	private int damage;
	@Attribute
	private DamageType type;
	
	//-------------------------------------------------------------------
	public DamageModification() {
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Modification o) {
		return 0;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the substract
	 */
	public boolean isSubstract() {
		return substract;
	}

	//-------------------------------------------------------------------
	/**
	 * @param substract the substract to set
	 */
	public void setSubstract(boolean substract) {
		this.substract = substract;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the damage
	 */
	public int getDamage() {
		return damage;
	}

	//-------------------------------------------------------------------
	/**
	 * @param damage the damage to set
	 */
	public void setDamage(int damage) {
		this.damage = damage;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the type
	 */
	public DamageType getType() {
		return type;
	}

	//-------------------------------------------------------------------
	/**
	 * @param type the type to set
	 */
	public void setType(DamageType type) {
		this.type = type;
	}

}
