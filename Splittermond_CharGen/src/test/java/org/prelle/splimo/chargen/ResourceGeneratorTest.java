/**
 * 
 */
package org.prelle.splimo.chargen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.prelle.rpgframework.splittermond.SplittermondRules;
import org.prelle.splimo.Resource;
import org.prelle.splimo.ResourceReference;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.modifications.ResourceModification;

/**
 * @author prelle
 *
 */
@FixMethodOrder(MethodSorters.JVM)
public class ResourceGeneratorTest {
	
	private final static int MAX_POINTS = 8;
	private final static int NUM_BASE_RESOURCES = 4;

	private static Resource nonBaseResource1;
	private static Resource nonBaseResource2;
	private static Resource resource2;
	private static Resource resource3;
	private SpliMoCharacter model;
	private ResourceGenerator generator;

	//-------------------------------------------------------------------
	static {
		SplitterMondCore.initialize(new SplittermondRules());
		nonBaseResource1 = SplitterMondCore.getResource("relic");
		nonBaseResource2 = SplitterMondCore.getResource("mentor");
		resource2 = SplitterMondCore.getResource("reputation");
		resource3 = SplitterMondCore.getResource("status");
	}

	//-------------------------------------------------------------------
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		SplitterMondCore.initialize(new SplittermondRules());
	}

	//-------------------------------------------------------------------
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		model = new SpliMoCharacter();
		generator = new ResourceGenerator(MAX_POINTS, model);
	}

	//-------------------------------------------------------------------
	@Test
	public void testIdleState() {
		assertEquals(MAX_POINTS, generator.getPointsLeft());
		assertEquals(NUM_BASE_RESOURCES, model.getResources().size());
	}

	//-------------------------------------------------------------------
	@Test
	public void testManualSelection() {
		ResourceReference nonBase = generator.openResource(nonBaseResource1);
		assertNotNull(nonBase);
		assertEquals(MAX_POINTS-1, generator.getPointsLeft());
		assertEquals(NUM_BASE_RESOURCES+1, model.getResources().size());
	}

	//-------------------------------------------------------------------
	@Test
	public void testSingleModification() {
		ResourceModification mod1 = new ResourceModification(nonBaseResource1, 1);
		generator.addModification(mod1);
		
		ResourceReference result = generator.getFirstValueFor(nonBaseResource1);
		assertNotNull(result);
		assertEquals(1, result.getValue());
		assertEquals(MAX_POINTS-1, generator.getPointsLeft());
		assertEquals(NUM_BASE_RESOURCES+1, model.getResources().size());
	}

	//-------------------------------------------------------------------
	@Test
	public void testIncreaseBaseModification() {
		System.out.println("---------testIncreaseBaseModification---------------------------------");
		ResourceModification mod1 = new ResourceModification(resource2, 1);
		// Add +1 reputation
		generator.addModification(mod1);
		// Add +1 reputation (by system)
		generator.addModification(new ResourceModification(resource2, 1));
		// No distributable points used
		assertEquals(MAX_POINTS-2, generator.getPointsLeft());
		// Only base resources selected
		assertEquals(NUM_BASE_RESOURCES, model.getResources().size());
		// Value should be 2
		assertEquals(2, generator.getFirstValueFor(resource2).getValue());
		
		generator.removeModification(mod1);
		assertEquals(MAX_POINTS-1, generator.getPointsLeft());
		assertEquals(NUM_BASE_RESOURCES, model.getResources().size());
		assertEquals(1, generator.getFirstValueFor(resource2).getValue());
	}

	//-------------------------------------------------------------------
	@Test
	public void testIncreaseBaseModificationDouble() {
		System.out.println("--------testIncreaseBaseModificationDouble");
		ResourceModification mod1 = new ResourceModification(resource2, 1);
		ResourceModification mod2 = new ResourceModification(resource2, 2);
		// Add +1 reputation
		generator.addModification(mod1);
		// Add +1 reputation
		generator.addModification(mod1);
		// Add +2 reputation
		generator.addModification(mod2);
		// No distributable points used
		assertEquals(MAX_POINTS-3, generator.getPointsLeft());
		// Only base resources selected
		assertEquals(NUM_BASE_RESOURCES, model.getResources().size());
		assertEquals(3, generator.getFirstValueFor(resource2).getValue());
		
		generator.removeModification(mod1);
		assertEquals(MAX_POINTS-2, generator.getPointsLeft());
		assertEquals(NUM_BASE_RESOURCES, model.getResources().size());
		assertEquals(2, generator.getFirstValueFor(resource2).getValue());
		
		generator.removeModification(mod2);
		assertEquals(MAX_POINTS, generator.getPointsLeft());
		assertEquals("Deleted base resource",NUM_BASE_RESOURCES, model.getResources().size());
		assertEquals(0, generator.getFirstValueFor(resource2).getValue());
	}

	//-------------------------------------------------------------------
	@Test
	public void testAddModifcation() {
		System.out.println("testAddModification");
		ResourceModification mod1 = new ResourceModification(nonBaseResource1, 1);
		generator.addModification(mod1);
		ResourceModification mod2 = new ResourceModification(nonBaseResource2, 1);
		generator.addModification(mod2);
		assertEquals(MAX_POINTS-2, generator.getPointsLeft());
		assertEquals(NUM_BASE_RESOURCES+2, model.getResources().size());
		assertNotNull(generator.getFirstValueFor(nonBaseResource1));
		assertEquals(1, generator.getFirstValueFor(nonBaseResource1).getValue());
	
		generator.removeModification(mod2);
		assertEquals(MAX_POINTS-1, generator.getPointsLeft());
		assertEquals(NUM_BASE_RESOURCES+1, model.getResources().size());
		assertNotNull(generator.getFirstValueFor(nonBaseResource1));
		assertEquals(1, generator.getFirstValueFor(nonBaseResource1).getValue());
		
		generator.removeModification(mod1);
		assertEquals(MAX_POINTS, generator.getPointsLeft());
		assertEquals(NUM_BASE_RESOURCES, model.getResources().size());
		assertNull(generator.getFirstValueFor(nonBaseResource1));
	}

	//-------------------------------------------------------------------
	@Test
	public void testIncreaseModifcation() {
		System.out.println("testIncreaseModifcation");
		generator.addModification(new ResourceModification(nonBaseResource1,1));
		generator.addModification(new ResourceModification(nonBaseResource1,1));
		assertEquals(MAX_POINTS-2, generator.getPointsLeft());
		System.out.println("doubleMod: "+model.getResources());
		assertEquals(NUM_BASE_RESOURCES+1, model.getResources().size());
		assertNotNull(generator.getFirstValueFor(nonBaseResource1));
		assertEquals(2, generator.getFirstValueFor(nonBaseResource1).getValue());
	}

	//-------------------------------------------------------------------
	@Test
	public void testManualSelectionBase() {
		System.out.println("testManualSelectionBase");
		// Trying to select a basic resource - should be present, but don't change a thing
		ResourceReference base1 = generator.openResource(resource2);
		assertNotNull(base1);
		assertEquals(MAX_POINTS, generator.getPointsLeft());
		assertEquals(NUM_BASE_RESOURCES, model.getResources().size());
		
		ResourceReference nonBase = generator.openResource(nonBaseResource1);
		assertNotNull(nonBase);
		assertEquals(MAX_POINTS-1, generator.getPointsLeft());
		assertEquals(NUM_BASE_RESOURCES+1, model.getResources().size());
		
		// Try to increase it
		assertTrue(generator.canBeIncreased(nonBase));
		assertTrue(generator.canBeIncreased(base1));
		
		// Increase once
		generator.increase(nonBase);
		assertEquals(MAX_POINTS-2, generator.getPointsLeft());
		assertEquals(NUM_BASE_RESOURCES+1, model.getResources().size());
		
//		// Now deselect base - should not work
//		generator.deselect(base1);
//		assertEquals(MAX_POINTS-2, generator.getPointsLeft());
//		assertEquals(NUM_BASE_RESOURCES+1, model.getResources().size());
//		
//		// Now deselect non-base 
//		generator.deselect(nonBase);
//		assertEquals(MAX_POINTS, generator.getPointsLeft());
//		assertEquals(NUM_BASE_RESOURCES, model.getResources().size());
	}

	//-------------------------------------------------------------------
	@Test
	public void testSelectManualAndThanAutomatic() {
		System.out.println("testSelectManualAndThanAutomatic");
		ResourceReference nonBase = generator.openResource(nonBaseResource1);
		ResourceModification mod1 = new ResourceModification(nonBaseResource1, 2);
		generator.addModification(mod1);

		assertEquals(MAX_POINTS-3, generator.getPointsLeft());
		assertEquals(NUM_BASE_RESOURCES+1, model.getResources().size());
		assertEquals(1+mod1.getValue(), generator.getFirstValueFor(nonBaseResource1).getValue());

		// Deselect again - no only modification remains
		generator.decrease(nonBase);
		assertEquals(MAX_POINTS-2, generator.getPointsLeft());
		assertEquals(NUM_BASE_RESOURCES+1, model.getResources().size());
		assertEquals(mod1.getValue(), generator.getFirstValueFor(nonBaseResource1).getValue());
	}

	//-------------------------------------------------------------------
	@Test
	public void testIncreaseMoreThanCostAllowed() {
		ResourceReference base2 = generator.getFirstValueFor(resource2);
		ResourceReference base4 = generator.getFirstValueFor(resource3);
		generator.increase(base2);
		generator.increase(base4);
		while (generator.getPointsLeft()>0) {
			if (generator.canBeIncreased(base2))
				generator.increase(base2);
			if (generator.canBeIncreased(base4))
				generator.increase(base4);
		}
		assertEquals(0, generator.getPointsLeft());
		// The following increase should not be possible
		generator.increase(base4);
		assertEquals(0, generator.getPointsLeft());
		generator.increase(base2);
		assertEquals(0, generator.getPointsLeft());
	}

	//-------------------------------------------------------------------
	@Test
	public void increaseMoreThanValueAllowed() {
		System.out.println("increaseMoreThanValueAllowed");
		ResourceReference base2 = generator.openResource(resource2);

		ResourceModification mod1 = new ResourceModification(resource2, 3);
		generator.addModification(mod1);
		assertEquals(3, generator.getFirstValueFor(resource2).getValue());
	
		generator.increase(base2);
		assertEquals("Increase of base resource failed", 4, base2.getValue());
		assertEquals(4, generator.getFirstValueFor(resource2).getValue());
		// The following increase should not be possible
		generator.increase(base2);
		assertEquals(4, generator.getFirstValueFor(resource2).getValue());
	}

	//-------------------------------------------------------------------
	@Test
	public void testDecreaseBasic() {
		System.out.println("testDecreaseBasic");
		ResourceReference base2 = generator.openResource(resource2);
		assertNotNull(base2);
		assertNotNull(base2.getResource());
		// Should not work
		generator.decrease(base2);
		assertEquals(MAX_POINTS, generator.getPointsLeft());
		assertEquals(0, generator.getFirstValueFor(resource2).getValue());
		// Should work
		generator.setAllowMaxResources(true);
		generator.decrease(base2);
		assertEquals(MAX_POINTS+1, generator.getPointsLeft());
		assertEquals(-1, generator.getFirstValueFor(resource2).getValue());
	}

	//-------------------------------------------------------------------
	@Test
	public void testSplit() {
		System.out.println("testSplit");
		
		ResourceReference base1 = generator.openResource(resource2);
		ResourceReference nonBase1 = generator.openResource(nonBaseResource1);
		
		assertFalse("May not split base resources",generator.canBeSplit(base1));
		assertNull(generator.split(nonBase1));
		assertFalse("May not split resources with value <2",generator.canBeSplit(base1));
		assertEquals(NUM_BASE_RESOURCES+1, model.getResources().size());
		
		generator.increase(nonBase1); // Should be splittable now
		assertEquals(2, nonBase1.getValue());
		ResourceReference splitted = generator.split(nonBase1);
		assertNotNull(splitted);
		assertEquals(1, nonBase1.getValue());
		assertEquals(1, splitted.getValue());
		assertFalse(generator.canBeSplit(base1));
		assertFalse(generator.canBeSplit(splitted));
	}

	//-------------------------------------------------------------------
	@Test
	public void testValidJoin() {
		System.out.println("testJoin");
		
		generator.addModification(new ResourceModification(resource2, 1));
		ResourceReference base1    = generator.getFirstValueFor(resource2);
		ResourceReference nonBase1 = generator.openResource(nonBaseResource1);
		ResourceReference nonBase2 = generator.openResource(nonBaseResource1);
		assertNotSame(nonBase1,nonBase2);
		
		assertFalse(generator.canBeJoined(base1, nonBase1));
		assertFalse(generator.canBeJoined(nonBase1, base1));
		assertTrue(generator.canBeJoined(nonBase1, nonBase2));
		
		generator.join(nonBase1, nonBase2);
		assertEquals(2, nonBase1.getValue());
	}

	//-------------------------------------------------------------------
	@Test
	public void testInvalidJoin() {
		System.out.println("testInvalidJoin");
		
		// Joining a single base resources should not be possible
		assertFalse(generator.canBeJoined(generator.getFirstValueFor(resource2)));
		
		generator.addModification(new ResourceModification(nonBaseResource1, 4));
		ResourceReference nonBase1 = generator.getFirstValueFor(nonBaseResource1);
		ResourceReference nonBase2 = generator.split(nonBase1);
		assertNotNull("Splitting failed",nonBase2);
		assertEquals(3, nonBase1.getValue());
		assertEquals(1, nonBase2.getValue());
		assertTrue(generator.canBeJoined(nonBase1, nonBase2));
		assertEquals(NUM_BASE_RESOURCES+2, model.getResources().size());

		// Raising one resource should result in a value>4 which is not allowed
		generator.increase(nonBase2);
		assertEquals(2, nonBase2.getValue());
		generator.setAllowMaxResources(false);
		assertFalse(generator.canBeJoined(nonBase1, nonBase2));
		generator.join(nonBase1, nonBase2); // Should not work
		assertEquals(NUM_BASE_RESOURCES+2, model.getResources().size());
		
		// Allowing extreme resources
		generator.setAllowMaxResources(true);
		assertTrue(generator.canBeJoined(nonBase1, nonBase2));
		
		generator.join(nonBase1, nonBase2); // Should work now
		assertEquals(5, nonBase1.getValue());
		assertEquals(NUM_BASE_RESOURCES+1, model.getResources().size());
	}

}
