/**
 * 
 */
package org.prelle.splimo.items;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.splimo.BasePluginData;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.persist.ItemTypeConverter;

/**
 * @author prelle
 *
 */
public class FeatureType extends BasePluginData implements Comparable<FeatureType> {

	@Attribute(required=true)
	private String id;
	@Attribute(name="types")
	@AttribConvert(ItemTypeConverter.class)
	private Collection<ItemType> applyableTypes;
	@Attribute
	private boolean level;
	@Attribute
	private boolean remark;

    //-------------------------------------------------------------------
    public static FeatureType getByName(String name) {
    	if (name.equals("Wurftauglich")) name="Wurffähig";
		for (FeatureType tmp : SplitterMondCore.getFeatureTypes()) {
			if (tmp.getName().equalsIgnoreCase(name)) {
				return tmp;
			}
		}
		throw new IllegalArgumentException("'"+name+"'");
    }
	
    //-------------------------------------------------------------------
    public FeatureType() {
    	applyableTypes = new ArrayList<ItemType>();
    }
	
    //-------------------------------------------------------------------
    public String getName() {
        return i18n.getString("feature."+id.toLowerCase());
    }

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.BasePluginData#getPageI18NKey()
	 */
	@Override
	public String getPageI18NKey() {
		return "feature."+id.toLowerCase()+".page";
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.BasePluginData#getHelpI18NKey()
	 */
	@Override
	public String getHelpI18NKey() {
		return "feature."+id.toLowerCase()+".desc";
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(FeatureType other) {
		return Collator.getInstance().compare(getName(), other.getName());
	}

	//-------------------------------------------------------------------
	public boolean hasLevel() {
		return level;
	}

	//-------------------------------------------------------------------
	public String getId() {
		return id;
	}

	//-------------------------------------------------------------------
	public String toString() {
		return id;
	}

	//-------------------------------------------------------------------
	/**
	 * The feature type is not a "Feature Type" as defined in the rules, but just
	 * something added by an enhancement that is memorized this way
	 */
	public boolean isRemark() {
		return remark;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the applyableTypes
	 */
	public Collection<ItemType> getApplyableItemTypes() {
		return applyableTypes;
	}

}
