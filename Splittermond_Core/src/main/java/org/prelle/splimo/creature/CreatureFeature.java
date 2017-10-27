/**
 * 
 */
package org.prelle.splimo.creature;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Element;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.DamageType;
import org.prelle.splimo.modifications.ModificationList;
import org.prelle.splimo.persist.CreatureFeatureTypeConverter;

/**
 * @author prelle
 *
 */
@Root(name="creaturefeature")
public class CreatureFeature {
	
	@Attribute
	@AttribConvert(CreatureFeatureTypeConverter.class)
	private CreatureFeatureType type;
	@Attribute(required=false)
	private int level;
	@Attribute(required=false)
	private int count;
	@Attribute(required=false,name="damage")
	private DamageType damageType;
	@Element(name="modifies")
	private ModificationList modifications;

	//-------------------------------------------------------------------
	public CreatureFeature() {
		modifications = new ModificationList();
	}

	//--------------------------------------------------------------------
	public CreatureFeature(CreatureFeatureType type) {
		this.type = type;
	}

	//--------------------------------------------------------------------
	public CreatureFeature(CreatureFeatureType type, int lvl) {
		this.type = type;
		this.level = lvl;
	}
	
	//--------------------------------------------------------------------
	public String getName() {
		String name = type.getName();
		if (damageType!=null)
			name = type.getName(damageType);
		if (level>0)
			name += " "+level;
		return name;
	}

	//--------------------------------------------------------------------
	public String toString() {
		return String.valueOf(type)+" "+level;
	}

	//--------------------------------------------------------------------
	public boolean equals(Object o) {
		if (o instanceof CreatureFeature) {
			CreatureFeature other = (CreatureFeature)o;
			if (type!=other.getType()) return false;
			return level==other.getLevel();
		}
		return false;
	}

	//--------------------------------------------------------------------
	/**
	 * @return the type
	 */
	public CreatureFeatureType getType() {
		return type;
	}

	//--------------------------------------------------------------------
	/**
	 * @param type the type to set
	 */
	public void setType(CreatureFeatureType type) {
		this.type = type;
	}

	//--------------------------------------------------------------------
	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	//--------------------------------------------------------------------
	/**
	 * @param level the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the damageType
	 */
	public DamageType getDamageType() {
		return damageType;
	}

	//-------------------------------------------------------------------
	/**
	 * @param damageType the damageType to set
	 */
	public void setDamageType(DamageType damageType) {
		this.damageType = damageType;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	//-------------------------------------------------------------------
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
	public ModificationList getModifications() {
		return modifications;
	}

	//-------------------------------------------------------------------
	/**
	 * @param modifications the modifications to set
	 */
	public void setModifications(ModificationList modifications) {
		this.modifications = modifications;
	}

}
