module splittermond.core {
	exports org.prelle.splimo;
	exports org.prelle.splimo.items;
	exports org.prelle.rpgframework.splittermond;
	exports org.prelle.splimo.requirements;
	exports org.prelle.splimo.creature;
	exports org.prelle.splimo.gamemaster;
	exports org.prelle.splimo.commandbus;
	exports org.prelle.splimo.modifications;
	exports org.prelle.splimo.persist;
	exports org.prelle.splimo.processor;

	provides de.rpgframework.RulePlugin with org.prelle.rpgframework.splittermond.SplittermondRules;
	
	requires transitive rpgframework.api;
	requires activation;
	requires java.datatransfer;
	requires java.prefs;
	requires java.xml;
	requires java.mail;
	requires org.apache.logging.log4j;
	requires simple.persist;
}