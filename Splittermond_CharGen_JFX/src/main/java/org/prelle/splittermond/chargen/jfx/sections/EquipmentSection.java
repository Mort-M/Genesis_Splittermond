package org.prelle.splittermond.chargen.jfx.sections;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.function.BiConsumer;

import org.prelle.javafx.CloseType;
import org.prelle.javafx.NavigButtonControl;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.charctrl.NewItemController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.equip.ItemLevellerAndGenerator;
import org.prelle.splimo.items.CarriedItem;
import org.prelle.splimo.items.ItemLocationType;
import org.prelle.splimo.items.ItemTemplate;
import org.prelle.splittermond.chargen.jfx.dialogs.EditCarriedItemDialog;
import org.prelle.splittermond.chargen.jfx.dialogs.SelectItemDialog;
import org.prelle.splittermond.chargen.jfx.listcells.CarriedItemListCell;

import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ListView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

/**
 * @author Stefan Prelle
 *
 */
public class EquipmentSection extends GenericListSection<CarriedItem> {

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(EquipmentSection.class.getName());

	private ItemLocationType location;
	private BiConsumer<CarriedItem,ItemLocationType> onMove;
	
	//-------------------------------------------------------------------
	public EquipmentSection(String title, CharacterController ctrl, ScreenManagerProvider provider, ItemLocationType location) {
		super(title, ctrl, provider);
		this.location = location;
		list.setCellFactory( lv -> { 
			CarriedItemListCell cell = new CarriedItemListCell();
			cell.setOnDragDetected(event -> dragDetected(event, list, cell));
			cell.setOnMouseClicked(event -> {if (event.getClickCount()==2) onEdit(cell.getItem());});
			return cell;
			});
		list.setOnDragOver(ev -> dragOver(ev));
		list.setOnDragDropped(ev -> dragDropped(ev));
		
		setData(ctrl.getModel().getItems(location));
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
		
		SelectItemDialog dialog = new SelectItemDialog(control);
		NavigButtonControl ctrl = new NavigButtonControl();
		dialog.selectedItemProperty().addListener( (ov,o,n) -> ctrl.setDisabled(CloseType.OK, n==null));
		ctrl.initialize(provider.getScreenManager(), dialog);
		CloseType closed = provider.getScreenManager().showAlertAndCall(dialog, ctrl);
		if (closed==CloseType.OK) {
			ItemTemplate item = dialog.selectedItemProperty().get();
			logger.warn("TODO: add "+item);
			if (item!=null) {
				CarriedItem toAdd = new CarriedItem(item, 1);
				control.getModel().addItem(toAdd);
				onMove.accept(toAdd, location);
			}
		}
//		CarriedItem value = tfFlaw.getText();
//		if (value!=null && value.length()>0) {
//			logger.info("Add item: "+value);
//			control.getModel().addItem(value);
//			list.getItems().add(value);
//			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.CHARACTER_CHANGED, control.getModel()));
//		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splittermond.chargen.jfx.sections.GenericListSection#onDelete()
	 */
	@Override
	protected void onDelete() {
		logger.trace("onDelete");
		CarriedItem toDelete = list.getSelectionModel().getSelectedItem();
		if (toDelete!=null) {
			logger.info("Remove item: "+toDelete);
			control.getModel().removeItem(toDelete);
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
		setData(control.getModel().getItems(location));
	}

	//-------------------------------------------------------------------
	protected void dragDetected(MouseEvent event, ListView<CarriedItem> list, CarriedItemListCell cell) {
		logger.debug("Drag started "+event.getSource());
		Node source = (Node) event.getSource();

		CarriedItem item = cell.getItem();
		String dragID = "";
		if (location==ItemLocationType.BODY)
			dragID="body";
		else if (location==ItemLocationType.BEASTOFBURDEN)
			dragID="beast";
		else if (location==ItemLocationType.CONTAINER)
			dragID="inventory";
		else if (location==ItemLocationType.SOMEWHEREELSE)
			dragID="other";
		dragID += ":"+control.getModel().getItems().indexOf(item);

		/* drag was detected, start a drag-and-drop gesture*/
        /* allow any transfer mode */
        Dragboard db = source.startDragAndDrop(TransferMode.ANY);

        /* Put a string on a dragboard */
        ClipboardContent content = new ClipboardContent();
        content.putString(dragID);
         logger.debug("Drag "+dragID);
        db.setContent(content);

        /* Drag image */
        WritableImage snapshot = source.snapshot(new SnapshotParameters(), null);
         db.setDragView(snapshot);

        event.consume();
    }

	//-------------------------------------------------------------------
	private void dragOver(DragEvent event) {
		Node target = (Node) event.getSource();
		if (event.getGestureSource() != target && event.getDragboard().hasString()) {
            /* allow for both copying and moving, whatever user chooses */
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
	}

	//-------------------------------------------------------------------
	private void dragDropped(DragEvent event) {
		Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasString()) {
            String idToMove = db.getString();
            logger.debug("drop "+idToMove+" at "+location);
        	StringTokenizer tok = new StringTokenizer(idToMove,":");

        	tok.nextToken();
        	int index  = Integer.parseInt(tok.nextToken());
        	CarriedItem toMove = control.getModel().getItems().get(index);

            	move(toMove, location);

            success = true;
        }
        /* let the source know whether the string was successfully
         * transferred and used */
        event.setDropCompleted(success);

        event.consume();
	}

	//-------------------------------------------------------------------
	public void setOnMove(BiConsumer<CarriedItem,ItemLocationType> value) {
		this.onMove = value;
	}

	//-------------------------------------------------------------------
	private void move(CarriedItem item, ItemLocationType newLocation) {
		logger.debug("Move "+item+" to "+newLocation);
		if (onMove!=null && item!=null) {
			onMove.accept(item, newLocation);
		}
	}

	//-------------------------------------------------------------------
	private void onEdit(CarriedItem item) {
		logger.warn("TODO: Edit "+item);
		
		NewItemController itemCtrl = new ItemLevellerAndGenerator(item, 2);
//		NewItemGeneratorPane itemPane = new NewItemGeneratorPane();
//		itemPane.setData(itemCtrl);
//		itemPane.updateContent();
//		
//		ManagedDialog dialog = new ManagedDialog("Edit", itemPane, CloseType.OK);
		
		EditCarriedItemDialog dialog = new EditCarriedItemDialog(control, itemCtrl);
		GenerationEventDispatcher.addListener(dialog);
		
		CloseType closed = provider.getScreenManager().showAlertAndCall(dialog, new NavigButtonControl());
		GenerationEventDispatcher.removeListener(dialog);
	}

}
