/**
 *
 */
package org.prelle.splittermond.chargen.jfx.basics;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.rpgframework.jfx.SingleSection;
import org.prelle.splimo.BasePluginData;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splittermond.chargen.jfx.attributes.AttributesSecondaryTableView;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * @author prelle
 *
 */
public class AttributeSecondarySection extends SingleSection {

	private final static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);

	private CharacterController control;
	private SpliMoCharacter model;

	private AttributesSecondaryTableView table;

	private ObjectProperty<BasePluginData> showHelpFor = new SimpleObjectProperty<>();

	//-------------------------------------------------------------------
	public AttributeSecondarySection(String title, CharacterController ctrl, ScreenManagerProvider provider) {
		super(provider, title, null);
		control = ctrl;
		model = ctrl.getModel();

		initComponents();
		initLayout();
		refresh();
		initInteractivity();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		table = new AttributesSecondaryTableView();
		table.setData(model);
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		setContent(table);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		table.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			logger.debug("Selection changed to "+n);
		});
	}

	//-------------------------------------------------------------------
	public void refresh() {
		logger.debug("refresh");
		table.setData(control.getModel());
	}

	//-------------------------------------------------------------------
	public ReadOnlyObjectProperty<BasePluginData> showHelpForProperty() {
		return showHelpFor;
	}

}
