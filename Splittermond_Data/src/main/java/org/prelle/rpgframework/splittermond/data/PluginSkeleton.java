/**
 * 
 */
package org.prelle.rpgframework.splittermond.data;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.splimo.SpliMoCharacter;

import de.rpgframework.ConfigContainer;
import de.rpgframework.ConfigOption;
import de.rpgframework.character.RulePlugin;
import de.rpgframework.character.RulePluginFeatures;
import de.rpgframework.core.CommandResult;
import de.rpgframework.core.CommandType;
import de.rpgframework.core.RoleplayingSystem;

/**
 * @author prelle
 *
 */
public class PluginSkeleton implements RulePlugin<SpliMoCharacter> {
	
	private String id;
	private String name;
	private PropertyResourceBundle i18NResources;
	private PropertyResourceBundle i18NHelpResources;

	//-------------------------------------------------------------------
	public PluginSkeleton(String id, String name) {
		this.id = id;
		this.name = name;
		try {
			i18NResources = (PropertyResourceBundle) ResourceBundle.getBundle("org.prelle.rpgframework.splittermond.data."+id.toLowerCase()+".i18n."+id.toLowerCase());
			i18NHelpResources = (PropertyResourceBundle) ResourceBundle.getBundle("org.prelle.rpgframework.splittermond.data."+id.toLowerCase()+".i18n."+id.toLowerCase()+"-help");
		} catch (Exception e) {
			if (!"custom".equals(id.toLowerCase()))
				e.printStackTrace();
		}
	}

	//-------------------------------------------------------------------
	public PropertyResourceBundle getResources() { return i18NResources; }
	public PropertyResourceBundle getHelpResources() { return i18NHelpResources; }

	@Override
	public String getReadableName() {
		return name;
	}

	@Override
	public boolean willProcessCommand(Object src, CommandType type, Object... values) {
		return false;
	}

	@Override
	public CommandResult handleCommand(Object src, CommandType type, Object... values) {
		return null;
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public RoleplayingSystem getRules() {
		return RoleplayingSystem.SPLITTERMOND;
	}

	@Override
	public Collection<String> getRequiredPlugins() {
		return new ArrayList<>();
	}

	@Override
	public Collection<RulePluginFeatures> getSupportedFeatures() {
		return Arrays.asList(new RulePluginFeatures[]{RulePluginFeatures.DATA});
	}

	@Override
	public void attachConfigurationTree(ConfigContainer addBelow) {
	}

	@Override
	public List<ConfigOption<?>> getConfiguration() {
		return null;
	}

	@Override
	public void init(RulePluginProgessListener callback) {
		// TODO Auto-generated method stub
	}

	@Override
	public InputStream getAboutHTML() {
		return ClassLoader.getSystemResourceAsStream("org.prelle.rpgframework.splittermond.data.i18n."+id.toLowerCase()+".html");
	}

	@Override
	public List<String> getLanguages() {
		return Arrays.asList(Locale.GERMAN.getLanguage());
	}

}
