/**
 * 
 */
package org.prelle.splimo.charctrl4;

import java.util.List;

import org.prelle.splimo.Resource;
import org.prelle.splimo.ResourceReference;

/**
 * @author prelle
 *
 */
public interface ResourceController extends Controller {
	
	//-------------------------------------------------------------------
	/**
	 * Return those resources that can be added/opened
	 */
	public List<Resource> getAvailableResources();
	
	//-------------------------------------------------------------------
	/**
	 * Add the resource to the list of existing resources.
	 * Some resources may be added multiple times.
	 */
	public ResourceReference openResource(Resource res);
	
	//-------------------------------------------------------------------
	public boolean canBeIncreased(ResourceReference ref);
	
	//-------------------------------------------------------------------
	public boolean canBeDecreased(ResourceReference ref);
	
	//-------------------------------------------------------------------
	/**
	 * One can only deselect those resources which are not
	 * core resources
	 */
	public boolean canBeDeselected(ResourceReference ref);
	
	//-------------------------------------------------------------------
	public boolean deselect(ResourceReference ref);
	
	//-------------------------------------------------------------------
	/**
	 * One can only deselect those resources which are not
	 * core resources
	 */
	public boolean canBeTrashed(ResourceReference ref);
	
	//-------------------------------------------------------------------
	public boolean trash(ResourceReference ref);
	
	//-------------------------------------------------------------------
	/**
	 * @return TRUE, if increase was successful
	 */
	public boolean increase(ResourceReference ref);
	
	//-------------------------------------------------------------------
	/**
	 * @return TRUE, if decrease was successful
	 */
	public boolean decrease(ResourceReference ref);

	//-------------------------------------------------------------------
	public boolean canBeSplit(ResourceReference ref);

	//-------------------------------------------------------------------
	public ResourceReference split(ResourceReference ref);

	//--------------------------------------------------------------------
	public boolean canBeJoined(ResourceReference... resources);	

	//--------------------------------------------------------------------
	public void join(ResourceReference... resources);

	
	//-------------------------------------------------------------------
	public ResourceReference findResourceReference(Resource res, String descr, String idref);

}
