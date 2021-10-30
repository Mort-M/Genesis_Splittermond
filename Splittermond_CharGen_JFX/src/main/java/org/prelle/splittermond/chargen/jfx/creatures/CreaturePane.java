package org.prelle.splittermond.chargen.jfx.creatures;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.FontIcon;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.chargen.creature.CreatureTrainer;
import org.prelle.splimo.creature.CreatureReference;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;

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

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);

	//-------------------------------------------------------------------
	public CreaturePane(SpliMoCharacter model, CreatureReference ref, ScreenManagerProvider provider) {
		super(20);
		logger.info("set up CreaturePane for "+ref);
		setUserData(ref);
		
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
			training.setData(new CreatureTrainer(model, ref), model, provider);
			lifeAndTrain.getChildren().addAll(training);
		}
		
		lifeAndTrain.getStyleClass().addAll("bordered");
		lifeAndTrain.setStyle("-fx-pref-width: 30em");
		
		getChildren().addAll(lifeAndTrain);
	}

}
