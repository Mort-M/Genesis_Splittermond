/**
 * 
 */
package org.prelle.splimo.items;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.prelle.splimo.Resource;
import org.prelle.splimo.ResourceReference;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.equip.ItemLevellerAndGenerator;
import org.prelle.splimo.items.Enhancement.EnhancementType;

import de.rpgframework.RPGFrameworkLoader;

/**
 * @author prelle
 *
 */
@FixMethodOrder
public class ItemControllerTest {

	private final static Logger logger = Logger.getLogger("junit.power");

	private static ItemTemplate DAGGER;
	private static Enhancement SPEED;
	private static Enhancement DAMAGE;
	private static Enhancement DMGREDUC;
	private static Enhancement HANDICAP;
	private static Enhancement RELICSONLY;
	private ItemLevellerAndGenerator generator;
	private CarriedItem model;

	private int MAX = Integer.MAX_VALUE;
	
	//-------------------------------------------------------------------
	static {
		RELICSONLY = new Enhancement("reliconly", 2, null, EnhancementType.RELIC);
		PropertyConfigurator.configure("log4j.properties");
		RPGFrameworkLoader.getInstance();
		SplitterMondCore.addEnhancement(RELICSONLY);

		DAGGER  = SplitterMondCore.getItem("dagger");
		SPEED   = SplitterMondCore.getEnhancement("speed");
		DAMAGE  = SplitterMondCore.getEnhancement("damage");
		DMGREDUC= SplitterMondCore.getEnhancement("damagereduction");
		HANDICAP= SplitterMondCore.getEnhancement("handicap");
	}

	//-------------------------------------------------------------------
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		MAX = Integer.MAX_VALUE;
		model = new CarriedItem(DAGGER);
		generator = new ItemLevellerAndGenerator(model, MAX);
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
		assertEquals(0, generator.getPointsLeft());
		assertEquals(0, model.getArtifactQuality());
		assertEquals(0, model.getItemQuality());
		
		List<Enhancement> avail = generator.getAvailableEnhancements();
		assertTrue(avail.contains(SPEED));
		assertFalse(avail.contains(HANDICAP));
	}

	//-------------------------------------------------------------------
	@Test
	public void testEnhancementsOnNonRelic() {
		logger.debug("------testEnhancementsNonRelic----------");
		
		assertTrue(generator.canBeAdded(SPEED));
		assertTrue(generator.canBeAdded(DAMAGE));
		assertFalse(generator.canBeAdded(DMGREDUC));
		assertFalse(generator.canBeAdded(RELICSONLY));
		assertFalse(generator.canBeAdded(HANDICAP));
	}

	//-------------------------------------------------------------------
	@Test
	public void testEnhancementsOnRelic() {
		logger.debug("------testEnhancementsRelic----------");
		
		model.setResource(new ResourceReference(new Resource(), 3));
		
		assertTrue(generator.canBeAdded(SPEED));
		assertTrue(generator.canBeAdded(DAMAGE));
		assertFalse(generator.canBeAdded(DMGREDUC));
		assertTrue(generator.canBeAdded(RELICSONLY));
		assertFalse(generator.canBeAdded(HANDICAP));
	}

	//-------------------------------------------------------------------
	@Test
	public void testMaxCountLimits() {
		logger.debug("------testMaxCountLimits----------");
		
		assertTrue(generator.canBeAdded(DAMAGE));
		assertNotNull(generator.addEnhancement(DAMAGE));
		assertTrue(generator.canBeAdded(DAMAGE));
		assertNotNull(generator.addEnhancement(DAMAGE));
		assertFalse(generator.canBeAdded(DAMAGE));
		assertNull(generator.addEnhancement(DAMAGE));
		
		assertEquals(DAMAGE.getSize()*2, model.getItemQuality());
	}

}
