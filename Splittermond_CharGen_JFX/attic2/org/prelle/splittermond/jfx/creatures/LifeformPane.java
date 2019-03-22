/**
 * 
 */
package org.prelle.splittermond.jfx.creatures;

import org.prelle.splimo.creature.Lifeform;

import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class LifeformPane extends VBox {
	
	private CreatureTypeViewPane  typeView;
	private AttributeViewPane attribView;
	private WeaponViewPane  weaponView;
	private SkillViewPane  skillView;
	private MastershipViewPane  masterView;
	private SpellViewPane  spellView;
	private CreatureFeatureViewPane  featView;

	//-------------------------------------------------------------------
	public LifeformPane() {
		initComponents();
		initStyle();
		initLayout();
		
		setStyle("-fx-min-width: 20em; -fx-max-width: 40em");
	}

	//--------------------------------------------------------------------
	private void initComponents() {
		typeView   = new CreatureTypeViewPane();
		attribView = new AttributeViewPane();
		weaponView = new WeaponViewPane();
		skillView  = new SkillViewPane(true);
		masterView = new MastershipViewPane();
		spellView  = new SpellViewPane();
		featView   = new CreatureFeatureViewPane();
	}

	//--------------------------------------------------------------------
	private void initStyle() {
		typeView.getStyleClass().add("content");
		attribView.getStyleClass().add("content");
		weaponView.getStyleClass().add("content");
		skillView.getStyleClass().add("content");
		masterView.getStyleClass().add("content");
		spellView.getStyleClass().add("content");
		featView.getStyleClass().add("content");
	}

	//--------------------------------------------------------------------
	private void initLayout() {
		setSpacing(4);
		attribView.setMaxWidth(Double.MAX_VALUE);
		weaponView.setMaxWidth(Double.MAX_VALUE);
		
		getChildren().add(typeView);
		getChildren().add(attribView);
		getChildren().add(weaponView);
		getChildren().add(skillView);
		getChildren().add(masterView);
		getChildren().add(spellView);
		getChildren().add(featView);
	}

	//--------------------------------------------------------------------
	public void refresh() {
		typeView.refresh();
		attribView.refresh();
		weaponView.refresh();
		skillView.refresh();
		masterView.refresh();
		spellView.refresh();
		featView.refresh();
	}

	//--------------------------------------------------------------------
	public void setData(Lifeform model) {
		typeView.setData(model);
		attribView.setData(model);
		weaponView.setData(model);
		skillView.setData(model);
		masterView.setData(model);
		spellView.setData(model);
		featView.setData(model);
		
		refresh();
	}

}
