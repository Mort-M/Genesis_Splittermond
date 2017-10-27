/**
 * 
 */
package org.prelle.splimo.items;

import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Root;

/**
 * @author prelle
 *
 */
@Root(name = "armor")
public class Armor extends Shield {

	@Attribute(name="dr")
	private int damageReduction;
	
	//--------------------------------------------------------------------
	public Armor() {
		type = ItemType.ARMOR;
	}
	
	//--------------------------------------------------------------------
	public String toString() {
		return "Armor(dr="+damageReduction+",def="+defense+",hc="+handicap+",tm="+tickMalus+",req="+requires+")";
	}
	
	//--------------------------------------------------------------------
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if (o instanceof Armor) {
			Armor other = (Armor)o;
			if (!super.equals(other)) return false;
			if (damageReduction!=other.getDamageReduction()) return false;
			return true;
		}
		return false;
	}

	//--------------------------------------------------------------------
	/**
	 * @return the damageReduction
	 */
	public int getDamageReduction() {
		return damageReduction;
	}

	//--------------------------------------------------------------------
	/**
	 * @param damageReduction the damageReduction to set
	 */
	public void setDamageReduction(int damageReduction) {
		this.damageReduction = damageReduction;
	}

}
