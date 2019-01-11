/**
 * 
 */
package org.prelle.splimo;

import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.TreeSet;
import java.util.Vector;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author prelle
 *
 */
public class WriteablePropertyResourceBundle extends ResourceBundle {

	private static Logger logger = LogManager.getLogger("splittermond");
	
	private Properties pro;
	private String name;

	//-------------------------------------------------------------------
	@SuppressWarnings("serial")
	public WriteablePropertyResourceBundle() {
		pro = new Properties() {
		    @Override
		    public synchronized Enumeration<Object> keys() {
		        return Collections.enumeration(new TreeSet<Object>(super.keySet()));
		    }
		};;
	}

	//-------------------------------------------------------------------
	public WriteablePropertyResourceBundle(String name, FileReader fileReader) {
		this();
		this.name = name;
		Properties input = new Properties();
		try {
			input.load(fileReader);
			for (Object key : input.keySet()) {
				pro.put((String)key, input.getProperty((String)key));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see java.util.ResourceBundle#getBaseBundleName()
	 */
	@Override
    public String getBaseBundleName() {
        return name;
    }

	//-------------------------------------------------------------------
	public String dump() {
		return pro.toString();
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.util.ResourceBundle#handleGetObject(java.lang.String)
	 */
	@Override
	protected Object handleGetObject(String key) {
		return pro.get(key);
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.util.ResourceBundle#getKeys()
	 */
	@Override
	public Enumeration<String> getKeys() {
		Vector<String> v = new Vector<String>();
		for (Object o : pro.keySet()) {
			v.add((String)o);
		}
		return v.elements();
	}

	//-------------------------------------------------------------------
	public void set(String key, Object value) {
		pro.put(key, value);
		logger.debug(getClass().getSimpleName()+".set "+key+"="+value+" to "+pro);
	}

	//-------------------------------------------------------------------
	public void remove(String key) {
		pro.remove(key);
	}

	//-------------------------------------------------------------------
	public void removePrefix(final String key) {
		final List<String> toDelete = new ArrayList<>();
		pro.keySet().forEach(new Consumer<Object>() {
			public void accept(Object t) {
				if (t.toString().startsWith(key)) {
					toDelete.add(t.toString());
				}
			}
		});
		
		for (String t : toDelete) {
			logger.debug(getClass().getSimpleName()+".remove "+t);
			pro.remove(t);
		}
	}

	//-------------------------------------------------------------------
	public void write(Writer out, String comments) throws IOException {
		logger.debug(getClass().getSimpleName()+".write "+pro.size()+" keys");
		pro.store(out, comments);
	}

}
