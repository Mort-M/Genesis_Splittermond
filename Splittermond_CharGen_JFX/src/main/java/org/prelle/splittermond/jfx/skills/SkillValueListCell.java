/**
 * 
 */
package org.prelle.splittermond.jfx.skills;

import org.prelle.splimo.Skill;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.charctrl.SkillController;

import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * @author prelle
 *
 */
public class SkillValueListCell extends ListCell<SkillValue> {

	private SkillController control;
	private SkillValue selected;
	
	private Label name;
	private Label attrib1;
	private Label attrib2;
	private Button btnDec;
	private Label  lblVal;
	private Button btnInc;
	private HBox  layout;
	
	//-------------------------------------------------------------------
	/**
	 */
	public SkillValueListCell(SkillController ctrl) {
		this.control = ctrl;
		setContentDisplay(ContentDisplay.RIGHT);
		
		initComponents();
		initLayout();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		name    = new Label();
		attrib1 = new Label();
		attrib2 = new Label();
		btnDec  = new Button();
		lblVal  = new Label();
		btnInc  = new Button();
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		name.setStyle("-fx-pref-width: 12em");
		attrib1.setStyle("-fx-pref-width: 3em");
		attrib2.setStyle("-fx-pref-width: 3em");
		name.setMaxWidth(Double.MAX_VALUE);
		
		layout = new HBox();
		layout.setStyle("-fx-hgap: 1em");
		layout.getChildren().addAll(name, attrib1, attrib2);
		HBox.setHgrow(name, Priority.ALWAYS);
	}

	//-------------------------------------------------------------------
	@Override
	public void updateItem(SkillValue item, boolean empty) {
		super.updateItem(item, empty);
		selected = item;
		
		if (empty && item==null) {
			setText(null);
			setGraphic(null);
		} else {
			Skill skill = item.getSkill();
//			setText(item.getSkill().getName());
			lblVal.setText(String.valueOf(item.getValue()));
			if (skill.isGrouped()) {
				
			} else {
				name.setText(skill.getName());
			}
			if (skill.getAttribute1()!=null)
				attrib1.setText(skill.getAttribute1().getShortName());
			if (skill.getAttribute2()!=null)
				attrib2.setText(skill.getAttribute2().getShortName());
			
			setGraphic(layout);
		}
		
	}

}
