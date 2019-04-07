/**
 *
 */
package org.prelle.splimo.chargen.creature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.DamageType;
import org.prelle.splimo.Mastership;
import org.prelle.splimo.ResourceReference;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SkillValue;
import org.prelle.splimo.Spell;
import org.prelle.splimo.SpellValue;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.CreatureController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.creature.CreatureFeature;
import org.prelle.splimo.creature.CreatureFeatureType;
import org.prelle.splimo.creature.CreatureModule;
import org.prelle.splimo.creature.CreatureModule.Type;
import org.prelle.splimo.creature.CreatureModuleReference;
import org.prelle.splimo.creature.CreatureReference;
import org.prelle.splimo.creature.CreatureTools;
import org.prelle.splimo.creature.CreatureType;
import org.prelle.splimo.creature.CreatureTypeValue;
import org.prelle.splimo.creature.ModuleBasedCreature;
import org.prelle.splimo.modifications.ConditionalModification;
import org.prelle.splimo.modifications.CountModification;
import org.prelle.splimo.modifications.CreatureFeatureModification;
import org.prelle.splimo.modifications.MastershipModification;
import org.prelle.splimo.modifications.ModificationChoice;
import org.prelle.splimo.modifications.ModificationList;
import org.prelle.splimo.modifications.SkillModification;
import org.prelle.splimo.modifications.SpellModification;
import org.prelle.splimo.requirements.Requirement;

import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;
import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class CreatureGenerator implements CreatureController {
	
	private static Logger logger = LogManager.getLogger("splittermond.chargen");

    private Skill melee;

    private ModuleBasedCreature modulBased;
    private CreatureReference model;

    //-------------------------------------------------------------------
    public CreatureGenerator(ResourceReference resource) {
    	logger.info("Start CreatureGenerator for resource "+resource);
    	modulBased = new ModuleBasedCreature();
    	modulBased.addCreatureType(new CreatureTypeValue(SplitterMondCore.getCreatureType("ANIMAL")));
    	model = new CreatureReference(modulBased);
    	model.setResource(resource);

        melee = SplitterMondCore.getSkill("melee");
         
        update();
    }

    //-------------------------------------------------------------------
    public CreatureGenerator(CreatureReference ref) {
    	if (ref.getEntourage()!=null)
    		throw new IllegalArgumentException("Not for entourages");
    	model = ref;
    	logger.info("Start CreatureGenerator for existing creature");
    	modulBased = ref.getModuleBasedCreature();
        melee = SplitterMondCore.getSkill("melee");
//        modulBased.getOptions() = model.getOptions()
        logger.warn("*************TODO: import chosen modules from creature");
        
        update();
    }

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.CreatureController#isRequirementMet(org.prelle.splimo.requirements.Requirement)
	 */
	@Override
	public boolean isRequirementMet(Requirement req) {
		try {
			return modulBased.isRequirementMet(req);
		} catch (Exception e) {
			logger.error("Cannot check requirement "+req,e);
		}
		return true;	
	}

    //-------------------------------------------------------------------
    private void update() {
        logger.debug("START: -----recalculate---------------------------");
        
        CreatureTools.calculateModuleBasedCreature(modulBased);
//        modulBased.clear();
////        modulBased.setBase(base);
////        modulBased.setRole(role);
//        apply(new CreatureFeatureModification(SplitterMondCore.getCreatureFeatureType("CREATURE"), getInvestedCreaturePoints()));
//        
//        // Reset all disabled choices
//        getChoicesToMake().forEach(choice -> choice.disabled=false);
// 
//        CreatureWeapon weapon = new CreatureWeapon();
//        weapon.addFeature(new Feature(SplitterMondCore.getFeatureType("BLUNT"), 1));
//        weapon.setDamage(10600);
//        weapon.setSpeed(7);
//        weapon.setSkill(melee);
//        weapon.setValue(0);
//        modulBased.addWeapon(weapon);
//
//        // Creature types
//        for (CreatureTypeValue tmp : modulBased.getCreatureTypes())
//        	modulBased.addCreatureType(tmp);
//        
//        if (modulBased.getBase()!=null) {
//            logger.debug("1. Base");
//            modulBased.getBase().getModifications().stream().filter(mod -> !needsToBeAppliedLater(mod)).forEach(mod -> apply(mod));
//        }
//        if (modulBased.getRole()!=null) {
//            logger.debug("2. Role");
//            modulBased.getRole().getModule().getModifications().stream().filter(mod -> !needsToBeAppliedLater(mod)).forEach(mod -> apply(mod));
//        }
//
//        for (CreatureModuleReference ref : modulBased.getOptions()) {
//            logger.debug("3. Option: "+ref);
//            ref.getModifications().stream().filter(mod -> !needsToBeAppliedLater(mod)).forEach(mod -> apply(mod));
//        }
//
//        if (modulBased.getRole()!=null) {
//            logger.debug("4. Late modifications Role");
//            modulBased.getRole().getModule().getModifications().stream().filter(mod -> needsToBeAppliedLater(mod)).forEach(mod -> apply(mod));
//        }
//        for (CreatureModuleReference ref : modulBased.getOptions()) {
//            logger.debug("5. Late modifications Option: "+ref);
//            ref.getModifications().stream().filter(mod -> needsToBeAppliedLater(mod)).forEach(mod -> apply(mod));
//        }
//
//        logger.debug("6. Choices");
//        for (CreatureModuleReference.NecessaryChoice ref : getChoicesToMake()) {
//        	if (ref.disabled) {
//        		logger.debug("  Choice disabled for: "+ref.originModule.getModule().getId()+"/"+ref.getOriginChoice());            
//        	} else if (ref.getMadeChoice()==null) {
//        		logger.debug("  No choice made for: "+ref.originModule.getModule().getId()+"/"+ref.getOriginChoice());            
//        	} else {
//        		logger.debug("  Choice made for: "+ref.originModule.getModule().getId()+"/"+ref.getOriginChoice()+" = "+ref.getMadeChoice());            
//        		apply(ref.getMadeChoice());
//        	}
//        }

        GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.CREATURE_CHANGED, model));
        logger.debug("STOP : -----recalculate---------------------------");
    }

    //-------------------------------------------------------------------
    /**
     * @see org.prelle.splimo.charctrl.CreatureController#getAvailableCreaturePoints()
     */
    @Override
    public int getAvailableCreaturePoints() {
    	int pointsSpent = 0;
    	if (modulBased.getBase()!=null) pointsSpent++;
    	if (modulBased.getRole()!=null) pointsSpent++;
		for (CreatureModuleReference opt : modulBased.getOptions()) {
			pointsSpent += opt.getModule().getCost();
		}
   	
		if (model.getResource()!=null) {
    		// Calculate available points
    		return model.getResource().getValue() -pointsSpent;
    	} else {
    		// Return points spent to far
    		return pointsSpent;
    	}
    }

	//-------------------------------------------------------------------
	public boolean canBeFinished() {
		if (model.getResource()==null)
			return true;
		
		return getAvailableCreaturePoints()==0;
	}

    //-------------------------------------------------------------------
    /**
     * @see org.prelle.splimo.charctrl.CreatureController#getBases()
     */
    @Override
    public List<CreatureModule> getBases() {
        List<CreatureModule> ret = new ArrayList<CreatureModule>();
        SplitterMondCore.getCreatureModules().forEach(mod -> {if (mod.getType()==CreatureModule.Type.BASE) ret.add(mod);});
        Collections.sort(ret);
        return ret;
    }

    //-------------------------------------------------------------------
    /**
     * @see org.prelle.splimo.charctrl.CreatureController#selectBase(org.prelle.splimo.creature.CreatureModule)
     */
    @Override
    public void selectBase(CreatureModule base) {
    	logger.info("Select base "+base.getName());
    	if (base.getType()!=Type.BASE)
    		throw new IllegalArgumentException("Not of type BASE");
    	CreatureModuleReference ref = new CreatureModuleReference(base);
    	ref.setUniqueId(null);
    	base.getModifications().forEach(mod -> CreatureTools.instantiateModification(modulBased, ref, mod));
    	modulBased.setBase(ref);

        update();
    }

    //-------------------------------------------------------------------
    /**
     * @see org.prelle.splimo.charctrl.CreatureController#getRoles()
     */
    @Override
    public List<CreatureModule> getRoles() {
        List<CreatureModule> ret = new ArrayList<CreatureModule>();
        SplitterMondCore.getCreatureModules().forEach(mod -> {if (mod.getType()==CreatureModule.Type.ROLE) ret.add(mod);});
        Collections.sort(ret);
        return ret;
    }

    //-------------------------------------------------------------------
    /**
     * @see org.prelle.splimo.charctrl.CreatureController#selectRole(org.prelle.splimo.creature.CreatureModule)
     */
    @Override
    public void selectRole(CreatureModule role) {
        logger.info("Select role "+role.getName());
        if (role.getType()!=Type.ROLE)
            throw new IllegalArgumentException("Not of type ROLE");
        
        // Since choices reside in the CreatureModuleReference now, they 
        // don't need to be undone when ref. changes
//        for (CreatureModuleReference.NecessaryChoice tmp : new ArrayList<>(choices)) {
//        	if (tmp.originModule==this.role) {
//        		logger.debug("Remove choice "+tmp);
//        		choices.remove(tmp);
//        	}
//        }
        CreatureModuleReference ref = new CreatureModuleReference(role);
    	ref.setUniqueId(null);
        role.getModifications().forEach(mod -> CreatureTools.instantiateModification(modulBased, ref, mod));
        modulBased.setRole(ref);

        update();
    }
	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.CreatureController#getSelectedRole()
	 */
	public CreatureModuleReference getSelectedRole() {
		return modulBased.getRole();
	}

    //-------------------------------------------------------------------
    /**
     * @see org.prelle.splimo.charctrl.CreatureController#getAvailableOptions()
     */
    @Override
    public List<CreatureModule> getAvailableOptions() {
        List<CreatureModule> ret = new ArrayList<CreatureModule>();
        SplitterMondCore.getCreatureModules().forEach(mod -> {
        	if (mod.getType()==CreatureModule.Type.OPTION && !modulBased.hasOption(mod)) {
        		// Option has not been chosen yet, so it might be available
//        		if (canSelect(mod))        		
        			ret.add(mod);
//        		else
//        			logger.warn("Mark "+mod.getId()+" is not available");
        	}
        });
        Collections.sort(ret);
        return ret;
    }

    //-------------------------------------------------------------------
    /**
     * @see org.prelle.splimo.charctrl.CreatureController#canSelect(org.prelle.splimo.creature.CreatureModule)
     */
    @Override
    public boolean canSelect(CreatureModule option) {
        // Can be only selected once
        for (CreatureModuleReference mod : modulBased.getOptions()) {
            if (mod.getModule()==option)
                return false;
        }

		for (Requirement req : option.getRequirements()) {
			boolean isMet = isRequirementMet(req);
			logger.trace("Requirement "+req+" of "+option.getId()+" is met = "+isMet);
			if (!isMet)
				return false;
		}

		return true;
    }

    //-------------------------------------------------------------------
    public boolean canDeselect(CreatureModuleReference option) {
        // Can be only selected once
        for (CreatureModuleReference mod : modulBased.getOptions()) {
            if (mod==option)
                return true;
        }

        return false;
    }

//    //-------------------------------------------------------------------
//    private List<Modification> instantiateModification(SkillModification mod) {
//    	logger.debug("instantiate "+mod);
//    	if (needsToBeChosen(mod)) {
//    		NecessaryChoice choice = new NecessaryChoice();
//    		choice.originModule = ref;
//    		choice.getOriginChoice() = mod;
//    		choices.add(choice);
//    		logger.debug("Added choice "+mod);
//    	} else {
//    		ref.addModification(mod);
//    	}
//    }
//
//    //-------------------------------------------------------------------
//    private void instantiateModification(CreatureModuleReference ref, Modification mod) {
//    	logger.debug("instantiate "+mod);
//    	if (CreatureTools.needsToBeChosen(modulBased, mod)) {
//    		NecessaryChoice choice = new CreatureModuleReference.NecessaryChoice();
//    		choice.originModule = ref;
//    		choice.setOriginChoice(mod);
//    		ref.addChoice(choice);
////    		choices.add(choice);
//    		logger.debug("Added choice for "+mod);
//    	} else {
//    		ref.addModification(mod);
//    		logger.debug("Added modification "+mod);
//    	}
//    }

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
        option.getModifications().forEach(mod -> CreatureTools.instantiateModification(modulBased, ref, mod));
        modulBased.addOption(ref);

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

        modulBased.removeOption(option);
        
//        for (CreatureModuleReference.NecessaryChoice tmp : new ArrayList<>(choices)) {
//        	if (tmp.originModule==option) {
//        		logger.debug("Remove choice "+tmp);
//        		choices.remove(tmp);
//        	}
//        }

        update();
    }

    //-------------------------------------------------------------------
    /**
     * @see org.prelle.splimo.charctrl.CreatureController#getCreature()
     */
    @Override
    public CreatureReference getCreature() {
        return model;
    }

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.CreatureController#getSelectedOptions()
	 */
	@Override
	public List<CreatureModuleReference> getSelectedOptions() {
		return new ArrayList<>(modulBased.getOptions());
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.CreatureController#getChoicesToMake()
	 */
	@Override
	public List<CreatureModuleReference.NecessaryChoice> getChoicesToMake() {
		return CreatureTools.getChoicesToMake(modulBased);
//    	List<NecessaryChoice> ret = new ArrayList<>();
//    	if (modulBased.getRole()!=null)
//    		ret.addAll(modulBased.getRole().getChoices());
//    	for (CreatureModuleReference tmp : modulBased.getOptions())
//    		ret.addAll(tmp.getChoices());
//    	
//    	return ret;
////		return new ArrayList<>(choices);
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
			CreatureTools.undo(modulBased, choice.getMadeChoice());
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
		
		
		choice.setMadeChoice(chosenOption);
		CreatureTools.apply(modulBased, chosenOption);
    	update();
	}

	//-------------------------------------------------------------------
	private List<SkillModification> instantiateModification(SkillModification smod) {
		List<SkillModification> ret = new ArrayList<>();
		List<Skill> options = (smod.getChoiceType()!=null)?SplitterMondCore.getSkills(smod.getChoiceType()):SplitterMondCore.getSkills();
		for (Skill skill : options) {
			switch (smod.getRestrictionType()) {
			case MUST_EXIST:
				if (modulBased.hasSkill(skill))
					ret.add(new SkillModification(skill, smod.getValue()));
				break;
			case MUST_NOT_EXIST:
				if (!modulBased.hasSkill(skill))
					ret.add(new SkillModification(skill, smod.getValue()));
				break;
			case ANY:
				ret.add(new SkillModification(skill, smod.getValue()));
				break;
			case ANY_EXIST:
				logger.warn("SkillModification.ANY_EXIST should not be instantiated here");
				if (modulBased.hasSkill(skill))
					ret.add(new SkillModification(skill, smod.getValue()));
				break;
				
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
				if (mmod.getSkillType()!=null && mmod.getSkillType()!=skill.getType())
					continue;
				SkillValue sVal = modulBased.getSkillValue(skill);
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
			for (SkillValue sVal : modulBased.getSkills(SkillType.MAGIC)) {
				logger.debug("* School "+sVal);
				for (Spell spell : SplitterMondCore.getSpells(sVal.getSkill())) {
					if (spell.getLevelInSchool(sVal.getSkill())>level)
						continue;
					SpellValue toAdd = new SpellValue(spell, sVal.getSkill());
					if (modulBased.hasSpell(toAdd))
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
		} else if (toChoose.getOriginChoice() instanceof CountModification) {
			CountModification cMod = (CountModification)toChoose.getOriginChoice();
			ModificationChoice choice = new ModificationChoice(Math.abs(cMod.getCount()));
			boolean remove = cMod.getCount()<0;
			logger.debug("Build list from "+cMod);
			switch (cMod.getType()) {
			case CREATURE_FEATURE_POSITIVE:
				if (remove) {
					logger.debug("  find all positive features currently in the creature");
					for (CreatureFeature feat : model.getFeatures()) {
						if (feat.getType().isNegative())
							continue;
						if (feat.getType().getId().equals("CREATURE"))
							continue;
						logger.debug("  .."+feat);
						CreatureFeatureModification toAdd = new CreatureFeatureModification(feat.getType(), remove);
						if (feat.getDamageType()!=null)
							toAdd.setDamageType(feat.getDamageType());
						choice.add(toAdd);
					}
				} else {
					logger.debug("  find all positive features currently not in the creature");
					for (CreatureFeatureType type : SplitterMondCore.getCreatureFeatureTypes()) {
						if (type.isNegative())
							continue;
						// Are there sub-selections necessary (e.g. DamageType)
						if (type.getSelect()!=null) {
							switch (type.getSelect()) {
							case DAMAGE_TYPE:
								for (DamageType dmg : DamageType.values()) {
									if (dmg==DamageType.PROFANE)
										continue;
									CreatureFeatureModification toAdd = new CreatureFeatureModification(type, 1);
									toAdd.setDamageType(dmg);
									choice.add(toAdd);
								}
								break;
							}
						} else {
							choice.add(new CreatureFeatureModification(type, false));
						}
					}
				}
				break;
			default:
				logger.fatal("Don't know how to create a selection for "+cMod);
	            BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 2, "Don't know how to create a selection for "+cMod);		
			}
			logger.debug("converted to "+choice);
			return choice;
		} else if (toChoose.getOriginChoice() instanceof ConditionalModification) {
			ConditionalModification cMod = (ConditionalModification)toChoose.getOriginChoice();
			boolean isMet = true;
			for (Requirement req : cMod.getConditions()) {
				if (!isRequirementMet(req)) {
					isMet = false;
					break;
				}
			}
			ModificationList toInstantiate = (isMet)?cMod.getTrueModification():cMod.getElseModification();
			if (toInstantiate.size()!=1 || !(toInstantiate.get(0) instanceof ModificationChoice)) {
				logger.error("Can only instantiate ConditionalModifications with one ModificationChoice(selmod) in ModificationList");
			} else {
				logger.debug("  instantiate "+toInstantiate.get(0)+" from condmod");
				return (ModificationChoice) toInstantiate.get(0);
			}
		} else {
			logger.fatal("Don't know how to convert "+toChoose.getOriginChoice());
            BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 2, "Don't know how to deal with "+toChoose.getOriginChoice());
		}
		
		return null;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.CreatureController#getCreatureTypes()
	 */
	@Override
	public List<CreatureTypeValue> getCreatureTypes() {
		return modulBased.getCreatureTypes();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.CreatureController#hasCreatureType(org.prelle.splimo.creature.CreatureType)
	 */
	@Override
	public boolean hasCreatureType(CreatureType type) {
		for (CreatureTypeValue tmp : modulBased.getCreatureTypes()) {
			if (tmp.getType()==type)
				return true;
		}
		return false;
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.CreatureController#setCreatureTypes(java.util.List)
	 */
	@Override
	public void setCreatureTypes(List<CreatureTypeValue> types) {
		modulBased.setCreatureTypes(types);
		update();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.charctrl.CreatureController#getAvailableTrainings()
	 */
	@Override
	public List<CreatureModule> getAvailableTrainings() {
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
	public List<CreatureModuleReference> getSelectedTrainings() {
		return new ArrayList<>(model.getTrainings());
	}

}
