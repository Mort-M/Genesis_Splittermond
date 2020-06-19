package de.rpgframework.splittermond.print.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.rpgframework.RPGFrameworkLoader;
import de.rpgframework.core.RoleplayingSystem;
import de.rpgframework.splittermond.print.json.model.JSONArmor;
import de.rpgframework.splittermond.print.json.model.JSONAttribute;
import de.rpgframework.splittermond.print.json.model.JSONCharacter;
import de.rpgframework.splittermond.print.json.model.JSONFeature;
import de.rpgframework.splittermond.print.json.model.JSONItem;
import de.rpgframework.splittermond.print.json.model.JSONMastership;
import de.rpgframework.splittermond.print.json.model.JSONMeleeWeapon;
import de.rpgframework.splittermond.print.json.model.JSONMoonSign;
import de.rpgframework.splittermond.print.json.model.JSONPower;
import de.rpgframework.splittermond.print.json.model.JSONLongRangeWeapon;
import de.rpgframework.splittermond.print.json.model.JSONResource;
import de.rpgframework.splittermond.print.json.model.JSONShield;
import de.rpgframework.splittermond.print.json.model.JSONSkill;
import de.rpgframework.splittermond.print.json.model.JSONSpell;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.AttributeValue;
import org.prelle.splimo.Mastership;
import org.prelle.splimo.MastershipReference;
import org.prelle.splimo.Power;
import org.prelle.splimo.PowerReference;
import org.prelle.splimo.ResourceReference;
import org.prelle.splimo.Skill;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.Spell;
import org.prelle.splimo.SpellValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.SplitterTools;
import org.prelle.splimo.items.CarriedItem;
import org.prelle.splimo.items.Feature;
import org.prelle.splimo.items.FeatureType;
import org.prelle.splimo.items.ItemType;
import org.prelle.splimo.items.LongRangeWeapon;
import org.prelle.splimo.persist.WeaponDamageConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.prelle.splimo.items.ItemType.*;

public class JSONExportService {

    public String exportCharacter(SpliMoCharacter character) {
        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
        return gson.toJson(getJSONCharacter(character));
    }

    private JSONCharacter getJSONCharacter(SpliMoCharacter character) {
        JSONCharacter jsonCharacter = new JSONCharacter();
        setGeneralInfo(jsonCharacter, character);
        setAttributes(jsonCharacter, character);
        setSkills(jsonCharacter, character);
        setSpells(jsonCharacter, character);
        setPowers(jsonCharacter, character);
        setResources(jsonCharacter, character);
        setWeaknesses(jsonCharacter, character);
        setArmors(jsonCharacter, character);
        setShields(jsonCharacter, character);
        setMeleeWeapons(jsonCharacter, character);
        setLongRangeWeapons(jsonCharacter, character);
        setItems(jsonCharacter, character);
        return jsonCharacter;
    }

    private void setLongRangeWeapons(JSONCharacter jsonCharacter, SpliMoCharacter character) {
        List<JSONLongRangeWeapon> jsonLongRangeWeapons = new ArrayList<>();
        List<CarriedItem> items = character.getItems().stream().filter(i -> i.isType(LONG_RANGE_WEAPON)).collect(Collectors.toList());
        for (CarriedItem item : items) {
            JSONLongRangeWeapon jsonLongRangeWeapon = new JSONLongRangeWeapon();
            //Fernkampf Wert Fertigkeit Attribute Schaden Geschw. Reichweite Merkmale
            jsonLongRangeWeapon.name = item.getName();

            boolean hasLicense = true;
            if (item.getItem().getPlugin()!=null){
                hasLicense = RPGFrameworkLoader.getInstance().getLicenseManager().hasLicense(RoleplayingSystem.SPLITTERMOND,item.getItem().getPlugin().getID()) || item.getItem().getPlugin().getID().equals("CORE");
            }
            jsonLongRangeWeapon.value = SplitterTools.getWeaponValueFor(character, item, LONG_RANGE_WEAPON);
            jsonLongRangeWeapon.skill = item.getSkill(LONG_RANGE_WEAPON).getName();
            jsonLongRangeWeapon.attribute1 = item.getAttribute1(LONG_RANGE_WEAPON).getShortName();
            jsonLongRangeWeapon.attribute1Id = item.getAttribute1(LONG_RANGE_WEAPON).name();
            jsonLongRangeWeapon.attribute2 = item.getAttribute2(LONG_RANGE_WEAPON).getShortName();
            jsonLongRangeWeapon.attribute2Id = item.getAttribute2(LONG_RANGE_WEAPON).name();
            jsonLongRangeWeapon.damage = hasLicense? getWeaponDamageString(item.getDamage(LONG_RANGE_WEAPON)) : "";
            int tickMalus   = SplitterTools.getTickMalusSum  (character, true);
            int weaponSpeed = SplitterTools.getWeaponSpeedFor(character, item, LONG_RANGE_WEAPON) - tickMalus;
            jsonLongRangeWeapon.weaponSpeed = hasLicense? weaponSpeed: 0;
            jsonLongRangeWeapon.characterTickMalus = tickMalus;
            jsonLongRangeWeapon.calculatedSpeed = hasLicense? weaponSpeed - tickMalus : 0;

            LongRangeWeapon longRangeWeapon = item.getItem().getType(LongRangeWeapon.class);
            int range = longRangeWeapon.getRange();
            jsonLongRangeWeapon.range = hasLicense? range : 0;
            jsonLongRangeWeapon.features = getFeatures(item, LONG_RANGE_WEAPON);
            jsonLongRangeWeapon.relic = item.isRelic();
            jsonLongRangeWeapon.personalized = item.getPersonalizations() != null && !item.getPersonalizations().isEmpty();
            jsonLongRangeWeapons.add(jsonLongRangeWeapon);
        }
        jsonCharacter.longRangeWeapons = jsonLongRangeWeapons;
    }

    private void setShields(JSONCharacter jsonCharacter, SpliMoCharacter character) {
        List<JSONShield> jsonShields = new ArrayList<>();
        List<CarriedItem> items = character.getItems().stream().filter(i -> i.isType(SHIELD)).collect(Collectors.toList());
        for (CarriedItem item : items) {
            JSONShield jsonShield = new JSONShield();
            SkillValue shieldSkillValue = character.getShieldSkillValue();
            int INT = character.getAttribute(Attribute.INTUITION).getValue();
            int str = character.getAttribute(Attribute.STRENGTH).getValue();
            int agi = character.getAttribute(Attribute.AGILITY).getValue();

            jsonShield.name = item.getName();
            jsonShield.activeDefenseValue = shieldSkillValue.getModifiedValue()+ INT + str;
            jsonShield.skill = shieldSkillValue.getName();
            jsonShield.attack = shieldSkillValue.getModifiedValue() + str + agi;
            jsonShield.damage = "1W6+1";
            jsonShield.defensePlus = item.getDefense(SHIELD);
            jsonShield.handicap = item.getDefense(SHIELD);
            jsonShield.tickMalus = item.getTickMalus(SHIELD);
            jsonShield.features = getFeatures(item, SHIELD);
            jsonShield.relic = item.isRelic();
            jsonShield.personalized = item.getPersonalizations() != null && !item.getPersonalizations().isEmpty();
            jsonShields.add(jsonShield);
        }
        jsonCharacter.shields = jsonShields;
    }

    private void setItems(JSONCharacter jsonCharacter, SpliMoCharacter character) {
        List<JSONItem> jsonItems = new ArrayList<>();
        List<CarriedItem> items = character.getItems()
                .stream()
                .filter(i -> !i.isType(SHIELD) && !i.isType(ARMOR) && !i.isType(WEAPON) && !i.isType(LONG_RANGE_WEAPON))
                .collect(Collectors.toList());
        for (CarriedItem item : items) {
            JSONItem jsonItem = new JSONItem();
            jsonItem.name = item.getName();
            jsonItem.count = item.getCount();
            jsonItem.relic = item.isRelic();
            jsonItem.personalized = item.getName() != null && !item.getPersonalizations().isEmpty();
            jsonItems.add(jsonItem);
        }
        jsonCharacter.items = jsonItems;
    }

    private void setMeleeWeapons(JSONCharacter jsonCharacter, SpliMoCharacter character) {
        List<JSONMeleeWeapon> meleeWeapons = new ArrayList<>();
        List<CarriedItem> items = character.getItems().stream().filter(i -> i.isType(WEAPON)).collect(Collectors.toList());
        meleeWeapons.add(getMeleeAsSkill(character));
        for (CarriedItem item : items) {
            JSONMeleeWeapon jsonWeapon = new JSONMeleeWeapon();
            jsonWeapon.name = item.getName();
            boolean hasLicense = false;
            if (item.getItem().getPlugin()!=null) {
                hasLicense = RPGFrameworkLoader.getInstance().getLicenseManager().hasLicense(RoleplayingSystem.SPLITTERMOND, item.getItem().getPlugin().getID()) || item.getItem().getPlugin().getID().equals("CORE");
            }
            String attribute1Name = item.getAttribute1(WEAPON).getShortName();
            String attribute1Id = item.getAttribute1(WEAPON).name();
            String attribute2Name = item.getAttribute2(WEAPON).getShortName();
            String attribute2Id = item.getAttribute2(WEAPON).name();
            int weaponSkill = SplitterTools.getWeaponValueFor(character, item, WEAPON);
            int tickMalus = SplitterTools.getTickMalusSum(character, true);
            int weaponSpeed = SplitterTools.getWeaponSpeedFor(character, item, WEAPON) - tickMalus;
            jsonWeapon.skill = item.getSkill(WEAPON).getName();
            jsonWeapon.value = weaponSkill;
            jsonWeapon.attribute1 = attribute1Name;
            jsonWeapon.attribute1Id = attribute1Id;
            jsonWeapon.attribute2 = attribute2Name;
            jsonWeapon.attribute2Id = attribute2Id;
            if (hasLicense) {
                jsonWeapon.damage = getWeaponDamageString(item.getDamage(WEAPON));
                jsonWeapon.weaponSpeed = weaponSpeed;
                jsonWeapon.calculatedSpeed = weaponSpeed + tickMalus;
            }
            jsonWeapon.characterTickMalus = tickMalus;
            jsonWeapon.relic = item.isRelic();
            jsonWeapon.personalized = item.getPersonalizations() != null && !item.getPersonalizations().isEmpty();
            jsonWeapon.features = getFeatures(item, WEAPON);
            meleeWeapons.add(jsonWeapon);
        }

        jsonCharacter.meleeWeapons = meleeWeapons;
    }

    public static String getWeaponDamageString(int damage) {
        try {
            return new WeaponDamageConverter().write(damage);
        } catch (Exception e) {
            return " ";
        }
    }

    private JSONMeleeWeapon getMeleeAsSkill(SpliMoCharacter character) {
        JSONMeleeWeapon weapon = new JSONMeleeWeapon();
        weapon.name = "Waffenlos";
        Skill melee = SplitterMondCore.getSkill("melee");
        if (melee != null) {
            weapon.skill = melee.getName();
        }
        weapon.attribute1 = Attribute.AGILITY.getShortName();
        weapon.attribute1Id = Attribute.AGILITY.name();
        weapon.attribute2 = Attribute.STRENGTH.getShortName();
        weapon.attribute2Id = Attribute.STRENGTH.name();
        weapon.value = character.getMeleeValue();
        List<JSONFeature> jsonFeatures = new ArrayList<>();
        String[] features = new String[]{"DISARMING", "BLUNT" , "CLUTCH"};
        for (String feature : features) {
            FeatureType featureType = SplitterMondCore.getFeatureType(feature);
            if (featureType != null) {
                JSONFeature jsonFeature = new JSONFeature();
                jsonFeature.name = featureType.getName();
                if (feature.equals("DISARMING")) {
                    jsonFeature.level = 1;
                }
                jsonFeature.description = featureType.getHelpText();
                jsonFeature.page = getPageString(featureType.getPage(), featureType.getProductNameShort());
                jsonFeatures.add(jsonFeature);
            }
        }
        weapon.features = jsonFeatures;
        weapon.damage = "1W6";
        weapon.weaponSpeed = 5;
        weapon.characterTickMalus = SplitterTools.getTickMalusSum(character, true);
        weapon.calculatedSpeed = 5 + SplitterTools.getTickMalusSum(character, true);
        return weapon;
    }

    private void setArmors(JSONCharacter jsonCharacter, SpliMoCharacter character) {
        //NAME, VTD, SR, Beh, tick+, merkmale
        List<JSONArmor> jsonArmors = new ArrayList<>();
        List<CarriedItem> items = character.getItems().stream().filter(carriedItem -> carriedItem.isType(ARMOR)).collect(Collectors.toList());
        for (CarriedItem item : items) {
            JSONArmor jsonArmor = new JSONArmor();
            jsonArmor.name = item.getName();
            jsonArmor.defense =item.getDefense(ARMOR);
            jsonArmor.damageReduction = item.getDamageReduction(ARMOR);
            jsonArmor.handicap = item.getHandicap(ARMOR);
            jsonArmor.tickMalus = item.getTickMalus(ARMOR);
            jsonArmor.features = getFeatures(item, ARMOR);
            if (item.isRelic()){
               jsonArmor.relic = true;
            }
            if (!item.getPersonalizations().isEmpty()){
                jsonArmor.personalized = true;
            }
            jsonArmors.add(jsonArmor);
        }
        jsonCharacter.armors = jsonArmors;
    }

    private List<JSONFeature> getFeatures(CarriedItem item, ItemType type) {
        List<JSONFeature> jsonFeatures = new ArrayList<>();
        for (Feature feature : item.getFeatures(type)) {
            JSONFeature jsonFeature = new JSONFeature();
            jsonFeature.name = feature.getName();
            jsonFeature.level = feature.getLevel();
            jsonFeature.description = feature.getType().getHelpText();
            jsonFeature.page = getPageString(feature.getType().getPage(), feature.getType().getProductNameShort());
            jsonFeatures.add(jsonFeature);
        }
        return jsonFeatures;
    }

    private void setSpells(JSONCharacter jsonCharacter, SpliMoCharacter character) {
        List<JSONSpell> jsonSpells = new ArrayList<>();
        for (SpellValue spellValue : character.getSpells()) {
            JSONSpell jsonSpell = new JSONSpell();
            Spell spell = spellValue.getSpell();
            Integer effectRange = spell.getEffectRange();
            jsonSpell.name = spell.getName();
            jsonSpell.id = spell.getId();
            jsonSpell.value = character.getSpellValueFor(spellValue);
            jsonSpell.school = spellValue.getSkill().getName();
            jsonSpell.schoolGrade = spell.getLevelInSchool(spellValue.getSkill());
            jsonSpell.difficulty = spell.getDifficultyString();
            if (effectRange != null && effectRange == -1) {
                jsonSpell.focus = "";
                jsonSpell.castDuration = "";
                jsonSpell.castRange = "";
                jsonSpell.spellDuration = "";
                jsonSpell.enhancement = "";
            } else {
                jsonSpell.focus = SplitterTools.getModifiedFocusString(character, spellValue);
                jsonSpell.castDuration = spell.getCastDurationString();
                jsonSpell.castRange = spell.getCastRangeString();
                jsonSpell.spellDuration = spell.getSpellDurationString();
                jsonSpell.enhancement = spell.getEnhancementString();
            }
            String pageBook;
            pageBook = getPageString(spell.getPage(), spell.getProductNameShort());
            jsonSpell.page = pageBook;
            jsonSpell.longDescription = spell.getDescription();
            jsonSpells.add(jsonSpell);
        }
        jsonCharacter.spells = jsonSpells;
    }

    private String getPageString(int page, String productNameShort) {
        String pageBook;
        if (page == 0) {
            pageBook = " ";
        } else {
            pageBook = String.format("%s %s", productNameShort, page);
        }
        return pageBook;
    }

    private void setSkills(JSONCharacter jsonCharacter, SpliMoCharacter character) {
        List<JSONSkill> jsonSkills = new ArrayList<>();
        for (SkillValue skillValue: character.getSkills()) {
            jsonSkills.add(getJSONSkill(skillValue, character));
        }
        jsonCharacter.skills = jsonSkills;
    }

    private JSONSkill getJSONSkill(SkillValue skillValue, SpliMoCharacter character) {
        JSONSkill jsonSkill = new JSONSkill();
        jsonSkill.name = skillValue.getName();
        jsonSkill.id = skillValue.getModifyable().getId();
        Attribute attribute1 = skillValue.getModifyable().getAttribute1();
        if (attribute1 != null) {
            jsonSkill.attribute1 = attribute1.getShortName();
        }
        Attribute attribute2 = skillValue.getModifyable().getAttribute2();
        if (attribute2 != null) {
            jsonSkill.attribute2 = attribute2.getShortName();
        }
        if (attribute1 != null && attribute2 != null) {
            int valueAttr1 = character.getAttribute(attribute1).getValue();
            int valueAttr2 = character.getAttribute(attribute2).getValue();
            jsonSkill.value = skillValue.getModifiedValue() + valueAttr1 + valueAttr2;
        } else {
            jsonSkill.value = skillValue.getModifiedValue();
        }
        jsonSkill.points = skillValue.getPoints();
        jsonSkill.modifier = skillValue.getModifier();
        List<MastershipReference> masterships = skillValue.getMasterships();
        List<JSONMastership> jsonMasterships = new ArrayList<>();
        for (MastershipReference mastershipRef : masterships) {
            JSONMastership jsonMastership = new JSONMastership();
            Mastership mastership = mastershipRef.getMastership();
            if (mastership == null){
                jsonMastership.name = mastershipRef.getSpecialization().getName();
                jsonMastership.id = mastershipRef.getSpecialization().getSpecial().getId();
                jsonMastership.level = mastershipRef.getSpecialization().getLevel();
            }   else {
                jsonMastership.name = mastership.getName();
                jsonMastership.id = mastership.getId();
                jsonMastership.level = mastership.getLevel();
                jsonMastership.shortDescription = mastership.getShortDescription();
                jsonMastership.longDescription = mastership.getHelpText();
                jsonMastership.page = getPageString(mastership.getPage(), mastership.getProductNameShort());
            }
            jsonMasterships.add(jsonMastership);
        }
        jsonSkill.masterships = jsonMasterships;
        return jsonSkill;
    }

    private void setWeaknesses(JSONCharacter jsonCharacter, SpliMoCharacter character) {
        jsonCharacter.weaknesses = character.getWeaknesses();
    }

    private void setResources(JSONCharacter jsonCharacter, SpliMoCharacter character) {
        List<JSONResource> resources = new ArrayList<>();
        for (ResourceReference resource : character.getResources()) {
            JSONResource jsonResource = new JSONResource();
            jsonResource.name = resource.getResource().getName();
            jsonResource.value = resource.getModifiedValue();
            jsonResource.description = resource.getDescription();
            resources.add(jsonResource);
        }
        jsonCharacter.resources = resources;
    }

    private void setPowers(JSONCharacter jsonCharacter, SpliMoCharacter character) {
        List<JSONPower> jsonPowers = new ArrayList<>();
        for (PowerReference powerReference : character.getPowers()) {
            JSONPower jsonPower = new JSONPower();
            Power power = powerReference.getModifyable();
            jsonPower.name = power.getName();
            jsonPower.id = power.getId();
            jsonPower.count = powerReference.getModifiedCount();
            jsonPower.page = getPageString(power.getPage(), powerReference.getPower().getProductNameShort());
            jsonPower.shortDescription = power.getDescription();
            jsonPower.longDescription = power.getHelpText();
            jsonPowers.add(jsonPower);
        }
        jsonCharacter.powers = jsonPowers;
    }

    private void setAttributes(JSONCharacter jsonCharacter, SpliMoCharacter character) {
        List<JSONAttribute> attributes = new ArrayList<>();
        for (Attribute attribute : Attribute.values()) {
            attributes.add(getJSONAttribute(attribute, character));
        }
       jsonCharacter.attributes = attributes;
    }

    private JSONAttribute getJSONAttribute(Attribute attribute, SpliMoCharacter character) {
        JSONAttribute jsonAttribute = new JSONAttribute();
        AttributeValue attributeValue = character.getAttribute(attribute);
        jsonAttribute.id = attribute.name();
        jsonAttribute.name = attribute.getName();
        jsonAttribute.shortName = attribute.getShortName();
        jsonAttribute.startValue = attributeValue.getStart();
        jsonAttribute.value = attributeValue.getValue();
        return jsonAttribute;
    }

    private void setGeneralInfo(JSONCharacter jsonCharacter, SpliMoCharacter character) {
        jsonCharacter.name = character.getName();
        jsonCharacter.race = character.getRace().getName();
        jsonCharacter.culture = character.getCulture().getName();
        jsonCharacter.background = character.getBackground().getName();
        jsonCharacter.birthplace = character.getBirthplace();
        jsonCharacter.education = character.getEducation().getName();
        jsonCharacter.cultureLores = getJSONCultureLores(character);
        jsonCharacter.languages = getJSONLanguages(character);
        jsonCharacter.moonSign = getJSONMoonSign(character);
        jsonCharacter.freeExp = character.getExperienceFree();
        jsonCharacter.investedExp = character.getExperienceInvested();
        jsonCharacter.hairColor = character.getHairColor();
        jsonCharacter.eyeColor = character.getEyeColor();
        jsonCharacter.furColor = character.getFurColor();
        jsonCharacter.size = character.getSize();
        jsonCharacter.weight = character.getWeight();
        jsonCharacter.gender = character.getGender().toString();
        if (character.getDeity()!=null) {
            jsonCharacter.deity = character.getDeity().getName();
        }
        jsonCharacter.notes = character.getNotes();
    }

    private JSONMoonSign getJSONMoonSign(SpliMoCharacter character) {
        JSONMoonSign jsonMoonSign = new JSONMoonSign();
        jsonMoonSign.name = character.getSplinter().getName();
        jsonMoonSign.description = character.getSplinter().getDescription();
        return jsonMoonSign;
    }

    private List<String> getJSONLanguages(SpliMoCharacter character) {
        return character.getLanguages().stream().map(l -> l.getLanguage().getName()).collect(Collectors.toList());
    }

    private List<String> getJSONCultureLores(SpliMoCharacter character) {
        return character.getCultureLores().stream().map(l -> l.getCultureLore().getName()).collect(Collectors.toList());
    }
}
