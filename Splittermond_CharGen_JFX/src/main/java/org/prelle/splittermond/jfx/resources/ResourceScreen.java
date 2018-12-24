/**
 * 
 */
package org.prelle.splittermond.jfx.resources;

import java.util.Arrays;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.prelle.javafx.ManagedScreen;
import org.prelle.javafx.ResponsiveVBox;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.javafx.WindowMode;
import org.prelle.javafx.fluent.CommandBar;
import org.prelle.rpgframework.jfx.FreePointsNode;
import org.prelle.rpgframework.jfx.SettingsAndCommandBar;
import org.prelle.splimo.LetUserChooseAdapter;
import org.prelle.splimo.Resource;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.chargen.ResourceGenerator;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.fluent.SpliMoCharGenConstants;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/**
 * @author Stefan
 *
 */
public class ResourceScreen extends ManagedScreen implements GenerationEventListener {

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

	private ScreenManagerProvider provider;
	
	private Label lbExpTotal;
	private Label lbExpInvested;
	private FreePointsNode freePoints;
	private Label lbLevel;
	private CommandBar commands;
	private SettingsAndCommandBar firstLine;
	
	private CharacterController control;

	private ResourcePane2 resources;
	private Label description;
	private VBox descLayout;
	private CheckBox allowExtreme;
	
	//--------------------------------------------------------------------
	/**
	 */
	public ResourceScreen(CharacterController control, ScreenManagerProvider provider) {
		this.provider = provider;
		this.control = control;
		
		initComponents();
		initLayout();
		initInteractivity();
		GenerationEventDispatcher.addListener(this);
		
		resources.setData(control.getModel());		
 	}

	//-------------------------------------------------------------------
	private void initComponents() {
		setTitle(UI.getString("resourcescreen.title"));
		
		LetUserChooseAdapter chooser = new LetUserChooseAdapter(provider);
		resources = new ResourcePane2(control.getResourceController(), true, chooser);
		resources.setManager(provider.getScreenManager());

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
		freePoints = new FreePointsNode();
		freePoints.setStyle("-fx-max-height: 3em; -fx-max-width: 3em");
		freePoints.setPoints(control.getModel().getExperienceFree());
		freePoints.setName(UI.getString("label.ep.free"));
		Label hdExpTotal    = new Label(SpliMoCharGenConstants.RES.getString("label.ep.total")+": ");
		Label hdExpInvested = new Label(SpliMoCharGenConstants.RES.getString("label.ep.used")+": ");
		Label hdLevel       = new Label(SpliMoCharGenConstants.RES.getString("label.level")+": ");
		lbExpTotal    = new Label("?");
		lbExpInvested = new Label("?");
		lbLevel       = new Label("?");
		lbExpTotal.getStyleClass().add("base");
		lbExpInvested.getStyleClass().add("base");
		lbLevel.getStyleClass().add("base");
		lbExpTotal.setText(String.valueOf(control.getModel().getExperienceInvested()+control.getModel().getExperienceFree()));
		lbExpInvested.setText(control.getModel().getExperienceInvested()+"");
		lbLevel.setText(control.getModel().getLevel()+"");
		
		commands = new CommandBar();
//		commands.getItems().add(new MenuItem("Drucken", new Label("\uD83D\uDDB6")));
		commands.getItems().add(new CheckMenuItem(UI.getString("screen.resources.allowExtreme")));
		
		HBox expLine = new HBox(5);
		expLine.getChildren().addAll(hdExpTotal, lbExpTotal, hdExpInvested, lbExpInvested, hdLevel, lbLevel);
		HBox.setMargin(hdLevel, new Insets(0,0,0,20));
		expLine.getStyleClass().add("character-document-view-firstline");
		
		firstLine = new SettingsAndCommandBar();
		firstLine.setSettings(expLine);
		firstLine.setCommandBar(commands);
		
		// Extreme values
		allowExtreme = new CheckBox(UI.getString("screen.resources.allowExtreme"));
		allowExtreme.setWrapText(true);
		allowExtreme.getStyleClass().add("text-subheader");
		allowExtreme.setStyle("-fx-text-fill: lighter;");
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		// Resources
		Label lblResource = new Label(UI.getString("label.resources"));
		resources.setMaxWidth(Double.MAX_VALUE);
		resources.setMaxHeight(Double.MAX_VALUE);
		HBox.setHgrow(resources, Priority.NEVER);
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
		HBox flow = new HBox();
		flow.getChildren().addAll(freePoints, resources, descScroll);
		flow.setStyle("-fx-spacing: 1em");

        VBox bxFoo = new VBox(20);
        bxFoo.getChildren().addAll(lblResource, flow);
        flow.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(flow, Priority.ALWAYS);
		bxFoo.setMaxWidth(Double.MAX_VALUE);
		ScrollPane scroll = new ScrollPane(bxFoo);
//		scroll.setMaxWidth(Double.MAX_VALUE);
		scroll.setMaxHeight(Double.MAX_VALUE);
		scroll.setFitToHeight(true);
        
		VBox content = new ResponsiveVBox() {
			@Override
			public void setResponsiveMode(WindowMode value) {
				descScroll.setManaged(value==WindowMode.EXPANDED);
				descScroll.setVisible(value==WindowMode.EXPANDED);
			}
		};
		content.setSpacing(20);
		content.getChildren().addAll(firstLine, flow);
		VBox.setVgrow(flow, Priority.ALWAYS);
		content.getStyleClass().add("resource-screen");
		
		
		setContent(content);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
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

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		resources.handleGenerationEvent(event);
		switch (event.getType()) {
		case EXPERIENCE_CHANGED:
			logger.debug("rcv "+event.getType()+"   "+Arrays.toString((int[])event.getValue()));
			lbExpTotal.setText(String.valueOf(control.getModel().getExperienceInvested()+control.getModel().getExperienceFree()));
			lbExpInvested.setText(control.getModel().getExperienceInvested()+"");
			lbLevel.setText(control.getModel().getLevel()+"");
			freePoints.setPoints(control.getModel().getExperienceFree());
			break;
		default:
			break;
		}		
	}

}
