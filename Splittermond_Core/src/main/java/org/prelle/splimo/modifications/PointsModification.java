package org.prelle.splimo.modifications;

import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Root;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
@Root(name = "pointsmod")
public class PointsModification extends ModificationImpl {

	@Attribute
	private PointsModType type;
	@Attribute
	private int val;

    //-----------------------------------------------------------------------
    public PointsModification() {
    }

    //-----------------------------------------------------------------------
    public PointsModification(PointsModType type, int val) {
        this.type = type;
        this.val  = val;
    }

    //-----------------------------------------------------------------------
    public PointsModification clone() {
    	PointsModification ret = new PointsModification(type, val);
    	ret.cloneAdd(this);
    	return ret;
    }

    //-----------------------------------------------------------------------
    public String toString() {
    	if (type==null)
    		return "PowerMod(type=null,val="+val+")";
    	switch (type) {
    	case POWER:
    		return "Points[Power]+"+val;
    	default:
    		return "Unknown type["+type+"]";
    	}
    }

    //-----------------------------------------------------------------------
    public PointsModType getType() {
        return type;
    }

    //-----------------------------------------------------------------------
    public void setType(PointsModType type) {
        this.type = type;
    }

    //-----------------------------------------------------------------------
    public int getValue() {
        return val;
    }

    //-----------------------------------------------------------------------
    public void setValue(int val) {
        this.val = val;
    }

    //-----------------------------------------------------------------------
    /**
     */
    public boolean equals(Object o) {
        if (o instanceof PointsModification) {
            PointsModification amod = (PointsModification)o;
            if (amod.getType()     !=type) return false;
            return (amod.getValue()==val);
        } else
            return false;
    }

    //-----------------------------------------------------------------------
    /**
     */
    public boolean matches(Object o) {
        if (o instanceof PointsModification) {
            PointsModification amod = (PointsModification)o;
            if (amod.getType()     !=type) return false;
             return true;
        } else
            return false;
    }

    //-------------------------------------------------------
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Modification obj) {
        if (!(obj instanceof PointsModification))
            return toString().compareTo(obj.toString());
        PointsModification other = (PointsModification)obj;
         return (Integer.valueOf(type.ordinal()).compareTo(Integer.valueOf(other.getType().ordinal())));
    }

}// AttributeModification
