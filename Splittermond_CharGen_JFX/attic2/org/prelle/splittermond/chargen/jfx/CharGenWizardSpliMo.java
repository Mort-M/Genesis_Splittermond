/**
 *
 */
package org.prelle.splittermond.chargen.jfx;

import org.prelle.javafx.Wizard;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.chargen.SpliMoCharacterGenerator;

/**
 * @author prelle
 *
 */
public class CharGenWizardSpliMo extends Wizard {

	private SpliMoCharacterGenerator charGen;
	private SpliMoCharacter generated;

	//-------------------------------------------------------------------
	/**
	 * @param nodes
	 */
	public CharGenWizardSpliMo(SpliMoCharacter model, SpliMoCharacterGenerator charGen) {
		this.charGen = charGen;
		LetUserChooseAdapter adapter = new LetUserChooseAdapter(this);
		getPages().addAll(
				new WizardPageRace(this, charGen, adapter),
				new WizardPageCulture(this, charGen, adapter),
				new WizardPageBackground(this, charGen, adapter),
				new WizardPageEducation(this, charGen, adapter),
				new WizardPageMoonSign(this, charGen),
				new WizardPageName(this, charGen)
				);
	}

	//-------------------------------------------------------------------
	public SpliMoCharacter getGenerated() {
		return generated;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.Wizard#canBeFinished()
	 */
	@Override
	public boolean canBeFinished() {
		return charGen.hasEnoughDialogData();
	}

}
