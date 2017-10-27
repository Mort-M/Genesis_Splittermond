/**
 * 
 */
package org.prelle.splimo;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;

import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.ElementList;
import org.prelle.splimo.persist.AspectConverter;

/**
 * @author prelle
 *
 */
public class DeityType extends BasePluginData implements Comparable<DeityType> {

	@Attribute(name="id")
	private String id;
	@ElementList(entry="aspect",type=Aspect.class,convert=AspectConverter.class)
	private List<Aspect> aspects;

	//-------------------------------------------------------------------
	/**
	 */
	public DeityType() {
		aspects = new ArrayList<Aspect>();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.BasePluginData#getPageI18NKey()
	 */
	@Override
	public String getPageI18NKey() {
		return "deitytype."+id+".page";
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.BasePluginData#getHelpI18NKey()
	 */
	@Override
	public String getHelpI18NKey() {
		return "deitytype."+id+".desc";
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
			return i18n.getString("deitytype."+id);
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
	public int compareTo(DeityType other) {
		return Collator.getInstance().compare(getName(), other.getName());
	}

	//-------------------------------------------------------------------
	/**
	 * @return the aspects
	 */
	public List<Aspect> getAspects() {
		return aspects;
	}

}
