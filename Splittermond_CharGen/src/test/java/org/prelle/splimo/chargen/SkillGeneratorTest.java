/**
 * 
 */
package org.prelle.splimo.chargen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.prelle.rpgframework.splittermond.SplittermondRules;
import org.prelle.rpgframework.splittermond.data.SplittermondDataPlugin;
import org.prelle.splimo.Skill;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.modifications.SkillModification;

/**
 * @author prelle
 *
 */
@FixMethodOrder(MethodSorters.JVM)
public class SkillGeneratorTest {
	
	private final static int MAX_POINTS = 3;

	private static Skill power1;
	private static Skill power2;
	private SkillValue power1Val;
	private SkillValue power2Val;
	private SkillGenerator generator;

	//-------------------------------------------------------------------
	static {
		power1 = SplitterMondCore.getSkill("blades");
		power2 = SplitterMondCore.getSkill("empathy");
	}

	//-------------------------------------------------------------------
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		SplittermondDataPlugin plugin = new SplittermondDataPlugin();
		plugin.init();
	}

	//-------------------------------------------------------------------
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		generator = new SkillGenerator(MAX_POINTS, new SpliMoCharacter(), null);
		power1Val = generator.getValueFor(power1);
		power2Val = generator.getValueFor(power2);
	}

	//-------------------------------------------------------------------
	@Test
	public void testIdleState() {
		assertEquals(3, generator.getPointsLeft());
		assertEquals(0,generator.getValueFor(power1).getValue());
		
		assertEquals(0,generator.getValueFor(power2).getValue());
	}

	//-------------------------------------------------------------------
	@Test
	public void testModifcationSelection() {
		assertEquals(0, generator.getValueFor(power1).getValue());
		SkillModification mod1 = new SkillModification(power1, 2);
		generator.addModification(mod1);
		assertEquals(2, generator.getValueFor(power1).getValue());
		generator.addModification(new SkillModification(power2, 1));
		assertEquals(2, generator.getValueFor(power1).getValue());
		assertEquals(1, generator.getValueFor(power2).getValue());
		assertEquals(MAX_POINTS-3, generator.getPointsLeft());
		
		generator.removeModification(mod1);
		assertEquals(MAX_POINTS-1, generator.getPointsLeft());
		assertEquals(0, generator.getValueFor(power1).getValue());
		assertEquals(1, generator.getValueFor(power2).getValue());
	}

	//-------------------------------------------------------------------
	@Test
	public void testDoubleModifcationSelection() {
		generator.addModification(new SkillModification(power1, 2));
		generator.addModification(new SkillModification(power1, 2));
		assertEquals(0, generator.getPointsLeft());
		assertEquals(3, generator.getValueFor(power1).getValue());
	}

	//-------------------------------------------------------------------
	@Test
	public void testManualSelection() {
		assertTrue(generator.canBeIncreased(power1Val));
		assertFalse(generator.canBeDecreased(power1Val));
		
		generator.increase(power1Val);
		assertEquals(2, generator.getPointsLeft());
		assertEquals(1, generator.getValueFor(power1).getValue());
		assertTrue(generator.canBeDecreased(power1Val));

		// Deselect again
		generator.decrease(power1Val);
		assertEquals(3, generator.getPointsLeft());
		assertEquals(0, generator.getValueFor(power1).getValue());
		assertFalse(generator.canBeDecreased(power1Val));
	}

	//-------------------------------------------------------------------
	@Test
	public void testManualDoubleSelection() {
		generator.increase(power1Val);
		generator.increase(power1Val);
		assertEquals(1, generator.getPointsLeft());
		assertEquals(2, generator.getValueFor(power1).getValue());
		assertTrue(generator.canBeDecreased(power1Val));

		// Deselect again
		generator.decrease(power1Val);
		generator.decrease(power1Val);
		assertEquals(3, generator.getPointsLeft());
		assertEquals(0, generator.getValueFor(power1).getValue());
		assertFalse(generator.canBeDecreased(power1Val));
	}

	//-------------------------------------------------------------------
	@Test
	public void testSelectMoreThanAllowed() {
		generator.increase(power1Val);
		generator.increase(power1Val);
		generator.increase(power1Val);
		assertFalse(generator.canBeIncreased(power1Val));
		assertEquals(0, generator.getPointsLeft());
		assertEquals(3, generator.getValueFor(power1).getValue());
		// Select more than allowed
		generator.increase(power1Val);
		assertEquals(0, generator.getPointsLeft());
		assertEquals(3, generator.getValueFor(power1).getValue());
	}

	//-------------------------------------------------------------------
	@Test
	public void testSelectManualAndThanAutomatic() {
		System.out.println("--------------------------");
		SkillModification mod1 = new SkillModification(power1, 2);
		SkillValue ref = generator.getValueFor(power1);
		
		generator.increase(power1Val);
		assertEquals(2, generator.getPointsLeft());
		assertEquals(1, generator.getValueFor(power1).getValue());
		generator.addModification(mod1);
		assertEquals(3, generator.getValueFor(power1).getValue());
		assertEquals(3, ref.getValue());
		assertEquals(MAX_POINTS-3, generator.getPointsLeft());
		assertEquals(3, generator.getValueFor(power1).getValue());
		
		// Now remove automatic mod
		generator.removeModification(mod1);
		assertEquals(2, generator.getPointsLeft());
		assertEquals(1, generator.getValueFor(power1).getValue());
	}

}
