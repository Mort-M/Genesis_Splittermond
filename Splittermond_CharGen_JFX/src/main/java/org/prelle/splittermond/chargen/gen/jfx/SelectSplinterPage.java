/**
 * 
 */
package org.prelle.splittermond.chargen.gen.jfx;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Moonsign;
import org.prelle.splimo.chargen.SpliMoCharacterGenerator;
import org.prelle.splittermond.chargen.fluent.SpliMoCharGenConstants;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * @author prelle
 *
 */
public class SelectSplinterPage extends WizardPage implements ChangeListener<Moonsign> {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle uiResources = SpliMoCharGenConstants.RES;

	private SpliMoCharacterGenerator charGen;
	
	private VBox content;
//	private ListView<Moonsplinter> list;
	private GridPane list;
	private Map<ImageView, Moonsign> mapping;
	private Moonsign selected;

	//-------------------------------------------------------------------
	public SelectSplinterPage(SpliMoCharacterGenerator chGen) {
		super(uiResources.getString("wizard.selectSplinter.title"), 
				new Image(SelectSplinterPage.class.getClassLoader().getResourceAsStream("data/Splittermond_hochkant.png")));
		this.charGen = chGen;
		nextButton.setDisable(true);
		
		
		// Set data
		List<Moonsign> data = new ArrayList<Moonsign>();
		for (Moonsign tmp : Moonsign.values())
			data.add(tmp);
		// Sort alphabetically
		Collections.sort(data, new Comparator<Moonsign>() {
			@Override
			public int compare(Moonsign o1, Moonsign o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		
		int i=0;
		for (Moonsign tmp : data) {
			int x= i%3;
			int y= i/3;
			Image img = null;
			String fname = "data/moon_"+tmp.name().toLowerCase()+".png";
			logger.debug("Load "+fname);
			InputStream in = getClass().getClassLoader().getResourceAsStream(fname);
			if (in!=null) {
				img = new Image(in);
			} else
				logger.warn("Missing image at "+fname);
			ImageView iView = new ImageView(img);
			iView.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					logger.debug("Foo");
					ImageView view = (ImageView) event.getSource();
					selected = mapping.get(view);
					view.setEffect(new DropShadow(10, Color.RED));
					for (ImageView tmp : mapping.keySet())
						if (tmp!=view)
							tmp.setEffect(null);
					// Mark selected
					logger.info("Moonsplinter now "+selected);
					nextButton.setDisable(false);
				}
			});
			
			Label label = new Label(tmp.getName());
//			label.setPrefWidth(110);
			
			VBox foo = new VBox();
			foo.setAlignment(Pos.CENTER);
			foo.getChildren().addAll(iView, label);
			mapping.put(iView, tmp);
			list.add(foo, x, y);
			
			i++;
		}
		
//		list.getItems().addAll(data);
	}

	//-------------------------------------------------------------------
	@Override
	public void changed(ObservableValue<? extends Moonsign> property, Moonsign oldBackground,
			Moonsign newBackground) {
		// TODO Auto-generated method stub
		logger.info("Moonsplinter now "+newBackground);
		this.nextButton.setDisable(newBackground==null);

	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splittermond.chargen.gen.jfx.WizardPage#getContent()
	 */
	@Override
	Parent getContent() {
		mapping = new HashMap<ImageView, Moonsign>();
		
		list = new GridPane();
		
//		list = new ListView<Moonsplinter>();
//		list.setMinHeight(200);
//		list.getSelectionModel().selectedItemProperty().addListener(this);
		
		content = new VBox(10);
		content.getChildren().addAll(list);
		
		return content;
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splittermond.chargen.gen.jfx.WizardPage#nextPage()
	 */
	void nextPage() {
		logger.warn("TODO: query for modification choices");
//		charGen.selectSplinter(list.getSelectionModel().getSelectedItem());
		charGen.selectSplinter(selected);
		super.nextPage();
//		// If they have complaints, go to the normal next page
//		if (options.getSelectedToggle().equals(yes)) {
//			super.nextPage();
//		} else {
//			// No complaints? Short-circuit the rest of the pages
//			navTo("Thanks");
//		}
	}

}
