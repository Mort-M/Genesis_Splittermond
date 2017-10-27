/**
 * 
 */
package org.prelle.splimo.levelling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Spell;
import org.prelle.splimo.SpellValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.SpellController;
import org.prelle.splimo.charctrl.SpellController.FreeSelection;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splittermond.genlvl.SpellLevellerAndGenerator;

import de.rpgframework.RPGFrameworkLoader;
import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class SpellControllerTest implements GenerationEventListener {

	private final static Logger logger = Logger.getLogger("junit");
	
	private static Skill HEALMAGIC;
	private static Skill ENHANCEMAGIC;
	
	private static Spell ENHANCECONST;
	private static Spell REFRESH;
	private static Spell DETECT_POISON;
	
	private SpellController control;
	private SpliMoCharacter model;
	private List<Modification> undoList;

	private List<GenerationEvent> events;
	
	//-------------------------------------------------------------------
	static {
//		ConsoleAppender console = new ConsoleAppender();
//		console.setLayout(new PatternLayout("%5p [%c] (%F:%L) - %m%n"));
//		console.setName("console");
//		console.setWriter(new OutputStreamWriter(System.out));
//		Logger.getRootLogger().addAppender(console);
//		Logger.getRootLogger().setLevel(Level.INFO);
//		
//		Logger.getLogger("splimo.level.spell").setLevel(Level.DEBUG);
//		Logger.getLogger("junit").setLevel(Level.DEBUG);

		PropertyConfigurator.configure("log4j.properties");
		RPGFrameworkLoader.getInstance();
		HEALMAGIC = SplitterMondCore.getSkill("healmagic");
		ENHANCEMAGIC = SplitterMondCore.getSkill("enhancemagic");
		
		ENHANCECONST = SplitterMondCore.getSpell("enhanceconst");
		REFRESH      = SplitterMondCore.getSpell("refresh");
		DETECT_POISON= SplitterMondCore.getSpell("detectpoison");
	}
	
	//--------------------------------------------------------------------
	public SpellControllerTest() {
		GenerationEventDispatcher.addListener(this);
	}
	
	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		logger.debug("RCV "+event);
		events.add(event);
	}


	//--------------------------------------------------------------------
	@Before
	public void setUp() throws Exception {
		model = new SpliMoCharacter();
		undoList = new ArrayList<Modification>();
		control = new SpellLevellerAndGenerator(model, undoList);
		events  = new ArrayList<GenerationEvent>();
	}

	//--------------------------------------------------------------------
	@After
	public void tearDown() throws Exception {
		GenerationEventDispatcher.removeListener((GenerationEventListener) control);
		GenerationEventDispatcher.removeListener(this);
	}

	//--------------------------------------------------------------------
	@Test
	public void testIdleNoExp() {
		logger.debug("testIdleNoExp----------------------------");
		assertTrue(control.getAvailableSpellSchools().isEmpty());
		assertTrue(control.getFreeSelections().isEmpty());
		assertTrue(model.getSpells().isEmpty());
	}

	//--------------------------------------------------------------------
	@Test
	public void testGetAvailableSpellSchools() {
		logger.debug("testGetAvailableSpellSchools----------------------------");
		
		// Increase school to 1
		model.getSkillValue(HEALMAGIC).setValue(1);
		// Ensure school is available
		assertEquals(1, control.getAvailableSpellSchools().size());
		assertTrue(control.getAvailableSpellSchools().contains(HEALMAGIC));
		
		// Increase a different school
		model.getSkillValue(ENHANCEMAGIC).setValue(1);
		// Ensure school is available
		assertEquals(2, control.getAvailableSpellSchools().size());
		assertTrue(control.getAvailableSpellSchools().contains(ENHANCEMAGIC));
		
		// Revoke point from first school
		model.getSkillValue(HEALMAGIC).setValue(0);
		// Ensure school is available
		assertEquals(1, control.getAvailableSpellSchools().size());
		assertTrue(control.getAvailableSpellSchools().contains(ENHANCEMAGIC));
	}

	//--------------------------------------------------------------------
	/**
	 * With 0 exp, set a school to 1, select a free spell. Afterwards the
	 * school should not be available any more 
	 */
	@Test
	public void testGetAvailableSpellSchoolsAfterSelection() {
		logger.debug("testGetAvailableSpellSchoolsAfterSelection----------------------------");
		
		// Increase school to 1
		model.getSkillValue(HEALMAGIC).setValue(1);

		List<SpellValue> avail = control.getAvailableSpells(HEALMAGIC);
		FreeSelection sel = control.canBeFreeSelected(avail.get(0));
		control.select(sel, avail.get(0));

		// Selection is done. School should not be offered anymore
		assertFalse(control.getAvailableSpellSchools().contains(HEALMAGIC));
		
	}

	//--------------------------------------------------------------------
	@Test
	public void testGetAvailableSpellsIdle() {
		logger.debug("testGetAvailableSpells----------------------------");
		
		// Increase school to 1
		model.getSkillValue(HEALMAGIC).setValue(1);
		
		List<SpellValue> avail = control.getAvailableSpells(HEALMAGIC);
		assertFalse(avail.isEmpty());
		assertTrue(control.getAvailableSpells(ENHANCEMAGIC).isEmpty());
		// Expect at least ENHANCECONST and REFRESH
		SpellValue expect1 = new SpellValue(ENHANCECONST, HEALMAGIC);
		SpellValue expect2 = new SpellValue(REFRESH     , HEALMAGIC);
		assertTrue(avail.contains(expect1));
		assertTrue(avail.contains(expect2));
		
		// Assume one spell is already selected
		model.addSpell(expect1);
		avail = control.getAvailableSpells(HEALMAGIC);
		assertFalse(avail.contains(expect1));
		assertTrue(avail.contains(expect2));
	}

	//--------------------------------------------------------------------
	/**
	 * With 0 exp, set a school to 1, select one spell for free and
	 * expect the list to be empty than
	 */
	@Test
	public void testGetAvailableSpellsAfterFreeSelection() {
		logger.debug("testGetAvailableSpellsAfterSelection----------------------------");
		
		// Increase school to 1
		model.getSkillValue(HEALMAGIC).setValue(1);
		
		List<SpellValue> avail = control.getAvailableSpells(HEALMAGIC);
		assertFalse(avail.isEmpty());
		// Select any level 0 spell
		FreeSelection sel = control.canBeFreeSelected(avail.get(0));
		control.select(sel, avail.get(0));
		
		// avail should be empty now
		avail = control.getAvailableSpells(HEALMAGIC);
		assertTrue(avail.isEmpty());
		
		/*
		 * Now do the same with a school value of 6. Select a level
		 * 2 spell and expect only level 0 or 1 spells to be offered
		 */
		model.getSkillValue(ENHANCEMAGIC).setValue(6);
		avail = control.getAvailableSpells(ENHANCEMAGIC);
		assertFalse(avail.isEmpty());
		// Select any level 2 spell
		SpellValue level2Spell = null;
		for (SpellValue val : avail) {
			if (val.getSpellLevel()==2) {
				level2Spell = val;
				break;
			}
		}
		assertNotNull("No level 2 spell offered", level2Spell);			
		
		sel = control.canBeFreeSelected(level2Spell);
		control.select(sel, avail.get(0));
		
		// avail should contain some level 0 and 1 spells now
		avail = control.getAvailableSpells(ENHANCEMAGIC);
		assertFalse(avail.isEmpty());
		for (SpellValue val : avail)
			assertTrue("Got offered spell with level>1: "+val,val.getSpellLevel()<2);
	}

	//--------------------------------------------------------------------
	@Test
	public void testSelect() {
		logger.debug("testSelect----------------------------");
		
		SpellValue spell = new SpellValue(ENHANCECONST, HEALMAGIC);
		
		// Increase school to 1 and sufficient exp
		model.getSkillValue(HEALMAGIC).setValue(1);
		model.setExperienceFree(1);
		// Try select
		assertFalse(control.canBeDeSelected(spell));
		assertTrue(control.canBeSelected(spell));
		assertTrue(control.select(spell));
		assertTrue(control.canBeDeSelected(spell));
		assertFalse(control.canBeSelected(spell));
	}

	//--------------------------------------------------------------------
	@Test
	public void testGetFreeSelections() {
		logger.debug("testGetFreeSelections----------------------------");
		
		model.getSkillValue(HEALMAGIC).setValue(1);
		model.getSkillValue(ENHANCEMAGIC).setValue(6);
		
		assertEquals(4, control.getFreeSelections().size());
		assertEquals(4, control.getUnusedFreeSelections().size());
		
	}

	//--------------------------------------------------------------------
	@Test
	public void testCanBeFreeSelected() {
		logger.debug("testCanBeFreeSelected----------------------------");
		
		model.getSkillValue(ENHANCEMAGIC).setValue(6);
		
		FreeSelection possToken = control.canBeFreeSelected(new SpellValue(ENHANCECONST, ENHANCEMAGIC));
		FreeSelection impossToken = control.canBeFreeSelected(new SpellValue(ENHANCECONST, HEALMAGIC));
		assertNotNull(possToken);
		assertNull(impossToken);
		
		assertEquals("Not token with lowest possible level returned",0, possToken.getLevel());
	}

	//--------------------------------------------------------------------
	@Test
	public void testSelectFree() {
		logger.debug("testSelectFree----------------------------");
		
		model.getSkillValue(ENHANCEMAGIC).setValue(1);
		model.getSkillValue(HEALMAGIC).setValue(1);
		
		SpellValue spell = new SpellValue(ENHANCECONST, ENHANCEMAGIC);
		FreeSelection token = control.canBeFreeSelected(spell);
		events.clear();
		control.select(token, spell);
		
		// Check for success
		assertFalse(model.getSpells().isEmpty());
		assertTrue(model.getSpells().contains(spell));
		assertEquals(0,spell.getFreeLevel());
		assertEquals(spell, token.getUsedFor());
		
		// Expect 2 events: SPELL_ADDED; SPELL_FREESELECTION_CHANGED, UNDOLIST
		assertEquals(3, events.size());
		
		// Check if lists are updated correctly
		assertEquals(1, control.getUnusedFreeSelections().size());
		assertEquals(2, control.getFreeSelections().size());
		
		
		/*
		 * Now select the same spell in a different school
		 */
		SpellValue spell2 = new SpellValue(ENHANCECONST, HEALMAGIC);
		FreeSelection token2 = control.canBeFreeSelected(spell2);
		events.clear();
		control.select(token2, spell2);

		// Check for success
		assertEquals(2, model.getSpells().size());
		assertTrue(model.getSpells().contains(spell2));
		assertEquals(0,spell2.getFreeLevel());
		assertEquals(spell2, token2.getUsedFor());
		
		// Check if lists are updated correctly
		assertEquals(0, control.getUnusedFreeSelections().size());
		assertEquals(2, control.getFreeSelections().size());
	}

	//--------------------------------------------------------------------
	@Test
	public void testGetPossibleSpells() {
		logger.debug("testGetPossibleSpells----------------------------");
		
		model.getSkillValue(HEALMAGIC).setValue(1);

		assertFalse(control.getUnusedFreeSelections().isEmpty());
		FreeSelection token = control.getUnusedFreeSelections().iterator().next();
		List<SpellValue> possible = control.getPossibleSpells(token);
		SpellValue expect1 = new SpellValue(ENHANCECONST, HEALMAGIC);
		SpellValue expect2 = new SpellValue(REFRESH     , HEALMAGIC);
		assertTrue(possible.contains(expect1));
		assertTrue(possible.contains(expect2));
		
		// Assume one was selected
		model.addSpell(expect1);
		possible = control.getPossibleSpells(token);
		assertFalse(possible.contains(expect1));
		assertTrue(possible.contains(expect2));
	}

	//--------------------------------------------------------------------
	@Test
	public void testSelectFreeThenReduce() {
		logger.debug("testSelectFreeThenReduce----------------------------");
		
		model.getSkillValue(HEALMAGIC).setValue(3);
		
		SpellValue spell0 = new SpellValue(REFRESH, HEALMAGIC);
		SpellValue spell1 = new SpellValue(DETECT_POISON, HEALMAGIC);
		FreeSelection token0 = control.canBeFreeSelected(spell0);
		FreeSelection token1 = control.canBeFreeSelected(spell1);
		// Select both spells
		control.select(token0, spell0);
		control.select(token1, spell1);
		assertEquals(0, control.getUnusedFreeSelections().size());
		assertEquals(2, control.getFreeSelections().size());
		assertTrue(model.hasSpell(spell0));
		assertTrue(model.hasSpell(spell1));
		events.clear();
		
		model.getSkillValue(HEALMAGIC).setValue(2);
		assertEquals(1, control.getFreeSelections().size());
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.SKILL_CHANGED, HEALMAGIC));
		assertTrue(model.hasSpell(spell0));
		assertFalse(model.hasSpell(spell1));
	}
	
}
