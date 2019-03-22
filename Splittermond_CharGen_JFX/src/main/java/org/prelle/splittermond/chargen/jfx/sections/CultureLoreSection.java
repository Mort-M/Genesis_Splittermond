package org.prelle.splittermond.chargen.jfx.sections;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.javafx.AlertType;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.splimo.CultureLore;
import org.prelle.splimo.CultureLoreReference;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splittermond.chargen.jfx.listcells.AvailableCultureLoreCell;
import org.prelle.splittermond.chargen.jfx.listcells.NewCultureLoreEditingCell;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

/**
 * @author Stefan Prelle
 *
 */
public class CultureLoreSection extends GenericListSection<CultureLoreReference> {

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(CultureLoreSection.class.getName());

	//-------------------------------------------------------------------
	public CultureLoreSection(String title, CharacterController ctrl, ScreenManagerProvider provider) {
		super(title, ctrl, provider);
		list.setCellFactory( lv -> new NewCultureLoreEditingCell(ctrl));
		
		setData(ctrl.getModel().getCultureLores());
		list.setStyle("-fx-min-width: 15em; -fx-pref-height: 15em;");
		
		list.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> getDeleteButton().setDisable(n==null));
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splittermond.chargen.jfx.sections.GenericListSection#onAdd()
	 */
	@Override
	protected void onAdd() {
		logger.trace("onAdd");
		Label question = new Label(RES.getString("section.culturelores.dialog.add.question"));
		CheckBox cbUncommon = new CheckBox(RES.getString("section.culturelores.dialog.add.uncommon"));
		ListView<CultureLore> myList = new ListView<>();
		myList.setCellFactory(lv -> new AvailableCultureLoreCell(control));
		cbUncommon.selectedProperty().addListener( (ov,o,n) -> {
			control.getCultureLoreController().showCultureLoresWithUnmetRequirements(n);
			myList.getItems().clear();
			myList.getItems().addAll(control.getCultureLoreController().getAvailableCultureLores());
		});
		myList.getItems().addAll(control.getCultureLoreController().getAvailableCultureLores());
		myList.setPlaceholder(new Label(RES.getString("section.culturelores.dialog.add.placeholder")));
		VBox layout = new VBox(10, question, cbUncommon, myList);
		
		CloseType result = provider.getScreenManager().showAlertAndCall(AlertType.QUESTION, RES.getString("section.culturelores.dialog.add.title"), layout);
		if (result==CloseType.OK) {
			CultureLore value = myList.getSelectionModel().getSelectedItem();
			if (value!=null) {
				logger.debug("Try add culture lore: "+value);
				myList.getItems().add(value);
				control.getCultureLoreController().select(value);
			}
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splittermond.chargen.jfx.sections.GenericListSection#onDelete()
	 */
	@Override
	protected void onDelete() {
		logger.trace("onDelete");
		CultureLoreReference toDelete = list.getSelectionModel().getSelectedItem();
		if (toDelete!=null) {
			logger.info("Try remove culture lore: "+toDelete);
			control.getCultureLoreController().deselect(toDelete);
			list.getItems().remove(toDelete);
			list.getSelectionModel().clearSelection();
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.rpgframework.jfx.Section#refresh()
	 */
	@Override
	public void refresh() {
		setData(control.getModel().getCultureLores());
	}

}
