/**
 *
 */
package org.prelle.rpgframework.splittermond;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.prelle.rpgframework.api.BabylonEventBus;
import org.prelle.rpgframework.api.BabylonEventType;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.SplittermondCustomDataCore;

import de.rpgframework.ConfigContainer;
import de.rpgframework.ConfigNode;
import de.rpgframework.ConfigOption;
import de.rpgframework.RPGFrameworkLoader;
import de.rpgframework.RulePlugin;
import de.rpgframework.RulePluginFeatures;
import de.rpgframework.character.DecodeEncodeException;
import de.rpgframework.core.CommandBus;
import de.rpgframework.core.CommandBusListener;
import de.rpgframework.core.CommandResult;
import de.rpgframework.core.CommandType;
import de.rpgframework.core.RoleplayingSystem;

/**
 * @author prelle
 *
 */
public class SplittermondRules implements RulePlugin<SpliMoCharacter>, CommandBusListener {

	private final static Logger logger = Logger.getLogger("splittermond");

	public final static String PROP_DEVELOPER_MODE = "developer_mode";

	private static List<RulePluginFeatures> FEATURES = new ArrayList<RulePluginFeatures>();

	private SplittermondCharacterPlugin charac;

	//-------------------------------------------------------------------
	static {
		FEATURES.add(RulePluginFeatures.PERSISTENCE);
	}

	private static ConfigContainer configRoot;

	//-------------------------------------------------------------------
	/**
	 */
	public SplittermondRules() {
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
		return "Splittermond Core Rules";
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
	@Override
	public Collection<RulePluginFeatures> getSupportedFeatures() {
		return FEATURES;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.core.CommandBusListener#willProcessCommand(java.lang.Object, de.rpgframework.core.CommandType, java.lang.Object[])
	 */
	@Override
	public boolean willProcessCommand(Object src, CommandType type, Object... values) {
		switch (type) {
		case ENCODE:
			if (values[0]!=RoleplayingSystem.SPLITTERMOND) return false;
			if (values.length<2) return false;
			return (values[1] instanceof SpliMoCharacter);
		case DECODE:
			if (values[0]!=RoleplayingSystem.SPLITTERMOND) return false;
			if (values.length<2) return false;
			return (values[1] instanceof byte[]);
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
		logger.debug("handleCommand("+type+", "+Arrays.toString(values)+")");
		switch (type) {
		case ENCODE:
			SpliMoCharacter model = (SpliMoCharacter)values[1];
			byte[] raw;
			try {
				raw = charac.marshal(model);
				return new CommandResult(type, raw);
			} catch (DecodeEncodeException e) {
				return new CommandResult(type, false, e.toString());
			}
		case DECODE:
			raw = (byte[])values[1];
			try {
				model = charac.unmarshal(raw);
				logger.debug("Unmarshal done");
				return new CommandResult(type, model);
			} catch (DecodeEncodeException e) {
				return new CommandResult(type, false, e.toString());
			}
		default:
			return new CommandResult(type, false, "Not supported");
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RulePlugin#attachConfigurationTree(de.rpgframework.ConfigContainer)
	 */
	@Override
	public void attachConfigurationTree(ConfigContainer addBelow) {
		logger.debug("Add configuration to "+addBelow);
		configRoot = addBelow.createContainer("splittermond");
		configRoot.setResourceBundle(SplitterMondCore.getI18nResources());
		configRoot.createOption(PROP_DEVELOPER_MODE, ConfigOption.Type.BOOLEAN, false);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RulePlugin#getConfiguration()
	 */
	@Override
	public List<ConfigOption<?>> getConfiguration() {
		List<ConfigOption<?>> ret = new ArrayList<>();
		for (ConfigNode node : configRoot) {
			if (node instanceof ConfigOption)
				ret.add( (ConfigOption<?>)node );
		}
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RulePlugin#init()
	 */
	@Override
	public void init() {
		logger.debug("init");

		SplitterMondCore.initialize(this);

		try {
//			SplitterMondCore.loadCustomItems();
			SplittermondCustomDataCore.getItems();
		} catch (Exception e) {
			logger.error("Failed loading custom items",e);
			BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 2, "Error loading database of your custom items");
		}

		try {
			SplitterMondCore.loadPromoData();
		} catch (Exception e) {
			logger.error("Failed loading promotional data",e);
			BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 2, "Error loading database of your promo data");
		}
		charac = new SplittermondCharacterPlugin();

		CommandBus.registerBusCommandListener(this);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RulePlugin#getAboutHTML()
	 */
	@Override
	public InputStream getAboutHTML() {
		return ClassLoader.getSystemResourceAsStream("i18n/splittermond-core.html");
	}

	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public static boolean isDeveloperMode() {
		try {
			RulePlugin<SpliMoCharacter> corePlugin = null;
			for (RulePlugin<?> plugin : RPGFrameworkLoader.getInstance().getCharacterAndRules().getRulePlugins(RoleplayingSystem.SPLITTERMOND)) {
				if (plugin.getID().equals("CORE")) {
					corePlugin = (RulePlugin<SpliMoCharacter>) plugin;
					break;
				}
			}
			ConfigOption<Boolean> devMode = null;
			for (ConfigOption<?> opt : corePlugin.getConfiguration()) {
				if (opt.getLocalId().equals(SplittermondRules.PROP_DEVELOPER_MODE))
					devMode = (ConfigOption<Boolean>) opt;
			}
			if (devMode!=null) {
				return (Boolean)devMode.getValue();
			}
		} catch (Exception e) {
			logger.error("Failed getting developer mode value",e);
		}
		return false;
	}

}
