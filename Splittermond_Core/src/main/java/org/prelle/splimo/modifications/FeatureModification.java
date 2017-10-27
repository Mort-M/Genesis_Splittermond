package org.prelle.splimo.modifications;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.items.FeatureType;
import org.prelle.splimo.persist.FeatureTypeConverter;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
@Root(name = "featuremod")
public class FeatureModification extends ModificationImpl {

	@Attribute
	private boolean remove;
	@Attribute(required=true)
	@AttribConvert(FeatureTypeConverter.class)
    private FeatureType feature;
	@Attribute
	private int level;
   
    //-----------------------------------------------------------------------
    public FeatureModification() {
        remove = false;
    }
    
    //-----------------------------------------------------------------------
    public FeatureModification(FeatureType feat, boolean rem) {
    	this.remove = rem;
    	this.feature = feat;
    }
    
    //-----------------------------------------------------------------------
    public String toString() {
    	if (remove)
    		return "-"+feature;
    	else
    		return "+"+feature;
    }
    
    //-----------------------------------------------------------------------
    public FeatureType getFeature() {
        return feature;
    }
    
    //-----------------------------------------------------------------------
    /**
     */
    public boolean equals(Object o) {
        if (o instanceof FeatureModification) {
            FeatureModification amod = (FeatureModification)o;
            if (amod.getFeature()     !=feature) return false;
            return amod.isRemoved()==remove;
        } else
            return false;
    }
    
    //-------------------------------------------------------
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Modification obj) {
        if (!(obj instanceof FeatureModification))
            return toString().compareTo(obj.toString());
        FeatureModification other = (FeatureModification)obj;
        return feature.compareTo(other.getFeature());
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
