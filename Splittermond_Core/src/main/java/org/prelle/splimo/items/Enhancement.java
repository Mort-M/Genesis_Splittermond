/**
 * 
 */
package org.prelle.splimo.items;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.MissingResourceException;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Element;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.BasePluginData;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.modifications.ModificationImpl;
import org.prelle.splimo.modifications.ModificationList;
import org.prelle.splimo.persist.EnhancementMaxConverter;
import org.prelle.splimo.persist.ItemTypeConverter;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * A wrapper for profane modifications made by the item.
 * 
 * @author prelle
 *
 */
@Root(name="enhancement")
public class Enhancement extends BasePluginData implements Comparable<Enhancement> {
	
	public enum EnhancementType {
		NORMAL,
		RELIC,
		MAGIC,
		ALCHEMY,
		DIVINE,
		SAINT;
		
		public String getName() {
			return SplitterMondCore.getI18nResources().getString("enhancementtype."+name().toLowerCase());
		}

	}
	
	public final static int MAX_ONCEPERLOAD = -1;
	public final static int MAX_HALFLOAD = -2;
	
	@Attribute(required=true)
	private String id;
	@Attribute(required=true)
	private int cost;
	@Attribute
	@AttribConvert(EnhancementMaxConverter.class)
	private int max;
	@Attribute
	private EnhancementType type;
	@Attribute(name="items",required=false)
	@AttribConvert(ItemTypeConverter.class)
	private Collection<ItemType> types;
	@Element
	private ModificationList modifications;

	//--------------------------------------------------------------------
	public Enhancement() {
		modifications = new ModificationList();
		type  = EnhancementType.NORMAL;
		types = new ArrayList<>(Arrays.asList(ItemType.values()));
	}

	//--------------------------------------------------------------------
	public Enhancement(String id, int size, ModificationImpl mod, EnhancementType type) {
		this();
		this.id   = id;
		this.cost = size;
		this.type = type;
		modifications.add(mod);
	}

	//--------------------------------------------------------------------
	public Enhancement(String id, int size, EnhancementType type) {
		this();
		this.id   = id;
		this.cost = size;
		this.type = type;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.BasePluginData#getPageI18NKey()
	 */
	@Override
	public String getPageI18NKey() {
		return "enhancement."+id+".page";
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.ubiquity.BasePluginData#getHelpI18NKey()
	 */
	@Override
	public String getHelpI18NKey() {
		return "enhancement."+id+".desc";
	}

	//-------------------------------------------------------------------
	public String getName() {
		try {
			return i18n.getString("enhancement."+id);
		} catch (MissingResourceException e) {
			System.out.println("Missing "+e.getKey()+" in "+i18n.getBaseBundleName());
			return "enhancement."+id;
		}
	}

	//-------------------------------------------------------------------
	public String toString() {
		if (i18n==null)
			return id;
		if (id==null || i18n==null)
			return super.toString();
		return getName();
	}

	//--------------------------------------------------------------------
	/**
	 * When creating an item how many item quality slots does the effect
	 * need.
	 * 
	 * @return
	 */
	public int getSize() {
		return cost;
	}

	//--------------------------------------------------------------------
	/**
	 * @return the modifications
	 */
	public List<Modification> getModifications() {
		return modifications;
	}

	//--------------------------------------------------------------------
	public void addModification(Modification modification) {
		modifications.add(modification);
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Enhancement other) {
		return Collator.getInstance().compare(getName(), other.getName());
	}

	//-------------------------------------------------------------------
	/**
	 * @return How often can this enhancement be applied
	 */
	public int getMaxApplyCount() {
		return max;
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
	 * @return the type
	 */
	public EnhancementType getType() {
		return type;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the types
	 */
	public Collection<ItemType> getItemTypeLimitations() {
		return types;
	}
	
}
