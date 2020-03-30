/**
 * @author Stefan Prelle
 *
 */
module splittermond.json {
	exports de.rpgframework.splittermond.print.json;

	provides de.rpgframework.character.RulePlugin with de.rpgframework.splittermond.print.json.JSONExportPlugin;

	requires java.prefs;
	requires org.apache.logging.log4j;
	requires de.rpgframework.core;
	requires transitive splittermond.core;
	requires de.rpgframework.chars;
	
}