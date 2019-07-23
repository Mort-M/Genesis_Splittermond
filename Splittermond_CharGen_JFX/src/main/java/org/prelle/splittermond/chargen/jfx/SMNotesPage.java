package org.prelle.splittermond.chargen.jfx;

import org.prelle.javafx.ScreenManager;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splittermond.chargen.jfx.sections.NotesSection;

import de.rpgframework.character.CharacterHandle;
import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;

/**
 * @author Stefan Prelle
 *
 */
public class SMNotesPage extends SpliMoManagedScreenPage {

	private ScreenManagerProvider provider;

	private NotesSection notes;


	//-------------------------------------------------------------------
	public SMNotesPage(CharacterController control, ViewMode mode, CharacterHandle handle, ScreenManagerProvider provider) {
		super(control, mode, handle);
		this.setId("splittermond-notes");
		this.setTitle(control.getModel().getName());
		this.provider = provider;
		this.mode = mode;
		if (this.mode==null)
			this.mode = ViewMode.MODIFICATION;
		
		initComponents();
		initInteractivity();

		refresh();
	}

	//-------------------------------------------------------------------
	private void initLine1() {
		notes = new NotesSection(UI.getString("section.notes"), charGen, provider);

		getSectionList().add(notes);

		// Interactivity
//		basic.showHelpForProperty().addListener( (ov,o,n) -> updateHelp(n));
	}

	//-------------------------------------------------------------------
	protected void initComponents() {
		setPointsNameProperty(UI.getString("label.experience.short"));

		initLine1();
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		cmdPrint.setOnAction( ev -> {BabylonEventBus.fireEvent(BabylonEventType.PRINT_REQUESTED, handle, charGen.getModel());});
	}

	//-------------------------------------------------------------------
	public void refresh() {
		super.refresh();
		
		notes.getToDoList().clear();
		for (String todo : charGen.getAttributeController().getToDos())
			notes.getToDoList().add(new ToDoElement(Severity.STOPPER, todo));
	}

//	//-------------------------------------------------------------------
//	private void updateHelp(BasePluginData data) {
//		if (data!=null) {
//			this.setDescriptionHeading(data.getName());
//			this.setDescriptionPageRef(data.getProductNameShort()+" "+data.getPage());
//			this.setDescriptionText(data.getHelpText());
//		} else {
//			this.setDescriptionHeading(null);
//			this.setDescriptionPageRef(null);
//			this.setDescriptionText(null);
//		}
//	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.NodeWithCommandBar#getScreenManager()
	 */
	@Override
	public ScreenManager getScreenManager() { 
		return provider.getScreenManager();
	}

}
