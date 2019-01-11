/**
 * 
 */
package org.prelle.splimo.charctrl4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Language;
import org.prelle.splimo.LanguageReference;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.modifications.LanguageModification;
import org.prelle.splimo.processor.SpliMoCharacterProcessor;

import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.modification.DecisionToMake;
import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class NewLanguageGenerator implements LanguageController, Generator, SpliMoCharacterProcessor {

	private final static ResourceBundle RES = SpliMoCharGenConstants.RES;
	
	protected static Logger logger = LogManager.getLogger("splittermond.chargen.lang");

	private SplitterEngineCharacterGenerator parent;
	private SpliMoCharacter model;
	private int maxFree;
	private int pointsLeft;
	private List<Language> available;

	//--------------------------------------------------------------------
	/**
	 * @param undoList Undo list for all controllers
	 */
	public NewLanguageGenerator(SplitterEngineCharacterGenerator parent, int free) {
		this.parent= parent;
		this.model = parent.getModel();
		this.maxFree = free;
		available = new ArrayList<Language>();

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
		for (Language power : SplitterMondCore.getLanguages()) {
			if (canBeSelected(power))
				available.add(power);
		}
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.LanguageController#canBeSelected(org.prelle.splimo.Language)
	 */
	@Override
	public boolean canBeSelected(Language lore) {
		// Can character afford language?
		if ( (model.getExperienceFree() < 5) && pointsLeft<1)
			return false;

		// Check if already selected
		return !model.hasLanguage(lore);
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.LanguageController#canBeDeselected(org.prelle.splimo.LanguageReference)
	 */
	@Override
	public boolean canBeDeselected(LanguageReference ref) {
		if (!model.hasLanguage(ref.getLanguage()))
			return false;
		return true;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.LanguageController#getAvailableLanguages()
	 */
	@Override
	public List<Language> getAvailableLanguages() {
		return available;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.LanguageController#select(org.prelle.splimo.Language)
	 */
	@Override
	public LanguageReference select(Language lang) {
		if (!canBeSelected(lang)) {
			return null;
		}

		// Change model
		LanguageReference ref = new LanguageReference(lang);
		model.addLanguage(ref);
		logger.info("Added language "+lang+ ((pointsLeft>0)?" as free selection":" for experience"));

		// Pay
		int expNeeded = (pointsLeft>0)?0:5;
		if (expNeeded>0) {
			model.setExperienceFree(model.getExperienceFree()-expNeeded);
			model.setExperienceInvested(model.getExperienceInvested()+expNeeded);
			// Log it
			LanguageModification mod = new LanguageModification(lang);
			mod.setExpCost(expNeeded);
			mod.setDate(new Date());
			model.addToHistory(mod);
		}		

		//		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.LANGUAGE_ADDED, ref));
		//		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.EXPERIENCE_CHANGED, null, new int[]{
		//				model.getExperienceFree(),
		//				model.getExperienceInvested()
		//		}));

		parent.runProcessors();
		return ref;
	}

	//--------------------------------------------------------------------
	private LanguageModification findModification(LanguageReference ref) {
		for (Modification mod : model.getHistory()) {
			if (mod instanceof LanguageModification && ((LanguageModification)mod).getData()==ref.getLanguage())
				return (LanguageModification) mod;
		}
		return null;
	}

	//--------------------------------------------------------------------
	private LanguageModification findAnyLanguageModificationWithExp() {
		for (Modification mod : model.getHistory()) {
			if (mod instanceof LanguageModification && mod.getExpCost()>0)
				return (LanguageModification) mod;
		}
		return null;
	}

	//--------------------------------------------------------------------
	private boolean containsLanguage(Language value) {
		for (LanguageReference ref : model.getLanguages()) {
			if (ref.getLanguage()==value)
				return true;
		}
		return false;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.LanguageController#deselect(org.prelle.splimo.LanguageReference)
	 */
	@Override
	public boolean deselect(LanguageReference ref) {
		if (!canBeDeselected(ref)) {
			return false;
		}

		// Change model
		model.removeLanguage(ref);

		// Undo and eventually grant exp
		LanguageModification mod = findModification(ref);
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
				mod = findAnyLanguageModificationWithExp();
				if (mod!=null) {
					logger.debug("Deselected language was free, so make another formerly bought language free and grant "+mod.getExpCost()+" exp");
					model.removeFromHistory(mod);
					model.setExperienceFree(model.getExperienceFree()+mod.getExpCost());
					model.setExperienceInvested(model.getExperienceInvested()-mod.getExpCost());
				}
			}
		}


		// Inform listener
//		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.LANGUAGE_REMOVED, ref));
//		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.EXPERIENCE_CHANGED, null, new int[]{
//				model.getExperienceFree(),
//				model.getExperienceInvested()
//		}));
		
		parent.runProcessors();
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.Generator#getPointsLeft()
	 */
	@Override
	public int getPointsLeft() {
		return getPointsLeft();
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
			return Arrays.asList(new ToDoElement(Severity.WARNING, String.format(RES.getString("languagegen.newtodo"), pointsLeft)));
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
			
			// Clear all free languages
			for (LanguageReference ref : new ArrayList<>(model.getLanguages())) {
				LanguageModification mod = findModification(ref);
				if (mod==null) {
					model.removeLanguage(ref);
				}
			}
			// Add Basargnomisch
			if (!containsLanguage(SplitterMondCore.getLanguage("basargnomisch")))
				previous.add(new LanguageModification(SplitterMondCore.getLanguage("basargnomisch")));
//			LanguageReference basar = new LanguageReference(SplitterMondCore.getLanguage("basargnomisch"));
//			model.addLanguage(basar);
//			pointsLeft--;
			
			// Apply LanguageModifications
			for (Modification mod : previous) {
				if (mod instanceof LanguageModification) {
					Language lang = ((LanguageModification)mod).getData();
					LanguageReference toAdd = new LanguageReference(lang);
					if (containsLanguage(lang)) {
						// If language is already selected with exp, reimburse its exp
						LanguageModification oldMod = findModification(toAdd);
						if (oldMod!=null) {
							logger.info("System added language "+lang.getId()+" has already been bought with exp - free used exp");
							model.removeFromHistory(oldMod);
							model.setExperienceFree(model.getExperienceFree()+oldMod.getExpCost());
							model.setExperienceInvested(model.getExperienceInvested()-oldMod.getExpCost());
							pointsLeft--;
						}
					} else {
						model.addLanguage(toAdd);
						logger.debug("Add by system: "+lang.getId());
						pointsLeft--;
					}
				} else
					unprocessed.add(mod);
			}
			
			for (Modification mod : model.getHistory()) {
				if (mod instanceof LanguageModification && mod.getExpCost()==0)
					pointsLeft--;
			}
		} finally {
			logger.trace("STOP : process() ends with "+unprocessed.size()+" modifications still to process");
		}
		return unprocessed;
	}

}
