/**
 * 
 */
package org.prelle.splimo.chargen.event;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

/**
 * @author prelle
 *
 */
public class GenerationEventDispatcher {
	
	private static Logger logger = Logger.getLogger("splittermond.chargen.event");
	
	private static Collection<GenerationEventListener> listener;

	//--------------------------------------------------------------------
	static {
		listener = new ArrayList<GenerationEventListener>();
	}

	//--------------------------------------------------------------------
	public static void addListener(GenerationEventListener callback) {
		if (!listener.contains(callback)) {
			listener.add(callback);
			logger.debug("Add to dispatcher     : "+callback.getClass());
		}
	}

	//--------------------------------------------------------------------
	public static void removeListener(GenerationEventListener callback) {
		if (listener.remove(callback))
			logger.debug("Remove from dispatcher: "+callback.getClass());
	}

	//--------------------------------------------------------------------
	public static void clear() {
		listener.clear();
	}

	//--------------------------------------------------------------------
	public static void fireEvent(GenerationEvent event) {
		logger.info("fire "+event.getType());
		for (GenerationEventListener callback : listener) {
			try {
				callback.handleGenerationEvent(event);
			} catch (Exception e) {
				logger.error("Error delivering generation event",e);
			}
		}
	}

}
