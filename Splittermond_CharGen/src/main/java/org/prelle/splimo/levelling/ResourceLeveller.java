/**
 * 
 */
package org.prelle.splimo.levelling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Resource;
import org.prelle.splimo.ResourceReference;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.ResourceController;
import org.prelle.splimo.charctrl4.SpliMoCharGenConstants;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.modifications.ModificationValueType;
import org.prelle.splimo.modifications.ResourceModification;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class ResourceLeveller implements ResourceController {
	
	private static Logger logger = LogManager.getLogger("splittermond.level.resrc");

	private final static ResourceBundle RES = SpliMoCharGenConstants.RES;

	private List<Modification> undoList;
	private Map<ResourceReference, Stack<ResourceModification>> resourceUndoStack;

	private SpliMoCharacter model;
	
	//--------------------------------------------------------------------
	/**
	 * @param undoList Undo list for all controllers
	 */
	public ResourceLeveller(SpliMoCharacter model, List<Modification> undoList) {
		this.model = model;
		this.undoList = undoList;

		resourceUndoStack = new HashMap<ResourceReference, Stack<ResourceModification>>();
		for (ResourceReference key : model.getResources())
			resourceUndoStack.put(key, new Stack<ResourceModification>());
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.ResourceController#getAvailableResources()
	 */
	@Override
	public List<Resource> getAvailableResources() {
		List<Resource> ret = new ArrayList<Resource>();
		if (model.getExperienceFree()<7)
			return ret;
		
		for (Resource tmp : SplitterMondCore.getResources()) {
			if (tmp.isBaseResource()) continue;
			ret.add(tmp);
		}
			
		return ret;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.ResourceController#openResource(org.prelle.splimo.Resource)
	 */
	@Override
	public ResourceReference openResource(Resource res) {
		if (!getAvailableResources().contains(res))
			return null;

		int expNeeded = 7;
		
		if (model.getExperienceFree()<expNeeded)
			return null;
				
		ResourceReference ref = new ResourceReference(res, 1);
		// Update model
		logger.info("Add resource "+res);
		model.addResource(ref);
		model.setExperienceFree(model.getExperienceFree()-expNeeded);
		model.setExperienceInvested(model.getExperienceInvested()+expNeeded);
		// Add to undo list
		ResourceModification mod = new ResourceModification(ref.getResource(), 1);
		mod.setExpCost(expNeeded);
		undoList.add(mod);
		resourceUndoStack.put(ref, new Stack<ResourceModification>());
		resourceUndoStack.get(ref).push(mod);
		// Inform listener
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.RESOURCES_CHANGED, ref));
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.EXPERIENCE_CHANGED, null, new int[]{
				model.getExperienceFree(),
				model.getExperienceInvested()
		}));
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.UNDO_LIST_CHANGED, undoList));
		
		return ref;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.ResourceController#canBeIncreased(org.prelle.splimo.ResourceReference)
	 */
	@Override
	public boolean canBeIncreased(ResourceReference ref) {
		int newVal = ref.getValue()+1;
		// Ensure that it cannot be increased above maximum
		if (newVal>6)
			return false;
		
		// Determine how much exp is needed
		int expNeeded = 7;
		return expNeeded<=model.getExperienceFree();
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.ResourceController#canBeDecreased(org.prelle.splimo.ResourceReference)
	 */
	@Override
	public boolean canBeDecreased(ResourceReference ref) {
		if (!resourceUndoStack.containsKey(ref)) {
			logger.warn("Testing a resource ref. "+ref+" with no entry in undo stack");
			return false;
		}
		return !resourceUndoStack.get(ref).isEmpty();
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.ResourceController#canBeDeselected(org.prelle.splimo.ResourceReference)
	 */
	@Override
	public boolean canBeDeselected(ResourceReference ref) {
		if (ref==null) return false;
		if (ref.getResource()==null) {
			logger.warn("No resource in resource reference "+ref);
		} else if (ref.getResource().isBaseResource()) {
			return false;
		}
		return ref.getValue()==1 && canBeDecreased(ref);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.ResourceController#deselect(org.prelle.splimo.ResourceReference)
	 */
	@Override
	public boolean deselect(ResourceReference ref) {
		int newVal = ref.getValue()-1;

		if (canBeDeselected(ref)) {
			// Update model
			ResourceModification mod = resourceUndoStack.get(ref).pop();
			logger.info("Deselect "+ref+" from "+ref.getValue()+" to "+newVal);
			ref.setValue(newVal);
			if (ref.getValue()==0)
				model.removeResource(ref);
			model.setExperienceFree(model.getExperienceFree()+mod.getExpCost());
			model.setExperienceInvested(model.getExperienceInvested()-mod.getExpCost());
			undoList.remove(mod);

			// Inform listener
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.RESOURCES_CHANGED, ref));
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
	 * @see org.prelle.splimo.charctrl.ResourceController#increase(org.prelle.splimo.ResourceReference)
	 */
	@Override
	public boolean increase(ResourceReference ref) {
		int newVal = ref.getValue()+1;
		
		// Determine how much exp is needed
		int expNeeded = 7;

		if (canBeIncreased(ref)) {
			// Update model
			logger.info("Increase "+ref+" from "+ref.getValue()+" to "+newVal);
			ref.setValue(newVal);
			model.setExperienceFree(model.getExperienceFree()-expNeeded);
			model.setExperienceInvested(model.getExperienceInvested()+expNeeded);
			// Add to undo list
			ResourceModification mod = new ResourceModification(ModificationValueType.RELATIVE, ref.getResource(), 1);
			mod.setExpCost(expNeeded);
			undoList.add(mod);
			resourceUndoStack.get(ref).push(mod);
			// Inform listener
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.RESOURCES_CHANGED, ref));
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
	 * @see org.prelle.splimo.charctrl.ResourceController#decrease(org.prelle.splimo.ResourceReference)
	 */
	@Override
	public boolean decrease(ResourceReference ref) {
		int newVal = ref.getValue()-1;

		if (canBeDecreased(ref)) {
			// Update model
			ResourceModification mod = resourceUndoStack.get(ref).pop();
			logger.info("Decrease "+ref+" from "+ref.getValue()+" to "+newVal);
			ref.setValue(newVal);
			if (ref.getValue()==0)
				model.removeResource(ref);
			model.setExperienceFree(model.getExperienceFree()+mod.getExpCost());
			model.setExperienceInvested(model.getExperienceInvested()-mod.getExpCost());
			undoList.remove(mod);

			// Inform listener
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.RESOURCE_CHANGED, ref));
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
	 * @see org.prelle.splimo.charctrl.ResourceController#canBeSplit(org.prelle.splimo.ResourceReference)
	 */
	@Override
	public boolean canBeSplit(ResourceReference ref) {
		return false;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.ResourceController#split(org.prelle.splimo.ResourceReference)
	 */
	@Override
	public ResourceReference split(ResourceReference ref) {
		return null;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.ResourceController#canBeJoined(org.prelle.splimo.ResourceReference[])
	 */
	@Override
	public boolean canBeJoined(ResourceReference... resources) {
		return false;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.ResourceController#join(org.prelle.splimo.ResourceReference[])
	 */
	@Override
	public void join(ResourceReference... resources) {
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.ResourceController#canBeTrashed(org.prelle.splimo.ResourceReference)
	 */
	@Override
	public boolean canBeTrashed(ResourceReference ref) {
		// You cannot trash base resources or those that need to be decreased
		// to a level before EXP have recently been invested
		return !ref.getResource().isBaseResource() && !canBeDecreased(ref);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.ResourceController#trash(org.prelle.splimo.ResourceReference)
	 */
	@Override
	public boolean trash(ResourceReference ref) {
		logger.debug("trash "+ref);
		logger.debug("trash "+canBeTrashed(ref));
		if (!canBeTrashed(ref))
			return false;

		resourceUndoStack.remove(ref);
		// Make a negative resource mod and add it to history
		ResourceModification mod = new ResourceModification(ref.getResource(), -ref.getValue());
		mod.setExpCost(0);
		mod.setComment("Weggeworfen/Deleted");
		model.addToHistory(mod);
		// Remove resource
		model.removeResource(ref);
		
		// Inform listener
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.RESOURCE_REMOVED, ref));
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.UNDO_LIST_CHANGED, undoList));

		return true;
	}
	
	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.ResourceController#findResourceReference(org.prelle.splimo.Resource, java.lang.String, java.lang.String)
	 */
	@Override
	public ResourceReference findResourceReference(Resource res, String descr, String idref) {
    	// Search matching reference
    	for (ResourceReference ref : model.getResources()) {
    		if (ref.getResource()!=res)
    			continue;
    		if (idref==null && ref.getIdReference()==null && descr==null && ref.getDescription()==null)
    			return ref;
    		if (idref!=null && ref.getIdReference()!=null && idref.equals(ref.getIdReference().toString())) 
    			return ref;
    		if (descr!=null && ref.getDescription()!=null && descr.equals(ref.getDescription())) 
    			return ref;
    	}
		
    	return null;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.Controller#getToDos()
	 */
	@Override
	public List<String> getToDos() {
		List<String> todo = new ArrayList<>();
		for (ResourceReference ref : model.getResources()) {
			if (ref.getValue()==0)
				continue;
			if (ref.getIdReference()!=null)
				continue;
			if (ref.getDescription()!=null)
				continue;
			todo.add(String.format(RES.getString("resourcegen.todo.select"), ref.getResource().getName()+" "+ref.getValue()));
		}
		return todo;
	}

}
