package org.prelle.splimo.chargen.jfx;

import org.prelle.javafx.ScreenManager;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.CultureLoreReference;
import org.prelle.splimo.Resource;
import org.prelle.splimo.ResourceReference;
import org.prelle.splimo.Skill;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.chargen.SpliMoCharacterGenerator;
import org.prelle.splimo.chargen.creature.CreatureGenerator;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.creature.Creature;
import org.prelle.splimo.equip.ItemLevellerAndGenerator;
import org.prelle.splimo.items.CarriedItem;
import org.prelle.splimo.items.Enhancement.EnhancementType;
import org.prelle.splimo.items.EnhancementReference;
import org.prelle.splimo.levelling.CharacterLeveller;
import org.prelle.splimo.modifications.AttributeModification;
import org.prelle.splimo.modifications.ModificationChoice;
import org.prelle.splimo.modifications.SkillModification;
import org.prelle.splimo.npc.CreatureTypeController;
import org.prelle.splimo.npc.NPCGenerator;
import org.prelle.splittermond.chargen.jfx.ViewMode;
import org.prelle.splittermond.chargen.jfx.attributes.AttributesView;
import org.prelle.splittermond.chargen.jfx.creatures.CreatureCreateDialog;
import org.prelle.splittermond.chargen.jfx.dialogs.EditCarriedItemDialog;
import org.prelle.splittermond.chargen.jfx.dialogs.MastershipDialog;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MyStarter extends Application {

	@SuppressWarnings("unused")
	@Override
	public void start(Stage stage) throws Exception {
		SpliMoCharacter model = new SpliMoCharacter();
		CharacterController charGen = null;
		ScreenManager manager = new ScreenManager();

		Parent dia = null;
		int variant = 19;
		switch (variant) {
		case 0:
			Skill skill1 = SplitterMondCore.getSkill("blades");
			Skill skill2 = SplitterMondCore.getSkill("deathmagic");
			Skill skill3 = SplitterMondCore.getSkill("hunting");
			model.setExperienceFree(30);
			model.setExperienceInvested(200);
			charGen = new CharacterLeveller(model, null);
			dia = new MastershipDialog(charGen);
			((MastershipDialog)dia).setData(model, model.getSkillValue(skill1));
			break;
		case 1:
			charGen = new SpliMoCharacterGenerator(model, null);
			((SpliMoCharacterGenerator)charGen).apply(new AttributeModification(Attribute.AGILITY, 1));
			model.getAttribute(Attribute.WILLPOWER).setStart(3);
			model.getAttribute(Attribute.WILLPOWER).setDistributed(3);
			dia = new AttributesView(charGen);
			break;
//		case 5:
//			model.setExperienceFree(50);
//			model.setExperienceInvested(200);
//			model.getSkillValue(SplitterMondCore.getSkill("empathy")).setValue(3);
//			charGen = new CharacterLeveller(model, null);
//			dia = new SkillPane(null, charGen.getSkillController(), charGen.getMastershipController(), true, SkillType.NORMAL);
//			((SkillPane)dia).setContent(model);
//			break;
//		case 6:
//			charGen = new SpliMoCharacterGenerator(model, null);
//			dia = new DistributeSkillsPage(null, model, (SpliMoCharacterGenerator) charGen);
//			break;
//		case 7:
//			dia = new RewardDialog();
//			break;
//		case 8:
//			model.setExperienceFree(20);
//			model.addCultureLore(new CultureLoreReference(SplitterMondCore.getCultureLore("borombri")));
//			charGen = new CharacterLeveller(model, null);
//			dia = new CultureLorePane(((CharacterLeveller)charGen).getCultureLoreController());
//			((CultureLorePane)dia).setData(model);
//			break;
//		case 10:
//			charGen = new SpliMoCharacterGenerator(model, null);
//			model.setRace("gnome");
//			model.setEducation("elementalist");
//			model.setCulture("patalis");
//			model.setBackground("academics");
//			dia = new BaseDataBlockSpliMo(ViewMode.MODIFICATION, charGen);
//			((BaseDataBlockSpliMo)dia).setManager(new ScreenManager());
//			((BaseDataBlockSpliMo)dia).setData(model);
//			break;
//		case 11:
//			Creature creature = SplitterMondCore.getCreature("phantom");
////			creature.initializeDefaultSkills();
//			dia = new CreaturePane(ViewMode.MODIFICATION);
//			((CreaturePane)dia).setData(creature);
//			break;
		case 12:
			charGen = new CharacterLeveller(model, null);
			model.setExperienceFree(15);
			CarriedItem item = new CarriedItem();
//			item.setItem(SplitterMondCore.getItem("falchion"));
//			item.addEnhancement(new EnhancementReference(SplitterMondCore.getEnhancement("tickmalus")));
//			item.addEnhancement(new EnhancementReference(SplitterMondCore.getEnhancement("damage")));
//			item.setCustomName("Tolles Ding");
//			model.addItem(item);
//			ResourceReference rref = charGen.getResourceController().openResource(SplitterMondCore.getResource("rank"));
//			rref.setDescription("Wächterbund");
////			charGen.getResourceController().increase(rref);
//			model.getResources().add(rref);
//			ResourceScreen screen = new ResourceScreen(charGen, manager);
//			dia = manager;
////			manager.show(screen);
//////			dia = new ResourcePane2(charGen.getResourceController(), true);
//////			((ResourcePane2)dia).setManager(manager);
//////			((ResourcePane2)dia).setData(model);
////			screen.setData(model);
			break;
		case 13:
			item = new CarriedItem();
//			item.setItem(SplitterMondCore.getItem("falchion"));
//			item.addEnhancement(new EnhancementReference(SplitterMondCore.getEnhancement("tickmalus")));
			item.setItem(SplitterMondCore.getItem("falchion"));
			item.addEnhancement(new EnhancementReference(SplitterMondCore.getEnhancement("load")));
//			LogManager.getLogger("splittermond.chargen").setLevel(Level.DEBUG);
			ItemLevellerAndGenerator itemLvl = new ItemLevellerAndGenerator(item, 3);
//			ItemGeneratorPane pane = new ItemGeneratorPane();
//			GenerationEventDispatcher.addListener(pane);
//			pane.setData(itemLvl);
//			dia = pane;
			break;
//		case 14:
//			ModificationChoice choice = new ModificationChoice(new SkillModification("blades", 0), new SkillModification("empathy", 0));
//			choice.setValues(new int[]{2,1});
//			UserDistributeDialog ddia = new UserDistributeDialog("Händler", choice);
//			dia = manager;
//			manager.show(ddia);
//			break;
//		case 15:
//			Creature modCreat = new Creature("test");
//			CreatureTypePane ctPane = new CreatureTypePane(new CreatureTypeController(modCreat));
//			ctPane.setData(modCreat);
//			dia = ctPane;
//			break;
		case 16:
			item = new CarriedItem();
			item.setResource(new ResourceReference(new Resource(), 4));
//			item.setItem(SplitterMondCore.getItem("falchion"));
//			item.addEnhancement(new EnhancementReference(SplitterMondCore.getEnhancement("tickmalus")));
			item.setItem(SplitterMondCore.getItem("peacockfeather"));
			item.setCustomName("Leichtigkeit");
			item.addEnhancement(new EnhancementReference(SplitterMondCore.getEnhancement("load")));
//			Logger.getLogger("splittermond.chargen").setLevel(Level.DEBUG);
			itemLvl = new ItemLevellerAndGenerator(item, 6);
//			NewItemGeneratorPane pane2 = new NewItemGeneratorPane();
//			pane2.setScreenManager(manager);
//			GenerationEventDispatcher.addListener(pane2);
//			pane2.setData(itemLvl);
//			dia = pane2;
			EditCarriedItemDialog screen2 = new EditCarriedItemDialog(null, itemLvl);
			dia = manager;
			manager.showAndWait(screen2);
			break;
//		case 17:
//			item = new CarriedItem();
////			item.setItem(SplitterMondCore.getItem("falchion"));
////			item.addEnhancement(new EnhancementReference(SplitterMondCore.getEnhancement("tickmalus")));
//			item.setItem(SplitterMondCore.getItem("falchion"));
//			item.addEnhancement(new EnhancementReference(SplitterMondCore.getEnhancement("load")));
////			Logger.getLogger( "splittermond.chargen").setLevel(Level.DEBUG);
//			itemLvl = new ItemLevellerAndGenerator(item, 3);
//			EnhancementTypeScreen screen3 = new EnhancementTypeScreen(EnhancementType.NORMAL, new ItemLevellerAndGenerator(item, 4));
//			GenerationEventDispatcher.addListener(screen3);
//			dia = manager;
//			manager.show(screen3);
//			break;
//		case 18:
//			model.getSkillValue(SplitterMondCore.getSkill("hunting")).setValue(5);
//			charGen = new CharacterLeveller(model, null);
//			SkillScreen2 screen4 = new SkillScreen2(charGen, manager, SkillType.NORMAL);
//			dia = manager;
////			manager.show(screen4);
//			break;
		case 19:
			CreatureGenerator creaCtrl = new CreatureGenerator(new ResourceReference(SplitterMondCore.getResource("creature"), 4));
			CreatureCreateDialog screen5 = new CreatureCreateDialog(creaCtrl);
			dia = manager;
			manager.showAndWait(screen5);
			break;
		}

		dia.getStyleClass().add("page");
		Scene scene = new Scene(dia, 1400, 800);
		scene.getStylesheets().add("css/size-medium.css");
//		scene.getStylesheets().add("css/color.css");
//		scene.getStylesheets().add("css/layout.css");
//		scene.getStylesheets().add("css/typography.css");
		scene.getStylesheets().add("css/splittermond.css");
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		PropertyConfigurator.configure("log4j.properties");
//		SplitterMondCore.initialize(new DummyRulePlugin<>());
//		(new MondstahlklingenPlugin()).init();
//		(new BestienUndUngeheuerPlugin()).init();
//		(new BeastMasterPlugin()).init();
////		Iterator<RulePlugin> it = ServiceLoader.load(RulePlugin.class).iterator();
////		while (it.hasNext()) {
////			RulePlugin<SpliMoCharacter> plug = it.next();
////			if (plug.getSupportedFeatures().contains(RulePluginFeatures.DATA)) {
////				System.err.println("plug = "+plug.getReadableName());
////				plug.init();
////			}
////		}
		launch(args);
	}

}
