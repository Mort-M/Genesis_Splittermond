/**
 * 
 */
package org.prelle.splittermond.jfx.resources;

import java.util.Arrays;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

import org.apache.log4j.Logger;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.ManagedScreen;
import org.prelle.javafx.ScreenManager;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.javafx.skin.ManagedScreenStructuredSkin;
import org.prelle.splimo.LetUserChooseAdapter;
import org.prelle.splimo.PointsPane;
import org.prelle.splimo.Resource;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.ViewMode;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.charctrl.Generator;
import org.prelle.splimo.chargen.ResourceGenerator;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;

/**
 * @author Stefan
 *
 */
public class ResourceScreen extends ManagedScreen implements GenerationEventListener, ScreenManagerProvider {

	public class ResourceTableElement {
		int value;
		String text;
		public ResourceTableElement(int val, String text) {
			this.value = val;
			this.text  = text;				
		}
		public int getValue() { return value; }
		public String getText() { return text; }
	}

	private final static Logger logger = Logger.getLogger("splimo.jfx");
	
	private static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splimo-chargen");

	private CharacterController control;
	private ViewMode mode;

	private PointsPane points;
	private ResourcePane2 resources;
	private Label description;
	private VBox descLayout;
	private CheckBox allowExtreme;
	
	//--------------------------------------------------------------------
	/**
	 */
	public ResourceScreen(CharacterController control, ViewMode mode) {
		this.control = control;
		this.mode    = mode;
		
		initComponents();
		initLayout();
		initInteractivity();
		setSkin(new ManagedScreenStructuredSkin(this));
		GenerationEventDispatcher.addListener(this);
 	}

	//-------------------------------------------------------------------
	private void initComponents() {
		getNavigButtons().add(CloseType.BACK);
		setTitle(UI.getString("resourcescreen.title"));
		
		LetUserChooseAdapter chooser = new LetUserChooseAdapter(this);
		resources = new ResourcePane2(control.getResourceController(), true, chooser);

		description = new Label();
		description.setWrapText(true);
//		description.setContentDisplay(ContentDisplay.BOTTOM);
//		description.setGraphicTextGap(2);
		description.getStyleClass().add("text-body");
		description.setMaxHeight(Double.MAX_VALUE);
		description.setMaxWidth(Double.MAX_VALUE);
		description.setTextAlignment(TextAlignment.JUSTIFY);
		description.setAlignment(Pos.TOP_LEFT);
		description.setStyle("-fx-min-width: 10em; -fx-max-width: 25em;");

		
		description.getStyleClass().add("content");
//		resources.getStyleClass().add("content");

		/*
		 * Exp & Co.
		 */
		points = new PointsPane(mode);
		if (control.getResourceController() instanceof Generator) {
			points.setGenerator((Generator) control.getResourceController());
		}
		
		// Extreme values
		allowExtreme = new CheckBox(UI.getString("screen.resources.allowExtreme"));
		allowExtreme.setWrapText(true);
		allowExtreme.getStyleClass().add("text-subheader");
		allowExtreme.setStyle("-fx-text-fill: lighter;");

		if (control.getResourceController() instanceof ResourceGenerator) {
			points.setExtraNode(allowExtreme);
		}
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		getStyleClass().add("text-body");

		// Resources
		Label lblResource = new Label(UI.getString("label.resources"));
		resources.setMaxWidth(Double.MAX_VALUE);
		resources.setMaxHeight(Double.MAX_VALUE);
		HBox.setHgrow(resources, Priority.ALWAYS);
		lblResource.getStyleClass().add("text-subheader");

		// Description
		descLayout = new VBox();
		descLayout.getChildren().addAll(description);
		descLayout.setMaxHeight(Double.MAX_VALUE);
		ScrollPane descScroll = new ScrollPane(descLayout);
		descScroll.setFitToWidth(true);
		descScroll.setMaxHeight(Double.MAX_VALUE);
		descScroll.setMaxWidth(Double.MAX_VALUE);
		
		/*
		 * Box for ResourcePane and Description
		 */
		GridPane flow = new GridPane();
		flow.add(resources  , 0, 0);
		flow.add(descScroll , 1, 0);
		flow.setHgap(20);
		ColumnConstraints col1 = new ColumnConstraints();
		ColumnConstraints col2 = new ColumnConstraints();
        col1.setPercentWidth(66);
        col2.setPercentWidth(33);
        flow.getColumnConstraints().addAll(col1, col2);
        RowConstraints row1 = new RowConstraints();
        row1.setMaxHeight(Double.MAX_VALUE);
        row1.setFillHeight(true);
        row1.setVgrow(Priority.ALWAYS);
        flow.getRowConstraints().add(row1);

        VBox bxFoo = new VBox(20);
        bxFoo.getChildren().addAll(lblResource, flow);
        flow.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(flow, Priority.ALWAYS);
		bxFoo.setMaxWidth(Double.MAX_VALUE);
		ScrollPane scroll = new ScrollPane(bxFoo);
//		scroll.setMaxWidth(Double.MAX_VALUE);
		scroll.setMaxHeight(Double.MAX_VALUE);
		scroll.setFitToHeight(true);
        
		HBox content = new HBox();
		content.setSpacing(20);
		content.getChildren().addAll(points, scroll);
		HBox.setMargin(points, new Insets(0,0,20,0));
		HBox.setMargin(scroll , new Insets(0,0,20,0));
		scroll.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(scroll, Priority.ALWAYS);
		
		setContent(content);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
//		resources.getTable().getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
//			logger.info("Selected "+n);
//			descLayout.getChildren().clear();
//			if (n!=null) {
//				try {
//					String text = RESOURCES.getString(n.getResource().getId());
//					description.setText(text);
//					
//					descLayout.getChildren().addAll(description, getResourceTable(n.getResource()));
//				} catch (MissingResourceException e) {
//					logger.error("Missing key '"+e.getKey()+"' in splimo-resources.properties");
//					description.setText("Missing key '"+e.getKey()+"' in splimo-resources.properties");
//				}
//			}
//		});
		
		resources.getSelectedItem().addListener( (ov, o,n) -> {
			logger.info("Selected "+n);
			descLayout.getChildren().clear();
			if (n!=null) {
				try {
					String text = n.getHelpResourceBundle().getString("resource."+n.getId());
					description.setText(text);
					
					descLayout.getChildren().addAll(description, getResourceTable(n));
				} catch (MissingResourceException e) {
					logger.error("Missing key '"+e.getKey()+"' in "+n.getHelpResourceBundle());
					description.setText("Missing key '"+e.getKey()+"' "+n.getHelpResourceBundle());
				}
			}
			
		});
		
		if (control.getResourceController() instanceof ResourceGenerator) {
			allowExtreme.selectedProperty().addListener( (ov,o,n) -> ((ResourceGenerator)control.getResourceController()).setAllowMaxResources(n));
		}
	}

	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private Node getResourceTable(Resource res) {
		TableView<ResourceTableElement> table = new TableView<ResourceTableElement>();
		TableColumn<ResourceTableElement, Number> colVal = new TableColumn<>(UI.getString("label.value"));
		TableColumn<ResourceTableElement, String> colTxt = new TableColumn<>(UI.getString("label.meaning"));
		table.getColumns().addAll(colVal, colTxt);
		colVal.setCellValueFactory(new PropertyValueFactory<>("value"));
		colTxt.setCellValueFactory(new PropertyValueFactory<>("text"));
		
		// Fill data
		int start = (res.isBaseResource()?-2:1);
		for (int i=start; i<7; i++) {
			String key = "resource."+res.getId()+".v"+i+".meaning";
			String text = "Missing key '"+key+"'";
			try {
				text = res.getHelpResourceBundle().getString(key);
			} catch (Exception e) {
			}
			table.getItems().add(new ResourceTableElement(i, text));
		}
		
		table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		table.setStyle("-fx-pref-height: "+((table.getItems().size()+1)*2.1)+"em");

		
		return table;
	}

	//-------------------------------------------------------------------
	public void setData(SpliMoCharacter model) {
		points.setData(model);
		resources.setData(model);		
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		switch (event.getType()) {
		case EXPERIENCE_CHANGED:
			logger.debug("rcv "+event.getType()+"   "+Arrays.toString((int[])event.getValue()));
			points.refresh();
			break;
		case POINTS_LEFT_RESOURCES:
			points.refresh();
			break;
		default:
			break;
		}		
	}

	//--------------------------------------------------------------------
	public void setScreenManager(ScreenManager manager) {
		super.setScreenManager(manager);
		resources.setManager(getScreenManager());
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.ManagedScreen#close(org.prelle.javafx.CloseType)
	 */
	@Override
	public boolean close(CloseType closeType) {
		GenerationEventDispatcher.removeListener(this);
		return true;
	}

}
