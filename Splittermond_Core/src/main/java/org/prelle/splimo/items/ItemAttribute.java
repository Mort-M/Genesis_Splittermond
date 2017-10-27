/**
 * 
 */
package org.prelle.splimo.items;

import org.prelle.splimo.SplitterMondCore;

/**
 * @author prelle
 *
 */
public enum ItemAttribute {
	
	LOAD,
	RIGIDITY,
	ROBUSTNESS,
	DAMAGE,
	SPEED,
	DAMAGE_REDUCTION,
	HANDICAP,
	TICK_MALUS, 
	DEFENSE,
	RANGE,
	COMPLEXITY,
	PRICE,
	AVAILABILITY,
	MIN_ATTRIBUTES,
	ATTRIBUTES,
	FEATURES,
	MATERIAL_TYPE,
	MATERIAL,
	INITIATIVE, // Only for creatures
	;

	//-------------------------------------------------------------------
	public String getShortName() {
		return SplitterMondCore.getI18nResources().getString("itemattribute."+this.name().toLowerCase()+".short");
	}

    //-------------------------------------------------------------------
    public String getName() {
        return SplitterMondCore.getI18nResources().getString("itemattribute."+this.name().toLowerCase());
    }

}
