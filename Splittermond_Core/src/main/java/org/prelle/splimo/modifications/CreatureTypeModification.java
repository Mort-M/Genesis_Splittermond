package org.prelle.splimo.modifications;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.creature.CreatureType;
import org.prelle.splimo.persist.CreatureTypeConverter;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
@Root(name = "creaturetypemod")
public class CreatureTypeModification extends ModificationImpl {

	@Attribute
	private boolean remove;
	@Attribute(required=true)
	@AttribConvert(CreatureTypeConverter.class)
    private CreatureType type;
	@Attribute
	private int level;
    
    //-----------------------------------------------------------------------
    public CreatureTypeModification() {
        remove = false;
    }
    
    //-----------------------------------------------------------------------
    public CreatureTypeModification(CreatureType feat, boolean rem) {
    	this.remove = rem;
    	this.type = feat;
    }
    
    //-----------------------------------------------------------------------
    public String toString() {
    	if (remove)
    		return "-"+type;
    	else
    		return "+"+type;
    }
    
    //-----------------------------------------------------------------------
    public CreatureType getFeature() {
        return type;
    }
    
    //-----------------------------------------------------------------------
    /**
     */
    public boolean equals(Object o) {
        if (o instanceof CreatureTypeModification) {
            CreatureTypeModification amod = (CreatureTypeModification)o;
            if (amod.getFeature()     !=type) return false;
            return amod.isRemoved()==remove;
        } else
            return false;
    }
    
    //-------------------------------------------------------
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Modification obj) {
        if (!(obj instanceof CreatureTypeModification))
            return toString().compareTo(obj.toString());
        CreatureTypeModification other = (CreatureTypeModification)obj;
        return type.getName().compareTo(other.getFeature().getName());
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
    
}// AttributeModification
