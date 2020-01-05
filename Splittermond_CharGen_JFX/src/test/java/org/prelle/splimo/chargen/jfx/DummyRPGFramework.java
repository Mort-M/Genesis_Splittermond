/**
 * 
 */
package org.prelle.splimo.chargen.jfx;

import java.util.ArrayList;
import java.util.List;

import de.rpgframework.ConfigContainer;
import de.rpgframework.PluginRegistry;
import de.rpgframework.RPGFramework;
import de.rpgframework.RPGFrameworkInitCallback;
import de.rpgframework.boot.BootStep;
import de.rpgframework.boot.StandardBootSteps;
import de.rpgframework.core.License;
import de.rpgframework.core.LicenseManager;
import de.rpgframework.core.RoleplayingSystem;

/**
 * @author prelle
 *
 */
public class DummyRPGFramework implements RPGFramework {

	//-------------------------------------------------------------------
	/**
	 */
	public DummyRPGFramework() {
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RPGFramework#addBootStep(de.rpgframework.boot.StandardBootSteps)
	 */
	@Override
	public void addBootStep(StandardBootSteps roleplayingSystems) {
		// TODO Auto-generated method stub

	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RPGFramework#addBootStep(de.rpgframework.boot.BootStep)
	 */
	@Override
	public void addBootStep(BootStep step) {
		// TODO Auto-generated method stub

	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RPGFramework#initialize(de.rpgframework.RPGFrameworkInitCallback)
	 */
	@Override
	public void initialize(RPGFrameworkInitCallback listener) {
		// TODO Auto-generated method stub

	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RPGFramework#getConfiguration()
	 */
	@Override
	public ConfigContainer getConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RPGFramework#getPluginConfigurationNode()
	 */
	@Override
	public ConfigContainer getPluginConfigurationNode() {
		// TODO Auto-generated method stub
		return null;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RPGFramework#getLicenseManager()
	 */
	@Override
	public LicenseManager getLicenseManager() {
		// TODO Auto-generated method stub
		return new LicenseManager() {
			
			@Override
			public boolean hasLicense(RoleplayingSystem rules, String value) {
				return false;
			}
			
			@Override
			public List<License> getLicenses(RoleplayingSystem rules) {
				return new ArrayList<License>();
			}
		};
	}

	@Override
	public void addStepDefinition(StandardBootSteps arg0, BootStep arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PluginRegistry getPluginRegistry() {
		// TODO Auto-generated method stub
		return null;
	}

}
