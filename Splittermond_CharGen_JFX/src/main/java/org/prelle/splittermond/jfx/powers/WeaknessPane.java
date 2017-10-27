package org.prelle.splittermond.jfx.powers;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.javafx.FontIcon;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.event.GenerationEventType;

import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class WeaknessPane extends VBox implements GenerationEventListener {
	
	private static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splimo-chargen");

	private SpliMoCharacter model;

	private TextField tfAdd;
	private Button btnAdd;
	private ListView<String> table;
	
	//--------------------------------------------------------------------
	/**
	 * @param withNotes Display a 'Notes' column
	 */
	public WeaknessPane() {
		GenerationEventDispatcher.addListener(this);
		doInit();
		initInteractivity();
	}
	
	//--------------------------------------------------------------------
	public void setData(SpliMoCharacter model) {
		this.model = model;
		
		updateContent();
	}

	//--------------------------------------------------------------------
	private void doInit() {
		tfAdd = new TextField();
		tfAdd.setMinWidth(200);
		btnAdd = new Button(UI.getString("button.add"));
		HBox addLine = new HBox(5);
		addLine.getChildren().addAll(tfAdd,btnAdd);
		HBox.setHgrow(tfAdd, Priority.ALWAYS);
		
		table = new ListView<String>();
        table.setPlaceholder(new Text(UI.getString("placeholder.weakness")));
        table.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
			public ListCell<String> call(ListView<String> param) {
				return new WeaknessCell(WeaknessPane.this);
			}
		});

		// Let table scroll
		ScrollPane scroll = new ScrollPane(table);
		scroll.setFitToWidth(true);
		scroll.setFitToHeight(true);
		
		// Add to layout
		super.getChildren().addAll(addLine, scroll);
		super.setSpacing(5);
		VBox.setVgrow(table, Priority.SOMETIMES);
		
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		// react on button "Add"
		btnAdd.setOnAction(event -> {
			String toAdd = tfAdd.getText();
			if (toAdd!=null && toAdd.length()>0)
				model.addWeakness(toAdd);
			tfAdd.setText(null);
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.WEAKNESS_ADDED, toAdd));
			});
		btnAdd.setDisable(true);
		
		// Enable/Disable button depending on textfield content
		tfAdd.textProperty().addListener( (ov,o,n) -> {
			btnAdd.setDisable( n==null || n.length()==0);
			});
		tfAdd.setOnAction(event -> {
			String toAdd = tfAdd.getText();
			if (toAdd!=null && toAdd.length()>0)
				model.addWeakness(toAdd);
			tfAdd.setText(null);
			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.WEAKNESS_ADDED, toAdd));
			});
	}

	//-------------------------------------------------------------------
	private void updateContent() {
		table.getItems().clear();
		table.getItems().addAll(this.model.getWeaknesses());		
	}

	//-------------------------------------------------------------------
	void removeWeakness(String key) {
		model.removeWeakness(key);
		GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.WEAKNESS_REMOVED, key));
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@SuppressWarnings({ "incomplete-switch"})
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		String ref = null;;
		switch (event.getType()) {
		case WEAKNESS_ADDED:
			ref = (String) event.getKey();
			updateContent();
			break;
		case WEAKNESS_REMOVED:
			ref = (String) event.getKey();
			table.getItems().remove(ref);
			break;
		}
	}

}

class WeaknessCell extends ListCell<String> {
	
	private WeaknessPane pane;
	
	//-------------------------------------------------------------------
	public WeaknessCell(WeaknessPane model) {
		this.pane = model;
	}
	
	//-------------------------------------------------------------------
	public void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);
		
		if (empty) {
			setGraphic(null);
			setText(null);
		} else {
			setText(item);
			FontIcon icon = new FontIcon("\uE0C6",20);
			icon.setUserData(item);
			icon.setOnMouseClicked(event -> {
				pane.removeWeakness( (String)icon.getUserData() );
			});
			setGraphic(icon);
			setText(item);
		}
	}
}