package de.rpgframework.splittermond.print.bbcode;

/**
 * Takes a text and surrounds it with BBCode-Tags.
 * <p>
 * For that, the chaining pattern is used, so a specific text might get multiple
 * option supported tags in one line.
 * <p>
 * <code> new BBCodeGenerator("The Bold, the Italic and the Blue").addBBCode(BBCodes.BOLD, BBCodes.ITALIC).addBBCode(BBCodes.COLOR,"BLUE"); </code>
 * <br>
 * <br>
 * generates<br>
 * <br>
 * <code> [color=BLUE][i][b]The Bold, the Italic and the Blue[/b][/i][/color]</code>
 * 
 * 
 * @author frank.buettner
 * 
 */
public class SingleBBCodeGenerator {

	public static final String LINE_FEED = System.getProperty("line.separator");

	private StringBuilder textBuilder;

	/**
	 * @param text
	 *            the text which should be surrounded by {@link BBCodes}
	 */
	public SingleBBCodeGenerator(String text) {
		textBuilder = new StringBuilder();
		textBuilder.append(text);
	}

	/**
	 * Surrounds the text with the BBCode-Tags. The option is set if supported
	 * by the BBCode. An exception is thrown otherwise.
	 * 
	 * @param bbcode
	 *            its tags will surround the text
	 * @param option
	 *            e.g. the color for {@link BBCodes#COLOR}. May be null.
	 * @return
	 * @throws BBCodeOptionNotSupportedException
	 *             if the given {@link BBCodes} doesn't support an option and
	 *             option is not empty
	 */
	public SingleBBCodeGenerator addBBCode(BBCodes bbcode, String option)
			throws BBCodeOptionNotSupportedException {
		
		if (!option.trim().isEmpty() && !bbcode.supportsOption) {
			throw new BBCodeOptionNotSupportedException(bbcode, option);
		}

		String open = bbcode.open;
		if (!option.trim().isEmpty()) {
			open = open.replace(BBCodeConstants.OPTION_STRING, option);
		}

		addBBCodeInternal(open, bbcode.close);

		return this;
	}

	/**
	 * With this method, multiple {@link BBCodes} can be added at a time. These
	 * {@link BBCodes} do not support options.
	 */
	public SingleBBCodeGenerator addBBCode(BBCodes... bbcodes) {
		for (BBCodes bbCode : bbcodes) {
			addBBCodeInternal(bbCode.open, bbCode.close);
		}
		return this;
	}

	/**
	 * Surrounds the {@link #textBuilder} with the open and the close string.
	 */
	private void addBBCodeInternal(String open, String close) {
		textBuilder.insert(0, open);
		textBuilder.append(close);
	}

	@Override
	public String toString() {
		return textBuilder.toString();
	}

	/**
	 * Adds the following string to the {@link #bbcodeBuilder} (inclusive line
	 * feed). <br>
	 * <br>
	 * " <code>[b]attributeKey:[/b] attributeValue<br></code>"
	 */
	public static void addGenericAttribute(StringBuilder bbcodeBuilder,
			String attributeKey, String attributeValue) {
		String keyBBCode = new SingleBBCodeGenerator(attributeKey + ":")
				.addBBCode(BBCodes.BOLD).toString();
		bbcodeBuilder.append(keyBBCode).append(" ").append(attributeValue)
				.append(LINE_FEED);
	}
}
