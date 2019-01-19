/**
 *
 */
package org.prelle.splimo;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;

import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Element;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.modifications.BackgroundModification;
import org.prelle.splimo.modifications.ModificationChoice;
import org.prelle.splimo.modifications.ModificationImpl;
import org.prelle.splimo.modifications.ModificationList;
import org.prelle.splimo.modifications.NotBackgroundModification;
import org.prelle.splimo.modifications.RaceModification;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
@Root(name = "culture")
public class Culture extends BasePluginData implements Comparable<Culture> {

	public enum Continent {
		CUSTOM,
		DRAGOREA,
		FROSTLANDE,
		TAKASADU,
		ARAKEA,
		PASHANAR,
		EXOTENKUESTE,
		BINNENMEER
	}

	@Attribute(name="id")
	private String key;
	@Attribute(required=false)
	private String name;
	@Attribute(name="continent")
	private Continent continent;
	@Element
	private ModificationList modifications;

	//-------------------------------------------------------------------
	public Culture() {
		modifications = new ModificationList();
	}

	//-------------------------------------------------------------------
	public Culture(String id, String customName) {
		this.key = id;
		this.name = customName;
		modifications = new ModificationList();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.BasePluginData#getPageI18NKey()
	 */
	@Override
	public String getPageI18NKey() {
		return "culture."+key+".page";
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.ubiquity.BasePluginData#getHelpI18NKey()
	 */
	@Override
	public String getHelpI18NKey() {
		return "culture."+key+".desc";
	}

	//-------------------------------------------------------------------
	public String toString() {
		if ( name!=null || (key!=null && i18n!=null))
			return getName();
		return key;
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
		String searchKey = "culture."+key;
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
	public List<Modification> getModifications() {
		return modifications;
	}

	//-------------------------------------------------------------------
	public void setModifications(List<Modification> mods) {
		this.modifications = new ModificationList(mods);
	}

	//-------------------------------------------------------------------
	public void addModification(ModificationImpl mod) {
		if (mod instanceof ModificationChoice) {
			if (((ModificationChoice)mod).getOptions().length>2 )
				throw new IllegalArgumentException("ModificationChoice without options: "+mod);
		}
		modifications.add(mod);
	}

	//-------------------------------------------------------------------
	public Continent getContinent() {
		return continent;
	}

	//-------------------------------------------------------------------
	public void setContinent(Continent continent) {
		this.continent = continent;
	}

	//-------------------------------------------------------------------
	public List<Race> getCommonRaces() {
		List<Race> ret = new ArrayList<Race>();
		for (Modification tmp : modifications) {
			if (tmp instanceof RaceModification) {
				ret.add( ((RaceModification) tmp).getRace() );
			}
		}

		return ret;
	}

	//-------------------------------------------------------------------
	public List<Background> getCommonBackgrounds() {
		List<Background> ret = new ArrayList<Background>();
		for (Modification tmp : modifications) {
			if (tmp instanceof BackgroundModification) {
				ret.add( ((BackgroundModification) tmp).getBackground() );
			} else
			if (tmp instanceof NotBackgroundModification) {
				ret.addAll(SplitterMondCore.getBackgrounds());
				for (Background remove : ((NotBackgroundModification) tmp).getBackgrounds())
					ret.add(remove);
			}
		}

		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Culture o) {
		return Collator.getInstance().compare(getName(), o.getName());
	}

}
