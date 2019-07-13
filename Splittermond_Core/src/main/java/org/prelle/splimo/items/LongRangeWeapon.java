/**
 * 
 */
package org.prelle.splimo.items;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.simplepersist.Root;

/**
 * @author prelle
 *
 */
@Root(name = "rangeweapon")
public class LongRangeWeapon extends Weapon {

	private static Logger logger = LogManager.getLogger("splittermond.items");
	
	@org.prelle.simplepersist.Attribute
	private int range;
	@org.prelle.simplepersist.Attribute(name="price_amo",required=false)
	private int ammuPrice;
	
	//--------------------------------------------------------------------
	public LongRangeWeapon() {
		type     = ItemType.LONG_RANGE_WEAPON;
	}
	
	//--------------------------------------------------------------------
	public String toString() {
		return "RangeWeapon(dmg="+damage+",spd="+speed+",a1="+attribute1+",a2="+attribute2+",range="+range+",skill="+skill+")";
	}
	
	//--------------------------------------------------------------------
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if (o instanceof LongRangeWeapon) {
            LongRangeWeapon other = (LongRangeWeapon) o;
            try {
                if (!super.equals(other)) return false;
                if (ammuPrice  != other.getAmmunitionPrice()) return false;
                if (range      != other.getRange()) return false;
                return true;
            } catch (Exception e) {
                logger.error("Error comparing items",e);
            }
        }
		return false;
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
	 * @return the price_amo
	 */
	public int getAmmunitionPrice() {
		return ammuPrice;
	}

	//-------------------------------------------------------------------
	/**
	 * @param price_amo the price_amo to set
	 */
	public void setAmmunitionPrice(int price) {
		this.ammuPrice = price;
	}

	//-------------------------------------------------------------------
	/**
	 * @param range the range to set
	 */
	public void setRange(int range) {
		this.range = range;
	}

}
