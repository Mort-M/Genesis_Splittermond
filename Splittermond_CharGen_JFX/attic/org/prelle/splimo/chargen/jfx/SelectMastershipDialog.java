/**
 *
 */
package org.prelle.splimo.chargen.jfx;

import java.io.IOException;
import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;

import org.apache.log4j.Logger;
import org.prelle.splimo.Background;
import org.prelle.splimo.Mastership;
import org.prelle.splimo.SkillSpecialization;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.chargen.MastershipGenerator;
import org.prelle.splimo.chargen.MastershipSelection;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventListener;

/**
 * @author prelle
 *
 */
public class SelectMastershipDialog extends Pane implements ChangeListener<MultipleSelectionModel<Background>>, GenerationEventListener, EventHandler<ActionEvent> {

	private final static Logger logger = Logger.getLogger("fxui");

	private static PropertyResourceBundle uiResources = SpliMoCharGenJFXConstants.UI;

	private MastershipGenerator charGen;

	private VBox content;
	private TilePane focusareas;
	private TabPane tabbed;
	private Tab common, special;
	private Button ok;

	//-------------------------------------------------------------------
	public SelectMastershipDialog(MastershipGenerator chGen) {
		content = getContent();
		getChildren().add(content);
		this.charGen = chGen;
		charGen.addListener(this);

		try {
			setData();
		} catch (IOException e) {
			logger.error("Error loading mastership FXML for "+charGen.getSkill(),e);
		}
	}

	//-------------------------------------------------------------------
	private void setData() throws IOException {
		logger.debug("setData(): "+charGen.getSkill().getType());
		for (SkillSpecialization focus : charGen.getSkill().getSpecializations()) {
			SkillSpecializationSelector select = new SkillSpecializationSelector(focus);
			focusareas.getChildren().add(select);
//
//			List<String> data = new ArrayList<String>();
//			for (int i=1; i<=4; i++)
//				data.add(focus.toString()+" "+i);
////			ListSpinner<String> spinner = new ListSpinner<String>(data);
//			ChoiceBox<String> spinner = new ChoiceBox<String>();
//			spinner.setItems(FXCollections.observableArrayList(data));
//			spinner.getSelectionModel().select(0);
//			spinner.setPrefHeight(60);
//			focusareas.getChildren().add(spinner);
		}

		String file = "fxml/mastership."+charGen.getSkill().getId()+".fxml";
		FXMLLoader loader = new FXMLLoader();
		loader.setResources(SplitterMondCore.getI18nResources());
		InputStream in = null;

		switch (charGen.getSkill().getType()) {
		case COMBAT:
			common = new Tab(resources.getString("masterships.common"));
			special= new Tab(resources.getString("masterships.special"));
			tabbed.getTabs().addAll(common, special);
			logger.debug("---Load common------");
			in = ClassLoader.getSystemResourceAsStream(SpliMoCharGenJFXConstants.PREFIX+"/fxml/mastership.combat.common.fxml");
			if (in!=null) {
				GridPane grid = (GridPane) loader.load(in);
				MastershipRequirementGrid reqGrid = new MastershipRequirementGrid(grid, charGen.getAvailable(), this);
				common.setContent(reqGrid);
			} else
				logger.warn("No FXML for common combat masterships");

			logger.debug("---Load special------");
			in = ClassLoader.getSystemResourceAsStream(file);
			if (in!=null) {
				GridPane grid = (GridPane) loader.load(in);
				MastershipRequirementGrid reqGrid = new MastershipRequirementGrid(grid, charGen.getAvailable(), this);
				special.setContent(reqGrid);
			} else
				logger.warn("Missing FXML: "+file);

			logger.debug("---Add tabs------");
			content.getChildren().add(1,tabbed);
			break;
		case MAGIC:
			common = new Tab(resources.getString("masterships.common"));
			special= new Tab(resources.getString("masterships.special"));
			tabbed.getTabs().addAll(common, special);
			logger.debug("---Load common------");
			in = ClassLoader.getSystemResourceAsStream(SpliMoCharGenJFXConstants.PREFIX+"/fxml/mastership.magic.common.fxml");
			if (in!=null) {
				GridPane grid = (GridPane) loader.load(in);
				MastershipRequirementGrid reqGrid = new MastershipRequirementGrid(grid, charGen.getAvailable(), this);
				common.setContent(reqGrid);
			} else
				logger.warn("No FXML for common magic masterships");

			logger.debug("---Load special------");
			in = ClassLoader.getSystemResourceAsStream(file);
			if (in!=null) {
				loader = new FXMLLoader();
				loader.setResources(SplitterMondCore.getI18nResources());
				GridPane grid = (GridPane) loader.load(in);
				MastershipRequirementGrid reqGrid = new MastershipRequirementGrid(grid, charGen.getAvailable(), this);
				special.setContent(reqGrid);
			} else
				logger.warn("Missing FXML: "+file);

			logger.debug("---Add tabs------");
			content.getChildren().add(1,tabbed);
			break;
		default:
			in = ClassLoader.getSystemResourceAsStream(file);
			if (in!=null) {
				GridPane grid = (GridPane) loader.load(in);
				MastershipRequirementGrid reqGrid = new MastershipRequirementGrid(grid, charGen.getAvailable(), this);
				content.getChildren().add(1,reqGrid);
			} else
				logger.warn("Missing FXML: "+file);
		}

	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		switch (event.getType()) {
		case MASTERSHIP_SELECTION_CHANGED:
			logger.debug("mastership selection changed: "+event.getKey());
			MastershipSelection mSelect = (MastershipSelection) event.getKey();
			Mastership master = mSelect.getMastership();
			ToggleButton button = (ToggleButton) tabbed.lookup("#"+master.getKey());
			button.setDisable(!mSelect.isEditable());
			button.setSelected(mSelect.isSelected());
			break;
		case MASTERSHIPGEN_POINTS_CHANGED:
			logger.warn("TODO: MASTERSHIPGEN_POINTS_CHANGED");
		default:
		}
	}

	//-------------------------------------------------------------------
	@Override
	public void changed(ObservableValue<? extends MultipleSelectionModel<Background>> property, MultipleSelectionModel<Background> oldModel,
			MultipleSelectionModel<Background> newModel) {
		// TODO Auto-generated method stub
//		Background newBackground = newModel.getSelectedItem();
//
//		logger.info("Background now "+newBackground);
//		this.nextButton.setDisable(newBackground==null);
//
//		Image img = imageByBackground.get(newBackground);
//		if (img==null) {
//			String fname = "data/background_"+newBackground.getKey()+".png";
//			logger.debug("Load "+fname);
//			InputStream in = getClass().getClassLoader().getResourceAsStream(fname);
//			if (in!=null) {
//				img = new Image(in);
//				imageByBackground.put(newBackground, img);
//			} else
//				logger.warn("Missing image at "+fname);
//		}
//		image.setImage(img);
	}

	//-------------------------------------------------------------------
	private VBox getContent() {

		tabbed = new TabPane();

		focusareas = new TilePane(Orientation.HORIZONTAL, 5, 10);
		focusareas.setAlignment(Pos.CENTER_LEFT);

		Text label = TextBuilder.create().textOrigin(VPos.BOTTOM).rotate(-90).text(resources.getString("mastership.focus")).build();
		label.setStyle("-fx-font-size:small");
		GridPane row0 = new GridPane();
		row0.setPrefHeight(85);
		row0.getRowConstraints().add(new RowConstraints(85));
		row0.setGridLinesVisible(true);
		row0.add(label, 0, 0);
		row0.add(focusareas, 1, 0);

//		ScrollPane scroll = new ScrollPane();
//		scroll.setFitToHeight(true);
//		scroll.setPrefHeight(40);
//		scroll.setContent(focusareas);

		ok    = new Button(uiResources.getString("button.ok"));
		ok.setOnAction(this);
		HBox buttonBox = new HBox(10);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.getChildren().add(ok);

		content = new VBox(10);
		content.setAlignment(Pos.TOP_LEFT);
		VBox.setVgrow(tabbed, Priority.ALWAYS);
		VBox.setVgrow(row0, Priority.SOMETIMES);
		content.getChildren().addAll(row0, buttonBox);
		content.setFillWidth(true);
		return content;
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.event.EventHandler#handle(javafx.event.Event)
	 */
	@Override
	public void handle(ActionEvent event) {
		logger.debug("Button clicked: "+event);
		if (event.getSource()==ok) {
			this.getScene().getWindow().hide();
			return;
		}

		ToggleButton mButton = (ToggleButton)event.getSource();
		logger.debug("Clicked on "+mButton.getId());
		MastershipSelection select = charGen.getSelection(mButton.getId());
		logger.debug("Clicked on2 "+select);
		select.setSelected(mButton.isSelected());
	}

}
