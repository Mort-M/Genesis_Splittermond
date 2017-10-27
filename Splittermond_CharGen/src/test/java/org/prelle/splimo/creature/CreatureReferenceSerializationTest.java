/**
 * 
 */
package org.prelle.splimo.creature;

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
import org.prelle.splimo.creature.CreatureModule;
import org.prelle.splimo.creature.CreatureModuleReference;
import org.prelle.splimo.creature.CreatureTypeValue;
import org.prelle.splimo.creature.CreatureWeapon;
import org.prelle.splimo.creature.ModuleBasedCreature;
import org.prelle.splimo.creature.CreatureModuleReference.NecessaryChoice;
import org.prelle.splimo.modifications.MastershipModification;

import de.rpgframework.RPGFrameworkLoader;

/**
 * @author prelle
 *
 */
public class CreatureReferenceSerializationTest {

	final static String SEP = System.getProperty("line.separator");
	final static String HEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
	final static String DATA = HEAD 
			+SEP+"<creatinst>"
			+SEP+"   <name>Mein Lämmchen</name>"
			+SEP+"   <modcreature>"
			+SEP+"      <base ref=\"agile\"/>"
			+SEP+"      <role ref=\"combatanimal\">"
			+SEP+"         <choices>"
			+SEP+"            <choice>"
			+SEP+"               <origin>"
			+SEP+"                  <mastermod level=\"1\" skill=\"melee\"/>"
			+SEP+"               </origin>"
			+SEP+"               <result>"
			+SEP+"                  <mastermod ref=\"melee/push\" skill=\"melee\"/>"
			+SEP+"               </result>"
			+SEP+"            </choice>"
			+SEP+"         </choices>"
			+SEP+"      </role>"
			+SEP+"      <creaturetypes>"
			+SEP+"         <creaturetype level=\"3\" type=\"MAGIC_CREATURE\"/>"
			+SEP+"      </creaturetypes>"
			+SEP+"   </modcreature>"
			+SEP+"</creatinst>"+SEP;
	
	final static ModuleBasedCreature MODULE = new ModuleBasedCreature();
	final static CreatureReference CHARAC = new CreatureReference(MODULE);
	static private Serializer m;

	//-------------------------------------------------------------------
	static {
		Logger.getRootLogger().setLevel(Level.WARN);

		RPGFrameworkLoader.getInstance();
		
		CHARAC.setName("Mein Lämmchen");
		CHARAC.setUniqueId(null);
		CreatureModuleReference agile =new CreatureModuleReference(SplitterMondCore.getCreatureModule("agile"));
		agile.setUniqueId(null);
		MODULE.setBase(agile);
		CreatureModuleReference combat =new CreatureModuleReference(SplitterMondCore.getCreatureModule("combatanimal"));
		combat.setUniqueId(null);
		
		NecessaryChoice agileChoice = new NecessaryChoice(
				new MastershipModification(SplitterMondCore.getSkill("melee"),1), 
				new MastershipModification(SplitterMondCore.getSkill("melee").getMastership("push")));
		combat.addChoice(agileChoice);
		MODULE.setRole(combat);
		
		MODULE.addCreatureType(new CreatureTypeValue(SplitterMondCore.getCreatureType("MAGIC_CREATURE"), 3));

		// To be ignored in XML
		CreatureWeapon weapon = new CreatureWeapon("body");
		weapon.setSkill(SplitterMondCore.getSkill("melee"));
		weapon.setSpeed(13);
		weapon.setDamage(30604);;
		MODULE.addWeapon(weapon);
		

//		CHARAC.getSkillValue(SplitterMondCore.getSkill("acrobatics")).setValue(11);
//		SkillValue insight = CHARAC.getSkillValue(SplitterMondCore.getSkill("naturemagic"));
//		insight.setValue(9);
//		insight.addMastership(new MastershipReference(insight.getSkill().getMastership("naturesong")));
////		MastershipReference mRef = new MastershipReference(insight.getSkill().getMastership("savingcaster"));
////		mRef.setFree(1);
////		insight.addMastership(mRef);
//		
//		SpellValue spell1 = new SpellValue(SplitterMondCore.getSpell("truesight"), insight.getSkill());
//		SpellValue spell2 = new SpellValue(SplitterMondCore.getSpell("magicmessage"), insight.getSkill());
//		spell2.setFreeLevel(1);
//		CHARAC.addSpell(spell1);
//		CHARAC.addSpell(spell2);
		
		m = new Persister();
		
//		Logger.getLogger("xml").setLevel(Level.DEBUG);
	}
	
	//-------------------------------------------------------------------
	public CreatureReferenceSerializationTest() throws Exception {
	}

	//-------------------------------------------------------------------
	@Test
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
			CreatureReference result = m.read(CreatureReference.class, new StringReader(DATA));
//			System.out.println("Read "+result.dump());
			
			assertEquals(CHARAC.getName(), result.getName());
			assertEquals("agile", result.getModuleBasedCreature().getBase().getId());
			assertEquals("combatanimal", result.getModuleBasedCreature().getRole().getModule().getId());
			assertEquals(1, result.getModuleBasedCreature().getRole().getChoices().size());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

}
