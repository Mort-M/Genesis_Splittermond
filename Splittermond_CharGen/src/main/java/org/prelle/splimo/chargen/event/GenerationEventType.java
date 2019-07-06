/**
 *
 */
package org.prelle.splimo.chargen.event;

/**
 * @author prelle
 *
 */
public enum GenerationEventType {

	/** Key is Character */
	CHARACTER_CHANGED,

	/** Key is Attribute, Value is AttributeValue */
	ATTRIBUTE_CHANGED,
	/** Key is null, Value is Array [ExpFree, ExpInvested] */
	EXPERIENCE_CHANGED,
	/** Key is null, Value is null */
	MONEY_CHANGED,
	/** Key is new level */
	LEVEL_CHANGED,

	/** Key is null, value is an Integer */
	POINTS_LEFT_ATTRIBUTES,
	/** Key is null, value is an Integer */
	POINTS_LEFT_POWERS,
	/** Key is null, value is an Integer */
	POINTS_LEFT_RESOURCES,
	/** Key is Skill, value is an Integer */
	POINTS_LEFT_MASTERSHIPS,

	/** Key is PowerReference */
	POWER_ADDED,
	/** Key is PowerReference */
	POWER_REMOVED,
	/** Key is PowerReference. Used for multiple selectable powers */
	POWER_CHANGED,
	/** Key is List<Power> */
	POWER_AVAILABLE_ADDED,
	/** Key is List<Power> */
	POWER_AVAILABLE_REMOVED,

	/** Key is Weakness */
	WEAKNESS_ADDED,
	/** Key is Weakness */
	WEAKNESS_REMOVED,

	/** Key is ResourceReference */
	RESOURCE_ADDED,
	/** Key is ResourceReference */
	RESOURCE_REMOVED,
	/** Key is ResourceReference. Used when resource value changed */
	RESOURCE_CHANGED,

	/** Key is SpellValue */
	SPELL_ADDED,
	/** Key is SpellValue */
	SPELL_REMOVED,
	/** Key is null. Value is List<FreeSelection> */
	SPELL_FREESELECTION_CHANGED,

	/** Key is SkillValue */
	SKILL_ADDED,
	/** Key is SkillValue */
	SKILL_REMOVED,
	/** Key is Skill. Value is Int-Array [old, new] */
	SKILL_CHANGED,

	/** Key is Skill, value is MastershipReference */
	MASTERSHIP_ADDED,
	/** Key is Skill, value is MastershipReference */
	MASTERSHIP_REMOVED,
	/** Key is Skill, value is MastershipReference. Used for changed levels in specializations */
	MASTERSHIP_CHANGED,

	/** Key is List<CultureLore> */
	CULTURELORE_AVAILABLE_CHANGED,
	/** Key is CultureLoreReference */
	CULTURELORE_ADDED,
	/** Key is CultureLoreReference */
	CULTURELORE_REMOVED,

	/** Key is LanguageReference */
	LANGUAGE_ADDED,
	/** Key is LanguageReference */
	LANGUAGE_REMOVED,

	/** An enhancement or effect was added to or removed from the item
	 * key=CarriedItem   value=[ItemAttribute or Modification, Spent points, max. points] */
	ITEM_CHANGED,

	/**
	 * The list of available and selected creature types changed.
	 * Key is available list. Value is selected list
	 */
	CREATURETYPES_CHANGED,

	RACE_SELECTED,
	CULTURE_OFFER_CHANGED,
	CULTURE_SELECTED,
	BACKGROUND_OFFER_CHANGED,
	/** key=Background */
	BACKGROUND_SELECTED,
	ORIGIN_SELECTED,
	EDUCATION_SELECTED,
	EDUCATION_OFFER_CHANGED,
	RESOURCES_CHANGED,
	MOONSIGN_SELECTED,
//	MASTERSHIPGEN_POINTS_CHANGED,
//	MASTERSHIP_SELECTION_CHANGED,
	SPELL_OFFER_CHANGED, // TODO: remove me
	SPELL_CHANGED, // TODO remove me
	/** Name, Gender, Weight, Size or colors changed. Value is NULL*/
	BASE_DATA_CHANGED,
	UNDO_LIST_CHANGED,

	/** A value of a creature changed
	 * key = creature
	 */
	CREATURE_CHANGED, 
	
	NOTES_CHANGED,
	
	FINISH_REQUESTED
}
