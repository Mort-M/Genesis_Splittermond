/**
 * 
 */
package org.prelle.splimo.chargen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.prelle.splimo.CultureLore;
import org.prelle.splimo.CultureLoreReference;
import org.prelle.splimo.Language;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.CharGenMode;
import org.prelle.splimo.charctrl.CultureLoreController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.modifications.CultureLoreModification;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class CultureLoreGenerator implements CultureLoreController, GenerationEventListener {
	
	private static Logger logger = Logger.getLogger("splittermond.level.cultlore");

	private final static ResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splittermond/chargen");

	private CharGenMode mode;
	private CultureLoreReference freeSelected;

	private List<Modification> undoList;
	private Map<CultureLoreReference, Stack<CultureLoreModification>> powerUndoStack;

	private SpliMoCharacter model;
	/** up to date list of available powers */
	private List<CultureLore> available;
	private boolean includeUnmet;
	
	//--------------------------------------------------------------------
	/**
	 * @param undoList Undo list for all controllers
	 */
	public CultureLoreGenerator(SpliMoCharacter model, List<Modification> undoList, CharGenMode mode) {
		this.mode  = mode;
		this.model = model;
		this.undoList = undoList;
		available = new ArrayList<CultureLore>();
		includeUnmet = false;

		powerUndoStack = new HashMap<CultureLoreReference, Stack<CultureLoreModification>>();
		for (CultureLoreReference key : model.getCultureLores())
			powerUndoStack.put(key, new Stack<CultureLoreModification>());
		
		updateAvailable();
		
		GenerationEventDispatcher.addListener(this);
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
		if ( (model.getExperienceFree() < 7) && freeSelected!=null)
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
		return (powerUndoStack.containsKey(ref) && !powerUndoStack.get(ref).isEmpty()) || ref==freeSelected;
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
		/*
		 * If in GENERATION mode and no free culture lore is selected yet,
		 * select it free. Otherwise try to buy language with EXP, no matter
		 * what mode
		 */
		if (mode==CharGenMode.CREATING && freeSelected==null) {
			CultureLoreReference ref = new CultureLoreReference(lore);
			model.addCultureLore(ref);
			freeSelected = ref;
			logger.info("Added culture lore "+lore+" as free selection");
			updateAvailable();
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.CULTURELORE_ADDED, ref));
			return ref;
		}
		
		int expNeeded = 7 ;
		if (model.getExperienceFree()<expNeeded) {
			logger.warn("Cannot select "+lore+" - Exp missing");
			return null;
		}
		
		if (model.hasCultureLore(lore)) {
			logger.warn("Cannot select "+lore+" - already selected");
			return null;
		}

		// CultureLore not selected yet
		CultureLoreReference ref = new CultureLoreReference(lore);
		
		model.addCultureLore(ref);
		model.setExperienceFree(model.getExperienceFree()-expNeeded);
		model.setExperienceInvested(model.getExperienceInvested()+expNeeded);
		
		logger.info("Selected culture lore "+lore);
		
		// make it undoable
		CultureLoreModification mod = new CultureLoreModification(lore);
		mod.setExpCost(expNeeded);
		powerUndoStack.put(ref, new Stack<CultureLoreModification>());
		powerUndoStack.get(ref).push(mod);
		undoList.add(mod);
		updateAvailable();
		
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.CULTURELORE_ADDED, ref));
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.EXPERIENCE_CHANGED, null, new int[]{
				model.getExperienceFree(),
				model.getExperienceInvested()
		}));
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.UNDO_LIST_CHANGED, undoList));
		
		return ref;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.CultureLoreController#deselect(org.prelle.splimo.CultureLoreReference)
	 */
	@Override
	public boolean deselect(CultureLoreReference ref) {
		if (mode==CharGenMode.CREATING && freeSelected==ref) {
			// Free selected language is removed
			model.removeCultureLore(ref);
			freeSelected = null;
			updateAvailable();
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.CULTURELORE_REMOVED, ref));
			
			// If there also was an language selected with EXP, consider this 
			// free selected now
			CultureLoreModification mod = null;
			for (Stack<CultureLoreModification> tmpMod : powerUndoStack.values()) {
				if (!tmpMod.isEmpty()) {
					mod = tmpMod.pop();
					break;
				}
			}
			
			if (mod!=null) {
				logger.info("Consider "+mod.getData()+" free selected now");
				freeSelected = new CultureLoreReference(mod.getData());
				model.setExperienceFree(model.getExperienceFree()+mod.getExpCost());
				model.setExperienceInvested(model.getExperienceInvested()-mod.getExpCost());
				undoList.remove(mod);
				updateAvailable();
//				BabylonEventBus.fireEvent(new BabylonEvent(this, BabylonEventType.UI_MESSAGE, 0, "Consider "+mod.getData()+" free selected now"));
				
				// Inform listener
				GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.EXPERIENCE_CHANGED, null, new int[]{
						model.getExperienceFree(),
						model.getExperienceInvested()
				}));
				GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.UNDO_LIST_CHANGED, undoList));
			}
			return true;
		}
		
		if (canBeDeselected(ref)) {
			// Update model
			CultureLoreModification mod = powerUndoStack.get(ref).pop();
			logger.info("Deselect "+ref);
			model.removeCultureLore(ref);
			model.setExperienceFree(model.getExperienceFree()+mod.getExpCost());
			model.setExperienceInvested(model.getExperienceInvested()-mod.getExpCost());
			undoList.remove(mod);
			updateAvailable();
			
			// Inform listener
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.CULTURELORE_REMOVED, ref));
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.EXPERIENCE_CHANGED, null, new int[]{
					model.getExperienceFree(),
					model.getExperienceInvested()
			}));
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.UNDO_LIST_CHANGED, undoList));
			return true;
		}
		return false;
	}


	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		switch (event.getType()) {
		case EXPERIENCE_CHANGED:
			logger.debug("Experience changed to "+Arrays.toString((int[])event.getValue())+" - update available");
			updateAvailable();
			break;
		case LANGUAGE_ADDED:
		case LANGUAGE_REMOVED:
			logger.debug("Known languages changed to "+Arrays.toString((int[])event.getValue())+" - update available");
			updateAvailable();
			break;
		default:
		}
		
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.Controller#getToDos()
	 */
	@Override
	public List<String> getToDos() {
		if (freeSelected==null)
			return Arrays.asList(RES.getString("cultloregen.todo"));
		return new ArrayList<>();
	}

}
