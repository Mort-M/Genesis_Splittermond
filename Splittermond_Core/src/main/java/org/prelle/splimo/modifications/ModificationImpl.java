package org.prelle.splimo.modifications;

import java.util.Date;

import org.prelle.simplepersist.Attribute;

import de.rpgframework.genericrpg.modification.Modification;


/**
 * @author prelle
 *
 */
public abstract class ModificationImpl implements Modification {
    
	@Attribute(required=false)
	protected Integer expCost;
	@Attribute(required=false)
	protected Date date;
	@Attribute(name="cond",required=false)
    private boolean conditional = false;
	/**
	 * If the modification belongs to a module, this keeps the reference
	 */
	protected transient Object source;
	
    //-----------------------------------------------------------------------
    public ModificationImpl() {
    }

	//-------------------------------------------------------------------
    public Modification clone() {
    	try {
    		return (Modification) super.clone();
    	} catch ( CloneNotSupportedException e ) {
    		throw new InternalError();
    	}
    }
    
    //-----------------------------------------------------------------------
    public boolean equals(Object o) {
    	if (o instanceof Modification) {
    		Modification other = (Modification)o;
    		if (source==null && other.getSource()!=null) return false; 
    		if (source!=null && other.getSource()==null) return false;
    		if (source!=null)
    			if (!source.equals(other.getSource())) return false;
    		return super.equals(o);
    	}
    	return false;
    }

	//-------------------------------------------------------------------
    protected void cloneAdd(ModificationImpl toClone) {
    	expCost = toClone.getExpCost();
    	date    = toClone.getDate();
    	conditional = toClone.isConditional();
    }

	//-------------------------------------------------------------------
	/**
	 * @return the expCost
	 */
	public int getExpCost() {
		if (expCost==null)
			return 0;
		return expCost;
	}

	//-------------------------------------------------------------------
	/**
	 * @param expCost the expCost to set
	 */
	public void setExpCost(int expCost) {
		if (expCost==0)
			this.expCost = null;
		else
		this.expCost = expCost;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.Datable#getDate()
	 */
	@Override
	public Date getDate() {
		return date;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.Datable#setDate(java.util.Date)
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.modification.Modification#getSource()
	 */
	public Object getSource() {
		return source;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.modification.Modification#setSource(java.lang.Object)
	 */
	public void setSource(Object source) {
		this.source = source;
	}

	//--------------------------------------------------------------------
	/**
	 * @return the conditional
	 */
	public boolean isConditional() {
		return conditional;
	}

	//--------------------------------------------------------------------
	/**
	 * @param conditional the conditional to set
	 */
	public void setConditional(boolean conditional) {
		this.conditional = conditional;
	}
    
}// Modification

