package org.prelle.splimo.requirements;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.persist.WeaponDamageConverter;

/**
 * @author prelle
 *
 */
@Root(name = "damagereq")
public class DamageRequirement extends Requirement {

	@org.prelle.simplepersist.Attribute
	@AttribConvert(WeaponDamageConverter.class)
    private int damage;
	
	private static transient WeaponDamageConverter CONV = new WeaponDamageConverter();
    
    //-----------------------------------------------------------------------
    public DamageRequirement() {
    }
    
    //-----------------------------------------------------------------------
    public DamageRequirement(int val) {
        this.damage  = val;
    }
    
    //-----------------------------------------------------------------------
    @Override
    public int hashCode() {
        return toString().hashCode();
    }
   
    //-----------------------------------------------------------------------
    public String toString() {
        return ">"+CONV.write(damage)+" "+SplitterMondCore.getI18nResources().getString("label.damage");
    }
    
    //-----------------------------------------------------------------------
    public Object clone() {
        return new DamageRequirement(damage);
    }
    
    //-----------------------------------------------------------------------
    /**
     */
    public boolean equals(Object o) {
        if (o instanceof DamageRequirement) {
            DamageRequirement amod = (DamageRequirement)o;
            return (amod.getDamage()==damage);
        } else
            return false;
    }

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.requirements.Requirement#resolve()
	 */
	@Override
	public boolean resolve() {
		// TODO Auto-generated method stub
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the damage
	 */
	public int getDamage() {
		return damage;
	}
   
}
