package org.prelle.splimo;

import org.prelle.splimo.items.ItemTemplate;

import java.util.HashMap;
import java.util.Map;

public class SplitterDataMigration {

    private static Map<String, String> itemMapping = new HashMap<String, String>() {{
        put("kettensichel", "chainscythe");
        put("leathershield", "lederschild");
        put("morgenstern", "morningstar");
        put("yonnus_sickle", "yonnus-sichel");
        put("zeltProPerson", "zeltproperson");
    }};

    private static Map<String, String> mastershipMapping = new HashMap<String, String>() {{
        put("kettensichel", "chainscythe");
        put("yonnus_sickle", "yonnus-sichel");
        put("morgenstern", "morningstar");
    }};

    public static String getItemId(String v) {
        return itemMapping.get(v);
    }

    public static String getMastershipId(String v) {
        return mastershipMapping.get(v);
    }
}
