/**
 * 
 */
package org.prelle.splimo.chargen;

import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.prelle.splimo.Mastership;
import org.prelle.splimo.MastershipReference;
import org.prelle.splimo.Skill;
import org.prelle.splimo.SkillSpecialization;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.GeneratingSkillController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.modifications.SkillModification;
import org.prelle.splittermond.genlvl.MastershipLevellerAndGenerator;

/**
 * @author prelle
 *
 */
public class SkillGenerator implements GeneratingSkillController {
	
	private static Logger logger = Logger.getLogger("splittermond.chargen.skill");

	private final static ResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splittermond/chargen");

	private int maxPointsToSpend;
	private int maxValue;
	private SpliMoCharacter model;
	private MastershipLevellerAndGenerator masterships;

	//-------------------------------------------------------------------
	public SkillGenerator(int toSpend, SpliMoCharacter model, MastershipLevellerAndGenerator masterships) {
		logger.info("Initialize skill generator with "+toSpend+" points to spend");
		this.maxPointsToSpend = toSpend;
		this.maxValue = 6;
		this.model    = model;
		this.masterships = masterships;
	}

	//-------------------------------------------------------------------
	private void fireChange(int oldVal, SkillValue skillVal) {
		logger.debug("Inform of "+skillVal);
		int val = skillVal.getValue();
		GenerationEvent event = new GenerationEvent(GenerationEventType.SKILL_CHANGED, skillVal.getSkill(), new int[]{oldVal, val});
		GenerationEventDispatcher.fireEvent(event);
	}

	//-------------------------------------------------------------------
	void addModification(SkillModification mod) {
		SkillValue ref = model.getSkillValue(mod.getSkill());
		int oldVal = ref.getValue();
		
		int maxRaise = maxValue - oldVal;
		int add      = Math.min(getPointsLeft(), Math.min(mod.getValue(), maxRaise));
		logger.debug("Add "+add+" to "+ref);
		ref.setValue(oldVal + add);
		
		fireChange(oldVal, ref);
	}

	//-------------------------------------------------------------------
	void removeModification(SkillModification mod) {
		SkillValue ref = model.getSkillValue(mod.getSkill());
		int oldVal = ref.getValue();
		
		int maxSub = Math.min(oldVal, mod.getValue());
		logger.info("Remove "+maxSub+" from "+ref);
		ref.setValue(oldVal - maxSub);

		fireChange(oldVal, ref);
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.Generator#getPointsLeft()
	 */
	@Override
	public int getPointsLeft() {
		int left = maxPointsToSpend;
		for (SkillValue tmp : model.getSkills()) {
			left -= tmp.getValue();
		}
		return left;
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
	public boolean canBeIncreased(SkillValue data) {
		if (!model.getSkills().contains(data))
			return false;
		int value = data.getValue();
		// Prevent increasing above the maximum
		if (value>=maxValue)
			return false;
		
		// Only allow when there are points left
		return getPointsLeft()>0;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.SkillController#canBeDecreased(org.prelle.splimo.SkillValue)
	 */
	@Override
	public boolean canBeDecreased(SkillValue data) {
		return data.getValue()>0;
	}

	//-------------------------------------------------------------------
	@Override
	public boolean increase(SkillValue data) {
		if (!canBeIncreased(data))
			return false;
		
		logger.info("increase "+data);
		int oldVal = data.getValue();
		data.setValue(oldVal +1);
		
		fireChange(oldVal, data);
		return true;
	}

	//-------------------------------------------------------------------
	public boolean decrease(SkillValue data) {
		if (!canBeDecreased(data))
			return false;
		
		logger.info("decrease "+data);
		int oldVal = data.getValue();
		data.setValue(oldVal -1);
		
		fireChange(oldVal, data);
		return true;
	}

	//-------------------------------------------------------------------
	public SkillValue getValueFor(Skill skill) {
		return model.getSkillValue(skill);
	}

	//-------------------------------------------------------------------
	public List<SkillValue> getValues() {
		List<SkillValue> ret = new ArrayList<SkillValue>();
		ret.addAll(model.getSkills(SkillType.COMBAT));
		ret.addAll(model.getSkills(SkillType.NORMAL));
		ret.addAll(model.getSkills(SkillType.MAGIC));
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.SkillController#getMaxSkill()
	 */
	@Override
	public int getMaxSkill() {
		return 6;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.Controller#getToDos()
	 */
	@Override
	public List<String> getToDos() {
		ArrayList<String> ret = new ArrayList<>();
		if (getPointsLeft()>0)
			ret.add(String.format(RES.getString("skillgen.todo.points"), getPointsLeft()));
		// Find grouped skills without focus
		for (SkillValue val : model.getSkills(SkillType.NORMAL)) {
			if (val.getSkill().isGrouped()) {
				for (MastershipReference masterRef : val.getMasterships()) {
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

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.GeneratingSkillController#getToDos(org.prelle.splimo.Skill.SkillType)
	 */
	@Override
	public List<String> getToDos(SkillType type) {
		List<String> ret = getToDos();
		if (getFreeMastershipsLeft(type)>0)
			ret.add(String.format(RES.getString("skillgen.todo.master"), getFreeMastershipsLeft(type)));
		// Find grouped skills without focus
		for (SkillValue val : model.getSkills(type)) {
			if (val.getSkill().isGrouped()) {
				for (MastershipReference masterRef : val.getMasterships()) {
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

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.GeneratingSkillController#getToDos(org.prelle.splimo.Skill.SkillType)
	 */
	@Override
	public List<String> getToDos(Skill skill) {
		List<String> ret = new ArrayList<String>();
		SkillValue val = model.getSkillValue(skill);
		if (val.getSkill().isGrouped()) {
			for (MastershipReference masterRef : val.getMasterships()) {
				if ("journeyman".equals(masterRef.getMastership().getId()))
					ret.add(String.format(RES.getString("skillgen.todo.group"), val.getSkill().getName(), masterRef.getMastership().getName()));
				if ("expert".equals(masterRef.getMastership().getId()))
					ret.add(String.format(RES.getString("skillgen.todo.group"), val.getSkill().getName(), masterRef.getMastership().getName()));
				if ("master".equals(masterRef.getMastership().getId()))
					ret.add(String.format(RES.getString("skillgen.todo.group"), val.getSkill().getName(), masterRef.getMastership().getName()));
			}
		}
		return ret;
	}

}
