/**
 * 
 */
package org.prelle.splimo;

import java.text.Collator;
import java.util.MissingResourceException;

import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Root;

/**
 * @author prelle
 *
 */
@Root(name = "language")
public class Language extends BasePluginData implements Comparable<Language> {
	
	@Attribute
	private String id;
	
	//-------------------------------------------------------------------
	public Language() {
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.BasePluginData#getPageI18NKey()
	 */
	@Override
	public String getPageI18NKey() {
		return "language."+id+".page";
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.ubiquity.BasePluginData#getHelpI18NKey()
	 */
	@Override
	public String getHelpI18NKey() {
		return "language."+id+".desc";
	}

	//-------------------------------------------------------------------
	public String toString() {
		return id;
	}

	//-------------------------------------------------------------------
	public String getName() {
		if (i18n==null)
			i18n = SplitterMondCore.getI18nResources();
		String searchKey = "language."+id;
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
			return id;
		}
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
	public String getKey() {
		return id;
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Language o) {
		return Collator.getInstance().compare(getName(), o.getName());
	}

}
