/**
 * 
 */
package org.prelle.splimo.charctrl4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.modifications.ModificationChoice;
import org.prelle.splimo.processor.SpliMoCharacterProcessor;

import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
/**
 * @author prelle
 *
 */
public abstract class SplitterEngineCharacterGenerator implements CharacterController {
	
	protected enum Mode {
		FINISHED,
		CREATION,
		TUNING
	}
	
	private static Logger logger = Logger.getLogger("splitter.chargen");
	private static Random RANDOM = new Random();
	
	protected SpliMoCharacter model;
	
	protected List<Modification> unitTestModifications;
	protected List<SpliMoCharacterProcessor> processChain;
	
	protected RaceController races;
	protected CultureController cultures;
	protected AttributeController attributes;
	protected BackgroundController backgrounds;
	protected PowerController powers;
	protected ResourceController resources;
	protected EducationController educations;
	protected SkillController skills;
////	private SpellGenerator spells;
	protected SpellController spells;
	protected MastershipController master;
	protected LanguageController languages;
	protected CultureLoreController cultlores;
	protected MoonsignProcessor moonsign;
	
	/**
	 * Memorizes all choices made by the user. 
	 */
	private Map<ModificationChoice, Modification[]> choices;
	protected Mode mode;
	
	//-------------------------------------------------------------------
	/**
	 */
	public SplitterEngineCharacterGenerator() {
		processChain = new ArrayList<>();
//		decisions    = new HashMap<>();
		unitTestModifications = new ArrayList<>();
		moonsign = new MoonsignProcessor();
	}

	//--------------------------------------------------------------------
	public SpliMoCharacter getModel() {
		return model;
	}

	//--------------------------------------------------------------------
	public Mode getMode() {
		return mode;
	}

	//-------------------------------------------------------------------
	/**
	 * Checks if the necessary data for character generation is entered.
	 */
	public boolean hasEnoughData() {
		for (ToDoElement elem : getToDos()) {
			if (elem.getSeverity()==Severity.STOPPER)
				return false;
		}
		return true;
				
	}

	//-------------------------------------------------------------------
	public AttributeController getAttributeGenerator() {
		return attributes;
	}

	//-------------------------------------------------------------------
	public BackgroundController getBackgroundGenerator() {
		return backgrounds;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl4.CharacterController#getResourceController()
	 */
	public ResourceController getResourceController() {
		return resources;
	}

	//-------------------------------------------------------------------
	public EducationController getEducationGenerator() {
		return educations;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl4.CharacterController#getSkillController()
	 */
	public SkillController getSkillController() {
		return skills;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl4.CharacterController#getSpellController()
	 */
	public SpellController getSpellController() {
		return spells;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl4.CharacterController#getRaceController()
	 */
	@Override
	public RaceController getRaceController() {
		return races;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl4.CharacterController#getCultureController()
	 */
	@Override
	public CultureController getCultureController() {
		return cultures;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.CharacterController#getAttributeController()
	 */
	@Override
	public AttributeController getAttributeController() {
		return attributes;
	}
	
//	//--------------------------------------------------------------------
//	/**
//	 * @see org.prelle.splimo.charctrl.CharacterController#getFreeMasterships(org.prelle.splimo.Skill)
//	 */
//	@Override
//	public int getFreeMasterships(Skill skill) {
//		return skills.getMastershipGenerator(skill).getUnusedFreeSelections();
//	}
//
//	//-------------------------------------------------------------------
//	/**
//	 * @see org.prelle.splimo.charctrl.CharacterController#canBeSelected(org.prelle.splimo.SkillSpecialization)
//	 */
//	@Override
//	public boolean isEditable(Skill skill, SkillSpecialization special, int level) {
//		return skills.isEditable(skill, special);
//	}
//
//	//-------------------------------------------------------------------
//	/**
//	 * @see org.prelle.splimo.charctrl.CharacterController#canBeSelected(org.prelle.splimo.Mastership)
//	 */
//	@Override
//	public boolean isEditable(Skill skill, Mastership master) {
//		return skills.isEditable(skill, master);
//	}
//
//	//-------------------------------------------------------------------
//	/**
//	 * @see org.prelle.splimo.charctrl.CharacterController#setSelected(org.prelle.splimo.Skill, org.prelle.splimo.SkillSpecialization, int, boolean)
//	 */
//	@Override
//	public void setSelected(Skill skill, SkillSpecialization special,
//			int level, boolean state) {
//		skills.setSelected(skill, special, state);
//	}
//
//	//-------------------------------------------------------------------
//	/**
//	 * @see org.prelle.splimo.charctrl.CharacterController#setSelected(org.prelle.splimo.Skill, org.prelle.splimo.Mastership, boolean)
//	 */
//	@Override
//	public void setSelected(Skill skill, Mastership master, boolean state) {
//		skills.setSelected(skill, master, state);
//	}


	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.CharacterController#getPowerController()
	 */
	@Override
	public PowerController getPowerController() {
		return powers;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.CharacterController#getMastershipController()
	 */
	@Override
	public MastershipController getMastershipController() {
		// TODO Auto-generated method stub
		return master;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.CharacterController#getCultureLoreController()
	 */
	@Override
	public CultureLoreController getCultureLoreController() {
		return cultlores;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.CharacterController#getLanguageController()
	 */
	@Override
	public LanguageController getLanguageController() {
		return languages;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.Controller#getToDos()
	 */
	@Override
	public List<ToDoElement> getToDos() {
		List<ToDoElement> ret = new ArrayList<>();
//		ret.addAll(backgrounds.getToDos());
//		ret.addAll(educations.getToDos());
//		ret.addAll(skills.getToDos());
//		ret.addAll(powers.getToDos());
//		ret.addAll(resources.getToDos());
//		ret.addAll(cultlores.getToDos());
//		ret.addAll(languages.getToDos());
//		ret.addAll(spells2.getToDos());
		return ret;
	}

	//--------------------------------------------------------------------
	public void start(SpliMoCharacter model) {
		// Stop previous
		stop();

		mode = Mode.CREATION;
		
		choices    = new HashMap<ModificationChoice, Modification[]>();
	}

	//-------------------------------------------------------------------
	public void startTuningMode() {
		logger.info("------Change to tuning mode----------------");
		mode = Mode.TUNING;
		
		runProcessors();
	}

	//--------------------------------------------------------------------
	public void stop() {
		logger.info("Stop generation");
	}

	//-------------------------------------------------------------------
	public abstract void runProcessors();
////		if (dontProcess)
////			return;
////		model.setKarmaFree(25);
////		model.setKarmaInvested(0);
//		logger.info("START: runProcessors: "+processChain.size()+"-------------------------------------------------------");
//		List<Modification> unprocessed = new ArrayList<>(unitTestModifications);
//		for (SpliMoCharacterProcessor processor : processChain) {
//			unprocessed = processor.process(model, unprocessed);
//			logger.info("------EXP is "+model.getExperienceFree()+" after "+processor.getClass().getSimpleName()+"     "+unprocessed);
//		}
//		logger.info("Remaining mods  = "+unprocessed);
//		logger.info("Remaining exp = "+model.getExperienceFree());
//		logger.info("ToDos = "+getToDos());
//		logger.info("STOP : runProcessors: "+processChain.size()+"-------------------------------------------------------");
////		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.EXPERIENCE_CHANGED, null, new Integer[] {model.getKarmaFree(), model.getKarmaInvested()}));
//		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.CHARACTER_CHANGED, model ));
//	}

}
