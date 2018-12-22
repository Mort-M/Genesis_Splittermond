/**
 * 
 */
package org.prelle.splimo.charctrl4;

import java.util.List;

import org.prelle.splimo.Power;
import org.prelle.splimo.PowerReference;

/**
 * @author prelle
 *
 */
public interface PowerController extends Controller {
	
	//-------------------------------------------------------------------
	/**
	 * Return those resources that can be added/opened
	 */
	public List<Power> getAvailablePowers();
	
	//-------------------------------------------------------------------
	public boolean canBeSelected(Power power);
	
	//-------------------------------------------------------------------
	public boolean canBeDeselected(PowerReference ref);
	
	//-------------------------------------------------------------------
	/**
	 * Add the power to the list of selected powers.
	 * When selecting an already selected power that can be used multiple
	 * times, the existing power is increased instead
	 */
	public PowerReference select(Power power);
	
	//-------------------------------------------------------------------
	/**
	 * Remove a power from the list of selected powers.
	 * Deselection is only possible for powers that cannot be selected
	 * multiple times. Those must be decreased
	 */
	public boolean deselect(PowerReference ref);
	
	//-------------------------------------------------------------------
	public boolean canBeIncreased(PowerReference ref);
	
	//-------------------------------------------------------------------
	public boolean canBeDecreased(PowerReference ref);
	
	//-------------------------------------------------------------------
	/**
	 * @return TRUE, if increase was successful
	 */
	public boolean increase(PowerReference ref);
	
	//-------------------------------------------------------------------
	/**
	 * @return TRUE, if decrease was successful
	 */
	public boolean decrease(PowerReference ref);

}
