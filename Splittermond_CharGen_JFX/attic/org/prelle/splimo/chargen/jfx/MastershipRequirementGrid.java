/**
 * 
 */
package org.prelle.splimo.chargen.jfx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;

import org.apache.log4j.Logger;
import org.prelle.splimo.Mastership;
import org.prelle.splimo.chargen.MastershipSelection;

/**
 * @author prelle
 *
 */
public class MastershipRequirementGrid extends StackPane {

	private final static Logger logger = Logger.getLogger("fxui");

	private Map<Mastership, ReadOnlyObjectProperty<Bounds>> boundsMap;
	private Map<ReadOnlyObjectProperty<Bounds>, Collection<Line>> lineStartMap;
	private Map<ReadOnlyObjectProperty<Bounds>, Collection<Line>> lineEndMap;
	private Pane linePane;
	private List<MastershipSelection> data;
	private EventHandler<ActionEvent> handler;
	
	//-------------------------------------------------------------------
	/**
	 */
	public MastershipRequirementGrid(GridPane grid, List<MastershipSelection> data, EventHandler<ActionEvent> handler) {
		this.setAlignment(Pos.TOP_LEFT);
		this.data = data;
		this.handler = handler;
		boundsMap = new HashMap<Mastership, ReadOnlyObjectProperty<Bounds>>();
		lineStartMap = new HashMap<ReadOnlyObjectProperty<Bounds>, Collection<Line>>();
		lineEndMap = new HashMap<ReadOnlyObjectProperty<Bounds>, Collection<Line>>();
		
		linePane = new Pane();

		getChildren().addAll(linePane, grid);
		drawLines();
	}

	//-------------------------------------------------------------------
	private void drawLines() {
		/* 
		 * Draw lines
		 */
		for (MastershipSelection mSelect : data) {
			Mastership tmp = mSelect.getMastership();
			String id = tmp.getKey();
			ToggleButton button = (ToggleButton) this.lookup("#"+id);
			// TODO: really determine if mastership is selectable
//			if (tmp.getLevel()>2)
//				button.setDisable(true);
			if (button==null) {
				// Button does not exist in this tab
				continue;
			}
			button.setDisable(!mSelect.isEditable());
			button.setSelected(mSelect.isSelected());
			button.setOnAction(handler);
			
			final ReadOnlyObjectProperty<Bounds> prop = button.boundsInParentProperty();

			boundsMap.put(tmp, button.boundsInParentProperty());
			prop.addListener(new ChangeListener<Bounds>() {
				@Override
				public void changed(ObservableValue<? extends Bounds> property,
						Bounds arg1, Bounds newBounds) {
					// TODO Auto-generated method stub
					Bounds bounds = property.getValue();
					Collection<Line> start = lineStartMap.get(property);
					if (start!=null) {
						for (Line line : start) {
							line.setStartX(bounds.getMinX()+bounds.getWidth()/2);
							line.setStartY(bounds.getMinY()+bounds.getHeight()/2);
						}
					}
					Collection<Line> end = lineEndMap.get(property);
					if (end!=null) {
						for (Line line : end) {
							line.setEndX(bounds.getMinX()+bounds.getWidth()/2);
							line.setEndY(bounds.getMinY()+bounds.getHeight()/2);
						}
					}
				}
			});
			
			// Create lines for prerequisites
			logger.debug("* "+id+" = "+tmp.getPrerequisites());
			for (Mastership requires : tmp.getPrerequisites()) {
				Line line = new Line(10,10, 30,30);
				line.setId(tmp.getKey()+"-"+requires.getKey());
				line.setStrokeWidth(5);
				linePane.getChildren().add(line);
				
				ReadOnlyObjectProperty<Bounds> start = boundsMap.get(requires);
				ReadOnlyObjectProperty<Bounds> end   = boundsMap.get(tmp);
				if (!lineStartMap.containsKey(start))
					lineStartMap.put(start, new ArrayList<Line>());
				if (!lineEndMap.containsKey(end))
					lineEndMap.put(end, new ArrayList<Line>());
				lineStartMap.get(start).add(line);
				lineEndMap.get(end).add(line);
			}
		}

	}

}
