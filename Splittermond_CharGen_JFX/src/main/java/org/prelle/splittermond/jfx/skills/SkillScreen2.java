/**
 * 
 */
package org.prelle.splittermond.jfx.skills;

import java.util.Arrays;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.ManagedScreen;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.javafx.skin.ManagedScreenStructuredSkin;
import org.prelle.rpgframework.jfx.AttentionPane;
import org.prelle.rpgframework.jfx.ThreeColumnPane;
import org.prelle.splimo.MastershipReference;
import org.prelle.splimo.PointsPane;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.ViewMode;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.charctrl.Generator;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
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
public class SkillScreen2 extends ManagedScreen implements GenerationEventListener, ScreenManagerProvider, SkillPaneCallback {

	private final static Logger logger = Logger.getLogger("splittermond.jfx");
	
	private static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splimo-chargen");

	private CharacterController control;
	private ViewMode mode;
	private SkillType type;
	private SpliMoCharacter model;

	private ListView<SkillValue> lvSkills;
	private VBox bxMasterships;
	private Label lblName;
	private Label lblProduct;
	private Label lblDescr;
	private SkillPane pane;
	
	private AttentionPane attention;
	
	private PointsPane points;
	private HBox content;
	
	//-------------------------------------------------------------------
	/**
	 */
	public SkillScreen2(CharacterController control, ViewMode mode, SkillType type) {
		this.control = control;
		this.mode    = mode;
		this.type    = type;
		
		initComponents();
		initLayout();
		initInteractivity();
		setSkin(new ManagedScreenStructuredSkin(this));
		attention.setAttentionFlag(!control.getMastershipController().getToDos().isEmpty());
		attention.setAttentionToolTip(control.getMastershipController().getToDos());
		GenerationEventDispatcher.addListener(this);
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		getNavigButtons().add(CloseType.BACK);
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
		points = new PointsPane(mode);
		points.setStyle("-fx-pref-width: 9em");
		if (control.getSkillController() instanceof Generator)
			points.setGenerator((Generator) control.getSkillController());
		else
			logger.error("\n\n"+control.getClass()+" is not a GENERATOR");
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		VBox bxDescr = new VBox(lblName, lblProduct, lblDescr);		
		attention       = new AttentionPane(bxMasterships, Pos.TOP_RIGHT);
		
		ThreeColumnPane threeCol = new ThreeColumnPane();
		
		threeCol.setHeadersVisible(true);
		threeCol.setColumn1Header(UI.getString("label.skills"));
		threeCol.setColumn2Header(UI.getString("label.masteries"));
		threeCol.setColumn3Header(UI.getString("label.description"));
		
		threeCol.setColumn1Node(lvSkills);
		threeCol.setColumn2Node(attention);
		threeCol.setColumn3Node(bxDescr);
		
		ScrollPane scroll = new ScrollPane(pane);
		
		content = new HBox();
		content.setSpacing(20);
		content.getChildren().addAll(points, scroll);
		HBox.setMargin(points, new Insets(0,0,20,0));
		HBox.setMargin(scroll, new Insets(0,0,20,0));
		HBox.setHgrow(scroll, Priority.ALWAYS);
		content.getStyleClass().add("text-body");
		
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
			points.refresh();
			break;
		case SKILL_CHANGED:
			logger.debug("rcv "+event.getType()+"   "+Arrays.toString((int[])event.getValue()));
			points.refresh();
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
	/**
	 * @see org.prelle.javafx.ManagedScreen#close(org.prelle.javafx.ManagedScreen.CloseType)
	 */
	@Override
	public boolean close(CloseType type) {
		GenerationEventDispatcher.removeListener(this);
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.ManagedScreen#childClosed(org.prelle.javafx.ManagedScreen, org.prelle.javafx.ManagedScreen.CloseType)
	 */
	@Override
	public void childClosed(ManagedScreen child, CloseType type) {
		// TODO Auto-generated method stub

	}

	//-------------------------------------------------------------------
	public void setData(SpliMoCharacter model) {
		this.model = model;
		setTitle(model.getName()+" / "+type.getName());
		
		List<SkillValue> list = model.getSkills(type);
		lvSkills.getItems().addAll(list);
		
		logger.debug("Call PointsPane.setData");
		points.setData(model);
		pane.setContent(model);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splittermond.jfx.skills.SkillPaneCallback#showAndWaitMasterships(org.prelle.splimo.Skill)
	 */
	@Override
	public void showAndWaitMasterships(Skill skill) {
		logger.debug("open mastership dialog");
		
		MastershipScreen screen = new MastershipScreen(control.getMastershipController(), mode);
		screen.setData(model, model.getSkillValue(skill));
		CloseType closed = (CloseType)manager.showAndWait(screen);
		logger.warn("Closed with "+closed);
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
