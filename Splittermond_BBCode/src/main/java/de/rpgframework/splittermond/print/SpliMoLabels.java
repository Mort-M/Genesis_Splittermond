package de.rpgframework.splittermond.print;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.splimo.SplitterMondCore;

/**
 * This class provides access to all rule relevant strings of the SpliMo universe.
 * 
 * @author frank.buettner
 *
 */
public final class SpliMoLabels {
	
	private static PropertyResourceBundle CORE = SplitterMondCore
			.getI18nResources();
	private static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/splimo-chargen");
	
	private SpliMoLabels() {
		// don't initialize this class
	}

	public static String getLabelAbwehr() {
		return UI.getString("label.abwehr");
	}

	public static String getLabelAbwehrShort() {
		return UI.getString("label.abwehr.short");
	}

	public static String getLabelArmor() {
		return UI.getString("label.armor");
	}

	public static String getLabelArtifactQuality() {
		return UI.getString("label.artifactQuality");
	}

	public static String getLabelAttribute() {
		return CORE.getString("label.attribute");
	}

	public static String getLabelAttribute1Short() {
		return UI.getString("label.attribute1.short");
	}

	public static String getLabelAttribute2Short() {
		return UI.getString("label.attribute2.short");
	}

	public static String getLabelAttributes() {
		return UI.getString("label.attributes");
	}

	public static String getLabelAvailability() {
		return UI.getString("label.availability");
	}

	public static String getLabelBackground() {
		return UI.getString("label.background");
	}

	public static String getLabelBirthplace() {
		return UI.getString("label.birthplace");
	}

	public static String getLabelCarriageLocation() {
		return UI.getString("label.carriage.location");
	}

	public static String getLabelCastdur() {
		return UI.getString("label.castdur");
	}

	public static String getLabelCastdurShort() {
		return UI.getString("label.castdur.short");
	}

	public static String getLabelCombatskillShort() {
		return UI.getString("label.combatskill.short");
	}

	public static String getLabelContainer() {
		return UI.getString("label.container");
	}

	public static String getLabelCost() {
		return UI.getString("label.cost");
	}

	public static String getLabelCulture() {
		return UI.getString("label.culture");
	}

	public static String getLabelCulturelore() {
		return UI.getString("label.culturelore");
	}

	public static String getLabelDate() {
		return UI.getString("label.date");
	}

	public static String getLabelDerived_values() {
		return UI.getString("label.derived_values");
	}

	public static String getLabelDiff() {
		return UI.getString("label.diff");
	}

	public static String getLabelDiffShort() {
		return UI.getString("label.diff.short");
	}

	public static String getLabelEducation() {
		return UI.getString("label.education");
	}

	public static String getLabelEffect() {
		return UI.getString("label.effect");
	}

	public static String getLabelEnhanced() {
		return UI.getString("label.enhanced");
	}

	public static String getLabelEnhancement() {
		return UI.getString("label.enhancement");
	}

	public static String getLabelEnhancementSkillspec() {
		return UI.getString("label.enhancement.skillspec");
	}

	public static String getLabelEpFree() {
		return UI.getString("label.ep.free");
	}

	public static String getLabelEpUsed() {
		return UI.getString("label.ep.used");
	}

	public static String getLabelEqmodifiedShort() {
		return UI.getString("label.eqmodified.short");
	}

	public static String getLabelEquipment() {
		return UI.getString("label.equipment");
	}

	public static String getLabelExperience() {
		return UI.getString("label.experience");
	}

	public static String getLabelExperienceShort() {
		return UI.getString("label.experience.short");
	}

	public static String getLabelEyes() {
		return UI.getString("label.eyes");
	}

	public static String getLabelFocus() {
		return UI.getString("label.focus");
	}

	public static String getLabelGender() {
		return UI.getString("label.gender");
	}

	public static String getLabelHair() {
		return UI.getString("label.hair");
	}

	public static String getLabelHistory() {
		return UI.getString("label.history");
	}

	public static String getLabelHistoryTitle() {
		return UI.getString("label.history.title");
	}

	public static String getLabelItemQuality() {
		return UI.getString("label.itemQuality");
	}

	public static String getLabelKnock() {
		return UI.getString("label.knock");
	}

	public static String getLabelKnockdamage() {
		return UI.getString("label.knockdamage");
	}

	public static String getLabelKnockdamageShort() {
		return UI.getString("label.knockdamage.short");
	}

	public static String getLabelLanguages() {
		return UI.getString("label.languages");
	}

	public static String getLabelLevel() {
		return UI.getString("label.level");
	}

	public static String getLabelLevel0() {
		return UI.getString("label.level0");
	}

	public static String getLabelLevel1() {
		return UI.getString("label.level1");
	}

	public static String getLabelLevel2() {
		return UI.getString("label.level2");
	}

	public static String getLabelLevelShort() {
		return UI.getString("label.level.short");
	}

	public static String getLabelMasteries() {
		return UI.getString("label.masteries");
	}

	public static String getLabelMastery() {
		return UI.getString("label.mastery");
	}

	public static String getLabelMaxAttrib() {
		return UI.getString("label.maxAttrib");
	}

	public static String getLabelMaxSkill() {
		return UI.getString("label.maxSkill");
	}

	public static String getLabelModifiedShort() {
		return UI.getString("label.modified.short");
	}

	public static String getLabelMoonsign() {
		return UI.getString("label.moonsign");
	}

	public static String getLabelName() {
		return UI.getString("label.name");
	}

	public static String getLabelNotes() {
		return UI.getString("label.notes");
	}

	public static String getLabelPage() {
		return UI.getString("label.page");
	}

	public static String getLabelParry() {
		return UI.getString("label.parry");
	}

	public static String getLabelPoints() {
		return UI.getString("label.points");
	}

	public static String getLabelPointsShort() {
		return UI.getString("label.points.short");
	}

	public static String getLabelPortrait() {
		return UI.getString("label.portrait");
	}

	public static String getLabelPower() {
		return UI.getString("label.power");
	}

	public static String getLabelRace() {
		return UI.getString("label.race");
	}

	public static String getLabelRange() {
		return UI.getString("label.range");
	}

	public static String getLabelRangeShort() {
		return UI.getString("label.range.short");
	}

	public static String getLabelRemove() {
		return UI.getString("label.remove");
	}

	public static String getLabelResource() {
		return UI.getString("label.resource");
	}

	public static String getLabelSchool() {
		return UI.getString("label.school");
	}

	public static String getLabelSchools() {
		return UI.getString("label.schools");
	}

	public static String getLabelSchoolShort() {
		return UI.getString("label.school.short");
	}

	public static String getLabelShield() {
		return UI.getString("label.shield");
	}

	public static String getLabelShieldparry() {
		return UI.getString("label.shieldparry");
	}

	public static String getLabelSize() {
		return UI.getString("label.size");
	}

	public static String getLabelSkill() {
		return UI.getString("label.skill");
	}

	public static String getLabelSkills() {
		return UI.getString("label.skills");
	}

	public static String getLabelSkin() {
		return UI.getString("label.skin");
	}

	public static String getLabelSpell() {
		return UI.getString("label.spell");
	}

	public static String getLabelSpelldur() {
		return UI.getString("label.spelldur");
	}

	public static String getLabelSpelldurShort() {
		return UI.getString("label.spelldur.short");
	}

	public static String getLabelSpellsFree() {
		return UI.getString("label.spells.free");
	}

	public static String getLabelSpellstructure() {
		return UI.getString("label.spellstructure");
	}

	public static String getLabelSplinterpoints() {
		return UI.getString("label.splinterpoints");
	}

	public static String getLabelStart() {
		return UI.getString("label.start");
	}

	public static String getLabelTempShort() {
		return UI.getString("label.temp.short");
	}

	public static String getLabelTitle() {
		return UI.getString("label.title");
	}

	public static String getLabelType() {
		return UI.getString("label.type");
	}

	public static String getLabelUnrestrictedLevelling() {
		return UI.getString("label.unrestrictedLevelling");
	}

	public static String getLabelValue() {
		return UI.getString("label.value");
	}

	public static String getLabelWeaknesses() {
		return UI.getString("label.weaknesses");
	}

	public static String getLabelWeapon() {
		return UI.getString("label.weapon");
	}

	public static String getLabelWeight() {
		return UI.getString("label.weight");
	}
}
