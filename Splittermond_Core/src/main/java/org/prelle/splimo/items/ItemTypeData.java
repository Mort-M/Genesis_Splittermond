/**
 * 
 */
package org.prelle.splimo.items;

import org.prelle.simplepersist.Attribute;

/**
 * @author prelle
 *
 */
public abstract class ItemTypeData {

	@Attribute(required=false)
	protected ItemType type;
	
	//-------------------------------------------------------------------
	protected ItemTypeData(ItemType type) {
		this.type = type;
	}
	
	//-------------------------------------------------------------------
	/**
	 * Return the kind of item
	 */
	public ItemType getType() {
		return type;
	}
	
}
