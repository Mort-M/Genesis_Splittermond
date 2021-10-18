package org.prelle.splittermond.chargen.jfx;

import org.prelle.javafx.ScreenManager;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.rpgframework.jfx.DoubleSection;
import org.prelle.rpgframework.jfx.Section;
import org.prelle.splimo.BasePluginData;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.creature.CreatureReference;
import org.prelle.splittermond.chargen.jfx.creatures.CreaturePane;
import org.prelle.splittermond.chargen.jfx.sections.CompanionSection;
import org.prelle.splittermond.chargen.jfx.sections.ResourceSection;

import de.rpgframework.character.CharacterHandle;
import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;

/**
 * @author Stefan Prelle
 *
 */
public class SMResourceCompanionPage extends SpliMoManagedScreenPage {

	private ScreenManagerProvider provider;

	private ResourceSection resources;
	private CompanionSection companions;

	private Section section1;
//	private Section secAttrib;

	//-------------------------------------------------------------------
	public SMResourceCompanionPage(CharacterController control, ViewMode mode, CharacterHandle handle, ScreenManagerProvider provider) {
		super(control, mode, handle);
		this.setId("splittermond-resources");
		this.setTitle(control.getModel().getName());
		this.provider = provider;
		if (this.mode==null)
			this.mode = ViewMode.MODIFICATION;
		
		initComponents();
		initInteractivity();

		refresh();
	}

	//-------------------------------------------------------------------
	private void initResources() {
		resources = new ResourceSection(UI.getString("section.resources"), charGen, provider);
		companions= new CompanionSection(UI.getString("section.companions"), charGen, provider);
		
		section1  = new DoubleSection(resources, companions);
		getSectionList().add(section1);

		// Interactivity
		resources.showHelpForProperty().addListener( (ov,o,n) -> { companions.getListView().getSelectionModel().clearSelection();  if (n!=null) updateHelp(n.getResource()); else updateHelp( (BasePluginData)null); });
		companions.showHelpForProperty().addListener( (ov,o,n) -> { resources.getListView().getSelectionModel().clearSelection(); updateHelp(n); });
	}

//	//-------------------------------------------------------------------
//	private void initAttributes() {
//		languages = new LanguagesSection(UI.getString("section.languages"), charGen, provider);
//		cultureLores= new CultureLoreSection(UI.getString("section.culturelores"), charGen, provider);
//
//		secAttrib = new DoubleSection(cultureLores, languages);
//		getSectionList().add(secAttrib);
//
//		// Interactivity
//		languages.showHelpForProperty().addListener( (ov,o,n) -> { if (n!=null) updateHelp(n.getLanguage()); else updateHelp(null); });
//		cultureLores.showHelpForProperty().addListener( (ov,o,n) -> { if (n!=null) updateHelp(n.getCultureLore()); else updateHelp(null); });
//	}

	//-------------------------------------------------------------------
	protected void initComponents() {
		setPointsNameProperty(UI.getString("label.experience.short"));

		initResources();
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		cmdPrint.setOnAction( ev -> {BabylonEventBus.fireEvent(BabylonEventType.PRINT_REQUESTED, handle, charGen.getModel());});
//		cmdDelete.setOnAction( ev -> BabylonEventBus.fireEvent(BabylonEventType.DELETE_REQUESTED, handle, charGen.getModel()));
	}

	//-------------------------------------------------------------------
	private void updateHelp(BasePluginData data) {
		if (getManager()==null)
			setManager(provider.getScreenManager());
		try {
			descrBtnEdit.setVisible(data!=null);
		} catch (NoSuchFieldError e) {
		}
		this.helpData = data;
		if (data!=null) {
			this.setDescriptionHeading(data.getName());
			this.setDescriptionPageRef(data.getProductNameShort()+" "+data.getPage());
			this.setDescriptionText(data.getHelpText());
		} else {
			this.setDescriptionHeading(null);
			this.setDescriptionPageRef(null);
			this.setDescriptionText(null);
		}
	}

	//-------------------------------------------------------------------
	private void updateHelp(CreatureReference data) {
		descrBtnEdit.setVisible(false);
		if (data!=null) {
			this.setDescriptionHeading(data.getName());
			
			CreaturePane pane = new CreaturePane(charGen.getModel(), data, provider);
			this.setDescriptionNode(pane);
//			this.setDescriptionPageRef(data.getProductNameShort()+" "+data.getPage());
//			this.setDescriptionText(data.getHelpText());
		} else {
			this.setDescriptionHeading(null);
			this.setDescriptionPageRef(null);
			this.setDescriptionText(null);
		}
	}

	//-------------------------------------------------------------------
	public void refresh() {
		super.refresh();
		resources.getToDoList().clear();
		for (String tmp : charGen.getResourceController().getToDos()) {
			resources.getToDoList().add(new ToDoElement(Severity.WARNING, tmp));
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.NodeWithCommandBar#getScreenManager()
	 */
	@Override
	public ScreenManager getScreenManager() { 
		return provider.getScreenManager();
	}

}
