/**
 * 
 */
package org.prelle.splittermond.chargen.fluent;

import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.CultureLore;
import org.prelle.splimo.CultureLoreReference;
import org.prelle.splimo.Language;
import org.prelle.splimo.LanguageReference;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * @author prelle
 *
 */
public class CultureAndLanguagePane extends HBox implements GenerationEventListener {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle RES = SpliMoCharGenJFXConstants.UI;

	private ObjectProperty<CultureLoreReference> selectedCultItemProperty;
	private ObjectProperty<CultureLore> selectedAvailCultProperty;
	private ObjectProperty<LanguageReference> selectedLangItemProperty;
	private ObjectProperty<Language> selectedAvailLangProperty;
	private CharacterController charGen;
	
	private CultureLoreReferenceListControl cultPane;
	private LanguageReferenceListControl    langPane;
	
	private ChoiceBox<CultureLore> cbAvailableCultureLores;
	private ChoiceBox<Language> cbAvailableLanguages;
	private Button btnAddCultureLore;
	private Button btnAddLanguage;

	//-------------------------------------------------------------------
	public CultureAndLanguagePane(CharacterController control) {
		this.charGen = control;
		initComponents();
		initLayout();
		initInteractivity();
		
		GenerationEventDispatcher.addListener(this);
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		selectedAvailCultProperty = new SimpleObjectProperty<>();
		selectedAvailLangProperty = new SimpleObjectProperty<>();
		
		cultPane = new CultureLoreReferenceListControl(charGen);
		langPane = new LanguageReferenceListControl(charGen);
		selectedCultItemProperty = cultPane.selectedItemProperty();
		selectedLangItemProperty = langPane.selectedItemProperty();
		langPane = new LanguageReferenceListControl(charGen);
		
		cbAvailableCultureLores = new ChoiceBox<>(FXCollections.observableArrayList(charGen.getCultureLoreController().getAvailableCultureLores()));
		cbAvailableCultureLores.setConverter(new StringConverter<CultureLore>() {
			public String toString(CultureLore value) { return value.getName(); }
			public CultureLore fromString(String string) { return null; }
		});
		cbAvailableLanguages = new ChoiceBox<>(FXCollections.observableArrayList(charGen.getLanguageController().getAvailableLanguages()));
		cbAvailableLanguages.setConverter(new StringConverter<Language>() {
			public String toString(Language value) { return value.getName(); }
			public Language fromString(String string) { return null; }
		});
		btnAddCultureLore    = new Button(RES.getString("button.add"));
		btnAddLanguage = new Button(RES.getString("button.add"));
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		cbAvailableCultureLores.setMaxWidth(Double.MAX_VALUE);
		cbAvailableLanguages.setMaxWidth(Double.MAX_VALUE);
		HBox lineCultureLore = new HBox(cbAvailableCultureLores, btnAddCultureLore);
		HBox lineWeakn = new HBox(cbAvailableLanguages, btnAddLanguage);
		lineCultureLore.setStyle("-fx-spacing: 0.5em");
		lineWeakn.setStyle("-fx-spacing: 0.5em");
		HBox.setHgrow(cbAvailableCultureLores, Priority.ALWAYS);
		HBox.setHgrow(cbAvailableLanguages, Priority.ALWAYS);
		
		VBox bxCultureLore = new VBox(lineCultureLore, cultPane);
		VBox bxLanguage = new VBox(lineWeakn, langPane);
		bxCultureLore.setMaxWidth(Double.MAX_VALUE);
		bxCultureLore.setStyle("-fx-spacing: 0.5em");
		bxLanguage.setStyle("-fx-spacing: 0.5em");
		HBox.setHgrow(bxCultureLore, Priority.ALWAYS);
		HBox.setHgrow(bxLanguage, Priority.ALWAYS);
		
		getChildren().addAll(bxCultureLore, bxLanguage);
		setStyle("-fx-spacing: 0.5em;m");
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		cbAvailableCultureLores.valueProperty().addListener( (ov,o,n) -> {
			btnAddCultureLore.setDisable(!charGen.getCultureLoreController().canBeSelected(n));
			selectedAvailCultProperty.set(n);
		});
		btnAddCultureLore.setOnAction( ev -> {
			logger.info("cbAvailableCult = "+cbAvailableCultureLores.getValue());
			charGen.getCultureLoreController().select(cbAvailableCultureLores.getValue());
			cbAvailableCultureLores.getItems().clear();
			cbAvailableCultureLores.getItems().addAll(charGen.getCultureLoreController().getAvailableCultureLores());
			});
		btnAddLanguage.setOnAction( ev -> {
			charGen.getLanguageController().select(cbAvailableLanguages.getValue());
			cbAvailableLanguages.getItems().clear();
			cbAvailableLanguages.getItems().addAll(charGen.getLanguageController().getAvailableLanguages());
		});
	}

	//-------------------------------------------------------------------
	public ObjectProperty<CultureLoreReference> selectedCultureItemProperty() { return selectedCultItemProperty; }
	public CultureLoreReference getSelectedCultureItem() { return selectedCultItemProperty.get(); }
	public void setSelectedCultureItem(CultureLoreReference value) { selectedCultItemProperty.set(value); }

	//-------------------------------------------------------------------
	public ObjectProperty<CultureLore> selectedAvailProperty() { return selectedAvailCultProperty; }
	public CultureLore getSelectedAvail() { return selectedAvailCultProperty.get(); }

	//-------------------------------------------------------------------
	public ObjectProperty<LanguageReference> selectedLanguageItemProperty() { return selectedLangItemProperty; }
	public LanguageReference getSelectedLanguageItem() { return selectedLangItemProperty.get(); }
	public void setSelectedLanguageItem(LanguageReference value) { selectedLangItemProperty.set(value); }

	//-------------------------------------------------------------------
	public ObjectProperty<Language> selectedLanguageAvailProperty() { return selectedAvailLangProperty; }
	public Language getSelectedLanguageAvail() { return selectedAvailLangProperty.get(); }

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		switch (event.getType()) {
		case CULTURELORE_AVAILABLE_CHANGED:
			logger.debug("RCV "+event);
			cbAvailableCultureLores.getItems().clear();
			cbAvailableCultureLores.getItems().addAll(charGen.getCultureLoreController().getAvailableCultureLores());
			break;
//		case LANUAGE_AVAILABLE_CHANGED:
//			logger.debug("RCV "+event);
//			cbAvailableLanguages.getItems().clear();
//			cbAvailableLanguages.getItems().addAll(charGen.getLanguageController().getAvailableLanguages());
//			break;
		default:
		}
	}

}
