package de.rpgframework.splittermond.print.bbcode.adder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.Spell;
import org.prelle.splimo.SpellValue;
import org.prelle.splimo.SpliMoCharacter;

/**
 * Adds all {@link Spell}s of a specific magic {@link Skill}.
 * 
 * @author frank.buettner
 * 
 */
public class SpellAdder extends AbstractAdder {

	private Skill skill;
	private int skillValueWithAtts;

	/**
	 * @param skillValueWithAtts
	 *            the effective skill value, which includes the values of both
	 *            attributes
	 * @throws IllegalArgumentException
	 *             if a non magic skill is given
	 */
	public SpellAdder(StringBuilder bbcodeBuilder,
			SpliMoCharacter spliMoCharacter, Skill skill, int skillValueWithAtts) {
		super(bbcodeBuilder, spliMoCharacter);
		this.skillValueWithAtts = skillValueWithAtts;
		if (!SkillType.MAGIC.equals(skill.getType())) {
			IllegalArgumentException e = new IllegalArgumentException(
					"Only magic skills are allowed. Given skill: "
							+ skill.getName());
			logger.error(e);
			throw e;
		}
		this.skill = skill;

	}

	@Override
	public void add() {
		List<SpellValue> spells = spliMoCharacter.getSpells();

		// first: get all spells for the skill and sort them afterwards
		List<SpellValue> spellValues = new ArrayList<SpellValue>();
		for (SpellValue spellValue : spells) {
			if (!skill.equals(spellValue.getSkill())) {
				continue;
			}
			spellValues.add(spellValue);
		}
		logger.trace(String.format("Found %d spells for skill %s",
				spells.size(), skill.getName()));

		// sort by spell level
		Collections.sort(spellValues, new SpellSorter());

		// add to string builder
		for (SpellValue spellValue : spellValues) {
			bbcodeBuilder.append(spellValue.getSpell().getName());

			// add the value only if the spell value is affected by a
			// mastership (then it differs from the skill value)
			if (true) {
				/*
				 * if (true): see feature request
				 * http://rpgframework.de/trac/ticket/11
				 */
				int spellValueValue = spliMoCharacter
						.getSpellValueFor(spellValue);

				if (spellValueValue != skillValueWithAtts) {
					bbcodeBuilder.append(" (");
					bbcodeBuilder.append(spellValueValue);
					bbcodeBuilder.append(")");
				}
			}
			addDelimiter(bbcodeBuilder);
		}
		deleteLastDelimiter(bbcodeBuilder);
	}

	/**
	 * Sorts the spells in ascending order on the basis of their level.
	 * 
	 * @author frank.buettner
	 * 
	 */
	private class SpellSorter implements Comparator<SpellValue> {

		@Override
		public int compare(SpellValue o1, SpellValue o2) {
			if (o1 == null && o2 == null) {
				return 0;
			}
			if (o1 == null) {
				return -1;
			}
			if (o2 == null) {
				return 1;
			}

			Integer o1Level = new Integer(o1.getSpellLevel());
			Integer o2Level = new Integer(o2.getSpellLevel());

			return o1Level.compareTo(o2Level);
		}
	}
}
