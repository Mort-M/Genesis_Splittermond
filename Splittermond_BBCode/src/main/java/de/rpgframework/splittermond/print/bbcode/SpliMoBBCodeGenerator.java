package de.rpgframework.splittermond.print.bbcode;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.prelle.splimo.Background;
import org.prelle.splimo.Culture;
import org.prelle.splimo.LanguageReference;
import org.prelle.splimo.Moonsign;
import org.prelle.splimo.PowerReference;
import org.prelle.splimo.Race;
import org.prelle.splimo.SpliMoCharacter;

import de.rpgframework.character.RuleSpecificCharacterObject;
import de.rpgframework.splittermond.SpliMoLabels;
import de.rpgframework.splittermond.print.bbcode.BBCodes;
import de.rpgframework.splittermond.print.bbcode.SingleBBCodeGenerator;
import de.rpgframework.splittermond.print.bbcode.adder.AbstractAdder;
import de.rpgframework.splittermond.print.bbcode.adder.AttributeAdder;
import de.rpgframework.splittermond.print.bbcode.adder.ResourcesAdder;
import de.rpgframework.splittermond.print.bbcode.adder.SkillAdder;

/**
 * Generates a BBCode encoded {@link String} representation of a
 * {@link SpliMoCharacter}.
 * 
 * @author frank.buettner
 * 
 */
public class SpliMoBBCodeGenerator {

	private StringBuilder bbcodeBuilder;

	private static Logger logger = Logger
			.getLogger(SpliMoBBCodeGenerator.class);
	private SpliMoCharacter spliMoCharacter;

	/**
	 * @param character
	 *            a {@link SpliMoCharacter}
	 * @throws IllegalArgumentException
	 *             if the given character is not an instance of
	 *             {@link SpliMoCharacter}
	 */
	public SpliMoBBCodeGenerator(RuleSpecificCharacterObject character) {
		if (!(character instanceof SpliMoCharacter)) {
			String msg = String.format(
					"Expected a SpliMoCharacter but got a %s",
					character != null ? character.getClass().getSimpleName()
							: "null");
			RuntimeException e = new IllegalArgumentException(msg);
			logger.error(e);
			throw e;
		}

		spliMoCharacter = (SpliMoCharacter) character;

		bbcodeBuilder = new StringBuilder();
	}

	public String generateBBCode() {
		logger.trace("enter generateBBCode");
		
		bbcodeBuilder = new StringBuilder();

		// Name
		addName(spliMoCharacter);

		// Ausbildung
		addEducation(spliMoCharacter);

		// Kultur
		addCulture();

		// Rasse
		addRace();

		// Spachen
		addLanguages();

		// Abstammung
		addBackground();

		// Mondzeichen
		Moonsign moonsign = spliMoCharacter.getSplinter();
		SingleBBCodeGenerator.addGenericAttribute(bbcodeBuilder,
				SpliMoLabels.getLabelMoonsign(), moonsign.getName());

		// Schw�che
		StringBuffer bufWeak = new StringBuffer();
		for (Iterator<String> it = spliMoCharacter.getWeaknesses().iterator(); it.hasNext(); ) {
			bufWeak.append(it.next());
			if (it.hasNext())
				bufWeak.append(", ");
		}
		String weakness = spliMoCharacter.getWeaknesses().isEmpty()?null:bufWeak.toString();
		SingleBBCodeGenerator.addGenericAttribute(bbcodeBuilder,
				SpliMoLabels.getLabelWeaknesses(), weakness);

		// << Attribute | berechnete Werte >>
		new AttributeAdder(bbcodeBuilder, spliMoCharacter).add();

		// Talente + Meisterschaften
		new SkillAdder(bbcodeBuilder, spliMoCharacter).add();

		// St�rken
		addPowers();

		// Ressourcen
		new ResourcesAdder(bbcodeBuilder, spliMoCharacter).add();

		String result = bbcodeBuilder.toString();
		logger.trace(result);
		return result;

	}

	private void addPowers() {
		StringBuilder powerBuilder = new StringBuilder();
		List<PowerReference> powers = spliMoCharacter.getPowers();
		for (PowerReference powerReference : powers) {
			powerBuilder.append(powerReference.getPower().getName());
			AbstractAdder.addDelimiter(powerBuilder);

		}
		// delete last delimiter
		AbstractAdder.deleteLastDelimiter(powerBuilder);
		SingleBBCodeGenerator.addGenericAttribute(bbcodeBuilder,
				SpliMoLabels.getLabelPower(), powerBuilder.toString());
	}

	private void addBackground() {
		Background background = spliMoCharacter.getBackground();
		SingleBBCodeGenerator.addGenericAttribute(bbcodeBuilder,
				SpliMoLabels.getLabelBackground(), background.getName());
	}

	private void addLanguages() {
		StringBuilder languageBuilder = new StringBuilder();
		List<LanguageReference> languages = spliMoCharacter.getLanguages();
		for (LanguageReference languageReference : languages) {
			languageBuilder.append(languageReference.getLanguage().getName());
			AbstractAdder.addDelimiter(languageBuilder);
		}
		// delete last delimiter
		if (languageBuilder.length()>3) {
			AbstractAdder.deleteLastDelimiter(languageBuilder);
		}
		SingleBBCodeGenerator.addGenericAttribute(bbcodeBuilder,
				SpliMoLabels.getLabelLanguages(), languageBuilder.toString());
	}

	private void addRace() {
		Race race = spliMoCharacter.getRace();
		SingleBBCodeGenerator.addGenericAttribute(bbcodeBuilder,
				SpliMoLabels.getLabelRace(), race.getName());
	}

	private void addCulture() {
		Culture culture = spliMoCharacter.getCulture();
		SingleBBCodeGenerator.addGenericAttribute(bbcodeBuilder,
				SpliMoLabels.getLabelCulture(), culture.getName());
	}

	private void addEducation(SpliMoCharacter character) {
		SingleBBCodeGenerator.addGenericAttribute(bbcodeBuilder, SpliMoLabels
				.getLabelEducation(), character.getEducation().getName());
	}

	private void addName(SpliMoCharacter character) {
		String charName = character.getName();
		charName = new SingleBBCodeGenerator(charName).addBBCode(BBCodes.BOLD,
				BBCodes.UNDERLINE).toString();
		bbcodeBuilder.append(charName).append(SingleBBCodeGenerator.LINE_FEED)
				.append(SingleBBCodeGenerator.LINE_FEED);
	}

}
