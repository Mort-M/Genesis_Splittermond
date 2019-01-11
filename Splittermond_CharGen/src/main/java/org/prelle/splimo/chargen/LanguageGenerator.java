/**
 * 
 */
package org.prelle.splimo.chargen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Language;
import org.prelle.splimo.LanguageReference;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.CharGenMode;
import org.prelle.splimo.charctrl.Generator;
import org.prelle.splimo.charctrl.LanguageController;
import org.prelle.splimo.charctrl4.SpliMoCharGenConstants;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.modifications.LanguageModification;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class LanguageGenerator implements LanguageController, Generator, GenerationEventListener {

	private final static ResourceBundle RES = SpliMoCharGenConstants.RES;
	
	private static Logger logger = LogManager.getLogger("splittermond.chargen");

	private CharGenMode mode;
	private LanguageReference freeSelected;
	
	private List<Modification> undoList;
	private Map<LanguageReference, Stack<LanguageModification>> langUndoStack;

	private SpliMoCharacter model;
	/** up to date list of available powers */
	private List<Language> available;
	
	//--------------------------------------------------------------------
	/**
	 * @param undoList Undo list for all controllers
	 */
	public LanguageGenerator(SpliMoCharacter model, List<Modification> undoList, CharGenMode mode) {
		this.mode  = mode;
		this.model = model;
		this.undoList = undoList;
		available = new ArrayList<Language>();

		langUndoStack = new HashMap<LanguageReference, Stack<LanguageModification>>();
		for (LanguageReference key : model.getLanguages())
			langUndoStack.put(key, new Stack<LanguageModification>());
		
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
		if ( (model.getExperienceFree() < 5) && freeSelected!=null)
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
		return (langUndoStack.containsKey(ref) && !langUndoStack.get(ref).isEmpty()) || ref==freeSelected;
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
//		try {
//			throw new RuntimeException("select language "+lang+" mode="+mode+"   free="+freeSelected);
//		} catch (Exception e) {
//			logger.error("Trace",e);
//		}
		/*
		 * If in GENERATION mode and no free language is selected yet,
		 * select it free. Otherwise try to buy language with EXP, no matter
		 * what mode
		 */
		if (mode==CharGenMode.CREATING && freeSelected==null) {
			LanguageReference ref = new LanguageReference(lang);
			model.addLanguage(ref);
			freeSelected = ref;
			logger.info("Added language "+lang+" as free selection");
			updateAvailable();
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.LANGUAGE_ADDED, ref));
			return ref;
		}
		
		int expNeeded = 5 ;
		if (model.getExperienceFree()<expNeeded) 
			return null;
		
		if (model.hasLanguage(lang)) 
			return null;

		// Language not selected yet
		LanguageReference ref = new LanguageReference(lang);
		
		model.addLanguage(ref);
		model.setExperienceFree(model.getExperienceFree()-expNeeded);
		model.setExperienceInvested(model.getExperienceInvested()+expNeeded);
		
		logger.info("Selected language "+lang);
		
		// make it undoable
		LanguageModification mod = new LanguageModification(lang);
		mod.setExpCost(expNeeded);
		langUndoStack.put(ref, new Stack<LanguageModification>());
		langUndoStack.get(ref).push(mod);
		undoList.add(mod);
		updateAvailable();
		
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.LANGUAGE_ADDED, ref));
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.EXPERIENCE_CHANGED, null, new int[]{
				model.getExperienceFree(),
				model.getExperienceInvested()
		}));
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.UNDO_LIST_CHANGED, undoList));
		
		return ref;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.LanguageController#deselect(org.prelle.splimo.LanguageReference)
	 */
	@Override
	public boolean deselect(LanguageReference ref) {
		if (mode==CharGenMode.CREATING && freeSelected==ref) {
			// Free selected language is removed
			model.removeLanguage(ref);
			freeSelected = null;
			updateAvailable();
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.LANGUAGE_REMOVED, ref));
			
			// If there also was an language selected with EXP, consider this 
			// free selected now
			LanguageModification mod = null;
			for (Stack<LanguageModification> tmpMod : langUndoStack.values()) {
				if (!tmpMod.isEmpty()) {
					mod = tmpMod.pop();
					break;
				}
			}
			
			if (mod!=null) {
				logger.info("Consider "+mod.getData()+" free selected now");
				freeSelected = new LanguageReference(mod.getData());
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
			LanguageModification mod = langUndoStack.get(ref).pop();
			logger.info("Deselect "+ref);
			model.removeLanguage(ref);
			model.setExperienceFree(model.getExperienceFree()+mod.getExpCost());
			model.setExperienceInvested(model.getExperienceInvested()-mod.getExpCost());
			undoList.remove(mod);
			updateAvailable();
			
			// Inform listener
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.LANGUAGE_REMOVED, ref));
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
		default:
		}
		
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.Generator#getPointsLeft()
	 */
	@Override
	public int getPointsLeft() {
		switch (mode) {
		case CREATING:
			return (freeSelected!=null)?0:1;
		default:
			return 0;
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.Controller#getToDos()
	 */
	@Override
	public List<String> getToDos() {
		if (freeSelected==null)
			return Arrays.asList(RES.getString("languagegen.todo"));
		return new ArrayList<>();
	}

}
