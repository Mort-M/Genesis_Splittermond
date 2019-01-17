/**
 *
 */
package org.prelle.rpgframework.splittermond.data;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
public class SplittermondDataPlugin implements RulePlugin<SpliMoCharacter> {

	private static Logger logger = LogManager.getLogger("splittermond.data");

	//--------------------------------------------------------------------
	public SplittermondDataPlugin() {
	}

	//--------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RulePlugin#getID()
	 */
	@Override
	public String getID() {
		return "Data";
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.core.CommandBusListener#getReadableName()
	 */
	@Override
	public String getReadableName() {
		if (this.getClass().getPackage().getImplementationTitle()!=null)
			return this.getClass().getPackage().getImplementationTitle();
		return "Data";
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
		String pack = getClass().getPackage().getName();
		pack = "org/prelle/rpgframework/splittermond/data";
		
		logger.info("START -------------------------------Core-----------------------------------------------");
		PluginSkeleton CORE = new PluginSkeleton("CORE", "Splittermond Core Rules");
		SplitterMondCore.loadPowers(CORE, ClassLoader.getSystemResourceAsStream(pack+"/core/data/powers.xml"), CORE.getResources(), CORE.getHelpResources());
		SplitterMondCore.loadSkills(CORE, ClassLoader.getSystemResourceAsStream(pack+"/core/data/skills.xml"), CORE.getResources(), CORE.getHelpResources());
		SplitterMondCore.loadMasterships(CORE, ClassLoader.getSystemResourceAsStream(pack+"/core/data/masterships.xml"), CORE.getResources(), CORE.getHelpResources());
		SplitterMondCore.loadSpells(CORE, ClassLoader.getSystemResourceAsStream(pack+"/core/data/spells.xml"), CORE.getResources(), CORE.getHelpResources());
		SplitterMondCore.loadResources(CORE, ClassLoader.getSystemResourceAsStream(pack+"/core/data/resources.xml"), CORE.getResources(), CORE.getHelpResources());
		SplitterMondCore.loadLanguages(CORE, ClassLoader.getSystemResourceAsStream(pack+"/core/data/languages.xml"), CORE.getResources(), CORE.getHelpResources());
		SplitterMondCore.loadCultureLores(CORE, ClassLoader.getSystemResourceAsStream(pack+"/core/data/culturelores.xml"), CORE.getResources(), CORE.getHelpResources());
		SplitterMondCore.loadBackgrounds(CORE, ClassLoader.getSystemResourceAsStream(pack+"/core/data/backgrounds.xml"), CORE.getResources(), CORE.getHelpResources());
		SplitterMondCore.loadRaces(CORE, ClassLoader.getSystemResourceAsStream(pack+"/core/data/races.xml"), CORE.getResources(), CORE.getHelpResources());
		SplitterMondCore.loadCultures(CORE, ClassLoader.getSystemResourceAsStream(pack+"/core/data/cultures.xml"), CORE.getResources(), CORE.getHelpResources());
		SplitterMondCore.loadEducations(CORE, ClassLoader.getSystemResourceAsStream(pack+"/core/data/educations.xml"), CORE.getResources(), CORE.getHelpResources());
		SplitterMondCore.loadFeatureTypes(CORE, ClassLoader.getSystemResourceAsStream(pack+"/core/data/featuretypes.xml"), CORE.getResources(), CORE.getHelpResources());
		SplitterMondCore.loadMaterials(CORE, ClassLoader.getSystemResourceAsStream(pack+"/core/data/materials.xml"), CORE.getResources(), CORE.getHelpResources());
		SplitterMondCore.loadEquipment(CORE, ClassLoader.getSystemResourceAsStream(pack+"/core/data/equipment.xml"), CORE.getResources(), CORE.getHelpResources());
		SplitterMondCore.loadEnhancements(CORE, ClassLoader.getSystemResourceAsStream(pack+"/core/data/enhancements.xml"), CORE.getResources(), CORE.getHelpResources());
		SplitterMondCore.loadCreatureTypes(CORE, ClassLoader.getSystemResourceAsStream(pack+"/core/data/creaturetypes.xml"), CORE.getResources(), CORE.getHelpResources());
		SplitterMondCore.loadCreatureFeatureTypes(CORE, ClassLoader.getSystemResourceAsStream(pack+"/core/data/creaturefeaturetypes.xml"), CORE.getResources(), CORE.getHelpResources());
		SplitterMondCore.loadCreatures(CORE, ClassLoader.getSystemResourceAsStream(pack+"/core/data/creatures.xml"), CORE.getResources(), CORE.getHelpResources());

		logger.info("START -------------------------------World-----------------------------------------------");
		PluginSkeleton WORLD = new PluginSkeleton("World", "Splittermond - Die Welt");
		SplitterMondCore.loadCultureLores(WORLD, ClassLoader.getSystemResourceAsStream(pack+"/world/data/culturelores-world.xml"), WORLD.getResources(), WORLD.getHelpResources());
		SplitterMondCore.loadLanguages(WORLD, ClassLoader.getSystemResourceAsStream(pack+"/world/data/languages-world.xml"), WORLD.getResources(), WORLD.getHelpResources());		
		SplitterMondCore.loadCultures(WORLD, ClassLoader.getSystemResourceAsStream(pack+"/world/data/cultures-world.xml"), WORLD.getResources(), WORLD.getHelpResources());	

		logger.info("START -------------------------------Mondstahlklingen------------------------------------");
		PluginSkeleton MSK = new PluginSkeleton("MSK", "Mondstahlklingen");
		SplitterMondCore.loadFeatureTypes(MSK, ClassLoader.getSystemResourceAsStream(pack+"/msk/data/featuretypes-msk.xml"), MSK.getResources(), MSK.getHelpResources());		
		SplitterMondCore.loadMaterials(MSK, ClassLoader.getSystemResourceAsStream(pack+"/msk/data/materials-msk.xml"), MSK.getResources(), MSK.getHelpResources());		
		SplitterMondCore.loadEnhancements(MSK, ClassLoader.getSystemResourceAsStream(pack+"/msk/data/enhancements-msk.xml"), MSK.getResources(), MSK.getHelpResources());		
		SplitterMondCore.loadPersonalizations(MSK, ClassLoader.getSystemResourceAsStream(pack+"/msk/data/personalizations-msk.xml"), MSK.getResources(), MSK.getHelpResources());		
		SplitterMondCore.loadEquipment(MSK, ClassLoader.getSystemResourceAsStream(pack+"/msk/data/handgemenge.xml"), MSK.getResources(), MSK.getHelpResources());		
		SplitterMondCore.loadEquipment(MSK, ClassLoader.getSystemResourceAsStream(pack+"/msk/data/hiebwaffen.xml"), MSK.getResources(), MSK.getHelpResources());		
		SplitterMondCore.loadEquipment(MSK, ClassLoader.getSystemResourceAsStream(pack+"/msk/data/kettenwaffen.xml"), MSK.getResources(), MSK.getHelpResources());		
		SplitterMondCore.loadEquipment(MSK, ClassLoader.getSystemResourceAsStream(pack+"/msk/data/klingenwaffen.xml"), MSK.getResources(), MSK.getHelpResources());		
		SplitterMondCore.loadEquipment(MSK, ClassLoader.getSystemResourceAsStream(pack+"/msk/data/stangenwaffen.xml"), MSK.getResources(), MSK.getHelpResources());		
		SplitterMondCore.loadEquipment(MSK, ClassLoader.getSystemResourceAsStream(pack+"/msk/data/schusswaffen.xml"), MSK.getResources(), MSK.getHelpResources());		
		SplitterMondCore.loadEquipment(MSK, ClassLoader.getSystemResourceAsStream(pack+"/msk/data/wurfwaffen.xml"), MSK.getResources(), MSK.getHelpResources());		
		SplitterMondCore.loadEquipment(MSK, ClassLoader.getSystemResourceAsStream(pack+"/msk/data/ruestungen.xml"), MSK.getResources(), MSK.getHelpResources());		
		SplitterMondCore.loadEquipment(MSK, ClassLoader.getSystemResourceAsStream(pack+"/msk/data/schilde.xml"), MSK.getResources(), MSK.getHelpResources());		
		SplitterMondCore.loadEquipment(MSK, ClassLoader.getSystemResourceAsStream(pack+"/msk/data/weaponitems.xml"), MSK.getResources(), MSK.getHelpResources());		
		SplitterMondCore.loadEquipment(MSK, ClassLoader.getSystemResourceAsStream(pack+"/msk/data/container.xml"), MSK.getResources(), MSK.getHelpResources());		
		SplitterMondCore.loadEquipment(MSK, ClassLoader.getSystemResourceAsStream(pack+"/msk/data/tools.xml"), MSK.getResources(), MSK.getHelpResources());		
		SplitterMondCore.loadEquipment(MSK, ClassLoader.getSystemResourceAsStream(pack+"/msk/data/animals.xml"), MSK.getResources(), MSK.getHelpResources());		
		SplitterMondCore.loadEquipment(MSK, ClassLoader.getSystemResourceAsStream(pack+"/msk/data/clothing.xml"), MSK.getResources(), MSK.getHelpResources());		
		SplitterMondCore.loadEquipment(MSK, ClassLoader.getSystemResourceAsStream(pack+"/msk/data/shady.xml"), MSK.getResources(), MSK.getHelpResources());		
		SplitterMondCore.loadEquipment(MSK, ClassLoader.getSystemResourceAsStream(pack+"/msk/data/light.xml"), MSK.getResources(), MSK.getHelpResources());		
		SplitterMondCore.loadEquipment(MSK, ClassLoader.getSystemResourceAsStream(pack+"/msk/data/travel.xml"), MSK.getResources(), MSK.getHelpResources());		
		SplitterMondCore.loadEquipment(MSK, ClassLoader.getSystemResourceAsStream(pack+"/msk/data/healing.xml"), MSK.getResources(), MSK.getHelpResources());		
		SplitterMondCore.loadEquipment(MSK, ClassLoader.getSystemResourceAsStream(pack+"/msk/data/alchemy.xml"), MSK.getResources(), MSK.getHelpResources());		
		SplitterMondCore.loadEquipment(MSK, ClassLoader.getSystemResourceAsStream(pack+"/msk/data/writing.xml"), MSK.getResources(), MSK.getHelpResources());		
		SplitterMondCore.loadEquipment(MSK, ClassLoader.getSystemResourceAsStream(pack+"/msk/data/climbing.xml"), MSK.getResources(), MSK.getHelpResources());		
		SplitterMondCore.loadEquipment(MSK, ClassLoader.getSystemResourceAsStream(pack+"/msk/data/cosmetics.xml"), MSK.getResources(), MSK.getHelpResources());		
		SplitterMondCore.loadEquipment(MSK, ClassLoader.getSystemResourceAsStream(pack+"/msk/data/recreation.xml"), MSK.getResources(), MSK.getHelpResources());		
		SplitterMondCore.loadSpells   (MSK, ClassLoader.getSystemResourceAsStream(pack+"/msk/data/spells-msk.xml"), MSK.getResources(), MSK.getHelpResources());		
		SplitterMondCore.loadMasterships(MSK, ClassLoader.getSystemResourceAsStream(pack+"/msk/data/skills-msk.xml"), MSK.getResources(), MSK.getHelpResources());		

		logger.info("START -------------------------------BuU-----------------------------------------------");
		PluginSkeleton BUU = new PluginSkeleton("BuU", "Bestien und Unheheuer");
		SplitterMondCore.loadCreatureTypes(BUU, ClassLoader.getSystemResourceAsStream(pack+"/buu/data/creaturetypes-buu.xml"), BUU.getResources(), BUU.getHelpResources());
		SplitterMondCore.loadCreatureFeatureTypes(BUU, ClassLoader.getSystemResourceAsStream(pack+"/buu/data/creaturefeaturetypes-buu.xml"), BUU.getResources(), BUU.getHelpResources());
		SplitterMondCore.loadCreatures(BUU, ClassLoader.getSystemResourceAsStream(pack+"/buu/data/creatures-buu.xml"), BUU.getResources(), BUU.getHelpResources());
		SplitterMondCore.loadMaterials(BUU, ClassLoader.getSystemResourceAsStream(pack+"/buu/data/materials-buu.xml"), BUU.getResources(), BUU.getHelpResources());

		logger.info("START -------------------------------Beastmaster---------------------------------------");
		PluginSkeleton BEAST = new PluginSkeleton("Beastmaster", "Bestienmeister");
		SplitterMondCore.loadMasterships(BEAST, ClassLoader.getSystemResourceAsStream(pack+"/beastmaster/data/masterships-beastmaster.xml"), BEAST.getResources(), BEAST.getHelpResources());
		SplitterMondCore.loadSpells(BEAST, ClassLoader.getSystemResourceAsStream(pack+"/beastmaster/data/spells-beastmaster.xml"), BEAST.getResources(), BEAST.getHelpResources());
		SplitterMondCore.loadCreatureTypes(BEAST, ClassLoader.getSystemResourceAsStream(pack+"/beastmaster/data/creaturetypes-beastmaster.xml"), BEAST.getResources(), BEAST.getHelpResources());
		SplitterMondCore.loadCreatureModules(BEAST, ClassLoader.getSystemResourceAsStream(pack+"/beastmaster/data/creaturemodules-beastmaster.xml"), BEAST.getResources(), BEAST.getHelpResources());
		SplitterMondCore.loadCreatures(BEAST, ClassLoader.getSystemResourceAsStream(pack+"/beastmaster/data/creatures-beastmaster.xml"), BEAST.getResources(), BEAST.getHelpResources());
		SplitterMondCore.loadCreatureModules(BEAST, ClassLoader.getSystemResourceAsStream(pack+"/beastmaster/data/creaturetrainings-beastmaster.xml"), BEAST.getResources(), BEAST.getHelpResources());
		SplitterMondCore.loadEducations(BEAST, ClassLoader.getSystemResourceAsStream(pack+"/beastmaster/data/educations-beastmaster.xml"), BEAST.getResources(), BEAST.getHelpResources());
		SplitterMondCore.loadEquipment(BEAST, ClassLoader.getSystemResourceAsStream(pack+"/beastmaster/data/equipment-beastmaster.xml"), BEAST.getResources(), BEAST.getHelpResources());

		logger.info("START -------------------------------Esmoda--------------------------------------------");
		PluginSkeleton ESMODA = new PluginSkeleton("Esmoda", "Esmoda");
		SplitterMondCore.loadEquipment(ESMODA, ClassLoader.getSystemResourceAsStream(pack+"/esmoda/data/alchemy-esmoda.xml"), ESMODA.getResources(), ESMODA.getHelpResources());
		SplitterMondCore.loadEquipment(ESMODA, ClassLoader.getSystemResourceAsStream(pack+"/esmoda/data/equipment-esmoda.xml"), ESMODA.getResources(), ESMODA.getHelpResources());
		SplitterMondCore.loadEducations(ESMODA, ClassLoader.getSystemResourceAsStream(pack+"/esmoda/data/educations-esmoda.xml"), ESMODA.getResources(), ESMODA.getHelpResources());
		SplitterMondCore.loadMaterials(ESMODA, ClassLoader.getSystemResourceAsStream(pack+"/esmoda/data/materials-esmoda.xml"), ESMODA.getResources(), ESMODA.getHelpResources());

		logger.info("START -------------------------------Fahrende Völker-----------------------------------");
		PluginSkeleton FAHREND = new PluginSkeleton("FahrendeVoelker", "Fahrende Völker");
		SplitterMondCore.loadMasterships(FAHREND, ClassLoader.getSystemResourceAsStream(pack+"/fahrendevoelker/data/masterships-fahrendevoelker.xml"), FAHREND.getResources(), FAHREND.getHelpResources());
		SplitterMondCore.loadCultures(FAHREND, ClassLoader.getSystemResourceAsStream(pack+"/fahrendevoelker/data/cultures-fahrendevoelker.xml"), FAHREND.getResources(), FAHREND.getHelpResources());
		SplitterMondCore.loadEquipment(FAHREND, ClassLoader.getSystemResourceAsStream(pack+"/fahrendevoelker/data/equipment-fahrendevoelker.xml"), FAHREND.getResources(), FAHREND.getHelpResources());
		SplitterMondCore.loadEducations(FAHREND, ClassLoader.getSystemResourceAsStream(pack+"/fahrendevoelker/data/educations-fahrendevoelker.xml"), FAHREND.getResources(), FAHREND.getHelpResources());
		SplitterMondCore.loadNameTable(FAHREND, ClassLoader.getSystemResourceAsStream(pack+"/fahrendevoelker/data/nametable-teleshai.xml"), FAHREND.getResources(), FAHREND.getHelpResources());
		SplitterMondCore.loadSpells(FAHREND, ClassLoader.getSystemResourceAsStream(pack+"/fahrendevoelker/data/spells-fahrendevoelker.xml"), FAHREND.getResources(), FAHREND.getHelpResources());
		SplitterMondCore.loadCreatures(FAHREND, ClassLoader.getSystemResourceAsStream(pack+"/fahrendevoelker/data/creatures-fahrendevoelker.xml"), FAHREND.getResources(), FAHREND.getHelpResources());

		logger.info("START -------------------------------Farukan-------------------------------------------");
		PluginSkeleton FARUKAN = new PluginSkeleton("Farukan", "Farukan");
		SplitterMondCore.loadMasterships(FARUKAN, ClassLoader.getSystemResourceAsStream(pack+"/farukan/data/masterships-farukan.xml"), FARUKAN.getResources(), FARUKAN.getHelpResources());
		SplitterMondCore.loadSpells(FARUKAN, ClassLoader.getSystemResourceAsStream(pack+"/farukan/data/spells-farukan.xml"), FARUKAN.getResources(), FARUKAN.getHelpResources());
    	SplitterMondCore.loadEquipment(FARUKAN, ClassLoader.getSystemResourceAsStream(pack+"/farukan/data/equipment-farukan.xml"), FARUKAN.getResources(), FARUKAN.getHelpResources());
		SplitterMondCore.loadEducations(FARUKAN, ClassLoader.getSystemResourceAsStream(pack+"/farukan/data/educations-farukan.xml"), FARUKAN.getResources(), FARUKAN.getHelpResources());
		SplitterMondCore.loadNameTable(FARUKAN, ClassLoader.getSystemResourceAsStream(pack+"/farukan/data/nametable-farukan.xml"), FARUKAN.getResources(), FARUKAN.getHelpResources());
		SplitterMondCore.loadMaterials(FARUKAN, ClassLoader.getSystemResourceAsStream(pack+"/farukan/data/materials-farukan.xml"), FARUKAN.getResources(), FARUKAN.getHelpResources());

		logger.info("START -------------------------------Flammensenke--------------------------------------");
		PluginSkeleton FLAMMEN = new PluginSkeleton("Flammensenke", "Flammensenke");
		SplitterMondCore.loadEquipment(FLAMMEN, ClassLoader.getSystemResourceAsStream(pack+"/flammensenke/data/equipment-flammensenke.xml"), FLAMMEN.getResources(), FLAMMEN.getHelpResources());
		SplitterMondCore.loadEducations(FLAMMEN, ClassLoader.getSystemResourceAsStream(pack+"/flammensenke/data/educations-flammensenke.xml"), FLAMMEN.getResources(), FLAMMEN.getHelpResources());
		SplitterMondCore.loadMaterials(FLAMMEN, ClassLoader.getSystemResourceAsStream(pack+"/flammensenke/data/materials-flammensenke.xml"), FLAMMEN.getResources(), FLAMMEN.getHelpResources());

		logger.info("START -------------------------------Selenia-------------------------------------------");
		PluginSkeleton SELENIA = new PluginSkeleton("Selenia", "Selenia");
		SplitterMondCore.loadMasterships(SELENIA, ClassLoader.getSystemResourceAsStream(pack+"/selenia/data/masterships-selenia.xml"), SELENIA.getResources(), SELENIA.getHelpResources());
		SplitterMondCore.loadEquipment(SELENIA, ClassLoader.getSystemResourceAsStream(pack+"/selenia/data/equipment-selenia.xml"), SELENIA.getResources(), SELENIA.getHelpResources());
		SplitterMondCore.loadEducations(SELENIA, ClassLoader.getSystemResourceAsStream(pack+"/selenia/data/educations-selenia.xml"), SELENIA.getResources(), SELENIA.getHelpResources());
		SplitterMondCore.loadNameTable(SELENIA, ClassLoader.getSystemResourceAsStream(pack+"/selenia/data/nametable-selenia.xml"), SELENIA.getResources(), SELENIA.getHelpResources());
		SplitterMondCore.loadFeatureTypes(SELENIA, ClassLoader.getSystemResourceAsStream(pack+"/selenia/data/featuretypes-selenia.xml"), SELENIA.getResources(), SELENIA.getHelpResources());

		logger.info("START -------------------------------Götter--------------------------------------------");
		PluginSkeleton GOETTER = new PluginSkeleton("GOETTER", "Die Götter");
		SplitterMondCore.loadResources(GOETTER, ClassLoader.getSystemResourceAsStream(pack+"/goetter/data/resources-goetter.xml"), GOETTER.getResources(), GOETTER.getHelpResources());		
		SplitterMondCore.loadPowers(GOETTER, ClassLoader.getSystemResourceAsStream(pack+"/goetter/data/powers-goetter.xml"), GOETTER.getResources(), GOETTER.getHelpResources());		
		SplitterMondCore.loadMasterships(GOETTER, ClassLoader.getSystemResourceAsStream(pack+"/goetter/data/masterships-goetter.xml"), GOETTER.getResources(), GOETTER.getHelpResources());		
		SplitterMondCore.loadAspects(GOETTER, ClassLoader.getSystemResourceAsStream(pack+"/goetter/data/aspects-goetter.xml"), GOETTER.getResources(), GOETTER.getHelpResources());		
		SplitterMondCore.loadSpells(GOETTER, ClassLoader.getSystemResourceAsStream(pack+"/goetter/data/spells-goetter.xml"), GOETTER.getResources(), GOETTER.getHelpResources());		
		SplitterMondCore.loadEducations(GOETTER, ClassLoader.getSystemResourceAsStream(pack+"/goetter/data/educations-goetter.xml"), GOETTER.getResources(), GOETTER.getHelpResources());
		SplitterMondCore.loadEnhancements(GOETTER, ClassLoader.getSystemResourceAsStream(pack+"/goetter/data/enhancements-goetter.xml"), GOETTER.getResources(), GOETTER.getHelpResources());		
		SplitterMondCore.loadMaterials(GOETTER, ClassLoader.getSystemResourceAsStream(pack+"/goetter/data/materials-goetter.xml"), GOETTER.getResources(), GOETTER.getHelpResources());		
		SplitterMondCore.loadDeityTypes(GOETTER, ClassLoader.getSystemResourceAsStream(pack+"/goetter/data/deitytypes-goetter.xml"), GOETTER.getResources(), GOETTER.getHelpResources());		
		SplitterMondCore.loadHolyPowers(GOETTER, ClassLoader.getSystemResourceAsStream(pack+"/goetter/data/holypowers-goetter.xml"), GOETTER.getResources(), GOETTER.getHelpResources());
		SplitterMondCore.loadDeities(GOETTER, ClassLoader.getSystemResourceAsStream(pack+"/goetter/data/deities-goetter.xml"), GOETTER.getResources(), GOETTER.getHelpResources());

		logger.info("START -------------------------------Diener der Götter---------------------------------");
		PluginSkeleton GODSERV = new PluginSkeleton("Goetterdiener", "Diener der Götter");
		SplitterMondCore.loadEducations(GODSERV, ClassLoader.getSystemResourceAsStream(pack+"/goetterdiener/data/educations-goetterdiener.xml"), GODSERV.getResources(), GODSERV.getHelpResources());

		logger.info("START -------------------------------Jenseits der Grenzen------------------------------");
		PluginSkeleton JDG = new PluginSkeleton("JDG", "Jenseits der Grenzen");
		SplitterMondCore.loadPowers(JDG, ClassLoader.getSystemResourceAsStream(pack+"/jdg/data/powers-jdg.xml"), JDG.getResources(), JDG.getHelpResources());
		SplitterMondCore.loadMasterships(JDG, ClassLoader.getSystemResourceAsStream(pack+"/jdg/data/masterships-jdg.xml"), JDG.getResources(), JDG.getHelpResources());
		SplitterMondCore.loadSpells(JDG, ClassLoader.getSystemResourceAsStream(pack+"/jdg/data/spells-jdg.xml"), JDG.getResources(), JDG.getHelpResources());

		logger.info("START -------------------------------Die Magie-----------------------------------------");
		PluginSkeleton MAGIE = new PluginSkeleton("Magie", "Die Magie");
		SplitterMondCore.loadPowers(MAGIE, ClassLoader.getSystemResourceAsStream(pack+"/magie/data/powers-magie.xml"), MAGIE.getResources(), MAGIE.getHelpResources());		
		SplitterMondCore.loadResources(MAGIE, ClassLoader.getSystemResourceAsStream(pack+"/magie/data/resources-magie.xml"), MAGIE.getResources(), MAGIE.getHelpResources());		
		SplitterMondCore.loadMasterships(MAGIE, ClassLoader.getSystemResourceAsStream(pack+"/magie/data/masterships-magie.xml"), MAGIE.getResources(), MAGIE.getHelpResources());		
		SplitterMondCore.loadSpells(MAGIE, ClassLoader.getSystemResourceAsStream(pack+"/magie/data/spells-magie.xml"), MAGIE.getResources(), MAGIE.getHelpResources());		
		SplitterMondCore.loadEducations(MAGIE, ClassLoader.getSystemResourceAsStream(pack+"/magie/data/educations-magie.xml"), MAGIE.getResources(), MAGIE.getHelpResources());		
		SplitterMondCore.loadEnhancements(MAGIE, ClassLoader.getSystemResourceAsStream(pack+"/magie/data/enhancements-magie.xml"), MAGIE.getResources(), MAGIE.getHelpResources());		

		logger.info("START -------------------------------Sadu----------------------------------------------");
		PluginSkeleton SADU = new PluginSkeleton("SADU", "Sadu");
		SplitterMondCore.loadCultures(SADU, ClassLoader.getSystemResourceAsStream(pack+"/sadu/data/cultures-sadu.xml"), SADU.getResources(), SADU.getHelpResources());
		SplitterMondCore.loadEquipment(SADU, ClassLoader.getSystemResourceAsStream(pack+"/sadu/data/equipment-sadu.xml"), SADU.getResources(), SADU.getHelpResources());
		SplitterMondCore.loadEducations(SADU, ClassLoader.getSystemResourceAsStream(pack+"/sadu/data/educations-sadu.xml"), SADU.getResources(), SADU.getHelpResources());
		SplitterMondCore.loadNameTable(SADU, ClassLoader.getSystemResourceAsStream(pack+"/sadu/data/nametable-sadu.xml"), SADU.getResources(), SADU.getHelpResources());

		logger.info("START -------------------------------Surmakar------------------------------------------");
		PluginSkeleton SURM = new PluginSkeleton("Surmakar", "Die Surmakar");
		SplitterMondCore.loadEquipment(SURM, ClassLoader.getSystemResourceAsStream(pack+"/surmakar/data/equipment-surmakar.xml"), SURM.getResources(), SURM.getHelpResources());
		SplitterMondCore.loadEquipment(SURM, ClassLoader.getSystemResourceAsStream(pack+"/surmakar/data/alchemy-surmakar.xml"), SURM.getResources(), SURM.getHelpResources());
		SplitterMondCore.loadEducations(SURM, ClassLoader.getSystemResourceAsStream(pack+"/surmakar/data/educations-surmakar.xml"), SURM.getResources(), SURM.getHelpResources());
		SplitterMondCore.loadMaterials(SURM, ClassLoader.getSystemResourceAsStream(pack+"/surmakar/data/materials-surmakar.xml"), SURM.getResources(), SURM.getHelpResources());

		logger.info("START -------------------------------Ungebrochen---------------------------------------");
		PluginSkeleton UNGE = new PluginSkeleton("UNGEBROCHEN", "Ungebrochen");
		SplitterMondCore.loadMasterships(UNGE, ClassLoader.getSystemResourceAsStream(pack+"/ungebrochen/data/masterships-ungebrochen.xml"), UNGE.getResources(), UNGE.getHelpResources());		
		SplitterMondCore.loadEquipment(UNGE, ClassLoader.getSystemResourceAsStream(pack+"/ungebrochen/data/equipment-ungebrochen.xml"), UNGE.getResources(), UNGE.getHelpResources());
		SplitterMondCore.loadCreatures(UNGE, ClassLoader.getSystemResourceAsStream(pack+"/ungebrochen/data/creatures-ungebrochen.xml"), UNGE.getResources(), UNGE.getHelpResources());
		SplitterMondCore.loadEducations(UNGE, ClassLoader.getSystemResourceAsStream(pack+"/ungebrochen/data/educations-ungebrochen.xml"), UNGE.getResources(), UNGE.getHelpResources());
		SplitterMondCore.loadNameTable(UNGE, ClassLoader.getSystemResourceAsStream(pack+"/ungebrochen/data/nametable-ungebrochen.xml"), UNGE.getResources(), UNGE.getHelpResources());
//		SplitterMondCore.loadMaterials(UNGE, ClassLoader.getSystemResourceAsStream(pack+"/ungebrochen/data/materials-ungebrochen.xml"), UNGE.getResources(), UNGE.getHelpResources());
		SplitterMondCore.loadEquipment(UNGE, ClassLoader.getSystemResourceAsStream(pack+"/ungebrochen/data/hiebwaffen-ungebrochen.xml"), UNGE.getResources(), UNGE.getHelpResources());
		SplitterMondCore.loadEquipment(UNGE, ClassLoader.getSystemResourceAsStream(pack+"/ungebrochen/data/schusswaffen-ungebrochen.xml"), UNGE.getResources(), UNGE.getHelpResources());
		SplitterMondCore.loadEquipment(UNGE, ClassLoader.getSystemResourceAsStream(pack+"/ungebrochen/data/stangenwaffen-ungebrochen.xml"), UNGE.getResources(), UNGE.getHelpResources());
		SplitterMondCore.loadEquipment(UNGE, ClassLoader.getSystemResourceAsStream(pack+"/ungebrochen/data/wurfwaffen-ungebrochen.xml"), UNGE.getResources(), UNGE.getHelpResources());

		logger.info("START -------------------------------Unreich-------------------------------------------");
		PluginSkeleton UNREICH = new PluginSkeleton("Unreich", "Das Unreich");
		SplitterMondCore.loadCultureLores(UNREICH, ClassLoader.getSystemResourceAsStream(pack+"/unreich/data/culturelores-unreich.xml"), UNREICH.getResources(), UNREICH.getHelpResources());
		SplitterMondCore.loadCultures(UNREICH, ClassLoader.getSystemResourceAsStream(pack+"/unreich/data/cultures-unreich.xml"), UNREICH.getResources(), UNREICH.getHelpResources());
		SplitterMondCore.loadEquipment(UNREICH, ClassLoader.getSystemResourceAsStream(pack+"/unreich/data/alchemy-unreich.xml"), UNREICH.getResources(), UNREICH.getHelpResources());
		SplitterMondCore.loadMaterials(UNREICH, ClassLoader.getSystemResourceAsStream(pack+"/unreich/data/materials-unreich.xml"), UNREICH.getResources(), UNREICH.getHelpResources());
		SplitterMondCore.loadEducations(UNREICH, ClassLoader.getSystemResourceAsStream(pack+"/unreich/data/educations-unreich.xml"), UNREICH.getResources(), UNREICH.getHelpResources());

		logger.info("START -------------------------------Zhoujiang-----------------------------------------");
		PluginSkeleton ZHOU = new PluginSkeleton("zhoujiang", "Zhoujiang");
		SplitterMondCore.loadCultureLores(ZHOU, ClassLoader.getSystemResourceAsStream(pack+"/zhoujiang/data/culturelores-zhoujiang.xml"), ZHOU.getResources(), ZHOU.getHelpResources());
		SplitterMondCore.loadFeatureTypes(ZHOU, ClassLoader.getSystemResourceAsStream(pack+"/zhoujiang/data/featuretypes-zhoujiang.xml"), ZHOU.getResources(), ZHOU.getHelpResources());
		SplitterMondCore.loadEquipment(ZHOU, ClassLoader.getSystemResourceAsStream(pack+"/zhoujiang/data/equipment-zhoujiang.xml"), ZHOU.getResources(), ZHOU.getHelpResources());
		SplitterMondCore.loadEquipment(ZHOU, ClassLoader.getSystemResourceAsStream(pack+"/zhoujiang/data/alchemy-zhoujiang.xml"), ZHOU.getResources(), ZHOU.getHelpResources());
		SplitterMondCore.loadMaterials(ZHOU, ClassLoader.getSystemResourceAsStream(pack+"/zhoujiang/data/materials-zhoujiang.xml"), ZHOU.getResources(), ZHOU.getHelpResources());
		SplitterMondCore.loadMasterships(ZHOU, ClassLoader.getSystemResourceAsStream(pack+"/zhoujiang/data/masterships-zhoujiang.xml"), ZHOU.getResources(), ZHOU.getHelpResources());
		SplitterMondCore.loadEducations(ZHOU, ClassLoader.getSystemResourceAsStream(pack+"/zhoujiang/data/educations-zhoujiang.xml"), ZHOU.getResources(), ZHOU.getHelpResources());
		SplitterMondCore.loadSpells(ZHOU, ClassLoader.getSystemResourceAsStream(pack+"/zhoujiang/data/spells-zhoujiang.xml"), ZHOU.getResources(), ZHOU.getHelpResources());
		SplitterMondCore.loadNameTable(ZHOU, ClassLoader.getSystemResourceAsStream(pack+"/zhoujiang/data/nametable-zhoujiang.xml"), ZHOU.getResources(), ZHOU.getHelpResources());

		BasePluginData.flushMissingKeys();
		logger.debug("STOP  Initialize");
//		logger.fatal("Stop here");
//		System.exit(0);
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

	//-------------------------------------------------------------------
	@Override
	public List<String> getLanguages() {
		return Arrays.asList(Locale.GERMAN.getLanguage());
	}

}
