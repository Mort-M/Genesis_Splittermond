package org.prelle.splittermond.chargen.jfx;

import java.util.Iterator;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.rpgframework.jfx.DoubleSection;
import org.prelle.rpgframework.jfx.Section;
import org.prelle.splimo.Spell;
import org.prelle.splimo.SpellType;
import org.prelle.splimo.SplitterTools;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splittermond.chargen.jfx.sections.SchoolSpecializationSection;
import org.prelle.splittermond.chargen.jfx.sections.SchoolSpecializationSection.SpecializationSort;
import org.prelle.splittermond.chargen.jfx.sections.SpellListSection;
import org.prelle.splittermond.chargen.jfx.sections.SpellSchoolSection;

import de.rpgframework.character.CharacterHandle;
import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * @author Stefan Prelle
 *
 */
public class SMSpellPage extends SpliMoManagedScreenPage {
	
	private ViewMode mode;
	private ScreenManagerProvider provider;

	private SpellSchoolSection schools;
	private SpellListSection spells;
	private SchoolSpecializationSection specials;

	private Section secLine2;
	
	private ChoiceBox<SpecializationSort> cbSort;

	//-------------------------------------------------------------------
	public SMSpellPage(CharacterController control, ViewMode mode, CharacterHandle handle, ScreenManagerProvider provider) {
		super(control, handle);
		this.setId("splittermond-spells");
		this.setTitle(control.getModel().getName());
		this.provider = provider;
		this.mode = mode;
		if (this.mode==null)
			this.mode = ViewMode.MODIFICATION;
		
		initComponents();
		initInteractivity();

		refresh();
		cbSort.getSelectionModel().select(SpecializationSort.ACTIVE_SPELLS);
	}

	//-------------------------------------------------------------------
	private void initLine1() {
		schools = new SpellSchoolSection(UI.getString("section.schools"), charGen, provider);
		getSectionList().add(schools);

		// Interactivity
//		schools.showHelpForProperty().addListener( (ov,o,n) -> { if (n!=null) updateHelp(n.getPower()); else updateHelp(null); });
	}

	//-------------------------------------------------------------------
	private void initLine2() {
		spells = new SpellListSection(UI.getString("section.spells"), charGen, provider);
		specials= new SchoolSpecializationSection(UI.getString("section.specializations"), charGen, provider);

		secLine2 = new DoubleSection(spells, specials);
		getSectionList().add(secLine2);

		// Interactivity
		spells.showHelpForProperty().addListener( (ov,o,n) -> updateHelp(n));
//		specials.showHelpForProperty().addListener( (ov,o,n) -> updateHelp(n.data));
		
		cbSort.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> specials.setSort(n));
	}

	//-------------------------------------------------------------------
	protected void initComponents() {
		setPointsNameProperty(UI.getString("label.experience.short"));
		cbSort = new ChoiceBox<SpecializationSort>();
		cbSort.getItems().addAll(SpecializationSort.values());
		cbSort.setConverter(new StringConverter<SpecializationSort>() {
			public String toString(SpecializationSort val) {
				return UI.getString("specialization.sort."+val.name().toLowerCase());
			}
			public SpecializationSort fromString(String string) { return null;}
			});
		
		Region grow = new Region();
		grow.setMaxWidth(Double.MAX_VALUE);
		Label lbSort = new Label(UI.getString("specialization.sort.label")+" ");
		HBox commandContent = new HBox(expLine, grow, lbSort, cbSort);
		HBox.setHgrow(grow, Priority.ALWAYS);
		commandContent.setAlignment(Pos.CENTER_LEFT);
		commandContent.setMaxWidth(Double.MAX_VALUE);;
		getCommandBar().setContent(commandContent);
		getCommandBar().getPrimaryCommands().add(new MenuItem("Test"));

		initLine1();
		initLine2();
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		cmdPrint.setOnAction( ev -> {BabylonEventBus.fireEvent(BabylonEventType.PRINT_REQUESTED, handle, charGen.getModel());});
//		cmdDelete.setOnAction( ev -> BabylonEventBus.fireEvent(BabylonEventType.DELETE_REQUESTED, handle, charGen.getModel()));
		
		schools.selectedSchoolProperty().addListener( (ov,o,n) -> {
			spells.setSchool(n);
			specials.setSchool(n);
		});
	}

	//-------------------------------------------------------------------
	private void updateHelp(Spell data) {
		if (data!=null) {
			this.setDescriptionHeading(data.getName());
			this.setDescriptionPageRef(data.getProductNameShort()+" "+data.getPage());
			this.setDescriptionNode(new SpellDescription(data));
		} else {
			this.setDescriptionHeading(null);
			this.setDescriptionPageRef(null);
			this.setDescriptionText(null);
		}
	}
	
	//-------------------------------------------------------------------
	public void refreshSpells() {
		spells.refresh();
		specials.refresh();
		setPointsFree(charGen.getModel().getExperienceFree());
	}

}

class SpellDescription extends VBox {

	private static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle.getBundle(SpellListSection.class.getName());
	
	public SpellDescription(Spell spell) {
		StringBuffer buf;
		
		// Type
		Label headType = new Label(UI.getString("label.spell.type")+": ");
		Label lblType  = new Label();
		lblType.setWrapText(true);
		headType.getStyleClass().add("base");
		buf = new StringBuffer();
		for (Iterator<SpellType> it=spell.getTypes().iterator(); it.hasNext(); ) {
			buf.append(it.next().getName());
			if (it.hasNext())
				buf.append(", ");
		}
		lblType.setText(buf.toString());
		
		// Difficulty
		Label headDiff = new Label(UI.getString("label.spell.difficulty")+": ");
		Label lblDiff  = new Label();
		headDiff.getStyleClass().add("base");
		lblDiff.setText(String.valueOf(spell.getDifficultyString()));
		
		// Cost
		Label headCost = new Label(UI.getString("label.spell.cost")+": ");
		Label lblCost  = new Label();
		headCost.getStyleClass().add("base");
		lblCost.setText(SplitterTools.getFocusString(spell.getCost()));
		
		// Cast duration
		Label headDur  = new Label(UI.getString("label.spell.castduration")+": ");
		Label lblDur   = new Label();
		headDur.getStyleClass().add("base");
		lblDur.setText(spell.getCastDurationString());

		// Range
		Label headRange= new Label(UI.getString("label.spell.castrange")+": ");
		Label lblRange = new Label();
		headRange.getStyleClass().add("base");
		lblRange.setText(spell.getCastRangeString());

		// Spell Duration
		Label headSpDur= new Label(UI.getString("label.spell.duration")+": ");
		Label lblSpDur = new Label();
		headSpDur.getStyleClass().add("base");
		lblSpDur.setText(spell.getSpellDurationString());
		
		// Effect
		Label lblDescr = new Label(spell.getDescription());
		lblDescr.setWrapText(true);
		
		// Enhancement
		Label headEnhan= new Label(UI.getString("label.spell.enhanced")+": ");
		Label lblEnhCost = new Label(spell.getEnhancementString());
		Label lblEnhan = new Label(spell.getEnhancementDescription());
		lblEnhan.setWrapText(true);
		headEnhan.getStyleClass().add("base");

		
		HBox boxEnhan = new HBox(headEnhan, lblEnhCost);
		getChildren().addAll(
				new HBox(headType, lblType), 
				new HBox(headDiff, lblDiff), 
				new HBox(headCost, lblCost), 
				new HBox(headDur , lblDur ), 
				new HBox(headRange, lblRange), 
				new HBox(headSpDur, lblSpDur), 
				lblDescr, 
				boxEnhan, 
				lblEnhan);
		VBox.setMargin(lblDescr, new Insets(20,0,0,0));
		VBox.setMargin(boxEnhan, new Insets(20,0,0,0));

	}

}