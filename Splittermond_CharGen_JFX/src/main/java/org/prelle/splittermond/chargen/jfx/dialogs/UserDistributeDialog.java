package org.prelle.splittermond.chargen.jfx.dialogs;

import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.ManagedDialog;
import org.prelle.javafx.NavigButtonControl;
import org.prelle.splimo.modifications.ModificationChoice;
import org.prelle.splimo.modifications.ResourceModification;
import org.prelle.splimo.modifications.SkillModification;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;

import de.rpgframework.genericrpg.modification.Modification;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

/**
 * @author prelle
 *
 */
public class UserDistributeDialog extends ManagedDialog {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);

	private static PropertyResourceBundle UI = SpliMoCharGenJFXConstants.UI;

	private ModificationChoice choice;
	private Parent content;

	private Label label;
	private ListView<Integer> distView;
	private ListView<Modification> modView;
	private ListView<Modification> resultView;
	private Button btnCombine;

	private NavigButtonControl buttonControl;

	//-------------------------------------------------------------------
	/**
	 */
	public UserDistributeDialog(String choiceReason, ModificationChoice choice) {
		super(UI.getString("wizard.selectMod.distribute"), null, CloseType.OK);
		if (choice.getValues()==null || choice.getValues().length==0)
			throw new IllegalArgumentException("No points to distribute in choice");
		this.choice = choice;

		initComponents(choiceReason);
		initLayout();
		initInteractivity();

		buttonControl = new NavigButtonControl();
		buttonControl.setDisabled(CloseType.OK, true);
	}

	//-------------------------------------------------------------------
	private void initComponents(String choiceReason) {
		distView   = new ListView<Integer>();
		modView    = new ListView<Modification>();
		resultView = new ListView<Modification>();
		distView.getItems().addAll(choice.getValues());
		modView.getItems().addAll(choice.getOptions());

		modView .getStyleClass().add("bordered");
		distView.getStyleClass().add("bordered");
		resultView .getStyleClass().add("bordered");

		modView.setCellFactory(new Callback<ListView<Modification>, ListCell<Modification>>() {
			public ListCell<Modification> call(ListView<Modification> list) {
				return new DistributeModificationCell();
			}}
				);
		resultView.setCellFactory(new Callback<ListView<Modification>, ListCell<Modification>>() {
			public ListCell<Modification> call(ListView<Modification> list) {
				return new ResultModificationCell();
			}}
				);


		btnCombine = new Button(UI.getString("button.combine"));
		btnCombine .getStyleClass().add("bordered");
		canBeCombined();

		/*
		 * Top
		 */
		label = new Label(choiceReason);
		label.getStyleClass().add("wizard-heading");
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		modView .setStyle("-fx-pref-width: 13em; -fx-pref-height: 10em;");
		distView.setStyle("-fx-pref-width: 6em; -fx-pref-height: 10em;");
		resultView.setStyle("-fx-max-width: 28.4em; -fx-pref-height: 10em;");

		HBox bxLTR = new HBox(20);
		bxLTR.getChildren().addAll(modView, btnCombine, distView);
		bxLTR.setAlignment(Pos.CENTER_LEFT);
		VBox content = new VBox(20);
		content.getChildren().addAll(label, bxLTR, resultView);
		BorderPane.setMargin(resultView, new Insets(20,0,0,0));

		resultView.prefWidthProperty().bind(bxLTR.widthProperty());

		setTitle(UI.getString("wizard.selectMod.distribute"));
		setContent(content);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		// Button 'combine'
		btnCombine.setOnAction(event -> combine());

		// List selections
		modView.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> canBeCombined() );
		distView.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> canBeCombined() );
	}

	//-------------------------------------------------------------------
	public NavigButtonControl getButtonControl() {
		return buttonControl;
	}

	//-------------------------------------------------------------------
	public Modification[] getChoice() {
		Modification[] ret = new Modification[choice.getNumberOfChoices()];
		ret = resultView.getItems().toArray(ret);
		return ret;
	}

	//-------------------------------------------------------------------
	private void canBeCombined() {
		btnCombine.setDisable( modView.getSelectionModel().getSelectedItem()==null ||
				distView.getSelectionModel().getSelectedItem()==null);
	}

	//-------------------------------------------------------------------
	private void combine() {
		Modification mod = modView.getSelectionModel().getSelectedItem();
		Integer  value   = distView.getSelectionModel().getSelectedItem();

		logger.debug("Combine "+mod+" with "+value);
		if (mod instanceof SkillModification) {
			((SkillModification)mod).setValue(value);
			modView.getItems().remove(mod);
			if (!distView.getItems().remove(value)) {
				throw new IllegalStateException("Could not remove "+value+" from list "+distView.getItems());
			}
			resultView.getItems().add(mod);
			logger.debug(" mods = "+modView.getItems());
			logger.debug(" ints = "+distView.getItems());
			if (!distView.getItems().isEmpty())
				distView.getSelectionModel().select(0);
			if (!modView.getItems().isEmpty())
				modView.getSelectionModel().select(0);
			buttonControl.setDisabled(CloseType.OK, !distView.getItems().isEmpty());
		} else if (mod instanceof ResourceModification) {
			((ResourceModification)mod).setValue(value);
			modView.getItems().remove(mod);
			distView.getItems().remove(value);
			resultView.getItems().add(mod);
			logger.debug(" mods = "+modView.getItems());
			logger.debug(" ints = "+distView.getItems());
			logger.debug(" resu = "+resultView.getItems());
			buttonControl.setDisabled(CloseType.OK, !distView.getItems().isEmpty());
		} else
			logger.error("Unsupported modification type "+mod);
	}
}


//-------------------------------------------------------------------
class DistributeModificationCell extends ListCell<Modification> {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	@Override
	public void updateItem(Modification item, boolean empty) {
		super.updateItem(item, empty);
		if (!empty) {
			if (item instanceof SkillModification) 
				setText(((SkillModification)item).getSkill().getName());
			else if (item instanceof ResourceModification) 
				setText(((ResourceModification)item).getResource().getName());
			else
				logger.error("Not implemented for modification type "+item);
		} else {
			setText(null);
			setGraphic(null);
		}
	}
}

//-------------------------------------------------------------------
class ResultModificationCell extends ListCell<Modification> {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	@Override
	public void updateItem(Modification item, boolean empty) {
		super.updateItem(item, empty);
		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			if (item instanceof SkillModification) 
				setText(((SkillModification)item).getSkill().getName()+" +"+((SkillModification)item).getValue());
			else if (item instanceof ResourceModification) 
				setText(((ResourceModification)item).getResource().getName()+" +"+((ResourceModification)item).getValue());
			else
				logger.error("Not implemented for modification type "+item);
		}
	}
}