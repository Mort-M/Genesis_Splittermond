/**
 * 
 */
package org.prelle.splimo;

import java.text.Collator;
import java.util.MissingResourceException;

import org.prelle.simplepersist.Attribute;

/**
 * @author prelle
 *
 */
public class Aspect extends BasePluginData implements Comparable<Aspect> {

	@Attribute(name="id")
	private String id;

	//-------------------------------------------------------------------
	/**
	 */
	public Aspect() {
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.BasePluginData#getPageI18NKey()
	 */
	@Override
	public String getPageI18NKey() {
		return "aspect."+id+".page";
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.BasePluginData#getHelpI18NKey()
	 */
	@Override
	public String getHelpI18NKey() {
		return "aspect."+id+".desc";
	}

	//-------------------------------------------------------------------
	public String toString() {
		return id;
	}

	//-------------------------------------------------------------------
	public String getId() {
		return id;
	}

	//-------------------------------------------------------------------
	public String getName() {
		try {
			return i18n.getString("aspect."+id);
		} catch (MissingResourceException e) {
			logger.error("Missing   "+e.getKey()+"   from "+i18n.getBaseBundleName());
			return e.getKey();
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Aspect other) {
		return Collator.getInstance().compare(getName(), other.getName());
	}

}
