/**
 * 
 */
package org.prelle.splimo.chargen.jfx;

import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.Wizard;
import org.prelle.javafx.WizardPage;
import org.prelle.splimo.chargen.SpliMoCharacterGenerator;

/**
 * @author prelle
 *
 */
public class WizardPageMoonSign extends WizardPage {

	private static Logger logger = LogManager.getLogger("splittermond.jfx");

	private static PropertyResourceBundle UI = SpliMoCharGenJFXConstants.UI;

	private SpliMoCharacterGenerator charGen;

	private MoonSignPane content;

	//-------------------------------------------------------------------
	public WizardPageMoonSign(Wizard wizard, SpliMoCharacterGenerator chGen) {
		super(wizard);
		this.charGen = chGen;

		initComponents();

		nextButton.set(false);
		finishButton.set(false);
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		// Page Header
		setTitle(UI.getString("wizard.selectSplinter.title"));

		content = new MoonSignPane();
		content.selectedProperty().addListener( (ov,o,n) -> {
			nextButton.set(n!=null);
			charGen.selectSplinter(n);
			finishButton.set(getWizard().canBeFinished());
		});

		setContent(content);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.WizardPage#pageLeft(org.prelle.javafx.CloseType)
	 */
	public void pageLeft(CloseType type) {
		if (type==CloseType.NEXT || type==CloseType.FINISH) {
			if (content.selectedProperty().get()==null) {
				logger.warn("Trying to continue without anything selected");
				throw new IllegalStateException("Nothing selected");
			}

			Runnable run = new Runnable() {
				public void run() {	charGen.selectSplinter(content.selectedProperty().get()); }				
			};
			Thread thread = new Thread(run,"BlocksInLetUserChoose");
			thread.start();
		}
	}

}
