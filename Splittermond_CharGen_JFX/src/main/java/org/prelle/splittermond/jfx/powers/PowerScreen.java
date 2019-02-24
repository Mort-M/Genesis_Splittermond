/**
 * 
 */
package org.prelle.splittermond.jfx.powers;

import java.util.Arrays;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.ManagedScreen;
import org.prelle.javafx.fluent.CommandBar;
import org.prelle.javafx.skin.ManagedScreenStructuredSkin;
import org.prelle.rpgframework.jfx.FreePointsNode;
import org.prelle.rpgframework.jfx.SettingsAndCommandBar;
import org.prelle.splimo.Power;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splittermond.chargen.fluent.SpliMoCharGenConstants;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/**
 * @author Stefan
 *
 */
public class PowerScreen extends ManagedScreen implements GenerationEventListener {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle UI = SpliMoCharGenConstants.RES;

	private CharacterController control;
	
	private Label lbExpTotal;
	private Label lbExpInvested;
	private FreePointsNode freePoints;
	private Label lbLevel;
	private CommandBar commands;
	private SettingsAndCommandBar firstLine;

	private PowerPane powers;
	private WeaknessPane weaknesses;
	private Label pageRef;
	private Label description;
	private VBox descLayout;
	
	//--------------------------------------------------------------------
	/**
	 */
	public PowerScreen(CharacterController control) {
		this.control = control;
		
		initComponents();
		initLayout();
		initInteractivity();
		setSkin(new ManagedScreenStructuredSkin(this));
		GenerationEventDispatcher.addListener(this);
 	}

	//-------------------------------------------------------------------
	private void initComponents() {
		getNavigButtons().add(CloseType.BACK);
		setTitle(UI.getString("powerscreen.title"));
		
		powers = new PowerPane(control.getPowerController(), true);
		weaknesses = new WeaknessPane();

		description = new Label();
		description.setWrapText(true);
//		description.setContentDisplay(ContentDisplay.BOTTOM);
//		description.setGraphicTextGap(2);
		description.getStyleClass().add("text-body");
		description.setMaxHeight(Double.MAX_VALUE);
		description.setMaxWidth(Double.MAX_VALUE);
		description.setTextAlignment(TextAlignment.JUSTIFY);
		description.setAlignment(Pos.TOP_LEFT);
		description.setStyle("-fx-min-width: 10em");
		pageRef = new Label();
		pageRef.getStyleClass().add("text-body");

		
		powers.getStyleClass().add("content");
		weaknesses.getStyleClass().add("content");

		/*
		 * Exp & Co.
		 */
		freePoints = new FreePointsNode();
		freePoints.setStyle("-fx-max-height: 3em; -fx-max-width: 3em");
		freePoints.setPoints(control.getModel().getExperienceFree());
		freePoints.setName(UI.getString("label.ep.free"));
		Label hdExpTotal    = new Label(SpliMoCharGenConstants.RES.getString("label.ep.total")+": ");
		Label hdExpInvested = new Label(SpliMoCharGenConstants.RES.getString("label.ep.used")+": ");
		Label hdLevel       = new Label(SpliMoCharGenConstants.RES.getString("label.level")+": ");
		lbExpTotal    = new Label("?");
		lbExpInvested = new Label("?");
		lbLevel       = new Label("?");
		lbExpTotal.getStyleClass().add("base");
		lbExpInvested.getStyleClass().add("base");
		lbLevel.getStyleClass().add("base");
		lbExpTotal.setText(String.valueOf(control.getModel().getExperienceInvested()+control.getModel().getExperienceFree()));
		lbExpInvested.setText(control.getModel().getExperienceInvested()+"");
		lbLevel.setText(control.getModel().getLevel()+"");
		
		commands = new CommandBar();
//		commands.getItems().add(new MenuItem("Drucken", new Label("\uD83D\uDDB6")));
		
		HBox expLine = new HBox(5);
		expLine.getChildren().addAll(hdExpTotal, lbExpTotal, hdExpInvested, lbExpInvested, hdLevel, lbLevel);
		HBox.setMargin(hdLevel, new Insets(0,0,0,20));
		expLine.getStyleClass().add("character-document-view-firstline");
		
		firstLine = new SettingsAndCommandBar();
		firstLine.setSettings(expLine);
		firstLine.setCommandBar(commands);
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		
		// Powers
		Label lblPower = new Label(UI.getString("label.powers"));
		powers.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(powers, Priority.ALWAYS);
		lblPower.getStyleClass().add("text-subheader");

		// Resources
		Label lblWeak = new Label(UI.getString("label.weaknesses"));
		weaknesses.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(weaknesses, Priority.ALWAYS);
		lblWeak.getStyleClass().add("text-subheader");

		// Description
		descLayout = new VBox(20);
		descLayout.getChildren().addAll(pageRef, description);
		descLayout.getStyleClass().add("content");
		ScrollPane descScroll = new ScrollPane(descLayout);
		descScroll.setFitToWidth(true);
		descScroll.setMaxHeight(Double.MAX_VALUE);
		descScroll.setMaxWidth(Double.MAX_VALUE);
		
		// Flow; Resources and Powers
		GridPane flow = new GridPane();
		flow.add(lblPower   , 0, 0);
		flow.add(powers     , 0, 1);
		flow.add(lblWeak    , 0, 2);
		flow.add(weaknesses , 0, 3);
		flow.add(descScroll , 1, 1, 1,3);
		flow.setVgap(20);
		flow.setHgap(20);
		ColumnConstraints col1 = new ColumnConstraints();
		ColumnConstraints col2 = new ColumnConstraints();
        col1.setPercentWidth(70);
        col2.setPercentWidth(30);
        flow.getColumnConstraints().addAll(col1, col2);

		HBox flow2 = new HBox();
		flow2.getChildren().addAll(freePoints, flow);
		flow2.setStyle("-fx-spacing: 1em");
		HBox.setHgrow(flow, Priority.ALWAYS);

		VBox content = new VBox();
		content.setSpacing(20);
		content.getChildren().addAll(firstLine, flow2);
		VBox.setVgrow(flow2, Priority.ALWAYS);
		VBox.setMargin(flow2  , new Insets(0,0,20,0));
		setContent(content);
	}

	//-------------------------------------------------------------------
	private void updateDescription(Power value) {
		if (value==null) {
			description.setText(null);
			pageRef.setText(null); 
		} else {
			String ref = "'"+value.getProductName()+"' "+value.getPage();
			pageRef.setText(ref);
			description.setText(value.getHelpText());
		}
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		powers.getTable().getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			logger.info("Selected in table "+n);
			if (n!=null) {
				updateDescription(n.getPower());
			}
		});
		powers.getChoiceBox().getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			logger.info("Selected in choice box "+n);
			if (n!=null) {
				updateDescription(n);
			}
		});
			
	}

	//-------------------------------------------------------------------
	public void setData(SpliMoCharacter model) {
		powers.setData(model);
		weaknesses.setData(model);
		
		lbExpInvested.setText(model.getExperienceInvested()+"");
		lbLevel.setText(model.getLevel()+"");
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		switch (event.getType()) {
		case EXPERIENCE_CHANGED:
			logger.debug("rcv "+event.getType()+"   "+Arrays.toString((int[])event.getValue()));
			lbExpTotal.setText(String.valueOf(control.getModel().getExperienceInvested()+control.getModel().getExperienceFree()));
			lbExpInvested.setText(control.getModel().getExperienceInvested()+"");
			lbLevel.setText(control.getModel().getLevel()+"");
			freePoints.setPoints(control.getModel().getExperienceFree());
			break;
		case POINTS_LEFT_POWERS:
			lbExpTotal.setText(String.valueOf(control.getModel().getExperienceInvested()+control.getModel().getExperienceFree()));
			lbExpInvested.setText(control.getModel().getExperienceInvested()+"");
			lbLevel.setText(control.getModel().getLevel()+"");
			freePoints.setPoints(control.getModel().getExperienceFree());
			break;
		default:
			break;
		}		
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.ManagedScreen#close(org.prelle.javafx.CloseType)
	 */
	@Override
	public boolean close(CloseType closeType) {
		GenerationEventDispatcher.removeListener(this);
		GenerationEventDispatcher.removeListener(powers);
		GenerationEventDispatcher.removeListener(weaknesses);
		return true;
	}

}
