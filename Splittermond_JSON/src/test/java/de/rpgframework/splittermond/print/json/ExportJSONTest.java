package de.rpgframework.splittermond.print.json;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.prefs.Preferences;

import org.junit.Test;
import org.prelle.rpgframework.splittermond.data.SplittermondDataPlugin;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;

import de.rpgframework.ConfigContainer;
import de.rpgframework.core.CommandResult;
import de.rpgframework.core.CommandType;
import de.rpgframework.print.PrintType;

/**
 * @author prelle
 *
 */
public class ExportJSONTest {

	//-------------------------------------------------------------------
	@Test
	public void loadDataTest() throws IOException {
		SplittermondDataPlugin plugin = new SplittermondDataPlugin();
		plugin.init( (percent) -> {});
		ConfigContainer parent = new ConfigContainerImpl(Preferences.userRoot(), "foo");
		parent.createContainer("splittermond");
		
		
        SpliMoCharacter character = SplitterMondCore.load(new FileInputStream("src/test/resources/testdata/Amberion.xml"));
        assertNotNull(character);
        System.out.println("Converting "+character.getName());
       
        JSONExportPlugin jsonPlugin = new JSONExportPlugin();
        jsonPlugin.attachConfigurationTree(parent);
        // Configure to convert to original character
//        jsonPlugin.OPTION_EXPORT_RESOLVED.set(false);
       
        CommandResult result = jsonPlugin.handleCommand(this, CommandType.PRINT, 
        		null,
        		character,
        		null, // Scene
        		null, // ScreenManager
        		PrintType.BBCODE
        		);
        
       assertNotNull(result);
       assertTrue(result.wasSuccessful());
       assertNotNull(result.getReturnValue());
       System.out.println(result.getReturnValue());
	}

}
