/**
 * 
 */
package org.prelle.splimo.chargen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Background;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.BackgroundController;
import org.prelle.splimo.charctrl4.SpliMoCharGenConstants;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.modifications.BackgroundModification;
import org.prelle.splimo.modifications.ModificationImpl;
import org.prelle.splimo.modifications.NotBackgroundModification;

/**
 * @author prelle
 *
 */
public class BackgroundGenerator implements BackgroundController {

	private final static ResourceBundle RES = SpliMoCharGenConstants.RES;
	
	private static Logger logger = LogManager.getLogger("splittermond.chargen");

	private SpliMoCharacter model;
	private SpliMoCharacterGenerator charGen;
	private List<ModificationImpl> modifications;
	private List<Background> available;
	private Background selected;
	private boolean includeUncommonBackgrounds;

	//-------------------------------------------------------------------
	public BackgroundGenerator(SpliMoCharacterGenerator charGen, SpliMoCharacter model) {
		this.charGen  = charGen;
		this.model    = model;
		available     = new ArrayList<Background>();
		modifications = new ArrayList<ModificationImpl>();
		
		updateAvailableResources();
	}

	//-------------------------------------------------------------------
	private void fireChange(Background data) {
		GenerationEvent event = new GenerationEvent(GenerationEventType.BACKGROUND_SELECTED, data);
		GenerationEventDispatcher.fireEvent(event);
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

	//-------------------------------------------------------------------
	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.BackgroundController#getSelected()
	 */
	@Override
	public Background getSelected() {
		return selected;
	}

	//-------------------------------------------------------------------
	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.BackgroundController#select(org.prelle.splimo.Background, org.prelle.splimo.chargen.LetUserChooseListener)
	 */
	@Override
	public void select(Background data, LetUserChooseListener callback) {
		logger.info("selectBackground("+data.getName()+")");
		if (selected==data) {
			// Nothing changed
			return;
		}
		
		if (selected!=null) {
			charGen.undo(selected.getModifications());
		}
		
		selected = data;
		model.setBackground(data.getKey());
		if (SplitterMondCore.getBackgrounds().contains(selected)) {
			model.setOwnBackground(null);
			logger.info("Selected stock background is now "+selected);
		} else {
			model.setOwnBackground(selected);
			logger.info("Selected own background is now "+selected);
		}
		
		charGen.apply(selected.getName(), selected.getModifications(), callback);
		
		logger.info("Newly selected: "+selected);
		
		fireChange(selected);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.Controller#getToDos()
	 */
	@Override
	public List<String> getToDos() {
		if (selected==null)
			return Arrays.asList(RES.getString("backgrgen.todo"));
		return new ArrayList<>();
	}

}
