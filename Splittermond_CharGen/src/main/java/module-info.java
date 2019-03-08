/**
 * @author Stefan Prelle
 *
 */
module splittermond.chargen {
	exports org.prelle.splimo.chargen;
	exports org.prelle.splimo.npc;
	exports org.prelle.splimo.chargen.creature;
	exports org.prelle.splimo.levelling;
	exports org.prelle.splimo.free;
	exports org.prelle.splimo.charctrl4;
	exports org.prelle.splimo.equip;
	exports org.prelle.splittermond.genlvl;
	exports org.prelle.splimo.chargen.event;
	exports org.prelle.splimo.charctrl;

	requires java.xml;
	requires org.apache.logging.log4j;
	requires transitive rpgframework.api;
	requires simple.persist;
	requires transitive splittermond.core;
	requires splittermond.data;
}