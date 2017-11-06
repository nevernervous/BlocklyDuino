package org.blockly;

import org.blockly.util.LanguageProvider;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class StateDialog {
	final Stage stage = new Stage(javafx.stage.StageStyle.UTILITY);
	private ProgressBar progress;
	private Button btn_cancel;
	private HBox container = new HBox();

	public StateDialog(Stage stg, String title) {
		progress = new ProgressBar();
		btn_cancel = new Button();

		stage.initModality(Modality.APPLICATION_MODAL);

		stage.initOwner(stg);
		stage.setTitle(title);
		stage.setResizable(false);
		stage.setOnCloseRequest(new EventHandler() {
			public void handle(Event event) {
				MyBrowser.cmdIsKilled = true;
			}
		});
		Group root = new Group();
		Scene scene = new Scene(root, 300.0D, 35.0D);

		btn_cancel.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				MyBrowser.cmdIsKilled = true;
				stage.hide();
			}

		});
		progress.setStyle("-fx-accent: red;");
		progress.setPrefSize(200.0D, 25.0D);
		btn_cancel.setPrefHeight(25.0D);
		btn_cancel.setText(LanguageProvider.getLocalString("btn_cancel"));
		container.setAlignment(Pos.CENTER);
		container.setPrefSize(300.0D, 35.0D);
		container.setPadding(new javafx.geometry.Insets(5.0D));
		container.getChildren().add(progress);
		container.getChildren().add(createSpacer());
		container.getChildren().add(btn_cancel);

		root.getChildren().add(container);
		stage.setScene(scene);
		stage.show();
	}

	private Node createSpacer() {
		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		return spacer;
	}

	public void hide() {
		Platform.runLater(new Runnable() {
			public void run() {
				stage.hide();
			}
		});
	}
}