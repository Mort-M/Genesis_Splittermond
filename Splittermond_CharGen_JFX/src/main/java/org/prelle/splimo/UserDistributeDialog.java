/**
 * 
 */
package org.prelle.splimo;

import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.ManagedScreen;
import org.prelle.javafx.skin.ManagedScreenDialogSkin;
import org.prelle.splimo.modifications.ModificationChoice;
import org.prelle.splimo.modifications.ResourceModification;
import org.prelle.splimo.modifications.SkillModification;

import de.rpgframework.genericrpg.modification.Modification;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

/**
 * @author prelle
 *
 */
public class UserDistributeDialog extends ManagedScreen {

	private static final ResourceBundle UI = ResourceBundle.getBundle("i18n/splimo-chargen");

	private final static Logger logger = Logger.getLogger("splittermond.jfx");
	
	private ManagedScreenDialogSkin skin;
	private ModificationChoice choice;
	private Parent content;

	private ObservableList<Integer> distList;
	private ObservableList<Modification> modList;
	private ObservableList<Modification> resultList;
	
	private Label label;
	private ListView<Integer> distView;
	private ListView<Modification> modView;
	private ListView<Modification> resultView;
	private Button btnCombine;
	
	//-------------------------------------------------------------------
	/**
	 */
	public UserDistributeDialog(String choiceReason, ModificationChoice choice) {
		if (choice.getValues()==null || choice.getValues().length==0)
			throw new IllegalArgumentException("No points to distribute in choice");
		this.choice = choice;

		distList = FXCollections.observableArrayList(choice.getValues());
		modList  = FXCollections.observableArrayList(choice.getOptions());
		resultList = FXCollections.observableArrayList();
		
		initComponents(choiceReason);
		initLayout();
		initInteractivity();
		getNavigButtons().add(CloseType.OK);
		
		skin = new ManagedScreenDialogSkin(this);
		skin.setDisabled(CloseType.OK, true);
		setSkin(skin);
	}

	//-------------------------------------------------------------------
	private void initComponents(String choiceReason) {
		distView   = new ListView<Integer>(distList);
		modView    = new ListView<Modification>(modList);
		resultView = new ListView<Modification>(resultList);
		
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
	public Modification[] getChoice() {
		Modification[] ret = new Modification[choice.getNumberOfChoices()];
		ret = resultList.toArray(ret);
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
			modList.remove(mod);
			modView.setItems(null);
			modView.setItems(modList);
			if (!distList.remove(value)) {
				throw new IllegalStateException("Could not remove "+value+" from list "+distList);
			}
			resultList.add(mod);
			logger.debug(" mods = "+modView.getItems()+" // "+modList);
			logger.debug(" ints = "+distView.getItems()+" // "+distList);
			if (!distList.isEmpty())
				distView.getSelectionModel().select(0);
			if (!modList.isEmpty())
				modView.getSelectionModel().select(0);
			skin.setDisabled(CloseType.OK, !distList.isEmpty());
		} else if (mod instanceof ResourceModification) {
			((ResourceModification)mod).setValue(value);
			modView.getItems().remove(mod);
			distView.getItems().remove(value);
			resultList.add(mod);
			skin.setDisabled(CloseType.OK, !distList.isEmpty());
		} else
			logger.error("Unsupported modification type "+mod);
	}
}


//-------------------------------------------------------------------
class DistributeModificationCell extends ListCell<Modification> {
	private final static Logger logger = Logger.getLogger("fxui.choice");
	 @Override
     public void updateItem(Modification item, boolean empty) {
         super.updateItem(item, empty);
         if (item != null) {
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
	private final static Logger logger = Logger.getLogger("fxui.choice");
	 @Override
   public void updateItem(Modification item, boolean empty) {
       super.updateItem(item, empty);
       if (item != null) {
      	 if (item instanceof SkillModification) 
      		 setText(((SkillModification)item).getSkill().getName()+" +"+((SkillModification)item).getValue());
      	 else if (item instanceof ResourceModification) 
      		 setText(((ResourceModification)item).getResource().getName()+" +"+((ResourceModification)item).getValue());
      	 else
      		 logger.error("Not implemented for modification type "+item);
        }
   }
}