package org.prelle.splittermond.chargen.jfx.sections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.rpgframework.jfx.SingleSection;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splittermond.chargen.jfx.tables.SkillValueTableView;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * @author Stefan Prelle
 *
 */
public class SkillSection extends SingleSection {

	private final static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);

	private CharacterController control;
	private SpliMoCharacter model;

	private SkillValueTableView table;

	private ObjectProperty<Skill> showHelpFor = new SimpleObjectProperty<>();

	//-------------------------------------------------------------------
	/**
	 * @param provider
	 * @param title
	 * @param content
	 */
	public SkillSection(String title, CharacterController ctrl, ScreenManagerProvider provider, SkillType type) {
		super(provider, title, null);
		control = ctrl;
		model = ctrl.getModel();

		initComponents(type, provider);
		initLayout();
		refresh();
		initInteractivity();
	}

	//-------------------------------------------------------------------
	private void initComponents(SkillType type, ScreenManagerProvider provider) {
		table = new SkillValueTableView(control, type, provider);
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
			if (n!=null)
				showHelpFor.set(n.getModifyable());
		});
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.rpgframework.jfx.Section#refresh()
	 */
	@Override
	public void refresh() {
		logger.debug("refresh");
		table.setData(control.getModel());
	}

	//-------------------------------------------------------------------
	public ReadOnlyObjectProperty<Skill> showHelpForProperty() {
		return showHelpFor;
	}

}
