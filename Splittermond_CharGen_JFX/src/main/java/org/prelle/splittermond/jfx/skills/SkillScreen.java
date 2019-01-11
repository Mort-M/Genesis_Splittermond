/**
 * 
 */
package org.prelle.splittermond.jfx.skills;

import java.text.Collator;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.ManagedScreen;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.javafx.skin.ManagedScreenStructuredSkin;
import org.prelle.rpgframework.jfx.AttentionPane;
import org.prelle.splimo.Mastership;
import org.prelle.splimo.MastershipReference;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SkillSpecialization;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.SpellType;
import org.prelle.splimo.SpellValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.charctrl.Generator;
import org.prelle.splimo.charctrl.MastershipController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.fluent.SpliMoCharGenConstants;
import org.prelle.splimo.chargen.jfx.PointsPane;
import org.prelle.splimo.chargen.jfx.ViewMode;
import org.prelle.splittermond.jfx.skills.SkillScreen.ListElemMastership;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

/**
 * @author prelle
 *
 */
public class SkillScreen extends ManagedScreen implements GenerationEventListener, ScreenManagerProvider, SkillPaneCallback {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle UI = SpliMoCharGenConstants.RES;
	
	class ListElemMastership {
		Skill skill;
		Mastership data;
		MastershipReference selected;
		BooleanProperty editable = new SimpleBooleanProperty();
	}

	private CharacterController control;
	private ViewMode mode;
	private SkillType type;
	private SpliMoCharacter model;
	private SkillValue selectedSkill;
	
	private SkillPane skills;
	private ListView<ListElemSpecialization> specializations;
	private ListView<ListElemMastership> masterships;

	private AttentionPane attention;
	
	private PointsPane points;
	private HBox content;
	
	//-------------------------------------------------------------------
	/**
	 */
	public SkillScreen(CharacterController control, ViewMode mode, SkillType type) {
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
		
		skills          = new SkillPane(this, control.getSkillController(), control.getMastershipController(), false, type);
		specializations = new ListView<ListElemSpecialization>();
		masterships     = new ListView<ListElemMastership>();

		specializations.setCellFactory(new Callback<ListView<ListElemSpecialization>, ListCell<ListElemSpecialization>>() {
			public ListCell<ListElemSpecialization> call(ListView<ListElemSpecialization> param) {
				return new SkillSpecListCell(control);
			}
		});

		masterships.setCellFactory(new Callback<ListView<ListElemMastership>, ListCell<ListElemMastership>>() {
			public ListCell<ListElemMastership> call(ListView<ListElemMastership> param) {
				return new SkillMastListCell(control);
			}
		});
		
		/*
		 * Exp & Co.
		 */
		points = new PointsPane(mode);
		if (control.getSkillController() instanceof Generator)
			points.setGenerator((Generator) control.getSkillController());
		else
			System.err.println("\n\n"+control.getClass()+" is not a GENERATOR");
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		points.setStyle("-fx-pref-width: 10em");
		
		VBox focusAttr = new VBox();
		focusAttr.setSpacing(20);
//		focusAttr.getChildren().addAll(focusAttributeName, focusAttributeDescription);
		
		// Skills
//		skills.setStyle("-fx-min-width: 28em; -fx-pref-width: 35.5em;");
		VBox bxSkill = new VBox(20);
		bxSkill.setStyle("-fx-min-width: 24em; -fx-pref-width: 35em;");
		Label lblSkill = new Label(UI.getString("label.skills"));
		ScrollPane scrSkill = new ScrollPane(skills);
		scrSkill.setFitToWidth(true);
//		scrSkill.setFitToHeight(true);
		bxSkill.getChildren().addAll(lblSkill, scrSkill);
		lblSkill.getStyleClass().add("text-subheader");
		bxSkill.getStyleClass().add("content");
		attention       = new AttentionPane(bxSkill, Pos.TOP_RIGHT);
		
		// Specializations
		VBox bxSpecial = new VBox(20);
		bxSpecial.setStyle("-fx-min-width: 18em;-fx-pref-width: 21em;");
		Label lblSpecial = new Label(UI.getString("label.specializations"));
		bxSpecial.getChildren().addAll(lblSpecial, specializations);
		VBox.setVgrow(specializations, Priority.ALWAYS);
		lblSpecial.getStyleClass().add("text-subheader");
		bxSpecial.getStyleClass().add("content");
		
		// Masteries
		VBox bxMaster = new VBox(20);
		bxMaster.setStyle("-fx-min-width: 16em;-fx-pref-width: 19em;");
		Label lblMaster = new Label(UI.getString("label.masteries"));
		bxMaster.getChildren().addAll(lblMaster, masterships);
		VBox.setVgrow(masterships, Priority.ALWAYS);
		lblMaster.getStyleClass().add("text-subheader");
		bxMaster.getStyleClass().add("content");
		
		// Flow
		HBox flow = new HBox(40);
//		flow.setPrefTileWidth(300);
//		flow.setHgap(20);
//		flow.setVgap(20);
		flow.getChildren().addAll(attention, bxSpecial, bxMaster);
		HBox.setMargin(attention  , new Insets(0,0,20,0));
		HBox.setMargin(bxSpecial, new Insets(0,0,20,0));
		HBox.setMargin(bxMaster , new Insets(0,0,20,0));
		
//		ScrollPane scrollFlow = new ScrollPane(flow);
//		scrollFlow.setMaxWidth(Double.MAX_VALUE);
//		scrollFlow.setFitToWidth(false);
//		scrollFlow.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		
		content = new HBox();
		content.setSpacing(20);
		content.getChildren().addAll(points, flow);
		HBox.setMargin(points, new Insets(0,0,20,0));
//		HBox.setHgrow(scrollFlow, Priority.ALWAYS);
		content.getStyleClass().add("text-body");
		
		setContent(content);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		
		skills.selectedSkillProperty().addListener( (ov,o,n) -> {
			if (selectedSkill==n)
				return;
			logger.debug("Selected "+n);
			selectedSkill = n;
			
			refreshSpecializations(n.getSkill());
			refreshMasteries(n.getSkill());
		});
		
		
	}

	//-------------------------------------------------------------------
	private void refreshSpecializations(Skill skill) {
		specializations.getItems().clear();
		
		for (SkillSpecialization spec : skill.getSpecializations()) {
			ListElemSpecialization item = new ListElemSpecialization();
			item.skill = skill;
			item.data  = spec;
			item.level = model.getSkillSpecializationLevel(spec);
			// Count how often the spec. is used by spells
			for (SpellValue spell : model.getSpells()) {
				if (spell.getSkill()!=skill)
					continue;
				for (SpellType type : spell.getSpell().getTypes()) {
					if (spec.getId().equalsIgnoreCase(type.name()))
						item.count++;
				}
				
			}
			specializations.getItems().add(item);
		}
		
		final Collator collat = Collator.getInstance();
		Collections.sort(specializations.getItems(), new Comparator<ListElemSpecialization>() {
			@Override
			public int compare(ListElemSpecialization o1, ListElemSpecialization o2) {				
				return collat.compare(o1.data.getName(), o2.data.getName());
			}
		});
	}

	//-------------------------------------------------------------------
	private void refreshMasteries(Skill skill) {
		masterships.getItems().clear();
		
		for (Mastership spec : skill.getMasterships()) {
			ListElemMastership item = new ListElemMastership();
			item.skill = skill;
			item.data  = spec;
			for (MastershipReference ref : model.getSkillValue(skill).getMasterships()) {
				if (ref.getMastership()==spec) {
					logger.debug("Set mastership "+ref);
					item.selected = ref;
				}
			}
			item.editable.set(control.getMastershipController().isEditable(spec));
			masterships.getItems().add(item);
		}
		
		Collections.sort(masterships.getItems(), new Comparator<ListElemMastership>() {
			@Override
			public int compare(ListElemMastership o1, ListElemMastership o2) {
				return o1.data.compareTo(o2.data);
			}
		});
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
			logger.debug(" changed: "+event.getKey()+"  selected="+selectedSkill);
			if (((Skill)event.getKey())==selectedSkill.getSkill()) {
				refreshMasteries(selectedSkill.getSkill());
				refreshSpecializations(selectedSkill.getSkill());
			}
			break;
		case MASTERSHIP_ADDED:
		case MASTERSHIP_CHANGED:
		case MASTERSHIP_REMOVED:
			logger.debug("rcv "+event);
			logger.debug(" changed: "+event.getKey()+"  selected="+selectedSkill);
			if (((Skill)event.getKey())==selectedSkill.getSkill()) {
				refreshMasteries(selectedSkill.getSkill());
				refreshSpecializations(selectedSkill.getSkill());
			}
			attention.setAttentionFlag(!control.getMastershipController().getToDos().isEmpty());
			attention.setAttentionToolTip(control.getMastershipController().getToDos());
			break;
		case POINTS_LEFT_MASTERSHIPS:
			logger.debug("rcv "+event);
			attention.setAttentionFlag(!control.getMastershipController().getToDos().isEmpty());
			attention.setAttentionToolTip(control.getMastershipController().getToDos());
			break;
		case LEVEL_CHANGED:
			logger.debug("rcv "+event);
			refreshMasteries(selectedSkill.getSkill());
			refreshSpecializations(selectedSkill.getSkill());
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
		skills.setContent(model);
//		skills.getItems().clear();
//		skills.getItems().addAll(model.getSkills(type));
		masterships.getItems().clear();
		specializations.getItems().clear();
//		focusAttributeName.setText(Attribute.CHARISMA.getName());
//		focusAttributeDescription.setText(uiResources.getString("descr.attribute.charisma"));
		
		logger.debug("Call PointsPane.setData");
		points.setData(model);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splittermond.jfx.skills.SkillPaneCallback#showAndWaitMasterships(org.prelle.splimo.Skill)
	 */
	@Override
	public void showAndWaitMasterships(Skill skill) {
		// TODO Auto-generated method stub
		logger.warn("TODO: open mastership dialog");
	}

}

/**
 * @author Stefan
 *
 */
class SkillMastListCell extends  ListCell<ListElemMastership> {
	
	private ToggleButton button;
	private MastershipController control;
	
	//--------------------------------------------------------------------
	public SkillMastListCell(CharacterController ctrl) {
		this.control = ctrl.getMastershipController();
		button = new ToggleButton();
		this.setPrefWidth(Double.MAX_VALUE);
		this.setPrefWidth(0);
		button.prefWidthProperty().bind(this.widthProperty());
		button.setOnAction(event -> clicked(button.selectedProperty().get()));
	}

	//--------------------------------------------------------------------
	private void clicked(boolean state) {
		if (state)
			control.select(this.getItem().data);
		else
			control.deselect(this.getItem().data);
	}
	
	//--------------------------------------------------------------------
	@Override
	public void updateItem(ListElemMastership item, boolean empty) {
		super.updateItem(item, empty);
		
		if (empty) {
			setGraphic(null);
			setText(null);
		} else {
			button.setText(item.data.getName());
			setGraphic(button);
			button.selectedProperty().set(item.selected!=null);
			button.setDisable(!item.editable.get());
		}
	}
}