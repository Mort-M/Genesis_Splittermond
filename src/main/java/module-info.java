/**
 * @author Stefan Prelle
 *
 */
module splittermond.data {
	exports org.prelle.rpgframework.splittermond.data;
	opens org.prelle.rpgframework.splittermond.data;
	// All packages that export education images
	opens org.prelle.rpgframework.splittermond.data.beastmaster.data;
	opens org.prelle.rpgframework.splittermond.data.core.data;
	opens org.prelle.rpgframework.splittermond.data.farukan.data;
	opens org.prelle.rpgframework.splittermond.data.goetter.data;
	opens org.prelle.rpgframework.splittermond.data.magie.data;
	opens org.prelle.rpgframework.splittermond.data.selenia.data;
	opens org.prelle.rpgframework.splittermond.data.ungebrochen.data;
	opens org.prelle.rpgframework.splittermond.data.unreich.data;
	opens org.prelle.rpgframework.splittermond.data.zhoujiang.data;
	opens org.prelle.rpgframework.splittermond.data.suderinseln.data;
	opens org.prelle.rpgframework.splittermond.data.kesh.data;
	opens org.prelle.rpgframework.splittermond.data.mahaluu.data;
	opens org.prelle.rpgframework.splittermond.data.badashan.data;

	provides de.rpgframework.RulePlugin with org.prelle.rpgframework.splittermond.data.SplittermondDataPlugin;

	requires org.apache.logging.log4j;
	requires rpgframework.api;
	requires splittermond.core;
}