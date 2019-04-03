package org.prelle.splittermond.chargen.jfx.sections;

import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Spell;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splittermond.chargen.jfx.skills.ListElemSpecialization;

/**
 * @author Stefan Prelle
 *
 */
public class SchoolSpecializationSection extends GenericListSection<ListElemSpecialization> {

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(PowerSection.class.getName());

	private Skill school;
	
	//-------------------------------------------------------------------
	public SchoolSpecializationSection(String title, CharacterController ctrl, ScreenManagerProvider provider) {
		super(title, ctrl, provider);
//		list.setCellFactory( lv -> new PowerEditingCell(ctrl.getPowerController()));
		
//		setData(Splitter);
		setDeleteButton(null);
		setAddButton(null);
		list.setStyle("-fx-pref-width: 27em");
		
		list.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> getDeleteButton().setDisable(n==null));
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splittermond.chargen.jfx.sections.GenericListSection#onAdd()
	 */
	@Override
	protected void onAdd() {
		logger.trace("onAdd");
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splittermond.chargen.jfx.sections.GenericListSection#onDelete()
	 */
	@Override
	protected void onDelete() {
		logger.trace("onDelete");
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.rpgframework.jfx.Section#refresh()
	 */
	@Override
	public void refresh() {
		List<ListElemSpecialization> data = new ArrayList<>();
		setData(data);
	}

	//-------------------------------------------------------------------
	public void setSchool(Skill value) {
		this.school = value;
		refresh();
	}


}
