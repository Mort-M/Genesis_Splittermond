/**
 *
 */
package org.prelle.splittermond.chargen.jfx;

import java.lang.reflect.Constructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.Wizard;
import org.prelle.javafx.WizardPage;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.chargen.LetUserChooseListener;
import org.prelle.splimo.chargen.SpliMoCharacterGenerator;

/**
 * @author prelle
 *
 */
public class SinglePageWizard<T extends WizardPage> extends Wizard {

	private static Logger logger = LogManager.getLogger("splittermond.jfx");

	private T page;

	//-------------------------------------------------------------------
	/**
	 * @param nodes
	 */
	public SinglePageWizard(SpliMoCharacter model, SpliMoCharacterGenerator charGen, Class<T> cls, boolean withUserChooseListener) {
		getNavigButtons().clear();
		getNavigButtons().addAll(
				CloseType.CANCEL,
				CloseType.OK
				);

		try {
			Constructor<T> cons =
					withUserChooseListener
					?
					cls.getConstructor(Wizard.class, SpliMoCharacterGenerator.class, LetUserChooseListener.class)
					:
					cls.getConstructor(Wizard.class, SpliMoCharacterGenerator.class)
					;

			page =
					withUserChooseListener
					?
					cons.newInstance(this, charGen, this)
					:
					cons.newInstance(this, charGen)
					;

			getPages().addAll(page);

			setOnAction(CloseType.OK, event -> {
//				page.pageLeft(CloseType.OK);
				this.impl_getBehaviour().finish();
			});

		} catch (Exception e) {
			logger.fatal("Failed creating wizard page",e);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.Wizard#canBeFinished()
	 */
	@Override
	public boolean canBeFinished() {
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the page
	 */
	public T getPage() {
		return page;
	}

}
