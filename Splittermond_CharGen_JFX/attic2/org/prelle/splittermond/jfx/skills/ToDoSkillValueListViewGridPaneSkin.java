/**
 *
 */
package org.prelle.splittermond.jfx.skills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;

import org.prelle.rpgframework.jfx.AttentionPane;
import org.prelle.splimo.MastershipReference;
import org.prelle.splimo.Skill;
import org.prelle.splimo.SkillSpecialization;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.charctrl.SkillController;
import org.prelle.splittermond.chargen.jfx.SkillField;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXUtil;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * @author prelle
 *
 */
public class ToDoSkillValueListViewGridPaneSkin extends SkinBase<ToDoSkillValueListView> {

	private static PropertyResourceBundle uiResources = SpliMoCharGenJFXConstants.UI;

	private Label headName, headAtt1, headAtt2, headPoints, headMod, headValue, headMastery;
	private Map<SkillValue, Label> names;
	private Map<SkillValue, Label> attrib1;
	private Map<SkillValue, Label> attrib2;
	private Map<SkillValue, Button> masteryButtons;
	private Map<SkillValue, Label> modification;
	private Map<SkillValue, SkillField> distributed;
	private Map<SkillValue, Label> finalValue;
	private Map<SkillValue, Label> mastery;
	private Map<SkillValue, AttentionPane> attentions;

	private GridPane layout;

	//-------------------------------------------------------------------
	/**
	 * @param control
	 */
	public ToDoSkillValueListViewGridPaneSkin(ToDoSkillValueListView control) {
		super(control);
		initComponents();
		initLayout();
		initData();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		names		 = new HashMap<SkillValue, Label>();
		attrib1		 = new HashMap<SkillValue, Label>();
		attrib2		 = new HashMap<SkillValue, Label>();
		modification = new HashMap<SkillValue, Label>();
		distributed  = new HashMap<SkillValue, SkillField>();
		finalValue   = new HashMap<SkillValue, Label>();
		mastery      = new HashMap<SkillValue, Label>();
		masteryButtons = new HashMap<SkillValue,Button>();
		attentions   = new HashMap<SkillValue, AttentionPane>();

		headName   = new Label(uiResources.getString("label.skills"));
		headAtt1   = new Label(uiResources.getString("label.attribute1.short"));
		headAtt2   = new Label(uiResources.getString("label.attribute2.short"));
		headPoints = new Label(uiResources.getString("label.points"));
		headMod    = new Label(uiResources.getString("label.modified.short"));
		headValue  = new Label(uiResources.getString("label.value"));
		headMastery= new Label(uiResources.getString("label.masteries"));

		headName   .getStyleClass().add("table-head");
		headPoints .getStyleClass().add("table-head");
		headAtt1   .getStyleClass().add("table-head");
		headAtt2   .getStyleClass().add("table-head");
		headMod    .getStyleClass().add("table-head");
		headValue  .getStyleClass().add("table-head");
		headMastery.getStyleClass().add("table-head");
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		headName   .setMaxWidth(Double.MAX_VALUE);
		headPoints .setMaxWidth(Double.MAX_VALUE);
		headAtt1   .setMaxWidth(Double.MAX_VALUE);
		headAtt2   .setMaxWidth(Double.MAX_VALUE);
		headMod    .setMaxWidth(Double.MAX_VALUE);
		headValue  .setMaxWidth(Double.MAX_VALUE);
		headMastery.setMaxWidth(Double.MAX_VALUE);
		headMastery.setMinWidth(200);

		layout = new GridPane();
		layout.setVgap(0);
		layout.setHgap(0);
		layout.add(headName   , 0,0);
//		if (withAttr) {
			layout.add(headAtt1   , 1,0);
			layout.add(headAtt2   , 2,0);
//		}
		layout.add(headPoints , 3,0);
		layout.add(headMod    , 4,0);
		layout.add(headValue  , 5,0);
//		if (withMaster) {
			layout.add(headMastery, 6,0);
//		}

		GridPane.setHgrow(headMastery, Priority.ALWAYS);

		headName.setStyle("-fx-pref-width: 15em");
		headAtt1.setStyle("-fx-min-width: 3.3em");
		headAtt2.setStyle("-fx-min-width: 3.3em");
		headPoints.setStyle("-fx-min-width: 9em");
		headMod.setStyle("-fx-min-width: 3em");
		headValue.setStyle("-fx-min-width: 3em");
		headMastery.setStyle("-fx-min-width: 20em");

		getChildren().add(layout);
	}

	//-------------------------------------------------------------------
	private void initData() {
		layout.getChildren().retainAll(headName, headAtt1, headAtt2, headPoints, headMod, headValue, headMastery);

		SpliMoCharacter   model = getSkinnable().getModel();
		SkillController control = getSkinnable().getSkillController();
		int y=0;
		for (SkillValue sVal : model.getSkills(getSkinnable().getSkillType())) {
			Skill skill = sVal.getSkill();
			y++;

			// Field name
			Label lblName    = new Label(sVal.getName());
			lblName.setContentDisplay(ContentDisplay.RIGHT);
			lblName.setGraphic(null);

			// Attributes
			Label att1Val     = new Label();
			Label att2Val     = new Label();
//			if (withAttr) {
				if (skill.getAttribute1()!=null)
					att1Val.setText(skill.getAttribute1().getShortName());
				if (skill.getAttribute2()!=null)
					att2Val.setText(skill.getAttribute2().getShortName());
//			}

			// Invested points
			int points = sVal.getValue();
			SkillField value = new SkillField(String.valueOf(points));
			value.dec.setDisable(!control.canBeDecreased(sVal));
			value.inc.setDisable(!control.canBeIncreased(sVal));
			value.inc.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent event) {
					control.increase(sVal);
				}
			});
			value.dec.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent event) {
					control.decrease(sVal);
				}
			});

			// Modified value
			int modValue  = sVal.getModifiedValue();
			Label modVal= new Label(String.valueOf(sVal.getModifier()));
			if (sVal.getModifier()!=0) {
				modVal.setText(Integer.toString(sVal.getModifier()));
				modVal.setTooltip(new Tooltip(SpliMoCharGenJFXUtil.getModificationTooltip(sVal)));
			} else {
				modVal.setText(null);
				modVal.setTooltip(null);
			}

			// Final value
			if (skill.getAttribute1()!=null) modValue += model.getAttribute(skill.getAttribute1()).getValue();
			if (skill.getAttribute2()!=null) modValue += model.getAttribute(skill.getAttribute2()).getValue();
			Label finVal= new Label(String.valueOf(modValue));

			// Masteries
			StringBuffer buf = new StringBuffer();
			for (SkillSpecialization spec : skill.getSpecializations()) {
				if (sVal.getSpecializationLevel(spec)>0)
					buf.append(spec.getName()+" "+sVal.getSpecializationLevel(spec)+",");
			}

			Iterator<MastershipReference> it = sVal.getMasterships().iterator();
			while (it.hasNext()) {
				MastershipReference ref = it.next();
				// Ignore skill specializations / non-masterships here
				if (ref.getMastership()==null)
					continue;
				buf.append(ref.getMastership().getName());
				if (it.hasNext())
					buf.append(",");
			}
			Label master = new Label(buf.toString());
//			FontIcon edit = new FontIcon();
//			edit.addFontSymbol("\uE104",14);
			Button button = new Button("\uE227\uE104");
//			button.setFont(Font.font(button.getFont().getName(), button.getFont().getSize()*1.5));
//			button.getStyleClass().add("mini-button");
			button.setAlignment(Pos.CENTER_LEFT);

			/*
			 * Style
			 */
			String lineStyle = ((y%2)==0)?"even":"odd";
			lblName.getStyleClass().addAll(lineStyle, "border-left");
//			if (withAttr) {
				att1Val.getStyleClass().add(lineStyle);
				att2Val.getStyleClass().add(lineStyle);
//			}
			value.getStyleClass().add(lineStyle);
			modVal.getStyleClass().add(lineStyle);
			button.setStyle("-fx-font-family: \"Segoe UI Symbol\"; -fx-font-size: 140%; -fx-padding: 0.1em; -fx-background-color: "+(((y%2)==0)?"transparent":"derive(light, -10%)"));
			button.getStyleClass().addAll(lineStyle);
			// Final value
			finVal.getStyleClass().addAll(lineStyle, "border-all");
			finVal.getStyleClass().addAll(lineStyle, "border-right");
//			if (withMaster) {
				master.getStyleClass().addAll(lineStyle, "border-rightleft");
//			}

			/*
			 * Interactivity
			 */
//			lblName.setOnMouseClicked(event -> select(sVal));
//			lblName.setOnMouseEntered(event -> highlightedSkill.set(sVal));
//			att1Val.setOnMouseClicked(event -> select(sVal));
//			att1Val.setOnMouseEntered(event -> highlightedSkill.set(sVal));
//			att2Val.setOnMouseClicked(event -> select(sVal));
//			att2Val.setOnMouseEntered(event -> highlightedSkill.set(sVal));
//			button.setOnAction(this);


			value.setUserData(skill);
			modVal.setUserData(skill);
			finVal.setUserData(skill);


			/**
			 * Attention pane
			 */
			AttentionPane attention = new AttentionPane(lblName, Pos.CENTER_RIGHT);
			List<String> todos = new ArrayList<>();
			todos.addAll(getSkinnable().getMastershipController().getToDos(skill));
			todos.addAll(control.getToDos(skill));
			attention.setAttentionFlag( !todos.isEmpty() );
			attention.setAttentionFlag(true);
			attention.setAttentionToolTip(todos);
//			button.setDisable(  (sVal.getSkill().isGrouped() && sVal.getFocus()==null)  || sVal.getValue()==0 );



			HBox mastBox = new HBox(0);
			HBox.setHgrow(master, Priority.ALWAYS);
			mastBox.getChildren().addAll(button, master);

			finVal.setAlignment(Pos.CENTER_RIGHT);
			modVal.setAlignment(Pos.CENTER);

			lblName.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			att1Val.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			att2Val.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			value.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			modVal.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			finVal.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			master.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			mastBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

			// Layout
			layout.add(attention  , 0, y);
//			if (withAttr) {
				layout.add(att1Val   , 1, y);
				layout.add(att2Val   , 2, y);
//			}
			layout.add(value    , 3, y);
			layout.add(modVal    , 4, y);
			layout.add(finVal    , 5, y);
//			if (withMaster) {
				layout.add(mastBox   , 6, y);
//			}

			// Memorize
			names.put(sVal, lblName);
			attrib1.put(sVal, att1Val);
			attrib2.put(sVal, att2Val);
			mastery.put(sVal, master);
			masteryButtons.put(sVal, button);
			finalValue.put(sVal, finVal);
			distributed.put(sVal, value);
			modification.put(sVal, modVal);

		}
	}

}
