package org.prelle.splimo.chargen.common.jfx;

import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SpellValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterTools;
import org.prelle.splimo.charctrl.SpellController;
import org.prelle.splimo.charctrl.SpellController.FreeSelection;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.jfx.SpliMoCharGenJFXConstants;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class SpellPane extends VBox implements GenerationEventListener {

	private static Logger logger = LogManager.getLogger("splittermond.jfx");

	private static PropertyResourceBundle uiResources = SpliMoCharGenJFXConstants.UI;

	private SpellController control;
	private SpliMoCharacter model;

	private boolean showSpellDetails;
	
	// Selector for available spell schools
	private ChoiceBox<Skill> school;
	// Selector for available spells within a school
	private ChoiceBox<SpellValue> spell;
	// Button to add with a free selection
	private Button addFree;
	// Button to add with exp
	private Button addExp;
	// Shows the remaining free selections
	private Label labFree;
	// Explains the meaning of labFree. With a line wrap
	private Label labExplain;
	
	private TableView<SpellValue> table;
	private TableColumn<SpellValue, Boolean> selectCol;
	private TableColumn<SpellValue, String> nameCol;
	private TableColumn<SpellValue, Number> valueCol;
	private TableColumn<SpellValue, String> schoolCol;
	private TableColumn<SpellValue, Number> lvlCol;
	private TableColumn<SpellValue, String> diffCol;
	private TableColumn<SpellValue, String> foCol;
	private TableColumn<SpellValue, String> cdCol;
	private TableColumn<SpellValue, String> raCol;
	private TableColumn<SpellValue, String> sdCol;
	private TableColumn<SpellValue, String> enhCol;

	//--------------------------------------------------------------------
	public SpellPane(SpellController control, boolean showSpellDetails) {
		this.control = control;
		this.showSpellDetails = showSpellDetails;
		
		initAddLine();
		initTable();
		doValueFactories();
		initInteractivity();
		
		GenerationEventDispatcher.addListener(this);
	}

	//--------------------------------------------------------------------
	private void initAddLine() {
		school     = new ChoiceBox<Skill>();
		spell      = new ChoiceBox<SpellValue>();
		addFree    = new Button(uiResources.getString("button.add.free"));
		addExp     = new Button(uiResources.getString("button.add.exp"));
		labFree    = new Label("0");
		labExplain = new Label(uiResources.getString("label.spells.free"));

		labFree.setStyle("-fx-font-size: 200%");
		labFree.setText(control.getFreeSelections().size()+"");
		school.getItems().addAll(control.getAvailableSpellSchools());

		school.setConverter(new StringConverter<Skill>() {
			public String toString(Skill val) { return val.getName(); }
			public Skill fromString(String string) { return null; }
		});
		spell.setConverter(new StringConverter<SpellValue>() {
			public String toString(SpellValue spell) {
				return spell.getSpell().getName()+" ("+uiResources.getString("label.level.short")+" "+spell.getSpell().getLevelInSchool(spell.getSkill())+")";
			}
			public SpellValue fromString(String arg0) {
				return null;
			}
		});
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(5);
		grid.add(school    , 0, 0);
		grid.add(spell     , 0, 1);
		grid.add(addFree   , 1, 0);
		grid.add(addExp    , 1, 1);
		grid.add(labFree   , 2, 0, 1,2);
		grid.add(labExplain, 3, 0, 1,2);
		
		addExp.setMaxWidth(Double.MAX_VALUE);
		
		getChildren().add(grid);
	}

	//--------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private void initTable() {
		table = new TableView<SpellValue>();
		getChildren().add(table);
		
		table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Text(uiResources.getString("placeholder.spell")));

		selectCol= new TableColumn<SpellValue, Boolean>();
		nameCol  = new TableColumn<SpellValue, String>(uiResources.getString("label.spell"));
		valueCol = new TableColumn<SpellValue, Number>(uiResources.getString("label.value"));
		schoolCol= new TableColumn<SpellValue, String>(uiResources.getString("label.school.short"));
		lvlCol   = new TableColumn<SpellValue, Number>(uiResources.getString("label.level.short"));
		diffCol  = new TableColumn<SpellValue, String>(uiResources.getString("label.diff.short"));
		foCol    = new TableColumn<SpellValue, String>(uiResources.getString("label.focus"));
		cdCol    = new TableColumn<SpellValue, String>(uiResources.getString("label.castdur.short"));
		raCol    = new TableColumn<SpellValue, String>(uiResources.getString("label.range.short"));
		sdCol    = new TableColumn<SpellValue, String>(uiResources.getString("label.spelldur.short"));
		enhCol   = new TableColumn<SpellValue, String>(uiResources.getString("label.enhanced"));
		
		if (showSpellDetails)
		table.getColumns().addAll(
				selectCol,
				nameCol,
				valueCol,
				schoolCol,
				lvlCol,
				diffCol,
				foCol,
				cdCol,
				raCol,
				sdCol,
				enhCol
				);
		else
			table.getColumns().addAll(
					selectCol,
					nameCol,
					valueCol,
					schoolCol,
					lvlCol
					);
		
		lvlCol.setSortable(true);
		nameCol.setSortable(true);
		schoolCol.setSortable(true);		
		
		selectCol.setMinWidth(80);
		nameCol.setMinWidth(150);
		schoolCol.setMaxWidth(150);
	}

	//-------------------------------------------------------------------
	private void doValueFactories() {
		selectCol.setCellValueFactory(new Callback<CellDataFeatures<SpellValue, Boolean>, ObservableValue<Boolean>>() {
			public ObservableValue<Boolean> call(CellDataFeatures<SpellValue, Boolean> p) {
				SpellValue item = p.getValue();
				return new SimpleBooleanProperty(control.canBeDeSelected(item));
			}
		});
		nameCol.setCellValueFactory(new Callback<CellDataFeatures<SpellValue, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<SpellValue, String> p) {
				SpellValue item = p.getValue();
				return new SimpleStringProperty(item.getSpell().getName());
			}
		});
		valueCol.setCellValueFactory(new Callback<CellDataFeatures<SpellValue, Number>, ObservableValue<Number>>() {
			public ObservableValue<Number> call(CellDataFeatures<SpellValue, Number> p) {
				SpellValue item = p.getValue();
				return new SimpleIntegerProperty( model.getSpellValueFor(item) );
			}
		});
		schoolCol.setCellValueFactory(new Callback<CellDataFeatures<SpellValue, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<SpellValue, String> p) {
				SpellValue item = p.getValue();
				return new SimpleStringProperty(item.getSkill().getName());
			}
		});
		lvlCol.setCellValueFactory(new Callback<CellDataFeatures<SpellValue, Number>, ObservableValue<Number>>() {
			public ObservableValue<Number> call(CellDataFeatures<SpellValue, Number> p) {
				SpellValue item = p.getValue();
				return new SimpleIntegerProperty( item.getSpell().getLevelInSchool(item.getSkill()) );
			}
		});
		diffCol.setCellValueFactory(new Callback<CellDataFeatures<SpellValue, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<SpellValue, String> p) {
				SpellValue item = p.getValue();
				return new SimpleStringProperty(item.getSpell().getDifficultyString());
			}
		});
		foCol.setCellValueFactory(new Callback<CellDataFeatures<SpellValue, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<SpellValue, String> p) {
				SpellValue item = p.getValue();
				return new SimpleStringProperty(SplitterTools.getFocusString(item.getSpell().getCost()));
			}
		});
		cdCol.setCellValueFactory(new Callback<CellDataFeatures<SpellValue, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<SpellValue, String> p) {
				SpellValue item = p.getValue();
				return new SimpleStringProperty(item.getSpell().getCastDurationString());
			}
		});
		sdCol.setCellValueFactory(new Callback<CellDataFeatures<SpellValue, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<SpellValue, String> p) {
				SpellValue item = p.getValue();
				return new SimpleStringProperty(item.getSpell().getSpellDurationString());
			}
		});
		raCol.setCellValueFactory(new Callback<CellDataFeatures<SpellValue, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<SpellValue, String> p) {
				SpellValue item = p.getValue();
				return new SimpleStringProperty(item.getSpell().getCastRangeString());
			}
		});
		enhCol.setCellValueFactory(new Callback<CellDataFeatures<SpellValue, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<SpellValue, String> p) {
				SpellValue item = p.getValue();
				return new SimpleStringProperty(item.getSpell().getEnhancementString());
			}
		});
		
		selectCol.setCellFactory(new Callback<TableColumn<SpellValue,Boolean>, TableCell<SpellValue,Boolean>>() {
			public TableCell<SpellValue, Boolean> call(TableColumn<SpellValue, Boolean> col) {
				return new UndoSelectionCell(control);
			}
		});
	}

	//--------------------------------------------------------------------
	private void initInteractivity() {
		addFree.setDisable(true);
		addExp.setDisable(true);
		
		school.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Skill>() {
			public void changed(ObservableValue<? extends Skill> box,
					Skill old, Skill newSelection) {
				logger.debug("Selected school now "+newSelection);
				spell.getItems().clear();
				if (newSelection!=null)
					spell.getItems().addAll(control.getAvailableSpells(newSelection));
			}
		});
		
		/*
		 * Enable or disable buttons when spell is selected
		 */
		spell.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<SpellValue>(){
			public void changed(ObservableValue<? extends SpellValue> box,
					SpellValue oldVal, SpellValue newSelection) {
				addExp.setDisable(true);
				addFree.setDisable(true);
				if (newSelection==null) {
					return;
				}
				
				// Search if possible with free selections
				addFree.setDisable(control.canBeFreeSelected(newSelection)==null);
				// and with exp
				addExp.setDisable(!control.canBeSelected(newSelection));
			}});
		
		// Add free
		addFree.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				SpellValue toAdd = spell.getSelectionModel().getSelectedItem();
				FreeSelection token = control.canBeFreeSelected(toAdd);
				logger.debug("Trying to add for free: "+toAdd+" using "+token);
				if (token!=null)
					control.select(token, toAdd);
			}
		});
		
		// Add with exp
		addExp.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				SpellValue toAdd = spell.getSelectionModel().getSelectedItem();
				logger.debug("Trying to add with exp "+toAdd);
				control.select(toAdd);
			}
		});
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		switch (event.getType()) {
		case ATTRIBUTE_CHANGED:
			// Base values for skills changed
			table.getItems().clear();
			if (model==null) {
				logger.warn("Model not set - ignore event "+event);
				return;
			}
			table.getItems().addAll(model.getSpells());
			break;
		case SKILL_CHANGED:
			if (((Skill)event.getKey()).getType()==SkillType.MAGIC) {
				logger.debug("Skill changed: "+event.getKey());
				updateContent();
			}
			break;
		case SPELL_ADDED:
		case SPELL_REMOVED:
			SpellValue changed = (SpellValue)event.getKey();
			table.getItems().clear();
			table.getItems().addAll(model.getSpells());
			school.getItems().clear();
			school.getItems().addAll(control.getAvailableSpellSchools());
			spell.getItems().clear();
			spell.getItems().addAll(control.getAvailableSpells(changed.getSkill()));
			break;
		case SPELL_FREESELECTION_CHANGED:
			logger.debug("FreeSelections changed");
			labFree.setText( String.valueOf( control.getUnusedFreeSelections().size() ));
			break;
		case EXPERIENCE_CHANGED:
			updateContent();
			break;
		default:
			break;
		}
		
	}
	
	//--------------------------------------------------------------------
	private void updateContent() {
		table.getItems().clear();
		table.getItems().addAll(model.getSpells());
		school.getItems().clear();
		school.getItems().addAll(control.getAvailableSpellSchools());
		
		labFree.setText(String.valueOf(control.getUnusedFreeSelections().size()));
	}
	
	//--------------------------------------------------------------------
	public void setData(SpliMoCharacter model) {
		logger.debug("setData");
		this.model = model;
		updateContent();
	}

	//--------------------------------------------------------------------
	public ReadOnlyObjectProperty<SpellValue> selectedSpellProperty() {
		return table.getSelectionModel().selectedItemProperty();
	}
}

class UndoSelectionCell extends TableCell<SpellValue, Boolean> implements EventHandler<ActionEvent> {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	private Button button;
	private SpellController control;
	private SpellValue spell;
	
	//-------------------------------------------------------------------
	public UndoSelectionCell(SpellController charGen) {
		this.control = charGen;
		button = new Button("Undo");
		button.setOnAction(this);
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
		
		if (item) {
			spell = (SpellValue)this.getTableRow().getItem();
			setGraphic(button);
		} else
			setGraphic(null);
	}

	//--------------------------------------------------------------------
	/**
	 * @see javafx.event.EventHandler#handle(javafx.event.Event)
	 */
	@Override
	public void handle(ActionEvent event) {
		logger.debug("Deselect clicked");
		// trying to deselect
		control.deselect(spell);
	}

}