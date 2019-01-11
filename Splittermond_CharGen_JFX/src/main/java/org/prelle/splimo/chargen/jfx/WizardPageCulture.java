/**
 * 
 */
package org.prelle.splimo.chargen.jfx;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.Wizard;
import org.prelle.javafx.WizardPage;
import org.prelle.splimo.Culture;
import org.prelle.splimo.Culture.Continent;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.chargen.LetUserChooseListener;
import org.prelle.splimo.chargen.SpliMoCharacterGenerator;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

/**
 * @author prelle
 *
 */
public class WizardPageCulture extends WizardPage implements GenerationEventListener {

	//-------------------------------------------------------------------
	class MyContinent extends Culture {
		private String label;
		private String id;
		public MyContinent(String id, String label) {this.id=id; this.label = label;}
		public String toString() {return label;}
		public String getKey() {return id;}

	}

	//-------------------------------------------------------------------
	class CultureTreeCell extends TreeCell<Culture> {
		@Override protected void updateItem(Culture item, boolean empty) {
			super.updateItem(item, empty);
			if (item==null)
				setText("");
			else {
				if (item instanceof MyContinent) {
					setText( ((MyContinent)item).toString() );
				} else
					setText( item.getName() );
			}
		}
	}


	private static Logger logger = LogManager.getLogger("splittermond.jfx");

	private static PropertyResourceBundle UI = SpliMoCharGenJFXConstants.UI;

	private static Map<Culture,Image> imageByRace;

	private static PropertyResourceBundle CORE = SplitterMondCore.getI18nResources();

	private SpliMoCharacterGenerator charGen;
	private LetUserChooseListener choiceCallback;
	
	private Map<Culture.Continent, TreeItem<Culture>> continents;
	private CheckBox includeUnusual;
	private TreeView<Culture> options;
	private Label description;
	
	private VBox content;

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

	//-------------------------------------------------------------------
	static {
		imageByRace = new HashMap<Culture, Image>();
	}

	//-------------------------------------------------------------------
	public WizardPageCulture(Wizard wizard, SpliMoCharacterGenerator charGen, LetUserChooseListener choiceCallback) {
		super(wizard);
		this.charGen = charGen;
		this.choiceCallback = choiceCallback;

		GenerationEventDispatcher.addListener(this);
		
		// Prepare continents
		continents = new HashMap<Culture.Continent, TreeItem<Culture>>();

		allItems = new HashMap<Culture, TreeItem<Culture>>();
		available = new ArrayList<Culture>();

		initComponents();
		initLayout();
		initInteractivity();
		fillData();
		
		nextButton.set(false);
		finishButton.set(false);
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		setTitle(UI.getString("wizard.selectCulture.title"));
		
		includeUnusual = new CheckBox(UI.getString("wizard.selectCulture.showUnusual"));

		TreeItem<Culture> root = new TreeItem<Culture>(null);
		root.setExpanded(true);
		
		initializeContinents(root);
		
		options = new TreeView<Culture>(root);
		options.setMinHeight(200);
		options.setShowRoot(false);

		options.setCellFactory(new Callback<TreeView<Culture>,TreeCell<Culture>>(){
            @Override
            public TreeCell<Culture> call(TreeView<Culture> p) {
                return new CultureTreeCell();
            }
        });
		
		content = new VBox();
		content.setSpacing(5);
		
		description = new Label();
		description.setWrapText(true);
		description.getStyleClass().add("text-body");
	}

	//-------------------------------------------------------------------
	private void initLayout() {
//		HBox.setMargin(ivSideImage, new Insets(20,0,0,0));
		description.setStyle("-fx-pref-width: 30em");
//		description.setPrefWidth(300);
		
		HBox sideBySide = new HBox(20);
		sideBySide.getChildren().addAll(options, description);
		
		content.getChildren().addAll(includeUnusual, sideBySide);
		super.setContent(content);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		// Listen to selections
		options.getSelectionModel().selectedItemProperty().addListener((ov,o,n) -> update(n.getValue()));
		includeUnusual.selectedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> checkbox,
					Boolean oldVal, Boolean newVal) {
				charGen.setIncludeUncommonCultures(newVal);
			}
		});
	}

	//-------------------------------------------------------------------
	private void update(Culture value) {
		logger.debug("updateCulture("+value+" / "+value.getKey()+")");
		memorizedSelection = value;
		
		if (value instanceof MyContinent) {
			nextButton.set(false); // Allow
			finishButton.set(false);
			description.setText(null);
		} else {
			nextButton.set(true); // Allow
			finishButton.set(true);
		}
		
		/*
		 * Description
		 */
		String pageLine = "";
		String helpLine = "";
		try {
			if (value.getKey()!=null) {
				// Line 1
				if (!(value instanceof MyContinent)) 
					pageLine = String.valueOf(value.getProductNameShort()+" "+value.getPage());
				// Line 2
				if (!(value instanceof MyContinent)) 
					helpLine = value.getHelpResourceBundle().getString("culture."+value.getKey()+".desc");
			}
			description.setText(pageLine+"\n\n"+helpLine);
		} catch (MissingResourceException e) {
			description.setText("Missing property '"+e.getKey()+"' in "+value.getHelpResourceBundle().getBaseBundleName());
		}
		
		/*
		 * Image
		 */
		Image image = imageByRace.get(value);
		if (image==null) {
			String fname = "data/culture_"+value.getKey()+".png";
			InputStream in = getClass().getClassLoader().getResourceAsStream(fname);
			if (in==null) {
				in = getClass().getClassLoader().getResourceAsStream("data/Culture.png");
			}
			if (in==null) {
				logger.warn("Missing image at "+fname);
			} else {
				image = new Image(in);
				imageByRace.put(value, image);
			}
		}
		setImage(image);
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
			Platform.runLater(new Runnable(){public void run(){updateCultures((List<Culture>) event.getKey());}});
			break;
		default:
		}
	}
	
	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.WizardPage#pageLeft(org.prelle.javafx.CloseType)
	 */
	@Override
	public void pageLeft(CloseType type) {
		if (type==CloseType.CANCEL || type==CloseType.PREVIOUS)
			return;
		Culture toSelect = options.getSelectionModel().getSelectedItem().getValue();
		logger.debug("Select "+toSelect);
		/*
		 * Call in extra thread, since it invokes blocking dialogs
		 */
		charGen.selectCulture(toSelect, choiceCallback);
	}

	//-------------------------------------------------------------------
	private void initializeContinents(TreeItem<Culture> root) {
		/*
		 * Set continents as high level tree nodes
		 */
		for (Continent cont : Culture.Continent.values()) {
			Culture continentRoot = new MyContinent("continent."+cont.name().toLowerCase(), CORE.getString("continent."+cont.name().toLowerCase()));
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
			setImage(new Image(in));
		}
	}

	//-------------------------------------------------------------------
	/**
	 * Compare previously available cultures with now offered cultures
	 */
	private void updateCultures(List<Culture> newAvailable) {
		logger.debug("updateCultures");
		Culture keepMemorized = memorizedSelection;
		
		List<Culture> notAvailableAnymore = new ArrayList<Culture>(available);
		for (Culture newAvail : newAvailable) {
			if (available.contains(newAvail)) {
				// Culture was available before and will stay that way
				notAvailableAnymore.remove(newAvail);
			} else {
				// Culture wasn't previously available
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

}
