/**
 * 
 */
package org.prelle.splimo.charctrl4;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.prelle.splimo.Skill;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.Spell;
import org.prelle.splimo.SpellValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.charctrl.SpellController.FreeSelection;
import org.prelle.splimo.charctrl4.MastershipController.FreeMastershipSelection;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.modifications.SpellModification;
import org.prelle.splimo.processor.SpliMoCharacterProcessor;
import org.prelle.splimo.requirements.Requirement;

import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.modification.DecisionToMake;
import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class NewSpellGenerator implements SpellController, SpliMoCharacterProcessor {

	private static Logger logger = Logger.getLogger("splittermond.chargen.spells");

	private final static ResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splittermond/chargen");

	private SpliMoCharacter model;

	private SplitterEngineCharacterGenerator charGen;
	private List<ToDoElement> todos;
	private List<FreeSelection> freeSelections;

	//-------------------------------------------------------------------
	/**
	 */
	public NewSpellGenerator(SplitterEngineCharacterGenerator charGen) {
		this.charGen  = charGen;
		model = charGen.getModel();
		todos = new ArrayList<>();
		freeSelections = new ArrayList<FreeSelection>();
		process(model, new ArrayList<>());
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

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.Controller#getToDos()
	 */
	@Override
	public List<ToDoElement> getToDos() {
		return todos;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.Generator#getPointsLeft()
	 */
	@Override
	public int getPointsLeft() {
		// TODO Auto-generated method stub
		return 0;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl4.Controller#getDecisionsToMake()
	 */
	@Override
	public List<DecisionToMake> getDecisionsToMake() {
		return new ArrayList<>();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl4.Controller#decide(de.rpgframework.genericrpg.modification.DecisionToMake, java.util.List)
	 */
	@Override
	public void decide(DecisionToMake choice, List<Modification> choosen) {
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.SpellController#getToDos(org.prelle.splimo.Skill)
	 */
	@Override
	public List<ToDoElement> getToDos(Skill skill) {
		List<ToDoElement> ret = new ArrayList<>();
		for (FreeSelection free : freeSelections) {
			if (free.getUsedFor()==null && free.getSchool()==skill)
				ret.add(new ToDoElement(Severity.WARNING, String.format(RES.getString("spellgen.todo.free"), free.getLevel(), free.getSchool().getName())));
		}
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.SpellController#getFreeSelections()
	 */
	@Override
	public Collection<FreeSelection> getFreeSelections() {
		return freeSelections;
	}

	//-------------------------------------------------------------------
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

	//-------------------------------------------------------------------
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

	//-------------------------------------------------------------------
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

	//-------------------------------------------------------------------
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
		return findLowestPossibleToken(search);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.SpellController#select(org.prelle.splimo.charctrl.SpellController.FreeSelection, org.prelle.splimo.SpellValue)
	 */
	@Override
	public void select(FreeSelection token, SpellValue spell) {
		if (canBeFreeSelected(spell)==null) {
			logger.warn("Trying to select "+spell+" for free which is not possible");
			return;
		}
		
		logger.info("Adding spell "+spell+" using free selection "+token);
		// Add to character
		model.addSpell(spell);
		charGen.runProcessors();
	}

	//-------------------------------------------------------------------
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

	//-------------------------------------------------------------------
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

	//-------------------------------------------------------------------
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

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.SpellController#canBeDeSelected(org.prelle.splimo.SpellValue)
	 */
	@Override
	public boolean canBeDeSelected(SpellValue spell) {
		// TODO Auto-generated method stub
		return false;
	}

	//-------------------------------------------------------------------
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
		logger.info("Select spell "+spell);
		model.addSpell(spell);

		charGen.runProcessors();
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.SpellController#deselect(org.prelle.splimo.SpellValue)
	 */
	@Override
	public boolean deselect(SpellValue spell) {
		if (!canBeDeSelected(spell)) {
			logger.warn("Trying to deselect a spell which cannot be deselected: "+spell);
			return false;
		}

		logger.info("Deselect spell "+spell);
		model.removeSpell(spell);

		charGen.runProcessors();
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.processor.SpliMoCharacterProcessor#process(org.prelle.splimo.SpliMoCharacter, java.util.List)
	 */
	@Override
	public List<Modification> process(SpliMoCharacter model, List<Modification> previous) {

		logger.trace("START: process");
		try {
			todos.clear();

			/* Rebuild free selections */
			freeSelections.clear();
			for (Skill school : SplitterMondCore.getSkills(SkillType.MAGIC)) {
				SkillValue value = model.getSkillValue(school);
				if (value.getModifiedValue()>=1)
					freeSelections.add(new FreeSelection(school, 0));
				if (value.getModifiedValue()>=3)
					freeSelections.add(new FreeSelection(school, 1));
				if (value.getModifiedValue()==6)
					freeSelections.add(new FreeSelection(school, 2));
			}

			/* Assign spells to free selections */
			int expInvest = 0;
			for (SpellValue spell : model.getSpells()) {
				FreeSelection free = findLowestPossibleToken(spell);
				if (free!=null) {
					free.setUsedFor(spell);
					spell.setFreeLevel(free.getLevel());
					logger.info("* Use free spell slot '"+free+"' for "+spell);
				} else {
					int expCost = 0;
					switch (spell.getSpellLevel()) {
					case 0: expCost = 1; break;
					case 1: expCost = 3; break;
					case 2: expCost = 6; break;
					}
					logger.info("* Pay "+expCost+" EP for "+spell);
					
					SpellModification mod = new SpellModification(spell);
					mod.setExpCost(expCost);
					model.addToHistory(mod);
					expInvest+=expCost;
				}
			}
			
			// Find unused free selections
			for (FreeSelection free : freeSelections) {
				if (free.getUsedFor()==null) {
					todos.add(new ToDoElement(Severity.INFO, String.format(RES.getString("spellgen.todo.free"), free.getLevel(), free.getSchool().getName())));
				}
			}
			
			if (expInvest>0) {
				todos.add(new ToDoElement(Severity.INFO, String.format(RES.getString("spellgen.todo.experience"), expInvest)));
				model.setExperienceFree( model.getExperienceFree() - expInvest );
				model.setExperienceInvested( model.getExperienceInvested() + expInvest );
			}
			
		} finally {
			logger.trace("STOP : process()");
		}
		return previous;
	}

}
