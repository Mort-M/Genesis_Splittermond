/**
 * 
 */
package org.prelle.splimo.items;

import org.prelle.splimo.SplitterMondCore;

/**
 * @author prelle
 *
 */
public enum MaterialType {
	
	UNKNOWN,
	FABRIC,
	WOOD,
	LEATHER,
	METAL,
	OTHER
	;

    //-------------------------------------------------------------------
    public String getName() {
        return SplitterMondCore.getI18nResources().getString("materialtype."+this.name().toLowerCase());
    }

}
