/**
 * 
 */
package org.prelle.splimo.charctrl4;

import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.prelle.splimo.MastershipReference;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
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
public class NewSkillGenerator implements SkillController, Generator, SpliMoCharacterProcessor {

	private static Logger logger = Logger.getLogger("splittermond.chargen.skill");

	private final static ResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splittermond/chargen");

	private SplitterEngineCharacterGenerator parent;
	private int maxPointsToSpend;
	private int pointsLeft;
	private int maxValue;
	private SpliMoCharacter model;
	private List<DecisionToMake> decisions;
	private List<ToDoElement> todos;

	//-------------------------------------------------------------------
	public NewSkillGenerator(SplitterEngineCharacterGenerator parent, int points) {
		logger.info("Initialize skill generator with "+points+" points to spend");
		this.parent = parent;
		this.maxPointsToSpend = points;
		this.maxValue = 6;
		this.model    = parent.getModel();
		todos = new ArrayList<>();
		decisions = new ArrayList<>();
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
		return pointsLeft;
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
	public List<ToDoElement> getToDos() {
		ArrayList<ToDoElement> ret = new ArrayList<>();
		if (getPointsLeft()>0)
			ret.add(new ToDoElement(Severity.STOPPER, String.format(RES.getString("skillgen.todo.points"), getPointsLeft())));
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
	 * @see org.prelle.splimo.charctrl.GeneratingSkillController#getToDos(org.prelle.splimo.Skill.SkillType)
	 */
	@Override
	public List<ToDoElement> getToDos(SkillType type) {
		List<ToDoElement> ret = getToDos();
		//		if (getFreeMastershipsLeft(type)>0)
		//			ret.add(String.format(RES.getString("skillgen.todo.master"), getFreeMastershipsLeft(type)));
		// Find grouped skills without focus
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
	 * @see org.prelle.splimo.charctrl.GeneratingSkillController#getToDos(org.prelle.splimo.Skill.SkillType)
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
	 * @see org.prelle.splimo.charctrl4.Controller#getDecisionsToMake()
	 */
	@Override
	public List<DecisionToMake> getDecisionsToMake() {
		return decisions;
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
	@Override
	public List<Modification> process(SpliMoCharacter model, List<Modification> previous) {
		List<Modification> unprocessed = new ArrayList<>();

		logger.trace("START: process");
		try {
			todos.clear();
			// Clear previous skill modifications
			for (SkillValue sVal : model.getSkills())
				sVal.clearModifications();

			for (Modification mod : previous) {
				if (mod instanceof SkillModification) {
					SkillModification amod = (SkillModification)mod;
					if (amod.getSkill()!=null) {
						logger.trace("  Apply modification "+amod);
						model.getSkillValue(amod.getSkill()).addModification(amod);
					} else {
						unprocessed.add(mod);
					}
				} else
					unprocessed.add(mod);
			}


			/*
			 * Calculate points left to spend
			 */
			pointsLeft = maxPointsToSpend;
			for (Skill key : SplitterMondCore.getSkills()) {
				SkillValue val = model.getSkillValue(key);
				logger.trace("  "+val.getModifiedValue()+" for "+key);
				pointsLeft -= val.getModifiedValue();
				if (val.getValue()>6) {
					logger.warn("Too many points distributed in "+key.toString());
				}
			}
			logger.debug("From "+maxPointsToSpend+" points have been "+(maxPointsToSpend-pointsLeft)+" invested and are "+pointsLeft+" left");
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
