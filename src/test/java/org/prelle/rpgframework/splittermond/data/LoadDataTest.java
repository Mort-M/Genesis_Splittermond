/**
 * 
 */
package org.prelle.rpgframework.splittermond.data;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;

/**
 * @author prelle
 *
 */
public class LoadDataTest {

	//-------------------------------------------------------------------
	@Test
	public void loadDataTest() {
		PropertyConfigurator.configure(getClass().getResource("log4j.properties"));
		SplittermondDataPlugin plugin = new SplittermondDataPlugin();
		plugin.init();
	}

}
