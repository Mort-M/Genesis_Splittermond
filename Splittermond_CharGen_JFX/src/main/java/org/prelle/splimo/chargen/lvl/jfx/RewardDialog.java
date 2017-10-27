/**
 * 
 */
package org.prelle.splimo.chargen.lvl.jfx;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.splimo.Resource;
import org.prelle.splimo.ResourceReference;
import org.prelle.splimo.RewardImpl;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.ResourceController;
import org.prelle.splimo.modifications.ResourceModification;
import org.prelle.splittermond.jfx.resources.ResourcePane;

import de.rpgframework.genericrpg.Reward;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class RewardDialog extends VBox {
	
	private static PropertyResourceBundle res = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/ui");
	
	private TextField title;
	private GridPane form;
	private TextField exp;
	private ResourcePane resources;
	
	private TilePane buttonBar;
	private Button ok;
	private Button cancel;
	
	private RewardImpl result;
	
	//--------------------------------------------------------------------
	public RewardDialog() {
		initComponents();
		initLayout();
		initInteractivity();
		
		validateInput();
	}
	
	//--------------------------------------------------------------------
	private void initComponents() {
		/*
		 * Form
		 */
		Label title_l = new Label(res.getString("label.title"));
		Label exp_l = new Label(res.getString("label.experience"));
		Label res_l = new Label(res.getString("label.resource"));
		res_l.setMaxHeight(Double.MAX_VALUE);
		res_l.setAlignment(Pos.TOP_LEFT);
		title = new TextField(); 
		exp = new TextField(); 
		exp.setMaxWidth(100);
		resources = new ResourcePane(new RewardResourceController(), true, false);
		resources.setPrefHeight(150);
		
//		exp_l.getStyleClass().add("wizard-heading");
//		res_l.getStyleClass().add("wizard-heading");
		
		form = new GridPane();
		form.add(title_l  , 0, 0);
		form.add(title    , 1, 0);
		form.add(exp_l    , 0, 1);
		form.add(exp      , 1, 1);
		form.add(res_l    , 0, 2);
		form.add(resources, 1, 2);
		form.setVgap(5);
		form.setHgap(5);
		
		/*
		 * Buttons
		 */
		ok     = new Button(res.getString("button.apply"));
		cancel = new Button(res.getString("button.cancel"));
		buttonBar = new TilePane(ok, cancel);
		buttonBar.setHgap(6);
		buttonBar.getStyleClass().add("wizard-buttonbar");
		buttonBar.setAlignment(Pos.CENTER);
	}
	
	//--------------------------------------------------------------------
	private void initLayout() {
		form.setPadding(new Insets(3));
		getChildren().addAll(form, buttonBar);
	}
	
	//--------------------------------------------------------------------
	private void initInteractivity() {
		title.textProperty().addListener(new ChangeListener<String>(){
			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String newVal) {
				validateInput();
			}});
		exp.textProperty().addListener(new ChangeListener<String>(){
			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String newVal) {
				validateInput();
			}});
		
		/*
		 * Apply
		 */
		ok.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				result = new RewardImpl();
				result.setTitle(title.getText());
				result.setExperiencePoints(Integer.parseInt(exp.getText()));
				result.setDate(new Date(System.currentTimeMillis()));
				// Add exp
//				int ep = Integer.parseInt(exp.getText());
//				model.setExperienceFree(model.getExperienceFree() + ep);
				// Add resources
				for (ResourceReference ref : resources.getResources()) {
//					// Add to model
//					model.addResource(ref);
					// Add to history
					ResourceModification mod = new ResourceModification(ref.getResource(), ref.getValue());
					mod.setDate(new Date(System.currentTimeMillis()));
//					model.addToHistory(mod);
					// Add to reward
					result.addModification(mod);
				}
				
				getScene().getWindow().hide();
			}
		});
		/*
		 * Cancel
		 */
		cancel.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				result = null;
				getScene().getWindow().hide();
			}
		});
	}
	
	//--------------------------------------------------------------------
	private void validateInput() {
		boolean enabled = true;
		if (title.getLength()<=3) { 
			enabled = false;
		}
		if (exp.getLength()>0) { 
			try {
				Integer.parseInt(exp.getText());
			} catch (NumberFormatException e) {
				enabled = false;
			}
		}
		
		ok.setDisable(!enabled);
	}

	//-------------------------------------------------------------------
	/**
	 * @return the result
	 */
	public Reward getResult() {
		return result;
	}

}

class RewardResourceController implements ResourceController {

	@Override
	public List<Resource> getAvailableResources() {
		return SplitterMondCore.getResources();
	}

	@Override
	public ResourceReference openResource(Resource res) {
		return new ResourceReference(res, 1);
	}

	@Override
	public boolean canBeIncreased(ResourceReference ref) {
		return ref.getValue()<6;
	}

	@Override
	public boolean canBeDecreased(ResourceReference ref) {
		return ref.getValue()>0;
	}

	@Override
	public boolean canBeDeselected(ResourceReference ref) {
		return true;
	}

	@Override
	public boolean increase(ResourceReference ref) {
		if (canBeIncreased(ref)) {
			ref.setValue(ref.getValue()+1);
			return true;
		}
		return false;
	}

	@Override
	public boolean decrease(ResourceReference ref) {
		if (canBeDecreased(ref)) {
			ref.setValue(ref.getValue()-1);
			return true;
		}
		return false;
	}

	@Override
	public boolean canBeSplit(ResourceReference ref) {
		return false;
	}

	@Override
	public ResourceReference split(ResourceReference ref) {
		return null;
	}

	@Override
	public boolean canBeJoined(ResourceReference... resources) {
		return false;
	}

	@Override
	public void join(ResourceReference... resources) {
	}

	@Override
	public boolean deselect(ResourceReference ref) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canBeTrashed(ResourceReference ref) {
		return false;
	}

	@Override
	public boolean trash(ResourceReference ref) {
		return false;
	}

	@Override
	public ResourceReference findResourceReference(Resource res, String descr,
			String idref) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getToDos() {
		return new ArrayList<>();
	}
	
}