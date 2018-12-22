package org.prelle.splimo.chargen4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.DummyRulePlugin;
import org.prelle.splimo.MastershipReference;
import org.prelle.splimo.Moonsign;
import org.prelle.splimo.ResourceReference;
import org.prelle.splimo.RewardImpl;
import org.prelle.splimo.SpellValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SpliMoCharacter.Gender;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl4.AttributeController;
import org.prelle.splimo.charctrl4.BackgroundController;
import org.prelle.splimo.charctrl4.CultureController;
import org.prelle.splimo.charctrl4.EducationController;
import org.prelle.splimo.charctrl4.Generator;
import org.prelle.splimo.charctrl4.MastershipController;
import org.prelle.splimo.charctrl4.NewSpliMoCharacterGenerator;
import org.prelle.splimo.charctrl4.PowerController;
import org.prelle.splimo.charctrl4.RaceController;
import org.prelle.splimo.charctrl4.ResourceController;
import org.prelle.splimo.charctrl4.SkillController;
import org.prelle.splimo.charctrl4.SpellController;
import org.prelle.splimo.charctrl4.SpellController.FreeSelection;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.modifications.MastershipModification;
import org.prelle.splimo.modifications.ModificationChoice;
import org.prelle.splimo.modifications.ResourceModification;
import org.prelle.splimo.modifications.SkillModification;

import de.rpgframework.genericrpg.Reward;

/**
 * @author prelle
 *
 */
public class ExampleCharactersTest { 

	private NewSpliMoCharacterGenerator charGen;
	private SpliMoCharacter model;
	
	//-------------------------------------------------------------------
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PropertyConfigurator.configure(ClassLoader.getSystemResource("log4j.properties"));
		SplitterMondCore.initialize(new DummyRulePlugin<SpliMoCharacter>());
//		System.exit(0);
	}

	//-------------------------------------------------------------------
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		model  = new SpliMoCharacter();
		GenerationEventDispatcher.clear();
		charGen = new NewSpliMoCharacterGenerator();
//		charGen.setCallback(this);
		charGen.start(model);
	}

	//-------------------------------------------------------------------
	@Test
	public void generateTiai() {
		assertEquals(10, (((Generator)charGen.getPowerController()).getPointsLeft()));
		assertEquals(8, ((Generator)charGen.getResourceController()).getPointsLeft());
		assertEquals(55, (((Generator)charGen.getSkillController()).getPointsLeft()));
		assertEquals(3, ((Generator)charGen.getMastershipController()).getPointsLeft());
		assertEquals(10, (((Generator)charGen.getAttributeController()).getPointsLeft()));

		/* 
		 * Culture
		 * 15 Skills, 1 Power, 1 Mastership
		 */
		CultureController cuCtrl = charGen.getCultureController();
		cuCtrl.selectCulture(SplitterMondCore.getCulture("albseebund"));
		// Make a decision
		cuCtrl.decide(cuCtrl.getDecisionsToMake().get(0), Arrays.asList(((ModificationChoice)cuCtrl.getDecisionsToMake().get(0).getChoice()).getOptionList().get(0)));
		cuCtrl.decide(cuCtrl.getDecisionsToMake().get(1), Arrays.asList(((ModificationChoice)cuCtrl.getDecisionsToMake().get(1).getChoice()).getOptionList().get(0)));
		cuCtrl.decide(cuCtrl.getDecisionsToMake().get(2), Arrays.asList(((ModificationChoice)cuCtrl.getDecisionsToMake().get(2).getChoice()).getOptionList().get(1)));
		cuCtrl.decide(cuCtrl.getDecisionsToMake().get(3), Arrays.asList(((ModificationChoice)cuCtrl.getDecisionsToMake().get(3).getChoice()).getOptionList().get(1)));
		assertEquals(9, (((Generator)charGen.getPowerController()).getPointsLeft()));
		assertEquals(8, ((Generator)charGen.getResourceController()).getPointsLeft());
		assertEquals(40, (((Generator)charGen.getSkillController()).getPointsLeft()));
		assertEquals(2, ((Generator)charGen.getMastershipController()).getPointsLeft());
		assertEquals(10, (((Generator)charGen.getAttributeController()).getPointsLeft()));
		
		/*
		 * Race
		 * 4 PowerPoints
		 */
		RaceController raCtrl = charGen.getRaceController();
		assertEquals(1, raCtrl.getAvailableRaces().size());
		raCtrl.selectRace(SplitterMondCore.getRace("alben"));
		// Make a decision
		raCtrl.decide(raCtrl.getDecisionsToMake().get(0), Arrays.asList(((ModificationChoice)raCtrl.getDecisionsToMake().get(0).getChoice()).getOptionList().get(0)));
		assertEquals(5, (((Generator)charGen.getPowerController()).getPointsLeft()));
		assertEquals(8, ((Generator)charGen.getResourceController()).getPointsLeft());
		assertEquals(40, (((Generator)charGen.getSkillController()).getPointsLeft()));
		assertEquals(2, ((Generator)charGen.getMastershipController()).getPointsLeft());
		assertEquals(10, (((Generator)charGen.getAttributeController()).getPointsLeft()));
		
		/*
		 * Background
		 * 4 Resource, 5 Skill
		 */
		BackgroundController bgCtrl = charGen.getBackgroundGenerator();
		bgCtrl.select(SplitterMondCore.getBackground("merchant"));
		// Make a decision
		bgCtrl.decide(bgCtrl.getDecisionsToMake().get(0), Arrays.asList(
				new ResourceModification(SplitterMondCore.getResource("contacts"), 1),
				new ResourceModification(SplitterMondCore.getResource("wealth"), 2)
				));
		bgCtrl.decide(bgCtrl.getDecisionsToMake().get(1), Arrays.asList(((ModificationChoice)bgCtrl.getDecisionsToMake().get(1).getChoice()).getOptionList().get(1)));
		bgCtrl.decide(bgCtrl.getDecisionsToMake().get(2), Arrays.asList(((ModificationChoice)bgCtrl.getDecisionsToMake().get(2).getChoice()).getOptionList().get(1)));
		assertEquals(5, (((Generator)charGen.getPowerController()).getPointsLeft()));
		assertEquals(4, ((Generator)charGen.getResourceController()).getPointsLeft());
		assertEquals(35, (((Generator)charGen.getSkillController()).getPointsLeft()));
		assertEquals(2, ((Generator)charGen.getMastershipController()).getPointsLeft());
		assertEquals(10, (((Generator)charGen.getAttributeController()).getPointsLeft()));
		
		/*
		 * Education
		 */
		EducationController edCtrl = charGen.getEducationGenerator();
		edCtrl.selectEducation(SplitterMondCore.getEducation("bladedancer"));
		edCtrl.decide(edCtrl.getDecisionsToMake().get(0), Arrays.asList(((ModificationChoice)edCtrl.getDecisionsToMake().get(0).getChoice()).getOptionList().get(1)));
		edCtrl.decide(edCtrl.getDecisionsToMake().get(1), Arrays.asList(((ModificationChoice)edCtrl.getDecisionsToMake().get(1).getChoice()).getOptionList().get(0)));
		edCtrl.decide(edCtrl.getDecisionsToMake().get(2), Arrays.asList(
				new SkillModification(SplitterMondCore.getSkill("chains"), 3),
				new SkillModification(SplitterMondCore.getSkill("blades"), 3)
				));
		edCtrl.decide(edCtrl.getDecisionsToMake().get(3), Arrays.asList(((ModificationChoice)edCtrl.getDecisionsToMake().get(3).getChoice()).getOptionList().get(0)));
		edCtrl.decide(edCtrl.getDecisionsToMake().get(4), Arrays.asList(
				new SkillModification(SplitterMondCore.getSkill("enhancemagic"), 3),
				new SkillModification(SplitterMondCore.getSkill("protectionmagic"), 2)
				));
		edCtrl.decide(edCtrl.getDecisionsToMake().get(5), Arrays.asList(((ModificationChoice)edCtrl.getDecisionsToMake().get(5).getChoice()).getOptionList().get(0)));
		edCtrl.decide(edCtrl.getDecisionsToMake().get(6), Arrays.asList(((ModificationChoice)edCtrl.getDecisionsToMake().get(6).getChoice()).getOptionList().get(0)));
		edCtrl.decide(edCtrl.getDecisionsToMake().get(7), Arrays.asList(new MastershipModification(SplitterMondCore.getSkill("chains").getMastership("knock"))));
		assertEquals(5, (((Generator)charGen.getSkillController()).getPointsLeft()));
		assertEquals(2, ((Generator)charGen.getResourceController()).getPointsLeft());
		assertEquals(0, ((Generator)charGen.getMastershipController()).getPointsLeft());
		
		/*
		 * Attributes
		 */
		AttributeController atCtrl = charGen.getAttributeController();
		assertFalse(atCtrl.canBeDecreased(Attribute.CHARISMA));
		assertTrue(atCtrl.canBeIncreased(Attribute.CHARISMA));
		charGen.getAttributeController().increase(Attribute.CHARISMA);
		charGen.getAttributeController().increase(Attribute.AGILITY);
		charGen.getAttributeController().increase(Attribute.AGILITY);
		charGen.getAttributeController().increase(Attribute.INTUITION);
		charGen.getAttributeController().increase(Attribute.CONSTITUTION);
		charGen.getAttributeController().increase(Attribute.MYSTIC);
		charGen.getAttributeController().increase(Attribute.MYSTIC);
		charGen.getAttributeController().increase(Attribute.STRENGTH);
		charGen.getAttributeController().increase(Attribute.MIND);
		charGen.getAttributeController().increase(Attribute.WILLPOWER);
		
		assertEquals(3, model.getAttribute(Attribute.CHARISMA).getValue());
		assertEquals(5, model.getAttribute(Attribute.AGILITY).getValue());
		assertEquals(2, model.getAttribute(Attribute.INTUITION).getValue());
		assertEquals(1, model.getAttribute(Attribute.CONSTITUTION).getValue());
		assertEquals(3, model.getAttribute(Attribute.MYSTIC).getValue());
		assertEquals(2, model.getAttribute(Attribute.STRENGTH).getValue());
		assertEquals(2, model.getAttribute(Attribute.MIND).getValue());
		assertEquals(2, model.getAttribute(Attribute.WILLPOWER).getValue());
		
		assertEquals(15, model.getAttribute(Attribute.FOCUS).getValue());
		
		/*
		 * Resources
		 */
		ResourceController reCtrl = charGen.getResourceController();
		ResourceReference resRef = reCtrl.openResource(SplitterMondCore.getResource("creature"));
		reCtrl.increase(resRef);
		assertEquals(0, ((Generator)charGen.getResourceController()).getPointsLeft());
		ResourceReference creature = resRef;
		ResourceReference contacts = null;
		ResourceReference status = null;
		for (ResourceReference tmp : model.getResources()) {
			if (tmp.getResource().getId().equals("contacts"))
				contacts = tmp;
			if (tmp.getResource().getId().equals("status"))
				status = tmp;
		}
		
		/*
		 * Powers
		 */
		PowerController pwCtrl = charGen.getPowerController();
		assertEquals(3, ((Generator)charGen.getPowerController()).getPointsLeft());
		pwCtrl.select(SplitterMondCore.getPower("animalfamiliar"));
		assertEquals(2, ((Generator)charGen.getPowerController()).getPointsLeft());
		pwCtrl.select(SplitterMondCore.getPower("focuspool"));
		assertEquals(0, ((Generator)charGen.getPowerController()).getPointsLeft());
		assertEquals(20, model.getAttribute(Attribute.FOCUS).getValue());
		
		/*
		 * Remaining skills
		 */
		SkillController skCtrl = charGen.getSkillController();
		assertEquals(0, ((Generator)charGen.getMastershipController()).getPointsLeft());
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("chains"))); // 4->5
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("chains"))); // 5->6
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("acrobatics"))); // 4->5
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("acrobatics"))); // 5->6
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("arcanelore"))); // 5->6
		assertEquals(3, ((Generator)charGen.getMastershipController()).getPointsLeft());
		
		/*
		 * Step 8: Moonsign and weaknesses
		 */
		charGen.selectSplinter(Moonsign.FLASH);
		
		/* 
		 * Step 9: Start-Exp
		 */
		Reward reward = new RewardImpl(15, "Start-Exp");
		reward.setDate(new Date(System.currentTimeMillis()));
		model.setExperienceFree(15);
		model.addReward(reward);
		System.exit(0);
		charGen.startTuningMode();
		
		// Increase with exp
		skCtrl = charGen.getSkillController();
//		assertEquals(3, ((Generator)charGen.getMastershipController()).getPointsLeft());
		assertEquals(15, model.getExperienceFree());
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("watermagic"))); // 4->5
		assertEquals(12, model.getExperienceFree());
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("watermagic"))); // 5->6
		assertEquals( 9, model.getExperienceFree());
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("protectionmagic"))); // 2->3
		assertEquals( 6, model.getExperienceFree());
		// Cannot spend 5 exp for acrobatics mastership, since there is still one free
		// History should contain 3 items here
		assertEquals(3, model.getHistory().size());
		
		/*
		 * Step 10: Start finetuning
		 */
		reCtrl = charGen.getResourceController();
		assertTrue(reCtrl.canBeDecreased(contacts));
		reCtrl.decrease(contacts);
		reCtrl.decrease(status);
		assertTrue(reCtrl.canBeIncreased(creature));
		assertTrue(reCtrl.increase(creature));
		assertTrue(reCtrl.increase(creature));
		// Fake attached creature for this ressource
		creature.setIdReference(UUID.randomUUID());
		// Change enhancemagic to combatmagic
		assertTrue(skCtrl.decrease(model.getSkillValue(SplitterMondCore.getSkill("enhancemagic"))));
		assertEquals( 6, model.getExperienceFree());
		assertTrue(skCtrl.decrease(model.getSkillValue(SplitterMondCore.getSkill("enhancemagic"))));
		assertTrue(skCtrl.decrease(model.getSkillValue(SplitterMondCore.getSkill("enhancemagic"))));
		assertEquals( 6, model.getExperienceFree());
		assertTrue(skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("combatmagic"))));
		assertEquals( 6, model.getExperienceFree());
		assertTrue(skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("combatmagic"))));
		assertTrue(skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("combatmagic"))));
		// Replace free generation mastership with another
		MastershipReference sailorslegs = null;
		for (MastershipReference ref : model.getSkillValue(SplitterMondCore.getSkill("seafaring")).getMasterships()) {
			if (ref.getMastership().getId().equals("sailorslegs")) {
				sailorslegs = ref;
				break;
			}
		}
		MastershipController maCtrl = charGen.getMastershipController();
		assertTrue(maCtrl.deselect(sailorslegs.getMastership()));
		assertTrue(maCtrl.select(SplitterMondCore.getSkill("combatmagic").getMastership("aimedspells")));
		// Remaining 4 free masterships granted by 6 points in skill
		assertTrue(maCtrl.select(SplitterMondCore.getSkill("chains").getMastership("ignoreshield")));
		assertTrue(maCtrl.select(SplitterMondCore.getSkill("acrobatics").getMastership("evade1")));
		assertTrue(maCtrl.select(SplitterMondCore.getSkill("arcanelore").getMastership("arcanedefense1")));
		assertTrue(maCtrl.select(SplitterMondCore.getSkill("watermagic").getMastership("savingcaster")));
		// Pay with 5 EP (from Step 9)
		assertTrue(maCtrl.select(SplitterMondCore.getSkill("acrobatics").getMastership("flashreflexes")));
		assertEquals(1, model.getExperienceFree());
		assertEquals(4, model.getHistory().size());
		// Spells
		SpellController spCtrl = charGen.getSpellController();
		Iterator<FreeSelection> it = spCtrl.getUnusedFreeSelections(SplitterMondCore.getSkill("combatmagic")).iterator(); 
		spCtrl.select(it.next(), new SpellValue(SplitterMondCore.getSpell("ghostdagger"), SplitterMondCore.getSkill("combatmagic")));
		spCtrl.select(it.next(), new SpellValue(SplitterMondCore.getSpell("flamingweapon"), SplitterMondCore.getSkill("combatmagic")));
		it = spCtrl.getUnusedFreeSelections(SplitterMondCore.getSkill("protectionmagic")).iterator(); 
		spCtrl.select(it.next(), new SpellValue(SplitterMondCore.getSpell("minorprotectmagic"), SplitterMondCore.getSkill("protectionmagic")));
		spCtrl.select(it.next(), new SpellValue(SplitterMondCore.getSpell("minormagicarmor"), SplitterMondCore.getSkill("protectionmagic")));
		it = spCtrl.getUnusedFreeSelections(SplitterMondCore.getSkill("watermagic")).iterator(); 
		spCtrl.select(it.next(), new SpellValue(SplitterMondCore.getSpell("quickwash"), SplitterMondCore.getSkill("watermagic")));
		spCtrl.select(it.next(), new SpellValue(SplitterMondCore.getSpell("freeze"), SplitterMondCore.getSkill("watermagic")));
		spCtrl.select(it.next(), new SpellValue(SplitterMondCore.getSpell("icelance"), SplitterMondCore.getSkill("watermagic")));
		assertEquals( 1, model.getExperienceFree());
		spCtrl.select(new SpellValue(SplitterMondCore.getSpell("harmitem"), SplitterMondCore.getSkill("combatmagic")));
		assertEquals(0, model.getExperienceFree());
		assertEquals(5, model.getHistory().size());
		
		
		model.setName("Tiai Schimmersee");
		model.setSize(192);
		model.setWeight(70);
		model.setBirthPlace("Tairon");
		model.setGender(Gender.FEMALE);
		
		assertTrue(charGen.getToDos().isEmpty());
		
		// Finalize
		charGen.stop();

		try {
			System.out.println("Save "+model.getName());
			byte[] data = SplitterMondCore.save(model);
			System.out.println(new String(data));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		System.out.println(model.dump());
	}

//	//-------------------------------------------------------------------
//	/**
//	 * @see org.prelle.shadowrun.gen.SR5LetUserChooseListener#letUserChoose(java.lang.String, org.prelle.shadowrun.modifications.ModificationChoice)
//	 */
//	@Override
//	public Collection<Modification> letUserChoose(String choiceReason, ModificationChoice choice) {
//		// TODO Auto-generated method stub
//		System.out.println("Because of '"+choiceReason+"' choose "+choice);
//		
//		if (choice.getNumberOfChoices()>0) {
//			System.out.println("Select "+choice.subList(0, choice.getNumberOfChoices()));
//			return choice.subList(0, choice.getNumberOfChoices());
//		}
//		
//		System.exit(0);
//		return null;
//	}

}
