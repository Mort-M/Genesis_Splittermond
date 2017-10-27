/**
 * 
 */
package org.prelle.splimo.chargen.gen.jfx;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

import org.apache.log4j.Logger;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.charctrl.GeneratingResourceController;
import org.prelle.splimo.chargen.SpliMoCharacterGenerator;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splittermond.jfx.resources.ResourcePane;

/**
 * @author prelle
 *
 */
public class DistributeResourcesPage extends WizardPage implements GenerationEventListener {

	private final static Logger logger = Logger.getLogger("fxui.resources");

	private static PropertyResourceBundle uiResources = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/ui");

	private SpliMoCharacterGenerator charGen;
	private GeneratingResourceController  control;
	private SpliMoCharacter    model;

	private VBox context;
	private ResourcePane content;
	private Label pointsLeft;
	private CheckBox allowMaxResources;
//	private Button add_b;
//	
//	private ChoiceBox<Resource> addChoice;
//	private TableView<ResourceReference> table;
//
//	private TableColumn<ResourceReference, Number> valueCol;
//	private TableColumn<ResourceReference, String>  nameCol;
//	/* for mapping */
//	private Map<ResourceReference, ResourceSelectedCell> itemsByResource;
//	
//	private ContextMenu contextMenu;
//	private MenuItem join;
//	private MenuItem split;

	//-------------------------------------------------------------------
	public DistributeResourcesPage(SpliMoCharacter model, SpliMoCharacterGenerator charGen) {
		this.charGen = charGen;
		this.control = charGen.getResourceGenerator();
		this.model   = model;

		pageInit(uiResources.getString("wizard.distrResource.title"), 
				new Image(DistributeResourcesPage.class.getClassLoader().getResourceAsStream("data/Splittermond_hochkant.png")));
		nextButton.setDisable(control.getPointsLeft()!=0);
		finishButton.setDisable(!charGen.hasEnoughData());
		
		GenerationEventDispatcher.addListener(this);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.gen.jfx.WizardPage#getContent()
	 */
	@Override
	Parent getContent() {
		content = new ResourcePane(control, false, true);
		content.setData(model);
		return content;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.gen.jfx.WizardPage#getContextMenu()
	 */
	@Override
	public Parent getContextMenu() {
		Label placeholder = new Label(" ");
		placeholder.getStyleClass().add("wizard-heading");

		// Line that reflects remaining points to distribute
		pointsLeft = new Label(String.valueOf(control.getPointsLeft()));
		pointsLeft.setStyle("-fx-font-size: 400%");
		pointsLeft.getStyleClass().add("wizard-context");
		Label pointsLeft_f = new Label(uiResources.getString("wizard.distrResource.pointsLeft")+" ");
		pointsLeft_f.getStyleClass().add("wizard-context");

		// Checkbox to allow to maximize resources
		allowMaxResources = new CheckBox(uiResources.getString("wizard.distrResource.allowMax"));
		allowMaxResources.setWrapText(true);
		allowMaxResources.getStyleClass().add("wizard-context");
		allowMaxResources.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean arg1, Boolean newVal) {
				control.setAllowMaxResources(newVal);
			}
		});


		context = new VBox(15);
		context.setAlignment(Pos.TOP_CENTER);
		context.setPrefWidth(104);
		context.getChildren().addAll(placeholder, pointsLeft, pointsLeft_f, allowMaxResources);
		return context;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.gen.jfx.WizardPage#nextPage()
	 */
	void nextPage() {
		super.nextPage();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		if (event.getType()==GenerationEventType.POINTS_LEFT_RESOURCES) {
			logger.debug("free points for resources changed to "+event.getValue());
			pointsLeft.setText(String.valueOf(event.getValue()));
			// Only enable NEXT button when all points are distributed
			nextButton.setDisable(control.getPointsLeft()!=0);
			finishButton.setDisable(!charGen.hasEnoughData());
		}
	}

//	//-------------------------------------------------------------------
//	/**
//	 * @see javafx.event.EventHandler#handle(javafx.event.Event)
//	 */
//	@Override
//	public void handle(ActionEvent event) {
//		// TODO Auto-generated method stub
//		logger.debug("Action : "+event);
//		if (event.getSource()==add_b) {
//			Resource res = addChoice.getSelectionModel().getSelectedItem();
//			control.openResource(res);
////		} else if (event.getSource()==deselect_b) {
////			ResourceReference ref = table.getSelectionModel().getSelectedItem();
////			if (charGen.deselect(ref))
////				table.getItems().remove(ref);
//		}
//	}

}

//class ResourceSelectedCell extends TableCell<ResourceReference, Number> implements ChangeListener<Integer>{
//	
//	private ListSpinner<Integer> box;
//	private ResourceGenerator charGen;
//	private boolean setByEvent;
//	private DistributeResourcesPage parent;
//	private ResourceReference resource;
//	
//	//-------------------------------------------------------------------
//	public ResourceSelectedCell(DistributeResourcesPage parent, ResourceGenerator charGen) {
//		this.charGen = charGen;
//		this.parent  = parent;
//		if (parent==null)
//			throw new NullPointerException("parent not set");
//		box = new ListSpinner<>(-2, 6, 1);
//		box.valueProperty().addListener(this);
//		box.setCyclic(false);
////		ListSpinnerSkin<Integer> skin = new ListSpinnerSkin<Integer>(box);
////		skin.setArrowPosition(ArrowPosition.SPLIT);
////		box.setSkin(skin);
//////		((ListSpinnerSkin<Race>)box.getSkin()).setArrowPosition(ArrowPosition.SPLIT);
//		setAlignment(Pos.CENTER);
//	}
//	
//	//-------------------------------------------------------------------
//	/**
//	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
//	 */
//	@Override
//	protected void updateItem(Number item, boolean empty) {
//		super.updateItem(item, empty);
//		
//		if (item==null || empty || getTableRow()==null) {
//			this.setGraphic(null);
//			return;
//		}
//		
//		resource = (ResourceReference) getTableRow().getItem();
//		if (resource==null)
//			return;
//		parent.setMapping(resource, this);
//		box.valueProperty().set(resource.getValue());
//		
//		this.setGraphic(box);
//	}
//
//	//-------------------------------------------------------------------
//	/**
//	 * @see javafx.beans.value.ChangeListener#changed(javafx.beans.value.ObservableValue, java.lang.Object, java.lang.Object)
//	 */
//	@Override
//	public void changed(ObservableValue<? extends Integer> item, Integer old, Integer val) {
//		if (setByEvent) {
//			setByEvent = false;
//			return;
//		}
//		setByEvent = false;
//		if (val>resource.getValue() && charGen.canBeIncreased(resource)) {
//			charGen.increase(resource);
//		} else if (val<resource.getValue() && charGen.canBeDecreased(resource)) {
//			charGen.decrease(resource);
//		} else {
//			// Revert back
//			box.valueProperty().set(resource.getValue());
//		}
//	}
//	
//	//-------------------------------------------------------------------
//	void setByEvent(int val) {
//		setByEvent = true;
//		box.valueProperty().setValue(val);
//	}
//}