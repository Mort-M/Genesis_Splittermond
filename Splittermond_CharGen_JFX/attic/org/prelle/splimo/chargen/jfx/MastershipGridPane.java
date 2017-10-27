/**
 * 
 */
package org.prelle.splimo.chargen.jfx;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

import org.apache.log4j.Logger;
import org.prelle.splimo.Mastership;
import org.prelle.splimo.chargen.MastershipGenerator;
import org.prelle.splimo.chargen.MastershipSelection;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;

/**
 * @author prelle
 *
 */
public class MastershipGridPane extends GridPane implements GenerationEventListener {

	private final static Logger logger = Logger.getLogger("fxui");

	private MastershipGenerator charGen;
	private List<MastershipSelection>[] itemsPerLine;
	
	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public MastershipGridPane(MastershipGenerator charGen) {
		this.charGen = charGen;
		GenerationEventDispatcher.addListener(this);
		
		this.setAlignment(Pos.CENTER);
		this.setHgap(20);
		this.setVgap(40);
//		this.setMaxWidth(900);
		this.getRowConstraints().addAll(
				new RowConstraints(100,200,200),
				new RowConstraints(100,200,200),
				new RowConstraints(100,200,200),
				new RowConstraints(100,200,200)
				);
		itemsPerLine = new ArrayList[4];
		for (int i=0; i<itemsPerLine.length; i++)
			itemsPerLine[i] = new ArrayList<MastershipSelection>();
		
		setData();
	}
	
	//-------------------------------------------------------------------
	private void setData() {
		getChildren().clear();
		
		for (MastershipSelection tmp : charGen.getAvailable()) {
			Mastership master = tmp.getMastership();
			List<MastershipSelection> line = itemsPerLine[master.getLevel()-1];
			ToggleButton button = new ToggleButton(master.getName());
			button.setId(master.getKey());
			button.setMaxHeight(200);
			button.setMaxWidth(200);
			GridPane.setHgrow(button, Priority.ALWAYS);
			GridPane.setVgrow(button, Priority.ALWAYS);
			add(button, line.size(), master.getLevel()-1);
			line.add(tmp);
			
			if (line.size()>getColumnConstraints().size()) {
				ColumnConstraints col = new ColumnConstraints();
				col.setHgrow(Priority.ALWAYS);
				col.setMinWidth(100);
				col.setMaxWidth(300);
				col.setFillWidth(true);
				getColumnConstraints().add(col);
			}
				
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		switch (event.getType()) {
		case MASTERSHIP_SELECTION_CHANGED:
			MastershipSelection mSelect = (MastershipSelection) event.getKey();
			Mastership master = mSelect.getMastership();
			logger.debug("mastership selection changed: "+master.getKey());
			ToggleButton button = (ToggleButton) this.lookup("#"+master.getKey());
			button.setDisable(!mSelect.isEditable());
			button.setSelected(mSelect.isSelected());
			break;
		default:
		}
	}

}
