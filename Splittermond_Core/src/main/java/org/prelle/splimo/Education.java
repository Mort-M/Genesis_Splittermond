/**
 * 
 */
package org.prelle.splimo;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.MissingResourceException;

import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Element;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.modifications.ModificationList;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
@Root(name = "education")
public class Education extends BasePluginData implements Comparable<Education> {
	
	@Attribute(name="id")
	private String key;
	@Attribute(required=false)
	private String name;
	@Element
	private ModificationList modifications;
	@Attribute(name="variantof",required=false) 
	private String variantOf;
	private transient List<Education> variants;
	
	//-------------------------------------------------------------------
	public Education() {
		modifications = new ModificationList();
		variants      = new ArrayList<Education>();
	}
	
	//-------------------------------------------------------------------
	public Education(String id) {
		this.key = id;
		modifications = new ModificationList();
		variants      = new ArrayList<Education>();
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.ubiquity.BasePluginData#getPageI18NKey()
	 */
	@Override
	public String getPageI18NKey() {
		return "education."+key+".page";
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.ubiquity.BasePluginData#getHelpI18NKey()
	 */
	@Override
	public String getHelpI18NKey() {
		return "education."+key+".descr";
	}

	//-------------------------------------------------------------------
	public String toString() {
		return key;
	}

	//-------------------------------------------------------------------
	public boolean equals(Object o) {
		if (!(o instanceof Education))
			return false;
		
		Education other = (Education)o;
		if (!key.equals(other.getKey())) return false;
		
		return modifications.equals(other.getModifications());
	}

	//-------------------------------------------------------------------
	public String dump() {
		return key+" = "+modifications;
	}

	//-------------------------------------------------------------------
	public String getName() {
		if (name!=null)
			return name;
		if (i18n==null)
			i18n = SplitterMondCore.getI18nResources();
		String searchKey = "education."+key;
		try {
			return i18n.getString(searchKey);
		} catch (MissingResourceException e) {
			if (!reportedKeys.contains(e.getKey())) {
				reportedKeys.add(e.getKey());
				logger.warn(String.format("key missing:    %s   %s", i18n.getBaseBundleName(), e.getKey()));
				if (MISSING!=null) {
					MISSING.println(e.getKey()+"=");
				}
			}
			return key;
		}
	}

	//-------------------------------------------------------------------
	public void setName(String name) {
		this.name = name;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.PluginData#getId()
	 */
	@Override
	public String getId() {
		return key;
	}

	//-------------------------------------------------------------------
	public String getKey() {
		return key;
	}

	//-------------------------------------------------------------------
	public void setKey(String key) {
		this.key = key;
	}

	//-------------------------------------------------------------------
	public Collection<Modification> getModifications() {
		return modifications;
	}

	//-------------------------------------------------------------------
	public void setModifications(ModificationList mods) {
		this.modifications = mods;
	}

	//-------------------------------------------------------------------
	public void addModifications(Modification mod) {
		modifications.add(mod);
	}

	//--------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Education o) {
		return Collator.getInstance().compare(getName(), o.getName());
	}

	//-------------------------------------------------------------------
	/**
	 * @return the variants
	 */
	public List<Education> getVariants() {
		return variants;
	}

	//-------------------------------------------------------------------
	public void addVariant(Education toAdd) {
		if (!variants.contains(toAdd))
			variants.add(toAdd);
	}

	//-------------------------------------------------------------------
	/**
	 * @return the variantOf
	 */
	public String getVariantOf() {
		return variantOf;
	}

	//-------------------------------------------------------------------
	/**
	 * @param variantOf the variantOf to set
	 */
	public void setVariantOf(String variantOf) {
		this.variantOf = variantOf;
	}

}
