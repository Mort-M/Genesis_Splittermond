package org.prelle.splittermond.genesis;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.prelle.rpgframework.splittermond.SplittermondRules;
import org.prelle.rpgframework.splittermond.data.SplittermondDataPlugin;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splittermond.chargen.jfx.GeneratorRulePlugin;

import de.rpgframework.ConfigContainer;
import de.rpgframework.ConfigOption;
import de.rpgframework.character.RulePlugin;
import de.rpgframework.character.RulePluginFeatures;
import de.rpgframework.core.CommandResult;
import de.rpgframework.core.CommandType;
import de.rpgframework.core.RoleplayingSystem;

/**
 * @author Stefan Prelle
 *
 */
public class SplittermondBasePlugin implements RulePlugin<SpliMoCharacter> {

	private SplittermondRules core;
	private GeneratorRulePlugin charGen;
	private SplittermondDataPlugin data;
	
	//-------------------------------------------------------------------
	public SplittermondBasePlugin() {
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.RulePlugin#getID()
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
		return "Splittermond Core Rules";
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.RulePlugin#getRequiredPlugins()
	 */
	@Override
	public Collection<String> getRequiredPlugins() {
		return new ArrayList<>();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.RulePlugin#getRules()
	 */
	@Override
	public RoleplayingSystem getRules() {
		return RoleplayingSystem.SPLITTERMOND;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.RulePlugin#getSupportedFeatures()
	 */
	@Override
	public Collection<RulePluginFeatures> getSupportedFeatures() {
		return Arrays.asList(new RulePluginFeatures[] {RulePluginFeatures.PERSISTENCE, RulePluginFeatures.CHARACTER_CREATION,  RulePluginFeatures.DATA});
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.RulePlugin#getAboutHTML()
	 */
	@Override
	public InputStream getAboutHTML() {
		// TODO Auto-generated method stub
		return null;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.RulePlugin#getConfiguration()
	 */
	@Override
	public List<ConfigOption<?>> getConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.RulePlugin#getLanguages()
	 */
	@Override
	public List<String> getLanguages() {
		return Arrays.asList(Locale.GERMAN.getLanguage());
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.RulePlugin#init()
	 */
	@Override
	public void init(RulePluginProgessListener callback) {
		core = new SplittermondRules();
		charGen = new GeneratorRulePlugin();
		data = new SplittermondDataPlugin();
		
		core.init(callback);
		charGen.init(callback);
		data.init(callback);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.RulePlugin#attachConfigurationTree(de.rpgframework.ConfigContainer)
	 */
	@Override
	public void attachConfigurationTree(ConfigContainer addBelow) {
		core.attachConfigurationTree(addBelow);
		charGen.attachConfigurationTree(addBelow);
		data.attachConfigurationTree(addBelow);
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
		switch (type) {
		case ENCODE:
		case DECODE:
			return core.handleCommand(src, type, values);
		case SHOW_CHARACTER_MODIFICATION_GUI:
		case SHOW_CHARACTER_CREATION_GUI:
		case SHOW_DATA_INPUT_GUI:
			return charGen.handleCommand(src, type, values);
		default:
			return new CommandResult(type, false, "Not supported");
		}
	}

}
