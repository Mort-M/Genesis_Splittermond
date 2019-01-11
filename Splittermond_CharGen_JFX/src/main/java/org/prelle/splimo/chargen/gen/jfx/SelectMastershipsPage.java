/**
 * 
 */
package org.prelle.splimo.chargen.gen.jfx;

import java.util.HashMap;
import java.util.Map;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Skill;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.charctrl.MastershipController;
import org.prelle.splimo.chargen.SpliMoCharacterGenerator;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.chargen.fluent.SpliMoCharGenConstants;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * @author prelle
 *
 */
public class SelectMastershipsPage extends WizardPage implements GenerationEventListener {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle uiResources = SpliMoCharGenConstants.RES;

	class Line extends HBox implements EventHandler<ActionEvent> {
		Skill skill;
		Label name;
		Label pts;
		Button but;
		
		Line(Skill skill) {
			super(5);
			this.skill = skill;
			name = new Label(skill.getName());
			pts  = new Label("0x");
			but  = new Button(uiResources.getString("button.select"));;
			but.setOnAction(this);
			
			name.setPrefWidth(200);
			pts .setPrefWidth(50);
			but .setPrefWidth(100);
			
			getChildren().addAll(name, pts, but);
		}

		//--------------------------------------------------------------------
		/**
		 * @see javafx.event.EventHandler#handle(javafx.event.Event)
		 */
		@Override
		public void handle(ActionEvent event) {
			SelectMastershipDialog2 dia = new SelectMastershipDialog2(charGen.getMastershipController(), model, skill);
			Scene scene = new Scene(dia);
			scene.getStylesheets().add("css/default.css");
			
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.show();			
		}
	}
	
	private SpliMoCharacterGenerator charGen;	
	private SpliMoCharacter model;
	
	private Map<Skill, Line> mapping;
	private VBox content;
	private VBox context;
	private Label pointsLeft;

	//-------------------------------------------------------------------
	public SelectMastershipsPage(SpliMoCharacter model, SpliMoCharacterGenerator chGen) {
		this.charGen = chGen;
		this.model   = model;
		mapping = new HashMap<>();

		pageInit(uiResources.getString("wizard.selectMasterships.title"), 
				new Image(SelectMastershipsPage.class.getClassLoader().getResourceAsStream("data/Splittermond_hochkant.png")));
		finishButton.setDisable(!charGen.hasEnoughData());
		GenerationEventDispatcher.addListener(this);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.gen.jfx.WizardPage#getContent()
	 */
	@Override
	Parent getContent() {
		content = new VBox(5);
		
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
		pointsLeft = new Label("0");
		pointsLeft.setStyle("-fx-font-size: 400%");
		pointsLeft.getStyleClass().add("wizard-context");
		Label pointsLeft_f = new Label(uiResources.getString("wizard.distrMaster.pointsLeft")+" ");
		pointsLeft_f.getStyleClass().add("wizard-context");


		context = new VBox(15);
		context.setAlignment(Pos.TOP_CENTER);
		context.setPrefWidth(104);
		context.getChildren().addAll(placeholder, pointsLeft, pointsLeft_f);
		return context;
	}

	
	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.gen.jfx.WizardPage#nextPage()
	 */
	void nextPage() {
		super.nextPage();
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		if (event.getType()==GenerationEventType.POINTS_LEFT_MASTERSHIPS) {
			logger.debug("Update masterships");
//			MastershipGenerator gen = (MastershipGenerator) event.getKey();
//			Skill skill = gen.getSkill();
			MastershipController gen = charGen.getMastershipController();
			Skill skill = (Skill)event.getKey();
			logger.debug("Change free selections in "+skill+" to "+gen.getFreeMasterships(skill));
			
			Line line = mapping.get(skill);
			if (line==null) {
				line = new Line(skill);
				line.pts.setText(gen.getFreeMasterships(skill)+"x");
				mapping.put(skill, line);
				// Find position to add
				ObservableList<Node> children = content.getChildren();
				if (children.isEmpty())
					content.getChildren().add(line);
				else {
					int pos=-1;
					for (int i=0; i<children.size(); i++) {
						int cmp = skill.compareTo(  ((Line)children.get(i)).skill);
						if (cmp<=0) {
							pos=i;
							children.add(i, line);
							break;
						}
					}
					if (pos==-1)
						children.add(line);
				}
			} else {
				line.pts.setText(gen.getFreeMasterships(skill)+"x");
				if (gen.getFreeMasterships(skill)==0 && gen.getAssignedFreeMasterships(skill)==0) {
					logger.debug("Remove free selection line for "+line.skill);
					content.getChildren().remove(line);
					mapping.remove(line.skill);
				}
			}
			
			/* Update wizard buttons */
//			int sumFree = 0;
//			for (Skill tmp : mapping.keySet())
//				sumFree += charGen.getSkillGenerator().getMastershipGenerator(tmp).getUnusedFreeSelections();
			pointsLeft.setText(String.valueOf(gen.getFreeMasterships()));
			super.nextButton.setDisable(gen.getFreeMasterships()>0);
			finishButton.setDisable(!charGen.hasEnoughData());
		}
	}

}
