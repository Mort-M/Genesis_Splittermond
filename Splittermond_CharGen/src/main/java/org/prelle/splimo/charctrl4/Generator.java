/**
 * 
 */
package org.prelle.splimo.charctrl4;

/**
 * @author Stefan
 *
 */
public interface Generator extends Controller {

	//--------------------------------------------------------------------
	/**
	 * Only for generation: Returns the available amount of points
	 * to distribute
	 */
	public int getPointsLeft();
	
}
