/**
 * 
 */
package org.prelle.splimo.chargen.gen.jfx;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Culture;
import org.prelle.splimo.Culture.Continent;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.chargen.LetUserChooseListener;
import org.prelle.splimo.chargen.SpliMoCharacterGenerator;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.fluent.SpliMoCharGenConstants;
import org.prelle.splimo.chargen.free.jfx.FreeSelectionDialog;
import org.prelle.splimo.free.FreeSelectionGenerator;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * @author prelle
 *
 */
public class SelectCulturePage extends WizardPage implements ChangeListener<TreeItem<Culture>>, GenerationEventListener {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle UI = SpliMoCharGenConstants.RES;

	private static Map<Culture,Image> imageBySelection;

	private static PropertyResourceBundle CORE = SplitterMondCore.getI18nResources();
	
	
	private SpliMoCharacterGenerator charGen;
	private LetUserChooseListener choiceCallback;
	
	private Map<Culture.Continent, TreeItem<Culture>> continents;
	private CheckBox includeUnusual;
	private Button freeCreation_b;
	private TreeView<Culture> options;
	private ImageView image;
	/**
	 * List of cultures that are currently shown as available
	 */
	private List<Culture> available;
	/**
	 * Contains TreeItems for all cultures, not just the available ones.
	 * This is to make sure that a once selected tree item can be
	 * still selected even after the race changed.
	 */
	private Map<Culture, TreeItem<Culture>> allItems;

	private VBox content;
	private VBox context;
	private Culture memorizedSelection;
	private Culture freeCreated;

	//-------------------------------------------------------------------
	static {
		imageBySelection = new HashMap<Culture, Image>();
	}

	//-------------------------------------------------------------------
	public SelectCulturePage(SpliMoCharacterGenerator chGen, LetUserChooseListener choiceCallback) {
		super(UI.getString("wizard.selectCulture.title"), 
				new Image(SelectCulturePage.class.getClassLoader().getResourceAsStream("data/Splittermond_hochkant.png")));
		this.charGen = chGen;
		GenerationEventDispatcher.addListener(this);
		this.choiceCallback = choiceCallback;
		nextButton.setDisable(true);
		finishButton.setDisable(!charGen.hasEnoughData());
		initInteractivity();
		// Fill with data
		fillData();
	}

	//-------------------------------------------------------------------
	private void fillData() {
		/*
		 * Fill with cultures - sort under continents
		 */
		available = charGen.getAvailableCultures();
		for (Culture cult : available) {
			addAvailableCulture(cult);
		}
		
		String fname = "data/Culture.png";
		InputStream in = getClass().getClassLoader().getResourceAsStream(fname);
		if (in!=null) {
			Image img = new Image(in);
			image.setImage(img);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.beans.value.ChangeListener#changed(javafx.beans.value.ObservableValue, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void changed(ObservableValue<? extends TreeItem<Culture>> property, TreeItem<Culture> oldValue,
			TreeItem<Culture> newValue2) {
		if (newValue2==null) {
			logger.error("Nothing selected");
			nextButton.setDisable(true);
			return;
		}
		
		Culture selectedCulture = newValue2.getValue();
		
		// Memorize the current selection
		memorizedSelection = selectedCulture;

		logger.info("Culture now "+selectedCulture);
		if (selectedCulture instanceof MyContinent) {
			nextButton.setDisable(true);
		} else {
			nextButton.setDisable(false);
		}

		/*
		 * Load image depending on selected culture
		 */
		if (selectedCulture.getKey()!=null) {
			Image img = imageBySelection.get(selectedCulture);
			if (img==null) {
				String fname = "data/culture_"+selectedCulture.getKey()+".png";
				InputStream in = getClass().getClassLoader().getResourceAsStream(fname);
				if (in!=null) {
					img = new Image(in);
					imageBySelection.put(selectedCulture, img);
				} else
					logger.warn("Missing image at "+fname);
			}
			image.setImage(img);
		}
		
		if (charGen!=null)
			finishButton.setDisable(!charGen.hasEnoughData());
	}

	//-------------------------------------------------------------------
	private void initializeContinents(TreeItem<Culture> root) {
		/*
		 * Set continents as high level tree nodes
		 */
		for (Continent cont : Culture.Continent.values()) {
			Culture continentRoot = new MyContinent(CORE.getString("continent."+cont.name().toLowerCase()));
			TreeItem<Culture> item = new TreeItem<Culture>(continentRoot);
			root.getChildren().add(item);
			continents.put(cont, item);
		}
	}

	//-------------------------------------------------------------------
	private void addAvailableCulture(Culture cult) {
		TreeItem<Culture> parent = continents.get(cult.getContinent());
		if (parent!=null) {
			TreeItem<Culture> item = allItems.get(cult);
			if (item==null) {
				item = new TreeItem<Culture>(cult);
				allItems.put(cult, item);
			}
			parent.getChildren().add(item);
			Collections.sort(parent.getChildren(), new Comparator<TreeItem<Culture>>() {

				@Override
				public int compare(TreeItem<Culture> o1, TreeItem<Culture> o2) {
					return o1.getValue().compareTo(o2.getValue());
				}
			});
		} else
			logger.error("No tree item for continent "+cult.getContinent());
	}

	//-------------------------------------------------------------------
	private void removeAvailableCulture(Culture cult) {
		available.remove(cult);
		
		TreeItem<Culture> parent = continents.get(cult.getContinent());
		if (parent!=null) {
			TreeItem<Culture> item = allItems.get(cult);
			if (item!=null) 
				parent.getChildren().remove(item);			
		} else
			logger.error("No tree item for continent "+cult.getContinent());
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.gen.jfx.WizardPage#getContent()
	 */
	@Override
	Parent getContent() {
		continents = new HashMap<Culture.Continent, TreeItem<Culture>>();

		allItems = new HashMap<Culture, TreeItem<Culture>>();
		available = new ArrayList<Culture>();
		
		image = new ImageView();
		image.setFitHeight(250);

		TreeItem<Culture> root = new TreeItem<Culture>(null);
		root.setExpanded(true);
		
		initializeContinents(root);
		
		options = new TreeView<Culture>(root);
		options.setPrefWidth(354);
		options.setMinHeight(200);
		options.setPrefHeight(300);
		options.setShowRoot(false);

		options.setCellFactory(new Callback<TreeView<Culture>,TreeCell<Culture>>(){
            @Override
            public TreeCell<Culture> call(TreeView<Culture> p) {
                return new CultureTreeCell();
            }
        });
		
		content = new VBox(10);
		content.setAlignment(Pos.TOP_CENTER);
		VBox.setVgrow(options, Priority.ALWAYS);
		content.getChildren().addAll(options, image);
		return content;
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		// Listen to selections
		options.getSelectionModel().selectedItemProperty().addListener(this);
		includeUnusual.selectedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> checkbox,
					Boolean oldVal, Boolean newVal) {
				charGen.setIncludeUncommonCultures(newVal);
			}
		});

		// Listen to button
		freeCreation_b.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FreeSelectionGenerator freeGen = charGen.getFreeCreatorCulture();
				// Initialize with current selection - if there is any
				TreeItem<Culture> sel = options.getSelectionModel().getSelectedItem();
				if (sel!=null && sel.getValue()!=null) {
					logger.debug("pre-fill free selection dialog with "+sel.getValue());
					freeGen.setFrom(sel.getValue());
				}
				
				FreeSelectionDialog dialog = new FreeSelectionDialog(freeGen);
				Stage stage = new Stage();
				stage.setTitle(UI.getString("freeselect.title.culture"));
				Scene scene2 = new Scene(dialog);
				scene2.getStylesheets().add("css/default.css");
				stage.setScene(scene2);
				stage.showAndWait();
				
				if (!dialog.hasBeenCancelled()) {
					freeCreated = freeGen.getAsCulture();
					charGen.selectCulture(freeCreated, choiceCallback);
					TreeItem<Culture> toAdd = new TreeItem<Culture>(freeCreated);
					options.getRoot().getChildren().add(toAdd);
					options.getSelectionModel().select(toAdd);
					nextButton.setDisable(false);
					SelectCulturePage.this.nextPage();
				} else {
					freeCreated = null;
				}
			}
		});
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.gen.jfx.WizardPage#nextPage()
	 */
	void nextPage() {
		Culture toSelect = (freeCreated!=null)?freeCreated:options.getSelectionModel().getSelectedItem().getValue();
		logger.debug("Select "+toSelect);
		this.setDisable(true);
		charGen.selectCulture(
				toSelect,
				choiceCallback
				);
		this.setDisable(false);

		super.nextPage();
	}

	//-------------------------------------------------------------------
	/**
	 * Compare previously available cultures with now offered cultures
	 */
	private void updateCultures(List<Culture> newAvailable) {
		logger.debug("updateCultures: "+newAvailable);
		Culture keepMemorized = memorizedSelection;
		
		List<Culture> notAvailableAnymore = new ArrayList<Culture>(available);
		for (Culture newAvail : newAvailable) {
			if (available.contains(newAvail)) {
				// Culture was available before and will stay that way
				notAvailableAnymore.remove(newAvail);
			} else {
				// Culture wasn't previously available
				logger.warn("newly available "+newAvail);
				available.add(newAvail);
				addAvailableCulture(newAvail);
			}
		}
		
		// All cultures still in "notAvailableAnymore" are exactly that
		// If the currently selected culture is among them - deselect it
//		if (notAvailableAnymore.contains(charGen.g))
		
		for (Culture toRemove : notAvailableAnymore) {
//			logger.warn("TODO: remove culture "+toRemove);
			removeAvailableCulture(toRemove);
		}
		
		memorizedSelection = keepMemorized;
		if (newAvailable.contains(memorizedSelection))
			options.getSelectionModel().select(allItems.get(memorizedSelection));
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		switch (event.getType()) {
		case CULTURE_OFFER_CHANGED:
			updateCultures((List<Culture>) event.getKey());
			break;
		default:
		}
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.gen.jfx.WizardPage#getContextMenu()
	 */
	@Override
	public Parent getContextMenu() {
		Label placeholder = new Label(" ");
		placeholder.getStyleClass().add("wizard-heading");
		includeUnusual = new CheckBox(UI.getString("wizard.selectCulture.showUnusual"));
		includeUnusual.setWrapText(true);
		includeUnusual.getStyleClass().add("wizard-context");
		freeCreation_b = new Button(UI.getString("wizard.freeCreation"));
	
		
		context = new VBox(15);
		context.setPrefWidth(104);
		context.getChildren().addAll(placeholder, includeUnusual, freeCreation_b);
		return context;
	}
	
}

class MyContinent extends Culture {

	private String label;

	//-------------------------------------------------------------------
	public MyContinent(String label) {
		this.label = label;
	}

	//-------------------------------------------------------------------
	public String toString() {
		return label;
	}

}

class CultureTreeCell extends TreeCell<Culture> {
	
	@Override protected void updateItem(Culture item, boolean empty) {
        // calling super here is very important - don't skip this!
        super.updateItem(item, empty);
          
        // format the number as if it were a monetary value using the 
        // formatting relevant to the current locale. This would format
        // 43.68 as "$43.68", and -23.67 as "-$23.67"
        if (item==null)
        	setText("");
        else {
        	if (item instanceof MyContinent) {
        		setText( ((MyContinent)item).toString() );
        	} else
        		setText( item.getName() );
        }
 
        // change the text fill based on whether it is positive (green)
        // or negative (red). If the cell is selected, the text will 
        // always be white (so that it can be read against the blue 
        // background), and if the value is zero, we'll make it black.
//        if (item != null) {
//            double value = item.doubleValue();
//            setTextFill(isSelected() ? Color.WHITE :
//                value == 0 ? Color.BLACK :
//                value < 0 ? Color.RED : Color.GREEN);
//        }
    }
}