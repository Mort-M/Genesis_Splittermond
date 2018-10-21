/**
 *
 */
package org.prelle.rpgframework.splittermond.buu;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.prelle.splimo.BasePluginData;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;

import de.rpgframework.ConfigContainer;
import de.rpgframework.ConfigOption;
import de.rpgframework.RulePlugin;
import de.rpgframework.RulePluginFeatures;
import de.rpgframework.core.CommandResult;
import de.rpgframework.core.CommandType;
import de.rpgframework.core.RoleplayingSystem;

/**
 * @author Stefan
 *
 */
public class BestienUndUngeheuerPlugin implements RulePlugin<SpliMoCharacter> {

	private static Logger logger = Logger.getLogger("splittermond.buu");

	private static PropertyResourceBundle i18NResources;
	private static PropertyResourceBundle i18NHelpResources;

	//--------------------------------------------------------------------
	public BestienUndUngeheuerPlugin() {
		i18NResources = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splittermond/buu");
		i18NHelpResources = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splittermond/buu-help");
	}

	//--------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RulePlugin#getID()
	 */
	@Override
	public String getID() {
		return "BuU";
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.core.CommandBusListener#getReadableName()
	 */
	@Override
	public String getReadableName() {
		if (this.getClass().getPackage().getImplementationTitle()!=null)
			return this.getClass().getPackage().getImplementationTitle();
		return "Bestien und Ungeheuer";
	}

	//--------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RulePlugin#getRules()
	 */
	@Override
	public RoleplayingSystem getRules() {
		return RoleplayingSystem.SPLITTERMOND;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RulePlugin#getRequiredPlugins()
	 */
	@Override
	public Collection<String> getRequiredPlugins() {
		return Arrays.asList("CORE","MSK");
	}

	//--------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RulePlugin#getSupportedFeatures()
	 */
	@Override
	public Collection<RulePluginFeatures> getSupportedFeatures() {
		return Arrays.asList(new RulePluginFeatures[]{RulePluginFeatures.DATA});
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RulePlugin#attachConfigurationTree(de.rpgframework.ConfigContainer)
	 */
	@Override
	public void attachConfigurationTree(ConfigContainer addBelow) {
	}

	//--------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RulePlugin#getConfiguration()
	 */
	@Override
	public List<ConfigOption<?>> getConfiguration() {
		return null;
	}

	//--------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RulePlugin#init()
	 */
	@Override
	public void init() {
		logger.info("START -------------------------------BuU-----------------------------------------------");
		SplitterMondCore.loadCreatureTypes(this, ClassLoader.getSystemResourceAsStream("data/splittermond/creaturetypes-buu.xml"), i18NResources, i18NHelpResources);
		SplitterMondCore.loadCreatureFeatureTypes(this, ClassLoader.getSystemResourceAsStream("data/splittermond/creaturefeaturetypes-buu.xml"), i18NResources, i18NHelpResources);
		SplitterMondCore.loadCreatures(this, ClassLoader.getSystemResourceAsStream("data/splittermond/creatures-buu.xml"), i18NResources, i18NHelpResources);
		SplitterMondCore.loadMaterials(this, ClassLoader.getSystemResourceAsStream("data/splittermond/materials-buu.xml"), i18NResources, i18NHelpResources);
		BasePluginData.flushMissingKeys();
		logger.debug("STOP  Initialize");
		
	}

	//--------------------------------------------------------------------
	/**
	 * @see de.rpgframework.core.CommandBusListener#willProcessCommand(java.lang.Object, de.rpgframework.core.CommandType, java.lang.Object[])
	 */
	@Override
	public boolean willProcessCommand(Object src, CommandType type,
			Object... values) {
		return false;
	}

	//--------------------------------------------------------------------
	/**
	 * @see de.rpgframework.core.CommandBusListener#handleCommand(java.lang.Object, de.rpgframework.core.CommandType, java.lang.Object[])
	 */
	@Override
	public CommandResult handleCommand(Object src, CommandType type,
			Object... values) {
		return null;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RulePlugin#getAboutHTML()
	 */
	@Override
	public InputStream getAboutHTML() {
		return ClassLoader.getSystemResourceAsStream("i18n/splittermond/buu.html");
	}

}
