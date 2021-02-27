package de.rpgframework.splittermond.export.foundry;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.prelle.splimo.Attribute;
import org.prelle.splimo.PowerReference;
import org.prelle.splimo.Skill;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.SpellSchoolEntry;
import org.prelle.splimo.SpellValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.items.CarriedItem;
import org.prelle.splimo.items.ItemType;
import org.prelle.splimo.persist.WeaponDamageConverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.rpgframework.splittermond.foundry.Actor;
import de.rpgframework.splittermond.foundry.Gear;
import de.rpgframework.splittermond.foundry.Item;
import de.rpgframework.splittermond.foundry.JSONSkillValue;
import de.rpgframework.splittermond.foundry.Power;
import de.rpgframework.splittermond.foundry.Spell;
import de.rpgframework.splittermond.foundry.Spell.SpellInSchool;
import de.rpgframework.splittermond.foundry.SplittermondFoundryCharacter;

public class FoundryExportService {

	public String exportCharacter(SpliMoCharacter character) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		Actor actor = new Actor(character.getName(), "pc", getJSONCharacter(character));
		addFoundryItems(actor, character);
		return gson.toJson(actor);
	}

	//-------------------------------------------------------------------
	private SplittermondFoundryCharacter getJSONCharacter(SpliMoCharacter character) {
		SplittermondFoundryCharacter jsonCharacter = new SplittermondFoundryCharacter();
		setAttributes(jsonCharacter, character);
		setSkills(jsonCharacter, character);
		return jsonCharacter;
	}


	//-------------------------------------------------------------------
	private void setAttributes(SplittermondFoundryCharacter json, SpliMoCharacter model) {
		for (Attribute attribute : Attribute.values()) {
			switch (attribute) {
			case CHARISMA : 
				json.attr.aus.value = model.getAttribute(attribute).getDistributed(); 
				json.attr.aus.mod   = model.getAttribute(attribute).getModifier(); 
				json.attr.aus.current = model.getAttribute(attribute).getValue(); 
				break;
			case AGILITY  : 
				json.attr.bew.value = model.getAttribute(attribute).getDistributed(); 
				json.attr.bew.mod   = model.getAttribute(attribute).getModifier(); 
				json.attr.bew.current = model.getAttribute(attribute).getValue(); 
				break;
			case INTUITION: 
				json.attr.inn.value = model.getAttribute(attribute).getDistributed(); 
				json.attr.inn.mod   = model.getAttribute(attribute).getModifier(); 
				json.attr.inn.current = model.getAttribute(attribute).getValue(); 
				break;
			case CONSTITUTION: 
				json.attr.kon.value = model.getAttribute(attribute).getDistributed(); 
				json.attr.kon.mod   = model.getAttribute(attribute).getModifier(); 
				json.attr.kon.current = model.getAttribute(attribute).getValue(); 
				break;
			case MYSTIC   : 
				json.attr.mys.value = model.getAttribute(attribute).getDistributed(); 
				json.attr.mys.mod   = model.getAttribute(attribute).getModifier(); 
				json.attr.mys.current = model.getAttribute(attribute).getValue(); 
				break;
			case STRENGTH : 
				json.attr.sta.value = model.getAttribute(attribute).getDistributed(); 
				json.attr.sta.mod   = model.getAttribute(attribute).getModifier(); 
				json.attr.sta.current = model.getAttribute(attribute).getValue(); 
				break;
			case MIND     : 
				json.attr.ver.value = model.getAttribute(attribute).getDistributed(); 
				json.attr.ver.mod   = model.getAttribute(attribute).getModifier(); 
				json.attr.ver.current = model.getAttribute(attribute).getValue(); 
				break;
			case WILLPOWER: 
				json.attr.wil.value = model.getAttribute(attribute).getDistributed(); 
				json.attr.wil.mod   = model.getAttribute(attribute).getModifier(); 
				json.attr.wil.current = model.getAttribute(attribute).getValue(); 
				break;

			case SIZE:
				json.attr2.gk.value = model.getAttribute(attribute).getDistributed(); 
				json.attr2.gk.mod   = model.getAttribute(attribute).getModifier(); 
				json.attr2.gk.current = model.getAttribute(attribute).getValue(); 
				break;
			case SPEED:
				json.attr2.gsw.value = model.getAttribute(attribute).getDistributed(); 
				json.attr2.gsw.mod   = model.getAttribute(attribute).getModifier(); 
				json.attr2.gsw.current = model.getAttribute(attribute).getValue(); 
				break;
			case LIFE:
				json.attr2.lp.max = model.getAttribute(attribute).getValue()*5; 
				break;
			case FOCUS:
				json.attr2.fo.max = model.getAttribute(attribute).getValue(); 
				break;
			case DEFENSE:
				json.attr2.vtd.value = model.getAttribute(attribute).getDistributed(); 
				json.attr2.vtd.mod   = model.getAttribute(attribute).getModifier(); 
				json.attr2.vtd.current = model.getAttribute(attribute).getValue(); 
				break;
			case DAMAGE_REDUCTION:
				json.attr2.sr.value = model.getAttribute(attribute).getDistributed(); 
				json.attr2.sr.mod   = model.getAttribute(attribute).getModifier(); 
				json.attr2.sr.current = model.getAttribute(attribute).getValue(); 
				break;
			case MINDRESIST:
				json.attr2.gw.value = model.getAttribute(attribute).getDistributed(); 
				json.attr2.gw.mod   = model.getAttribute(attribute).getModifier(); 
				json.attr2.gw.current = model.getAttribute(attribute).getValue(); 
				break;
			case BODYRESIST:
				json.attr2.kw.value = model.getAttribute(attribute).getDistributed(); 
				json.attr2.kw.mod   = model.getAttribute(attribute).getModifier(); 
				json.attr2.kw.current = model.getAttribute(attribute).getValue(); 
				break;
			default:
			}
		}
	}

	private void setSkills(SplittermondFoundryCharacter json, SpliMoCharacter model) {
		List<Skill> skills = SplitterMondCore.getSkills();
		Collections.sort(skills);

		for (Skill skill : skills) {
			SkillValue val = model.getSkillValue(skill);
			JSONSkillValue jVal = new JSONSkillValue();
			jVal.name = skill.getName();
			if (skill.getAttribute1()!=null) {
				jVal.attribute1 =  skill.getAttribute1().getShortName().toLowerCase();
				if (jVal.attribute1.equals("stä")) jVal.attribute1="sta";
				if (jVal.attribute1.equals("int")) jVal.attribute1="inn";
			} else 
				jVal.attribute1="";
			if (skill.getAttribute2()!=null) {
				jVal.attribute2 =  skill.getAttribute2().getShortName().toLowerCase();
				if (jVal.attribute2.equals("stä")) jVal.attribute2="sta";
				if (jVal.attribute2.equals("int")) jVal.attribute2="inn";
			} else 
				jVal.attribute2="";
			jVal.points = val.getPoints();
			jVal.modifier = val.getModifier();
			jVal.value    = val.getModifiedValue();
			List<String> masterships = val.getMasterships().stream().map(ref -> ref.getName()).collect(Collectors.toList());
			jVal.masterships = String.join(", ", masterships);
			jVal.type     = skill.getType().name().toLowerCase();
			jVal.sortKey  = skill.getType().ordinal()+"-"+skill.getName();
			json.skills.put(skill.getId(), jVal);
		}
	}

	//-------------------------------------------------------------------
	private void addFoundryItems(Actor actor, SpliMoCharacter character) {
		addGear(actor, character);		
		addSpells(actor, character);
		addPowers(actor, character);
	}

	//-------------------------------------------------------------------
	private void addGear(Actor actor, SpliMoCharacter character) {
		WeaponDamageConverter dmgConv = new WeaponDamageConverter();
		for (CarriedItem item : character.getItems()) {
			ItemType type = item.getItem().getFirstItemType();
			Gear gear = new Gear();
			gear.load = item.getLoad();
			gear.availability = item.getAvailability().name();
			gear.complexity = item.getItem().getComplexity().getID();
			if (item.getSkill(type)!=null) {
				gear.skill = item.getSkill(item.getItem().getFirstItemType()).getId();
			}
			if (item.isType(ItemType.WEAPON) || item.isType(ItemType.LONG_RANGE_WEAPON)) {
				gear.damage = dmgConv.writeEnglish(item.getDamage(type));
				gear.speed = item.getSpeed(type);
				gear.attribute1 = Util.translateAttribute(item.getAttribute1(type)).toUpperCase();
				gear.attribute2 = Util.translateAttribute(item.getAttribute2(type)).toUpperCase();
				List<String> features = item.getFeatures(type).stream().map(f -> f.getName()).collect(Collectors.toList());
				gear.features = String.join(", ", features);
			}
			
			String typeName = "normal";
			if (type==ItemType.WEAPON || type==ItemType.LONG_RANGE_WEAPON) {
				typeName = "weapon";
			}
			if (type==ItemType.ARMOR || type==ItemType.SHIELD) {
				typeName = "armor";
			}
			
			Item<Gear> foundry = new Item<Gear>(item.getName(), typeName, gear);
			actor.addItems(foundry);
		}
	}

	//-------------------------------------------------------------------
	private void addSpells(Actor actor, SpliMoCharacter character) {
		for (SpellValue item : character.getSpells()) {
			Spell spell = new Spell();
			spell.id = item.getSpell().getId();
			for (SpellSchoolEntry entry : item.getSpell().getSchools()) {
				Spell.SpellInSchool foo = new SpellInSchool(entry.getSchool().getId(), entry.getLevel());
				spell.schools.add(foo);
			}
			
			spell.skill= item.getSkill().getId();
			spell.diff = item.getSpell().getDifficulty();
			spell.castDur = item.getSpell().getCastDurationString();
			spell.castTicks= item.getSpell().getCastDurationTicks();
			spell.costK= item.getSpell().getCost().getChannelled();
			spell.costV= item.getSpell().getCost().getConsumed();
			spell.costE= item.getSpell().getCost().getExhausted();
			spell.effDur = item.getSpell().getSpellDuration();
			spell.effRangeMeter = item.getSpell().getEffectRange();
			spell.effRange = item.getSpell().getEffectRange()+"m";
			
			Item<Spell> foundry = new Item<Spell>(item.getSpell().getName(), "spell", spell);
			actor.addItems(foundry);
		}
	}

	//-------------------------------------------------------------------
	private void addPowers(Actor actor, SpliMoCharacter character) {
		for (PowerReference item : character.getPowers()) {
			Power spell = new Power();
			spell.id = item.getPower().getId();
			spell.value = item.getModifiedCount();
			
			Item<Power> foundry = new Item<Power>(item.getPower().getName(), "spell", spell);
			actor.addItems(foundry);
		}
	}

}
