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
@Root(name = "resource")
public class Resource extends BasePluginData implements Comparable<Resource> {

	@Attribute(name="id")
	private String id;

	//-------------------------------------------------------------------
	public Resource() {
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.BasePluginData#getPageI18NKey()
	 */
	@Override
	public String getPageI18NKey() {
		return "resource."+id+".page";
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.ubiquity.BasePluginData#getHelpI18NKey()
	 */
	@Override
	public String getHelpI18NKey() {
		return "resource."+id;
	}

	//-------------------------------------------------------------------
	public String getName() {
		String searchKey = "resource."+id;
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
			return searchKey;
		}
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
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Resource other) {
		return Collator.getInstance().compare(getName(), other.getName());
	}
	
	//-------------------------------------------------------------------
	public boolean isBaseResource() {
		return SplitterMondCore.isBaseResource(this);
	}

}
