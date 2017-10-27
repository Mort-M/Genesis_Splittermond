/**
 * 
 */
package org.prelle.splittermond.jfx.attributes;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javafx.event.EventTarget;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

import org.apache.log4j.Logger;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.ManagedScreen;
import org.prelle.javafx.skin.ManagedScreenStructuredSkin;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.PointsPane;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.ViewMode;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.charctrl.Generator;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;

/**
 * @author prelle
 *
 */
public class AttributeScreen extends ManagedScreen implements GenerationEventListener {

	private final static Logger logger = Logger.getLogger("splittermond.jfx");
	
	private static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splimo-chargen");

	private CharacterController control;
	private ViewMode mode;
	
	private AttributePanePrimary primary;
	private AttributePaneSecondary secondary;
	private Label focusAttributeName;
	private Label focusAttributeDescription;
	private Attribute focussed;
	private TilePane flow;
	
	private PointsPane points;
	private HBox content;
	
	//-------------------------------------------------------------------
	/**
	 */
	public AttributeScreen(CharacterController control, ViewMode mode) {
		this.control = control;
		this.mode    = mode;
		if (control==null)
			throw new NullPointerException();
		
		initComponents();
		initLayout();
		initInteractivity();
		setSkin(new ManagedScreenStructuredSkin(this));
		GenerationEventDispatcher.addListener(this);
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		getNavigButtons().add(CloseType.BACK);
		setTitle(UI.getString("label.attributes"));
		
		focusAttributeName = new Label();
		focusAttributeName.getStyleClass().add("section-head");
		focusAttributeName.setWrapText(true);
		
		focusAttributeDescription = new Label();
		focusAttributeDescription.setWrapText(true);
		focusAttributeDescription.getStyleClass().add("text-body");
		focusAttributeDescription.setMaxHeight(Double.MAX_VALUE);
		focusAttributeDescription.setTextAlignment(TextAlignment.JUSTIFY);
		focusAttributeDescription.setAlignment(Pos.TOP_LEFT);
		
		primary = new AttributePanePrimary(control.getAttributeController(), mode);
		secondary = new AttributePaneSecondary(control.getAttributeController(), mode);

		/*
		 * Exp & Co.
		 */
		points = new PointsPane(mode);
		if (control instanceof Generator)
			points.setGenerator((Generator) control);
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		VBox focusAttr = new VBox();
		focusAttr.setSpacing(20);
		focusAttr.getChildren().addAll(focusAttributeName, focusAttributeDescription);
		
		Label mouseOverHint = new Label(UI.getString("screen.attribute.modMouseOver"));
		mouseOverHint.getStyleClass().add("text-body");
		mouseOverHint.setWrapText(true);

		flow = new TilePane(Orientation.VERTICAL);
		flow.setPrefRows(2);
		flow.setPrefColumns(2); 
		flow.setPrefTileWidth(400);
		flow.setHgap(20);
		flow.setVgap(20);
		flow.getChildren().addAll(primary, secondary, focusAttr, mouseOverHint);
		

		ScrollPane scroll = new ScrollPane(flow);
		scroll.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scroll.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scroll.setPannable(false); 
		
		content = new HBox();
		content.setSpacing(20);		
		content.getChildren().addAll(points, scroll);
		HBox.setMargin(points, new Insets(0,0,20,0));
		setContent(content); 
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		primary.setOnMouseEntered(event -> mouseFocusChanged(event));
		primary.setOnMouseMoved(event -> mouseFocusChanged(event));
		secondary.setOnMouseEntered(event -> mouseFocusChanged(event));
		secondary.setOnMouseMoved(event -> mouseFocusChanged(event));
	}

	//-------------------------------------------------------------------
	private void mouseFocusChanged(MouseEvent event) {
		EventTarget target = event.getTarget();
		if ((target instanceof Label) && (((Label)target).getUserData() instanceof Attribute)) {
			Attribute newAttr = (Attribute) ((Label)target).getUserData();
			if (newAttr==focussed)
				return;
			logger.debug("Focus changes to "+newAttr);
			focusAttributeName.setText(newAttr.getName());
			focusAttributeDescription.setText(UI.getString("descr.attribute."+newAttr.name().toLowerCase()));
			focussed = newAttr;
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		switch (event.getType()) {
		case EXPERIENCE_CHANGED:
		case POINTS_LEFT_ATTRIBUTES:
			logger.debug("rcv "+event.getType());
			points.refresh();
			break;
		default:
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.ManagedScreen#close(org.prelle.javafx.ManagedScreen.CloseType)
	 */
	@Override
	public boolean close(CloseType type) {
		GenerationEventDispatcher.removeListener(this);
		GenerationEventDispatcher.removeListener(primary);
		GenerationEventDispatcher.removeListener(secondary);
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.ManagedScreen#childClosed(org.prelle.javafx.ManagedScreen, org.prelle.javafx.ManagedScreen.CloseType)
	 */
	@Override
	public void childClosed(ManagedScreen child, CloseType type) {
		// TODO Auto-generated method stub

	}

	//-------------------------------------------------------------------
	public void setData(SpliMoCharacter model) {
		setTitle(model.getName()+" / "+UI.getString("label.attributes"));
		primary.setData(model);
		secondary.setData(model);
		focusAttributeName.setText(Attribute.CHARISMA.getName());
		focusAttributeDescription.setText(UI.getString("descr.attribute.charisma"));
		
		points.setData(model);
		if (control.getAttributeController() instanceof Generator) {
			points.setGenerator((Generator)control.getAttributeController());
			points.refresh();
		} 
	}

}
