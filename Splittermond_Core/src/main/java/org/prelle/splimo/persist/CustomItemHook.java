/**
 * 
 */
package org.prelle.splimo.persist;

import java.util.List;

import org.apache.log4j.Logger;
import org.prelle.simplepersist.AfterLoadHookInterface;
import org.prelle.splimo.SplittermondCustomDataCore;
import org.prelle.splimo.items.ItemTemplate;

/**
 * @author Stefan
 *
 */
public class CustomItemHook implements AfterLoadHookInterface {

	private final static Logger logger = Logger.getLogger("splittermond.persist");

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.AfterLoadHookInterface#afterLoad(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void afterLoad(Object data) {
		if (data instanceof ItemTemplate) {
			ItemTemplate item = (ItemTemplate)data;
			try {
				SplittermondCustomDataCore.addItem(item);
			} catch (Exception e) {
			}
			logger.debug("Added custom item "+item.getID()+" from character");
			throw new RuntimeException("Trace");
		} else if (data instanceof List) {
			List<ItemTemplate> list = (List<ItemTemplate>)data;
			for (ItemTemplate item : list) {
				try {
					SplittermondCustomDataCore.addItem(item);
				} catch (Exception e) {
				}
				logger.debug("Added custom item "+item.getID()+" from character");				
			}
		} else {
			logger.warn("Ignore "+data.getClass());
		}
			
	}

}
