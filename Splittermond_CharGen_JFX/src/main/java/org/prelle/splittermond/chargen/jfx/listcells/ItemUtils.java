package org.prelle.splittermond.chargen.jfx.listcells;

import java.util.List;

import org.prelle.splimo.Attribute;
import org.prelle.splimo.items.Armor;
import org.prelle.splimo.items.CarriedItem;
import org.prelle.splimo.items.Feature;
import org.prelle.splimo.items.FeatureList;
import org.prelle.splimo.items.ItemType;
import org.prelle.splimo.items.LongRangeWeapon;
import org.prelle.splimo.items.Shield;
import org.prelle.splimo.items.Weapon;
import org.prelle.splimo.persist.WeaponDamageConverter;
import org.prelle.splimo.requirements.AttributeRequirement;
import org.prelle.splimo.requirements.Requirement;
import org.prelle.splimo.requirements.RequirementList;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;

import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * @author prelle
 *
 */
public class ItemUtils {

	//--------------------------------------------------------------------
	public static ImageView getItemTypeIcon(ItemType type) {
		ImageView iView = new ImageView();
		iView.setFitHeight(48);
		iView.setFitWidth(48);
		switch (type) {
		case ARMOR  : iView.setImage(new Image(SpliMoCharGenJFXConstants.class.getResourceAsStream("images/icon_armor.png"))); break;
		case LONG_RANGE_WEAPON: iView.setImage(new Image(SpliMoCharGenJFXConstants.class.getResourceAsStream("images/icon_bow.png"))); break;
		case WEAPON : iView.setImage(new Image(SpliMoCharGenJFXConstants.class.getResourceAsStream("images/icon_sword.png"))); break;
		case SHIELD : iView.setImage(new Image(SpliMoCharGenJFXConstants.class.getResourceAsStream("images/icon_shield.png"))); break;
		case POTION : iView.setImage(new Image(SpliMoCharGenJFXConstants.class.getResourceAsStream("images/icon_potion.png"))); break;
		case CONTAINER: iView.setImage(new Image(SpliMoCharGenJFXConstants.class.getResourceAsStream("images/icon_container.png"))); break;
		case TRAVEL : iView.setImage(new Image(SpliMoCharGenJFXConstants.class.getResourceAsStream("images/icon_travel.png"))); break;
		default:
			break;
		}
//		return itemTypeIcons.get(type);
		return iView;
	}

	//--------------------------------------------------------------------
	static ListView<Feature> fillFeatureListView(CarriedItem item, ListView<Feature> lv, ItemType type) {
		lv.getItems().clear();
		List<Feature> featureList = new FeatureList();

		switch (type) {
		case WEAPON:
			Weapon weapon = item.getItem().getType(Weapon.class);
			featureList = weapon.deliverFeaturesAsDeepClone();
			break;
		case LONG_RANGE_WEAPON:
			LongRangeWeapon longRangeWeapon = item.getItem().getType(LongRangeWeapon.class);
			featureList = longRangeWeapon.deliverFeaturesAsDeepClone();
			break;
		case SHIELD:
			Shield shield = item.getItem().getType(Shield.class);
			featureList = shield.deliverFeaturesAsDeepClone();
			break;
		case ARMOR:
			Armor armor = item.getItem().getType(Armor.class);
			featureList = armor.deliverFeaturesAsDeepClone();
			break;
		default:
			break;
		}

		if (featureList != null && featureList.size() > 0) {
			lv.getItems().addAll(featureList);
		}
		return lv;
	}

	//--------------------------------------------------------------------
	static String getWeaponDamageString(int damage) {
		try {
			return new WeaponDamageConverter().write(damage);
		} catch (Exception e) {
			return "";
		}
	}

	//--------------------------------------------------------------------
	static String getAttributeString(Attribute attr1, Attribute attr2) {
		if (attr1 != null && attr2 != null) {
			return attr1.getShortName() + "/" + attr2.getShortName();
		} else {
			return "Keine";
		}
	}

	//--------------------------------------------------------------------
	static String getMindestAttribute(RequirementList list) {
		StringBuilder sb = new StringBuilder();
		for (Requirement requirement : list) {
			if (requirement instanceof AttributeRequirement) {
				AttributeRequirement req = (AttributeRequirement) requirement;
				sb.append(req.getAttribute().getShortName());
				sb.append(" ");
				sb.append(req.getValue());
				sb.append(", ");
			}
		}
		return sb.length() >0 ? sb.substring(0, sb.length() - 2) : "Keine";
	}

}
