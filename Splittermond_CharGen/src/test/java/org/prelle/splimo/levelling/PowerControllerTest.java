/**
 * 
 */
package org.prelle.splimo.levelling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.prelle.rpgframework.splittermond.SplittermondRules;
import org.prelle.splimo.Power;
import org.prelle.splimo.PowerReference;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.PowerController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.modifications.PowerModification;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class PowerControllerTest implements GenerationEventListener {
	
	private static Power ONCE_GENONLY;
	private static Power ONCE_ALWAYS;
	private static Power MULTI_ALWAYS;
	private static Power MULTI_LEVEL;
	private static Power MULTI_MAX3;
	
	private PowerController control;
	private SpliMoCharacter model;
	private List<Modification> undoList;

	private List<GenerationEvent> events;

	//-------------------------------------------------------------------
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
//		ConsoleAppender console = new ConsoleAppender();
//		console.setLayout(new PatternLayout("%5p [%c] (%F:%L) - %m%n"));
//		console.setCustomName("console");
//		console.setWriter(new OutputStreamWriter(System.out));
//		Logger.getRootLogger().addAppender(console);
//		Logger.getRootLogger().setLevel(Level.INFO);
//		
//		Logger.getLogger("splimo.level.resource").setLevel(Level.DEBUG);
//		Logger.getLogger("junit").setLevel(Level.DEBUG);

		SplitterMondCore.initialize(new SplittermondRules());
		ONCE_GENONLY = SplitterMondCore.getPower("attractive");
		ONCE_ALWAYS = SplitterMondCore.getPower("socialable");
		MULTI_ALWAYS = SplitterMondCore.getPower("focuspool");
		MULTI_LEVEL = SplitterMondCore.getPower("resistbody");
		MULTI_MAX3 = SplitterMondCore.getPower("packmule");
		if (ONCE_GENONLY==null) throw new NullPointerException();
		if (ONCE_ALWAYS==null) throw new NullPointerException();
		if (MULTI_ALWAYS==null) throw new NullPointerException();
		if (MULTI_LEVEL==null) throw new NullPointerException();
		if (MULTI_MAX3==null) throw new NullPointerException();
	}
	
	//--------------------------------------------------------------------
	public PowerControllerTest() {
		events = new ArrayList<>();
		GenerationEventDispatcher.addListener(this);
	}
	
	//-------------------------------------------------------------------
	private boolean hasEvent(GenerationEventType type) {
		for (GenerationEvent ev : events)
			if (ev.getType()==type)
				return true;
		return false;
	}
	
	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		events.add(event);
	}


	//--------------------------------------------------------------------
	@Before
	public void setUp() throws Exception {
		model = new SpliMoCharacter();
		undoList = new ArrayList<Modification>();
		control = new PowerLeveller(model, undoList, new CharacterLeveller(model, null));
		events.clear();
	}

	//--------------------------------------------------------------------
	@After
	public void tearDown() {
		GenerationEventDispatcher.removeListener((PowerLeveller)control);
	}

	//--------------------------------------------------------------------
	@Test
	public void testIdleNoEXP() {
		assertFalse(control.canBeSelected(ONCE_GENONLY));
		assertFalse(control.getAvailablePowers().contains(ONCE_GENONLY));
		assertFalse(control.canBeSelected(ONCE_ALWAYS));
		assertFalse(control.getAvailablePowers().contains(ONCE_ALWAYS));
		assertFalse(control.canBeSelected(MULTI_ALWAYS));
		assertFalse(control.getAvailablePowers().contains(MULTI_ALWAYS));
		assertFalse(control.canBeSelected(MULTI_LEVEL));
		assertFalse(control.getAvailablePowers().contains(MULTI_LEVEL));
		assertFalse(control.canBeSelected(MULTI_MAX3));
		assertFalse(control.getAvailablePowers().contains(MULTI_MAX3));
	}

	//--------------------------------------------------------------------
	@Test
	public void testIdleWithEXP() {
		model.setExperienceFree(99);
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.EXPERIENCE_CHANGED, null, new int[]{
				model.getExperienceFree(),
				model.getExperienceInvested()
		}));
		assertFalse(control.canBeSelected(ONCE_GENONLY));
		assertFalse(control.getAvailablePowers().contains(ONCE_GENONLY));
		assertTrue(control.canBeSelected(ONCE_ALWAYS));
		assertTrue(control.getAvailablePowers().contains(ONCE_ALWAYS));
		assertTrue(control.canBeSelected(MULTI_ALWAYS));
		assertTrue(control.getAvailablePowers().contains(MULTI_ALWAYS));
		assertTrue(control.canBeSelected(MULTI_LEVEL));
		assertTrue(control.getAvailablePowers().contains(MULTI_LEVEL));
		assertTrue(control.canBeSelected(MULTI_MAX3));
		assertTrue(control.getAvailablePowers().contains(MULTI_MAX3));
	}

	//--------------------------------------------------------------------
	@Test
	public void selectNormal() {
		model.setExperienceFree(50);
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.EXPERIENCE_CHANGED, null, new int[]{
				model.getExperienceFree(),
				model.getExperienceInvested()
		}));
		events.clear();

		// Start test
		PowerReference ref = control.select(ONCE_ALWAYS);
		
		int expCost = ONCE_ALWAYS.getCost()*7;
		assertNotNull(ref);
		assertEquals(1, ref.getCount());
		assertTrue(model.hasPower(ONCE_ALWAYS));
		assertEquals(expCost, model.getExperienceInvested());
		// Check undo modification
		assertFalse(undoList.isEmpty());
		PowerModification mod = (PowerModification) undoList.get(0);
		assertEquals(mod.getPower(), ONCE_ALWAYS);
		assertEquals(expCost, mod.getExpCost());
		
		// Check for events: POWERS, EXPERIENCE, UNDO_LIST
		assertTrue(hasEvent(GenerationEventType.POWER_ADDED));
		assertTrue(hasEvent(GenerationEventType.EXPERIENCE_CHANGED));
		assertTrue(hasEvent(GenerationEventType.UNDO_LIST_CHANGED));

		// A single selectable power can not be increased or decreased
		assertFalse(control.canBeIncreased(ref));
		assertFalse(control.canBeDecreased(ref));
		
		// Is undo possible
		assertTrue(control.canBeDeselected(ref));
		
		// Undo
		events.clear();
		assertTrue(control.deselect(ref));

		assertFalse(model.hasPower(ONCE_ALWAYS));
		assertEquals(0, model.getExperienceInvested());
		
		// Check for events: POWERS, EXPERIENCE, UNDO_LIST
		assertTrue(hasEvent(GenerationEventType.POWER_REMOVED));
		assertTrue(hasEvent(GenerationEventType.EXPERIENCE_CHANGED));
		assertTrue(hasEvent(GenerationEventType.UNDO_LIST_CHANGED));
	}

	//--------------------------------------------------------------------
	@Test
	public void selectMultiAlways() {
		model.setExperienceFree(50);
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.EXPERIENCE_CHANGED, null, new int[]{
				model.getExperienceFree(),
				model.getExperienceInvested()
		}));
		events.clear();

		// Start test
		PowerReference ref = control.select(MULTI_ALWAYS);
		
		int expCost = MULTI_ALWAYS.getCost()*7;
		assertNotNull(ref);
		assertEquals(1, ref.getCount());
		assertTrue(model.hasPower(MULTI_ALWAYS));
		assertEquals(expCost, model.getExperienceInvested());
		// Check undo modification
		assertFalse(undoList.isEmpty());
		PowerModification mod = (PowerModification) undoList.get(0);
		assertEquals(mod.getPower(), MULTI_ALWAYS);
		assertEquals(expCost, mod.getExpCost());
		
		// Check for events: ATTRIBUTE_CHANGED, POWERS, EXPERIENCE, CULTURELORE_AVAILABLE, UNDO_LIST
//		assertEquals(5, events.size());
		assertTrue(hasEvent(GenerationEventType.ATTRIBUTE_CHANGED));
		assertTrue(hasEvent(GenerationEventType.POWER_ADDED));
		assertTrue(hasEvent(GenerationEventType.EXPERIENCE_CHANGED));
		assertTrue(hasEvent(GenerationEventType.UNDO_LIST_CHANGED));

		// A multiple selectable power can not be deselected
		assertFalse(control.canBeDeselected(ref));
		
		// Is undo possible
		assertTrue(control.canBeDecreased(ref));
		// Is another selection possible
		assertTrue(control.canBeIncreased(ref));
	
		// Increase now
		events.clear();
		assertTrue(control.increase(ref));

		assertEquals(2, ref.getCount());
		assertTrue(model.hasPower(MULTI_ALWAYS));
		assertEquals(2*expCost, model.getExperienceInvested());
		// Check undo modification
		assertEquals(2, undoList.size());
		mod = (PowerModification) undoList.get(1);
		assertEquals(mod.getPower(), MULTI_ALWAYS);
		assertEquals(expCost, mod.getExpCost());
		
		// Check for events: ATTRIBUTE_CHANGED, POWERS, EXPERIENCE, CULTURELORE_AVAILABLE, UNDO_LIST
//		assertEquals(5, events.size());
		assertTrue(hasEvent(GenerationEventType.ATTRIBUTE_CHANGED));
		assertTrue(hasEvent(GenerationEventType.POWER_CHANGED));
		assertTrue(hasEvent(GenerationEventType.EXPERIENCE_CHANGED));
		assertTrue(hasEvent(GenerationEventType.UNDO_LIST_CHANGED));
		
		// Decrease now
		events.clear();
		assertTrue(control.decrease(ref));
		assertEquals(1, ref.getCount());
		assertTrue(model.hasPower(MULTI_ALWAYS));
		assertEquals(expCost, model.getExperienceInvested());
		// Check undo modification
		assertEquals(1, undoList.size());
		
		// Decrease again
		events.clear();
		assertTrue(control.decrease(ref));
		assertEquals(0, ref.getCount());
		assertFalse(model.hasPower(MULTI_ALWAYS));
		assertEquals(0, model.getExperienceInvested());
		// Check undo modification
		assertTrue(undoList.isEmpty());
		assertTrue(hasEvent(GenerationEventType.ATTRIBUTE_CHANGED));
		assertTrue(hasEvent(GenerationEventType.POWER_REMOVED));
		assertTrue(hasEvent(GenerationEventType.EXPERIENCE_CHANGED));
		assertTrue(hasEvent(GenerationEventType.UNDO_LIST_CHANGED));

		assertFalse(control.canBeDecreased(ref));
	}

	//--------------------------------------------------------------------
	@Test
	public void selectMultiLevel() {
		model.setExperienceFree(50);
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.EXPERIENCE_CHANGED, null, new int[]{
				model.getExperienceFree(),
				model.getExperienceInvested()
		}));
		events.clear();

		// Start test
		PowerReference ref = control.select(MULTI_LEVEL);
		
		int expCost = MULTI_LEVEL.getCost()*7;
		assertNotNull(ref);
		assertEquals(1, ref.getCount());
		assertTrue(model.hasPower(MULTI_LEVEL));
		assertEquals(expCost, model.getExperienceInvested());
		// Check undo modification
		assertFalse(undoList.isEmpty());
		PowerModification mod = (PowerModification) undoList.get(0);
		assertEquals(mod.getPower(), MULTI_LEVEL);
		assertEquals(expCost, mod.getExpCost());
		
		// Check for events: ATTRIBUTE_CHANGED, POWERS, EXPERIENCE, CULTURELORE_AVAILABLE, UNDO_LIST
		assertTrue(hasEvent(GenerationEventType.ATTRIBUTE_CHANGED));
		assertTrue(hasEvent(GenerationEventType.POWER_ADDED));
		assertTrue(hasEvent(GenerationEventType.EXPERIENCE_CHANGED));
		assertTrue(hasEvent(GenerationEventType.UNDO_LIST_CHANGED));

		// A multiple selectable power can not be deselected
		assertFalse(control.canBeDeselected(ref));
		
		// Is undo possible
		assertTrue(control.canBeDecreased(ref));
		// Should not be possible, because level requirement not met
		assertFalse(control.canBeIncreased(ref));
		assertFalse(control.increase(ref));
	
		// Raise level - should be possible now
		model.setLevel(2);
		assertTrue(control.canBeIncreased(ref));
		
		// Increase now
		events.clear();
		assertTrue(control.increase(ref));

		assertEquals(2, ref.getCount());
		assertTrue(model.hasPower(MULTI_LEVEL));
		assertEquals(2*expCost, model.getExperienceInvested());
		// Check undo modification
		assertEquals(2, undoList.size());
		mod = (PowerModification) undoList.get(1);
		assertEquals(mod.getPower(), MULTI_LEVEL);
		assertEquals(expCost, mod.getExpCost());
		
		// Check for events: ATTRIBUTE_CHANGED, POWERS, EXPERIENCE, CULTURELORE_AVAILABLE, UNDO_LIST
		assertTrue(hasEvent(GenerationEventType.ATTRIBUTE_CHANGED));
		assertTrue(hasEvent(GenerationEventType.POWER_CHANGED));
		assertTrue(hasEvent(GenerationEventType.EXPERIENCE_CHANGED));
		assertTrue(hasEvent(GenerationEventType.UNDO_LIST_CHANGED));
		
		// Decrease now
		events.clear();
		assertTrue(control.decrease(ref));
		assertEquals(1, ref.getCount());
		assertTrue(model.hasPower(MULTI_LEVEL));
		assertEquals(expCost, model.getExperienceInvested());
		// Check undo modification
		assertEquals(1, undoList.size());
		
		// Decrease again
		events.clear();
		assertTrue(control.decrease(ref));
		assertEquals(0, ref.getCount());
		assertFalse(model.hasPower(MULTI_ALWAYS));
		assertEquals(0, model.getExperienceInvested());
		// Check undo modification
		assertTrue(undoList.isEmpty());

		assertFalse(control.canBeDecreased(ref));
	}

}
