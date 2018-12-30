/**
 * 
 */
package org.prelle.splimo.charctrl4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.AttributeValue;
import org.prelle.splimo.Moonsign;
import org.prelle.splimo.PowerReference;
import org.prelle.splimo.ResourceReference;
import org.prelle.splimo.Skill;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SpliMoCharacter.Gender;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.processor.CalculateDerivedAttributesProcessor;
import org.prelle.splimo.processor.CalculateLevelProcessor;
import org.prelle.splimo.processor.ClearAllModificationsProcessor;
import org.prelle.splimo.processor.ModifyDerivedValuesByLevelProcessor;
import org.prelle.splimo.processor.SpliMoCharacterProcessor;

import de.rpgframework.RPGFrameworkLoader;
import de.rpgframework.character.Attachment;
import de.rpgframework.character.CharacterHandle;
import de.rpgframework.character.CharacterHandle.Format;
import de.rpgframework.character.CharacterHandle.Type;
import de.rpgframework.character.CharacterProvider;
import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;
import de.rpgframework.core.RoleplayingSystem;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.modification.DecisionToMake;
import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
/**
 * @author prelle
 *
 */
public class NewSpliMoCharacterLeveller extends SplitterEngineCharacterGenerator {
	
	private static Logger logger = Logger.getLogger("splittermond.chargen");
	
	private SpliMoCharacter model;
	
	//-------------------------------------------------------------------
	public NewSpliMoCharacterLeveller(SpliMoCharacter model) {
		this.model = model;
		
		attributes = new LevellingAttributeGenerator(this, 0);
		skills     = new NewSkillLeveller(this);
		master     = new NewMastershipLeveller(this, 3);
		resources  = new NewResourceLeveller(this, 8);
		processChain.clear();
		processChain.add( new ClearAllModificationsProcessor() );
		processChain.add( new CalculateDerivedAttributesProcessor() );
		processChain.add( new CalculateLevelProcessor() );
		processChain.add( new ModifyDerivedValuesByLevelProcessor() );
		processChain.add( (SpliMoCharacterProcessor) attributes );
//		processChain.add( new ResetModificationsOnGeneration());
//		processChain.add( (SpliMoCharacterProcessor) backgrounds );
//		processChain.add( (SpliMoCharacterProcessor) attributes );
		processChain.add( (SpliMoCharacterProcessor) skills );
		processChain.add( (SpliMoCharacterProcessor) resources );
		processChain.add( (SpliMoCharacterProcessor) master );
		processChain.add( (SpliMoCharacterProcessor) spells );
		
		runProcessors();
	}
	
	//--------------------------------------------------------------------
	/**
	 * @param selectedSplinter the selectedSplinter to set
	 */
	public void selectSplinter(Moonsign selectedSplinter) {
		model.setSplinter(selectedSplinter);
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.MOONSIGN_SELECTED, null));
		runProcessors();
	}

	//-------------------------------------------------------------------
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		model.setName(name);
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.BASE_DATA_CHANGED, null));
	}

	//-------------------------------------------------------------------
	/**
	 * @param imageBytes the imageBytes to set
	 */
	public void setImageBytes(byte[] imageBytes) {
		model.setImage(imageBytes);
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.BASE_DATA_CHANGED, null));
	}

	//-------------------------------------------------------------------
	/**
	 * @param hairColor the hairColor to set
	 */
	public void setHairColor(String hairColor) {
		model.setHairColor(hairColor);
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.BASE_DATA_CHANGED, null));
	}

	//-------------------------------------------------------------------
	/**
	 * @param eyeColor the eyeColor to set
	 */
	public void setEyeColor(String eyeColor) {
		model.setEyeColor(eyeColor);
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.BASE_DATA_CHANGED, null));
	}

	//-------------------------------------------------------------------
	/**
	 * @param furColor the furColor to set
	 */
	public void setFurColor(String furColor) {
		model.setFurColor(furColor);
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.BASE_DATA_CHANGED, null));
	}

	//-------------------------------------------------------------------
	/**
	 * @param birthPlace the birthPlace to set
	 */
	public void setBirthPlace(String birthPlace) {
		model.setBirthPlace(birthPlace);
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.BASE_DATA_CHANGED, null));
	}


	//-------------------------------------------------------------------
	/**
	 * @param gender the gender to set
	 */
	public void setGender(Gender gender) {
		model.setGender(gender);
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.BASE_DATA_CHANGED, null));
	}

	//-------------------------------------------------------------------
	public void setSize(int sizeInCM) {
		model.setSize(sizeInCM);
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.BASE_DATA_CHANGED, null));
	}


	//-------------------------------------------------------------------
	/**
	 * @param weight the weight to set
	 */
	public void setWeight(int weight) {
		model.setWeight(weight);
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.BASE_DATA_CHANGED, null));
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.CharacterController#getAttributeController()
	 */
	@Override
	public AttributeController getAttributeController() {
		return attributes;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.Controller#getToDos()
	 */
	@Override
	public List<ToDoElement> getToDos() {
		List<ToDoElement> ret = new ArrayList<>();
		for (SpliMoCharacterProcessor step : processChain) {
			if (step instanceof Controller) {
				ret.addAll( ((Controller)step).getToDos() );
			}
		}
//		ret.addAll(((Controller)races).getToDos());
//		ret.addAll(((Controller)cultures).getToDos());
//		ret.addAll(((Controller)backgrounds).getToDos());
//		ret.addAll(((Controller)educations).getToDos());
//		ret.addAll(((Controller)powers).getToDos());
//		ret.addAll(((Controller)attributes).getToDos());
//		ret.addAll(((Controller)resources).getToDos());
//		ret.addAll(((Controller)skills).getToDos());
//		ret.addAll(((Controller)master).getToDos());
//		ret.addAll(moonsign.getToDos());
		
		// Find decisions not made yet
		for (DecisionToMake dec : getDecisionsToMake()) {
			if (dec.getDecision()==null) {
				ret.add(new ToDoElement(Severity.STOPPER, String.valueOf(dec.getChoice())));
			}
		}
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl4.Controller#getDecisionsToMake()
	 */
	@Override
	public List<DecisionToMake> getDecisionsToMake() {
		List<DecisionToMake> ret = new ArrayList<>();
		for (SpliMoCharacterProcessor step : processChain) {
			if (step instanceof Controller) {
				ret.addAll( ((Controller)step).getDecisionsToMake() );
			}
		}
		return ret;
	}

	//--------------------------------------------------------------------
	public SpliMoCharacter getModel() {
		return model;
	}

	//--------------------------------------------------------------------
	public void start(SpliMoCharacter model) {
		// Stop previous
		stop();

		this.model = model;
		
		races      = new RaceGenerator(this);
		cultures   = new NewCultureGenerator(this);
		backgrounds= new NewBackgroundGenerator(this);
		educations = new NewEducationGenerator(this);
		attributes = new NewAttributeGenerator(this, 18);
		powers     = new NewPowerGenerator(this, 10);  // 4 (Race) + 1 (Culture) + 2 (Education) + 3 (free)
		resources  = new NewResourceGenerator(this, 8, true);
		master     = new NewMastershipGenerator(this, 3);
		skills     = new NewSkillGenerator(this, 55);
//		moonsign   = new MoonsignProcessor();
////		spells     = new SpellGenerator(skills, model);
//		educations = new EducationGenerator();
		spells     = new NewSpellGenerator(this);
//		languages  = new LanguageGenerator(model, new ArrayList<>(), CharGenMode.CREATING);
//		cultlores  = new CultureLoreGenerator(model, new ArrayList<>(), CharGenMode.CREATING);

		processChain.clear();
		processChain.add( new ResetModificationsOnGeneration());
		processChain.add( (SpliMoCharacterProcessor) cultures );
		processChain.add( (SpliMoCharacterProcessor) races );
		processChain.add( (SpliMoCharacterProcessor) backgrounds );
		processChain.add( (SpliMoCharacterProcessor) educations );
		processChain.add( (SpliMoCharacterProcessor) powers );
		processChain.add( (SpliMoCharacterProcessor) attributes );
		processChain.add( (SpliMoCharacterProcessor) skills );
		processChain.add( (SpliMoCharacterProcessor) resources );
		processChain.add( (SpliMoCharacterProcessor) master );
		processChain.add( (SpliMoCharacterProcessor) spells );
		processChain.add( moonsign );
		
		runProcessors();
	}


	//--------------------------------------------------------------------
	public void stop() {
		logger.info("Stop generation");
	}

	//-------------------------------------------------------------------
	public void runProcessors() {
//		if (dontProcess)
//			return;
//		model.setKarmaFree(25);
//		model.setKarmaInvested(0);
		logger.info("START: runProcessors: "+processChain.size()+"-------------------------------------------------------");
		List<Modification> unprocessed = new ArrayList<>(unitTestModifications);
		for (SpliMoCharacterProcessor processor : processChain) {
			unprocessed = processor.process(model, unprocessed);
			logger.info("------EXP is "+model.getExperienceFree()+" after "+processor.getClass().getSimpleName()+"     "+unprocessed);
		}
		logger.info("Remaining mods  = "+unprocessed);
		logger.info("Remaining exp = "+model.getExperienceFree());
		logger.info("Decisions = "+getDecisionsToMake());
		logger.info("ToDos = "+getToDos());
		logger.info("STOP : runProcessors: "+processChain.size()+"-------------------------------------------------------");
//		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.EXPERIENCE_CHANGED, null, new Integer[] {model.getKarmaFree(), model.getKarmaInvested()}));
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.CHARACTER_CHANGED, model ));
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl4.Controller#decide(de.rpgframework.genericrpg.modification.DecisionToMake, java.util.List)
	 */
	@Override
	public void decide(DecisionToMake choice, List<Modification> choosen) {
		// Find suitable controller
		for (SpliMoCharacterProcessor step : processChain) {
			if (step instanceof Controller) {
				if ( ((Controller)step).getDecisionsToMake().contains(choice)) {
					((Controller)step).decide(choice, choosen);
				}
			}
		}
		
//		if (races.getDecisionsToMake().contains(choice)) races.decide(choice, choosen);
//		if (cultures.getDecisionsToMake().contains(choice)) cultures.decide(choice, choosen);
//		if (backgrounds.getDecisionsToMake().contains(choice)) backgrounds.decide(choice, choosen);
//		if (educations.getDecisionsToMake().contains(choice)) educations.decide(choice, choosen);
//		if (powers.getDecisionsToMake().contains(choice)) powers.decide(choice, choosen);
//		if (attributes.getDecisionsToMake().contains(choice)) attributes.decide(choice, choosen);
//		if (resources.getDecisionsToMake().contains(choice)) resources.decide(choice, choosen);
//		if (skills.getDecisionsToMake().contains(choice)) skills.decide(choice, choosen);
//		if (master.getDecisionsToMake().contains(choice)) master.decide(choice, choosen);
	}

}
