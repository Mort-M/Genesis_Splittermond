/**
 *
 */
package org.prelle.splittermond.jfx.equip;

import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.ScreenManager;
import org.prelle.splimo.Deity;
import org.prelle.splimo.charctrl.NewItemController;
import org.prelle.splimo.chargen.fluent.SpliMoCharGenConstants;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * @author Stefan
 *
 */
public class HolyItemPane extends VBox {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle RES = SpliMoCharGenConstants.RES;

	private NewItemController control;
	private ScreenManager manager;

	private ChoiceBox<Deity> cbDeities;

	private EnhancementListView lvAvailable;
	private EnhancementReferenceListView lvSelected;

	//--------------------------------------------------------------------
	public HolyItemPane() {
		initComponents();
		initLayout();
		initInteractivity();
	}

	//--------------------------------------------------------------------
	private void initComponents() {
	}

	//--------------------------------------------------------------------
	private void initLayout() {
		Label heaDeity     = new Label(RES.getString("label.deity"));
		Label heaCategory  = new Label(RES.getString("holyitempane.category"));
		Label heaAvailable = new Label(RES.getString("label.available"));
		Label heaSelected  = new Label(RES.getString("label.selected"));
		Label heaDescription = new Label(RES.getString("label.description"));
	}

	//--------------------------------------------------------------------
	private void initInteractivity() {
	}

}
