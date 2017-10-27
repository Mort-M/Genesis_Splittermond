/**
 * 
 */
package org.prelle.splittermond.jfx.creatures;

import java.util.Iterator;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;

import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.creature.Lifeform;

/**
 * @author prelle
 *
 */
public class SkillViewPane extends FlowPane {
	
	private static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splimo-chargen");

	private Lifeform model;
	
	private Label lblHeading;
	private boolean ignoreAttributes;

	//-------------------------------------------------------------------
	/**
	 */
	public SkillViewPane(boolean ignoreAttributes) {
		this.ignoreAttributes = ignoreAttributes;
		initComponents();
		initLayout();
	}

	//--------------------------------------------------------------------
	private void initComponents() {
		lblHeading = new Label(UI.getString("label.skills")+":");
		lblHeading.getStyleClass().add("text-small-subheader");
	}

	//--------------------------------------------------------------------
	private void initLayout() {
		setHgap(3);
		setVgap(3);
		getChildren().add(lblHeading);
	}

	//--------------------------------------------------------------------
	private void initInteractivity() {
	}

	//--------------------------------------------------------------------
	void refresh() {
		getChildren().retainAll(lblHeading);
		
		for (Iterator<SkillValue> it=model.getSkills().iterator(); it.hasNext(); ) {
			SkillValue val = it.next();
			int attrVal = 0;
			if (!ignoreAttributes) {
				if (val.getSkill().getType()!=SkillType.COMBAT) {
					attrVal = model.getAttribute(val.getSkill().getAttribute1()).getValue() + model.getAttribute(val.getSkill().getAttribute2()).getValue();
				}
			}
			int finVal = attrVal + val.getModifiedValue();
			if (finVal==0)
				continue;
			String text = val.getSkill().getName()+" "+finVal;
			if (it.hasNext())
				text +=",";
			Text textNode = new Text(text);
			getChildren().add(textNode);
		}
	}

	//--------------------------------------------------------------------
	public void setData(Lifeform model) {
		this.model = model;
//		logger.info("setData "+model.dump());
		refresh();
		initInteractivity();
	}

}
