package org.prelle.splimo.items;

import org.prelle.splimo.SplitterMondCore;

/**
 * Created by rupp on 28.09.2014.
 */
public enum ItemLocationType {
    BODY,
    CONTAINER,
    BEASTOFBURDEN,
    SOMEWHEREELSE;

    public String getName() {
        return SplitterMondCore.getI18nResources().getString("itemlocationtype."+name().toLowerCase());
    }
}
