/**
 *
 */
package org.prelle.splimo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SkillSpecialization.SkillSpecializationType;
import org.prelle.splimo.creature.Creature;
import org.prelle.splimo.creature.CreatureModuleReference;
import org.prelle.splimo.creature.CreatureReference;
import org.prelle.splimo.creature.CreatureTools;
import org.prelle.splimo.creature.CreatureModuleReference.NecessaryChoice;
import org.prelle.splimo.items.CarriedItem;
import org.prelle.splimo.items.EnhancementReference;
import org.prelle.splimo.items.ItemLocationType;
import org.prelle.splimo.items.ItemType;
import org.prelle.splimo.items.LongRangeWeapon;
import org.prelle.splimo.items.Weapon;
import org.prelle.splimo.jaxb.WeaponDamageAdapter;
import org.prelle.splimo.modifications.AllOfModification;
import org.prelle.splimo.modifications.AttitudeModification;
import org.prelle.splimo.modifications.AttributeChangeModification;
import org.prelle.splimo.modifications.AttributeModification;
import org.prelle.splimo.modifications.ConditionalModification;
import org.prelle.splimo.modifications.CountModification;
import org.prelle.splimo.modifications.CreatureFeatureModification;
import org.prelle.splimo.modifications.CultureLoreModification;
import org.prelle.splimo.modifications.DamageModification;
import org.prelle.splimo.modifications.FeatureModification;
import org.prelle.splimo.modifications.ItemFeatureModification;
import org.prelle.splimo.modifications.ItemModification;
import org.prelle.splimo.modifications.MastershipModification;
import org.prelle.splimo.modifications.ModificationChoice;
import org.prelle.splimo.modifications.ModificationImpl;
import org.prelle.splimo.modifications.ModificationValueType;
import org.prelle.splimo.modifications.MoneyModification;
import org.prelle.splimo.modifications.PowerModification;
import org.prelle.splimo.modifications.ResourceModification;
import org.prelle.splimo.modifications.SkillModification;
import org.prelle.splimo.modifications.SkillModification.RestrictionType;
import org.prelle.splimo.modifications.SpellModification;
import org.prelle.splimo.modifications.SubModificationChoice;
import org.prelle.splimo.persist.WeaponDamageConverter;
import org.prelle.splimo.requirements.AnyRequirement;
import org.prelle.splimo.requirements.AttributeRequirement;
import org.prelle.splimo.requirements.CreatureFeatureRequirement;
import org.prelle.splimo.requirements.CreatureModuleRequirement;
import org.prelle.splimo.requirements.CreatureTypeRequirement;
import org.prelle.splimo.requirements.ItemFeatureRequirement;
import org.prelle.splimo.requirements.MastershipRequirement;
import org.prelle.splimo.requirements.Requirement;
import org.prelle.splimo.requirements.SkillRequirement;

import de.rpgframework.RPGFrameworkLoader;
import de.rpgframework.core.RoleplayingSystem;
import de.rpgframework.genericrpg.Datable;
import de.rpgframework.genericrpg.Reward;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.products.Adventure;
import de.rpgframework.products.ProductService;

/**
 * @author prelle
 *
 */
public class SplitterTools {

	private final static ResourceBundle CORE = SplitterMondCore.getI18nResources();

	private final static Logger logger = Logger.getLogger("splittermond");

	public final static String LEVEL4 = "Level 4";
	public final static String LEVEL3 = "Level 3";
	public final static String LEVEL2 = "Level 2";

	//-------------------------------------------------------------------
	public static String getGenerationModificationString(Modification mod) {
		String head = getModificationString(mod);
		if (mod instanceof PowerModification) {
			PowerModification pMod = (PowerModification)mod;
			return head+"  ("+pMod.getPower().getProductNameShort()+" "+pMod.getPower().getPage()+")";
		}
		if (mod instanceof CultureLoreModification) {
			CultureLoreModification clMod = (CultureLoreModification)mod;
			return head+"  ("+clMod.getData().getProductNameShort()+" "+clMod.getData().getPage()+")";
		}
		if (mod instanceof ResourceModification) {
			ResourceModification rMod = (ResourceModification)mod;
			return head+"  ("+rMod.getResource().getProductNameShort()+" "+rMod.getResource().getPage()+")";
		}
		if (mod instanceof MastershipModification) {
			MastershipModification mMod = (MastershipModification)mod;
			Mastership master = mMod.getMastership();
			if (master!=null)
				return head+"  ("+master.getProductNameShort()+" "+master.getPage()+")";
			return head;
		}
		if (mod instanceof SpellModification) {
			SpellModification rMod = (SpellModification)mod;
			return head+"  ("+rMod.getSpell().getSpell().getProductNameShort()+" "+rMod.getSpell().getSpell().getPage()+")";
		}

		return head;
	}

	//-------------------------------------------------------------------
	public static String getEnhancementString(EnhancementReference enhRef){
		logger.warn("getEnhancementString "+enhRef);
//		Enhancement data = enhRef.getEnhancement();

		StringBuffer buf = new StringBuffer();
		if (enhRef.getSpellValue()!=null)
			buf.append(enhRef.getSpellValue().getSpell().getName()+" ");
//		if (enhRef.getSkillSpecialization()!=null)
//			buf.append(enhRef.getSkillSpecialization().getName()+" ");

		Iterator<Modification> it = enhRef.getModifications().iterator();
		while (it.hasNext()) {
			buf.append(getModificationString(it.next()));
			if (it.hasNext())
				buf.append(", ");
		}

		return buf.toString();
	}

	//-------------------------------------------------------------------
	public static String getModificationString(Modification mod) {
		if (mod instanceof AttributeModification) {
			AttributeModification aMod = (AttributeModification)mod;
			if (aMod.getValue()<0)
				return aMod.getAttribute().getName()+" "+aMod.getValue();
			return aMod.getAttribute().getName()+" +"+aMod.getValue();
		}
		if (mod instanceof SkillModification) {
			SkillModification sMod = (SkillModification)mod;
			if (sMod.getSkill()==null) {
				// Restrict
				String restrict = "";
				if (sMod.getRestrictionType()==RestrictionType.MUST_EXIST) restrict = SplitterMondCore.getI18nResources().getString("restriction.must_exist");
				if (sMod.getRestrictionType()==RestrictionType.MUST_NOT_EXIST) restrict = SplitterMondCore.getI18nResources().getString("restriction.must_not_exist");
				// Type
				String type = SplitterMondCore.getI18nResources().getString("skilltype.any");
				if (sMod.getChoiceType()==SkillType.COMBAT) type = SplitterMondCore.getI18nResources().getString("skilltype.combat");
				if (sMod.getChoiceType()==SkillType.MAGIC)  type = SplitterMondCore.getI18nResources().getString("skilltype.magic");
				if (sMod.getChoiceType()==SkillType.NORMAL) type = SplitterMondCore.getI18nResources().getString("skilltype.normal");

				String value = "?";
				if (sMod.getType()==ModificationValueType.ABSOLUT) {
					value = " = "+sMod.getValue();
				} else {
					if (sMod.getValue()<0)
						value = String.valueOf(sMod.getValue());
					else
						value = " +"+sMod.getValue();
				}
				return String.format(SplitterMondCore.getI18nResources().getString("skillmod.format"), restrict, type, value);
			} else {
				if (sMod.getValue()<0)
					return sMod.getSkill().getName()+" "+sMod.getValue();
				return sMod.getSkill().getName()+" +"+sMod.getValue();
			}
			
		}
		if (mod instanceof PowerModification) {
			PowerModification pMod = (PowerModification)mod;
			return SplitterMondCore.getI18nResources().getString("label.power")
					+" "+pMod.getPower().getName();
		}
		if (mod instanceof CultureLoreModification) {
			CultureLoreModification clMod = (CultureLoreModification)mod;
			return SplitterMondCore.getI18nResources().getString("label.culturelore")
					+" "+clMod.getData().getName();
		}
		if (mod instanceof ResourceModification) {
			ResourceModification rMod = (ResourceModification)mod;
			return SplitterMondCore.getI18nResources().getString("label.resource")
					+" "+rMod.getResource().getName();
		}
		if (mod instanceof SpellModification) {
			SpellModification sMod = (SpellModification)mod;
			if (sMod.getSpell()!=null) {
				return String.format(SplitterMondCore.getI18nResources().getString("spellmodification.normal"), sMod.getSpell().getSpell().getName(), sMod.getSpell().getSkill().getName());
			} else {
				if (sMod.isRemove()) {
					return String.format(SplitterMondCore.getI18nResources().getString("spellmodification.remove"), sMod.getLevel());
				} else {
					return String.format(SplitterMondCore.getI18nResources().getString("spellmodification.add"), sMod.getLevel());
				}
			}
		}
		if (mod instanceof MastershipModification) {
			MastershipModification mMod = (MastershipModification)mod;
			Mastership master = mMod.getMastership();
			SkillSpecializationValue spec = mMod.getSpecialization();
			if (master!=null) {
				return SplitterMondCore.getI18nResources().getString("label.mastership")
						+" "+master.getSkill().getName()+"/"+master.getName();
			} else if (spec!=null) {
				return SplitterMondCore.getI18nResources().getString("label.specialization")
						+" "+spec.getSpecial().getSkill().getName()+"/"+spec.getSpecial().getName();
			} else {
				if (mMod.getSkill()!=null)
					return String.format(SplitterMondCore.getI18nResources().getString("mastermod.anystring.skill"), mMod.getLevel(), mMod.getSkill().getName());
				if (mMod.getSkillType()!=null)
					return String.format(SplitterMondCore.getI18nResources().getString("mastermod.anystring.skilltype"), mMod.getLevel(), mMod.getSkillType().getName());
//				logger.error("Should not get here: master="+master+"  spec="+spec+"  mMod="+mMod);
				return mMod.toString();
			}
		}
		if (mod instanceof ItemModification) {
			ItemModification iMod = (ItemModification)mod;
			if (iMod.getValue()<0)
				return iMod.getAttribute().getName()+iMod.getValue();
			else
				return iMod.getAttribute().getName()+"+"+iMod.getValue();
		}
		if (mod instanceof FeatureModification) {
			FeatureModification fMod = (FeatureModification)mod;
			return "+"+fMod.getFeature().getName()+" ("+fMod.getFeature().getProductNameShort()+" "+fMod.getFeature().getPage()+")";
		}

		if (mod instanceof MoneyModification) {
			MoneyModification mMod = (MoneyModification)mod;
			int sol = mMod.getTelare()/10000;
			int lun = (mMod.getTelare()/100)%100;
			int tel = mMod.getTelare()%100;
			StringBuffer buf = new StringBuffer();
			if (sol!=0)
				buf.append(sol+CORE.getString("currency.solare.short")+"  ");
			if (lun!=0)
				buf.append(lun+CORE.getString("currency.lunare.short")+"  ");
			if (tel!=0)
				buf.append(tel+CORE.getString("currency.telare.short")+"  ");
			return buf.toString();
		}

		if (mod instanceof CreatureFeatureModification) {
			CreatureFeatureModification fMod = (CreatureFeatureModification)mod;
			String name = fMod.getFeature().getName();
			if (fMod.getDamageType()!=null) {
				name = fMod.getFeature().getName(fMod.getDamageType());
			}
			if (fMod.isRemoved())
				return String.format(SplitterMondCore.getI18nResources().getString("creaturefeaturemod.format.remove"), name, fMod.getFeature().getProductNameShort(), fMod.getFeature().getPage());
			return String.format(SplitterMondCore.getI18nResources().getString("creaturefeaturemod.format.add"), name, fMod.getFeature().getProductNameShort(), fMod.getFeature().getPage());
		}

		if (mod instanceof ModificationChoice) {
			ModificationChoice fMod = (ModificationChoice)mod;
			List<String> list = new ArrayList<>();
			fMod.getOptionList().forEach(mod2 -> list.add(getModificationString(mod2)));
			return String.join(" "+SplitterMondCore.getI18nResources().getString("label.choiceOR")+" ", list);
		}

		if (mod instanceof SubModificationChoice) {
			SubModificationChoice fMod = (SubModificationChoice)mod;
			List<String> list = new ArrayList<>();
			fMod.getOptionList().forEach(mod2 -> list.add(getModificationString(mod2)));
			return "("+String.join(" "+SplitterMondCore.getI18nResources().getString("label.choiceOR")+" ", list)+")";
		}

		if (mod instanceof AllOfModification) {
			AllOfModification fMod = (AllOfModification)mod;
			List<String> list = new ArrayList<>();
			fMod.forEach(mod2 -> list.add(getModificationString(mod2)));
			return String.join(" "+SplitterMondCore.getI18nResources().getString("label.choiceAND")+" ", list);
		}

		if (mod instanceof CountModification) {
			CountModification foo = (CountModification)mod;
			return ((foo.getCount()<0)?String.valueOf(foo.getCount()):("+"+foo.getCount()))+" "+foo.getType().getName();
		}

		if (mod instanceof DamageModification) {
			DamageModification foo = (DamageModification)mod;
			StringBuffer buf = new StringBuffer();
			buf.append(foo.isSubstract()?"-":"+");
			if (foo.getDamage()!=0)
				buf.append( (new WeaponDamageConverter()).write(foo.getDamage()));
			buf.append(" "+SplitterMondCore.getI18nResources().getString("label.damage"));
			if (foo.getType()!=null)
				buf.append(" ("+foo.getType().getName()+")");
			return buf.toString();
		}
		if (mod instanceof ItemFeatureModification) {
			ItemFeatureModification fMod = (ItemFeatureModification)mod;
			return SplitterMondCore.getI18nResources().getString("label.feature")+" "+fMod.getFeature().getName();
		}
		if (mod instanceof ConditionalModification) {
			ConditionalModification cMod = (ConditionalModification)mod;
			List<String> list = new ArrayList<>();
			cMod.getTrueModification().forEach(mod2 -> list.add(getModificationString(mod2)));
			return String.join(" "+SplitterMondCore.getI18nResources().getString("label.choiceOR")+" ", list);
		}

		logger.error("Missing string conversion for "+mod.getClass());
		return mod.toString();
	}

	//-------------------------------------------------------------------
	public static String getRequirementString(Requirement req) {
		if (req instanceof AttributeRequirement) {
			AttributeRequirement tmp = (AttributeRequirement)req;
			return tmp.getAttribute().getName()+" "+tmp.getValue();
		}
		if (req instanceof CreatureModuleRequirement) {
			CreatureModuleRequirement tmp = (CreatureModuleRequirement)req;
			if (tmp.isNegated())
				return SplitterMondCore.getI18nResources().getString("label.negation1")+" "+tmp.getModule().getName();
			return tmp.getModule().getName();
		}
		if (req instanceof CreatureTypeRequirement) {
			CreatureTypeRequirement tmp = (CreatureTypeRequirement)req;
			if (tmp.isNegated())
				return SplitterMondCore.getI18nResources().getString("label.negation1")+" "+tmp.getType().getName();
			return tmp.getType().getName();
		}
		if (req instanceof ItemFeatureRequirement) {
			ItemFeatureRequirement tmp = (ItemFeatureRequirement)req;
			if (tmp.isNegated())
				return SplitterMondCore.getI18nResources().getString("label.negation1")+" "+tmp.getFeature().getName();
			return tmp.getFeature().getName();
		}
		if (req instanceof MastershipRequirement) {
			MastershipRequirement tmp = (MastershipRequirement)req;
			return SplitterMondCore.getI18nResources().getString("label.character.short")+" "+tmp.getMastership().getName();
		}
		if (req instanceof SkillRequirement) {
			SkillRequirement tmp = (SkillRequirement)req;
			if (tmp.getValue()<=1)
				return tmp.getSkill().getName();
			return tmp.getSkill().getName()+" "+tmp.getValue();
		}
		if (req instanceof AnyRequirement) {
			AnyRequirement tmp = (AnyRequirement)req;
			List<String> ret = new ArrayList<>();
			for (Requirement req2 : tmp.getOptionList()) {
				ret.add(getRequirementString(req2));
			}
			return String.join(" "+SplitterMondCore.getI18nResources().getString("label.choiceOR"), ret);
		}
		if (req instanceof CreatureFeatureRequirement) {
			CreatureFeatureRequirement tmp = (CreatureFeatureRequirement)req;
			if (tmp.isNegated())
				return SplitterMondCore.getI18nResources().getString("label.negation1")+" "+tmp.getFeature().getName();
			return tmp.getFeature().getName();
		}

		logger.error("Missing string conversion for "+req.getClass());
		return req.toString();
	}		

	//-------------------------------------------------------------------
	private static void sort(List<Datable> rewardsAndMods) {
		Collections.sort(rewardsAndMods, new Comparator<Datable>() {
			public int compare(Datable o1, Datable o2) {
				Long time1 = 0L;
				Long time2 = 0L;
				if (o1.getDate()!=null)	time1 = o1.getDate().getTime();
				if (o2.getDate()!=null)	time2 = o2.getDate().getTime();

				int cmp = time1.compareTo(time2);
				if (cmp==0) {
					if (o1 instanceof RewardImpl && o2 instanceof ModificationImpl) return -1;
					if (o1 instanceof ModificationImpl && o2 instanceof RewardImpl) return  1;
				}
				return cmp;
			}
		});
	}

	//-------------------------------------------------------------------
	/**
	 * @param aggregate Aggregate history elements with same adventure
	 */
	public static List<HistoryElementImpl> convertToHistoryElementList(SpliMoCharacter charac, boolean aggregate) {
		// Initial reward
		logger.warn("Sort "+charac.getRewards().size()+" rewards  and "+charac.getHistory().size()+" mods");

		/*
		 * Build a merged list of rewards and modifications and sort it by time
		 */
		List<Datable> rewardsAndMods = new ArrayList<Datable>();
		rewardsAndMods.addAll(charac.getRewards());
		for (Modification mod : charac.getHistory()) {
			rewardsAndMods.add(mod.clone());
		}
//		rewardsAndMods.addAll(charac.getHistory());
		sort(rewardsAndMods);

		/*
		 * Does the creation reward need to be added?
		 */
		if (rewardsAndMods.isEmpty() || !(rewardsAndMods.get(0) instanceof Reward)) {
			RewardImpl creation = new RewardImpl(15, CORE.getString("label.reward.creation"));
			rewardsAndMods.add(creation);
			sort(rewardsAndMods);
		}

		/*
		 * Now build a list of HistoryElements. Start a new H
		 */
		List<HistoryElementImpl> ret = new ArrayList<HistoryElementImpl>();
		HistoryElementImpl current = null;
		ProductService sessServ = RPGFrameworkLoader.getInstance().getProductService();

		for (Datable item : rewardsAndMods) {
			if (item instanceof RewardImpl) {
				RewardImpl reward = (RewardImpl)item;
				Adventure adv = null;
				System.err.println("Reward "+reward+" / "+reward.getTitle());
				if (reward.getId()!=null) {
					adv = sessServ.getAdventure(RoleplayingSystem.SPLITTERMOND, reward.getId());
					if (adv==null) {
						logger.warn("Rewards of character '"+charac.getName()+"' reference an unknown adventure: "+reward.getId());
					}
				}
				// If is same adventure as current, keep same history element
				if (!aggregate || !(adv!=null && current!=null && adv==current.getAdventure()) ) {
					current = new HistoryElementImpl();
					current.setName(reward.getTitle());
					if (adv!=null) {
						current.setName(adv.getTitle());
						current.setAdventure(adv);
					}
					ret.add(current);
				}
				current.addGained(reward);
			} else if (item instanceof ModificationImpl) {
				if (current==null) {
					logger.error("Failed preparing history: Exp spent on modification without previous reward");
				} else {
					Modification lastMod = (current.getSpent().isEmpty())?null:current.getSpent().get(current.getSpent().size()-1);
					if (lastMod!=null && lastMod.getClass()==item.getClass()) {
						if (item instanceof SkillModification) {
//							logger.debug("Combine "+lastMod+" with "+item);
							// Aggregate same skill
							SkillModification lastSMod = (SkillModification)lastMod;
							SkillModification newtMod = (SkillModification)item;
							if (lastSMod.getSkill()==newtMod.getSkill()) {
								// Same skill
								lastSMod.setValue(newtMod.getValue());
								lastSMod.setExpCost(lastSMod.getExpCost() + newtMod.getExpCost());
							} else {
								// Different skill
								current.addSpent((ModificationImpl) item);
							}
						} else {
							current.addSpent((ModificationImpl) item);
						}
					} else {
						current.addSpent((ModificationImpl) item);
					}
				}
			}
		}


		logger.warn("  return "+ret.size()+" elements");
		return ret;
	}


	//-------------------------------------------------------------------
	/**
	 * Convert spell cost to a printable string. Also takes into account
	 * modifications to the spell cost by spell school masterships.
	 */
	public static String getModifiedFocusString(SpliMoCharacter model, SpellValue spellVal) {
		Spell spell = spellVal.getSpell();
		SpellCost cost = spell.getCost();

		int channelled = cost.getChannelled(); // Kanalisiert
		int exhausted  = cost.getExhausted();  // Erschöpft
		int consumed   = cost.getConsumed();   // Verzehrt

		/*
		 * Modifications
		 */
		List<String> modifications = new ArrayList<>();
		for (Mastership master : spellVal.getSkill().getMasterships()) {
			if (model.hasMastership(master))
				modifications.add(master.getKey());
		}
//		logger.warn("***Spell "+spell.getName()+" mods="+modifications);
		// Check influential modifications
		if (modifications.contains("savingcaster")) {
			exhausted  = (exhausted >1)?(exhausted -1):exhausted;
			channelled = (channelled>1)?(channelled-1):channelled;
		}
		if (modifications.contains("wardinghand")) {  // Bannende Hand
//			logger.warn("*** warding = "+spell.getTypes());
			if (spell.getTypes().contains(SpellType.BREAKSPELL))
				exhausted   = Math.max(1, exhausted-2);
		}
		if (modifications.contains("effectivemotion")) { // Effektive Bewegung
//			logger.warn("*** effectivemotion = "+spell.getTypes());
			if (spell.getTypes().contains(SpellType.ENHANCE_MOTION))
				channelled   = Math.max(1, channelled-2);
		}

		String chan = SplitterMondCore.getI18nResources().getString("spell.cost.channelled.short");
		String cons = SplitterMondCore.getI18nResources().getString("spell.cost.consumed.short");
		StringBuffer buf = new StringBuffer();
		if (channelled>0)
			buf.append(chan+channelled);
		else
			buf.append(String.valueOf(exhausted));

		if (consumed>0)
			buf.append(cons+consumed);

		return buf.toString();

	}

	//-------------------------------------------------------------------
	/**
	 * Convert spell cost to a printable string.
	 */
	public static String getFocusString(SpellCost cost) {
		int channelled = cost.getChannelled(); // Kanalisiert
		int exhausted  = cost.getExhausted();  // Erschöpft
		int consumed   = cost.getConsumed();   // Verzehrt

		String chan = SplitterMondCore.getI18nResources().getString("spell.cost.channelled.short");
		String cons = SplitterMondCore.getI18nResources().getString("spell.cost.consumed.short");
		StringBuffer buf = new StringBuffer();
		if (channelled>0)
			buf.append(chan+channelled);
		else
			buf.append(String.valueOf(exhausted));

		if (consumed>0)
			buf.append(cons+consumed);

		return buf.toString();

	}

	//-------------------------------------------------------------------
	/**
	 * Iterate through enhancements. Get their modifications. Apply those that
	 * refer to the item itself. Return those that needs to be applied to the
	 * character
	 */
	public static void applyToCharacter(SpliMoCharacter model, List<Modification> modifications, Skill skill, SkillSpecialization spec) {
		logger.debug("Apply "+modifications.size()+" modifications to character");
		for (Modification mod : modifications) {
			if (mod instanceof AttributeModification) {
				AttributeModification data = (AttributeModification)mod;
				model.getAttribute(data.getAttribute()).addModification(mod);
				logger.debug("Added "+data.getAttribute()+" +"+data.getValue()+"  ... result now "+model.getAttribute(data.getAttribute()));
			} else if (mod instanceof SkillModification) {
				SkillModification data = (SkillModification)mod;
				model.getSkillValue(data.getSkill()).addModification(mod);
				logger.debug("Added "+data.getSkill()+" "+data.getValue());
			} else if (mod instanceof MastershipModification) {
				MastershipModification data = (MastershipModification)mod;
				if (data.getSpecialization()!=null) {
					SkillSpecializationValue special = data.getSpecialization();
					model.getSkillValue(data.getSkill()).addModification(mod);
					logger.debug("Added "+data.getSkill()+" "+special.getLevel());
				} else if (data.getMastership()==null) {
					// No specialization, no mastership - must be associated with the skill of the item itself
					logger.warn("Character has an invalid saved Mastership modification. Try to fix it");
					data.setSpecialization(new SkillSpecializationValue(spec, data.getLevel()));
					logger.warn("Added specializatation  "+model.getSkillValue(skill));
				} else {
					logger.warn("Don't know how to apply "+data);
					throw new RuntimeException("Trace");
				}
			} else if (mod instanceof ItemModification) {
				// Can be ignored here
			} else if (mod instanceof AttributeChangeModification) {
				// Can be ignored here
			} else
				logger.warn("Don't know how to apply to character: "+mod.getClass());
		}
	}

	//-------------------------------------------------------------------
	/**
	 * Iterate through enhancements. Get their modifications. Apply those that
	 * refer to the item itself. Return those that needs to be applied to the
	 * character
	 */
	public static void unapplyToCharacter(SpliMoCharacter model, List<Modification> modifications) {
		logger.debug("unapply "+modifications.size()+" modifications from character");
		for (Modification mod : modifications) {
			if (mod instanceof AttributeModification) {
				AttributeModification data = (AttributeModification)mod;
				model.getAttribute(data.getAttribute()).removeModification(mod);
				logger.debug("Removed "+data.getAttribute()+" +"+data.getValue());
			} else if (mod instanceof SkillModification) {
				SkillModification data = (SkillModification)mod;
				model.getSkillValue(data.getSkill()).removeModification(mod);
				logger.debug("Removed "+data.getSkill()+" "+data.getValue());
			} else
				logger.warn("Don't know how to apply to character: "+mod.getClass());
		}
	}

	//-------------------------------------------------------------------
	public static void convertFromV1Chars(CarriedItem item) {
		logger.debug("Update V1 item modifications to enhancements for "+item.getName());
		/*
		 * Apply Genesis 1.x Magic effects and convert them to enhancements
		 */
		logger.debug("  Mods of "+item+" = "+item.getModifications());
		for (Modification mod : new ArrayList<Modification>(item.getModifications())) {
			if (mod instanceof AttributeModification) {
				AttributeModification aMod = (AttributeModification)mod;
				logger.warn("Found an attribute modification "+aMod+" -> convert to enhancement");
				Attribute attrib = aMod.getAttribute();
				if (attrib==null) {
					logger.error(String.format("Character has item %s with invalid attribute modification", item.getItem().getID()));
					continue;
				}
				// Convert to enhancement
				EnhancementReference enh = null;
				switch (aMod.getAttribute()) {
				case MINDRESIST:
					enh = new EnhancementReference(SplitterMondCore.getEnhancement("mindresist"));
					logger.info("Convert "+aMod+" to "+enh.getName());
					break;
				case BODYRESIST:
					enh = new EnhancementReference(SplitterMondCore.getEnhancement("bodyresist"));
					logger.info("Convert "+aMod+" to "+enh.getName());
					break;
				case CHARISMA:	case AGILITY:  case INTUITION: case CONSTITUTION:
				case MYSTIC  :  case STRENGTH: case MIND:  case WILLPOWER:
					enh = new EnhancementReference(SplitterMondCore.getEnhancement("raiseattrib"));
					logger.info("Convert "+aMod+" to "+enh.getName());
					break;
				default:
					logger.error("Cannot convert attribute modification "+aMod+" to enhancement");
					continue;
				}
				item.addEnhancement(enh);
				item.removeModification(mod);
			}
		}

	}

	//-------------------------------------------------------------------
	public static void reward(SpliMoCharacter charac, RewardImpl reward) {
		logger.info("Add reward "+reward+" to "+charac);
		charac.addReward(reward);

		charac.setExperienceFree(charac.getExperienceFree() + reward.getExperiencePoints());
		for (Modification mod : reward.getModifications()) {
			if (mod instanceof ResourceModification) {
				Resource res = ((ResourceModification)mod).getResource();
				int      val = ((ResourceModification)mod).getValue();
				String title = ((ResourceModification)mod).getResourceName();

				if (res.isBaseResource()) {
					for (ResourceReference ref : charac.getResources()) {
						if (ref.getResource()==res) {
							ref.setValue(ref.getValue() + val);
							logger.info("Reward existing resource to "+ref);
							ref.setDescription(title);
							break;
						}
					}
				} else {
					ResourceReference ref = new ResourceReference(res, val);
					ref.setDescription(title);
					logger.info("Reward new resource "+ref);
					charac.addResource(ref);
				}
				// Add to history
				charac.addToHistory(mod);
			} else if (mod instanceof MoneyModification) {
				int value = ((MoneyModification)mod).getTelare();
				logger.debug("Add "+value+" telare");
				charac.setTelare(charac.getTelare() + value);
			} else {
				logger.error("Unsupported modification: "+mod.getClass());
			}
		}

	}

	//-------------------------------------------------------------------
	public static void calculateDerivedAttribute(SpliMoCharacter model) {
		model.setAttribute(Attribute.SPEED     , model.getAttribute(Attribute.SIZE).getValue() + model.getAttribute(Attribute.AGILITY).getValue());
		model.setAttribute(Attribute.INITIATIVE,                  10 - model.getAttribute(Attribute.INTUITION).getValue());
		model.setAttribute(Attribute.LIFE      , model.getAttribute(Attribute.SIZE).getValue() + model.getAttribute(Attribute.CONSTITUTION).getValue());
		model.setAttribute(Attribute.FOCUS     , 2*(model.getAttribute(Attribute.MYSTIC).getValue() + model.getAttribute(Attribute.WILLPOWER).getValue()) );
		model.setAttribute(Attribute.DEFENSE   , 12 + model.getAttribute(Attribute.AGILITY).getValue() + model.getAttribute(Attribute.STRENGTH).getValue());
		model.setAttribute(Attribute.MINDRESIST, 12 + model.getAttribute(Attribute.MIND).getValue() + model.getAttribute(Attribute.WILLPOWER).getValue());
		model.setAttribute(Attribute.BODYRESIST, 12 + model.getAttribute(Attribute.CONSTITUTION).getValue() + model.getAttribute(Attribute.WILLPOWER).getValue());
	}

	//-------------------------------------------------------------------
	private static void setMaxBonusCap(SpliMoCharacter data, int cap) {
		logger.info("Set cap for equipment and magic bonus to "+cap);

		// Apply to derived attribute
		for (Attribute key : Attribute.secondaryValues()) {
			data.getAttribute(key).setModifierCap(cap);
		}

		// Apply to skills
		for (Skill key : SplitterMondCore.getSkills()) {
			data.getSkillValue(key).setModifierCap(cap);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * Apply those modifications not saved in the character - e.g. the
	 * resistance boni depending on level
	 */
	public static void applyAfterLoadModifications(SpliMoCharacter data) {
		logger.info("calculate modifications by level, size, race, powers, etc.");

		/*
		 * V1 versions saved the size as an attribute. Since V2 it is derived from
		 * the raze
		 */
		data.getAttribute(Attribute.SIZE).setDistributed(0);
		data.getAttribute(Attribute.SPLINTER).setDistributed(3);

		int cap = 3;

		/*
		 * Calculate level and add resistances depending on it
		 */
		// Level 2
		if (data.getExperienceInvested()>=100) {
			data.setLevel(2);
			data.getAttribute(Attribute.SPLINTER  ).addModification( new AttributeModification(Attribute.SPLINTER  , 1, SplitterTools.LEVEL2) );
			data.getAttribute(Attribute.BODYRESIST).addModification( new AttributeModification(Attribute.BODYRESIST, 2, SplitterTools.LEVEL2) );
			data.getAttribute(Attribute.MINDRESIST).addModification( new AttributeModification(Attribute.MINDRESIST, 2, SplitterTools.LEVEL2) );
			data.getAttribute(Attribute.DEFENSE   ).addModification( new AttributeModification(Attribute.DEFENSE   , 2, SplitterTools.LEVEL2) );
			cap = 4;
		}
		// Level 3
		if (data.getExperienceInvested()>=300) {
			data.setLevel(3);
			data.getAttribute(Attribute.SPLINTER  ).addModification( new AttributeModification(Attribute.SPLINTER  , 1, SplitterTools.LEVEL3) );
			data.getAttribute(Attribute.BODYRESIST).addModification( new AttributeModification(Attribute.BODYRESIST, 2, SplitterTools.LEVEL3) );
			data.getAttribute(Attribute.MINDRESIST).addModification( new AttributeModification(Attribute.MINDRESIST, 2, SplitterTools.LEVEL3) );
			data.getAttribute(Attribute.DEFENSE   ).addModification( new AttributeModification(Attribute.DEFENSE   , 2, SplitterTools.LEVEL3) );
			cap = 5;
		}
		// Level 4
		if (data.getExperienceInvested()>=600) {
			data.setLevel(4);
			data.getAttribute(Attribute.SPLINTER  ).addModification( new AttributeModification(Attribute.SPLINTER  , 1, SplitterTools.LEVEL4) );
			data.getAttribute(Attribute.BODYRESIST).addModification( new AttributeModification(Attribute.BODYRESIST, 2, SplitterTools.LEVEL4) );
			data.getAttribute(Attribute.MINDRESIST).addModification( new AttributeModification(Attribute.MINDRESIST, 2, SplitterTools.LEVEL4) );
			data.getAttribute(Attribute.DEFENSE   ).addModification( new AttributeModification(Attribute.DEFENSE   , 2, SplitterTools.LEVEL4) );
			cap = 6;
		}

		/*
		 * Has character power that raises cap
		 */
		if (data.getPower(SplitterMondCore.getPower("onestepahead"))!=null)
			cap++;
		// Set cap
		setMaxBonusCap(data, cap);

		/*
		 * Size depending on race
		 */
		for (Modification mod : data.getRace().getModifications()) {
			if (mod instanceof AttributeModification)  {
				AttributeModification aMod = (AttributeModification)mod;
				if (aMod.getAttribute()==Attribute.SIZE)
					data.getAttribute(Attribute.SIZE).addModification( new AttributeModification(Attribute.SIZE, aMod.getValue(), data.getRace()) );
			}
		}

		/*
		 * Powers
		 */
		for (PowerReference powerRef : data.getPowers()) {
			logger.info("PowerRef "+powerRef);
			Power power = powerRef.getPower();
			for (Modification mod : power.getModifications()) {
				mod.setSource(power);
				if (mod instanceof AttributeModification) {
					AttributeModification aMod = (AttributeModification)mod;
					AttributeModification cloned = aMod.clone();
					AttributeValue aVal = data.getAttribute(aMod.getAttribute());
					cloned.setValue(powerRef.getCount()*aMod.getValue());
					cloned.setSource(powerRef);
					logger.info("Add "+cloned+"  (already exist = "+aVal.getModifications()+")");
					aVal.addModification(cloned);
					powerRef.getModifications().add(cloned);
				} else if (mod instanceof SkillModification) {
					SkillModification sMod = (SkillModification)mod;
					SkillModification cloned = sMod.clone();
					SkillValue sVal = data.getSkillValue(sMod.getSkill());
					cloned.setValue(powerRef.getCount()*sMod.getValue());
					cloned.setSource(powerRef);
					logger.info("Add "+cloned+"  (already exist = "+sVal.getModifications()+")");
					sVal.addModification(cloned);
					logger.info("... sval="+sVal);
					powerRef.getModifications().add(cloned);
				} else {
					logger.warn("Unsupported modification "+mod+" in power "+power);
				}
			}
		}

		/*
		 * Masterships
		 */
		for (Skill skill : SplitterMondCore.getSkills()) {
			SkillValue sVal = data.getSkillValue(skill);
			for (MastershipReference ref : sVal.getMasterships()) {
				// Depending on masterships or skill specialization
				if (ref.getMastership()!=null) {
					for (Modification mod : ref.getMastership().getModifications()) {
						mod.setSource(ref.getMastership());
						if (mod instanceof AttributeModification) {
							AttributeModification aMod = (AttributeModification)mod;
							AttributeValue aVal = data.getAttribute(aMod.getAttribute());
							aMod.setValue(aMod.getValue());
							aMod.setSource(ref.getMastership());
							logger.info("Add "+aMod+"  (already exist = "+aVal.getModifications()+")");
							aVal.addModification(aMod);
						} else if (mod instanceof AttitudeModification) {
						} else if (mod instanceof SkillModification) {
							SkillModification sMod = (SkillModification)mod;
							if (sMod.getSource()==null)
								sMod.setSource(ref.getMastership());
							SkillValue sVal2 = data.getSkillValue(sMod.getSkill());
							logger.info("Add "+sMod+"  (already exist = "+sVal2.getModifications()+")");
							sVal2.addModification(sMod);
						} else {
							logger.warn("Unsupported modification "+mod+"//"+mod.getClass()+" in mastership "+ref.getMastership());
						}
					}
				} else if (ref.getSpecialization()!=null) {
					// Specializations are taken into account only in specific situations
				}
			}
		}


		/*
		 * Modifications depending on size
		 */
		int diff = 5 - data.getAttribute(Attribute.SIZE).getValue();
		if (diff!=0) {
			data.getAttribute(Attribute.DEFENSE).addModification( new AttributeModification(Attribute.DEFENSE, diff*2, Attribute.SIZE) );
			data.getSkillValue(SplitterMondCore.getSkill("stealth")).addModification(new SkillModification("stealth", diff, Attribute.SIZE));
		}


		/*
		 * Calculate derived attributes
		 */
		calculateDerivedAttribute(data);

		/*
		 * Mark items that are relics
		 */
		for (ResourceReference ref : data.getResources()) {
			if (!ref.getResource().getId().equals("relic"))
				continue;
			UUID uniqueID = ref.getIdReference();
			CarriedItem item = (CarriedItem)data.getUniqueObject(uniqueID);
			if (item==null) {
				logger.warn("Resource "+ref+" refers to an unknown unique ID");
			} else {
				item.setResource(ref);
				logger.debug("Mark "+item+" as an relic");
			}
		}

		/*
		 * Mark creatures that are resources
		 */
		for (ResourceReference ref : data.getResources()) {
			if (!ref.getResource().getId().equals("creature"))
				continue;
			UUID uniqueID = ref.getIdReference();
			CreatureReference item = (CreatureReference)data.getUniqueObject(uniqueID);
			if (item==null) {
				logger.warn("Resource "+ref+" refers to an unknown unique ID");
			} else {
				item.setResource(ref);
				logger.debug("Mark "+item+" as an creature");
			}
		}
		
		/*
		 * Calculate ModuleBasedCreatures
		 */
		for (CreatureReference ref : data.getCreatures()) {
			if (ref.getModuleBasedCreature()!=null) {
				logger.info("  calculate creature "+ref+" from modules");
				// Fix originModule
				for (NecessaryChoice choice : ref.getModuleBasedCreature().getRole().getChoices()) 
					choice.originModule = ref.getModuleBasedCreature().getRole();
				for (CreatureModuleReference origin : ref.getModuleBasedCreature().getOptions()) {
					for (Modification mod : origin.getModule().getModifications()) {
						CreatureTools.instantiateModification(ref.getModuleBasedCreature(), origin, mod);
					}
					logger.debug("  ..."+origin);
					for (NecessaryChoice choice : origin.getChoices()) {
						choice.originModule = origin;
					}
				}
				
				logger.info("  START: Calculate values for "+ref);
				CreatureTools.calculateModuleBasedCreature(ref.getModuleBasedCreature());
				logger.info("  STOP : Calculate values for "+ref);
//				System.exit(0);
			}
			CreatureTools.calculateTrainings(ref);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @param total Value measured in Silver(Telare)
	 * @return
	 */
	public static String telareAsCurrencyString(int total) {
		int solare = total/10000;
		int lunare = (total/100)%100;
		int telare = total%100;
		StringBuffer ret = new StringBuffer();
		if (solare>0)
			ret.append(solare+CORE.getString("currency.solare.short"));
		ret.append(" ");
		if (lunare>0)
			ret.append(lunare+CORE.getString("currency.lunare.short"));
		ret.append(" ");
		if (telare>0)
			ret.append(telare+CORE.getString("currency.telare.short"));

		return ret.toString().trim();
	}

	//-------------------------------------------------------------------
	/**
	 * Since distributed points may not be saved in the creature xml,
	 * calculate them from value minus attributes
	 */
	public static void fixCreatureSkillPoints(Creature model) {
		for (SkillValue val : model.getSkills()) {
			if (val.getSkill().getType()==SkillType.COMBAT)
				continue;
			Skill skill = val.getSkill();
			int attrVal = model.getAttribute(skill.getAttribute1()).getValue() + model.getAttribute(skill.getAttribute2()).getValue();
			int points  = val.getValue() - attrVal;
			val.setValue(points);
		}
	}


	//-------------------------------------------------------------------
	public static int getModifiedWeaponSpeed(SpliMoCharacter model, CarriedItem item) {
		int speed = 0;
		if (item.isType(ItemType.WEAPON))
			speed = item.getSpeed(ItemType.WEAPON);
		else if (item.isType(ItemType.LONG_RANGE_WEAPON))
			speed = item.getSpeed(ItemType.LONG_RANGE_WEAPON);

		speed += getTickMalusSum(model, true);
		return speed;
	}

	//-------------------------------------------------------------------
	public static int getDefenseSum(SpliMoCharacter character, boolean inclusiveShield) {
		return getSumForArmorAttribute(character, ArmorAttributeType.DEFENSE, inclusiveShield);
	}

	//-------------------------------------------------------------------
	public static int getDamageReductionSum(SpliMoCharacter character) {
		int base = character.getAttribute(Attribute.DAMAGE_REDUCTION).getValue();
		return  base + getSumForArmorAttribute(character, ArmorAttributeType.DAMAGE_REDUCTION, false);
	}

	//-------------------------------------------------------------------
	public static int getHandicapSum(SpliMoCharacter character, boolean inclusiveShield) {
		return   getSumForArmorAttribute(character, ArmorAttributeType.HANDICAP, inclusiveShield);
	}

	//-------------------------------------------------------------------
	public static int getTickMalusSum(SpliMoCharacter character, boolean inclusiveShield) {
		return   getSumForArmorAttribute(character, ArmorAttributeType.TICKMALUS, inclusiveShield);
	}

	//-------------------------------------------------------------------
	private static int getSumForArmorAttribute(SpliMoCharacter character, ArmorAttributeType type, boolean withShield){

		List<CarriedItem> items = character.getItems();
		int sum = 0;
		int sumWithShield = 0;
		for (CarriedItem carriedItem : items) {
			if (carriedItem.isType(ItemType.ARMOR)) {
				if (ItemLocationType.BODY.equals(carriedItem.getLocation())) {
					switch(type) {
						case DEFENSE:
							sum += carriedItem.getDefense(ItemType.ARMOR);
							break;
						case DAMAGE_REDUCTION:
							sum += carriedItem.getDamageReduction(ItemType.ARMOR);
							break;
						case HANDICAP:
							sum += carriedItem.getHandicap(ItemType.ARMOR);
							break;
						case TICKMALUS:
							sum += carriedItem.getTickMalus(ItemType.ARMOR);
							break;
					}
				}
			}
			if (withShield && carriedItem.isType(ItemType.SHIELD)) {
				if (ItemLocationType.BODY.equals(carriedItem.getLocation())){
					switch(type) {
						case DEFENSE:
							sumWithShield += carriedItem.getDefense(ItemType.SHIELD);
							break;
						case HANDICAP:
							sumWithShield += carriedItem.getHandicap(ItemType.SHIELD);
							break;
						case TICKMALUS:
							sumWithShield += carriedItem.getTickMalus(ItemType.SHIELD);
							break;
						case DAMAGE_REDUCTION:
							// does not apply for shields
							break;
					}
				}
			}
		}

		// check Meisterschaften in Zähigkeit
		Skill endurance = SplitterMondCore.getSkill("endurance");
		List<MastershipReference> masterships = character.getSkillValue(endurance).getMasterships();
		for (MastershipReference mastership : masterships) {
			if (mastership.getMastership() != null) {
				switch (type) {
					case TICKMALUS:
						if (sum > 0
								&& mastership.getMastership().getKey().equals("armour2")) {
							sum -= 1;
						}
						if (withShield
								&& sumWithShield > 0
								&& mastership.getMastership().getKey().equals("shield2")) {
							sumWithShield -= 1;
						}
					case HANDICAP:
						if (sum > 0
								&& mastership.getMastership().getKey().equals("armour1")) {
							sum -= 1;
						}
						if (withShield
								&& sumWithShield > 0
								&& mastership.getMastership().getKey().equals("shield1")) {
							sumWithShield -= 1;
						}
					default:
				}
			}
		}


		if (withShield) {
			return sum + sumWithShield;
		} else {
			return sum;
		}
	}

	private enum ArmorAttributeType{
		DEFENSE,
		DAMAGE_REDUCTION,
		HANDICAP,
		TICKMALUS
	}

	//--------------------------------------------------------------------
	public static String getWeaponDamageString(int damage) {
		try {
			return new WeaponDamageAdapter().marshal(damage);
		} catch (Exception e) {
			return "";
		}
	}

	//--------------------------------------------------------------------
	public static int parseWeaponDamageString(String damage) {
		try {
			return new WeaponDamageAdapter().unmarshal(damage);
		} catch (Exception e) {
			return 0;
		}
	}

	//--------------------------------------------------------------------
	public static String encode(String toEncode) {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<toEncode.length(); i++) {
			char c = toEncode.charAt(i);
			switch (c) {
			case '<': buf.append("&lt;"); break;
			case '>': buf.append("&gt;"); break;
			case '"': buf.append("&quot;"); break;
			case '&': buf.append("&amp;"); break;
			default:
				buf.append(c);
			}
		}
		return buf.toString();
	}

	//--------------------------------------------------------------------
	public static String decode(String toDecode) {
		StringBuffer buf = new StringBuffer();
		int pos=0;
		while (pos<toDecode.length()) {
			String sub = toDecode.substring(pos);
			if (sub.startsWith("&lt;")) {
				buf.append("<");
				pos+=4;
			} else if (sub.startsWith("&gt;")) {
				buf.append(">");
				pos+=4;
			} else if (sub.startsWith("&quot;")) {
				buf.append('"');
				pos+=5;
			} else if (sub.startsWith("&amp;")) {
				buf.append('&');
				pos+=5;
			} else {
				buf.append(toDecode.charAt(pos));
				pos++;
			}

		}
		return buf.toString();
	}

	//-------------------------------------------------------------------
	public static List<String> getGMRelevantPowers(SpliMoCharacter model) {
		List<String> ret = new ArrayList<>();
		Iterator<PowerReference> itPow = model.getPowers().iterator();
		while (itPow.hasNext()) {
			PowerReference ref = itPow.next();
			if (ref.getPower().getId().equals("addsplinter"))
				continue;
			if (ref.getPower().getId().equals("focuspool"))
				continue;
			if (ref.getPower().getId().equals("sturdy"))
				continue;
			if (ref.getPower().getId().equals("swift"))
				continue;
			String name = ref.getPower().getName();
			if (ref.getCount()>1)
				name += ""+ref.getCount();

			ret.add(name);
		}
		return ret;
	}

	//-------------------------------------------------------------------
	public static List<String> getGMRelevantItems(SpliMoCharacter model) {
		List<String> ret = new ArrayList<>();
		Iterator<CarriedItem> itItem = model.getItems().iterator();
		while (itItem.hasNext()) {
			CarriedItem ref = itItem.next();
			int quality = ref.getArtifactQuality() + ref.getItemQuality();
			if (quality==0)
				continue;
			String name = ref.getName();
			name+=" (";
			if (ref.isRelic())
				name+="R ";
			name += "Q"+quality+")";

			ret.add(name);
		}
		return ret;
	}

	//-------------------------------------------------------------------
	public static List<SkillValue> getHighestSkills(SpliMoCharacter model) {
		List<SkillValue> values = new ArrayList<>(model.getSkills(SkillType.NORMAL));
		values.addAll(model.getSkills(SkillType.MAGIC));
		values.remove(model.getSkillValue(SplitterMondCore.getSkill("athletics")));
		values.remove(model.getSkillValue(SplitterMondCore.getSkill("empathy")));
		values.remove(model.getSkillValue(SplitterMondCore.getSkill("determination")));
		values.remove(model.getSkillValue(SplitterMondCore.getSkill("perception")));
		values.remove(model.getSkillValue(SplitterMondCore.getSkill("endurance")));
//		values.remove(model.getSkillValue(SplitterMondCore.getSkill("locksntraps")));

		Collections.sort(values, new Comparator<SkillValue>() {
			public int compare(SkillValue o1, SkillValue o2) {
				return ((Integer)o1.getValue()).compareTo(o2.getValue());
			}
		});
		Collections.reverse(values);

		List<SkillValue> ret = values.subList(0, 6);
		Collections.sort(ret);
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * Get the speed for the assigned weapon, including mastership and
	 * weapon modifications.
	 */
	public static int getWeaponSpeedFor(SpliMoCharacter model, CarriedItem item) {
		// TODO Pastore 11.04.2017: this is a workaround. The value of the mastership should be a modification.
		int tickMalus = SplitterTools.getTickMalusSum(model, true);
		if (item.isType(ItemType.WEAPON)) {
			Weapon weapon = item.getItem().getType(Weapon.class);
			SkillValue skillVal = model.getSkillValue(weapon.getSkill());
			return (int) Math.max(3, tickMalus + item.getSpeed(ItemType.WEAPON)
			                                   - skillVal.getMasterships().stream()
                                                                          .map(m -> (m.getMastership()!=null)?m.getMastership().getKey():"")
                                                                          .filter(s -> s.equals("dancingblade")).count());
		}
		if (item.isType(ItemType.LONG_RANGE_WEAPON)) {
			LongRangeWeapon longRangeWeapon = item.getItem().getType(LongRangeWeapon.class);
			SkillValue skillVal = model.getSkillValue(longRangeWeapon.getSkill());
			return (int) (tickMalus + item.getSpeed(ItemType.LONG_RANGE_WEAPON)
			                        - skillVal.getMasterships().stream()
                                                               .map(m -> (m.getMastership()!=null)?m.getMastership().getKey():"")
                                                               .filter(s -> s.startsWith("fastshooter")).count());
		}
		logger.error("Unknown item type of " + item);
		return Short.MAX_VALUE;
	}

	//-------------------------------------------------------------------
	/**
	 * Get the value for a specific weapon, including skill value and
	 * eventually existing weapon specializations
	 */
	public static int getWeaponValueFor(SpliMoCharacter model, CarriedItem item, ItemType type) {
		int val = 0;
		if (!item.isType(type)) {
			return val;
		}

		SkillValue skillVal = new SkillValue();
		SkillSpecialization spec = new SkillSpecialization();
		switch (type) {

		case WEAPON:
			Weapon weapon = item.getItem().getType(Weapon.class);
			skillVal = model.getSkillValue(weapon.getSkill());
			// Calculate value for skill, including attributes
			val = skillVal.getModifiedValue();
			val += model.getAttribute(item.getAttribute1(ItemType.WEAPON)).getValue();
			val += model.getAttribute(item.getAttribute2(ItemType.WEAPON)).getValue();

			// Check for a skill bonus in the item
			for (Modification mod : item.getCharacterModifications()) {
				logger.info("Mod of "+item.getName()+" = "+mod);
				if (mod instanceof MastershipModification) {
					MastershipModification mmod = (MastershipModification)mod;
					logger.info("  mastermod = "+mmod.getSkill()+" / "+mmod.getSpecialization()+" / "+mmod.getSpecialization().getSpecial()+"  =?= "+item.getItem().getID());
					if (mmod.getSkill()==weapon.getSkill() && mmod.getSpecialization().getSpecial().getId().equals(item.getItem().getID())) {
						val += mmod.getSpecialization().getLevel();
					}
				}
			}

			// Check for specialization
			spec.setType(SkillSpecializationType.WEAPON);
			spec.setId(item.getItem().getID());
			break;
		case LONG_RANGE_WEAPON:
			LongRangeWeapon longRangeWeapon = item.getItem().getType(LongRangeWeapon.class);
			skillVal = model.getSkillValue(longRangeWeapon.getSkill());
			// Calculate value for skill, including attributes
			val = skillVal.getValue();
			val += model.getAttribute(item.getAttribute1(ItemType.LONG_RANGE_WEAPON)).getValue();
			val += model.getAttribute(item.getAttribute2(ItemType.LONG_RANGE_WEAPON)).getValue();

			// Check for a skill bonus in the item
			for (Modification mod : item.getModifications()) {
				if (mod instanceof MastershipModification) {
					MastershipModification mmod = (MastershipModification)mod;
					if (mmod.getSkill()==longRangeWeapon.getSkill() && mmod.getSpecialization().getSpecial().getId().equals(item.getItem().getID())){
						val += mmod.getSpecialization().getLevel();
					}
				}
			}

			// Check for specialization
			spec.setType(SkillSpecializationType.WEAPON);
			spec.setId(item.getItem().getID());
			break;
		default:
			break;
		}

		val += skillVal.getSpecializationLevel(spec);


		return val;
	}

	//-------------------------------------------------------------------
	/**
	 * Calculate training potential for a given creature.
	 * @See Bestienmeister S.15
	 */
	public static int getCreaturePotential(SpliMoCharacter model, CreatureReference creature) {
		int value = 1;
		// +2 per value in resource "creature"
		if (creature.getResource()!=null) {
			value += 2*creature.getResource().getValue();
		}
		// TODO: is animal connected via Power "Tiervertrauter" ?
		// TODO: Meisterschaft "Tierspezialist"
		// Meiste der Bestien
		if (model.hasMastership(SplitterMondCore.getSkill("animals").getMastership("lord_of_the_beasts"))) {
			value += 2;
		}
		// TODO: Meisterbändiger

		return value;
	}

	//-------------------------------------------------------------------
	/**
	 * Calculate training potential for a given creature.
	 */
	public static int getUsedCreaturePotential(CreatureReference creature) {
		int value = 0;
		if (creature != null && !creature.getTrainings().isEmpty()) {
			List<CreatureModuleReference> trainings = creature.getTrainings();
			value = trainings.stream().mapToInt(training -> training.getModule().getCost()).sum();
		}
		return value;
	}

	//-------------------------------------------------------------------
	public static boolean isRequirementMet(SpliMoCharacter model, Requirement requires) {
		if (requires instanceof AttributeRequirement) {
			AttributeRequirement req = (AttributeRequirement)requires;
			return model.getAttribute(req.getAttribute()).getValue()>=req.getValue();
		} else if (requires instanceof MastershipRequirement) {
			MastershipRequirement req = (MastershipRequirement)requires;
			return model.hasMastership(req.getMastership());
		}
		logger.warn("I don't know how to check for "+requires.getClass());
		return true;
	}

}
