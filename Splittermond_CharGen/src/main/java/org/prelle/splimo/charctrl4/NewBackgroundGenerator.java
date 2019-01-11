/**
 * 
 */
package org.prelle.splimo.charctrl4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Background;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.modifications.BackgroundModification;
import org.prelle.splimo.modifications.ModificationChoice;
import org.prelle.splimo.modifications.ModificationImpl;
import org.prelle.splimo.modifications.NotBackgroundModification;
import org.prelle.splimo.processor.SpliMoCharacterProcessor;

import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.modification.DecisionToMake;
import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class NewBackgroundGenerator implements BackgroundController, SpliMoCharacterProcessor {
	
	private static Logger logger = LogManager.getLogger("splittermond.chargen.backg");

	private final static ResourceBundle RES = SpliMoCharGenConstants.RES;

	private SpliMoCharacter model;
	private SplitterEngineCharacterGenerator parent;
	
	private List<ModificationImpl> modifications;
	private List<Background> available;
	private boolean includeUncommonBackgrounds;
	private List<ToDoElement> todos;
	private List<DecisionToMake> decisions;

	//-------------------------------------------------------------------
	public NewBackgroundGenerator(SplitterEngineCharacterGenerator charGen) {
		this.parent   = charGen;
		this.model    = charGen.getModel();
		available     = new ArrayList<Background>();
		modifications = new ArrayList<ModificationImpl>();
		
		todos = new ArrayList<>();
		decisions = new ArrayList<>();
		updateAvailableResources();
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.BackgroundController#setIncludeUncommonBackgrounds(boolean)
	 */
	@Override
	public void setIncludeUncommonBackgrounds(boolean uncommon) {
		logger.debug("setIncludeUncommonBackgrounds("+uncommon+")");
		includeUncommonBackgrounds = uncommon;
		
		GenerationEvent event = new GenerationEvent(GenerationEventType.BACKGROUND_OFFER_CHANGED, getAvailableBackgrounds());
		GenerationEventDispatcher.fireEvent(event);
	}
	
//	//-------------------------------------------------------------------
//	public void setCommonBackgrounds(List<Background> common) {
//		available = new ArrayList<Background>(common);
//	}

	//-------------------------------------------------------------------
	/**
	 * From all resources create a list of those that are either not selected
	 * yet or can be selected multiple times
	 */
	private void updateAvailableResources() {
		logger.info("updateBackgrounds");
		// List of backgrounds to remove - starts with all that exist
		List<Background> toRemove = new ArrayList<Background>(available);
		
		for (ModificationImpl mod : modifications) {
			if (mod instanceof BackgroundModification) {
				Background toAdd = ((BackgroundModification)mod).getBackground();
				if (available.contains(toAdd))
					toRemove.remove(toAdd);
				else
					available.add(toAdd);
			} else if (mod instanceof NotBackgroundModification) {
				NotBackgroundModification not = (NotBackgroundModification)mod;
				for (Background toAdd : not.getBackgrounds()) {
					if (available.contains(toAdd))
						toRemove.remove(toAdd);
					else
						available.add(toAdd);
				}
			}
		}
		
		// All previous modifications that have not been renewed
		// shall be removed
		logger.debug("avail1: "+available);
		available.removeAll(toRemove);
		logger.debug("avail2: "+available);
		
		
		// Inform listener of change
		GenerationEvent event = new GenerationEvent(GenerationEventType.BACKGROUND_OFFER_CHANGED, available);
		GenerationEventDispatcher.fireEvent(event);
	}

	//-------------------------------------------------------------------
	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.BackgroundController#getAvailableBackgrounds()
	 */
	@Override
	public List<Background> getAvailableBackgrounds() {
		Collections.sort(available);
		if (!includeUncommonBackgrounds) {
			return available;
		} else
			return SplitterMondCore.getBackgrounds();
	}

	//-------------------------------------------------------------------
	void addModification(BackgroundModification mod) {
		logger.debug("Add modification: "+mod);
		if (!modifications.contains(mod)) {
			modifications.add(mod);
			updateAvailableResources();
		}
	}

	//-------------------------------------------------------------------
	void addModification(NotBackgroundModification mod) {
		logger.debug("Add modification: "+mod);
		if (!modifications.contains(mod)) {
			modifications.add(mod);
			updateAvailableResources();
		}
	}

	//-------------------------------------------------------------------
	void removeModification(ModificationImpl mod) {
		logger.debug("remove modification: "+mod);
		if (modifications.contains(mod)) {
			modifications.remove(mod);
			updateAvailableResources();
		}
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.BackgroundController#select(org.prelle.splimo.Background, org.prelle.splimo.chargen.LetUserChooseListener)
	 */
	@Override
	public void select(Background value) {
		if (model.getBackground()==value)
			return;
		
		logger.info("User selected background: "+value);
		decisions.clear();
		// Find decisions
		for (Modification mod : value.getModifications()) {
			if (mod instanceof ModificationChoice) {
				logger.debug("  to decide: "+mod);
				decisions.add(new DecisionToMake(mod));
			}
		}

		model.setBackground(value.getKey());
		parent.runProcessors();
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
		List<Modification> unprocessed = new ArrayList<>();

		logger.trace("START: process");
		try {
			todos.clear();
			// All backgrounds are available - remove if not recommended
			available = new ArrayList<>(SplitterMondCore.getBackgrounds());

			/*
			 * Process incoming modifications
			 */
			for (Modification mod : previous) {
				if (mod instanceof NotBackgroundModification) {
					NotBackgroundModification nbMod = (NotBackgroundModification)mod;
					available.retainAll(nbMod.getBackgrounds());
					for (Background tmp : nbMod.getBackgroundsToRemove()) {
						logger.debug(" * remove unrecommended background "+tmp.getId());
					}
				} else {
					unprocessed.add(mod);
				}
			}
			
			/*
			 * Make sure a background has been selected
			 */
			Background selected = model.getBackground();
			if (selected==null) {
				todos.add(new ToDoElement(Severity.STOPPER, RES.getString("backgrgen.todo")));
			} else {
				// A background has been selected
				for (Modification mod : selected.getModifications()) {
					if (mod instanceof ModificationChoice) {
						DecisionToMake decision = findDecision(mod);
						if (decision==null) {
							// Error: Should have been filled by selectCulture(Culture)
							logger.error("Missing decision to make for "+mod);
						} else if (decision.getDecision()!=null) {
							logger.debug(" User decided "+decision.getDecision()+" for "+mod);
							decision.getDecision().forEach( (mod2) -> mod2.setSource(mod.getSource()));
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

}
