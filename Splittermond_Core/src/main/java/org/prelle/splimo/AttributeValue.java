/**
 * 
 */
package org.prelle.splimo;

import org.prelle.simplepersist.Root;
import org.prelle.splimo.modifications.AttributeModification;
import org.prelle.splimo.modifications.ModificationSource;

import de.rpgframework.genericrpg.NumericalValue;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ModifyableImpl;

/**
 * @author prelle
 *
 */
@Root(name="attr")
public class AttributeValue extends ModifyableImpl implements Comparable<AttributeValue>, NumericalValue<Attribute> {

	@org.prelle.simplepersist.Attribute(name="id",required=true)
	private Attribute id;

	/**
	 * The recent value of the attribute, including modifications by
	 * race on generation, the distributed points on generation
	 * and the points bought.
	 * Bought = unmodifiedValue - start;
	 */
	@org.prelle.simplepersist.Attribute(name="value",required=true)
	private int distributed;

	/**
	 * The final value of the attribute after generation, before
	 * exp have been spent
	 */
	@org.prelle.simplepersist.Attribute(name="start",required=false)
	private int start;

//	/**
//	 * Modificator chosen by character generation
//	 */
//	@org.prelle.simplepersist.Attribute(name="mod",required=false)
//	private int generationModifier;
//
//	/**
//	 * The recent value of the attribute, including modifications by
//	 * race on generation, the distributed points on generation
//	 * and the points bought.
//	 * Bought = unmodifiedValue - start;
//	 */
//	@org.prelle.simplepersist.Attribute(name="value",required=false)
//	private int unmodifiedValue;
	
	private transient int modifierCap;

	//-------------------------------------------------------------------
	public AttributeValue() {
	}

	//-------------------------------------------------------------------
	public AttributeValue(Attribute attr, int val) {
		this.id = attr;
		this.distributed = val;
		this.start = val;
	}

	//-------------------------------------------------------------------
	public AttributeValue(AttributeValue copy) {
		this.id = copy.getAttribute();
		this.start = copy.getStart();
		this.distributed = copy.getDistributed();
	}

	//-------------------------------------------------------------------
	public String toString() {
		return String.format("%s  \tSTART=%d  NORM=%d MOD=%d  VAL=%d  CAP=%d", id, start, distributed, getModifier(), getValue(),modifierCap);
	}

	//-------------------------------------------------------------------
	public int getModifier() {
		int count = 0;
		int countEquip = 0;
		int countMagic = 0;
		for (Modification mod : modifications) {
			if (mod instanceof AttributeModification) {
				AttributeModification aMod = (AttributeModification)mod;
				if (aMod.isConditional())
					continue;
				if (aMod.getAttribute()==id) {
					if (aMod.getModificationSource()==ModificationSource.EQUIPMENT)
						countEquip += aMod.getValue();
					else if (aMod.getModificationSource()==ModificationSource.MAGICAL)
						countMagic += aMod.getValue();
					else
						count += aMod.getValue();
//					System.out.println("... after "+aMod+"  counts are "+count+"/"+countEquip+"/"+countMagic);
				}
			}
		}
		
		if (modifierCap>0) {
			count += Math.min(countMagic, modifierCap);
			count += Math.min(countEquip, modifierCap);
		} else {
			count += countMagic;
			count += countEquip;
		}
		
		return count;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the value
	 */
	public int getValue() {
		return distributed + getModifier();
	}

	//-------------------------------------------------------------------
	/**
	 * @return the id
	 */
	public Attribute getAttribute() {
		return id;
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(AttributeValue other) {
		return id.compareTo(other.getAttribute());
	}

	//-------------------------------------------------------------------
	/**
	 * @return the distributed
	 */
	public int getDistributed() {
		return distributed;
	}

	//-------------------------------------------------------------------
	/**
	 * @param distributed the distributed to set
	 */
	public void setDistributed(int distributed) {
		this.distributed = distributed;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the start
	 */
	public int getStart() {
		return start;
	}

	//-------------------------------------------------------------------
	/**
	 * @param start the start to set
	 */
	public void setStart(int start) {
		this.start = start;
	}

	//-------------------------------------------------------------------
	/**
	 * Points gained by investing EXP
	 * @return the v1Value
	 */
	public int getBought() {
		return distributed - start;
	}

	//-------------------------------------------------------------------
	/**
	 * @param modifierCap the modifierCap to set
	 */
	public void setModifierCap(int modifierCap) {
		this.modifierCap = modifierCap;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.SelectedValue#getModifyable()
	 */
	@Override
	public Attribute getModifyable() {
		return getAttribute();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValue#getPoints()
	 */
	@Override
	public int getPoints() {
		return getDistributed();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValue#setPoints(int)
	 */
	@Override
	public void setPoints(int points) {
		setDistributed(points);
	}

}
