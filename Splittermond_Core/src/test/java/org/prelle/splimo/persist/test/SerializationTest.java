/**
 * 
 */
package org.prelle.splimo.persist.test;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.prelle.splimo.DummyRulePlugin;
import org.prelle.splimo.SplitterMondCore;

import de.rpgframework.RPGFrameworkLoader;

/**
 * @author prelle
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	AttributeSerialization.class, 
	BackgroundSerialization.class, 
	CharacterSerialization.class, 
	CultureSerialization.class,
	ItemSerialization.class,
	MonsterSerialization.class,
	SpellSerialization.class
	})
public class SerializationTest {
	
	static {
	}

	//-------------------------------------------------------------------
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		SplitterMondCore.initialize(new DummyRulePlugin<>());
		RPGFrameworkLoader.getInstance();
	}

}
