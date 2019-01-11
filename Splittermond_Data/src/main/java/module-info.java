/**
 * 
 */
/**
 * @author prelle
 *
 */
module splittermond.data {
	exports org.prelle.rpgframework.splittermond.data;
	
	provides de.rpgframework.RulePlugin with org.prelle.rpgframework.splittermond.data.SplittermondDataPlugin;

	requires org.apache.logging.log4j;
	requires rpgframework.api;
	requires splittermond.core;
}