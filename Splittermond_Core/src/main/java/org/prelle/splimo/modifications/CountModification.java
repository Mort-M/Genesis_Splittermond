/**
 * 
 */
package org.prelle.splimo.modifications;

import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.SplitterMondCore;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
@Root(name = "countmod")
public class CountModification extends ModificationImpl {

	public enum Type {
		MASTERSHIP,
		SPELL,
		CREATURE_FEATURE_POSITIVE,
		WEAPON
		;
		public String getName() {
			return SplitterMondCore.getI18nResources().getString("countmodification.type."+name().toLowerCase());
		}
	}
	
	@Attribute
	private Type type;
	@Attribute
	private int count;
	
	//-------------------------------------------------------------------
	public CountModification() {
	}

	//-------------------------------------------------------------------
	public String toString() {
		return "CountModification(type="+type+", count="+count+")";
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Modification o) {
		if (o instanceof CountModification)
			return type.compareTo(((CountModification)o).getType());
		return 0;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	//-------------------------------------------------------------------
	/**
	 * @param type the type to set
	 */
	public void setType(Type type) {
		this.type = type;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	//-------------------------------------------------------------------
	/**
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

}
