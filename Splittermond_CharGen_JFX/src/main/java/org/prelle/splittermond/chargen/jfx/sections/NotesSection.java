package org.prelle.splittermond.chargen.jfx.sections;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.rpgframework.jfx.SingleSection;
import org.prelle.splimo.charctrl.CharacterController;

import javafx.scene.control.TextArea;

/**
 * @author Stefan Prelle
 *
 */
public class NotesSection extends SingleSection {

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(NotesSection.class.getName());

	protected CharacterController control; 
	protected TextArea taNotes;;
	protected ScreenManagerProvider provider;

	//-------------------------------------------------------------------
	public NotesSection(String title, CharacterController ctrl, ScreenManagerProvider provider) {
		super(provider, title, null);
		this.control = ctrl;
		taNotes = new TextArea();
		taNotes.setPromptText(RES.getString("notessection.placeholder"));
		super.setContent(taNotes);
		taNotes.setStyle("-fx-pref-height: 12em; -fx-pref-width: 25em;");
		refresh();
		
		taNotes.textProperty().addListener( (ov,o,n) -> control.getModel().setNotes(n));
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.rpgframework.jfx.Section#refresh()
	 */
	@Override
	public void refresh() {
		taNotes.setText(control.getModel().getNotes());
	}

}
