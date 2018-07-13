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
import org.junit.BeforeClass;
import org.junit.Test;
import org.prelle.rpgframework.splittermond.SplittermondRules;
import org.prelle.rpgframework.splittermond.beastmaster.BeastMasterPlugin;
import org.prelle.rpgframework.splittermond.buu.BestienUndUngeheuerPlugin;
import org.prelle.rpgframework.splittermond.msk.MondstahlklingenPlugin;
import org.prelle.simplepersist.Persister;
import org.prelle.simplepersist.SerializationException;
import org.prelle.simplepersist.Serializer;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.modifications.MastershipModification;

import de.rpgframework.RPGFrameworkLoader;

/**
 * @author prelle
 *
 */
public class ModuleBasedCreatureSerializationTest {

	final static String SEP = System.getProperty("line.separator");
	final static String HEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
	static String DATA = HEAD 
			+SEP+"<modcreature>\n" + 
			"            <base ref=\"strong\"/>\n" + 
			"            <role ref=\"familiar\">\n" + 
			"               <choices>\n" + 
			"                  <choice>\n" + 
			"                     <origin>\n" + 
			"                        <selmod num=\"1\" values=\"\">\n" + 
			"                           <skillmod ref=\"antimagic\" type=\"rel\" value=\"6\"/>\n" + 
			"                           <skillmod ref=\"combatmagic\" type=\"rel\" value=\"6\"/>\n" + 
			"                           <skillmod ref=\"windmagic\" type=\"rel\" value=\"6\"/>\n" + 
			"                           <skillmod ref=\"stonemagic\" type=\"rel\" value=\"6\"/>\n" + 
			"                           <skillmod ref=\"enhancemagic\" type=\"rel\" value=\"6\"/>\n" + 
			"                           <skillmod ref=\"firemagic\" type=\"rel\" value=\"6\"/>\n" + 
			"                           <skillmod ref=\"watermagic\" type=\"rel\" value=\"6\"/>\n" + 
			"                           <skillmod ref=\"shadowmagic\" type=\"rel\" value=\"6\"/>\n" + 
			"                           <skillmod ref=\"naturemagic\" type=\"rel\" value=\"6\"/>\n" + 
			"                           <skillmod ref=\"illusionmagic\" type=\"rel\" value=\"6\"/>\n" + 
			"                           <skillmod ref=\"controlmagic\" type=\"rel\" value=\"6\"/>\n" + 
			"                           <skillmod ref=\"motionmagic\" type=\"rel\" value=\"6\"/>\n" + 
			"                           <skillmod ref=\"insightmagic\" type=\"rel\" value=\"6\"/>\n" + 
			"                           <skillmod ref=\"healmagic\" type=\"rel\" value=\"6\"/>\n" + 
			"                           <skillmod ref=\"lightmagic\" type=\"rel\" value=\"6\"/>\n" + 
			"                           <skillmod ref=\"fatemagic\" type=\"rel\" value=\"6\"/>\n" + 
			"                           <skillmod ref=\"protectionmagic\" type=\"rel\" value=\"6\"/>\n" + 
			"                           <skillmod ref=\"deathmagic\" type=\"rel\" value=\"6\"/>\n" + 
			"                           <skillmod ref=\"transformationmagic\" type=\"rel\" value=\"6\"/>\n" + 
			"                        </selmod>\n" + 
			"                     </origin>\n" + 
			"                     <result>\n" + 
			"                        <skillmod ref=\"healmagic\" type=\"rel\" value=\"6\"/>\n" + 
			"                     </result>\n" + 
			"                  </choice>\n" + 
			"                  <choice>\n" + 
			"                     <origin>\n" + 
			"                        <spellmod levelplusone=\"1\"/>\n" + 
			"                     </origin>\n" + 
			"                     <result>\n" + 
			"                        <spellmod>\n" + 
			"                           <spell free=\"-1\" school=\"healmagic\" spell=\"enhanceconst\"/>\n" + 
			"                        </spellmod>\n" + 
			"                     </result>\n" + 
			"                  </choice>\n" + 
			"               </choices>\n" + 
			"            </role>\n" + 
			"            <options>\n" + 
			"               <option ref=\"steeped_in_magic\" uniqueid=\"20d924f3-fecc-4ddf-90c9-2e73769bf94f\">\n" + 
			"                  <choices>\n" + 
			"                     <choice>\n" + 
			"                        <origin>\n" + 
			"                           <selmod num=\"1\" values=\"\">\n" + 
			"                              <attrmod attr=\"CHARISMA\" type=\"rel\" val=\"1\"/>\n" + 
			"                              <attrmod attr=\"MIND\" type=\"rel\" val=\"1\"/>\n" + 
			"                           </selmod>\n" + 
			"                        </origin>\n" + 
			"                        <result>\n" + 
			"                           <attrmod attr=\"MIND\" type=\"rel\" val=\"1\"/>\n" + 
			"                        </result>\n" + 
			"                     </choice>\n" + 
			"                     <choice>\n" + 
			"                        <origin>\n" + 
			"                           <skillmod choice=\"MAGIC\" restrict=\"MUST_NOT_EXIST\" type=\"rel\" value=\"9\"/>\n" + 
			"                        </origin>\n" + 
			"                        <result>\n" + 
			"                           <skillmod ref=\"firemagic\" type=\"rel\" value=\"9\"/>\n" + 
			"                        </result>\n" + 
			"                     </choice>\n" + 
			"                     <choice>\n" + 
			"                        <origin>\n" + 
			"                           <skillmod choice=\"MAGIC\" restrict=\"MUST_NOT_EXIST\" type=\"rel\" value=\"6\"/>\n" + 
			"                        </origin>\n" + 
			"                        <result>\n" + 
			"                           <skillmod ref=\"protectionmagic\" type=\"rel\" value=\"6\"/>\n" + 
			"                        </result>\n" + 
			"                     </choice>\n" + 
			"                     <choice>\n" + 
			"                        <origin>\n" + 
			"                           <spellmod levelplusone=\"1\"/>\n" + 
			"                        </origin>\n" + 
			"                        <result>\n" + 
			"                           <spellmod>\n" + 
			"                              <spell free=\"-1\" school=\"protectionmagic\" spell=\"minorprotectmagic\"/>\n" + 
			"                           </spellmod>\n" + 
			"                        </result>\n" + 
			"                     </choice>\n" + 
			"                     <choice>\n" + 
			"                        <origin>\n" + 
			"                           <spellmod levelplusone=\"1\"/>\n" + 
			"                        </origin>\n" + 
			"                        <result>\n" + 
			"                           <spellmod>\n" + 
			"                              <spell free=\"-1\" school=\"firemagic\" spell=\"flame\"/>\n" + 
			"                           </spellmod>\n" + 
			"                        </result>\n" + 
			"                     </choice>\n" + 
			"                     <choice>\n" + 
			"                        <origin>\n" + 
			"                           <spellmod levelplusone=\"2\"/>\n" + 
			"                        </origin>\n" + 
			"                        <result>\n" + 
			"                           <spellmod>\n" + 
			"                              <spell free=\"-1\" school=\"protectionmagic\" spell=\"minormagicarmor\"/>\n" + 
			"                           </spellmod>\n" + 
			"                        </result>\n" + 
			"                     </choice>\n" + 
			"                     <choice>\n" + 
			"                        <origin>\n" + 
			"                           <spellmod levelplusone=\"3\"/>\n" + 
			"                        </origin>\n" + 
			"                        <result>\n" + 
			"                           <spellmod>\n" + 
			"                              <spell free=\"-1\" school=\"firemagic\" spell=\"firelance\"/>\n" + 
			"                           </spellmod>\n" + 
			"                        </result>\n" + 
			"                     </choice>\n" + 
			"                     <choice>\n" + 
			"                        <origin>\n" + 
			"                           <selmod num=\"1\" values=\"\">\n" + 
			"                              <creaturefeaturemod feature=\"COWARD\" remove=\"true\"/>\n" + 
			"                              <creaturefeaturemod feature=\"uncontrollable\"/>\n" + 
			"                              <creaturefeaturemod feature=\"concentrated\"/>\n" + 
			"                           </selmod>\n" + 
			"                        </origin>\n" + 
			"                        <result>\n" + 
			"                           <creaturefeaturemod feature=\"COWARD\" remove=\"true\"/>\n" + 
			"                        </result>\n" + 
			"                     </choice>\n" + 
			"                  </choices>\n" + 
			"               </option>\n" + 
			"            </options>\n" + 
			"            <creaturetypes>\n" + 
			"               <creaturetype type=\"ANIMAL\"/>\n" + 
			"            </creaturetypes>\n" + 
			"         </modcreature>"+SEP;

	final static ModuleBasedCreature CHARAC = new ModuleBasedCreature();
	static private Serializer m;

	//-------------------------------------------------------------------
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Logger.getRootLogger().setLevel(Level.WARN);
		DATA = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + 
		"<modcreature>\n" + 
		"   <base ref=\"agile\"/>\n" + 
		"   <role ref=\"combatanimal\">\n" + 
		"      <choices>\n" + 
		"         <choice>\n" + 
		"            <origin>\n" + 
		"               <mastermod level=\"1\" skill=\"melee\"/>\n" + 
		"            </origin>\n" + 
		"            <result>\n" + 
		"               <mastermod ref=\"melee/push\" skill=\"melee\"/>\n" + 
		"            </result>\n" + 
		"         </choice>\n" + 
		"      </choices>\n" + 
		"   </role>\n" + 
		"   <creaturetypes>\n" + 
		"      <creaturetype level=\"3\" type=\"MAGIC_CREATURE\"/>\n" + 
		"   </creaturetypes>\n" + 
		"</modcreature>\n";
		
		SplitterMondCore.initialize(new SplittermondRules());
		(new MondstahlklingenPlugin()).init();
		(new BestienUndUngeheuerPlugin()).init();
		(new BeastMasterPlugin()).init();

		CreatureModuleReference agile =new CreatureModuleReference(SplitterMondCore.getCreatureModule("agile"));
		agile.setUniqueId(null);
		agile.addModification(new MastershipModification(SplitterMondCore.getSkill("melee").getMastership("push")));
		CHARAC.setBase(agile);
		CreatureModuleReference combat =new CreatureModuleReference(SplitterMondCore.getCreatureModule("combatanimal"));
		combat.setUniqueId(null);
		combat.addChoice(new CreatureModuleReference.NecessaryChoice(
				new MastershipModification(SplitterMondCore.getSkill("melee"), 1),
				new MastershipModification(SplitterMondCore.getSkill("melee").getMastership("push"))));
		CHARAC.setRole(combat);

		CHARAC.addCreatureType(new CreatureTypeValue(SplitterMondCore.getCreatureType("MAGIC_CREATURE"), 3));

		// To be ignored in XML
		CreatureWeapon weapon = new CreatureWeapon("body");
		weapon.setSkill(SplitterMondCore.getSkill("melee"));
		weapon.setSpeed(13);
		weapon.setDamage(30604);;
		CHARAC.addWeapon(weapon);


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
	public ModuleBasedCreatureSerializationTest() throws Exception {
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
			ModuleBasedCreature result = m.read(ModuleBasedCreature.class, new StringReader(DATA));
			System.out.println("Read "+result.dump());

			//			assertEquals(11, result.getSkillPoints(SplitterMondCore.getSkill("acrobatics")));
			//			assertEquals(4, result.getAttribute(Attribute.CHARISMA).getDistributed());
			//			assertEquals(3, result.getAttribute(Attribute.MIND).getBought());
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
