package org.prelle.splittermond.chargen.jfx.sections;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.javafx.AlertType;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.splimo.Language;
import org.prelle.splimo.LanguageReference;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splittermond.chargen.jfx.listcells.LanguageListCell;
import org.prelle.splittermond.chargen.jfx.listcells.LanguageReferenceCell;

import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

/**
 * @author Stefan Prelle
 *
 */
public class LanguagesSection extends GenericListSection<LanguageReference> {

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(LanguagesSection.class.getName());

	//-------------------------------------------------------------------
	public LanguagesSection(String title, CharacterController ctrl, ScreenManagerProvider provider) {
		super(title, ctrl, provider);
		list.setCellFactory( lv -> new LanguageReferenceCell());
		
		setData(ctrl.getModel().getLanguages());
		list.setStyle("-fx-min-width: 15em; -fx-pref-height: 15em;");
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splittermond.chargen.jfx.sections.GenericListSection#onAdd()
	 */
	@Override
	protected void onAdd() {
		logger.trace("onAdd");
		Label question = new Label(RES.getString("section.languages.dialog.add.question"));
		ListView<Language> myList = new ListView<>();
		myList.setCellFactory(lv -> new LanguageListCell(control));
		myList.getItems().addAll(control.getLanguageController().getAvailableLanguages());
		myList.setPlaceholder(new Label(RES.getString("section.languages.dialog.add.placeholder")));
		VBox layout = new VBox(10, question, myList);
		
		CloseType result = provider.getScreenManager().showAlertAndCall(AlertType.QUESTION, RES.getString("section.languages.dialog.add.title"), layout);
		if (result==CloseType.OK) {
			Language value = myList.getSelectionModel().getSelectedItem();
			if (value!=null) {
				logger.debug("Try add language: "+value);
				myList.getItems().add(value);
				control.getLanguageController().select(value);
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
		LanguageReference toDelete = list.getSelectionModel().getSelectedItem();
		if (toDelete!=null) {
			logger.info("Try remove language: "+toDelete);
			control.getLanguageController().deselect(toDelete);
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
		setData(control.getModel().getLanguages());
	}

}
