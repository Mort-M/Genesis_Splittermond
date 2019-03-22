package org.prelle.splittermond.chargen.jfx.sections;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.splimo.LanguageReference;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splittermond.chargen.jfx.listcells.LanguageReferenceCell;

/**
 * @author Stefan Prelle
 *
 */
public class LanguagesSection extends GenericListSection<LanguageReference> {

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(LanguagesSection.class.getName());

	//-------------------------------------------------------------------
	public LanguagesSection(String title, CharacterController ctrl, ScreenManagerProvider provider) {
		super(title, ctrl, provider);
		list.setCellFactory( lv -> new LanguageReferenceCell());
		
		setData(ctrl.getModel().getLanguages());
		list.setStyle("-fx-min-width: 15em; -fx-pref-height: 15em;");
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
		setData(control.getModel().getLanguages());
	}

}
