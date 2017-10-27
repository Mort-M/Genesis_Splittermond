/**
 * 
 */
package org.prelle.splimo.chargen.lvl.jfx;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import org.apache.log4j.Logger;
import org.prelle.splimo.Culture;
import org.prelle.splimo.Culture.Continent;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.chargen.SpliMoCharacterGenerator;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;

/**
 * @author prelle
 *
 */
public class SelectCulturePopUp extends VBox implements MyPopUpContent<Culture>, ChangeListener<TreeItem<Culture>>, GenerationEventListener {

	private final static Logger logger = Logger.getLogger("chargen.ui");

	private static Map<Culture,Image> imageBySelection;

	private static PropertyResourceBundle ruleResources = SplitterMondCore.getI18nResources();
	private static PropertyResourceBundle uiResources = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/ui");
	
	
	private SpliMoCharacterGenerator charGen;
	
	private Map<Culture.Continent, TreeItem<Culture>> continents;
	private CheckBox includeUnsual;
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

	private Culture memorizedSelection;
	
	private BooleanProperty readyProperty;
	private Culture selectedCulture;

	//-------------------------------------------------------------------
	static {
		imageBySelection = new HashMap<Culture, Image>();
	}

	//-------------------------------------------------------------------
	public SelectCulturePopUp(SpliMoCharacterGenerator chGen) {
		this.charGen = chGen;
		GenerationEventDispatcher.addListener(this);
		readyProperty = new SimpleBooleanProperty();
		
		allItems   = new HashMap<Culture, TreeItem<Culture>>();
		available  = new ArrayList<Culture>();
		continents = new HashMap<Culture.Continent, TreeItem<Culture>>();

		init();
		
		// Listen to selections
		options.getSelectionModel().selectedItemProperty().addListener(this);
		includeUnsual.selectedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> checkbox,
					Boolean oldVal, Boolean newVal) {
				charGen.setIncludeUncommonCultures(newVal);
			}
		});
		// Fill with data
		fillData();
	}

	//-------------------------------------------------------------------
	void init() {
		setSpacing(10);
		setFillWidth(true);
		setPadding(new Insets(3));
		setAlignment(Pos.TOP_CENTER);
		

		includeUnsual = new CheckBox(uiResources.getString("wizard.selectCulture.showUnsual"));

		image = new ImageView();
		image.setFitHeight(250);

		TreeItem<Culture> root = new TreeItem<Culture>(null);
		root.setExpanded(true);
		
		initializeContinents(root);
		
		options = new TreeView<Culture>(root);
		options.setPrefWidth(360);
		options.setMinHeight(200);
		options.setShowRoot(false);

		options.setCellFactory(new Callback<TreeView<Culture>,TreeCell<Culture>>(){
            @Override
            public TreeCell<Culture> call(TreeView<Culture> p) {
                return new CultureTreeCell();
            }
        });
		
		
		VBox.setVgrow(options, Priority.ALWAYS);
		VBox.setVgrow(image  , Priority.ALWAYS);
		getChildren().addAll(includeUnsual, options, image);
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
			return;
		}
		
		selectedCulture = newValue2.getValue();
		
		// Memorize the current selection
		memorizedSelection = selectedCulture;

		logger.info("Culture now "+selectedCulture);
		readyProperty.set(!(selectedCulture instanceof MyContinent));

		/*
		 * Load image depending on selected culture
		 */
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

	//-------------------------------------------------------------------
	private void initializeContinents(TreeItem<Culture> root) {
		/*
		 * Set continents as high level tree nodes
		 */
		for (Continent cont : Culture.Continent.values()) {
			Culture continentRoot = new MyContinent(ruleResources.getString("continent."+cont.name().toLowerCase()));
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

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.lvl.jfx.MyPopUpContent#getReadyProperty()
	 */
	@Override
	public BooleanProperty getReadyProperty() {
		return readyProperty;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.lvl.jfx.MyPopUpContent#getSelected()
	 */
	@Override
	public Culture getSelected() {
		return selectedCulture;
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
        		setText( SplitterMondCore.getI18nResources().getString("culture."+item.getKey()));
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