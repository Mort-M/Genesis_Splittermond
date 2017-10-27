/**
 *
 */
package org.prelle.splittermond.jfx.creatures;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import org.apache.log4j.Logger;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.Wizard;
import org.prelle.javafx.WizardPage;
import org.prelle.splimo.charctrl.BackgroundController;
import org.prelle.splimo.chargen.SpliMoCharacterGenerator;
import org.prelle.splimo.chargen.LetUserChooseListener;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.npc.CreatureTypeController;
import org.prelle.splimo.npc.NPCGenerator;

/**
 * @author prelle
 *
 */
public class WizardPageCreatureType extends WizardPage {

	private final static Logger logger = Logger.getLogger("splittermond.jfx");

	private static PropertyResourceBundle uiResources = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splimo-chargen");

	private NPCGenerator control;
	private CreatureTypePane pane;

	//-------------------------------------------------------------------
	public WizardPageCreatureType(Wizard wizard, NPCGenerator ctrl) {
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
		setTitle(uiResources.getString("wizard.creatureType.title"));
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
		pane = new CreatureTypePane(control.getCreatureTypeController());
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
