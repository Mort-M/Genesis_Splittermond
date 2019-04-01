package org.prelle.splittermond.chargen.jfx;

import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.rpgframework.jfx.CharacterDocumentView;
import org.prelle.rpgframework.jfx.FreePointsNode;
import org.prelle.splimo.charctrl.CharacterController;

import de.rpgframework.character.CharacterHandle;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;

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
	protected MenuItem cmdDelete;
	
	private ExpLine expLine;

	//-------------------------------------------------------------------
	public SpliMoManagedScreenPage(CharacterController charGen, CharacterHandle handle) {
		super();
		this.charGen = charGen;
		initPrivateComponents();
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
		
		cmdPrint = new MenuItem(UI.getString("command.primary.print"), new Label("\uE749"));
		cmdDelete = new MenuItem(UI.getString("command.primary.delete"), new Label("\uE74D"));
		setHandle(handle);
		
		getCommandBar().setContent(expLine);
	}

	//-------------------------------------------------------------------
	protected void setHandle(CharacterHandle value) {
		if (this.handle==value)
			return;
		if (this.handle!=null) {
			getCommandBar().getPrimaryCommands().removeAll(cmdPrint, cmdDelete);			
		}
		
		this.handle = value;
		if (handle!=null) {
			getCommandBar().getPrimaryCommands().addAll(cmdPrint, cmdDelete);
		}
	}

	//-------------------------------------------------------------------
	public void refresh() {
		expLine.setData(charGen.getModel());
		
		getSectionList().forEach(sect -> sect.refresh());
		setPointsFree(charGen.getModel().getExperienceFree());
	}

}
