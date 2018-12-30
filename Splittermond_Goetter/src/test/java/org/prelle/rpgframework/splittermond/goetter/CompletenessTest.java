package org.prelle.rpgframework.splittermond.goetter;
import static org.junit.Assert.*;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.junit.Test;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;

public class CompletenessTest {

  private static final String BASE_DIR = "src/main/resources/data/splittermond/";

  @Test
  public void testAspectCompleteness() throws Exception{

    List<String> deityTypes = getAspectsByField("deitytypes-goetter.xml","ref");
    List<String> aspects = getAspectsByField("aspects-goetter.xml","id");
    List<String> deities = getAspectsByField("deities-goetter.xml","ref");

    compareLists(aspects, deityTypes,"Aspekte ohne Familie:");
    compareLists(deityTypes, aspects, "AspektFamilien ohne Aspekt:");
    compareLists(aspects, deities,"Aspekte ohne Gott:");
    compareLists(deities, aspects, "Gott ohne Aspekte:");
    Properties props = new Properties();
    FileInputStream input = new FileInputStream(new File("src/main/resources/i18n/splittermond/goetter.properties"));
    props.load(new InputStreamReader(input, Charset.forName("ISO-8859-1")));
    for (String deity:deities) {
      if (!props.contains("aspect." + deity)) {
        System.out.println("aspect." + deity);
      }
      // assertTrue(props.contains("aspect." + deity.trim()));
    }
  }

  private static void compareLists(List<String> list1, List<String> list2, String desc) {
    List<String> missing = new ArrayList<>();
    for (String aspect: list1) {
      if (!list2.contains(aspect)) {
        missing.add(aspect);
      }
    }
    if (missing.size() > 0) {
      System.out.println(desc);
      for (String result:missing) {
        System.out.println(result);
      }
      System.out.println(missing.size());
    }

    assertEquals(0, missing.size());
  }

  private static List<String> getAspectsByField(String file, String field) throws Exception {
    File inputFile = new File(BASE_DIR + file);
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document doc = dBuilder.parse(inputFile);
    doc.getDocumentElement().normalize();
    NodeList nList = doc.getElementsByTagName("aspect");
    List<String> aspects = new ArrayList<>();
    for (int temp = 0; temp < nList.getLength(); temp++) {
      Element eElement =  (Element) nList.item(temp);
      aspects.add(eElement.getAttribute(field));
    }
    return aspects;
  }
}