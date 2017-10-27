/**
 * 
 */
package org.prelle.rpgframework.splittermond.msk;

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
public class MondstahlklingenPlugin implements RulePlugin<SpliMoCharacter> {
	
	private static Logger logger = Logger.getLogger("splittermond.msk");

	private static PropertyResourceBundle i18NResources;
	private static PropertyResourceBundle i18NHelpResources;

	//--------------------------------------------------------------------
	public MondstahlklingenPlugin() {
		i18NResources = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splittermond-msk");
		i18NHelpResources = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splittermond-msk-help");
	}

	//--------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RulePlugin#getID()
	 */
	@Override
	public String getID() {
		return "MSK";
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.core.CommandBusListener#getReadableName()
	 */
	@Override
	public String getReadableName() {
		if (this.getClass().getPackage().getImplementationTitle()!=null)
			return this.getClass().getPackage().getImplementationTitle();
		return "Mondstahlklingen";
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
		return Arrays.asList("CORE");
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
		logger.info("START -------------------------------MSG-------------------------------------------------");
		SplitterMondCore.loadFeatureTypes(this, ClassLoader.getSystemResourceAsStream("data/splittermond/featuretypes-msk.xml"), i18NResources, i18NHelpResources);		
		SplitterMondCore.loadMaterials(this, ClassLoader.getSystemResourceAsStream("data/splittermond/materials-msk.xml"), i18NResources, i18NHelpResources);		
		SplitterMondCore.loadEnhancements(this, ClassLoader.getSystemResourceAsStream("data/splittermond/enhancements-msk.xml"), i18NResources, i18NHelpResources);		
		SplitterMondCore.loadPersonalizations(this, ClassLoader.getSystemResourceAsStream("data/splittermond/personalizations-msk.xml"), i18NResources, i18NHelpResources);		
		SplitterMondCore.loadEquipment(this, ClassLoader.getSystemResourceAsStream("data/splittermond/handgemenge.xml"), i18NResources, i18NHelpResources);		
		SplitterMondCore.loadEquipment(this, ClassLoader.getSystemResourceAsStream("data/splittermond/hiebwaffen.xml"), i18NResources, i18NHelpResources);		
		SplitterMondCore.loadEquipment(this, ClassLoader.getSystemResourceAsStream("data/splittermond/kettenwaffen.xml"), i18NResources, i18NHelpResources);		
		SplitterMondCore.loadEquipment(this, ClassLoader.getSystemResourceAsStream("data/splittermond/klingenwaffen.xml"), i18NResources, i18NHelpResources);		
		SplitterMondCore.loadEquipment(this, ClassLoader.getSystemResourceAsStream("data/splittermond/stangenwaffen.xml"), i18NResources, i18NHelpResources);		
		SplitterMondCore.loadEquipment(this, ClassLoader.getSystemResourceAsStream("data/splittermond/schusswaffen.xml"), i18NResources, i18NHelpResources);		
		SplitterMondCore.loadEquipment(this, ClassLoader.getSystemResourceAsStream("data/splittermond/wurfwaffen.xml"), i18NResources, i18NHelpResources);		
		SplitterMondCore.loadEquipment(this, ClassLoader.getSystemResourceAsStream("data/splittermond/ruestungen.xml"), i18NResources, i18NHelpResources);		
		SplitterMondCore.loadEquipment(this, ClassLoader.getSystemResourceAsStream("data/splittermond/schilde.xml"), i18NResources, i18NHelpResources);		
		SplitterMondCore.loadEquipment(this, ClassLoader.getSystemResourceAsStream("data/splittermond/weaponitems.xml"), i18NResources, i18NHelpResources);		
		SplitterMondCore.loadEquipment(this, ClassLoader.getSystemResourceAsStream("data/splittermond/container.xml"), i18NResources, i18NHelpResources);		
		SplitterMondCore.loadEquipment(this, ClassLoader.getSystemResourceAsStream("data/splittermond/tools.xml"), i18NResources, i18NHelpResources);		
		SplitterMondCore.loadEquipment(this, ClassLoader.getSystemResourceAsStream("data/splittermond/animals.xml"), i18NResources, i18NHelpResources);		
		SplitterMondCore.loadEquipment(this, ClassLoader.getSystemResourceAsStream("data/splittermond/clothing.xml"), i18NResources, i18NHelpResources);		
		SplitterMondCore.loadEquipment(this, ClassLoader.getSystemResourceAsStream("data/splittermond/shady.xml"), i18NResources, i18NHelpResources);		
		SplitterMondCore.loadEquipment(this, ClassLoader.getSystemResourceAsStream("data/splittermond/light.xml"), i18NResources, i18NHelpResources);		
		SplitterMondCore.loadEquipment(this, ClassLoader.getSystemResourceAsStream("data/splittermond/travel.xml"), i18NResources, i18NHelpResources);		
		SplitterMondCore.loadEquipment(this, ClassLoader.getSystemResourceAsStream("data/splittermond/healing.xml"), i18NResources, i18NHelpResources);		
		SplitterMondCore.loadEquipment(this, ClassLoader.getSystemResourceAsStream("data/splittermond/alchemy.xml"), i18NResources, i18NHelpResources);		
		SplitterMondCore.loadEquipment(this, ClassLoader.getSystemResourceAsStream("data/splittermond/writing.xml"), i18NResources, i18NHelpResources);		
		SplitterMondCore.loadEquipment(this, ClassLoader.getSystemResourceAsStream("data/splittermond/climbing.xml"), i18NResources, i18NHelpResources);		
		SplitterMondCore.loadEquipment(this, ClassLoader.getSystemResourceAsStream("data/splittermond/cosmetics.xml"), i18NResources, i18NHelpResources);		
		SplitterMondCore.loadEquipment(this, ClassLoader.getSystemResourceAsStream("data/splittermond/recreation.xml"), i18NResources, i18NHelpResources);		
		SplitterMondCore.loadSpells(this, ClassLoader.getSystemResourceAsStream("data/splittermond/spells-msk.xml"), i18NResources, i18NHelpResources);		
		SplitterMondCore.loadMasterships(this, ClassLoader.getSystemResourceAsStream("data/splittermond/skills-msk.xml"), i18NResources, i18NHelpResources);		
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
		return ClassLoader.getSystemResourceAsStream("i18n/splittermond-msk.html");
	}

}
