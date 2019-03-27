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
import org.prelle.splimo.creature.Creature;
import org.prelle.splimo.creature.CreatureReference;
import org.prelle.splittermond.chargen.jfx.listcells.AvailablePowerCell;
import org.prelle.splittermond.chargen.jfx.listcells.CreatureReferenceListCell;
import org.prelle.splittermond.chargen.jfx.listcells.PowerEditingCell;

import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * @author Stefan Prelle
 *
 */
public class CompanionSection extends GenericListSection<CreatureReference> {

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(CompanionSection.class.getName());

	//-------------------------------------------------------------------
	public CompanionSection(String title, CharacterController ctrl, ScreenManagerProvider provider) {
		super(title, ctrl, provider);
		list.setCellFactory( lv -> new CreatureReferenceListCell(ctrl, provider));
		
		setData(ctrl.getModel().getCreatures());
		list.setStyle("-fx-pref-width: 32em");
		GridPane.setVgrow(list, Priority.ALWAYS);
		list.setMaxHeight(Double.MAX_VALUE);
		
		list.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> getDeleteButton().setDisable(n==null));
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splittermond.chargen.jfx.sections.GenericListSection#onAdd()
	 */
	@Override
	protected void onAdd() {
		logger.warn("TODO: onAdd");
//		Label question = new Label(RES.getString("section.resource.dialog.add.question"));
//		ListView<Creature> myList = new ListView<>();
////		myList.setCellFactory(lv -> new CreatureListCell(control.getCreatureController()));
//		myList.getItems().addAll(control.getCreatureController().getAvailableCreatures());
//		myList.setPlaceholder(new Label(RES.getString("section.resource.dialog.add.placeholder")));
//		VBox innerLayout = new VBox(10, question, myList);
//		
//		final DescriptionPane descr = new DescriptionPane();
//		OptionalDescriptionPane layout = new OptionalDescriptionPane(innerLayout, descr);
//		
//		myList.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
//			descr.setText(n.getName(), n.getProductName()+" "+n.getPage(), n.getHelpText());
//		});
//		
//		
//		CloseType result = provider.getScreenManager().showAlertAndCall(AlertType.QUESTION, RES.getString("section.resource.dialog.add.title"), layout);
//		if (result==CloseType.OK) {
//			Creature value = myList.getSelectionModel().getSelectedItem();
//			if (value!=null) {
//				logger.debug("Try add resource: "+value);
//				myList.getItems().add(value);
//				control.getCreatureController().openCreature(value);
//			}
//		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splittermond.chargen.jfx.sections.GenericListSection#onDelete()
	 */
	@Override
	protected void onDelete() {
		logger.trace("onDelete");
		CreatureReference toDelete = list.getSelectionModel().getSelectedItem();
		if (toDelete!=null) {
			logger.info("Try remove resource: "+toDelete);
			control.getModel().removeCreature(toDelete);
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
		setData(control.getModel().getCreatures());
	}

}
