/**
 * 
 */
package org.prelle.splittermond.chargen.lvl.jfx;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Education;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Callback;

/**
 * @author prelle
 *
 */
public class SelectEducationPopUp extends HBox implements MyPopUpContent<Education>, ChangeListener<TreeItem<Education>> {

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

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);

	private static Map<Education,Image> imageByEducation;

	private TreeView<Education> tree;
	private TreeItem<Education> root;
	private ImageView image;
	private BooleanProperty readyProperty;
	
	private Education selected;

	//-------------------------------------------------------------------
	static {
		imageByEducation = new HashMap<Education, Image>();
	}

	//-------------------------------------------------------------------
	public SelectEducationPopUp() {
		readyProperty = new SimpleBooleanProperty();
		
		init();
	}

	//-------------------------------------------------------------------
	@Override
	public void changed(ObservableValue<? extends TreeItem<Education>> property, TreeItem<Education> oldEducation,
			TreeItem<Education> newItem) {
		if (newItem==null)
			return;

		Education newEducation = newItem.getValue();
		selected = newItem.getValue();
		logger.info("Education now "+newEducation);

		Image img = imageByEducation.get(newEducation);
		if (img==null) {
			String fname = "data/education_"+newEducation.getKey()+".png";
			logger.debug("Load "+fname);
			InputStream in = getClass().getClassLoader().getResourceAsStream(fname);
			if (in!=null) {
				img = new Image(in);
				imageByEducation.put(newEducation, img);
			} else if (newEducation.getVariantOf()!=null) {
				Education edu2 = SplitterMondCore.getEducation(newEducation.getVariantOf());
				img = imageByEducation.get(newEducation);
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
		image.setImage(img);
		
		readyProperty.set(true);
	}

	//-------------------------------------------------------------------
	private void init() {
		setFillHeight(true);
		setSpacing(10);

		//		content.setGridLinesVisible(false);
		//		content.setHgap(5);
		//		content.setVgap(5);
		//		content.setPadding(new Insets(3));

		image = new ImageView();
		image.setPreserveRatio(true);
		image.setFitHeight(400);
		//image.setFitWidth(500);

		root = new TreeItem<Education>();
		tree = new TreeView<Education>(root);
		tree.setShowRoot(false);
		tree.getSelectionModel().selectedItemProperty().addListener(this);
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
//		tree.setPrefWidth(250);
		tree.setMaxWidth(300);
//		tree.setPrefHeight(400);

		this.setPrefWidth(500);
		
		getChildren().addAll(tree, image);
		
		HBox.setHgrow(tree, Priority.SOMETIMES);
		HBox.setHgrow(image, Priority.SOMETIMES);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splittermond.chargen.lvl.jfx.MyPopUpContent#getReadyProperty()
	 */
	@Override
	public BooleanProperty getReadyProperty() {
		return readyProperty;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splittermond.chargen.lvl.jfx.MyPopUpContent#getSelected()
	 */
	@Override
	public Education getSelected() {
		return selected;
	}

}
