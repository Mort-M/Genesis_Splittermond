/**
 *
 */
package org.prelle.splimo.creature;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.AttributeValue;
import org.prelle.splimo.DamageType;
import org.prelle.splimo.Mastership;
import org.prelle.splimo.MastershipReference;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.Spell;
import org.prelle.splimo.SpellValue;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.SplitterTools;
import org.prelle.splimo.charctrl.CreatureTrainerController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.creature.CreatureModule.Type;
import org.prelle.splimo.items.Feature;
import org.prelle.splimo.items.FeatureType;
import org.prelle.splimo.modifications.AttributeModification;
import org.prelle.splimo.modifications.ConditionalModification;
import org.prelle.splimo.modifications.CountModification;
import org.prelle.splimo.modifications.CreatureFeatureModification;
import org.prelle.splimo.modifications.CreatureTypeModification;
import org.prelle.splimo.modifications.DamageModification;
import org.prelle.splimo.modifications.FeatureModification;
import org.prelle.splimo.modifications.ItemFeatureModification;
import org.prelle.splimo.modifications.ItemModification;
import org.prelle.splimo.modifications.MastershipModification;
import org.prelle.splimo.modifications.ModificationChoice;
import org.prelle.splimo.modifications.SkillModification;
import org.prelle.splimo.modifications.SkillModification.RestrictionType;
import org.prelle.splimo.modifications.SpellModification;
import org.prelle.splimo.modifications.SubModificationChoice;
import org.prelle.splimo.requirements.AnyRequirement;
import org.prelle.splimo.requirements.AttributeRequirement;
import org.prelle.splimo.requirements.CreatureFeatureRequirement;
import org.prelle.splimo.requirements.CreatureModuleRequirement;
import org.prelle.splimo.requirements.CreatureTypeRequirement;
import org.prelle.splimo.requirements.DamageRequirement;
import org.prelle.splimo.requirements.ItemFeatureRequirement;
import org.prelle.splimo.requirements.MastershipRequirement;
import org.prelle.splimo.requirements.PowerRequirement;
import org.prelle.splimo.requirements.Requirement;
import org.prelle.splimo.requirements.SkillRequirement;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class CreatureTrainer implements CreatureTrainerController {

    private static Logger logger = Logger.getLogger("splittermond.chargen");

    private Skill melee;

    private SpliMoCharacter charac;
    private CreatureReference model;
//    private ModuleBasedCreature model;
    
    private List<CreatureModuleReference.NecessaryChoice> choices;

    //-------------------------------------------------------------------
    public CreatureTrainer(SpliMoCharacter charac, CreatureReference ref) {
    	this.charac = charac;
    	if (ref.getEntourage()!=null)
    		throw new IllegalArgumentException("Not for entourages");
    	model = ref;
//    	model = ref.getModuleBasedCreature();
    	logger.info("Start CreatureGenerator for existing creature");
    	choices = new ArrayList<>();
        melee = SplitterMondCore.getSkill("melee");
//        selectedOptions = model.getOptions()
        
        update();
    }

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.CommonCreatureController#getCreature()
	 */
	@Override
	public CreatureReference getCreature() {
		return model;
	}

	//-------------------------------------------------------------------
	/**
	 * Check if the given modification can be applied. E.g. a modification
	 * to remove a mastership the creature doesn't have, isn't valid.
	 * 
	 * @see java.util.function.Predicate#test(java.lang.Object)
	 */
//	@Override
	public boolean test(Modification mod) {
//		logger.warn("TODO: "+mod);
        if (mod instanceof CreatureFeatureModification) {
        	CreatureFeatureModification cMod = (CreatureFeatureModification)mod;
        	if (cMod.isRemoved()) {
        		CreatureFeature feat = model.getCreatureFeature(cMod.getFeature());
//        		logger.debug("TODO: model.getCreatureFeature("+cMod.getFeature()+") returned "+feat);
        		if (feat==null) {
//        			logger.debug("   "+mod+" can not be applied");
        			return false;
        		}
        		if (cMod.getFeature().hasLevels() && feat.getCount()==0) {
//        			logger.debug("   "+mod+" can not be applied");
        			return false;
        		}
        	}
        }
        
        if (mod instanceof SkillModification) {
        	SkillModification foo = (SkillModification)mod;
        	if (foo.getSkill()!=null)
        		return true;
        	
        }
//		logger.debug("   "+mod+" can be applied");
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.CreatureController#isRequirementMet(org.prelle.splimo.requirements.Requirement)
	 */
	@Override
	public boolean isRequirementMet(Requirement req) {
		if (req instanceof CreatureModuleRequirement) {
			CreatureModuleRequirement foo = (CreatureModuleRequirement)req;
			CreatureModule option = foo.getModule();
			if (option.getType()==Type.TRAINING) {
				boolean found = false;
				for (CreatureModuleReference ref : model.getTrainings())
					if (ref.getModule()==option) found=true;
				if (foo.isNegated()) {
					return !found;
				} else
					return found;
			}
			if (model.getModuleBasedCreature()==null)
				return false;
			boolean found = model.getModuleBasedCreature().hasOption(option);
			// Iterate only through trainings or all options
			for (CreatureModuleReference ref : model.getTrainings())
				if (ref.getModule()==option) found=true;
			if (foo.isNegated()) {
				return !found;
			} else
				return found;
		} else if (req instanceof AttributeRequirement) {
			AttributeRequirement foo = (AttributeRequirement)req;
			Attribute key = foo.getAttribute();
			AttributeValue aVal = model.getAttribute(key);
			return aVal.getValue()>=foo.getValue();
		} else if (req instanceof CreatureFeatureRequirement) {
			CreatureFeatureRequirement foo = (CreatureFeatureRequirement)req;
			CreatureFeature tmp = model.getCreatureFeature(foo.getFeature());
			boolean hasFeature = tmp!=null;
			return foo.isNegated()?!hasFeature:hasFeature;
		} else if (req instanceof SkillRequirement) {
			SkillRequirement foo = (SkillRequirement)req;
			Skill key = foo.getSkill();
			SkillValue aVal = model.getSkillValue(key);
//			logger.debug("      ...Expect "+foo.getValue()+"  found "+aVal);
			if (aVal==null)
				return foo.getValue()==0;
			return aVal.getModifiedValue()>=foo.getValue();
		} else if (req instanceof CreatureTypeRequirement) {
			CreatureTypeRequirement foo = (CreatureTypeRequirement)req;
			CreatureType key = foo.getType();
			boolean typeMatch = model.getCreatureType(key)!=null;
			return foo.isNegated()?!typeMatch:typeMatch;
		} else if (req instanceof ItemFeatureRequirement) {
			ItemFeatureRequirement foo = (ItemFeatureRequirement)req;
			FeatureType key = foo.getFeature();
			for (CreatureWeapon weapon : model.getCreatureWeapons()) {
				if (weapon.getFeature(key)!=null) {
					logger.debug("Feature type found: "+key+"   negated="+foo.isNegated());
					return !foo.isNegated();
				}
			}
			return foo.isNegated();
		} else if (req instanceof DamageRequirement) {
			DamageRequirement foo = (DamageRequirement)req;
			CreatureWeapon weapon = model.getCreatureWeapons().get(0);
			return weapon.getDamage()>=foo.getDamage();
		} else if (req instanceof MastershipRequirement) {
			return SplitterTools.isRequirementMet(charac, req);
		} else if (req instanceof AnyRequirement) {
			AnyRequirement foo = (AnyRequirement)req;
			for (Requirement opt : foo.getOptionList()) {
				if (this.isRequirementMet(opt))
					return true;
			}
			return false;
		} else if (req instanceof PowerRequirement) {
			PowerRequirement foo = (PowerRequirement)req;
			logger.debug("Requires power "+foo.getPower()+" in character");
			return charac.getPower(foo.getPower())!=null;
		} else {
			try {
				throw new RuntimeException("Don't know how to verify this requirement: "+req.getClass());
			} catch (Exception e) {
				logger.error("Cannot check requirement "+req,e);
			}
		}
		return true;	
	}

    //-------------------------------------------------------------------
    private void applyToWeapon(CreatureWeapon weapon, Modification mod) {
        if (mod instanceof SkillModification) {
            SkillModification sMod = (SkillModification)mod;
            weapon.setValue(weapon.getValue()+sMod.getValue());
        } else if (mod instanceof DamageModification) {
            DamageModification dMod = (DamageModification)mod;
            if (dMod.isSubstract())
                weapon.setDamage(weapon.getDamage() -  dMod.getDamage() );
            else
            	weapon.setDamage(weapon.getDamage() +  dMod.getDamage() );
            if (dMod.getType()!=null)
                weapon.setDamageType(dMod.getType());
        } else if (mod instanceof ItemModification) {
            ItemModification iMod = (ItemModification)mod;
            switch (iMod.getAttribute()) {
            case SPEED:
                weapon.setSpeed(weapon.getSpeed() + iMod.getValue());
                break;
            case INITIATIVE:
                weapon.setInitiative(weapon.getInitiative() + iMod.getValue());
                break;
            case DAMAGE:
                weapon.setDamage(weapon.getDamage() + iMod.getValue());
                break;
            default:
                logger.warn("Don't know how to modify weapon attribute: "+mod);
                System.exit(0);
            }
        } else if (mod instanceof ItemFeatureModification) {
            ItemFeatureModification ifMod = (ItemFeatureModification)mod;
            FeatureType type = ifMod.getFeature();
            logger.debug("apply "+ifMod);
            if (type.hasLevel()) {
                Feature old = weapon.getFeature(type);
                if (ifMod.isRemoved()) {
                    if (old!=null) {
                        old.setLevel(Math.max(0, old.getLevel() -ifMod.getLevel()) );
                        if (old.getLevel()==0)
                            weapon.removeFeature(type);
                    }
                } else {
                    if (old==null)
                        old = weapon.addFeature(type);
                    old.setLevel(old.getLevel() +ifMod.getLevel());
                }
            } else {
                if (ifMod.isRemoved()) {
                    weapon.removeFeature(type);
                } else
                    weapon.addFeature(type);
            }
        } else if (mod instanceof FeatureModification) {
        	FeatureModification fMod = (FeatureModification)mod;
        	FeatureType type = fMod.getFeature();
            logger.debug("apply "+fMod);
            if (type.hasLevel()) {
                Feature old = weapon.getFeature(type);
                if (fMod.isRemoved()) {
                    if (old!=null) {
                        old.setLevel(Math.max(0, old.getLevel() -1) );
                        if (old.getLevel()==0)
                            weapon.removeFeature(type);
                    }
                } else {
                    if (old==null)
                        old = weapon.addFeature(type);
                    old.setLevel(old.getLevel() +1);
                }
            } else {
                if (fMod.isRemoved()) {
                    weapon.removeFeature(type);
                } else
                    weapon.addFeature(type);
            }
      } else {
            logger.warn("Don't know how to apply to weapon: "+mod);
            System.exit(0);
        }
    }

    //-------------------------------------------------------------------
    private void applyMastershipModification(MastershipModification mod) {
        if (mod.getMastership()!=null) {
        	// Apply given mastership
        	Mastership master = mod.getMastership();
        	Skill skill = mod.getSkill();
        	SkillValue sVal = model.getSkillValue(skill);
            logger.debug("  add mastership "+master+" to "+skill.getId());
        	sVal.addMastership(new MastershipReference(master));
        	
        }
    }

    //-------------------------------------------------------------------
    private void apply(Modification mod) {
        logger.debug("   apply "+mod.getClass()+" / "+mod);
        if (mod instanceof AttributeModification) {
            AttributeModification aMod = (AttributeModification)mod;
            model.getAttribute(aMod.getAttribute()).addModification(aMod);
        } else if (mod instanceof SkillModification) {
            SkillModification sMod = (SkillModification)mod;
            if (sMod.getSkill()==melee) {
                applyToWeapon(model.getCreatureWeapons().get(0), sMod);
            } else if (sMod.getSkill()!=null) {
            	if (model.getSkillValue(sMod.getSkill())==null) {
            		logger.error("Implementation missing here");
            		throw new RuntimeException("trace");
//            		model.addSkill(new SkillValue(sMod.getSkill(), 0));
            	}
            	model.getSkillValue(sMod.getSkill()).addModification(sMod);
            } else {
            	for (Skill skill : SplitterMondCore.getSkills()) {
            		SkillValue sVal = model.getSkillValue(skill);
            		// Check existence condition
            		boolean exists = (sVal!=null && sVal.getModifiedValue()>0);
            		switch (sMod.getRestrictionType()) {
            		case ANY_EXIST:
            		case MUST_EXIST:
            			if (!exists)
            				continue;
            			break;
            		case MUST_NOT_EXIST:
            			if (exists)
            				continue;
            			break;
            		case ANY:
            		}
            		// Check type condition
            		if (sMod.getChoiceType()!=null) {
            			if (sMod.getChoiceType()!=skill.getType())
            				continue;
            		}
            		// Eventually add skill
            		// Apply modification
            		if (sVal==null)
            			throw new NullPointerException("No skill value for "+skill);
            		SkillModification tmp = new SkillModification(skill, sMod.getValue());
            		tmp.setSource(sMod.getSource());
                	sVal.addModification(tmp);
                	logger.debug("    convert to '"+tmp+"'   and add to "+sVal);
            	}
            }
//        } else if (mod instanceof CreatureFeatureModification) {
//            CreatureFeatureModification cfMod = (CreatureFeatureModification)mod;
//            CreatureFeatureType type = cfMod.getFeature();
//            if (type.hasLevels()) {
//                CreatureFeature old = model.getCreatureFeature(type);
//                if (cfMod.isRemoved()) {
//                    if (old!=null) {
//                        old.setLevel(Math.max(0, old.getLevel() +cfMod.getLevel()) );
//                        if (old.getLevel()==0)
//                        	model.removeCreatureFeature(type);
//                    }
//                } else {
//                    if (old==null)
//                        old = model.addCreatureFeature(type);
//                    old.setLevel(old.getLevel() +cfMod.getLevel());
//                }
//            } else {
//                if (cfMod.isRemoved()) {
//                	model.removeCreatureFeature(type);
//                } else
//                	model.addCreatureFeature(type);
//            }
//        } else if (mod instanceof CreatureTypeModification) {
//        	CreatureTypeModification foo = (CreatureTypeModification)mod;
//        	CreatureType type = foo.getFeature();
//            CreatureTypeValue old = model.getCreatureType(type);
//        	if (type.hasLevel()) {
//                if (foo.isRemoved()) {
//                    if (old!=null) {
//                        old.setLevel(Math.max(0, old.getLevel() -foo.getLevel()) );
//                        if (old.getLevel()==0)
//                        	model.removeCreatureType(old);
//                    }
//                } else {
//                    if (old==null)
//                        model.addCreatureType(new CreatureTypeValue(type, foo.getLevel()));
//                    else
//                    	old.setLevel(old.getLevel() +foo.getLevel());
//                }
//            } else {
//            	if (foo.isRemoved()) {
//            		if (old!=null) 
//           				model.removeCreatureType(old);            		
//                } else {
//                	if (old==null) {
//                       	model.addCreatureType(new CreatureTypeValue(type));
//                	}
//                }
//         	}
        } else if (mod instanceof DamageModification) {
            applyToWeapon(model.getCreatureWeapons().get(0), mod);
        } else if (mod instanceof ItemModification) {
            applyToWeapon(model.getCreatureWeapons().get(0), mod);
        } else if (mod instanceof ItemFeatureModification) {
            applyToWeapon(model.getCreatureWeapons().get(0), mod);
        } else if (mod instanceof MastershipModification) {
            applyMastershipModification((MastershipModification) mod);
        } else if (mod instanceof FeatureModification) {
            applyToWeapon(model.getCreatureWeapons().get(0), mod);
        } else if (mod instanceof CountModification) {
        	CountModification rcMod = (CountModification)mod;
        	int count = rcMod.getCount();
        	int done = 0;
        	switch (rcMod.getType()) {
        	case SPELL:
        		for (CreatureModuleReference.NecessaryChoice choice : choices) {
        			if (choice.disabled)
        				continue;
//        			logger.debug("Compare with SPELL "+choice.getOriginChoice()+" from "+choice.originModule.getModule().getId());
        			if (choice.getOriginChoice() instanceof SpellModification && done<count) {
        				logger.debug("  Disable choice: "+choice.getOriginChoice());
        				choice.disabled = true;
        				done++;
        			}
        		}
        		break;
        	case MASTERSHIP:
        		for (CreatureModuleReference.NecessaryChoice choice : choices) {
        			if (choice.disabled)
        				continue;
        			logger.debug("Compare with MASTERSHIP "+choice.getOriginChoice()+" from "+choice.originModule.getModule().getId());
        			if (choice.getOriginChoice() instanceof MastershipModification && done<count) {
        				logger.debug("  Disable choice: "+choice.getOriginChoice());
        				choice.disabled = true;
        				done++;
        			}
        		}
        		break;
        	case CREATURE_FEATURE_POSITIVE:
        		logger.warn("TODO: remove positive creature feature");
                System.exit(0);
        		break;
//        	case WEAPON:
//        		logger.debug("Add new weapon");
//        		CreatureWeapon weapon = new CreatureWeapon();
//                weapon.setSkill(melee);
//                weapon.setValue(model.getCreatureWeapons().get(0).getValue());
//                weapon.setInitiative(model.getCreatureWeapons().get(0).getInitiative());
//        		switch (getInvestedCreaturePoints()) {
//        		case 1:
//                    weapon.setDamage(10600);
//                    weapon.setSpeed(7);
//                    weapon.addFeature(new Feature(SplitterMondCore.getFeatureType("PIERCING"), 2));
//                    break;
//        		case 2:
//                    weapon.setDamage(10601);
//                    weapon.setSpeed(6);
//                    weapon.addFeature(new Feature(SplitterMondCore.getFeatureType("PIERCING"), 2));
//                    break;
//        		case 3:
//                    weapon.setDamage(10602);
//                    weapon.setSpeed(7);
//                    weapon.addFeature(new Feature(SplitterMondCore.getFeatureType("PIERCING"), 3));
//                    break;
//        		case 4:
//                    weapon.setDamage(10604);
//                    weapon.setSpeed(8);
//                    weapon.addFeature(new Feature(SplitterMondCore.getFeatureType("PENETRATING"), 1));
//                    weapon.addFeature(new Feature(SplitterMondCore.getFeatureType("PIERCING"), 3));
//                    break;
//        		case 5:
//                    weapon.setDamage(20602);
//                    weapon.setSpeed(8);
//                    weapon.addFeature(new Feature(SplitterMondCore.getFeatureType("PENETRATING"), 2));
//                    weapon.addFeature(new Feature(SplitterMondCore.getFeatureType("PIERCING"), 3));
//                    break;
//        		}
//                model.addWeapon(weapon);
//                logger.debug("Added "+weapon);
//                break;
        	default:
                logger.warn("TODO: apply "+rcMod.getType());
                System.exit(0);
        	}
        } else if (mod instanceof ModificationChoice) {
        	// If a ModificationChoice gets here, there should be not more than one possible option left
        	// Count valid choices
        	List<Modification> valid = new ArrayList<>();
        	for (Modification testMod : ((ModificationChoice)mod).getOptions()) {
        		if (test(testMod)) {
        			logger.debug("   option still valid: "+testMod);
        			valid.add(testMod);
        		} else {
        			logger.debug("   option is invalid: "+testMod);
        		}
        	}
        	switch (valid.size()) {
        	case 0:
        		logger.warn("   Did not apply ModificationChoice "+mod+" since it has no valid options");
        		return;
        	case 1:
        		logger.debug("   Auto apply last remaining valid option "+valid.get(0)+"   (from "+mod+" )");
        		apply(valid.get(0));
        		return;
        	default:
        		logger.error("   Auto apply first option "+valid.get(0)+"  from "+valid.size()+" valid choices - this should not happen");
        		apply(valid.get(0));
        		return;
        	}
        } else if (mod instanceof SubModificationChoice) {
        	// If a ModificationChoice gets here, there should be not more than one possible option left
        	// Count valid choices
        	List<Modification> valid = new ArrayList<>();
        	for (Modification testMod : ((SubModificationChoice)mod).getOptions()) {
        		if (test(testMod)) {
        			logger.debug("   option still valid: "+testMod);
        			valid.add(testMod);
        		} else {
        			logger.debug("   option is invalid: "+testMod);
        		}
        	}
        	switch (valid.size()) {
        	case 0:
        		logger.warn("   Did not apply SubModificationChoice "+mod+" since it has no valid options");
        		return;
        	case 1:
        		logger.debug("   Auto apply last remaining valid option "+valid.get(0)+"   (from "+mod+" )");
        		apply(valid.get(0));
        		return;
        	default:
        		logger.error("   Auto apply first option "+valid.get(0)+"  from "+valid.size()+" valid choices - this should not happen");
        		apply(valid.get(0));
        		return;
        	}
        } else if (mod instanceof ConditionalModification) {
        	ConditionalModification condMod = (ConditionalModification)mod;
        	logger.debug("    Apply "+condMod.getTrueModification()+" IF "+condMod.getConditions()+" ELSE "+condMod.getElseModification());
        	boolean requirementsAreMet = true;
        	for (Requirement req : condMod.getConditions()) {
        		logger.debug("    * check "+req+" = "+isRequirementMet(req));
        		if (!isRequirementMet(req)) {
        			requirementsAreMet = false;
        			break;
        		}
        	}
        	if (requirementsAreMet) {
        		logger.debug("      All requirements are met");
        		for (Modification tmp : condMod.getTrueModification()) {
        			apply(tmp);
        		}
        	} else if (condMod.getElseModification()!=null) {
        		logger.debug("      Some requirements are not met");
        		for (Modification tmp : condMod.getElseModification()) {
        			apply(tmp);
        		}
        	}
        	
//        } else if (mod instanceof SpellModification) {
//        	SpellModification sMod = (SpellModification)mod;
//        	logger.debug("    Apply "+sMod);
//        	if (sMod.isRemove()) {
//        		if (!model.hasSpell(sMod.getSpell()))
//        			logger.error("Trying to remove a spell that does not exist in model");
//        		model.removeSpell(sMod.getSpell());
//        	} else {
//        		model.addSpell(sMod.getSpell());
//        	}
       } else {
            logger.warn("TODO: apply "+mod);
            logger.warn("TODO: apply "+mod.getClass());
            System.exit(0);
        }
    }

    //-------------------------------------------------------------------
    private void undoMastershipModification(MastershipModification mod) {
        if (mod.getMastership()!=null) {
        	// Undo given mastership
        	Mastership master = mod.getMastership();
        	Skill skill = mod.getSkill();
        	SkillValue sVal = model.getSkillValue(skill);
            logger.debug("  remove mastership "+master+" from "+skill.getId());
         	sVal.removeMastership(master);
        }
    }

    //-------------------------------------------------------------------
    private void undoFromWeapon(CreatureWeapon weapon, Modification mod) {
        if (mod instanceof SkillModification) {
            SkillModification sMod = (SkillModification)mod;
            weapon.setValue(weapon.getValue()-sMod.getValue());
        } else if (mod instanceof DamageModification) {
            DamageModification dMod = (DamageModification)mod;
            weapon.setDamage(weapon.getDamage() -  dMod.getDamage() );
            if (dMod.getType()!=null)
                weapon.setDamageType(DamageType.PROFANE);
        } else if (mod instanceof ItemModification) {
            ItemModification iMod = (ItemModification)mod;
            switch (iMod.getAttribute()) {
            case SPEED:
                weapon.setSpeed(weapon.getSpeed() - iMod.getValue());
                break;
            case INITIATIVE:
                weapon.setInitiative(weapon.getInitiative() - iMod.getValue());
                break;
            case DAMAGE:
                weapon.setDamage(weapon.getDamage() - iMod.getValue());
                break;
            default:
                logger.warn("Don't know how to modify weapon attribute: "+mod);
                System.exit(0);
            }
        } else if (mod instanceof ItemFeatureModification) {
            ItemFeatureModification ifMod = (ItemFeatureModification)mod;
            FeatureType type = ifMod.getFeature();
            logger.debug("apply "+ifMod);
            if (type.hasLevel()) {
                Feature old = weapon.getFeature(type);
                if (ifMod.isRemoved()) {
                	// Undo removing
                    if (old!=null) {
                        old.setLevel(old.getLevel() +ifMod.getLevel() );
                        if (old.getLevel()==0)
                            weapon.removeFeature(type);
                    } else {
                        old = weapon.addFeature(type);
                        old.setLevel(ifMod.getLevel());
                    }
                } else {
                	// Undo adding
                    old.setLevel(old.getLevel() -ifMod.getLevel());
                    if (old.getLevel()<=0)
                        weapon.removeFeature(type);
                }
            } else {
                if (ifMod.isRemoved()) {
                    weapon.addFeature(type);
                } else
                    weapon.removeFeature(type);
            }
        } else if (mod instanceof FeatureModification) {
        	FeatureModification fMod = (FeatureModification)mod;
        	FeatureType type = fMod.getFeature();
            logger.debug("apply "+fMod);
            if (type.hasLevel()) {
                Feature old = weapon.getFeature(type);
                if (fMod.isRemoved()) {
                	// Undo removing
                    if (old!=null) {
                        old.setLevel(old.getLevel() +1 );
                        if (old.getLevel()==0)
                            weapon.removeFeature(type);
                    } else {
                        old = weapon.addFeature(type);
                        old.setLevel(1);
                    }
                } else {
                	// Undo adding
                    old.setLevel(old.getLevel() -1);
                    if (old.getLevel()<=0)
                        weapon.removeFeature(type);
                }
            } else {
                if (fMod.isRemoved()) {
                    weapon.addFeature(type);
                } else
                    weapon.removeFeature(type);
            }
      } else {
            logger.warn("Don't know how to apply to weapon: "+mod);
            System.exit(0);
        }
    }

    //-------------------------------------------------------------------
    private void undo(Modification mod) {
    	logger.debug("  undo "+mod.getClass()+" / "+mod);
    	if (mod instanceof AttributeModification) {
    		AttributeModification aMod = (AttributeModification)mod;
    		model.getAttribute(aMod.getAttribute()).removeModification(aMod);
    	} else if (mod instanceof SkillModification) {
    		SkillModification sMod = (SkillModification)mod;
    		if (sMod.getSkill()==melee) {
    			applyToWeapon(model.getCreatureWeapons().get(0), sMod);
    		} else {
    			model.getSkillValue(sMod.getSkill()).removeModification(sMod);
    		}
//    	} else if (mod instanceof CreatureFeatureModification) {
//    		CreatureFeatureModification cfMod = (CreatureFeatureModification)mod;
//    		CreatureFeatureType type = cfMod.getFeature();
//    		if (type.hasLevels()) {
//    			CreatureFeature old = model.getCreatureFeature(type);
//    			if (old==null) {
//    				if (cfMod.isRemoved()) 
//    					model.addCreatureFeature(type);
//    			} else {
//    				if (cfMod.isRemoved()) {
//    					old.setLevel(old.getLevel() +cfMod.getLevel() );
//    				} else {
//    					old.setLevel(old.getLevel() -cfMod.getLevel() );
//    					if (old.getLevel()<=0)
//    						model.removeCreatureFeature(type);
//    				}
//    			}
//    		} else {
//    			if (cfMod.isRemoved()) {
//    				model.addCreatureFeature(type);
//    			} else
//    				model.removeCreatureFeature(type);
//    		}
    	} else if (mod instanceof DamageModification) {
    		undoFromWeapon(model.getCreatureWeapons().get(0), mod);
        } else if (mod instanceof ItemModification) {
            applyToWeapon(model.getCreatureWeapons().get(0), mod);
        } else if (mod instanceof ItemFeatureModification) {
            applyToWeapon(model.getCreatureWeapons().get(0), mod);
        } else if (mod instanceof MastershipModification) {
            undoMastershipModification((MastershipModification) mod);
//        } else if (mod instanceof SpellModification) {
//        	SpellModification sMod = (SpellModification)mod;
//        	logger.debug("    Undo "+sMod);
//        	if (sMod.isRemove()) {
//        		if (model.hasSpell(sMod.getSpell()))
//        			logger.error("Trying to add a spell that already exists in model");
//        		model.addSpell(sMod.getSpell());
//        	} else {
//        		model.removeSpell(sMod.getSpell());
//        	}
        } else {
            logger.warn("TODO: don't know how to undo "+mod);
            System.exit(0);
        }
    }

    //-------------------------------------------------------------------
    private void update() {
        logger.debug("START: -----update---------------------------");
        
        	CreatureTools.calculateTrainings(model);
//        model.clear();
//        
//        // Reset all disabled choices
//        choices.forEach(choice -> choice.disabled=false);
// 
//        CreatureWeapon weapon = new CreatureWeapon();
//        weapon.addFeature(new Feature(SplitterMondCore.getFeatureType("BLUNT"), 1));
//        weapon.setDamage(10600);
//        weapon.setSpeed(7);
//        weapon.setSkill(melee);
//        weapon.setValue(0);
//        model.addWeapon(weapon);
//
//        // Creature types
//        for (CreatureTypeValue tmp : types)
//        	model.addCreatureType(tmp);
//        
//        if (base!=null) {
//            logger.debug("1. Base");
//            base.getModifications().stream().filter(mod -> !needsToBeAppliedLater(mod)).forEach(mod -> apply(mod));
//        }
//        if (role!=null) {
//            logger.debug("2. Role");
//            role.getModifications().stream().filter(mod -> !needsToBeAppliedLater(mod)).forEach(mod -> apply(mod));
//        }
//
//        for (CreatureModuleReference ref : selectedOptions) {
//            logger.debug("3. Option: "+ref);
//            ref.getModifications().stream().filter(mod -> !needsToBeAppliedLater(mod)).forEach(mod -> apply(mod));
//        }
//
//        if (role!=null) {
//            logger.debug("4. Late modifications Role");
//        	role.getModifications().stream().filter(mod -> needsToBeAppliedLater(mod)).forEach(mod -> apply(mod));
//        }
//        for (CreatureModuleReference ref : selectedOptions) {
//            logger.debug("5. Late modifications Option: "+ref);
//            ref.getModifications().stream().filter(mod -> needsToBeAppliedLater(mod)).forEach(mod -> apply(mod));
//        }
//
//        logger.debug("6. Choices");
//        for (NecessaryChoice ref : choices) {
//        	if (ref.disabled) {
//        		logger.debug("  Choice disabled for: "+ref.originModule.getModule().getId()+"/"+ref.originChoice);            
//        	} else if (ref.madeChoice==null) {
//        		logger.debug("  No choice made for: "+ref.originModule.getModule().getId()+"/"+ref.originChoice);            
//        	} else {
//        		logger.debug("  Choice made for: "+ref.originModule.getModule().getId()+"/"+ref.originChoice+" = "+ref.madeChoice);            
//        		apply(ref.madeChoice);
//        	}
//        }

        GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.CREATURE_CHANGED, model));
        logger.debug("STOP : -----update---------------------------");
    }

    //-------------------------------------------------------------------
    /**
     * @see org.prelle.splimo.charctrl.CreatureController#getAvailableCreaturePoints()
     */
    @Override
    public int getAvailablePotential() {
    	int pointsSpent = 0;
		for (CreatureModuleReference opt : model.getTrainings()) {
			pointsSpent += opt.getModule().getCost();
		}
   	
		return SplitterTools.getCreaturePotential(charac, model) - pointsSpent;
    }

    //-------------------------------------------------------------------
    /**
     * @see org.prelle.splimo.charctrl.CreatureTrainerController#canSelect(org.prelle.splimo.creature.CreatureModule)
     */
    @Override
    public boolean canSelect(CreatureModule option) {
        // Can be only selected once
        if (!getAvailableOptions().contains(option))
        	return false;

		for (Requirement req : option.getRequirements()) {
			boolean isMet = isRequirementMet(req);
//			logger.debug("Requirement "+req+" of "+option.getId()+" is met = "+isMet);
			if (!isMet)
				return false;
		}

		return true;
    }

    //-------------------------------------------------------------------
    public boolean canDeselect(CreatureModuleReference option) {
        // Can be only selected once
        for (CreatureModuleReference mod : model.getTrainings()) {
            if (mod==option)
                return true;
        }

        return false;
    }

    //-------------------------------------------------------------------
    private boolean needsToBeAppliedLater(Modification mod) {
        if (mod instanceof CreatureFeatureModification) return ((CreatureFeatureModification)mod).isRemoved();
        if (mod instanceof FeatureModification) return ((FeatureModification)mod).isRemoved();
        if (mod instanceof CountModification) return true;
        if (mod instanceof ModificationChoice) return true;
        if (mod instanceof SkillModification) {
        	SkillModification sMod = (SkillModification)mod;
        	return (sMod.getSkill()==null);
        } 
   	
        return false;
    }

    //-------------------------------------------------------------------
    private boolean needsToBeChosen(Modification mod) {
        if (mod instanceof AttributeModification) return false;
        if (mod instanceof CreatureFeatureModification) return false;
        if (mod instanceof CreatureTypeModification) return false;
        if (mod instanceof DamageModification) return false;
        if (mod instanceof ItemModification) return false;
        if (mod instanceof ItemFeatureModification) return false;
        if (mod instanceof FeatureModification) return false;
        if (mod instanceof CountModification) {
        	return ((CountModification)mod).getType()==CountModification.Type.CREATURE_FEATURE_POSITIVE;
        }
        if (mod instanceof SkillModification) {
        	logger.debug("Needs to be chosen("+mod+") will return "+(((SkillModification)mod).getSkill()==null));
        	return ((SkillModification)mod).getSkill()==null && ((SkillModification)mod).getRestrictionType()!=RestrictionType.ANY_EXIST;
        } 
        if (mod instanceof MastershipModification) {
            MastershipModification mmod = (MastershipModification)mod;
            if (mmod.getMastership()==null) {
                return true;
            } else {
        		return false;
            }
        } else if (mod instanceof ModificationChoice) {
        	// Count valid choices
        	List<Modification> valid = new ArrayList<>();
        	for (Modification testMod : ((ModificationChoice)mod).getOptions()) {
        		if (test(testMod)) {
        			logger.debug("..option still valid: "+testMod);
        			valid.add(testMod);
        		} else {
        			logger.debug("..option is invalid: "+testMod);
        		}
        	}
        	return (valid.size()>1);
        } else if (mod instanceof SpellModification) {
        	SpellModification sMod = (SpellModification)mod;
        	if (sMod.getSpell()!=null)
        		return false;
        	else {
        		return true;
        	}
        } else if (mod instanceof ConditionalModification) {
        	return false;
        } else {
            logger.warn("Don't know how to handle "+mod.getClass()+" // "+mod);
            System.exit(0);
        }
    	return false;
    }

//    //-------------------------------------------------------------------
//    private List<Modification> instantiateModification(SkillModification mod) {
//    	logger.debug("instantiate "+mod);
//    	if (needsToBeChosen(mod)) {
//    		NecessaryChoice choice = new NecessaryChoice();
//    		choice.originModule = ref;
//    		choice.originChoice = mod;
//    		choices.add(choice);
//    		logger.debug("Added choice "+mod);
//    	} else {
//    		ref.addModification(mod);
//    	}
//    }

    //-------------------------------------------------------------------
    private void instantiateModification(CreatureModuleReference ref, Modification mod) {
    	logger.debug("instantiate "+mod);
    	if (needsToBeChosen(mod)) {
    		CreatureModuleReference.NecessaryChoice choice = new CreatureModuleReference.NecessaryChoice();
    		choice.originModule = ref;
    		choice.setOriginChoice(mod);
    		choices.add(choice);
    		logger.debug("Added choice "+mod);
    	} else {
    		ref.addModification(mod);
    	}
    }

    //-------------------------------------------------------------------
    /**
     * @see org.prelle.splimo.charctrl.CreatureController#selectOption(org.prelle.splimo.creature.CreatureModule)
     */
    @Override
    public CreatureModuleReference selectOption(CreatureModule option) {
        if (!canSelect(option)) {
            logger.error("Trying to select unselectable option");
            return null;
        }
        logger.info("Select option "+option.getId());

        CreatureModuleReference ref = new CreatureModuleReference(option);
        option.getModifications().forEach(mod -> instantiateModification(ref, mod));
        model.addTraining(ref);

        update();

        return ref;
    }

    //-------------------------------------------------------------------
    /**
     * @see org.prelle.splimo.charctrl.CreatureController#deselectOption(org.prelle.splimo.creature.CreatureModuleReference)
     */
    @Override
    public void deselectOption(CreatureModuleReference option) {
        if (!canDeselect(option)) {
            logger.error("Trying to deselect unselected option");
            return;
        }
        logger.info("Deselect option "+option.getModule().getId());

        model.removeTraining(option);
        
        for (CreatureModuleReference.NecessaryChoice tmp : new ArrayList<>(choices)) {
        	if (tmp.originModule==option) {
        		logger.debug("Remove choice "+tmp);
        		choices.remove(tmp);
        	}
        }

        update();
    }

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.CreatureController#getChoicesToMake()
	 */
	@Override
	public List<CreatureModuleReference.NecessaryChoice> getChoicesToMake() {
		return new ArrayList<>(choices);
	}

	//-------------------------------------------------------------------
	private Modification getModificationForChoice(CreatureModuleReference ref, Modification mod) {
		for (CreatureModuleReference.NecessaryChoice choice : choices) {
			if (choice.originModule!=ref)
				continue;
			if (choice.getOriginChoice()!=mod)
				continue;
			if (choice.disabled)
				return null;
			return choice.getMadeChoice();
		}
		return null;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.CreatureController#makeChoice(org.prelle.splimo.charctrl.CreatureController.NecessaryChoice, de.rpgframework.genericrpg.modification.Modification)
	 */
	@Override
	public void makeChoice(CreatureModuleReference.NecessaryChoice choice, Modification chosenOption) {
		logger.info("makeChoice '"+chosenOption+"'  old="+choice.getMadeChoice());
		
		if (choice.getMadeChoice()==chosenOption) {
			return;
		}
		
		if (choice.getMadeChoice()!=null) {
			// Undo previous made choice
			undo(choice.getMadeChoice());
		}
		
		logger.debug("Chose "+chosenOption);
		
		// Does this choice requires another choice to be made?
		if (chosenOption instanceof SkillModification && ((SkillModification)chosenOption).getSkill()==null) {
			logger.debug("  another skill choice needs to be made");
//			throw new RuntimeException("Trace");
			choice.setOriginChoice(chosenOption);
			instantiateChoice(choice);
			return;
		}
		
		
		choice.setOriginChoice(chosenOption);
		apply(chosenOption);
    	update();
	}

	//-------------------------------------------------------------------
	private List<SkillModification> instantiateModification(SkillModification smod) {
		List<SkillModification> ret = new ArrayList<>();
		List<Skill> options = (smod.getChoiceType()!=null)?SplitterMondCore.getSkills(smod.getChoiceType()):SplitterMondCore.getSkills();
		for (Skill skill : options) {
			switch (smod.getRestrictionType()) {
			case MUST_EXIST:
				if (model.hasSkill(skill))
					ret.add(new SkillModification(skill, smod.getValue()));
				break;
			case MUST_NOT_EXIST:
				if (!model.hasSkill(skill))
					ret.add(new SkillModification(skill, smod.getValue()));
				break;
			case ANY:
				ret.add(new SkillModification(skill, smod.getValue()));
				break;
			case ANY_EXIST:
				logger.warn("SkillModification.ANY_EXIST should not be instantiated here");
				throw new RuntimeException("Foo");
//				break;
				
			default:
				logger.warn("How did I get here");
			}
		}
		return ret;
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.CreatureController#instantiateChoice(org.prelle.splimo.charctrl.CreatureController.NecessaryChoice)
	 */
	@Override
	public ModificationChoice instantiateChoice(CreatureModuleReference.NecessaryChoice toChoose) {
		logger.warn("instantiateChoice "+toChoose.getOriginChoice());
		
		if (toChoose.getOriginChoice() instanceof ModificationChoice) {
			logger.debug("Instantiate "+toChoose);
			return (ModificationChoice) toChoose.getOriginChoice();
		}
		
		if (toChoose.getOriginChoice() instanceof MastershipModification) {
			MastershipModification mmod = (MastershipModification)toChoose.getOriginChoice();
			logger.debug("Convert "+mmod+" into a choice");
			ModificationChoice choice = new ModificationChoice(1);
			for (Skill skill : SplitterMondCore.getSkills()) {
				if (mmod.getSkill()!=null && mmod.getSkill()!=skill)
					continue;
				SkillValue sVal = model.getSkillValue(skill);
				if (sVal==null)
					continue;
				if (sVal.getModifiedValue()==0 && !sVal.getSkill().getId().equals("melee"))
					continue;
				for (Mastership master : skill.getMasterships()) {
					if (sVal.hasMastership(master) && !master.isMultiple())
						continue;
					if (master.getLevel()>mmod.getLevel())
						continue;
					MastershipModification newMod = new MastershipModification(master);
					choice.add(newMod);
					logger.debug("  Add "+newMod);
				}
			}
			logger.debug("converted to "+choice);
			return choice;
		} else if (toChoose.getOriginChoice() instanceof SpellModification) {
			SpellModification mmod = (SpellModification)toChoose.getOriginChoice();
			logger.debug("Convert "+mmod+" into a choice");
			int level = mmod.getLevel()-1;
			ModificationChoice choice = new ModificationChoice(1);
			for (SkillValue sVal : model.getSkills(SkillType.MAGIC)) {
				logger.debug("* School "+sVal);
				for (Spell spell : SplitterMondCore.getSpells(sVal.getSkill())) {
					if (spell.getLevelInSchool(sVal.getSkill())>level)
						continue;
					SpellValue toAdd = new SpellValue(spell, sVal.getSkill());
					if (model.hasSpell(toAdd))
						continue;
					SpellModification newMod = new SpellModification(toAdd);
					choice.add(newMod);
					logger.debug("  Add "+newMod);
				}
			}
			logger.debug("converted to "+choice);
			return choice;

		} else if (toChoose.getOriginChoice() instanceof SkillModification) {
			SkillModification smod = (SkillModification)toChoose.getOriginChoice();
			ModificationChoice choice = new ModificationChoice(1);
			logger.debug("Build list from "+smod);
			for (SkillModification toAdd : instantiateModification(smod))
				choice.add(toAdd);
			
//			List<Skill> options = (smod.getChoiceType()!=null)?SplitterMondCore.getSkills(smod.getChoiceType()):SplitterMondCore.getSkills();
//			for (Skill skill : options) {
//				switch (smod.getRestrictionType()) {
//				case MUST_EXIST:
//					if (model.hasSkill(skill))
//						choice.add(new SkillModification(skill, smod.getValue()));
//					break;
//				case MUST_NOT_EXIST:
//					if (!model.hasSkill(skill))
//						choice.add(new SkillModification(skill, smod.getValue()));
//					break;
//				case ANY:
//					choice.add(new SkillModification(skill, smod.getValue()));
//					break;
//				default:
//					logger.warn("How did I get here");
//				}
//			}
			logger.debug("converted to "+choice);
			return choice;
		} else {
			logger.fatal("Don't know how to convert "+toChoose.getOriginChoice());
			System.exit(0);
		}
		
		return null;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.CreatureController#getAvailableTrainings()
	 */
	@Override
	public List<CreatureModule> getAvailableOptions() {
		List<CreatureModule> ret = new ArrayList<>();
		for (CreatureModule mod : SplitterMondCore.getCreatureModules()) {
			if (mod.getType()!=Type.TRAINING)
				continue;
			boolean exist = model.hasTraining(mod);
			if (!exist || mod.canBeUsedMultiple())
				ret.add(mod);
		}
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.CreatureController#getSelectedTrainings()
	 */
	@Override
	public List<CreatureModuleReference> getSelectedOptions() {
		return new ArrayList<>(model.getTrainings());
	}

}
