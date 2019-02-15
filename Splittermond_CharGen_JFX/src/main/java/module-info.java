/**
 * @author Stefan Prelle
 *
 */
module splittermond.chargen.jfx {
	exports org.prelle.splimo.chargen.jfx;
	exports org.prelle.splittermond.jfx.cultures;
	exports org.prelle.splimo.chargen.lvl.jfx;
	exports org.prelle.splittermond.jfx.spells;
	exports org.prelle.splittermond.jfx.master;
	exports org.prelle.splittermond.jfx.notes;
	exports org.prelle.splittermond.jfx.powers;
	exports org.prelle.splittermond.jfx.resources;
	exports org.prelle.splittermond.jfx.languages;
	exports org.prelle.splittermond.jfx.skills;
	exports org.prelle.splittermond.jfx.attributes;
	exports org.prelle.splittermond.jfx.creatures;
	exports org.prelle.splittermond.jfx.equip;
	exports org.prelle.splittermond.jfx.equip.input;
	exports org.prelle.splimo.chargen.gen.jfx;
	exports org.prelle.splimo.chargen.common.jfx;
	exports org.prelle.splimo.chargen.fluent;
	exports org.prelle.splimo.chargen.free.jfx;

	provides de.rpgframework.RulePlugin with org.prelle.splimo.chargen.jfx.GeneratorRulePlugin;

	requires java.prefs;
	requires javafx.base;
	requires javafx.controls;
	requires javafx.extensions;
	requires javafx.graphics;
	requires org.apache.logging.log4j;
	requires rpgframework.api;
	requires rpgframework.api.jfx;
	requires rpgframework.jfx;
	requires simple.persist;
	requires splittermond.chargen;
	requires splittermond.core;
}