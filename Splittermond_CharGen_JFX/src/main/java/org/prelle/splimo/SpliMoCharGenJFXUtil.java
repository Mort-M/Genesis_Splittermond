/**
 * 
 */
package org.prelle.splimo;

import java.util.Iterator;

import javafx.scene.Parent;
import javafx.scene.layout.VBox;

import org.apache.log4j.Logger;
import org.prelle.splimo.creature.CreatureReference;
import org.prelle.splimo.items.CarriedItem;
import org.prelle.splimo.modifications.AttributeModification;
import org.prelle.splimo.modifications.MastershipModification;
import org.prelle.splimo.modifications.SkillModification;
import org.prelle.splittermond.jfx.creatures.AttributeViewPane;
import org.prelle.splittermond.jfx.creatures.CreatureFeatureViewPane;
import org.prelle.splittermond.jfx.creatures.CreatureTypeViewPane;
import org.prelle.splittermond.jfx.creatures.SkillViewPane;
import org.prelle.splittermond.jfx.creatures.SpellViewPane;
import org.prelle.splittermond.jfx.creatures.WeaponViewPane;

import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.Modifyable;

/**
 * @author prelle
 *
 */
public class SpliMoCharGenJFXUtil {

	private final static Logger logger = Logger.getLogger("splittermond.jfx");

	//-------------------------------------------------------------------
	/**
	 * @param mods
	 * @return
	 */
	public static String getModificationTooltip(Modifyable mods) {
		StringBuffer buf = new StringBuffer();
		for (Iterator<Modification> it=mods.getModifications().iterator(); it.hasNext();) {
			Modification mod = it.next();
			if (mod instanceof AttributeModification) {
				AttributeModification aMod = (AttributeModification)mod;
				if (aMod.isConditional())
					continue;
				buf.append(aMod.getValue());
			} else if (mod instanceof SkillModification) {
					buf.append( ((SkillModification)mod).getValue());
			} else if (mod instanceof MastershipModification) {
				MastershipModification mmod = (MastershipModification)mod;
				if (mmod.getMastership()!=null) {
					buf.append(mmod.getMastership().getName());
				} else if (mmod.getSpecialization()!=null) {
					buf.append(mmod.getSpecialization().getName());
				} else {
					logger.warn("Don't know how to display to user: "+mod);
					buf.append(String.valueOf(mod));
				}
			} else {
				logger.warn("Unsupported modification "+mod.getClass());
				buf.append(mod.toString());
			}
			buf.append(" (");
			if (mod.getSource()==null) {
				buf.append("?");
			} else if (mod.getSource().getClass()==CarriedItem.class) {
				buf.append(  ((CarriedItem)mod.getSource()).getName() );
			} else if (mod.getSource().getClass()==String.class) {
				if (mod.getSource()==SplitterTools.LEVEL2)
					buf.append(SplitterMondCore.getI18nResources().getString("modification.source.level2"));
				else if (mod.getSource()==SplitterTools.LEVEL3)
					buf.append(SplitterMondCore.getI18nResources().getString("modification.source.level3"));
				else if (mod.getSource()==SplitterTools.LEVEL4)
					buf.append(SplitterMondCore.getI18nResources().getString("modification.source.level4"));
				else if (((String)mod.getSource()).startsWith(EquipmentTools.TOTAL_HANDICAP))
					buf.append(SplitterMondCore.getI18nResources().getString("modification.source.handicap"));
				else
					logger.warn("Unknown modification source string '"+mod.getSource()+"/"+mod.getClass()+"'");
			} else if (mod.getSource().getClass()==Attribute.class) {
				buf.append(  ((Attribute)mod.getSource()).getName() );
			} else if (mod.getSource().getClass()==Race.class) {
				buf.append(  ((Race)mod.getSource()).getName() );
			} else if (mod.getSource().getClass()==Power.class) {
				buf.append(  ((Power)mod.getSource()).getName() );
			} else if (mod.getSource().getClass()==PowerReference.class) {
				buf.append(  ((PowerReference)mod.getSource()).getPower().getName()+" "+((PowerReference)mod.getSource()).getCount() );
			} else if (mod.getSource().getClass()==MastershipReference.class) {
				buf.append(  ((MastershipReference)mod.getSource()).getMastership().getName());
			} else if (mod.getSource().getClass()==Mastership.class) {
				buf.append(  ((Mastership)mod.getSource()).getName());
			} else {
				logger.warn("Unsupported modification source "+mod.getSource().getClass());
				buf.append(String.valueOf(mod.getSource()));
			}
			buf.append(")");
			
			if (it.hasNext())
				buf.append("\n");
		}
		return buf.toString();
	}
	
	
	//-------------------------------------------------------------------
	public static Parent getDisplayNode(CreatureReference model) {
		CreatureTypeViewPane  typeView;
		AttributeViewPane attribView;
		WeaponViewPane  weaponView;
		SkillViewPane  skillView;
		SpellViewPane  spellView;
		CreatureFeatureViewPane  featView;
		
		typeView   = new CreatureTypeViewPane();
		attribView = new AttributeViewPane();
		weaponView = new WeaponViewPane();
		skillView  = new SkillViewPane(true);
		spellView  = new SpellViewPane();
		featView   = new CreatureFeatureViewPane();

		VBox layout = new VBox();
		layout.getChildren().addAll(typeView, attribView, weaponView, skillView, spellView, featView);
		
		typeView.setData(model.getTemplate());
		attribView.setData(model.getTemplate());
		weaponView.setData(model.getTemplate());
		skillView.setData(model.getTemplate());
		spellView.setData(model.getTemplate());
		featView.setData(model.getTemplate());
		
		return layout;
	}
}
