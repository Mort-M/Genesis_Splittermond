/**
 * 
 */
package org.prelle.splimo;

import java.text.Collator;
import java.util.List;

import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Element;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.modifications.ModificationList;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
@Root(name = "background")
public class Background extends BasePluginData implements Comparable<Background> {
	
	@Attribute(name="id")
	private String key;
	@Attribute(required=false)
	private String name;
	@Element
	private ModificationList modifications;
	
	//-------------------------------------------------------------------
	public Background() {
		modifications = new ModificationList();
	}
	
	//-------------------------------------------------------------------
	public Background(String id) {
		this.key = id;
		modifications = new ModificationList();
	}
	
	//-------------------------------------------------------------------
	public Background(String id, String customName) {
		this.key = id;
		this.name= customName;
		modifications = new ModificationList();
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
	/**
	 * @see org.prelle.splimo.BasePluginData#getPageI18NKey()
	 */
	@Override
	public String getPageI18NKey() {
		return "background."+key+".page";
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.ubiquity.BasePluginData#getHelpI18NKey()
	 */
	@Override
	public String getHelpI18NKey() {
		return "background."+key+".desc";
	}

	//-------------------------------------------------------------------
	public String toString() {
		return key;
	}

	//-------------------------------------------------------------------
	public boolean equals(Object o) {
		if (!(o instanceof Background))
			return false;
		
		Background other = (Background)o;
		if (!key.equals(other.getKey())) return false;
		if (name!=null && !name.equals(other.name)) return false;
		if (other.name!=null && !other.name.equals(name)) return false;
		
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
		return SplitterMondCore.getI18nResources().getString("background."+key);
	}

	//-------------------------------------------------------------------
	public void setName(String name) {
		this.name = name;
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
	public List<Modification> getModifications() {
		return modifications;
	}

	//-------------------------------------------------------------------
	public void setModifications(List<Modification> mods) {
		this.modifications = new ModificationList(mods);
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
	public int compareTo(Background o) {
		return Collator.getInstance().compare(getName(), o.getName());
	}

}
