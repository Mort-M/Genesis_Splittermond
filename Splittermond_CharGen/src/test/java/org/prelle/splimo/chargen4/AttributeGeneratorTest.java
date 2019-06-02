package org.prelle.splimo.chargen4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.prelle.rpgframework.splittermond.data.SplittermondDataPlugin;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.DummyRulePlugin;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl4.NewAttributeGenerator;
import org.prelle.splimo.charctrl4.SplitterEngineCharacterGenerator;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.modifications.AttributeModification;

import de.rpgframework.genericrpg.modification.DecisionToMake;
import de.rpgframework.genericrpg.modification.Modification;

public class AttributeGeneratorTest {
	
	private SpliMoCharacter testModel;
	private NewAttributeGenerator attrGen;
	private SplitterEngineCharacterGenerator parent;
	private List<Modification> previous;
	
	//-------------------------------------------------------------------
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
				attrGen.process(model, previous);
			}
		};
		GenerationEventDispatcher.clear();
		attrGen = new NewAttributeGenerator(parent, 18);
	}

	//-------------------------------------------------------------------
	@Test
	public void testIdle() {
		assertEquals(18, attrGen.getPointsLeft());
		assertTrue(attrGen.getToDos().isEmpty());
		attrGen.process(testModel, new ArrayList<>());
		assertEquals(10, attrGen.getPointsLeft());
		assertFalse(attrGen.getToDos().isEmpty());
		assertEquals(15, testModel.getExperienceFree());
		
		assertEquals(1, testModel.getAttribute(Attribute.AGILITY).getDistributed());
		assertEquals(1, testModel.getAttribute(Attribute.AGILITY).getValue());
	}

	//-------------------------------------------------------------------
	@Test
	public void testIncreasing() {
		Attribute key = Attribute.AGILITY;
		assertTrue(attrGen.canBeIncreased(key));
		assertTrue(attrGen.increase(key)); // 2
		assertTrue(attrGen.canBeIncreased(key));
		assertTrue(attrGen.increase(key)); // 3
		assertEquals(15, testModel.getExperienceFree());
		assertEquals(3, testModel.getAttribute(key).getDistributed());
		assertEquals(3, testModel.getAttribute(key).getValue());
		assertEquals(3, testModel.getAttribute(key).getStart());
		assertEquals(1, attrGen.getToDos().size());
		// Now increasing works only with exp
		assertTrue("Increase attribute with exp fails", attrGen.canBeIncreased(key));
		assertTrue(attrGen.increase(key)); // 4
		assertEquals(5, testModel.getExperienceFree());
		assertEquals(4, testModel.getAttribute(key).getDistributed());
		assertEquals(4, testModel.getAttribute(key).getValue());
		assertEquals(3, testModel.getAttribute(key).getStart());
		assertEquals(2, attrGen.getToDos().size());
		// Now increasing wont work anymore
		assertFalse(attrGen.canBeIncreased(key));
		assertFalse(attrGen.increase(key)); // 4
		assertEquals(5, testModel.getExperienceFree());
		assertEquals(4, testModel.getAttribute(key).getDistributed());
		assertEquals(4, testModel.getAttribute(key).getValue());
		assertEquals(3, testModel.getAttribute(key).getStart());
		assertEquals(2, attrGen.getToDos().size());
	}

	//-------------------------------------------------------------------
	@Test
	public void testIncreasingWithNotEnoughPoints() {
		// Spend all 18 points
		testModel.getAttribute(Attribute.CHARISMA    ).setDistributed(2);
		testModel.getAttribute(Attribute.AGILITY     ).setDistributed(2);
		testModel.getAttribute(Attribute.INTUITION   ).setDistributed(3);
		testModel.getAttribute(Attribute.CONSTITUTION).setDistributed(3);
		testModel.getAttribute(Attribute.MYSTIC      ).setDistributed(2);
		testModel.getAttribute(Attribute.STRENGTH    ).setDistributed(2);
		testModel.getAttribute(Attribute.MIND        ).setDistributed(2);
		testModel.getAttribute(Attribute.WILLPOWER   ).setDistributed(2);
		attrGen.process(testModel, previous);
		assertEquals(0, attrGen.getToDos().size());
		
		// Now try to increase AGILITY, which is below 3 but there are not enough points left, so exp is needed
		Attribute key = Attribute.AGILITY;
		assertTrue("Increase attribute with exp fails", attrGen.canBeIncreased(key));
		assertTrue(attrGen.increase(key)); // 3
		assertEquals(5, testModel.getExperienceFree());
		assertEquals(3, testModel.getAttribute(key).getDistributed());
		assertEquals(3, testModel.getAttribute(key).getValue());
		assertEquals(3, testModel.getAttribute(key).getStart());
		assertEquals(2, testModel.getAttribute(Attribute.WILLPOWER).getDistributed());
		assertEquals(2, testModel.getAttribute(Attribute.WILLPOWER).getValue());
		assertEquals(1, testModel.getAttribute(Attribute.WILLPOWER).getStart());
		assertEquals(1, attrGen.getToDos().size());
		// Now increasing wont work anymore on AGILITY or any other Attribute
		assertFalse(attrGen.canBeIncreased(Attribute.CHARISMA));
		assertFalse(attrGen.canBeIncreased(key));
		assertFalse(attrGen.increase(key)); // 4
		assertEquals(5, testModel.getExperienceFree());
		assertEquals(3, testModel.getAttribute(key).getDistributed());
		assertEquals(3, testModel.getAttribute(key).getValue());
		assertEquals(3, testModel.getAttribute(key).getStart());
		assertEquals(1, attrGen.getToDos().size());
	}

	//-------------------------------------------------------------------
	@Test
	public void testIncreasingWithModification() {
		Attribute key = Attribute.AGILITY;
		AttributeModification mod = new AttributeModification(key, 1);
		previous.add(mod);
		attrGen.process(testModel, previous);
		
		assertTrue(attrGen.canBeIncreased(key));
		assertTrue(attrGen.increase(key)); // 2
		assertTrue(attrGen.canBeIncreased(key));
		assertTrue(attrGen.increase(key)); // 3
		assertEquals(15, testModel.getExperienceFree());
		assertEquals(3, testModel.getAttribute(key).getDistributed());
		assertEquals(4, testModel.getAttribute(key).getValue());
		assertEquals(4, testModel.getAttribute(key).getStart());
		assertEquals(1, attrGen.getToDos().size());
		// Now increasing works only with exp
		assertTrue("Increase attribute with exp fails", attrGen.canBeIncreased(key));
		assertTrue(attrGen.increase(key)); // 4
		assertEquals(5, testModel.getExperienceFree());
		assertEquals(4, testModel.getAttribute(key).getDistributed());
		assertEquals(5, testModel.getAttribute(key).getValue());
		assertEquals(4, testModel.getAttribute(key).getStart());
		assertEquals(2, attrGen.getToDos().size());
		// Now increasing wont work anymore
		assertFalse(attrGen.canBeIncreased(key));
		assertFalse(attrGen.increase(key)); // 4
		assertEquals(5, testModel.getExperienceFree());
		assertEquals(4, testModel.getAttribute(key).getDistributed());
		assertEquals(5, testModel.getAttribute(key).getValue());
		assertEquals(4, testModel.getAttribute(key).getStart());
		assertEquals(2, attrGen.getToDos().size());
	}

	//-------------------------------------------------------------------
	@Test
	public void testDecreasing() {
		Attribute key = Attribute.AGILITY;
		testModel.getAttribute(key).setDistributed(3);
		attrGen.process(testModel, new ArrayList<>());
		
		assertTrue(attrGen.canBeDecreased(key));
		assertTrue(attrGen.decrease(key)); // 2
		assertEquals(2, testModel.getAttribute(Attribute.AGILITY).getDistributed());
		assertEquals(2, testModel.getAttribute(Attribute.AGILITY).getValue());
		assertTrue(attrGen.canBeDecreased(key));
		assertTrue(attrGen.decrease(key)); // 1
		assertEquals(1, testModel.getAttribute(Attribute.AGILITY).getDistributed());
		assertEquals(1, testModel.getAttribute(Attribute.AGILITY).getValue());
		// Should not be able to decrease below 1
		assertFalse(attrGen.canBeDecreased(key));
		assertFalse(attrGen.decrease(key)); // 1
		assertEquals(1, testModel.getAttribute(Attribute.AGILITY).getDistributed());
		assertEquals(1, testModel.getAttribute(Attribute.AGILITY).getValue());
	}

	//-------------------------------------------------------------------
	@Test
	public void testDecreasingWithModification() {
		Attribute key = Attribute.AGILITY;
		AttributeModification mod = new AttributeModification(key, -1);
		previous.add(mod);
		attrGen.process(testModel, previous);
		
		testModel.getAttribute(key).setDistributed(3);
		attrGen.process(testModel, previous);
		
		assertTrue(attrGen.canBeDecreased(key));
		assertTrue(attrGen.decrease(key)); // 2
		assertEquals(2, testModel.getAttribute(Attribute.AGILITY).getDistributed());
		assertEquals(1, testModel.getAttribute(Attribute.AGILITY).getValue());
		assertTrue(attrGen.canBeDecreased(key));
		assertTrue(attrGen.decrease(key)); // 1
		assertEquals(1, testModel.getAttribute(Attribute.AGILITY).getDistributed());
		assertEquals(0, testModel.getAttribute(Attribute.AGILITY).getValue());
		// Should not be able to decrease below 1
		assertFalse(attrGen.canBeDecreased(key));
		assertFalse(attrGen.decrease(key)); // 1
		assertEquals(1, testModel.getAttribute(Attribute.AGILITY).getDistributed());
		assertEquals(0, testModel.getAttribute(Attribute.AGILITY).getValue());
	}
	
}
