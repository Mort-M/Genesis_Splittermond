package org.prelle.splittermond.chargen.jfx.sections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Spell;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splittermond.chargen.jfx.listcells.SpellListCell;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * @author Stefan Prelle
 *
 */
public class SpellListSection extends GenericListSection<Spell> {

	private ObjectProperty<Skill> school = new SimpleObjectProperty<Skill>();
	
	//-------------------------------------------------------------------
	public SpellListSection(String title, CharacterController ctrl, ScreenManagerProvider provider) {
		super(title, ctrl, provider);
		list.setCellFactory( lv -> new SpellListCell(ctrl, school));
		
//		setData(Splitter);
		setDeleteButton(null);
		setAddButton(null);
		VBox.setVgrow(list, Priority.ALWAYS);
		list.setMaxHeight(Double.MAX_VALUE);
		list.setStyle("-fx-pref-width: 32em; -fx-min-height: 40em");
		
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splittermond.chargen.jfx.sections.GenericListSection#onAdd()
	 */
	@Override
	protected void onAdd() {
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splittermond.chargen.jfx.sections.GenericListSection#onDelete()
	 */
	@Override
	protected void onDelete() {
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.rpgframework.jfx.Section#refresh()
	 */
	@Override
	public void refresh() {
		List<Spell> data = new ArrayList<>();
		if (school!=null) {
			List<Spell> toAdd = SplitterMondCore.getSpells(school.get());
			Collections.sort(toAdd, new Comparator<Spell>() {
				public int compare(Spell o1, Spell o2) {
					Integer l1 = o1.getLevelInSchool(school.get());
					Integer l2 = o2.getLevelInSchool(school.get());
					int cmp = l1.compareTo(l2);
					if (cmp!=0) return cmp;
					return o1.getName().compareTo(o2.getName());
				}
			});
			data.addAll(toAdd);
		}
		setData(data);
	}

	//-------------------------------------------------------------------
	public void setSchool(Skill value) {
		this.school.set(value);
		refresh();
	}

}
