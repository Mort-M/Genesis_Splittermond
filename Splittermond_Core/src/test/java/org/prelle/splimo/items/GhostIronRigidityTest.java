package org.prelle.splimo.items;

import static org.junit.Assert.assertEquals;
import java.lang.reflect.Field;
import org.junit.Test;
import org.prelle.splimo.modifications.ItemModification;

public class GhostIronRigidityTest {
    private Material material(String id) throws Exception {
        Material result = new Material();
        Field field = Material.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(result, id);
        return result;
    }

    private CarriedItem item(int rigidity, String materialId) throws Exception {
        ItemTemplate template = new ItemTemplate();
        template.setRigidity(rigidity);
        CarriedItem result = new CarriedItem(template);
        if (materialId != null) result.setMaterial(material(materialId));
        return result;
    }

    @Test public void reductionHasFloorAndDoesNotMutateTemplateOrAccumulate() throws Exception {
        for (int base = 0; base <= 8; base++) {
            CarriedItem item = item(base, "ghostiron");
            assertEquals(Math.max(1, base - 2), item.getRigidity());
            assertEquals(Math.max(1, base - 2), item.getRigidity());
            assertEquals(base, item.getItem().getRigidity());
        }
    }

    @Test public void otherRigidityModifiersAreIncluded() throws Exception {
        CarriedItem item = item(4, "ghostiron");
        item.addItemModification(new ItemModification(ItemAttribute.RIGIDITY, 3));
        assertEquals(5, item.getRigidity());
        item.addItemModification(new ItemModification(ItemAttribute.RIGIDITY, -8));
        assertEquals(1, item.getRigidity());
    }

    @Test public void switchingMaterialRestoresOrdinaryRigidity() throws Exception {
        CarriedItem item = item(5, "ghostiron");
        assertEquals(3, item.getRigidity());
        item.setMaterial(null);
        assertEquals(5, item.getRigidity());
        item.setMaterial(material("ordinary-test-material"));
        assertEquals(5, item.getRigidity());
        item.setMaterial(material("ghostiron"));
        assertEquals(3, item.getRigidity());
    }

    @Test public void ordinaryItemsKeepTheirExistingValuesAndModifiers() throws Exception {
        CarriedItem item = item(0, null);
        assertEquals(0, item.getRigidity());
        item.setMaterial(material("ordinary-test-material"));
        item.addItemModification(new ItemModification(ItemAttribute.RIGIDITY, -1));
        assertEquals(-1, item.getRigidity());
    }
}
