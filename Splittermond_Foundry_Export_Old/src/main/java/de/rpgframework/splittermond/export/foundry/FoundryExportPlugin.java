package de.rpgframework.splittermond.export.foundry;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.SpliMoCharacter;

import de.rpgframework.ConfigContainer;
import de.rpgframework.ConfigOption;
import de.rpgframework.character.RulePlugin;
import de.rpgframework.character.RulePluginFeatures;
import de.rpgframework.core.CommandBus;
import de.rpgframework.core.CommandBusListener;
import de.rpgframework.core.CommandResult;
import de.rpgframework.core.CommandType;
import de.rpgframework.core.RoleplayingSystem;
import de.rpgframework.print.PrintType;

/**
 * This is the print plugin for the creation of json output for a
 * {@link SpliMoCharacter}. This class registers itself to the
 * {@link CommandBus}.
 */
public class FoundryExportPlugin implements RulePlugin<SpliMoCharacter>, CommandBusListener {

	private static Logger logger = LogManager.getLogger(FoundryExportPlugin.class);

	private static Preferences usr = Preferences.userRoot().node("/org/prelle/splittermond/foundry");
	private ConfigOption<String> OPTION_PATH;

	//-------------------------------------------------------------------
	public FoundryExportPlugin() {
	}

	//-------------------------------------------------------------------
	public String getID() {
		return "FOUNDRY";
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.core.CommandBusListener#getReadableName()
	 */
	@Override
	public String getReadableName() {
		return "Splittermond Foundry Export";
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
	 * @see de.rpgframework.RulePlugin#attachConfigurationTree(de.rpgframework.ConfigContainer)
	 */
	@Override
	public void attachConfigurationTree(ConfigContainer addBelow) {
		ConfigContainer cfgSpliMo = (ConfigContainer)addBelow.getChild("splittermond");
		if (cfgSpliMo==null) {
			logger.error("Expected splittermond node below "+addBelow.getPathID());
			return;
		}
		ConfigContainer cfgJSON = cfgSpliMo.createContainer("foundry");
		cfgJSON.changePreferences(usr);
		cfgJSON.setResourceBundle( (PropertyResourceBundle)ResourceBundle.getBundle(FoundryExportPlugin.class.getName()));
		OPTION_PATH = cfgJSON.createOption("path", ConfigOption.Type.DIRECTORY, System.getProperty("user.home"));
	}

	//-------------------------------------------------------------------
	@Override
	public List<ConfigOption<?>> getConfiguration() {
		return new ArrayList<ConfigOption<?>>(Arrays.asList(OPTION_PATH));
	}

	//-------------------------------------------------------------------
	@Override
	public boolean willProcessCommand(Object src, CommandType type,
			Object... values) {
		boolean result = false;
		switch (type) {
		case PRINT:
			result = values[4]==PrintType.JSON;
			break;
		case PRINT_GET_OPTIONS:
			if (values[0] == RoleplayingSystem.SPLITTERMOND)
				result = true;
			break;
		default:
		}

		logger.trace(result);
		return result;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.core.CommandBusListener#handleCommand(java.lang.Object, de.rpgframework.core.CommandType, java.lang.Object[])
	 */
	@Override
	public CommandResult handleCommand(Object src, CommandType type,
			Object... values) {
		CommandResult commandResult = null;
		switch (type) {
		case PRINT_GET_OPTIONS:
			Object[] result = new Object[2];
			result[0] = Arrays.asList(PrintType.JSON);
			result[1] = getConfiguration();
			commandResult = new CommandResult(type, result);
			break;
		case PRINT:
			SpliMoCharacter model = (SpliMoCharacter) values[1];
			// 2. Scene
			// 3. ScreenManager
			PrintType format = (PrintType) values[4];
			logger.info("print called  "+format);
			if (format == PrintType.JSON) {
				try {
					/*
					 * Create your JSON
					 */
					logger.info("Export as resolved JSON format: "+model.getName());

					String json = new FoundryExportService().exportCharacter(model);
					logger.debug(json);
					// Write
					Path   printToFile = new File(new File(OPTION_PATH.getStringValue()), "Actor_"+model.getName()+".json").toPath();
					Files.writeString(printToFile, json);
					System.out.println("printToFile = " + json);

					commandResult = new CommandResult(type, printToFile);
				} catch (Exception e) {
					logger.error("Failed",e);
					commandResult = new CommandResult(type, false, e.toString());
				}
			}
			break;
			// Continue with default
		default:
			commandResult = new CommandResult(type, false, "Not supported");
		}

		logger.trace(commandResult);
		return commandResult;
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
	 * @see de.rpgframework.RulePlugin#getSupportedFeatures()
	 */
	@Override
	public Collection<RulePluginFeatures> getSupportedFeatures() {
		return Arrays.asList(RulePluginFeatures.PRINT);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RulePlugin#init()
	 */
	@Override
	public void init(RulePluginProgessListener callback) {
		CommandBus.registerBusCommandListener(this);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RulePlugin#getAboutHTML()
	 */
	@Override
	public InputStream getAboutHTML() {
		return ClassLoader.getSystemResourceAsStream("de/rpgframework/splittermond/print/json/i18n/splittermond/print_foundry.html");
	}

	//-------------------------------------------------------------------
	@Override
	public List<String> getLanguages() {
		return Arrays.asList(Locale.GERMAN.getLanguage());
	}

}
