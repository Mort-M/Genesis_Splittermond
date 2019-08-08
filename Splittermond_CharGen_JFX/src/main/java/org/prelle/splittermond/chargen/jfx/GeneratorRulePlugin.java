/**
 *
 */
package org.prelle.splittermond.chargen.jfx;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.ScreenManager;
import org.prelle.rpgframework.splittermond.SplittermondRules;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.chargen.SpliMoCharacterGenerator;
import org.prelle.splimo.levelling.CharacterLeveller;

import de.rpgframework.ConfigContainer;
import de.rpgframework.ConfigOption;
import de.rpgframework.RulePlugin;
import de.rpgframework.RulePluginFeatures;
import de.rpgframework.character.CharacterHandle;
import de.rpgframework.core.CommandBus;
import de.rpgframework.core.CommandBusListener;
import de.rpgframework.core.CommandResult;
import de.rpgframework.core.CommandType;
import de.rpgframework.core.RoleplayingSystem;

/**
 * @author prelle
 *
 */
public class GeneratorRulePlugin implements RulePlugin<SpliMoCharacter>, CommandBusListener {

	private static Logger logger = LogManager.getLogger("splittermond.jfx");

	private static List<RulePluginFeatures> FEATURES = new ArrayList<RulePluginFeatures>();
	private static ConfigOption<Double>     hgFactor;

	//-------------------------------------------------------------------
	static {
		FEATURES.add(RulePluginFeatures.CHARACTER_CREATION);
		FEATURES.add(RulePluginFeatures.DATA_INPUT);
	}

	//-------------------------------------------------------------------
	/**
	 */
	public GeneratorRulePlugin() {
	}

	//--------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RulePlugin#getID()
	 */
	@Override
	public String getID() {
		return "CHARGEN";
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
		return Arrays.asList("CORE");
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RulePlugin#getSupportedFeatures()
	 */
	@Override
	public Collection<RulePluginFeatures> getSupportedFeatures() {
		List<RulePluginFeatures> ret = new ArrayList<>(FEATURES);
//		if (developerMode!=null && !(Boolean)developerMode.getValue()) {
//			ret.remove(RulePluginFeatures.DATA_INPUT);
//		}
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RulePlugin#attachConfigurationTree(de.rpgframework.ConfigContainer)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void attachConfigurationTree(ConfigContainer addBelow) {
		logger.debug("attach");
		ConfigContainer splittermond = (ConfigContainer)addBelow.getChild("splittermond");
		hgFactor     = (ConfigOption<Double> ) splittermond.getChild(SplittermondRules.PROP_EXPERIENCE_FACTOR);
//		System.exit(0);
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
		case SHOW_CHARACTER_MODIFICATION_GUI:
			if (values[0]!=RoleplayingSystem.SPLITTERMOND) return false;
			if (!(values[1] instanceof SpliMoCharacter)) return false;
			if (!(values[2] instanceof CharacterHandle)) return false;
			if (!(values[4] instanceof ScreenManager)) return false;
			return true;
		case SHOW_CHARACTER_CREATION_GUI:
			if (values[0]!=RoleplayingSystem.SPLITTERMOND) return false;
			if (!(values[2] instanceof ScreenManager)) return false;
			return true;
		case SHOW_DATA_INPUT_GUI:
			if (values[0]!=RoleplayingSystem.SPLITTERMOND) return false;
			if (!(values[2] instanceof ScreenManager)) return false;
			return true;
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

		ScreenManager manager;
		CharacterController control;
		SpliMoCharacter model;
//		CharacterViewScreenSpliMo screen;
		CharacterViewScreenSpliMo2 screen;
		switch (type) {
		case SHOW_CHARACTER_MODIFICATION_GUI:
			logger.debug("start character modification");
			model = (SpliMoCharacter)values[1];
			control = new CharacterLeveller(model, hgFactor);
			CharacterHandle handle = (CharacterHandle)values[2];
			manager = (ScreenManager)values[4];
			screen = new CharacterViewScreenSpliMo2(control, ViewMode.MODIFICATION, handle);
//			screen.setData(model, handle);
			manager.navigateTo(screen);
//			SplittermondCharGenView altScreen = new SplittermondCharGenView(control);
//			altScreen.setData(model, handle);
//			manager.show(altScreen, CSS);

			return new CommandResult(type, true);
		case SHOW_CHARACTER_CREATION_GUI:
			logger.debug("start character creation");
			model = new SpliMoCharacter();
			control = new SpliMoCharacterGenerator(model, hgFactor);
			manager = (ScreenManager)values[2];

			screen = new CharacterViewScreenSpliMo2(control, ViewMode.GENERATION, null);
			manager.navigateTo(screen);
			screen.startGeneration();
			logger.info("-----------------return--------------------");

			CommandResult result = new CommandResult(type, true);
			result.setReturnValue(model);
			return result;
//		case SHOW_DATA_INPUT_GUI:
//			logger.debug("start data input");
//			manager = (ScreenManager)values[2];
//
//			DataInputScreen screen2 = new DataInputScreen();
//			manager.show(screen2, CSS);
//			result = new CommandResult(type, true);
////			result.setReturnValue(model);
//			return result;
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
		CommandBus.registerBusCommandListener(this);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RulePlugin#getAboutHTML()
	 */
	@Override
	public InputStream getAboutHTML() {
		return ClassLoader.getSystemResourceAsStream(SpliMoCharGenJFXConstants.PREFIX+"/i18n/splittermond-chargen.html");
	}

	//-------------------------------------------------------------------
	@Override
	public List<String> getLanguages() {
		return Arrays.asList(Locale.GERMAN.getLanguage());
	}

}
