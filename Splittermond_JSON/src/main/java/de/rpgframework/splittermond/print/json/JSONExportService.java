package de.rpgframework.splittermond.print.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.rpgframework.splittermond.print.json.model.JSONAttribute;
import de.rpgframework.splittermond.print.json.model.JSONCharacter;
import de.rpgframework.splittermond.print.json.model.JSONPower;
import de.rpgframework.splittermond.print.json.model.JSONResource;
import de.rpgframework.splittermond.print.json.model.JSONSkill;
import de.rpgframework.splittermond.print.json.model.JSONSpell;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.AttributeValue;
import org.prelle.splimo.MastershipReference;
import org.prelle.splimo.PowerReference;
import org.prelle.splimo.ResourceReference;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.Spell;
import org.prelle.splimo.SpellValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterTools;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JSONExportService {

    public String exportCharacter(SpliMoCharacter character) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
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

        //items, creatures, spells, notes
        return jsonCharacter;
    }

    private void setSpells(JSONCharacter jsonCharacter, SpliMoCharacter character) {
        List<JSONSpell> jsonSpells = new ArrayList<>();
        for (SpellValue spellValue : character.getSpells()) {
            JSONSpell jsonSpell = new JSONSpell();
            //Zauber, schule, wert, schwelle, focus, zd, rw, wd, verstärkung
            Spell spell = spellValue.getSpell();
            Integer effectRange = spell.getEffectRange();
            jsonSpell.name = spell.getName();
            int value = character.getSpellValueFor(spellValue);
            jsonSpell.value = value;
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
            if (spell.getPage() == 0) {
                pageBook = " ";
            } else {
                String page = String.valueOf(spell.getPage());
                String book = spell.getProductNameShort();
                pageBook = String.format("%s %s", book, page);
            }
            jsonSpell.page = pageBook;
            jsonSpells.add(jsonSpell);
        }
        jsonCharacter.spells = jsonSpells;
    }

    private void setSkills(JSONCharacter jsonCharacter, SpliMoCharacter character) {
        List<JSONSkill> jsonSkills = new ArrayList<>();
        for (SkillValue skillValue: character.getSkills()) {
            jsonSkills.add(getJSONSkill(skillValue));
        }
        jsonCharacter.skills = jsonSkills;
    }

    private JSONSkill getJSONSkill(SkillValue skillValue) {
        JSONSkill jsonSkill = new JSONSkill();
        jsonSkill.name = skillValue.getName();
        if (skillValue.getModifyable().getAttribute1() != null) {
            jsonSkill.attribute1 = skillValue.getModifyable().getAttribute1().getName();
        }
        if (skillValue.getModifyable().getAttribute2() != null) {
            jsonSkill.attribute2 = skillValue.getModifyable().getAttribute2().getName();
        }
        jsonSkill.value = skillValue.getModifiedValue();
        jsonSkill.points = skillValue.getPoints();
        jsonSkill.modifier = skillValue.getModifier();
        StringBuilder sb = new StringBuilder();
        List<MastershipReference> masterships = skillValue.getMasterships();
        List<String> mastershipNames = new ArrayList<>();
        for (MastershipReference mastership : masterships) {
            if (mastership.getMastership() == null){
                mastershipNames.add(mastership.getSpecialization().getName());
            }   else {
                mastershipNames.add(mastership.getMastership().getName());
            }
        }
        jsonSkill.masterships = String.join(", ", mastershipNames);
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
        for (PowerReference power : character.getPowers()) {
            JSONPower jsonPower = new JSONPower();
            jsonPower.name = power.getModifyable().getName();
            jsonPower.count = power.getModifiedCount();
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
        jsonAttribute.name = attribute.getName();
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
        jsonCharacter.moonSign = character.getSplinter().getName();
        jsonCharacter.freeExp = character.getExperienceFree();
        jsonCharacter.investedExp = character.getExperienceInvested();
        jsonCharacter.hairColor = character.getHairColor();
        jsonCharacter.eyeColor = character.getEyeColor();
        jsonCharacter.furColor = character.getFurColor();
        jsonCharacter.size = character.getSize();
        jsonCharacter.weight = character.getWeight();
        jsonCharacter.gender = character.getGender().toString();
        jsonCharacter.deity = character.getDeity().getName();
        //Heldengrad, Splitterpunkte, Wert Glaube
    }

    private List<String> getJSONLanguages(SpliMoCharacter character) {
        return character.getLanguages().stream().map(l -> l.getLanguage().getName()).collect(Collectors.toList());
    }

    private List<String> getJSONCultureLores(SpliMoCharacter character) {
        return character.getCultureLores().stream().map(l -> l.getCultureLore().getName()).collect(Collectors.toList());
    }
}
