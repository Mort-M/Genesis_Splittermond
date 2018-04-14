/**
 * 
 */
package org.prelle.splimo.creature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.prelle.splimo.DamageType;
import org.prelle.splimo.Mastership;
import org.prelle.splimo.MastershipReference;
import org.prelle.splimo.Skill;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.creature.CreatureModuleReference.NecessaryChoice;
import org.prelle.splimo.items.Feature;
import org.prelle.splimo.items.FeatureType;
import org.prelle.splimo.modifications.AllOfModification;
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
import org.prelle.splimo.requirements.Requirement;

import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;
import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class CreatureTools {

	private final static Logger logger = Logger.getLogger("splittermond");

	private static Skill melee = SplitterMondCore.getSkill("melee");


    //-------------------------------------------------------------------
    private static int getInvestedCreaturePoints(ModuleBasedCreature modulBased) {
//    	if (model.getResource()!=null) {
//    		return model.getResource().getValue();
//    	}
    	int pointsSpent = 0;
    	if (modulBased.getBase()!=null) pointsSpent++;
    	if (modulBased.getRole()!=null) pointsSpent++;
		for (CreatureModuleReference opt : modulBased.getOptions()) {
			pointsSpent += opt.getModule().getCost();
		}
		return pointsSpent;
    }

    //-------------------------------------------------------------------
    public static void calculateModuleBasedCreature(ModuleBasedCreature modulBased) {
	        logger.debug("START: -----recalculate---------------------------");
	        modulBased.clear();
	        apply(modulBased, new CreatureFeatureModification(SplitterMondCore.getCreatureFeatureType("CREATURE"), getInvestedCreaturePoints(modulBased)));
	        
	        // Reset all disabled choices
	        getChoicesToMake(modulBased).forEach(choice -> choice.disabled=false);
	 
	        CreatureWeapon weapon = new CreatureWeapon();
	        weapon.addFeature(new Feature(SplitterMondCore.getFeatureType("BLUNT"), 1));
	        weapon.setDamage(10600);
	        weapon.setSpeed(7);
	        weapon.setSkill(melee);
	        weapon.setValue(0);
	        modulBased.addWeapon(weapon);

	        // Creature types
	        for (CreatureTypeValue tmp : modulBased.getCreatureTypes())
	        	modulBased.addCreatureType(tmp);
	        
	        if (modulBased.getBase()!=null) {
	            logger.debug("1. Base");
	            modulBased.getBase().getModifications().stream().filter(mod -> !needsToBeAppliedLater(mod)).forEach(mod -> apply(modulBased, mod));
	        }
	        if (modulBased.getRole()!=null) {
	            logger.debug("2. Role = "+modulBased.getRole().getModule().getId());
	            modulBased.getRole().getModifications().stream().filter(mod -> !needsToBeAppliedLater(mod)).forEach(mod -> apply(modulBased, mod));
	        }

	        for (CreatureModuleReference ref : modulBased.getOptions()) {
	            logger.debug("3. Option: "+ref);
	            ref.getModifications().stream().filter(mod -> !needsToBeAppliedLater(mod)).forEach(mod -> apply(modulBased, mod));
	        }

	        if (modulBased.getRole()!=null) {
	            logger.debug("4. Late modifications Role");
	            modulBased.getRole().getModifications().stream().filter(mod -> needsToBeAppliedLater(mod)).forEach(mod -> apply(modulBased, mod));
	        }
	        for (CreatureModuleReference ref : modulBased.getOptions()) {
	            logger.debug("5. Late modifications Option: "+ref);
	            ref.getModifications().stream().filter(mod -> needsToBeAppliedLater(mod)).forEach(mod -> apply(modulBased, mod));
	        }

	        logger.debug("6. Choices");
	        for (CreatureModuleReference.NecessaryChoice ref : getChoicesToMake(modulBased)) {
	        	if (ref.disabled) {
	        		logger.debug("   * Choice disabled for: "+ref.originModule.getModule().getId()+"/"+ref.getOriginChoice());            
	        	} else if (ref.getMadeChoice()==null) {
	        		logger.debug("   * No choice made for: "+ref.originModule.getModule().getId()+"/"+ref.getOriginChoice());            
	        	} else {
	        		logger.debug("   * Choice made for: "+ref.originModule.getModule().getId()+"/"+ref.getOriginChoice()+" = "+ref.getMadeChoice());            
	        		apply(modulBased, ref.getMadeChoice());
	        	}
	        }
	        logger.debug("STOP : -----recalculate---------------------------");
	}

    //-------------------------------------------------------------------
    public static void calculateTrainings(CreatureReference creature) {
        logger.debug("START: -----calculateTrainings---------------------------");
        
        creature.clearTrainingModifications();
        
        for (CreatureModuleReference ref : creature.getTrainings()) {
	            logger.debug("1. Training: "+ref);
//	            ref.getModifications().forEach(mod -> CreatureTools.instantiateModification(null, ref, mod));

	            ref.getModifications()
	            	.stream()
//	            	.filter(mod -> !needsToBeAppliedLater(mod))
	            	.forEach(mod -> creature.addTrainingModifications(mod));
        }
    	
//        for (CreatureModuleReference ref : creature.getTrainings()) {
//            logger.debug("2. Late modifications trainings: "+ref);
//            ref.getModifications().stream().filter(mod -> needsToBeAppliedLater(mod)).forEach(mod -> apply(creature, mod));
//        }

        logger.debug("3. Choices");
        for (CreatureModuleReference ref2 : creature.getTrainings()) {
        	for (CreatureModuleReference.NecessaryChoice ref : ref2.getChoices()) {
        		if (ref.disabled) {
        			logger.debug("   * Choice disabled for: "+ref.originModule.getModule().getId()+"/"+ref.getOriginChoice());            
        		} else if (ref.getMadeChoice()==null) {
        			logger.debug("   * No choice made for: "+ref.originModule.getModule().getId()+"/"+ref.getOriginChoice());            
        		} else {
        			logger.debug("   * Choice made for: "+ref.originModule.getModule().getId()+"/"+ref.getOriginChoice()+" = "+ref.getMadeChoice());            
        			creature.addTrainingModifications(ref.getMadeChoice());
        		}
        	}
        }
        
        logger.debug("STOP : -----calculateTrainings---------------------------");
    }


    //-------------------------------------------------------------------
    public static void apply(ModuleBasedCreature modulBased, Modification mod) {
        logger.debug("   apply "+mod.getClass()+" / "+mod);
        if (mod instanceof AttributeModification) {
            AttributeModification aMod = (AttributeModification)mod;
            modulBased.getAttribute(aMod.getAttribute()).addModification(aMod);
        } else if (mod instanceof SkillModification) {
            SkillModification sMod = (SkillModification)mod;
            if (sMod.getSkill()==melee) {
                applyToWeapon(modulBased, modulBased.getCreatureWeapons().get(0), sMod);
            } else if (sMod.getSkill()!=null) {
            	if (modulBased.getSkillValue(sMod.getSkill())==null)
            		modulBased.addSkill(new SkillValue(sMod.getSkill(), 0));
            	modulBased.getSkillValue(sMod.getSkill()).addModification(sMod);
            } else {
            	for (Skill skill : SplitterMondCore.getSkills()) {
            		SkillValue sVal = modulBased.getSkillValue(skill);
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
        } else if (mod instanceof CreatureFeatureModification) {
            CreatureFeatureModification cfMod = (CreatureFeatureModification)mod;
            CreatureFeatureType type = cfMod.getFeature();
            if (type.hasLevels()) {
                CreatureFeature old = modulBased.getCreatureFeature(type);
                if (cfMod.isRemoved()) {
                    if (old!=null) {
                        old.setLevel(Math.max(0, old.getLevel() +cfMod.getLevel()) );
                        if (old.getLevel()==0)
                        	modulBased.removeCreatureFeature(type);
                    }
                } else {
                    if (old==null) {
                        old = modulBased.addCreatureFeature(type);
                        if (cfMod.getDamageType()!=null)
                        	old.setDamageType(cfMod.getDamageType());
                    }
                    old.setLevel(old.getLevel() +cfMod.getLevel());
                }
            } else {
                if (cfMod.isRemoved()) {
                	modulBased.removeCreatureFeature(type);
                } else {
                	modulBased.addCreatureFeature(type);
                    if (cfMod.getDamageType()!=null) {
                    	CreatureFeature old = modulBased.getCreatureFeature(type);
                    	old.setDamageType(cfMod.getDamageType());
                    }
               }
            }
        } else if (mod instanceof CreatureTypeModification) {
        	CreatureTypeModification foo = (CreatureTypeModification)mod;
        	CreatureType type = foo.getFeature();
            CreatureTypeValue old = modulBased.getCreatureType(type);
        	if (type.hasLevel()) {
                if (foo.isRemoved()) {
                    if (old!=null) {
                        old.setLevel(Math.max(0, old.getLevel() -foo.getLevel()) );
                        if (old.getLevel()==0)
                        	modulBased.removeCreatureType(old);
                    }
                } else {
                    if (old==null)
                        modulBased.addCreatureType(new CreatureTypeValue(type, foo.getLevel()));
                    else
                    	old.setLevel(old.getLevel() +foo.getLevel());
                }
            } else {
            	if (foo.isRemoved()) {
            		if (old!=null) 
           				modulBased.removeCreatureType(old);            		
                } else {
                	if (old==null) {
                       	modulBased.addCreatureType(new CreatureTypeValue(type));
                	}
                }
         	}
        } else if (mod instanceof DamageModification) {
            applyToWeapon(modulBased, modulBased.getCreatureWeapons().get(0), mod);
        } else if (mod instanceof ItemModification) {
            applyToWeapon(modulBased, modulBased.getCreatureWeapons().get(0), mod);
        } else if (mod instanceof ItemFeatureModification) {
            applyToWeapon(modulBased, modulBased.getCreatureWeapons().get(0), mod);
        } else if (mod instanceof MastershipModification) {
            applyMastershipModification(modulBased, (MastershipModification) mod);
        } else if (mod instanceof FeatureModification) {
            applyToWeapon(modulBased, modulBased.getCreatureWeapons().get(0), mod);
        } else if (mod instanceof CountModification) {
        	CountModification rcMod = (CountModification)mod;
        	int count = rcMod.getCount();
        	int done = 0;
        	switch (rcMod.getType()) {
        	case SPELL:
        		List<CreatureModuleReference.NecessaryChoice> choices = getChoicesToMake(modulBased);
        		// Filter out those that are not spell modifications
        		choices = choices.stream().filter(ev -> ev.getOriginChoice() instanceof SpellModification).collect(Collectors.toList());
        		logger.info("Pre-Sort : "+choices);
        		Collections.sort(choices, new Comparator<CreatureModuleReference.NecessaryChoice>() {
					public int compare(NecessaryChoice o1, NecessaryChoice o2) {
						Integer v1 = (o1.getOriginChoice() instanceof SpellModification)?(((SpellModification)o1.getOriginChoice()).getLevel()):0;
						Integer v2 = (o2.getOriginChoice() instanceof SpellModification)?(((SpellModification)o2.getOriginChoice()).getLevel()):0;
						return v1.compareTo(v2);
					}
				});
        		logger.info("Post-Sort: "+choices);
        		Collections.reverse(choices);
        		for (CreatureModuleReference.NecessaryChoice choice : choices) {
        			if (choice.disabled)
        				continue;
//        			logger.debug("Compare with SPELL "+choice.originChoice+" from "+choice.originModule.getModule().getId());
        			if (choice.getOriginChoice() instanceof SpellModification && done<count) {
        				logger.debug("  Disable choice: "+choice.getOriginChoice()+" // "+choice.getMadeChoice());
        				choice.disabled = true;
        				done++;
        			}
        		}
        		break;
        	case MASTERSHIP:
        		for (CreatureModuleReference.NecessaryChoice choice : getChoicesToMake(modulBased)) {
        			if (choice.disabled)
        				continue;
//        			logger.debug("Compare with MASTERSHIP "+choice.getOriginChoice()+" from "+choice.originModule.getModule().getId());
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
        	case WEAPON:
        		logger.debug("Add new weapon");
        		CreatureWeapon weapon = new CreatureWeapon();
                weapon.setSkill(melee);
                weapon.setValue(modulBased.getCreatureWeapons().get(0).getValue());
                weapon.setInitiative(modulBased.getCreatureWeapons().get(0).getInitiative());
        		switch (getInvestedCreaturePoints(modulBased)) {
        		case 1:
                    weapon.setDamage(10600);
                    weapon.setSpeed(7);
                    weapon.addFeature(new Feature(SplitterMondCore.getFeatureType("PIERCING"), 2));
                    break;
        		case 2:
                    weapon.setDamage(10601);
                    weapon.setSpeed(6);
                    weapon.addFeature(new Feature(SplitterMondCore.getFeatureType("PIERCING"), 2));
                    break;
        		case 3:
                    weapon.setDamage(10602);
                    weapon.setSpeed(7);
                    weapon.addFeature(new Feature(SplitterMondCore.getFeatureType("PIERCING"), 3));
                    break;
        		case 4:
                    weapon.setDamage(10604);
                    weapon.setSpeed(8);
                    weapon.addFeature(new Feature(SplitterMondCore.getFeatureType("PENETRATING"), 1));
                    weapon.addFeature(new Feature(SplitterMondCore.getFeatureType("PIERCING"), 3));
                    break;
        		case 5:
                    weapon.setDamage(20602);
                    weapon.setSpeed(8);
                    weapon.addFeature(new Feature(SplitterMondCore.getFeatureType("PENETRATING"), 2));
                    weapon.addFeature(new Feature(SplitterMondCore.getFeatureType("PIERCING"), 3));
                    break;
        		}
                modulBased.addWeapon(weapon);
                logger.debug("Added "+weapon);
                break;
        	default:
                logger.error("TODO: apply "+rcMod.getType());
                BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 2, "Not implemented yet: Applying CountModification for "+rcMod.getType());
        	}
        } else if (mod instanceof ModificationChoice) {
        	// If a ModificationChoice gets here, there should be not more than one possible option left
        	// Count valid choices
        	List<Modification> valid = new ArrayList<>();
        	for (Modification testMod : ((ModificationChoice)mod).getOptions()) {
        		if (test(modulBased, testMod)) {
//        			logger.debug("   option still valid: "+testMod);
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
        		apply(modulBased, valid.get(0));
        		return;
        	default:
        		logger.error("   Auto apply first option "+valid.get(0)+"  from "+valid.size()+" valid choices - this should not happen");
        		apply(modulBased, valid.get(0));
        		return;
        	}
        } else if (mod instanceof SubModificationChoice) {
        	// If a ModificationChoice gets here, there should be not more than one possible option left
        	// Count valid choices
        	List<Modification> valid = new ArrayList<>();
        	for (Modification testMod : ((SubModificationChoice)mod).getOptions()) {
        		if (test(modulBased, testMod)) {
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
        		apply(modulBased, valid.get(0));
        		return;
        	default:
        		logger.error("   Auto apply first option "+valid.get(0)+"  from "+valid.size()+" valid choices - this should not happen");
        		apply(modulBased, valid.get(0));
        		return;
        	}
        } else if (mod instanceof ConditionalModification) {
        	ConditionalModification condMod = (ConditionalModification)mod;
        	logger.debug("    Apply "+condMod.getTrueModification()+" IF "+condMod.getConditions()+" ELSE "+condMod.getElseModification());
        	boolean requirementsAreMet = true;
        	for (Requirement req : condMod.getConditions()) {
        		logger.debug("    * check "+req+" = "+modulBased.isRequirementMet(req));
        		if (!modulBased.isRequirementMet(req)) {
        			requirementsAreMet = false;
        			break;
        		}
        	}
        	if (requirementsAreMet) {
        		logger.debug("      All requirements are met");
        		for (Modification tmp : condMod.getTrueModification()) {
        			apply(modulBased, tmp);
        		}
        	} else if (condMod.getElseModification()!=null) {
        		logger.debug("      Some requirements are not met");
        		for (Modification tmp : condMod.getElseModification()) {
        			apply(modulBased, tmp);
        		}
        	}
        	
        } else if (mod instanceof SpellModification) {
        	SpellModification sMod = (SpellModification)mod;
        	logger.debug("    Apply "+sMod);
        	if (sMod.isRemove()) {
        		if (!modulBased.hasSpell(sMod.getSpell()))
        			logger.error("Trying to remove a spell that does not exist in model");
        		modulBased.removeSpell(sMod.getSpell());
        	} else {
        		modulBased.addSpell(sMod.getSpell());
        	}
        } else if (mod instanceof AllOfModification) {
        	AllOfModification aMod = (AllOfModification)mod;
        	logger.debug("    Apply all of "+aMod);
        	for (Modification toApply : aMod) {
        		apply(modulBased, toApply);
        	}
      } else {
            logger.warn("TODO: apply "+mod);
            logger.warn("TODO: apply "+mod.getClass());
            BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 2, "Not implemented yet: Applying "+mod);
        }
    }

    //-------------------------------------------------------------------
    public static void undo(ModuleBasedCreature modulBased, Modification mod) {
    	logger.debug("  undo "+mod.getClass()+" / "+mod);
    	if (mod instanceof AttributeModification) {
    		AttributeModification aMod = (AttributeModification)mod;
    		modulBased.getAttribute(aMod.getAttribute()).removeModification(aMod);
    	} else if (mod instanceof SkillModification) {
    		SkillModification sMod = (SkillModification)mod;
    		if (sMod.getSkill()==melee) {
    			CreatureTools.applyToWeapon(modulBased, modulBased.getCreatureWeapons().get(0), sMod);
    		} else {
    			modulBased.getSkillValue(sMod.getSkill()).removeModification(sMod);
    		}
    	} else if (mod instanceof CreatureFeatureModification) {
    		CreatureFeatureModification cfMod = (CreatureFeatureModification)mod;
    		CreatureFeatureType type = cfMod.getFeature();
    		if (type.hasLevels()) {
    			CreatureFeature old = modulBased.getCreatureFeature(type);
    			if (old==null) {
    				if (cfMod.isRemoved()) 
    					modulBased.addCreatureFeature(type);
    			} else {
    				if (cfMod.isRemoved()) {
    					old.setLevel(old.getLevel() +cfMod.getLevel() );
    				} else {
    					old.setLevel(old.getLevel() -cfMod.getLevel() );
    					if (old.getLevel()<=0)
    						modulBased.removeCreatureFeature(type);
    				}
    			}
    		} else {
    			if (cfMod.isRemoved()) {
    				modulBased.addCreatureFeature(type);
    			} else
    				modulBased.removeCreatureFeature(type);
    		}
    	} else if (mod instanceof DamageModification) {
    		undoFromWeapon(modulBased.getCreatureWeapons().get(0), mod);
        } else if (mod instanceof ItemModification) {
            CreatureTools.applyToWeapon(modulBased, modulBased.getCreatureWeapons().get(0), mod);
        } else if (mod instanceof ItemFeatureModification) {
        	CreatureTools.applyToWeapon(modulBased, modulBased.getCreatureWeapons().get(0), mod);
        } else if (mod instanceof MastershipModification) {
        	CreatureTools.undoMastershipModification(modulBased, (MastershipModification) mod);
        } else if (mod instanceof SpellModification) {
        	SpellModification sMod = (SpellModification)mod;
        	logger.debug("    Undo "+sMod);
        	if (sMod.isRemove()) {
        		if (modulBased.hasSpell(sMod.getSpell()))
        			logger.error("Trying to add a spell that already exists in model");
        		modulBased.addSpell(sMod.getSpell());
        	} else {
        		modulBased.removeSpell(sMod.getSpell());
        	}
        } else if (mod instanceof CountModification) {
        	CountModification rcMod = (CountModification)mod;
        	int count = rcMod.getCount();
        	int done = 0;
        	switch (rcMod.getType()) {
        	case SPELL:
        		for (CreatureModuleReference.NecessaryChoice choice : getChoicesToMake(modulBased)) {
        			if (!choice.disabled)
        				continue;
//        			logger.debug("Compare with SPELL "+choice.originChoice+" from "+choice.originModule.getModule().getId());
        			if (choice.getOriginChoice() instanceof SpellModification && done<count) {
        				logger.debug("  Enable choice: "+choice.getOriginChoice());
        				choice.disabled = false;
        				done++;
        			}
        		}
        		break;
        	case MASTERSHIP:
        		for (CreatureModuleReference.NecessaryChoice choice : getChoicesToMake(modulBased)) {
        			if (!choice.disabled)
        				continue;
        			logger.debug("Compare with MASTERSHIP "+choice.getOriginChoice()+" from "+choice.originModule.getModule().getId());
        			if (choice.getOriginChoice() instanceof MastershipModification && done<count) {
        				logger.debug("  Enable choice: "+choice.getOriginChoice());
        				choice.disabled = false;
        				done++;
        			}
        		}
        		break;
        	case CREATURE_FEATURE_POSITIVE:
        		logger.warn("TODO: add positive creature feature");
                System.exit(0);
        		break;
        	case WEAPON:
        		logger.debug("Remove a weapon");
        		if (modulBased.getCreatureWeapons().size()<=1) {
        			logger.error("Cannot remove another weapon from the creature - at least one must be left");
        			return;
        		}
        		CreatureWeapon weapon = modulBased.getCreatureWeapons().get(modulBased.getCreatureWeapons().size()-1);
                modulBased.removeWeapon(weapon);
                logger.debug("Removed "+weapon);
                break;
        	default:
                logger.warn("TODO: undo "+rcMod.getType());
                BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 2, "Not implemented yet: Undoing "+mod);
         	}
        } else if (mod instanceof AllOfModification) {
        	AllOfModification aMod = (AllOfModification)mod;
        	logger.debug("    Undo all of "+aMod);
        	for (Modification toUndo : aMod) {
        		undo(modulBased, toUndo);
        	}
        } else {
            logger.warn("TODO: don't know how to undo "+mod);
            BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 2, "Don't know how to deal with "+mod);
        }
    }

    //-------------------------------------------------------------------
    public static void applyToWeapon(ModuleBasedCreature modulBased, CreatureWeapon weapon, Modification mod) {
        if (mod instanceof SkillModification) {
            SkillModification sMod = (SkillModification)mod;
            weapon.setValue(weapon.getValue()+sMod.getValue());
        } else if (mod instanceof DamageModification) {
            DamageModification dMod = (DamageModification)mod;
        	logger.debug("apply "+dMod.getDamage()+" to "+weapon.getDamage());
            if (dMod.isSubstract()) {
            	int val = dMod.getDamage();
            	if (val<10) {
            		int tmp = weapon.getDamage()/100;
            		int old = weapon.getDamage()%100 - val;
            		if (old<0) old = 100 + old;
            		weapon.setDamage(tmp*100 + old);
            	} else {	
            		weapon.setDamage(weapon.getDamage() - val);
            	}
            } else {
            	int val = dMod.getDamage();
            	if (val<10) {
            		int tmp = weapon.getDamage()/100;
            		int old = weapon.getDamage()%100 + val;
            		if (old<0) old = 100 + old;
            		weapon.setDamage(tmp*100 + old);
            	} else {	
            		weapon.setDamage(weapon.getDamage() + val);
            	}
            }
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
            	logger.debug("apply "+iMod.getValue());
            	if (iMod.getValue()<0) {
            		int tmp = weapon.getDamage()/100;
            		int old = weapon.getDamage()%100 + iMod.getValue();
            		if (old<0) old = 100 + old;
            		weapon.setDamage(tmp*100 + old);
            	} else {	
            		weapon.setDamage(weapon.getDamage() + iMod.getValue());
            	}
                break;
            default:
                logger.warn("Don't know how to modify weapon attribute: "+mod);
                BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 2, "Not implemented yet: Don't know how to modify weapon attribute: "+mod);
            }
        } else if (mod instanceof ItemFeatureModification) {
            ItemFeatureModification ifMod = (ItemFeatureModification)mod;
            FeatureType type = ifMod.getFeature();
            logger.debug("apply "+ifMod+"   hasLevel="+type.hasLevel());
            if (type.hasLevel() || type.getId().equals("BLUNT")) {            	
                Feature old = weapon.getFeature(type);
                int lvl =ifMod.getLevel();
                if (lvl<1)
                	lvl=1;
                if (ifMod.isRemoved()) {
                	logger.debug("  remove "+lvl+" from "+old.getLevel());
                    if (old!=null) {
                        old.setLevel(Math.max(0, old.getLevel() -lvl) );
                        if (old.getLevel()==0)
                            weapon.removeFeature(type);
                    }
                } else {
                    if (old==null)
                        old = weapon.addFeature(type);
                    logger.debug(" add "+lvl+" to "+old.getLevel());
                    old.setLevel(old.getLevel() +lvl);
                }
                logger.debug("  new level of "+type+" is "+old.getLevel());
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
            BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 2, "Don't know how to apply to weapon: "+mod);
        }
    }

    //-------------------------------------------------------------------
    public static void applyToWeapon(CreatureReference modulBased, CreatureWeapon weapon, Modification mod) {
        if (mod instanceof SkillModification) {
            SkillModification sMod = (SkillModification)mod;
            weapon.setValue(weapon.getValue()+sMod.getValue());
        } else if (mod instanceof DamageModification) {
            DamageModification dMod = (DamageModification)mod;
        	logger.debug("apply "+dMod.getDamage()+" to "+weapon.getDamage());
            if (dMod.isSubstract()) {
            	int val = dMod.getDamage();
            	if (val<10) {
            		int tmp = weapon.getDamage()/100;
            		int old = weapon.getDamage()%100 - val;
            		if (old<0) old = 100 + old;
            		weapon.setDamage(tmp*100 + old);
            	} else {	
            		weapon.setDamage(weapon.getDamage() - val);
            	}
            } else {
            	int val = dMod.getDamage();
            	if (val<10) {
            		int tmp = weapon.getDamage()/100;
            		int old = weapon.getDamage()%100 + val;
            		if (old<0) old = 100 + old;
            		weapon.setDamage(tmp*100 + old);
            	} else {	
            		weapon.setDamage(weapon.getDamage() + val);
            	}
            }
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
            	logger.debug("apply "+iMod.getValue());
            	if (iMod.getValue()<0) {
            		int tmp = weapon.getDamage()/100;
            		int old = weapon.getDamage()%100 + iMod.getValue();
            		if (old<0) old = 100 + old;
            		weapon.setDamage(tmp*100 + old);
            	} else {	
            		weapon.setDamage(weapon.getDamage() + iMod.getValue());
            	}
                break;
            default:
                logger.warn("Don't know how to modify weapon attribute: "+mod);
                BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 2, "Not implemented yet: Don't know how to modify weapon attribute: "+mod);
            }
        } else if (mod instanceof ItemFeatureModification) {
            ItemFeatureModification ifMod = (ItemFeatureModification)mod;
            FeatureType type = ifMod.getFeature();
            logger.debug("apply "+ifMod+"   hasLevel="+type.hasLevel());
            if (type.hasLevel() || type.getId().equals("BLUNT")) {            	
                Feature old = weapon.getFeature(type);
                int lvl =ifMod.getLevel();
                if (lvl<1)
                	lvl=1;
                if (ifMod.isRemoved()) {
                	logger.debug("  remove "+lvl+" from "+old.getLevel());
                    if (old!=null) {
                        old.setLevel(Math.max(0, old.getLevel() -lvl) );
                        if (old.getLevel()==0)
                            weapon.removeFeature(type);
                    }
                } else {
                    if (old==null)
                        old = weapon.addFeature(type);
                    logger.debug(" add "+lvl+" to "+old.getLevel());
                    old.setLevel(old.getLevel() +lvl);
                }
                logger.debug("  new level of "+type+" is "+old.getLevel());
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
            BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 2, "Don't know how to apply to weapon: "+mod);
        }
    }

    //-------------------------------------------------------------------
    private static void undoFromWeapon(CreatureWeapon weapon, Modification mod) {
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
                BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 2, "Don't know how to modify weapon attribute: "+mod);
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
	public static List<CreatureModuleReference.NecessaryChoice> getChoicesToMake(ModuleBasedCreature modulBased) {
    	List<NecessaryChoice> ret = new ArrayList<>();
    	if (modulBased.getRole()!=null)
    		ret.addAll(modulBased.getRole().getChoices());
    	for (CreatureModuleReference tmp : modulBased.getOptions())
    		ret.addAll(tmp.getChoices());
    	
    	return ret;
	}

    //-------------------------------------------------------------------
	public static boolean needsToBeAppliedLater(Modification mod) {
        if (mod instanceof CreatureFeatureModification) return ((CreatureFeatureModification)mod).isRemoved();
        if (mod instanceof FeatureModification) return ((FeatureModification)mod).isRemoved();
        if (mod instanceof CountModification) return true;
        if (mod instanceof ModificationChoice) return true;
        if (mod instanceof SkillModification) {
        	SkillModification sMod = (SkillModification)mod;
        	return (sMod.getSkill()==null);
        } 
        if (mod instanceof SpellModification) {
        	SpellModification sMod = (SpellModification)mod;
        	return (sMod.getSpell()==null);
        } 
   	
        return false;
    }

    //-------------------------------------------------------------------
    public static boolean needsToBeChosen(ModuleBasedCreature modulBased, Modification mod) {
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
//        	logger.debug("Needs to be chosen("+mod+") will return "+(((SkillModification)mod).getSkill()==null));
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
        		if (test(modulBased, testMod)) {
//        			logger.debug("..option still valid: "+testMod);
        			valid.add(testMod);
        		} else {
        			logger.debug("..option is invalid: "+testMod);
        		}
        	}
        	logger.debug("START: needsToBeChosen "+mod+" = "+(valid.size()>1));
        	return (valid.size()>1);
        } else if (mod instanceof SpellModification) {
        	SpellModification sMod = (SpellModification)mod;
        	logger.debug("NeedsToBeChosen: "+sMod+" / "+sMod.getSpell());
        	if (sMod.getSpell()!=null)
        		return false;
        	else {
        		return true;
        	}
        } else if (mod instanceof ConditionalModification) {
        	ConditionalModification cMod = (ConditionalModification)mod;
        	for (Modification tmp : cMod.getTrueModification()) {
        		if (needsToBeChosen(modulBased, tmp))
        			return true;
        	}
        	return false;
        } else {
            logger.warn("Don't know how to handle "+mod.getClass()+" // "+mod);
            System.exit(0);
        }
    	return false;
    }

    //-------------------------------------------------------------------
    public static void instantiateModification(ModuleBasedCreature modulBased, CreatureModuleReference ref, Modification mod) {
    	logger.debug("  instantiate "+mod);
    	if (CreatureTools.needsToBeChosen(modulBased, mod)) {
    		NecessaryChoice choice = new CreatureModuleReference.NecessaryChoice();
    		choice.originModule = ref;
    		choice.setOriginChoice(mod);
    		ref.addChoice(choice);
//    		choices.add(choice);
    		logger.debug("  Added choice for "+mod);
    	} else {
    		ref.addModification(mod);
    		logger.debug("  Added modification "+mod);
    	}
    }

	//-------------------------------------------------------------------
	/**
	 * Check if the given modification can be applied. E.g. a modification
	 * to remove a mastership the creature doesn't have, isn't valid.
	 * 
	 * @see java.util.function.Predicate#test(java.lang.Object)
	 */
//	@Override
	private static boolean test(ModuleBasedCreature modulBased, Modification mod) {
//		logger.warn("TODO: "+mod);
        if (mod instanceof CreatureFeatureModification) {
        	CreatureFeatureModification cMod = (CreatureFeatureModification)mod;
        	if (cMod.isRemoved()) {
        		CreatureFeature feat = modulBased.getCreatureFeature(cMod.getFeature());
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
    public static void applyMastershipModification(ModuleBasedCreature modulBased, MastershipModification mod) {
        if (mod.getMastership()!=null) {
        	// Apply given mastership
        	Mastership master = mod.getMastership();
        	Skill skill = mod.getSkill();
        	SkillValue sVal = modulBased.getSkillValue(skill);
            logger.debug("  add mastership "+master+" to "+skill.getId());
        	sVal.addMastership(new MastershipReference(master));
        	
        }
    }

    //-------------------------------------------------------------------
    public static  void undoMastershipModification(ModuleBasedCreature modulBased, MastershipModification mod) {
        if (mod.getMastership()!=null) {
        	// Undo given mastership
        	Mastership master = mod.getMastership();
        	Skill skill = mod.getSkill();
        	SkillValue sVal = modulBased.getSkillValue(skill);
            logger.debug("  remove mastership "+master+" from "+skill.getId());
         	sVal.removeMastership(master);
        }
    }

}
