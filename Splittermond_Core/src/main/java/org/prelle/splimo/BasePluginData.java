/**
 *
 */
package org.prelle.splimo;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import de.rpgframework.HardcopyPluginData;
import de.rpgframework.RulePlugin;
import de.rpgframework.character.RuleSpecificCharacterObject;

/**
 * @author Stefan
 *
 */
public abstract class BasePluginData implements HardcopyPluginData {

	protected static Logger logger = Logger.getLogger("splittermond");

	protected static PrintWriter MISSING;
	protected static PrintWriter MISSING_HELP;
	protected static List<String> reportedKeys;
	protected transient ResourceBundle i18n;
	protected transient ResourceBundle i18nHelp;

	protected transient RulePlugin<? extends RuleSpecificCharacterObject> plugin;

	//--------------------------------------------------------------------
	static {
		reportedKeys = new ArrayList<String>();
		try {
			if (System.getProperty("logdir")==null) {
				Path path = Files.createTempDirectory("genesis");
				MISSING = new PrintWriter(path+System.getProperty("file.separator")+"/missing-keys-splimo.txt");
				MISSING_HELP = new PrintWriter(path+System.getProperty("file.separator")+"/missing-keys-help-splimo.txt");
				path.toFile().deleteOnExit();
			} else {
				MISSING = new PrintWriter(System.getProperty("logdir")+System.getProperty("file.separator")+"/missing-keys-splimo.txt");
				MISSING_HELP = new PrintWriter(System.getProperty("logdir")+System.getProperty("file.separator")+"/missing-keys-help-splimo.txt");
			}
		} catch (IOException e) {
			logger.error("Failed setting up file for missing keys",e);
		}
	}

	//--------------------------------------------------------------------
	public BasePluginData() {
	}

	//--------------------------------------------------------------------
	/**
	 * @see de.rpgframework.PluginData#setPlugin(de.rpgframework.RulePlugin)
	 */
	@Override
	public void setPlugin(RulePlugin<?> plugin) {
		this.plugin = plugin;
	}

	//--------------------------------------------------------------------
	/**
	 * @see de.rpgframework.PluginData#getPlugin()
	 */
	@Override
	public RulePlugin<?> getPlugin() {
		return plugin;
	}

	//--------------------------------------------------------------------
	public abstract String getPageI18NKey();

	//--------------------------------------------------------------------
	public abstract String getHelpI18NKey();

	//--------------------------------------------------------------------
	/**
	 * @see de.rpgframework.HardcopyPluginData#getPage()
	 */
	@Override
	public int getPage() {
		String key = getPageI18NKey();
		try {
			if (i18n==null || i18n.getString(key)==null)
				return 0;
			return Integer.parseInt(i18n.getString(key));
		} catch (MissingResourceException mre) {
			if (!reportedKeys.contains(mre.getKey())) {
				reportedKeys.add(mre.getKey());
				logger.error("Missing property '"+key+"' in "+i18n.getBaseBundleName());
				if (MISSING!=null)
					MISSING.println(mre.getKey()+"=");
			}
		} catch (NumberFormatException nfe) {
			if (i18n.getString(key).length()==0)
				return 0;
			logger.error("property '"+key+"' ("+i18n.getString(key)+") in "+i18n.getBaseBundleName()+" is not an integer");
		}
		return 0;
	}

	//--------------------------------------------------------------------
	public void setPage(int page) {
		String key = getPageI18NKey();
		if (i18n instanceof WriteablePropertyResourceBundle) 
			((WriteablePropertyResourceBundle)i18n).set(key, String.valueOf(page));
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.HardcopyPluginData#getHelpText()
	 */
	@Override
	public String getHelpText() {
		if (i18nHelp==null)
			return null;
		String key = getHelpI18NKey();
		if (!SplitterMondCore.hasLicense())
			return null;

		try {
			return i18nHelp.getString(key);
		} catch (MissingResourceException mre) {
			if (!reportedKeys.contains(key)) {
				reportedKeys.add(key);
				logger.error("Missing property '"+key+"' in "+i18nHelp.getBaseBundleName());
				if (MISSING_HELP!=null)
					MISSING_HELP.println(mre.getKey()+"=");
			}
		}
		return null;
	}

	//--------------------------------------------------------------------
	public void setHelpText(String txt) {
		String key = getHelpI18NKey();
		if (i18n instanceof WriteablePropertyResourceBundle) 
			((WriteablePropertyResourceBundle)i18nHelp).set(key, txt);
	}

	//-------------------------------------------------------------------
	public void setResourceBundle(ResourceBundle i18n) {
		this.i18n = i18n;
	}

	//--------------------------------------------------------------------
	/**
	 * @see de.rpgframework.PluginData#getResourceBundle()
	 */
	@Override
	public ResourceBundle getResourceBundle() {
		return i18n;
	}

	//-------------------------------------------------------------------
	public void setHelpResourceBundle(ResourceBundle i18n) {
		this.i18nHelp = i18n;
	}

	//--------------------------------------------------------------------
	/**
	 * @see de.rpgframework.PluginData#getHelpResourceBundle()
	 */
	@Override
	public ResourceBundle getHelpResourceBundle() {
		return i18nHelp;
	}

	//--------------------------------------------------------------------
	/**
	 * If this data reflects a product, this method returns the
	 * unabbreviated product name.
	 * @return full product name or NULL
	 */
	public String getProductName() {
		try {
			return i18n.getString("plugin."+getPlugin().getID()+".productname.full");
		} catch (MissingResourceException e) {
			if (!reportedKeys.contains(e.getKey())) {
				reportedKeys.add(e.getKey());
				logger.error(e.toString()+" in "+i18n.getBaseBundleName());
				if (MISSING!=null)
					MISSING.println(e.getKey()+"   \t in "+i18n.getBaseBundleName()+".properties");
			}
		}
		return null;
	}

	//--------------------------------------------------------------------
	/**
	 * If this plugin reflects a product, this method returns the
	 * abbreviated product name.
	 * @return Short product name or NULL
	 */
	public String getProductNameShort() {
		try {
			return i18n.getString("plugin."+getPlugin().getID()+".productname.short");
		} catch (MissingResourceException e) {
			if (!reportedKeys.contains(e.getKey())) {
				reportedKeys.add(e.getKey());
				logger.error(e.toString()+" in "+i18n.getBaseBundleName());
				if (MISSING!=null)
					MISSING.println(e.getKey()+"   \t in "+i18n.getBaseBundleName()+".properties");
			}
		}
		return null;
	}

	//-------------------------------------------------------------------
	public static void flushMissingKeys() {
		if (MISSING!=null)
			MISSING.flush();
		if (MISSING_HELP!=null)
			MISSING_HELP.flush();
	}

}
