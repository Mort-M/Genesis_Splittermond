/**
 * 
 */
package org.prelle.splimo.creature;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.persist.CreatureTypeConverter;

/**
 * @author prelle
 *
 */
@Root(name="creaturetype")
public class CreatureTypeValue {
	
	@Attribute
	@AttribConvert(CreatureTypeConverter.class)
	private CreatureType type;
	@Attribute(required=false)
	private int level;
	

	//-------------------------------------------------------------------
	public CreatureTypeValue() {
		// TODO Auto-generated constructor stub
	}

	//--------------------------------------------------------------------
	public CreatureTypeValue(CreatureType type) {
		this.type = type;
	}

	//--------------------------------------------------------------------
	public CreatureTypeValue(CreatureType type, int lvl) {
		this.type = type;
		this.level = lvl;
	}
	
	//--------------------------------------------------------------------
	public String getName() {
		if (level==0)
			return type.getName();
		return type.getName()+" "+level;
	}

	//--------------------------------------------------------------------
	public String toString() {
		return String.valueOf(type)+" "+level;
	}

	//--------------------------------------------------------------------
	public boolean equals(Object o) {
		if (o instanceof CreatureTypeValue) {
			CreatureTypeValue other = (CreatureTypeValue)o;
			if (type!=other.getType()) return false;
			return level==other.getLevel();
		}
		return false;
	}

	//--------------------------------------------------------------------
	/**
	 * @return the type
	 */
	public CreatureType getType() {
		return type;
	}

	//--------------------------------------------------------------------
	/**
	 * @param type the type to set
	 */
	public void setType(CreatureType type) {
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

}
