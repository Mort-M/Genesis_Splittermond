/**
 *
 */
package org.prelle.rpgframework.splittermond.beastmaster;

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
public class BeastMasterPlugin implements RulePlugin<SpliMoCharacter> {

	private static Logger logger = Logger.getLogger("splittermond.beast");

	private static PropertyResourceBundle i18NResources;
	private static PropertyResourceBundle i18NHelpResources;

	//--------------------------------------------------------------------
	public BeastMasterPlugin() {
		i18NResources = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splittermond/beastmaster");
		i18NHelpResources = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splittermond/beastmaster-help");
	}

	//--------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RulePlugin#getID()
	 */
	@Override
	public String getID() {
		return "Beastmaster";
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.core.CommandBusListener#getReadableName()
	 */
	@Override
	public String getReadableName() {
		if (this.getClass().getPackage().getImplementationTitle()!=null)
			return this.getClass().getPackage().getImplementationTitle();
		return "Bestienmeister";
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
		return Arrays.asList("CORE","Selenia","BuU");
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
		logger.info("START -------------------------------Beastmaster--------------------------------------------");
		SplitterMondCore.loadMasterships(this, ClassLoader.getSystemResourceAsStream("data/splittermond/masterships-beastmaster.xml"), i18NResources, i18NHelpResources);
		SplitterMondCore.loadSpells(this, ClassLoader.getSystemResourceAsStream("data/splittermond/spells-beastmaster.xml"), i18NResources, i18NHelpResources);
		SplitterMondCore.loadCreatureTypes(this, ClassLoader.getSystemResourceAsStream("data/splittermond/creaturetypes-beastmaster.xml"), i18NResources, i18NHelpResources);
		SplitterMondCore.loadCreatureModules(this, ClassLoader.getSystemResourceAsStream("data/splittermond/creaturemodules-beastmaster.xml"), i18NResources, i18NHelpResources);
		SplitterMondCore.loadCreatures(this, ClassLoader.getSystemResourceAsStream("data/splittermond/creatures-beastmaster.xml"), i18NResources, i18NHelpResources);
		SplitterMondCore.loadCreatureModules(this, ClassLoader.getSystemResourceAsStream("data/splittermond/creaturetrainings-beastmaster.xml"), i18NResources, i18NHelpResources);
		SplitterMondCore.loadEducations(this, ClassLoader.getSystemResourceAsStream("data/splittermond/educations-beastmaster.xml"), i18NResources, i18NHelpResources);
		BasePluginData.flushMissingKeys();
		logger.info("STOP  -------------------------------Beastmaster--------------------------------------------");
//		logger.fatal("Stop here");
//		System.exit(9);
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
		return ClassLoader.getSystemResourceAsStream("i18n/splittermond/beastmaster.html");
	}

}
