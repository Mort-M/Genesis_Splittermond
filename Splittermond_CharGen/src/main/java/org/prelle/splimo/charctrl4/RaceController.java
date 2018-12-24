/**
 * 
 */
package org.prelle.splimo.charctrl4;

import java.util.List;

import org.prelle.splimo.Race;

/**
 * @author prelle
 *
 */
public interface RaceController extends Controller {

	//-------------------------------------------------------------------
	public List<Race> getAvailableRaces();

	//-------------------------------------------------------------------
	public void selectRace(Race value);
	
	//-------------------------------------------------------------------
	/**
	 * Roll a random hair color based on the selected race
	 */
	public String rollHair();
	
	//-------------------------------------------------------------------
	/**
	 * Roll a random eye color based on the selected race
	 */
	public String rollEyes();

	//-------------------------------------------------------------------
	public int[] rollSizeAndWeight();

}
