/**
 * @author Stefan Prelle
 *
 */
module splittermond.chargen.jfx {
	exports org.prelle.splittermond.chargen.jfx;
//	exports org.prelle.splittermond.jfx.cultures;
//	exports org.prelle.splittermond.chargen.lvl.jfx;
//	exports org.prelle.splittermond.jfx.spells;
//	exports org.prelle.splittermond.jfx.master;
//	exports org.prelle.splittermond.jfx.notes;
//	exports org.prelle.splittermond.jfx.powers;
//	exports org.prelle.splittermond.jfx.resources;
//	exports org.prelle.splittermond.jfx.languages;
//	exports org.prelle.splittermond.jfx.skills;
//	exports org.prelle.splittermond.jfx.attributes;
//	exports org.prelle.splittermond.jfx.creatures;
//	exports org.prelle.splittermond.jfx.equip;
//	exports org.prelle.splittermond.jfx.equip.input;
//	exports org.prelle.splittermond.chargen.gen.jfx;
//	exports org.prelle.splittermond.chargen.common.jfx;
//	exports org.prelle.splittermond.chargen.fluent;
//	exports org.prelle.splittermond.chargen.free.jfx;

	provides de.rpgframework.character.RulePlugin with org.prelle.splittermond.chargen.jfx.GeneratorRulePlugin;

	requires java.prefs;
	requires javafx.base;
	requires javafx.controls;
	requires javafx.extensions;
	requires javafx.graphics;
	requires org.apache.logging.log4j;
	requires transitive de.rpgframework.core;
	requires transitive de.rpgframework.chars;
	requires transitive de.rpgframework.javafx;
	requires simple.persist;
	requires transitive splittermond.chargen;
	requires transitive splittermond.core;
}