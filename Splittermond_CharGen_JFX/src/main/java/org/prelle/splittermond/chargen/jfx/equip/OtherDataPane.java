package org.prelle.splittermond.chargen.jfx.equip;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.NewItemController;
import org.prelle.splimo.items.CarriedItem;
import org.prelle.splimo.items.Material;
import org.prelle.splimo.items.PersonalizationReference;
import org.prelle.splittermond.chargen.jfx.dialogs.EditCarriedItemDialog;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

/**
 * @author prelle
 *
 */
public class OtherDataPane extends GridPane {

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(EditCarriedItemDialog.class.getName());
	
	private CarriedItem model;
	private NewItemController control;
	
	private ChoiceBox<Material> cbMaterial;
	private ChoiceBox<PersonalizationReference> cbPersonal1;
	private ChoiceBox<PersonalizationReference> cbPersonal2;

	//-------------------------------------------------------------------
	public OtherDataPane() {
		initComponents();
		initLayout();
		initInteractivity();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		cbMaterial = new ChoiceBox<>();
		cbMaterial.getItems().addAll(SplitterMondCore.getMaterials());
		cbMaterial.setConverter(new StringConverter<Material>() {
			public String toString(Material data) {
				return data != null ? data.getName() : "";
			}
			public Material fromString(String data) {
				return null;
			}
		});

		cbPersonal1    = new ChoiceBox<>();
		cbPersonal1.setConverter(new StringConverter<PersonalizationReference>() {
			public String toString(PersonalizationReference data) {
				return data != null ? data.getName() : "";
			}
			public PersonalizationReference fromString(String data) {
				return null;
			}
		});
		cbPersonal2    = new ChoiceBox<>();
		cbPersonal2.setConverter(new StringConverter<PersonalizationReference>() {
			public String toString(PersonalizationReference data) {
				return data != null ? data.getName() : "";
			}
			public PersonalizationReference fromString(String data) {
				return null;
			}
		});
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		Label heaMaterial = new Label(RES.getString("label.material"));
		Label heaPers1 = new Label(RES.getString("label.personalization.first"));
		Label heaPers2 = new Label(RES.getString("label.personalization.second"));

		setVgap(5);
		setHgap(5);
		add(heaMaterial, 0, 0);
		add(cbMaterial , 1, 0);
		add(heaPers1   , 0, 1);
		add(cbPersonal1, 1, 1);
		add(heaPers2   , 0, 2);
		add(cbPersonal2, 1, 2);
		getStyleClass().add("content");
		
		/*
		 * Styles
		 */
		this.getStyleClass().addAll("content","text-body","bordered");
		heaMaterial.getStyleClass().add("base");
		heaPers1.getStyleClass().add("base");
		heaPers2.getStyleClass().add("base");
	}

	//-------------------------------------------------------------------
	private void updatePersonalizations() {
		model.getPersonalizations().clear();
		if (cbPersonal1.getValue()!=null)
			model.getPersonalizations().add(cbPersonal1.getValue());
		if (cbPersonal2.getValue()!=null)
			model.getPersonalizations().add(cbPersonal2.getValue());
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		cbPersonal1.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> updatePersonalizations());
		cbPersonal2.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> updatePersonalizations());
		cbMaterial.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> model.setMaterial(n));
	}

	//-------------------------------------------------------------------
	private void filterCombobox(ChoiceBox<PersonalizationReference>  box, List<PersonalizationReference> allItems, Optional<PersonalizationReference> template) {
		List<PersonalizationReference> actual = box.getItems();
		int pos=0;
		for (int i = 0; i < allItems.size(); i++) {
			PersonalizationReference test = allItems.get(i);
			if (test==null) continue;
			if (test.getPersonalization().getId().equals("other_skillplus") && model.getItem().getSkill()==null) {
				// Without assigned skill, not "Fertigkeitsbonus"
				continue;
			}			
			if (!actual.contains(test)) {
				actual.add(pos, test);
				pos++;
			}
		}
		List<PersonalizationReference> filteredItems = template.map(t -> control.makeCompartibleList(t, allItems)).orElse(allItems);
		if (filteredItems.size() != allItems.size()) {
			for (Iterator<PersonalizationReference> iter = actual.iterator(); iter.hasNext();) {
				if (!filteredItems.contains(iter.next())) {
					iter.remove();
				}
			}
		}
	}

	//-------------------------------------------------------------------
	public void refresh() {
		cbMaterial.getItems().clear();
		cbMaterial.getItems().addAll(control.getAvailableMaterials());
		cbMaterial.getSelectionModel().select(model.getMaterial());

		filterCombobox(cbPersonal1, control.getAvailableFirstPersonalizations(),  Optional.of(model.getPersonalizations())
				                                                                          .filter(l -> l.size() >= 2)
				                                                                          .map(l -> l.get(1)));
		filterCombobox(cbPersonal2, control.getAvailableSecondPersonalizations(), Optional.of(model.getPersonalizations())
																				          .filter(l -> !l.isEmpty())
																				          .map(l -> l.get(0)));
		// Remove "Fertigkeitsbonus" for items without skill
		
		
		
		if  (model.getPersonalizations().isEmpty()) {
			cbPersonal1.getSelectionModel().clearSelection();
		} else {
			cbPersonal1.getSelectionModel().select(model.getPersonalizations().get(0));
		}
		if (model.getPersonalizations().size() < 2) {
			cbPersonal2.getSelectionModel().clearSelection();
		} else {
			cbPersonal2.getSelectionModel().select(model.getPersonalizations().get(1));
		}

		cbPersonal2.setDisable(!control.isSecondPersonalizationAllowed());
	}

	//-------------------------------------------------------------------
	public void setData(CarriedItem model, NewItemController ctrl) {
		this.control = ctrl;
		this.model = model;
		refresh();
	}

}
