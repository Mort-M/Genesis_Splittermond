package org.prelle.splimo;

import java.util.function.Predicate;

import org.apache.log4j.PropertyConfigurator;
import org.prelle.javafx.ManagedScreen;
import org.prelle.javafx.ModernUI;
import org.prelle.javafx.ScreenManager;
import org.prelle.javafx.TriStateCheckBox;
import org.prelle.javafx.TriStateCheckBox.State;
import org.prelle.javafx.skin.MetroSliderSkin;
import org.prelle.rpgframework.jfx.CharacterDocumentView;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.chargen.LetUserChooseListener;
import org.prelle.splimo.chargen.SpliMoCharacterGenerator;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.fluent.AttributesView;
import org.prelle.splimo.chargen.fluent.SplittermondCharGenView;
import org.prelle.splimo.creature.Creature;
import org.prelle.splimo.creature.CreatureGenerator;
import org.prelle.splimo.equip.ItemLevellerAndGenerator;
import org.prelle.splimo.items.CarriedItem;
import org.prelle.splimo.items.EnhancementReference;
import org.prelle.splimo.items.ItemTemplate;
import org.prelle.splimo.levelling.CharacterLeveller;
import org.prelle.splimo.modifications.MastershipModification;
import org.prelle.splimo.modifications.ModificationChoice;
import org.prelle.splimo.npc.NPCGenerator;
import org.prelle.splittermond.jfx.creatures.CreatureCreateScreen;
import org.prelle.splittermond.jfx.creatures.CreatureWizardSpliMo;
import org.prelle.splittermond.jfx.equip.ItemGeneratorPane;
import org.prelle.splittermond.jfx.equip.input.EnterItemTemplatePane;
import org.prelle.splittermond.jfx.spells.SpellSlider;

import de.rpgframework.RPGFrameworkLoader;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.modification.Modification;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class SPRStarter extends Application {

	public static void main(String[] args) {
		PropertyConfigurator.configure(ClassLoader.getSystemResourceAsStream("log4j.properties"));

		SPRStarter.launch(args);
	}

	//--------------------------------------------------------------------
	/**
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage prim) throws Exception {
		Font.loadFont(ClassLoader.getSystemResourceAsStream("fonts/segoeui.ttf"), 12);
		Scene scene = null;
		RPGFrameworkLoader.setInstance(new DummyRPGFramework());
		int show = 11;

		switch (show) {
		case 0:
			SplitterMondCore.initialize(new DummyRulePlugin<>());
			ItemTemplate template = SplitterMondCore.getItem("falchion");

			CarriedItem item = new CarriedItem();
			item.setItem(template);
			item.addEnhancement(new EnhancementReference(SplitterMondCore.getEnhancement("load")));
			item.addEnhancement(new EnhancementReference(SplitterMondCore.getEnhancement("speed")));
			ItemLevellerAndGenerator itemGen = new ItemLevellerAndGenerator(item, Integer.MAX_VALUE);
			Parent toShow = new ItemGeneratorPane();
			GenerationEventDispatcher.addListener((ItemGeneratorPane)toShow);
			((ItemGeneratorPane)toShow).setData(itemGen);
			toShow.getStyleClass().add("page");


			scene = new Scene(toShow);
			scene.getStylesheets().addAll("css/splittermond.css");
			break;
		case 1:
			SpellSlider slider2 = new SpellSlider();
			scene = new Scene(slider2);
			break;
		case 2:
			Slider slider = new Slider(0, 2, 0);
			slider.setSkin(new MetroSliderSkin(slider));
			slider.setMinorTickCount(0);
	        slider.setMajorTickUnit(1);
	        slider.setSnapToTicks(true);
	        slider.setShowTickMarks(true);
	        slider.setShowTickLabels(true);

	        slider.setLabelFormatter(new StringConverter<Double>() {
	            @Override
	            public String toString(Double n) {
	                if (n < 0.5) return "Ungelernt";
	                if (n < 1.5) return "Exp-Kauf";
	                if (n < 2.5) return "frei";

	                return "Foo";
	            }

	            @Override
	            public Double fromString(String s) {
	                switch (s) {
	                    case "Ungelernt":
	                        return 0d;
	                    case "Exp-Kauf":
	                        return 1d;
	                    case "Advanced":
	                        return 2d;
	                    case "frei":
	                        return 3d;

	                    default:
	                        return 3d;
	                }
	            }
	        });
			scene = new Scene(slider);
			slider.setValue(1);
	        break;
		case 3:
			TriStateCheckBox slider3 = new TriStateCheckBox();
			//slider3.setStyle("-fx-background-color: lime");
//			slider3.setText("Hallo");
			slider3.setStateFormatter(new StringConverter<TriStateCheckBox.State>() {
				public String toString(State state) {
					switch (state) {
					case SELECTION1: return "unbekannt";
					case SELECTION2: return "gekauft";
					case SELECTION3: return "frei";
					}
					return null;
				}
				public State fromString(String string) { return null;}
			});
			slider3.setMaxWidth(Double.MAX_VALUE);

			scene = new Scene(slider3);
			break;
		case 4:
			SplitterMondCore.initialize(new DummyRulePlugin<>());
			template = SplitterMondCore.getItem("bihander");
			System.out.println("Show "+template);
			toShow = new EnterItemTemplatePane(template);
			toShow.getStyleClass().add("page");


			scene = new Scene(toShow);
			scene.getStylesheets().addAll("css/splittermond.css");
			break;
		case 5:
			template = SplitterMondCore.getItem("longbow");
			template.setCustomName("Mein Langbogen");
//			Logger.getLogger("xml").setLevel(Level.DEBUG);
			SplittermondCustomDataCore.addItem(template);
			System.exit(0);
		case 6:
			SplitterMondCore.initialize(new DummyRulePlugin<>());
			Creature creature = new Creature();
			NPCGenerator npcGen = new NPCGenerator(creature);
			CreatureWizardSpliMo creaWiz = new CreatureWizardSpliMo(npcGen);
			ScreenManager mgmr = new ScreenManager();
			mgmr.show(creaWiz);
			scene = new Scene(mgmr, 1000, 10000);
			scene.getStylesheets().addAll("css/splittermond.css");
			break;
		case 7:
			SplitterMondCore.initialize(new DummyRulePlugin<>());
			ResourceReference resource = new ResourceReference(SplitterMondCore.getResource("creature"),2);
			LetUserChooseListener callback = new LetUserChooseListener() {
				
				@Override
				public MastershipModification letUserChoose(String choiceReason,
						MastershipModification vagueMod) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public Modification[] letUserChoose(String choiceReason,
						ModificationChoice choice) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public void addPrefilter(Predicate<Modification> filter) {
					// TODO Auto-generated method stub
					
				}
			};
			ManagedScreen dia = new CreatureCreateScreen(new CreatureGenerator(resource));
			mgmr = new ScreenManager();
			mgmr.show(dia);
			scene = new Scene(mgmr, 1500, 1000);
			scene.getStylesheets().addAll("css/splittermond.css");
			break;
		case 8:
			SplitterMondCore.initialize(new DummyRulePlugin<>());
			SpliMoCharacter lenkan = SplitterMondCore.load(ClassLoader.getSystemResourceAsStream("Lenkan.xml"));
			CharacterController control = new CharacterLeveller(lenkan, null);

			mgmr = new ScreenManager();
			scene = new Scene(mgmr, 1570, 1000);
			ModernUI.initialize(scene);
			scene.getStylesheets().addAll("css/rpgframework.css");
			SplittermondCharGenView diaS = new SplittermondCharGenView(control, mgmr, null); 
			diaS.setData(lenkan, null);
			mgmr.show(diaS);
			break;
		case 9:
			CharacterDocumentView view = new CharacterDocumentView();
			Label longText = new Label("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");
			longText.setWrapText(true);
			view.setDescriptionNode(longText);
			CharacterDocumentView.Section sect1 = new CharacterDocumentView.Section();
			sect1.setTitle("Abschnitt 1");
			sect1.setContent(new Label("Hallo Welt"));
//			sect1.getToDoList().add(new Label("Einfache Anweisung"));
			CharacterDocumentView.Section sect2 = new CharacterDocumentView.Section();
			sect2.setTitle("Abschnitt 2");
			sect2.setContent(new Label("Hell(o) World\nHow are you?\n\nOh, I see! :("));
			sect2.getToDoList().add(new ToDoElement(ToDoElement.Severity.STOPPER, "Einfache Anweisung"));
			sect2.getToDoList().add(new ToDoElement(ToDoElement.Severity.WARNING, "Warnmeldung"));
			view.getSectionList().addAll(sect1, sect2);
			
			mgmr = new ScreenManager();
			scene = new Scene(view, 1500, 1000);
			scene.getStylesheets().addAll("css/rpgframework.css");
			
//			ManagedScreen charViewScreen = new ManagedScreen() {
//				
//				@Override
//				public String[] getStyleSheets() {
//					// TODO Auto-generated method stub
//					return new String[] {"css/rpgframework.css"};
//				}
//			};
//			charViewScreen.setContent(view);
//			mgmr.replaceContent(charViewScreen);
			break;
		case 10:
			SplitterMondCore.initialize(new DummyRulePlugin<>());
			control = new SpliMoCharacterGenerator(new SpliMoCharacter());
			AttributesView aView = new AttributesView(control);
			scene = new Scene(aView);
			ModernUI.initialize(scene);
			scene.getStylesheets().addAll("css/rpgframework.css","css/splittermond.css");
			break;
		case 11:
			SplitterMondCore.initialize(new DummyRulePlugin<>());
			SpliMoCharacter model = new SpliMoCharacter();
			SpliMoCharacterGenerator charGen = new SpliMoCharacterGenerator(model);
			CharGenWizardSpliMo charWiz = new CharGenWizardSpliMo(model, charGen);
			mgmr = new ScreenManager();
			mgmr.show(charWiz);
			scene = new Scene(mgmr, 1000, 10000);
			ModernUI.initialize(scene);
			scene.getStylesheets().addAll("css/rpgframework.css");
			break;
		}

//		ModernUI.initialize(scene);
		prim.setScene(scene);
		prim.setFullScreen(false);
		prim.setTitle("Test");
		prim.show();
	}

}
