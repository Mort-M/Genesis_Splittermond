package de.rpgframework.splittermond.print.json.model;

import de.rpgframework.core.RoleplayingSystem;

import java.util.List;

public class JSONCharacter {

    public String jsonExporterVersion = "1.4.0";
    public String system = RoleplayingSystem.SPLITTERMOND.name();

    public String name;
    public String race;
    public String culture;
    public String background;
    public String birthplace;
    public String education;
    public List<String> cultureLores;
    public List<String> languages;
    public JSONMoonSign moonSign;
    public int freeExp;
    public int investedExp;
    public String hairColor;
    public String eyeColor;
    public String furColor;
    public int size;
    public int weight;
    public String deity;
    public String gender;
    public List<String> weaknesses;
    public List<JSONAttribute> attributes;
    public List<JSONSkill> skills;
    public List<JSONPower> powers;
    public List<JSONResource> resources;
    public List<JSONSpell> spells;
    public List<JSONArmor> armors;
    public List<JSONShield> shields;
    public List<JSONMeleeWeapon> meleeWeapons;
    public List<JSONLongRangeWeapon> longRangeWeapons;
    public List<JSONItem> items;
    public String notes;
    public int telare;
}
