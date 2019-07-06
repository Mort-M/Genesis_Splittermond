package org.prelle.splittermond.chargen.jfx;

import org.prelle.javafx.ScreenManager;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.splimo.BasePluginData;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splittermond.chargen.jfx.sections.SkillSection;

import de.rpgframework.character.CharacterHandle;
import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;

/**
 * @author Stefan Prelle
 *
 */
public class SMSkillPage extends SpliMoManagedScreenPage {

	private ScreenManagerProvider provider;

	private SkillSection normal;
	private SkillSection combat;
	private SkillSection magic;

	//-------------------------------------------------------------------
	public SMSkillPage(CharacterController control, ViewMode mode, CharacterHandle handle, ScreenManagerProvider provider) {
		super(control, mode, handle);
		this.setId("splittermond-skills");
		this.setTitle(control.getModel().getName());
		this.provider = provider;
		if (this.mode==null)
			this.mode = ViewMode.MODIFICATION;
		
		initComponents();
		initInteractivity();

		refresh();
	}

	//-------------------------------------------------------------------
	private void initNormal() {
		normal = new SkillSection(UI.getString("section.skill.normal"), charGen, provider, SkillType.NORMAL);
		getSectionList().add(normal);

		// Interactivity
		normal.showHelpForProperty().addListener( (ov,o,n) -> updateHelp(n));
	}

	//-------------------------------------------------------------------
	private void initCombat() {
		combat = new SkillSection(UI.getString("section.skill.combat"), charGen, provider, SkillType.COMBAT);
		getSectionList().add(combat);

		// Interactivity
		combat.showHelpForProperty().addListener( (ov,o,n) -> updateHelp(n));
	}

	//-------------------------------------------------------------------
	private void initMagic() {
		magic = new SkillSection(UI.getString("section.skill.magic"), charGen, provider, SkillType.MAGIC);
		getSectionList().add(magic);

		// Interactivity
		magic.showHelpForProperty().addListener( (ov,o,n) -> updateHelp(n));
	}

	//-------------------------------------------------------------------
	protected void initComponents() {
		setPointsNameProperty(UI.getString("label.experience.short"));

		initNormal();
		initCombat();
		initMagic();
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		cmdPrint.setOnAction( ev -> {BabylonEventBus.fireEvent(BabylonEventType.PRINT_REQUESTED, handle, charGen.getModel());});
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
		combat.getToDoList().clear();
		for (String tmp : charGen.getSkillController().getToDos(SkillType.COMBAT)) {
			combat.getToDoList().add(new ToDoElement(Severity.WARNING, tmp));
		}
		normal.getToDoList().clear();
		for (String tmp : charGen.getSkillController().getToDos(SkillType.NORMAL)) {
			normal.getToDoList().add(new ToDoElement(Severity.WARNING, tmp));
		}
		magic.getToDoList().clear();
		for (String tmp : charGen.getSkillController().getToDos(SkillType.MAGIC)) {
			magic.getToDoList().add(new ToDoElement(Severity.WARNING, tmp));
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
