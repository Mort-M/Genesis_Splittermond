/**
 *
 */
package org.prelle.splittermond.chargen.gen.jfx;

import java.util.PropertyResourceBundle;

import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.charctrl.AttributeController;
import org.prelle.splimo.chargen.SpliMoCharacterGenerator;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splittermond.chargen.jfx.ViewMode;
import org.prelle.splittermond.jfx.attributes.AttributePane;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class DistributeAttributesPage extends WizardPage implements GenerationEventListener {

 	private static PropertyResourceBundle uiResources = SpliMoCharGenJFXConstants.UI;

 	private SpliMoCharacterGenerator charGen;
	private AttributeController control;

	private AttributePane content;
	private VBox context;
	private Label pointsLeft;

	//-------------------------------------------------------------------
	public DistributeAttributesPage(SpliMoCharacter model, SpliMoCharacterGenerator charGen) {
		this.charGen = charGen;
		control = charGen.getAttributeController();
		content = new AttributePane(control, ViewMode.GENERATION);
		super.pageInit(
				uiResources.getString("wizard.distrAttrib.title"),
				new Image(DistributeAttributesPage.class.getClassLoader().getResourceAsStream("data/Splittermond_hochkant.png")));
		content.setData(model);

		// Only enable NEXT button when all points are distributed
		nextButton.setDisable(control.getPointsLeft()!=0);
		finishButton.setDisable(!charGen.hasEnoughData());

		GenerationEventDispatcher.addListener(this);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splittermond.chargen.gen.jfx.WizardPage#getContent()
	 */
	@Override
	Parent getContent() {
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
		pointsLeft = new Label(String.valueOf(control.getPointsLeft()));
		pointsLeft.setStyle("-fx-font-size: 400%");
		pointsLeft.getStyleClass().add("wizard-context");
		Label pointsLeft_f = new Label(uiResources.getString("wizard.distrAttrib.pointsLeft")+" ");
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
		super.nextPage();
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		if (event.getType()==GenerationEventType.POINTS_LEFT_ATTRIBUTES) {
			pointsLeft.setText(String.valueOf(event.getValue()));
			// Only enable NEXT button when all points are distributed
			nextButton.setDisable(control.getPointsLeft()!=0);
			finishButton.setDisable(!charGen.hasEnoughData());
		}

	}

}
