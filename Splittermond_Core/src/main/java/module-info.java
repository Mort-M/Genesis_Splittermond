/**
 * @author Stefan Prelle
 *
 */
module splittermond.core {
	exports org.prelle.rpgframework.splittermond;
	exports org.prelle.splimo.items;
	exports org.prelle.splimo.requirements;
	exports org.prelle.splimo.creature;
	exports org.prelle.splimo.gamemaster;
	exports org.prelle.splimo.commandbus;
	exports org.prelle.splimo.modifications;
	exports org.prelle.splimo.persist;
	exports org.prelle.splimo.processor;
	exports org.prelle.splimo;

	provides de.rpgframework.RulePlugin with org.prelle.rpgframework.splittermond.SplittermondRules;

	opens org.prelle.splimo to simple.persist;
	opens org.prelle.splimo.creature to simple.persist;
	opens org.prelle.splimo.items to simple.persist;
	opens org.prelle.splimo.modifications to simple.persist;
	opens org.prelle.splimo.requirements to simple.persist;

	requires activation;
	requires java.datatransfer;
	requires java.prefs;
	requires java.xml;
	requires java.mail;
	requires org.apache.logging.log4j;
	requires rpgframework.api;
	requires simple.persist;
}