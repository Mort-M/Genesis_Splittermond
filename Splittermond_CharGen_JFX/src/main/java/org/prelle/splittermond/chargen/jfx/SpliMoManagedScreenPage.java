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
	private CharacterHandle handle;

	protected MenuItem cmdPrint;
	protected MenuItem cmdDelete;
	
	private Label lbExpTotal;
	private Label lbExpInvested;
	private FreePointsNode freePoints;
	private Label lbLevel;

	//-------------------------------------------------------------------
	public SpliMoManagedScreenPage(CharacterController charGen, CharacterHandle handle) {
		super();
		this.charGen = charGen;
		this.handle  = handle;
		
		initPrivateComponents();
	}

	//-------------------------------------------------------------------
	private void initPrivateComponents() {
		/*
		 * Exp & Co.
		 */
		freePoints = new FreePointsNode();
		freePoints.setStyle("-fx-max-height: 3em; -fx-max-width: 3em");
		freePoints.setPoints(charGen.getModel().getExperienceFree());
		freePoints.setName(UI.getString("label.ep.free"));
		Label hdExpTotal    = new Label(SpliMoCharGenJFXConstants.UI.getString("label.ep.total")+": ");
		Label hdExpInvested = new Label(SpliMoCharGenJFXConstants.UI.getString("label.ep.used")+": ");
		Label hdLevel       = new Label(SpliMoCharGenJFXConstants.UI.getString("label.level")+": ");
		lbExpTotal    = new Label("?");
		lbExpInvested = new Label("?");
		lbLevel       = new Label("?");
		lbExpTotal.getStyleClass().add("base");
		lbExpInvested.getStyleClass().add("base");
		lbLevel.getStyleClass().add("base");
		lbExpTotal.setText(String.valueOf(charGen.getModel().getExperienceInvested()+charGen.getModel().getExperienceFree()));
		lbExpInvested.setText(charGen.getModel().getExperienceInvested()+"");
		lbLevel.setText(charGen.getModel().getLevel()+"");
		
		cmdPrint = new MenuItem(UI.getString("command.primary.print"), new Label("\uE749"));
		cmdDelete = new MenuItem(UI.getString("command.primary.delete"), new Label("\uE74D"));
		setHandle(handle);
		
		HBox expLine = new HBox(5);
		expLine.getChildren().addAll(hdExpTotal, lbExpTotal, hdExpInvested, lbExpInvested, hdLevel, lbLevel);
		HBox.setMargin(hdLevel, new Insets(0,0,0,20));
		expLine.getStyleClass().add("character-document-view-firstline");
		
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
		lbExpTotal.setText((charGen.getModel().getExperienceInvested()+charGen.getModel().getExperienceFree())+"");
		lbExpInvested.setText(charGen.getModel().getExperienceInvested()+"");
		lbLevel.setText(charGen.getModel().getLevel()+"");
		freePoints.setPoints(charGen.getModel().getExperienceFree());
		
		getSectionList().forEach(sect -> sect.refresh());
	}

}
