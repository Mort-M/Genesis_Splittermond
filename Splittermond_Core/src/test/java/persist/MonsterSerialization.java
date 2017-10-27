/**
 * 
 */
package persist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.prelle.simplepersist.Persister;
import org.prelle.simplepersist.SerializationException;
import org.prelle.simplepersist.Serializer;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.MastershipReference;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.SpellValue;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.creature.Creature;
import org.prelle.splimo.creature.CreatureTypeValue;
import org.prelle.splimo.creature.CreatureWeapon;

import de.rpgframework.RPGFrameworkLoader;

/**
 * @author prelle
 *
 */
public class MonsterSerialization {

	final static String SEP = System.getProperty("line.separator");
	final static String HEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
	final static String DATA = HEAD 
			+SEP+"<creature id=\"baumwandler\" lvl1=\"4\" lvl2=\"2\">"
			+SEP+"   <attributes>"
			+SEP+"      <attr id=\"CHARISMA\" value=\"4\"/>"
			+SEP+"      <attr id=\"AGILITY\" value=\"2\"/>"
			+SEP+"      <attr id=\"INTUITION\" value=\"3\"/>"
			+SEP+"      <attr id=\"CONSTITUTION\" value=\"8\"/>"
			+SEP+"      <attr id=\"MYSTIC\" value=\"4\"/>"
			+SEP+"      <attr id=\"STRENGTH\" value=\"9\"/>"
			+SEP+"      <attr id=\"MIND\" value=\"3\"/>"
			+SEP+"      <attr id=\"WILLPOWER\" value=\"4\"/>"
			+SEP+"      <attr id=\"SIZE\" value=\"7\"/>"
			+SEP+"      <attr id=\"SPEED\" value=\"8\"/>"
			+SEP+"      <attr id=\"INITIATIVE\" value=\"0\"/>" 
			+SEP+"      <attr id=\"LIFE\" value=\"15\"/>"
			+SEP+"      <attr id=\"FOCUS\" value=\"16\"/>"
			+SEP+"      <attr id=\"DEFENSE\" value=\"29\"/>"
			+SEP+"      <attr id=\"DAMAGE_REDUCTION\" value=\"4\"/>"
			+SEP+"      <attr id=\"MINDRESIST\" value=\"27\"/>"
			+SEP+"      <attr id=\"BODYRESIST\" value=\"33\"/>"
			+SEP+"   </attributes>"
			+SEP+"   <creaturetypes>"
			+SEP+"      <creaturetype level=\"3\" type=\"MAGIC_CREATURE\"/>"
			+SEP+"   </creaturetypes>"
			+SEP+"   <weapons>"
			+SEP+"      <cweapon dmg=\"3W6+4\" id=\"body\" ini=\"7\" spd=\"13\" val=\"22\"  />"
			+SEP+"   </weapons>"
			+SEP+"   <skillvals>"
			+SEP+"      <skillval skill=\"acrobatics\" val=\"11\"/>"
			+SEP+"      <skillval skill=\"melee\" val=\"11\">"
			+SEP+"         <masterref ref=\"melee/clutch\"/>"
			+SEP+"      </skillval>"
			+SEP+"      <skillval skill=\"naturemagic\" val=\"13\">"
			+SEP+"         <masterref ref=\"naturemagic/naturesong\"/>"
			+SEP+"      </skillval>"
			+SEP+"   </skillvals>"
			+SEP+"   <spellvals>"
			+SEP+"      <spellval free=\"-1\" school=\"naturemagic\" spell=\"truesight\"/>"
			+SEP+"      <spellval free=\"1\" school=\"naturemagic\" spell=\"magicmessage\"/>"
			+SEP+"   </spellvals>"
			+SEP+"</creature>"+SEP;
	
	final static Creature CHARAC = new Creature("baumwandler");
	static private Serializer m;

	//-------------------------------------------------------------------
	static {
		Logger.getRootLogger().setLevel(Level.WARN);
//		PropertyConfigurator.configure(ClassLoader.getSystemResourceAsStream("log4j.properties"));
//		Enumeration<Appender> e = Logger.getRootLogger().getAllAppenders();
//		while (e.hasMoreElements()) {
//			Appender ap = e.nextElement();
//			ap.setLayout(new PatternLayout("%5p [%c] (%F:%L) - %m%n"));
//		}
//		Logger.getRootLogger().setLevel(Level.DEBUG);
//		Logger.getLogger("xml").setLevel(Level.INFO);

		RPGFrameworkLoader.getInstance();
		CHARAC.getAttribute(Attribute.CHARISMA    ).setDistributed(4);
		CHARAC.getAttribute(Attribute.AGILITY     ).setDistributed(2);
		CHARAC.getAttribute(Attribute.INTUITION   ).setDistributed(3);
		CHARAC.getAttribute(Attribute.CONSTITUTION).setDistributed(8);
		CHARAC.getAttribute(Attribute.MYSTIC      ).setDistributed(4);
		CHARAC.getAttribute(Attribute.STRENGTH    ).setDistributed(9);
		CHARAC.getAttribute(Attribute.MIND		  ).setDistributed(3);
		CHARAC.getAttribute(Attribute.WILLPOWER   ).setDistributed(4);
		
		CHARAC.getAttribute(Attribute.SIZE        ).setDistributed(7);
		CHARAC.getAttribute(Attribute.SPEED       ).setDistributed(8);
		CHARAC.getAttribute(Attribute.LIFE        ).setDistributed(15);
		CHARAC.getAttribute(Attribute.FOCUS       ).setDistributed(16);
		CHARAC.getAttribute(Attribute.DEFENSE     ).setDistributed(29);
		CHARAC.getAttribute(Attribute.DAMAGE_REDUCTION).setDistributed(4);
		CHARAC.getAttribute(Attribute.BODYRESIST  ).setDistributed(33);
		CHARAC.getAttribute(Attribute.MINDRESIST  ).setDistributed(27);
		
		CHARAC.setLevelAlone(4);
		CHARAC.setLevelGroup(2);
		
		CHARAC.addCreatureType(new CreatureTypeValue(SplitterMondCore.getCreatureType("MAGIC_CREATURE"), 3));
		
		CreatureWeapon weapon = new CreatureWeapon("body");
		weapon.setSkill(SplitterMondCore.getSkill("melee"));
		weapon.setSpeed(13);
		weapon.setDamage(30604);;
		CHARAC.addWeapon(weapon);
		

		CHARAC.getSkillValue(SplitterMondCore.getSkill("acrobatics")).setValue(11);
		SkillValue insight = CHARAC.getSkillValue(SplitterMondCore.getSkill("naturemagic"));
		insight.setValue(9);
		insight.addMastership(new MastershipReference(insight.getSkill().getMastership("naturesong")));
//		MastershipReference mRef = new MastershipReference(insight.getSkill().getMastership("savingcaster"));
//		mRef.setFree(1);
//		insight.addMastership(mRef);
		
		SpellValue spell1 = new SpellValue(SplitterMondCore.getSpell("truesight"), insight.getSkill());
		SpellValue spell2 = new SpellValue(SplitterMondCore.getSpell("magicmessage"), insight.getSkill());
		spell2.setFreeLevel(1);
		CHARAC.addSpell(spell1);
		CHARAC.addSpell(spell2);
		
		m = new Persister();
		
//		Logger.getLogger("xml").setLevel(Level.DEBUG);
	}
	
	//-------------------------------------------------------------------
	public MonsterSerialization() throws Exception {
	}

	//-------------------------------------------------------------------
//	@Test
	public void serialize() throws SerializationException, IOException {
		System.out.println("-----serialize----------------------------------");
//		Logger.getLogger("xml").setLevel(Level.DEBUG);
			StringWriter out = new StringWriter();
			m.write(CHARAC, out);
			
			System.out.println("OUT: "+out);
			assertEquals(DATA, out.toString());
	}

	//-------------------------------------------------------------------
	@Test
	public void deserialize() {
		System.out.println("-----deserialize----------------------------------");
		try {
			Creature result = m.read(Creature.class, new StringReader(DATA));
//			System.out.println("Read "+result.dump());
			
//			assertEquals(CHARAC.getName(), result.getName());
			assertEquals(11, result.getSkillPoints(SplitterMondCore.getSkill("acrobatics")));
			assertEquals(4, result.getAttribute(Attribute.CHARISMA).getDistributed());
			assertEquals(3, result.getAttribute(Attribute.MIND).getBought());
//			assertNotNull(result.getImage());
//			byte[] img = result.getImage();
//			assertEquals(4, img.length);
//			assertEquals(1, img[0]);
//			assertEquals(4, img[3]);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

}
