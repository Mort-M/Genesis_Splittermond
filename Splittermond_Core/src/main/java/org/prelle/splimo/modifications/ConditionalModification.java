/**
 * 
 */
package org.prelle.splimo.modifications;

import org.prelle.simplepersist.Element;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.requirements.RequirementList;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
@Root(name = "condmod")
public class ConditionalModification extends ModificationImpl {

	@Element
	private RequirementList conditions;
	@Element(name="truemod",required=true)
	private ModificationList trueModification;
	@Element(name="elsemod",required=false)
	private ModificationList elseModification;
	
	//-------------------------------------------------------------------
	/**
	 */
	public ConditionalModification() {
		// TODO Auto-generated constructor stub
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
	 * @return the condition
	 */
	public RequirementList getConditions() {
		return conditions;
	}

	//-------------------------------------------------------------------
	/**
	 * @param condition the condition to set
	 */
	public void setConditions(RequirementList condition) {
		this.conditions = condition;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the trueModification
	 */
	public ModificationList getTrueModification() {
		return trueModification;
	}

	//-------------------------------------------------------------------
	/**
	 * @param trueModification the trueModification to set
	 */
	public void setTrueModification(ModificationList trueModification) {
		this.trueModification = trueModification;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the elseModification
	 */
	public ModificationList getElseModification() {
		return elseModification;
	}

	//-------------------------------------------------------------------
	/**
	 * @param elseModification the elseModification to set
	 */
	public void setElseModification(ModificationList elseModification) {
		this.elseModification = elseModification;
	}

}
