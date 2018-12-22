/**
 * 
 */
package org.prelle.splimo.chargen.fluent;

import java.util.PropertyResourceBundle;

import org.apache.log4j.Logger;
import org.prelle.splimo.Power;
import org.prelle.splimo.PowerReference;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.event.GenerationEventType;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * @author prelle
 *
 */
public class PowersAndWeaknessesPane extends HBox implements GenerationEventListener {

	private static Logger logger = Logger.getLogger(SpliMoCharGenConstants.BASE_LOGGER_NAME);

	private static PropertyResourceBundle RES = SpliMoCharGenConstants.RES;

	private ObjectProperty<PowerReference> selectedItemProperty;
	private ObjectProperty<Power> selectedAvailProperty;
	private CharacterController charGen;
	
	private PowerReferenceListControl powerPane;
	private WeaknessListControl    weaknessPane;
	
	private ChoiceBox<Power> cbAvailablePowers;
	private TextField tfWeakness;
	private Button btnAddPower;
	private Button btnAddWeakness;

	//-------------------------------------------------------------------
	public PowersAndWeaknessesPane(CharacterController control) {
		this.charGen = control;
		initComponents();
		initLayout();
		initInteractivity();
		
		GenerationEventDispatcher.addListener(this);
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		selectedAvailProperty = new SimpleObjectProperty<>();
		
		powerPane = new PowerReferenceListControl(charGen);
		selectedItemProperty = powerPane.selectedItemProperty();
		weaknessPane = new WeaknessListControl(charGen);
		
		cbAvailablePowers = new ChoiceBox<>(FXCollections.observableArrayList(charGen.getPowerController().getAvailablePowers()));
		cbAvailablePowers.setConverter(new StringConverter<Power>() {
			public String toString(Power value) { return value.getName(); }
			public Power fromString(String string) { return null; }
		});
		tfWeakness     = new TextField();
		btnAddPower    = new Button(RES.getString("button.add"));
		btnAddWeakness = new Button(RES.getString("button.add"));
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		tfWeakness.setMaxWidth(Double.MAX_VALUE);
		cbAvailablePowers.setMaxWidth(Double.MAX_VALUE);
		HBox linePower = new HBox(cbAvailablePowers, btnAddPower);
		HBox lineWeakn = new HBox(tfWeakness, btnAddWeakness);
		linePower.setStyle("-fx-spacing: 0.5em");
		lineWeakn.setStyle("-fx-spacing: 0.5em");
		HBox.setHgrow(cbAvailablePowers, Priority.ALWAYS);
		HBox.setHgrow(tfWeakness, Priority.ALWAYS);
		
		VBox bxPower = new VBox(linePower, powerPane);
		VBox bxWeakn = new VBox(lineWeakn, weaknessPane);
		bxPower.setMaxWidth(Double.MAX_VALUE);
		bxPower.setStyle("-fx-spacing: 0.5em");
		bxWeakn.setStyle("-fx-spacing: 0.5em");
		HBox.setHgrow(bxPower, Priority.ALWAYS);
		HBox.setHgrow(bxWeakn, Priority.SOMETIMES);
		
		getChildren().addAll(bxPower, bxWeakn);
		setStyle("-fx-spacing: 0.5em");
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		cbAvailablePowers.valueProperty().addListener( (ov,o,n) -> {
			btnAddPower.setDisable(!charGen.getPowerController().canBeSelected(n));
			selectedAvailProperty.set(n);
		});
		btnAddPower.setOnAction( ev -> {
			charGen.getPowerController().select(cbAvailablePowers.getValue());
			cbAvailablePowers.getItems().clear();
			cbAvailablePowers.getItems().addAll(charGen.getPowerController().getAvailablePowers());
			});
		btnAddWeakness.setOnAction( ev -> {
			if (tfWeakness.getText()!=null) {
				logger.info("Add weakness "+tfWeakness.getText());
				charGen.getModel().addWeakness(tfWeakness.getText());
				GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.WEAKNESS_ADDED, tfWeakness.getText()));
			}
			tfWeakness.setText(null);
		});
	}

	//-------------------------------------------------------------------
	public ObjectProperty<PowerReference> selectedItemProperty() { return selectedItemProperty; }
	public PowerReference getSelectedItem() { return selectedItemProperty.get(); }
	public void setSelectedItem(PowerReference value) { selectedItemProperty.set(value); }

	//-------------------------------------------------------------------
	public ObjectProperty<Power> selectedAvailProperty() { return selectedAvailProperty; }
	public Power getSelectedAvail() { return selectedAvailProperty.get(); }

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		switch (event.getType()) {
		case POWER_AVAILABLE_ADDED:
		case POWER_AVAILABLE_REMOVED:
			logger.debug("RCV "+event);
			cbAvailablePowers.getItems().clear();
			cbAvailablePowers.getItems().addAll(charGen.getPowerController().getAvailablePowers());
			break;
		default:
		}
	}

}
