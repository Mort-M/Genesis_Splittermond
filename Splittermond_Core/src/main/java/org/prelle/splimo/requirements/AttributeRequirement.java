package org.prelle.splimo.requirements;

import org.prelle.simplepersist.Root;
import org.prelle.splimo.Attribute;

/**
 * @author prelle
 *
 */
@Root(name = "attrreq")
public class AttributeRequirement extends Requirement {

	@org.prelle.simplepersist.Attribute
    private Attribute attr;
	@org.prelle.simplepersist.Attribute
    private int val;
    
    //-----------------------------------------------------------------------
    public AttributeRequirement() {
    }
    
    //-----------------------------------------------------------------------
    public AttributeRequirement(Attribute attr, int val) {
        this.attr = attr;
        this.val  = val;
    }
    
    //-----------------------------------------------------------------------
    @Override
    public int hashCode() {
        return toString().hashCode();
    }
   
    //-----------------------------------------------------------------------
    public String toString() {
        return attr+" "+val;
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
    
    //-----------------------------------------------------------------------
    public Object clone() {
        return new AttributeRequirement(attr, val);
    }
    
    //-----------------------------------------------------------------------
    /**
     */
    public boolean equals(Object o) {
        if (o instanceof AttributeRequirement) {
            AttributeRequirement amod = (AttributeRequirement)o;
            if (amod.getAttribute()!=attr) return false;
            return (amod.getValue()==val);
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
   
}
