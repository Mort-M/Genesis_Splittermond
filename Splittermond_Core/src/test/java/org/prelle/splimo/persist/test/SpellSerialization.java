/**
 * 
 */
package org.prelle.splimo.persist.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.StringReader;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.junit.Test;
import org.prelle.simplepersist.Persister;
import org.prelle.simplepersist.Serializer;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.Spell;
import org.prelle.splimo.SpellType;
import org.prelle.splimo.SplitterMondCore;

/**
 * @author prelle
 *
 */
@SuppressWarnings("unchecked")
public class SpellSerialization {

	final static String SEP = System.getProperty("line.separator");
	final static String HEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
	final static String DATA = HEAD 
			+SEP+"<spell id=\"alarm\" diff=\"18\" cost=\"4V1\" castdur=\"2T\" range=\"CASTER\" spelldur=\"6h\" enh=\"1/+1V1\"> "
			+SEP+"   <school skill=\"insightmagic\" level=\"1\"/>"
			+SEP+"   <school skill=\"protectionmagic\" level=\"1\"/>"
			+SEP+"   <type>PERCEPTION</type>"
			+"</spell>"+SEP;

	static Spell INSTANCE = new Spell();
	static Skill insight, protection;
	
	static private Serializer m;

	//-------------------------------------------------------------------
	static {
		insight    = new Skill("insightmagic", SkillType.MAGIC, Attribute.MIND, Attribute.MYSTIC);
		protection = new Skill("protectionmagic", SkillType.MAGIC, Attribute.MIND, Attribute.MYSTIC);
		SplitterMondCore.getSkills().add(insight);
		SplitterMondCore.getSkills().add(protection);
		
		//		SplitterMondCore.initialize(FileSystems.getDefault());
		m = new Persister();
		
//		Enumeration<Appender> e = Logger.getRootLogger().getAllAppenders();
//		while (e.hasMoreElements()) {
//			Appender ap = e.nextElement();
//			ap.setLayout(new PatternLayout("%5p [%c] (%F:%L) - %m%n"));
//		}
//		Logger.getRootLogger().setLevel(Level.WARN);
//		Logger.getLogger("xml").setLevel(Level.DEBUG);
	}

	//-------------------------------------------------------------------
	public SpellSerialization() throws Exception {


		
		INSTANCE.setDifficulty(18);
		INSTANCE.addSchool(insight, 1);
		INSTANCE.addSchool(protection, 1);
		INSTANCE.addAllTypes(SpellType.PERCEPTION);
	}

	//-------------------------------------------------------------------
	@Test
	public void serialize() {
	}

	//-------------------------------------------------------------------
	@Test
	public void deserialize() {
		try {
			System.out.println("-----deserialize----------------------------------");
			try {
				Spell result = m.read(Spell.class, new StringReader(DATA));
				result.setResourceBundle((PropertyResourceBundle) ResourceBundle.getBundle("i18n/splittermond-core"));
				System.out.println("Read "+result.dump());

				assertTrue(result.getTypes().contains(SpellType.PERCEPTION));
			} catch (Exception e) {
				e.printStackTrace();
				fail(e.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

}
