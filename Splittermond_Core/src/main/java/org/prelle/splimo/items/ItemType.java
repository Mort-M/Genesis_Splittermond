/**
 * 
 */
package org.prelle.splimo.items;

import org.prelle.splimo.SplitterMondCore;

/**
 * @author prelle
 *
 */
public enum ItemType {
	
	WEAPON,
	LONG_RANGE_WEAPON,
	PROJECTILE,
	ARMOR,
    SHIELD,
    CONTAINER,
    POTION,
    TRAVEL,
    TOOLS,
    ANIMALS,
    CLOTHING,
    SHADY,
    LIGHT,
	CREATURES,
	HEALING,
	RECREATION,
	WEAPONITEMS,
	WRITING,
	OTHER,
    ;

    public String getName() {
        return SplitterMondCore.getI18nResources().getString("itemtype."+name().toLowerCase());
    }

}
