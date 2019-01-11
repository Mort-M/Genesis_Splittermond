/**
 * 
 */
package org.prelle.splimo.chargen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.prelle.rpgframework.splittermond.SplittermondRules;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.AttributeValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.modifications.AttributeModification;

/**
 * @author prelle
 *
 */
public class AttributeGeneratorTest implements GenerationEventListener {

	private AttributeGenerator generator;
	private SpliMoCharacter model;
	private List<GenerationEvent> events;

	//-------------------------------------------------------------------
	static {

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
		model     = new SpliMoCharacter();
		generator = new AttributeGenerator(model);
		events    = new ArrayList<GenerationEvent>();
		GenerationEventDispatcher.addListener(this);
	}

	//-------------------------------------------------------------------
	@Test
	public void testEmpty() {
		System.out.println("---------testEmpty-----------------------");
		for (Attribute key : Attribute.primaryValues()) {
			assertEquals(0,model.getAttribute(key).getModifier());
			assertEquals(0,model.getAttribute(key).getStart());
			assertEquals(2,model.getAttribute(key).getDistributed());
			assertEquals(2,model.getAttribute(key).getValue());
		}
		
		assertEquals(0,model.getAttribute(Attribute.SIZE).getDistributed());
		assertEquals(0,model.getAttribute(Attribute.SIZE).getValue());
		// Speed
		assertEquals(2,model.getAttribute(Attribute.SPEED).getDistributed());
		assertEquals(2,model.getAttribute(Attribute.SPEED).getValue());
		// Initiative
		assertEquals(8,model.getAttribute(Attribute.INITIATIVE).getDistributed());
		assertEquals(8,model.getAttribute(Attribute.INITIATIVE).getValue());
		// Focus
		assertEquals(8,model.getAttribute(Attribute.FOCUS).getDistributed());
		assertEquals(8,model.getAttribute(Attribute.FOCUS).getValue());
		// Defense
		assertEquals(16,model.getAttribute(Attribute.DEFENSE).getDistributed());
		assertEquals(16,model.getAttribute(Attribute.DEFENSE).getValue());
		// MindResist
		assertEquals(16,model.getAttribute(Attribute.MINDRESIST).getDistributed());
		assertEquals(16,model.getAttribute(Attribute.MINDRESIST).getValue());
		// BodyResist
		assertEquals(16,model.getAttribute(Attribute.BODYRESIST).getDistributed());
		assertEquals(16,model.getAttribute(Attribute.BODYRESIST).getValue());
		// Splinter
		assertEquals(3,model.getAttribute(Attribute.SPLINTER).getDistributed());
		assertEquals(3,model.getAttribute(Attribute.SPLINTER).getValue());
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		events.add(event);
	}

	//-------------------------------------------------------------------
	@Test
	public void testEventMechanism() {
		System.out.println("---------testEventMechanism-----------------------");
		generator.increase(Attribute.CHARISMA);
		assertEquals(2, events.size());
		assertEquals(events.get(0).getType(), GenerationEventType.ATTRIBUTE_CHANGED);
		assertEquals(events.get(1).getType(), GenerationEventType.POINTS_LEFT_ATTRIBUTES);
		assertEquals(3, ((AttributeValue)events.get(0).getValue()).getDistributed());
		assertEquals(3, ((AttributeValue)events.get(0).getValue()).getValue());
	}

	//-------------------------------------------------------------------
	@Test
	public void testIncrease() {
		System.out.println("---------testIncrease-----------------------");
		assertEquals(2,model.getAttribute(Attribute.CHARISMA).getValue());
		assertTrue(generator.canBeIncreased(Attribute.CHARISMA));

		generator.increase(Attribute.CHARISMA);
		assertEquals(3,model.getAttribute(Attribute.CHARISMA).getValue());

		assertFalse(generator.canBeIncreased(Attribute.CHARISMA));
		events.clear();
		generator.increase(Attribute.CHARISMA);
		assertEquals(3,model.getAttribute(Attribute.CHARISMA).getValue());
		assertEquals(0, events.size());
	}

	//-------------------------------------------------------------------
	@Test
	public void testDecrease() {
		System.out.println("---------testDecrease-----------------------");
		assertEquals(2,model.getAttribute(Attribute.CHARISMA).getValue());
		assertTrue(generator.canBeDecreased(Attribute.CHARISMA));

		generator.decrease(Attribute.CHARISMA);
		assertEquals(1,model.getAttribute(Attribute.CHARISMA).getValue());

		generator.decrease(Attribute.CHARISMA);
		assertEquals(0,model.getAttribute(Attribute.CHARISMA).getValue());

		assertFalse(generator.canBeDecreased(Attribute.CHARISMA));
		events.clear();
		generator.decrease(Attribute.CHARISMA);
		assertEquals(0,model.getAttribute(Attribute.CHARISMA).getValue());
		assertEquals(0, events.size());
	}

	//-------------------------------------------------------------------
	@Test
	public void testDerived() {
		System.out.println("---------testDerived-----------------------");
		generator.increase(Attribute.AGILITY);
		assertEquals(3,model.getAttribute(Attribute.SPEED).getDistributed());  // Size = 0
		assertEquals(0,model.getAttribute(Attribute.SPEED).getModifier());
		assertEquals(3,model.getAttribute(Attribute.SPEED).getValue());  // Size = 0
		assertEquals(0,model.getAttribute(Attribute.SPEED).getStart());
		assertEquals(4, events.size());  // Agility, Speed, Defense
		events.clear();
		
		// Now add modification
		System.out.println("---------testDerived2-----------------------");
		generator.addModification(new AttributeModification(Attribute.SPEED, 1));
		assertEquals(3,model.getAttribute(Attribute.SPEED).getDistributed());  // Size = 0
		assertEquals(1,model.getAttribute(Attribute.SPEED).getModifier());
		assertEquals(4,model.getAttribute(Attribute.SPEED).getValue());  // Size = 0
		assertEquals(0,model.getAttribute(Attribute.SPEED).getStart());
		assertEquals(1, events.size());  // Speed
		events.clear();
		
		// Now add another modification
		System.out.println("---------testDerived3-----------------------");
		generator.addModification(new AttributeModification(Attribute.WILLPOWER, 1));
		assertEquals(2,model.getAttribute(Attribute.WILLPOWER).getDistributed()); 
		assertEquals(1,model.getAttribute(Attribute.WILLPOWER).getModifier());
		assertEquals(3,model.getAttribute(Attribute.WILLPOWER).getValue()); 
		assertEquals(0,model.getAttribute(Attribute.WILLPOWER).getStart());
		assertEquals(4, events.size());  // Willpower, Focus, Body, Mind
		events.clear();
	}

}
