/**
 *
 */
package org.prelle.splittermond.chargen.free.jfx;

import java.util.PropertyResourceBundle;

import org.prelle.splimo.CultureLore;
import org.prelle.splimo.Language;
import org.prelle.splimo.Skill;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.free.FreeSelectionGenerator;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * @author prelle
 *
 */
public class FreeSelectionDialog extends VBox implements EventHandler<ActionEvent> {

	private static PropertyResourceBundle res = SpliMoCharGenJFXConstants.UI;

	private FreeSelectionGenerator control;

	private TextField name_tf;
	private ChoiceBox<CultureLore> cultLore_cb;
	private ChoiceBox<Language> language_cb;
	private FreeMastershipPane master;

	private HBox leftToRight;
	private VBox left, right;
	private Button ok, cancel;
	private boolean hasBeenCancelled;

	//-------------------------------------------------------------------
	public FreeSelectionDialog(FreeSelectionGenerator control) {
		super(0);
		this.control = control;

		initBaseLayout();
		initName();

		if (control.canSelectCultureLore()) initCultureLore();
		if (control.canSelectLanguage()) initLanguage();
		if (control.canSelectPowers()) initPowers();
		if (control.canSelectSkills()) initSkills();
		if (control.canSelectMasterships()) initMasterships();
		if (control.canSelectResources()) initResources();

		initButtons();
	}

	//-------------------------------------------------------------------
	private void initBaseLayout() {
		left  = new VBox(0);
		right = new VBox(0);
		Region space = new Region();
		space.setPrefWidth(50);

		leftToRight = new HBox(0);
		leftToRight.getChildren().addAll(left, space, right);
		getChildren().add(leftToRight);

	}

	//-------------------------------------------------------------------
	private void initButtons() {
		TilePane box = new TilePane();
		ok     = new Button(res.getString("button.ok"));
		ok.setMaxWidth(Double.MAX_VALUE);
		cancel = new Button(res.getString("button.cancel"));
		box.getChildren().addAll(ok, cancel);
		box.getStyleClass().add("wizard-buttonbar");
		box.setAlignment(Pos.CENTER);
		box.setHgap(10);

		getChildren().add(box);
//		VBox.setMargin(box, new Insets(5));

		// Interactivity
		ok.setOnAction(this);
		cancel.setOnAction(this);

		ok.setDisable(true);
		hasBeenCancelled = true;
	}

	//-------------------------------------------------------------------
	private void initName() {
		// Heading
		Label text   = new Label(res.getString("freeselect.heading.name"));
		text.setMaxWidth(Double.MAX_VALUE);
		text.getStyleClass().add("wizard-heading");

		// Content
		name_tf = new TextField(control.getName());


		VBox content = new VBox();
		content.getChildren().add(name_tf);
		content.getStyleClass().add("wizard-content");
		content.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(content, Priority.ALWAYS);

		/* Sidebar */
		// Line that reflects remaining points to distribute
		Label spacing = new Label("");
		spacing.getStyleClass().add("wizard-context");
		VBox sidebar = new VBox(15);
		sidebar.setPrefWidth(80);
		sidebar.setMaxWidth(80);
		sidebar.getStyleClass().add("wizard-context");
		sidebar.getChildren().addAll(spacing);

		HBox ltr = new HBox();
		ltr.getChildren().addAll(content, sidebar);

		// Layout
		VBox layout = new VBox(0);
		layout.getChildren().addAll(text, ltr);

		left.getChildren().add(layout);
	}

	//-------------------------------------------------------------------
	private void initCultureLore() {
		// Heading
		Label text   = new Label(res.getString("freeselect.heading.cultlore"));
		text.setMaxWidth(Double.MAX_VALUE);
		text.getStyleClass().add("wizard-heading");

		// Content
		cultLore_cb = new ChoiceBox<CultureLore>(FXCollections.observableArrayList(SplitterMondCore.getCultureLores()));
		cultLore_cb.setConverter(new StringConverter<CultureLore>() {
			public String toString(CultureLore data) { return data.getName(); }
			public CultureLore fromString(String data) { return null; }
		});
		// Set data before listener
		if (control.getCultureLore()!=null)
			cultLore_cb.getSelectionModel().select(control.getCultureLore());

		cultLore_cb.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<CultureLore>() {
			public void changed(ObservableValue<? extends CultureLore> arg0,
					CultureLore old, CultureLore selected) {
				control.selectCultureLore(selected);
				ok.setDisable(!control.isDone());
			}
		});

		VBox content = new VBox();
		content.getChildren().add(cultLore_cb);
		content.getStyleClass().add("wizard-content");
		content.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(content, Priority.ALWAYS);

		/* Sidebar */
		// Line that reflects remaining points to distribute
		Label spacing = new Label("");
		spacing.getStyleClass().add("wizard-context");
		VBox sidebar = new VBox(15);
		sidebar.setPrefWidth(80);
		sidebar.setMaxWidth(80);
		sidebar.getStyleClass().add("wizard-context");
		sidebar.getChildren().addAll(spacing);

		HBox ltr = new HBox();
		ltr.getChildren().addAll(content, sidebar);

		// Layout
		VBox layout = new VBox(0);
		layout.getChildren().addAll(text, ltr);

		left.getChildren().add(layout);

	}

	//-------------------------------------------------------------------
	private void initLanguage() {
		// Heading
		Label text   = new Label(res.getString("freeselect.heading.language"));
		text.setMaxWidth(Double.MAX_VALUE);
		text.getStyleClass().add("wizard-heading");

		// Content
		language_cb = new ChoiceBox<Language>(FXCollections.observableArrayList(SplitterMondCore.getLanguages()));
		language_cb.setConverter(new StringConverter<Language>() {
			public String toString(Language data) { return data.getName(); }
			public Language fromString(String data) { return null; }
		});
		// Set data before listener
		if (control.getLanguage()!=null)
			language_cb.getSelectionModel().select(control.getLanguage());

		language_cb.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Language>() {
			public void changed(ObservableValue<? extends Language> arg0,
					Language old, Language selected) {
				control.selectLanguage(selected);
				ok.setDisable(!control.isDone());
			}
		});


		VBox content = new VBox();
		content.getChildren().add(language_cb);
		content.getStyleClass().add("wizard-content");
		content.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(content, Priority.ALWAYS);

		/* Sidebar */
		// Line that reflects remaining points to distribute
		Label spacing = new Label("");
		spacing.getStyleClass().add("wizard-context");
		VBox sidebar = new VBox(15);
		sidebar.setPrefWidth(80);
		sidebar.setMaxWidth(80);
		sidebar.getStyleClass().add("wizard-context");
		sidebar.getChildren().addAll(spacing);

		HBox ltr = new HBox();
		ltr.getChildren().addAll(content, sidebar);

		// Layout
		VBox layout = new VBox(0);
		layout.getChildren().addAll(text, ltr);

		left.getChildren().add(layout);
	}

	//-------------------------------------------------------------------
	private void initPowers() {
		// Heading
		Label text   = new Label(res.getString("freeselect.heading.power"));
		text.setMaxWidth(Double.MAX_VALUE);
		text.getStyleClass().add("wizard-heading");

		// Layout
		VBox layout = new VBox(0);
		layout.getChildren().addAll(text);
		layout.getChildren().add(new FreePowerPane(control, this));

		left.getChildren().add(layout);
	}

	//-------------------------------------------------------------------
	private void initSkills() {
		// Heading
		Label text   = new Label(res.getString("freeselect.heading.skill"));
		text.setMaxWidth(Double.MAX_VALUE);
		text.getStyleClass().add("wizard-heading");

		VBox layout = new VBox(0);
		layout.getChildren().addAll(text);
		layout.getChildren().add(new FreeSkillPane(control, this));

		right.getChildren().add(layout);
	}

	//-------------------------------------------------------------------
	private void initMasterships() {
		// Heading
		Label text   = new Label(res.getString("freeselect.heading.mastership"));
		text.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(text, Priority.ALWAYS);
		text.getStyleClass().add("wizard-heading");

		master = new FreeMastershipPane(control, this);

		// Layout
		VBox layout = new VBox(0);
		layout.getChildren().addAll(text, master);

		right.getChildren().add(layout);
	}

	//-------------------------------------------------------------------
	private void initResources() {
		// Heading
		Label text   = new Label(res.getString("freeselect.heading.resource"));
		text.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(text           , Priority.ALWAYS);
		text.getStyleClass().add("wizard-heading");

		FreeResourcePane resources = new FreeResourcePane(control, this);

		// Layout
		VBox layout = new VBox(0);
		layout.getChildren().addAll(text, resources);

		left.getChildren().add(layout);

	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.event.EventHandler#handle(javafx.event.Event)
	 */
	@Override
	public void handle(ActionEvent event) {
		if (event.getSource()==ok) {
			hasBeenCancelled = false;
			getScene().getWindow().hide();
			control.setName(name_tf.getText());
		} else
		if (event.getSource()==cancel) {
			hasBeenCancelled = true;
			getScene().getWindow().hide();
		}

	}

	//-------------------------------------------------------------------
	public boolean hasBeenCancelled() {
		return hasBeenCancelled;
	}

	//--------------------------------------------------------------------
	public void updateButtons() {
		if (ok!=null)
			ok.setDisable(!control.isDone());
	}

	//--------------------------------------------------------------------
	public void skillAdded(Skill skill) {
		if (master!=null)
			master.skillAdded(skill);
	}

	//--------------------------------------------------------------------
	public void skillRemoved(Skill skill) {
		if (master!=null)
			master.skillRemoved(skill);
	}

}

