package org.prelle.splittermond.chargen.jfx.listcells;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.prelle.splimo.items.Armor;
import org.prelle.splimo.items.CarriedItem;
import org.prelle.splimo.items.EnhancementReference;
import org.prelle.splimo.items.ItemAttribute;
import org.prelle.splimo.items.ItemType;
import org.prelle.splimo.items.LongRangeWeapon;
import org.prelle.splimo.items.PersonalizationReference;
import org.prelle.splimo.items.Shield;
import org.prelle.splimo.items.Weapon;
import org.prelle.splimo.persist.WeaponDamageConverter;
import org.prelle.splimo.requirements.AttributeRequirement;
import org.prelle.splittermond.chargen.jfx.sections.EquipmentSection;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Spinner;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * @author Stefan Prelle
 *
 */
public class CarriedItemListCell extends ListCell<CarriedItem> {

	private static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle.getBundle(EquipmentSection.class.getName());

	private GridPane grid;
	private ImageView image;
	private Label name;
	private Label extra;
	private Label statsWeapon;
	private Label statsLongRg;
	private Label statsArmor;
	private Label statsShield;
	private Label statsCommon;
	private Spinner<Integer> spCount;

	private transient CarriedItem data;

	//-------------------------------------------------------------------
	public CarriedItemListCell() {
		image = new ImageView();
		image.setFitHeight(48);
		image.setFitWidth(48);
		name = new Label();
		name.getStyleClass().add("base");
		extra = new Label();
		extra.setWrapText(true);
		extra.getStyleClass().add("caption");
		extra.setStyle("-fx-text-fill: textcolor-highlight-primary");
		spCount = new Spinner<>(1, 20, 1);
		spCount.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
		spCount.setPrefWidth(80);
		spCount.setStyle("-fx-min-width: 6em");
		statsWeapon = new Label();
		statsWeapon.setWrapText(true);
		statsLongRg = new Label();
		statsArmor = new Label();
		statsArmor.setWrapText(true);
		statsShield = new Label();
		statsShield.setWrapText(true);
		statsCommon = new Label();

		grid = new GridPane();
		grid.setHgap(2);
		grid.add(image, 0, 0, 1, 7);
		grid.add(name , 1, 0);
		grid.add(extra, 1, 1);
		grid.add(statsWeapon, 1, 2);
		grid.add(statsLongRg, 1, 3);
		grid.add(statsArmor , 1, 4);
		grid.add(statsShield, 1, 5);
		grid.add(statsCommon, 1, 6);
		grid.add(spCount    , 2, 0, 1, 7);

		GridPane.setHgrow(name, Priority.ALWAYS);
		GridPane.setHgrow(extra, Priority.ALWAYS);


		spCount.valueProperty().addListener( (ov,o,n) -> data.setCount(n));
		setStyle("-fx-pref-width: 20em;");
	}

	//--------------------------------------------------------------------
	static String getWeaponDamageString(int damage) {
		try {
			return new WeaponDamageConverter().write(damage);
		} catch (Exception e) {
			return "";
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	public void updateItem(CarriedItem item, boolean empty) {
		super.updateItem(item, empty);
		this.data = item;

		setGraphicTextGap(0);
		if (empty || item==null) {
			setText(null);
			setGraphic(null);
			return;
		} else {
			setGraphic(grid);
			fillGrid(item);
		}
	}

	//-------------------------------------------------------------------
	private void fillGrid(CarriedItem item) {
		name.setText(item.getName());
		image.setImage(null);
		spCount.getValueFactory().setValue(item.getCount());

		// Weapon
		grid.getChildren().remove(statsWeapon);
		if (item.getItem().isType(ItemType.WEAPON)) {
			image.setImage(ItemUtils.getItemTypeIcon(ItemType.WEAPON).getImage());
			Weapon weapon = item.getItem().getType(Weapon.class);

			System.err.println("CarriedItemList.fillGrid: "+weapon.getRequirements());
			String minAttr = weapon.getRequirements().stream().filter(AttributeRequirement.class::isInstance)
					                                          .map(AttributeRequirement.class::cast)
					                                          .map(aReq -> aReq.getAttribute().getShortName()+" "+aReq.getValue())
					                                          .collect(Collectors.joining(","));
			statsWeapon.setText(String.format("%s:%s  %s:%d\n%s:%s  %s:%s",
					ItemAttribute.DAMAGE.getShortName(), getWeaponDamageString(item.getDamage(ItemType.WEAPON)),
					ItemAttribute.SPEED.getShortName(), item.getSpeed(ItemType.WEAPON),
					ItemAttribute.ATTRIBUTES.getShortName(), item.getAttribute1(ItemType.WEAPON).getShortName()+"+"+item.getAttribute2(ItemType.WEAPON).getShortName(),
					(minAttr.length()>0)?ItemAttribute.MIN_ATTRIBUTES.getShortName():"", minAttr));
			grid.add(statsWeapon, 1, 2);
		}

		// Long Range Weapon
		grid.getChildren().remove(statsLongRg);
		if (item.getItem().isType(ItemType.LONG_RANGE_WEAPON)) {
			image.setImage(ItemUtils.getItemTypeIcon(ItemType.LONG_RANGE_WEAPON).getImage());
			LongRangeWeapon weapon = item.getItem().getType(LongRangeWeapon.class);

			String minAttr = weapon.getRequirements().stream().filter(AttributeRequirement.class::isInstance)
								                     .map(AttributeRequirement.class::cast)
								                     .map(aReq -> aReq.getAttribute().getShortName()+" "+aReq.getValue())
								                     .collect(Collectors.joining(","));
			statsLongRg.setText(String.format("%s:%s %s:%d\n%s:%s %s:%s %s:%s",
					ItemAttribute.DAMAGE.getShortName(), getWeaponDamageString(item.getDamage(ItemType.LONG_RANGE_WEAPON)),
					ItemAttribute.SPEED.getShortName(), item.getSpeed(ItemType.LONG_RANGE_WEAPON),
					ItemAttribute.RANGE.getShortName(), weapon.getRange(),
					ItemAttribute.ATTRIBUTES.getShortName(), item.getAttribute1(ItemType.LONG_RANGE_WEAPON).getShortName()+"+"+item.getAttribute2(ItemType.LONG_RANGE_WEAPON).getShortName(),
					ItemAttribute.MIN_ATTRIBUTES.getShortName(), minAttr));
			grid.add(statsLongRg, 1, 3);
		}

		// Armor
		grid.getChildren().remove(statsArmor);
		if (item.getItem().isType(ItemType.ARMOR)) {
			image.setImage(ItemUtils.getItemTypeIcon(ItemType.ARMOR).getImage());
			Armor armor = item.getItem().getType(Armor.class);

			String minAttr = armor.getRequirements().stream().filter(AttributeRequirement.class::isInstance)
								                    .map(AttributeRequirement.class::cast)
								                    .map(aReq -> aReq.getAttribute().getShortName()+" "+aReq.getValue())
								                    .collect(Collectors.joining(","));
			statsArmor.setText(String.format("%s:%d %s:%d %s:%d %s:%d %s:%s",
					ItemAttribute.DEFENSE.getShortName(), item.getDefense(ItemType.ARMOR),
					ItemAttribute.DAMAGE_REDUCTION.getShortName(), item.getDamageReduction(ItemType.ARMOR),
					ItemAttribute.HANDICAP.getShortName(), item.getHandicap(ItemType.ARMOR),
					ItemAttribute.TICK_MALUS.getShortName(), item.getTickMalus(ItemType.ARMOR),
					(minAttr.length()>0)?ItemAttribute.MIN_ATTRIBUTES.getShortName():"", minAttr));
			grid.add(statsArmor , 1, 4);
		}

		// Shield
		grid.getChildren().remove(statsShield);
		if (item.getItem().isType(ItemType.SHIELD)) {
			image.setImage(ItemUtils.getItemTypeIcon(ItemType.SHIELD).getImage());
			Shield shield = item.getItem().getType(Shield.class);

			String minAttr = shield.getRequirements().stream().filter(AttributeRequirement.class::isInstance)
								                     .map(AttributeRequirement.class::cast)
								                     .map(aReq -> aReq.getAttribute().getShortName()+" "+aReq.getValue())
								                     .collect(Collectors.joining(","));
			statsShield.setText(String.format("%s:%d %s:%d %s:%d %s:%d %s:%s",
					ItemAttribute.DEFENSE.getShortName(), item.getDefense(ItemType.SHIELD),
					ItemAttribute.DAMAGE_REDUCTION.getShortName(), item.getDamageReduction(ItemType.SHIELD),
					ItemAttribute.HANDICAP.getShortName(), item.getHandicap(ItemType.SHIELD),
					ItemAttribute.TICK_MALUS.getShortName(), item.getTickMalus(ItemType.SHIELD),
					(minAttr.length()>0)?ItemAttribute.MIN_ATTRIBUTES.getShortName():"", minAttr));
			grid.add(statsShield, 1, 5);
		}

		if (item.getItem().isType(ItemType.POTION)) {
			image.setImage(ItemUtils.getItemTypeIcon(ItemType.POTION).getImage());
		}
		if (item.getItem().isType(ItemType.CONTAINER)) {
			image.setImage(ItemUtils.getItemTypeIcon(ItemType.CONTAINER).getImage());
		}

		statsCommon.setText(String.format("%s: %d  %s: %d",
				ItemAttribute.LOAD.getShortName(), item.getItem().getLoad(),
				ItemAttribute.RIGIDITY.getShortName(), item.getItem().getRigidity()));

		grid.getChildren().remove(extra);
		Stream.Builder<String> buf = Stream.builder();
		for (EnhancementReference enRef : item.getEnhancements()) {
			if (enRef.getSpellValue()!=null) {
				buf.add(String.format(UI.getString("screen.enhancements.embedspell.cell"), enRef.getSpellValue().getSpell().getName()));
			} else if (enRef.getSkillSpecialization()!=null) {
				buf.add(enRef.getSkillSpecialization().getName()+"+1");
			} else {
				buf.add(enRef.getEnhancement().getName());
			}
		}
		for (PersonalizationReference enRef :item.getPersonalizations()) {
			buf.add(enRef.getName());
		}
		String textExtra = buf.build().collect(Collectors.joining(","));
		if (!textExtra.isEmpty()) {
			grid.add(extra, 1, 1);
			extra.setText(textExtra);
		}
	}

}
