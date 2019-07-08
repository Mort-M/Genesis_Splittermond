package org.prelle.splittermond.chargen.jfx.sections;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.javafx.AlertType;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.javafx.SymbolIcon;
import org.prelle.rpgframework.jfx.DescriptionPane;
import org.prelle.rpgframework.jfx.OptionalDescriptionPane;
import org.prelle.splimo.Resource;
import org.prelle.splimo.ResourceReference;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splittermond.chargen.jfx.listcells.ResourceListCell;
import org.prelle.splittermond.chargen.jfx.listcells.ResourceReferenceListCell;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * @author Stefan Prelle
 *
 */
public class ResourceSection extends GenericListSection<ResourceReference> {

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(ResourceSection.class.getName());

	//-------------------------------------------------------------------
	public ResourceSection(String title, CharacterController ctrl, ScreenManagerProvider provider) {
		super(title, ctrl, provider);
		list.setCellFactory( lv -> new ResourceReferenceListCell(ctrl, provider));

		setData(ctrl.getModel().getResources());
		list.setStyle("-fx-pref-height: 50em; -fx-pref-width: 32em");
		GridPane.setVgrow(list, Priority.ALWAYS);
		list.setMaxHeight(Double.MAX_VALUE);

		list.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> getDeleteButton().setDisable(n==null));

		setSettingsButton( new Button(null, new SymbolIcon("setting")) );
	}

	//-------------------------------------------------------------------
	protected void initInteractivity() {
		super.initInteractivity();
		list.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			Platform.runLater( () -> {
				getDeleteButton().setDisable( !control.getResourceController().canBeDeselected(n));
			});
		});
		getSettingsButton().setOnAction( ev -> logger.warn("TODO: Support settings"));
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splittermond.chargen.jfx.sections.GenericListSection#onAdd()
	 */
	@Override
	protected void onAdd() {
		logger.trace("onAdd");
		Label question = new Label(RES.getString("section.resource.dialog.add.question"));
		ListView<Resource> myList = new ListView<>();
		myList.setCellFactory(lv -> new ResourceListCell(control.getResourceController()));
		myList.getItems().addAll(control.getResourceController().getAvailableResources());
		myList.setPlaceholder(new Label(RES.getString("section.resource.dialog.add.placeholder")));
		VBox innerLayout = new VBox(10, question, myList);

		final DescriptionPane descr = new DescriptionPane();
		OptionalDescriptionPane layout = new OptionalDescriptionPane(innerLayout, descr);

		myList.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			descr.setText(n.getName(), n.getProductName()+" "+n.getPage(), n.getHelpText());
		});


		CloseType result = provider.getScreenManager().showAlertAndCall(AlertType.QUESTION, RES.getString("section.resource.dialog.add.title"), layout);
		if (result==CloseType.OK) {
			Resource value = myList.getSelectionModel().getSelectedItem();
			if (value!=null) {
				logger.debug("Try add resource: "+value);
				myList.getItems().add(value);
				control.getResourceController().openResource(value);
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
		ResourceReference toDelete = list.getSelectionModel().getSelectedItem();
		if (toDelete!=null) {
			logger.info("Try remove resource: "+toDelete);
			if (control.getResourceController().deselect(toDelete)) {
				list.getItems().remove(toDelete);
				list.getSelectionModel().clearSelection();
			} else 
				logger.warn("GUI allowed removing a resource which cannot be deselected: "+toDelete);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.rpgframework.jfx.Section#refresh()
	 */
	@Override
	public void refresh() {
		setData(control.getModel().getResources());
	}

}
