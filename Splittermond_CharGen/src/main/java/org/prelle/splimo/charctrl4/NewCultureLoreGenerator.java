/**
 * 
 */
package org.prelle.splimo.charctrl4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.prelle.splimo.CultureLore;
import org.prelle.splimo.CultureLoreReference;
import org.prelle.splimo.Language;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.modifications.CultureLoreModification;
import org.prelle.splimo.processor.SpliMoCharacterProcessor;

import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.modification.DecisionToMake;
import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class NewCultureLoreGenerator implements CultureLoreController, SpliMoCharacterProcessor {

	protected static Logger logger = Logger.getLogger("splittermond.chargen.cultlore");

	private final static ResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splittermond/chargen");

	private SplitterEngineCharacterGenerator parent;
	private SpliMoCharacter model;
	private int maxFree;
	private int pointsLeft;
	/** up to date list of available powers */
	private List<CultureLore> available;
	private boolean includeUnmet;

	//--------------------------------------------------------------------
	/**
	 * @param undoList Undo list for all controllers
	 */
	public NewCultureLoreGenerator(SplitterEngineCharacterGenerator parent, int free) {
		logger.info("Initialize culture lore generator with "+free+" points to spend");
		this.model = parent.getModel();
		this.parent= parent;
		this.maxFree = free;
		available = new ArrayList<CultureLore>();
		includeUnmet = false;

		updateAvailable();
	}

	//--------------------------------------------------------------------
	/**
	 * By default all powers are available. Sort out those that
	 * - are already selected and
	 *   - can only be selected once
	 *   - can be selected multiple times but not yet
	 * - the user does not have the points to pay
	 */
	private void updateAvailable() {
		available.clear();
		for (CultureLore power : SplitterMondCore.getCultureLores()) {
			if (canBeSelected(power))
				available.add(power);
		}
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.CULTURELORE_AVAILABLE_CHANGED, available));
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.CultureLoreController#canBeSelected(org.prelle.splimo.CultureLore)
	 */
	@Override
	public boolean canBeSelected(CultureLore lore) {
		if (lore==null)
			return false;
		// Can character afford culture lore?
		if ( (model.getExperienceFree() < 7) && pointsLeft<=0)
			return false;

		// Does hero speak necessary language
		if (!includeUnmet) {
			boolean languageMet = false;
			for (Language lang : lore.getLanguages()) {
				if (model.hasLanguage(lang))
					languageMet = true;
			}
			if (!languageMet) {
				// Necessary language missing
				return false;
			}
		}

		// Check if already selected
		return !model.hasCultureLore(lore);
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.CultureLoreController#canBeDeselected(org.prelle.splimo.CultureLoreReference)
	 */
	@Override
	public boolean canBeDeselected(CultureLoreReference ref) {
		if (!model.hasCultureLore(ref.getCultureLore()))
			return false;
		return true;
	}
	//-------------------------------------------------------------------
	/**
	 * Shall getAvailableCultureLores() also return culturelores where the
	 * requirements are not fulfilled
	 */
	public void showCultureLoresWithUnmetRequirements(boolean show) {
		includeUnmet = show;
		updateAvailable();
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.CultureLoreController#getAvailableCultureLores()
	 */
	@Override
	public List<CultureLore> getAvailableCultureLores() {
		return available;
	}

	//--------------------------------------------------------------------
	private CultureLoreModification findModification(CultureLoreReference ref) {
		for (Modification mod : model.getHistory()) {
			if (mod instanceof CultureLoreModification && ((CultureLoreModification)mod).getData()==ref.getCultureLore())
				return (CultureLoreModification) mod;
		}
		return null;
	}

	//--------------------------------------------------------------------
	private CultureLoreModification findAnyCultureLoreModificationWithExp() {
		for (Modification mod : model.getHistory()) {
			if (mod instanceof CultureLoreModification && mod.getExpCost()>0)
				return (CultureLoreModification) mod;
		}
		return null;
	}

	//--------------------------------------------------------------------
	private boolean containsCultureLore(CultureLore value) {
		for (CultureLoreReference ref : model.getCultureLores()) {
			if (ref.getCultureLore()==value)
				return true;
		}
		return false;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.CultureLoreController#select(org.prelle.splimo.CultureLore)
	 */
	@Override
	public CultureLoreReference select(CultureLore lore) {
		logger.debug("select "+lore);
		if (!canBeSelected(lore)) {
			logger.error("Trying to select CultureLore "+lore+" which cannot be selected");
			return null;
		}

		CultureLoreReference ref = new CultureLoreReference(lore);
		model.addCultureLore(ref);

		if (pointsLeft==0) {
			// Pay exp
			int expNeeded = 7 ;
			model.setExperienceFree(model.getExperienceFree()-expNeeded);
			model.setExperienceInvested(model.getExperienceInvested()+expNeeded);
			// Log in history
			CultureLoreModification mod = new CultureLoreModification(lore);
			mod.setExpCost(expNeeded);
			mod.setDate(new Date());
		}

		//		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.CULTURELORE_ADDED, ref));
		//		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.EXPERIENCE_CHANGED, null, new int[]{
		//				model.getExperienceFree(),
		//				model.getExperienceInvested()
		//		}));
		parent.runProcessors();

		return ref;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.CultureLoreController#deselect(org.prelle.splimo.CultureLoreReference)
	 */
	@Override
	public boolean deselect(CultureLoreReference ref) {
		if (!canBeDeselected(ref)) {
			return false;
		}

		// Change model
		model.removeCultureLore(ref);

		// Undo and eventually grant exp
		CultureLoreModification mod = findModification(ref);
		if (mod!=null) {
			// Undo from history
			model.removeFromHistory(mod);
			if (mod.getExpCost()>0) {
				logger.debug("Grant "+mod.getExpCost()+" exp for deselected language");
				model.setExperienceFree(model.getExperienceFree()+mod.getExpCost());
				model.setExperienceInvested(model.getExperienceInvested()-mod.getExpCost());
			} else {
				// The deselected language was a free one, but there may be other
				// selected languages that required exp to buy
				mod = findAnyCultureLoreModificationWithExp();
				if (mod!=null) {
					logger.debug("Deselected language was free, so make another formerly bought language free and grant "+mod.getExpCost()+" exp");
					model.removeFromHistory(mod);
					model.setExperienceFree(model.getExperienceFree()+mod.getExpCost());
					model.setExperienceInvested(model.getExperienceInvested()-mod.getExpCost());
				}
			}
		}

//		// Inform listener
//		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.CULTURELORE_REMOVED, ref));
//		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.EXPERIENCE_CHANGED, null, new int[]{
//				model.getExperienceFree(),
//				model.getExperienceInvested()
//		}));
		parent.runProcessors();
		return true;
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
	 * @see org.prelle.splimo.charctrl.Controller#getToDos()
	 */
	@Override
	public List<ToDoElement> getToDos() {
		if (pointsLeft>0)
			return Arrays.asList(new ToDoElement(Severity.STOPPER, RES.getString("cultloregen.todo")));
		return new ArrayList<>();
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
			pointsLeft = maxFree;

			// Clear all free culture lores
			for (CultureLoreReference ref : new ArrayList<>(model.getCultureLores())) {
				CultureLoreModification mod = findModification(ref);
				if (mod==null) {
					model.removeCultureLore(ref);
				}
			}

			// Apply CultureLoreModifications
			for (Modification mod : previous) {
				logger.debug("Process "+mod);
				if (mod instanceof CultureLoreModification) {
					CultureLore lang = ((CultureLoreModification)mod).getData();
					if (lang==null) {
						logger.error("No valid culture lore in modification");
					}
					CultureLoreReference toAdd = new CultureLoreReference(lang);
					if (containsCultureLore(lang)) {
						// If language is already selected with exp, reimburse its exp
						CultureLoreModification oldMod = findModification(toAdd);
						if (oldMod!=null) {
							logger.info("System added language "+lang.getId()+" has already been bought with exp - free used exp");
							model.removeFromHistory(oldMod);
							model.setExperienceFree(model.getExperienceFree()+oldMod.getExpCost());
							model.setExperienceInvested(model.getExperienceInvested()-oldMod.getExpCost());
							pointsLeft--;
						}
					} else {
						model.addCultureLore(toAdd);
						logger.debug("Add by system: "+lang.getId());
						pointsLeft--;
					}
				} else
					unprocessed.add(mod);
			}

			for (Modification mod : model.getHistory()) {
				if (mod instanceof CultureLoreModification && mod.getExpCost()==0)
					pointsLeft--;
			}
		} finally {
			logger.trace("STOP : process() ends with "+unprocessed.size()+" modifications still to process");
		}
		return unprocessed;
	}

}
