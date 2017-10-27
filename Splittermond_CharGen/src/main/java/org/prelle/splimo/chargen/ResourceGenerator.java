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

import org.apache.log4j.Logger;
import org.prelle.splimo.Resource;
import org.prelle.splimo.ResourceReference;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.GeneratingResourceController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.modifications.ResourceModification;

/**
 * @author prelle
 *
 */
public class ResourceGenerator implements GeneratingResourceController {
	
	private static Logger logger = Logger.getLogger("splittermond.chargen.resource");

	private final static ResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splittermond/chargen");
	
	private static List<Resource> BASE_RESOURCES;

	private int maxPointsToSpend;
//	private int pointsFree;
	private int maxValue;
	private SpliMoCharacter model;
	private List<Resource> available;
	private Map<Resource, Integer> minValByModifications;
	private Map<ResourceReference, Integer> pointsAdded;
	private List<ResourceModification> knownModifications;

	//-------------------------------------------------------------------
	public ResourceGenerator(int toSpend, SpliMoCharacter model) {
		logger.info("Initialize resource generator with "+toSpend+" points to spend");
		maxPointsToSpend = toSpend;
//		this.pointsFree = toSpend;
		this.maxValue = 4;
		this.model    = model;
		available     = new ArrayList<Resource>();
		minValByModifications = new HashMap<Resource, Integer>();
		pointsAdded   = new HashMap<ResourceReference,Integer>();
		knownModifications = new ArrayList<ResourceModification>();

		BASE_RESOURCES = new ArrayList<Resource>(Arrays.asList(new Resource[]{
				SplitterMondCore.getResource("reputation"),
				SplitterMondCore.getResource("status"),
				SplitterMondCore.getResource("contacts"),
				SplitterMondCore.getResource("wealth")
		}));
		/*
		 * By default some resources are selected
		 */
		for (Resource res : BASE_RESOURCES) {
			ResourceReference ref = new ResourceReference(res, 0);
			model.addResource(ref);
			pointsAdded.put(ref, 0);
		}
		
//		updateAvailableResources();
		for (Resource res : SplitterMondCore.getResources())
			if (!BASE_RESOURCES.contains(res))
				available.add(res);
	}

	//-------------------------------------------------------------------
	/**
	 * Return the list of resources that can be added
	 */
	@Override
	public List<Resource> getAvailableResources() {
		return available;
	}

	//-------------------------------------------------------------------
	void addModification(ResourceModification mod) {
		logger.debug("addMod("+mod+")");
		/*
		 * Ignore already known modifications
		 */
		if (knownModifications.contains(mod)) {
			logger.debug("Modification "+mod+" already known in "+knownModifications);
			return;
		}
		
		/*
		 * Update the minimal points to be spent in this resource
		 */
		Resource res = mod.getResource();
		Integer old = minValByModifications.get(res);
		if (old==null) {
			old = 0;
		}
		minValByModifications.put(res, old+mod.getValue());
		logger.debug("Minimal value for "+res+" now "+minValByModifications.get(res));
		knownModifications.add(mod);
		
		/*
		 * Find the first ResourceReference and add its value 
		 * by the modification
		 */
		ResourceReference ref = getFirstValueFor(mod.getResource());
		if (ref!=null) {
			ref.setValue( ref.getValue() + mod.getValue() );
			logger.debug("Set value of "+ref.getResource()+" to "+ref.getValue());
			GenerationEventDispatcher.fireEvent(
					new GenerationEvent(GenerationEventType.RESOURCES_CHANGED, ref));
			return;
		}

		// Not found
		ref = new ResourceReference(mod.getResource(), mod.getValue());
		model.addResource(ref);
		
		GenerationEventDispatcher.fireEvent(
				new GenerationEvent(GenerationEventType.RESOURCE_ADDED, ref));		
		GenerationEventDispatcher.fireEvent(
				new GenerationEvent(GenerationEventType.POINTS_LEFT_RESOURCES, null, getPointsLeft()));
	}

	//-------------------------------------------------------------------
	void removeModification(ResourceModification mod) {
		logger.debug("removeMod("+mod+")");
		/*
		 * Don't undo unknown modifications
		 */
		if (!knownModifications.contains(mod)) {
			logger.warn("Cannot remove unknown modification");
			return;
		}
		
		/*
		 * Update the minimal points to be spent in this resource
		 */
		Resource res = mod.getResource();
		Integer old = minValByModifications.get(res);
		if (old==null) {
			old = 0;
		}
		minValByModifications.put(res, old-mod.getValue());
		logger.debug("Minimal value for "+res+" now "+minValByModifications.get(res));
		knownModifications.remove(mod);
		
		/*
		 * Find the first ResourceReference and subtract its value 
		 * by the modification
		 */
		ResourceReference ref = getFirstValueFor(mod.getResource());
		if (ref!=null) {
			ref.setValue( ref.getValue() - mod.getValue() );
			logger.debug("Set value of "+ref.getResource()+" to "+ref.getValue());
			// Remove non base resources when value is 0
			if (ref.getValue()==0 && !BASE_RESOURCES.contains(ref.getResource())) {
				model.removeResource(ref);
				GenerationEventDispatcher.fireEvent(
						new GenerationEvent(GenerationEventType.RESOURCE_REMOVED, ref));
				return;
			}
			GenerationEventDispatcher.fireEvent(
					new GenerationEvent(GenerationEventType.RESOURCE_CHANGED, ref));		
			GenerationEventDispatcher.fireEvent(
					new GenerationEvent(GenerationEventType.POINTS_LEFT_RESOURCES, null, getPointsLeft()));
			return;
		}
		
		// Not found
		logger.warn("Removing known modification but did not find reference for it in model");
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.Generator#getPointsLeft()
	 */
	@Override
	public int getPointsLeft() {
		int left = maxPointsToSpend;
		for (ResourceReference ref : model.getResources()) {
			left -= ref.getValue();
		}

		return left;
//		return pointsFree;
	}

	//-------------------------------------------------------------------
	public ResourceReference getFirstValueFor(Resource res) {
		for (ResourceReference ref : model.getResources())
			if (ref.getResource()==res) {
				return ref;
			}
		
		return null;
	}


	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.ResourceController#canBeIncreased(org.prelle.splimo.ResourceReference)
	 */
	@Override
	public boolean canBeIncreased(ResourceReference ref) {
		logger.debug("canBeIncreased("+ref+")  "+getPointsLeft());
		// Prevent increasing above the maximum
		if (ref.getValue()>=maxValue)
			return false;
		
		// Only allow when there are points left
		return getPointsLeft()>0;
	}

	//-------------------------------------------------------------------
	@Override
	public boolean canBeDecreased(ResourceReference ref) {
		logger.debug("canBeDecreased("+ref+")");
		/*
		 * Find the current points spent in all references of the
		 * given type
		 */
		int currentSpent = 0;
		for (ResourceReference tmp : model.getResources())
			if (tmp.getResource()==ref.getResource())
				currentSpent += tmp.getValue();
		
		// Compare with expected minimum
		boolean gamemasterException = maxValue==6;
		int expectedMin = 0;
		if (BASE_RESOURCES.contains(ref.getResource()) && gamemasterException)
			expectedMin = -2;
//		if (minValByModifications.containsKey(ref.getResource()))
//			expectedMin = minValByModifications.get(ref.getResource());
		
		// Prevent decreasing below the minimum
		if (currentSpent<=expectedMin && !(BASE_RESOURCES.contains(ref.getResource()) && gamemasterException)) {
			logger.debug("Cannot decrease "+ref+" ... current="+currentSpent+"  expectedMin="+expectedMin);
			return false;
		}
		
		return true;
	}

	//-------------------------------------------------------------------
	@Override
	public boolean increase(ResourceReference ref) {
		logger.debug("increase "+ref);
		if (!canBeIncreased(ref))
			return false;

//		pointsFree--;
		
		ref.setValue( ref.getValue()+1 );
		logger.info("increased resource "+ref.getResource()+" to "+ref.getValue());
		
		GenerationEventDispatcher.fireEvent(
				new GenerationEvent(GenerationEventType.RESOURCES_CHANGED, ref));
		GenerationEventDispatcher.fireEvent(
				new GenerationEvent(GenerationEventType.POINTS_LEFT_RESOURCES, null, getPointsLeft()));
		return true;
	}

	//-------------------------------------------------------------------
	@Override
	public boolean decrease(ResourceReference ref) {
		logger.debug("decrease "+ref);
		if (!canBeDecreased(ref))
			return false;
		
		ref.setValue( ref.getValue()-1 );
//		pointsFree++;
		logger.info("Resource decreased to "+ref);
		
		if (ref.getValue()==0 && !BASE_RESOURCES.contains(ref.getResource())) {
			logger.debug("Remove oblivious non-base resource");
			model.removeResource(ref);			
			GenerationEventDispatcher.fireEvent(
					new GenerationEvent(GenerationEventType.RESOURCE_REMOVED, ref));
		} else		
			GenerationEventDispatcher.fireEvent(
				new GenerationEvent(GenerationEventType.RESOURCES_CHANGED, ref));
		
		
		GenerationEventDispatcher.fireEvent(
				new GenerationEvent(GenerationEventType.POINTS_LEFT_RESOURCES, null, getPointsLeft()));
		return true;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.GeneratingResourceController#setAllowMaxResources(boolean)
	 */
	@Override
	public void setAllowMaxResources(boolean allowMax) {
		this.maxValue = (allowMax)?6:4;
		logger.debug("Set resource maximum to "+maxValue);
//		updateAvailableResources();
	}

	//--------------------------------------------------------------------
	@Override
	public ResourceReference openResource(Resource res) {
		if (getPointsLeft()<=0)
			return null;

		// Cannot have base resources multiple times
		if (BASE_RESOURCES.contains(res))
			return getFirstValueFor(res);
		
		ResourceReference ref = new ResourceReference(res, 1);
		model.addResource(ref);
//		pointsFree--;
		
		GenerationEventDispatcher.fireEvent(
				new GenerationEvent(GenerationEventType.RESOURCE_ADDED, ref));
		GenerationEventDispatcher.fireEvent(
				new GenerationEvent(GenerationEventType.POINTS_LEFT_RESOURCES, null, getPointsLeft()));
		
		return ref;
	}

	//--------------------------------------------------------------------
	@Override
	public boolean canBeDeselected(ResourceReference key) {
		return key.getValue()>0 && !BASE_RESOURCES.contains(key.getResource());
	}

	//-------------------------------------------------------------------
	@Override
	public boolean deselect(ResourceReference ref) {
		logger.debug("deselect "+ref);
		if (!canBeDeselected(ref))
			return false;
		
		ref.setValue( ref.getValue()-1 );
//		pointsFree++;
		logger.info("Resource decreased to "+ref);
		
		if (ref.getValue()==0 && !BASE_RESOURCES.contains(ref.getResource())) {
			logger.debug("Remove oblivious non-base resource");
			model.removeResource(ref);			
			GenerationEventDispatcher.fireEvent(
					new GenerationEvent(GenerationEventType.RESOURCE_REMOVED, ref));
		} else		
			GenerationEventDispatcher.fireEvent(
				new GenerationEvent(GenerationEventType.RESOURCES_CHANGED, ref));
		
		
		GenerationEventDispatcher.fireEvent(
				new GenerationEvent(GenerationEventType.POINTS_LEFT_RESOURCES, null, getPointsLeft()));
		return true;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.ResourceController#canBeSplit(org.prelle.splimo.ResourceReference)
	 */
	@Override
	public boolean canBeSplit(ResourceReference ref) {
		if (BASE_RESOURCES.contains(ref))
			return false;
		
		return ref.getValue()>1;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.ResourceController#split(org.prelle.splimo.ResourceReference)
	 */
	@Override
	public ResourceReference split(ResourceReference ref) {
		if (!canBeSplit(ref))
			return null;
		
		// Reduce current resource by one
		ref.setValue(ref.getValue()-1);
		// Add new resource with value 1
		ResourceReference newRef = new ResourceReference(ref.getResource(), 1);
		model.addResource(newRef);
		
		GenerationEventDispatcher.fireEvent(
				new GenerationEvent(GenerationEventType.RESOURCE_CHANGED, ref));		
		GenerationEventDispatcher.fireEvent(
				new GenerationEvent(GenerationEventType.RESOURCE_ADDED, newRef));		
		return newRef;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.ResourceController#canBeJoined(org.prelle.splimo.ResourceReference[])
	 */
	@Override
	public boolean canBeJoined(ResourceReference... resources) {
		// Must be more than one resource
		if (resources.length<2)
			return false;
		
		// Must be all identical resource types
		Resource res = resources[0].getResource();
		for (int i=1; i<resources.length; i++)
			if (resources[i].getResource()!=res)
				return false;

		// Sum of all values may not be exceed maximum
		int sum = resources[0].getValue();
		for (int i=1; i<resources.length; i++) 
			sum += resources[i].getValue();
				
		return sum<=maxValue;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.ResourceController#join(org.prelle.splimo.ResourceReference[])
	 */
	@Override
	public void join(ResourceReference... resources) {
		logger.debug("join "+Arrays.toString(resources)+"  can="+canBeJoined(resources));
		if (!canBeJoined(resources))
			return;
		// Join all on first resource
		ResourceReference keep = resources[0];
		for (int i=1; i<resources.length; i++) {
			// Add value of resource
			keep.setValue(keep.getValue() + resources[i].getValue());
			// Remove joined resource
			model.removeResource(resources[i]);
			GenerationEventDispatcher.fireEvent(
					new GenerationEvent(GenerationEventType.RESOURCE_REMOVED, resources[i]));		
		}
		
		GenerationEventDispatcher.fireEvent(
				new GenerationEvent(GenerationEventType.RESOURCE_CHANGED, resources[0]));		
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.ResourceController#canBeTrashed(org.prelle.splimo.ResourceReference)
	 */
	@Override
	public boolean canBeTrashed(ResourceReference ref) {
		// Destroying resources (and losing points) is not possible during generation
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.ResourceController#trash(org.prelle.splimo.ResourceReference)
	 */
	@Override
	public boolean trash(ResourceReference ref) {
		// Destroying resources (and losing points) is not possible during generation
		return false;
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
		if (getPointsLeft()>0)
			todo.add(String.format(RES.getString("resourcegen.todo"), getPointsLeft()));

		for (ResourceReference ref : model.getResources()) {
			if (ref.getValue()==0)
				continue;
			if (ref.getIdReference()!=null)
				continue;
			if (ref.getDescription()!=null)
				continue;
			
			if (ref.getResource().getId().equals("creature") || ref.getResource().getId().equals("relic"))
				todo.add(String.format(RES.getString("resourcegen.todo.select"), ref.getResource().getName()+" "+ref.getValue()));
		}
		return todo;
	}

}
