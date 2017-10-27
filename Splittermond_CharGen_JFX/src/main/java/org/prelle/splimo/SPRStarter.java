package org.prelle.splimo;

import java.util.function.Predicate;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import org.apache.log4j.PropertyConfigurator;
import org.prelle.javafx.ManagedScreen;
import org.prelle.javafx.ModernUI;
import org.prelle.javafx.ScreenManager;
import org.prelle.javafx.TriStateCheckBox;
import org.prelle.javafx.TriStateCheckBox.State;
import org.prelle.javafx.skin.MetroSliderSkin;
import org.prelle.splimo.chargen.LetUserChooseListener;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.creature.Creature;
import org.prelle.splimo.creature.CreatureGenerator;
import org.prelle.splimo.equip.ItemLevellerAndGenerator;
import org.prelle.splimo.items.CarriedItem;
import org.prelle.splimo.items.EnhancementReference;
import org.prelle.splimo.items.ItemTemplate;
import org.prelle.splimo.modifications.MastershipModification;
import org.prelle.splimo.modifications.ModificationChoice;
import org.prelle.splimo.npc.NPCGenerator;
import org.prelle.splittermond.jfx.creatures.CreatureCreateScreen;
import org.prelle.splittermond.jfx.creatures.CreatureWizardSpliMo;
import org.prelle.splittermond.jfx.equip.ItemGeneratorPane;
import org.prelle.splittermond.jfx.equip.input.EnterItemTemplatePane;
import org.prelle.splittermond.jfx.spells.SpellSlider;

import de.rpgframework.genericrpg.modification.Modification;

public class SPRStarter extends Application {

	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j-debug.properties");

		SPRStarter.launch(args);
	}

	//--------------------------------------------------------------------
	/**
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage prim) throws Exception {
		ModernUI.initialize();

		Scene scene = null;
		int show = 7;

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
		}

		ModernUI.initialize(scene);
		prim.setScene(scene);
		prim.setFullScreen(false);
		prim.setTitle("Test");
		prim.show();
	}

}
