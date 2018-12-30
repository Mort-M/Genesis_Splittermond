package org.prelle.splimo.chargen4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.prelle.splimo.DummyRulePlugin;
import org.prelle.splimo.Skill;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl4.NewSkillGenerator;
import org.prelle.splimo.charctrl4.SplitterEngineCharacterGenerator;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.modifications.SkillModification;

import de.rpgframework.genericrpg.modification.DecisionToMake;
import de.rpgframework.genericrpg.modification.Modification;

public class SkillGeneratorTest {
	
	private final static int MAX = 10;
	
	private static Skill FIREMAGIC;
	private static Skill ATHLETICS;
	
	private SpliMoCharacter testModel;
	private NewSkillGenerator skillGen;
	private SplitterEngineCharacterGenerator parent;
	private List<Modification> previous;
	
	//-------------------------------------------------------------------
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PropertyConfigurator.configure(ClassLoader.getSystemResource("log4j.properties"));
		SplitterMondCore.initialize(new DummyRulePlugin<SpliMoCharacter>());
		FIREMAGIC = SplitterMondCore.getSkill("firemagic");
		ATHLETICS = SplitterMondCore.getSkill("athletics");
	}

	//-------------------------------------------------------------------
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		previous = new ArrayList<>();
		testModel  = new SpliMoCharacter();
		testModel.setExperienceFree(15);
		parent = new SplitterEngineCharacterGenerator() {
			{this.model = testModel;}
			public SpliMoCharacter getModel() { return testModel; }
			public List<DecisionToMake> getDecisionsToMake() { return new ArrayList<>(); }
			public void decide(DecisionToMake choice, List<Modification> choosen) {}
			public void runProcessors() {
				model.setExperienceFree(15);
				model.setExperienceInvested(0);
				skillGen.process(model, previous);
			}
		};
		GenerationEventDispatcher.clear();
		skillGen = new NewSkillGenerator(parent, MAX);
	}

	//-------------------------------------------------------------------
	@Test
	public void testIdle() {
//		assertEquals(0, skillGen.getPointsLeft());
//		assertTrue(skillGen.getToDos().isEmpty());
//		skillGen.process(testModel, new ArrayList<>());
		assertEquals(10, skillGen.getPointsLeft());
		assertFalse(skillGen.getToDos().isEmpty());
		assertEquals(15, testModel.getExperienceFree());
		
		assertEquals(0, testModel.getSkillValue(FIREMAGIC).getModifier());
		assertEquals(0, testModel.getSkillValue(FIREMAGIC).getValue());
		assertEquals(0, testModel.getSkillValue(FIREMAGIC).getModifiedValue());
	}

	//-------------------------------------------------------------------
	@Test
	public void testIncreasing() {
		SkillValue key = testModel.getSkillValue(FIREMAGIC);
		assertTrue("Cannot increase skill at all", skillGen.canBeIncreased(key));
		assertTrue(skillGen.increase(key)); // 1
		assertTrue(skillGen.increase(key)); // 2
		assertTrue(skillGen.increase(key)); // 3
		assertTrue(skillGen.increase(key)); // 4
		assertTrue(skillGen.increase(key)); // 5
		assertTrue(skillGen.increase(key)); // 6
		assertFalse(skillGen.canBeIncreased(key));
		assertFalse(skillGen.increase(key)); // 6
		assertEquals(15, testModel.getExperienceFree());
		assertEquals(0, testModel.getSkillValue(FIREMAGIC).getModifier());
		assertEquals(6, testModel.getSkillValue(FIREMAGIC).getValue());
		assertEquals(6, testModel.getSkillValue(FIREMAGIC).getModifiedValue());
		// There are 4 points left to increase ATHLETICS, the next need to payed with exp
		key = testModel.getSkillValue(ATHLETICS);
		assertTrue("Cannot increase skill at all", skillGen.canBeIncreased(key));
		assertTrue(skillGen.increase(key)); // 1
		assertTrue(skillGen.increase(key)); // 2
		assertTrue(skillGen.increase(key)); // 3
		assertTrue(skillGen.increase(key)); // 4
		assertEquals(15, testModel.getExperienceFree());
		assertEquals(0, skillGen.getPointsLeft());
		// Now pay with exp
		assertTrue("Increasing with exp not possible", skillGen.canBeIncreased(key));
		assertTrue(skillGen.increase(key)); // 5
		assertEquals(12, testModel.getExperienceFree());
		assertEquals(0, skillGen.getPointsLeft());

		// Reducing FIREMAGIC should mean not paying exp
		key = testModel.getSkillValue(FIREMAGIC);
		assertTrue(skillGen.canBeDecreased(key));
		assertTrue(skillGen.decrease(key));
		assertEquals(15, testModel.getExperienceFree());
		assertEquals(0, skillGen.getPointsLeft());
	}

	//-------------------------------------------------------------------
	@Test
	public void testIncreasingWithModifications() {
		SkillModification mod = new SkillModification(FIREMAGIC, 4);
		previous.add(mod);
		skillGen.process(testModel, previous);
		assertEquals(4, testModel.getSkillValue(FIREMAGIC).getModifier());
		assertEquals(0, testModel.getSkillValue(FIREMAGIC).getValue());
		assertEquals(4, testModel.getSkillValue(FIREMAGIC).getModifiedValue());
		assertEquals(MAX-4, skillGen.getPointsLeft());
		
		SkillValue key = testModel.getSkillValue(FIREMAGIC);
		assertTrue(skillGen.increase(key)); // 5
		assertTrue(skillGen.increase(key)); // 6
		assertFalse(skillGen.canBeIncreased(key));
		assertFalse(skillGen.increase(key)); // 6
		assertEquals(15, testModel.getExperienceFree());
		assertEquals(4, testModel.getSkillValue(FIREMAGIC).getModifier());
		assertEquals(2, testModel.getSkillValue(FIREMAGIC).getValue());
		assertEquals(6, testModel.getSkillValue(FIREMAGIC).getModifiedValue());
		assertEquals(MAX-6, skillGen.getPointsLeft());
		assertFalse(skillGen.canBeIncreased(key));
		
		// After removing the modification, increasing should be possible
		previous.clear();
		skillGen.process(testModel, previous);
		assertEquals(0, testModel.getSkillValue(FIREMAGIC).getModifier());
		assertEquals(2, testModel.getSkillValue(FIREMAGIC).getValue());
		assertEquals(2, testModel.getSkillValue(FIREMAGIC).getModifiedValue());
		assertEquals(8, skillGen.getPointsLeft());
		assertTrue(skillGen.canBeIncreased(key));
		assertTrue(skillGen.increase(key)); // 3
		assertTrue(skillGen.increase(key)); // 4
		assertTrue(skillGen.increase(key)); // 5
		assertTrue(skillGen.increase(key)); // 6
		assertEquals(0, testModel.getSkillValue(FIREMAGIC).getModifier());
		assertEquals(6, testModel.getSkillValue(FIREMAGIC).getValue());
		assertEquals(6, testModel.getSkillValue(FIREMAGIC).getModifiedValue());
		assertEquals(MAX-6, skillGen.getPointsLeft());

		// Now adding the +4 modification should reduce the points to 2
		previous.add(mod);
		skillGen.process(testModel, previous);
		assertEquals(4, testModel.getSkillValue(FIREMAGIC).getModifier());
		assertEquals(2, testModel.getSkillValue(FIREMAGIC).getValue());
		assertEquals(6, testModel.getSkillValue(FIREMAGIC).getModifiedValue());
		assertEquals(MAX-6, skillGen.getPointsLeft());
	}

	//-------------------------------------------------------------------
	@Test
	public void testDecreaseModification() {
		SkillModification mod = new SkillModification(FIREMAGIC, 4);
		previous.add(mod);
		skillGen.process(testModel, previous);
		
		assertTrue(skillGen.canBeDecreased(testModel.getSkillValue(FIREMAGIC)));
		assertTrue(skillGen.decrease(testModel.getSkillValue(FIREMAGIC)));
		assertEquals(4, testModel.getSkillValue(FIREMAGIC).getModifier());
		assertEquals(-1, testModel.getSkillValue(FIREMAGIC).getValue());
		assertEquals(3, testModel.getSkillValue(FIREMAGIC).getModifiedValue());
		assertEquals(MAX-3, skillGen.getPointsLeft());
	}
	
}
