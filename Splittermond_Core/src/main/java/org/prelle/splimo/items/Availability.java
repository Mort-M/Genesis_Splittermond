/**
 * 
 */
package org.prelle.splimo.items;

import org.prelle.splimo.SplitterMondCore;

/**
 * @author prelle
 *
 */
public enum Availability {

	VILLAGE,
	SMALL_TOWN,
	LARGE_TOWN,
	CAPITAL,
	NONE;

    public String getName() {
        return SplitterMondCore.getI18nResources().getString("availability."+name().toLowerCase());
    }

    public static Availability getByName(String name) {
    	for (Availability avail : Availability.values())
    		if (name.equalsIgnoreCase(avail.getName()))
    			return avail;
    	throw new IllegalArgumentException(name);
    }

}
