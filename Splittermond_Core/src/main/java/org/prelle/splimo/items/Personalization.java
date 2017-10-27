/**
 * 
 */
package org.prelle.splimo.items;

import java.util.List;

import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Element;
import org.prelle.splimo.BasePluginData;
import org.prelle.splimo.modifications.ModificationList;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class Personalization extends BasePluginData {

	@Attribute
	private String id;
	@Attribute
	private ItemType type;
	@Element
	private ModificationList modifications;
	
	//-------------------------------------------------------------------
	/**
	 */
	public Personalization() {
		modifications = new ModificationList();
	}
	
	//-------------------------------------------------------------------
	/**
	 */
	public Personalization(String id, ItemType type) {
		this();
		this.id = id;
		this.type = type;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.BasePluginData#getPageI18NKey()
	 */
	@Override
	public String getPageI18NKey() {
		return "personalization."+id+".page";
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.ubiquity.BasePluginData#getHelpI18NKey()
	 */
	@Override
	public String getHelpI18NKey() {
		return "personalization."+id+".desc";
	}

	//-------------------------------------------------------------------
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	//-------------------------------------------------------------------
	public String getName() {
		return i18n.getString("personalization."+id);
	}

	//-------------------------------------------------------------------
	/**
	 * @return the modifications
	 */
	public List<Modification> getModifications() {
		return modifications;
	}

	//-------------------------------------------------------------------
	public ItemType getSupportedItemType() {
		return type;
	}

}
