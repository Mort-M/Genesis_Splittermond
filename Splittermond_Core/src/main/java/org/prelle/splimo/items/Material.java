/**
 * 
 */
package org.prelle.splimo.items;

import java.text.Collator;
import java.util.MissingResourceException;

import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Element;
import org.prelle.splimo.BasePluginData;
import org.prelle.splimo.modifications.ModificationList;

/**
 * @author prelle
 *
 */
public class Material extends BasePluginData implements Comparable<Material> {

	@Attribute
	private String id;
	@Attribute
	private int quality;
	/* Additional price per load in Telaren */
	@Attribute
	private int price;
	@Attribute
	private MaterialType type;
	@Element(name="modifications")
	private ModificationList modifications;

	//-------------------------------------------------------------------
	public Material() {
		modifications = new ModificationList();
	}

	//-------------------------------------------------------------------
	public String toString() {
		return getId();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.BasePluginData#getPageI18NKey()
	 */
	@Override
	public String getPageI18NKey() {
		return "material."+id+".page";
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.ubiquity.BasePluginData#getHelpI18NKey()
	 */
	@Override
	public String getHelpI18NKey() {
		return "material."+id+".desc";
	}

	//-------------------------------------------------------------------
	public String getName() {
		try {
			return i18n.getString("material."+id);
		} catch (MissingResourceException e) {
			logger.error("Missing  "+e.getKey()+"  in "+i18n.getBaseBundleName());
			return e.getKey();
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the quality
	 */
	public int getQuality() {
		return quality;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the price
	 */
	public int getPrice() {
		return price;
	}
	
	//--------------------------------------------------------------------
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if (id==null) return 0;
		return id.hashCode();
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Material other) {
		if (id.startsWith("common_")) return -1;
		if (other.getId().startsWith("common_")) return +1;
		return Collator.getInstance().compare(getName(), other.getName());
	}

	//-------------------------------------------------------------------
	/**
	 * @return the type
	 */
	public MaterialType getType() {
		return type;
	}
	
	//--------------------------------------------------------------------
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if (o instanceof Material) {
			Material other = (Material)o;
			if (!id.equals(other.getId())) return false;
			if (quality!=other.getQuality()) return false;
			if (type   !=other.getType()) return false;
			if (price!=other.getPrice()) return false;
			return modifications.equals(other.getModifications());
		}
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the modifications
	 */
	public ModificationList getModifications() {
		return modifications;
	}

	//-------------------------------------------------------------------
	public String getEffectText() {
		if (i18nHelp==null)
			return null;
		String key = "material."+id+".effect";
//		if (!LicenseManager.hasLicense(plugin.getRules(), plugin.getID()))
//			return null;

		try {
			return i18nHelp.getString(key);
		} catch (MissingResourceException mre) {
			logger.warn("Missing property '"+key+"' in "+i18nHelp.getBaseBundleName());
		}
		return null;
	}

}
