package de.rpgframework.splittermond.print.bbcode.adder;

import org.prelle.splimo.Attribute;
import org.prelle.splimo.AttributeValue;
import org.prelle.splimo.SpliMoCharacter;

import de.rpgframework.splittermond.print.bbcode.BBCodes;
import de.rpgframework.splittermond.print.bbcode.SingleBBCodeGenerator;

/**
 * Adds a string representation for the character's attributes. Looks like<br>
 * <code>
 * AUS 3 | GK 5<br>
BEW 1 | GSW 6<br>
INT 3 | INI 7<br>
KON 1 | LP 6<br>
MYS 4 | FO 33<br>
ST� 1 | VTD 15<br>
VER 3 | GW 20<br>
WIL 5 | KW 18
 * </code>
 * 
 * @author frank.buettner
 * 
 */
public class AttributeAdder extends AbstractAdder {

	private StringBuilder attributeAdder;

	public AttributeAdder(StringBuilder bbcodeBuilder,
			SpliMoCharacter spliMoCharacter) {
		super(bbcodeBuilder, spliMoCharacter);
		attributeAdder = new StringBuilder();
	}

	@Override
	public void add() {
		bbcodeBuilder.append(SingleBBCodeGenerator.LINE_FEED);

		addAttributeLine(spliMoCharacter.getAttribute(Attribute.CHARISMA),
				spliMoCharacter.getAttribute(Attribute.SIZE));
		addAttributeLine(spliMoCharacter.getAttribute(Attribute.AGILITY),
				spliMoCharacter.getAttribute(Attribute.SPEED));
		addAttributeLine(spliMoCharacter.getAttribute(Attribute.INTUITION),
				spliMoCharacter.getAttribute(Attribute.INITIATIVE));
		addAttributeLine(spliMoCharacter.getAttribute(Attribute.CONSTITUTION),
				spliMoCharacter.getAttribute(Attribute.LIFE));
		addAttributeLine(spliMoCharacter.getAttribute(Attribute.MYSTIC),
				spliMoCharacter.getAttribute(Attribute.FOCUS));
		addAttributeLine(spliMoCharacter.getAttribute(Attribute.STRENGTH),
				spliMoCharacter.getAttribute(Attribute.DEFENSE));
		addAttributeLine(spliMoCharacter.getAttribute(Attribute.MIND),
				spliMoCharacter.getAttribute(Attribute.MINDRESIST));
		addAttributeLine(spliMoCharacter.getAttribute(Attribute.WILLPOWER),
				spliMoCharacter.getAttribute(Attribute.BODYRESIST));

		// add typewriter
		SingleBBCodeGenerator singleBBCodeGenerator = new SingleBBCodeGenerator(
				attributeAdder.toString());
		singleBBCodeGenerator = singleBBCodeGenerator
				.addBBCode(BBCodes.TYPEWRITER);
		bbcodeBuilder.append(singleBBCodeGenerator.toString());

		bbcodeBuilder.append(SingleBBCodeGenerator.LINE_FEED);
	}

	/**
	 * Adds one line of a pair of attributes. Looks like<br>
	 * <code>
	 * AUS 3 | GK 5
	 * </code>
	 * 
	 */
	private void addAttributeLine(AttributeValue attribute1,
			AttributeValue attribute2) {
		attributeAdder.append(attribute1.getAttribute().getShortName());
		attributeAdder.append(" ");
		attributeAdder.append(attribute1.getValue());
		attributeAdder.append(" | ");
		attributeAdder.append(attribute2.getAttribute().getShortName());
		attributeAdder.append(" ");
		attributeAdder.append(attribute2.getValue());
		attributeAdder.append(SingleBBCodeGenerator.LINE_FEED);
	}
}
