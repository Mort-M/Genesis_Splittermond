package de.rpgframework.splittermond.print.bbcode.adder;

import java.security.InvalidParameterException;

import org.apache.log4j.Logger;
import org.prelle.splimo.SpliMoCharacter;

/**
 * Skeleton implementation of a class, which adds specific category values to a
 * given {@link StringBuilder}.
 *
 * @author frank.buettner
 *
 */
public abstract class AbstractAdder {
	protected final StringBuilder bbcodeBuilder;
	protected final SpliMoCharacter spliMoCharacter;
	protected static Logger logger = Logger.getLogger(AbstractAdder.class);
	private static final String DELIMITER_STRING = " - ";

	/**
	 * @param bbcodeBuilder
	 *            the {@link StringBuilder}, to which everything gets appended
	 * @param spliMoCharacter
	 *            the {@link SpliMoCharacter}, which data get's encoded in
	 *            BBCode
	 */
	public AbstractAdder(StringBuilder bbcodeBuilder,
			SpliMoCharacter spliMoCharacter) {
		if (bbcodeBuilder == null) {
			String msg = "bbcodeBuilder is null";
			logger.error(msg);
			throw new InvalidParameterException(msg);
		}

		if (spliMoCharacter == null) {
			String msg = "spliMoCharacter is null";
			logger.error(msg);
			throw new InvalidParameterException(msg);
		}
		this.bbcodeBuilder = bbcodeBuilder;
		this.spliMoCharacter = spliMoCharacter;
	}

	/**
	 * Adds information of a {@link SpliMoCharacter} to the given
	 * {@link StringBuilder}. The type of information which is added depends on
	 * the implementation class.
	 */
	public abstract void add();

	/**
	 * If the builder ends with a {@link #DELIMITER_STRING}, it's removed.
	 *
	 * @see #addDelimiter(StringBuilder)
	 */
	public static void deleteLastDelimiter(StringBuilder builder) {
		// delete last " - "
		if (builder.length()<3)
			return;
		String delemiterSubstring = builder.substring(builder.length() - 3,
				builder.length());
		if (DELIMITER_STRING.equals(delemiterSubstring)) {
			builder.delete(builder.length() - 3, builder.length() - 1);
		} else {
			logger.trace(String.format(
					"builder does not end with the delimiter <%s>: %s",
					DELIMITER_STRING, builder.toString()));
		}
	}

	/**
	 * Adds the {@link #DELIMITER_STRING} to the {@link StringBuilder}.
	 */
	public static void addDelimiter(StringBuilder builder) {
		builder.append(DELIMITER_STRING);
	}
}
