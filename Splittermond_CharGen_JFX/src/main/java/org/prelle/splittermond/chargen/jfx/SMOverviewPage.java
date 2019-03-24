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
import org.prelle.splittermond.chargen.jfx.sections.PortraitSection;

import de.rpgframework.character.CharacterHandle;
import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;

/**
 * @author Stefan Prelle
 *
 */
public class SMOverviewPage extends SpliMoManagedScreenPage {

	private ViewMode mode;
	private ScreenManagerProvider provider;

	private BasicDataSection basic;
	private PortraitSection portrait;
	private AttributePrimarySection attrPri;
	private AttributeSecondarySection attrSec;

	private Section secBasic;
	private Section secAttrib;

	//-------------------------------------------------------------------
	public SMOverviewPage(CharacterController control, ViewMode mode, CharacterHandle handle, ScreenManagerProvider provider) {
		super(control, handle);
		this.setId("splittermond-overview");
		this.setTitle(control.getModel().getName());
		this.provider = provider;
		this.handle   = handle;
		this.mode = mode;
		if (this.mode==null)
			this.mode = ViewMode.MODIFICATION;
		
		initComponents();
		initInteractivity();

		refresh();
	}

	//-------------------------------------------------------------------
	private void initBasicData() {
		basic = new BasicDataSection(UI.getString("section.basedata"), charGen, handle, mode, provider);
		portrait = new PortraitSection(UI.getString("section.appearance"), charGen, handle, provider);

		secBasic = new DoubleSection(basic, portrait);
		getSectionList().add(secBasic);

		// Interactivity
		basic.showHelpForProperty().addListener( (ov,o,n) -> updateHelp(n));
	}

	//-------------------------------------------------------------------
	private void initAttributes() {
		attrPri = new AttributePrimarySection(UI.getString("section.attr.primary"), charGen, provider);
		attrSec = new AttributeSecondarySection(UI.getString("section.attr.secondary"), charGen, provider);

		secAttrib = new DoubleSection(attrPri, attrSec);
		getSectionList().add(secAttrib);

		// Interactivity
		attrPri.showHelpForProperty().addListener( (ov,o,n) -> updateHelp(n));
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
		cmdDelete.setOnAction( ev -> BabylonEventBus.fireEvent(BabylonEventType.PRINT_REQUESTED, handle, charGen.getModel()));
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
	private void updateHelp(Attribute data) {
		if (data!=null) {
			this.setDescriptionHeading(data.getName());
			this.setDescriptionPageRef(null);
			this.setDescriptionText(null);
		} else {
			this.setDescriptionHeading(null);
			this.setDescriptionPageRef(null);
			this.setDescriptionText(null);
		}
	}

}
