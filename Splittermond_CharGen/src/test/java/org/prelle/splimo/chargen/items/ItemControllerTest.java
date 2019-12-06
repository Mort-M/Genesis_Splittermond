/**
 *
 */
package org.prelle.splimo.chargen.items;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.prelle.rpgframework.splittermond.data.SplittermondDataPlugin;
import org.prelle.splimo.Resource;
import org.prelle.splimo.ResourceReference;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.equip.ItemLevellerAndGenerator;
import org.prelle.splimo.items.CarriedItem;
import org.prelle.splimo.items.Enhancement;
import org.prelle.splimo.items.Enhancement.EnhancementType;
import org.prelle.splimo.items.ItemTemplate;
import org.prelle.splimo.items.Material;

/**
 * @author prelle
 *
 */
@FixMethodOrder
public class ItemControllerTest {

	private static ItemTemplate DAGGER;
	private static Enhancement SPEED;
	private static Enhancement DAMAGE;
	private static Enhancement DMGREDUC;
	private static Enhancement HANDICAP;
	private static Enhancement SPELL1;
	private static Enhancement RELICSONLY;

	private static Material FAIRYTWINE;
	private static Material JADEIRON;
	private static Material MOONSTEEL;
	private ItemLevellerAndGenerator generator;
	private CarriedItem model;

	private int MAX = Integer.MAX_VALUE;

	//-------------------------------------------------------------------
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		RELICSONLY = new Enhancement("reliconly", 2, null, EnhancementType.RELIC);
		SplittermondDataPlugin plugin = new SplittermondDataPlugin();
		plugin.init( (percent) -> {});
//		SplitterMondCore.initialize(new SplittermondRules());
//		(new MondstahlklingenPlugin()).init();
		SplitterMondCore.addEnhancement(RELICSONLY);

		DAGGER  = SplitterMondCore.getItem("dagger");
		SPEED   = SplitterMondCore.getEnhancement("speed");
		DAMAGE  = SplitterMondCore.getEnhancement("damage");
		DMGREDUC= SplitterMondCore.getEnhancement("damagereduction");
		HANDICAP= SplitterMondCore.getEnhancement("handicap");
		SPELL1  = SplitterMondCore.getEnhancement("embedspell1");
		FAIRYTWINE = SplitterMondCore.getMaterial("fairytwine");
		JADEIRON   = SplitterMondCore.getMaterial("jadeiron");
		MOONSTEEL  = SplitterMondCore.getMaterial("moonsteel");
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
		assertEquals(0, generator.getPointsLeft());
		assertEquals(0, model.getArtifactQuality());
		assertEquals(0, model.getItemQuality());
		assertEquals(0, model.getRelicQuality());

		List<Enhancement> avail = generator.getAvailableEnhancements();
		assertTrue(avail.contains(SPEED));
		assertFalse(avail.contains(HANDICAP));
	}

	//-------------------------------------------------------------------
	@Test
	public void testEnhancementsOnNonRelic() {
		assertTrue(generator.canBeAdded(SPEED));
		assertTrue(generator.canBeAdded(DAMAGE));
		assertFalse(generator.canBeAdded(DMGREDUC));
		assertFalse(generator.canBeAdded(RELICSONLY));
		assertFalse(generator.canBeAdded(HANDICAP));
		assertTrue(generator.canBeAdded(SPELL1));
	}

	//-------------------------------------------------------------------
	@Test
	public void testEnhancementsOnRelic() {
		model.setResource(new ResourceReference(new Resource(), 3));

		assertTrue(generator.canBeAdded(SPEED));
		assertTrue(generator.canBeAdded(DAMAGE));
		assertFalse(generator.canBeAdded(DMGREDUC));
		assertTrue(generator.canBeAdded(RELICSONLY));
		assertFalse(generator.canBeAdded(HANDICAP));
		assertTrue(generator.canBeAdded(SPELL1));
	}

	//-------------------------------------------------------------------
	@Test
	public void testMaxCountLimits() {
		assertTrue(generator.canBeAdded(DAMAGE));
		assertNotNull(generator.addEnhancement(DAMAGE));
		assertTrue(generator.canBeAdded(DAMAGE));
		assertNotNull(generator.addEnhancement(DAMAGE));
		assertFalse(generator.canBeAdded(DAMAGE));
		assertNull(generator.addEnhancement(DAMAGE));

		assertEquals(DAMAGE.getSize()*2, model.getItemQuality());
	}

	//-------------------------------------------------------------------
	@Test
	public void testNonAvailableEnhancements() {
		assertTrue(generator.canBeAdded(DAMAGE));
		assertNotNull(generator.addEnhancement(DAMAGE));
		assertTrue(generator.canBeAdded(DAMAGE));
		assertNotNull(generator.addEnhancement(DAMAGE));
		assertFalse(generator.canBeAdded(DAMAGE));
		assertNull(generator.addEnhancement(DAMAGE));

		assertEquals(DAMAGE.getSize()*2, model.getItemQuality());
	}

	//-------------------------------------------------------------------
	@Test
	public void testPointsCalculations() {
		assertNotNull(generator.addEnhancement(SPELL1));
		assertNotNull(generator.addEnhancement(DAMAGE));
//		assertNotNull(generator.addEnhancement(DAMAGE));

		assertEquals(2,model.getArtifactQuality());
		assertEquals(1,model.getItemQuality());
		assertEquals(3,model.getRelicQuality());

		// Add material
		System.out.println("Available Materials = "+generator.getAvailableMaterials());
		generator.setMaterial(JADEIRON);
		assertEquals(2,model.getArtifactQuality());
		assertEquals(3,model.getItemQuality());  // min. 3 like material quality
		assertEquals(6,model.getRelicQuality()); // 2 (artifact), 3 (item), 1 (material)
	}

}
