package org.prelle.splittermond.chargen.jfx;

import org.prelle.rpgframework.jfx.FreePointsNode;
import org.prelle.splimo.SpliMoCharacter;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;

/**
 * @author Stefan Prelle
 *
 */
public class ExpLine extends HBox {

	private Label lbExpTotal;
	private Label lbExpInvested;
	private Label lbLevel;

	//-------------------------------------------------------------------
	public ExpLine() {
		super(5);
		initComponents();
		initLayout();
		getStyleClass().add("character-document-view-firstline");
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		lbExpTotal    = new Label("?");
		lbExpInvested = new Label("?");
		lbLevel       = new Label("?");
		lbExpTotal.getStyleClass().add("base");
		lbExpInvested.getStyleClass().add("base");
		lbLevel.getStyleClass().add("base");
//		lbExpTotal.setText(String.valueOf(charGen.getModel().getExperienceInvested()+charGen.getModel().getExperienceFree()));
//		lbExpInvested.setText(charGen.getModel().getExperienceInvested()+"");
//		lbLevel.setText(charGen.getModel().getLevel()+"");
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		Label hdExpTotal    = new Label(SpliMoCharGenJFXConstants.UI.getString("label.ep.total")+": ");
		Label hdExpInvested = new Label(SpliMoCharGenJFXConstants.UI.getString("label.ep.used")+": ");
		Label hdLevel       = new Label(SpliMoCharGenJFXConstants.UI.getString("label.level")+": ");
		getChildren().addAll(hdExpTotal, lbExpTotal, hdExpInvested, lbExpInvested, hdLevel, lbLevel);
		HBox.setMargin(hdLevel, new Insets(0,0,0,20));
	}

	//-------------------------------------------------------------------
	public void setData(SpliMoCharacter model) {
		lbExpTotal.setText(model.getExperienceInvested()+model.getExperienceFree()+"");
		lbExpInvested.setText(model.getExperienceInvested()+"");
		lbLevel.setText(model.getLevel()+"");
	}

}
