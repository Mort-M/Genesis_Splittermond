/**
 * 
 */
package org.prelle.splittermond.chargen.gen.jfx;

import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.charctrl.PowerController;
import org.prelle.splimo.chargen.SpliMoCharacterGenerator;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splittermond.chargen.fluent.SpliMoCharGenConstants;
import org.prelle.splittermond.jfx.powers.PowerPane;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class DistributePowersPage extends WizardPage implements GenerationEventListener {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle uiResources = SpliMoCharGenConstants.RES;

	private SpliMoCharacterGenerator charGen;
	private PowerController control;
	private SpliMoCharacter model;
	private PowerPane content;
	private VBox      context;

	private Label pointsLeft;
	
	//-------------------------------------------------------------------
	public DistributePowersPage(SpliMoCharacter model, SpliMoCharacterGenerator charGen) {
		this.charGen = charGen;
		this.control = charGen.getPowerController();
		this.model   = model;
		pageInit(uiResources.getString("wizard.distrPower.title"), 
				new Image(DistributePowersPage.class.getClassLoader().getResourceAsStream("data/Splittermond_hochkant.png")));

		GenerationEventDispatcher.addListener(this);

		nextButton.setDisable(control.getPointsLeft()!=0);
		finishButton.setDisable(!charGen.hasEnoughData());
		
		pointsLeft.setText(""+this.control.getPointsLeft());
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splittermond.chargen.gen.jfx.WizardPage#getContent()
	 */
	@Override
	Parent getContent() {
		content = new PowerPane(control, false);
		content.setData(model);
		return content;
	}
	
	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splittermond.chargen.gen.jfx.WizardPage#getContextMenu()
	 */
	@Override
	public Parent getContextMenu() {
		Label placeholder = new Label(" ");
		placeholder.getStyleClass().add("wizard-heading");

		// Line that reflects remaining points to distribute
		pointsLeft = new Label();
		pointsLeft.setStyle("-fx-font-size: 400%");
		pointsLeft.getStyleClass().add("wizard-context");
		Label pointsLeft_f = new Label(uiResources.getString("wizard.distrPower.pointsLeft")+" ");
		pointsLeft_f.getStyleClass().add("wizard-context");


		context = new VBox(15);
		context.setAlignment(Pos.TOP_CENTER);
		context.setPrefWidth(104);
		context.getChildren().addAll(placeholder, pointsLeft, pointsLeft_f);
		return context;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splittermond.chargen.gen.jfx.WizardPage#nextPage()
	 */
	void nextPage() {
		logger.warn("TODO: query for modification choices");
		//		charGen.selectPower(
		//				table.getSelectionModel().getSelectedItem().getValue(),
		//				choiceCallback
		//				);
		super.nextPage();
		//		// If they have complaints, go to the normal next page
		//		if (table.getSelectedToggle().equals(yes)) {
		//			super.nextPage();
		//		} else {
		//			// No complaints? Short-circuit the rest of the pages
		//			navTo("Thanks");
		//		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		if (event.getType()!=GenerationEventType.POINTS_LEFT_POWERS)
			return;

		int left = control.getPointsLeft();
		logger.debug("handleEvent: "+event);
		pointsLeft.setText(""+control.getPointsLeft());
		// Only enable NEXT button when all points are distributed
		nextButton.setDisable(left!=0);
		finishButton.setDisable(!charGen.hasEnoughData());
	}

}
