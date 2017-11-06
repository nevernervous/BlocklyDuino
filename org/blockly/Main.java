package org.blockly;

import org.blockly.util.MyMethod;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jssc.SerialPort;

public class Main extends Application {
	private Scene scene;
	private Browser browser;
	private boolean stageIconified;

	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage stage) {
		MyMethod.createSomeFolder();
		MyMethod.setCustom();
		MyMethod.setCompanyLanguage();
		stage.setTitle(Browser.getMyTitle());
		StackPane root = new StackPane();
		browser = new Browser(stage);
		root.getChildren().add(browser);
		scene = new Scene(root, 1150.0D, 650.0D);
		stage.setMaximized(true);
		
		stage.iconifiedProperty().addListener((observable, oldValue, newValue) -> {
            stageIconified = newValue;
        }
        );
        stage.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (stageIconified && newValue.booleanValue()) {
                stage.setIconified(false);
            }
        }
        );
        
		stage.setScene(scene);
		stage.getIcons().add(new Image("logo.png"));
		stage.show();
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent event) {
				if (!Browser.isXmlSaved()) {
					event.consume();
					Browser.unSaveWarningDialog.setYesOrNo(false);
					Browser.unSaveWarningDialog.setLocationRelativeTo(null);
					Browser.unSaveWarningDialog.setVisible(true);
					Browser.unSaveWarningDialog.toFront();
					new Thread() {
						public void run() {
							while (Browser.unSaveWarningDialog.isVisible()) {
								try {
									Thread.sleep(100L);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							if (Browser.unSaveWarningDialog.isYesOrNo()) {
								Platform.exit();
							}
						}
					}.start();
				}
			}
		});
	}

	public void stop() throws Exception {
		super.stop();
		browser.setIsalive(false);

		if ((SerialPortDialog.serialPortCom != null) && (SerialPortDialog.serialPortCom.isOpened())) {
			SerialPortDialog.serialPortCom.closePort();
			SerialPortDialog.serialPortCom = null;
		}
		if (MyMethod.getOsName().toLowerCase().startsWith("mac")) {
			System.exit(0);
		} else {
			try {
				Browser.portDialog.dispose();
			} catch (Exception localException) {
			}
			try {
				Browser.libmanagerDialog.dispose();
			} catch (Exception localException1) {
			}
			try {
				Browser.unSaveWarningDialog.dispose();
			} catch (Exception localException2) {
			}
		}
		System.exit(0);
	}
}