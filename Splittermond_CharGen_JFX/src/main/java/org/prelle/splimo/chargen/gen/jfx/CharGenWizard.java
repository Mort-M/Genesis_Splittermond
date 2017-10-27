/**
 * 
 */
package org.prelle.splimo.chargen.gen.jfx;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javafx.stage.Stage;

import org.apache.log4j.Logger;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.chargen.SpliMoCharacterGenerator;
import org.prelle.splimo.chargen.LetUserChooseListener;

/**
 * @author prelle
 *
 */
public class CharGenWizard extends Wizard {

	private final static Logger logger = Logger.getLogger("fxui");

	private static PropertyResourceBundle uiResources = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/ui");

	Stage owner;
	private SpliMoCharacterGenerator charGen;
	private SpliMoCharacter generated;

	//-------------------------------------------------------------------
	/**
	 * @param nodes
	 */
	public CharGenWizard(Stage owner, SpliMoCharacter model, SpliMoCharacterGenerator charGen, LetUserChooseListener choiceCallback) {
		super(owner,
				new SelectRacePage(charGen, choiceCallback),
				new SelectCulturePage(charGen, choiceCallback),
				new SelectBackgroundPage(charGen, choiceCallback),
				new SelectEducationPage(charGen, choiceCallback),
				new DistributePowersPage    (model, charGen),
				new DistributeAttributesPage(model, charGen),
				new DistributeResourcesPage (model, charGen),
				new DistributeSkillsPage    (null, model, charGen),
				new SelectMastershipsPage   (model, charGen),
				new SelectSpellsPage        (model, charGen),
				new SelectSplinterPage(charGen),
				new SelectNamePage(charGen)
				);
		this.owner = owner;
		this.charGen = charGen;
		
		owner.show();
		owner.hide();
		owner.showAndWait();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.gen.jfx.Wizard#finish()
	 */
	@Override
	public void finish() {
		logger.debug("finish clicked");
		// Call nextPage of last page to apply data
		pages.get(pages.size()-1).nextPage();
		
		logger.info("---------------Finishing with CharGenWizard");
		generated = charGen.generate();
		logger.debug("Generated = "+generated);
		close();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.gen.jfx.Wizard#cancel()
	 */
	@Override
	public void cancel() {
		logger.info("character creation cancel called");
		// Ask user to confirm cancellation
		boolean really = MessageDialog.showConfirmMessage(uiResources.getString("wizard.confirm.cancelGeneration"));
		if (really) {
			generated = null;
			close();
		}
	}

	//-------------------------------------------------------------------
	public SpliMoCharacter getGenerated() {
		return generated;
	}

}
