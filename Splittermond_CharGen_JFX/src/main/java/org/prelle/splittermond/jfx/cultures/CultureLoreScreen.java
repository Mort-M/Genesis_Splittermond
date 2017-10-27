/**
 * 
 */
package org.prelle.splittermond.jfx.cultures;

import java.util.Arrays;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import org.apache.log4j.Logger;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.ManagedScreen;
import org.prelle.javafx.skin.ManagedScreenStructuredSkin;
import org.prelle.splimo.CultureLore;
import org.prelle.splimo.PointsPane;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.ViewMode;
import org.prelle.splimo.charctrl.CultureLoreController;
import org.prelle.splimo.charctrl.Generator;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;

/**
 * @author Stefan
 *
 */
public class CultureLoreScreen extends ManagedScreen implements GenerationEventListener {

	private final static Logger logger = Logger.getLogger("splimo.jfx");
	
	private static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splimo-chargen");

	private CultureLoreController control;
	private ViewMode mode;

	private PointsPane points;
	private AvailableCultureLorePane available;
	private CultureLorePane cultures;
	
	private VBox descLayout;
	private Label    lblDescription;
	private CheckBox chkIncludeUnavailable;
	private Label    lblIncludeUnavailable;
	
	//--------------------------------------------------------------------
	/**
	 */
	public CultureLoreScreen(CultureLoreController control, ViewMode mode) {
		this.control = control;
		this.mode    = mode;
		if (control==null)
			throw new NullPointerException("Controller is null");
		
		initComponents();
		initLayout();
		initInteractivity();
		setSkin(new ManagedScreenStructuredSkin(this));
		GenerationEventDispatcher.addListener(this);
 	}

	//-------------------------------------------------------------------
	private void initComponents() {
		getNavigButtons().add(CloseType.BACK);
		setTitle(UI.getString("culturelorescreen.title"));
		
		available = new AvailableCultureLorePane(control, AvailableCultureLorePane.DisplayMode.AVAILABLE);
		available.getStyleClass().add("content");
		
		cultures  = new CultureLorePane(control);
		cultures.getStyleClass().add("content");

		lblDescription = new Label(UI.getString("culturelorescreen.lblDesc"));
		lblDescription.setWrapText(true);
		lblDescription.getStyleClass().add("body");
		chkIncludeUnavailable = new CheckBox(UI.getString("culturelorescreen.chkIncl"));
		chkIncludeUnavailable.setWrapText(true);
		chkIncludeUnavailable.getStyleClass().add("text-small-subheader");
		lblIncludeUnavailable = new Label(UI.getString("culturelorescreen.lblIncl"));
		lblIncludeUnavailable.setWrapText(true);
		lblIncludeUnavailable.getStyleClass().add("body");
		
		/*
		 * Exp & Co.
		 */
		points = new PointsPane(mode);
		if (control instanceof Generator)
			points.setGenerator((Generator) control);
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		getStyleClass().add("text-body");
		
		// Available
		Label lblAvailable = new Label(UI.getString("label.available"));
		available.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		VBox.setVgrow(available, Priority.ALWAYS);
		lblAvailable.getStyleClass().add("text-subheader");
		VBox boxAvailable = new VBox(20);
		boxAvailable.getChildren().addAll(lblAvailable, available);
		
		// CultureLores
		Label lblSelected = new Label(UI.getString("label.selected"));
		cultures.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		VBox.setVgrow(cultures, Priority.ALWAYS);
		lblSelected.getStyleClass().add("text-subheader");
		VBox boxCultLores = new VBox(20);
		boxCultLores.getChildren().addAll(lblSelected, cultures);

		// Description, controls
		Label lblDesc = new Label("Erläuterung");
		lblDesc.getStyleClass().add("text-subheader");
		descLayout = new VBox(20);
		descLayout.setStyle("-fx-pref-width: 20em");
		descLayout.getChildren().addAll(lblDesc, lblDescription, chkIncludeUnavailable, lblIncludeUnavailable);

		// Flow; Resources and Powers
		HBox flow = new HBox(40);
		flow.getChildren().addAll(boxAvailable, boxCultLores, descLayout);

		HBox content = new HBox();
		content.setSpacing(20);
		content.getChildren().addAll(points, flow);
		content.setMaxHeight(Double.MAX_VALUE);
		HBox.setHgrow(flow, Priority.ALWAYS);
		HBox.setMargin(points, new Insets(0,0,20,0));
		HBox.setMargin(flow  , new Insets(0,0,20,0));
		setContent(content);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		chkIncludeUnavailable.selectedProperty().addListener( (ov,o,n) -> {
			logger.debug("---switch "+n);
			control.showCultureLoresWithUnmetRequirements(n);
		});
		
		available.getList().setOnSwipeRight(event -> {
			CultureLore selected = available.selectedItemProperty().get();
			logger.debug("Swiped right: "+selected);
			if (selected!=null && control.canBeSelected(selected)) {
				control.select(selected);
			}
		});
		available.setOnAction(event -> {
			logger.debug("Action "+event.getSource());
			CultureLore selected = (CultureLore)event.getSource();
			if (selected!=null && control.canBeSelected(selected)) {
				logger.debug("Select "+selected);
				control.select(selected);
			}
		});
	}

	//-------------------------------------------------------------------
	public void setData(SpliMoCharacter model) {
		points.setData(model);
		available.setData(model);
		cultures.setData(model);
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
		case LANGUAGE_ADDED:
		case LANGUAGE_REMOVED:
			points.refresh();
			break;
		case CULTURELORE_ADDED:
		case CULTURELORE_REMOVED:
			points.refresh();
			break;
		default:
			break;
		}		
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
