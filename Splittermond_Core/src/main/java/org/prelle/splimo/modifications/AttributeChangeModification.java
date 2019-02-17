package org.prelle.splimo.modifications;

import org.prelle.simplepersist.Root;
import org.prelle.splimo.Attribute;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
@Root(name = "attrchgmod")
public class AttributeChangeModification extends ModificationImpl {

	@org.prelle.simplepersist.Attribute
    private Attribute from;
	@org.prelle.simplepersist.Attribute
    private Attribute to;

    //-----------------------------------------------------------------------
    public AttributeChangeModification() {
    }

    //-----------------------------------------------------------------------
    public AttributeChangeModification(Attribute from, Attribute to) {
    	this.from = from;
    	this.to   = to;
    }

    //-----------------------------------------------------------------------
    public String toString() {
    	return "From "+from+" to "+to;
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean equals(Object o) {
        if (o instanceof AttributeChangeModification) {
            AttributeChangeModification amod = (AttributeChangeModification)o;
            if (amod.getFrom()!=from  ) return false;
            if (amod.getTo  ()!=to) return false;
            return true;
        } else
            return false;
    }

    //-------------------------------------------------------
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Modification obj) {
        if (!(obj instanceof AttributeChangeModification))
            return toString().compareTo(obj.toString());
        AttributeChangeModification other = (AttributeChangeModification)obj;
        if (from!=other.getFrom())
            return (Integer.valueOf(from.ordinal())).compareTo(Integer.valueOf(other.getFrom().ordinal()));
        return (Integer.valueOf(to.ordinal()).compareTo(Integer.valueOf(other.getTo().ordinal())));
    }

	//--------------------------------------------------------------------
	/**
	 * @return the from
	 */
	public Attribute getFrom() {
		return from;
	}

	//--------------------------------------------------------------------
	/**
	 * @return the to
	 */
	public Attribute getTo() {
		return to;
	}

}// AttributeModification
