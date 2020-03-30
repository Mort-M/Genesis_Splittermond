package de.rpgframework.splittermond.print.json;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;
import org.prelle.rpgframework.splittermond.data.SplittermondDataPlugin;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;

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
		
        SpliMoCharacter character = SplitterMondCore.load(new FileInputStream("src/test/resources/testdata/Amberion.xml"));
       
        JSONExportPlugin jsonPlugin = new JSONExportPlugin();
        
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
