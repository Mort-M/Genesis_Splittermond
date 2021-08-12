package de.rpgframework.splittermond.export.foundry;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.prelle.splimo.Attribute;
import org.prelle.splimo.PowerReference;
import org.prelle.splimo.ResourceReference;
import org.prelle.splimo.Skill;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.SpellSchoolEntry;
import org.prelle.splimo.SpellValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.SplitterTools;
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
import de.rpgframework.splittermond.foundry.Resource;
import de.rpgframework.splittermond.foundry.Spell;
import de.rpgframework.splittermond.foundry.SplittermondFoundryCharacter;

public class FoundryExportService {

	public String exportCharacter(SpliMoCharacter character) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		Actor actor = new Actor(character.getName(), "character", getJSONCharacter(character));
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
				json.attributes.charisma.initial = model.getAttribute(attribute).getStart(); 
				json.attributes.charisma.advances = model.getAttribute(attribute).getValue() - model.getAttribute(attribute).getStart(); 
				break;
			case AGILITY  : 
				json.attributes.agility.initial = model.getAttribute(attribute).getStart(); 
				json.attributes.agility.advances = model.getAttribute(attribute).getValue() - model.getAttribute(attribute).getStart(); 
				break;
			case INTUITION: 
				json.attributes.intuition.initial = model.getAttribute(attribute).getStart(); 
				json.attributes.intuition.advances = model.getAttribute(attribute).getValue() - model.getAttribute(attribute).getStart(); 
				break;
			case CONSTITUTION: 
				json.attributes.constitution.initial = model.getAttribute(attribute).getStart(); 
				json.attributes.constitution.advances = model.getAttribute(attribute).getValue() - model.getAttribute(attribute).getStart(); 
				break;
			case MYSTIC   : 
				json.attributes.mystic.initial = model.getAttribute(attribute).getStart(); 
				json.attributes.mystic.advances = model.getAttribute(attribute).getValue() - model.getAttribute(attribute).getStart(); 
				break;
			case STRENGTH : 
				json.attributes.strength.initial = model.getAttribute(attribute).getStart(); 
				json.attributes.strength.advances = model.getAttribute(attribute).getValue() - model.getAttribute(attribute).getStart(); 
				break;
			case MIND     : 
				json.attributes.mind.initial = model.getAttribute(attribute).getStart(); 
				json.attributes.mind.advances = model.getAttribute(attribute).getValue() - model.getAttribute(attribute).getStart(); 
				break;
			case WILLPOWER: 
				json.attributes.willpower.initial = model.getAttribute(attribute).getStart(); 
				json.attributes.willpower.advances = model.getAttribute(attribute).getValue() - model.getAttribute(attribute).getStart(); 
				break;

//			case SIZE:
////				json.attr2.size.value = model.getAttribute(attribute).getDistributed(); 
////				json.attr2.size.mod   = model.getAttribute(attribute).getModifier(); 
//				json.attr2.size.value = model.getAttribute(attribute).getValue(); 
//				break;
//			case SPEED:
////				json.attr2.speed.value = model.getAttribute(attribute).getDistributed(); 
////				json.attr2.speed.mod   = model.getAttribute(attribute).getModifier(); 
////				json.attr2.speed.current = model.getAttribute(attribute).getValue(); 
//				json.attr2.speed.value = model.getAttribute(attribute).getValue(); 
//				break;
//			case LIFE:
//				json.attr2.healthpoints.value = model.getAttribute(attribute).getValue()*5; 
//				break;
//			case FOCUS:
//				json.attr2.focuspoints.value = model.getAttribute(attribute).getValue(); 
//				break;
//			case DEFENSE:
////				json.attr2.defense.value = model.getAttribute(attribute).getDistributed(); 
////				json.attr2.defense.mod   = model.getAttribute(attribute).getModifier(); 
//				json.attr2.defense.value = model.getAttribute(attribute).getValue(); 
//				break;
//			case DAMAGE_REDUCTION:
////				json.attr2.sr.value = model.getAttribute(attribute).getDistributed(); 
////				json.attr2.sr.mod   = model.getAttribute(attribute).getModifier(); 
//				json.attr2.sr.value = model.getAttribute(attribute).getValue(); 
//				break;
//			case MINDRESIST:
////				json.attr2.mindresist.value = model.getAttribute(attribute).getDistributed(); 
////				json.attr2.mindresist.mod   = model.getAttribute(attribute).getModifier(); 
//				json.attr2.mindresist.value = model.getAttribute(attribute).getValue(); 
//				break;
//			case BODYRESIST:
////				json.attr2.bodyresist.value = model.getAttribute(attribute).getDistributed(); 
////				json.attr2.bodyresist.mod   = model.getAttribute(attribute).getModifier(); 
//				json.attr2.bodyresist.value = model.getAttribute(attribute).getValue(); 
//				break;
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
//			jVal.name = skill.getName();
//			if (skill.getAttribute1()!=null) {
//				jVal.attribute1 =  skill.getAttribute1().getShortName().toLowerCase();
//				if (jVal.attribute1.equals("stä")) jVal.attribute1="sta";
//				if (jVal.attribute1.equals("int")) jVal.attribute1="inn";
//			} else 
//				jVal.attribute1="";
//			if (skill.getAttribute2()!=null) {
//				jVal.attribute2 =  skill.getAttribute2().getShortName().toLowerCase();
//				if (jVal.attribute2.equals("stä")) jVal.attribute2="sta";
//				if (jVal.attribute2.equals("int")) jVal.attribute2="inn";
//			} else 
//				jVal.attribute2="";
			jVal.points = val.getPoints();
//			jVal.modifier = val.getModifier();
			jVal.value    = val.getModifiedValue();
//			List<String> masterships = val.getMasterships().stream().map(ref -> ref.getName()).collect(Collectors.toList());
//			jVal.masterships = String.join(", ", masterships);
//			jVal.type     = skill.getType().name().toLowerCase();
			jVal.sortKey  = skill.getType().ordinal()+"-"+skill.getName();
			json.skills.put(skill.getId(), jVal);
		}
	}

	//-------------------------------------------------------------------
	private void addFoundryItems(Actor actor, SpliMoCharacter character) {
		addGear(actor, character);		
		addSpells(actor, character);
		addPowers(actor, character);
		addResources(actor, character);
	}

	//-------------------------------------------------------------------
	private void addGear(Actor actor, SpliMoCharacter character) {
		WeaponDamageConverter dmgConv = new WeaponDamageConverter();
		for (CarriedItem item : character.getItems()) {
			ItemType type = item.getItem().getFirstItemType();
			Gear gear = new Gear();
			gear.weight = item.getLoad();
			gear.availability = item.getAvailability().name();
			if (item.getItem().getComplexity()!=null)
				gear.complexity = item.getItem().getComplexity().getID();
			else
				System.err.println("No complexity for "+item.getItem());
			if (item.getSkill(type)!=null) {
				gear.skill = item.getSkill(item.getItem().getFirstItemType()).getId();
			}
			if (item.isType(ItemType.WEAPON) || item.isType(ItemType.LONG_RANGE_WEAPON)) {
				gear.damage = dmgConv.writeEnglish(item.getDamage(type));
				gear.weaponSpeed = item.getSpeed(type);
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
			spell.genesisId = item.getSpell().getId();
			for (SpellSchoolEntry entry : item.getSpell().getSchools()) {
				if (item.getSkill()==entry.getSchool())
					spell.skillLevel = entry.getLevel();
			}
			
			spell.skill= item.getSkill().getId();
			spell.difficulty = item.getSpell().getDifficultyString();
			spell.castDuration = item.getSpell().getCastDurationString();
			spell.castTicks= item.getSpell().getCastDurationTicks();
			spell.costs = SplitterTools.getFocusString( item.getSpell().getCost() );
			spell.effectDuration = item.getSpell().getSpellDuration();
			if (item.getSpell().getEffectRange()!=null) {
				spell.effRangeMeter = item.getSpell().getEffectRange();
				spell.effectArea = item.getSpell().getEffectRange()+"m";
			}
			
			Item<Spell> foundry = new Item<Spell>(item.getSpell().getName(), "spell", spell);
			actor.addItems(foundry);
		}
	}

	//-------------------------------------------------------------------
	private void addPowers(Actor actor, SpliMoCharacter character) {
		for (PowerReference item : character.getPowers()) {
			Power spell = new Power();
			spell.id = item.getPower().getId();
//			spell.level = item.getPower().;
			
			Item<Power> foundry = new Item<Power>(item.getPower().getName(), "power", spell);
			actor.addItems(foundry);
		}
	}


	//-------------------------------------------------------------------
	private void addResources(Actor actor, SpliMoCharacter character) {
		for (ResourceReference item : character.getResources()) {
			Resource spell = new Resource();
//			spell.id = item.getResource().getId();
//			spell.value = item.getModifiedValue();
			
			Item<Resource> foundry = new Item<Resource>(item.getResource().getName(), "resource", spell);
			actor.addItems(foundry);
		}
	}
}
