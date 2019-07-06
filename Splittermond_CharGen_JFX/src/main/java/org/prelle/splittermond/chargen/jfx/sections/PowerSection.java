package org.prelle.splittermond.chargen.jfx.sections;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.javafx.AlertType;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.rpgframework.jfx.DescriptionPane;
import org.prelle.rpgframework.jfx.OptionalDescriptionPane;
import org.prelle.splimo.Power;
import org.prelle.splimo.PowerReference;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splittermond.chargen.jfx.listcells.AvailablePowerCell;
import org.prelle.splittermond.chargen.jfx.listcells.PowerEditingCell;

import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

/**
 * @author Stefan Prelle
 *
 */
public class PowerSection extends GenericListSection<PowerReference> {

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(PowerSection.class.getName());

	//-------------------------------------------------------------------
	public PowerSection(String title, CharacterController ctrl, ScreenManagerProvider provider) {
		super(title, ctrl, provider);
		list.setCellFactory( lv -> new PowerEditingCell(ctrl.getPowerController()));
		
		setData(ctrl.getModel().getPowers());
		list.setStyle("-fx-pref-height: 15em; -fx-pref-width: 32em");
		
		list.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> getDeleteButton().setDisable(n==null));
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splittermond.chargen.jfx.sections.GenericListSection#onAdd()
	 */
	@Override
	protected void onAdd() {
		logger.debug("onAdd");
		Label question = new Label(RES.getString("section.power.dialog.add.question"));
		ListView<Power> myList = new ListView<>();
		myList.setCellFactory(lv -> new AvailablePowerCell(control));
		myList.getItems().addAll(control.getPowerController().getAvailablePowers());
		myList.setPlaceholder(new Label(RES.getString("section.power.dialog.add.placeholder")));
		VBox innerLayout = new VBox(10, question, myList);
		
		final DescriptionPane descr = new DescriptionPane();
		OptionalDescriptionPane layout = new OptionalDescriptionPane(innerLayout, descr);
		
		myList.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			descr.setText(n.getName(), n.getProductName()+" "+n.getPage(), n.getHelpText());
		});
		
		
		CloseType result = provider.getScreenManager().showAlertAndCall(AlertType.QUESTION, RES.getString("section.power.dialog.add.title"), layout);
		if (result==CloseType.OK) {
			Power value = myList.getSelectionModel().getSelectedItem();
			if (value!=null) {
				logger.debug("Try add power: "+value);
				myList.getItems().add(value);
				control.getPowerController().select(value);
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
		PowerReference toDelete = list.getSelectionModel().getSelectedItem();
		if (toDelete!=null) {
			logger.info("Try remove power: "+toDelete);
			control.getPowerController().deselect(toDelete);
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
		setData(control.getModel().getPowers());
	}

}
