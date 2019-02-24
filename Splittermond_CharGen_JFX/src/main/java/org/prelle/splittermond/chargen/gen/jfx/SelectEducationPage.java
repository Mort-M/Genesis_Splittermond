/**
 * 
 */
package org.prelle.splittermond.chargen.gen.jfx;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Education;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.chargen.LetUserChooseListener;
import org.prelle.splimo.chargen.SpliMoCharacterGenerator;
import org.prelle.splimo.free.FreeSelectionGenerator;
import org.prelle.splittermond.chargen.fluent.SpliMoCharGenConstants;
import org.prelle.splittermond.chargen.free.jfx.FreeSelectionDialog;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * @author prelle
 *
 */
public class SelectEducationPage extends WizardPage implements ChangeListener<TreeItem<Education>> {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle uiResources = SpliMoCharGenConstants.RES;

	private static Map<Education,Image> imageByEducation;

	private SpliMoCharacterGenerator charGen;
	private LetUserChooseListener choiceCallback;
	
	private TreeView<Education> tree;
	private TreeItem<Education> root;
	private ImageView image;
	
	private HBox content;
	private VBox context;
	
	private Education selected;
	private Button freeCreation_b;

	//-------------------------------------------------------------------
	static {
		imageByEducation = new HashMap<Education, Image>();
	}

	//-------------------------------------------------------------------
	public SelectEducationPage(SpliMoCharacterGenerator charGen, LetUserChooseListener choiceCallback) {
		super(uiResources.getString("wizard.selectEducation.title"), 
				new Image(SelectEducationPage.class.getClassLoader().getResourceAsStream("data/Splittermond_hochkant.png")));
		this.charGen = charGen;
		this.choiceCallback = choiceCallback;
		nextButton.setDisable(true);
		finishButton.setDisable(!charGen.hasEnoughData());
		
		initInteractivity();
	}

	//-------------------------------------------------------------------
	@Override
	public void changed(ObservableValue<? extends TreeItem<Education>> property, TreeItem<Education> oldEducation,
			TreeItem<Education> newItem) {
		if (newItem==null)
			return;
		
		selected = newItem.getValue();
		logger.info("Education now "+selected);
		nextButton.setDisable(newItem==null);
		finishButton.setDisable(!charGen.hasEnoughData());

		Image img = imageByEducation.get(selected);
		if (img==null) {
			String fname = "data/education_"+selected.getKey()+".png";
			logger.debug("Load "+fname);
			InputStream in = getClass().getClassLoader().getResourceAsStream(fname);
			if (in!=null) {
				img = new Image(in);
				imageByEducation.put(selected, img);
			} else if (selected.getVariantOf()!=null) {
				Education edu2 = SplitterMondCore.getEducation(selected.getVariantOf());
				img = imageByEducation.get(selected);
				if (img==null) {
					fname = "data/education_"+edu2.getKey()+".png";
					logger.debug("Load "+fname);
					in = getClass().getClassLoader().getResourceAsStream(fname);
					if (in!=null) {
						img = new Image(in);
						imageByEducation.put(edu2, img);
					} else {
						logger.warn("Missing image at "+fname);
					}
				}
			} else
				logger.warn("Missing image at "+fname);
				
		}
		
		if (img!=null) {
			double scaleX = 300.0/img.getWidth();
			double scaleY = 400.0/img.getHeight();
			double scale  = Math.min(scaleX, scaleY);
			double scaledX = img.getWidth() * scale;
			double scaledY = img.getHeight() * scale;
			logger.trace(String.format("Scale image to %3.2f x %3.2f", scaledX, scaledY));
			image.setImage(img);
			image.setFitWidth(scaledX);
			image.setFitHeight(scaledY);
			Rectangle clip = new Rectangle(scaledX, scaledY);
			clip.setArcWidth(50);
			clip.setArcHeight(50);
			image.setClip(clip);		
		}
		
		if (charGen!=null)
			finishButton.setDisable(!charGen.hasEnoughData());
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splittermond.chargen.gen.jfx.WizardPage#getContent()
	 */
	@Override
	Parent getContent() {
		content = new HBox();
		content.setFillHeight(true);
		content.setSpacing(10);
		content.setAlignment(Pos.TOP_LEFT);

		image = new ImageView();
		image.setPreserveRatio(true);
		
		image.setFitWidth(300);
		image.setFitHeight(400);

		root = new TreeItem<Education>();
		tree = new TreeView<Education>(root);
		tree.setShowRoot(false);
		tree.setCellFactory(new Callback<TreeView<Education>,TreeCell<Education>>(){
            @Override
            public TreeCell<Education> call(TreeView<Education> p) {
                return new EducationTreeCell();
            }
        });
		// Set data
		for (Education edu : SplitterMondCore.getEducations()) {
			TreeItem<Education> item = new TreeItem<Education>(edu);
			root.getChildren().add(item);
			// Now variants
			for (Education variant : edu.getVariants()) {
				TreeItem<Education> variantItem = new TreeItem<Education>(variant);
				item.getChildren().add(variantItem);
			}
		}
		tree.setMinWidth(200);
		tree.setMinHeight(400);

		content.getChildren().addAll(tree, image);
		content.setAlignment(Pos.TOP_LEFT);
		
		return content;
	}
	
	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splittermond.chargen.gen.jfx.WizardPage#getContextMenu()
	 */
	@Override
	public Parent getContextMenu() {
		Label placeholder = new Label(" ");
		placeholder.getStyleClass().add("wizard-heading");
		freeCreation_b = new Button(uiResources.getString("wizard.freeCreation"));	
		
		context = new VBox(15);
		context.setPrefWidth(104);
		context.getChildren().addAll(placeholder, freeCreation_b);
		return context;
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		tree.getSelectionModel().selectedItemProperty().addListener(this);
		
		// Listen to button
		freeCreation_b.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FreeSelectionGenerator freeGen = charGen.getFreeCreatorEducation();
				// Initialize with current selection - if there is any
				TreeItem<Education> sel = tree.getSelectionModel().getSelectedItem();
				if (sel!=null && sel.getValue()!=null) {
					logger.debug("pre-fill free selection dialog with "+sel.getValue());
					freeGen.setFrom(sel.getValue());
				}
				FreeSelectionDialog dialog = new FreeSelectionDialog(freeGen);
				Stage stage = new Stage();
				stage.setTitle(uiResources.getString("freeselect.title.education"));
				Scene scene2 = new Scene(dialog);
				scene2.getStylesheets().add("css/default.css");
				stage.setScene(scene2);
				stage.showAndWait();
				
				if (!dialog.hasBeenCancelled()) {
					selected = freeGen.getAsEducation();
					logger.debug("Add custom education "+selected.getModifications()+" to tree");
					TreeItem<Education> added = new TreeItem<Education>(selected);
					root.getChildren().add(added);
					tree.getSelectionModel().select(added);
					logger.debug("Select custom education "+selected.getModifications());
					nextButton.setDisable(false);
					SelectEducationPage.this.nextPage();
				}
			}
		});
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splittermond.chargen.gen.jfx.WizardPage#nextPage()
	 */
	void nextPage() {
		if (selected==null)
			throw new IllegalStateException("Nothing selected");
		
		this.setDisable(true);
		charGen.selectEducation(selected, choiceCallback);
		this.setDisable(false);
		
		super.nextPage();
	}

}

class EducationTreeCell extends TreeCell<Education> {

	//-------------------------------------------------------------------
	@Override
    public void updateItem(Education item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
        	setText(item.getName());
        }
	}
}