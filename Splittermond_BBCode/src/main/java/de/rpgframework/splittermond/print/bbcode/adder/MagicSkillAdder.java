package de.rpgframework.splittermond.print.bbcode.adder;

import java.util.List;

import org.prelle.splimo.MastershipReference;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.SpliMoCharacter;

import de.rpgframework.splittermond.print.SpliMoLabels;
import de.rpgframework.splittermond.print.bbcode.BBCodes;
import de.rpgframework.splittermond.print.bbcode.SingleBBCodeGenerator;

/**
 * Adds all magic skills with a <code>value > 0</code> in the following way:<br>
 * <br>
 * [b]skillName (skillValue):[/b] List of spells (spellValue
 * <code>if != skillValue</code>) [i]static string "Meisterschaften": [/i] List
 * of mastership (level if applicable)
 * 
 * 
 * @author frank.buettner
 * 
 */
public class MagicSkillAdder extends SkillAdder {

	public MagicSkillAdder(StringBuilder bbcodeBuilder,
			SpliMoCharacter spliMoCharacter) {
		super(bbcodeBuilder, spliMoCharacter);
	}

	@Override
	public void add() {

		for (SkillValue skillValue : spliMoCharacter.getSkills(SkillType.MAGIC)) {
			StringBuilder skillBuilder = new StringBuilder();

			int value = skillValue.getValue();
			// print magic skill type only when its value is > 0
			if (value <= 0) {
				continue;
			}

			// add spells
			int skillValueWithAtts = getNonCombatSkillValueWithAtts(skillValue.getSkill());
			SpellAdder spellAdder = new SpellAdder(skillBuilder,
					spliMoCharacter, skillValue.getSkill(), skillValueWithAtts);
			spellAdder.add();

			String skillName = skillValue.getSkill().getName();

			// add masterships
			List<MastershipReference> masterships = skillValue.getMasterships();
			if (masterships != null && masterships.size() > 0) {
				SingleBBCodeGenerator generator = new SingleBBCodeGenerator(
						SpliMoLabels.getLabelMasteries() + ": ");
				skillBuilder.append(generator.addBBCode(BBCodes.ITALIC)
						.toString());

				for (MastershipReference mastershipReference : masterships) {
					String name = (mastershipReference.getMastership()!=null)?
							mastershipReference.getMastership().getName()
							:
							mastershipReference.getSpecialization().getName()+" "+mastershipReference.getSpecialization().getLevel();
					skillBuilder.append(name);
					addDelimiter(skillBuilder);
				}

				// delete last delimiter
				deleteLastDelimiter(skillBuilder);
			}

			String skillNameAndValue = String.format("%s (%s)", skillName,
					getNonCombatSkillValueWithAtts(skillValue.getSkill()));
			SingleBBCodeGenerator.addGenericAttribute(bbcodeBuilder,
					skillNameAndValue, skillBuilder.toString());
		}
	}
}
