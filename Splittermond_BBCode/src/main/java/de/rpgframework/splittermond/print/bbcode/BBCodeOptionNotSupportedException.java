package de.rpgframework.splittermond.print.bbcode;

/**
 * This Exception is thrown if an option is given for a {@link BBCodes} and this
 * {@link BBCodes} doesn't support options.
 * 
 * @author frank.buettner
 * 
 */
public class BBCodeOptionNotSupportedException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8141061758178319112L;
	
	private String usedOption;
	private BBCodes usedBBCode;

	public BBCodeOptionNotSupportedException(BBCodes usedBBCode, String usedOption) {
		this.usedBBCode = usedBBCode;
		this.usedOption = usedOption;
	}
	
	@Override
	public String toString() {
		return String.format("User BBCode: %s. Used option: %s", usedBBCode, usedOption);
	}
}
