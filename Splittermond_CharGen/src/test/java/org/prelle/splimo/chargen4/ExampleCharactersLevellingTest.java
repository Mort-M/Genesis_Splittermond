package org.prelle.splimo.chargen4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.prelle.rpgframework.splittermond.data.SplittermondDataPlugin;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.RewardImpl;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.charctrl4.NewSpliMoCharacterLeveller;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;

import de.rpgframework.genericrpg.Reward;

/**
 * @author prelle
 *
 */
public class ExampleCharactersLevellingTest { 

	private ExampleCharactersTest generator;
	private NewSpliMoCharacterLeveller charGen;
	private SpliMoCharacter model;
	
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
		generator = new ExampleCharactersTest();
		generator.setUp();
		generator.generateTiai();	
		System.err.println("model = "+generator.getModel().dump());
		charGen = new NewSpliMoCharacterLeveller(generator.getModel());
		model = charGen.getModel();
		System.err.println("model = "+model.dump());
		GenerationEventDispatcher.clear();
	}

	//-------------------------------------------------------------------
	@Test
	public void testAttributes() {
		assertEquals(0, model.getExperienceFree());
		assertEquals(15, model.getExperienceInvested());
		
		assertEquals(5, model.getAttribute(Attribute.AGILITY).getDistributed());
		assertEquals(3, model.getAttribute(Attribute.CHARISMA).getDistributed());
		assertFalse(charGen.getAttributeController().canBeIncreased(Attribute.CHARISMA));

		Reward reward = new RewardImpl(15, "UnitTest");
		model.addReward(reward);
		model.setExperienceFree(15);
		charGen.runProcessors();
		
		assertTrue(charGen.getAttributeController().canBeIncreased(Attribute.CHARISMA));
		assertFalse(charGen.getAttributeController().canBeDecreased(Attribute.CHARISMA));
		assertTrue(charGen.getAttributeController().increase(Attribute.CHARISMA));
		assertEquals(5, model.getExperienceFree());
		assertEquals(25, model.getExperienceInvested());
		
//		System.out.println(model.dump());
	}

//	//-------------------------------------------------------------------
//	/**
//	 * @see org.prelle.shadowrun.gen.SR5LetUserChooseListener#letUserChoose(java.lang.String, org.prelle.shadowrun.modifications.ModificationChoice)
//	 */
//	@Override
//	public Collection<Modification> letUserChoose(String choiceReason, ModificationChoice choice) {
//		// TODO Auto-generated method stub
//		System.out.println("Because of '"+choiceReason+"' choose "+choice);
//		
//		if (choice.getNumberOfChoices()>0) {
//			System.out.println("Select "+choice.subList(0, choice.getNumberOfChoices()));
//			return choice.subList(0, choice.getNumberOfChoices());
//		}
//		
//		System.exit(0);
//		return null;
//	}

}
