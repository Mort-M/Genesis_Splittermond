/**
 *
 */
package org.prelle.splittermond.jfx.equip;

import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.ManagedScreen;
import org.prelle.javafx.ScreenManager;
import org.prelle.javafx.skin.ManagedScreenStructuredSkin;
import org.prelle.rpgframework.splittermond.SplittermondRules;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.charctrl.NewItemController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;

import de.rpgframework.ConfigOption;
import de.rpgframework.RPGFrameworkLoader;
import de.rpgframework.RulePlugin;
import de.rpgframework.core.RoleplayingSystem;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

/**
 * @author prelle
 *
 */
public class EditItemScreen extends ManagedScreen implements GenerationEventListener {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle UI = SpliMoCharGenJFXConstants.UI;

	private RelicPointsPane   points;
	private Region pane;
	private boolean developerMode;

	//-------------------------------------------------------------------
	public EditItemScreen() {
		initComponents();
		initLayout();
		initInteractivity();

		getNavigButtons().add(CloseType.OK);
		setTitle(UI.getString("screen.levelequipment.title"));
		setSkin(new ManagedScreenStructuredSkin(this));
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		developerMode = false;
		try {
			RulePlugin<SpliMoCharacter> corePlugin = null;
			for (RulePlugin<?> plugin : RPGFrameworkLoader.getInstance().getCharacterAndRules().getRulePlugins(RoleplayingSystem.SPLITTERMOND)) {
				if (plugin.getID().equals("CORE"))
					corePlugin = (RulePlugin<SpliMoCharacter>) plugin;
			}
			ConfigOption<Boolean> devMode = null;
			for (ConfigOption<?> opt : corePlugin.getConfiguration()) {
				if (opt.getLocalId().equals(SplittermondRules.PROP_DEVELOPER_MODE))
					devMode = (ConfigOption<Boolean>) opt;
			}
			if (devMode!=null) {
				developerMode = (Boolean)devMode.getValue();
			}
		} catch (Exception e) {
			logger.error("Failed getting developer mode value",e);
		}

		points = new RelicPointsPane();
//		if (developerMode) {
//			pane = new NewItemGeneratorPane();
//		} else {
			pane   = new ItemGeneratorPane();
//		}

	}

	//-------------------------------------------------------------------
	private void initLayout() {
		HBox content = new HBox(20);
		content.getChildren().addAll(points, pane);

		HBox.setMargin(points, new Insets(0,0,20,0));
		HBox.setMargin(pane  , new Insets(0,0,20,0));
		setContent(content);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		GenerationEventDispatcher.addListener(this);
	}

	//--------------------------------------------------------------------
	@Override
	public void setScreenManager(ScreenManager manager) {
		super.setScreenManager(manager);
		((CommonItemGeneratorMethods)pane).setScreenManager(manager);
	}

	//-------------------------------------------------------------------
	public void setData(SpliMoCharacter charac, NewItemController ctrl) {
		points.setData(ctrl, charac);
		((CommonItemGeneratorMethods)pane).setData(ctrl);

		setTitle(ctrl.getItem().getName());
	}

	//-------------------------------------------------------------------
	@Override
	public boolean close(CloseType closeType) {
		GenerationEventDispatcher.removeListener(this);
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		((CommonItemGeneratorMethods)pane).handleGenerationEvent(event);
		if (event.getType()==GenerationEventType.ITEM_CHANGED)
			points.refresh();
	}

}
