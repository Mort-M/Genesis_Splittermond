package org.prelle.splittermond.chargen.jfx;

import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.rpgframework.jfx.DoubleSection;
import org.prelle.rpgframework.jfx.Section;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.BasePluginData;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splittermond.chargen.jfx.sections.AttributePrimarySection;
import org.prelle.splittermond.chargen.jfx.sections.AttributeSecondarySection;
import org.prelle.splittermond.chargen.jfx.sections.BasicDataSection;
import org.prelle.splittermond.chargen.jfx.sections.CultureLoreSection;
import org.prelle.splittermond.chargen.jfx.sections.FlawsSection;
import org.prelle.splittermond.chargen.jfx.sections.LanguagesSection;
import org.prelle.splittermond.chargen.jfx.sections.PortraitSection;
import org.prelle.splittermond.chargen.jfx.sections.PowerSection;
import org.prelle.splittermond.chargen.jfx.sections.SchoolSpecializationSection;
import org.prelle.splittermond.chargen.jfx.sections.SpellListSection;
import org.prelle.splittermond.chargen.jfx.sections.SpellSchoolSection;

import de.rpgframework.character.CharacterHandle;
import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;

/**
 * @author Stefan Prelle
 *
 */
public class SMSpellPage extends SpliMoManagedScreenPage {

	private ViewMode mode;
	private ScreenManagerProvider provider;

	private SpellSchoolSection schools;
	private SpellListSection spells;
	private SchoolSpecializationSection specials;

	private Section secLine2;

	//-------------------------------------------------------------------
	public SMSpellPage(CharacterController control, ViewMode mode, CharacterHandle handle, ScreenManagerProvider provider) {
		super(control, handle);
		this.setId("splittermond-spells");
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
		schools = new SpellSchoolSection(UI.getString("section.schools"), charGen, provider);
		getSectionList().add(schools);

		// Interactivity
//		schools.showHelpForProperty().addListener( (ov,o,n) -> { if (n!=null) updateHelp(n.getPower()); else updateHelp(null); });
	}

	//-------------------------------------------------------------------
	private void initLine2() {
		spells = new SpellListSection(UI.getString("section.spells"), charGen, provider);
		specials= new SchoolSpecializationSection(UI.getString("section.specializations"), charGen, provider);

		secLine2 = new DoubleSection(spells, specials);
		getSectionList().add(secLine2);

		// Interactivity
		spells.showHelpForProperty().addListener( (ov,o,n) -> updateHelp(n));
//		specials.showHelpForProperty().addListener( (ov,o,n) -> updateHelp(n.data));
	}

	//-------------------------------------------------------------------
	protected void initComponents() {
		setPointsNameProperty(UI.getString("label.experience.short"));

		initLine1();
		initLine2();
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		cmdPrint.setOnAction( ev -> {BabylonEventBus.fireEvent(BabylonEventType.PRINT_REQUESTED, handle, charGen.getModel());});
//		cmdDelete.setOnAction( ev -> BabylonEventBus.fireEvent(BabylonEventType.DELETE_REQUESTED, handle, charGen.getModel()));
		
		schools.selectedSchoolProperty().addListener( (ov,o,n) -> {
			spells.setSchool(n);
			specials.setSchool(n);
		});
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

}
