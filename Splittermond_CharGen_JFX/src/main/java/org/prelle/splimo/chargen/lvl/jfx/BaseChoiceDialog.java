package org.prelle.splimo.chargen.lvl.jfx;

import java.util.ResourceBundle;

import org.prelle.splimo.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splimo.modifications.ModificationChoice;

import de.rpgframework.genericrpg.modification.Modification;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public abstract class BaseChoiceDialog extends VBox {

	protected static final ResourceBundle res = SpliMoCharGenJFXConstants.UI;

	protected ModificationChoice choice;
	protected Button ok;

	//-------------------------------------------------------------------
	public BaseChoiceDialog(double padding) {
		super(padding);
	}

	//-------------------------------------------------------------------
	public void setOnAction(EventHandler<ActionEvent> handler) {
		ok.setOnAction(handler);
	}

	//-------------------------------------------------------------------
	public abstract Modification[] getChoice();

}