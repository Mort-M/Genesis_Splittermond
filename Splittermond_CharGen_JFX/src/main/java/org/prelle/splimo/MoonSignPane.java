/**
 * 
 */
package org.prelle.splimo;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import org.apache.log4j.Logger;

/**
 * @author Stefan
 *
 */
public class MoonSignPane extends HBox {

	private final static Logger logger = Logger.getLogger("splittermond.jfx");

	private static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splimo-chargen");

	private Map<ImageView, Moonsign> mapping;
	private ObjectProperty<Moonsign> selected;
	private Label description;
	private GridPane content;

	//--------------------------------------------------------------------
	public MoonSignPane() {
		super(20);
		selected = new SimpleObjectProperty<>();
		initComponents();
	}

	//--------------------------------------------------------------------
	private void initComponents() {

		content = new GridPane();
		description = new Label();
		description.setWrapText(true);
		description.getStyleClass().add("text-body");
		description.setStyle("-fx-pref-width: 30em");

		mapping = new HashMap<ImageView, Moonsign>();
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
			iView.setOnMouseClicked(event -> {
					logger.debug("Foo");
					ImageView view = (ImageView) event.getSource();
					selected.set(mapping.get(view));
					description.setText(mapping.get(view).getDescription());
					view.setEffect(new DropShadow(10, Color.RED));
					for (ImageView temp : mapping.keySet())
						if (temp!=view)
							temp.setEffect(null);
					// Mark selected
					logger.info("Moonsplinter now "+selected);
			});
			
			Label label = new Label(tmp.getName());
//			label.setPrefWidth(110);
			
			VBox foo = new VBox();
			foo.setAlignment(Pos.CENTER);
			foo.getChildren().addAll(iView, label);
			mapping.put(iView, tmp);
			content.add(foo, x, y);
			
			i++;
		}

		getChildren().addAll(content, description);

	}

	//--------------------------------------------------------------------
	public ObjectProperty<Moonsign> selectedProperty() {
		return selected;
	}

}
