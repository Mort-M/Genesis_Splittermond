/**
 * 
 */
package org.prelle.splimo.levelling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.AttributeValue;
import org.prelle.splimo.Skill;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterTools;
import org.prelle.splimo.charctrl.AttributeController;
import org.prelle.splimo.charctrl.CharGenMode;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.charctrl.CultureLoreController;
import org.prelle.splimo.charctrl.LanguageController;
import org.prelle.splimo.charctrl.MastershipController;
import org.prelle.splimo.charctrl.PowerController;
import org.prelle.splimo.charctrl.ResourceController;
import org.prelle.splimo.charctrl.SkillController;
import org.prelle.splimo.charctrl.SpellController;
import org.prelle.splimo.chargen.CultureLoreGenerator;
import org.prelle.splimo.chargen.LanguageGenerator;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.modifications.AttributeModification;
import org.prelle.splimo.modifications.SkillModification;
import org.prelle.splittermond.genlvl.MastershipLevellerAndGenerator;
import org.prelle.splittermond.genlvl.SpellLevellerAndGenerator;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class CharacterLeveller implements CharacterController, GenerationEventListener {
	
	private static Logger logger = Logger.getLogger("splittermond.level");

	private SpliMoCharacter data;
	private int maxAttribute;
	private List<Modification> undoList;
	private AttributeLeveller attributes;
	private ResourceLeveller resources;
	private PowerLeveller    powers;
	private SkillLeveller    skills;
	private MastershipLevellerAndGenerator masterships;
	private SpellLevellerAndGenerator spells;
	private CultureLoreController cultures;
	private LanguageController languages;
	
	//-------------------------------------------------------------------
	public CharacterLeveller(SpliMoCharacter data) {
		this.data = data;
		undoList  = new ArrayList<Modification>();
		
		attributes= new AttributeLeveller(data, undoList);
		resources = new ResourceLeveller(data, undoList);
		powers    = new PowerLeveller(data, undoList, this);
		masterships = new MastershipLevellerAndGenerator(3, data, undoList, null, this);
		skills    = new SkillLeveller(data, undoList, masterships);
		spells    = new SpellLevellerAndGenerator(data, undoList);
		cultures  = new CultureLoreGenerator(data, undoList, CharGenMode.LEVELING);
		languages = new LanguageGenerator(data, undoList, CharGenMode.LEVELING);
		
//		int totalExp = data.getExperienceInvested() + data.getExperienceFree();
		// Calculcate character level
		updateLevelAndMax();
		
		GenerationEventDispatcher.addListener(this);
	}

	//-------------------------------------------------------------------
	private void updateLevelAndMax() {
		int oldLevel = data.getLevel();
		
		if (data.getExperienceInvested()>=600) {
			data.setLevel(4);
			maxAttribute = 4;
			if (oldLevel==3) {
				logger.info("Level increased from 3 to 4");
				data.getAttribute(Attribute.SPLINTER  ).addModification( new AttributeModification(Attribute.SPLINTER  , 1, SplitterTools.LEVEL4) );
				data.getAttribute(Attribute.BODYRESIST).addModification( new AttributeModification(Attribute.BODYRESIST, 2, SplitterTools.LEVEL4) );
				data.getAttribute(Attribute.MINDRESIST).addModification( new AttributeModification(Attribute.MINDRESIST, 2, SplitterTools.LEVEL4) );
				data.getAttribute(Attribute.DEFENSE   ).addModification( new AttributeModification(Attribute.DEFENSE   , 2, SplitterTools.LEVEL4) );
				GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.ATTRIBUTE_CHANGED, Attribute.SPLINTER, data.getAttribute(Attribute.SPLINTER)));
			}
		} else if (data.getExperienceInvested()>=300) {
			data.setLevel(3);
			maxAttribute = 3;
			if (oldLevel==2) {
				logger.info("Level increased from 21 to 3");
				data.getAttribute(Attribute.SPLINTER  ).addModification( new AttributeModification(Attribute.SPLINTER  , 1, SplitterTools.LEVEL3) );
				data.getAttribute(Attribute.BODYRESIST).addModification( new AttributeModification(Attribute.BODYRESIST, 2, SplitterTools.LEVEL3) );
				data.getAttribute(Attribute.MINDRESIST).addModification( new AttributeModification(Attribute.MINDRESIST, 2, SplitterTools.LEVEL3) );
				data.getAttribute(Attribute.DEFENSE   ).addModification( new AttributeModification(Attribute.DEFENSE   , 2, SplitterTools.LEVEL3) );
				GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.ATTRIBUTE_CHANGED, Attribute.SPLINTER, data.getAttribute(Attribute.SPLINTER)));
			} else
			if (oldLevel==4) {
				logger.info("Level decreased from 4 to 3");
				data.getAttribute(Attribute.SPLINTER).removeModificationForSource(SplitterTools.LEVEL4);
				GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.ATTRIBUTE_CHANGED, Attribute.SPLINTER, data.getAttribute(Attribute.SPLINTER)));
			}
		} else if (data.getExperienceInvested()>=100) {
			data.setLevel(2);
			maxAttribute = 2;
			if (oldLevel==1) {
				logger.info("Level increased from 1 to 2");
				data.getAttribute(Attribute.SPLINTER  ).addModification( new AttributeModification(Attribute.SPLINTER  , 1, SplitterTools.LEVEL2) );
				data.getAttribute(Attribute.BODYRESIST).addModification( new AttributeModification(Attribute.BODYRESIST, 2, SplitterTools.LEVEL2) );
				data.getAttribute(Attribute.MINDRESIST).addModification( new AttributeModification(Attribute.MINDRESIST, 2, SplitterTools.LEVEL2) );
				data.getAttribute(Attribute.DEFENSE   ).addModification( new AttributeModification(Attribute.DEFENSE   , 2, SplitterTools.LEVEL2) );
				GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.ATTRIBUTE_CHANGED, Attribute.SPLINTER, data.getAttribute(Attribute.SPLINTER)));
			} else
			if (oldLevel==3) {
				logger.info("Level decreased from 3 to 2");
				data.getAttribute(Attribute.SPLINTER).removeModificationForSource(SplitterTools.LEVEL3);
				GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.ATTRIBUTE_CHANGED, Attribute.SPLINTER, data.getAttribute(Attribute.SPLINTER)));
			}
		} else {
			data.setLevel(1);
			maxAttribute = 1;
			if (oldLevel==2) {
				logger.info("Level decreased from 2 to 1");
				data.getAttribute(Attribute.SPLINTER).removeModificationForSource(SplitterTools.LEVEL2);
				GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.ATTRIBUTE_CHANGED, Attribute.SPLINTER, data.getAttribute(Attribute.SPLINTER)));
			}
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		if (event.getType()==GenerationEventType.EXPERIENCE_CHANGED) {
			updateLevelAndMax();
		}
	}
	
	//-------------------------------------------------------------------
	public List<Modification> getUndoList() {
		return new ArrayList<Modification>(undoList);
	}
	
	//-------------------------------------------------------------------
	public int getMaxAttribute() {
		return maxAttribute;
	}

	//-------------------------------------------------------------------
	public int getIncreaseCost(Attribute key) {
		AttributeValue set = data.getAttribute(key);
		
		// Determine new target value
		int newVal = set.getValue()+1;
		
		// Ensure that it cannot be increased above maximum
		// Determine how often the attribute has been increased then
		int times = newVal - set.getStart();

		// Determine how much exp is needed
		return 5 + times*5;
	}

	//--------------------------------------------------------------------
	public void updateHistory() {
		logger.info("Update character history");
		Date now = new Date(System.currentTimeMillis());
		for (Modification changed : undoList) {
			changed.setDate(now);
			data.addToHistory(changed);
		}
	}
	
	/**
	 * Is the character altered. 
	 * <p>
	 * Only existing characters are checked by the {@link #undoList}.
	 */
	public boolean isAltered() {
		return !undoList.isEmpty();
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.CharacterController#getAttributeController()
	 */
	@Override
	public AttributeController getAttributeController() {
		return attributes;
	}

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

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.CharacterController#getMastershipController()
	 */
	@Override
	public MastershipController getMastershipController() {
		return masterships;
	}

	//-------------------------------------------------------------------
	public CultureLoreController getCultureLoreController() {
		return cultures;
	}

	//-------------------------------------------------------------------
	public LanguageController getLanguageController() {
		return languages;
	}

	//-------------------------------------------------------------------
	public void apply(Modification mod) {
		logger.debug("apply "+mod);
		if (mod instanceof AttributeModification) {
			attributes.addModification((AttributeModification) mod);
		} else if (mod instanceof SkillModification) {
			Skill skill = ((SkillModification)mod).getSkill();
			data.getSkillValue(skill).addModification(mod);
		} else {
			logger.warn("Don't know how to apply modification of type "+mod.getClass());
		}
	}

	//-------------------------------------------------------------------
	private void undo(Modification mod) {
		logger.debug("undo "+mod);
		if (mod instanceof AttributeModification) {
			attributes.removeModification((AttributeModification) mod);
		} else if (mod instanceof SkillModification) {
			Skill skill = ((SkillModification)mod).getSkill();
			data.getSkillValue(skill).removeModification(mod);
		} else {
			logger.warn("Don't know how to undo modification of type "+mod.getClass()+": "+mod);
		}
	}

	//-------------------------------------------------------------------
	public void apply(Collection<Modification> modsToApply) {
		for (Modification mod : modsToApply) {
			try {
				apply(mod);
			} catch (Exception e) {
				logger.error("Failed applying modification "+mod,e);
			}
		}
	}

	//-------------------------------------------------------------------
	public void undo(Collection<Modification> modsToApply) {
		for (Modification mod : modsToApply) {
			try {
				undo(mod);
			} catch (Exception e) {
				logger.error("Failed undoing modification "+mod,e);
			}
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @return the unrestrictedMode
	 */
	public boolean isUnrestrictedMode() {
		return skills.isUnrestrictedMode();
	}

	//-------------------------------------------------------------------
	/**
	 * @param unrestrictedMode the unrestrictedMode to set
	 */
	public void setUnrestrictedMode(boolean unrestrictedMode) {
		skills.setUnrestrictedMode(unrestrictedMode);
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.EXPERIENCE_CHANGED, null, new int[]{
				data.getExperienceFree(),
				data.getExperienceInvested()
		}));
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.CharacterController#getSpellController()
	 */
	@Override
	public SpellController getSpellController() {
		return spells;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.Controller#getToDos()
	 */
	@Override
	public List<String> getToDos() {
		List<String> ret = new ArrayList<>();
		ret.addAll(skills.getToDos());
		ret.addAll(powers.getToDos());
		ret.addAll(resources.getToDos());
		ret.addAll(cultures.getToDos());
		ret.addAll(languages.getToDos());
		ret.addAll(spells.getToDos());
		return ret;
	}

}
