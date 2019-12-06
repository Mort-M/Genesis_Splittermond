package de.rpgframework.splittermond.print.bbcode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
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
 * This is the print plugin for the creation of bbcodes for a
 * {@link SpliMoCharacter}. This class registers itself to the
 * {@link CommandBus}.
 *
 * @author frank.buettner
 *
 */
public class BBCodePlugin implements RulePlugin<SpliMoCharacter>, CommandBusListener {

	private static Logger logger = LogManager.getLogger(BBCodePlugin.class);

	private static Preferences usr = Preferences.userRoot().node("/org/prelle/splittermond/print");
	ConfigOption<String> OPTION_PATH;

	//-------------------------------------------------------------------
	public BBCodePlugin() {
	}

	//-------------------------------------------------------------------
	public String getID() {
		return "BBCode";
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.core.CommandBusListener#getReadableName()
	 */
	@Override
	public String getReadableName() {
		return "Splittermond BBCode Printer (Frank Büttner)";
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
		ConfigContainer cfgBBCode = cfgSpliMo.createContainer("bbcode");
		cfgBBCode.changePreferences(usr);
		cfgBBCode.setResourceBundle( (PropertyResourceBundle)ResourceBundle.getBundle("de/rpgframework/splittermond/print/bbcode/i18n/splittermond/print_bbcode"));
		OPTION_PATH = cfgBBCode.createOption("path", ConfigOption.Type.DIRECTORY, System.getProperty("user.home"));
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
		if (logger.isTraceEnabled()) {
			logger.trace(src);
			logger.trace(type);
			logger.trace(values);
		}

		boolean result = false;
		switch (type) {
		case PRINT:
			result = values[4]==PrintType.BBCODE;
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

	@Override
	public CommandResult handleCommand(Object src, CommandType type,
			Object... values) {
		logger.trace(src);
		logger.trace(type);
		logger.trace(values);

		CommandResult commandResult = null;
		switch (type) {
		case PRINT_GET_OPTIONS:
			Object[] result = new Object[2];
			result[0] = Arrays.asList(PrintType.BBCODE);
			result[1] = getConfiguration();
			commandResult = new CommandResult(type, result);
			break;
		case PRINT:
			SpliMoCharacter model = (SpliMoCharacter) values[1];
			// 2. Scene
			// 3. ScreenManager
			PrintType format = (PrintType) values[4];
			// Only BBCode printing supported
			logger.info("print called  "+format);
			if (format == PrintType.BBCODE) {
				try {
					SpliMoBBCodeGenerator spliMoBBCodeGenerator = new SpliMoBBCodeGenerator(
							model);
					logger.info("Start");
					String bbCode = spliMoBBCodeGenerator.generateBBCode();
					logger.info("BBCode = "+bbCode);
					// create a temp file
					Path   printToFile = new File(new File(OPTION_PATH.getStringValue()), model.getName()+".bbcode").toPath();
//					File savedBBCode = File.createTempFile(UUID.randomUUID()
//							.toString(), ".txt");
//					savedBBCode.deleteOnExit();

					// write it
					BufferedWriter bw = new BufferedWriter(new FileWriter(
							printToFile.toFile().getAbsolutePath()));
					bw.write(bbCode);
					bw.close();

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
		logger.trace("getRules called");
		return RoleplayingSystem.SPLITTERMOND;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RulePlugin#getSupportedFeatures()
	 */
	@Override
	public Collection<RulePluginFeatures> getSupportedFeatures() {
		logger.trace("getSupportedFeatures called");
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
		return ClassLoader.getSystemResourceAsStream("de/rpgframework/splittermond/print/bbcode/i18n/splittermond/print_bbcode.html");
	}

	//-------------------------------------------------------------------
	@Override
	public List<String> getLanguages() {
		return Arrays.asList(Locale.GERMAN.getLanguage());
	}

}
