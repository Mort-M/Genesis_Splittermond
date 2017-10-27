/**
 * 
 */
package org.prelle.splimo.charctrl;

import java.util.List;

import org.prelle.splimo.CultureLore;
import org.prelle.splimo.CultureLoreReference;

/**
 * @author prelle
 *
 */
public interface CultureLoreController extends Controller {
	
	//-------------------------------------------------------------------
	/**
	 * Shall getAvailableCultureLores() also return culturelores where the
	 * requirements are not fulfilled
	 */
	public void showCultureLoresWithUnmetRequirements(boolean show);
	
	//-------------------------------------------------------------------
	/**
	 * Return those resources that can be added/opened
	 */
	public List<CultureLore> getAvailableCultureLores();
	
	//-------------------------------------------------------------------
	/**
	 * Add the resource to the list of existing resources.
	 * Some resources may be added multiple times.
	 */
	public CultureLoreReference select(CultureLore res);
	
	//-------------------------------------------------------------------
	public boolean canBeDeselected(CultureLoreReference ref);
	
	//-------------------------------------------------------------------
	public boolean canBeSelected(CultureLore lore);

	//-------------------------------------------------------------------
	public boolean deselect(CultureLoreReference ref);
	
}
