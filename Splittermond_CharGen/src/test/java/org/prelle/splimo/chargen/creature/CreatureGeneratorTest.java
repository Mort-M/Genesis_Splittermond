/**
 *
 */
package org.prelle.splimo.chargen.creature;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.Mastership;
import org.prelle.splimo.ResourceReference;
import org.prelle.splimo.Skill;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.CreatureController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.creature.CreatureFeature;
import org.prelle.splimo.creature.CreatureModuleReference;
import org.prelle.splimo.creature.CreatureWeapon;
import org.prelle.splimo.creature.ModuleBasedCreature;
import org.prelle.splimo.modifications.MastershipModification;

/**
 * @author prelle
 *
 */
public class CreatureGeneratorTest implements GenerationEventListener {

	private CreatureController generator;
	private List<GenerationEvent> events;
	private Map<Skill, Mastership> selectMasters;

	//-------------------------------------------------------------------
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
//		PropertyConfigurator.configure(ClassLoader.getSystemResourceAsStream("log4j.properties"));
//		SplitterMondCore.initialize(new SplittermondRules());
//		if (SplitterMondCore.getItem("bergbarte")==null)
//			(new MondstahlklingenPlugin()).init();
//		if (SplitterMondCore.getMaterial("ankuumakrabbe_panzer")==null)
//			(new BestienUndUngeheuerPlugin()).init();
//		if (SplitterMondCore.getCreatureModule("tiny")==null)
//			(new BeastMasterPlugin()).init();
	}

	//-------------------------------------------------------------------
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		events    = new ArrayList<GenerationEvent>();
		selectMasters = new HashMap<Skill, Mastership>();
		GenerationEventDispatcher.addListener(this);
	}

	//-------------------------------------------------------------------
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		GenerationEventDispatcher.removeListener(this);
	}

	//-------------------------------------------------------------------
	@Test
	public void testEmpty() {
		System.out.println("---------testEmpty-----------------------");
		generator = new CreatureGenerator(new ResourceReference(SplitterMondCore.getResource("creature"),2));

		ModuleBasedCreature model = generator.getCreature().getModuleBasedCreature();
		for (Attribute key : Attribute.primaryValues()) {
			try {
				assertEquals(0,model.getAttribute(key).getModifier());
				assertEquals(0,model.getAttribute(key).getDistributed());
				assertEquals(0,model.getAttribute(key).getValue());
			} catch (Exception e) {
				fail("Error accessing attribute "+key+": "+e);
			}
		}

		assertNotNull(SplitterMondCore.getCreatureModule("agile"));
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		events.add(event);
	}

	//-------------------------------------------------------------------
	@Test
	public void testEventMechanism() {
		System.out.println("---------testEventMechanism-----------------------");
		generator = new CreatureGenerator(new ResourceReference(SplitterMondCore.getResource("creature"),2));
		events.clear();
		generator.selectBase(SplitterMondCore.getCreatureModule("agile"));
		assertEquals(1, events.size());
		assertEquals(events.get(0).getType(), GenerationEventType.CREATURE_CHANGED);
		assertEquals(generator.getCreature(), events.get(0).getKey());
	}

	//-------------------------------------------------------------------
	@Test
	public void testSelectBase() {
		System.out.println("---------testSelectBase-----------------------");
		generator = new CreatureGenerator(new ResourceReference(SplitterMondCore.getResource("creature"),2));
		generator.selectBase(SplitterMondCore.getCreatureModule("agile"));
		ModuleBasedCreature model = generator.getCreature().getModuleBasedCreature();

		assertEquals(2,model.getAttribute(Attribute.CHARISMA).getValue());
		assertEquals(4,model.getAttribute(Attribute.AGILITY).getValue());
		assertNotNull(model.getCreatureFeature(SplitterMondCore.getCreatureFeatureType("WEAK")));

		assertNotNull( model.getCreatureWeapons() );
		assertEquals(1, model.getCreatureWeapons().size() );
		CreatureWeapon weapon = model.getCreatureWeapons().get(0);
		assertEquals(10600, weapon.getDamage());
		assertEquals( 7, weapon.getSpeed());
		
		
		System.out.println(model.dump());
	}

	//-------------------------------------------------------------------
	@Test
	public void testSelectRole() {
		System.out.println("---------testSelectRole-----------------------");
		Skill melee = SplitterMondCore.getSkill("melee");
		generator = new CreatureGenerator(new ResourceReference(SplitterMondCore.getResource("creature"),2));
		generator.selectBase(SplitterMondCore.getCreatureModule("agile"));
		generator.selectRole(SplitterMondCore.getCreatureModule("combatanimal"));
		ModuleBasedCreature model = generator.getCreature().getModuleBasedCreature();

		// Now ensure choices
		List<CreatureModuleReference.NecessaryChoice> choices = generator.getChoicesToMake();
		assertEquals(1, choices.size());
		
		// No choice for a melee mastership yet
		assertTrue(model.getSkillValue(melee).getMasterships().isEmpty());
		
		// No select
		generator.makeChoice(choices.get(0), new MastershipModification(melee.getMastership("push")));

		assertEquals(2,model.getAttribute(Attribute.CHARISMA).getValue());
		assertEquals(18,model.getAttribute(Attribute.DEFENSE).getValue());
		assertEquals(9,model.getCreatureWeapons().get(0).getValue());
		assertNull(model.getCreatureFeature(SplitterMondCore.getCreatureFeatureType("COWARD")));
		assertEquals(9,model.getSkillValue(SplitterMondCore.getSkill("athletics")).getModifiedValue());

		CreatureWeapon weapon = model.getCreatureWeapons().get(0);
		assertEquals(10601, weapon.getDamage());
		assertEquals( 6, weapon.getSpeed());

		assertTrue(generator.canBeFinished());
		System.out.println(model.dump());
	}

	//-------------------------------------------------------------------
	@Test
	public void testSelectOption() {
		System.out.println("---------testSelectOption-----------------------");
		// Prepare choices
		Mastership toSelect1 = SplitterMondCore.getSkill("melee").getMasterships().get(0);
		selectMasters.put(SplitterMondCore.getSkill("melee"), toSelect1);
		Mastership toSelect2 = SplitterMondCore.getSkill("athletics").getMasterships().get(0);
		selectMasters.put(SplitterMondCore.getSkill("acrobatics"), toSelect2);

		generator = new CreatureGenerator(new ResourceReference(SplitterMondCore.getResource("creature"),3));
		generator.selectBase(SplitterMondCore.getCreatureModule("agile"));
		generator.selectRole(SplitterMondCore.getCreatureModule("combatanimal"));
		generator.selectOption(SplitterMondCore.getCreatureModule("big"));
		ModuleBasedCreature model = generator.getCreature().getModuleBasedCreature();
		assertEquals(1, model.getOptions().size());
		
		// There should be 3 necessary choices
		List<CreatureModuleReference.NecessaryChoice> choices = generator.getChoicesToMake();
		assertNotNull(choices);
		assertEquals(2, choices.size());
		assertEquals("combatanimal",choices.get(0).originModule.getModule().getId());
		assertEquals("big",choices.get(1).originModule.getModule().getId());
//		assertEquals("big",choices.get(2).originModule.getModule().getId());
		
		generator.makeChoice(choices.get(0), new MastershipModification(toSelect1));
		generator.makeChoice(choices.get(1), new MastershipModification(toSelect2));
//		generator.makeChoice(choices.get(2), new CreatureFeatureModification(SplitterMondCore.getCreatureFeatureType("WEAK"), true));
		
		
		assertEquals(2,model.getAttribute(Attribute.CHARISMA).getValue());
		assertEquals(18,model.getAttribute(Attribute.DEFENSE).getValue());
		assertEquals(11,model.getCreatureWeapons().get(0).getValue());
		assertNull(model.getCreatureFeature(SplitterMondCore.getCreatureFeatureType("COWARD")));
		assertEquals(11,model.getSkillValue(SplitterMondCore.getSkill("athletics")).getModifiedValue());
		assertTrue(model.getSkillValue(SplitterMondCore.getSkill("athletics")).hasMastership(toSelect2));
		assertNull(model.getCreatureFeature(SplitterMondCore.getCreatureFeatureType("LOWLIGHT")));
		assertNull(model.getCreatureFeature(SplitterMondCore.getCreatureFeatureType("WEAK")));
		

		CreatureWeapon weapon = model.getCreatureWeapons().get(0);
		assertEquals(10603, weapon.getDamage());
		assertEquals( 7, weapon.getSpeed());

		assertEquals(0, generator.getAvailableCreaturePoints());
		assertTrue(generator.canBeFinished());
		System.out.println(model.dump());
	}

	//-------------------------------------------------------------------
	@Test
	public void testSelectFairy() {
		System.out.println("---------testSelectFairy-----------------------");
		// Prepare choices
		Mastership toSelect1 = SplitterMondCore.getSkill("melee").getMasterships().get(0);
		selectMasters.put(SplitterMondCore.getSkill("melee"), toSelect1);

		generator = new CreatureGenerator(new ResourceReference(SplitterMondCore.getResource("creature"),3));
		generator.selectBase(SplitterMondCore.getCreatureModule("agile"));
		generator.selectRole(SplitterMondCore.getCreatureModule("combatanimal"));
		generator.selectOption(SplitterMondCore.getCreatureModule("fairybeing"));
		ModuleBasedCreature model = generator.getCreature().getModuleBasedCreature();
		
		// There should be 4 necessary choices
		List<CreatureModuleReference.NecessaryChoice> choices = generator.getChoicesToMake();
		assertNotNull(choices);
		System.err.println("Choices = "+choices);
		assertEquals(3, choices.size());
		assertEquals("combatanimal",choices.get(0).originModule.getModule().getId());
		assertEquals("fairybeing",choices.get(1).originModule.getModule().getId());
		assertEquals("fairybeing",choices.get(2).originModule.getModule().getId());
		
		generator.makeChoice(choices.get(0), new MastershipModification(toSelect1));
//		generator.makeChoice(choices.get(1), new MastershipModification(toSelect));
//		generator.makeChoice(choices.get(2), new CreatureFeatureModification(SplitterMondCore.getCreatureFeatureType("WEAK"), true));

		assertEquals(3,model.getAttribute(Attribute.CHARISMA).getValue());
		assertEquals(19,model.getAttribute(Attribute.DEFENSE).getValue());
		assertEquals(10,model.getSkillValue(SplitterMondCore.getSkill("athletics")).getModifiedValue());
		assertEquals(10,model.getCreatureWeapons().get(0).getValue());
		assertTrue(model.getSkillValue(SplitterMondCore.getSkill("melee")).hasMastership(toSelect1));
		assertNull(model.getSkillValue(SplitterMondCore.getSkill("alchemy")));

		assertEquals(10,model.getSkillValue(SplitterMondCore.getSkill("athletics")).getModifiedValue());
		assertEquals(0, generator.getAvailableCreaturePoints());
		assertTrue(generator.canBeFinished());
		System.out.println(model.dump());
	}

	//-------------------------------------------------------------------
	@Test
	public void testSelectTinyFairyFamiliar() {
		System.out.println("---------testSelectTinyFairyFamiliar-----------------------");
		// Prepare choices
		Mastership toSelect = SplitterMondCore.getSkill("acrobatics").getMasterships().get(0);
		selectMasters.put(SplitterMondCore.getSkill("acrobatics"), toSelect);

		generator = new CreatureGenerator(new ResourceReference(SplitterMondCore.getResource("creature"),1));
		generator.selectBase(SplitterMondCore.getCreatureModule("agile"));
		assertEquals( 8,generator.getCreature().getModuleBasedCreature().getSkillValue(SplitterMondCore.getSkill("athletics")).getModifiedValue());
		generator.selectRole(SplitterMondCore.getCreatureModule("familiar"));
		assertEquals( 8,generator.getCreature().getModuleBasedCreature().getSkillValue(SplitterMondCore.getSkill("athletics")).getModifiedValue());
		generator.selectOption(SplitterMondCore.getCreatureModule("tiny"));
		assertEquals( 4,generator.getCreature().getModuleBasedCreature().getSkillValue(SplitterMondCore.getSkill("athletics")).getModifiedValue());
		generator.selectOption(SplitterMondCore.getCreatureModule("fairybeing"));
		ModuleBasedCreature model = generator.getCreature().getModuleBasedCreature();

		assertEquals( 3,model.getAttribute(Attribute.CHARISMA).getValue());
		assertEquals(14,model.getAttribute(Attribute.DEFENSE).getValue());
		assertEquals( 5,model.getSkillValue(SplitterMondCore.getSkill("athletics")).getModifiedValue());

		assertEquals(0, generator.getAvailableCreaturePoints());
		assertTrue(generator.canBeFinished());
		System.out.println(model.dump());
	}

	//-------------------------------------------------------------------
	@Test
	public void testSelectBigCombatAnimal() {
		System.out.println("---------testSelectBigCombatAnimal-----------------------");
		// Prepare choices
		Mastership toSelect = SplitterMondCore.getSkill("acrobatics").getMasterships().get(0);
		selectMasters.put(SplitterMondCore.getSkill("acrobatics"), toSelect);

		generator = new CreatureGenerator(new ResourceReference(SplitterMondCore.getResource("creature"),3));
		generator.selectBase(SplitterMondCore.getCreatureModule("fast"));
		generator.selectRole(SplitterMondCore.getCreatureModule("combatanimal"));
		generator.selectOption(SplitterMondCore.getCreatureModule("big"));
		ModuleBasedCreature model = generator.getCreature().getModuleBasedCreature();

		assertEquals( 2,model.getAttribute(Attribute.CHARISMA).getValue());
		assertEquals( 2,model.getAttribute(Attribute.STRENGTH).getValue());
		assertEquals(18,model.getAttribute(Attribute.DEFENSE).getValue());
		assertEquals(11,model.getCreatureWeapons().get(0).getValue());
		assertEquals(10,model.getSkillValue(SplitterMondCore.getSkill("athletics")).getModifiedValue());
		assertNull(model.getCreatureFeature(SplitterMondCore.getCreatureFeatureType("COWARD")));
		assertNull(model.getCreatureFeature(SplitterMondCore.getCreatureFeatureType("WEAK")));

		assertEquals(0, generator.getAvailableCreaturePoints());
		assertTrue(generator.canBeFinished());
		System.out.println(model.dump());
	}

	//-------------------------------------------------------------------
	@Test
	public void testModuleWithConditionalModification() {
		System.out.println("---------testModuleWithConditionalModification-----------------------");

		generator = new CreatureGenerator(new ResourceReference(SplitterMondCore.getResource("creature"),3));
		generator.selectOption(SplitterMondCore.getCreatureModule("undead"));
		ModuleBasedCreature model = generator.getCreature().getModuleBasedCreature();

		CreatureFeature scary = model.getCreatureFeature(SplitterMondCore.getCreatureFeatureType("SCARY"));
		assertNotNull("Creature should be scary", scary);
		assertEquals(10, scary.getLevel());
		
		generator.selectOption(SplitterMondCore.getCreatureModule("threatening"));
		model = generator.getCreature().getModuleBasedCreature();
		scary = model.getCreatureFeature(SplitterMondCore.getCreatureFeatureType("SCARY"));
		assertNotNull("Creature should be scary", scary);
		assertEquals(16, scary.getLevel());
	}

//	//--------------------------------------------------------------------
//	/**
//	 * @see org.prelle.splimo.chargen.LetUserChooseListener#letUserChoose(java.lang.String, org.prelle.splimo.modifications.ModificationChoice)
//	 */
//	@Override
//	public Modification[] letUserChoose(String choiceReason,
//			ModificationChoice choice) {
//		System.out.println("letUserChoose: because '"+choiceReason+"' choose "+choice);
//		System.out.println("letUserChoose: final = "+choice.getOptions()[0]);
//		
//		if (!filter.isEmpty()) {
//			System.out.println("Apply filter");
//			ModificationChoice filtered = new ModificationChoice(choice.getNumberOfChoices());
//			filtered.setDistribute(choice.getDistribute());
//			filtered.setValues(choice.getValues());
//			
//			List<Modification> toTest = new ArrayList<>(choice.getOptionList());
//			for (Predicate<Modification> tmp : filter) {
//				for (Modification mod : new ArrayList<>(toTest)) {
//					if (!tmp.test(mod)) {
//						System.out.println("  Remove "+mod+" from options");
//						toTest.remove(mod);
//					}
//				}
//			}
//			for (Modification tmp : toTest)
//				filtered.add(tmp);
//			choice = filtered;
//		}
//
//		if (choice.getOptions().length==0)
//			return new Modification[0];
//		return new Modification[]{choice.getOptions()[0]};
//	}
//
//	//--------------------------------------------------------------------
//	/**
//	 * @see org.prelle.splimo.chargen.LetUserChooseListener#letUserChoose(java.lang.String, org.prelle.splimo.modifications.MastershipModification)
//	 */
//	@Override
//	public MastershipModification letUserChoose(String choiceReason, MastershipModification vagueMod) {
//		System.err.println("letUserChoose: because '"+choiceReason+"' choose "+vagueMod);
//		if (vagueMod.getMastership()!=null) {
//			return vagueMod;
//		}
//		if (vagueMod.getSkill()!=null) {
//			Mastership toSelect = selectMasters.get(vagueMod.getSkill());
//			if (toSelect==null) {
//				System.err.println("No selection prepared for mastership in "+vagueMod.getSkill());
//			}
//			System.err.println("letUserChoose: final = "+new MastershipModification(toSelect));
//			return new MastershipModification(toSelect);
//		}
//		if (vagueMod.getLevel()>0) {
//			Mastership toSelect = selectMasters.get(ANY_SKILL);
//			System.err.println("letUserChoose: final = "+new MastershipModification(toSelect));
//			return new MastershipModification(toSelect);
//		}
//		System.err.println("CreatureGenTest: TODO: letUserChoose "+vagueMod);
//		System.exit(0);
//		return null;
//	}
//
//	//-------------------------------------------------------------------
//	/**
//	 * @see org.prelle.splimo.chargen.LetUserChooseListener#addPrefilter(java.util.function.Predicate)
//	 */
//	@Override
//	public void addPrefilter(Predicate<Modification> toAdd) {
//		if (!filter.contains(toAdd))
//			filter.add(toAdd);
//	}

}
