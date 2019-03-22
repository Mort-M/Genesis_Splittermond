package org.prelle.splittermond.chargen.jfx;

import java.util.PropertyResourceBundle;

import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.charctrl.Generator;

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
public class PointsPane extends VBox {

	private static PropertyResourceBundle SPLIMO_CHARGEN = SpliMoCharGenJFXConstants.UI;

	private ViewMode mode;
	private Label lblGPLeft;
	private Label lblExpLeft;
	private Label lblExpInvested;
	private Label lblLevel;
	private VBox  extra;

	private SpliMoCharacter model;
	private Generator generator;

	//-------------------------------------------------------------------
	/**
	 */
	public PointsPane(ViewMode mode) {
		this.mode = mode;
		initComponents();
		initLayout();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		lblGPLeft      = new Label("?");
		lblExpLeft     = new Label("?");
		lblExpInvested = new Label("?");
		lblLevel       = new Label("?");

		lblGPLeft     .getStyleClass().add("text-header");
		lblExpLeft    .getStyleClass().add("text-header");
		lblExpInvested.getStyleClass().add("text-subheader");
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

		Label heaGPLeft     = new Label(SPLIMO_CHARGEN.getString("label.gp.free"));
		Label heaExPLeft    = new Label(SPLIMO_CHARGEN.getString("label.ep.free"));
		Label heaEPInvested = new Label(SPLIMO_CHARGEN.getString("label.ep.used"));
		Label heaLevel      = new Label(SPLIMO_CHARGEN.getString("label.level"));

		GridPane grid = new GridPane();
		grid.setMaxWidth(Double.MAX_VALUE);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.add(lblGPLeft     , 0, 0, 2,1);
		grid.add(heaGPLeft     , 0, 1, 2,1);
		grid.add(lblExpLeft    , 0, 2, 2,1);
		grid.add(heaExPLeft    , 0, 3, 2,1);
		grid.add(heaEPInvested , 0, 4);
		grid.add(lblExpInvested, 1, 4);
		grid.add(heaLevel      , 0, 5);
		grid.add(lblLevel      , 1, 5);
		GridPane.setFillWidth(heaGPLeft, true);
		GridPane.setFillWidth(lblGPLeft, true);
		GridPane.setFillWidth(heaExPLeft, true);
		GridPane.setFillWidth(lblExpLeft, true);
		GridPane.setHalignment(lblExpLeft, HPos.CENTER);
		GridPane.setHalignment(heaExPLeft, HPos.CENTER);
		GridPane.setHalignment(lblGPLeft, HPos.CENTER);
		GridPane.setHalignment(heaGPLeft, HPos.CENTER);
		GridPane.setMargin(heaExPLeft, new Insets(-10,0,0,0));

		getChildren().add(grid);


		if (mode!=ViewMode.GENERATION) {
			lblGPLeft.setVisible(false);
			heaGPLeft.setVisible(false);
			grid.getChildren().remove(lblGPLeft);
			grid.getChildren().remove(heaGPLeft);
		}

		Region spacing = new Region();
		spacing.setMaxHeight(Double.MAX_VALUE);
		VBox.setVgrow(spacing, Priority.ALWAYS);
		getChildren().addAll(spacing, extra);
	}

	//-------------------------------------------------------------------
	public void setData(SpliMoCharacter model) {
		this.model = model;
		refresh();
	}

	//-------------------------------------------------------------------
	public void refresh() {
		if (generator!=null) {
			lblGPLeft.setText(String.valueOf(generator.getPointsLeft()));
		}
		lblExpLeft.setText(String.valueOf(model.getExperienceFree()));
		lblExpInvested.setText(String.valueOf(model.getExperienceInvested()));
		lblLevel.setText(String.valueOf(model.getLevel()));
	}

	//--------------------------------------------------------------------
	/**
	 * @param generator the generator to set
	 */
	public void setGenerator(Generator generator) {
		this.generator = generator;
	}

	//-------------------------------------------------------------------
	public void setExtraNode(Node node) {
		extra.getChildren().clear();
		extra.getChildren().add(node);
	}

}
