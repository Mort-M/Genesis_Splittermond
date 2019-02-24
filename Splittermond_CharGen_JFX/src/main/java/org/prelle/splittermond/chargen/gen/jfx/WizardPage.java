package org.prelle.splittermond.chargen.gen.jfx;

import java.util.PropertyResourceBundle;

import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/** basic wizard page class */
abstract class WizardPage extends HBox {

	private static PropertyResourceBundle uiResources = SpliMoCharGenJFXConstants.UI;

	private Label     heading;
	private ImageView sidebar;
	private Parent    content;
	private Parent    context;
	protected HBox      buttonBar;
	protected Wizard wizard;
	Button priorButton  = new Button(uiResources.getString("button.prev"));
	Button nextButton   = new Button(uiResources.getString("button.next"));
	Button cancelButton = new Button(uiResources.getString("button.cancel"));
	Button finishButton = new Button(uiResources.getString("button.finish"));

	//-------------------------------------------------------------------
	public WizardPage() {
	}

	//-------------------------------------------------------------------
	public WizardPage(String title_s, Image image) {
		this.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean arg1, Boolean arg2) {
				System.err.println("WizardPage "+getClass().getSimpleName()+" is visible="+arg2+"  "+WizardPage.this.getWidth());
			}
		});
		pageInit(title_s, image);
	}
	//-------------------------------------------------------------------
	void setWizard(Wizard wizard) {
		this.wizard = wizard;
	}

	//-------------------------------------------------------------------
	/**
	 * @param title_s Title of this page
	 * @param image  Image to use left of the page - may be null
	 */
	protected void pageInit(String title_s, Image image) {
		getStyleClass().add("wizardpage");
		setSpacing(0);
		setId(title_s);

		/*
		 * Heading
		 */
		heading = new Label(title_s);
		heading.getStyleClass().add("wizard-heading");
		heading.setMaxWidth(Double.MAX_VALUE);

		/*
		 * Content
		 */
		content = getContent();
		content.getStyleClass().add("wizard-content");

		/*
		 * Context
		 */
		context = getContextMenu();
		if (context!=null)
			context.getStyleClass().add("wizard-context");

		/*
		 * Button Bar
		 */
		buttonBar = getButtons();

		/*
		 * Side Bar
		 */
		VBox leftColumn = new VBox();
		sidebar = new ImageView();
		leftColumn.getStyleClass().add("wizard-sidebar");
		if (image!=null)
			sidebar.setImage(image);
		leftColumn.getChildren().add(sidebar);

		/*
		 * Now layout the right column
		 */
		VBox rightColumn = new VBox();
		rightColumn.setAlignment(Pos.TOP_LEFT);
		HBox.setHgrow(rightColumn, Priority.ALWAYS);
		HBox.setHgrow(heading, Priority.SOMETIMES);

		Region spring = new Region();
		VBox.setVgrow(heading  , Priority.NEVER);
		VBox.setVgrow(spring   , Priority.ALWAYS);
		VBox.setVgrow(content  , Priority.ALWAYS);
		VBox.setVgrow(buttonBar, Priority.NEVER);
		rightColumn.getChildren().addAll(heading, content, buttonBar);

		/*
		 * Left and right side of dialog
		 */
		getChildren().addAll(leftColumn, rightColumn);
		if (context!=null)
			getChildren().add(context);

		priorButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				priorPage();
			}
		});
		nextButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				nextPage();
			}
		});
		cancelButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				getWizard().cancel();
			}
		});
		finishButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				getWizard().finish();
			}
		});
	}

	//-------------------------------------------------------------------
	HBox getButtons() {
		Region spring = new Region();
		HBox.setHgrow(spring, Priority.ALWAYS);
		cancelButton.setCancelButton(true);
		finishButton.setDefaultButton(true);

		HBox buttonBar = new HBox(5);
		buttonBar.getStyleClass().add("wizard-buttonbar");
		buttonBar.getChildren().addAll(spring, priorButton, nextButton,
				cancelButton, finishButton);
		return buttonBar;
	}

	//-------------------------------------------------------------------
	abstract Parent getContent();

	//-------------------------------------------------------------------
	protected Parent getContextMenu() {
		return null;
	}

	//-------------------------------------------------------------------
	boolean hasNextPage() {
		return getWizard().hasNextPage();
	}

	//-------------------------------------------------------------------
	boolean hasPriorPage() {
		return getWizard().hasPriorPage();
	}

	//-------------------------------------------------------------------
	void nextPage() {
		getWizard().nextPage();
	}

	//-------------------------------------------------------------------
	void priorPage() {
		getWizard().priorPage();
	}

//	//-------------------------------------------------------------------
//	void navTo(String id) {
//		getWizard().navTo(id);
//	}

	//-------------------------------------------------------------------
	Wizard getWizard() {
		return wizard;
//		return (Wizard) getParent();
	}

	//-------------------------------------------------------------------
	public void manageButtons() {
		if (!hasPriorPage()) {
			priorButton.setDisable(true);
		}

		if (!hasNextPage()) {
			nextButton.setDisable(true);
		}
	}
}