/**
 * 
 */
package org.prelle.splittermond.jfx.skills;

import java.util.Arrays;
import java.util.List;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.ManagedScreen;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.javafx.fluent.CommandBar;
import org.prelle.rpgframework.jfx.AttentionPane;
import org.prelle.rpgframework.jfx.FreePointsNode;
import org.prelle.rpgframework.jfx.SettingsAndCommandBar;
import org.prelle.splimo.MastershipReference;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splittermond.chargen.fluent.SpliMoCharGenConstants;
import org.prelle.splittermond.jfx.master.MastershipScreen;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

/**
 * @author prelle
 *
 */
public class SkillScreen2 extends ManagedScreen implements GenerationEventListener, SkillPaneCallback {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle UI = SpliMoCharGenConstants.RES;

	private CharacterController control;
	private SkillType type;
	private SpliMoCharacter model;

	private ListView<SkillValue> lvSkills;
	private VBox bxMasterships;
	private Label lblName;
	private Label lblProduct;
	private Label lblDescr;
	private SkillPane pane;
	
	private AttentionPane attention;
	
	private Label lbExpTotal;
	private Label lbExpInvested;
	private FreePointsNode freePoints;
	private Label lbLevel;
	private CommandBar commands;
	private SettingsAndCommandBar firstLine;
	
	private ScreenManagerProvider provider;
	
	//-------------------------------------------------------------------
	/**
	 */
	public SkillScreen2(CharacterController control, ScreenManagerProvider provider, SkillType type) {
		this.control = control;
		this.provider= provider;
		this.type    = type;
		if (provider==null)
			throw new NullPointerException();
		
		initComponents();
		initLayout();
		initInteractivity();
		attention.setAttentionFlag(!control.getMastershipController().getToDos().isEmpty());
		attention.setAttentionToolTip(control.getMastershipController().getToDos());
		GenerationEventDispatcher.addListener(this);
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		setTitle(type.getName());
		
		pane = new SkillPane((SkillPaneCallback)this, control.getSkillController(), control.getMastershipController(), true, type);
		
		lvSkills = new ListView<SkillValue>();
		lvSkills.setCellFactory(new Callback<ListView<SkillValue>, ListCell<SkillValue>>() {
			public ListCell<SkillValue> call(ListView<SkillValue> param) {
				return new SkillValueListCell(control.getSkillController());
			}
		});
		bxMasterships = new VBox();
		bxMasterships.setStyle("-fx-vgap: 0.4em");
		
		lblName = new Label();
		lblName.getStyleClass().add("text-subheader");
		lblProduct = new Label();
		lblDescr = new Label();
		lblDescr.setWrapText(true);
		
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
//		firstLine.setCommandBar(commands);
	}

	//-------------------------------------------------------------------
	private void initLayout() {
//		VBox bxDescr = new VBox(lblName, lblProduct, lblDescr);		
		attention       = new AttentionPane(bxMasterships, Pos.TOP_RIGHT);
		
//		ThreeColumnPane threeCol = new ThreeColumnPane();
//		
//		threeCol.setHeadersVisible(true);
//		threeCol.setColumn1Header(UI.getString("label.skills"));
//		threeCol.setColumn2Header(UI.getString("label.masteries"));
//		threeCol.setColumn3Header(UI.getString("label.description"));
//		
//		threeCol.setColumn1Node(lvSkills);
//		threeCol.setColumn2Node(attention);
//		threeCol.setColumn3Node(bxDescr);
		
		ScrollPane scroll = new ScrollPane(pane);

		HBox flow = new HBox();
		flow.getChildren().addAll(freePoints, scroll);
		flow.setStyle("-fx-spacing: 1em");

		VBox content = new VBox();
		content.setSpacing(20);
		content.getChildren().addAll(firstLine, flow);
		VBox.setVgrow(flow, Priority.ALWAYS);
		VBox.setMargin(flow  , new Insets(0,0,20,0));
		setContent(content);
		content.getStyleClass().add("skill-screen-"+type.name().toLowerCase());
		
		setContent(content);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		
		lvSkills.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> skillSelected(n));
	}

	//-------------------------------------------------------------------
	private void skillSelected(SkillValue sVal) {
		logger.debug("skill selected");
		lblName.setText(null);
//		lblProduct.setText(sVal.ge);
		
		if (sVal!=null) {
			lblName.setText(sVal.getSkill().getName());
			refreshMasteries(sVal.getSkill());
		}
	}

	//-------------------------------------------------------------------
	private void refreshMasteries(Skill skill) {
		bxMasterships.getChildren().clear();
		
		if (model==null)
			return;
		SkillValue sVal = model.getSkillValue(skill);
		for (MastershipReference ref : sVal.getMasterships()) {
			String text = null;
			if (ref.getMastership()!=null)
				text = ref.getMastership().getName();
			else if (ref.getSpecialization()!=null)
				text = ref.getSpecialization().getName();
			
			Label lbl = new Label(text);
			bxMasterships.getChildren().add(lbl);
		}
		
//		Collections.sort(masterships.getItems(), new Comparator<ListElemMastership>() {
//			@Override
//			public int compare(ListElemMastership o1, ListElemMastership o2) {
//				return o1.data.compareTo(o2.data);
//			}
//		});
	}

	//-------------------------------------------------------------------
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
		case SKILL_CHANGED:
			logger.debug("rcv "+event.getType()+"   "+Arrays.toString((int[])event.getValue()));
			lbExpInvested.setText(control.getModel().getExperienceInvested()+"");
			lbLevel.setText(control.getModel().getLevel()+"");
			refreshMasteries( (Skill)event.getKey() );

			break;
		case MASTERSHIP_ADDED:
		case MASTERSHIP_CHANGED:
		case MASTERSHIP_REMOVED:
			logger.debug("rcv "+event);
			logger.debug(" changed: "+event.getKey());
			refreshMasteries( (Skill)event.getKey() );
			attention.setAttentionFlag(!control.getMastershipController().getToDos().isEmpty());
			attention.setAttentionToolTip(control.getMastershipController().getToDos());
			break;
		case POINTS_LEFT_MASTERSHIPS:
			logger.debug("rcv "+event);
			attention.setAttentionFlag(!control.getMastershipController().getToDos().isEmpty());
			attention.setAttentionToolTip(control.getMastershipController().getToDos());
			break;
		default:
			break;
		}		
	}

	//-------------------------------------------------------------------
	public void setData(SpliMoCharacter model) {
		this.model = model;
		setTitle(model.getName()+" / "+type.getName());
		
		List<SkillValue> list = model.getSkills(type);
		lvSkills.getItems().addAll(list);
		
		logger.debug("Call PointsPane.setData");
		lbExpInvested.setText(control.getModel().getExperienceInvested()+"");
		lbLevel.setText(control.getModel().getLevel()+"");
		pane.setContent(model);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splittermond.jfx.skills.SkillPaneCallback#showAndWaitMasterships(org.prelle.splimo.Skill)
	 */
	@Override
	public void showAndWaitMasterships(Skill skill) {
		logger.debug("open mastership dialog");
		
		MastershipScreen screen = new MastershipScreen(control);
		screen.setData(model, model.getSkillValue(skill));
		provider.getScreenManager().show(screen);
//		logger.warn("Closed with "+closed);
	}

}

class SkillValueListCell2 extends ListCell<SkillValue> {
	
	//-------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	public void updateItem(SkillValue item, boolean empty) {
		super.updateItem(item, empty);
	}
}
