package org.prelle.splittermond.chargen.jfx.sections;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.splimo.Skill;
import org.prelle.splimo.SkillSpecialization;
import org.prelle.splimo.Spell;
import org.prelle.splimo.SpellType;
import org.prelle.splimo.SpellValue;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splittermond.chargen.jfx.listcells.SkillSpecListCell;
import org.prelle.splittermond.chargen.jfx.skills.ListElemSpecialization;

/**
 * @author Stefan Prelle
 *
 */
public class SchoolSpecializationSection extends GenericListSection<ListElemSpecialization> {

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(PowerSection.class.getName());

	public enum SpecializationSort {
		ACTIVE_SPELLS,
		ALL_SPELLS,
		ALPHABET
	}

	private Skill school;
	private Collator collat = Collator.getInstance();
	
	private Comparator<ListElemSpecialization> comparator;
	private Comparator<ListElemSpecialization> compAlpha;
	private Comparator<ListElemSpecialization> compActive;
	private Comparator<ListElemSpecialization> compAll;
	
	//-------------------------------------------------------------------
	public SchoolSpecializationSection(String title, CharacterController ctrl, ScreenManagerProvider provider) {
		super(title, ctrl, provider);
		list.setCellFactory( lv -> new SkillSpecListCell(ctrl));
		
//		setData(Splitter);
		setDeleteButton(null);
		setAddButton(null);
		list.setStyle("-fx-pref-width: 23em");
		
		compAlpha = new Comparator<ListElemSpecialization>() {
			public int compare(ListElemSpecialization o1, ListElemSpecialization o2) {
				return collat.compare(o1.data.getName(), o2.data.getName());
			}
		};
		compActive = new Comparator<ListElemSpecialization>() {
			public int compare(ListElemSpecialization o1, ListElemSpecialization o2) {
				int cmp = ((Integer)o1.count).compareTo(o2.count);
				if (cmp!=0)
					return -cmp;
				return collat.compare(o1.data.getName(), o2.data.getName());
			}
		};
		compAll = new Comparator<ListElemSpecialization>() {
			public int compare(ListElemSpecialization o1, ListElemSpecialization o2) {
				int cmp = ((Integer)o1.countAll).compareTo(o2.countAll);
				if (cmp!=0)
					return -cmp;
				return collat.compare(o1.data.getName(), o2.data.getName());
			}
		};
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splittermond.chargen.jfx.sections.GenericListSection#onAdd()
	 */
	@Override
	protected void onAdd() {
		logger.trace("onAdd");
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splittermond.chargen.jfx.sections.GenericListSection#onDelete()
	 */
	@Override
	protected void onDelete() {
		logger.trace("onDelete");
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.rpgframework.jfx.Section#refresh()
	 */
	@Override
	public void refresh() {
		List<ListElemSpecialization> data = new ArrayList<>();
		if (school!=null) {
			for (SkillSpecialization spec : school.getSpecializations()) {
				ListElemSpecialization item = new ListElemSpecialization();
				item.skill = school;
				item.data  = spec;
				item.level = control.getModel().getSkillSpecializationLevel(spec);
				// Count how often the spec. is used by spells
				for (SpellValue spell : control.getModel().getSpells()) {
					if (spell.getSkill()!=school)
						continue;
					for (SpellType type : spell.getSpell().getTypes()) {
						if (spec.getId().equalsIgnoreCase(type.name()))
							item.count++;
					}
				}
				// Count how often the spec. is used by all spells
				for (Spell spell : SplitterMondCore.getSpells(school)) {
					for (SpellType type : spell.getTypes()) {
						if (spec.getId().equalsIgnoreCase(type.name()))
							item.countAll++;
					}
				}
				data.add(item);
			}
		}
		
		if (comparator!=null)
			Collections.sort(data, comparator);
		setData(data);
	}

	//-------------------------------------------------------------------
	public void setSchool(Skill value) {
		this.school = value;
		refresh();
	}

	//-------------------------------------------------------------------
	public void setSort(SpecializationSort sort) {
		switch (sort) {
		case ACTIVE_SPELLS: comparator = compActive; break;
		case ALPHABET: comparator = compAlpha; break;
		case ALL_SPELLS: comparator = compAll; break;
		}
		refresh();
	}


}
