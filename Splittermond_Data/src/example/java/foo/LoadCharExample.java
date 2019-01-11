/**
 * 
 */
package foo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Enumeration;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.prelle.splimo.DummyRulePlugin;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;

/**
 * @author prelle
 *
 */
public class LoadCharExample {

	//-------------------------------------------------------------------
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		Enumeration<Appender> e = Logger.getRootLogger().getAllAppenders();
		while (e.hasMoreElements()) {
			Appender ap = e.nextElement();
			ap.setLayout(new PatternLayout("%5p [%c] (%F:%L) - %m%n"));
		}
		Logger.getRootLogger().setLevel(Level.DEBUG);
		Logger.getLogger("xml").setLevel(Level.WARN);

		SplitterMondCore.initialize(new DummyRulePlugin<>());

        File path = (new File("testdata/Lenkan.xml"));
        System.out.println("path = " + path.getAbsolutePath());
        SpliMoCharacter character = SplitterMondCore.load(path.toPath());
        File imgPath = (new File("testdata/Tiai.img"));
        System.out.println("image = " + imgPath.getAbsolutePath());
        character.setImage(Files.readAllBytes(imgPath.toPath()));
        
        System.out.println("Geladen: "+character.dump());

        for (SkillValue skillVal : character.getSkills(SkillType.MAGIC)) {        	
        	System.out.println(String.format(
        			"In der Fertigkeit %s hat %s den Wert von %d",
        			skillVal.getSkill().getName(),
        			character.getName(),
        			skillVal.getValue()
        			));
        }
        
	}

}
