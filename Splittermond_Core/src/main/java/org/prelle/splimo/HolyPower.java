/**
 * 
 */
package org.prelle.splimo;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.splimo.items.Enhancement;
import org.prelle.splimo.persist.DeityTypeConverter;

/**
 * @author prelle
 *
 */
public class HolyPower extends Enhancement {

	@Attribute(name="dtype")
	@AttribConvert(DeityTypeConverter.class)
	private DeityType deityType;
	@Attribute
	private int level;

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
	 * @return the deityType
	 */
	public DeityType getDeityType() {
		return deityType;
	}

}
