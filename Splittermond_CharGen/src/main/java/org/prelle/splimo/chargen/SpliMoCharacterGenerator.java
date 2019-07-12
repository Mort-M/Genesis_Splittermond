/**
 *
 */
package org.prelle.splimo.chargen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.AttributeValue;
import org.prelle.splimo.Culture;
import org.prelle.splimo.CultureLoreReference;
import org.prelle.splimo.Education;
import org.prelle.splimo.LanguageReference;
import org.prelle.splimo.Moonsign;
import org.prelle.splimo.Race;
import org.prelle.splimo.RewardImpl;
import org.prelle.splimo.Size;
import org.prelle.splimo.Skill;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SpliMoCharacter.Gender;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.AttributeController;
import org.prelle.splimo.charctrl.BackgroundController;
import org.prelle.splimo.charctrl.CharGenMode;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.charctrl.CultureLoreController;
import org.prelle.splimo.charctrl.GeneratingResourceController;
import org.prelle.splimo.charctrl.GeneratingSkillController;
import org.prelle.splimo.charctrl.LanguageController;
import org.prelle.splimo.charctrl.MastershipController;
import org.prelle.splimo.charctrl.PowerController;
import org.prelle.splimo.charctrl.ResourceController;
import org.prelle.splimo.charctrl.SkillController;
import org.prelle.splimo.charctrl.SpellController;
import org.prelle.splimo.charctrl4.SpliMoCharGenConstants;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.free.FreeSelectionGenerator;
import org.prelle.splimo.items.CarriedItem;
import org.prelle.splimo.modifications.AttitudeModification;
import org.prelle.splimo.modifications.AttributeModification;
import org.prelle.splimo.modifications.BackgroundModification;
import org.prelle.splimo.modifications.CultureLoreModification;
import org.prelle.splimo.modifications.LanguageModification;
import org.prelle.splimo.modifications.MastershipModification;
import org.prelle.splimo.modifications.ModificationChoice;
import org.prelle.splimo.modifications.NotBackgroundModification;
import org.prelle.splimo.modifications.PointsModification;
import org.prelle.splimo.modifications.PowerModification;
import org.prelle.splimo.modifications.RaceModification;
import org.prelle.splimo.modifications.RequirementModification;
import org.prelle.splimo.modifications.ResourceModification;
import org.prelle.splimo.modifications.SkillModification;
import org.prelle.splittermond.genlvl.MastershipLevellerAndGenerator;
import org.prelle.splittermond.genlvl.SpellLevellerAndGenerator;

import de.rpgframework.ConfigOption;
import de.rpgframework.RPGFrameworkLoader;
import de.rpgframework.character.Attachment;
import de.rpgframework.character.CharacterHandle;
import de.rpgframework.character.CharacterHandle.Format;
import de.rpgframework.character.CharacterHandle.Type;
import de.rpgframework.character.CharacterProvider;
import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;
import de.rpgframework.core.RoleplayingSystem;
import de.rpgframework.genericrpg.Reward;
import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
/**
 * @author prelle
 *
 */
public class SpliMoCharacterGenerator implements CharacterController {

	private final static ResourceBundle CORE = SplitterMondCore.getI18nResources();

	private static Logger logger = LogManager.getLogger("splittermond.chargen");
	private static Random RANDOM = new Random();

	private Race       selectedRace;
	private Culture    selectedCulture;
	private Education  selectedEducation;

	/**
	 * If TRUE uncommon cultures for the selected race shall also be included
	 */
	private boolean includeUncommonCultures;
	/**
	 * If TRUE uncommon backgrounds for the selected culture shall also be included
	 */
	private AttributeGenerator attributes;
	private BackgroundGenerator backgrounds;
	private PowerGenerator2 powers;
	private ResourceGenerator resources;
	private EducationGenerator educations;
	private SkillGenerator skills;
//	private SpellGenerator spells;
	private SpellLevellerAndGenerator spells2;
	private MastershipLevellerAndGenerator master;
	private LanguageGenerator languages;
	private CultureLoreGenerator cultlores;

	/**
	 * Memorizes all choices made by the user.
	 */
	private Map<ModificationChoice, Modification[]> choices;
	private SpliMoCharacter model;
	private ConfigOption<Double> hgFactor;

	//-------------------------------------------------------------------
	/**
	 */
	public SpliMoCharacterGenerator(SpliMoCharacter model, ConfigOption<Double> hgFactor) {
		this.model = model;
		this.hgFactor = hgFactor;
		Reward reward = new RewardImpl(15, CORE.getString("label.reward.creation"));
		reward.setDate(new Date(System.currentTimeMillis()));
		model.setExperienceFree(15);
		model.addLanguage(new LanguageReference(SplitterMondCore.getLanguage("basargnomisch")));
		model.addReward(reward);
		model.addItem(new CarriedItem(SplitterMondCore.getItem("decke")));
		model.addItem(new CarriedItem(SplitterMondCore.getItem("essgeschirr")));
		model.addItem(new CarriedItem(SplitterMondCore.getItem("feuersteinstahlundzunder")));
		model.addItem(new CarriedItem(SplitterMondCore.getItem("fackel"), 5));
		model.addItem(new CarriedItem(SplitterMondCore.getItem("marschrationprotag"), 3));

		choices    = new HashMap<ModificationChoice, Modification[]>();
		attributes = new AttributeGenerator(model);
		powers     = new PowerGenerator2(model, 10, this);  // 4 (Race) + 1 (Culture) + 2 (Education) + 3 (free)
		backgrounds= new BackgroundGenerator(this, model);
		resources  = new ResourceGenerator(8, model);
		master     = new MastershipLevellerAndGenerator(3, model, new ArrayList<Modification>(), this, null);
		skills     = new SkillGenerator(55, model, master);
//		spells     = new SpellGenerator(skills, model);
		educations = new EducationGenerator();
		spells2    = new SpellLevellerAndGenerator(model, new ArrayList<Modification>());
		languages  = new LanguageGenerator(model, new ArrayList<>(), CharGenMode.CREATING);
		cultlores  = new CultureLoreGenerator(model, new ArrayList<>(), CharGenMode.CREATING);
	}

	//-------------------------------------------------------------------
	public void apply(Modification mod) {
		logger.debug("apply "+mod);
		if (mod instanceof AttributeModification) {
			attributes.addModification((AttributeModification) mod);
		} else if (mod instanceof RaceModification) {
		} else if (mod instanceof BackgroundModification) {
			backgrounds.addModification((BackgroundModification) mod);
		} else if (mod instanceof NotBackgroundModification) {
			backgrounds.addModification((NotBackgroundModification) mod);
		} else if (mod instanceof PowerModification) {
			powers.addModification((PowerModification) mod);
		} else if (mod instanceof ResourceModification) {
			ResourceModification rMod = (ResourceModification)mod;
			resources.addModification(rMod);
		} else if (mod instanceof SkillModification) {
			skills.addModification((SkillModification) mod);
		} else if (mod instanceof PointsModification) {
			PointsModification pMod = (PointsModification)mod;
			switch (pMod.getType()) {
			case POWER:
				powers.addModification(pMod);
				break;
			default:
				logger.error("Unsupported PointsModification type "+pMod.getType());
				System.exit(0);
			}
		} else if (mod instanceof MastershipModification) {
			master.addModification((MastershipModification) mod);
//			skills.addModification((MastershipModification) mod);
		} else if (mod instanceof RequirementModification) {
			RequirementModification req = (RequirementModification)mod;
			logger.warn("Don't know how to apply requirement for type "+req.getType());
		} else if (mod instanceof CultureLoreModification) {
			cultlores.select(((CultureLoreModification)mod).getData());
//			model.addCultureLore( new CultureLoreReference(((CultureLoreModification)mod).getData()) );
		} else if (mod instanceof LanguageModification) {
			languages.select(((LanguageModification)mod).getData());
		} else if (mod instanceof AttitudeModification) {
		} else {
			logger.error("Don't know how to apply modification of type "+mod.getClass());
			System.exit(0);
		}
	}

	//-------------------------------------------------------------------
	public void apply(String origin, Collection<Modification> modsToApply, LetUserChooseListener callback) {
		for (Modification mod : modsToApply) {
			try {
				// Check if modification is a choice - if so, ask user
				if (mod instanceof ModificationChoice) {
					logger.debug("Let user choose: "+mod);
					if (callback==null) {
						logger.error("LetUserChooseListener not set");
						continue;
					}
					if ( ((ModificationChoice) mod).getOptions()[0] instanceof RequirementModification) {
						// Don't let user choose requirements
						continue;
					}

					Modification[] madeChoices = callback.letUserChoose(origin, (ModificationChoice) mod);
					if (madeChoices==null)
						throw new IllegalArgumentException("No choice has been made on "+mod);

					/*
					 * Eventually a MastershipModification with a skill type
					 * is selected. In this case build another choice
					 */
					for (Modification mod2 : madeChoices) {
						if (mod2 instanceof MastershipModification) {
							MastershipModification mMod = (MastershipModification)mod2;
							logger.debug("A mastership modication choice has been made: "+mod2);
							if (mMod.getSkillType()==null) continue;
							// Build a ModificationChoice for the skill type
							List<Modification> mods = new ArrayList<Modification>();
							for (Skill skill : SplitterMondCore.getSkills(mMod.getSkillType())) {
								if (model.getSkillValue(skill).getValue()>0)
									mods.add(new MastershipModification(skill, mMod.getLevel()));
							}
							ModificationChoice modC = new ModificationChoice(mods, 1);
							madeChoices = callback.letUserChoose(origin, modC);
							if (madeChoices==null)
								throw new IllegalArgumentException("No choice has been made on "+mod);
							logger.debug("made choices : "+Arrays.toString(madeChoices));
						}
					}

					choices.put((ModificationChoice) mod, madeChoices);
					for (Modification choice : madeChoices) {
						apply(choice);
					}
				} else if (mod instanceof MastershipModification && ((MastershipModification)mod).getSkillType()!=null) {
					MastershipModification mMod = (MastershipModification)mod;
					logger.debug("Select mastership of type "+mMod.getSkillType()+" for a selected skill");
					// Build a ModificationChoice for the skill type
					List<Modification> mods = new ArrayList<Modification>();
					for (Skill skill : SplitterMondCore.getSkills(mMod.getSkillType())) {
						if (model.getSkillValue(skill).getValue()>0)
							mods.add(new MastershipModification(skill, mMod.getLevel()));
					}
					ModificationChoice modC = new ModificationChoice(mods, 1);
					Modification[] madeChoices = callback.letUserChoose(origin, modC);
					if (madeChoices==null)
						throw new IllegalArgumentException("No choice has been made on "+mod);
					logger.debug("made choices : "+Arrays.toString(madeChoices));

					choices.put(modC, madeChoices);
					for (Modification choice : madeChoices) {
						apply(choice);
					}
				} else {
					apply(mod);
				}
			} catch (Exception e) {
				logger.error("Failed applying modification "+mod,e);
			}
		}
	}

	//-------------------------------------------------------------------
	private void undo(Modification mod) {
		logger.debug("undo "+mod);
		if (mod instanceof AttributeModification) {
			attributes.removeModification((AttributeModification) mod);
		} else if (mod instanceof RaceModification) {
		} else if (mod instanceof PowerModification) {
			powers.removeModification((PowerModification) mod);
		} else if (mod instanceof SkillModification) {
			skills.removeModification((SkillModification) mod);
		} else if (mod instanceof MastershipModification) {
			master.removeModification((MastershipModification) mod);
		} else if (mod instanceof PointsModification) {
			PointsModification pMod = (PointsModification)mod;
			switch (pMod.getType()) {
			case POWER:
				powers.removeModification(pMod);
				break;
			default:
				logger.error("Unsupported PointsModification type "+pMod.getType());
				System.exit(0);
			}
		} else if (mod instanceof ResourceModification) {
			resources.removeModification((ResourceModification) mod);
		} else if (mod instanceof BackgroundModification) {
			backgrounds.removeModification((BackgroundModification) mod);
		} else if (mod instanceof NotBackgroundModification) {
			backgrounds.removeModification((NotBackgroundModification) mod);
		} else if (mod instanceof RequirementModification) {
			RequirementModification req = (RequirementModification)mod;
			logger.warn("Don't know how to undo requirement for type "+req.getType());
		} else if (mod instanceof CultureLoreModification) {
			model.removeCultureLore( new CultureLoreReference(((CultureLoreModification)mod).getData()) );
		} else if (mod instanceof LanguageModification) {
			model.removeLanguage( new LanguageReference(((LanguageModification)mod).getData()) );
		} else {
			logger.warn("Don't know how to undo modification of type "+mod.getClass()+": "+mod);
//			System.exit(0);
		}
	}

	//-------------------------------------------------------------------
	public void undo(Collection<Modification> modsToUndo) {
		logger.debug("undo "+modsToUndo);
		for (Modification mod : modsToUndo) {
			// Check if modification is a choice - if so, ask user
			if (mod instanceof ModificationChoice) {
				Modification[] madeChoices = choices.get((ModificationChoice) mod);
				choices.remove((ModificationChoice) mod);

				if (madeChoices==null) {
					logger.warn("Trying to undo choices that are not registed: "+mod);
					continue;
				}

				for (Modification choice : madeChoices) {
					undo(choice);
				}
			} else {
				undo(mod);
			}

		}
	}

	//-------------------------------------------------------------------
	public Race getSelectedRace() {
		return selectedRace;
	}

	//-------------------------------------------------------------------
	public void selectRace(Race selected, LetUserChooseListener callback) {
		if (selected==selectedRace) {
			// Nothing changed
			return;
		}

		/*
		 * Undo all modifications from previously made selected
		 */
		if (selectedRace!=null && selected!=null)
			undo(selectedRace.getModifications());
//		if (selectedRace!=null && selectedRace.getKey().equals("human")) {
//			powers.modifyPoints(-2);
//		}

		/*
		 * Now selected
		 */
		apply(
				SpliMoCharGenConstants.RES.getString("label.race")+" "+selected.getName(),
				selected.getModifications(), callback);

		selectedRace = selected;
		model.setRace(selected.getKey());
		logger.info("Selected race is now "+selected);

//		if (selected.getKey().equals("human")) {
//			powers.modifyPoints(2);
//		}

		/*
		 * Inform listener
		 */
		GenerationEvent event = new GenerationEvent(GenerationEventType.RACE_SELECTED, selected);
		GenerationEventDispatcher.fireEvent(event);
		event = new GenerationEvent(GenerationEventType.CULTURE_OFFER_CHANGED, getAvailableCultures());
		GenerationEventDispatcher.fireEvent(event);
	}

	//-------------------------------------------------------------------
	public FreeSelectionGenerator getFreeCreatorCulture() {
		return new FreeSelectionGenerator(model,
				true, // Culture Lore
				true, // Language
				1, // Powers
				15, 1, 2, 1,  // Skills: total, max. magic schools, max points per skill / magic skill
				1,  // Masterships
				0   // Resources
				);
	}

	//-------------------------------------------------------------------
	public FreeSelectionGenerator getFreeCreatorBackground() {
		return new FreeSelectionGenerator(model,
				false, // Culture Lore
				false, // Language
				0, // Powers
				5, 0, 1, 0,  // Skills: total, max. magic schools, max points per skill / magic skill
				0,  // Masterships
				4   // Resources
				);
	}

	//-------------------------------------------------------------------
	public FreeSelectionGenerator getFreeCreatorEducation() {
		return new FreeSelectionGenerator(model,
				false, // Culture Lore
				false, // Language
				2, // Powers
				30, 99, 3, 4,  // Skills: total, max. magic schools, max points per skill / magic skill
				2,  // Masterships
				2   // Resources
				);
	}

	//-------------------------------------------------------------------
	public void setIncludeUncommonCultures(boolean uncommon) {
		includeUncommonCultures = uncommon;

		GenerationEvent event = new GenerationEvent(GenerationEventType.CULTURE_OFFER_CHANGED, getAvailableCultures());
		GenerationEventDispatcher.fireEvent(event);
	}

	//-------------------------------------------------------------------
	/**
	 * Return the list of cultures to offer to the user.
	 */
	public List<Culture> getAvailableCultures() {
		List<Culture> toOffer = new ArrayList<Culture>(SplitterMondCore.getCultures());
		if (!includeUncommonCultures && selectedRace!=null) {
			toOffer = new ArrayList<Culture>();
			for (Culture cult : SplitterMondCore.getCultures()) {
				if (cult.getCommonRaces().contains(selectedRace))
					toOffer.add(cult);
				else
					logger.trace("Culture '"+cult+"' usually has no race "+selectedRace);
			}
		}

		return toOffer;
	}

	//-------------------------------------------------------------------
	public void selectCulture(Culture selected, LetUserChooseListener callback) {
		logger.info("selectCulture("+selected+")");
		if (selected==selectedCulture) {
			// Nothing changed
			return;
		}

		/*
		 * Undo all modifications from previously made selected
		 */
		if (selectedCulture!=null && selected!=null)
			undo(selectedCulture.getModifications());

		/*
		 * Now selected
		 */
		if (selected!=null)
		apply(
				SpliMoCharGenConstants.RES.getString("label.culture")+" "+selected.getName(),
				selected.getModifications(), callback);


		selectedCulture = selected;
		model.setCulture(selected.getKey());
		if (SplitterMondCore.getCultures().contains(selected)) {
			model.setOwnCulture(null);
			logger.info("Selected stock culture is now "+selected);
		} else {
			model.setOwnCulture(selected);
			logger.info("Selected own culture is now "+selected);
		}

		/*
		 * Inform listener
		 */
		GenerationEvent event = new GenerationEvent(GenerationEventType.CULTURE_SELECTED, selected);
		GenerationEventDispatcher.fireEvent(event);
	}

	//-------------------------------------------------------------------
	public void selectEducation(Education selected, LetUserChooseListener callback) {
		if (selected==selectedEducation) {
			// Nothing changed
			return;
		}

		if (selected==null)
			throw new NullPointerException("Selected education is NULL");

		logger.debug("-------------selectEducation---------------------------------\n\n");
		/*
		 * Undo all modifications from previously made selected
		 */
		if (selectedEducation!=null && selected!=null) {
			logger.debug("remove previous selected "+selectedEducation);
			undo(selectedEducation.getModifications());
		}

		/*
		 * Now selected
		 */
		logger.debug("Edu mods = "+selected.getModifications());
		apply(
				SpliMoCharGenConstants.RES.getString("label.education")+" "+selected.getName(),
				selected.getModifications(), callback);

		selectedEducation = selected;
		model.setEducation(selected.getKey());
		// Search through educations and variants
		boolean isStock = false;
		for (Education edu : SplitterMondCore.getEducations()) {
			if (edu==selected || edu.getVariants().contains(selected)) {
				isStock = true;
				break;
			}
		}

		if (isStock) {
			model.setOwnEducation(null);
			logger.info("Selected stock education is now "+selected);
		} else {
			model.setOwnEducation(selected);
			logger.info("Selected own education is now "+selected);
			if (!selected.getKey().equals("custom")) {
				logger.fatal("Stop here: expected own education to have id 'common'");
//				System.exit(0);
			}
		}
		logger.info("Selected education is now "+selected);

		/*
		 * Inform listener
		 */
		GenerationEvent event = new GenerationEvent(GenerationEventType.EDUCATION_SELECTED, selected);
		GenerationEventDispatcher.fireEvent(event);
	}

//	//-------------------------------------------------------------------
//	public void selectBackground(Background selected, LetUserChooseListener callback) {
//		if (selected==selectedBackground) {
//			// Nothing changed
//			return;
//		}
//
//		/*
//		 * Undo all modifications from previously made selected
//		 */
//		if (selectedBackground!=null && selected!=null)
//			undo(selectedBackground.getModifications());
//
//		/*
//		 * Now selected
//		 */
//		apply(
//				SpliMoCharGenConstants.RES.getString("label.background")+" "+selected.getName(),
//				selected.getModifications(), callback);
//
//		selectedBackground = selected;
//		logger.info("Selected background is now "+selected);
//
//		GenerationEvent event = new GenerationEvent(GenerationEventType.BACKGROUND_SELECTED, selected);
//		for (GenerationEventListener callback2 : listener) {
//			try {
//				callback2.handleGenerationEvent(event);
//			} catch (Exception e) {
//				logger.error("Exception delivering background value change event",e);
//			}
//		}
//	}

	//-------------------------------------------------------------------
	public int getAttribute(Attribute attrib) {
		return attributes.get(attrib);
	}

	//-------------------------------------------------------------------
	public void increaseAttribute(Attribute attrib) {
		logger.debug("INC "+attrib);
		attributes.increase(attrib);
	}

	//-------------------------------------------------------------------
	public void decreaseAttribute(Attribute attrib) {
		logger.debug("DEC "+attrib);
		attributes.decrease(attrib);
	}

	//-------------------------------------------------------------------
	public boolean hasEnoughRaceData() {
		return selectedRace!=null;
	}

	//-------------------------------------------------------------------
	public boolean hasEnoughAttributeData() {
		return attributes.getPointsLeft()==0;
	}

	//-------------------------------------------------------------------
	/**
	 * Checks if the necessary data for character generation is entered.
	 */
	public boolean hasEnoughData() {
		if (logger.isTraceEnabled()) {
			logger.trace("  race   "+selectedRace);
			logger.trace("  cult   "+selectedCulture);
			logger.trace("  back   "+backgrounds.getSelected());
//			logger.trace("  educ   "+educations.getSelected());
			logger.trace("  splint "+model.getSplinter());
			logger.trace("  attr   "+attributes.getPointsLeft());
			logger.trace("  power  "+powers.getPointsLeft());
			logger.trace("  resrc  "+resources.getPointsLeft());
			logger.trace("  skill  "+skills.getPointsLeft());
		}

		if (model.getName()==null) logger.debug("model.getName() = null");
		else if (model.getName().length()==0) logger.debug("model.getName().length = 0");
		if (selectedRace==null) logger.debug("selectedRace = null");
		if (selectedCulture==null) logger.debug("selectedCulture = null");
		if (backgrounds.getSelected()==null) logger.debug("backgrounds = null");
//		if (educations.getSelected()==null) logger.debug("educations = null");
		if (model.getSplinter()==null) logger.debug("splinter = null");
		if (attributes.getPointsLeft()>0) logger.debug("attributes.getPointsLeft() > 0");
		if (powers.getPointsLeft()>0) logger.debug("powers.getPointsLeft() > 0");
		if (resources.getPointsLeft()>0) logger.debug("resources.getPointsLeft() > 0");
		if (skills.getPointsLeft()>0) logger.debug("skills.getPointsLeft() > 0");

		boolean result = (model.getName()!=null && model.getName().length()>0)
				&& selectedRace!=null
				&& selectedCulture!=null
				&& backgrounds.getSelected()!=null
				&& model.getSplinter()!=null
				&& attributes.getPointsLeft()==0
				&& powers.getPointsLeft()==0
				&& resources.getPointsLeft()==0
				&& skills.getPointsLeft()==0
				;
		logger.warn("has enough data returns "+result);
		return result;

	}

	//-------------------------------------------------------------------
	/**
	 * Checks if the necessary data for character generation is entered.
	 */
	public boolean hasEnoughDialogData() {
		return
				selectedRace!=null
				&& selectedCulture!=null
				&& backgrounds.getSelected()!=null
				&& model.getSplinter()!=null
				&& (model.getName()!=null && model.getName().length()>0)
				;
	}

	//-------------------------------------------------------------------
	public SpliMoCharacter generate() {
		/*
		 * Copy all final attribute values to start values
		 */
		logger.debug("Copy attribute values as start values");
		for (Attribute attr : Attribute.primaryValues()) {
			AttributeValue val = model.getAttribute(attr);
			val.setStart(val.getValue());
			val.setDistributed(val.getValue());
			val.clearModifications();
//			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.ATTRIBUTE_CHANGED, attr, val));
		}

		// Set character level to 1
		model.setLevel(1);
//		model.setExperienceFree(15);
		model.setExperienceInvested(15-model.getExperienceFree());
//		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.BASE_DATA_CHANGED, null));


		logger.debug("Generate");

		CharacterProvider charProv     = RPGFrameworkLoader.getInstance().getCharacterService();
		try {
			CharacterHandle handle = charProv.createCharacter(model.getName(), RoleplayingSystem.SPLITTERMOND);
			logger.info("Calling handle.setCharacter("+model+")");
			handle.setCharacter(model);
			logger.debug("Successfully called handle.setCharacter("+model+")");
//			RPGFrameworkLoader.getInstance().getCharacterAndRules().getCharacterService().addAttachment(handle, Type.CHARACTER, Format.RULESPECIFIC, null, SplitterMondCore.save(model));
//			BabylonEventBus.fireEvent(BabylonEventType.CHAR_MODIFIED, handle, 2);
			// Attach image if exists
			if (model.getImage()!=null) {
				Attachment imgAttachment = handle.getFirstAttachment(Type.CHARACTER, Format.IMAGE);
				if (imgAttachment==null) {
					charProv.addAttachment(handle, Type.CHARACTER, Format.IMAGE, null, model.getImage());
				} else {
					imgAttachment.setData(model.getImage());
					charProv.modifyAttachment(handle, imgAttachment);
				}
			}
		} catch (Exception e) {
			logger.error("Failed saving created character",e);
			logger.debug("Inform user");
			BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 2, "Failed saving created character.\n"+e.getMessage());
			return null;
		}

		return model;
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
	public GeneratingResourceController getResourceGenerator() {
		return resources;
	}

	//-------------------------------------------------------------------
	public EducationGenerator getEducationGenerator() {
		return educations;
	}

	//-------------------------------------------------------------------
	public GeneratingSkillController getSkillGenerator() {
		return skills;
	}

//	//-------------------------------------------------------------------
//	public SpellGenerator getSpellGenerator() {
//		return spells;
//	}

	//--------------------------------------------------------------------
	/**
	 * @param selectedSplinter the selectedSplinter to set
	 */
	public void selectSplinter(Moonsign selectedSplinter) {
		model.setSplinter(selectedSplinter);
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.MOONSIGN_SELECTED, null));
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
	 * Roll a random hair or fur color
	 * @return
	 */
	public String rollHair() {
		if (selectedRace==null)
			return null;
		int roll = 2+RANDOM.nextInt(10)+RANDOM.nextInt(10);
		String color = selectedRace.getHair().getColor(roll);
		// TODO: treat color string as ID not as an already translated word
		logger.debug("rollHair returns '"+color+"'");
		return color;
	}

	//-------------------------------------------------------------------
	/**
	 * Roll a random eye color
	 * @return
	 */
	public String rollEyes() {
		int roll = 2+RANDOM.nextInt(10)+RANDOM.nextInt(10);
		String color = selectedRace.getEyes().getColor(roll);
		// TODO: treat color string as ID not as an already translated word
		logger.debug("rollEyes returns '"+color+"'");
		return color;
	}

	//-------------------------------------------------------------------
	/**
	 * @return 0=size, 1=weight
	 */
	public int[] rollSizeAndWeight() {
		Size size = selectedRace.getSize();
		int result = size.getSizeBase();
		// D10
		for (int i=0; i<size.getSizeD10(); i++)
			result += (1+RANDOM.nextInt(10));
		// D6
		for (int i=0; i<size.getSizeD6(); i++)
			result += (1+RANDOM.nextInt(6));

		// Choose weight relative to size
		double relSize = ((double)result-(double)size.getSizeBase())/(10*size.getSizeD10());
		logger.info("Size "+result+" is "+relSize+" %");
		int weightSpan = size.getWeightMax() - size.getWeightMin();
		logger.info("Weight span = "+weightSpan);

		return new int[]{result,  size.getWeightMin() + (int)(relSize*weightSpan)};
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
	 * @see org.prelle.splimo.charctrl.CharacterController#getResourceController()
	 */
	@Override
	public ResourceController getResourceController() {
		return resources;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.CharacterController#getSkillController()
	 */
	@Override
	public SkillController getSkillController() {
		return skills;
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
	 * @see org.prelle.splimo.charctrl.CharacterController#getSpellController()
	 */
	@Override
	public SpellController getSpellController() {
		return spells2;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.Controller#getToDos()
	 */
	@Override
	public List<String> getToDos() {
		List<String> ret = new ArrayList<>();
		ret.addAll(backgrounds.getToDos());
		ret.addAll(educations.getToDos());
		ret.addAll(skills.getToDos());
		ret.addAll(powers.getToDos());
		ret.addAll(resources.getToDos());
		ret.addAll(cultlores.getToDos());
		ret.addAll(languages.getToDos());
		ret.addAll(spells2.getToDos());
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.CharacterController#getModel()
	 */
	@Override
	public SpliMoCharacter getModel() {
		return model;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the hgFactor
	 */
	public ConfigOption<Double> getHGFactor() {
		return hgFactor;
	}

}
