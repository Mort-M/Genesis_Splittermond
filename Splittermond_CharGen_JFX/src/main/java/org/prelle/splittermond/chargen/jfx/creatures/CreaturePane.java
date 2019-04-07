package org.prelle.splittermond.chargen.jfx.creatures;

import org.prelle.javafx.FontIcon;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.splimo.chargen.creature.CreatureTrainer;
import org.prelle.splimo.creature.CreatureReference;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * @author Stefan Prelle
 *
 */
public class CreaturePane extends VBox {

	//-------------------------------------------------------------------
	public CreaturePane(CreatureReference ref) {
		super(20);
		setUserData(ref);
		Label heading = new Label(ref.getName());
		heading.getStyleClass().add("text-subheader");
		Button btnDel = new Button(null, new FontIcon("\uE107"));
//		btnDel.setTooltip(new Tooltip(UI.getString("tooltip.creature.delete")));
//		btnDel.setOnAction(event -> {remove(ref); update();});
		Button btnEdit = new Button(null, new FontIcon("\uE0D8"));
//		btnEdit.setOnAction(event -> {askName(ref); update();});
		
		HBox headline = new HBox(10);
		headline.getChildren().addAll(heading, btnDel, btnEdit);
		HBox.setMargin(btnDel, new Insets(0,0,0,10));
		HBox.setHgrow(heading, Priority.ALWAYS);
		heading.setMaxWidth(Double.MAX_VALUE);
		headline.setMaxWidth(Double.MAX_VALUE);
		headline.setAlignment(Pos.CENTER_LEFT);
		
		VBox lifeAndTrain = new VBox(20);
		// Companion data
		LifeformPane pane = new LifeformPane();
		if (ref.getModuleBasedCreature()!=null)
			pane.setData(ref.getModuleBasedCreature());
		else if (ref.getEntourage()!=null)
			pane.setData(ref.getEntourage());
		else if (ref.getTemplate()!=null)
			pane.setData(ref.getTemplate());

		lifeAndTrain.getChildren().addAll(pane);

		// Training data
		if (ref.getEntourage()==null) {
			TrainingPane training = new TrainingPane();
			training.setData(new CreatureTrainer(model, ref), model, (ScreenManagerProvider)this);
			lifeAndTrain.getChildren().addAll(training);
		}
		
		lifeAndTrain.getStyleClass().addAll("bordered","content");
		lifeAndTrain.setStyle("-fx-pref-width: 30em");
		
		getChildren().addAll(headline, lifeAndTrain);
	}

}
