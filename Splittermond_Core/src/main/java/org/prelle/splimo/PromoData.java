/**
 * 
 */
package org.prelle.splimo;

import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Element;
import org.prelle.splimo.creature.Creature;
import org.prelle.splimo.items.ItemTemplate;

/**
 * @author prelle
 *
 */
public class PromoData {
	
	@Attribute
	private String code;
	@Element
	private Creature creature;
	@Element
	private ItemTemplate item;

	//-------------------------------------------------------------------
	/**
	 */
	public PromoData() {
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	//-------------------------------------------------------------------
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the creature
	 */
	public Creature getCreature() {
		return creature;
	}

	//-------------------------------------------------------------------
	/**
	 * @param creature the creature to set
	 */
	public void setCreature(Creature creature) {
		this.creature = creature;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the item
	 */
	public ItemTemplate getItem() {
		return item;
	}

	//-------------------------------------------------------------------
	/**
	 * @param item the item to set
	 */
	public void setItem(ItemTemplate item) {
		this.item = item;
	}

}
