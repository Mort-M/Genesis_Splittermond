package org.prelle.splimo.chargen4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.UUID;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.prelle.rpgframework.splittermond.data.SplittermondDataPlugin;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.MastershipReference;
import org.prelle.splimo.Moonsign;
import org.prelle.splimo.ResourceReference;
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
		SplittermondDataPlugin plugin = new SplittermondDataPlugin();
		plugin.init( (percent) -> {});
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
	public SpliMoCharacter getModel() {
		return charGen.getModel();
	}

	//-------------------------------------------------------------------
	@Test
	public void testGenerateTiai() {
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
//		Reward reward = new RewardImpl(15, "Start-Exp");
//		reward.setDate(new Date(System.currentTimeMillis()));
//		model.setExperienceFree(15);
//		model.addReward(reward);
//		charGen.startTuningMode();
		
		// Increase with exp
		skCtrl = charGen.getSkillController();
//		assertEquals(3, ((Generator)charGen.getMastershipController()).getPointsLeft());
		assertEquals(15, model.getExperienceFree());
		assertEquals(0, model.getHistory().size());
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("watermagic"))); // 4->5
		assertEquals(12, model.getExperienceFree());
		assertEquals(1, model.getHistory().size());
		
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("watermagic"))); // 5->6
		assertEquals( 9, model.getExperienceFree());
		assertEquals( 1, model.getHistory().size());
		
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("protectionmagic"))); // 2->3
		assertEquals( 6, model.getExperienceFree());
		assertEquals( 1, model.getHistory().size()); // 3 points in watermagic
		
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
		assertTrue(skCtrl.decrease(model.getSkillValue(SplitterMondCore.getSkill("enhancemagic")))); // Grant 3 EP invested for water- & protectionmagic
		assertEquals( 9, model.getExperienceFree());
		assertTrue(skCtrl.decrease(model.getSkillValue(SplitterMondCore.getSkill("enhancemagic"))));
		assertTrue(skCtrl.decrease(model.getSkillValue(SplitterMondCore.getSkill("enhancemagic"))));
		assertEquals(15, model.getExperienceFree());
		assertTrue(skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("combatmagic"))));
		assertEquals(12, model.getExperienceFree());
		assertTrue(skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("combatmagic"))));
		assertTrue(skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("combatmagic"))));
		assertEquals( 6, model.getExperienceFree());
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
		assertNotNull(maCtrl.select(SplitterMondCore.getSkill("combatmagic").getMastership("aimedspells")));
		// Remaining 4 free masterships granted by 6 points in skill
		assertNotNull(maCtrl.select(SplitterMondCore.getSkill("chains").getMastership("ignoreshield")));
		assertNotNull(maCtrl.select(SplitterMondCore.getSkill("acrobatics").getMastership("evade1")));
		assertNotNull(maCtrl.select(SplitterMondCore.getSkill("arcanelore").getMastership("arcanedefense1")));
		assertNotNull(maCtrl.select(SplitterMondCore.getSkill("watermagic").getMastership("savingcaster")));
		// Pay with 5 EP (from Step 9)
		assertEquals(6, model.getExperienceFree());
		assertNotNull("Cannot buy mastership with EXP", maCtrl.select(SplitterMondCore.getSkill("acrobatics").getMastership("flashreflexes")));
		assertEquals(1, model.getExperienceFree());
		assertEquals(2, model.getHistory().size());

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
		assertEquals(3, model.getHistory().size());
		
		
		model.setName("Tiai Schimmersee");
		model.setSize(192);
		model.setWeight(70);
		model.setBirthPlace("Tairon");
		model.setGender(Gender.FEMALE);
		
		assertEquals(3, charGen.getToDos().size());
		
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

	//-------------------------------------------------------------------
	/*
	 * Generate Carimea
	 */
	@Test
	public void generateFree() {

		/* 
		 * Culture
		 */
		CultureController cuCtrl = charGen.getCultureController();
		assertEquals(1, cuCtrl.getToDos().size());
//		charGen.getModel().setOwnCulture(new Culture("custom", "Feen"));
		
		/*
		 * Race
		 */
		RaceController raCtrl = charGen.getRaceController();
		assertEquals(5, raCtrl.getAvailableRaces().size());
		raCtrl.selectRace(SplitterMondCore.getRace("human"));
		// Make a decision
		raCtrl.decide(raCtrl.getDecisionsToMake().get(0), Arrays.asList(
				((ModificationChoice)raCtrl.getDecisionsToMake().get(0).getChoice()).getOptionList().get(5),
				((ModificationChoice)raCtrl.getDecisionsToMake().get(0).getChoice()).getOptionList().get(6)
				));
//		assertEquals(5, (((Generator)charGen.getPowerController()).getPointsLeft()));
//		assertEquals(8, ((Generator)charGen.getResourceController()).getPointsLeft());
//		assertEquals(40, (((Generator)charGen.getSkillController()).getPointsLeft()));
//		assertEquals(2, ((Generator)charGen.getMastershipController()).getPointsLeft());
//		assertEquals(10, (((Generator)charGen.getAttributeController()).getPointsLeft()));

		/*
		 * Attributes
		 */
		charGen.getAttributeController().increase(Attribute.CHARISMA);
		charGen.getAttributeController().increase(Attribute.AGILITY);
		charGen.getAttributeController().increase(Attribute.AGILITY);
		charGen.getAttributeController().increase(Attribute.INTUITION);
		charGen.getAttributeController().increase(Attribute.CONSTITUTION);
		charGen.getAttributeController().increase(Attribute.MYSTIC);
		charGen.getAttributeController().increase(Attribute.MYSTIC);
		charGen.getAttributeController().increase(Attribute.STRENGTH);
		charGen.getAttributeController().increase(Attribute.STRENGTH);
		charGen.getAttributeController().increase(Attribute.WILLPOWER);
		
		assertEquals(2, model.getAttribute(Attribute.CHARISMA).getValue());
		assertEquals(3, model.getAttribute(Attribute.AGILITY).getValue());
		assertEquals(2, model.getAttribute(Attribute.INTUITION).getValue());
		assertEquals(2, model.getAttribute(Attribute.CONSTITUTION).getValue());
		assertEquals(4, model.getAttribute(Attribute.MYSTIC).getValue());
		assertEquals(4, model.getAttribute(Attribute.STRENGTH).getValue());
		assertEquals(1, model.getAttribute(Attribute.MIND).getValue());
		assertEquals(2, model.getAttribute(Attribute.WILLPOWER).getValue());
		
		assertEquals(12, model.getAttribute(Attribute.FOCUS).getValue());
		
		/*
		 * Powers
		 */
		PowerController pwCtrl = charGen.getPowerController();
		assertEquals(8, ((Generator)charGen.getPowerController()).getPointsLeft());
		pwCtrl.select(SplitterMondCore.getPower("resistheat"));
		assertEquals(7, ((Generator)charGen.getPowerController()).getPointsLeft());
		pwCtrl.select(SplitterMondCore.getPower("childfire"));
		assertEquals(5, ((Generator)charGen.getPowerController()).getPointsLeft());
		pwCtrl.select(SplitterMondCore.getPower("friendfire"));
		assertEquals(4, ((Generator)charGen.getPowerController()).getPointsLeft());
		pwCtrl.select(SplitterMondCore.getPower("focuspool"));
		assertEquals(2, ((Generator)charGen.getPowerController()).getPointsLeft());
		pwCtrl.select(SplitterMondCore.getPower("attractive"));
		assertEquals(1, ((Generator)charGen.getPowerController()).getPointsLeft());
		pwCtrl.select(SplitterMondCore.getPower("sensefairy"));
		assertEquals(0, ((Generator)charGen.getPowerController()).getPointsLeft());
		assertEquals(17, model.getAttribute(Attribute.FOCUS).getValue());

		/*
		 * Resources
		 */
		ResourceController reCtrl = charGen.getResourceController();
		ResourceReference relic1 = reCtrl.openResource(SplitterMondCore.getResource("relic"));
		reCtrl.increase(relic1);
		reCtrl.increase(relic1);
		reCtrl.increase(relic1);
		reCtrl.increase(relic1);
		reCtrl.setAllowExtremeResourcesOnGeneration(true);
		assertTrue( reCtrl.canBeIncreased(relic1));
		reCtrl.setAllowExtremeResourcesOnGeneration(false);
		ResourceReference relic2 = reCtrl.openResource(SplitterMondCore.getResource("relic"));
		reCtrl.increase(relic2);
		reCtrl.increase(relic2);
		reCtrl.increase(relic2);
		assertEquals(0, ((Generator)charGen.getResourceController()).getPointsLeft());
		assertFalse( reCtrl.canBeIncreased(relic1));
		
		/*
		 * Skills
		 */
		MastershipController maCtrl = charGen.getMastershipController();
		assertEquals(3, maCtrl.getFreeMasterships());

		SkillController skCtrl = charGen.getSkillController();
		assertEquals(55, ((Generator)charGen.getSkillController()).getPointsLeft());
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("blades"))); // 1
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("blades"))); // 2
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("blades"))); // 3
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("blades"))); // 4
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("blades"))); // 5
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("blades"))); // 6
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("leadership"))); // 1
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("leadership"))); // 2
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("arcanelore"))); // 1
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("arcanelore"))); // 2
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("arcanelore"))); // 3
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("athletics"))); // 1
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("athletics"))); // 2
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("athletics"))); // 3
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("athletics"))); // 4
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("athletics"))); // 5
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("athletics"))); // 6
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("diplomacy"))); // 1
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("diplomacy"))); // 2
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("diplomacy"))); // 3
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("empathy"))); // 1
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("empathy"))); // 2
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("empathy"))); // 3
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("determination"))); // 1
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("determination"))); // 2
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("determination"))); // 3
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("determination"))); // 4
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("nature"))); // 1
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("eloquence"))); // 1
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("eloquence"))); // 2
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("eloquence"))); // 3
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("eloquence"))); // 4
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("survival"))); // 1
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("survival"))); // 2
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("perception"))); // 1
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("perception"))); // 2
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("endurance"))); // 1
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("endurance"))); // 2
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("endurance"))); // 3
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("endurance"))); // 4
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("endurance"))); // 5
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("endurance"))); // 6
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("antimagic"))); // 1
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("firemagic"))); // 1
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("firemagic"))); // 2
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("firemagic"))); // 3
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("firemagic"))); // 4
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("firemagic"))); // 5
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("firemagic"))); // 6
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("protectionmagic"))); // 1
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("protectionmagic"))); // 2
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("protectionmagic"))); // 3
//		assertEquals(0, ((Generator)charGen.getSkillController()).getPointsLeft());
		// Seltsam - keine Ahnung wie da die Startwerte waren
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("arcanelore"))); // 4
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("arcanelore"))); // 5
		skCtrl.increase(model.getSkillValue(SplitterMondCore.getSkill("determination"))); // 6
		assertEquals(0, ((Generator)charGen.getSkillController()).getPointsLeft());
		
		/*
		 * Select 3 free masterships
		 */
		maCtrl = charGen.getMastershipController();
		assertEquals(7, maCtrl.getFreeMasterships());
		// Those where level is not 6 yet
		assertNotNull(maCtrl.select(SplitterMondCore.getSkill("arcanelore").getMastership("fairylore1")));
		assertEquals(6, maCtrl.getFreeMasterships());
		maCtrl.select(SplitterMondCore.getSkill("arcanelore").getMastership("orientOtherworld1"));
		maCtrl.select(SplitterMondCore.getSkill("diplomacy").getMastership("fairytongue"));
		assertEquals(4, maCtrl.getFreeMasterships());
		assertNotNull( maCtrl.select(SplitterMondCore.getSkill("endurance").getMastership("armour1")) );
		assertEquals(3, maCtrl.getFreeMasterships());
		assertNotNull( maCtrl.select(SplitterMondCore.getSkill("blades").getMastership("knock")) );
		assertEquals(2, maCtrl.getFreeMasterships());
		assertNotNull( maCtrl.select(SplitterMondCore.getSkill("firemagic").getMastership("flameheart")) );
		assertEquals(1, maCtrl.getFreeMasterships());
		// This should not work - only a mastership for athletics is still open. So exp is needed
		MastershipReference masterRef = maCtrl.select(SplitterMondCore.getSkill("firemagic").getMastership("fireresistence1"));
		assertNotNull( masterRef );
		assertEquals(10, charGen.getModel().getExperienceFree());
		assertEquals(1, maCtrl.getFreeMasterships());
		maCtrl.deselect(masterRef.getMastership());
		assertEquals(15, charGen.getModel().getExperienceFree());
		assertEquals(1, maCtrl.getFreeMasterships());
		assertNotNull( maCtrl.select(SplitterMondCore.getSkill("athletics").getMastership("longjump")) );
		assertEquals(0, maCtrl.getFreeMasterships());
		
		/*
		 * Language and culture lore
		 */
		assertNotNull(charGen.getLanguageController().select(SplitterMondCore.getLanguage("feeisch")));
		
		/*
		 * Step 8: Moonsign and weaknesses
		 */
		charGen.selectSplinter(Moonsign.BLOODMOON);
		
		/* 
		 * Step 9: Start-Exp
		 */
		
		// Increase with exp
		reCtrl = charGen.getResourceController();
		assertFalse( reCtrl.canBeIncreased(relic1));
		reCtrl.setAllowExtremeResourcesOnGeneration(true);
		assertTrue( reCtrl.canBeIncreased(relic1));
		assertEquals(0, ((Generator)charGen.getResourceController()).getPointsLeft());
		assertTrue("Could not increase beyond 4", reCtrl.increase(relic1));
		assertEquals(8, model.getExperienceFree());
		ResourceReference wealth = reCtrl.openResource(SplitterMondCore.getResource("wealth"));
		assertNotNull(wealth);
		wealth.setDescription("Deal mit Feen");
		assertEquals(1, model.getExperienceFree());

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
		
	}
}
