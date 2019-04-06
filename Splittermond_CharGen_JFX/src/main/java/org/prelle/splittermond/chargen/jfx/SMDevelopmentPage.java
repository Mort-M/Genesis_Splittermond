/**
 * 
 */
package org.prelle.splittermond.chargen.jfx;

import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.AlertType;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.ManagedScreen;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.rpgframework.jfx.DevelopmentPage;
import org.prelle.splimo.HistoryElementImpl;
import org.prelle.splimo.RewardImpl;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterTools;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splittermond.chargen.jfx.dialogs.RewardBox;

import de.rpgframework.RPGFrameworkLoader;
import de.rpgframework.character.CharacterHandle;
import de.rpgframework.core.RoleplayingSystem;
import de.rpgframework.genericrpg.HistoryElement;
import de.rpgframework.genericrpg.Reward;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.products.Adventure;
import de.rpgframework.products.ProductService;
import javafx.util.StringConverter;

/**
 * @author prelle
 *
 */
public class SMDevelopmentPage extends DevelopmentPage {

	private static Logger logger = LogManager.getLogger("splittermond.jfx");

	private static PropertyResourceBundle UI = SpliMoCharGenJFXConstants.UI;

	private CharacterController control;
	private SpliMoCharacter model;

	private ExpLine expLine;
	
	//-------------------------------------------------------------------
	/**
	 */
	public SMDevelopmentPage(CharacterController control, CharacterHandle handle, ScreenManagerProvider provider) {
		super(UI);
		this.setId("splittermond-development");
		this.setTitle(control.getModel().getName());
		this.control = control;
		model = control.getModel();
		logger.info("<init>()");
		
		expLine = new ExpLine();
		getCommandBar().setContent(expLine);
		setConverter(new StringConverter<Modification>() {
			public String toString(Modification arg0) {  return SplitterTools.getModificationString(arg0);}
			public Modification fromString(String arg0) {return null;}
		});
		refresh();
	}

	//-------------------------------------------------------------------
	@Override
	public void refresh() {
		history.setData(SplitterTools.convertToHistoryElementList(model, super.shallBeAggregated()));
		expLine.setData(control.getModel());
		
		getSectionList().forEach(sect -> sect.refresh());
		setPointsFree(control.getModel().getExperienceFree());
	}

	//-------------------------------------------------------------------
	public void setData(SpliMoCharacter model) {
		this.model = model;
//		setTitle(model.getName()+" / "+UI.getString("label.development"));
		
//		history.getItems().clear();
//		history.getItems().addAll(SplitterTools.convertToHistoryElementList(model));
		refresh();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.rpgframework.jfx.DevelopmentScreen#openAdd()
	 */
	@Override
	public HistoryElement openAdd() {
		RewardBox dialog = new RewardBox();
		logger.warn("TODO: openAdd");
//		ManagedScreen screen = new ManagedScreen() {
//			@Override
//			public boolean close(CloseType closeType) {
//				logger.debug("close("+closeType+") overwritten  "+dialog.hasEnoughData());
//				if (closeType==CloseType.OK) {
//					return dialog.hasEnoughData();
//				}
//				return true;
//			}
//		};
//		ManagedScreenDialogSkin skin = new ManagedScreenDialogSkin(screen);
//		screen.setTitle(UI.getString("dialog.reward.title"));
//		screen.setContent(dialog);
//		screen.getNavigButtons().addAll(CloseType.OK, CloseType.CANCEL);
//		screen.setSkin(skin);
//		screen.setOnAction(CloseType.OK, event -> manager.close(screen, CloseType.OK));
//		screen.setOnAction(CloseType.CANCEL, event -> manager.close(screen, CloseType.CANCEL));
//		skin.setDisabled(CloseType.OK, true);
//		dialog.enoughDataProperty().addListener( (ov,o,n) -> {
//			skin.setDisabled(CloseType.OK, !n);
//		});
//		CloseType closed = (CloseType)manager.showAndWait(screen);
//		
//		if (closed==CloseType.OK) {
//			RewardImpl reward = dialog.getDataAsReward();
//			logger.debug("Add reward "+reward);
//			SplitterTools.reward(model, reward);
//			HistoryElementImpl elem = new HistoryElementImpl();
//			elem.setName(reward.getTitle());
//			elem.addGained(reward);
//			if (reward.getId()!=null)
//				elem.setAdventure(RPGFrameworkLoader.getInstance().getProductService().getAdventure(RoleplayingSystem.SPLITTERMOND, reward.getId()));
//			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.EXPERIENCE_CHANGED, new int[]{model.getExperienceFree(), model.getExperienceInvested()}));
//			GenerationEventDispatcher.fireEvent(new GenerationEvent(GenerationEventType.MONEY_CHANGED, null));
//			return elem;
//		}
		return null;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.rpgframework.jfx.DevelopmentScreen#openEdit(de.rpgframework.genericrpg.HistoryElement)
	 */
	@Override
	public boolean openEdit(HistoryElement elem) {
		logger.warn("TODO: openEdit");
		/*
		 * Currently only elements
		 */
		if (elem.getGained().size()>1) {
			getScreenManager().showAlertAndCall(
					AlertType.ERROR, 
					UI.getString("error.not-possible"), 
					UI.getString("error.only-single-rewards-editable"));
			return false;
		}
		
		Reward toEdit = elem.getGained().get(0);
		RewardBox dialog = new RewardBox(toEdit);
		
//		ManagedScreen screen = new ManagedScreen() {
//			@Override
//			public boolean close(CloseType closeType) {
//				logger.debug("close("+closeType+") overwritten  "+dialog.hasEnoughData());
//				if (closeType==CloseType.OK) {
//					return dialog.hasEnoughData();
//				}
//				return true;
//			}
//		};
//		ManagedScreenDialogSkin skin = new ManagedScreenDialogSkin(screen);
//		screen.setTitle(UI.getString("dialog.reward.title"));
//		screen.setContent(dialog);
//		screen.getNavigButtons().addAll(CloseType.OK, CloseType.CANCEL);
//		screen.setSkin(skin);
//		screen.setOnAction(CloseType.OK, event -> manager.close(screen, CloseType.OK));
//		screen.setOnAction(CloseType.CANCEL, event -> manager.close(screen, CloseType.CANCEL));
//		CloseType closed = (CloseType)manager.showAndWait(screen);
//		
//		if (closed==CloseType.OK) {
//			RewardImpl reward = dialog.getDataAsReward();
//			logger.debug("Copy edited data: ORIG = "+toEdit);
//			toEdit.setTitle(reward.getTitle());
//			toEdit.setId(reward.getId());
//			toEdit.setDate(reward.getDate());
//			// Was single reward. Changes in reward title, change element title
//			((HistoryElementImpl)elem).setName(reward.getTitle());
//			ProductService sessServ = RPGFrameworkLoader.getInstance().getProductService();
//			if (reward.getId()!=null)  {
//				Adventure adv = sessServ.getAdventure(RoleplayingSystem.SPLITTERMOND, reward.getId());
//				if (adv==null) {
//					logger.warn("Reference to an unknown adventure: "+reward.getId());
//				} else
//					((HistoryElementImpl)elem).setAdventure(adv);
//
//			}
////			((HistoryElementImpl)elem).set(reward.getTitle());
//			logger.debug("Copy edited data: NEW  = "+toEdit);
//			logger.debug("Element now   = "+elem);
//			return true;
//		}
		return false;
	}

}