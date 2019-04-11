package org.prelle.splittermond.chargen.jfx.wizard;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.Wizard;
import org.prelle.javafx.WizardPage;
import org.prelle.splimo.Education;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.chargen.LetUserChooseListener;
import org.prelle.splimo.chargen.SpliMoCharacterGenerator;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

/**
 * @author prelle
 *
 */
public class WizardPageEducation extends WizardPage implements ChangeListener<TreeItem<Education>> {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);

	private static PropertyResourceBundle uiResources = (PropertyResourceBundle) ResourceBundle.getBundle(CharGenWizardSpliMo.class.getName());

	private static Map<Education,Image> imageByEducation;

	private SpliMoCharacterGenerator charGen;
	private LetUserChooseListener choiceCallback;

	private TreeView<Education> tree;
	private TreeItem<Education> root;

	private HBox content;
	private Label description;
	private Label heading;
	private Label reference;

	private Education selected;

	//-------------------------------------------------------------------
	static {
		imageByEducation = new HashMap<Education, Image>();
	}

	//-------------------------------------------------------------------
	public WizardPageEducation(Wizard wizard, SpliMoCharacterGenerator charGen, LetUserChooseListener choiceCallback) {
		super(wizard);
		this.charGen = charGen;
		this.choiceCallback = choiceCallback;

		initComponents();
		initLayout();
		initInteractivity();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		// Page Header
		setTitle(uiResources.getString("wizard.selectEducation.title"));
		// Page Image
		//setImageSize(300,400);


		description = new Label();
		description.setWrapText(true);
		description.getStyleClass().add("text-body");

		heading = new Label();
		heading.setWrapText(true);
		heading.getStyleClass().add("text-subheader");

		reference = new Label();
		reference.getStyleClass().add("base");

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

	}

	//-------------------------------------------------------------------
	private void initLayout() {
		tree.setMinWidth(200);
		tree.setMinHeight(400);

		VBox box = new VBox(heading, reference, description);
		VBox.setMargin(description, new Insets(20,0,0,0));
		VBox.setVgrow(description, Priority.ALWAYS);

		// Description scroll
		ScrollPane scroll = new ScrollPane(box);
		scroll.setMinWidth(300);
		scroll.setMaxWidth(500);
		scroll.setFitToWidth(true);

		/*
		 * Page content
		 */
		content = new HBox();
		content.setFillHeight(true);
		content.setSpacing(10);
		content.setAlignment(Pos.TOP_LEFT);
		content.getChildren().addAll(tree, scroll);
		content.setAlignment(Pos.TOP_LEFT);
		setContent(content);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		tree.getSelectionModel().selectedItemProperty().addListener(this);
	}

	//-------------------------------------------------------------------
	@Override
	public void changed(ObservableValue<? extends TreeItem<Education>> property, TreeItem<Education> oldEducation,
			TreeItem<Education> newItem) {
		if (newItem==null)
			return;

		selected = newItem.getValue();
		logger.info("Education now "+selected);

		// Update text
		heading.setText(selected.getName());
		reference.setText(selected.getProductName()+" "+selected.getPage());
		description.setText(selected.getHelpText());

		// Update image
		Image img = imageByEducation.get(selected);
		String plugin = selected.getPlugin().getID().toLowerCase();
		if (img==null) {
			String fname = plugin+"/data/education_"+selected.getKey()+".png";
//			InputStream in = ImageAnchor.class.getResourceAsStream(fname);
			InputStream in = selected.getPlugin().getClass().getResourceAsStream(fname);
			logger.info("Load "+selected.getPlugin().getClass().getName()+"/"+fname);
			if (in!=null) {
				img = new Image(in);
				imageByEducation.put(selected, img);
			} else if (selected.getVariantOf()!=null) {
				Education edu2 = SplitterMondCore.getEducation(selected.getVariantOf());
				img = imageByEducation.get(selected);
				if (img==null) {
					fname = plugin+"/data/education_"+edu2.getKey()+".png";
					in = edu2.getPlugin().getClass().getResourceAsStream(fname);
					if (in!=null) {
						img = new Image(in);
						imageByEducation.put(edu2, img);
					} else {
						logger.warn("Missing image at "+fname+" from class "+selected.getPlugin().getClass()+" = "+selected.getPlugin().getClass().getPackageName().replace(".", "/")+"/"+fname);
					}
				}
			} else
				logger.warn("Missing image at "+fname+" from class "+selected.getPlugin().getClass()+" = "+selected.getPlugin().getClass().getPackageName().replace(".", "/")+"/"+fname);
			setImage(img);
		}

		if (img!=null) {
			double scaleX = 300.0/img.getWidth();
			double scaleY = 400.0/img.getHeight();
			double scale  = Math.min(scaleX, scaleY);
			double scaledX = img.getWidth() * scale;
			double scaledY = img.getHeight() * scale;
			logger.trace(String.format("Scale image to %3.2f x %3.2f", scaledX, scaledY));
//			image.setImage(img);
//			image.setFitWidth(scaledX);
//			image.setFitHeight(scaledY);
//			Rectangle clip = new Rectangle(scaledX, scaledY);
//			clip.setArcWidth(50);
//			clip.setArcHeight(50);
//			image.setClip(clip);
			logger.info("Set image");
			setImage(img);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.WizardPage#pageLeft(org.prelle.javafx.CloseType)
	 */
	public void pageLeft() {
		if (selected!=null)
			charGen.selectEducation(selected, choiceCallback);
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