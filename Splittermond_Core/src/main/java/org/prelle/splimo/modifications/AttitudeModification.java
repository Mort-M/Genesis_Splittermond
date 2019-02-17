package org.prelle.splimo.modifications;

import org.prelle.simplepersist.Root;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
@Root(name = "attitudemod")
public class AttitudeModification extends ModificationImpl {

	@org.prelle.simplepersist.Attribute(required=false)
	private ModificationValueType type;
	@org.prelle.simplepersist.Attribute
    private int val;

    //-----------------------------------------------------------------------
    public AttitudeModification() {
        type = ModificationValueType.RELATIVE;
    }

    //-----------------------------------------------------------------------
    public AttitudeModification(int val) {
        type = ModificationValueType.RELATIVE;
        this.val  = val;
    }

    //-----------------------------------------------------------------------
    public AttitudeModification(ModificationValueType type, int val) {
        this.type = type;
        this.val  = val;
    }

    //-----------------------------------------------------------------------
    public AttitudeModification clone() {
    	AttitudeModification ret = new AttitudeModification(type, val);
    	ret.cloneAdd(this);
    	return ret;
    }

    //-----------------------------------------------------------------------
    public String toString() {
        if (type==ModificationValueType.RELATIVE) {
           return (val<0)?(" "+val):(" +"+val);
        }
        return " "+val;
    }

    //-----------------------------------------------------------------------
    public ModificationValueType getType() {
        return type;
    }

    //-----------------------------------------------------------------------
    public void setType(ModificationValueType type) {
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
    @Override
    public boolean equals(Object o) {
        if (o instanceof AttitudeModification) {
            AttitudeModification amod = (AttitudeModification)o;
            if (amod.getType()     !=type) return false;
            if (amod.getSource() !=source) return false;
            return (amod.getValue()==val);
        } else
            return false;
    }

    //-----------------------------------------------------------------------
    /**
     */
    public boolean matches(Object o) {
        if (o instanceof AttitudeModification) {
            AttitudeModification amod = (AttitudeModification)o;
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
        if (!(obj instanceof AttitudeModification))
            return toString().compareTo(obj.toString());
        AttitudeModification other = (AttitudeModification)obj;
        return (Integer.valueOf(type.ordinal()).compareTo(Integer.valueOf(other.getType().ordinal())));
    }

}// AttributeModification
