package org.prelle.splittermond.chargen.jfx.wizard;

import org.apache.logging.log4j.LogManager;
import org.prelle.javafx.AlertType;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.Wizard;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.chargen.SpliMoCharacterGenerator;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splittermond.chargen.jfx.LetUserChooseAdapter;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;

/**
 * @author prelle
 *
 */
public class CharGenWizardSpliMo extends Wizard implements GenerationEventListener {

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
		
		GenerationEventDispatcher.addListener(this);

		this.setConfirmCancelCallback( wizardParam -> {
			CloseType answer = getScreenManager().showAlertAndCall(
					AlertType.CONFIRMATION,
					SpliMoCharGenJFXConstants.UI.getString("wizard.cancelconfirm.header"),
					SpliMoCharGenJFXConstants.UI.getString("wizard.cancelconfirm.content"));
			return answer==CloseType.OK || answer==CloseType.YES;
		});
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
		LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME).info("hasEnoughDialogData="+charGen.hasEnoughDialogData());
		return charGen.hasEnoughDialogData();
	}

	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		refresh();
	}

}
