/**
 * 
 */
package org.prelle.splittermond.genlvl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.Spell;
import org.prelle.splimo.SpellValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.SpellController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.modifications.SpellModification;
import org.prelle.splimo.requirements.Requirement;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class SpellLevellerAndGenerator implements SpellController, GenerationEventListener {
	
	private static Logger logger = Logger.getLogger("splittermond.genlvl.spell");

	private final static ResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splittermond/chargen");

	private List<Modification> undoList;
	private Map<SpellValue, SpellModification> spellUndoList;
	/** List of tokens usable for free selections */
	private List<FreeSelection> freeSelections;

	private SpliMoCharacter model;

	//--------------------------------------------------------------------
	public SpellLevellerAndGenerator(SpliMoCharacter model, List<Modification> undoList) {
		this.model = model;
		this.undoList = undoList;

		freeSelections = new ArrayList<FreeSelection>();
		spellUndoList  = new HashMap<SpellValue, SpellModification>();
		
		updateTokens();

		// Fill with data from character
		for (SpellValue val : model.getSpells()) {
			if (!val.wasFree())
				continue;
			// Find the lowest possible 
			FreeSelection token = findLowestPossibleToken(val);
			if (token==null) {
				logger.warn("Character says "+val+" was free selected, but this is not possible");
				throw new IllegalArgumentException("No possible free selection for "+val);
			}
			token.setUsedFor(val);
			val.setFreeLevel(token.getLevel());
			logger.debug("Use "+token+" for "+val+" // "+token.getLevel());
		}
		
		GenerationEventDispatcher.addListener(this);
	}

	//--------------------------------------------------------------------
	private FreeSelection findLowestPossibleToken(SpellValue search) {
		if (search==null)
			throw new NullPointerException("Parameter may not be null");
		List<FreeSelection> possible = new ArrayList<FreeSelection>();
		for (FreeSelection token : freeSelections) {
			if (token.getSchool()!=search.getSkill())
				continue;
			if (token.getUsedFor()!=null)
				continue;
			if (token.getLevel()>=search.getSpellLevel())
				possible.add(token);
		}
		Collections.sort(possible);
		
		if (possible.isEmpty())
			return null;
		return possible.get(0);
	}

	//--------------------------------------------------------------------
	private void updateTokens() {
		boolean changed = false; // Has the list of tokens changed
		
		for (Skill school : SplitterMondCore.getSkills(SkillType.MAGIC)) {
			SkillValue value = model.getSkillValue(school);
			// How many free selections are expected for this school
			int expect = ((value.getValue()>0)?1:0) + value.getValue()/3;
			int highestLevel = expect-1;
			/* Count existing. Remove those which level is higher
			 * than the expected level
			 */
			boolean[] found = new boolean[expect];
			for (FreeSelection token : new ArrayList<>(freeSelections)) {
				// Ignore those from other schools
				if (token.getSchool()!=school)
					continue;
				// Remove those which level is too high
				if (token.getLevel()>highestLevel) {
					logger.debug("Free selection in "+school+" level "+token.getLevel()+" deleted: "+token.getUsedFor());
					// Check if there is a free selected spell to be removed
					if (token.getUsedFor()!=null) {
						deselect(token.getUsedFor());
					}
					token.setUsedFor(null);
					freeSelections.remove(token);
					changed = true;
					continue;
				}
				// Mark as found. Check by the way if there are multiple
				// tokens for the same level
				if (found[token.getLevel()]) {
					logger.warn("Found two free selection tokens for the same level in the same school. Removing one");
					token.setUsedFor(null);
					freeSelections.remove(token);
					changed = true;
				}
				// Mark as found
				found[token.getLevel()] = true;
			}
			/* Ensure that for each expected level a token has been found
			 * If not, create it */
			 for (int lvl=0; lvl<found.length; lvl++) {
				 if (!found[lvl]) {
					 freeSelections.add( new FreeSelection(school, lvl) );
					 changed = true;
					 logger.debug("Added a free selection token for "+school+" level "+lvl);
				 }
			 }
		}		
		
		if (changed) 
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.SPELL_FREESELECTION_CHANGED, null, freeSelections));
		
	}

	//--------------------------------------------------------------------
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		if (event.getType()==GenerationEventType.SKILL_CHANGED) {
			Skill skill = (Skill)event.getKey();
			if (skill.getType()==SkillType.MAGIC) {
				logger.debug("Magic skill changed - check free selection for updates");
				updateTokens();
			}
		}		
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.SpellController#getFreeSelections()
	 */
	@Override
	public Collection<FreeSelection> getFreeSelections() {
		updateTokens();
		return freeSelections;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.SpellController#getUnusedFreeSelections()
	 */
	@Override
	public Collection<FreeSelection> getUnusedFreeSelections() {
		List<FreeSelection> unused = new ArrayList<FreeSelection>();
		for (FreeSelection tmp : getFreeSelections()) 
			if (tmp.getUsedFor()==null)
				unused.add(tmp);
		return unused;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.SpellController#getUnusedFreeSelections(org.prelle.splimo.Skill)
	 */
	@Override
	public Collection<FreeSelection> getUnusedFreeSelections(Skill school) {
		List<FreeSelection> unused = new ArrayList<FreeSelection>();
		for (FreeSelection tmp : getFreeSelections()) 
			if (tmp.getUsedFor()==null && tmp.getSchool()==school)
				unused.add(tmp);
		return unused;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.SpellController#getPossibleSpells(org.prelle.splimo.charctrl.SpellController.FreeSelection)
	 */
	@Override
	public List<SpellValue> getPossibleSpells(FreeSelection token) {
		List<SpellValue> possible = new ArrayList<SpellValue>();
		
		for (Spell spell : SplitterMondCore.getSpells(token.getSchool())) {
			SpellValue poss = new SpellValue(spell, token.getSchool());
			if (poss.getSpellLevel()>token.getLevel())
				continue;
			if (model.hasSpell(poss))
				continue;
			possible.add(poss);
		}
		
		return possible;
	}
	
	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.SpellController#canBeFreeSelected(org.prelle.splimo.SpellValue)
	 */
	@Override
	public FreeSelection canBeFreeSelected(SpellValue search) {
//		logger.debug("canBeFreeSelected("+search+")   ... hasSpell="+model.hasSpell(search));
		if (model.hasSpell(search))
			return null;
		
		/*
		 * Search all tokens which are suitable to free select this
		 * spell. Return the one with the lowest level.
		 */
		updateTokens();
		return findLowestPossibleToken(search);
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.SpellController#select(org.prelle.splimo.charctrl.SpellController.FreeSelection, org.prelle.splimo.Spell)
	 */
	@Override
	public void select(FreeSelection token, SpellValue spell) {
		// Ensure that it is a valid token
		if (!freeSelections.contains(token)) {
			logger.warn("Trying to select for free with invalid token");
			return;
		}
		// Ensure token is not used yet
		if (token.getUsedFor()!=null) {
			logger.warn("Trying to select with spent token: "+token.getUsedFor());
			return;
		}
		
		logger.info("Adding spell "+spell+" using free selection "+token);
		// Add to character
		model.addSpell(spell);
		// Mark token spent
		token.setUsedFor(spell);
		spell.setFreeLevel(token.getLevel());
		// Prepare undo
		SpellModification mod = new SpellModification(spell);
		spellUndoList.put(spell, mod);
		undoList.add(mod);
		
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.SPELL_ADDED, spell));
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.SPELL_FREESELECTION_CHANGED, freeSelections));
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.UNDO_LIST_CHANGED, undoList));
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.SpellController#getAvailableSpellSchools()
	 */
	@Override
	public List<Skill> getAvailableSpellSchools() {
		List<Skill> ret = new ArrayList<Skill>();
		for (SkillValue skill : model.getSkills(SkillType.MAGIC)) {
			if (skill.getValue()>0 ) {
				if (!getAvailableSpells(skill.getSkill()).isEmpty())
					ret.add(skill.getSkill());
			}
		}

		Collections.sort(ret);
		return ret;
	}
	
	//--------------------------------------------------------------------
	private int getSpellLevelInSchool(Skill school) {
		int value = model.getSkillValue(school).getValue();
		int lvl=-1;
		if (value>=1) lvl++;
		if (value>=3) lvl++;
		if (value>=6) lvl++;
		if (value>=9) lvl++;
		if (value>=12) lvl++;
		return lvl;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.SpellController#getAvailableSpells(org.prelle.splimo.Skill)
	 */
	@Override
	public List<SpellValue> getAvailableSpells(Skill school) {
		List<SpellValue> avail = new ArrayList<SpellValue>();

		
		for (Spell spell : SplitterMondCore.getSpells(school)) {
			SpellValue toCheck = new SpellValue(spell, school);
			if (canBeSelected(toCheck) || canBeFreeSelected(toCheck)!=null)
				avail.add(toCheck);
		}

		// Sort
		Collections.sort(avail);
		return avail;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.SpellController#select(org.prelle.splimo.SpellValue)
	 */
	@Override
	public boolean select(SpellValue spell) {
		if (!canBeSelected(spell)) {
			logger.warn("Trying to select a spell which cannot be selected: "+spell);
			return false;
		}

		// Select
		int lvl = spell.getSpell().getLevelInSchool(spell.getSkill());
		int expCost = (lvl==0)?1:(lvl*3);
		logger.info("Select spell "+spell+" for "+expCost+" exp");
		model.addSpell(spell);
		model.setExperienceFree(model.getExperienceFree()-expCost);
		model.setExperienceInvested(model.getExperienceInvested()+expCost);
		
		// Add to undo list
		SpellModification mod = new SpellModification(spell);
		mod.setExpCost(expCost);
		undoList.add(mod);
		spellUndoList.put(spell, mod);

		// Inform listener
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.SPELL_ADDED, spell));
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.EXPERIENCE_CHANGED, null, new int[]{
				model.getExperienceFree(),
				model.getExperienceInvested()
		}));
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.UNDO_LIST_CHANGED, undoList));

		return true;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.SpellController#deselect(org.prelle.splimo.SpellValue)
	 */
	@Override
	public boolean deselect(SpellValue spell) {
		if (!canBeDeSelected(spell)) {
			logger.warn("Cannot deselect "+spell);
			return false;
		}
		
		/*
		 * Depending on whether the spell was selected via free selection
		 * or bought with exp proceed
		 */
		
		if (spell.getFreeLevel()>-1) {
			logger.debug("Spell to deselect was a free selection");
			// Go through all free selections and try to find the spell
			for (FreeSelection token : freeSelections) {
				if (token.getUsedFor()!=null && token.getUsedFor().equals(spell)) {
					logger.info("Remove spell "+spell+" bought with free selection");
					token.setUsedFor(null);
					spell.setFreeLevel(-1);
					model.removeSpell(spell);
					
					// Update undolist
					SpellModification mod = spellUndoList.get(spell);
					spellUndoList.remove(spell);
					undoList.remove(mod);

					// Inform listener
					GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.SPELL_REMOVED, spell));
					GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.SPELL_FREESELECTION_CHANGED, freeSelections));
					GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.UNDO_LIST_CHANGED, undoList));
					return true;
				}
			}
			logger.warn("Spell to deselect seems to be selected by free selection - but that could not be found");
			return false;
		} else {
			logger.info("Remove spell "+spell+" bought with exp");
			model.removeSpell(spell);
			
			// Update undolist
			SpellModification mod = spellUndoList.get(spell);
			spellUndoList.remove(spell);
			undoList.remove(mod);
			
			// Get exp back
			model.setExperienceFree(model.getExperienceFree()+mod.getExpCost());
			model.setExperienceInvested(model.getExperienceInvested()-mod.getExpCost());
			// Inform listener
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.SPELL_REMOVED, spell));
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.EXPERIENCE_CHANGED, null, new int[]{
					model.getExperienceFree(),
					model.getExperienceInvested()
			}));
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.UNDO_LIST_CHANGED, undoList));
			return true;
		}

	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.SpellController#canBeSelected(org.prelle.splimo.SpellValue)
	 */
	@Override
	public boolean canBeSelected(SpellValue spell) {
		if (spell==null)
			return false;
		// Check if already selected
		if (model.hasSpell(spell)) {
			return false;
		}
		
		// Check requirements
		for (Requirement req : spell.getSpell().getRequirements()) {
			if (!model.meetsRequirement(req)) {
				return false;
			}
		}
		
		// Check for enough exp
		int lvl = spell.getSpell().getLevelInSchool(spell.getSkill());
		int expCost = (lvl==0)?1:(lvl*3);
		if (model.getExperienceFree()<expCost) {
			return false;
		}
//		logger.info("EXP cost of "+expCost+" met with "+model.getExperienceFree());
		
		if (lvl>getSpellLevelInSchool(spell.getSkill()))
			return false;
		
		return true;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.SpellController#canBeDeSelected(org.prelle.splimo.SpellValue)
	 */
	@Override
	public boolean canBeDeSelected(SpellValue spell) {
		for (FreeSelection token : freeSelections) {
			if (token.getUsedFor()!=null && token.getUsedFor().equals(spell)) 
				return true;
		}
		
		return model.hasSpell(spell) && spellUndoList.containsKey(spell);		
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.Controller#getToDos()
	 */
	@Override
	public List<String> getToDos() {
		List<String> ret = new ArrayList<>();
		for (FreeSelection free : freeSelections) {
			if (free.getUsedFor()==null)
				ret.add(String.format(RES.getString("spellgen.todo.free"), free.getLevel(), free.getSchool().getName()));
		}
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.SpellController#getToDos(org.prelle.splimo.Skill)
	 */
	@Override
	public List<String> getToDos(Skill skill) {
		List<String> ret = new ArrayList<>();
		for (FreeSelection free : freeSelections) {
			if (free.getUsedFor()==null && free.getSchool()==skill)
				ret.add(String.format(RES.getString("spellgen.todo.free"), free.getLevel(), free.getSchool().getName()));
		}
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.Generator#getPointsLeft()
	 */
	@Override
	public int getPointsLeft() {
		return getToDos().size();
	}

}
