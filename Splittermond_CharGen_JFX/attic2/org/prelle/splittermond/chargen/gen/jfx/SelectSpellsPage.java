/**
 * 
 */
package org.prelle.splittermond.chargen.gen.jfx;

import java.util.PropertyResourceBundle;

import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.charctrl.SpellController;
import org.prelle.splimo.chargen.SpliMoCharacterGenerator;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splittermond.chargen.common.jfx.SpellPane;
import org.prelle.splittermond.chargen.fluent.SpliMoCharGenConstants;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class SelectSpellsPage extends WizardPage implements GenerationEventListener {

	private static PropertyResourceBundle uiResources = SpliMoCharGenConstants.RES;
	
	private SpliMoCharacterGenerator charGen;
//	private SpliMoCharacter model;
	private SpellController control;
	
	private VBox content;
	private VBox context;
	private Label pointsLeft;
	private SpellPane pane;
	
	//-------------------------------------------------------------------
	public SelectSpellsPage(SpliMoCharacter model, SpliMoCharacterGenerator chGen) {
		this.control = chGen.getSpellController();
		this.charGen = chGen;
		pageInit(uiResources.getString("wizard.selectSpell.title"), 
				new Image(SelectSpellsPage.class.getClassLoader().getResourceAsStream("data/Splittermond_hochkant.png")));
		

		GenerationEventDispatcher.addListener(this);
		nextButton.setDisable(!control.getUnusedFreeSelections().isEmpty());
		finishButton.setDisable(!charGen.hasEnoughData());
		
//		setData();
		pane.setData(model);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		switch (event.getType()) {
		case SPELL_FREESELECTION_CHANGED:
			pointsLeft.setText(String.valueOf(control.getUnusedFreeSelections().size()));
			nextButton.setDisable(!control.getUnusedFreeSelections().isEmpty());
			finishButton.setDisable(!charGen.hasEnoughData());
			break;
		default:
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splittermond.chargen.gen.jfx.WizardPage#getContent()
	 */
	@Override
	Parent getContent() {
		content = new VBox();
		pane = new SpellPane(control, false);
		content.getChildren().add(pane);

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
		pointsLeft = new Label(String.valueOf(control.getUnusedFreeSelections().size()));
		pointsLeft.setStyle("-fx-font-size: 400%");
		pointsLeft.getStyleClass().add("wizard-context");
		Label pointsLeft_f = new Label(uiResources.getString("wizard.distrSkill.pointsLeft")+" ");
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

}
