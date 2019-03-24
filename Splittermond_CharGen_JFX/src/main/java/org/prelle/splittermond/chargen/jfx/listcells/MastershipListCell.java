/**
 *
 */
package org.prelle.splittermond.chargen.jfx.listcells;

import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.prelle.splimo.Mastership;
import org.prelle.splimo.MastershipOrSpecialization;
import org.prelle.splimo.Skill;
import org.prelle.splimo.SkillSpecialization;
import org.prelle.splimo.SkillSpecialization.SkillSpecializationType;
import org.prelle.splimo.Spell;
import org.prelle.splimo.SpellType;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.MastershipController;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class MastershipListCell extends ListCell<MastershipOrSpecialization> {

	private static PropertyResourceBundle UI = SpliMoCharGenJFXConstants.UI;

	private MastershipController control;
	private Label lblName;
	private Label lblRequire;
	private Label lblLevel;
	private StackPane stack;

	private MastershipOrSpecialization data;

	//-------------------------------------------------------------------
	public MastershipListCell(MastershipController ctrl) {
		this.control = ctrl;

		initComponents();
		initLayout();
		this.setOnDragDetected(event -> dragStarted(event));
		this.setOnMouseClicked(event -> clicked(event));
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		lblName    = new Label();
		lblRequire = new Label();
		lblLevel   = new Label();

		lblName.getStyleClass().add("baser");
		lblLevel.getStyleClass().add("text-subheader");
		lblRequire.setStyle("-fx-text-fill: red");
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		VBox box = new VBox();
		box.getChildren().addAll(lblName, lblRequire);

		stack = new StackPane();
		stack.getChildren().addAll(lblLevel, box);
		StackPane.setAlignment(box, Pos.TOP_LEFT);
		StackPane.setAlignment(lblLevel, Pos.TOP_RIGHT);
		stack.getStyleClass().add("content");
		stack.setStyle("-fx-max-width:22em");
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	public void updateItem(MastershipOrSpecialization item, boolean empty) {
		super.updateItem(item, empty);
		this.data = item;

		if (empty) {
			setGraphic(null);
		} else {
			setGraphic(stack);
			lblName.setText(item.getName());
			if (item instanceof Mastership) {
				Mastership master = (Mastership)item;
				lblLevel.setText(String.valueOf(master.getLevel()));
				lblRequire.setText(String.join(", ", control.getUnfulfilledRequirements(master)));
				lblRequire.setStyle("-fx-text-fill: red");
				stack.setDisable(!control.canBeSelected(master));
			} else {
				SkillSpecialization special = (SkillSpecialization)item;
				lblLevel.setText(String.valueOf(special.getLevel()));
				lblRequire.setText(null);
				lblRequire.setStyle("-fx-text-fill: red");
				if (special.getType()==SkillSpecializationType.SPELLTYPE) {
					Skill skill = special.getSkill();
					// Build list of spelly with that type in that school
					SpellType sType = SpellType.valueOf(special.getId());
					List<Spell> spells = new ArrayList<Spell>();
					for (Spell spell : SplitterMondCore.getSpells(skill)) {
						if (spell.getTypes().contains(sType))
							spells.add(spell);
					}
					lblRequire.setText(String.format(UI.getString("mastershiplistcell.spellstring"), spells.size()));
					lblRequire.setStyle("");
				}

				stack.setDisable(false);
			}
		}
	}

	//-------------------------------------------------------------------
	private void dragStarted(MouseEvent event) {
		LogManager.getLogger("splittermond.jfx").warn("dragStarted "+event+"   with data "+data);
		Node source = (Node) event.getSource();

		/* drag was detected, start a drag-and-drop gesture*/
        /* allow any transfer mode */
        Dragboard db = source.startDragAndDrop(TransferMode.ANY);

        /* Put a string on a dragboard */
        ClipboardContent content = new ClipboardContent();
        if (data==null)
        	return;
        if (data instanceof Mastership) {
        	if (!control.canBeSelected((Mastership) data))
        		return;
            content.putString("master:select:"+((Mastership)data).getSkill().getId()+"/"+((Mastership)data).getKey());
        } else {
        	if (!control.canBeSelected((SkillSpecialization) data,1))
        		return;
            content.putString("special:select:"+((SkillSpecialization)data).getSkill().getId()+"/"+((SkillSpecialization)data).getId());
        }
        db.setContent(content);
		LogManager.getLogger("splittermond.jfx").warn("dragStarted content = "+content);

        /* Drag image */
        WritableImage snapshot = source.snapshot(new SnapshotParameters(), null);
        db.setDragView(snapshot);

        event.consume();
	}

	//-------------------------------------------------------------------
	private void clicked(MouseEvent event) {
		if (event.getClickCount()!=2)
			return;
		if (data==null)
			return;
        if (data instanceof Mastership) {
        	if (!control.canBeSelected((Mastership) data))
        		return;
        	LogManager.getLogger("splittermond.jfx").debug("Select "+data);
            control.select( (Mastership)data);
        } else {
        	if (!control.canBeSelected((SkillSpecialization) data,1))
        		return;
        	LogManager.getLogger("splittermond.jfx").debug("Select "+data);
            control.select( (SkillSpecialization)data, 1);
        }
    }
}
