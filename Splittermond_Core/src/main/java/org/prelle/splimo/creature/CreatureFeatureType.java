/**
 * 
 */
package org.prelle.splimo.creature;

import java.util.MissingResourceException;

import org.prelle.simplepersist.Attribute;
import org.prelle.splimo.BasePluginData;
import org.prelle.splimo.DamageType;

/**
 * @author prelle
 *
 */
/**
 * @author prelle
 *
 */
public class CreatureFeatureType extends BasePluginData {

	public enum Selector {
		DAMAGE_TYPE
	}
	

	@Attribute(required=true)
	private String id;
	@Attribute(name="levels",required=false)
	private boolean levels;
	@Attribute(name="select",required=false)
	private Selector select;
	@Attribute(name="count",required=false)
	private boolean count;
	@Attribute(name="mods",required=false)
	private boolean modifications;
	@Attribute(required=false)
	private boolean negative;
	
    //-------------------------------------------------------------------
	public String getId() {
		return id;
	}

	//-------------------------------------------------------------------
	public String toString() {
		return String.format("%s(lvl=%s, sel=%s)", id, levels, select);
	}

	//-------------------------------------------------------------------
	public String getName() {
		try {
			return i18n.getString("creaturefeaturetype."+getId());
		} catch (MissingResourceException mre) {
			logger.error("Missing property '"+mre.getKey()+"' in "+i18n.getBaseBundleName());
		}
		return "creaturefeaturetype."+id;
	}

	//-------------------------------------------------------------------
	public String getName(DamageType type) {
		try {
			return i18n.getString("creaturefeaturetype."+getId()+"."+type.name().toLowerCase());
		} catch (MissingResourceException mre) {
			logger.error("Missing property '"+mre.getKey()+"' in "+i18n.getBaseBundleName());
		}
		return "creaturefeaturetype."+id;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.BasePluginData#getPageI18NKey()
	 */
	@Override
	public String getPageI18NKey() {
		return "creaturefeaturetype."+getId()+".page";
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.BasePluginData#getHelpI18NKey()
	 */
	@Override
	public String getHelpI18NKey() {
		return "creaturefeaturetype."+getId()+".desc";
	}

	//-------------------------------------------------------------------
	/**
	 * @return the levels
	 */
	public boolean hasLevels() {
		return levels;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the select
	 */
	public Selector getSelect() {
		return select;
	}

	//-------------------------------------------------------------------
	public boolean hasCount() {
		return count;
	}

	//-------------------------------------------------------------------
	public boolean hasModifications() {
		return modifications;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the negative
	 */
	public boolean isNegative() {
		return negative;
	}

}
