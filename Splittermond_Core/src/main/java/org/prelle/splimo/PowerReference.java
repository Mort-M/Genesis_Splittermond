/**
 * 
 */
package org.prelle.splimo;

import java.util.List;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.splimo.modifications.PowerModification;
import org.prelle.splimo.persist.PowerConverter;

import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ModifyableImpl;

/**
 * @author prelle
 *
 */
public class PowerReference extends ModifyableImpl implements Comparable<PowerReference> {

	
	@Attribute(name="ref")
	@AttribConvert(PowerConverter.class)
	private Power power;
	@Attribute(required=false)
	private int count;
	
	/** Is assigned from a module - and not actively from the user */
	private transient boolean systemAssigned;
	/** Cannot be deselected by user (e.g. because it originates from race) */
	@Attribute
	private boolean fixed;
	
	//-------------------------------------------------------------------
	public PowerReference() {
		count = 1;
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
		if (power!=null && power.canBeUsedMultipleTimes())
			return String.valueOf(power)+" "+getModifiedCount()+" (mods="+modifications+")";
		return String.valueOf(power)+" (mods="+modifications+")";
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

	//-------------------------------------------------------------------
	/**
	 * @return the value
	 */
	public int getModifiedCount() {
		int sum = count;
		for (Modification mod : modifications) {
			if (mod instanceof PowerModification && ((PowerModification)mod).getPower()==power)
				sum+= 1;
		}
		return sum;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the systemAssigned
	 */
	public boolean isSystemAssigned() {
		return systemAssigned;
	}

	//-------------------------------------------------------------------
	/**
	 * @param systemAssigned the systemAssigned to set
	 */
	public void setSystemAssigned(boolean systemAssigned) {
		this.systemAssigned = systemAssigned;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the fixed
	 */
	public boolean isFixed() {
		return fixed;
	}

	//-------------------------------------------------------------------
	/**
	 * @param fixed the fixed to set
	 */
	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}

}
