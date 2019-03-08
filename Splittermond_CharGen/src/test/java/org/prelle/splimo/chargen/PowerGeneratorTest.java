/**
 *
 */
package org.prelle.splimo.chargen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.prelle.rpgframework.splittermond.data.SplittermondDataPlugin;
import org.prelle.splimo.Power;
import org.prelle.splimo.PowerReference;
import org.prelle.splimo.Race;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.modifications.PowerModification;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class PowerGeneratorTest {

	private static Logger logger = LogManager.getLogger("junit.power");

	private final static int MAX = 10;

	private static Power ONCE_GENONLY;
	private static Power ONCE_ALWAYS;
	private static Power MULTI_ALWAYS;
	private static Power MULTI_MAX3;
	private static Power power4;
	private SpliMoCharacter model;
	private PowerGenerator2 generator;

	//-------------------------------------------------------------------
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		SplittermondDataPlugin plugin = new SplittermondDataPlugin();
		plugin.init();
		ONCE_GENONLY = SplitterMondCore.getPower("attractive");
		ONCE_ALWAYS = SplitterMondCore.getPower("socialable");
		MULTI_ALWAYS = SplitterMondCore.getPower("focuspool");
		MULTI_MAX3   = SplitterMondCore.getPower("resistmind");
		power4 = SplitterMondCore.getPower("enduring");
	}

	//-------------------------------------------------------------------
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		model = new SpliMoCharacter();
		SpliMoCharacterGenerator charGen = new SpliMoCharacterGenerator(model, null);
		generator = (PowerGenerator2) charGen.getPowerController();
	}

	//-------------------------------------------------------------------
	@Test
	public void testIdleState() {
		logger.debug("testIdleState-------------------------------");
		assertEquals(MAX, generator.getPointsLeft());
		assertTrue(model.getPowers().isEmpty());
	}

	//-------------------------------------------------------------------
	@Test
	public void testModifcationSelection() {
		logger.debug("testModifcationSelection-------------------------------");
		PowerModification mod1 = new PowerModification(ONCE_GENONLY);
		generator.addModification(mod1);
		generator.addModification(new PowerModification(ONCE_ALWAYS));
		assertEquals(MAX-ONCE_GENONLY.getCost()-ONCE_ALWAYS.getCost(), generator.getPointsLeft());
		assertEquals(2, model.getPowers().size());
		assertNotNull(model.getPower(ONCE_GENONLY));
		assertNotNull(model.getPower(ONCE_ALWAYS));

		generator.removeModification(mod1);
		assertEquals(MAX-ONCE_ALWAYS.getCost(), generator.getPointsLeft());
		assertEquals(1, model.getPowers().size());
		assertNull(model.getPower(ONCE_GENONLY));
		assertNotNull(model.getPower(ONCE_ALWAYS));
	}

	//-------------------------------------------------------------------
	@Test
	public void testDoubleModifcationSelection() {
		logger.debug("testDoubleModifcationSelection-------------------------------");
		generator.addModification(new PowerModification(ONCE_GENONLY));
		generator.addModification(new PowerModification(ONCE_GENONLY));
		assertEquals(MAX-ONCE_GENONLY.getCost(), generator.getPointsLeft());
		assertEquals(1, model.getPowers().size());
	}

	//-------------------------------------------------------------------
	@Test
	public void testIdleCanBeIncreased() {
		logger.debug("testCanBeIncreased-------------------------------");
		assertFalse(generator.canBeIncreased(new PowerReference(ONCE_GENONLY)));
		assertFalse(generator.canBeIncreased(new PowerReference(ONCE_ALWAYS)));
		assertFalse(generator.canBeIncreased(new PowerReference(MULTI_ALWAYS)));
		assertFalse(generator.canBeIncreased(new PowerReference(power4)));
	}

	//-------------------------------------------------------------------
	@Test
	public void testIdleCanBeDecreased() {
		logger.debug("testCanBeDecreased-------------------------------");
		assertFalse(generator.canBeDecreased(new PowerReference(ONCE_GENONLY)));
		assertFalse(generator.canBeDecreased(new PowerReference(ONCE_ALWAYS)));
		assertFalse(generator.canBeDecreased(new PowerReference(MULTI_ALWAYS)));
		assertFalse(generator.canBeDecreased(new PowerReference(power4)));
	}

	//-------------------------------------------------------------------
	@Test
	public void testIdleCanBeSelected() {
		logger.debug("testIdleCanBeSelected-------------------------------");
		assertTrue(generator.canBeSelected(ONCE_GENONLY));
		assertTrue(generator.canBeSelected(ONCE_ALWAYS));
		assertTrue(generator.canBeSelected(MULTI_ALWAYS));
		assertTrue(generator.canBeSelected(power4));
	}

	//-------------------------------------------------------------------
	@Test
	public void testIdleCanBeDeselected() {
		logger.debug("testIdleCanBeDeselected-------------------------------");
		assertFalse(generator.canBeDeselected(new PowerReference(ONCE_GENONLY)));
		assertFalse(generator.canBeDeselected(new PowerReference(ONCE_ALWAYS)));
		assertFalse(generator.canBeDeselected(new PowerReference(MULTI_ALWAYS)));
		assertFalse(generator.canBeDeselected(new PowerReference(power4)));
	}

	//-------------------------------------------------------------------
	@Test
	public void testCanBeSelectedNotEnoughGP() {
		logger.debug("testCanBeSelectedNotEnoughGP-------------------------------");
		generator.select(MULTI_ALWAYS); // 2 GP
		generator.select(MULTI_ALWAYS); // 2 GP
		generator.select(MULTI_ALWAYS); // 2 GP
		generator.select(MULTI_ALWAYS); // 2 GP
		generator.select(ONCE_GENONLY); // 1 GP
//		assertTrue(generator.canBeSelected(ONCE_GENONLY)); // 1 GP
		assertTrue(generator.canBeSelected(ONCE_ALWAYS)); // 1 GP
		assertFalse(generator.canBeSelected(MULTI_ALWAYS)); // 2 GP
		assertTrue(generator.canBeSelected(power4));
	}

	//-------------------------------------------------------------------
	@Test
	public void testManualSelection() {
		logger.debug("testManualSelection-------------------------------");
		PowerReference ref1 = generator.select(ONCE_GENONLY);
//		PowerReference ref2 = generator.select(ONCE_ALWAYS);
		assertTrue(model.hasPower(ONCE_GENONLY));
//		assertTrue(model.hasPower(ONCE_ALWAYS));
		assertEquals(MAX-ONCE_GENONLY.getCost(), generator.getPointsLeft());
//		assertEquals(2, model.getPowers().size());
//		assertFalse(generator.getAvailablePowers().contains(ONCE_ALWAYS));
		assertFalse(generator.getAvailablePowers().contains(ONCE_GENONLY));

		// Deselect again
		generator.deselect(ref1);
//		generator.deselect(ref2);
		assertEquals(MAX, generator.getPointsLeft());
		assertEquals(0, model.getPowers().size());
	}

	//-------------------------------------------------------------------
	@Test
	public void testManualDoubleSelection() {
		logger.debug("testManualDoubleSelection-------------------------------");
		PowerReference ref1a = generator.select(ONCE_GENONLY);
		PowerReference ref1b = generator.select(ONCE_GENONLY);
		assertEquals(MAX-ONCE_GENONLY.getCost(), generator.getPointsLeft());
		assertEquals(1, model.getPowers().size());
		assertNotNull(ref1a);
		assertNull(ref1b);

		// Deselect again
		generator.deselect(ref1a);
		assertEquals(MAX, generator.getPointsLeft());
		assertEquals(0, model.getPowers().size());
	}

	//-------------------------------------------------------------------
	@Test
	public void testSelectMoreThanAllowed() {
		generator.select(MULTI_ALWAYS);
		generator.increase(model.getPower(MULTI_ALWAYS));
		generator.increase(model.getPower(MULTI_ALWAYS));
		generator.increase(model.getPower(MULTI_ALWAYS));
		generator.increase(model.getPower(MULTI_ALWAYS));
		assertEquals(MAX-MULTI_ALWAYS.getCost()*5, generator.getPointsLeft());
		assertEquals(1, model.getPowers().size());
		// Select more than allowed
		generator.select(power4);
		assertEquals(MAX-MULTI_ALWAYS.getCost()*5, generator.getPointsLeft());
		assertEquals(1, model.getPowers().size());
	}

	//-------------------------------------------------------------------
	@Test
	public void testSelectManualAndThanAutomatic() {
		PowerModification mod1 = new PowerModification(ONCE_GENONLY);

		generator.select(ONCE_GENONLY);
		assertEquals(MAX-ONCE_GENONLY.getCost(), generator.getPointsLeft());
		generator.addModification(mod1);
		assertEquals(MAX-ONCE_GENONLY.getCost(), generator.getPointsLeft());
		assertEquals(1, model.getPowers().size());

		// Now remove automatic mod
		generator.removeModification(mod1);
		assertEquals(MAX, generator.getPointsLeft());
		assertEquals(0, model.getPowers().size());
	}

	//-------------------------------------------------------------------
	@Test
	public void testAutomaticThanManual() {
		PowerModification mod1 = new PowerModification(ONCE_ALWAYS);
		generator.addModification(mod1);
		assertEquals(MAX-ONCE_ALWAYS.getCost(), generator.getPointsLeft());

		assertFalse(generator.canBeSelected(ONCE_ALWAYS));
		assertNull(generator.select(ONCE_ALWAYS));
		assertEquals(MAX-ONCE_ALWAYS.getCost(), generator.getPointsLeft());
		assertEquals(1, model.getPowers().size());
	}

	//-------------------------------------------------------------------
	@Test
	public void testAutomaticThanDecrease() {
		PowerModification mod1 = new PowerModification(MULTI_ALWAYS);
		generator.addModification(mod1);
		assertEquals(MAX-MULTI_ALWAYS.getCost(), generator.getPointsLeft());

		assertTrue(generator.canBeSelected(MULTI_ALWAYS));
		PowerReference ref = model.getPower(MULTI_ALWAYS);
		assertTrue(generator.canBeDecreased(ref));
		assertTrue(generator.canBeDecreased(new PowerReference(MULTI_ALWAYS)));
	}

	//-------------------------------------------------------------------
	@Test
	public void testDeselectRaceModificationsOnce() {
		Race race = SplitterMondCore.getRace("alben");
		for (Modification mod : race.getModifications()) {
			if (mod instanceof PowerModification)
				generator.addModification((PowerModification) mod);
		}
		assertFalse(generator.canBeDeselected(model.getPower(ONCE_GENONLY)));
	}

	//-------------------------------------------------------------------
	@Test
	public void testDeselectRaceModificationsMulti() {
		Race race = SplitterMondCore.getRace("gnome");
		for (Modification mod : race.getModifications()) {
			if (mod instanceof PowerModification)
				generator.addModification((PowerModification) mod);
		}
		assertNotNull(model.getPower(MULTI_MAX3));
		assertFalse(generator.canBeDeselected(model.getPower(MULTI_MAX3)));
		assertFalse(generator.canBeIncreased(model.getPower(MULTI_MAX3)));
		assertTrue(generator.canBeSelected(MULTI_ALWAYS));
	}

}
