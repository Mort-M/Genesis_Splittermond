package org.prelle.splimo.chargen4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.prelle.splimo.DummyRulePlugin;
import org.prelle.splimo.Power;
import org.prelle.splimo.PowerReference;
import org.prelle.splimo.Race;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl4.NewPowerGenerator;
import org.prelle.splimo.charctrl4.SplitterEngineCharacterGenerator;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.modifications.PowerModification;

import de.rpgframework.genericrpg.modification.DecisionToMake;
import de.rpgframework.genericrpg.modification.Modification;

public class PowerGeneratorTest {
	
	private final static int MAX = 10;
	
	private static Power ONCE_GENONLY;
	private static Power ONCE_ALWAYS;
	private static Power MULTI_ALWAYS;
	private static Power MULTI_MAX3;
	private static Power power4;
	
	private SpliMoCharacter testModel;
	private NewPowerGenerator gen;
	private SplitterEngineCharacterGenerator parent;
	private List<Modification> previous;
	
	//-------------------------------------------------------------------
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PropertyConfigurator.configure(ClassLoader.getSystemResource("log4j.properties"));
		SplitterMondCore.initialize(new DummyRulePlugin<SpliMoCharacter>());
//		System.exit(0);
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
				gen.process(model, previous);
			}
		};
		GenerationEventDispatcher.clear();
		gen = new NewPowerGenerator(parent, MAX);
	}

	//-------------------------------------------------------------------
	@Test
	public void testIdle() {
		assertEquals(10, gen.getPointsLeft());
		assertTrue(gen.getToDos().isEmpty());
		gen.process(testModel, new ArrayList<>());
		assertEquals(MAX, gen.getPointsLeft());
		assertFalse(gen.getToDos().isEmpty());
		assertEquals(15, testModel.getExperienceFree());
		
	}

	//-------------------------------------------------------------------
	@Test
	public void testModifcationSelection() {
		PowerModification mod1 = new PowerModification(ONCE_GENONLY);
		previous.add(mod1);
		previous.add(new PowerModification(ONCE_ALWAYS));
		gen.process(testModel, previous);
		assertEquals(MAX-ONCE_GENONLY.getCost()-ONCE_ALWAYS.getCost(), gen.getPointsLeft());
		assertEquals(2, testModel.getPowers().size());
		assertNotNull(testModel.getPower(ONCE_GENONLY));
		assertNotNull(testModel.getPower(ONCE_ALWAYS));
		
		previous.remove(mod1);
		gen.process(testModel, previous);
		assertEquals(MAX-ONCE_ALWAYS.getCost(), gen.getPointsLeft());
		assertEquals(1, testModel.getPowers().size());
		assertNull(testModel.getPower(ONCE_GENONLY));
		assertNotNull(testModel.getPower(ONCE_ALWAYS));
	}

	//-------------------------------------------------------------------
	@Test
	public void testDoubleModifcationSelection() {
		previous.add(new PowerModification(ONCE_GENONLY));
		previous.add(new PowerModification(ONCE_GENONLY));
		gen.process(testModel, previous);
		assertEquals(MAX-ONCE_GENONLY.getCost(), gen.getPointsLeft());
		assertEquals(1, testModel.getPowers().size());
	}

	//-------------------------------------------------------------------
	@Test
	public void testIdleCanBeIncreased() {
		assertFalse(gen.canBeIncreased(new PowerReference(ONCE_GENONLY)));
		assertFalse(gen.canBeIncreased(new PowerReference(ONCE_ALWAYS)));
		assertFalse(gen.canBeIncreased(new PowerReference(MULTI_ALWAYS)));
		assertFalse(gen.canBeIncreased(new PowerReference(power4)));
	}

	//-------------------------------------------------------------------
	@Test
	public void testIdleCanBeDecreased() {
		assertFalse(gen.canBeDecreased(new PowerReference(ONCE_GENONLY)));
		assertFalse(gen.canBeDecreased(new PowerReference(ONCE_ALWAYS)));
		assertFalse(gen.canBeDecreased(new PowerReference(MULTI_ALWAYS)));
		assertFalse(gen.canBeDecreased(new PowerReference(power4)));
	}

	//-------------------------------------------------------------------
	@Test
	public void testIdleCanBeSelected() {
		assertTrue(gen.canBeSelected(ONCE_GENONLY));
		assertTrue(gen.canBeSelected(ONCE_ALWAYS));
		assertTrue(gen.canBeSelected(MULTI_ALWAYS));
		assertTrue(gen.canBeSelected(power4));
	}

	//-------------------------------------------------------------------
	@Test
	public void testIdleCanBeDeselected() {
		assertFalse(gen.canBeDeselected(new PowerReference(ONCE_GENONLY)));
		assertFalse(gen.canBeDeselected(new PowerReference(ONCE_ALWAYS)));
		assertFalse(gen.canBeDeselected(new PowerReference(MULTI_ALWAYS)));
		assertFalse(gen.canBeDeselected(new PowerReference(power4)));
	}

	//-------------------------------------------------------------------
	@Test
	public void testCanBeSelectedNotEnoughGP() {
		assertEquals(10, gen.getPointsLeft());
		gen.select(MULTI_ALWAYS); // 2 GP
		assertEquals(8, gen.getPointsLeft());
		gen.select(MULTI_ALWAYS); // 2 GP
		gen.select(MULTI_ALWAYS); // 2 GP
		gen.select(MULTI_ALWAYS); // 2 GP
		gen.select(MULTI_ALWAYS); // 2 GP
		assertEquals(0, gen.getPointsLeft());
		assertTrue(gen.canBeSelected(ONCE_ALWAYS)); // 1 GP
		testModel.setExperienceFree(2);
		assertFalse(gen.canBeSelected(MULTI_ALWAYS)); // 2 GP
		testModel.setExperienceFree(15);
		assertTrue(gen.canBeSelected(MULTI_ALWAYS)); // 2 GP
		assertTrue(gen.canBeSelected(power4));
	}

	//-------------------------------------------------------------------
	@Test
	public void testManualSelection() {
		gen.process(testModel, previous);
		assertTrue(gen.getAvailablePowers().contains(ONCE_GENONLY));
		PowerReference ref1 = gen.select(ONCE_GENONLY);
//		PowerReference ref2 = gen.select(ONCE_ALWAYS);
		assertTrue(testModel.hasPower(ONCE_GENONLY));
//		assertTrue(model.hasPower(ONCE_ALWAYS));
		assertEquals(MAX-ONCE_GENONLY.getCost(), gen.getPointsLeft());
//		assertEquals(2, model.getPowers().size());
//		assertFalse(gen.getAvailablePowers().contains(ONCE_ALWAYS));
		assertFalse(gen.getAvailablePowers().contains(ONCE_GENONLY));

		// Deselect again
		gen.deselect(ref1);
//		gen.deselect(ref2);
		assertEquals(MAX, gen.getPointsLeft());
		assertEquals(0, testModel.getPowers().size());
	}

	//-------------------------------------------------------------------
	@Test
	public void testManualDoubleSelection() {
		PowerReference ref1a = gen.select(ONCE_GENONLY);
		PowerReference ref1b = gen.select(ONCE_GENONLY);
		assertEquals(MAX-ONCE_GENONLY.getCost(), gen.getPointsLeft());
		assertEquals(1, testModel.getPowers().size());
		assertNotNull(ref1a);
		assertNull(ref1b);

		// Deselect again
		gen.deselect(ref1a);
		assertEquals(MAX, gen.getPointsLeft());
		assertEquals(0, testModel.getPowers().size());
	}

	//-------------------------------------------------------------------
	@Test
	public void testSelectMoreThanAllowed() {
		gen.select(MULTI_ALWAYS);
		gen.increase(testModel.getPower(MULTI_ALWAYS));
		gen.increase(testModel.getPower(MULTI_ALWAYS));
		gen.increase(testModel.getPower(MULTI_ALWAYS));
		gen.increase(testModel.getPower(MULTI_ALWAYS));
		assertEquals(MAX-MULTI_ALWAYS.getCost()*5, gen.getPointsLeft());
		assertEquals(1, testModel.getPowers().size());
		// Select more than allowed
		gen.select(power4);
		assertEquals(MAX-MULTI_ALWAYS.getCost()*5, gen.getPointsLeft());
		assertEquals(1, testModel.getPowers().size());
	}

	//-------------------------------------------------------------------
	@Test
	public void testSelectManualAndThanAutomatic() {
		PowerModification mod1 = new PowerModification(ONCE_GENONLY);
		
		gen.select(ONCE_GENONLY);
		assertEquals(MAX-ONCE_GENONLY.getCost(), gen.getPointsLeft());
		previous.add(mod1);
		gen.process(testModel, previous);
		assertEquals(MAX-ONCE_GENONLY.getCost(), gen.getPointsLeft());
		assertEquals(1, testModel.getPowers().size());
		
		// Now remove automatic mod
		previous.remove(mod1);
		gen.process(testModel, previous);
		assertEquals(MAX, gen.getPointsLeft());
		assertEquals(0, testModel.getPowers().size());
	}

	//-------------------------------------------------------------------
	@Test
	public void testAutomaticThanManual() {
		PowerModification mod1 = new PowerModification(ONCE_ALWAYS);
		previous.add(mod1);
		gen.process(testModel, previous);
		assertEquals(MAX-ONCE_ALWAYS.getCost(), gen.getPointsLeft());

		assertFalse(gen.canBeSelected(ONCE_ALWAYS));
		assertNull(gen.select(ONCE_ALWAYS));
		assertEquals(MAX-ONCE_ALWAYS.getCost(), gen.getPointsLeft());
		assertEquals(1, testModel.getPowers().size());
	}

	//-------------------------------------------------------------------
	@Test
	public void testAutomaticThanDecrease() {
		PowerModification mod1 = new PowerModification(MULTI_ALWAYS);
		previous.add(mod1);
		gen.process(testModel, previous);
		assertEquals(MAX-MULTI_ALWAYS.getCost(), gen.getPointsLeft());

		assertTrue(gen.canBeSelected(MULTI_ALWAYS));
		PowerReference ref = testModel.getPower(MULTI_ALWAYS);
		assertTrue(gen.canBeDecreased(ref));
		assertTrue(gen.canBeDecreased(new PowerReference(MULTI_ALWAYS)));
	}

	//-------------------------------------------------------------------
	@Test
	public void testDeselectRaceModificationsOnce() {
		Race race = SplitterMondCore.getRace("alben");
		for (Modification mod : race.getModifications()) {
			if (mod instanceof PowerModification) 
				previous.add((PowerModification) mod);
		}
		gen.process(testModel, previous);
		assertFalse("Should not allow to deselect race given modifications", gen.canBeDeselected(testModel.getPower(ONCE_GENONLY)));
	}

	//-------------------------------------------------------------------
	@Test
	public void testDeselectRaceModificationsMulti() {
		Race race = SplitterMondCore.getRace("gnome");
		for (Modification mod : race.getModifications()) {
			if (mod instanceof PowerModification) 
				previous.add((PowerModification) mod);
		}
		gen.process(testModel, previous);
		assertNotNull(testModel.getPower(MULTI_MAX3));
		assertFalse(gen.canBeDeselected(testModel.getPower(MULTI_MAX3)));
		assertFalse(gen.canBeIncreased(testModel.getPower(MULTI_MAX3)));
		assertTrue(gen.canBeSelected(MULTI_ALWAYS));
	}
	
}
