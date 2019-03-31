package org.prelle.splittermond.chargen.jfx;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;

import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.rpgframework.jfx.DoubleSection;
import org.prelle.rpgframework.jfx.Section;
import org.prelle.splimo.EquipmentTools;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.items.CarriedItem;
import org.prelle.splimo.items.ItemLocationType;
import org.prelle.splittermond.chargen.jfx.sections.CompanionSection;
import org.prelle.splittermond.chargen.jfx.sections.EquipmentSection;

import de.rpgframework.character.CharacterHandle;
import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;

/**
 * @author Stefan Prelle
 *
 */
public class SMEquipmentPage extends SpliMoManagedScreenPage {

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(EquipmentSection.class.getName());

	private ViewMode mode;
	private ScreenManagerProvider provider;

	private EquipmentSection body;
	private EquipmentSection container;
	private EquipmentSection beast;
	private EquipmentSection other;

	private Section secLine1;
	private Section secLine2;

	//-------------------------------------------------------------------
	public SMEquipmentPage(CharacterController control, ViewMode mode, CharacterHandle handle, ScreenManagerProvider provider) {
		super(control, handle);
		this.setId("splittermond-equipment");
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
	private void initLine1() {
		body = new EquipmentSection(RES.getString("section.body"), charGen, provider, ItemLocationType.BODY);
		container = new EquipmentSection(RES.getString("section.container"), charGen, provider, ItemLocationType.CONTAINER);

		secLine1 = new DoubleSection(body, container);
		getSectionList().add(secLine1);

		// Interactivity
		body.showHelpForProperty().addListener( (ov,o,n) -> updateHelp(n));
		container.showHelpForProperty().addListener( (ov,o,n) -> updateHelp(n));
		
		body.setOnMove( (item,newLoc) -> move(item, newLoc));
		container.setOnMove( (item,newLoc) -> move(item, newLoc));
	}

	//-------------------------------------------------------------------
	private void initLine2() {
		beast = new EquipmentSection(RES.getString("section.beast"), charGen, provider, ItemLocationType.BEASTOFBURDEN);
		other = new EquipmentSection(RES.getString("section.other"), charGen, provider, ItemLocationType.SOMEWHEREELSE);

		secLine2 = new DoubleSection(beast, other);
		getSectionList().add(secLine2);

		// Interactivity
		beast.showHelpForProperty().addListener( (ov,o,n) -> updateHelp(n));
		other.showHelpForProperty().addListener( (ov,o,n) -> updateHelp(n));
		
		beast.setOnMove( (item,newLoc) -> move(item, newLoc));
		other.setOnMove( (item,newLoc) -> move(item, newLoc));
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
		cmdDelete.setOnAction( ev -> BabylonEventBus.fireEvent(BabylonEventType.PRINT_REQUESTED, handle, charGen.getModel()));
	}

	//-------------------------------------------------------------------
	private void updateHelp(CarriedItem data) {
		if (data!=null) {
			this.setDescriptionHeading(data.getName());
			this.setDescriptionPageRef(data.getItem().getProductNameShort()+" "+data.getItem().getPage());
			this.setDescriptionText(data.getItem().getHelpText());
		} else {
			this.setDescriptionHeading(null);
			this.setDescriptionPageRef(null);
			this.setDescriptionText(null);
		}
	}

	//-------------------------------------------------------------------
	private void move(CarriedItem item, ItemLocationType newLoc) {
       	logger.debug("Move "+item+" to new location "+newLoc);
		SpliMoCharacter model = charGen.getModel();
		// Remove from old position
		switch (item.getLocation()) {
		case BODY:
//			bxBody.getItems().remove(item);
			EquipmentTools.unequip(model, item);
			break;
		case CONTAINER:
		case SOMEWHEREELSE:
//			bxInventory.getItems().remove(item);
			break;
		case BEASTOFBURDEN:
//			bxBeast.getItems().remove(item);
			break;
		}
		
		// Add add new location
		item.setItemLocation(newLoc);
		switch (newLoc) {
		case BODY:
//			bxBody.getItems().add(item);
			EquipmentTools.equip(model, item);
			break;
		case CONTAINER:
		case SOMEWHEREELSE:
//			bxInventory.getItems().add(item);
			break;
		case BEASTOFBURDEN:
//			bxBeast.getItems().add(item);
			break;
		}
		
		refresh();
	}

}
