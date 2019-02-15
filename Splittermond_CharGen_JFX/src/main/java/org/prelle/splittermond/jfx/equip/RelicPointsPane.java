/**
 *
 */
package org.prelle.splittermond.jfx.equip;

import java.util.PropertyResourceBundle;

import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterTools;
import org.prelle.splimo.charctrl.NewItemController;
import org.prelle.splimo.chargen.jfx.SpliMoCharGenJFXConstants;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class RelicPointsPane extends VBox {

	private static PropertyResourceBundle SPLIMO_CHARGEN = SpliMoCharGenJFXConstants.UI;

	private Label lblRelicWorth;
	private Label lblMoneyWorth;
//	private Label lblRelicAvail;
	private Label lblMoneyAvail;
	private Label lblLevel;
	private VBox  extra;

	private SpliMoCharacter model;
	private NewItemController generator;

	//-------------------------------------------------------------------
	/**
	 */
	public RelicPointsPane() {
		initComponents();
		initLayout();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		lblRelicWorth      = new Label("?");
		lblMoneyWorth     = new Label("?");
//		lblRelicAvail = new Label("?");
		lblMoneyAvail  = new Label("?");
		lblLevel       = new Label("?");

		lblRelicWorth     .getStyleClass().add("text-header");
		lblMoneyWorth    .getStyleClass().add("text-header");
//		lblRelicAvail.getStyleClass().add("text-subheader");
		lblMoneyAvail.getStyleClass().add("text-subheader");
		lblLevel     .getStyleClass().add("text-subheader");

		extra = new VBox();
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		setAlignment(Pos.TOP_CENTER);
		getStyleClass().add("section-bar");
//		setPrefWidth(300);
		setMinWidth(200);
		setMinHeight(300);

		Label heaRelicWorth = new Label(SPLIMO_CHARGEN.getString("label.relic.worth"));
//		Label heaRelicAvail = new Label(SPLIMO_CHARGEN.getString("label.relic.avail"));
		Label heaMoneyAvail = new Label(SPLIMO_CHARGEN.getString("label.money.free"));
		Label heaMoneyWorth = new Label(SPLIMO_CHARGEN.getString("label.money.worth"));
		Label heaLevel      = new Label(SPLIMO_CHARGEN.getString("label.level"));

		GridPane grid = new GridPane();
		grid.setMaxWidth(Double.MAX_VALUE);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.add(lblRelicWorth , 0, 0, 2,1);
		grid.add(heaRelicWorth , 0, 1, 2,1);
//		grid.add(lblRelicAvail , 0, 2, 2,1);
//		grid.add(heaRelicAvail , 0, 3, 2,1);

		grid.add(lblMoneyWorth , 0, 4, 2,1);
		grid.add(heaMoneyWorth , 0, 5, 2,1);
		grid.add(lblMoneyAvail , 0, 6, 2,1);
		grid.add(heaMoneyAvail , 0, 7, 2,1);
		grid.add(heaLevel      , 0, 8);
		grid.add(lblLevel      , 1, 8);
		GridPane.setFillWidth(heaRelicWorth, true);
		GridPane.setFillWidth(lblRelicWorth, true);
		GridPane.setFillWidth(heaMoneyAvail, true);
		GridPane.setFillWidth(lblMoneyWorth, true);
		GridPane.setHalignment(lblMoneyWorth, HPos.CENTER);
		GridPane.setHalignment(heaMoneyAvail, HPos.CENTER);
		GridPane.setHalignment(lblRelicWorth, HPos.CENTER);
		GridPane.setHalignment(heaRelicWorth, HPos.CENTER);
		GridPane.setMargin(heaMoneyAvail, new Insets(-10,0,0,0));

		getChildren().add(grid);

		Region spacing = new Region();
		spacing.setMaxHeight(Double.MAX_VALUE);
		VBox.setVgrow(spacing, Priority.ALWAYS);
		getChildren().addAll(spacing, extra);
	}

	//-------------------------------------------------------------------
	public void setData(NewItemController control, SpliMoCharacter model) {
		this.generator = control;
		this.model = model;
		refresh();
	}

	//-------------------------------------------------------------------
	public void refresh() {
		if (generator.getItem().getResource()!=null)
			lblRelicWorth.setText( generator.getItem().getRelicQuality() +"/"+ generator.getItem().getResource().getValue() );
		else
			lblRelicWorth.setText( String.valueOf( generator.getItem().getRelicQuality()) );
		lblMoneyWorth.setText(SplitterTools.telareAsCurrencyString(generator.getItem().getPrice()));
		lblLevel.setText(String.valueOf(model.getLevel()));
	}

	//-------------------------------------------------------------------
	public void setExtraNode(Node node) {
		extra.getChildren().clear();
		extra.getChildren().add(node);
	}

}
