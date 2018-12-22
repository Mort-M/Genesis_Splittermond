/**
 * 
 */
package org.prelle.splimo.modifications;

import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.modifications.ModificationImpl;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
@Root(name = "limitmod")
public class LimitModification extends ModificationImpl implements Modification {
	
	public enum LimitType {
		ATTRIBUTE,
		SKILL,
	}
	
	@Attribute
	private LimitType type;
	@Attribute
	private int value;

	//-------------------------------------------------------------------
	public LimitModification(LimitType type, int val) {
		this.type  = type;
		this.value = val;
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
	 * @return the type
	 */
	public LimitType getType() {
		return type;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the val
	 */
	public int getValue() {
		return value;
	}

}
