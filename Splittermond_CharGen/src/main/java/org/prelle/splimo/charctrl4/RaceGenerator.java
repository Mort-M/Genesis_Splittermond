/**
 * 
 */
package org.prelle.splimo.charctrl4;

import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.Random;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Race;
import org.prelle.splimo.Size;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.modifications.ModificationChoice;
import org.prelle.splimo.modifications.RaceModification;
import org.prelle.splimo.processor.SpliMoCharacterProcessor;

import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.modification.DecisionToMake;
import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class RaceGenerator implements RaceController, SpliMoCharacterProcessor {

	private static Logger logger = LogManager.getLogger("splittermond.chargen.race");

	private final static ResourceBundle RES = SpliMoCharGenConstants.RES;
	private static Random RANDOM = new Random();

	private SplitterEngineCharacterGenerator parent;
	private SpliMoCharacter model;
	private List<ToDoElement> todos;
	private List<DecisionToMake> decisions;
	private List<Race> available;

	//-------------------------------------------------------------------
	public RaceGenerator(SplitterEngineCharacterGenerator parent) {
		this.parent = parent;
		this.model = parent.getModel();
		available     = new ArrayList<Race>();
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
		List<Modification> unprocessed = new ArrayList<>();

		logger.trace("START: process");
		try {
			todos.clear();
			// All races are available - remove if not recommended
			available = new ArrayList<>(SplitterMondCore.getRaces());

			/*
			 * Process incoming modifications
			 */
			for (Modification mod : previous) {
				if (mod instanceof RaceModification) {
					RaceModification rMod = (RaceModification)mod;
					available.clear();
					available.add(rMod.getRace());
					logger.debug("  mark "+rMod.getRace().getId()+" as only recommended race (from "+rMod.getSource()+")");
				} else {
					unprocessed.add(mod);
				}
			}

			/*
			 * Make sure a race has been selected
			 */
			Race selected = model.getRace();
			if (selected==null) {
				todos.add(new ToDoElement(Severity.STOPPER, RES.getString("racegen.todo")));
			} else {
				// A race has been selected
				for (Modification mod : selected.getModifications()) {
					if (mod instanceof ModificationChoice) {
						DecisionToMake decision = findDecision(mod);
						if (decision==null) {
							// Error: Should have been filled by selectRace(Race)
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
			logger.trace("STOP : process() ends with "+unprocessed.size()+" modifications still to process");
		}

		return unprocessed;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl4.RaceController#getAvailableRaces()
	 */
	@Override
	public List<Race> getAvailableRaces() {
		return available;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl4.RaceController#selectRace(org.prelle.splimo.Race)
	 */
	@Override
	public void selectRace(Race value) {
		if (model.getRace()==value)
			return;

		logger.info("User selected race: "+value);
		decisions.clear();
		// Find decisions
		for (Modification mod : value.getModifications()) {
			if (mod instanceof ModificationChoice) {
				logger.debug("  to decide: "+mod);
				decisions.add(new DecisionToMake(mod, value.getName()));
			}
		}

		model.setRace(value.getId());
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
	 * @see org.prelle.splimo.charctrl4.RaceController#rollHair()
	 */
	@Override
	public String rollHair() {
		Race selectedRace = model.getRace(); 
		if (selectedRace==null)
			return null;
		int roll = 2+RANDOM.nextInt(10)+RANDOM.nextInt(10);
		String color = selectedRace.getHair().getColor(roll);
		// TODO: treat color string as ID not as an already translated word
		logger.debug("rollHair returns '"+color+"'");
		return color;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl4.RaceController#rollEyes()
	 */
	@Override
	public String rollEyes() {
		int roll = 2+RANDOM.nextInt(10)+RANDOM.nextInt(10);
		String color = model.getRace().getEyes().getColor(roll);
		// TODO: treat color string as ID not as an already translated word
		logger.debug("rollEyes returns '"+color+"'");
		return color;
	}

	//-------------------------------------------------------------------
	/**
	 * @return 0=size, 1=weight
	 * @see org.prelle.splimo.charctrl4.RaceController#rollSizeAndWeight()
	 */
	@Override
	public int[] rollSizeAndWeight() {
		Size size = model.getRace().getSize();
		int result = size.getSizeBase();
		// D10
		for (int i=0; i<size.getSizeD10(); i++)
			result += (1+RANDOM.nextInt(10));
		// D6
		for (int i=0; i<size.getSizeD6(); i++)
			result += (1+RANDOM.nextInt(6));

		// Choose weight relative to size
		double relSize = ((double)result-(double)size.getSizeBase())/(10*size.getSizeD10());
		logger.info("Size "+result+" is "+relSize+" %");
		int weightSpan = size.getWeightMax() - size.getWeightMin();
		logger.info("Weight span = "+weightSpan);

		return new int[]{result,  size.getWeightMin() + (int)(relSize*weightSpan)};
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
