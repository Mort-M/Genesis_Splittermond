/**
 * 
 */
package org.prelle.splimo.chargen.jfx;

import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.Spell;
import org.prelle.splimo.SpellValue;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.ItemController;
import org.prelle.splimo.items.CarriedItem;
import org.prelle.splimo.modifications.AttributeModification;
import org.prelle.splimo.modifications.ItemModification;
import org.prelle.splimo.modifications.MastershipModification;
import org.prelle.splimo.modifications.SpellModification;

import com.sun.javafx.scene.control.skin.ContextMenuSkin;

import de.rpgframework.genericrpg.modification.Modification;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;
import jfxtras.scene.control.ListSpinner;

/**
 * @author prelle
 *
 */
public class MagicEffectPane extends VBox implements EventHandler<ActionEvent> {

	private static PropertyResourceBundle uiResources = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splimo-chargen");

	private ItemController control;
	private CarriedItem model;

	private ChoiceBox<Modification> addChoice;
	private Button add;
	private TableView<Modification> table;

	private TableColumn<Modification, String> nameCol;
	private TableColumn<Modification, Object> valueCol;

	private ContextMenu contextMenu;
	private MenuItem remove;

	//-------------------------------------------------------------------
	/**
	 */
	public MagicEffectPane(ItemController control) {
		this.control = control;
		doInit();
		initContextMenu();
		doValueFactories();
		initInteractivity();
	}

	//--------------------------------------------------------------------
	public void setData(CarriedItem model) {
		this.model = model;

		refresh();
	}

	//--------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private void doInit() {
		addChoice = new ChoiceBox<>();
//		addChoice.setPrefWidth(200);
		addChoice.getItems().addAll(control.getAvailableMagicModifications());
		addChoice.setConverter(new StringConverter<Modification>() {
			public String toString(Modification mod) {
				if (mod instanceof SpellModification)
					return uiResources.getString("label.spellstructure");
				return mod.toString();
			}
			public Modification fromString(String val) {
				return null;
			}
		});
		
		add = new Button(uiResources.getString("button.add"));
		HBox addLine = new HBox(5);
		addLine.getChildren().addAll(addChoice,add);
		HBox.setHgrow(addChoice, Priority.ALWAYS);

		table = new TableView<Modification>();
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		table.setPrefHeight(120);
        table.setPlaceholder(new Text(uiResources.getString("placeholder.magiceffect")));

		nameCol = new TableColumn<Modification, String>(uiResources.getString("label.effect"));
		valueCol = new TableColumn<Modification, Object>(uiResources.getString("label.value"));

		nameCol.setMinWidth(100);
		nameCol.setMaxWidth(300);
		valueCol.setMinWidth(80);
		valueCol.setMaxWidth(160);
		table.getColumns().addAll(nameCol, valueCol);

		nameCol.setMaxWidth(Double.MAX_VALUE);


		// Add to layout
		super.getChildren().addAll(addLine, table);
		super.setSpacing(5);
		VBox.setVgrow(table, Priority.SOMETIMES);

	}

	//--------------------------------------------------------------------
	private void initContextMenu() {
		remove  = new MenuItem(uiResources.getString("label.remove"));
		contextMenu = new ContextMenu();
		contextMenu.setSkin(new ContextMenuSkin(contextMenu));
		contextMenu.getSkin().getNode().setEffect(new DropShadow());
		contextMenu.getItems().addAll(remove);		
	}

	//-------------------------------------------------------------------
	private void doValueFactories() {
		nameCol.setCellValueFactory(new Callback<CellDataFeatures<Modification, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<Modification, String> p) {
				Modification item = p.getValue();
				return new SimpleStringProperty(item.toString());
			}
		});
		valueCol.setCellValueFactory(new Callback<CellDataFeatures<Modification, Object>, ObservableValue<Object>>() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public ObservableValue<Object> call(CellDataFeatures<Modification, Object> p) {
				if (p.getValue() instanceof AttributeModification)
					return new SimpleObjectProperty( (Integer)CarriedItem.getModificationSize(p.getValue()) );
				return new SimpleObjectProperty<Object>(p.getValue());
			}
		});

		valueCol.setCellFactory(new Callback<TableColumn<Modification,Object>, TableCell<Modification,Object>>() {
			public TableCell<Modification,Object> call(TableColumn<Modification,Object> p) {
				return new MagicEffectEditingCell(control);
			}
		});
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		add.setOnAction(this);
		add.setDisable(true);
		
		addChoice.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Modification>() {
			public void changed(ObservableValue<? extends Modification> item,
					Modification arg1, Modification newVal) {
				add.setDisable(newVal==null);
			}
		});

		if (contextMenu==null)
			return;

		/*
		 * Split and join
		 */
		table.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				if (event.getClickCount()==1 && event.getButton()==MouseButton.SECONDARY) {
					contextMenu.show(table, event.getScreenX(), event.getScreenY());
				}
			}
		});

		// Remove
		remove.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				control.removeModification(table.getSelectionModel().getSelectedItem());
			}
		});
	}

	//-------------------------------------------------------------------
	void refresh() {
		if (model!=null) {
			table.getItems().clear();
			for (Modification mod : model.getModifications())
				if (!(mod instanceof MastershipModification) && !(mod instanceof ItemModification))
					table.getItems().add(mod);
			addChoice.getItems().clear();
			addChoice.getItems().addAll(control.getAvailableMagicModifications());
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.event.EventHandler#handle(javafx.event.Event)
	 */
	@Override
	public void handle(ActionEvent event) {
		if (event.getSource()==add) {
			Modification mod = addChoice.getSelectionModel().getSelectedItem();
			if (mod instanceof SpellModification) {
				/*
				 * Show popup dialog to query for spell to embed
				 */
				SpellValueDialog dia = new SpellValueDialog(control);
				Scene scene = new Scene(dia);
				scene.getStylesheets().addAll(getScene().getStylesheets());
				Stage stage = new Stage();
				stage.setResizable(false);
				stage.setScene(scene);
				dia.setOnClose();
				stage.showAndWait();
				SpellValue spellVal = dia.getSelection();
				if (spellVal==null)
					return;
				
				((SpellModification)mod).setSpell(spellVal);
				
			}

			control.addModification(mod);
		}
	}

//	//-------------------------------------------------------------------
//	public List<Modification> getModifications() {
//		return table.getItems();
//	}

}

class MagicEffectEditingCell extends TableCell<Modification, Object> {

	private ListSpinner<Integer> box;
	private CheckBox checkBox;
	private ItemController charGen;
	private Modification mod;

	//-------------------------------------------------------------------
	public MagicEffectEditingCell(ItemController control) {
		this.charGen = control;
		checkBox = new CheckBox();
		checkBox.selectedProperty().addListener(new ChangeListener<Boolean>(){
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean arg1, Boolean arg2) {
				MagicEffectEditingCell.this.changed(arg0, arg1, arg2);
			}});
		
		box = new ListSpinner<>(-2, 6, 1);
		box.valueProperty().addListener(new ChangeListener<Integer>() {
			public void changed(ObservableValue<? extends Integer> arg0,
					Integer arg1, Integer arg2) {
				MagicEffectEditingCell.this.changed(arg0, arg1, arg2);
			}
		});
		box.setCyclic(false);

		setAlignment(Pos.CENTER);
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	protected void updateItem(Object item, boolean empty) {
		super.updateItem(item, empty);

		
		if (item==null || empty || getTableRow()==null) {
			this.setGraphic(null);
			return;
		}
		
		mod = (Modification) getTableRow().getItem();
		if (mod==null)
			return;
		
		if (mod instanceof AttributeModification)
			box.valueProperty().set(  ((AttributeModification)mod).getValue() );
		
		if (item instanceof Number)
			this.setGraphic(box);
		else {
			checkBox.setSelected(true);
			checkBox.setVisible(true);
			this.setGraphic(checkBox);
		}
	}

	//-------------------------------------------------------------------
	private void changed(ObservableValue<? extends Integer> item, Integer old, Integer val) {
		AttributeModification data = (AttributeModification)mod;
		if (val>data.getValue() && charGen.canBeIncreased(mod)) {
			charGen.increase(data);
		} else if (val<data.getValue() && charGen.canBeDecreased(data)) {
			charGen.decrease(data);
		} else {
			// Revert back
			box.valueProperty().set(data.getValue());
		}
	}

	//-------------------------------------------------------------------
	private void changed(ObservableValue<? extends Boolean> item, Boolean old, Boolean val) {
		if (old==true && val==false) {
			charGen.removeModification(mod);
		}
	}

}

class SpellValueDialog extends VBox {

	private static PropertyResourceBundle uiResources = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/ui");
	
	private SpellValue selected;
	private ItemController control;

	private ChoiceBox<Skill> school;
	private ChoiceBox<Spell> spell;
	private Button ok, cancel;
	
	//-------------------------------------------------------------------
	public SpellValueDialog(ItemController ctrl) {
		super(10);
		this.setAlignment(Pos.CENTER);
		this.control = ctrl;
		
		Label title = new Label(uiResources.getString("dialog.embedspell.title"));
		title.setAlignment(Pos.CENTER);
		title.setMaxWidth(Double.MAX_VALUE);
		title.getStyleClass().add("wizard-heading");
		title.getStyleClass().add("title");
		
		
		school = new ChoiceBox<Skill>();
		school.getItems().addAll(SplitterMondCore.getSkills(SkillType.MAGIC));
		school.setMaxWidth(Double.MAX_VALUE);
		
		spell = new ChoiceBox<Spell>();
		spell.setMaxWidth(Double.MAX_VALUE);
		spell.setConverter(new StringConverter<Spell>() {
			public Spell fromString(String arg0) {
				return null;
			}
			public String toString(Spell spell) {
				return spell.getName();
			}
		});
		
		TilePane buttonBox = new TilePane();
		buttonBox.setHgap(10);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.getStyleClass().add("wizard-buttonbar");
//		buttonBox.setMaxWidth(Double.MAX_VALUE);
		
		ok = new Button(uiResources.getString("button.ok"));
		ok.setMaxWidth(Double.MAX_VALUE);
		cancel = new Button(uiResources.getString("button.cancel"));
		cancel.setMaxWidth(Double.MAX_VALUE);
		buttonBox.getChildren().addAll(ok, cancel);
		
		getChildren().addAll(title, school, spell, buttonBox);
		
		VBox.setMargin(school, new Insets(3));
		VBox.setMargin(spell , new Insets(5));
		
		/*
		 * Interactivity
		 */
		school.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Skill>() {
			public void changed(ObservableValue<? extends Skill> arg0, Skill oldVal, Skill newVal) {
				List<Spell> foo = control.getEmbeddableSpells(newVal);
				spell.getItems().clear();
				spell.getItems().addAll(foo);
				if (!foo.isEmpty())
					spell.getSelectionModel().select(0);
			}
		});
		spell.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Spell>() {
			public void changed(ObservableValue<? extends Spell> arg0,
					Spell arg1, Spell newSpell) {
				ok.setDisable(false);
				selected = new SpellValue(newSpell, school.getSelectionModel().getSelectedItem());
			}
		});
		ok.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent ev) {
				getScene().getWindow().hide();
			}
		});
		cancel.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent ev) {
				selected = null;
				getScene().getWindow().hide();
			}
		});
		

		school.getSelectionModel().select(0);
	}
	
	//-------------------------------------------------------------------
	void setOnClose() {
		getScene().getWindow().setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent arg0) {
				selected = null;
				getScene().getWindow().hide();
			}
		});
	}
	
	//-------------------------------------------------------------------
	public SpellValue getSelection() {
		return selected;
	}

}