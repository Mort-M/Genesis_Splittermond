/**
 * 
 */
package org.prelle.splimo.chargen.fluent;

import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.chargen.jfx.SpliMoCharGenJFXConstants;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

/**
 * @author prelle
 *
 */
public class MoonsignDeityExpGrid extends GridPane {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle RES = SpliMoCharGenJFXConstants.UI;
	
	private CharacterController  control;
	
	private Label lbExpFree;
	private Label lbExpInvest;
	private Label lbExpTotal;
	private Label lbLevel;
	
	private Button btnAddExp;

	//-------------------------------------------------------------------
	public MoonsignDeityExpGrid(CharacterController control) {
		this.control = control;
		initComponents();
		initLayout();
		
		refresh();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		lbExpFree     = new Label();
		lbExpInvest   = new Label();
		lbExpTotal    = new Label();
		lbLevel       = new Label();
		btnAddExp     = new Button("\uE0C5");
		btnAddExp.getStyleClass().add("mini-button");
		
		lbExpFree.setStyle("-fx-font-size: 150%");
		lbExpInvest.setStyle("-fx-font-size: 150%; -fx-pref-width: 5em");
		lbExpTotal.setStyle("-fx-font-size: 150%");
		lbLevel.setStyle("-fx-font-size: 150%; -fx-pref-width: 3em");
		btnAddExp.setStyle("-fx-font-size: 150%");
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		setStyle("-fx-background-color: white;");
		Label hdExperience= new Label(RES.getString("label.experience"));
		Label hdExpFree  = new Label(RES.getString("label.free"));
		Label hdExpInvest= new Label(RES.getString("label.invested"));
		Label hdExpTotal = new Label(RES.getString("label.total"));
		Label hdLevel    = new Label(RES.getString("label.level"));
		
		hdExpFree.getStyleClass().add("table-head");
		hdExpInvest.getStyleClass().add("table-head");
		hdExpTotal.getStyleClass().add("table-head");
		
		add(hdExperience, 0,3, 4,1);
		add(hdExpFree   , 0,4);
		add(lbExpFree   , 1,4);
		add(btnAddExp   , 2,4, 2,1);
		add(hdExpInvest , 0,5);
		add(lbExpInvest , 1,5);
		add(hdLevel     , 2,5);
		add(lbLevel     , 3,5);
		add(hdExpTotal  , 0,6);
		add(lbExpTotal  , 1,6);
		
//		getColumnConstraints().add(new Col)

		for (Node child : this.getChildren()) {
			logger.debug("child = "+child);
			int x = GridPane.getColumnIndex(child);
			int y = GridPane.getRowIndex(child);
			// Set style
			if (x==0 || x==2)
				child.getStyleClass().addAll("base","table-head");
			
			// Let node fill every available space
			if (child instanceof Region && !(child instanceof Button)) {
				((Region)child).setMaxWidth(Double.MAX_VALUE);
				if (x==0 || x==2)
					((Region)child).setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			}
			
			if (x==1 || x==3) {
				GridPane.setMargin(child, new Insets( 4));
				GridPane.setFillWidth(child, true);
			}
		}
	}
	
	//-------------------------------------------------------------------
	private void refresh() {
		SpliMoCharacter model = control.getModel();
		
		lbExpFree.setText(String.valueOf(model.getExperienceFree()));
		lbExpInvest.setText(String.valueOf(model.getExperienceInvested()));
		lbExpTotal.setText(String.valueOf(model.getExperienceInvested()+model.getExperienceFree()));
		lbLevel.setText(String.valueOf(model.getLevel()));
	}

}
