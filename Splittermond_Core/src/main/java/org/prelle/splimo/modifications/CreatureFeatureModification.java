package org.prelle.splimo.modifications;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.DamageType;
import org.prelle.splimo.creature.CreatureFeatureType;
import org.prelle.splimo.persist.CreatureFeatureTypeConverter;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
@Root(name = "creaturefeaturemod")
public class CreatureFeatureModification extends ModificationImpl {

	@Attribute
	private boolean remove;
	@Attribute(required=true)
	@AttribConvert(CreatureFeatureTypeConverter.class)
    private CreatureFeatureType feature;
	@Attribute
	private int level;
	@Attribute
	private DamageType damagetype;
    
    //-----------------------------------------------------------------------
    public CreatureFeatureModification() {
        remove = false;
    }
    
    //-----------------------------------------------------------------------
    public CreatureFeatureModification(CreatureFeatureType feat, boolean rem) {
    	this.remove = rem;
    	this.feature = feat;
    }
    
    //-----------------------------------------------------------------------
    public CreatureFeatureModification(CreatureFeatureType feat, int level) {
    	this.remove = false;
    	this.feature = feat;
    	this.level  = level;
    }
    
    //-----------------------------------------------------------------------
    public String toString() {
    	if (remove)
    		return "-"+feature;
    	else
    		return "+"+feature;
    }
    
    //-----------------------------------------------------------------------
    public CreatureFeatureType getFeature() {
        return feature;
    }
    
    //-----------------------------------------------------------------------
    /**
     */
    public boolean equals(Object o) {
        if (o instanceof CreatureFeatureModification) {
            CreatureFeatureModification amod = (CreatureFeatureModification)o;
            if (amod.getFeature()     !=feature) return false;
            if (amod.getDamageType()  !=damagetype) return false;
            return amod.isRemoved()==remove;
        } else
            return false;
    }
    
    //-------------------------------------------------------
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Modification obj) {
        if (!(obj instanceof CreatureFeatureModification))
            return toString().compareTo(obj.toString());
        CreatureFeatureModification other = (CreatureFeatureModification)obj;
        return feature.getName().compareTo(other.getFeature().getName());
    }

	//-------------------------------------------------------------------
	/**
	 * @return the remove
	 */
	public boolean isRemoved() {
		return remove;
	}

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
	 * @return the damagetype
	 */
	public DamageType getDamageType() {
		return damagetype;
	}

	//-------------------------------------------------------------------
	/**
	 * @param damagetype the damagetype to set
	 */
	public void setDamageType(DamageType damagetype) {
		this.damagetype = damagetype;
	}
    
}
