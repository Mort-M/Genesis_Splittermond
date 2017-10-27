/**
 * 
 */
package org.prelle.splimo.items;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.splimo.persist.FeatureTypeConverter;

/**
 * @author prelle
 *
 */
public class Feature implements Cloneable {
	
	@Attribute
	@AttribConvert(FeatureTypeConverter.class)
	private FeatureType type;
	@Attribute(required=false)
	private int level;

	//--------------------------------------------------------------------
	public Feature() {
	}

	//--------------------------------------------------------------------
	public Feature(FeatureType type) {
		this.type = type;
	}

	//--------------------------------------------------------------------
	public Feature(FeatureType type, int lvl) {
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
		if (o instanceof Feature) {
			Feature other = (Feature)o;
			if (type!=other.getType()) return false;
			return level==other.getLevel();
		}
		return false;
	}

	//--------------------------------------------------------------------
	/**
	 * @return the type
	 */
	public FeatureType getType() {
		return type;
	}

	//--------------------------------------------------------------------
	/**
	 * @param type the type to set
	 */
	public void setType(FeatureType type) {
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
	
	@Override
	public Feature clone() { 
		try {
			return (Feature) super.clone();
		} catch (CloneNotSupportedException e) {
			// should never happened
			throw new RuntimeException(e);
		}
	}

}
