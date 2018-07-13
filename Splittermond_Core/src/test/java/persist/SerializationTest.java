/**
 * 
 */
package persist;

import org.apache.log4j.PropertyConfigurator;
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
//		System.err.println("Running from "+(new File(".")).getAbsolutePath());
//		InputStream in = ClassLoader.getSystemResourceAsStream("log4j.properties");
//		BufferedReader rin = new BufferedReader(new InputStreamReader(in));
//		rin.lines().forEach(line -> System.err.println(line));
		PropertyConfigurator.configure(ClassLoader.getSystemResourceAsStream("log4j.properties"));
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
