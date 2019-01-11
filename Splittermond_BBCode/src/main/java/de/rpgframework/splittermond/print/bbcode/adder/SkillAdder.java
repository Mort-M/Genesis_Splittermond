package de.rpgframework.splittermond.print.bbcode.adder;

import java.util.List;

import org.prelle.splimo.Attribute;
import org.prelle.splimo.MastershipReference;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.SpliMoCharacter;

import de.rpgframework.splittermond.print.SpliMoLabels;
import de.rpgframework.splittermond.print.bbcode.BBCodes;
import de.rpgframework.splittermond.print.bbcode.SingleBBCodeGenerator;

/**
 * Adds all {@link SkillValue}s of all {@link Skill}s.
 *
 * @author frank.buettner
 *
 */
public class SkillAdder extends AbstractAdder {
	public SkillAdder(StringBuilder bbcodeBuilder,
			SpliMoCharacter spliMoCharacter) {
		super(bbcodeBuilder, spliMoCharacter);
	}

	/**
	 * Adds all {@link SkillValue}s of the given {@link SkillType}. Only
	 * operable with non magic skills (@see {@link MagicSkillAdder}).
	 *
	 * @throws IllegalArgumentException
	 *             if {@link SkillType#MAGIC} is given
	 *
	 */
	private void addSkillType(SkillType skillType) {

		if (SkillType.MAGIC.equals(skillType)) {
			IllegalArgumentException e = new IllegalArgumentException(
					"this method is only operable with non magic skill types");
			logger.error(e);
			throw e;
		}

		StringBuilder skillBuilder = new StringBuilder();

		List<SkillValue> skillValues = spliMoCharacter.getSkills(skillType);
		for (SkillValue skillValue : skillValues) {
			int value = skillValue.getValue();
			// print spell skill type only when value > 0
			if (value == 0) {
				continue;
			}

			SingleBBCodeGenerator generator = new SingleBBCodeGenerator(
					skillValue.getSkill().getName() + ": ");
			skillBuilder.append(generator.addBBCode(BBCodes.ITALIC).toString());

			// calculate effective value only for non combat skills
			if (!Skill.SkillType.COMBAT.equals(skillValue.getSkill().getType())) {
				value = getNonCombatSkillValueWithAtts(skillValue.getSkill());
			}
			skillBuilder.append(value);

			if (!Skill.SkillType.COMBAT.equals(skillValue.getSkill().getType())) {
				skillBuilder.append(" (").append(skillValue.getValue())
						.append(")");
			}

			// masterships
			List<MastershipReference> masterships = skillValue.getMasterships();
			if (masterships != null && masterships.size() > 0) {
				skillBuilder.append(" ")
						.append(SpliMoLabels.getLabelMasteries()).append(" ");
				for (MastershipReference mastershipReference : masterships) {
					String name = mastershipReference.getMastership().getName();
					skillBuilder.append(name);
					addDelimiter(skillBuilder);
				}
				deleteLastDelimiter(skillBuilder);
			}
			addDelimiter(skillBuilder);

		}
		deleteLastDelimiter(skillBuilder);
		SingleBBCodeGenerator.addGenericAttribute(bbcodeBuilder,
				skillType.getName(), skillBuilder.toString());
	}

	/**
	 * Returns the effective skill value inclusive both attributes. Only
	 * operable with non combat skills.
	 */
	protected int getNonCombatSkillValueWithAtts(Skill skill) {

		if (SkillType.COMBAT.equals(skill.getType())) {
			IllegalArgumentException e = new IllegalArgumentException(
					String.format("Should calculate skill value with attributes. Funtions only for non-combat skills."));
			logger.error(e);
			throw e;
		}

		int value = spliMoCharacter.getSkillValue(skill).getValue();

		Attribute att1 = skill.getAttribute1();
		Attribute att2 = skill.getAttribute2();
		value += spliMoCharacter.getAttribute(att1).getValue();
		value += spliMoCharacter.getAttribute(att2).getValue();
		return value;
	}

	@Override
	public void add() {
		// normal
		addSkillType(SkillType.NORMAL);

		// combat
		addSkillType(SkillType.COMBAT);

		// // magic
		MagicSkillAdder magicSkillAdder = new MagicSkillAdder(bbcodeBuilder,
				spliMoCharacter);
		magicSkillAdder.add();
	}
}
