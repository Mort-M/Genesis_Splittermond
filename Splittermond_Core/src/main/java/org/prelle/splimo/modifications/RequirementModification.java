package org.prelle.splimo.modifications;

import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Root;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
@Root(name = "reqmod")
public class RequirementModification extends ModificationImpl {

	@Attribute
	private RequirementType type;
	@Attribute
    private String ref;
    
    //-----------------------------------------------------------------------
    public RequirementModification() {
    }
    
    //-----------------------------------------------------------------------
    public String toString() {
        return type+"="+ref;
    }
    
    //-----------------------------------------------------------------------
    public RequirementType getType() {
        return type;
    }
    
    //-----------------------------------------------------------------------
    public void setType(RequirementType type) {
        this.type = type;
    }
    
    //-----------------------------------------------------------------------
    /**
     */
    public boolean equals(Object o) {
        if (o instanceof RequirementModification) {
            RequirementModification amod = (RequirementModification)o;
            if (amod.getType()     !=type) return false;
            return (amod.getReference()==ref);
        } else
            return false;
    }
    
    //-------------------------------------------------------
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Modification obj) {
        if (!(obj instanceof RequirementModification))
            return toString().compareTo(obj.toString());
        RequirementModification other = (RequirementModification)obj;
        int cmp = (new Integer(type.ordinal()).compareTo(new Integer(other.getType().ordinal())));
        if (cmp!=0) return cmp;
        return ref.compareTo(other.getReference());
    }

	//-------------------------------------------------------------------
	/**
	 * @return the ref
	 */
	public String getReference() {
		return ref;
	}

	//-------------------------------------------------------------------
	/**
	 * @param ref the ref to set
	 */
	public void setReference(String ref) {
		this.ref = ref;
	}
    
}// AttributeModification
