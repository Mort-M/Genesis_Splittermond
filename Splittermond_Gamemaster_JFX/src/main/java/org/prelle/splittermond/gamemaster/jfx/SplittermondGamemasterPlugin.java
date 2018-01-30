/**
 * 
 */
package org.prelle.splittermond.gamemaster.jfx;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;

import org.apache.log4j.Logger;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.Skill;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.SplitterTools;
import org.prelle.splimo.items.CarriedItem;
import org.prelle.splimo.items.ItemAttribute;
import org.prelle.splimo.items.ItemLocationType;
import org.prelle.splimo.items.ItemType;

import de.rpgframework.ConfigContainer;
import de.rpgframework.ConfigOption;
import de.rpgframework.RulePlugin;
import de.rpgframework.RulePluginFeatures;
import de.rpgframework.core.CommandBus;
import de.rpgframework.core.CommandBusListener;
import de.rpgframework.core.CommandResult;
import de.rpgframework.core.CommandType;
import de.rpgframework.core.Player;
import de.rpgframework.core.RoleplayingSystem;

/**
 * @author prelle
 *
 */
public class SplittermondGamemasterPlugin implements RulePlugin<SpliMoCharacter>, CommandBusListener {
	
	private final static Logger logger = Logger.getLogger("splittermond.gm");

	private static PropertyResourceBundle GM = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splittermond-gm");
	
	private static List<RulePluginFeatures> FEATURES = new ArrayList<RulePluginFeatures>();
	
	//-------------------------------------------------------------------
	static {
		FEATURES.add(RulePluginFeatures.GAMEMASTER);
	}

	//-------------------------------------------------------------------
	/**
	 */
	public SplittermondGamemasterPlugin() {
		logger.fatal("--------------YEAH--------------------------");
		CommandBus.registerBusCommandListener(this);
	}

	//--------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RulePlugin#getID()
	 */
	@Override
	public String getID() {
		return "GAMEMASTER";
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.core.CommandBusListener#getReadableName()
	 */
	@Override
	public String getReadableName() {
		if (this.getClass().getPackage().getImplementationTitle()!=null)
			return this.getClass().getPackage().getImplementationTitle();
		return "Splittermond Gamemaster Tools";
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RulePlugin#getRules()
	 */
	@Override
	public RoleplayingSystem getRules() {
		return RoleplayingSystem.SPLITTERMOND;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RulePlugin#getRequiredPlugins()
	 */
	@Override
	public Collection<String> getRequiredPlugins() {
		return Arrays.asList("CORE");
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RulePlugin#getSupportedFeatures()
	 */
	@Override
	public Collection<RulePluginFeatures> getSupportedFeatures() {
		return FEATURES;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RulePlugin#attachConfigurationTree(de.rpgframework.ConfigContainer)
	 */
	@Override
	public void attachConfigurationTree(ConfigContainer addBelow) {
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RulePlugin#getConfiguration()
	 */
	@Override
	public List<ConfigOption<?>> getConfiguration() {
		return new ArrayList<>();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.core.CommandBusListener#willProcessCommand(java.lang.Object, de.rpgframework.core.CommandType, java.lang.Object[])
	 */
	@Override
	public boolean willProcessCommand(Object src, CommandType type, Object... values) {
//		logger.info("WILL DO? "+type+" "+Arrays.toString(values));
		switch (type) {
		case RENDER_CHAR_INFO:
			if (values[0]!=RoleplayingSystem.SPLITTERMOND) return false;
			if (values[1]==null || !(values[1] instanceof Player)) return false;
			if (values[2]==null || !(values[2] instanceof SpliMoCharacter)) return false;
//			if (!(values[2] instanceof CharacterHandle)) return false;
//			if (!(values[4] instanceof ScreenManager)) return false;
			return true;
		case RENDER_PARTY_GMINFO:
			if (values[0]!=RoleplayingSystem.SPLITTERMOND) return false;
			if (values[1]==null || !(values[1] instanceof List)) 
				return false;
			if (values[2]==null || !(values[2] instanceof List)) 
				return false;
			logger.info("WILL DO! "+type+" "+Arrays.toString(values));
			return true;
		default:
			return false;
		}
		
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.core.CommandBusListener#handleCommand(java.lang.Object, de.rpgframework.core.CommandType, java.lang.Object[])
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CommandResult handleCommand(Object src, CommandType type, Object... values) {
		if (!willProcessCommand(src, type, values))
			return new CommandResult(type, false, null, false);
		
		logger.info("handle "+type+" "+Arrays.toString(values));
		
		Player player;
		SpliMoCharacter model;
		switch (type) {
		case RENDER_CHAR_INFO:
			try {
				player = (Player)values[1];
				model  = (SpliMoCharacter)values[2];
				
				return new CommandResult(type, render(player, model));
			} catch (Exception e) {
				logger.error("Failed rendering",e);
				return new CommandResult(type, false, e.toString());
			}
		case RENDER_PARTY_GMINFO:
			logger.warn("render spielleiterbrief");
			try {
				List<Player> pList = (List<Player>)values[1];
				List<SpliMoCharacter> models = (List<SpliMoCharacter>)values[2];
				
				return new CommandResult(type, renderGMPartyInfo(pList,models));
			} catch (Exception e) {
				logger.error("Failed rendering",e);
				return new CommandResult(type, false, e.toString());
			}
		default:
			return new CommandResult(type, false);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RulePlugin#init()
	 */
	@Override
	public void init() {
		CommandBus.registerBusCommandListener(this);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RulePlugin#getAboutHTML()
	 */
	@Override
	public InputStream getAboutHTML() {
		return ClassLoader.getSystemResourceAsStream("i18n/splittermond-gmgui.html");
	}

	//-------------------------------------------------------------------
	private Node render(Player player, SpliMoCharacter model) {
		logger.warn("TODO: render "+model);
		
		ImageView iView = new ImageView();
		iView.setFitHeight(110);
		iView.setFitWidth(110);
		if (model.getImage()!=null) {
			iView.setImage(new Image(new ByteArrayInputStream(model.getImage())));
		}		
		iView.setClip(new Circle(55, 55,55, Color.ALICEBLUE));
		
		Random rand = new Random();
		int curLife = rand.nextInt(model.getAttribute(Attribute.LIFE).getValue()*5)+1;
		int curFocus= rand.nextInt(model.getAttribute(Attribute.FOCUS).getValue())+1;
		double lifePerc = ((double)curLife)/(model.getAttribute(Attribute.LIFE).getValue()*5);
		double focusPerc = ((double)curFocus)/(model.getAttribute(Attribute.FOCUS).getValue());
		
		Canvas canvas = new Canvas(150, 150);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.RED);
		gc.fillArc(0, 0, 150, 150, 270, -180*lifePerc, ArcType.ROUND);
		gc.setFill(Color.BLUE);
		gc.fillArc(0, 0, 150, 150, 270, 180*focusPerc, ArcType.ROUND);
		// Draw intervals
		gc.setStroke(Color.BLACK);
		for (int i=1; i<=5; i++) {
			double degree = Math.toRadians(90+i*36);
			gc.strokeLine(75, 75, 75+Math.cos(degree)*75, 75+Math.sin(degree)*75);
		}
		
		gc.setStroke(Color.BLACK);
		gc.strokeOval(0, 0, 150, 150);
		canvas.setClip(new Circle(75, 75,75, Color.ALICEBLUE));
		
		StackPane stack = new StackPane();
		stack.setPrefHeight(150);
		stack.setPrefWidth(150);
		stack.setMaxWidth(150);
		stack.getChildren().add(canvas);
		stack.getChildren().add(iView);
		
		StackPane.setAlignment(canvas , Pos.CENTER);
		StackPane.setAlignment(iView, Pos.CENTER);
		
		return stack;
	}

	//-------------------------------------------------------------------
	private static int getSkillValue(SpliMoCharacter model, Skill skill) {
		int ret = model.getSkillValue(skill).getModifiedValue();
		ret += model.getAttribute(skill.getAttribute1()).getValue();
		ret += model.getAttribute(skill.getAttribute2()).getValue();
		return ret;
	}

	//-------------------------------------------------------------------
	private static int getSkillValue(SpliMoCharacter model, CarriedItem weapon, ItemType type) {
		Skill skill = weapon.getSkill(type);
		int ret = model.getSkillValue(skill).getModifiedValue();
		ret += model.getAttribute(weapon.getAttribute1(type)).getValue();
		ret += model.getAttribute(weapon.getAttribute2(type)).getValue();
		return ret;
	}

	//-------------------------------------------------------------------
	private Label[] makeLabels(GridPane table, int line , HPos... align) {
		int firstSpan = 5-align.length;
		Label[] ret = new Label[align.length];
		ret[0] = new Label();
		table.add(ret[0], 0, line, firstSpan, 1);
		GridPane.setConstraints(ret[0] , 0, line, firstSpan, 1, align[0], VPos.CENTER	);
		
		for (int i=1; i<=align.length-1; i++) {
			ret[i] = new Label(); 
			ret[i].setMaxWidth(Double.MAX_VALUE);
			ret[i].setAlignment(Pos.CENTER);
			table.add(ret[i], firstSpan-1+i, line);
			GridPane.setConstraints(ret[i] , firstSpan-1+i, line, 1, 1, align[i], VPos.CENTER	);
			ret[i].getStyleClass().add("border-left");
			}
		return ret;
	}

	//-------------------------------------------------------------------
	private Node renderGMPartyInfo(List<Player> pList, List<SpliMoCharacter>  models) {
		logger.warn("TODO: render "+models);
		
		Skill ACROBATICS = SplitterMondCore.getSkill("acrobatics");
		Skill ENDURANCE  = SplitterMondCore.getSkill("endurance");
		Skill DETERMINATION= SplitterMondCore.getSkill("determination");
		Skill MELEE      = SplitterMondCore.getSkill("melee");
		
		HBox columns = new HBox();
		columns.setStyle("-fx-spacing: 3em");
		
		for (SpliMoCharacter model : models) {
			if (model==null)
				continue;
			/*
			 * Define a table and columns
			 */
			GridPane table = new GridPane();
			columns.getChildren().add(table);
			table.setStyle("-fx-pref-width: 16em");
			table.getStyleClass().addAll("gmplayerview","border-all");
//			table.setGridLinesVisible(true);
			ColumnConstraints constr1 = new ColumnConstraints();
			ColumnConstraints constr2 = new ColumnConstraints();
			ColumnConstraints constr3 = new ColumnConstraints();
			ColumnConstraints constr4 = new ColumnConstraints();
//			ColumnConstraints constr5 = new ColumnConstraints();
//			constr1.setMaxWidth(80);
//			constr2.setMaxWidth(80);
//			constr3.setMaxWidth(80);
//			constr4.setMaxWidth(80);
//			constr5.setMaxWidth(20);
			constr2.setHalignment(HPos.CENTER);
			constr3.setHalignment(HPos.CENTER);
			constr4.setHalignment(HPos.CENTER);
			constr1.setPercentWidth(27);
			constr2.setPercentWidth(23);
			constr3.setPercentWidth(25);
			constr4.setPercentWidth(25);
			table.getColumnConstraints().addAll(constr1, constr2, constr3);
			
			// Player name
			int index = models.indexOf(model);
			int line=0;
			// Line 1
			Label heaPlayer = new Label(GM.getString("label.player"));
			heaPlayer.getStyleClass().add("text-tertiary-info");
			Label lblPlayer = new Label(pList.get(index).getName());
			lblPlayer.getStyleClass().add("text-small-subheader");
			VBox bxPlayer = new VBox(5);
			bxPlayer.getChildren().addAll(heaPlayer,lblPlayer);
			bxPlayer.getStyleClass().addAll("border-bottom");
			table.add(bxPlayer, 0, 0, 4,1);
			
			// Line 2
			Label heaName = new Label(GM.getString("label.character"));
			heaName.getStyleClass().addAll("text-tertiary-info");
			Label lblName = new Label(model.getName());
			lblName.getStyleClass().addAll("text-small-subheader");
			VBox bxName = new VBox(3);
			bxName.getChildren().addAll(heaName,lblName);
			bxName.getStyleClass().addAll("border-bottom");
			table.add(bxName, 0, 1, 4,1);
			
			// Line3
			Label heaProf = new Label(GM.getString("label.profession"));
			heaProf.getStyleClass().add("text-tertiary-info");
			logger.info("**** "+model.getName()+" // "+heaProf.getStyleClass()+" // "+heaProf.getStylesheets());
			Label lblProf= new Label(model.getEducation().getName());
			lblProf.getStyleClass().add("text-small-subheader");
			VBox bxProf = new VBox(3);
			bxProf.getChildren().addAll(heaProf,lblProf);
			bxProf.getStyleClass().addAll("border-bottom");
			table.add(bxProf, 0, 2, 4,1);
			
			// Line 4-7
			line+=3;
			Attribute[] attCol1 = new Attribute[]{Attribute.CHARISMA, Attribute.AGILITY, Attribute.CONSTITUTION, Attribute.STRENGTH};
			Attribute[] attCol2 = new Attribute[]{Attribute.INTUITION, Attribute.MIND, Attribute.MYSTIC, Attribute.WILLPOWER};
			int i=0;
			for (Attribute attr :attCol1) {
				Label lblAttName = new Label(attr.getShortName());
				Label lblAttVal  = new Label(String.valueOf(model.getAttribute(attr).getValue()));
				lblAttName.getStyleClass().addAll("border-left","text-small-subheader");
				lblAttVal.getStyleClass().addAll("border-right");
				lblAttName.setMaxWidth(Double.MAX_VALUE);
				lblAttVal.setMaxWidth(Double.MAX_VALUE);
				lblAttName.setAlignment(Pos.CENTER);
				lblAttVal.setAlignment(Pos.CENTER);
				table.add(lblAttName, 0  , 3+i);
				table.add(lblAttVal , 1, 3+i);
				GridPane.setFillWidth(lblAttName, true);
				GridPane.setFillWidth(lblAttVal , true);
				GridPane.setConstraints(lblAttName, 0, line+i, 1, 1, HPos.LEFT, VPos.CENTER	, Priority.ALWAYS, Priority.NEVER);
				GridPane.setConstraints(lblAttVal , 1, line+i, 1, 1, HPos.LEFT, VPos.CENTER	, Priority.ALWAYS, Priority.NEVER);
				i++;
			}
			i=0;
			for (Attribute attr :attCol2) {
				Label lblAttName = new Label(attr.getShortName());
				Label lblAttVal  = new Label(String.valueOf(model.getAttribute(attr).getValue()));
				lblAttName.getStyleClass().addAll("border-left","text-small-subheader");
				lblAttVal.getStyleClass().addAll("border-right");
				lblAttName.setMaxWidth(Double.MAX_VALUE);
				lblAttVal.setMaxWidth(Double.MAX_VALUE);
				lblAttName.setAlignment(Pos.CENTER);
				lblAttVal.setAlignment(Pos.CENTER);
				table.add(lblAttName, 2, 3+i);
				table.add(lblAttVal , 3, 3+i);
				GridPane.setConstraints(lblAttName, 2 , line+i, 1, 1, HPos.LEFT, VPos.CENTER	, Priority.ALWAYS, Priority.NEVER);
				GridPane.setConstraints(lblAttVal , 3, line+i, 1, 1, HPos.LEFT, VPos.CENTER	, Priority.ALWAYS, Priority.NEVER);
				i++;
			}
			
			// Combat
			line+=4;
			Label lblHeadingCombat = new Label(GM.getString("heading.combat"));
			lblHeadingCombat.setMaxWidth(Double.MAX_VALUE);
			lblHeadingCombat.setAlignment(Pos.CENTER);
			lblHeadingCombat.getStyleClass().add("heading-cell");
			table.add(lblHeadingCombat, 0, line, 4,1);
			
			// Initiative and Damage reduction
			line++;
			Label[] ini_dr = makeLabels(table, line, HPos.CENTER, HPos.CENTER, HPos.CENTER, HPos.CENTER);
			ini_dr[0].setText(Attribute.INITIATIVE.getShortName());
			ini_dr[0].setStyle("-fx-font-weight: bold");
			ini_dr[1].setText(""+model.getAttribute(Attribute.INITIATIVE).getValue());
			ini_dr[2].setText(Attribute.DAMAGE_REDUCTION.getShortName());
			ini_dr[2].setStyle("-fx-font-weight: bold");
			ini_dr[3].setText(""+SplitterTools.getDamageReductionSum(model));
			
			// health and focus
			line++;
			Label[] hea_foc = makeLabels(table, line, HPos.CENTER, HPos.CENTER, HPos.CENTER, HPos.CENTER);
			hea_foc[0].setText(Attribute.LIFE.getShortName());
			hea_foc[0].setStyle("-fx-font-weight: bold");
			hea_foc[1].setText(""+model.getAttribute(Attribute.LIFE).getValue()*5);
			hea_foc[2].setText(Attribute.FOCUS.getShortName());
			hea_foc[2].setStyle("-fx-font-weight: bold");
			hea_foc[3].setText(""+model.getAttribute(Attribute.FOCUS).getValue());

			// Defenses
			line++;
			Label[] defH = makeLabels(table, line, HPos.CENTER, HPos.CENTER, HPos.CENTER);
			defH[0].setText(GM.getString("gamemaster.combat.type"));
			defH[1].setText(GM.getString("gamemaster.combat.passive"));
			defH[2].setText(GM.getString("gamemaster.combat.active"));
			defH[0].setStyle("-fx-font-weight: bold");
			defH[1].setStyle("-fx-font-weight: bold");
			defH[2].setStyle("-fx-font-weight: bold");
			
			line++;
			Label[] def = makeLabels(table, line, HPos.LEFT, HPos.CENTER, HPos.CENTER);
			def[0].setText(Attribute.DEFENSE.getShortName());
			def[1].setText(""+model.getAttribute(Attribute.DEFENSE).getValue());
			def[2].setText(""+getSkillValue(model, ACROBATICS));
			line++;
			Label[] body = makeLabels(table, line, HPos.LEFT, HPos.CENTER, HPos.CENTER);
			body[0].setText(Attribute.BODYRESIST.getShortName());
			body[1].setText(""+model.getAttribute(Attribute.BODYRESIST).getValue());
			body[2].setText(""+getSkillValue(model, ENDURANCE));
			line++;
			Label[] mind = makeLabels(table, line, HPos.LEFT, HPos.CENTER, HPos.CENTER);
			mind[0].setText(Attribute.MINDRESIST.getShortName());
			mind[1].setText(""+model.getAttribute(Attribute.MINDRESIST).getValue());
			mind[2].setText(""+getSkillValue(model, DETERMINATION));

			// Weapons
			line++;
			Label[] weapons = makeLabels(table, line, HPos.LEFT, HPos.CENTER, HPos.CENTER, HPos.CENTER);
			GridPane.setConstraints(weapons[0], 0, line, 1, 1, HPos.CENTER, VPos.CENTER);
			weapons[0].setText(GM.getString("label.weapon"));
			weapons[1].setText(GM.getString("label.value"));
			weapons[2].setText(ItemAttribute.SPEED.getShortName());
			weapons[3].setText(ItemAttribute.DAMAGE.getShortName());
			weapons[0].setStyle("-fx-font-weight: bold");
			weapons[1].setStyle("-fx-font-weight: bold");
			weapons[2].setStyle("-fx-font-weight: bold");
			weapons[3].setStyle("-fx-font-weight: bold");
			
			line++;
			int tickMalus = SplitterTools.getTickMalusSum(model, true);
			CarriedItem closeWeapon = null;
			CarriedItem rangeWeapon = null;
			for (CarriedItem item : model.getItems()) {
				if (item.getLocation()!=ItemLocationType.BODY)
					continue;
				if (item.isType(ItemType.WEAPON) && closeWeapon==null)
					closeWeapon = item;
				if (item.isType(ItemType.LONG_RANGE_WEAPON) && rangeWeapon==null)
					rangeWeapon = item;
			}
			if (closeWeapon!=null) {
				Label[] weap = makeLabels(table, line, HPos.LEFT, HPos.CENTER, HPos.CENTER, HPos.CENTER);
				weap[0].setText(closeWeapon.getName());
				weap[1].setText(""+getSkillValue(model, closeWeapon, ItemType.WEAPON));
				weap[2].setText((tickMalus+closeWeapon.getSpeed(ItemType.WEAPON))+"T");
				weap[3].setText(SplitterTools.getWeaponDamageString(closeWeapon.getDamage(ItemType.WEAPON)));
			} else
				makeLabels(table, line, HPos.LEFT, HPos.CENTER, HPos.CENTER, HPos.CENTER);
			line++;
			if (rangeWeapon!=null) {
				Label[] weap = makeLabels(table, line, HPos.LEFT, HPos.CENTER, HPos.CENTER, HPos.CENTER);
				weap[0].setText(rangeWeapon.getName());
				weap[1].setText(""+getSkillValue(model, rangeWeapon, ItemType.LONG_RANGE_WEAPON));
				weap[2].setText((tickMalus+rangeWeapon.getSpeed(ItemType.LONG_RANGE_WEAPON))+"T");
				weap[3].setText(SplitterTools.getWeaponDamageString(rangeWeapon.getDamage(ItemType.LONG_RANGE_WEAPON)));
			} else
				makeLabels(table, line, HPos.LEFT, HPos.CENTER, HPos.CENTER, HPos.CENTER);
			line++;
			Label[] weap = makeLabels(table, line, HPos.LEFT, HPos.CENTER, HPos.CENTER, HPos.CENTER);
			weap[0].setText(MELEE.getName());
			weap[1].setText(""+getSkillValue(model, MELEE));
			weap[2].setText("5T");
			weap[3].setText(SplitterTools.getWeaponDamageString(10600));

			// Skills
			line++;
			Label lblHeadingSkills = new Label(GM.getString("heading.skills"));
			lblHeadingSkills.setMaxWidth(Double.MAX_VALUE);
			lblHeadingSkills.setAlignment(Pos.CENTER);
			lblHeadingSkills.getStyleClass().add("heading-cell");
			table.add(lblHeadingSkills, 0, line, 4,1);
			line++;
			List<SkillValue> skills = new ArrayList<>();
			skills.add(model.getSkillValue(SplitterMondCore.getSkill("athletics")));
			skills.add(model.getSkillValue(SplitterMondCore.getSkill("empathy")));
			skills.add(model.getSkillValue(SplitterMondCore.getSkill("perception")));
			skills.add(model.getSkillValue(SplitterMondCore.getSkill("locksntraps")));
			skills.add(model.getSkillValue(SplitterMondCore.getSkill("endurance")));
			skills.add(model.getSkillValue(SplitterMondCore.getSkill("determination")));
			Collections.sort(skills);
			skills.addAll(SplitterTools.getHighestSkills(model));

			i=0;
			for (SkillValue sVal : skills) {
				int finVal = sVal.getModifiedValue()+model.getAttribute(sVal.getSkill().getAttribute1()).getValue()+model.getAttribute(sVal.getSkill().getAttribute2()).getValue();
				Label lblSkillName = new Label(sVal.getSkill().getName());
				Label lblSkillVal  = new Label(String.valueOf(finVal));
				lblSkillVal.setMaxWidth(Double.MAX_VALUE);
				lblSkillVal.setStyle("-fx-pref-width: 3em");
				lblSkillVal.setAlignment(Pos.CENTER);
				lblSkillName.getStyleClass().addAll("border-left");
				lblSkillVal.getStyleClass().addAll("border-right");
				table.add(lblSkillName, 0, line+i,3,1);
				table.add(lblSkillVal , 3, line+i);
				GridPane.setConstraints(lblSkillName, 0, line+i, 3, 1, HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
				i++;
			}
			
			/*
			 * Weaknesses
			 */
			line+=skills.size();
			Label lblHeadingWeaknesses = new Label(GM.getString("heading.weaknesses"));
			lblHeadingWeaknesses.setMaxWidth(Double.MAX_VALUE);
			lblHeadingWeaknesses.setAlignment(Pos.CENTER);
			lblHeadingWeaknesses.getStyleClass().add("heading-cell");
			table.add(lblHeadingWeaknesses, 0, line, 4,1);
			line++;
			
			Label lblWeak = new Label(String.join(", ", model.getWeaknesses()));
			lblWeak.setWrapText(true);
			table.add(lblWeak, 0, line, 4,1);
			
			/*
			 * Powers
			 */
			line++;
			Label lblHeadingPowers = new Label(GM.getString("heading.powers"));
			lblHeadingPowers.setMaxWidth(Double.MAX_VALUE);
			lblHeadingPowers.setAlignment(Pos.CENTER);
			lblHeadingPowers.getStyleClass().add("heading-cell");
			table.add(lblHeadingPowers, 0, line, 4,1);
			line++;
			
			Label lblPow = new Label(String.join(", ", SplitterTools.getGMRelevantPowers(model)));
			lblPow.setWrapText(true);
			table.add(lblPow, 0, line, 4,1);
			
			/*
			 * Powers
			 */
			line++;
			Label lblHeadingItems = new Label(GM.getString("heading.items"));
			lblHeadingItems.setMaxWidth(Double.MAX_VALUE);
			lblHeadingItems.setAlignment(Pos.CENTER);
			lblHeadingItems.getStyleClass().add("heading-cell");
			table.add(lblHeadingItems, 0, line, 4,1);
			line++;
			
			Label lblItem = new Label(String.join(", ", SplitterTools.getGMRelevantItems(model)));
			lblItem.setWrapText(true);
			table.add(lblItem, 0, line, 4,1);
			
		}
		
		// Grow dummy
		Region region = new Region();
		region.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(region, Priority.ALWAYS);
		columns.getChildren().add(region);
		
		return columns;
	}
}
