package org.prelle.splittermond.chargen.jfx;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.AlertType;
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

import de.rpgframework.RPGFrameworkLoader;
import de.rpgframework.character.Attachment;
import de.rpgframework.character.CharacterHandle;
import de.rpgframework.character.CharacterHandle.Format;
import de.rpgframework.character.CharacterHandle.Type;
import de.rpgframework.character.CharacterProvider;
import de.rpgframework.core.RoleplayingSystem;
import javafx.scene.control.MenuItem;

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

	private MenuItem navOverview;
	private MenuItem navPowers;
	private MenuItem navSkills;

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
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		this.setLandingPage(pgOverview);
	}

	//-------------------------------------------------------------------
	private void initNavigation() {
		navOverview   = new MenuItem(RES.getString("navItem.overview"), new SymbolIcon("home"));
		navPowers     = new MenuItem(RES.getString("navItem.powers"), new FontIcon("\uD83C\uDFAD"));
		navSkills     = new MenuItem(RES.getString("navItem.skills"), new FontIcon("\uD83C\uDFAD"));

		this.getNavigationItems().addAll(navOverview, navPowers, navSkills);
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
				logger.debug("User denied saving character");
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
	private void refresh() {
		logger.debug("refresh");

		setHeader(model.getName());
		pgOverview.refresh();
		pgPowers.refresh();
		pgSkills.refresh();
//		pgEquipment.refresh();
//		pgVehicles.refresh();
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
//		} else if (newValue==navMatrix) {
//			setContent(pgMatrix);
//		} else if (newValue==navEquipment) {
//			setContent(pgEquipment);
//		} else if (newValue==navVehicles) {
//			setContent(pgVehicles);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.ResponsiveControl#setResponsiveMode(org.prelle.javafx.WindowMode)
	 */
	@Override
	public void setResponsiveMode(WindowMode value) {
		logger.info("......"+value);
		pgOverview.setResponsiveMode(value);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.shadowrun.gen.event.GenerationEventListener#handleGenerationEvent(org.prelle.shadowrun.gen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		logger.debug("RCV "+event.getType());
		switch (event.getType()) {
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
		case CULTURELORE_AVAILABLE_CHANGED:
		case POWER_AVAILABLE_ADDED:
		case POWER_AVAILABLE_REMOVED:
			break;
		case SKILL_CHANGED:
		case MASTERSHIP_ADDED:
		case MASTERSHIP_REMOVED:
			pgSkills.refresh();
			break;
		case CHARACTER_CHANGED:
			refresh();
			break;
		default:
			logger.warn("What to do on "+event.getType());
		}
	}

}
