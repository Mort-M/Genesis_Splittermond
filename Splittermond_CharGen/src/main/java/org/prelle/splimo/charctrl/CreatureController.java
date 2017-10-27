/**
 *
 */
package org.prelle.splimo.charctrl;

import java.util.List;

import org.prelle.splimo.creature.CreatureModule;
import org.prelle.splimo.creature.CreatureModuleReference;
import org.prelle.splimo.creature.CreatureType;
import org.prelle.splimo.creature.CreatureTypeValue;

/**
 * @author prelle
 *
 */
public interface CreatureController extends CommonCreatureController {
	
	//-------------------------------------------------------------------
	public int getAvailableCreaturePoints();

	//-------------------------------------------------------------------
	public boolean canBeFinished();

	
	//-------------------------------------------------------------------
	public List<CreatureModule> getBases();

	//-------------------------------------------------------------------
	public void selectBase(CreatureModule base);

	
	//-------------------------------------------------------------------
	public List<CreatureModule> getRoles();

	//-------------------------------------------------------------------
	public void selectRole(CreatureModule role);


	//-------------------------------------------------------------------
	public List<CreatureTypeValue> getCreatureTypes();
	public void setCreatureTypes(List<CreatureTypeValue> types);
	public boolean hasCreatureType(CreatureType type);

	
	//-------------------------------------------------------------------
	public List<CreatureModule> getAvailableTrainings();

	//-------------------------------------------------------------------
	public List<CreatureModuleReference> getSelectedTrainings();
	
}
