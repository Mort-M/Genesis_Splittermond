package org.prelle.splittermond.chargen.jfx;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.prelle.rpgframework.splittermond.SplittermondRules;
import org.prelle.rpgframework.splittermond.data.SplittermondDataPlugin;
import org.prelle.splimo.SpliMoCharacter;

import de.rpgframework.ConfigContainer;
import de.rpgframework.ConfigOption;
import de.rpgframework.character.RulePlugin;
import de.rpgframework.character.RulePluginFeatures;
import de.rpgframework.core.CommandBusListener;
import de.rpgframework.core.CommandResult;
import de.rpgframework.core.CommandType;
import de.rpgframework.core.RoleplayingSystem;

/**
 * @author prelle
 *
 */
public class SplittermondBasePlugin implements RulePlugin<SpliMoCharacter>, CommandBusListener {

	private static List<RulePluginFeatures> FEATURES = new ArrayList<RulePluginFeatures>();

	private SplittermondRules core;
	private GeneratorRulePlugin charGen;
	private SplittermondDataPlugin data;

	//-------------------------------------------------------------------
	static {
		FEATURES.add(RulePluginFeatures.PERSISTENCE);
		FEATURES.add(RulePluginFeatures.CHARACTER_CREATION);
		FEATURES.add(RulePluginFeatures.DATA_INPUT);
	}

	//-------------------------------------------------------------------
	/**
	 */
	public SplittermondBasePlugin() {
	}

	//--------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RulePlugin#getID()
	 */
	@Override
	public String getID() {
		return "CORE";
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.core.CommandBusListener#getReadableName()
	 */
	@Override
	public String getReadableName() {
		if (this.getClass().getPackage().getImplementationTitle()!=null)
			return this.getClass().getPackage().getImplementationTitle();
		return "Splittermond Character Generator";
	}

	//-------------------------------------------------------------------
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
		return new ArrayList<>();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RulePlugin#getSupportedFeatures()
	 */
	@Override
	public Collection<RulePluginFeatures> getSupportedFeatures() {
		return new ArrayList<>(FEATURES);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RulePlugin#attachConfigurationTree(de.rpgframework.ConfigContainer)
	 */
	@Override
	public void attachConfigurationTree(ConfigContainer addBelow) {
		core.attachConfigurationTree(addBelow);
		data.attachConfigurationTree(addBelow);
		charGen.attachConfigurationTree(addBelow);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RulePlugin#getConfiguration()
	 */
	@Override
	public List<ConfigOption<?>> getConfiguration() {
		return new ArrayList<>();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.core.CommandBusListener#willProcessCommand(java.lang.Object, de.rpgframework.core.CommandType, java.lang.Object[])
	 */
	@Override
	public boolean willProcessCommand(Object src, CommandType type, Object... values) {
		switch (type) {
		case ENCODE:
		case DECODE:
			return core.willProcessCommand(src, type, values);
		case SHOW_CHARACTER_MODIFICATION_GUI:
		case SHOW_CHARACTER_CREATION_GUI:
		case SHOW_DATA_INPUT_GUI:
			return charGen.willProcessCommand(src, type, values);
		default:
			return false;
		}

	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.core.CommandBusListener#handleCommand(java.lang.Object, de.rpgframework.core.CommandType, java.lang.Object[])
	 */
	@Override
	public CommandResult handleCommand(Object src, CommandType type, Object... values) {
		if (!willProcessCommand(src, type, values))
			return new CommandResult(type, false, null, false);

		switch (type) {
		case ENCODE:
		case DECODE:
			return core.handleCommand(src, type, values);
		case SHOW_CHARACTER_MODIFICATION_GUI:
		case SHOW_CHARACTER_CREATION_GUI:
		case SHOW_DATA_INPUT_GUI:
			return charGen.handleCommand(src, type, values);
		default:
			return new CommandResult(type, false);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RulePlugin#init()
	 */
	@Override
	public void init() {
		core = new SplittermondRules();
		data = new SplittermondDataPlugin();
		charGen = new GeneratorRulePlugin();
		
		core.init();
		data.init();
		charGen.init();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RulePlugin#getAboutHTML()
	 */
	@Override
	public InputStream getAboutHTML() {
		return charGen.getAboutHTML();
	}

	//-------------------------------------------------------------------
	@Override
	public List<String> getLanguages() {
		return Arrays.asList(Locale.GERMAN.getLanguage());
	}

}
