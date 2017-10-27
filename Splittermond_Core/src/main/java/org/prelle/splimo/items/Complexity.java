/**
 * 
 */
package org.prelle.splimo.items;

import org.prelle.splimo.SplitterMondCore;

/**
 * @author prelle
 *
 */
public enum Complexity {

	INEXPERIENCED("U"),
	CRAFTSMEN("G"),
	EXPERT("F"),
	MASTER("M"),
	RECIPE("A")
	;
	
	private String id;
	
	//--------------------------------------------------------------------
	private Complexity(String literal) {
		this.id = literal;
	}
	
	//--------------------------------------------------------------------
	public String getID() {
		return id;
	}
	
	
	//--------------------------------------------------------------------
	public static Complexity getByID(String literal) {
		for (Complexity tmp : Complexity.values())
			if (tmp.getID().equals(literal))
				return tmp;
		throw new IllegalArgumentException("No complexity '"+literal+"'");
	}

    public String getName() {
        return SplitterMondCore.getI18nResources().getString("complexity."+name().toLowerCase());
    }

}
