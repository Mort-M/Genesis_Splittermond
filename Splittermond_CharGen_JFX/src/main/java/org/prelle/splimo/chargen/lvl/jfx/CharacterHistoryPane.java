/**
 * 
 */
package org.prelle.splimo.chargen.lvl.jfx;

import java.text.DateFormat;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splimo.modifications.AttributeModification;
import org.prelle.splimo.modifications.MastershipModification;
import org.prelle.splimo.modifications.PowerModification;
import org.prelle.splimo.modifications.ResourceModification;
import org.prelle.splimo.modifications.SkillModification;
import org.prelle.splimo.modifications.SpellModification;

import de.rpgframework.genericrpg.modification.Modification;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

/**
 * @author prelle
 *
 */
public class CharacterHistoryPane extends HBox {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle res = SpliMoCharGenJFXConstants.UI;

	private final static DateFormat FORMAT = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
	
	private TableView<Modification> table;
	private TableColumn<Modification, String> typeCol;
	private TableColumn<Modification, String> nameCol;
	private TableColumn<Modification, Number> expCol;
	private TableColumn<Modification, String> dateCol;
	
	private Button ok;
	private Button cancel;

	private CharacterEditCallback callback;
	
	//--------------------------------------------------------------------
	public CharacterHistoryPane(CharacterEditCallback callback) {
		this.callback = callback;
		initComponents();
		initLayout();
		initInteractivity();
	}

	//--------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private void initComponents() {
		table = new TableView<>();
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Text(res.getString("placeholder.modification")));
		
		typeCol = new TableColumn<>(res.getString("label.type"));
		nameCol = new TableColumn<>(res.getString("label.name"));
		expCol  = new TableColumn<>(res.getString("label.experience"));
		dateCol = new TableColumn<>(res.getString("label.date"));
		
		typeCol.setPrefWidth(150);
		nameCol.setPrefWidth(250);
		expCol.setPrefWidth(100);
		dateCol.setPrefWidth(200);
		
		typeCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Modification,String>, ObservableValue<String>>() {
			public ObservableValue<String> call(
					CellDataFeatures<Modification, String> p) {
				String type = "";
				Modification mod = p.getValue();
				if (mod instanceof AttributeModification)
					type = res.getString("label.attribute");
				else if (mod instanceof ResourceModification)
					type = res.getString("label.resource");
				else if (mod instanceof MastershipModification)
					type = res.getString("label.mastery");
				else if (mod instanceof SkillModification)
					type = res.getString("label.skill");
				else if (mod instanceof SpellModification)
					type = res.getString("label.spell");
				else if (mod instanceof PowerModification)
					type = res.getString("label.power");
				return new SimpleStringProperty(type);
			}
		});
		nameCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Modification,String>, ObservableValue<String>>() {
			public ObservableValue<String> call(
					CellDataFeatures<Modification, String> p) {
				
				return new SimpleStringProperty(p.getValue().toString());
			}
		});
		expCol.setCellValueFactory(new PropertyValueFactory<Modification,Number>("expCost"));
		dateCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Modification,String>, ObservableValue<String>>() {
			public ObservableValue<String> call(
					CellDataFeatures<Modification, String> p) {
				return new SimpleStringProperty(FORMAT.format(p.getValue().getDate()));
			}
		});
		
		
		table.getColumns().addAll(typeCol, nameCol, expCol, dateCol);

		ok     = new Button(res.getString("button.apply"));
		cancel = new Button(res.getString("button.cancel"));
	}

	//--------------------------------------------------------------------
	private void initLayout() {
		VBox right = new VBox();
		right.getStyleClass().add("wizard-buttonbar");

		HBox buttons = new HBox(8);
		buttons.setMaxWidth(Double.MAX_VALUE);
		buttons.getChildren().add(ok);
		buttons.getChildren().add(cancel);

		right.setAlignment(Pos.BOTTOM_CENTER);
		right.getChildren().add(buttons);
//		Region spacing = new Region();
//		spacing.setMaxWidth(Double.MAX_VALUE);
//		HBox.setHgrow(spacing, Priority.ALWAYS);
		
		Label heading = new Label(res.getString("label.history.title"));
		heading.setMaxWidth(Double.MAX_VALUE);
		heading.setAlignment(Pos.CENTER);
		heading.getStyleClass().add("wizard-heading");
		
		VBox left = new VBox(5);
		left.getChildren().addAll(heading, table);
		left.setMaxWidth(Double.MAX_VALUE);

		table.setMaxWidth(Double.MAX_VALUE);
		
		getChildren().addAll(left, right);
	}

	//-------------------------------------------------------------------
	/*
	 * Interactivity
	 */
	private void initInteractivity() {
		ok.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				// Close dialog
				CharacterHistoryPane.this.getScene().getWindow().hide();
				CharacterHistoryPane.this.callback.dialogClosed(false);
			}
		});
		cancel.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				// Close dialog
				CharacterHistoryPane.this.getScene().getWindow().hide();
				CharacterHistoryPane.this.callback.dialogClosed(true);
			}
		});

	}
	
	//--------------------------------------------------------------------
	public void setData(SpliMoCharacter model) {
		logger.info("setData("+model.getHistory()+")");
		table.getItems().setAll(model.getHistory());
	}
	
}
