package org.prelle.splittermond.chargen.jfx.sections;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.javafx.AlertType;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventType;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * @author Stefan Prelle
 *
 */
public class FlawsSection extends GenericListSection<String> {

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(FlawsSection.class.getName());

	//-------------------------------------------------------------------
	public FlawsSection(String title, CharacterController ctrl, ScreenManagerProvider provider) {
		super(title, ctrl, provider);
//		list.setCellFactory( lv -> new PowerReferenceCell());
		
		setData(ctrl.getModel().getWeaknesses());
		setStyle("-fx-min-width: 20em;");
		
		list.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> getDeleteButton().setDisable(n==null));
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splittermond.chargen.jfx.sections.GenericListSection#onAdd()
	 */
	@Override
	protected void onAdd() {
		logger.trace("onAdd");
		Label question = new Label(RES.getString("section.flaws.dialog.add.question"));
		TextField tfFlaw = new TextField();
		tfFlaw.setPromptText(RES.getString("section.flaws.dialog.add.prompt"));
		VBox layout = new VBox(10, question, tfFlaw);
		
		provider.getScreenManager().showAlertAndCall(AlertType.NOTIFICATION, RES.getString("section.flaws.dialog.add.title"), layout);
		String value = tfFlaw.getText();
		if (value!=null && value.length()>0) {
			logger.info("Add flaw: "+value);
			control.getModel().addWeakness(value);
			list.getItems().add(value);
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.CHARACTER_CHANGED, control.getModel()));
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splittermond.chargen.jfx.sections.GenericListSection#onDelete()
	 */
	@Override
	protected void onDelete() {
		logger.trace("onDelete");
		String toDelete = list.getSelectionModel().getSelectedItem();
		if (toDelete!=null) {
			logger.info("Remove flaw: "+toDelete);
			control.getModel().removeWeakness(toDelete);
			list.getItems().remove(toDelete);
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.CHARACTER_CHANGED, control.getModel()));
			list.getSelectionModel().clearSelection();
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.rpgframework.jfx.Section#refresh()
	 */
	@Override
	public void refresh() {
		setData(control.getModel().getWeaknesses());
	}

}
