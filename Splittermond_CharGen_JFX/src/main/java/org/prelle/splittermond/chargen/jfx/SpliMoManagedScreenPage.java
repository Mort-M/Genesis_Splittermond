package org.prelle.splittermond.chargen.jfx;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.PropertyResourceBundle;
import java.util.function.Predicate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.AlertType;
import org.prelle.javafx.AppBarButton;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.SymbolIcon;
import org.prelle.rpgframework.jfx.CharacterDocumentView;
import org.prelle.splimo.BasePluginData;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.chargen.SpliMoCharacterGenerator;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventType;

import de.rpgframework.RPGFrameworkLoader;
import de.rpgframework.ResourceI18N;
import de.rpgframework.character.CharacterHandle;
import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;
import de.rpgframework.core.RoleplayingSystem;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;

/**
 * @author Stefan Prelle
 *
 */
public class SpliMoManagedScreenPage extends CharacterDocumentView {

	protected static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	protected static PropertyResourceBundle UI = SpliMoCharGenJFXConstants.UI;

	protected CharacterController charGen;
	protected CharacterHandle handle;

	protected MenuItem cmdPrint;
	
	protected ExpLine expLine;
	protected ViewMode mode;
	protected AppBarButton cmdFinish;
	
	protected BasePluginData helpData;

	//-------------------------------------------------------------------
	public SpliMoManagedScreenPage(CharacterController charGen, ViewMode mode, CharacterHandle handle) {
		super();
		this.charGen = charGen;
		this.mode    = mode;
		getCommandBar().setOpen(true);
		initPrivateComponents();
		setHandle(handle);
		initInteractivity();
		try {
			descrBtnEdit.setVisible(false);
		} catch (NoSuchFieldError e) {
		}
	}

	//-------------------------------------------------------------------
	private void initPrivateComponents() {
		/*
		 * Exp & Co.
		 */
		setPointsNameProperty(UI.getString("label.ep.free"));
		setPointsFree(charGen.getModel().getExperienceFree());
		expLine = new ExpLine();
		expLine.setData(charGen.getModel());
		
		
		cmdPrint  = new MenuItem(UI.getString("command.primary.print"), new Label("\uE749"));
		cmdFinish = new AppBarButton(UI.getString("command.primary.finish"), new SymbolIcon("accept"));
		
		getCommandBar().setContent(expLine);
		if (mode==ViewMode.GENERATION) {
			getCommandBar().getPrimaryCommands().add(cmdFinish);
		}
		setHandle(handle);
	}

	//-------------------------------------------------------------------
	protected void setHandle(CharacterHandle value) {
		if (this.handle==value)
			return;
		if (this.handle!=null) {
			getCommandBar().getPrimaryCommands().removeAll(cmdPrint);			
		}
		
		this.handle = value;
		if (handle!=null) {
			getCommandBar().getPrimaryCommands().addAll(cmdPrint);
		}
	}

	//-------------------------------------------------------------------
	public void refresh() {
		expLine.setData(charGen.getModel());
		
		getSectionList().forEach(sect -> sect.refresh());
		setPointsFree(charGen.getModel().getExperienceFree());
		
		if (mode==ViewMode.GENERATION) {
			boolean ready = ((SpliMoCharacterGenerator)charGen).hasEnoughData();
			cmdFinish.setDisable(!ready);
		}
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		cmdPrint.setOnAction( ev -> {BabylonEventBus.fireEvent(BabylonEventType.PRINT_REQUESTED, handle, charGen.getModel());});
		cmdFinish.setOnAction( ev -> {GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.FINISH_REQUESTED, handle, charGen.getModel()));});
	}

	//--------------------------------------------------------------------
	public void changeCustomTextTo(String newText) {
		logger.info("change text for "+helpData);
		try {
			if (helpData!=null) {
				logger.info("Call "+helpData.getClass()+".setCustomHelpText()");
				helpData.setCustomHelpText(newText);
			} else {
				getScreenManager().showAlertAndCall(AlertType.ERROR, "Es gibt ein Problem", ResourceI18N.get(UI, "error.customText.nothing_selected"));
			}
		} catch (Exception e) {
			logger.error("Failed setting custom text: ",e);
			getScreenManager().showAlertAndCall(AlertType.ERROR, "Es gibt ein Problem", ResourceI18N.get(UI, "error.customText.error_setting")+"\n"+e.toString());
		}
	}

}
