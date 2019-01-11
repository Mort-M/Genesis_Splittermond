/**
 * 
 */
package org.prelle.splittermond.jfx.equip.input;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.prefs.Preferences;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.AlertType;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.FontIcon;
import org.prelle.javafx.ManagedScreen;
import org.prelle.javafx.skin.ManagedScreenStructuredSkin;
import org.prelle.javafx.skin.NavigButtonControl;
import org.prelle.splimo.Education;
import org.prelle.splimo.SplittermondCustomDataCore;
import org.prelle.splimo.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splimo.items.ItemTemplate;
import org.prelle.splimo.items.ItemType;
import org.prelle.splittermond.jfx.equip.ItemTemplateListView;

import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

/**
 * @author prelle
 *
 */
public class DataInputScreen extends ManagedScreen {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle UI = SpliMoCharGenJFXConstants.UI;
	
	public final static String KEY_MAIL = "my.mailaddress";
	public final static String KEY_NAME = "my.name";
	static Preferences CONFIG = Preferences.userRoot().node("/org/rpgframework/"+System.getProperty("application.id")+"/splittermond");

	
	private VBox sidebar;
	
	private TabPane pane;
	private Tab tabItem;
	private Tab tabEduc;

	private Button btnUpload;
	
	//-------------------------------------------------------------------
	/**
	 */
	public DataInputScreen() {
		setSkin(new ManagedScreenStructuredSkin(this));
		setTitle(UI.getString("screen.datainput.title"));
		
		initComponents();
		initLayout();
//		initInteractivity();
	
		getNavigButtons().add(CloseType.BACK);
		getContextButtons().add(btnUpload);
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		pane = new TabPane();
		pane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		pane.sideProperty().set(Side.LEFT);
		pane.setMaxWidth(Double.MAX_VALUE);
		pane.rotateGraphicProperty().set(true);
		initItems();
		initEducations();

		btnUpload = new Button(null,new FontIcon("\uE0FA",40));
		btnUpload.setTooltip(new Tooltip(UI.getString("button.upload.tooltip")));
		initInteractivity();
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		// Sidebar
		Label lblInfo = new Label(UI.getString("screen.datainput.info"));
		lblInfo.setWrapText(true);
		sidebar = new VBox();
		sidebar.getChildren().add(lblInfo);
		sidebar.getStyleClass().add("section-bar");
		sidebar.setStyle("-fx-pref-width: 15em");
		
		HBox layout = new HBox();
		layout.setStyle("-fx-spacing: 1em");
		layout.getChildren().addAll(sidebar, pane);
		HBox.setMargin(sidebar, new Insets(0,0,20,0));
		HBox.setMargin(pane, new Insets(0,0,20,10));
		HBox.setHgrow(pane, Priority.ALWAYS);
		
		setContent(layout);
	}

	//-------------------------------------------------------------------
	private void initItems() {
		EnterItemTemplatePane editPane = new EnterItemTemplatePane();
		editPane.setStyle("-fx-min-width: 40em;");
		
		HBox itemLayout = new HBox(10);
		itemLayout.setMaxWidth(Double.MAX_VALUE);
		
		ItemTemplateListView.DataProvider provider = new ItemTemplateListView.DataProvider() {
			
			@Override
			public List<ItemTemplate> getItems(ItemType type) {
				try {
					return SplittermondCustomDataCore.getItems();
				} catch (Exception e) {
					logger.error("Failed loading data",e);
				}
				return null;
			}
		};
		
			
		ItemTemplateListView dataView = new ItemTemplateListView(provider);
		dataView.setStyle("-fx-min-width: 20em");
		itemLayout.getChildren().addAll(dataView, editPane);
		itemLayout.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(editPane, Priority.ALWAYS);
		
		dataView.setCellFactory(new Callback<ListView<ItemTemplate>, ListCell<ItemTemplate>>() {
			public ListCell<ItemTemplate> call(ListView<ItemTemplate> param) {
				return new CustomItemTemplateListCell( 
//						item -> {
//							logger.debug("Edit"); 
//							editPane.setData(item);
//							},
						item -> {logger.debug("Delete"); SplittermondCustomDataCore.removeItem(item); dataView.getItems().remove(item);});
			}
		});
		dataView.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			if (n!=null) {
				logger.debug("Selected "+n);
				editPane.setData(n);
				editPane.requestLayout();
			}
		});
		
		editPane.setMaxWidth(Double.MAX_VALUE);
		itemLayout.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(editPane, Priority.ALWAYS);
		
		tabItem = new Tab("Gegenstände", itemLayout);
		pane.getTabs().add(tabItem);
	}

	//-------------------------------------------------------------------
	private void initEducations() {
		EnterEducationPane editPane = new EnterEducationPane();
		editPane.setStyle("-fx-min-width: 40em;");
		
		HBox itemLayout = new HBox(10);
		itemLayout.setMaxWidth(Double.MAX_VALUE);
			
		ListView<Education> dataView = new ListView<Education>();
		dataView.getItems().addAll(SplittermondCustomDataCore.getEducations());
		dataView.setStyle("-fx-min-width: 20em");
		itemLayout.getChildren().addAll(dataView, editPane);
		itemLayout.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(editPane, Priority.ALWAYS);
		
		dataView.setCellFactory(new Callback<ListView<Education>, ListCell<Education>>() {
			public ListCell<Education> call(ListView<Education> param) {
				return new CustomEducationListCell( 
//						item -> {
//							logger.debug("Edit"); 
//							editPane.setData(item);
//							},
						item -> {logger.debug("Delete"); SplittermondCustomDataCore.removeEducation(item); dataView.getItems().remove(item);});
			}
		});
		dataView.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			if (n!=null) {
				logger.debug("Selected "+n);
				editPane.setData(n);
				editPane.requestLayout();
			}
		});
		
		editPane.setMaxWidth(Double.MAX_VALUE);
		itemLayout.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(editPane, Priority.ALWAYS);
		
		tabEduc = new Tab("Ausbildungen");
		pane.getTabs().add(tabEduc);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		btnUpload.setOnAction(event -> {
			Tab selected = pane.getSelectionModel().getSelectedItem();
			if (selected==tabItem)
				uploadItemsClicked();
			else
				logger.warn("Don't know how to upload "+selected);
		});
	}

	//-------------------------------------------------------------------
	private void uploadItemsClicked() {
		logger.info("upload items");
		
		List<ItemTemplate> toUpload = new ArrayList<ItemTemplate>();
		for (ItemTemplate item : SplittermondCustomDataCore.getItems()) {
			if ( item.getPage()!=0 && item.getHelpText()!=null && !item.getHelpText().isEmpty())
				toUpload.add(item);
		}
		

		NavigButtonControl buttonControl = new NavigButtonControl();
		UploadPane pane = new UploadPane(buttonControl);
		String title = UI.getString("dialog.upload.items.title");
		
		CloseType result = manager.showAlertAndCall(AlertType.QUESTION, title, pane, buttonControl);
		if (result==CloseType.OK) {
			try {
				boolean success = SplittermondCustomDataCore.upload(toUpload, pane.getFromAddress(), pane.getDisplayName(), pane.getDescription());
				CONFIG.put(KEY_MAIL, pane.getFromAddress());
				CONFIG.put(KEY_NAME, pane.getDisplayName());
				if (success) {
					manager.showAlertAndCall(AlertType.NOTIFICATION, "", String.format(UI.getString("dialog.upload.items.success"), toUpload.size()));
				} else {
					manager.showAlertAndCall(AlertType.NOTIFICATION, "", UI.getString("dialog.upload.items.error"));
				}
			} catch (IOException e) {
				logger.error("Failed uploading",e);
				manager.showAlertAndCall(AlertType.NOTIFICATION, "", String.format(UI.getString("dialog.upload.items.exception"), e.getMessage()));
			}
		}
	}

}

class CustomItemTemplateListCell extends ListCell<ItemTemplate> {
	
//	public interface CellEditCallback {
//		public void editEvent(ItemTemplate item);
//	}
	public interface CellDeleteCallback {
		public void deleteEvent(ItemTemplate item);
	}
	
	private ItemTemplate data;
	
	private Label  lblName;
	private Button btnDel;
	private Label  lblState;
//	private Button btnEdit;
	private HBox box;
	
	//-------------------------------------------------------------------
	public CustomItemTemplateListCell(CellDeleteCallback onDelete) {
		lblName = new Label();
		lblState= new Label("\uE10B");
		btnDel = new Button("\uE107");
//		btnEdit= new Button("\uE104");
		Region reg = new Region();
		reg.setMaxWidth(Double.MAX_VALUE);
		box = new HBox(lblName, reg, btnDel, lblState);
		box.setStyle("-fx-spacing: 0.6em");
		HBox.setHgrow(reg, Priority.ALWAYS);
		
		btnDel .setOnAction(event -> onDelete.deleteEvent(data));
//		btnEdit.setOnAction(event -> onEdit.editEvent(data));
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	public void updateItem(ItemTemplate item, boolean empty) {
		super.updateItem(item, empty);
		this.data = item;
		
		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
//			setText(item.getName());
			lblName.setText(item.getName());
//				btnDel.setVisible(model.getSelectedItems().contains(item));
			lblState.setVisible(item.getPage()>0 && item.getHelpText()!=null && !item.getHelpText().isEmpty());
			setGraphic(box);
			
		}
	}
}

class CustomEducationListCell extends ListCell<Education> {
	
	public interface CellDeleteCallback {
		public void deleteEvent(Education item);
	}
	
	private Education data;
	
	private Label  lblName;
	private Button btnDel;
	private Label  lblState;
//	private Button btnEdit;
	private HBox box;
	
	//-------------------------------------------------------------------
	public CustomEducationListCell(CellDeleteCallback onDelete) {
		lblName = new Label();
		lblState= new Label("\uE10B");
		btnDel = new Button("\uE107");
//		btnEdit= new Button("\uE104");
		Region reg = new Region();
		reg.setMaxWidth(Double.MAX_VALUE);
		box = new HBox(lblName, reg, btnDel, lblState);
		box.setStyle("-fx-spacing: 0.6em");
		HBox.setHgrow(reg, Priority.ALWAYS);
		
		btnDel .setOnAction(event -> onDelete.deleteEvent(data));
//		btnEdit.setOnAction(event -> onEdit.editEvent(data));
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	public void updateItem(Education item, boolean empty) {
		super.updateItem(item, empty);
		this.data = item;
		
		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
//			setText(item.getName());
			lblName.setText(item.getName());
//				btnDel.setVisible(model.getSelectedItems().contains(item));
			lblState.setVisible(item.getPage()>0 && item.getHelpText()!=null && !item.getHelpText().isEmpty());
			setGraphic(box);
			
		}
	}
}

