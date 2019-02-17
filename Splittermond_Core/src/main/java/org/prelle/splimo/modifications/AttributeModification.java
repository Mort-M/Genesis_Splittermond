package org.prelle.splimo.modifications;

import org.prelle.simplepersist.Root;
import org.prelle.splimo.Attribute;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
@Root(name = "attrmod")
public class AttributeModification extends ModificationImpl {

	@org.prelle.simplepersist.Attribute(required=false)
	private ModificationValueType type;
	@org.prelle.simplepersist.Attribute
    private Attribute attr;
	@org.prelle.simplepersist.Attribute
    private int val;
	@org.prelle.simplepersist.Attribute(name="modsrc")
    private ModificationSource modSource;

	/*
	 * Can be used to distinguish otherwise identical modifications
	 */
	private transient String uniqueID;

    //-----------------------------------------------------------------------
    public AttributeModification() {
        type = ModificationValueType.RELATIVE;
    }

    //-----------------------------------------------------------------------
    public AttributeModification(Attribute attr, int val) {
    	this();
        this.attr = attr;
        this.val  = val;
     }

    //-----------------------------------------------------------------------
    public AttributeModification(Attribute attr, int val, ModificationSource modSrc) {
    	this();
        this.attr = attr;
        this.val  = val;
        this.modSource = modSrc;
    }

    //-----------------------------------------------------------------------
    public AttributeModification(Attribute attr, int val, Object source) {
    	this();
        this.attr = attr;
        this.val  = val;
        this.source = source;
    }

    //-----------------------------------------------------------------------
    public AttributeModification(ModificationValueType type, Attribute attr, int val) {
        this.type = type;
        this.attr = attr;
        this.val  = val;
    }

    //-----------------------------------------------------------------------
    public AttributeModification clone() {
    	AttributeModification ret = new AttributeModification(type, attr, val);
    	ret.cloneAdd(this);
    	return ret;
    }

    //-----------------------------------------------------------------------
    public String toString() {
        if (type==ModificationValueType.RELATIVE) {
        	if (attr!=null)
      			return attr.getShortName()+((val<0)?(" "+val):(" +"+val))+" ("+modSource+")";
           return attr+((val<0)?(" "+val):(" +"+val));
        }
    	if (attr!=null)
  			return attr.getShortName()+" "+val;
        return attr+" "+val;
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
    public Attribute getAttribute() {
        return attr;
    }

    //-----------------------------------------------------------------------
    public void setAttribute(Attribute attr) {
        this.attr = attr;
    }

    //-----------------------------------------------------------------------
    public int getValue() {
        return val;
    }

    //-----------------------------------------------------------------------
    public void setValue(int val) {
        this.val = val;
    }

//    //-----------------------------------------------------------------------
//    @Override
//    public boolean equals(Object o) {
//        if (o instanceof AttributeModification) {
//            AttributeModification amod = (AttributeModification)o;
//            if (amod.getType()     !=type) return false;
//            if (amod.getAttribute()!=attr) return false;
//            if (amod.getSource() !=source) return false;
//            if (amod.getUniqueID()!=null) {
//            	if (uniqueID==null) return false;
//            	if (!uniqueID.equals(amod.getUniqueID()))
//            		return false;
//            } else if (uniqueID!=null)
//            	return false;
//            System.out.println("AttrMod.equals: "+uniqueID+" == "+amod.getUniqueID());
//            return (amod.getValue()==val);
//        } else
//            return false;
//    }

    //-----------------------------------------------------------------------
    /**
     */
    public boolean matches(Object o) {
        if (o instanceof AttributeModification) {
            AttributeModification amod = (AttributeModification)o;
            if (amod.getType()     !=type) return false;
            if (amod.getAttribute()!=attr) return false;
            return true;
        } else
            return false;
    }

    //-------------------------------------------------------
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Modification obj) {
        if (!(obj instanceof AttributeModification))
            return toString().compareTo(obj.toString());
        AttributeModification other = (AttributeModification)obj;
        if (attr!=other.getAttribute())
            return (Integer.valueOf(attr.ordinal())).compareTo(Integer.valueOf(other.getAttribute().ordinal()));
        return (Integer.valueOf(type.ordinal()).compareTo(Integer.valueOf(other.getType().ordinal())));
    }

	//-------------------------------------------------------------------
	/**
	 * @return the uniqueID
	 */
	public String getUniqueID() {
		return uniqueID;
	}

	//-------------------------------------------------------------------
	/**
	 * @param uniqueID the uniqueID to set
	 */
	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the modSource
	 */
	public ModificationSource getModificationSource() {
		return modSource;
	}

	//-------------------------------------------------------------------
	/**
	 * @param modSource the modSource to set
	 */
	public void setModificationSource(ModificationSource modSource) {
		this.modSource = modSource;
	}

}// AttributeModification
