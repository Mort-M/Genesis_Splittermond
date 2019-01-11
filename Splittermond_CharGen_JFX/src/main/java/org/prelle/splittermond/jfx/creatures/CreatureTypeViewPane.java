/**
 * 
 */
package org.prelle.splittermond.jfx.creatures;

import java.util.Iterator;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splimo.creature.CreatureTypeValue;
import org.prelle.splimo.creature.Lifeform;

import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;

/**
 * @author prelle
 *
 */
public class CreatureTypeViewPane extends FlowPane {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle UI = SpliMoCharGenJFXConstants.UI;

	private Lifeform model;
	
	private Label lblHeading;

	//-------------------------------------------------------------------
	/**
	 */
	public CreatureTypeViewPane() {
		initComponents();
		initLayout();
	}

	//--------------------------------------------------------------------
	private void initComponents() {
		lblHeading = new Label(UI.getString("label.typ")+":");
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
		
		for (Iterator<CreatureTypeValue> it=model.getCreatureTypes().iterator(); it.hasNext(); ) {
			CreatureTypeValue val = it.next();
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
		logger.info("setData "+model);
		refresh();
		initInteractivity();
	}

}
