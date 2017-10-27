package de.rpgframework.splittermond.print.bbcode;

import de.rpgframework.character.RuleSpecificCharacterObject;

/**
 * Contains some "standard" BBCode-Tags used for the export of a
 * {@link RuleSpecificCharacterObject}.
 * 
 * @see http://www.java-forum.org/misc.php?do=bbcode
 * @author frank.buettner
 * 
 */
public enum BBCodes {

	BOLD("[b]", "[/b]", false), 
	ITALIC("[i]", "[/i]", false), 
	UNDERLINE("[u]","[/u]", false),
	/**
	 * For the options see http://www.w3schools.com/html/html_colornames.asp
	 */
	COLOR("[color=" + BBCodeConstants.OPTION_STRING + "]", "[/color]", true),
	SIZE("[size=" + BBCodeConstants.OPTION_STRING + "]","[/size]", true),
	HIGHLIGHT("[highlight]","[/highlight]", false),
	TYPEWRITER("[tt]","[/tt]",false);
	

	/**
	 * the opening tag
	 */
	public final String open;

	/**
	 * the closing tag
	 */
	public final String close;

	/**
	 * True, if this BBCode has an option - false otherwise.
	 */
	public final boolean supportsOption;

	BBCodes(String open, String close, boolean supportsOption) {
		this.open = open;
		this.close = close;
		this.supportsOption = supportsOption;
	}
	
	@Override
	public String toString() {
		//TODO really the name?
		return String.format(this.name());
	}
}
