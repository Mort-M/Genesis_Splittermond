/**
 *
 */
package org.prelle.splittermond.jfx.creatures;

import org.apache.log4j.Logger;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.Wizard;
import org.prelle.splimo.chargen.SpliMoCharacterGenerator;
import org.prelle.splimo.chargen.LetUserChooseListener;
import org.prelle.splimo.modifications.ModificationChoice;
import org.prelle.splimo.npc.CreatureTypeController;
import org.prelle.splimo.npc.NPCGenerator;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class CreatureWizardSpliMo extends Wizard {

	private final static Logger logger = Logger.getLogger("splittermond.jfx");

	private NPCGenerator charGen;

	//-------------------------------------------------------------------
	/**
	 * @param nodes
	 */
	public CreatureWizardSpliMo(NPCGenerator charGen) {
		this.charGen = charGen;
		getPages().addAll(
				new WizardPageAttributes(this, charGen),
				new WizardPageCreatureType(this, charGen),
				new WizardPageWeapons(this, charGen)
				);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.Wizard#canBeFinished()
	 */
	@Override
	public boolean canBeFinished() {
		return false;
	}

}
