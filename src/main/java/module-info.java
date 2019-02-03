module splittermond.data {
	exports org.prelle.rpgframework.splittermond.data;

	provides de.rpgframework.RulePlugin with org.prelle.rpgframework.splittermond.data.SplittermondDataPlugin;

	requires rpgframework.api;
	requires splittermond.core;
	requires org.apache.logging.log4j;
}