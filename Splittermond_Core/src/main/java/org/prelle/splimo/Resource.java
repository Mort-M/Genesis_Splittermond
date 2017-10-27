/**
 * 
 */
package org.prelle.splimo;

import java.text.Collator;

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
		return i18n.getString("resource."+id);
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
