/**
 * 
 */
package org.prelle.splimo.modifications;

import org.prelle.simplepersist.Attribute;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class MoneyModification extends ModificationImpl implements Modification {
	
	@Attribute
	private int telare;

	//-------------------------------------------------------------------
	public MoneyModification() {
	}

	//-------------------------------------------------------------------
	public MoneyModification(int money) {
		this.telare = money;
	}

	//-------------------------------------------------------------------
    public Modification clone() {
    	MoneyModification ret = new MoneyModification(telare);
		ret.setSource(source);
    	ret.cloneAdd(this);
    	return ret;
    }

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Modification o) {
		return 0;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the telare
	 */
	public int getTelare() {
		return telare;
	}

	//-------------------------------------------------------------------
	/**
	 * @param telare the telare to set
	 */
	public void setTelare(int telare) {
		this.telare = telare;
	}

}
