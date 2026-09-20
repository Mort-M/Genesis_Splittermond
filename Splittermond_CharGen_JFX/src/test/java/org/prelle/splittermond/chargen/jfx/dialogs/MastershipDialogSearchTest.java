package org.prelle.splittermond.chargen.jfx.dialogs;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.prelle.splimo.MastershipOrSpecialization;
import org.prelle.splimo.Skill;

public class MastershipDialogSearchTest {

	@Test
	public void filtersNamesCaseInsensitively() {
		MastershipOrSpecialization entry = named("Akrobatikmeister");

		assertTrue(MastershipSearchFilter.matches(entry, MastershipSearchFilter.normalize("a")));
		assertTrue(MastershipSearchFilter.matches(entry, MastershipSearchFilter.normalize("MEISTER")));
		assertFalse(MastershipSearchFilter.matches(entry, MastershipSearchFilter.normalize("Zauber")));
	}

	@Test
	public void treatsUmlautsLikeTheirBaseLetter() {
		MastershipOrSpecialization entry = named("Überlebenskünstler");

		assertTrue(MastershipSearchFilter.matches(entry, MastershipSearchFilter.normalize("uber")));
		assertTrue(MastershipSearchFilter.matches(entry, MastershipSearchFilter.normalize("KÜNSTLER")));
	}

	@Test
	public void emptySearchKeepsEveryEntry() {
		assertTrue(MastershipSearchFilter.matches(named("Sprinter"), MastershipSearchFilter.normalize("  ")));
	}

	private static MastershipOrSpecialization named(String name) {
		return new MastershipOrSpecialization() {
			@Override
			public String getName() {
				return name;
			}

			@Override
			public Skill getSkill() {
				return null;
			}

			@Override
			public int getLevel() {
				return 1;
			}

			@Override
			public int compareTo(MastershipOrSpecialization other) {
				return name.compareTo(other.getName());
			}
		};
	}
}
