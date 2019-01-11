/**
 *
 */
package org.prelle.splimo.chargen.jfx;

import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.ManagedScreen;
import org.prelle.javafx.skin.ManagedScreenDialogSkin;
import org.prelle.splimo.SplitterTools;
import org.prelle.splimo.modifications.ModificationChoice;

import de.rpgframework.genericrpg.modification.Modification;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * @author prelle
 *
 */
public class UserChoiceDialog extends ManagedScreen implements ChangeListener<Boolean> {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle res = SpliMoCharGenJFXConstants.UI;

	private ModificationChoice choice;
	private VBox content;

	private boolean isChoiceBox;
	private Modification choiceByChoiceBox;

	//-------------------------------------------------------------------
	/**
	 */
	public UserChoiceDialog(String choiceReason, ModificationChoice choice) {
		this.choice = choice;
		setSkin(new ManagedScreenDialogSkin(this));

		initComponents();

	}

	//-------------------------------------------------------------------
	private void initComponents() {
		/*
		 * Heading
		 */
		String heading = (choice.getNumberOfChoices()>1)?String.format(res.getString("wizard.selectMod.multiple"), choice.getNumberOfChoices()):res.getString("wizard.selectMod.single");

		/*
		 * Content
		 */
		content = new VBox();
		content.setSpacing(10);
		if (choice.getOptions().length<10 && choice.getNumberOfChoices()>1) {
			isChoiceBox = false;
			ToggleGroup group = new ToggleGroup();
			for (Modification mod : choice.getOptions()) {
				if (choice.getNumberOfChoices()<2) {
					RadioButton box = new RadioButton(SplitterTools.getGenerationModificationString(mod));
					box.setToggleGroup(group);
					box.setUserData(mod);
					box.getStyleClass().add("text-body");
					content.getChildren().add(box);
				} else {
					CheckBox check = new CheckBox(SplitterTools.getGenerationModificationString(mod));
					check.setUserData(mod);
					check.selectedProperty().addListener(this);
					content.getChildren().add(check);
				}
			}
			if (!group.getToggles().isEmpty())
				group.getToggles().get(0).setSelected(true);
		} else {
			isChoiceBox = true;
			ChoiceBox<Modification> cbSelect = new ChoiceBox<>();
			cbSelect.getItems().addAll(choice.getOptionList());
			cbSelect.setConverter(new StringConverter<Modification>() {
				public String toString(Modification mod) {
					return SplitterTools.getGenerationModificationString(mod);
				}
				public Modification fromString(String arg0) {return null;}
			});
			cbSelect.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> selectionChanged(n));
			cbSelect.getSelectionModel().select(0);
			content.getChildren().add(cbSelect);
		}


		setTitle(heading);
		setContent(content);
		getNavigButtons().add(CloseType.OK);
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.beans.value.ChangeListener#changed(javafx.beans.value.ObservableValue, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void changed(ObservableValue<? extends Boolean> button, Boolean old,
			Boolean newVal) {
		// Count
		int numSelected = 0;
		for (Node node : content.getChildren()) {
			ButtonBase tmp = (ButtonBase)node;
			boolean isSelected = (tmp instanceof Toggle)?((Toggle)tmp).isSelected():((CheckBox)tmp).isSelected();
			if (isSelected)
				numSelected++;
		}

		for (Node node : content.getChildren()) {
			ButtonBase tmp = (ButtonBase)node;
			boolean isSelected = (tmp instanceof Toggle)?((Toggle)tmp).isSelected():((CheckBox)tmp).isSelected();
			if (!isSelected)
				tmp.setDisable(numSelected>=choice.getNumberOfChoices());
		}


		((ManagedScreenDialogSkin)getSkin()).setDisabled(CloseType.OK, (numSelected!=choice.getNumberOfChoices()));
	}

	//-------------------------------------------------------------------
	private void selectionChanged(Modification mod) {
		logger.debug("Selected "+mod);
		choiceByChoiceBox = mod;
		((ManagedScreenDialogSkin)getSkin()).setDisabled(CloseType.OK, choiceByChoiceBox==null);
	}

	//-------------------------------------------------------------------
	public Modification[] getChoice() {
		Modification[] ret = new Modification[choice.getNumberOfChoices()];
		if (isChoiceBox) {
			ret[0] = choiceByChoiceBox;
			return ret;
		}

		int pos=0;
		for (Node node : content.getChildren()) {
			ButtonBase toggle = (ButtonBase)node;
			boolean isSelected = (toggle instanceof Toggle)?((Toggle)toggle).isSelected():((CheckBox)toggle).isSelected();
			if (isSelected) {
				ret[pos++] = (Modification) toggle.getUserData();
			}
			if (pos==choice.getNumberOfChoices())
				break;
		}
		return ret;
	}

}
