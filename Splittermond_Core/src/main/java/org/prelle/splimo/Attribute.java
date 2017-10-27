/**
 * 
 */
package org.prelle.splimo;

/**
 * @author prelle
 *
 */
public enum Attribute {

	CHARISMA,
	AGILITY,
	INTUITION,
	CONSTITUTION,
	MYSTIC,
	STRENGTH,
	MIND,
	WILLPOWER,
	SPLINTER,
	
	SIZE,
	SPEED,
	INITIATIVE,
	LIFE,
	FOCUS,
	DEFENSE,
	DAMAGE_REDUCTION,
	MINDRESIST,
	BODYRESIST,
	;

	//-------------------------------------------------------------------
	public String getShortName() {
		return SplitterMondCore.getI18nResources().getString("attribute."+this.name().toLowerCase()+".short");
	}

    //-------------------------------------------------------------------
    public String getName() {
        return SplitterMondCore.getI18nResources().getString("attribute."+this.name().toLowerCase());
    }
	
	//-------------------------------------------------------------------
	public static Attribute[] primaryValues() {
		return new Attribute[]{CHARISMA,AGILITY,INTUITION,CONSTITUTION,MYSTIC,STRENGTH,MIND,WILLPOWER};
	}
	
	//-------------------------------------------------------------------
	public static Attribute[] secondaryValues() {
		return new Attribute[]{SIZE,SPEED,INITIATIVE,LIFE,FOCUS,DEFENSE,DAMAGE_REDUCTION,BODYRESIST,MINDRESIST};
	}
	
	//-------------------------------------------------------------------
	public boolean isPrimary() {
		for (Attribute key : primaryValues())
			if (this==key) return true;
		return false;
	}

}
