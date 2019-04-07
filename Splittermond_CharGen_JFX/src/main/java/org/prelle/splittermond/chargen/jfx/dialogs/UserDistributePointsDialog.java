package org.prelle.splittermond.chargen.jfx.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.AlertType;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.ManagedDialog;
import org.prelle.javafx.NavigButtonControl;
import org.prelle.splimo.modifications.ModificationChoice;
import org.prelle.splimo.modifications.ResourceModification;
import org.prelle.splimo.modifications.SkillModification;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;

import de.rpgframework.genericrpg.modification.Modification;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class UserDistributePointsDialog extends ManagedDialog implements EventHandler<ActionEvent> {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);

	private static PropertyResourceBundle res = SpliMoCharGenJFXConstants.UI;

	private class SkillField extends HBox {
		public Button dec;
		public Button inc;
		private TextField value;
		
		//--------------------------------------------------------------------
		public SkillField() {
			dec  = new Button("\uE0C6");
			inc  = new Button("\uE0C5");
			inc.getStyleClass().add("mini-button");
			dec.getStyleClass().add("mini-button");
			value = new TextField();
			value.setPrefColumnCount(1);
			value.setEditable(false);
			value.setFocusTraversable(false);
			
			this.getChildren().addAll(dec, value, inc);
			setStyle("-fx-min-width: 7.5em");
		}
		
		//--------------------------------------------------------------------
		public SkillField(String text) {
			dec  = new Button("\uE0C6");
			inc  = new Button("\uE0C5");
			inc.getStyleClass().add("mini-button");
			dec.getStyleClass().add("mini-button");
			value = new TextField();
			value.setPrefColumnCount(text.length());
			value.setText(text);
			value.setEditable(false);
			value.setFocusTraversable(false);
			this.getChildren().addAll(dec, value, inc);
			setStyle("-fx-min-width: 7.5em");
		}
		
		//--------------------------------------------------------------------
		public void setText(String txt) {
			this.value.setText(txt);
		}

		//--------------------------------------------------------------------
		public int getInt() {
			try {
				return Integer.parseInt(value.getText());
			} catch (NumberFormatException e) {
				return 0;
			}
		}

	}
	
	private ModificationChoice choice;

	Map<Modification, SkillField> mapping;
	Map<ButtonBase, Modification> incMapping;
	Map<ButtonBase, Modification> decMapping;
	
	private int pointsLeft;
	private Label pointsLeft_l;
	
	private NavigButtonControl buttonControl;
	
	//-------------------------------------------------------------------
	/**
	 */
	public UserDistributePointsDialog(String choiceReason, ModificationChoice choice) {
		super(null, null, CloseType.OK);
		pointsLeft = choice.getDistribute();
		
		buttonControl = new NavigButtonControl();
		buttonControl.setDisabled(CloseType.OK, true);
		
		this.choice = choice;
		mapping = new HashMap<Modification, SkillField>();
		incMapping = new HashMap<ButtonBase, Modification>();
		decMapping = new HashMap<ButtonBase, Modification>();

		initComponents(choiceReason);
	}
	
	
	//-------------------------------------------------------------------
	private void initComponents(String choiceReason) {
		
		/*
		 * Content
		 */
		Label select = new Label(res.getString("wizard.selectMod.distribute"));
		
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(4));
		grid.setVgap(7);
		grid.setHgap(5);
		
		pointsLeft_l = new Label(String.valueOf(pointsLeft));
		pointsLeft_l.setStyle("-fx-font-size: 400%");
		pointsLeft_l.getStyleClass().add("wizard-context");

		int y=0;
		for (Modification mod : choice.getOptions()) {
			Label tmpL = new Label(mod.toString());
			if (mod instanceof ResourceModification) {
				tmpL.setText(((ResourceModification)mod).getResourceName());
				((ResourceModification)mod).setValue(0);
			}
			if (mod instanceof SkillModification) {
				tmpL.setText(((SkillModification)mod).getSkillName());
				((SkillModification)mod).setValue(0);
			}
			
			SkillField field = new SkillField();
			field.dec.setOnAction(this);
			field.inc.setOnAction(this);
			field.setText("0");
			mapping.put(mod, field);
			incMapping.put(field.inc, mod);
			decMapping.put(field.dec, mod);

			
			grid.add(tmpL , 1, y);
			grid.add(field, 2, y);
			y++;
		}
		
		grid.add(pointsLeft_l, 0, 0, 1,y);
		GridPane.setMargin(pointsLeft_l, new Insets(0, 20, 0, 0));
		
		

		VBox content = new VBox(10);
		content.setPadding(new Insets(5));
		content.getStyleClass().add("wizard-content");
		content.getChildren().addAll(select, grid);
		
		setTitle(choiceReason);
		setContent(content);
	}

//	//-------------------------------------------------------------------
//	/**
//	 * @see javafx.beans.value.ChangeListener#changed(javafx.beans.value.ObservableValue, java.lang.Object, java.lang.Object)
//	 */
//	@Override
//	public void changed(ObservableValue<? extends Boolean> button, Boolean old,
//			Boolean newVal) {
//		// Count
//		int numSelected = 0;
//		for (ButtonBase tmp : mapping.keySet()) {
//			boolean isSelected = (tmp instanceof Toggle)?((Toggle)tmp).isSelected():((CheckBox)tmp).isSelected();
//			if (isSelected)
//				numSelected++;
//		}
//		
//		for (ButtonBase tmp : mapping.keySet()) {
//			boolean isSelected = (tmp instanceof Toggle)?((Toggle)tmp).isSelected():((CheckBox)tmp).isSelected();
//			if (!isSelected)
//				tmp.setDisable(numSelected>=choice.getNumberOfChoices());
//		}
//		
//		ok.setDisable( (numSelected!=choice.getNumberOfChoices()));
//	}
	
	//-------------------------------------------------------------------
	public Modification[] getChoice() {
		List<Modification> mods = new ArrayList<Modification>();
		for (Entry<Modification, SkillField> entry : mapping.entrySet()) {
			if (entry.getValue().getInt()>0)
				mods.add(entry.getKey());
		}
		
		Modification[] ret = new Modification[mods.size()];
		mods.toArray(ret);
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.event.EventHandler#handle(javafx.event.Event)
	 */
	@Override
	public void handle(ActionEvent event) {
		Button key = (Button) event.getSource();
		Modification mod = null;
		if (incMapping.containsKey(key)) {
			mod = incMapping.get(key);
			SkillField field = mapping.get(mod);
			if (pointsLeft>0) {
				if (mod instanceof ResourceModification) {
					((ResourceModification)mod).setValue(((ResourceModification)mod).getValue()+1);
					field.setText(String.valueOf(((ResourceModification)mod).getValue()));
					pointsLeft--;
				} else if (mod instanceof SkillModification) {
					((SkillModification)mod).setValue(((SkillModification)mod).getValue()+1);
					field.setText(String.valueOf(((SkillModification)mod).getValue()));
					pointsLeft--;
				} else {
					getScreenManager().showAlertAndCall(AlertType.ERROR, "Internal Error", 
							"Ups, da ist was schief gelaufen.\nUserDistributePointsDialog does not support "+mod.getClass());
				}
			}
		} else {
			mod = decMapping.get(key);
			SkillField field = mapping.get(mod);
			if (field.getInt()>0) {
				if (mod instanceof ResourceModification) {
					((ResourceModification)mod).setValue(((ResourceModification)mod).getValue()-1);
					field.setText(String.valueOf(((ResourceModification)mod).getValue()));
					pointsLeft++;
				} else if (mod instanceof SkillModification) {
					((SkillModification)mod).setValue(((SkillModification)mod).getValue()-1);
					field.setText(String.valueOf(((SkillModification)mod).getValue()));
					pointsLeft++;
				} else {
					getScreenManager().showAlertAndCall(AlertType.ERROR, "Internal Error", 
							"Ups, da ist was schief gelaufen.\nUserDistributePointsDialog does not support "+mod.getClass());
				}
			}
		}
		
		// Update
		pointsLeft_l.setText(String.valueOf(pointsLeft));
		buttonControl.setDisabled(CloseType.OK, pointsLeft>0);
	}

}
