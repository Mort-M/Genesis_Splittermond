/**
 * 
 */
package org.prelle.splimo;

import java.util.ArrayList;
import java.util.List;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.splimo.persist.PowerConverter;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class PowerReference implements Comparable<PowerReference> {

	
	@Attribute(name="ref")
	@AttribConvert(PowerConverter.class)
	private Power power;
	@Attribute(required=false)
	private int count;

	/**
	 * For stackable powers with attached attribute modifications (e.g. "sturdy"),
	 * the previous made modifications are listed here to allow to modify them
	 */
	private transient List<Modification> modifications;
	
	//-------------------------------------------------------------------
	public PowerReference() {
		count = 1;
		modifications  = new ArrayList<>();
	}

	//-------------------------------------------------------------------
	public PowerReference(Power power) {
		this();
		this.power = power;
	}

	//-------------------------------------------------------------------
	public PowerReference(Power power, int count) {
		this();
		this.power = power;
		this.count = count;
	}

	//-------------------------------------------------------------------
	public boolean equals(Object o) {
		if (o instanceof PowerReference) {
			PowerReference other = (PowerReference)o;
			if (power!=other.getPower()) return false;
			return true;
		}
		return false;
	}

	//-------------------------------------------------------------------
	@Override
	public int hashCode() {
		return power.hashCode();
	}

	//-------------------------------------------------------------------
	public String toString() {
		return String.valueOf(power)+" "+count;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the skill
	 */
	public Power getPower() {
		return power;
	}

	//--------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(PowerReference other) {
		return power.compareTo(other.getPower());
	}

	//--------------------------------------------------------------------
	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	//--------------------------------------------------------------------
	/**
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the modifications
	 */
	public List<Modification> getModifications() {
		return modifications;
	}

}
