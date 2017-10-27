package org.prelle.splittermond.jfx.spells;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

//--------------------------------------------------------------------
public class SpellSlider extends StackPane {

	private static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splimo-chargen");
	
	private final static String[] STATE_NAMES = {
			UI.getString("spellslider.inactive"),
			UI.getString("spellslider.bought"),
			UI.getString("spellslider.free")
	};
	
	private Canvas background;
	private Label  knob;
	
	private GraphicsContext g;
	
	//--------------------------------------------------------------------
	public SpellSlider() {
		initComponents();
		initLayout();
		initInteractivity();
		
		// Find longest word
	}

	//--------------------------------------------------------------------
	private void initComponents() {
		background = new Canvas(200,30);
		knob       = new Label(STATE_NAMES[0]);
		
		g = background.getGraphicsContext2D();
		g.setFill(Color.ORANGE);
		g.fillRoundRect(0, 0, 200, 30, 10,10);
	}

	//--------------------------------------------------------------------
	private void initLayout() {
		getChildren().addAll(background, knob);
	}

	//--------------------------------------------------------------------
	private void initInteractivity() {
		knob.minWidthProperty().addListener( (ov,o,n) -> this.minWidthProperty().set(((Double)n)*2));
		knob.prefWidthProperty().addListener( (ov,o,n) -> this.prefWidthProperty().set(((Double)n)*2));
	}
	
}