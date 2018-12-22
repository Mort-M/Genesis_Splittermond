/**
 *
 */
package org.prelle.splimo.charctrl4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.prelle.splimo.Mastership;
import org.prelle.splimo.MastershipReference;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SkillSpecialization;
import org.prelle.splimo.SkillSpecializationValue;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.modifications.MastershipModification;
import org.prelle.splimo.processor.SpliMoCharacterProcessor;
import org.prelle.splimo.requirements.AnyRequirement;
import org.prelle.splimo.requirements.MastershipRequirement;
import org.prelle.splimo.requirements.PowerRequirement;
import org.prelle.splimo.requirements.Requirement;
import org.prelle.splimo.requirements.ResourceRequirement;
import org.prelle.splimo.requirements.SkillRequirement;
import org.prelle.splimo.requirements.SpecialRequirement;
import org.prelle.splimo.requirements.SpellRequirement;

import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.modification.DecisionToMake;
import de.rpgframework.genericrpg.modification.Modification;

/**
 * - Unlimited free masterships
 *   Can be invested in any skill with a value of at least 1
 * - Limited free mastership
 *   Can be invested upon reaching a level in a skill
 * - Bought masterships
 *   Paid with EXP
 *
 * @author prelle
 *
 */
public class NewMastershipLeveller implements MastershipController, SpliMoCharacterProcessor, Generator {

	private static Logger logger = Logger.getLogger("splittermond.chargen.master");

	private final static ResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splittermond/chargen");

	private int unlimitedFree;
	private int pointsLeft;
	/** List of tokens usable for free selections */
	private List<FreeMastershipSelection> freeSelections;
	private SplitterEngineCharacterGenerator parent;

	private SpliMoCharacter data;

	private List<ToDoElement> todos;

	//-------------------------------------------------------------------
	/**
	 */
	public NewMastershipLeveller(SplitterEngineCharacterGenerator charGen, int toSpend) {
		this.parent = charGen;
		logger.info("Initialize mastership leveller with "+toSpend+" points to spend");
		unlimitedFree = toSpend;
		this.data = charGen.getModel();
		freeSelections = new ArrayList<MastershipController.FreeMastershipSelection>();
		todos = new ArrayList<>();

		updateFreeSelections();
	}

	//-------------------------------------------------------------------
	private FreeMastershipSelection getFreeSelectionFor(Skill skill, int level) {
		for (FreeMastershipSelection free : freeSelections) {
			if (free.getUsedFor()==null && (free.getSkill()==skill || free.getSkill()==null) && free.getLevel()==level)
				return free;
		}
		return null;
	}

	//-------------------------------------------------------------------
	private void updateFreeSelections() {
		freeSelections.clear();

		/*
		 * Now check skill levels and ensure that there are the necessary
		 * free selections
		 */
		for (Skill skill : SplitterMondCore.getSkills()) {
			SkillValue sVal = data.getSkillValue(skill);
			// Level 1
			if (sVal.getValue()>=6) {
				freeSelections.add(new FreeMastershipSelection(skill, 1));
				logger.debug("Created free level 1 mastership selection for "+skill);
			}
			// Level 2
			if (sVal.getValue()>=9) {
				freeSelections.add(new FreeMastershipSelection(skill, 2));
				logger.debug("Created free level 2 mastership selection for "+skill);
			}
			// Level 3
			if (sVal.getValue()>=12) {
				freeSelections.add(new FreeMastershipSelection(skill, 3));
				logger.debug("Created free level 3 mastership selection for "+skill);
			}
			// Level 4
			if (sVal.getValue()>=15) {
				freeSelections.add(new FreeMastershipSelection(skill, 4));
				logger.debug("Created free level 4 mastership selection for "+skill);
			}
		}

		/*
		 * 3 free masterships usable for any skill>0 
		 */
		for (int i=0; i<unlimitedFree; i++) {
			freeSelections.add(new FreeMastershipSelection(null, 1));
			logger.debug("Created free level 1 mastership selection");
		}


		/*
		 * In case that a skill has been decreased, remove invalid free masterships
		 */
		for (FreeMastershipSelection free : new ArrayList<>(freeSelections)) {
			if (free.getSkill()==null)
				continue;
			SkillValue sVal = data.getSkillValue(free.getSkill());
			if (free.getLevel()==4 && sVal.getValue()<15) {
				freeSelections.remove(free);
				logger.debug("Removed free level 4 mastership selection for "+free.getSkill());
			}
			if (free.getLevel()==3 && sVal.getValue()<12) {
				freeSelections.remove(free);
				logger.debug("Removed free level 3 mastership selection for "+free.getSkill());
			}
			if (free.getLevel()==2 && sVal.getValue()<9) {
				freeSelections.remove(free);
				logger.debug("Removed free level 2 mastership selection for "+free.getSkill());
			}
			if (free.getLevel()==1 && sVal.getValue()<6) {
				freeSelections.remove(free);
				logger.debug("Removed free level 1 mastership selection for "+free.getSkill());
			}
		}


	}

	//-------------------------------------------------------------------
	private void link(FreeMastershipSelection free, MastershipReference ref) {
		free.setUsedFor(ref);
		ref.setFree(free.getLevel());
	}

	//-------------------------------------------------------------------
	private void unlink(FreeMastershipSelection free, MastershipReference ref) {
		free.setUsedFor(null);
		ref.setFree(0);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.MastershipController#getFreeMasterships()
	 */
	@Override
	public int getFreeMasterships() {
		int count = 0;
		for (FreeMastershipSelection tmp : freeSelections)
			if (tmp.getUsedFor()==null)
				count++;
		return count;
	}

	//-------------------------------------------------------------------
	public int getFreeMasterships(SkillType type) {
		int count = 0;
		for (FreeMastershipSelection tmp : freeSelections)
			if (tmp.getUsedFor()==null && ((tmp.getSkill()!=null && tmp.getSkill().getType()==type) || (tmp.getSkill()==null)) )
				count++;
		return count;
	}

	//-------------------------------------------------------------------
	private List<FreeMastershipSelection> getUnlimitedFreeMastershipsList() {
		List<FreeMastershipSelection> ret = new ArrayList<FreeMastershipSelection>();
		for (FreeMastershipSelection tmp : freeSelections)
			if (tmp.getSkill()==null && tmp.getUsedFor()==null)
				ret.add(tmp);

		Collections.sort(ret, new Comparator<FreeMastershipSelection>() {
			public int compare(FreeMastershipSelection o1, FreeMastershipSelection o2) {
				return ((Integer)o1.getLevel()).compareTo(o2.getLevel());
			}
		});
		return ret;
	}

	//-------------------------------------------------------------------
	private List<FreeMastershipSelection> getFreeMastershipsList(Skill skill) {
		List<FreeMastershipSelection> ret = new ArrayList<FreeMastershipSelection>();
		for (FreeMastershipSelection tmp : freeSelections)
			if (tmp.getSkill()==skill && tmp.getUsedFor()==null)
				ret.add(tmp);

		Collections.sort(ret, new Comparator<FreeMastershipSelection>() {
			public int compare(FreeMastershipSelection o1, FreeMastershipSelection o2) {
				return ((Integer)o1.getLevel()).compareTo(o2.getLevel());
			}
		});
		return ret;
	}

	//-------------------------------------------------------------------
	private List<FreeMastershipSelection> getAllFreeMastershipsList(Skill skill) {
		List<FreeMastershipSelection> ret = getFreeMastershipsList(skill);
		ret.addAll(getUnlimitedFreeMastershipsList());

		Collections.sort(ret, new Comparator<FreeMastershipSelection>() {
			public int compare(FreeMastershipSelection o1, FreeMastershipSelection o2) {
				return ((Integer)o1.getLevel()).compareTo(o2.getLevel());
			}
		});
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.MastershipController#getFreeMasterships(org.prelle.splimo.Skill)
	 */
	@Override
	public int getFreeMasterships(Skill skill) {
		int count = 0;
		for (FreeMastershipSelection tmp : freeSelections)
			if (tmp.getSkill()==skill && tmp.getUsedFor()==null)
				count++;
		return count;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.MastershipController#getAssignedFreeMasterships(org.prelle.splimo.Skill)
	 */
	@Override
	public int getAssignedFreeMasterships(Skill skill) {
		int count = 0;
		for (FreeMastershipSelection tmp : freeSelections)
			if (tmp.getSkill()==skill && tmp.getUsedFor()!=null)
				count++;
		return count;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.MastershipController#canBeSelected(org.prelle.splimo.SkillSpecialization, int)
	 */
	@Override
	public boolean canBeSelected(SkillSpecialization special, int level) {
		Skill skill = special.getSkill();
//		logger.debug("canBeSelected("+special+","+level+") myLvl="+data.getLevel()+"  mySpecLevel="+data.getSkillValue(skill).getSpecializationLevel(special));

		// Does character fulfill level requirement
		if (level>data.getLevel())
			return false;

		// Is the mastership already selected
		if (data.getSkillValue(skill).getSpecializationLevel(special)>=level)
			return false;
		if ((data.getSkillValue(skill).getSpecializationLevel(special)+1)<level)
			return false;

		// Does the player have a free selection for this
		if (data.getSkillValue(skill).getValue()>0) {
			for (FreeMastershipSelection tmp : getFreeMastershipsList(skill)) {
				if (tmp.getLevel()>=level)
					return true;
			}
			for (FreeMastershipSelection tmp : getUnlimitedFreeMastershipsList()) {
				if (tmp.getLevel()>=level)
					return true;
			}
		}

		// Does character fulfill skill requirement
		if (data.getSkillPoints(special.getSkill())< (level*3)+3) {
//			logger.debug("cannot select "+skill+": value ("+data.getSkillPoints(skill)+" not met and no free selections");
			return false;
		}

		// Does the player have the required experience?
		int expNeeded = 5;
		return data.getExperienceFree()>=expNeeded;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.MastershipController#canBeDeselected(org.prelle.splimo.SkillSpecialization, int)
	 */
	@Override
	public boolean canBeDeselected(SkillSpecialization special, int level) {
		Skill skill = special.getSkill();
//		logger.trace("canBeDeselected?("+special+","+level+") myLvl="+data.getLevel()+"  mySpecLevel="+data.getSkillValue(skill).getSpecializationLevel(special));

		// Find the mastership reference in the model
//		MastershipReference ref = null;
//		for (MastershipReference tmp : data.getSkillValue(skill).getMasterships()) {
//			if (tmp.getSpecialization()==null)
//				continue;
//			if (tmp.getSpecialization().getSpecial()==special && tmp.getSpecialization().getLevel()==level) {
//				ref = tmp;
//				break;
//			}
//		}

		// Is the mastership already selected
		if (data.getSkillValue(skill).getSpecializationLevel(special)!=level)
			return false;

		// Only undo those masterships that have been bought recently
//		if (boughtHistorically.contains(ref))
//			return false;

//		// Is this in the undo list
//		List<MastershipModification> stack = masteryUndoStack.get(skill);
//		if (stack==null || stack.isEmpty())
//			return false;
//		for (MastershipModification mod : stack) {
//			if (mod.getSpecialization()==null)
//				continue;
//			if (mod.getSpecialization().getSpecial()==special && mod.getSpecialization().getLevel()==level)
//				return true;
//		}

		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.MastershipController#isEditable(org.prelle.splimo.Skill, org.prelle.splimo.SkillSpecialization, int)
	 */
	@Override
	public boolean isEditable(SkillSpecialization special, int level) {
		return canBeSelected(special, level) || canBeDeselected(special, level);
	}

	//-------------------------------------------------------------------
	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.MastershipController#canBeSelected(org.prelle.splimo.Mastership)
	 */
	@Override
	public boolean canBeSelected(Mastership master) {
		// Does character fulfill level requirement
		if (master.getLevel()>data.getLevel())
			return false;

		// Is the mastership already selected
		if (data.hasMastership(master, null))
			return false;

		// Some masterships have requirements. Are they met?
		for (Requirement req : master.getPrerequisites()) {
//			logger.debug("canBeSelected("+master+") , req = "+req);
			if (!data.meetsRequirement(req))
				return false;
		}

		// Does the player have a free selection for this
		Skill skill = master.getSkill();
		if (data.getSkillValue(skill).getValue()>0) {
			for (FreeMastershipSelection tmp : getFreeMastershipsList(skill)) {
				if (tmp.getLevel()>=master.getLevel())
					return true;
			}
			for (FreeMastershipSelection tmp : getUnlimitedFreeMastershipsList()) {
				if (tmp.getLevel()>=master.getLevel())
					return true;
			}
		}

		// Does character fulfill skill requirement
		if (data.getSkillPoints(master.getSkill())< (master.getLevel()*3)+3) {
//			if (logger.isTraceEnabled())
//				logger.trace("cannot select "+skill+": value ("+data.getSkillPoints(skill)+" not met and no free selections)");
			return false;
		}

		// Does the player have the required experience?
		int expNeeded = master.getLevel()*5;
		return data.getExperienceFree()>=expNeeded;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.MastershipController#getUnfulfilledRequirements(org.prelle.splimo.Mastership)
	 */
	@Override
	public List<String> getUnfulfilledRequirements(Mastership master) {
		List<String> ret = new ArrayList<>();
		if (canBeSelected(master))
			return ret;

		if (master.getLevel()>data.getLevel())
			return ret;

		// Some masterships have requirements. Are they met?
		for (Requirement req : master.getPrerequisites()) {
			if (!data.meetsRequirement(req)) {
				if (req instanceof MastershipRequirement) {
					MastershipRequirement foo = (MastershipRequirement)req;
					if (foo.getFokus()==null)
						ret.add( ((MastershipRequirement)req).getMastership().getName());
					else
						ret.add( ((MastershipRequirement)req).getMastership().getName()+" ("+foo.getFokus().getName()+")");
				} else if (req instanceof SkillRequirement) {
					ret.add( ((SkillRequirement)req).getSkillName()+" "+((SkillRequirement)req).getValue());
				} else if (req instanceof PowerRequirement) {
					ret.add( ((PowerRequirement)req).getPower().getName());
				} else if (req instanceof SpecialRequirement) {
					ret.add( ((SpecialRequirement)req).getSpecialization().getName());
				} else if (req instanceof AnyRequirement) {
					ret.add( ((AnyRequirement)req).toString() );
				} else if (req instanceof SpellRequirement) {
					ret.add( RES.getString("label.spell")+" "+((SpellRequirement)req).getSpellName() );
				} else if (req instanceof ResourceRequirement) {
					ret.add( ((ResourceRequirement)req).getResource().getName()+" "+((ResourceRequirement)req).getValue() );
				} else
					logger.warn("Don't know how to show "+req.getClass());
			}
		}

		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.MastershipController#canBeDeselected(org.prelle.splimo.Mastership)
	 */
	@Override
	public boolean canBeDeselected(Mastership master) {
		Skill skill = master.getSkill();
		logger.trace("canBeDeselected("+master+")?");

		// Find the mastership reference in the model
		MastershipReference ref = null;
		for (MastershipReference tmp : data.getSkillValue(skill).getMasterships()) {
			if (tmp.getMastership()==master) {
				ref = tmp;
				break;
			}
		}
		// Was it selected at all?
		if (ref==null) {
			logger.trace("  No - seems not to be selected");
			return false;
		}

		// Are there any other selected masterships that require this one
		for (MastershipReference tmp : data.getSkillValue(skill).getMasterships()) {
			if (tmp.getMastership()==null) continue;
			for (Requirement req : tmp.getMastership().getPrerequisites()) {
				if (req instanceof MastershipRequirement) {
					if ( ((MastershipRequirement)req).getMastership()==master ) {
						logger.debug("cannot deselect "+master+" because "+tmp.getMastership()+" requires it");
						return false;
					}
				}
			}
		}

		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.MastershipController#isEditable(org.prelle.splimo.Skill, org.prelle.splimo.Mastership)
	 */
	@Override
	public boolean isEditable(Mastership master) {
		if (master.getLevel()>data.getLevel())
			return false;

		return canBeSelected(master) || canBeDeselected(master);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.MastershipController#select(org.prelle.splimo.SkillSpecialization, int)
	 */
	@Override
	public boolean select(SkillSpecialization special, int level) {
		if (!canBeSelected(special, level))
			return false;

		Skill skill = special.getSkill();
		// Check if there is a free mastership for this
		for (FreeMastershipSelection token : getAllFreeMastershipsList(skill)) {
			if (token.getLevel()>=level) {
				// Add new
				logger.debug("Found a free selection token to select "+special+"/"+level+": "+token);
				int oldLevel = data.getSkillValue(skill).getSpecializationLevel(special);
				if (oldLevel==0) {
					MastershipReference ref = new MastershipReference(special, level);
					token.setUsedFor(ref);
					MastershipModification mod = new MastershipModification(special, level);
					link(token, ref);
//					masteryUndoStack.get(skill).add(mod);
//					undoList.add(mod);
					// Add to model
					data.getSkillValue(skill).addMastership(ref);
					logger.info("Add skill specialization '"+special+"' level "+level+" using a free selection "+token);
					GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.MASTERSHIP_ADDED, skill, ref));
					GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.POINTS_LEFT_MASTERSHIPS, skill, getFreeMasterships(skill)));
//					GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.UNDO_LIST_CHANGED, undoList));
				} else if ((oldLevel+1)==level) {
					// Increase level
					MastershipReference ref = null;
					for (MastershipReference tmp : data.getSkillValue(skill).getMasterships()) {
						if (tmp.getSpecialization()!=null && tmp.getSpecialization().getSpecial()==special) {
							ref = tmp;
							break;
						}
					}
					token.setUsedFor(ref);
					MastershipModification mod = new MastershipModification(special, level);
					link(token, ref);
//					masteryUndoStack.get(skill).add(mod);
//					undoList.add(mod);
					// Add to model
					data.getSkillValue(skill).setSpecializationLevel(special, level);
					logger.info("Increase skill specialization '"+special+"' level "+level+" using a free selection "+token);
					GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.MASTERSHIP_CHANGED, skill, ref));
					GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.POINTS_LEFT_MASTERSHIPS, skill, getFreeMasterships(skill)));
//					GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.UNDO_LIST_CHANGED, undoList));
				}
				parent.runProcessors();
				return true;
			}
		}

		// Buy mastership
		int expNeeded = 5;
		MastershipModification mod = new MastershipModification(special, level);
		mod.setExpCost(expNeeded);
//		masteryUndoStack.get(skill).add(mod);
//		undoList.add(mod);
		// Add to model
		int oldLevel = data.getSkillValue(skill).getSpecializationLevel(special);
		if (oldLevel==0) {
			MastershipReference ref = new MastershipReference(special, level);
			data.getSkillValue(skill).addMastership(ref);
			logger.info("Add skill specialization '"+special+"' level "+level+" using exp");
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.MASTERSHIP_ADDED, skill, ref));
//			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.UNDO_LIST_CHANGED, undoList));
		} else {
			// Increase level
			MastershipReference ref = null;
			for (MastershipReference tmp : data.getSkillValue(skill).getMasterships()) {
				if (tmp.getSpecialization()!=null && tmp.getSpecialization().getSpecial()==special) {
					ref = tmp;
					break;
				}
			}
			ref.getSpecialization().setLevel(oldLevel+1);
			logger.info("Increase skill specialization '"+special+"' to level "+level+" using exp");
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.MASTERSHIP_CHANGED, skill, ref));
//			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.UNDO_LIST_CHANGED, undoList));
		}
		data.setExperienceInvested(data.getExperienceInvested()+expNeeded);
		data.setExperienceFree(data.getExperienceFree()-expNeeded);
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.EXPERIENCE_CHANGED, null, new int[]{data.getExperienceFree(), data.getExperienceInvested()}));
		parent.runProcessors();
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.MastershipController#deselect(org.prelle.splimo.SkillSpecialization, int)
	 */
	@Override
	public boolean deselect(SkillSpecialization special, int level) {
		if (!canBeDeselected(special, level))
			return false;

		Skill skill = special.getSkill();

		// Find the mastership reference in the model
		MastershipReference ref = null;
		for (MastershipReference tmp : data.getSkillValue(skill).getMasterships()) {
			if (tmp.getSpecialization()==null) continue;
			if (tmp.getSpecialization().getSpecial()==special && tmp.getSpecialization().getLevel()==level) {
				ref = tmp;
				break;
			}
		}
		// Was it selected at all?
		if (ref==null)
			return false;
		logger.debug("ref = "+ref);

//		// Check if it was system added
//		for (MastershipModification mod : systemAdded.keySet()) {
//			if (mod.getSpecialization()!=null && mod.getSpecialization().getSpecial()==special && mod.getSpecialization().getLevel()==level) {
//				logger.info("Free used FreeSelection");
//				unlink(systemAdded.get(mod), ref);
//				GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.POINTS_LEFT_MASTERSHIPS, skill, getFreeMasterships(skill)));
//				logger.debug("Remove marking as system added for "+special);
//				systemAdded.remove(mod);
//				break;
//			}
//		}

		// Remove mastership
		SkillValue sVal = data.getSkillValue(skill);
		sVal.setSpecializationLevel(special, sVal.getSpecializationLevel(special)-1);
		if (sVal.getSpecializationLevel(special)==0) {
			logger.info("Removed specialization '"+special+"'");
			sVal.removeSpecialization(special);
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.MASTERSHIP_REMOVED, skill, ref));
		} else {
			logger.info("Reduced specialization '"+special+"'");
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.MASTERSHIP_CHANGED, skill, ref));
		}

		/*
		 * Free an eventually claimed free selection token
		 */
		for (FreeMastershipSelection token : freeSelections) {
			if (token.getUsedFor()==ref) {
				logger.info("Free used FreeSelection "+token);
				unlink(token, ref);
				GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.POINTS_LEFT_MASTERSHIPS, skill, getFreeMasterships(skill)));
				break;
			}
		}

		// Find the matching modification
		MastershipModification mod = null;
//		for (Modification tmp : undoList) {
//			if (!(tmp instanceof MastershipModification))
//					continue;
//			MastershipModification tmp2 = (MastershipModification)tmp;
//			if (tmp2.getSpecialization()==null) continue;
//			if (tmp2.getSpecialization().getSpecial()==special && tmp2.getSpecialization().getLevel()==level) {
//				mod = tmp2;
//				break;
//			}
//		}
		// If necessary find modification in character history
		for (Modification tmp : data.getHistory()) {
			if (!(tmp instanceof MastershipModification))
					continue;
			MastershipModification tmp2 = (MastershipModification)tmp;
			if (tmp2.getSkill()!=special.getSkill())
				continue;
			if (tmp2.getSpecialization()==null)
				continue;
			logger.debug("  2 "+tmp2+" // "+(tmp2.getSpecialization().getSpecial()==special)+" // "+tmp2.getLevel()+"=="+level+" = "+(tmp2.getLevel()==level));
			if ( tmp2.getSpecialization().getSpecial()==special && tmp2.getLevel()==level) {
				mod = tmp2;
				logger.trace("  character history modification "+mod);
				break;
			}
		}

//		List<MastershipModification> stack = masteryUndoStack.get(skill);
//		for (MastershipModification tmp : stack) {
//			if (tmp.getSpecialization()==null) continue;
//			if (tmp.getSpecialization().getSpecial()==special && tmp.getSpecialization().getLevel()==level) {
//				mod = tmp;
//				break;
//			}
//		}
//		stack.remove(mod);
		if (mod!=null) {
//			undoList.remove(mod);
//			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.UNDO_LIST_CHANGED, undoList));

			// If exp was paid, return it
			if (mod.getExpCost()>0) {
				data.setExperienceFree(data.getExperienceFree()+mod.getExpCost());
				data.setExperienceInvested(data.getExperienceInvested()-mod.getExpCost());
				GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.EXPERIENCE_CHANGED, null, new int[]{data.getExperienceFree(), data.getExperienceInvested()}));
			}
		}

		parent.runProcessors();
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.MastershipController#setSelected(org.prelle.splimo.Skill, org.prelle.splimo.Mastership, boolean)
	 */
	@Override
	public boolean select(Mastership master) {
		if (!canBeSelected(master))
			return false;

		Skill skill = master.getSkill();
		// Check if there is a free mastership for this
		for (FreeMastershipSelection token : getAllFreeMastershipsList(skill)) {
			if (token.getLevel()>=master.getLevel()) {
				logger.debug("Found a free selection token to select '"+master+"' from "+skill.getId());
				MastershipReference ref = new MastershipReference(master);
				token.setUsedFor(ref);
				MastershipModification mod = new MastershipModification(master);
//				masteryUndoStack.get(skill).add(mod);
				link(token, ref);
//				logger.debug("UndoList = "+undoList);
//				undoList.add(mod);
				// Add to model
				data.getSkillValue(skill).addMastership(ref);
//				applyModifications(master);
				logger.info("Add mastership '"+master+"' using a free selection");
				GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.MASTERSHIP_ADDED, skill, ref));
				GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.POINTS_LEFT_MASTERSHIPS, skill, getFreeMasterships(skill)));
//				GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.UNDO_LIST_CHANGED, undoList));
				parent.runProcessors();
				return true;
			}
		}

		// Buy mastership
		int expNeeded = master.getLevel()*5;
		MastershipModification mod = new MastershipModification(master);
		mod.setExpCost(expNeeded);
		mod.setDate(new Date());
		data.addToHistory(mod);

		// Add to model
		MastershipReference ref = new MastershipReference(master);
		data.getSkillValue(skill).addMastership(ref);
		logger.info("Add mastership '"+master+"' using exp");
//		applyModifications(master);
		data.setExperienceInvested(data.getExperienceInvested()+expNeeded);
		data.setExperienceFree(data.getExperienceFree()-expNeeded);
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.MASTERSHIP_ADDED, skill, ref));
//		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.UNDO_LIST_CHANGED, undoList));
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.EXPERIENCE_CHANGED, null, new int[]{data.getExperienceFree(), data.getExperienceInvested()}));
		parent.runProcessors();
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * Find the reference under which the mastership was added to the
	 * model.
	 */
	private MastershipReference getSelectedReference(Mastership master) {
		// Find the mastership reference in the model
		for (MastershipReference tmp : data.getSkillValue(master.getSkill()).getMasterships()) {
			if (tmp.getMastership()==master)
				return tmp;
		}
		return null;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.MastershipController#deselect(org.prelle.splimo.Skill, org.prelle.splimo.Mastership)
	 */
	@Override
	public boolean deselect(Mastership master) {
		Skill skill = master.getSkill();

		// Find the mastership reference in the model
		MastershipReference ref = getSelectedReference(master);
		// Was it selected at all?
		if (ref==null)
			return false;

		// Remove mastership
		logger.debug("Remove before : "+data.getSkillValue(skill).getMasterships());
		data.getSkillValue(skill).removeMastership(master);
		logger.debug("Remove after  : "+data.getSkillValue(skill).getMasterships());
//		undoModifications(master);
		logger.info("Removed mastership '"+master+"'");

		// Check if it was system added
//		for (MastershipModification mod : systemAdded.keySet()) {
//			if (mod.getMastership()==master) {
//				logger.debug("Remove marking as system added for "+master);
//				systemAdded.remove(mod);
//				break;
//			}
//		}
		logger.debug("Free selection was "+ref.getFree());

//		/*
//		 * If bought with EXP, grant them
//		 */
//		for (Modification mod : data.getHistory()) {
//			if (!(mod instanceof MastershipModification))
//				continue;
//			MastershipModification mMod = (MastershipModification)mod;
//			if (mMod.getMastership()==master && mMod.getExpCost()>0) {
//				int toFree = mMod.getExpCost();
//				logger.info("Free "+toFree+" EP");
//				data.setExperienceFree(data.getExperienceFree()+toFree);
//				data.setExperienceInvested(data.getExperienceInvested()-toFree);
//				GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.POINTS_LEFT_MASTERSHIPS, skill, getFreeMasterships(skill)));
//			}
//		}


		/*
		 * Free an eventually claimed free selection token
		 */
		for (FreeMastershipSelection token : freeSelections) {
			if (token.getUsedFor()==ref) {
				logger.info("Free used FreeSelection: "+token);
				unlink(token, ref);
				logger.debug("Make free selection usable for other skills");
//				token.setSkill(null);
				GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.POINTS_LEFT_MASTERSHIPS, skill, getFreeMasterships(skill)));
				break;
			}
		}
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.MASTERSHIP_REMOVED, skill, ref));

		// Find the matching modification in session history
//		List<MastershipModification> stack = masteryUndoStack.get(skill);
		MastershipModification mod = null;
//		for (Modification tmp : undoList) {
//			if (!(tmp instanceof MastershipModification))
//					continue;
//			MastershipModification tmp2 = (MastershipModification)tmp;
//			if (tmp2.getMastership()==master) {
//				mod = tmp2;
//				logger.trace("  recent modification "+mod);
//				break;
//			}
//		}
		// If necessary find modification in character history
		for (Modification tmp : data.getHistory()) {
			if (!(tmp instanceof MastershipModification))
					continue;
			MastershipModification tmp2 = (MastershipModification)tmp;
			if (tmp2.getMastership()==master) {
				mod = tmp2;
				logger.trace("  character history modification "+mod);
				break;
			}
		}

		if (mod!=null) {
//			undoList.remove(mod);
			data.removeFromHistory(mod);
//			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.UNDO_LIST_CHANGED, undoList));

			// If exp was paid, return it
			if (mod.getExpCost()>0) {
				logger.info("Free "+mod.getExpCost()+" EP");
				data.setExperienceFree(data.getExperienceFree()+mod.getExpCost());
				data.setExperienceInvested(data.getExperienceInvested()-mod.getExpCost());
				GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.EXPERIENCE_CHANGED, null, new int[]{data.getExperienceFree(), data.getExperienceInvested()}));
			}
		}

		parent.runProcessors();
		return true;
	}

	//-------------------------------------------------------------------
	public void addModification(MastershipModification mod) {
		Mastership master = mod.getMastership();
		SkillSpecializationValue specVal = mod.getSpecialization();
		Skill skill = mod.getSkill();
		logger.debug("  addModification: master="+master+"  specVal="+specVal+"  skill="+skill+"   skilltype="+mod.getSkillType());

		if (master!=null) {
			skill = master.getSkill();
			if (data.getSkillValue(skill).hasMastership(master)) {
				// Mastership added a second time.
				logger.info("  Added modification for already selected mastership "+master);
				return;
			} else {
				MastershipReference ref =new MastershipReference(master);
				boolean found = false;
				for (FreeMastershipSelection free : freeSelections) {
					if (free.getUsedFor()==null && free.getSkill()==null && master.getLevel()==1) {
						link(free, ref);
//						systemAdded.put(mod, free);
						logger.debug("  Use "+free+" for "+mod);
						found = true;
						break;
					}
				}
				if (!found) {
					logger.error("Cannot add mastership modification - no free selection");
					return;
				}
				data.getSkillValue(skill).addMastership(ref);
				logger.info("  Added mastership '"+master+"' by system");
				GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.MASTERSHIP_ADDED, skill, ref));
			}
		} else if (specVal!=null) {
			boolean found = false;
			FreeMastershipSelection token = null;
			for (FreeMastershipSelection free : freeSelections) {
				if (free.getUsedFor()==null && free.getSkill()==null && specVal.getLevel()==1) {
					token = free;
					logger.debug("  Use "+free+" for "+mod);
					found = true;
					break;
				}
			}
			if (!found) {
				logger.error("Cannot add mastership modification - no free selection");
				return;
			}
			skill = specVal.getSpecial().getSkill();
			SkillValue skillVal = data.getSkillValue(skill);
			int currLevel = data.getSkillSpecializationLevel(specVal.getSpecial());
			if (currLevel==0 && specVal.getLevel()==1) {
				MastershipReference ref = skillVal.setSpecializationLevel(specVal.getSpecial(), specVal.getLevel());
				link(token, ref);
//				systemAdded.put(mod, token);
				logger.info("  Added skill specialization '"+specVal+"' by system");
				GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.MASTERSHIP_ADDED, skill, ref));
			} else {
				logger.error("  Adding skill specialization "+specVal+" while current level is "+currLevel+" is not supported");
				System.exit(0);
			}
		} else if (skill!=null) {
			// Fill a free mastership slot
			boolean found = false;
			FreeMastershipSelection token = null;
			for (FreeMastershipSelection free : freeSelections) {
				if (free.getUsedFor()==null && free.getSkill()==null) {
					token = free;
					logger.debug("Use "+free+" for "+mod);
					found = true;
					break;
				}
			}
			if (!found) {
				logger.error("Cannot add mastership modification - no free selection");
				return;
			}
			token.setSkill(skill);
//			FreeMastershipSelection token = new FreeMastershipSelection(skill, mod.getLevel());
//			freeSelections.add(0,token);
//			systemAdded.put(mod, token);
			logger.info("  Added free selection in "+token+" by system");
			logger.debug("  Get free masterships after : "+getFreeMasterships(skill));
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.POINTS_LEFT_MASTERSHIPS, skill, getFreeMasterships(skill)));
//		} else if (mod.getSkillType()!=null) {
//			// Fill a free mastership slot
//			boolean found = false;
//			FreeMastershipSelection token = null;
//			for (FreeMastershipSelection free : freeSelections) {
//				if (free.getUsedFor()==null && free.getSkill()==null) {
//					token = free;
//					logger.debug("Use "+free+" for "+mod);
//					found = true;
//					break;
//				}
//			}
//			if (!found) {
//				logger.error("Cannot add mastership modification - no free selection");
//				return;
//			}
//			token.setSkill(skill);
//			logger.info("  Added free selection in "+token+" by system");
//			logger.debug("  Get free masterships after : "+getFreeMasterships(skill));
//			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.POINTS_LEFT_MASTERSHIPS, skill, getFreeMasterships(skill)));
		} else {
			logger.error("Cannot add this modification: "+mod);
		}
	}

//	//-------------------------------------------------------------------
//	public void removeModification(MastershipModification mod) {
//		logger.debug("removeModification");
//		if (!systemAdded.containsKey(mod)) {
//			logger.error("Trying to remove unknown system modification");
//			return;
//		}
//
//
//		FreeMastershipSelection free = systemAdded.get(mod);
//		Mastership master = mod.getMastership();
//		SkillSpecializationValue specVal = mod.getSpecialization();
//		Skill skill = mod.getSkill();
//
//		if (master!=null) {
//			skill = master.getSkill();
//			MastershipReference ref = getSelectedReference(master);
//			data.getSkillValue(skill).removeMastership(master);
//			logger.info("Removed mastership '"+master+"' by system");
//			unlink(free, ref);
//			systemAdded.remove(mod);
//			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.MASTERSHIP_REMOVED, skill, ref));
//		} else if (specVal!=null) {
//			skill = specVal.getSpecial().getSkill();
//			SkillValue skillVal = data.getSkillValue(skill);
//			systemAdded.remove(mod);
//			int currLevel = data.getSkillSpecializationLevel(specVal.getSpecial());
//			if (currLevel==1 && specVal.getLevel()==1) {
//				MastershipReference ref = skillVal.setSpecializationLevel(specVal.getSpecial(), 0);
//				logger.info("Removed skill specialization '"+specVal+"' by system");
//				GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.MASTERSHIP_REMOVED, skill, ref));
//			} else {
//				logger.error("Removing skill specialization "+specVal+" while current level is "+currLevel+" is not supported");
//				System.exit(0);
//			}
//		} else if (skill!=null) {
//			systemAdded.remove(mod);
//			for (FreeMastershipSelection token : freeSelections) {
//				if (token.getSkill()==skill && token.getLevel()==mod.getLevel()) {
//					freeSelections.remove(token);
//					logger.info("Removed free selection in "+token+" by system");
//					GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.POINTS_LEFT_MASTERSHIPS, skill, getFreeMasterships(skill)));
//					return;
//				}
//			}
//			logger.warn("Removing a modification for a free selection that is not known");
//		}
//
//	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.Controller#getToDos()
	 */
	@Override
	public List<ToDoElement> getToDos() {
		List<ToDoElement> ret = new ArrayList<>();
//		for (FreeMastershipSelection tmp : freeSelections) {
//			if (tmp.getUsedFor()==null && tmp.getSkill()!=null) {
//				ret.add(String.format(RES.getString("mastergen.todo.free"), tmp.getLevel(), tmp.getSkill().getName()));
//			}
//			if (tmp.getUsedFor()==null && tmp.getSkill()==null) {
//				ret.add(RES.getString("mastergen.todo.free.any"));
//			}
//		}

		return todos;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.Controller#getToDos()
	 */
	@Override
	public List<String> getToDos(Skill skill) {
		List<String> ret = new ArrayList<>();
		for (FreeMastershipSelection tmp : freeSelections) {
			if (tmp.getUsedFor()==null && tmp.getSkill()==skill) {
				ret.add(String.format(RES.getString("mastergen.todo.free"), tmp.getLevel(), tmp.getSkill().getName()));
			}
		}
		SkillValue val = data.getSkillValue(skill);
		if (val.getSkill().isGrouped()) {
			for (MastershipReference masterRef : val.getMasterships()) {
				if (masterRef.getMastership()!=null) {
					logger.debug("...."+masterRef.getMastership().getId());
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
	@Override
	public List<Modification> process(SpliMoCharacter model, List<Modification> previous) {
		List<Modification> unprocessed = new ArrayList<>();

		logger.trace("START: process");
		try {

			todos.clear();
			
			updateFreeSelections();
			logger.debug("free selections = "+freeSelections);

			/*
			 * Now assign the existing user selected masterships to free selections.
			 * All system assigned masterships have been cleared before
			 */
			for (SkillValue sval : model.getSkills()) {
				for (MastershipReference ref : sval.getMasterships()) {
					FreeMastershipSelection free = getFreeSelectionFor(sval.getSkill(), ref.getMastership().getLevel());
					if (free!=null) {
						logger.debug(" use "+free+" for "+sval.getSkill().getId()+"/"+ref.getMastership());
						free.setUsedFor(ref);
						freeSelections.remove(free);
					} else {
						logger.debug(" no free mastership found for "+sval.getSkill().getId()+"/"+ref.getMastership());
					}
				}
			}
			
			/* 
			 * Now process system given masterships
			 */
			for (Modification mod : previous) {
				if (mod instanceof MastershipModification) {
					MastershipModification mmod = (MastershipModification)mod;
					logger.debug("mmod = "+mmod+" (from "+mod.getSource()+")");
					addModification(mmod);
				} else
					unprocessed.add(mod);
			}
			
			/* 
			 * Count free unused masterships 
			 */
			pointsLeft = 0;
			for (FreeMastershipSelection free : freeSelections) {
				logger.debug("check "+free+"     usedFor="+free.getUsedFor()+"   skill="+free.getSkill()+"   level="+free.getLevel());
				if (free.getUsedFor()==null) {
					if (free.getSkill()==null) {
						pointsLeft++;
					} else {
						todos.add(new ToDoElement(Severity.WARNING, String.format(RES.getString("mastergen.todo.free.skill"), free.getLevel(), free.getSkill().getName())));
					}
				}
			}
			if (pointsLeft>0) {
				todos.add(new ToDoElement(Severity.WARNING, String.format(RES.getString("mastergen.todo.free.any"), pointsLeft)));
			}
			
		} finally {
			logger.trace("STOP : process() ends with "+unprocessed.size()+" modifications still to process");
		}
		return unprocessed;
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
		// TODO Auto-generated method stub
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl4.Generator#getPointsLeft()
	 */
	@Override
	public int getPointsLeft() {
		return pointsLeft;
	}

}
