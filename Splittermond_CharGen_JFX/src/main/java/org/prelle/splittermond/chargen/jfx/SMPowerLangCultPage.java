package org.prelle.splittermond.chargen.jfx;

import org.prelle.javafx.ScreenManager;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.rpgframework.jfx.DoubleSection;
import org.prelle.rpgframework.jfx.Section;
import org.prelle.splimo.BasePluginData;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splittermond.chargen.jfx.sections.CultureLoreSection;
import org.prelle.splittermond.chargen.jfx.sections.FlawsSection;
import org.prelle.splittermond.chargen.jfx.sections.LanguagesSection;
import org.prelle.splittermond.chargen.jfx.sections.PowerSection;

import de.rpgframework.character.CharacterHandle;
import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;

/**
 * @author Stefan Prelle
 *
 */
public class SMPowerLangCultPage extends SpliMoManagedScreenPage {

	private ScreenManagerProvider provider;

	private PowerSection powers;
	private FlawsSection flaws;
	private LanguagesSection languages;
	private CultureLoreSection cultureLores;

	private Section secLine1;
	private Section secLine2;

	//-------------------------------------------------------------------
	public SMPowerLangCultPage(CharacterController control, ViewMode mode, CharacterHandle handle, ScreenManagerProvider provider) {
		super(control, mode, handle);
		this.setId("splittermond-powers");
		this.setTitle(control.getModel().getName());
		this.provider = provider;
		if (this.mode==null)
			this.mode = ViewMode.MODIFICATION;
		
		initComponents();
		initInteractivity();

		refresh();
	}

	//-------------------------------------------------------------------
	private void initBasicData() {
		powers = new PowerSection(UI.getString("section.powers"), charGen, provider);
		flaws = new FlawsSection(UI.getString("section.flaws"), charGen, provider);

		secLine1 = new DoubleSection(powers, flaws);
		getSectionList().add(secLine1);

		// Interactivity
		powers.showHelpForProperty().addListener( (ov,o,n) -> { if (n!=null) updateHelp(n.getPower()); else updateHelp(null); });
	}

	//-------------------------------------------------------------------
	private void initAttributes() {
		languages = new LanguagesSection(UI.getString("section.languages"), charGen, provider);
		cultureLores= new CultureLoreSection(UI.getString("section.culturelores"), charGen, provider);

		secLine2 = new DoubleSection(cultureLores, languages);
		getSectionList().add(secLine2);

		// Interactivity
		languages.showHelpForProperty().addListener( (ov,o,n) -> { if (n!=null) updateHelp(n.getLanguage()); else updateHelp(null); });
		cultureLores.showHelpForProperty().addListener( (ov,o,n) -> { if (n!=null) updateHelp(n.getCultureLore()); else updateHelp(null); });
	}

	//-------------------------------------------------------------------
	protected void initComponents() {
		setPointsNameProperty(UI.getString("label.experience.short"));

		initBasicData();
		initAttributes();
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		cmdPrint.setOnAction( ev -> {BabylonEventBus.fireEvent(BabylonEventType.PRINT_REQUESTED, handle, charGen.getModel());});
//		cmdDelete.setOnAction( ev -> BabylonEventBus.fireEvent(BabylonEventType.DELETE_REQUESTED, handle, charGen.getModel()));
	}

	//-------------------------------------------------------------------
	private void updateHelp(BasePluginData data) {
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
	public void refresh() {
		super.refresh();
		secLine1.getToDoList().clear();
		for (String tmp : charGen.getPowerController().getToDos()) {
			secLine1.getToDoList().add(new ToDoElement(Severity.STOPPER, tmp));
		}
		
		secLine2.getToDoList().clear();
		for (String tmp : charGen.getLanguageController().getToDos()) {
			secLine2.getToDoList().add(new ToDoElement(Severity.STOPPER, tmp));
		}
		for (String tmp : charGen.getCultureLoreController().getToDos()) {
			secLine2.getToDoList().add(new ToDoElement(Severity.STOPPER, tmp));
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
