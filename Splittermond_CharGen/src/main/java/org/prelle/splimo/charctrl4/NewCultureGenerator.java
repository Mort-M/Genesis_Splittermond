/**
 * 
 */
package org.prelle.splimo.charctrl4;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Culture;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.modifications.MastershipModification;
import org.prelle.splimo.modifications.ModificationChoice;
import org.prelle.splimo.processor.SpliMoCharacterProcessor;

import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.modification.DecisionToMake;
import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class NewCultureGenerator implements CultureController, SpliMoCharacterProcessor {

	private final static ResourceBundle RES = SpliMoCharGenConstants.RES;
	
	private static Logger logger = LogManager.getLogger("splittermond.chargen");

	private SplitterEngineCharacterGenerator parent;
	private SpliMoCharacter model;
	private List<ToDoElement> todos;
	private List<DecisionToMake> decisions;

	//-------------------------------------------------------------------
	public NewCultureGenerator(SplitterEngineCharacterGenerator parent) {
		this.parent = parent;
		this.model = parent.getModel();
		todos = new ArrayList<>();
		decisions = new ArrayList<>();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl4.Controller#getToDos()
	 */
	@Override
	public List<ToDoElement> getToDos() {
		return todos;
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

			/*
			 * Make sure a race has been selected
			 */
			Culture selected = model.getCulture();
			if (selected==null) {
				todos.add(new ToDoElement(Severity.WARNING, RES.getString("cultgen.todo")));
			} else {
				// A race has been selected
				for (Modification mod : selected.getModifications()) {
					if (mod instanceof ModificationChoice) {
						DecisionToMake decision = findDecision(mod);
						if (decision==null) {
							// Error: Should have been filled by selectCulture(Culture)
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
				}
			}
		} finally {
			logger.trace("STOP : process");
		}
		return unprocessed;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl4.CultureController#getAvailableCultures()
	 */
	@Override
	public List<Culture> getAvailableCultures() {
		return SplitterMondCore.getCultures();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl4.CultureController#selectCulture(org.prelle.splimo.Culture)
	 */
	@Override
	public void selectCulture(Culture value) {
		if (model.getCulture()==value)
			return;
		
		logger.info("User selected culture: "+value);
		decisions.clear();
		// Find decisions
		for (Modification mod : value.getModifications()) {
			if (mod instanceof ModificationChoice) {
				logger.debug("  to decide: "+mod);
				decisions.add(new DecisionToMake(mod, value.getName()));
			} else if (mod instanceof MastershipModification) {
				MastershipModification mmod = (MastershipModification)mod;
				if (mmod.getMastership()==null && mmod.getSkill()==null && mmod.getSkillType()!=null) {
					logger.debug("  to decide: "+mod);
					decisions.add(new DecisionToMake(mod, value.getName()));
				}
			}
		}

		model.setCulture(value.getId());
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

}
