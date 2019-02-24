/**
 *
 */
package org.prelle.splittermond.jfx.creatures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;

import org.prelle.splimo.Attribute;
import org.prelle.splimo.SplitterTools;
import org.prelle.splimo.creature.CreatureWeapon;
import org.prelle.splimo.creature.Lifeform;
import org.prelle.splimo.items.Feature;
import org.prelle.splimo.items.ItemAttribute;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;

/**
 * @author prelle
 *
 */
public class WeaponViewPane extends GridPane {

	private static PropertyResourceBundle UI = SpliMoCharGenJFXConstants.UI;

	private Lifeform model;

	private Map<CreatureWeapon, List<Node>> labelsByWeapon;

	//-------------------------------------------------------------------
	public WeaponViewPane() {
		initComponents();
		initLayout();
	}

	//--------------------------------------------------------------------
	private void initComponents() {
		labelsByWeapon = new HashMap<>();

//		tfInput = new HashMap<>();
//		for (Attribute attr : Attribute.values()) {
//			Label tf = new Label();
//			tf.setStyle("-fx-min-width: 2.5em");
//			tf.setUserData(attr);
//			tfInput.put(attr, tf);
//		}
	}

	//--------------------------------------------------------------------
	private void initLayout() {
		Label headWeapon = new Label(UI.getString("creature.weapon.name"));
		Label headValue  = new Label(UI.getString("creature.weapon.value"));
		Label headDamage = new Label(ItemAttribute.DAMAGE.getShortName());
		Label headSpeed  = new Label(ItemAttribute.SPEED.getShortName());
		Label headINI    = new Label(Attribute.INITIATIVE.getShortName());
		Label headFeature= new Label(ItemAttribute.FEATURES.getName());

		headWeapon .getStyleClass().add("table-head");
		headValue  .getStyleClass().add("table-head");
		headDamage .getStyleClass().add("table-head");
		headSpeed  .getStyleClass().add("table-head");
		headINI    .getStyleClass().add("table-head");
		headFeature.getStyleClass().add("table-head");

		headWeapon.setMaxWidth(Double.MAX_VALUE);
		headValue .setMaxWidth(Double.MAX_VALUE);
		headDamage.setMaxWidth(Double.MAX_VALUE);
		headSpeed .setMaxWidth(Double.MAX_VALUE);
		headINI   .setMaxWidth(Double.MAX_VALUE);
		headFeature.setMaxWidth(Double.MAX_VALUE);

		headWeapon.setAlignment(Pos.CENTER_LEFT);
		headValue.setAlignment(Pos.CENTER);
		headDamage.setAlignment(Pos.CENTER);
		headSpeed.setAlignment(Pos.CENTER);
		headINI.setAlignment(Pos.CENTER);
		headFeature.setAlignment(Pos.CENTER_LEFT);

		this.add(headWeapon, 0, 0);
		this.add(headValue , 1, 0);
		this.add(headDamage, 2, 0);
		this.add(headSpeed , 3, 0);
		this.add(headINI   , 4, 0);
		this.add(headFeature, 5, 0);
	}

	//--------------------------------------------------------------------
	private void removeWeapon(CreatureWeapon weapon) {
		if (!labelsByWeapon.containsKey(weapon))
			return;

		for (Node lbl : labelsByWeapon.get(weapon)) {
			getChildren().remove(lbl);
		}
		labelsByWeapon.remove(weapon);
	}

	//--------------------------------------------------------------------
	private void addWeapon(int line, CreatureWeapon weapon) {
		Label lblWeapon = new Label(weapon.getName());
		Label lblValue  = new Label(String.valueOf(weapon.getValue()));
		Label lblDamage = new Label(SplitterTools.getWeaponDamageString(weapon.getDamage()));
		Label lblSpeed  = new Label(weapon.getSpeed()+" "+UI.getString("label.ticks"));
		Label lblINI    = new Label(weapon.getInitiative()+"-"+UI.getString("label.1d6"));

		FlowPane features = new FlowPane();
		for (Iterator<Feature> it = weapon.getFeatures().iterator(); it.hasNext(); ) {
			Feature feat = it.next();
			String text = feat.getName();
			if (it.hasNext())
				text +=",";
			Text textNode = new Text(text);
			features.getChildren().add(textNode);
		}
		// Memorize labels
		List<Node> memorize = Arrays.asList(lblWeapon, lblValue, lblDamage, lblSpeed, lblINI, features);
		labelsByWeapon.put(weapon, memorize);


		lblWeapon.setStyle("-fx-min-width: 6em");
		lblValue .setStyle("-fx-min-width: 3em");
		lblDamage.setStyle("-fx-min-width: 4em");
		lblSpeed .setStyle("-fx-min-width: 4em");
		lblINI   .setStyle("-fx-min-width: 4em");
		features .setStyle("-fx-pref-width: 8em");

		lblWeapon.setAlignment(Pos.CENTER_LEFT);
		lblValue.setAlignment(Pos.CENTER);
		lblDamage.setAlignment(Pos.CENTER);
		lblSpeed.setAlignment(Pos.CENTER);
		lblINI.setAlignment(Pos.CENTER);
		features.setAlignment(Pos.CENTER_LEFT);

		this.add(lblWeapon, 0, line);
		this.add(lblValue , 1, line);
		this.add(lblDamage, 2, line);
		this.add(lblSpeed , 3, line);
		this.add(lblINI   , 4, line);
		this.add(features , 5, line);

		GridPane.setHalignment(lblWeapon, HPos.LEFT);
		GridPane.setHalignment(lblValue , HPos.CENTER);
		GridPane.setHalignment(lblDamage, HPos.CENTER);
		GridPane.setHalignment(lblSpeed , HPos.CENTER);

		GridPane.setHgrow(lblWeapon, Priority.SOMETIMES);
		GridPane.setHgrow(features, Priority.ALWAYS);


		GridPane.setMargin(lblWeapon, new Insets(0,5,0,0));
		GridPane.setMargin(lblValue , new Insets(0,3,0,3));
		GridPane.setMargin(lblDamage , new Insets(0,3,0,3));
		GridPane.setMargin(lblSpeed , new Insets(0,3,0,3));
		GridPane.setMargin(lblINI , new Insets(0,3,0,3));
	}

	//--------------------------------------------------------------------
	void refresh() {
		// Clear all
		for (CreatureWeapon tmp : new ArrayList<>(labelsByWeapon.keySet()))
			removeWeapon(tmp);

		// Add anew
		int line = 0;
		for (CreatureWeapon weapon : model.getCreatureWeapons()) {
			line++;
			addWeapon(line, weapon);
		}
	}

	//--------------------------------------------------------------------
	public void setData(Lifeform model) {
		this.model = model;
		refresh();
	}

}
