/**
 * 
 */
package org.prelle.splittermond.jfx.creatures;

import java.io.InputStream;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.Wizard;
import org.prelle.javafx.WizardPage;
import org.prelle.splimo.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splimo.npc.NPCGenerator;

import javafx.geometry.Insets;
import javafx.scene.image.Image;

/**
 * @author prelle
 *
 */
public class WizardPageAttributes extends WizardPage {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle uiResources = SpliMoCharGenJFXConstants.UI;
	
	private NPCGenerator control;
	private CreatureAttributeEditPane pane;

	//-------------------------------------------------------------------
	public WizardPageAttributes(Wizard wizard, NPCGenerator ctrl) {
		super(wizard);
		this.control = ctrl;
		
		initComponents();
		initLayout();
		initInteractivity();
		
//		nextButton.set(false);
		finishButton.set(false);

		setData();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		// Page Header
		setTitle(uiResources.getString("wizard.creatureAttr.title"));
		// Page Image
		Image img = null;
		String fname = "data/Background.png";
		logger.debug("Load "+fname);
		InputStream in = getClass().getClassLoader().getResourceAsStream(fname);
		if (in!=null) {
			img = new Image(in);
		} else
			logger.warn("Missing image at "+fname);
		setImage(img);
		
		/*
		 * Page content
		 */
		pane = new CreatureAttributeEditPane(control); 
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		setImageInsets(new Insets(-40,0,0,0));
//		setImageSize(388,255);
		super.setContent(pane);
	}
	

	//-------------------------------------------------------------------
	private void initInteractivity() {
//		backgList.getSelectionModel().selectedItemProperty().addListener(this);
	}

	//-------------------------------------------------------------------
	private void setData() {
		pane.setData(control.getCreature());
	}

//	//-------------------------------------------------------------------
//	@Override
//	public void changed(ObservableValue<? extends Background> property, Background oldModel,
//			Background newModel) {
//		selected = newModel;
//
//		if (newModel!=null) {
//			try {
//				description.setText(newModel.getHelpText());
//			} catch (Exception e) {
//				description.setText("Missing property '"+newModel.getHelpI18NKey()+"'");
//			}
//		}
//		
////		logger.debug("Background now "+selected);
//		nextButton.set(selected!=null);
//		finishButton.set(selected!=null);
//	}
	
	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.gen.jfx.WizardPage#nextPage()
	 */
	@Override
	public void pageLeft(CloseType type) {
		if (type!=CloseType.NEXT && type!=CloseType.FINISH)
			return;
	}

}
