package org.prelle.splittermond.chargen.jfx;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.AlertType;
import org.prelle.javafx.AttentionMenuItem;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.FontIcon;
import org.prelle.javafx.ManagedScreen;
import org.prelle.javafx.SymbolIcon;
import org.prelle.javafx.WindowMode;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.chargen.SpliMoCharacterGenerator;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.levelling.CharacterLeveller;
import org.prelle.splittermond.chargen.jfx.wizard.CharGenWizardSpliMo;

import de.rpgframework.RPGFrameworkLoader;
import de.rpgframework.character.Attachment;
import de.rpgframework.character.CharacterHandle;
import de.rpgframework.character.CharacterHandle.Format;
import de.rpgframework.character.CharacterHandle.Type;
import de.rpgframework.character.CharacterProvider;
import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;
import de.rpgframework.core.RoleplayingSystem;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Region;
import javafx.scene.shape.SVGPath;

/**
 * @author Stefan Prelle
 *
 */
public class CharacterViewScreenSpliMo2 extends ManagedScreen implements GenerationEventListener {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);
	private final static String CSS = "css/splittermond.css";

	private static PropertyResourceBundle RES = SpliMoCharGenJFXConstants.UI;

	private SpliMoCharacter model;
	private CharacterHandle handle;
	private CharacterController control;
	private ViewMode mode;

	private SMOverviewPage  pgOverview;
	private SMPowerLangCultPage  pgPowers;
	private SMSkillPage  pgSkills;
	private SMSpellPage  pgSpells;
	private SMResourceCompanionPage  pgResources;
	private SMEquipmentPage  pgEquipment;
	private SMDevelopmentPage pgDevelop;

	private AttentionMenuItem navOverview;
	private AttentionMenuItem navPowers;
	private AttentionMenuItem navSkills;
	private AttentionMenuItem navSpells;
	private AttentionMenuItem navResources;
	private MenuItem navEquipment;
	private MenuItem navDevelop;

	//-------------------------------------------------------------------
	public CharacterViewScreenSpliMo2(CharacterController control, ViewMode mode, CharacterHandle handle) {
		this.setId("genesis/splittermond");
		this.control = control;
		this.handle  = handle;
		this.mode = mode;
		if (this.mode==null)
			this.mode = ViewMode.MODIFICATION;
		model = control.getModel();

		initComponents();
		initLayout();
		initNavigation();
		initInteractivity();

		refresh();
		
		GenerationEventDispatcher.addListener(this);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.ManagedScreen#getStyleSheets()
	 */
	@Override
	public String[] getStyleSheets() {
		return new String[] {SpliMoCharGenJFXConstants.class.getResource(CSS).toExternalForm()};
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		pgOverview  = new SMOverviewPage(control, mode, handle, this);
		pgPowers    = new SMPowerLangCultPage(control, mode, handle, this);
		pgSkills    = new SMSkillPage(control, mode, handle, this);
		pgSpells    = new SMSpellPage(control, mode, handle, this);
		pgResources = new SMResourceCompanionPage(control, mode, handle, this);
		pgEquipment = new SMEquipmentPage(control, mode, handle, this);
		pgDevelop   = new SMDevelopmentPage(control, handle, this);
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		this.setLandingPage(pgOverview);
	}

	//-------------------------------------------------------------------
	private void initNavigation() {
		SVGPath svgMagic = new SVGPath();
		svgMagic.setContent("M250.53 22.03c-57.055 45.157-80.673 37.81-100.31.22 16.598 61.517 10.408 66.415-44.72 116.594 67.324-35.666 96.206-34.238 130.97 7.187-34.906-53.112-30.954-75.35 14.06-124zm18.407.126l11.688 114.938-99.875 58.094 97.75 21.093c-9.58 8.352-20.214 19.028-31.28 30.095l-.032.03L18.563 472.438v19.438h32.156L273.343 272.5c10.26-10.263 18.902-19.538 25.78-27.75l18.938 87.75 58.094-99.875 114.938 11.688-77.03-86.094 46.655-105.69-105.69 46.657-86.092-77.03zM26.875 55.938c33.765 27.66 35.21 42.767 30.75 87.78 18.975-53.73 27.964-67.297 64.5-82C82.972 71.094 66.21 73 26.875 55.94zm54.75 102.406c24.955 27.012 26.97 43.684 24.25 72.062 14.775-34.45 22.072-45.66 55.625-64.312-34.56 11.183-45.5 10.22-79.875-7.75zm325.594 95c9.27 51.694-4.61 73.708-32.845 106.687 43.3-37.043 57.852-44.284 96.844-38.75-38.597-11.457-47.426-20.624-64-67.936zm-55.658 72.812c-18.705 68.79-45.304 83.944-107.625 70.125 54.126 20.1 56.34 21.07 53.532 85.25 24.757-55.42 46.49-52.217 95.06-37.217-41.775-31.838-45.71-48.97-40.967-118.157zm109.344 55.97c-15.32 17.994-22.932 17.49-43.812 9.343 22.828 18.444 17.596 34.024 10.844 59.405 16.05-19.12 23.516-25.237 50.312-12.688-22.86-21.342-27.13-29.857-17.344-56.062z");
		Region icoMagic = new Region();
		icoMagic.setShape(svgMagic);
		icoMagic.setMinSize(15,15);
		icoMagic.setPrefSize(20,20);
		icoMagic.setStyle("-fx-background-color: black");
		icoMagic.setScaleX(0.5);

		SVGPath svgChest = new SVGPath();
		svgChest.setContent("M146.857 20.842c-12.535-.036-24.268 2.86-37.285 9.424h.004C61.356 54.6 19.966 120.734 17.982 175.91l41.848 14.236c4.33-61.89 47.057-128.37 101.527-155.86h.002c4.423-2.23 8.822-4.162 13.185-5.8l-22.26-7.45c-1.83-.123-3.637-.19-5.428-.194zm59.34 20.19c-10.478-.09-22.832 3.093-36.424 9.943l.004-.004c-48.23 24.34-89.625 90.513-91.548 145.436l156.485 53.24c3.865-62.22 46.797-129.372 101.613-157.035h.002l.002-.003c4.303-2.168 8.584-4.056 12.832-5.666l-134.54-45.036c-2.652-.542-5.458-.847-8.427-.873zm174.97 58.323c-10.476-.09-22.83 3.092-36.42 9.94l-.005.002c-48.577 24.518-90.225 91.473-91.586 146.623l46.205 15.72c3.914-62.188 46.825-129.274 101.607-156.92 4.522-2.283 9.04-4.258 13.53-5.91l-26.544-8.884c-2.164-.35-4.423-.55-6.785-.57zm63.554 22.014c-10.267.093-22.094 3.353-35.333 10.034-47.158 23.8-87.777 87.587-91.362 141.75l174.55-73.726c-.404-39.01-10.754-61.304-24.415-71.082-2.347-1.68-4.867-3.057-7.55-4.137l-.01.034-4.735-1.584c-3.48-.887-7.195-1.327-11.144-1.29zM17.9 195.622l-.035 187.484L59.46 397.58V209.764L17.9 195.624zM78.15 216.12v187.962l156.282 54.37V269.288l-29.053-9.886v119.43l-101.054-34.082V225.025L78.15 216.12zm414.22 3.683L318.433 293.27v189.236l173.935-73.504v-189.2zm-369.354 11.582v99.947l63.675 21.477v-99.763l-63.674-21.662zm31.306 28.797c9.705 0 17.573 7.867 17.573 17.572 0 6.34-3.37 11.88-8.407 14.97v28.53h-18.69v-28.746c-4.838-3.13-8.048-8.562-8.048-14.754 0-9.705 7.867-17.572 17.572-17.572zm98.797 15.464v189.307l46.626 16.22V291.51l-46.627-15.864z");
		Region icoChest = new Region();
		icoChest.setShape(svgChest);
		icoChest.setMinSize(15,15);
		icoChest.setPrefSize(20,15);
		icoChest.setStyle("-fx-background-color: black");
		icoChest.setScaleX(0.5);
		
		SVGPath svgBattleGear = new SVGPath();
		svgBattleGear.setContent("M262.406 17.188c-27.22 8.822-54.017 28.012-72.375 55.53 17.544 47.898 17.544 57.26 0 105.157 19.92 15.463 40.304 24.76 60.782 27.47-2.063-25.563-3.63-51.13 1.125-76.69-13.625-1.483-23.374-5.995-37-13.874V82.563c35.866 19.096 61.84 18.777 98.813 0v32.22c-13.364 6.497-21.886 11.16-35.25 13.218 3.614 25.568 3.48 51.15 1.375 76.72 18.644-3.265 37.236-12.113 55.5-26.845-14.353-47.897-14.355-57.26 0-105.156-16.982-28.008-47.453-46.633-72.97-55.532zm-129.594 8.218c-25.906 110.414-27.35 215.33-27.4 330.922-18.84-1.537-37.582-5.12-56.027-11.12v28.554h69.066c8.715 35.025 6.472 70.052-1.036 105.078h28.13c-7.195-35.026-8.237-70.053-.872-105.078h68.904v-28.555c-18.49 4.942-37.256 8.552-56.097 10.46.082-114.94 2.496-223.068-24.667-330.26zm89.47 202.375c0 117.27 25.517 233.342 120.155 257.97C446.62 464.716 462.72 345.374 462.72 227.78H222.28z");
		Region icoGear = new Region();
		icoGear.setShape(svgBattleGear);
		icoGear.setMinSize(15,15);
		icoGear.setPrefSize(20,15);
		icoGear.setStyle("-fx-background-color: black");
		icoGear.setScaleX(0.5);

		navOverview   = new AttentionMenuItem(RES.getString("navItem.overview"), new SymbolIcon("home"));
		navPowers     = new AttentionMenuItem(RES.getString("navItem.powers"), new FontIcon("\uD83C\uDFAD"));
		navSkills     = new AttentionMenuItem(RES.getString("navItem.skills"), new SymbolIcon("education"));
//		navSpells     = new AttentionMenuItem(RES.getString("navItem.spells"), icoMagic);
		navSpells     = new AttentionMenuItem(RES.getString("navItem.spells"), new FontIcon("\u26E4"));
//		navResources  = new AttentionMenuItem(RES.getString("navItem.resources"), icoChest);
		navResources  = new AttentionMenuItem(RES.getString("navItem.resources"), new FontIcon("\uD83D\uDC0E"));
//		navEquipment  = new MenuItem(RES.getString("navItem.gear"), icoGear);
		navEquipment  = new MenuItem(RES.getString("navItem.gear"), new FontIcon("\u2694"));
		navDevelop    = new MenuItem(RES.getString("navItem.develop"), new FontIcon("\uD83D\uDCC8"));

		this.getNavigationItems().addAll(navOverview, navPowers, navSkills, navSpells, navResources, navEquipment, navDevelop);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		setCanBeLeftCallback( screen -> userTriesToLeave());
	}

	//-------------------------------------------------------------------
	private boolean saveCharacter() {
		logger.debug("START: saveCharacter");

		if (mode==ViewMode.MODIFICATION) {
			/*
			 * Write all made modifications to character
			 */
			logger.debug("Add modifications to character log");
			((CharacterLeveller)control).updateHistory();
		}

		try {
			/*
			 * 1. Convert character into Byte Buffer - or fail
			 */
			byte[] encoded = SplitterMondCore.save(model);;
//			byte[] encoded = null;
//			try { encoded = SplitterMondCore.save(model); } catch (IOException e) {
//				logger.error("Cannot save character, since encoding failed: "+e);
//				StringWriter out = new StringWriter();
//				e.printStackTrace(new PrintWriter(out));
//				getManager().showAlertAndCall(
//						AlertType.ERROR,
//						RES.getString("error.encoding.title"),
//						RES.getString("error.encoding.content")+"\n"+out
//						);
//				return false;
//			}

			/*
			 * 2. Use character service to save character
			 */
			try {
				if (handle==null) {
					logger.debug("CharacterHandle does not exist yet - prepare it");
					handle = RPGFrameworkLoader.getInstance().getCharacterService().createCharacter(model.getName(), RoleplayingSystem.SHADOWRUN);
				}
				logger.info("Save character "+model.getName());
				RPGFrameworkLoader.getInstance().getCharacterAndRules().getCharacterService().addAttachment(handle, Type.CHARACTER, Format.RULESPECIFIC, null, encoded);
				handle.setCharacter(model);
				logger.info("Saved character "+model.getName()+" successfully");
			} catch (IOException e) {
				logger.error("Failed saving character",e);
				StringWriter out = new StringWriter();
				e.printStackTrace(new PrintWriter(out));
				getManager().showAlertAndCall(
						AlertType.ERROR,
						RES.getString("error.saving_character.title"),
						RES.getString("error.saving_character.message")+"\n"+out
						);
				return false;
			}

			// 3. Eventually rename
			try {
				if (handle!=null && !handle.getName().equals(model.getName())) {
					logger.info("Character has been renamed");
					RPGFrameworkLoader.getInstance().getCharacterService().renameCharacter(handle, model.getName());
				}
			} catch (IOException e) {
				logger.error("Renaming failed",e);
				BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 2, "Renaming failed: "+e);
			}

			/*
			 * 3. Update portrait
			 */
			logger.debug("Update portrait");
			CharacterProvider charServ = RPGFrameworkLoader.getInstance().getCharacterAndRules().getCharacterService();
			try {
				if (model.getImage()!=null && handle!=null) {
					Attachment attach = handle.getFirstAttachment(Type.CHARACTER, Format.IMAGE);
					if (attach!=null) {
						logger.info("Update character image");
						attach.setData(model.getImage());
						charServ.modifyAttachment(handle, attach);
					} else {
						charServ.addAttachment(handle, Type.CHARACTER, Format.IMAGE, null, model.getImage());
					}
				} else if (handle!=null) {
					Attachment attach = handle.getFirstAttachment(Type.CHARACTER, Format.IMAGE);
					if (attach!=null) {
						logger.info("Delete old character image");
						charServ.removeAttachment(handle, attach);
					}
				}
			} catch (IOException e) {
				logger.error("Failed modifying portrait attachment",e);
			}
		} finally {
			logger.debug("STOP : saveCharacter");
		}
		return true;
	}

	//-------------------------------------------------------------------
	private boolean userTriesToLeave() {
		logger.info("userTriesToLeave");
		
		if (mode==ViewMode.GENERATION) {
			logger.warn("TODO: Check if creation is finished");
			if ( ((SpliMoCharacterGenerator)control).hasEnoughData() ) {
				logger.info("User wants to leave and generator is finished - try to save character");
				((SpliMoCharacterGenerator)control).generate();
				return saveCharacter();
			} else {
				logger.info("User wants to leave the generation early.");
				CloseType result = getManager().showAlertAndCall(
						AlertType.CONFIRMATION,
						RES.getString("alert.cancel_creation.title"),
						RES.getString("alert.cancel_creation.message")
						);
				return result==CloseType.YES;
			}
		} else {
			logger.info("User wants to leave character modifiction");
			CloseType result = getManager().showAlertAndCall(
					AlertType.CONFIRMATION,
					RES.getString("alert.save_character.title"),
					RES.getString("alert.save_character.message")
					);
			if (result==CloseType.YES) {
				logger.debug("User confirmed saving character");
				saveCharacter();
			} else if (result==CloseType.CANCEL) {
				logger.debug("User cancelled leaving");
				return false;
			} else {
				logger.debug("User denied saving character - reload it");
				try {
					if (handle!=null) {
						handle.setCharacter(null);
						handle.getCharacter();
					}
				} catch (IOException e) {
					logger.error("Failed reloading character",e);
					getManager().showAlertAndCall(AlertType.ERROR, "", RES.getString("error.reloading.char"));
				}
			}
		}
		
		return true;
	}

//	//-------------------------------------------------------------------
//	public void startGeneration(CharacterGenerator charGen) {
//		// TODO Auto-generated method stub
//		logger.warn("TODO: startGeneration: "+charGen);
//
////		CharGenWizardNG wizard = new CharGenWizardNG(model);
////		CloseType close = (CloseType)getManager().showAndWait(wizard);
////		logger.info("Closed with "+close);
////
////		if (close==CloseType.FINISH) {
////			logger.info("Wizard finished");
//////			CoriolisCharacter model = charGen.getCharacter();
//////			try {
//////				byte[] data = CoriolisCore.save(model);
//////				handle = RPGFrameworkLoader.getInstance().getCharacterService().createCharacter(model.getName(), RoleplayingSystem.CORIOLIS);
//////				RPGFrameworkLoader.getInstance().getCharacterService().addAttachment(handle, Type.CHARACTER, Format.RULESPECIFIC, model.getName()+".xml", data);
//////				manager.showAlertAndCall(AlertType.NOTIFICATION, UI.getString("alert.generation_finished.title"),
//////						String.format(UI.getString("alert.generation_finished.message"), handle.getPath().toString()));
//////			} catch (IOException e) {
//////				logger.error("Failed writing newly created character to disk",e);
//////				manager.showAlertAndCall(AlertType.ERROR, UI.getString("error.saving_character.title"),
//////						String.format(UI.getString("error.saving_character.message"), e.toString()));
//////			}
//////			this.control = new CoriolisCharacterLeveller(model);
////			refresh();
////			
////		}
//
//	}

	//--------------------------------------------------------------------
	private void updateAttentionFlags() {
		logger.debug("updateAttentionFlags");
		navOverview.setAttentionFlag(!control.getAttributeController().getToDos().isEmpty());
		navOverview.setAttentionToolTip(control.getAttributeController().getToDos());
		
		navSpells.setAttentionFlag(!control.getSpellController().getToDos().isEmpty());
		navSpells.setAttentionToolTip(control.getSpellController().getToDos());

		navSkills.setAttentionFlag(!control.getSkillController().getToDos().isEmpty());
		navSkills.setAttentionToolTip(control.getSkillController().getToDos());

		navResources.setAttentionFlag(!control.getResourceController().getToDos().isEmpty());
		navResources.setAttentionToolTip(control.getResourceController().getToDos());

		List<String> powerCultToDos = new ArrayList<>();
		powerCultToDos.addAll(control.getPowerController().getToDos());
		powerCultToDos.addAll(control.getLanguageController().getToDos());
		powerCultToDos.addAll(control.getCultureLoreController().getToDos());
		navPowers.setAttentionFlag(!powerCultToDos.isEmpty());
		navPowers.setAttentionToolTip(powerCultToDos);
	}

	//--------------------------------------------------------------------
	private void refresh() {
		logger.debug("refresh");

		logger.debug("ToDos = "+control.getToDos());
		setHeader(model.getName());
		pgOverview.refresh();
		pgPowers.refresh();
		pgSkills.refresh();
		pgSpells.refresh();
		pgResources.refresh();
		pgEquipment.refresh();
		pgDevelop.refresh();
		
		updateAttentionFlags();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.ManagedScreen#navigationItemChanged(javafx.scene.control.MenuItem, javafx.scene.control.MenuItem)
	 */
	@Override
	public void navigationItemChanged(MenuItem oldValue, MenuItem newValue) {
		logger.info("Navigation changed to "+newValue);
		
		if (newValue==navOverview) {
			setContent(pgOverview);
		} else if (newValue==navPowers) {
			setContent(pgPowers);
		} else if (newValue==navSkills) {
			setContent(pgSkills);
		} else if (newValue==navSpells) {
			setContent(pgSpells);
		} else if (newValue==navResources) {
			setContent(pgResources);
		} else if (newValue==navEquipment) {
			setContent(pgEquipment);
		} else if (newValue==navDevelop) {
			setContent(pgDevelop);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.ResponsiveControl#setResponsiveMode(org.prelle.javafx.WindowMode)
	 */
	@Override
	public void setResponsiveMode(WindowMode value) {
		pgOverview.setResponsiveMode(value);
		pgPowers.setResponsiveMode(value);
		pgSkills.setResponsiveMode(value);
		pgSpells.setResponsiveMode(value);
		pgResources.setResponsiveMode(value);
		pgEquipment.setResponsiveMode(value);
//		pgDevelop.setResponsiveMode(value);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.shadowrun.gen.event.GenerationEventListener#handleGenerationEvent(org.prelle.shadowrun.gen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		logger.debug("RCV "+event.getType());
		updateAttentionFlags();
		switch (event.getType()) {
		case ATTRIBUTE_CHANGED:
		case POINTS_LEFT_ATTRIBUTES:
			pgOverview.refresh();
			break;
		case POWER_ADDED:
		case POWER_CHANGED:
		case POWER_REMOVED:
		case CULTURELORE_REMOVED:
		case CULTURELORE_ADDED:
		case WEAKNESS_ADDED:
		case WEAKNESS_REMOVED:
		case LANGUAGE_ADDED:
		case LANGUAGE_REMOVED:
		case POINTS_LEFT_POWERS:
			pgPowers.refresh();
			break;
		case RESOURCE_ADDED:
		case RESOURCE_CHANGED:
		case RESOURCE_REMOVED:
		case CREATURE_CHANGED:
			pgResources.refresh();
			break;
		case CULTURELORE_AVAILABLE_CHANGED:
		case POWER_AVAILABLE_ADDED:
		case POWER_AVAILABLE_REMOVED:
			break;
		case SKILL_CHANGED:
			pgSpells.refresh();
		case MASTERSHIP_ADDED:
		case MASTERSHIP_REMOVED:
			pgSkills.refresh();
			break;
		case SPELL_ADDED:
		case SPELL_REMOVED:
		case SPELL_FREESELECTION_CHANGED:
			pgSpells.refreshSpells();
			break;
		case ITEM_CHANGED:
			pgEquipment.refresh();
			break;
		case CHARACTER_CHANGED:
		case EXPERIENCE_CHANGED:
			refresh();
			break;
		case UNDO_LIST_CHANGED:
			break;
		default:
			logger.warn("What to do on "+event.getType());
		}
	}

	//-------------------------------------------------------------------
	public void startGeneration() {
		logger.info("startGeneration "+model);
		this.mode = ViewMode.GENERATION;

		pgDevelop.setDisable(true);
		navDevelop.setVisible(true);

		CharGenWizardSpliMo wizard = new CharGenWizardSpliMo(model, (SpliMoCharacterGenerator)control);
		CloseType close = (CloseType)getManager().showAndWait(wizard);
		logger.info("TODO Closed with "+close);
		GenerationEventDispatcher.removeListener(wizard);

		if (close==CloseType.FINISH) {
			logger.info("Wizard finished");
			try {
				byte[] data =SplitterMondCore.save(model);
				handle = RPGFrameworkLoader.getInstance().getCharacterService().createCharacter(model.getName(), RoleplayingSystem.SPLITTERMOND);
				RPGFrameworkLoader.getInstance().getCharacterService().addAttachment(handle, Type.CHARACTER, Format.RULESPECIFIC, model.getName()+".xml", data);
				getManager().showAlertAndCall(AlertType.NOTIFICATION, RES.getString("alert.start_tuning.title"),
						String.format(RES.getString("alert.start_tuning.message"), handle.getPath().toString()));
//				commandBar.getItems().addAll(cmdPrint);
			} catch (IOException e) {
				logger.error("Failed writing newly created character to disk",e);
				getManager().showAlertAndCall(AlertType.ERROR, RES.getString("error.saving_character.title"),
						String.format(RES.getString("error.saving_character.message"), e.toString()));
			}
			refresh();
		} else {
			logger.warn("Wizard "+close);
			getScreenManager().closeScreen();
		}
	}

}
