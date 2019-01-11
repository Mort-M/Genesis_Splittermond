/**
 * 
 */
package org.prelle.splittermond.jfx.creatures;

import java.util.Arrays;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.splimo.SplitterTools;
import org.prelle.splimo.charctrl.CommonCreatureController;
import org.prelle.splimo.chargen.fluent.SpliMoCharGenConstants;
import org.prelle.splimo.chargen.jfx.LetUserChooseAdapter;
import org.prelle.splimo.creature.CreatureModuleReference;
import org.prelle.splimo.modifications.ModificationChoice;

import de.rpgframework.genericrpg.modification.Modification;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class NecessaryChoicesPane extends VBox {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle UI = SpliMoCharGenConstants.RES;

	private CommonCreatureController control;
	private LetUserChooseAdapter adapter;
	
	//-------------------------------------------------------------------
	public NecessaryChoicesPane(CommonCreatureController ctrl, ScreenManagerProvider provider) {
		this.control = ctrl;
		adapter = new LetUserChooseAdapter(provider);
		
		setStyle("-fx-max-width: 40em");
	}
	
	//-------------------------------------------------------------------
	public void refresh() {
		getChildren().clear();
		
		for (CreatureModuleReference.NecessaryChoice tmp : control.getChoicesToMake()) {
			Label label = new Label(tmp.originModule.getModule().getName());
			label.setWrapText(true);
			label.setStyle("-fx-min-width: 6em");
			label.getStyleClass().add("text-small-subheader");
			Label option = new Label(SplitterTools.getModificationString(tmp.getOriginChoice()));
			option.setWrapText(true);
			HBox line1 = new HBox();
			line1.getChildren().addAll(label, option);
			line1.setStyle("-fx-spacing: 1em");
			
			Label choice = new Label("-");
			if (tmp.getMadeChoice()!=null)
				choice.setText(SplitterTools.getModificationString(tmp.getMadeChoice()));
			
			Button btnSelect = new Button(UI.getString("button.select"));
			btnSelect.getStyleClass().add("bordered");
			HBox line2 = new HBox();
			line2.setAlignment(Pos.CENTER_LEFT);
			line2.getChildren().addAll(btnSelect, choice);
			line2.setStyle("-fx-spacing: 1em");
			choice.setStyle("-fx-font-weight: bold");
			
			line1.setDisable(tmp.disabled!=null && tmp.disabled);
			line2.setDisable(tmp.disabled!=null && tmp.disabled);
			getChildren().addAll(line1, line2);
			VBox.setMargin(line2, new Insets(0, 0, 20, 0));
			
			btnSelect.setOnAction(event -> {
				Modification mod = choose(tmp);
				if (mod!=null) {
					choice.setText(SplitterTools.getModificationString(tmp.getMadeChoice()));
				} else {
					option.setText(SplitterTools.getModificationString(tmp.getOriginChoice()));
				}
			});
		}
	}

	//-------------------------------------------------------------------
	private Modification choose(CreatureModuleReference.NecessaryChoice tmp) {
		logger.debug("Choose from "+tmp.getOriginChoice());
		
		ModificationChoice choice = control.instantiateChoice(tmp);
		logger.debug("  convert to "+choice);
		Modification[] result = adapter.letUserChoose(tmp.originModule.getModule().getName(), choice);
		logger.debug("  letUserChoose returned "+Arrays.toString(result)+" // size="+result.length);
		if (result.length!=1) {
			logger.error("Expect only one selection, but received "+result.length);
			return null;
//		} else if (tmp.madeChoice==null) {
//			logger.debug("  Another choice is necessary");
//			tmp.originChoice = result[0];
//			return choose(tmp);
		} else {
			control.makeChoice(tmp, result[0]);
			logger.info("User chose "+tmp.getMadeChoice());
			return tmp.getMadeChoice();
		}
	}

}
