/**
 * @author Stefan Prelle
 *
 */
module splittermond.bbcode {
	exports de.rpgframework.splittermond.print.bbcode;
	exports de.rpgframework.splittermond.print;
	exports de.rpgframework.splittermond.print.bbcode.adder;

	requires java.prefs;
	requires org.apache.logging.log4j;
	requires de.rpgframework.core;
	requires splittermond.core;
	requires de.rpgframework.chars;
}