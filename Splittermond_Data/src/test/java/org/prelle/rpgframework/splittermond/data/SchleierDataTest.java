package org.prelle.rpgframework.splittermond.data;

import static org.junit.Assert.*;

import java.util.*;
import java.io.*;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.prelle.simplepersist.Persister;
import org.prelle.splimo.*;
import org.prelle.splimo.items.*;
import org.prelle.splimo.modifications.*;
import de.rpgframework.genericrpg.modification.Modification;

/** Runs against both the original API and the private integration build. */
public class SchleierDataTest {
    private static final String ROOT = "/org/prelle/rpgframework/splittermond/data/";
    private static final String[] EDUCATIONS = {"spiritist", "mistcrow", "whispershadow",
        "ancestorcarrier", "zhoujiang_exorcist", "obsidianseeker", "otherworld_emissary"};

    @BeforeClass public static void load() {
        new SplittermondDataPlugin().init(percent -> {});
    }

    private Document xml(String path) throws Exception {
        try (InputStream in = getClass().getResourceAsStream(ROOT + path)) {
            assertNotNull(path, in);
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
        }
    }

    private Element education(String id) throws Exception {
        String path = "spiritbanisher".equals(id) ? "zhoujiang/data/educations-zhoujiang.xml"
                : "schleier/data/educations-schleier.xml";
        NodeList nodes = xml(path).getElementsByTagName("education");
        for (int i=0; i<nodes.getLength(); i++) {
            Element e = (Element)nodes.item(i);
            if (id.equals(e.getAttribute("id"))) return e;
        }
        throw new AssertionError(id);
    }

    private List<Element> children(Element parent) {
        List<Element> result = new ArrayList<>();
        for (Node n=parent.getFirstChild(); n!=null; n=n.getNextSibling())
            if (n instanceof Element) result.add((Element)n);
        return result;
    }

    private int sum(Element e, String kind) {
        if (kind.equals(e.getTagName()))
            return e.hasAttribute("value") ? Integer.parseInt(e.getAttribute("value")) : 1;
        List<Element> nodes = children(e);
        if ("selmod".equals(e.getTagName()) && !nodes.isEmpty()) {
            boolean relevant = nodes.stream().anyMatch(n -> sum(n, kind)>0);
            if (!relevant) return 0;
            if (e.hasAttribute("dist")) return Integer.parseInt(e.getAttribute("dist"));
            if (e.hasAttribute("values"))
                return Arrays.stream(e.getAttribute("values").split(" ")).mapToInt(Integer::parseInt).sum();
            Set<Integer> totals = new HashSet<>();
            for (Element n : nodes) totals.add(sum(n, kind));
            assertEquals("Unequal alternatives: " + e.getTextContent(), 1, totals.size());
            return totals.iterator().next() * (e.hasAttribute("num") ? Integer.parseInt(e.getAttribute("num")) : 1);
        }
        return nodes.stream().mapToInt(n -> sum(n, kind)).sum();
    }

    @Test public void allEducationsLoadWithCorrectParentsAndBook() {
        String[] parents = {null,"spiritist","spiritist","spiritist","mysticwarrior","lorepriest","summoner"};
        for (int i=0; i<EDUCATIONS.length; i++) {
            Education e = SplitterMondCore.getEducation(EDUCATIONS[i]);
            assertNotNull(EDUCATIONS[i], e);
            assertEquals(parents[i], e.getVariantOf());
            assertEquals("Hinter dem Schleier", e.getProductName());
            assertFalse(e.getName().startsWith("education."));
            if (parents[i]!=null)
                assertTrue(SplitterMondCore.getEducation(parents[i]).getVariants().contains(e));
        }
    }

    @Test public void everyEducationHasThirtySkillPointsAndTwoResourcePoints() throws Exception {
        for (String id : EDUCATIONS) {
            assertEquals(id, 30, sum(education(id), "skillmod"));
            assertEquals(id, 2, sum(education(id), "resourcemod"));
            assertEquals(id, 2, sum(education(id), "mastermod"));
        }
    }

    @Test public void allFixedReferencesResolve() throws Exception {
        for (String id : EDUCATIONS) {
            NodeList nodes = education(id).getElementsByTagName("*");
            for (int i=0; i<nodes.getLength(); i++) {
                Element e = (Element)nodes.item(i);
                String ref = e.getAttribute("ref");
                switch (e.getTagName()) {
                case "skillmod": assertNotNull(ref, SplitterMondCore.getSkill(ref)); break;
                case "powermod": assertNotNull(ref, SplitterMondCore.getPower(ref)); break;
                case "resourcemod": assertNotNull(ref, SplitterMondCore.getResource(ref)); break;
                case "mastermod":
                    if (!ref.isEmpty()) {
                        String[] parts=ref.split("/");
                        assertNotNull(ref, SplitterMondCore.getSkill(parts[0]).getMastership(parts[1]));
                    }
                    break;
                case "reqmod":
                    for (String culture : ref.split(",")) assertNotNull(culture, SplitterMondCore.getCulture(culture));
                    break;
                default: break;
                }
            }
        }
    }

    @Test public void removedChoicesAndMandatorySkillsAreCorrect() throws Exception {
        assertFalse(refs(education("mistcrow"), "skillmod").contains("countrylore"));
        assertFalse(refs(education("mistcrow"), "skillmod").contains("streetlore"));
        for (String id : new String[]{"whispershadow", "ancestorcarrier"})
            for (String skill : new String[]{"melee","slashing","blades","staffs","chains","longrange","throwing"})
                assertFalse(id, refs(education(id), "skillmod").contains(skill));
        assertFalse(refs(education("whispershadow"), "skillmod").contains("antimagic"));
        assertFalse(refs(education("whispershadow"), "skillmod").contains("protectionmagic"));
        assertFalse(refs(education("zhoujiang_exorcist"), "skillmod").contains("combatmagic"));
        assertFalse(refs(education("ancestorcarrier"), "skillmod").contains("streetlore"));
        assertTrue(refs(education("ancestorcarrier"), "resourcemod").contains("reputation"));
    }

    private Set<String> refs(Element parent, String kind) {
        Set<String> result = new HashSet<>();
        NodeList nodes=parent.getElementsByTagName(kind);
        for (int i=0;i<nodes.getLength();i++) result.add(((Element)nodes.item(i)).getAttribute("ref"));
        return result;
    }

    @Test public void spiritbanisherErratumUsesInheritedPowersAndCalligraphy() throws Exception {
        Element e=education("spiritbanisher");
        assertEquals(new HashSet<>(Arrays.asList("literate","sensesocial")), refs(e,"powermod"));
        assertEquals(30, sum(e,"skillmod"));
        assertEquals(2, sum(e,"resourcemod"));
        boolean calligraphy=false;
        for (Modification mod : SplitterMondCore.getEducation("spiritbanisher").getModifications()) {
            if (mod instanceof MastershipModification) {
                MastershipModification m=(MastershipModification)mod;
                if (m.getMastership()!=null && "clscraft".equals(m.getSkill().getId())) {
                    calligraphy=true;
                    assertEquals("journeyman_calligraphy",m.getMastership().getId());
                    assertEquals(1,m.getMastership().getLevel());
                }
            }
        }
        assertTrue(calligraphy);
    }

    @Test public void situationalEffectsDoNotBecomePermanentBonuses() {
        assertTrue(SplitterMondCore.getSkill("deathmagic").getMastership("medium1").getModifications().isEmpty());
        Material iron=SplitterMondCore.getMaterial("ghostiron");
        assertNotNull(iron);
        assertEquals(4,iron.getQuality());
        assertEquals(1000,iron.getPrice());
        assertTrue("No unbounded rigidity reduction",iron.getModifications().isEmpty());
    }

    @Test public void existingMaterialsAreUniqueAndRetainTheirBonuses() {
        for (String id : new String[]{"ghostwood","whisperwood","ghostsilk","weisser_rabe_federn","sirene_federn"})
            assertEquals(id,1,SplitterMondCore.getMaterials().stream().filter(m -> id.equals(m.getId())).count());
        assertMaterialSkill("ghostwood","deathmagic",2);
        assertMaterialSkill("whisperwood","deathmagic",1);
        assertMaterialSkill("whisperwood","naturemagic",1);
        assertMaterialSkill("maidh_bewerdu_feathers","deathmagic",2);
        assertEquals(-1,SplitterMondCore.getMaterial("maidh_bewerdu_feathers").getPrice());
        assertTrue(SplitterMondCore.getMaterial("ghostwood").getEffectText().contains("Geisterholzkiste"));
    }

    private void assertMaterialSkill(String id,String skill,int value) {
        int found=0;
        for (Modification mod : SplitterMondCore.getMaterial(id).getModifications())
            if (mod instanceof SkillModification && skill.equals(((SkillModification)mod).getSkill().getId())) {
                found++; assertEquals(value,((SkillModification)mod).getValue());
            }
        assertEquals(id,1,found);
    }

    @Test public void equipmentPricesAndPhysicalValuesMatch() {
        String[] ids={"akherat_glass","akherat_glass_improved","prayer_wheel","ghost_lights"};
        int[] prices={15000,21000,300,500};
        int[] rigidity={0,0,1,0};
        for (int i=0;i<ids.length;i++) {
            ItemTemplate item=SplitterMondCore.getItem(ids[i]);
            assertNotNull(ids[i],item);
            assertEquals(prices[i],item.getPrice());
            assertEquals(1,item.getLoad());
            assertEquals(rigidity[i],item.getRigidity());
            assertEquals("Schleier",item.getPlugin().getID());
        }
    }

    @Test public void newItemAndMaterialReferencesSurviveSaving() throws Exception {
        Persister serializer=new Persister();
        for (String id : new String[]{"akherat_glass","akherat_glass_improved","prayer_wheel","ghost_lights"}) {
            CarriedItem item=new CarriedItem(SplitterMondCore.getItem(id));
            StringWriter out=new StringWriter();
            serializer.write(item,out);
            CarriedItem restored=serializer.read(CarriedItem.class,new StringReader(out.toString()));
            assertSame(item.getItem(),restored.getItem());
        }
        CarriedItem item=new CarriedItem(SplitterMondCore.getItem("dagger"));
        item.setMaterial(SplitterMondCore.getMaterial("ghostiron"));
        StringWriter out=new StringWriter();
        serializer.write(item,out);
        CarriedItem restored=serializer.read(CarriedItem.class,new StringReader(out.toString()));
        assertSame(item.getMaterial(),restored.getMaterial());
    }

    @Test public void educationDefinitionsRetainChoicesAndReferencesWhenSaved() throws Exception {
        Persister serializer=new Persister();
        for (String id : EDUCATIONS) {
            Education original=SplitterMondCore.getEducation(id);
            StringWriter out=new StringWriter();
            serializer.write(original,out);
            Education restored=serializer.read(Education.class,new StringReader(out.toString()));
            assertEquals(id,restored.getId());
            assertEquals(original.getVariantOf(),restored.getVariantOf());
            assertEquals(original.getModifications().size(),restored.getModifications().size());
            StringWriter again=new StringWriter();
            serializer.write(restored,again);
            assertEquals("Persistent modifications changed: "+id,out.toString(),again.toString());
        }
    }
}
