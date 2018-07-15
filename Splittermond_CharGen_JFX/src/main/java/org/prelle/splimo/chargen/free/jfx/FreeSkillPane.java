package org.prelle.splimo.chargen.free.jfx;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.prelle.splimo.Skill;
import org.prelle.splimo.free.FreeSelectionGenerator;
import org.prelle.splimo.modifications.SkillModification;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class FreeSkillPane extends HBox implements EventHandler<ActionEvent> {
	
	private static Logger logger = Logger.getLogger("chargen.ui.skill");
	
	private static PropertyResourceBundle res = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/ui");

	private FreeSelectionGenerator control;
	FreeSelectionDialog parent;

	private TableView<SkillModification> skills;
	private ChoiceBox<Skill> skills_cb;
	private Button newSkill;
	private Label pointsLeft;
	
	//--------------------------------------------------------------------
	/**
	 * @param withNotes Display a 'Notes' column
	 */
	public FreeSkillPane(FreeSelectionGenerator ctrl, FreeSelectionDialog parent) {
		this.control = ctrl;
		this.parent  = parent;

		doInit();
		doValueFactories();
		initInteractivity();

		updateSkills();
	}
	
	//--------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private void doInit() {
		// Add line
		HBox line = new HBox(10);
		newSkill     = new Button(res.getString("button.add"));
		skills_cb = new ChoiceBox<Skill>(FXCollections.observableArrayList(control.getAvailableSkills()));
		skills_cb.setConverter(new StringConverter<Skill>() {
			public String toString(Skill data) { return data.getName(); }
			public Skill fromString(String data) { return null; }
		});
		line.getChildren().addAll(skills_cb, newSkill);
		
		// Content
		skills   = new TableView<SkillModification>();
		TableColumn<SkillModification, String> nameCol = new TableColumn<SkillModification, String>(res.getString("table.skills.name"));
		TableColumn<SkillModification, Number> pointsCol = new TableColumn<SkillModification, Number>(res.getString("label.points"));
		nameCol.setCellValueFactory(new PropertyValueFactory<SkillModification, String>("SkillName"));
		pointsCol.setCellValueFactory(new PropertyValueFactory<SkillModification, Number>("Value"));
		pointsCol.setCellFactory(new Callback<TableColumn<SkillModification,Number>, TableCell<SkillModification,Number>>() {
            public TableCell<SkillModification,Number> call(TableColumn<SkillModification,Number> p) {
                 return new SkillModCell(control, FreeSkillPane.this);
             }
         });
		skills.getColumns().addAll(nameCol, pointsCol);
		skills.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        skills.setPlaceholder(new Text(res.getString("placeholder.skill")));
		nameCol.setMinWidth(200);
		nameCol.setPrefWidth(300);
		pointsCol.setMinWidth(80);
		


		/* Sidebar */
		Label placeholder = new Label(" ");
		placeholder.getStyleClass().add("wizard-heading");

		// Line that reflects remaining points to distribute
		pointsLeft = new Label(String.valueOf(control.getPointsPowers()));
		pointsLeft.setStyle("-fx-font-size: 400%");
		pointsLeft.getStyleClass().add("wizard-context");
		VBox sidebar = new VBox(15);
		sidebar.setPrefWidth(80);
		sidebar.setAlignment(Pos.TOP_CENTER);
		sidebar.getStyleClass().add("wizard-context");
		sidebar.getChildren().addAll(placeholder,pointsLeft);

		VBox topDown = new VBox(5);
		topDown.getChildren().addAll(line, skills);
		topDown.getStyleClass().add("wizard-content");
		VBox.setVgrow(skills, Priority.ALWAYS);

		// Add to layout
		getChildren().addAll(topDown, sidebar);
		
		HBox.setHgrow(topDown, Priority.ALWAYS);
		topDown.setMaxWidth(Double.MAX_VALUE);

	}

	//-------------------------------------------------------------------
	private void doValueFactories() {
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		newSkill.setOnAction(this);
	}

	//-------------------------------------------------------------------
	void updateSkills() {
		skills.getItems().clear();
		skills.getItems().addAll(control.getSelectedSkills());
		skills_cb.setItems(FXCollections.observableArrayList(control.getAvailableSkills()));
		pointsLeft.setText(String.valueOf(control.getPointsSkills()));
		
		parent.updateButtons();
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.event.EventHandler#handle(javafx.event.Event)
	 */
	@Override
	public void handle(ActionEvent event) {
		if (event.getSource()==newSkill) {
			logger.debug("add clicked");
			Skill selection = skills_cb.getSelectionModel().getSelectedItem();
			if (selection!=null) {
				control.set(selection, 1);
				updateSkills();
				parent.skillAdded(selection);
//				int count = control.getPointsMasterships() + control.getMasterships().size();
//				for (int i=0; i<count; i++) {
//					master_skill_cb[i].getItems().add(selection);
//					Collections.sort(master_skill_cb[i].getItems());
//				}
			}

		}
	}

}

class SkillModCell extends TableCell<SkillModification, Number> implements ChangeListener<Integer>{
	
	private static Logger logger = Logger.getLogger("chargen.ui.skill");
	
	private Spinner<Integer> box;
	private FreeSelectionGenerator charGen;
	private boolean setByEvent;
	private SkillModification data;
	private FreeSkillPane parent;
	
	//-------------------------------------------------------------------
	public SkillModCell(FreeSelectionGenerator charGen, FreeSkillPane parent) {
		if (charGen==null)
			throw new NullPointerException();
		this.parent  = parent;
		this.charGen = charGen;
		box = new Spinner<>(0, 
				Math.max(charGen.getMaxSkillValue(), charGen.getMaxMagicSkillValue()),
				1);
		box.valueProperty().addListener(this);
//		box.setCyclic(false);
//		removeScrollHandler(box);
		box.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
//		ListSpinnerSkin<Integer> skin = new ListSpinnerSkin<Integer>(box);
//		skin.setArrowPosition(ArrowPosition.SPLIT);
//		box.setSkin(skin);
		setAlignment(Pos.CENTER);
	}
	
//	//-------------------------------------------------------------------
//	@SuppressWarnings("rawtypes")
//	private void removeScrollHandler(ListSpinner pf) {
//	    try {
//	    	ListSpinnerSkin skin = (ListSpinnerSkin) pf.getSkin();
//			Field f = skin.getClass().getDeclaredField("skinNode");
//			f.setAccessible(true);
//			BorderPane skinNode = (BorderPane) f.get(skin);
//			if (skinNode!=null) {
//				skinNode.setOnScroll(null);
//			}
//		} catch (Exception e) {
//			logger.warn("Error removing scroll handler from spinner: "+e);
//		}
//	}
	
	//-------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	protected void updateItem(Number item, boolean empty) {
		super.updateItem(item, empty);
		if (item==null || empty || super.getTableRow()==null) {
			this.setGraphic(null);
			return;
		}
		
		data = (SkillModification) getTableRow().getItem();
		if (empty || data==null) {
			this.setGraphic(null);
			return;
		}
		box.getValueFactory().setValue(data.getValue());
		
		this.setGraphic(box);
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.beans.value.ChangeListener#changed(javafx.beans.value.ObservableValue, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void changed(ObservableValue<? extends Integer> item, Integer old, Integer val) {
		if (setByEvent) {
			setByEvent = false;
			return;
		}
		setByEvent = false;
		charGen.set(data.getSkill(), val);
		if (val==0)
			parent.parent.skillRemoved(data.getSkill());
		parent.updateSkills();
		
		// Remove scroll handler, if present
// 		removeScrollHandler(box);
	}
	
	//-------------------------------------------------------------------
	void setByEvent(int val) {
		setByEvent = true;
		box.getValueFactory().setValue(val);
	}
}
