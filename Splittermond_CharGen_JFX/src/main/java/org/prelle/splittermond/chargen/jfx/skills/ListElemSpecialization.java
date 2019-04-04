package org.prelle.splittermond.chargen.jfx.skills;

import org.prelle.splimo.Skill;
import org.prelle.splimo.SkillSpecialization;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class ListElemSpecialization {
	public Skill skill;
	public SkillSpecialization data;
	public int level;
	public int count;
	public int countAll;
	public BooleanProperty editable = new SimpleBooleanProperty();
}