package org.prelle.splittermond.chargen.jfx.creatures;

import java.util.Iterator;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.ManagedDialog;
import org.prelle.javafx.NavigButtonControl;
import org.prelle.splimo.charctrl.CreatureController;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.creature.CreatureTypeValue;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splittermond.chargen.jfx.sections.CompanionSection;

/**
 * @author Stefan
 *
 */
public class CreatureCreateDialog extends ManagedDialog implements GenerationEventListener {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);

	private static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle.getBundle(CompanionSection.class.getName());

	private CreatureController control;
	
	private CreatureEditPane content;
	
	private NavigButtonControl btnControl;

	//--------------------------------------------------------------------
	public CreatureCreateDialog(CreatureController ctrl) {
		super(UI.getString("dialog.creature.create.title"), null, CloseType.APPLY, CloseType.CANCEL);
		this.control = ctrl;
		initComponents();
		initLayout();
		initInteractivity();
		
		btnControl = new NavigButtonControl();

		update();

		GenerationEventDispatcher.addListener(this);
	}

	//--------------------------------------------------------------------
	private void initComponents() {
		content = new CreatureEditPane(control, this);
	}

	//--------------------------------------------------------------------
	private void initLayout() {
		setContent(content);
	}

	//--------------------------------------------------------------------
	private void initInteractivity() {
	}

	//--------------------------------------------------------------------
	CreatureController getCreatureController() {
		return control;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.ManagedScreen#close(org.prelle.javafx.CloseType)
	 */
	@Override
	public void onClose(CloseType closeType) {
		logger.info("onClose("+closeType+")");
		GenerationEventDispatcher.removeListener(this);
	}

	//--------------------------------------------------------------------
	private String getCreatureTypesString() {
		StringBuffer buf = new StringBuffer();
		for (Iterator<CreatureTypeValue> it=control.getCreatureTypes().iterator(); it.hasNext(); ) {
			CreatureTypeValue val = it.next();
			String text = val.getName();
			if (it.hasNext())
				text +=",";
			buf.append(text);
		}
		return buf.toString();
	}
	
	//--------------------------------------------------------------------
	public void update() {
		content.update();
		btnControl.setDisabled(CloseType.OK, !control.canBeFinished());
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		logger.info("RCV "+event);
		switch (event.getType()) {
		case CREATURE_CHANGED:
			logger.debug("RCV "+event);
			update();
			break;
		default:
		}
	}

}