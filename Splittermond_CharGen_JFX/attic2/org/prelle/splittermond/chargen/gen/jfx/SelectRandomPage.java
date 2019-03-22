/**
 * 
 */
package org.prelle.splittermond.chargen.gen.jfx;

import java.util.PropertyResourceBundle;

import org.prelle.splimo.Race;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.chargen.LetUserChooseListener;
import org.prelle.splimo.chargen.SpliMoCharacterGenerator;
import org.prelle.splittermond.chargen.fluent.SpliMoCharGenConstants;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;

/**
 * @author prelle
 *
 */
public class SelectRandomPage extends WizardPage {
	
	private static PropertyResourceBundle ruleResources = SplitterMondCore.getI18nResources();
	private static PropertyResourceBundle uiResources = SpliMoCharGenConstants.RES;
	
	
	private SpliMoCharacterGenerator charGen;
	private LetUserChooseListener choiceCallback;
	
	private ChoiceBox<Race> race;

	//-------------------------------------------------------------------
	public SelectRandomPage(SpliMoCharacterGenerator chGen, LetUserChooseListener choiceCallback) {
		this.charGen = chGen;
		pageInit(uiResources.getString("wizard.selectRandom.title"), 
				new Image(SelectRandomPage.class.getClassLoader().getResourceAsStream("data/Splittermond_hochkant.png")));
		this.choiceCallback = choiceCallback;
		nextButton.setDisable(true);
		finishButton.setDisable(!charGen.hasEnoughData());
		
		initInteractivity();
		// Fill with data
		fillData();
	}

	//-------------------------------------------------------------------
	private void fillData() {
		race.getItems().addAll(SplitterMondCore.getRaces());
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splittermond.chargen.gen.jfx.WizardPage#getContent()
	 */
	@Override
	Parent getContent() {
		race = new ChoiceBox<Race>();
		
		GridPane grid = new GridPane();
		grid.add(new Label(ruleResources.getString("label.race")), 0, 0);
		grid.add(race, 0, 0);
		
		return race;
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		race.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Race>() {
			@Override
			public void changed(ObservableValue<? extends Race> arg0,
					Race arg1, Race select) {
				if (select!=null)
					charGen.selectRace(select, choiceCallback);
			}
		});
	}
	
}
