/**
 * 
 */
package org.prelle.splittermond.jfx.notes;

import java.util.Arrays;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.ManagedScreen;
import org.prelle.javafx.skin.ManagedScreenStructuredSkin;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.fluent.SpliMoCharGenConstants;

import javafx.geometry.Insets;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * @author Stefan
 *
 */
public class NotesScreen extends ManagedScreen implements
		GenerationEventListener {

	private static Logger logger = Logger.getLogger(SpliMoCharGenConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle UI = SpliMoCharGenConstants.RES;

	private CharacterController control;

	private TextArea tfNotes;
	private SpliMoCharacter model;
	
	//--------------------------------------------------------------------
	/**
	 */
	public NotesScreen(CharacterController control) {
		this.control = control;
		model = control.getModel();
		
		initComponents();
		initLayout();
		initInteractivity();
		setSkin(new ManagedScreenStructuredSkin(this));
		GenerationEventDispatcher.addListener(this);
		tfNotes.setText(model.getNotes());
 	}

	//-------------------------------------------------------------------
	private void initComponents() {
		getNavigButtons().add(CloseType.BACK);
		setTitle(UI.getString("screen.notes.title"));
		
		tfNotes = new TextArea();
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		getStyleClass().add("text-body");

		// Resources

//		resources.setMaxWidth(Double.MAX_VALUE);
//		HBox.setHgrow(resources, Priority.ALWAYS);
//		VBox.setVgrow(resources, Priority.ALWAYS);
		
		HBox content = new HBox();
		content.setSpacing(20);
		content.getChildren().addAll(tfNotes);
		HBox.setHgrow(tfNotes, Priority.NEVER);
//		HBox.setMargin(points, new Insets(0,0,20,0));
		HBox.setMargin(tfNotes  , new Insets(0,0,20,0));
		setContent(content);

//		// Description
//		descLayout = new VBox();
//		descLayout.getChildren().addAll(description);
//		ScrollPane descScroll = new ScrollPane(descLayout);
//		descScroll.setFitToWidth(true);
//		descScroll.setMaxHeight(Double.MAX_VALUE);
//		descScroll.setMaxWidth(Double.MAX_VALUE);
//		
//		// Flow; Resources and Powers
//		GridPane flow = new GridPane();
//		flow.add(lblResource, 0, 0);
//		flow.add(resources  , 0, 1);
//		flow.add(descScroll , 1, 1, 1,3);
//		flow.setVgap(20);
//		flow.setHgap(20);
//		ColumnConstraints col1 = new ColumnConstraints();
//		ColumnConstraints col2 = new ColumnConstraints();
//        col1.setPercentWidth(66);
//        col2.setPercentWidth(33);
//        flow.getColumnConstraints().addAll(col1, col2);
//
//		HBox content = new HBox();
//		content.setSpacing(20);
//		content.getChildren().addAll(points, flow);
//		HBox.setHgrow(flow, Priority.ALWAYS);
//		HBox.setMargin(points, new Insets(0,0,20,0));
//		HBox.setMargin(flow  , new Insets(0,0,20,0));
//		setContent(content);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
//		resources.getTable().getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
//			logger.info("Selected "+n);
//			descLayout.getChildren().clear();
//			if (n!=null) {
//				try {
//					String text = DESCRIPTIONS.getString("resource."+n.getResource().getId()+".text");
//					description.setText(text);
//					
//					descLayout.getChildren().addAll(description, getResourceTable(n.getResource()));
//
//				} catch (MissingResourceException e) {
//					logger.error("Missing key '"+e.getKey()+"' "+DESCRIPTIONS.getBaseBundleName());
//					description.setText("Missing key '"+e.getKey()+"' "+DESCRIPTIONS.getBaseBundleName());
//				}
//			}
//		});
			
	}

	//-------------------------------------------------------------------
	public void setData(SpliMoCharacter model) {
		setTitle(UI.getString("screen.notes.title")+" / "+model.getName());
		this.model = model;
		tfNotes.setText(model.getNotes());
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.ManagedScreen#close(org.prelle.javafx.ManagedScreen.CloseType)
	 */
	@Override
	public boolean close(CloseType type) {
		logger.debug("close("+type+")");
		if (type==CloseType.OK || type==CloseType.BACK) {
			String txt = tfNotes.getText();
			model.setNotes(txt);
		}
		return true;
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		switch (event.getType()) {
		case NOTES_CHANGED:
			logger.debug("rcv "+event.getType()+"   "+Arrays.toString((int[])event.getValue()));
			break;
		default:
			break;
		}		
	}

}
