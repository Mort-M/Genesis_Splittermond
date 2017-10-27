/**
 * 
 */
package org.prelle.splimo.creature;

import java.text.Collator;
import java.util.MissingResourceException;

import org.apache.log4j.Logger;
import org.prelle.simplepersist.Attribute;
import org.prelle.splimo.BasePluginData;

/**
 * @author prelle
 *
 */
public class CreatureType extends BasePluginData implements Comparable<CreatureType> {
	
	@Attribute(required=true)
	private String id;
	@Attribute(name="levels",required=false)
	private boolean levels;
	@Attribute(name="special",required=false)
	private boolean special;

	//-------------------------------------------------------------------
	public String getName() {
		try {
			return i18n.getString("creature.type."+id.toLowerCase());
		} catch (MissingResourceException e) {
			Logger.getLogger("splittermond").error("Missing key "+e.getKey()+" in "+i18n.getBaseBundleName());
			return "creature.type."+id.toLowerCase();
		}
	}

	//-------------------------------------------------------------------
	public String getKey() {
		return id;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.PluginData#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	//-------------------------------------------------------------------
	public String toString() {
		return id;
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(CreatureType o) {
		return Collator.getInstance().compare(getName(), o.getName());
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.BasePluginData#getPageI18NKey()
	 */
	@Override
	public String getPageI18NKey() {
		return "creature.type."+id.toLowerCase()+".page";
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.BasePluginData#getHelpI18NKey()
	 */
	@Override
	public String getHelpI18NKey() {
		return "creature.type."+id.toLowerCase()+".desc";
	}

	//-------------------------------------------------------------------
	public boolean hasLevel() {
		return levels;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the special
	 */
	public boolean isSpecial() {
		return special;
	}

}
