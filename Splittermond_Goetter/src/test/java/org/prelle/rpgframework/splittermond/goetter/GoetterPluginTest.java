package org.prelle.rpgframework.splittermond.goetter;
import static org.junit.Assert.*;

import java.util.List;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;
import org.prelle.rpgframework.splittermond.SplittermondRules;
import org.prelle.rpgframework.splittermond.msk.MondstahlklingenPlugin;
import org.prelle.rpgframework.splittermond.selenia.SeleniaPlugin;
import org.prelle.rpgframework.splittermond.unreich.UnreichPlugin;
import org.prelle.rpgframework.splittermond.world.WorldPlugin;
import org.prelle.splimo.Culture;
import org.prelle.splimo.Deity;
import org.prelle.splimo.SplitterMondCore;

public class GoetterPluginTest {

  private static Logger logger = Logger.getLogger("splittermond.goetter");

  //-------------------------------------------------------------------
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    PropertyConfigurator.configure(ClassLoader.getSystemResourceAsStream("log4j.properties"));
    SplitterMondCore.initialize(new SplittermondRules());

  }
  @Test
  public void testCultures() {
    (new WorldPlugin()).init();
    (new MondstahlklingenPlugin()).init();
    (new SeleniaPlugin()).init();
    (new UnreichPlugin()).init();
    (new GoetterPlugin()).init();
    for (Deity deity: SplitterMondCore.getDeities()) {
      for (Culture culture: deity.getFavoredCultures()) {
        logger.info(deity.getName() + ":" + culture.getName());
      }
    }

    List<Deity> deities = SplitterMondCore.getDeitiesForCulture(SplitterMondCore.getCulture("selenia"));
    assertTrue(deities.contains(SplitterMondCore.getDeity("caran")));
    assertTrue(deities.contains(SplitterMondCore.getDeity("yonnus")));
    assertFalse(deities.contains(SplitterMondCore.getDeity("darunwal")));
  }
}