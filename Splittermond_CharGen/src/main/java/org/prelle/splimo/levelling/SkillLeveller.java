/**
 * 
 */
package org.prelle.splimo.levelling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Mastership;
import org.prelle.splimo.MastershipReference;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.GeneratingSkillController;
import org.prelle.splimo.charctrl4.SpliMoCharGenConstants;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.modifications.ModificationValueType;
import org.prelle.splimo.modifications.SkillModification;
import org.prelle.splittermond.genlvl.MastershipLevellerAndGenerator;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class SkillLeveller implements GeneratingSkillController {
	
	private static Logger logger = LogManager.getLogger("splittermond.level.skill");

	private final static ResourceBundle RES = SpliMoCharGenConstants.RES;

	private boolean unrestrictedMode;
	
	private List<Modification> undoList;
	private Map<Skill, Stack<SkillModification>> skillUndoStack;
	private MastershipLevellerAndGenerator masterships;

	private SpliMoCharacter data;

	//-------------------------------------------------------------------
	public SkillLeveller(SpliMoCharacter model, List<Modification> undoList, MastershipLevellerAndGenerator masterships) {
		this.data = model;
		this.undoList = undoList;
		this.masterships = masterships;
		// Skills
		skillUndoStack = new HashMap<Skill, Stack<SkillModification>>();
		for (Skill key : SplitterMondCore.getSkills()) {
			skillUndoStack.put(key, new Stack<SkillModification>());
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.SkillController#canBeIncreased(org.prelle.splimo.Skill)
	 */
	@Override
	public boolean canBeIncreased(SkillValue set) {
		// Determine new target value
		int newVal = set.getValue()+1;
		
		// Ensure that it cannot be increased above maximum
		int max = 3 + 3*data.getLevel();
//		logger.debug("canBeIncreased("+skill+") = lvl="+data.getLevel()+" "+max+"  newVal="+newVal);
		if (newVal>max)
			return false;
		
		// Determine how much exp is needed
		int expNeeded = Integer.MAX_VALUE;
		if (newVal<=6) expNeeded=3;
		else if (newVal<= 9) expNeeded=5;
		else if (newVal<=12) expNeeded=7;
		else if (newVal<=15) expNeeded=9;
		return unrestrictedMode || expNeeded<=data.getExperienceFree();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.SkillController#canBeDecreased(org.prelle.splimo.Skill)
	 */
	@Override
	public boolean canBeDecreased(SkillValue set) {
		int formerLevel = 0;
		switch (set.getValue()) {
		case 15: formerLevel=4; break;
		case 12: formerLevel=3; break;
		case  9: formerLevel=2; break;
		case  6: formerLevel=1; break;
		}
		
		for (MastershipReference ref : set.getMasterships()) {
			Mastership master = ref.getMastership();
			// Cannot decrease because the skill requirement for that 
			// mastership would not be fulfilled.
			if (master!=null && master.getLevel()==formerLevel)
				return false;
			if (ref.getSpecialization()!=null && ref.getSpecialization().getLevel()==formerLevel)
				return false;
		}
		return (unrestrictedMode && set.getValue()>0) || !skillUndoStack.get(set.getSkill()).isEmpty();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.SkillController#increase(org.prelle.splimo.Skill)
	 */
	@Override
	public boolean increase(SkillValue set) {
		Skill skill = set.getSkill();
		
		int oldVal = set.getValue();
		int newVal = set.getValue()+1;
		// Determine how much exp is needed
		int expNeeded = Integer.MAX_VALUE;
		if (newVal<=6) expNeeded=3;
		else if (newVal<= 9) expNeeded=5;
		else if (newVal<=12) expNeeded=7;
		else if (newVal<=15) expNeeded=9;

		if (unrestrictedMode)
			expNeeded = 0;
		
		
		if (canBeIncreased(set)) {
			// Update model
			logger.info("Increase "+set.getSkill()+" from "+set.getValue()+" to "+newVal);
			set.setValue(newVal);
			data.setExperienceFree(data.getExperienceFree()-expNeeded);
			data.setExperienceInvested(data.getExperienceInvested()+expNeeded);
			// Add to undo list
			SkillModification mod = new SkillModification(ModificationValueType.ABSOLUT, skill, newVal);
			mod.setExpCost(expNeeded);
			undoList.add(mod);
			skillUndoStack.get(skill).push(mod);
			// Inform listener
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.SKILL_CHANGED, skill, new int[]{oldVal,newVal}));
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.EXPERIENCE_CHANGED, null, new int[]{
					data.getExperienceFree(),
					data.getExperienceInvested()
			}));
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.UNDO_LIST_CHANGED, undoList));
			
			return true;
		}
		return false;
	}

	//-------------------------------------------------------------------
	private boolean canBeRemoved(SkillValue sVal) {
		Skill skill = sVal.getSkill();
		if (!skill.isGrouped())
			return false;
		if (sVal.getValue()>0)
			return false;
		
		// Are there others
		boolean othersExist = false;
		for (SkillValue tmp : data.getSkills()) {
			if (tmp.getSkill()==skill && tmp!=sVal) {
				othersExist = true;
				break;
			}
		}
		return othersExist; 
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.SkillController#decrease(org.prelle.splimo.Skill)
	 */
	@Override
	public boolean decrease(SkillValue set) {
		Skill skill = set.getSkill();
		int oldVal = set.getValue();
		int newVal = set.getValue()-1;

		if (canBeDecreased(set)) {
			// Update model
			logger.info("Decrease "+skill+" from "+set.getValue()+" to "+newVal);
			set.setValue(newVal);
			if (!skillUndoStack.get(skill).isEmpty()) {
				SkillModification mod = skillUndoStack.get(skill).pop();
				data.setExperienceFree(data.getExperienceFree()+mod.getExpCost());
				data.setExperienceInvested(data.getExperienceInvested()-mod.getExpCost());
				logger.info("Undo = "+undoList.remove(mod));
			} else {
				SkillModification mod = new SkillModification(ModificationValueType.ABSOLUT, skill, newVal);
				mod.setExpCost(0);
				undoList.add(mod);
				skillUndoStack.get(skill).push(mod);
			}

			// Inform listener
			if (canBeRemoved(set)) {
				data.removeGroupedSkill(set);
				GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.SKILL_REMOVED, set));
			} else
				GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.SKILL_CHANGED, skill, new int[]{oldVal,newVal}));
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.EXPERIENCE_CHANGED, null, new int[]{
					data.getExperienceFree(),
					data.getExperienceInvested()
			}));
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.UNDO_LIST_CHANGED, undoList));
			return true;
		}
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.SkillController#getMaxSkill()
	 */
	@Override
	public int getMaxSkill() {
		return 3+ 3*data.getLevel();
	}

	//-------------------------------------------------------------------
	/**
	 * @return the unrestrictedMode
	 */
	public boolean isUnrestrictedMode() {
		return unrestrictedMode;
	}

	//-------------------------------------------------------------------
	/**
	 * @param unrestrictedMode the unrestrictedMode to set
	 */
	public void setUnrestrictedMode(boolean unrestrictedMode) {
		this.unrestrictedMode = unrestrictedMode;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.Generator#getPointsLeft()
	 */
	@Override
	public int getPointsLeft() {
		return masterships.getFreeMasterships();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.GeneratingSkillController#getFreeMastershipsLeft(org.prelle.splimo.Skill.SkillType)
	 */
	@Override
	public int getFreeMastershipsLeft(SkillType type) {
		return masterships.getFreeMasterships(type);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.Controller#getToDos()
	 */
	@Override
	public List<String> getToDos() {
		ArrayList<String> ret = new ArrayList<>();
		if (getPointsLeft()>0)
			ret.add(String.format(RES.getString("skillgen.todo.masterships"), getPointsLeft()));
		// Find grouped skills without focus
		for (SkillValue val : data.getSkills(SkillType.NORMAL)) {
			if (val.getSkill().isGrouped()) {
				for (MastershipReference masterRef : val.getMasterships()) {
					if (masterRef.getMastership()!=null) {
						if ("journeyman".equals(masterRef.getMastership().getId()))
							ret.add(String.format(RES.getString("skillgen.todo.group"), val.getSkill().getName(), masterRef.getMastership().getName()));
						if ("expert".equals(masterRef.getMastership().getId()))
							ret.add(String.format(RES.getString("skillgen.todo.group"), val.getSkill().getName(), masterRef.getMastership().getName()));
						if ("master".equals(masterRef.getMastership().getId()))
							ret.add(String.format(RES.getString("skillgen.todo.group"), val.getSkill().getName(), masterRef.getMastership().getName()));
					}
				}
			}
		}
		
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.GeneratingSkillController#getToDos(org.prelle.splimo.Skill.SkillType)
	 */
	@Override
	public List<String> getToDos(SkillType type) {
		ArrayList<String> ret = new ArrayList<>();
		if (getFreeMastershipsLeft(type)>0)
			ret.add(String.format(RES.getString("skillgen.todo.master"), getFreeMastershipsLeft(type)));
		// Find grouped skills without focus
		for (SkillValue val : data.getSkills(type)) {
			if (val.getSkill().isGrouped()) {
				for (MastershipReference masterRef : val.getMasterships()) {
					if (masterRef.getMastership()!=null) {
						if ("journeyman".equals(masterRef.getMastership().getId()))
							ret.add(String.format(RES.getString("skillgen.todo.group"), val.getSkill().getName(), masterRef.getMastership().getName()));
						if ("expert".equals(masterRef.getMastership().getId()))
							ret.add(String.format(RES.getString("skillgen.todo.group"), val.getSkill().getName(), masterRef.getMastership().getName()));
						if ("master".equals(masterRef.getMastership().getId()))
							ret.add(String.format(RES.getString("skillgen.todo.group"), val.getSkill().getName(), masterRef.getMastership().getName()));
					}
				}
			}
		}
		
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.GeneratingSkillController#getToDos(org.prelle.splimo.Skill.SkillType)
	 */
	@Override
	public List<String> getToDos(Skill skill) {
		List<String> ret = new ArrayList<String>();
		SkillValue val = data.getSkillValue(skill);
		if (val.getSkill().isGrouped()) {
			for (MastershipReference masterRef : val.getMasterships()) {
				if (masterRef.getMastership()!=null) {
					if ("journeyman".equals(masterRef.getMastership().getId()))
						ret.add(String.format(RES.getString("skillgen.todo.group"), val.getSkill().getName(), masterRef.getMastership().getName()));
					if ("expert".equals(masterRef.getMastership().getId()))
						ret.add(String.format(RES.getString("skillgen.todo.group"), val.getSkill().getName(), masterRef.getMastership().getName()));
					if ("master".equals(masterRef.getMastership().getId()))
						ret.add(String.format(RES.getString("skillgen.todo.group"), val.getSkill().getName(), masterRef.getMastership().getName()));
				}
			}
		}
		return ret;
	}

}
