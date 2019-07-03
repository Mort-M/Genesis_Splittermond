package org.prelle.splittermond.chargen.jfx.tables;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.prelle.javafx.AttentionPane;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.NavigButtonControl;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.javafx.SymbolIcon;
import org.prelle.javafx.skin.GridPaneTableViewSkin;
import org.prelle.rpgframework.jfx.NumericalValueTableCell;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.MastershipReference;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SkillSpecialization;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.modifications.SkillModification;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXUtil;
import org.prelle.splittermond.chargen.jfx.dialogs.MastershipDialog;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Callback;

/**
 * @author Stefan Prelle
 *
 */
public class SkillValueTableView extends TableView<SkillValue> {

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(SkillValueTableView.class.getName());

	private CharacterController control;
	private SkillType type;
	private ScreenManagerProvider provider;
	
	private TableColumn<SkillValue, String> colName;
	private TableColumn<SkillValue, Attribute> colAttrib1;
	private TableColumn<SkillValue, Attribute> colAttrib2;
	private TableColumn<SkillValue, SkillValue> colPoints;
	private TableColumn<SkillValue, Number> colValue;
	private TableColumn<SkillValue, Number> colMod;
	private TableColumn<SkillValue, Button> colButton;
	private TableColumn<SkillValue, String> colMasteries;

	//-------------------------------------------------------------------
	public SkillValueTableView(CharacterController control, SkillType type, ScreenManagerProvider provider) {
		this.control = control;
		this.type    = type;
		this.provider= provider;
		initColumns();
		setSkin(new GridPaneTableViewSkin<>(this));
		setData(control.getModel());
	}

	//-------------------------------------------------------------------
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initColumns() {		
		colName     = new TableColumn<SkillValue, String>(RES.getString("label.name"));
		colAttrib1  = new TableColumn<SkillValue, Attribute>(RES.getString("label.attr1"));
		colAttrib2  = new TableColumn<SkillValue, Attribute>(RES.getString("label.attr2"));
		colPoints   = new TableColumn<SkillValue, SkillValue>(RES.getString("label.points"));
		colMod      = new TableColumn<SkillValue, Number>(RES.getString("label.mod"));
		colValue    = new TableColumn<SkillValue, Number>(RES.getString("label.value"));
		colButton   = new TableColumn<SkillValue, Button>();
		colMasteries= new TableColumn<SkillValue, String>();

		colName.setMinWidth(180);
//		colName.setPrefWidth(180);
		colAttrib1.setMinWidth(50);
//		colAttrib1.setPrefWidth(50);
		colAttrib2.setMinWidth(50);
//		colAttrib2.setPrefWidth(50);
		colPoints.setMinWidth(140);
		colMod.setMinWidth(50);
//		colMod.setPrefWidth(50);
		colValue.setMinWidth(50);
//		colValue.setPrefWidth(50);
		colMasteries.setMinWidth(200);
		colMasteries.setPrefWidth(500);
//		colMasteries.setMaxWidth(5000);

		colName.setStyle( "-fx-alignment: top-left;");
		colAttrib1.setStyle( "-fx-alignment: top-center;");
		colAttrib2.setStyle( "-fx-alignment: top-center;");
		colPoints.setStyle( "-fx-alignment: top-center;");
		colMod.setStyle( "-fx-alignment: top-center;");
		colValue.setStyle( "-fx-alignment: top-center;");
		colButton.setStyle( "-fx-alignment: top-center;");
		colMasteries.setStyle( "-fx-alignment: top-left;");
		
		getColumns().addAll(colName, colAttrib1, colAttrib2, colPoints, colMod, colValue, colMasteries);
		setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		
		colName.setCellValueFactory( param-> new SimpleStringProperty(param.getValue().getSkill().getName()));
		colAttrib1.setCellValueFactory( param-> new SimpleObjectProperty(param.getValue().getSkill().getAttribute1()));
		colAttrib2.setCellValueFactory( param-> new SimpleObjectProperty(param.getValue().getSkill().getAttribute2()));
		colPoints.setCellValueFactory( param-> new SimpleObjectProperty<SkillValue>(param.getValue()));
		colMod.setCellValueFactory( param-> new SimpleIntegerProperty(param.getValue().getModifier()));
		colValue.setCellValueFactory( param-> new SimpleIntegerProperty(param.getValue().getModifiedValue()));
		colButton.setCellValueFactory( param-> {
			Button btn = new Button(null, new SymbolIcon("edit"));
			btn.setUserData(param.getValue());
			return new SimpleObjectProperty(btn);
			});
		colMasteries.setCellValueFactory( param-> new SimpleStringProperty(getMasteriesString(param.getValue())));
		
		colName.setCellFactory( (col) -> new TableCell<SkillValue,String>() {
			public void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (item==null) { setGraphic(null); } else {
					Label label = new Label(item);
					label.setMaxWidth(Double.MAX_VALUE);
					AttentionPane attPane = new AttentionPane(label, Pos.TOP_RIGHT);
					Skill skill = getTableView().getItems().get(getIndex()).getSkill();
					attPane.setAttentionFlag(control.getMastershipController().getToDos(skill).size()>0);
					attPane.setAttentionToolTip(control.getMastershipController().getToDos(skill));
					setGraphic(attPane); 
				}
			}
		});
		colAttrib1.setCellFactory( (col) -> new TableCell<SkillValue,Attribute>() {
			public void updateItem(Attribute item, boolean empty) {
				super.updateItem(item, empty);
				if (item==null) { setText(null); } else { setText(item.getShortName()); }
			}
		});
		colAttrib2.setCellFactory( (col) -> new TableCell<SkillValue,Attribute>() {
			public void updateItem(Attribute item, boolean empty) {
				super.updateItem(item, empty);
				if (item==null) { setText(null); } else { setText(item.getShortName()); }
			}
		});
		colMod.setCellFactory( (col) -> new SkillValueModifierTableCell() );
		colValue.setCellFactory( (col) -> new TableCell<SkillValue,Number>() {
			public void updateItem(Number item, boolean empty) {
				super.updateItem(item, empty);
				if (item==null) { setText(null); } else {
					SkillValue sval = getTableView().getItems().get(getIndex());
					Attribute a1 = sval.getSkill().getAttribute1();
					Attribute a2 = sval.getSkill().getAttribute2();
					if (a1!=null && a2!=null) {
						int val = control.getModel().getAttribute(a1).getValue() + control.getModel().getAttribute(a2).getValue();
						setText(String.valueOf(val + sval.getModifiedValue()));
					}
				}
			}
		});
		colPoints.setCellFactory( (col) -> new NumericalValueTableCell<Skill,SkillValue,SkillValue>(control.getSkillController()));
		colButton.setCellFactory( (col) -> new TableCell<SkillValue,Button>() {
			public void updateItem(Button item, boolean empty) {
				super.updateItem(item, empty);
				if (item==null) { setGraphic(null); } else {
					setGraphic(item);
				}
			}
		});
		colMasteries.setCellFactory( col -> new SkillValueMasteriesTableCell(control, provider));
//		colMasteries.setCellFactory( col -> new TableCell<SkillValue,String>() {
//			public void updateItem(String item, boolean empty) {
//				super.updateItem(item, empty);
//				if (item==null) { 
//					setText(null); 
//				} else {
//					setGraphic(new Button(null, new SymbolIcon("edit")));
//					setWrapText(true);
//					setText(item);
////					Label lbl = new Label(item);
//					setStyle("-fx-background-color: pink");
////					lbl.setWrapText(true);
////					setGraphic(lbl);
//				}
//			}
//		});
	}

	//-------------------------------------------------------------------
	public void setData(SpliMoCharacter model ) {
		getItems().clear();
		getItems().addAll(model.getSkills(type));
	}

	//-------------------------------------------------------------------
	static String getMasteriesString(SkillValue sVal) {
		StringBuffer buf = new StringBuffer();
		for (SkillSpecialization spec : sVal.getSkill().getSpecializations()) {
			if (sVal.getSpecializationLevel(spec)>0)
				buf.append(spec.getName()+" "+sVal.getSpecializationLevel(spec)+",");
		}

		Iterator<MastershipReference> it = sVal.getMasterships().iterator();
		while (it.hasNext()) {
			MastershipReference ref = it.next();
			// Ignore skill specializations / non-masterships here
			if (ref.getMastership()==null)
				continue;
			buf.append(ref.getMastership().getName());
			if (it.hasNext())
				buf.append(", ");
		}
		return buf.toString();
	}
}

class SkillValueModifierTableCell extends TableCell<SkillValue, Number> {
	
	private Label label;
	
	public SkillValueModifierTableCell() {
		label    = new Label();
	}
	
	public void updateItem(Number item, boolean empty) {
		super.updateItem(item, empty);
		if (item==null || (0==(int)item)) {
			setGraphic(null);
		} else {
			SkillValue sval = getTableView().getItems().get(getIndex());
			
			List<String> names = new ArrayList<>();
			sval.getModifications().forEach( elem -> names.add(  ((SkillModification)elem).getValue()+" ("+((SkillModification)elem).getSource()+")"));
			label.setTooltip(new Tooltip(SpliMoCharGenJFXUtil.getModificationTooltip(sval)));
			label.setText(String.valueOf(item));
			setGraphic(label);
		}
	}
}

class SkillValueMasteriesTableCell extends TableCell<SkillValue, String> {
	
	private Button btnOpen;
	private Label flow;
	private HBox layout;
	private CharacterController control;
	private ScreenManagerProvider provider;
	
	public SkillValueMasteriesTableCell(CharacterController control, ScreenManagerProvider provider) {
		this.control  = control;
		this.provider = provider;
		btnOpen = new Button(null, new SymbolIcon("edit"));
//		btnOpen.setStyle("-fx-background-color: yellow");
		flow    = new Label();
//		flow.setStyle("-fx-background-color: pink");
		flow.setWrapText(true);
		flow.setMaxWidth(Double.MAX_VALUE);
		layout  = new HBox(btnOpen, flow);
		HBox.setHgrow(flow, Priority.ALWAYS);
//		GridPane.setFillHeight(this, false);
//		layout.setStyle("-fx-max-height: 4em; -fx-background-color: red");
	}
	
	//-------------------------------------------------------------------
	public void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);
		if (empty) {
			setGraphic(null);
		} else {
			SkillValue sVal = getTableView().getItems().get(getIndex());
			flow.setText(item);
			
			
			layout  = new HBox(btnOpen, getMasteriesFlowPane(sVal));
//			layout.setStyle("-fx-background-color: red");
			HBox.setHgrow(flow, Priority.ALWAYS);
			setGraphic(layout);
//			setStyle("-fx-max-height: 7em; -fx-background-color: lime");
//			setMaxHeight(60);
			
			btnOpen.setOnAction(ev -> buttonClicked(sVal));
		}
	}

	//-------------------------------------------------------------------
	private static FlowPane getMasteriesFlowPane(SkillValue sVal) {
		FlowPane ret = new FlowPane();
		ret.setPrefWrapLength(2000);
		List<String> buf = new ArrayList<String>();
		for (SkillSpecialization spec : sVal.getSkill().getSpecializations()) {
			if (sVal.getSpecializationLevel(spec)>0)
				buf.add(spec.getName()+" "+sVal.getSpecializationLevel(spec));
		}

		Iterator<MastershipReference> it = sVal.getMasterships().iterator();
		while (it.hasNext()) {
			MastershipReference ref = it.next();
			// Ignore skill specializations / non-masterships here
			if (ref.getMastership()==null)
				continue;
			buf.add(ref.getMastership().getName());
		}
		// Convert to labels
		Iterator<String> it2 = buf.iterator();
		while(it2.hasNext()) {
			Label lbl = new Label(it2.next());
			if (it2.hasNext())
				lbl.setText(lbl.getText()+", ");
			ret.getChildren().add(lbl);
		}
		
		return ret;
	}
	
	//-------------------------------------------------------------------
	private void buttonClicked(SkillValue sval) {
		LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME).debug("START: editMasterships");
		
		MastershipDialog dialog = new MastershipDialog(control);
		dialog.setData(control.getModel(), sval);
		NavigButtonControl ctrl = new NavigButtonControl();
//		ctrl.initialize(provider.getScreenManager(), dialog);
//		ctrl.setDisabled(CloseType.OK, false);
		provider.getScreenManager().showAlertAndCall(dialog, ctrl);
		LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME).debug("STOP : editMasterships");
//		getTableView().refresh();
//		this.updateItem(SkillValueTableView.getMasteriesString(sval), false);
		
	}
}
