package org.prelle.splittermond.jfx.languages;

import java.util.Collections;
import java.util.List;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Language;
import org.prelle.splimo.LanguageReference;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.LanguageController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.jfx.SpliMoCharGenJFXConstants;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class LanguagePane extends VBox implements GenerationEventListener, EventHandler<ActionEvent> {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle uiLanguages = SpliMoCharGenJFXConstants.UI;

	private LanguageController control;
	private SpliMoCharacter model;

	private ChoiceBox<Language> addChoice;
	private Button add;
	private TableView<LanguageReference> table;

	private TableColumn<LanguageReference, String> nameCol;
	private TableColumn<LanguageReference, Boolean> valueCol;

	//--------------------------------------------------------------------
	/**
	 * @param withNotes Display a 'Notes' column
	 */
	public LanguagePane(LanguageController ctrl) {
		if (ctrl==null)
			throw new NullPointerException("May not be null");
		this.control = ctrl;

		GenerationEventDispatcher.addListener(this);
		doInit();
		doValueFactories();
		initInteractivity();
		
		add.setDisable(true);
	}

	//--------------------------------------------------------------------
	public void setData(SpliMoCharacter model) {
		this.model = model;

		updateContent();
	}

	//--------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private void doInit() {
		addChoice = new ChoiceBox<>();
		addChoice.setPrefWidth(100);
		addChoice.getItems().addAll(SplitterMondCore.getLanguages());
		addChoice.setConverter(new StringConverter<Language>(){
			public Language fromString(String from) { return null; }
			public String toString(Language from) { return from.getName();}
			});
		add = new Button(uiLanguages.getString("button.add"));
		HBox addLine = new HBox(5);
		addLine.getChildren().addAll(addChoice,add);
		HBox.setHgrow(addChoice, Priority.ALWAYS);

		table = new TableView<LanguageReference>();
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.setPlaceholder(new Text(uiLanguages.getString("placeholder.language")));

		nameCol = new TableColumn<LanguageReference, String>(uiLanguages.getString("label.languages"));
		valueCol = new TableColumn<LanguageReference, Boolean>();

//		nameCol.setMinWidth(80);
		nameCol.setPrefWidth(120);
		nameCol.setMaxWidth(Double.MAX_VALUE);
		valueCol.setMinWidth(40);
		valueCol.setMaxWidth(60);
		table.getColumns().addAll(nameCol, valueCol);


		// Add to layout
		super.getChildren().addAll(addLine, table);
		super.setSpacing(5);
		VBox.setVgrow(table, Priority.ALWAYS);
		table.setMaxHeight(Double.MAX_VALUE);
	}

	//-------------------------------------------------------------------
	private void doValueFactories() {
		nameCol.setCellValueFactory(new Callback<CellDataFeatures<LanguageReference, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<LanguageReference, String> p) {
				LanguageReference item = p.getValue();
				if (item==null) return null;
				return new SimpleStringProperty(item.getLanguage().getName());
			}
		});
		valueCol.setCellValueFactory(new Callback<CellDataFeatures<LanguageReference, Boolean>, ObservableValue<Boolean>>() {
			public ObservableValue<Boolean> call(CellDataFeatures<LanguageReference, Boolean> p) {
				return new SimpleBooleanProperty(true);
			}
		});

		valueCol.setCellFactory(new Callback<TableColumn<LanguageReference,Boolean>, TableCell<LanguageReference,Boolean>>() {
			public TableCell<LanguageReference,Boolean> call(TableColumn<LanguageReference,Boolean> p) {
				return new LanguageEditingCell(control);
			}
		});
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		add.setOnAction(this);
		
		addChoice.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Language>() {
			public void changed(ObservableValue<? extends Language> arg0,
					Language arg1, Language newSel) {
				add.setDisable(newSel==null);
			}
		});
	}

	//-------------------------------------------------------------------
	private void updateContent() {
		if (model!=null) {
			table.getItems().clear();
			table.getItems().addAll(model.getLanguages());
			addChoice.getItems().clear();
			if (control!=null)
				addChoice.getItems().addAll(control.getAvailableLanguages());
			else
				addChoice.getItems().addAll(SplitterMondCore.getLanguages());
			add.setDisable(true);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@SuppressWarnings("incomplete-switch")
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		switch (event.getType()) {
		case LANGUAGE_ADDED:
			addChoice.getItems().remove(((LanguageReference)event.getKey()).getLanguage());
			updateContent();
			break;
		case LANGUAGE_REMOVED:
			addChoice.getItems().add(((LanguageReference)event.getKey()).getLanguage());
			Collections.sort(addChoice.getItems());
			updateContent();
			break;
		case EXPERIENCE_CHANGED:
			updateContent();
			break;
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.event.EventHandler#handle(javafx.event.Event)
	 */
	@Override
	public void handle(ActionEvent event) {
		if (event.getSource()==add) {
			Language res = addChoice.getSelectionModel().getSelectedItem();
			logger.info("Select "+res);
			LanguageReference ref = control.select(res);
			if (ref==null) {
				logger.error("Adding "+res+" failed");
			} else
				updateContent();
		}
	}

	//-------------------------------------------------------------------
	public List<LanguageReference> getLanguages() {
		return table.getItems();
	}

}


class LanguageEditingCell extends TableCell<LanguageReference, Boolean> {
	
	private CheckBox checkBox;
	private LanguageController charGen;
	private LanguageReference data;
	
	//-------------------------------------------------------------------
	public LanguageEditingCell(LanguageController charGen) {
		this.charGen = charGen;
		checkBox = new CheckBox();
		checkBox.selectedProperty().addListener(new ChangeListener<Boolean>(){
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean arg1, Boolean arg2) {
				LanguageEditingCell.this.changed(arg0, arg1, arg2);
			}});
		

		setAlignment(Pos.CENTER);
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	protected void updateItem(Boolean item, boolean empty) {
		super.updateItem(item, empty);
		
		if (item==null || empty || getTableRow()==null) {
			this.setGraphic(null);
			return;
		}
		
		data = (LanguageReference) getTableRow().getItem();
		if (data==null)
			return;
		
			checkBox.setSelected((Boolean)item);
			if (charGen!=null)
				checkBox.setVisible(charGen.canBeDeselected(data));
			this.setGraphic(checkBox);
	}

	//-------------------------------------------------------------------
	private void changed(ObservableValue<? extends Boolean> item, Boolean old, Boolean val) {
		if (old==true && val==false) {
			charGen.deselect(data);
		}
	}

}
