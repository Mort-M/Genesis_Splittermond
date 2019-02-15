/**
 *
 */
package org.prelle.splittermond.jfx.creatures;

import java.util.Iterator;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.splimo.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splimo.creature.CreatureFeature;
import org.prelle.splimo.creature.Lifeform;

import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;

/**
 * @author prelle
 *
 */
public class CreatureFeatureViewPane extends FlowPane {

	private static PropertyResourceBundle UI = SpliMoCharGenJFXConstants.UI;

	private Lifeform model;

	private Label lblHeading;

	//-------------------------------------------------------------------
	/**
	 */
	public CreatureFeatureViewPane() {
		initComponents();
		initLayout();
	}

	//--------------------------------------------------------------------
	private void initComponents() {
		lblHeading = new Label(UI.getString("label.features")+":");
		lblHeading.getStyleClass().add("text-small-subheader");
	}

	//--------------------------------------------------------------------
	private void initLayout() {
		setHgap(3);
		setVgap(3);
		getChildren().add(lblHeading);
	}

	//--------------------------------------------------------------------
	private void initInteractivity() {
	}

	//--------------------------------------------------------------------
	void refresh() {
		getChildren().retainAll(lblHeading);

		for (Iterator<CreatureFeature> it=model.getFeatures().iterator(); it.hasNext(); ) {
			CreatureFeature val = it.next();
			String text = val.getName();
			if (it.hasNext())
				text +=",";
			Text textNode = new Text(text);
			getChildren().add(textNode);
		}
	}

	//--------------------------------------------------------------------
	public void setData(Lifeform model) {
		this.model = model;
//		logger.info("setData "+model.dump());
		refresh();
		initInteractivity();
	}

}
