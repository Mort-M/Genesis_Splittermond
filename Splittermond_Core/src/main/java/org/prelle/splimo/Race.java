/**
 * 
 */
package org.prelle.splimo;

import java.text.Collator;
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
@Root(name = "race")
public class Race extends BasePluginData implements Comparable<Race> {

	@Attribute(name="id")
	private String key;
	@Element
	private ModificationList modifications;
	@Element
	private ColorDiceTable hair;
	@Element
	private ColorDiceTable eyes;
	@Element
	private Size size;
	
	//-------------------------------------------------------------------
	public Race() {
		modifications = new ModificationList();
		hair = new ColorDiceTable();
		eyes = new ColorDiceTable();
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
		return "race."+key+".page";
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.ubiquity.BasePluginData#getHelpI18NKey()
	 */
	@Override
	public String getHelpI18NKey() {
		return "race."+key+".desc";
	}

	//-------------------------------------------------------------------
	public String toString() {
		return key;
	}

	//-------------------------------------------------------------------
	public String getName() {
		try {
			return i18n.getString("race."+key);
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
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Race other) {
		return Collator.getInstance().compare(getName(), other.getName());
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
	public void setModifications(ModificationList mods) {
		this.modifications = mods;
	}

	//-------------------------------------------------------------------
	public void addModifications(Modification mod) {
		modifications.add(mod);
	}

	//-------------------------------------------------------------------
	/**
	 * @return the hair
	 */
	public ColorDiceTable getHair() {
		return hair;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the eyes
	 */
	public ColorDiceTable getEyes() {
		return eyes;
	}

	//-------------------------------------------------------------------
	/**
	 * @return
	 */
	public String dump() {
		StringBuffer buf = new StringBuffer();
		hair.dump(buf);
		eyes.dump(buf);
		return key+"  "+modifications+"\n"+buf;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the size
	 */
	public Size getSize() {
		return size;
	}

	//-------------------------------------------------------------------
	public void fixModifications() {
		for (Modification mod : modifications)
			mod.setSource(this);
	}

}
