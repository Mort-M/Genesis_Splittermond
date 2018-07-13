/**
 * 
 */
package org.prelle.splimo.levelling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.prelle.rpgframework.splittermond.SplittermondRules;
import org.prelle.splimo.Mastership;
import org.prelle.splimo.Skill;
import org.prelle.splimo.SkillSpecialization;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.modifications.MastershipModification;
import org.prelle.splittermond.genlvl.MastershipLevellerAndGenerator;

import de.rpgframework.RPGFrameworkLoader;
import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
@FixMethodOrder
public class MastershipControllerTest {

	private final static Logger logger = Logger.getLogger("junit.power");

	private static Skill skill;
	private static Mastership EVADE1;
	private static Mastership EVADE2;
	private static Mastership LVL1_B;
	private static Mastership LVL2_B;
	private static SkillSpecialization special1;
	private MastershipLevellerAndGenerator generator;
	private SpliMoCharacter model;

	//-------------------------------------------------------------------
	static {
		PropertyConfigurator.configure("log4j.properties");
		SplitterMondCore.initialize(new SplittermondRules());

		skill = SplitterMondCore.getSkill("acrobatics");
		EVADE1 = skill.getMastership("evade1");
		EVADE2 = skill.getMastership("evade2");
		LVL1_B = skill.getMastership("balance");
		LVL2_B = skill.getMastership("roll");
		special1 = skill.getSpecialization("stunts");
	}

	//-------------------------------------------------------------------
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		model = new SpliMoCharacter();
		generator = new MastershipLevellerAndGenerator(3, model, new ArrayList<Modification>(), null, null);
	}

	//-------------------------------------------------------------------
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		GenerationEventDispatcher.clear();
	}


	//-------------------------------------------------------------------
	@Test
	public void testIdleState() {
		logger.debug("------testIdleState----------");
		assertEquals(0, generator.getFreeMasterships(skill));
		assertFalse(generator.isEditable(EVADE1));
		assertFalse(generator.isEditable(LVL1_B));
		assertFalse(generator.isEditable(EVADE2));
		assertFalse(generator.isEditable(LVL2_B));
		assertFalse(generator.isEditable(special1, 1));
		assertFalse(model.hasMastership(EVADE1));
		assertFalse(model.hasMastership(EVADE2));
		assertFalse(model.hasMastership(LVL1_B));
		assertFalse(model.hasMastership(LVL2_B));
	}

	//-------------------------------------------------------------------
	@Test
	public void testIdleStateSkillChanged() {
		logger.debug("------testIdleStateSkillChanged----------");
		generator = new MastershipLevellerAndGenerator(0, model, new ArrayList<Modification>(), null, null);
		
		// Raise skill to 5 - should have no influence
		model.getSkillValue(skill).setValue(5);
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.SKILL_CHANGED, skill, new int[]{4,5}));
		assertEquals(0, generator.getFreeMasterships(skill));
		assertFalse(generator.isEditable(EVADE1));
		assertFalse(generator.isEditable(LVL1_B));
		assertFalse(generator.isEditable(EVADE2));
		assertFalse(generator.isEditable(LVL2_B));
		assertFalse(generator.isEditable(special1, 1));
		assertFalse(model.hasMastership(EVADE1));
		assertFalse(model.hasMastership(EVADE2));
		assertFalse(model.hasMastership(LVL1_B));
		assertFalse(model.hasMastership(LVL2_B));
		
		// Raise skill to 6 - should give one free selection
		model.getSkillValue(skill).setValue(6);
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.SKILL_CHANGED, skill, new int[]{5,6}));
		assertEquals(1, generator.getFreeMasterships(skill));
		assertTrue(generator.isEditable(EVADE1));
		assertTrue(generator.isEditable(LVL1_B));
		assertFalse(generator.isEditable(EVADE2));
		assertFalse(generator.isEditable(LVL2_B));
		assertTrue(generator.isEditable(special1, 1));
		assertFalse(model.hasMastership(EVADE1));
		assertFalse(model.hasMastership(EVADE2));
		assertFalse(model.hasMastership(LVL1_B));
		assertFalse(model.hasMastership(LVL2_B));
		
		// Raise skill to 9 - should give two free selections
		model.getSkillValue(skill).setValue(9);
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.SKILL_CHANGED, skill, new int[]{8,9}));
		assertEquals(2, generator.getFreeMasterships(skill));
		assertTrue(generator.isEditable(EVADE1));
		assertTrue(generator.isEditable(LVL1_B));
		assertFalse(generator.isEditable(EVADE2));
		assertFalse(generator.isEditable(LVL2_B));
		assertTrue(generator.isEditable(special1, 1));
		assertFalse(model.hasMastership(EVADE1));
		assertFalse(model.hasMastership(EVADE2));
		assertFalse(model.hasMastership(LVL1_B));
		assertFalse(model.hasMastership(LVL2_B));
		
		// Lower skill to 8 - should loose one free selection
		model.getSkillValue(skill).setValue(6);
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.SKILL_CHANGED, skill, new int[]{9,8}));
		assertEquals(1, generator.getFreeMasterships(skill));
		assertTrue(generator.isEditable(EVADE1));
		assertTrue(generator.isEditable(LVL1_B));
		assertFalse(generator.isEditable(EVADE2));
		assertFalse(generator.isEditable(LVL2_B));
		assertTrue(generator.isEditable(special1, 1));
		assertFalse(model.hasMastership(EVADE1));
		assertFalse(model.hasMastership(EVADE2));
		assertFalse(model.hasMastership(LVL1_B));
		assertFalse(model.hasMastership(LVL2_B));
	}

	//-------------------------------------------------------------------
	@Test
	public void testIdleOneFree() {
		logger.debug("------testIdleOneFree----------");
		generator.addModification(new MastershipModification(skill, 1));
		model.getSkillValue(skill).setValue(1);
		assertEquals(1, generator.getFreeMasterships(skill));
		assertFalse(model.hasMastership(EVADE1));
		assertFalse(model.hasMastership(EVADE2));
		assertFalse(model.hasMastership(LVL1_B));
		assertFalse(model.hasMastership(LVL2_B));
		assertTrue(generator.isEditable(EVADE1));
		assertTrue(generator.isEditable(LVL1_B));
		assertFalse(generator.isEditable(EVADE2));
		assertFalse(generator.isEditable(LVL2_B));		
		assertTrue(generator.isEditable(special1, 1));
	}
	
	//-------------------------------------------------------------------
	@Test
	public void testLevel1Free() {
		logger.debug("------testLevel1Free----------");
		generator = new MastershipLevellerAndGenerator(1, model, new ArrayList<Modification>(), null, null);
		model.getSkillValue(skill).setValue(1);
//		generator.addModification(new MastershipModification(skill, 1));

		assertTrue(generator.select(EVADE1));
		// Free selection used on EVADE1, more should not be possible
		assertEquals(0, generator.getFreeMasterships(skill));
		assertTrue(model.hasMastership(EVADE1));
		assertFalse(model.hasMastership(EVADE2));
		assertFalse(model.hasMastership(LVL1_B));
		assertFalse(model.hasMastership(LVL2_B));
		assertFalse(model.getSkillSpecializationLevel(special1)>0);
		
		assertTrue("Not undoable",generator.isEditable(EVADE1));
		assertFalse(generator.isEditable(EVADE2));
		assertFalse(generator.isEditable(LVL1_B));
		assertFalse(generator.isEditable(LVL2_B));
		assertFalse(generator.isEditable(special1, 1));

		
		// Revert selection
		assertTrue(generator.deselect(EVADE1));
		assertEquals(1, generator.getFreeMasterships());
		assertFalse(model.hasMastership(EVADE1));
		assertFalse(model.hasMastership(EVADE2));
		assertFalse(model.hasMastership(LVL1_B));
		assertFalse(model.hasMastership(LVL2_B));
		assertTrue(generator.isEditable(EVADE1));
		assertTrue(generator.isEditable(LVL1_B));
		assertFalse(generator.isEditable(EVADE2));
		assertFalse(generator.isEditable(LVL2_B));		
		assertTrue(generator.isEditable(special1, 1));
	}
	
	//-------------------------------------------------------------------
	@Test
	public void testLevel1() {
		logger.debug("------testLevel1----------");
		model.setExperienceFree(15);
		model.getSkillValue(skill).setValue(1);
		assertTrue(generator.isEditable(EVADE1));

		MastershipModification mod = new MastershipModification(EVADE1);
		
		generator.addModification(mod);

		assertTrue(model.hasMastership(EVADE1));
		assertTrue(generator.isEditable(EVADE1));
		
		generator.removeModification(mod);
		assertFalse(model.hasMastership(EVADE1));
		assertTrue(generator.isEditable(EVADE1));
	}
	
	//-------------------------------------------------------------------
	@Test
	public void testLevel1SpecialFree() {
		logger.debug("------testLevel1SpecialFree----------");
		generator = new MastershipLevellerAndGenerator(1, model, new ArrayList<Modification>(), null, null);
//		generator.addModification(new MastershipModification(skill, 1));
		model.getSkillValue(skill).setValue(1);

		assertTrue(generator.select(special1, 1));
		// Free selection used on EVADE1, more should not be possible
		assertEquals(0, generator.getFreeMasterships(skill));
		assertFalse(model.hasMastership(EVADE1));
		assertFalse(model.hasMastership(EVADE2));
		assertFalse(model.hasMastership(LVL1_B));
		assertFalse(model.hasMastership(LVL2_B));
		assertTrue(model.getSkillSpecializationLevel(special1)>0);
		
		assertTrue("Not undoable",generator.isEditable(special1, 1));
		assertFalse(generator.isEditable(EVADE2));
		assertFalse(generator.isEditable(LVL1_B));
		assertFalse(generator.isEditable(LVL2_B));
		assertFalse(generator.isEditable(EVADE1));

		
		// Revert selection
		assertEquals(0, generator.getFreeMasterships(skill));
		assertTrue(generator.deselect(special1, 1));
		assertEquals(1, generator.getFreeMasterships());
		assertFalse("Deselection failed",model.getSkillSpecializationLevel(special1)>0);
		assertFalse(model.hasMastership(EVADE1));
		assertFalse(model.hasMastership(EVADE2));
		assertFalse(model.hasMastership(LVL1_B));
		assertFalse(model.hasMastership(LVL2_B));
		assertTrue(generator.isEditable(EVADE1));
		assertTrue(generator.isEditable(LVL1_B));
		assertFalse(generator.isEditable(EVADE2));
		assertFalse(generator.isEditable(LVL2_B));		
		assertTrue(generator.isEditable(special1, 1));
	}
	
	//-------------------------------------------------------------------
	@Test
	public void testLevel1Special() {
		logger.debug("------testLevel1Special------------------------------------------");
		model.setExperienceFree(15);
		model.getSkillValue(skill).setValue(1);
		assertTrue(generator.isEditable(special1,1));

		MastershipModification mod = new MastershipModification(special1,1);
		generator.addModification(mod);

		assertEquals(1,model.getSkillSpecializationLevel(special1));
		assertTrue(generator.isEditable(special1,1));
		
		generator.removeModification(mod);
		assertEquals(0,model.getSkillSpecializationLevel(special1));
		assertTrue(generator.isEditable(special1,1));
	}
	
	//-------------------------------------------------------------------
	@Test
	public void testRaiseAndLowerSkill() {
		logger.debug("------testRaiseAndLowerSkill----------");
		model.getSkillValue(skill).setValue(1);
		generator = new MastershipLevellerAndGenerator(1, model, new ArrayList<Modification>(), null, null);
		generator.addModification(new MastershipModification(skill, 5));

		generator.select(EVADE1);
		// Free selection used on EVADE1, more should not be possible
		assertEquals(0, generator.getFreeMasterships(skill));
		assertTrue(model.hasMastership(EVADE1));
		assertFalse(model.hasMastership(EVADE2));
		assertFalse(model.hasMastership(LVL1_B));
		assertFalse(model.hasMastership(LVL2_B));
		assertFalse(model.getSkillSpecializationLevel(special1)>0);
		
		assertTrue("Not undoable",generator.isEditable(EVADE1));
		assertFalse(generator.isEditable(EVADE2));
		assertFalse(generator.isEditable(LVL1_B));
		assertFalse(generator.isEditable(LVL2_B));
		assertFalse(generator.isEditable(special1, 1));

		
		// Revert selection
		generator.deselect(EVADE1);
		assertEquals(1, generator.getFreeMasterships(skill));
		assertFalse(model.hasMastership(EVADE1));
		assertFalse(model.hasMastership(EVADE2));
		assertFalse(model.hasMastership(LVL1_B));
		assertFalse(model.hasMastership(LVL2_B));
		assertTrue(generator.isEditable(EVADE1));
		assertTrue(generator.isEditable(LVL1_B));
		assertFalse(generator.isEditable(EVADE2));
		assertFalse(generator.isEditable(LVL2_B));		
		assertTrue(generator.isEditable(special1, 1));
	}
	
	//-------------------------------------------------------------------
	@Test
	public void testLevel2Special() {
		logger.debug("------testLevel2Special----------");
		model.setExperienceInvested(600);
		model.setLevel(3);
		model.getSkillValue(skill).setValue(12);
		generator = new MastershipLevellerAndGenerator(3, model, new ArrayList<Modification>(), null, null);
//		generator.addModification(new MastershipModification(skill, 1));
//		generator.addModification(new MastershipModification(skill, 2));
//		generator.addModification(new MastershipModification(skill, 3));
		assertTrue(generator.isEditable(special1, 1));
		assertFalse(generator.isEditable(special1, 2));
		assertFalse(generator.isEditable(special1, 3));

		assertTrue(generator.select(special1, 1));
		assertEquals(1, model.getSkillSpecializationLevel(special1));
		assertTrue(generator.isEditable(special1, 1));
		assertTrue(generator.isEditable(special1, 2));
		assertFalse(generator.isEditable(special1, 3));

		assertTrue(generator.select(special1, 2));
		assertEquals(2, model.getSkillSpecializationLevel(special1));
		assertFalse(generator.isEditable(special1, 1));
		assertTrue(generator.isEditable(special1, 2));
		assertTrue(generator.isEditable(special1, 3));
	}
	
	//-------------------------------------------------------------------
	@Test
	public void testLevel2Req() {
		logger.debug("------testLevel2Req----------");
		model.setExperienceInvested(140);
		model.setLevel(2);
		model.getSkillValue(skill).setValue(9);
		generator = new MastershipLevellerAndGenerator(1, model, new ArrayList<Modification>(), null, null);
		generator.addModification(new MastershipModification(skill, 1));
		generator.addModification(new MastershipModification(skill, 2));
		generator.addModification(new MastershipModification(skill, 3));
		assertTrue(generator.isEditable(EVADE1));
		assertFalse(generator.isEditable(EVADE2));

		assertTrue(generator.select(EVADE1));
		assertTrue(model.hasMastership(EVADE1));
		assertFalse(model.hasMastership(EVADE2));
		assertTrue(generator.isEditable(EVADE1));
		assertTrue(generator.isEditable(EVADE2));

		assertTrue(generator.select(EVADE2));
		assertTrue(model.hasMastership(EVADE1));
		assertTrue(model.hasMastership(EVADE2));
		assertFalse(generator.isEditable(EVADE1));
		assertTrue(generator.isEditable(EVADE2));

		assertTrue(generator.deselect(EVADE2));
		assertTrue(model.hasMastership(EVADE1));
		assertFalse(model.hasMastership(EVADE2));
		assertTrue(model.hasMastership(EVADE1));
		assertTrue(generator.isEditable(EVADE2));
	}

}
