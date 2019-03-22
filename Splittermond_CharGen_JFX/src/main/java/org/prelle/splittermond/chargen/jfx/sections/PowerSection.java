package org.prelle.splittermond.chargen.jfx.sections;

import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.splimo.PowerReference;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splittermond.chargen.jfx.listcells.PowerEditingCell;

/**
 * @author Stefan Prelle
 *
 */
public class PowerSection extends GenericListSection<PowerReference> {

	//-------------------------------------------------------------------
	public PowerSection(String title, CharacterController ctrl, ScreenManagerProvider provider) {
		super(title, ctrl, provider);
		list.setCellFactory( lv -> new PowerEditingCell(ctrl.getPowerController()));
		
		setData(ctrl.getModel().getPowers());
		list.setStyle("-fx-pref-height: 15em; -fx-pref-width: 32em");
		
		list.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> getDeleteButton().setDisable(n==null));
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splittermond.chargen.jfx.sections.GenericListSection#onAdd()
	 */
	@Override
	protected void onAdd() {
		// TODO Auto-generated method stub
		logger.debug("onAdd");
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splittermond.chargen.jfx.sections.GenericListSection#onDelete()
	 */
	@Override
	protected void onDelete() {
		// TODO Auto-generated method stub
		logger.debug("onDelete");
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.rpgframework.jfx.Section#refresh()
	 */
	@Override
	public void refresh() {
		setData(control.getModel().getPowers());
	}

}
