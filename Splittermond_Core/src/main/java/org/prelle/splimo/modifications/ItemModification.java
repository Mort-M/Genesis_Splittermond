package org.prelle.splimo.modifications;

import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.items.ItemAttribute;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
@Root(name = "itemmod")
public class ItemModification extends ModificationImpl {

	@Attribute
	private ModificationValueType type;
	@Attribute
    private ItemAttribute attr;
	@Attribute
    private int val;

    //-----------------------------------------------------------------------
    public ItemModification() {
        type = ModificationValueType.RELATIVE;
    }

    //-----------------------------------------------------------------------
    public ItemModification(ItemAttribute attr, int val) {
        type = ModificationValueType.RELATIVE;
        this.attr = attr;
        this.val  = val;
    }

    //-----------------------------------------------------------------------
    public ItemModification(ModificationValueType type, ItemAttribute attr, int val) {
        this.type = type;
        this.attr = attr;
        this.val  = val;
    }

    //-----------------------------------------------------------------------
    public ItemModification clone() {
    	ItemModification ret = new ItemModification(type, attr, val);
    	ret.cloneAdd(this);
    	return ret;
    }

    //-----------------------------------------------------------------------
    public String toString() {
    	if (attr==null)
    		return "itemMod "+val;
        if (type==ModificationValueType.RELATIVE)
            return attr.getName()+((val<0)?(" "+val):(" +"+val));
        return attr.getName()+" = "+val;
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
    public ItemAttribute getAttribute() {
        return attr;
    }

    //-----------------------------------------------------------------------
    public void setAttribute(ItemAttribute attr) {
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

    //-----------------------------------------------------------------------
    /**
     */
    public boolean equals(Object o) {
        if (o instanceof ItemModification) {
            ItemModification amod = (ItemModification)o;
            if (amod.getType()     !=type) return false;
            if (amod.getAttribute()!=attr) return false;
            return (amod.getValue()==val);
        } else
            return false;
    }

    //-----------------------------------------------------------------------
    /**
     */
    public boolean matches(Object o) {
        if (o instanceof ItemModification) {
            ItemModification amod = (ItemModification)o;
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
        if (!(obj instanceof ItemModification))
            return toString().compareTo(obj.toString());
        ItemModification other = (ItemModification)obj;
        if (attr!=other.getAttribute())
            return (Integer.valueOf(attr.ordinal())).compareTo(Integer.valueOf(other.getAttribute().ordinal()));
        return (Integer.valueOf(type.ordinal()).compareTo(Integer.valueOf(other.getType().ordinal())));
    }

}// AttributeModification
