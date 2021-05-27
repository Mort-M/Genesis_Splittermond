package org.prelle.splittermond.chargen.jfx;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.javafx.ScreenManager;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.rpgframework.jfx.DoubleSection;
import org.prelle.rpgframework.jfx.Section;
import org.prelle.splimo.EquipmentTools;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.items.CarriedItem;
import org.prelle.splimo.items.ItemLocationType;
import org.prelle.splittermond.chargen.jfx.sections.EquipmentSection;

import de.rpgframework.character.CharacterHandle;
import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/**
 * @author Stefan Prelle
 *
 */
public class SMEquipmentPage extends SpliMoManagedScreenPage {

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(EquipmentSection.class.getName());

	private ScreenManagerProvider provider;

	private TextField tfSolare;
	private TextField tfLunare;
	private TextField tfTelare;
	
	private EquipmentSection body;
	private EquipmentSection container;
	private EquipmentSection beast;
	private EquipmentSection other;

	private Section secLine1;
	private Section secLine2;

	private boolean ignoreEvent;

	//-------------------------------------------------------------------
	public SMEquipmentPage(CharacterController control, ViewMode mode, CharacterHandle handle, ScreenManagerProvider provider) {
		super(control, mode, handle);
		this.setId("splittermond-equipment");
		this.setTitle(control.getModel().getName());
		this.provider = provider;
		this.handle   = handle;
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
		
		tfSolare = new TextField();
		tfSolare.setPrefColumnCount(2);
		tfLunare = new TextField();
		tfLunare.setPrefColumnCount(2);
		tfTelare = new TextField();
		tfTelare.setPrefColumnCount(2);
		Label lbSol = new Label(UI.getString("label.currency.solare"));
		Label lbLun = new Label(UI.getString("label.currency.lunare"));
		Label lbTel = new Label(UI.getString("label.currency.telare"));
		expLine.getChildren().addAll(lbSol, tfSolare);
		expLine.getChildren().addAll(lbLun, tfLunare);
		expLine.getChildren().addAll(lbTel, tfTelare);
		HBox.setMargin(lbSol, new Insets(0,0,0,40));

		initLine1();
		initLine2();
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		cmdPrint.setOnAction( ev -> {BabylonEventBus.fireEvent(BabylonEventType.PRINT_REQUESTED, handle, charGen.getModel());});
		tfSolare.textProperty().addListener(ev -> readCurrency());
		tfLunare.textProperty().addListener(ev -> readCurrency());
		tfTelare.textProperty().addListener(ev -> readCurrency());
	}
	
	//-------------------------------------------------------------------
	public void refresh() {
		super.refresh();
		ignoreEvent = true;
		tfSolare.setText(String.valueOf(charGen.getModel().getTelare()/10000));
		tfLunare.setText(String.valueOf((charGen.getModel().getTelare()/100)%100));
		tfTelare.setText(String.valueOf(charGen.getModel().getTelare()%100));
		ignoreEvent = false;
	}
	
	//-------------------------------------------------------------------
	private void readCurrency() {
		if (ignoreEvent)
			return;
		try {
			int sol = Integer.parseInt(tfSolare.getText());
			int lun = Integer.parseInt(tfLunare.getText());
			int tel = Integer.parseInt(tfTelare.getText());
			int val = sol*10000 + lun*100 + tel;
			charGen.getModel().setTelare(val);
		} catch (NumberFormatException e) {
			logger.warn("Failed parsing currency",e);
		}
	}

	//-------------------------------------------------------------------
	private void updateHelp(CarriedItem data) {
		if (data!=null) {
			this.setDescriptionHeading(data.getName());
			this.setDescriptionPageRef(data.getItem().getProductNameShort()+" "+data.getItem().getPage());
//			this.setDescriptionText(data.getItem().getHelpText());
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

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.NodeWithCommandBar#getScreenManager()
	 */
	@Override
	public ScreenManager getScreenManager() { 
		return provider.getScreenManager();
	}

}
