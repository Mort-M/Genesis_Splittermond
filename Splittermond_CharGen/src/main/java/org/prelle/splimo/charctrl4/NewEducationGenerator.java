/**
 * 
 */
package org.prelle.splimo.charctrl4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.prelle.splimo.Culture;
import org.prelle.splimo.Education;
import org.prelle.splimo.Race;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.SpliMoCharacter.Gender;
import org.prelle.splimo.modifications.MastershipModification;
import org.prelle.splimo.modifications.ModificationChoice;
import org.prelle.splimo.modifications.RequirementModification;
import org.prelle.splimo.processor.SpliMoCharacterProcessor;

import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.modification.DecisionToMake;
import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class NewEducationGenerator implements EducationController, SpliMoCharacterProcessor {
	
	private static Logger logger = Logger.getLogger("splittermond.chargen.edu");

	private final static ResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splittermond/chargen");

	private SplitterEngineCharacterGenerator parent;
	private SpliMoCharacter model;
	private List<Education> available;
	private List<DecisionToMake> decisions;
	private List<ToDoElement> todos;

	//-------------------------------------------------------------------
	public NewEducationGenerator(SplitterEngineCharacterGenerator charGen) {
		this.parent = charGen;
		this.model  = charGen.getModel();
		available     = new ArrayList<Education>();
		todos     = new ArrayList<>();
		decisions = new ArrayList<>();
		updateAvailableData();
	}

	//-------------------------------------------------------------------
	private void updateAvailableData() {
		available.clear();
		
		nextEducation:
		for (Education edu : SplitterMondCore.getEducations()) {
			// Check for requirements
			for (Modification mod : edu.getModifications()) {
				if (mod instanceof RequirementModification) {
					RequirementModification rmod = (RequirementModification)mod;
					switch (rmod.getType()) {
					case CULTURE:
						Culture reqCulture = SplitterMondCore.getCulture(rmod.getReference());
						if (reqCulture==null) {
							logger.error("education "+edu.getId()+" requires an unknown culture: "+rmod.getReference());
						} else {
							if (model.getCulture()!=reqCulture) {
								continue nextEducation;
							}
						}
						break;
					case RACE:
						Race reqRace = SplitterMondCore.getRace(rmod.getReference());
						if (reqRace==null) {
							logger.error("Selected education "+edu.getId()+" requires an unknown race: "+rmod.getReference());
						} else {
							if (model.getRace()!=reqRace) {
								continue nextEducation;
							}
						}
						break;
					case GENDER:
						Gender reqGender = Gender.valueOf(rmod.getReference());
						if (model.getGender()!=reqGender) {
							continue nextEducation;
						}
						break;
					}
				}
			}
			// All requirements -if there were any - are met
			available.add(edu);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * Return the list of resources that can be added
	 */
	public List<Education> getAvailableEducations() {
		Collections.sort(available);
		return available;
	}

//	//-------------------------------------------------------------------
//	void addModification(EducationModification mod) {
//		logger.debug("Add modification: "+mod);
//		if (!modifications.contains(mod)) {
//			modifications.add(mod);
//			updateAvailableData();
//		}
//	}
//
//	//-------------------------------------------------------------------
//	void addModification(NotEducationModification mod) {
//		logger.debug("Add modification: "+mod);
//		if (!modifications.contains(mod)) {
//			modifications.add(mod);
//			updateAvailableData();
//		}
//	}

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
	private DecisionToMake findDecision(Modification mod) {
		for (DecisionToMake tmp : decisions) {
			if (tmp.getChoice()==mod)
				return tmp;
		}
		return null;
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
			todos.clear();
			
			// All backgrounds are available - remove if not recommended
			available = new ArrayList<>(SplitterMondCore.getEducations());

			/*
			 * Make sure an education has been selected
			 */
			Education selected = model.getEducation();
			if (selected==null) {
				todos.add(new ToDoElement(Severity.STOPPER, RES.getString("educationgen.todo")));
				logger.debug(" no education selected");
			} else {
				// An education has been selected
				for (Modification mod : selected.getModifications()) {
					if (mod instanceof ModificationChoice) {
						DecisionToMake decision = findDecision(mod);
						if (decision==null) {
							// Error: Should have been filled by selectEducation(Education)
							logger.error("Missing decision to make for "+mod);
						} else if (decision.getDecision()!=null) {
							logger.debug(" User decided "+decision.getDecision()+" for "+mod);
							unprocessed.addAll(decision.getDecision());
						} else if (decision.getDecision()==null) {
							logger.trace(" User did not decide yet for "+mod);
						}
					} else if (mod instanceof MastershipModification) {
						MastershipModification mmod = (MastershipModification)mod;
						if (mmod.getMastership()==null && mmod.getSkill()==null && mmod.getSkillType()!=null) {
							DecisionToMake decision = findDecision(mod);
							if (decision==null) {
								// Error: Should have been filled by selectEducation(Education)
								logger.error("Missing decision to make for "+mod);
							} else if (decision.getDecision()!=null) {
								logger.debug(" User decided "+decision.getDecision()+" for "+mod);
								unprocessed.addAll(decision.getDecision());
							} else if (decision.getDecision()==null) {
								logger.trace(" User did not decide yet for "+mod);
							}
						} else {
							unprocessed.add(mod);
						}
					} else if (mod instanceof RequirementModification) {
						RequirementModification rmod = (RequirementModification)mod;
						switch (rmod.getType()) {
						case CULTURE:
							Culture reqCulture = SplitterMondCore.getCulture(rmod.getReference());
							if (reqCulture==null) {
								logger.error("Selected education "+selected.getId()+" requires an unknown culture: "+rmod.getReference());
							} else {
								if (model.getCulture()!=reqCulture) {
									todos.add(new ToDoElement(Severity.WARNING, String.format(RES.getString("educationgen.todo.culturerequirement"), selected.getName(), reqCulture.getName())));
								}
							}
							break;
						case RACE:
							Race reqRace = SplitterMondCore.getRace(rmod.getReference());
							if (reqRace==null) {
								logger.error("Selected education "+selected.getId()+" requires an unknown race: "+rmod.getReference());
							} else {
								if (model.getRace()!=reqRace) {
									todos.add(new ToDoElement(Severity.WARNING, String.format(RES.getString("educationgen.todo.racerequirement"), selected.getName(), reqRace.getName())));
								}
							}
							break;
						case GENDER:
							Gender reqGender = Gender.valueOf(rmod.getReference());
							if (model.getGender()!=reqGender) {
								todos.add(new ToDoElement(Severity.WARNING, String.format(RES.getString("educationgen.todo.genderrequirement"), selected.getName(), reqGender)));
							}
							break;
						}
					} else {
						unprocessed.add(mod);
					}

				}
			}
			
			/*
			 * Check all points are spent
			 */
//			if (pointsLeft>0)
//				todos.add(new ToDoElement(Severity.STOPPER, String.format(RES.getString("powergen.todo"), getPointsLeft())));
		} finally {
			logger.trace("STOP : process");
		}
		return unprocessed;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl4.Generator#getPointsLeft()
	 */
	@Override
	public int getPointsLeft() {
		// TODO Auto-generated method stub
		return 0;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl4.EducationController#selectEducation(org.prelle.splimo.Education)
	 */
	@Override
	public void selectEducation(Education value) {
		logger.info("User selected education "+value.getId());
		model.setEducation(value.getId());
		decisions.clear();
		// Find decisions
		for (Modification mod : value.getModifications()) {
			if (mod instanceof ModificationChoice) {
				logger.debug("  to decide: "+mod);
				decisions.add(new DecisionToMake(mod));
			} else if (mod instanceof MastershipModification) {
				MastershipModification mmod = (MastershipModification)mod;
				if (mmod.getMastership()==null && mmod.getSkill()==null && mmod.getSkillType()!=null) {
					logger.debug("  to decide: "+mod);
					decisions.add(new DecisionToMake(mod));
				}
			}
		}

		parent.runProcessors();
	}

}
