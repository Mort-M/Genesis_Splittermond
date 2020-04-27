/**
 * 
 */
package org.prelle.splimo;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.MissingResourceException;
import java.util.StringTokenizer;

import org.prelle.simplepersist.Attribute;
import org.prelle.splimo.items.Availability;

/**
 * @author prelle
 *
 */
public class Town extends BasePluginData implements Comparable<Town> {
	
	@Attribute
	private String id;
	@Attribute(name="size",required=true)
	private Availability size;
	@Attribute
	private String cultures;
	@Attribute
	private String id;	
	
	public String getId() { return id; }

	//-------------------------------------------------------------------
	/**
	 */
	public Town() {
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.BasePluginData#getPageI18NKey()
	 */
	@Override
	public String getPageI18NKey() {
		return "town."+id+".page";
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.BasePluginData#getHelpI18NKey()
	 */
	@Override
	public String getHelpI18NKey() {
		return "town."+id+".desc";
	}

	//-------------------------------------------------------------------
	public String toString() {
		return id;
	}

	//-------------------------------------------------------------------
	public String getName() {
		try {
			return i18n.getString("town."+id);
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
	public int compareTo(Town other) {
		return Collator.getInstance().compare(getName(), other.getName());
	}

	//-------------------------------------------------------------------
	/**
	 * @return List of cultures in this town
	 */
	public List<Culture> getCultures() {
		Collection<String> tmp = new ArrayList<String>();
		if (cultures==null)
			return new ArrayList<Culture>();
		StringTokenizer tok = new StringTokenizer(cultures, ", ");
		while (tok.hasMoreTokens())
			tmp.add(tok.nextToken());
		
		List<Culture> ret = new ArrayList<Culture>(SplitterMondCore.getCultures());
		for (String ref : tmp) {
			Culture data = SplitterMondCore.getCulture(ref);
			if (ret.contains(data))
				ret.remove(data);
		}
		
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the size
	 */
	public Availability getSize() {
		return size;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.PluginData#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

}
