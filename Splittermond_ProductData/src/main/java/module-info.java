module rpgframework.products.splittermond {
	exports de.rpgframework.products.splittermond;
	opens de.rpgframework.products.splittermond;

	provides de.rpgframework.products.ProductDataPlugin with de.rpgframework.products.splittermond.ProductDataSplittermond;

	requires de.rpgframework.core;
	requires de.rpgframework.products;
	requires org.apache.logging.log4j;
	
}