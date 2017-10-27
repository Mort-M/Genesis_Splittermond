/**
 * 
 */
package org.prelle.splimo;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;

import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Root;
import org.prelle.splimo.modifications.LanguageModification;
import org.prelle.splimo.modifications.ModificationChoice;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
@Root(name = "culturelore")
public class CultureLore extends BasePluginData implements Comparable<CultureLore> {
	
	@Attribute
	private String id;
	
	private transient List<Culture> cultures;
	private transient List<Language> languages;
	
	//-------------------------------------------------------------------
	public CultureLore() {
		cultures = new ArrayList<>();
		languages = new ArrayList<>();
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.ubiquity.BasePluginData#getPageI18NKey()
	 */
	@Override
	public String getPageI18NKey() {
		return "culturelore."+id+".page";
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.ubiquity.BasePluginData#getHelpI18NKey()
	 */
	@Override
	public String getHelpI18NKey() {
		return "culturelore."+id+".desc";
	}

	//-------------------------------------------------------------------
	public String toString() {
		return id;
	}

	//-------------------------------------------------------------------
	public String getName() {
		try {
			return i18n.getString("culturelore."+id);
		} catch (MissingResourceException e) {
			logger.error(e.toString()+" in "+i18n.getBaseBundleName());
			if (MISSING!=null)
				MISSING.println(e.getKey()+"   \t in "+i18n.getBaseBundleName()+".properties");
		}
		return null;
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
	public int compareTo(CultureLore o) {
		return Collator.getInstance().compare(getName(), o.getName());
	}

	//-------------------------------------------------------------------
	/**
	 * @return the culture
	 */
	public Collection<Culture> getCultures() {
		return cultures;
	}

	//-------------------------------------------------------------------
	/**
	 * @param culture the culture to set
	 */
	public void addCulture(Culture culture) {
		if (!cultures.contains(culture)) {
			cultures.add(culture);
			// Find languagemodification
			for (Modification mod : culture.getModifications()) {
				if (mod instanceof LanguageModification) {
					LanguageModification cMod = (LanguageModification)mod;
					Language lang = cMod.getData();
					if (!languages.contains(lang))
						languages.add(lang);
				} else if (mod instanceof ModificationChoice) {
					for (Modification mod2 : ((ModificationChoice)mod).getOptionList()) {
						if (mod2 instanceof LanguageModification) {
							LanguageModification cMod = (LanguageModification)mod2;
							Language lang = cMod.getData();
							if (!languages.contains(lang))
								languages.add(lang);
						}
					}
					
				}
			}
			
		}
	}

	//-------------------------------------------------------------------
	public Collection<Language> getLanguages() {
		return languages;
	}

}
