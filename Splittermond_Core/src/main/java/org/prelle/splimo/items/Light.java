/**
 * 
 */
package org.prelle.splimo.items;

import org.prelle.simplepersist.Root;

/**
 * @author prelle
 *
 */
@Root(name = "light")
public class Light extends ItemTypeData {

	@org.prelle.simplepersist.Attribute
	protected int level;
	@org.prelle.simplepersist.Attribute
	protected int range;
	
	//--------------------------------------------------------------------
	public Light() {
		super(ItemType.LIGHT);
	}
	
	//--------------------------------------------------------------------
	public String toString() {
		return "Light(level="+level+", range="+range+"m)";
	}
	
	//--------------------------------------------------------------------
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if (o instanceof Light) {
			Light other = (Light)o;
			if (!super.equals(other)) return false;
            if (level != other.getLevel()) return false;
            if (range != other.getRange()) return false;
			return true;
		}
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	//-------------------------------------------------------------------
	/**
	 * @param level the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the range
	 */
	public int getRange() {
		return range;
	}

	//-------------------------------------------------------------------
	/**
	 * @param range the range to set
	 */
	public void setRange(int range) {
		this.range = range;
	}

}
