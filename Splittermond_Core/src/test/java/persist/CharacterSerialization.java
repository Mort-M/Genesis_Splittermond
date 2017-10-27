/**
 * 
 */
package persist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.UUID;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;
import org.prelle.simplepersist.Persister;
import org.prelle.simplepersist.SerializationException;
import org.prelle.simplepersist.Serializer;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.CultureLoreReference;
import org.prelle.splimo.LanguageReference;
import org.prelle.splimo.MastershipReference;
import org.prelle.splimo.Moonsign;
import org.prelle.splimo.PowerReference;
import org.prelle.splimo.ResourceReference;
import org.prelle.splimo.RewardImpl;
import org.prelle.splimo.SkillSpecialization;
import org.prelle.splimo.SkillSpecialization.SkillSpecializationType;
import org.prelle.splimo.SkillSpecializationValue;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.SpellValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SpliMoCharacter.Gender;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.items.Availability;
import org.prelle.splimo.items.CarriedItem;
import org.prelle.splimo.items.Complexity;
import org.prelle.splimo.items.EnhancementReference;
import org.prelle.splimo.items.ItemLocationType;
import org.prelle.splimo.items.ItemTemplate;
import org.prelle.splimo.modifications.AttributeModification;
import org.prelle.splimo.modifications.ResourceModification;
import org.prelle.splimo.modifications.SkillModification;

import de.rpgframework.RPGFrameworkLoader;

/**
 * @author prelle
 *
 */
@SuppressWarnings("unchecked")
public class CharacterSerialization {

	final static String SEP = System.getProperty("line.separator");
	final static String HEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
	final static String DATA = HEAD 
			+SEP+"<splimochar bground=\"custom\" culture=\"waechter\" edu=\"custom\" expfree=\"1\" expinv=\"150\" race=\"gnome\" splinter=\"GHOSTTHOUGHT\">"
			+SEP+"   <name>Unnamed Ümläut</name>"
			+SEP+"   <culturelores>"
			+SEP+"      <cultloreref ref=\"waechterbund\"/>"
			+SEP+"   </culturelores>"
			+SEP+"   <languages>"
			+SEP+"      <languageref ref=\"dragoreisch\"/>"
			+SEP+"   </languages>"
			+SEP+"   <powerrefs>"
			+SEP+"      <powerref count=\"1\" ref=\"sensefairy\"/>"
			+SEP+"      <powerref count=\"2\" ref=\"resistmind\"/>"
			+SEP+"   </powerrefs>"
			+SEP+"   <resourcerefs>"
			+SEP+"      <resourceref ref=\"reputation\"/>"
			+SEP+"      <resourceref ref=\"mentor\" val=\"4\"/>"
			+SEP+"   </resourcerefs>"
			+SEP+"   <attributes>"
			+SEP+"      <attr id=\"CHARISMA\" start=\"3\" value=\"5\"/>"
			+SEP+"      <attr id=\"AGILITY\" value=\"0\"/>"
			+SEP+"      <attr id=\"INTUITION\" value=\"0\"/>"
			+SEP+"      <attr id=\"CONSTITUTION\" value=\"0\"/>"
			+SEP+"      <attr id=\"MYSTIC\" value=\"0\"/>"
			+SEP+"      <attr id=\"STRENGTH\" value=\"0\"/>"
			+SEP+"      <attr id=\"MIND\" start=\"3\" value=\"4\"/>"
			+SEP+"      <attr id=\"WILLPOWER\" value=\"0\"/>"
			+SEP+"   </attributes>"
			+SEP+"   <skillvals>"
			+SEP+"      <skillval skill=\"empathy\" val=\"6\"/>"
			+SEP+"      <skillval skill=\"insightmagic\" val=\"9\">"
			+SEP+"         <masterref ref=\"insightmagic/telepathicshield\"/>"
			+SEP+"         <masterref free=\"1\" ref=\"insightmagic/savingcaster\"/>"
			+SEP+"         <masterref spec=\"insightmagic/clairvoyance/2\"/>"
			+SEP+"      </skillval>"
			+SEP+"   </skillvals>"
			+SEP+"   <spellvals>"
			+SEP+"      <spellval free=\"-1\" school=\"insightmagic\" spell=\"truesight\"/>"
			+SEP+"      <spellval free=\"1\" school=\"insightmagic\" spell=\"magicmessage\"/>"
			+SEP+"   </spellvals>"
			+SEP+"   <image>AQIDBA==</image>"
			+SEP+"   <hairColor>blond</hairColor>"
			+SEP+"   <eyeColor>eisgrau</eyeColor>"
			+SEP+"   <furColor>hell</furColor>"
			+SEP+"   <size>180</size>"
			+SEP+"   <weight>80</weight>"
			+SEP+"   <gender>FEMALE</gender>"
			+SEP+"   <weaknesses>"
			+SEP+"      <weakness>Träge</weakness>"
			+SEP+"   </weaknesses>"
			+SEP+"   <itemdefs>"
			+SEP+"      <item avail=\"SMALL_TOWN\" cplx=\"F\" customName=\"Mein irgendetwas\" id=\"customMeinirgendetwas\" load=\"2\" material=\"OTHER\" price=\"0\" robust=\"0\" type=\"\"/>"
			+SEP+"   </itemdefs>"
			+SEP+"   <carries>"
			+SEP+"      <item count=\"1\" customName=\"Superstab\" location=\"BODY\" ref=\"combatstaff\" uniqueid=\"791f8f8a-7989-46ce-83bb-920fa1201eae\"/>"
			+SEP+"      <item count=\"1\" location=\"BODY\" ref=\"flöte\" uniqueid=\"791f8f8a-7989-46ce-83bb-920fa1201e12\">"
			+SEP+"         <modifications>"
			+SEP+"            <attrmod attr=\"MINDRESIST\" type=\"rel\" val=\"1\"/>"
			+SEP+"         </modifications>"
			+SEP+"      </item>"
			+SEP+"      <item count=\"2\" customName=\"Test\" location=\"SOMEWHEREELSE\" ref=\"customMeinirgendetwas\" uniqueid=\"5c2b035b-2bab-4262-be73-3dc667cc6a18\">"
			+SEP+"         <enhancements>"
			+SEP+"            <enhancement ref=\"load\"/>"
			+SEP+"         </enhancements>"
			+SEP+"      </item>"
			+SEP+"   </carries>"
			+SEP+"   <history>"
			+SEP+"      <resourcemod date=\"2014-07-20T23:56:54.784+02:00\" expCost=\"7\" ref=\"creature\" value=\"1\">"
			+SEP+"         <comment>Melba, die Milbe</comment>"
			+SEP+"      </resourcemod>"
			+SEP+"      <skillmod date=\"2014-07-20T23:56:54.784+02:00\" ref=\"empathy\" type=\"rel\" value=\"2\"/>"
			+SEP+"   </history>"
			+SEP+"   <rewards>"
			+SEP+"      <reward date=\"2014-07-20T23:56:54.784+02:00\" exp=\"12\">"
			+SEP+"         <title>Fluch der Hexenkönigin (Midstad)</title>"
			+SEP+"      </reward>"
			+SEP+"   </rewards>"
			+SEP+"</splimochar>"+SEP;
	
	final static SpliMoCharacter CHARAC = new SpliMoCharacter();
	static private Serializer m;

	//-------------------------------------------------------------------
	static {
		Logger.getRootLogger().setLevel(Level.WARN);
//		PropertyConfigurator.configure(ClassLoader.getSystemResourceAsStream("log4j.properties"));
		Enumeration<Appender> e = Logger.getRootLogger().getAllAppenders();
		while (e.hasMoreElements()) {
			Appender ap = e.nextElement();
			ap.setLayout(new PatternLayout("%5p [%c] (%F:%L) - %m%n"));
		}
		Logger.getRootLogger().setLevel(Level.DEBUG);
		Logger.getLogger("xml").setLevel(Level.INFO);

//		RPGFrameworkLoader.getInstance();
		CHARAC.setCulture("waechter");
		CHARAC.setBackground("custom");
		CHARAC.setEducation("custom");
		
		CHARAC.setExperienceInvested(150);
		CHARAC.setExperienceFree(1);
		CHARAC.setRace("gnome");
		CHARAC.setSplinter(Moonsign.GHOSTTHOUGHT);

		CHARAC.setName("Unnamed Ümläut");
		CHARAC.addCultureLore(new CultureLoreReference(SplitterMondCore.getCultureLore("waechterbund")));
		CHARAC.addLanguage(new LanguageReference(SplitterMondCore.getLanguage("dragoreisch")));
		CHARAC.addPower(new PowerReference(SplitterMondCore.getPower("sensefairy")));
		CHARAC.addPower(new PowerReference(SplitterMondCore.getPower("resistmind"),2));
		CHARAC.addResource(new ResourceReference(SplitterMondCore.getResource("reputation"), 0));
		CHARAC.addResource(new ResourceReference(SplitterMondCore.getResource("mentor"), 4));
		CHARAC.setGender(Gender.FEMALE);
		CHARAC.setSize(180);
		CHARAC.setWeight(80);
		CHARAC.setHairColor("blond");
		CHARAC.setFurColor("hell");
		CHARAC.setEyeColor("eisgrau");
		byte[] image = new byte[]{(byte)1,(byte)2,(byte)3,(byte)4};
		CHARAC.setImage(image);
		CHARAC.getAttribute(Attribute.CHARISMA    ).setDistributed(5);
		CHARAC.getAttribute(Attribute.CHARISMA    ).setStart(3);
		CHARAC.getAttribute(Attribute.MIND        ).setDistributed(4);
		CHARAC.getAttribute(Attribute.MIND        ).setStart(3);
//		CHARAC.getAttribute(Attribute.MIND        ).setGenerationModifier(1);

		CHARAC.getSkillValue(SplitterMondCore.getSkill("empathy")).setValue(6);
		SkillValue insight = CHARAC.getSkillValue(SplitterMondCore.getSkill("insightmagic"));
		insight.setValue(9);
		insight.addMastership(new MastershipReference(insight.getSkill().getMastership("telepathicshield")));
		MastershipReference mRef = new MastershipReference(insight.getSkill().getMastership("savingcaster"));
		mRef.setFree(1);
		insight.addMastership(mRef);
		
		SkillSpecialization spec = new SkillSpecialization();
		spec.setSkill(insight.getSkill());
		spec.setId("clairvoyance");
		spec.setType(SkillSpecializationType.SPELLTYPE);
		SkillSpecializationValue specVal = new SkillSpecializationValue(spec, 2);
		insight.addMastership(new MastershipReference(specVal));
		
		SpellValue spell1 = new SpellValue(SplitterMondCore.getSpell("truesight"), insight.getSkill());
		SpellValue spell2 = new SpellValue(SplitterMondCore.getSpell("magicmessage"), insight.getSkill());
		spell2.setFreeLevel(1);
		CHARAC.addSpell(spell1);
		CHARAC.addSpell(spell2);
		
		CarriedItem staff = new CarriedItem();
		staff.setItem(SplitterMondCore.getItem("combatstaff"));
		staff.setItemLocation(ItemLocationType.BODY);
		staff.setName("Superstab");
		staff.setUniqueId(UUID.fromString("791f8f8a-7989-46ce-83bb-920fa1201eae"));
		CHARAC.addItem(staff);
		
		CarriedItem floete = new CarriedItem();
		floete.setItem(SplitterMondCore.getItem("flöte"));
		floete.setItemLocation(ItemLocationType.BODY);
		floete.setUniqueId(UUID.fromString("791f8f8a-7989-46ce-83bb-920fa1201e12"));
		floete.addModification(new AttributeModification(Attribute.MINDRESIST, 1));
		CHARAC.addItem(floete);
		
		Date date = null;
		try {
			date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")).parse("2014-07-20T23:56:54.784+02:00");
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		ResourceModification mod1 = new ResourceModification(SplitterMondCore.getResource("creature"), 1);
		mod1.setExpCost(7);
		mod1.setDate(date);
		mod1.setComment("Melba, die Milbe");
		CHARAC.addToHistory(mod1);
		SkillModification mod2 = new SkillModification(SplitterMondCore.getSkill("empathy"), 2);
		mod2.setDate(date);
		CHARAC.addToHistory(mod2);

		RewardImpl reward1 = new RewardImpl(12, "Fluch der Hexenkönigin (Midstad)");
		reward1.setDate(date);
		CHARAC.addReward(reward1);
		
		CHARAC.addWeakness("Träge");
		
		/*
		 * Add custom item
		 */
		ItemTemplate myItem = new ItemTemplate();
		myItem.setCustomName("Mein irgendetwas");
		myItem.setAvailability(Availability.SMALL_TOWN);
		myItem.setLoad(2);
		myItem.setComplexity(Complexity.EXPERT);
		CHARAC.addCustomItem(myItem);
		CarriedItem myCarriedItem = new CarriedItem(myItem, 2);
		myCarriedItem.setName("Test");
		myCarriedItem.addEnhancement(new EnhancementReference(
				SplitterMondCore.getEnhancement("load")));
		myCarriedItem.setUniqueId(UUID.fromString("5c2b035b-2bab-4262-be73-3dc667cc6a18"));
		CHARAC.addItem(myCarriedItem);
		
		m = new Persister();
		
//		Logger.getLogger("xml").setLevel(Level.DEBUG);
	}
	
	//-------------------------------------------------------------------
	public CharacterSerialization() throws Exception {
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
//		Logger.getLogger("xml").setLevel(Level.DEBUG);
		try {
			SpliMoCharacter result = m.read(SpliMoCharacter.class, new StringReader(DATA));
//			System.out.println("Read "+result.dump());
//			
//			assertEquals(CHARAC.getName(), result.getName());
//			assertEquals(6, result.getSkillPoints(SplitterMondCore.getSkill("empathy")));
//			assertEquals(5, result.getAttribute(Attribute.CHARISMA).getDistributed());
//			assertEquals(1, result.getAttribute(Attribute.MIND).getBought());
//			assertNotNull(result.getImage());
//			byte[] img = result.getImage();
//			assertEquals(4, img.length);
//			assertEquals(1, img[0]);
//			assertEquals(4, img[3]);
//			assertEquals(2, result.getItems().size());
//			assertEquals("Flöte", result.getItems().get(0).getName());
//			assertEquals(0, result.getItems().get(1).getModifications().size());
//			assertEquals(1, result.getWeaknesses().size());
			assertEquals("Träge", result.getWeaknesses().get(0));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

}
