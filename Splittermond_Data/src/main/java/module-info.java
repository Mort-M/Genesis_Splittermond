/**
 * @author Stefan Prelle
 *
 */
module splittermond.data {
	exports org.prelle.rpgframework.splittermond.data;
	opens org.prelle.rpgframework.splittermond.data;

	provides de.rpgframework.character.RulePlugin with org.prelle.rpgframework.splittermond.data.SplittermondDataPlugin;

	requires org.apache.logging.log4j;
	requires transitive de.rpgframework.core;
	requires transitive de.rpgframework.chars;
	requires splittermond.core;
	
}