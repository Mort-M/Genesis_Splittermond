/**
 *
 */
package org.prelle.splimo.chargen;

import java.util.function.Predicate;

import org.prelle.splimo.modifications.MastershipModification;
import org.prelle.splimo.modifications.ModificationChoice;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public interface LetUserChooseListener {

	//-------------------------------------------------------------------
	public void addPrefilter(Predicate<Modification> filter);

	//-------------------------------------------------------------------
	public Modification[] letUserChoose(String choiceReason, ModificationChoice choice);

	//-------------------------------------------------------------------
	public MastershipModification letUserChoose(String choiceReason, MastershipModification vagueMod);

//	//-------------------------------------------------------------------
//	/**
//	 * @param mod
//	 * @param current  Currently existing value
//	 * @return
//	 */
//	public ResourceModAddOptions letUserChooseResource(ResourceModification mod, SelectedResource current);

}
