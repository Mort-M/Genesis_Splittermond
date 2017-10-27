/**
 * 
 */
package org.prelle.splimo.charctrl;

import java.util.List;

import org.prelle.splimo.creature.CreatureModule;
import org.prelle.splimo.creature.CreatureModuleReference;
import org.prelle.splimo.creature.CreatureReference;
import org.prelle.splimo.modifications.ModificationChoice;
import org.prelle.splimo.requirements.Requirement;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public interface CommonCreatureController {

	//-------------------------------------------------------------------
	public CreatureReference getCreature();

	//-------------------------------------------------------------------
	List<CreatureModule> getAvailableOptions();

	//-------------------------------------------------------------------
	List<CreatureModuleReference> getSelectedOptions();

	//-------------------------------------------------------------------
	boolean isRequirementMet(Requirement req);

	//-------------------------------------------------------------------
	boolean canSelect(CreatureModule option);

	//-------------------------------------------------------------------
	CreatureModuleReference selectOption(CreatureModule option);

	//-------------------------------------------------------------------
	void deselectOption(CreatureModuleReference option);

	//-------------------------------------------------------------------
	List<CreatureModuleReference.NecessaryChoice> getChoicesToMake();

	//-------------------------------------------------------------------
	ModificationChoice instantiateChoice(CreatureModuleReference.NecessaryChoice toChoose);

	//-------------------------------------------------------------------
	void makeChoice(CreatureModuleReference.NecessaryChoice choice, Modification chosenOption);

}