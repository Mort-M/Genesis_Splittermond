/**
 *
 */
package org.prelle.splittermond.jfx.creatures;

import java.util.Iterator;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.splimo.MastershipReference;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splimo.creature.Lifeform;

import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;

/**
 * @author prelle
 *
 */
public class MastershipViewPane extends FlowPane {

	private static PropertyResourceBundle UI = SpliMoCharGenJFXConstants.UI;

	private Lifeform model;

	private Label lblHeading;

	//-------------------------------------------------------------------
	/**
	 */
	public MastershipViewPane() {
		initComponents();
		initLayout();
	}

	//--------------------------------------------------------------------
	private void initComponents() {
		lblHeading = new Label(UI.getString("label.masterships")+":");
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
			List<MastershipReference> list = val.getMasterships();
			if (list.isEmpty())
				continue;

			// Print masterships for this skill
			getChildren().add(new Text(val.getSkill().getName()+" ("));
			int lastLevel = 0;
			for (Iterator<MastershipReference> it2=list.iterator(); it2.hasNext(); ) {
				MastershipReference ref = it2.next();
				if (ref.getMastership().getLevel()>lastLevel)
					getChildren().add(new Text(ref.getMastership().getLevel()+": "));
				String text =ref.getMastership().getName();
				if (it2.hasNext())
					text +=",";
				Text textNode = new Text(text);
				getChildren().add(textNode);
			}
			getChildren().add(new Text(")"));
		}
	}

	//--------------------------------------------------------------------
	public void setData(Lifeform model) {
		this.model = model;
		refresh();
		initInteractivity();
	}

}
