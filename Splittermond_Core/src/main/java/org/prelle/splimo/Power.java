/**
 * 
 */
package org.prelle.splimo;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.MissingResourceException;

import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Element;
import org.prelle.splimo.modifications.ModificationList;
import org.prelle.splimo.requirements.Requirement;
import org.prelle.splimo.requirements.RequirementList;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class Power extends BasePluginData implements Comparable<Power> {

	public enum SelectionType {
		ALWAYS,
		/** Multiple times - unlimited */
		MULTIPLE,
		/** Only at generation */
		GENERATION,
		/** To a total of 3 times */
		MAX3,
		/** Once per level */
		LEVEL, 
	}
	
	@Attribute(name="id")
	private String id;
	@Attribute(name="cost",required=false)
	private int cost;
	@Attribute(name="selectable",required=false)
	private SelectionType selectable;
	@Element
	private ModificationList modifications= new ModificationList();
	@Element
	private RequirementList requires= new RequirementList();

	//-------------------------------------------------------------------
	public Power() {
		cost=1;
		selectable = SelectionType.ALWAYS;
	}

	//-------------------------------------------------------------------
	public String getName() {
		try {
			return i18n.getString("power."+id);
		} catch (MissingResourceException e) {
			if (!reportedKeys.contains(e.getKey())) {
				reportedKeys.add(e.getKey());
				logger.error("Missing property '"+e.getKey()+"' in "+i18n.getBaseBundleName());
				if (MISSING!=null)
					MISSING.println(e.getKey()+"=");
			}
			return e.getKey();
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.BasePluginData#getPageI18NKey()
	 */
	@Override
	public String getPageI18NKey() {
		return "power."+id+".page";
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.ubiquity.BasePluginData#getHelpI18NKey()
	 */
	@Override
	public String getHelpI18NKey() {
		return "power."+id+".desc";
	}

	//-------------------------------------------------------------------
	public String getDescription() {
		try {
			return i18n.getString("power."+id+".desc");
		} catch (MissingResourceException e) {
			if (!reportedKeys.contains(e.getKey())) {
				reportedKeys.add(e.getKey());
				logger.error("Missing property '"+e.getKey()+"' in "+i18n.getBaseBundleName());
				if (MISSING!=null)
					MISSING.println(e.getKey()+"=");
			}
			return "";
		}
	}

	//-------------------------------------------------------------------
	public String getId() {
		return id;
	}

	//-------------------------------------------------------------------
	public int getCost() {
		return cost;
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
	public int compareTo(Power other) {
		return Collator.getInstance().compare(getName(), other.getName());
	}

	//-------------------------------------------------------------------
	/**
	 * @return the attrmod
	 */
	public Collection<Modification> getModifications() {
		if (modifications==null) return new ArrayList<Modification>();
		return new ArrayList<Modification>(modifications);
	}

	//-------------------------------------------------------------------
	public Collection<Requirement> getRequirement() {
		if (requires==null) return new ArrayList<Requirement>();
		return new ArrayList<Requirement>(requires);
	}

	//--------------------------------------------------------------------
	/**
	 * @return the selectable
	 */
	public SelectionType getSelectable() {
		return selectable;
	}

	//--------------------------------------------------------------------
	/**
	 * @param selectable the selectable to set
	 */
	public void setSelectable(SelectionType selectable) {
		this.selectable = selectable;
	}

}
