package org.prelle.splimo.chargen4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.prelle.rpgframework.splittermond.data.SplittermondDataPlugin;
import org.prelle.splimo.Mastership;
import org.prelle.splimo.Skill;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl4.NewMastershipGenerator;
import org.prelle.splimo.charctrl4.SplitterEngineCharacterGenerator;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.modifications.MastershipModification;

import de.rpgframework.genericrpg.modification.DecisionToMake;
import de.rpgframework.genericrpg.modification.Modification;

public class MastershipGeneratorTest {
	
	private final static int MAX_POINTS = 3;
	
	private SpliMoCharacter testModel;
	private NewMastershipGenerator gen;
	private SplitterEngineCharacterGenerator parent;
	private List<Modification> previous;

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
		previous = new ArrayList<>();
		testModel  = new SpliMoCharacter();
		testModel.setExperienceFree(15);
		parent = new SplitterEngineCharacterGenerator() {
			{this.model = testModel;}
			public SpliMoCharacter getModel() { return testModel; }
			public List<DecisionToMake> getDecisionsToMake() { return new ArrayList<>(); }
			public void decide(DecisionToMake choice, List<Modification> choosen) {}
			public void runProcessors() {
				model.setExperienceFree(5);
				model.setExperienceInvested(0);
				gen.process(model, previous);
			}
		};
		GenerationEventDispatcher.clear();
		gen = new NewMastershipGenerator(parent, MAX_POINTS);
	}

	//-------------------------------------------------------------------
	@Test
	public void testIdleState() {
		gen.process(testModel, previous);
		assertEquals(MAX_POINTS, gen.getPointsLeft());
	}

	//-------------------------------------------------------------------
	@Test
	public void testManualSelection() {
		gen = new NewMastershipGenerator(parent, 0);
		// Should not be able to select anything without a skill value of 6
		Skill skill = SplitterMondCore.getSkill("combatmagic");
		Mastership master  = skill.getMastership("aimedspells");
		Mastership master2 = skill.getMastership("savingcaster");
		
		assertFalse(gen.canBeSelected(master));
		assertNull(gen.select(master));
		
		// Increase skill to 1 (sufficient for totally free masterships, but not for bought ones)
		testModel.getSkillValue(skill).setValue(1);
		gen.process(testModel, previous);
		assertFalse(gen.canBeSelected(master));
		assertNull(gen.select(master));
		
		// Increase skill to 6
		testModel.getSkillValue(skill).setValue(6);
		gen.process(testModel, previous);
		assertTrue(gen.canBeSelected(master));
		assertNotNull(gen.select(master));
		// Another mastership should fail due to missing exp
		testModel.setExperienceFree(0);
		assertFalse(gen.canBeSelected(master2));
		assertNull(gen.select(master2));
		// With 5 EXP it should work
		testModel.setExperienceFree(5);
		assertTrue(gen.canBeSelected(master2));
		assertNotNull(gen.select(master2));
		assertEquals(0, testModel.getExperienceFree());
		
		// Now deselecting the first (free) selected mastership should return the EXP
		assertTrue(gen.deselect(master));
		assertEquals(5, testModel.getExperienceFree());
	}

	//-------------------------------------------------------------------
	@Test
	public void testSelectByModification() {
		Skill skill = SplitterMondCore.getSkill("combatmagic");
		Mastership master1 = skill.getMastership("aimedspells");
		assertEquals(MAX_POINTS, gen.getPointsLeft());
		
		// Should not be possible to select it, when skill value is 0
		MastershipModification mod = new MastershipModification(master1);
		previous.add(mod);
		gen.process(testModel, previous);
		assertEquals(MAX_POINTS, gen.getPointsLeft());

		// With skill value 1, it should work
		testModel.getSkillValue(skill).setValue(1);
		gen.process(testModel, previous);
		assertEquals(MAX_POINTS-1, gen.getPointsLeft());
	}

	//-------------------------------------------------------------------
	@Test
	public void testDeselectByModification() {
		Skill skill = SplitterMondCore.getSkill("combatmagic");
		Mastership master1 = skill.getMastership("aimedspells");
		
		testModel.getSkillValue(skill).setValue(1);
		gen.process(testModel, previous);
		MastershipModification mod = new MastershipModification(master1);
		previous.add(mod);
		gen.process(testModel, previous);
		assertEquals(MAX_POINTS-1, gen.getPointsLeft());

		// Now deselect mastership
		assertTrue(gen.canBeDeselected(master1));
		assertTrue(gen.deselect(master1));
		assertEquals("System given mastership not removable", 0, testModel.getSkillValue(skill).getMasterships().size());
		assertEquals(MAX_POINTS, gen.getPointsLeft());
	}
	
}
