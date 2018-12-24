/**
 * 
 */
package org.prelle.splimo.charctrl4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.prelle.splimo.Resource;
import org.prelle.splimo.ResourceReference;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.modifications.ResourceModification;
import org.prelle.splimo.processor.SpliMoCharacterProcessor;

import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.modification.DecisionToMake;
import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class NewResourceGenerator implements ResourceController, Generator, SpliMoCharacterProcessor {
	
	private static Logger logger = Logger.getLogger("splittermond.chargen.resrc");

	private final static ResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splittermond/chargen");
	
	 static List<Resource> BASE_RESOURCES;

	private SplitterEngineCharacterGenerator parent; 
	private int maxPointsToSpend;
//	private int pointsFree;
	private int maxValue;
	private SpliMoCharacter model;
	private List<Resource> available;
	private List<ToDoElement> todos;
	private List<DecisionToMake> decisions;

	private int pointsLeft;

	//-------------------------------------------------------------------
	public NewResourceGenerator(SplitterEngineCharacterGenerator charGen, int toSpend, boolean initBaseResources) {
		logger.info("Initialize resource generator with "+toSpend+" points to spend");
		maxPointsToSpend = toSpend;
//		this.pointsFree = toSpend;
		this.parent   = charGen;
		this.maxValue = 4;
		this.model    = charGen.getModel();
		available     = new ArrayList<Resource>();
		todos = new ArrayList<>();
		decisions = new ArrayList<>();

		BASE_RESOURCES = new ArrayList<Resource>(Arrays.asList(new Resource[]{
				SplitterMondCore.getResource("reputation"),
				SplitterMondCore.getResource("status"),
				SplitterMondCore.getResource("contacts"),
				SplitterMondCore.getResource("wealth")
		}));
		/*
		 * By default some resources are selected
		 */
		if (initBaseResources) {
			for (Resource res : BASE_RESOURCES) {
				ResourceReference ref = new ResourceReference(res, 0);
				model.addResource(ref);
			}
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

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.Generator#getPointsLeft()
	 */
	@Override
	public int getPointsLeft() {
		return pointsLeft;
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
		parent.runProcessors();
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
		parent.runProcessors();
		return true;
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
		
		logger.info(" User added resource "+res.getId());
		
		GenerationEventDispatcher.fireEvent(
				new GenerationEvent(GenerationEventType.RESOURCE_ADDED, ref));
		GenerationEventDispatcher.fireEvent(
				new GenerationEvent(GenerationEventType.POINTS_LEFT_RESOURCES, null, getPointsLeft()));
		
		parent.runProcessors();
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
		if (BASE_RESOURCES.contains(ref.getResource()))
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

			/*
			 * Process incoming modifications
			 */
			for (Modification mod : previous) {
				if (mod instanceof ResourceModification) {
					ResourceModification rMod = (ResourceModification)mod;
					// If model already has that resource, increase it - otherwise ad
					boolean notFound = true;
					for (ResourceReference ref : model.getResources()) {
						if (ref.getResource()==rMod.getResource()) {
							logger.debug(" * increase resource '"+rMod.getResource().getId()+" +"+rMod.getValue()+"' from "+rMod.getSource());
							ref.addModification(rMod);
							notFound = false;
							break;
						}
					}
					if (notFound) {
						logger.debug(" * Add resource '"+rMod.getResource().getId()+" "+rMod.getValue()+"' from "+rMod.getSource());
						ResourceReference toAdd = new ResourceReference(rMod.getResource(), 0);
						model.addResource(toAdd);
						toAdd.addModification(rMod);
					}
				} else {
					unprocessed.add(mod);
				}
			}
			
			/*
			 * Check points spent in resources
			 */
			pointsLeft = maxPointsToSpend;
			for (ResourceReference ref : model.getResources()) {
				logger.debug(" Invest "+ref.getModifiedValue()+" points for "+ref);
				pointsLeft -= ref.getModifiedValue();
			}
			logger.debug("  Invested "+(maxPointsToSpend-pointsLeft)+" of "+maxPointsToSpend+" points for resources");
			
			/*
			 * Check all points are spent
			 */
			if (pointsLeft>0)
				todos.add(new ToDoElement(Severity.STOPPER, String.format(RES.getString("resourcegen.todo"), pointsLeft)));
			/*
			 * Check if all resources are named
			 */
			for (ResourceReference ref : model.getResources()) {
				if (ref.getValue()==0)
					continue;
				if (ref.getIdReference()!=null)
					continue;
				if (ref.getDescription()!=null)
					continue;
				
				if (ref.getResource().getId().equals("creature") || ref.getResource().getId().equals("relic")) {
					todos.add(new ToDoElement(Severity.WARNING, String.format(RES.getString("resourcegen.todo.select"), ref.getResource().getName()+" "+ref.getValue())));
				}
				if (ref.getValue()>maxValue) {
					todos.add(new ToDoElement(Severity.WARNING, String.format(RES.getString("resourcegen.todo.withgm"), ref.getResource().getName()+" "+ref.getValue())));
				}
			}
		} finally {
			logger.trace("STOP : process");
		}
		return unprocessed;
	}

}
