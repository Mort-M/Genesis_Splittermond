package org.prelle.splittermond.chargen.jfx.dialogs;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.Resource;
import org.prelle.splimo.ResourceReference;
import org.prelle.splimo.RewardImpl;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.ResourceController;
import org.prelle.splimo.modifications.MoneyModification;
import org.prelle.splimo.modifications.ResourceModification;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splittermond.chargen.jfx.ViewMode;

import de.rpgframework.RPGFrameworkLoader;
import de.rpgframework.core.RoleplayingSystem;
import de.rpgframework.genericrpg.Reward;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.products.Adventure;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import javafx.util.converter.LocalTimeStringConverter;

public class RewardBox extends HBox {

	private final static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	
	private static PropertyResourceBundle res = (PropertyResourceBundle) ResourceBundle.getBundle(RewardBox.class.getName());

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
			return false;
		}

		@Override
		public boolean canBeTrashed(ResourceReference ref) {
			return false;
		}

		@Override
		public boolean trash(ResourceReference ref) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public ResourceReference findResourceReference(Resource res,
				String descr, String idref) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<String> getToDos() {
			return new ArrayList<String>();
		}

	}
	private Reward model;
	private ViewMode mode;

	private TextField tfDescr;
	private TextField tfMoney;
	private ChoiceBox<Adventure> cbAdventure;
	private TextField tfExp;
	private ResourcePane resources;
	private ImageView cover;
	private Spinner<LocalTime> timeSpinner;
	private DatePicker datePicker;

	private BooleanProperty enoughData;

	//-------------------------------------------------------------------
	public RewardBox() {
		super(20);
		mode = ViewMode.GENERATION;
		initComponents();
		initLayout();
		initInterActivity();
	}

	//-------------------------------------------------------------------
	public RewardBox(Reward elem) {
		super(20);
		mode = ViewMode.MODIFICATION;
		initComponents();
		initLayout();
		initInterActivity();

		setData(elem);
	}

	//--------------------------------------------------------------------
	public BooleanProperty enoughDataProperty() { return enoughData; }
	public boolean hasEnoughData() { return enoughData.get(); }

	//--------------------------------------------------------------------
	private void initComponents() {
		LocalDateTime now = LocalDateTime.now();

		enoughData = new SimpleBooleanProperty(true);
		datePicker = new DatePicker();
		datePicker.setValue(now.toLocalDate());
		timeSpinner = new Spinner<LocalTime>(new SpinnerValueFactory<LocalTime>(){
			{
	            setConverter(new LocalTimeStringConverter(FormatStyle.MEDIUM));
	        }
			@Override
			public void decrement(int steps) {
				 if (getValue() == null)
		                setValue(LocalTime.now());
		            else {
		                LocalTime time = (LocalTime) getValue();
		                setValue(time.minusMinutes(steps));
		            }
			}

			@Override
			public void increment(int steps) {
				if (this.getValue() == null)
	                setValue(LocalTime.now());
	            else {
	                LocalTime time = (LocalTime) getValue();
	                setValue(time.plusMinutes(steps));
	            }
			}});
		timeSpinner.setEditable(true);
		timeSpinner.getEditor().textProperty().addListener( (ov,o,n) -> {
			LocalTime newVal = timeSpinner.getValueFactory().getConverter().fromString(n);
			timeSpinner.getValueFactory().setValue(newVal);
		});
		timeSpinner.getValueFactory().setValue(now.toLocalTime());
		/*
		 * Form
		 */
		tfDescr = new TextField();
		cbAdventure = new ChoiceBox<>();
		cbAdventure.getItems().addAll(RPGFrameworkLoader.getInstance().getProductService().getAdventures(RoleplayingSystem.SPLITTERMOND));
		cbAdventure.setConverter(new StringConverter<Adventure>() {
			public String toString(Adventure value) {return value.getTitle();}
			public Adventure fromString(String string) {return null;}
		});
		Collections.sort(cbAdventure.getItems());
		tfExp = new TextField();
		tfExp.setMaxWidth(100);
		tfMoney = new TextField();
		tfMoney.setStyle("-fx-max-width: 5em");
		resources = new ResourcePane(new RewardResourceController(), true, false);
		resources.setPrefHeight(150);

		cover = new ImageView();
		cover.setFitWidth(200);
		cover.setFitHeight(300);
	}

	//--------------------------------------------------------------------
	private void initLayout() {
		Label title_l = new Label(res.getString("label.title"));
		Label adv_l = new Label(res.getString("label.adventure"));
		Label exp_l = new Label(res.getString("label.experience"));
		Label res_l = new Label(res.getString("label.resource"));
		Label dat_l = new Label(res.getString("label.when"));
		Label tel_l = new Label(res.getString("label.telare"));
		res_l.setMaxHeight(Double.MAX_VALUE);
		res_l.setAlignment(Pos.TOP_LEFT);

		HBox timeAndDate = new HBox(5);
		timeAndDate.getChildren().addAll(datePicker, timeSpinner);


		VBox form = new VBox(5);
		form.getChildren().addAll(title_l, tfDescr, adv_l, cbAdventure, dat_l, timeAndDate);
		if (mode!=ViewMode.MODIFICATION) {
			GridPane grid = new GridPane();
			grid.setHgap(40);
			grid.add(exp_l, 0, 0);
			grid.add(tfExp, 0, 1);
			grid.add(tel_l, 1, 0);
			grid.add(tfMoney, 1, 1);
			form.getChildren().addAll(grid, resources);
			VBox.setMargin(exp_l, new Insets(10,0,0,0));
			VBox.setMargin(res_l, new Insets(20,0,0,0));
			VBox.setMargin(tel_l, new Insets(10,0,0,0));
			VBox.setMargin(grid , new Insets(10,0,0,0));
		}
		VBox.setMargin(adv_l, new Insets(10,0,0,0));

		getChildren().addAll(cover, form);
	}

	//--------------------------------------------------------------------
	private void initInterActivity() {
		cbAdventure.getSelectionModel().selectedItemProperty().addListener((ov,o,n) -> {
			if (n!=null && (n.getCover()!=null)) {
				Image img = new Image(new ByteArrayInputStream(n.getCover()));
				cover.setImage(img);
			} else
				cover.setImage(null);
		});

		tfDescr.textProperty().addListener((ov,o,n) -> testEnoughData());
		tfExp.textProperty().addListener((ov,o,n) -> testEnoughData());
		tfMoney.textProperty().addListener((ov,o,n) -> testEnoughData());
		resources.getTable().getItems().addListener(new ListChangeListener<ResourceReference>(){
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends ResourceReference> c) {
				testEnoughData();
			}});
	}

	//--------------------------------------------------------------------
	public void setData(Reward value) {
		this.model = value;
		tfDescr.setText(value.getTitle());

		Instant dateTime = Instant.ofEpochMilli(value.getDate().getTime());
		LocalDateTime timeAndDate = LocalDateTime.ofInstant(dateTime, ZoneId.systemDefault());
		datePicker.setValue(timeAndDate.toLocalDate());
		timeSpinner.getValueFactory().setValue(timeAndDate.toLocalTime());

		// Collect EP
		tfExp.setText(String.valueOf(model.getExperiencePoints()));

		logger.debug("Search "+value.getId());
		if (value.getId()!=null) {
			for (Adventure adv : RPGFrameworkLoader.getInstance().getProductService().getAdventures(RoleplayingSystem.SPLITTERMOND))
//				logger.debug("  Compare with "+adv.getId());
				if (value.getId().equals(adv.getId())) {
					cbAdventure.getSelectionModel().select(adv);
					break;
				}
		} else
			cbAdventure.getSelectionModel().clearSelection();

		// Other rewards
			for (Modification mod : model.getModifications()) {
				if (mod instanceof ResourceModification) {
					logger.warn("TODO: add resources");
				} else if (mod instanceof MoneyModification) {
					tfMoney.setText( String.valueOf( ((MoneyModification)mod).getTelare()));
				}
		}
	}

	//--------------------------------------------------------------------
	private void testEnoughData() {
		enoughData.set(testEnoughData_impl());
	}

	//--------------------------------------------------------------------
	private boolean testEnoughData_impl() {
		logger.debug("testEnoughData_impl");
		if (tfDescr.getText()==null || tfDescr.getText().length()<1) return false;
		if (tfExp.getText()==null || tfExp.getText().length()<1) return false;
		
		if (!tfMoney.getText().isEmpty()) {
			try {
				Integer.parseInt(tfMoney.getText());
			} catch (NumberFormatException e) {
				return false;
			}
		}
		
		try {
			logger.debug("...."+resources.getResources());
			int foo = Integer.parseInt(tfExp.getText());
			if (foo==0 && resources.getResources().isEmpty())
				return false;
		} catch (NumberFormatException e) {
			return false;
		}

		return true;
	}

	//--------------------------------------------------------------------
	public RewardImpl getDataAsReward() {
		LocalDateTime dateAndTime = LocalDateTime.of(datePicker.getValue(), timeSpinner.getValue());
		ZonedDateTime zonedDateTime = dateAndTime.atZone(ZoneId.systemDefault());
		Instant instant = Instant.from(zonedDateTime);
		Date date = Date.from(instant);

		int exp = Integer.valueOf(tfExp.getText());
		RewardImpl ret = new RewardImpl(exp, tfDescr.getText());
		ret.setDate(date);
		if (cbAdventure.getValue()!=null)
			ret.setId(cbAdventure.getValue().getId());
		try {
			if (!tfMoney.getText().isEmpty()) {
				int val = Integer.parseInt(tfMoney.getText());
				ret.addModification(new MoneyModification(val));
			}
		} catch (NumberFormatException e) {
			logger.error("User entered invalid number");
		}

		logger.debug("Resources in pane = "+resources.getResources());
		for (ResourceReference val : resources.getResources()) {
			ResourceModification rMod = new ResourceModification(val.getResource(), val.getValue());
			rMod.setComment(val.getDescription());
			ret.addModification(rMod);
		}

		return ret;
	}
}