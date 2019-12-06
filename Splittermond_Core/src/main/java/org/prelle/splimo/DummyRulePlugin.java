/**
 * 
 */
package org.prelle.splimo;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.rpgframework.ConfigContainer;
import de.rpgframework.ConfigOption;
import de.rpgframework.character.RulePlugin;
import de.rpgframework.character.RulePluginFeatures;
import de.rpgframework.character.RuleSpecificCharacterObject;
import de.rpgframework.core.CommandResult;
import de.rpgframework.core.CommandType;
import de.rpgframework.core.RoleplayingSystem;

/**
 * @author Stefan
 *
 */
public class DummyRulePlugin<C extends RuleSpecificCharacterObject> implements RulePlugin<C> {

	private RoleplayingSystem rules;
	private String id;
	
	//--------------------------------------------------------------------
	public DummyRulePlugin() {
		this.rules = RoleplayingSystem.SPLITTERMOND;
		this.id    = "CORE";
	}
	
	//--------------------------------------------------------------------
	/**
	 */
	public DummyRulePlugin(RoleplayingSystem rules, String id) {
		this.rules = rules;
		this.id    = id;
	}

	@Override
	public boolean willProcessCommand(Object src, CommandType type,
			Object... values) {
		return false;
	}

	@Override
	public CommandResult handleCommand(Object src, CommandType type,
			Object... values) {
		return new CommandResult(type, false, null, false);
	}

	@Override
	public String getID() {
		return id;
	}
	public String getReadableName() {return id;}

	@Override
	public RoleplayingSystem getRules() {
		return rules;
	}

	@Override
	public Collection<RulePluginFeatures> getSupportedFeatures() {
		return new ArrayList<RulePluginFeatures>();
	}

	@Override
	public List<ConfigOption<?>> getConfiguration() {
		return new ArrayList<>();
	}

	@Override
	public void init(RulePluginProgessListener callback) {
		// TODO Auto-generated method stub
		
	}

	//-------------------------------------------------------------------
	public InputStream getAboutHTML() {
		return null;
	}
	
	public Collection<String> getRequiredPlugins() {return new ArrayList<>();}

	@Override
	public void attachConfigurationTree(ConfigContainer addBelow) {
		// TODO Auto-generated method stub
		
	}

//	@Override
	public List<String> getLanguages() {
		// TODO Auto-generated method stub
		return null;
	}

}
