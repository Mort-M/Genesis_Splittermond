package org.prelle.splimo;

/**
 * Created by anja on 22.11.2015.
 */
public enum SpellEnhancementType {

    RELEASETIME,
    SPELLDURATION,
    CONSUMED_FOCUS,
    EXHAUSTED_FOCUS,
    CHANNELIZED_FOCUS,
    RANGE,
    EFFECT_RANGE,
    DAMAGE
    ;


    public String getName() {
        return SplitterMondCore.getI18nResources().getString("spell.enhancementtype."+name().toLowerCase());
    }

    public String toString() {
        return getName();
    }

}
