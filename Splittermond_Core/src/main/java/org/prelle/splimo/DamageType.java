/**
 * 
 */
package org.prelle.splimo;

/**
 * @author prelle
 *
 */
public enum DamageType {

	PROFANE,
	
	FIRE,
	WATER,
	STONE,
	AIR,
	
	LIGHT,
	SHADOW,
	;
	
	public String getName() {
		return SplitterMondCore.getI18nResources().getString("damagetype."+name().toLowerCase());
	}
}
