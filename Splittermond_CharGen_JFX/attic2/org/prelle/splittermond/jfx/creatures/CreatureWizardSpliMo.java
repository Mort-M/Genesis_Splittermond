/**
 *
 */
package org.prelle.splittermond.jfx.creatures;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.Wizard;
import org.prelle.splimo.npc.NPCGenerator;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;

/**
 * @author prelle
 *
 */
public class CreatureWizardSpliMo extends Wizard {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);

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
