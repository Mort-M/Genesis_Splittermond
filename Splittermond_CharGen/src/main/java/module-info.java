/**
 * 
 */
/**
 * @author prelle
 *
 */
module splittermond.chargen {
	exports org.prelle.splimo.chargen;
	exports org.prelle.splimo.npc;
	exports org.prelle.splimo.free;
	exports org.prelle.splimo.chargen.creature;
	exports org.prelle.splimo.charctrl4;
	exports org.prelle.splimo.chargen.event;
	exports org.prelle.splimo.charctrl;
	exports org.prelle.splimo.levelling;
	exports org.prelle.splimo.equip;
	exports org.prelle.splittermond.genlvl;

	requires transitive rpgframework.api;
	requires transitive splittermond.core;
	requires java.xml;
	requires org.apache.logging.log4j;
	requires simple.persist;
	requires splittermond.data;
}