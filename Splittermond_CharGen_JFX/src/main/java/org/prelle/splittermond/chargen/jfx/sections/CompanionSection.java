package org.prelle.splittermond.chargen.jfx.sections;

import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.javafx.AlertType;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.splimo.ResourceReference;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.chargen.creature.CreatureGenerator;
import org.prelle.splimo.creature.Creature;
import org.prelle.splimo.creature.CreatureReference;
import org.prelle.splittermond.chargen.jfx.creatures.CreatureCreateDialog;
import org.prelle.splittermond.chargen.jfx.creatures.CreatureListView;
import org.prelle.splittermond.chargen.jfx.listcells.CreatureReferenceListCell;

import javafx.geometry.Bounds;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * @author Stefan Prelle
 *
 */
public class CompanionSection extends GenericListSection<CreatureReference> {

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(CompanionSection.class.getName());

	private ContextMenu ctxMenuAdd;
	private MenuItem menuAddCreature;
	private MenuItem menuCreateCreature;
	private MenuItem menuCreateEntourage;

	//-------------------------------------------------------------------
	public CompanionSection(String title, CharacterController ctrl, ScreenManagerProvider provider) {
		super(title, ctrl, provider);
		list.setCellFactory( lv -> new CreatureReferenceListCell(ctrl, provider));
		
		setData(ctrl.getModel().getCreatures());
		list.setStyle("-fx-pref-width: 32em");
		GridPane.setVgrow(list, Priority.ALWAYS);
		list.setMaxHeight(Double.MAX_VALUE);
		
		list.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> getDeleteButton().setDisable(n==null));

		/*
		 * Add-Button context menu
		 * List all available ruleplugins
		 */
		ctxMenuAdd = new ContextMenu();
		menuAddCreature    = new MenuItem(RES.getString("section.creature.add.add_creature"));
		menuCreateCreature = new MenuItem(RES.getString("section.creature.add.create_creature"));
		menuCreateEntourage= new MenuItem(RES.getString("section.creature.add.create_entourage"));
//		getStaticButtons().addAll(menuAddCreature, menuCreateCreature);
		menuAddCreature.setOnAction( event -> addCreatureClicked());
		menuCreateCreature.setOnAction( event -> createCreatureClicked());
//		ctxMenuAdd.getItems().addAll(menuAddCreature);
		ctxMenuAdd.getItems().addAll(menuAddCreature, menuCreateCreature);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splittermond.chargen.jfx.sections.GenericListSection#onAdd()
	 */
	@Override
	protected void onAdd() {
		Bounds bounds = getAddButton().getBoundsInLocal();
        Bounds screenBounds = getAddButton().localToScreen(bounds);
        int x = (int) screenBounds.getMinX();
        int y = (int) screenBounds.getMinY();
		ctxMenuAdd.show(getAddButton(), x,y);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splittermond.chargen.jfx.sections.GenericListSection#onDelete()
	 */
	@Override
	protected void onDelete() {
		logger.trace("onDelete");
		CreatureReference toDelete = list.getSelectionModel().getSelectedItem();
		if (toDelete!=null) {
			logger.info("Try remove resource: "+toDelete);
			control.getModel().removeCreature(toDelete);
			list.getItems().remove(toDelete);
			list.getSelectionModel().clearSelection();
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.rpgframework.jfx.Section#refresh()
	 */
	@Override
	public void refresh() {
		setData(control.getModel().getCreatures());
	}

	//-------------------------------------------------------------------
	private void addCreatureClicked() {
		logger.debug("addCreatureClicked");
		String heading = RES.getString("section.creatures.addcreaturedialog.title"); 

		CreatureListView list = new CreatureListView();
		list.getItems().addAll(SplitterMondCore.getCreatures(SplitterMondCore.getCreatureFeatureType("CREATURE")));
		
//		NavigButtonControl control = new NavigButtonControl();
		
		CloseType close = getManagerProvider().getScreenManager().showAlertAndCall(AlertType.QUESTION, heading, list);
		if (close==CloseType.OK) {
			Creature selected = list.getSelectionModel().getSelectedItem();
			if (selected==null) {
				logger.warn("Clicked OK but selected nothing");
			} else {
				CreatureReference ref = new CreatureReference(selected);
				control.getModel().addCreature(ref);
				refresh();
			}
		}
	}

	//-------------------------------------------------------------------
	private void createCreatureClicked() {
		logger.debug("createCreatureClicked");
		CreatureGenerator creatGen = new CreatureGenerator((ResourceReference)null);
		try {
			CreatureCreateDialog screen = new CreatureCreateDialog(creatGen);
			CloseType result = (CloseType)getManagerProvider().getScreenManager().showAndWait(screen);
			logger.debug("CreateCreatureDialog returned with "+result);
			if (result==CloseType.APPLY) {
				CreatureReference ref = creatGen.getCreature();
				logger.info("Created creature: "+ref);
				control.getModel().addCreature(ref);
			}
			refresh();
		} catch (MissingResourceException e) {
			e.printStackTrace();
			logger.error("Missing "+e.getKey()+" in "+ResourceBundle.getBundle(CompanionSection.class.getName()));
		}
	}

}
