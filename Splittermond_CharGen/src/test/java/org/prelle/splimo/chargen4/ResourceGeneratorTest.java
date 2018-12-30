package org.prelle.splimo.chargen4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.prelle.splimo.DummyRulePlugin;
import org.prelle.splimo.Resource;
import org.prelle.splimo.ResourceReference;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl4.NewResourceGenerator;
import org.prelle.splimo.charctrl4.SplitterEngineCharacterGenerator;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.modifications.ResourceModification;

import de.rpgframework.genericrpg.modification.DecisionToMake;
import de.rpgframework.genericrpg.modification.Modification;

public class ResourceGeneratorTest {
	
	private final static int MAX_POINTS = 8;
	private final static int NUM_BASE_RESOURCES = 4;

	private static Resource nonBaseResource1;
	private static Resource nonBaseResource2;
	private static Resource resource2;
	private static Resource resource3;
	
	private SpliMoCharacter testModel;
	private NewResourceGenerator gen;
	private SplitterEngineCharacterGenerator parent;
	private List<Modification> previous;
	
	//-------------------------------------------------------------------
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PropertyConfigurator.configure(ClassLoader.getSystemResource("log4j.properties"));
		SplitterMondCore.initialize(new DummyRulePlugin<SpliMoCharacter>());
		nonBaseResource1 = SplitterMondCore.getResource("relic");
		nonBaseResource2 = SplitterMondCore.getResource("mentor");
		resource2 = SplitterMondCore.getResource("reputation");
		resource3 = SplitterMondCore.getResource("status");
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
				testModel.setExperienceFree(15);
				testModel.setExperienceInvested(0);
				gen.process(model, previous);
			}
		};
		GenerationEventDispatcher.clear();
		gen = new NewResourceGenerator(parent, MAX_POINTS, true);
	}

	//-------------------------------------------------------------------
	@Test
	public void testIdleState() {
		gen.process(testModel, previous);
		assertEquals(MAX_POINTS, gen.getPointsLeft());
		assertEquals(NUM_BASE_RESOURCES, testModel.getResources().size());
	}

	//-------------------------------------------------------------------
	@Test
	public void testManualSelection() {
		ResourceReference nonBase = gen.openResource(nonBaseResource1);
		assertNotNull(nonBase);
		assertEquals(MAX_POINTS-1, gen.getPointsLeft());
		assertEquals(NUM_BASE_RESOURCES+1, testModel.getResources().size());
	}

	//-------------------------------------------------------------------
	@Test
	public void testSingleModification() {
		ResourceModification mod1 = new ResourceModification(nonBaseResource1, 1);
		previous.add(mod1);
		gen.process(testModel, previous);
		
		ResourceReference result = gen.getFirstValueFor(nonBaseResource1);
		assertNotNull(result);
		assertEquals(0, result.getValue());
		assertEquals(1, result.getModifiedValue());
		assertEquals(MAX_POINTS-1, gen.getPointsLeft());
		assertEquals(NUM_BASE_RESOURCES+1, testModel.getResources().size());
	}

	//-------------------------------------------------------------------
	@Test
	public void testIncreaseBaseModification() {
		System.out.println("---------testIncreaseBaseModification---------------------------------");
		ResourceModification mod1 = new ResourceModification(resource2, 1);
		// Add +1 reputation
		previous.add(mod1);
		// Add +1 reputation (by system)
		previous.add(new ResourceModification(resource2, 1));
		gen.process(testModel, previous);
		// No distributable points used
		assertEquals(MAX_POINTS-2, gen.getPointsLeft());
		// Only base resources selected
		assertEquals(NUM_BASE_RESOURCES, testModel.getResources().size());
		// Value should be 2
		assertEquals(2, gen.getFirstValueFor(resource2).getModifiedValue());
		assertEquals(0, gen.getFirstValueFor(resource2).getValue());
		
		previous.remove(mod1);
		gen.process(testModel, previous);
		assertEquals(MAX_POINTS-1, gen.getPointsLeft());
		assertEquals(NUM_BASE_RESOURCES, testModel.getResources().size());
		assertEquals(1, gen.getFirstValueFor(resource2).getModifiedValue());
	}

	//-------------------------------------------------------------------
	@Test
	public void testIncreaseBaseModificationDouble() {
		System.out.println("--------testIncreaseBaseModificationDouble");
		ResourceModification mod1 = new ResourceModification(resource2, 1);
		ResourceModification mod2 = new ResourceModification(resource2, 2);
		// Add +1 reputation
		previous.add(mod1);
		// Add +1 reputation
		previous.add(mod1);
		// Add +2 reputation
		previous.add(mod2);
		gen.process(testModel, previous);
		// No distributable points used
		assertEquals(MAX_POINTS-4, gen.getPointsLeft());
		// Only base resources selected
		assertEquals(NUM_BASE_RESOURCES, testModel.getResources().size());
		assertEquals(4, gen.getFirstValueFor(resource2).getModifiedValue());
		
		previous.remove(mod1);
		gen.process(testModel, previous);
		assertEquals(MAX_POINTS-3, gen.getPointsLeft());
		assertEquals(NUM_BASE_RESOURCES, testModel.getResources().size());
		assertEquals(3, gen.getFirstValueFor(resource2).getModifiedValue());
		
		previous.remove(mod2);
		gen.process(testModel, previous);
		assertEquals(MAX_POINTS-1, gen.getPointsLeft());
		assertEquals("Deleted base resource",NUM_BASE_RESOURCES, testModel.getResources().size());
		assertEquals(1, gen.getFirstValueFor(resource2).getModifiedValue());
	}

	//-------------------------------------------------------------------
	@Test
	public void testAddModifcation() {
		System.out.println("testAddModification");
		ResourceModification mod1 = new ResourceModification(nonBaseResource1, 1);
		previous.add(mod1);
		ResourceModification mod2 = new ResourceModification(nonBaseResource2, 1);
		previous.add(mod2);
		gen.process(testModel, previous);
		assertEquals(MAX_POINTS-2, gen.getPointsLeft());
		assertEquals(NUM_BASE_RESOURCES+2, testModel.getResources().size());
		assertNotNull(gen.getFirstValueFor(nonBaseResource1));
		assertEquals(1, gen.getFirstValueFor(nonBaseResource1).getModifiedValue());
	
		previous.remove(mod2);
		gen.process(testModel, previous);
		assertEquals(MAX_POINTS-1, gen.getPointsLeft());
		assertEquals(NUM_BASE_RESOURCES+1, testModel.getResources().size());
		assertNotNull(gen.getFirstValueFor(nonBaseResource1));
		assertEquals(1, gen.getFirstValueFor(nonBaseResource1).getModifiedValue());
		
		previous.remove(mod1);
		gen.process(testModel, previous);
		assertEquals(MAX_POINTS, gen.getPointsLeft());
		assertEquals(NUM_BASE_RESOURCES, testModel.getResources().size());
		assertNull(gen.getFirstValueFor(nonBaseResource1));
	}

	//-------------------------------------------------------------------
	@Test
	public void testIncreaseModifcation() {
		System.out.println("testIncreaseModifcation");
		previous.add(new ResourceModification(nonBaseResource1,1));
		previous.add(new ResourceModification(nonBaseResource1,1));
		gen.process(testModel, previous);
		assertEquals(MAX_POINTS-2, gen.getPointsLeft());
		System.out.println("doubleMod: "+testModel.getResources());
		assertEquals(NUM_BASE_RESOURCES+1, testModel.getResources().size());
		assertNotNull(gen.getFirstValueFor(nonBaseResource1));
		assertEquals(2, gen.getFirstValueFor(nonBaseResource1).getModifiedValue());
	}

	//-------------------------------------------------------------------
	@Test
	public void testManualSelectionBase() {
		System.out.println("testManualSelectionBase");
		// Trying to select a basic resource - should be present, but don't change a thing
		ResourceReference base1 = gen.openResource(resource2);
		assertNotNull(base1);
		assertEquals(MAX_POINTS-1, gen.getPointsLeft());
		assertEquals(NUM_BASE_RESOURCES, testModel.getResources().size());
		
		ResourceReference nonBase = gen.openResource(nonBaseResource1);
		assertNotNull(nonBase);
		assertEquals(MAX_POINTS-2, gen.getPointsLeft());
		assertEquals(NUM_BASE_RESOURCES+1, testModel.getResources().size());
		
		// Try to increase it
		assertTrue(gen.canBeIncreased(nonBase));
		assertTrue(gen.canBeIncreased(base1));
		
		// Increase once
		gen.increase(nonBase);
		assertEquals(MAX_POINTS-3, gen.getPointsLeft());
		assertEquals(NUM_BASE_RESOURCES+1, testModel.getResources().size());
		
//		// Now deselect base - should not work
//		gen.deselect(base1);
//		assertEquals(MAX_POINTS-2, gen.getPointsLeft());
//		assertEquals(NUM_BASE_RESOURCES+1, testModel.getResources().size());
//		
//		// Now deselect non-base 
//		gen.deselect(nonBase);
//		assertEquals(MAX_POINTS, gen.getPointsLeft());
//		assertEquals(NUM_BASE_RESOURCES, testModel.getResources().size());
	}

	//-------------------------------------------------------------------
	@Test
	public void testSelectManualAndThanAutomatic() {
		System.out.println("testSelectManualAndThanAutomatic");
		ResourceReference nonBase = gen.openResource(nonBaseResource1);
		assertEquals(1, gen.getFirstValueFor(nonBaseResource1).getValue());
		assertEquals(1, gen.getFirstValueFor(nonBaseResource1).getModifiedValue());
		ResourceModification mod1 = new ResourceModification(nonBaseResource1, 2);
		previous.add(mod1);
		gen.process(testModel, previous);

		assertEquals(MAX_POINTS-3, gen.getPointsLeft());
		assertEquals(NUM_BASE_RESOURCES+1, testModel.getResources().size());
		assertEquals(1, gen.getFirstValueFor(nonBaseResource1).getValue());
		assertEquals(1+mod1.getValue(), gen.getFirstValueFor(nonBaseResource1).getModifiedValue());

		// Deselect again - now only modification remains
		gen.decrease(nonBase);
		assertEquals(MAX_POINTS-2, gen.getPointsLeft());
		assertEquals(NUM_BASE_RESOURCES+1, testModel.getResources().size());
		assertEquals(0, gen.getFirstValueFor(nonBaseResource1).getValue());
		assertEquals(mod1.getValue(), gen.getFirstValueFor(nonBaseResource1).getModifiedValue());
	}

	//-------------------------------------------------------------------
	@Test
	public void testIncreaseMoreThanCostAllowed() {
		ResourceReference base2 = gen.getFirstValueFor(resource2);
		ResourceReference base4 = gen.getFirstValueFor(resource3);
		gen.increase(base2);
		gen.increase(base4);
		while (gen.getPointsLeft()>0) {
			if (gen.canBeIncreased(base2))
				gen.increase(base2);
			if (gen.canBeIncreased(base4))
				gen.increase(base4);
		}
		assertEquals(0, gen.getPointsLeft());
		// The following increase should not be possible
		gen.increase(base4);
		assertEquals(0, gen.getPointsLeft());
		gen.increase(base2);
		assertEquals(0, gen.getPointsLeft());
	}

	//-------------------------------------------------------------------
	@Test
	public void increaseMoreThanValueAllowed() {
		System.out.println("increaseMoreThanValueAllowed");
		ResourceReference base2 = gen.openResource(resource2);
		assertEquals(1, gen.getFirstValueFor(resource2).getValue());
		assertEquals(1, gen.getFirstValueFor(resource2).getModifiedValue());

		ResourceModification mod1 = new ResourceModification(resource2, 2);
		previous.add(mod1);
		gen.process(testModel, previous);
		assertEquals(1, gen.getFirstValueFor(resource2).getValue());
		assertEquals(3, gen.getFirstValueFor(resource2).getModifiedValue());
	
		gen.increase(base2);
		assertEquals("Increase of base resource failed", 4, base2.getModifiedValue());
		assertEquals(2, gen.getFirstValueFor(resource2).getValue());
		assertEquals(4, gen.getFirstValueFor(resource2).getModifiedValue());
		// The following increase should not be possible
		assertFalse(gen.canBeIncreased(base2));
		assertFalse(gen.increase(base2));
		assertEquals(2, gen.getFirstValueFor(resource2).getValue());
		assertEquals(4, gen.getFirstValueFor(resource2).getModifiedValue());
		// Allow max resources
		gen.setAllowExtremeResourcesOnGeneration(true);
		assertTrue(gen.canBeIncreased(base2));
		assertTrue(gen.increase(base2));
		assertEquals(3, gen.getFirstValueFor(resource2).getValue());
		assertEquals(5, gen.getFirstValueFor(resource2).getModifiedValue());
	}

	//-------------------------------------------------------------------
	@Test
	public void increaseWithExp() {
		System.out.println("increaseWithExp");
		testModel.addResource( new ResourceReference(nonBaseResource1, 4) );
		testModel.addResource( new ResourceReference(nonBaseResource2, 4) );
		testModel.setExperienceFree(0);
		gen.process(testModel, previous);
		assertEquals(0, gen.getPointsLeft());
		
		// Without exp increasing other resources should not be possible
		assertFalse(gen.canBeIncreased(gen.getFirstValueFor(resource2)));
		assertFalse(gen.increase(gen.getFirstValueFor(resource2)));
		assertNull(gen.openResource(resource2));
		assertEquals(0, gen.getPointsLeft());
		assertEquals(0, testModel.getExperienceInvested());
		
		// Add exp
		testModel.setExperienceFree(15);
		assertTrue(gen.canBeIncreased(gen.getFirstValueFor(resource2)));
		assertTrue(gen.increase(gen.getFirstValueFor(resource2)));
		testModel.setExperienceFree(8);
		testModel.setExperienceInvested(7);
		assertEquals(0, gen.getPointsLeft());
		
		// Decrease other resource - should make EXP obsolete
		assertTrue( gen.decrease(gen.getFirstValueFor(nonBaseResource1)));
		testModel.setExperienceFree(15);
		testModel.setExperienceInvested(0);
		assertEquals(0, gen.getPointsLeft());
	}

	//-------------------------------------------------------------------
	@Test
	public void testDecreaseBasic() {
		System.out.println("testDecreaseBasic");
		ResourceReference base2 = gen.openResource(resource2);
		assertNotNull(base2);
		assertNotNull(base2.getResource());
		// Should not work
		gen.decrease(base2);
		assertEquals(MAX_POINTS, gen.getPointsLeft());
		assertEquals(0, gen.getFirstValueFor(resource2).getValue());
		// Should work
		gen.setAllowExtremeResourcesOnGeneration(true);
		gen.decrease(base2);
		assertEquals(MAX_POINTS+1, gen.getPointsLeft());
		assertEquals(-1, gen.getFirstValueFor(resource2).getValue());
	}

	//-------------------------------------------------------------------
	@Test
	public void testDecreaseModification() {
		ResourceModification mod = new ResourceModification(nonBaseResource1, 4);
		previous.add(mod);
		gen.process(testModel, previous);
		assertEquals(MAX_POINTS-4, gen.getPointsLeft());
		ResourceReference ref = gen.getFirstValueFor(nonBaseResource1);
		assertEquals(0, ref.getValue());
		assertEquals(4, ref.getModifiedValue());
		
		assertTrue(gen.canBeDecreased(ref));
		assertTrue(gen.decrease(ref));
		assertEquals(-1, ref.getValue());
		assertEquals(3, ref.getModifiedValue());
		assertEquals(MAX_POINTS-3, gen.getPointsLeft());
		assertTrue(gen.decrease(ref));
		assertTrue(gen.decrease(ref));
		assertTrue(gen.decrease(ref));
		assertEquals(MAX_POINTS, gen.getPointsLeft());
		
		// When the modification is removed, the resource should be not be present anymore
		previous.remove(mod);
		gen.process(testModel, previous);
		assertEquals(MAX_POINTS, gen.getPointsLeft());
		assertNull(gen.getFirstValueFor(nonBaseResource1));
	}

	//-------------------------------------------------------------------
	@Test
	public void testOverwriteModifications() {
		previous.add(new ResourceModification(nonBaseResource1, 4));
		previous.add(new ResourceModification(nonBaseResource2, 4));
		gen.process(testModel, previous);
	}

	//-------------------------------------------------------------------
	@Test
	public void testSplit() {
		System.out.println("testSplit");
		
		ResourceReference base1 = gen.openResource(resource2);
		ResourceReference nonBase1 = gen.openResource(nonBaseResource1);
		
		assertFalse("May not split base resources",gen.canBeSplit(base1));
		assertNull(gen.split(nonBase1));
		assertFalse("May not split resources with value <2",gen.canBeSplit(base1));
		assertEquals(NUM_BASE_RESOURCES+1, testModel.getResources().size());
		
		gen.increase(nonBase1); // Should be splittable now
		assertEquals(2, nonBase1.getValue());
		ResourceReference splitted = gen.split(nonBase1);
		assertNotNull(splitted);
		assertEquals(1, nonBase1.getValue());
		assertEquals(1, splitted.getValue());
		assertFalse(gen.canBeSplit(base1));
		assertFalse(gen.canBeSplit(splitted));
	}

	//-------------------------------------------------------------------
	@Test
	public void testValidJoin() {
		System.out.println("testJoin");
		
		previous.add(new ResourceModification(resource2, 1));
		gen.process(testModel, previous);
		ResourceReference base1    = gen.getFirstValueFor(resource2);
		ResourceReference nonBase1 = gen.openResource(nonBaseResource1);
		ResourceReference nonBase2 = gen.openResource(nonBaseResource1);
		assertNotSame(nonBase1,nonBase2);
		
		assertFalse(gen.canBeJoined(base1, nonBase1));
		assertFalse(gen.canBeJoined(nonBase1, base1));
		assertTrue(gen.canBeJoined(nonBase1, nonBase2));
		
		gen.join(nonBase1, nonBase2);
		assertEquals(2, nonBase1.getValue());
	}

	//-------------------------------------------------------------------
	@Test
	public void testInvalidJoin() {
		System.out.println("testInvalidJoin");
		
		// Joining a single base resources should not be possible
		assertFalse(gen.canBeJoined(gen.getFirstValueFor(resource2)));
		
		previous.add(new ResourceModification(nonBaseResource1, 4));
		gen.process(testModel, previous);
		ResourceReference nonBase1 = gen.getFirstValueFor(nonBaseResource1);
		ResourceReference nonBase2 = gen.split(nonBase1);
		assertNotNull("Splitting failed",nonBase2);
		assertEquals(-1, nonBase1.getValue());
		assertEquals(3, nonBase1.getModifiedValue());
		assertEquals(1, nonBase2.getValue());
		assertEquals(1, nonBase2.getModifiedValue());
		assertTrue(gen.canBeJoined(nonBase1, nonBase2));
		assertEquals(NUM_BASE_RESOURCES+2, testModel.getResources().size());

		// Raising one resource should result in a value>4 which is not allowed
		gen.increase(nonBase2);
		assertEquals(2, nonBase2.getValue());
		gen.setAllowExtremeResourcesOnGeneration(false);
		assertFalse("Resulting join above maximum not detected", gen.canBeJoined(nonBase1, nonBase2));
		gen.join(nonBase1, nonBase2); // Should not work
		assertEquals(NUM_BASE_RESOURCES+2, testModel.getResources().size());
		
		// Allowing extreme resources
		gen.setAllowExtremeResourcesOnGeneration(true);
		assertTrue(gen.canBeJoined(nonBase1, nonBase2));
		
		gen.join(nonBase1, nonBase2); // Should work now
		assertEquals(1, nonBase1.getValue());
		assertEquals(5, nonBase1.getModifiedValue());
		assertEquals(NUM_BASE_RESOURCES+1, testModel.getResources().size());
	}
	
}
