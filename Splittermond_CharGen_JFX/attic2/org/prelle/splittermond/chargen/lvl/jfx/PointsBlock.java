/**
 * 
 */
package org.prelle.splittermond.chargen.lvl.jfx;

import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.levelling.CharacterLeveller;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;

import de.rpgframework.genericrpg.modification.Modification;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @author prelle
 *
 */
public class PointsBlock extends VBox implements GenerationEventListener {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle uiResources = SpliMoCharGenJFXConstants.UI;
	
	private Label epFree_l, epInv_l;
	private Label maxAtt_l, maxSkill_l;
	private Label level_l;

	private Label epFree_d, epInv_d;
	private Label maxAtt_d, maxSkill_d;
	private Label level_d;
	private CheckBox unrestricted;
	private Label unrestricted_l;
	
	private Button reward;
	private Button ok;
	private Button cancel;
	private ListView<Modification> modView;
	
	private CharacterLeveller control;
	private SpliMoCharacter     model;
	

	//-------------------------------------------------------------------
	/**
	 */
	public PointsBlock() {
		super(10);
		
		doInit();
		doLayout();
		doInteractivity();
		
		GenerationEventDispatcher.addListener(this);
	}

	//-------------------------------------------------------------------
	private void doInit() {
		epFree_l = new Label(uiResources.getString("label.ep.free"));
		epFree_d = new Label();
		epInv_l  = new Label(uiResources.getString("label.ep.used"));
		epInv_d  = new Label();
		level_l  = new Label(uiResources.getString("label.level"));
		level_d  = new Label();
		
		maxAtt_l = new Label(uiResources.getString("label.maxAttrib"));
		maxAtt_d = new Label();
		maxSkill_l = new Label(uiResources.getString("label.maxSkill"));
		maxSkill_d = new Label();
		
		unrestricted_l = new Label(uiResources.getString("label.unrestrictedLevelling"));
		unrestricted   = new CheckBox();
		
		modView  = new ListView<Modification>();
		ok     = new Button(uiResources.getString("button.apply"));
		cancel = new Button(uiResources.getString("button.cancel"));
		reward = new Button(uiResources.getString("button.addReward"));

		getStyleClass().add("wizard-context");
		epFree_d.getStyleClass().add("wizard-context");
		epFree_l.getStyleClass().add("wizard-context");
		epInv_d.getStyleClass().add("wizard-context");
		epInv_l.getStyleClass().add("wizard-context");
		epFree_d.setStyle("-fx-font-size: 400%");
		maxAtt_l.getStyleClass().add("wizard-context");
		maxAtt_d.getStyleClass().add("wizard-context");
		maxSkill_l.getStyleClass().add("wizard-context");
		maxSkill_d.getStyleClass().add("wizard-context");
		level_l.getStyleClass().add("wizard-context");
		level_d.getStyleClass().add("wizard-context");
		unrestricted_l.getStyleClass().add("wizard-context");
		unrestricted.getStyleClass().add("wizard-context");
		
	}

	//-------------------------------------------------------------------
	private void doLayout() {
		modView.setPrefWidth(250);
		maxSkill_l.setAlignment(Pos.CENTER);
		
		setAlignment(Pos.TOP_CENTER);

		getChildren().addAll(epFree_d, epFree_l);
		
		GridPane form = new GridPane();
		form.add(level_l , 0, 0);
		form.add(level_d , 1, 0);
		form.add(epInv_l , 0, 1);
		form.add(epInv_d , 1, 1);
		form.add(maxAtt_l, 0, 2);
		form.add(maxAtt_d, 1, 2);
		form.add(maxSkill_l, 0, 3);
		form.add(maxSkill_d, 1, 3);
		form.add(unrestricted_l, 0, 4);
		form.add(unrestricted  , 1, 4);
		getChildren().add(form);
		getChildren().add(reward);
		
		HBox buttons = new HBox(8);
		buttons.setMaxWidth(Double.MAX_VALUE);
		buttons.getChildren().add(ok);
		buttons.getChildren().add(cancel);
		
		buttons.setAlignment(Pos.CENTER);
		getChildren().addAll(modView, buttons);
		
		VBox.setVgrow(modView, Priority.ALWAYS);
		
		VBox.setMargin(modView, new Insets(4));
	}

	//-------------------------------------------------------------------
	private void update() {
		level_d.setText(Integer.toString(model.getLevel()));
		epFree_d.setText(Integer.toString(model.getExperienceFree()));
		epInv_d .setText(Integer.toString(model.getExperienceInvested()));
		maxAtt_d.setText("+ "+Integer.toString(control.getMaxAttribute()));
		maxSkill_d.setText(Integer.toString(control.getSkillController().getMaxSkill()));
		level_d.setText(Integer.toString(model.getLevel()));
		
		unrestricted.setSelected(control.isUnrestrictedMode());
	}

	//-------------------------------------------------------------------
	public void setContent(SpliMoCharacter model, CharacterLeveller control) {
		this.model = model;
		this.control = control;
		
		update();
		modView.getItems().addAll(control.getUndoList());
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	@SuppressWarnings("incomplete-switch")
	public void handleGenerationEvent(GenerationEvent event) {
		switch (event.getType()) {
		case EXPERIENCE_CHANGED:
			if (model==null)
				return;
			level_d.setText(Integer.toString(model.getLevel()));
			epFree_d.setText(Integer.toString(model.getExperienceFree()));
			epInv_d .setText(Integer.toString(model.getExperienceInvested()));
			maxAtt_d.setText("+ "+Integer.toString(control.getMaxAttribute()));
			maxSkill_d.setText(Integer.toString(control.getSkillController().getMaxSkill()));
			break;
		case UNDO_LIST_CHANGED:
			modView.getItems().clear();
			if (control!=null)
				modView.getItems().addAll(control.getUndoList());
		}
	}

	//--------------------------------------------------------------------
	public Button getOKButton() {
		return ok;
	}

	//--------------------------------------------------------------------
	public Button getCancelButton() {
		return cancel;
	}

	//--------------------------------------------------------------------
	private void doInteractivity() {
		reward.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				RewardDialog dia = new RewardDialog();
				Scene scene = new Scene(dia);
				scene.getStylesheets().addAll(PointsBlock.this.getScene().getStylesheets());
				Stage stage = new Stage();
				stage.setTitle(uiResources.getString("dialog.reward.title"));
				stage.initModality(Modality.WINDOW_MODAL);
				stage.setScene(scene);
				stage.showAndWait();
				
				if (dia.getResult()!=null) {
					logger.debug("Reward entered");
					model.addReward(dia.getResult());
					update();
					GenerationEventDispatcher.fireEvent(new GenerationEvent(
							GenerationEventType.EXPERIENCE_CHANGED, 
							null,
							new int[]{model.getExperienceFree(), model.getExperienceInvested()}));
				}
			}
		});
		
		unrestricted.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent ev) {
				logger.debug("Set unrestricted mode to "+unrestricted.isSelected());
				control.setUnrestrictedMode(unrestricted.isSelected());
				update();
			}
		});
	}

}
