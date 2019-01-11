/**
 * 
 */
package org.prelle.splimo.charctrl4;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.MastershipReference;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.modifications.SkillModification;
import org.prelle.splimo.processor.SpliMoCharacterProcessor;

import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.modification.DecisionToMake;
import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class NewSkillLeveller implements SpliMoCharacterProcessor, SkillController {

	private final static ResourceBundle RES = SpliMoCharGenConstants.RES;
	
	private static Logger logger = LogManager.getLogger("splittermond.chargen.skill");

	private SplitterEngineCharacterGenerator parent;
	private int maxValue;
	private SpliMoCharacter model;
	private List<DecisionToMake> decisions;
	private List<ToDoElement> todos;
	private int pointsLeft;

	//-------------------------------------------------------------------
	/**
	 */
	public NewSkillLeveller(SplitterEngineCharacterGenerator parent) {
		this.parent = parent;
		this.model    = parent.getModel();
		todos = new ArrayList<>();
		decisions = new ArrayList<>();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl4.Controller#getToDos()
	 */
	@Override
	public List<ToDoElement> getToDos() {
		ArrayList<ToDoElement> ret = new ArrayList<>();
		if (pointsLeft>0)
			ret.add(new ToDoElement(Severity.STOPPER, String.format(RES.getString("skillgen.todo.points"), pointsLeft)));
		// Find grouped skills without focus
		for (SkillValue val : model.getSkills(SkillType.NORMAL)) {
			if (val.getSkill().isGrouped()) {
				for (MastershipReference masterRef : val.getMasterships()) {
					if ("journeyman".equals(masterRef.getMastership().getId()))
						ret.add(new ToDoElement(Severity.STOPPER, String.format(RES.getString("skillgen.todo.group"), val.getSkill().getName(), masterRef.getMastership().getName())));
					if ("expert".equals(masterRef.getMastership().getId()))
						ret.add(new ToDoElement(Severity.STOPPER, String.format(RES.getString("skillgen.todo.group"), val.getSkill().getName(), masterRef.getMastership().getName())));
					if ("master".equals(masterRef.getMastership().getId()))
						ret.add(new ToDoElement(Severity.STOPPER, String.format(RES.getString("skillgen.todo.group"), val.getSkill().getName(), masterRef.getMastership().getName())));
				}
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
		return decisions;
	}

	//-------------------------------------------------------------------
	private void fireChange(int oldVal, SkillValue skillVal) {
		logger.debug("Inform of "+skillVal);
		int val = skillVal.getValue();
		GenerationEvent event = new GenerationEvent(GenerationEventType.SKILL_CHANGED, skillVal.getSkill(), new int[]{oldVal, val});
		GenerationEventDispatcher.fireEvent(event);
		
		parent.runProcessors();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl4.Controller#decide(de.rpgframework.genericrpg.modification.DecisionToMake, java.util.List)
	 */
	@Override
	public void decide(DecisionToMake choice, List<Modification> choosen) {
		// Find decision
		if (!decisions.contains(choice))
			throw new IllegalArgumentException("Unknown option: "+choice+"   (origin was "+choice.getChoice().getSource()+")");

		for (Modification mod : choosen)
			mod.setSource(choice.getChoice().getSource());
		logger.info("User decided: "+choice.getChoice()+" => "+choosen);
		choice.setDecision(choosen);
		// Recalculate
		parent.runProcessors();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl4.SkillController#getToDos(org.prelle.splimo.Skill.SkillType)
	 */
	@Override
	public List<ToDoElement> getToDos(SkillType type) {
		List<ToDoElement> ret = getToDos();
		for (SkillValue val : model.getSkills(type)) {
			if (val.getSkill().isGrouped()) {
				for (MastershipReference masterRef : val.getMasterships()) {
					if ("journeyman".equals(masterRef.getMastership().getId()))
						ret.add(new ToDoElement(Severity.STOPPER, String.format(RES.getString("skillgen.todo.group"), val.getSkill().getName(), masterRef.getMastership().getName())));
					if ("expert".equals(masterRef.getMastership().getId()))
						ret.add(new ToDoElement(Severity.STOPPER, String.format(RES.getString("skillgen.todo.group"), val.getSkill().getName(), masterRef.getMastership().getName())));
					if ("master".equals(masterRef.getMastership().getId()))
						ret.add(new ToDoElement(Severity.STOPPER, String.format(RES.getString("skillgen.todo.group"), val.getSkill().getName(), masterRef.getMastership().getName())));
				}
			}
		}
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl4.SkillController#getToDos(org.prelle.splimo.Skill)
	 */
	@Override
	public List<ToDoElement> getToDos(Skill skill) {
		List<ToDoElement> ret = new ArrayList<ToDoElement>();
		SkillValue val = model.getSkillValue(skill);
		if (val.getSkill().isGrouped()) {
			for (MastershipReference masterRef : val.getMasterships()) {
				if ("journeyman".equals(masterRef.getMastership().getId()))
					ret.add(new ToDoElement(Severity.STOPPER, String.format(RES.getString("skillgen.todo.group"), val.getSkill().getName(), masterRef.getMastership().getName())));
				if ("expert".equals(masterRef.getMastership().getId()))
					ret.add(new ToDoElement(Severity.STOPPER, String.format(RES.getString("skillgen.todo.group"), val.getSkill().getName(), masterRef.getMastership().getName())));
				if ("master".equals(masterRef.getMastership().getId()))
					ret.add(new ToDoElement(Severity.STOPPER, String.format(RES.getString("skillgen.todo.group"), val.getSkill().getName(), masterRef.getMastership().getName())));
			}
		}
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl4.SkillController#getMaxSkill()
	 */
	@Override
	public int getMaxSkill() {
		switch (model.getLevel()) {
		case 1: return 6;
		case 2: return 9; 
		case 3: return 12;
		case 4: return 15;
		}
		return 0;
	}

	//-------------------------------------------------------------------
	private int getIncreaseCost(int current) {
		if (current<6) return 3;
		if (current<9) return 5;
		if (current<12) return 7;
		if (current<15) return 9;
		return Integer.MAX_VALUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl4.SkillController#canBeIncreased(org.prelle.splimo.SkillValue)
	 */
	@Override
	public boolean canBeIncreased(SkillValue data) {
		if (!model.getSkills().contains(data))
			return false;
		int value = data.getValue();
		// Prevent increasing above the maximum
		if (value>=maxValue)
			return false;

		// Reimbursed generation skill points left
		if (pointsLeft>0)
			return true;
		// Only allow when there are enough exp left
		return model.getExperienceFree()>=getIncreaseCost(data.getValue());
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl4.SkillController#canBeDecreased(org.prelle.splimo.SkillValue)
	 */
	@Override
	public boolean canBeDecreased(SkillValue skill) {
		return skill.getValue()>0;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl4.SkillController#increase(org.prelle.splimo.SkillValue)
	 */
	@Override
	public boolean increase(SkillValue data) {
		if (!canBeIncreased(data))
			return false;

		int oldVal = data.getValue();
		int expCost = getIncreaseCost(oldVal);
		data.setValue(oldVal +1);
		logger.info("increased "+data.getSkill().getId()+" from "+oldVal+" to "+data);
		
		if (pointsLeft>0) {
			pointsLeft--;
		} else {
			// Pay exp
			model.setExperienceFree(model.getExperienceFree()-expCost);
			model.setExperienceInvested(model.getExperienceInvested()+expCost);
			// Log it
			SkillModification mod = new SkillModification(data.getSkill(), data.getValue());
			mod.setExpCost(expCost);
			mod.setDate(new Date(System.currentTimeMillis()));
			model.addToHistory(mod);
		}

		fireChange(oldVal, data);
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl4.SkillController#decrease(org.prelle.splimo.SkillValue)
	 */
	@Override
	public boolean decrease(SkillValue skill) {
		if (!canBeDecreased(skill))
			return false;

		logger.info("decrease "+skill);
		int oldVal = skill.getValue();
		// Find modification 
		SkillModification history = null;
		for (Modification mod : model.getHistory()) {
			if (mod instanceof SkillModification) {
				SkillModification sMod = (SkillModification)mod;
				if (sMod.getSkill()==skill.getSkill() && sMod.getValue()==oldVal) {
					history = sMod;
					model.getHistory().remove(mod);
					break;
				}
			}
		}
		// If this was paid with exp, grant them
		if (history!=null && history.getExpCost()>0) {
			logger.info("  grant "+history.getExpCost()+" exp originally spent on this skill");
			model.setExperienceFree(model.getExperienceFree() + history.getExpCost());
			model.setExperienceInvested(model.getExperienceInvested() - history.getExpCost());
		} else {
			logger.info("  skill was not bought with exp - grant points for another free skill");
			pointsLeft++;
		}
		
		// Really decrease
		skill.setValue(oldVal -1);

		fireChange(oldVal, skill);
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.processor.SpliMoCharacterProcessor#process(org.prelle.splimo.SpliMoCharacter, java.util.List)
	 */
	@Override
	public List<Modification> process(SpliMoCharacter model, List<Modification> previous) {
		List<Modification> unprocessed = new ArrayList<>(previous);

		logger.trace("START: process");
		try {
			// Detect maximum value based on level
			maxValue = getMaxSkill();
			logger.debug("  set maximum skill level to "+maxValue);
			
			// Check if there are free skill points to distribute
			logger.debug("pointsLeft = "+pointsLeft);
			if (pointsLeft>0) {
				todos.add(new ToDoElement(Severity.STOPPER, String.format(RES.getString("skillgen.todo.points"), pointsLeft)));
			} else if (pointsLeft<0) {
				todos.add(new ToDoElement(Severity.STOPPER, String.format(RES.getString("skillgen.todo.points2"), pointsLeft)));
			}
		} finally {
			logger.trace("STOP : process() ends with "+unprocessed.size()+" modifications still to process");
		}
		return unprocessed;
	}

}
