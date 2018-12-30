/**
 * 
 */
package org.prelle.splimo.charctrl4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.prelle.splimo.Resource;
import org.prelle.splimo.ResourceReference;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.CharGenConstants;
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
	
	protected static Logger logger = Logger.getLogger("splittermond.chargen.resrc");

	private final static ResourceBundle RES = CharGenConstants.RES;
	
	 static List<Resource> BASE_RESOURCES;

	protected SplitterEngineCharacterGenerator parent; 
	private int maxPointsToSpend;
//	private int pointsFree;
	private int minValue;
	private int maxValue;
	protected SpliMoCharacter model;
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
		this.minValue = 0;
		this.model    = charGen.getModel();
		available     = new ArrayList<Resource>();
		todos = new ArrayList<>();
		decisions = new ArrayList<>();
		pointsLeft    = toSpend;

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
	public void setAllowExtremeResourcesOnGeneration(boolean allow) {
		maxValue = allow?6:4;
		minValue = allow?-2:0;
	}

	//-------------------------------------------------------------------
	protected int getMaxValue() {
		return maxValue;
	}

	//-------------------------------------------------------------------
	protected int getMinValue() {
		return minValue;
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
		if (ref.getModifiedValue()>=getMaxValue())
			return false;
		
		// Only allow when there are points left
		return getPointsLeft()>0 || model.getExperienceFree()>=7;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl4.ResourceController#canBeDecreased(org.prelle.splimo.ResourceReference)
	 */
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
				currentSpent += tmp.getModifiedValue();
		
		// Compare with expected minimum
		int expectedMin = 0;
		if (BASE_RESOURCES.contains(ref.getResource()))
				expectedMin = getMinValue();
//		if (minValByModifications.containsKey(ref.getResource()))
//			expectedMin = minValByModifications.get(ref.getResource());
		
		// Prevent decreasing below the minimum
		if (currentSpent<=expectedMin ) {
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
		
		parent.runProcessors();
		return true;
	}

	//-------------------------------------------------------------------
	@Override
	public boolean decrease(ResourceReference ref) {
		logger.debug("decrease "+ref);
		if (!canBeDecreased(ref)) {
			logger.warn("Trying to decrease resource "+ref+" which cannot be decreased");
			return false;
		}
		
		ref.setValue( ref.getValue()-1 );
//		pointsFree++;
		logger.info("Resource decreased to "+ref);
		
		if (ref.getValue()==0 && !BASE_RESOURCES.contains(ref.getResource())) {
			logger.debug("Remove oblivious non-base resource");
			model.removeResource(ref);			
		}
		
		parent.runProcessors();
		return true;
	}

	//--------------------------------------------------------------------
	@Override
	public ResourceReference openResource(Resource res) {
		if (getPointsLeft()<=0 && model.getExperienceFree()<7)
			return null;

		// Cannot have base resources multiple times
		if (BASE_RESOURCES.contains(res)) {
			ResourceReference ref = getFirstValueFor(res);
			if (ref.getValue()==0 && canBeIncreased(ref))
				ref.setValue(1);
			parent.runProcessors();
			return ref;
		}
		
		ResourceReference ref = new ResourceReference(res, 1);
		model.addResource(ref);
		
		logger.info(" User added resource "+res.getId());
		
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
		logger.info("Resource decreased to "+ref);
		
		if (ref.getValue()==0 && !BASE_RESOURCES.contains(ref.getResource())) {
			logger.debug("Remove oblivious non-base resource");
			model.removeResource(ref);			
		}
		
		parent.runProcessors();
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
		
		return ref.getModifiedValue()>1;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.ResourceController#split(org.prelle.splimo.ResourceReference)
	 */
	@Override
	public ResourceReference split(ResourceReference ref) {
		if (!canBeSplit(ref)) {
			logger.warn("Trying to split an unsplittable resource: "+ref);
			return null;
		}
		
		// Reduce current resource by one
		ref.setValue(ref.getValue()-1);
		
		// Add new resource with value 1
		ResourceReference newRef = new ResourceReference(ref.getResource(), 1);
		model.addResource(newRef);
		
		parent.runProcessors();
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
		int sum = resources[0].getModifiedValue();
		for (int i=1; i<resources.length; i++) 
			sum += resources[i].getModifiedValue();
				
		return sum<=getMaxValue();
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
		}
		
		parent.runProcessors();
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
			 * Clear all powers that are not user selected
			 */
			for (ResourceReference ref : new ArrayList<>(model.getResources())) {
				if (ref.isSystemAssigned() && ref.getValue()==0) {
					logger.trace("  clear system assigned resource "+ref);
					model.removeResource(ref);
				}
				ref.clearModifications();
			}

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
						toAdd.setSystemAssigned(true);
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
				if (ref.getModifiedValue()<0) {
					if (BASE_RESOURCES.contains(ref.getResource()) && ref.getModifiedValue()>-2) {
						logger.debug(" Gain "+Math.abs(ref.getModifiedValue())+" points for "+ref);
						pointsLeft -= ref.getModifiedValue();
					} else {
						logger.debug(" Remove negative resource "+ref);
						model.removeResource(ref);
					}
				} else {
					logger.debug(" Invest "+ref.getModifiedValue()+" points for "+ref);
					pointsLeft -= ref.getModifiedValue();
				}
			}
			int expCost = 0;
			if (pointsLeft<0) {
				expCost = 7*Math.abs(pointsLeft);
				pointsLeft = 0;
				model.setExperienceFree( model.getExperienceFree() - expCost );
				model.setExperienceInvested( model.getExperienceInvested() + expCost);
				todos.add(new ToDoElement(Severity.INFO, String.format(RES.getString("resourcegen.todo.experience"), expCost)));
			}
			
			logger.debug("  Invested "+(maxPointsToSpend-pointsLeft)+" points and "+expCost+" EP for resources");
			
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
				if (ref.getValue()>getMaxValue()) {
					todos.add(new ToDoElement(Severity.WARNING, String.format(RES.getString("resourcegen.todo.withgm"), ref.getResource().getName()+" "+ref.getValue())));
				}
			}
		} finally {
			logger.trace("STOP : process");
		}
		return unprocessed;
	}

}
