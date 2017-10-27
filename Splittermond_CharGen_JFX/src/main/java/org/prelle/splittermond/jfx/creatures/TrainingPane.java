/**
 * 
 */
package org.prelle.splittermond.jfx.creatures;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.prelle.javafx.FontIcon;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterTools;
import org.prelle.splimo.charctrl.CreatureTrainerController;
import org.prelle.splimo.creature.CreatureModuleReference;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class TrainingPane extends VBox {

	private final static Logger logger = Logger.getLogger("splittermond.jfx");
	
	private static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splimo-chargen");
	
	private CreatureTrainerController ctrl;
	private Label lblHeading; 
	private Button btnEdit;
	private TilePane tiles;
	private ScreenManagerProvider provider;

	//-------------------------------------------------------------------
	public TrainingPane() {
		initComponents();
		initStyle();
		initLayout();
		initInteractivity();
	}

	//--------------------------------------------------------------------
	private void initComponents() {
		lblHeading = new Label(UI.getString("trainingpane.heading")); 
		tiles = new TilePane();
		tiles.setPrefColumns(3);
		btnEdit = new Button(null, new FontIcon("\uE0DD\uE0D8"));
	}

	//--------------------------------------------------------------------
	private void initStyle() {
		lblHeading.getStyleClass().add("text-subheader");
	}

	//--------------------------------------------------------------------
	private void initLayout() {
		setSpacing(10);
		
		HBox headline = new HBox(20);
		headline.getChildren().addAll(lblHeading, btnEdit);
		HBox.setHgrow(lblHeading, Priority.ALWAYS);
		lblHeading.setMaxWidth(Double.MAX_VALUE);
		headline.setMaxWidth(Double.MAX_VALUE);
		headline.setAlignment(Pos.CENTER_LEFT);
		
		tiles.setVgap(5);
		tiles.setHgap(5);
		
		getChildren().addAll(headline, tiles);
		
	}

	//--------------------------------------------------------------------
	private void initInteractivity() {
		btnEdit.setOnAction(ev -> editClicked());
	}

	//--------------------------------------------------------------------
	public void setData(CreatureTrainerController control, SpliMoCharacter model, ScreenManagerProvider provider) {
		this.provider = provider;
		this.ctrl = control;
		tiles.getChildren().clear();
		for (CreatureModuleReference mod : control.getCreature().getTrainings()) {
			Label lblTraining = new Label(mod.getModule().getName());
			lblTraining.getStyleClass().addAll("text-small-subheader");
			lblTraining.setMaxHeight(Double.MAX_VALUE);
			lblTraining.setMaxWidth(Double.MAX_VALUE);
			lblTraining.setStyle("-fx-pref-height: 2em");
			tiles.getChildren().add(lblTraining);
		}
		logger.debug("Creature belongs to resource "+ctrl.getCreature().getResource());
		lblHeading.setText(UI.getString("trainingpane.heading")+" ("+ctrl.getCreature().getInvestedPotential()+"/"+SplitterTools.getCreaturePotential(model, ctrl.getCreature())+")");
	}

	//--------------------------------------------------------------------
	private void editClicked() {
		logger.debug("editClicked");
		
		CreatureTrainingScreen screen = new CreatureTrainingScreen(ctrl);
		provider.getScreenManager().show(screen);
	}

}
