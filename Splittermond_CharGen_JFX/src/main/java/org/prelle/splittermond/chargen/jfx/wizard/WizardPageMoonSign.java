package org.prelle.splittermond.chargen.jfx.wizard;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.Wizard;
import org.prelle.javafx.WizardPage;
import org.prelle.splimo.chargen.SpliMoCharacterGenerator;
import org.prelle.splittermond.chargen.jfx.MoonSignPane;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;

/**
 * @author prelle
 *
 */
public class WizardPageMoonSign extends WizardPage {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);

	private static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle.getBundle(CharGenWizardSpliMo.class.getName());

	private SpliMoCharacterGenerator charGen;

	private MoonSignPane content;

	//-------------------------------------------------------------------
	public WizardPageMoonSign(Wizard wizard, SpliMoCharacterGenerator chGen) {
		super(wizard);
		this.charGen = chGen;

		initComponents();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		// Page Header
		setTitle(UI.getString("wizard.selectSplinter.title"));

		content = new MoonSignPane();
		content.selectedProperty().addListener( (ov,o,n) -> {
			charGen.selectSplinter(n);
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
