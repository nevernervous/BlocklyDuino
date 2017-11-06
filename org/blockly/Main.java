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
	private MyBrowser browser;
	private boolean stageIconified;

	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage stage) {
		MyMethod.createSomeFolder();
		MyMethod.setCustom();
		MyMethod.setCompanyLanguage();
		stage.setTitle(MyBrowser.getMyTitle());
		StackPane root = new StackPane();
		browser = new MyBrowser(stage);
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
				if (!MyBrowser.isXmlSaved()) {
					event.consume();
					MyBrowser.unSaveWarningDialog.setYesOrNo(false);
					MyBrowser.unSaveWarningDialog.setLocationRelativeTo(null);
					MyBrowser.unSaveWarningDialog.setVisible(true);
					MyBrowser.unSaveWarningDialog.toFront();
					new Thread() {
						public void run() {
							while (MyBrowser.unSaveWarningDialog.isVisible()) {
								try {
									Thread.sleep(100L);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							if (MyBrowser.unSaveWarningDialog.isYesOrNo()) {
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
				MyBrowser.portDialog.dispose();
			} catch (Exception localException) {
			}
			try {
				MyBrowser.libmanagerDialog.dispose();
			} catch (Exception localException1) {
			}
			try {
				MyBrowser.unSaveWarningDialog.dispose();
			} catch (Exception localException2) {
			}
		}
		System.exit(0);
	}
}