package org.blockly;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.blockly.util.Coder;
import org.blockly.util.FileUtil;
import org.blockly.util.HardWareUtil;
import org.blockly.util.LanguageProvider;
import org.blockly.util.MyMethod;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jssc.SerialPort;
import jssc.SerialPortException;

public class Browser extends Region {
	SerialPort serialPortCom;
	private static double progressSize = 20;
	private static boolean shouldSaveConfig = true;
	private boolean isalive = true;
	private static boolean compileSuccess = true;
	private static boolean uploadSuccess = true;
	private static String xmlFileName = null;
	private static Boolean cleared = Boolean.valueOf(false);
	public static SerialPortDialog portDialog = new SerialPortDialog();
	public static LibManagerDialog libmanagerDialog = new LibManagerDialog();
	public static UnSaveWarningDialog unSaveWarningDialog = new UnSaveWarningDialog();
	private StateDialog sd;
	private HardWareTestDialog hardwareDialog;
	private static double fontSize = 13;
	private static String curCom = null;
	static String[] pre = null;
	static String[] cur = null;
	public static boolean cmdIsKilled = false;
	static Process process;

	public void setIsalive(boolean isalive) {
		this.isalive = isalive;
	}

	private SplitPane sp = new SplitPane();
	private StackPane sp1 = new StackPane();
	private StackPane sp2 = new StackPane();
	private VBox below = new VBox();
	private HBox toolBar;
	static Button btn_hardware_test = new Button();
	static Button clear = new Button(LanguageProvider.getLocalString("new"));
	static Button save1 = new Button(LanguageProvider.getLocalString("save"));
	static Button save2 = new Button(LanguageProvider.getLocalString("save_as"));
	static Button load = new Button(LanguageProvider.getLocalString("open"));
	static Button openSerialPort = new Button(LanguageProvider.getLocalString("port_monitor"));
	static final Slider slider = new Slider();
	static Button compile = new Button(LanguageProvider.getLocalString("compile"));
	static Button upload = new Button(LanguageProvider.getLocalString("upload"));
	static ComboBox<String> boardsComboBox = new ComboBox<String>();
	static ComboBox<String> comboBox = new ComboBox<String>();
	static final ProgressIndicator progress = new ProgressIndicator();
	static final ProgressIndicator progress2 = new ProgressIndicator();
	static final TextArea output_text = new TextArea();
	static final WebView browser = new WebView();
	static final WebEngine webEngine = browser.getEngine();
	private static int sumCmd = 1;

	private static String webPath;

	public static String arduinoPath;

	private static String projectPath;

	private static String releasePath;

	private static String arduino_complie;

	private static String arduino_save;

	private static String arduino_upload;

	private static String serialPort;
	public static String cofig = "config.properties";
	private Stage stage;
	private static String version = "";

	private void setButtonDisable(final Boolean state) {
		Platform.runLater(new Runnable() {
			public void run() {
				clear.setDisable(state.booleanValue());
				save1.setDisable(state.booleanValue());
				save2.setDisable(state.booleanValue());
				load.setDisable(state.booleanValue());
				openSerialPort.setDisable(state.booleanValue());
				compile.setDisable(state.booleanValue());
				upload.setDisable(state.booleanValue());
				boardsComboBox.setDisable(state.booleanValue());
				comboBox.setDisable(state.booleanValue());
				btn_hardware_test.setDisable(state.booleanValue());
			}
		});
	}

	public static void setUploadDisable(Boolean state) {
		upload.setDisable(state.booleanValue());
	}

	public static void setHardWareTestDisable(Boolean state) {
		btn_hardware_test.setDisable(state.booleanValue());
	}

	public Browser(Stage stage) {
		this.stage = stage;
		init();
		initToolBar();
		setToolTips();
		setStyle();
		
		browser.setContextMenuEnabled(false);
		webEngine.load(webPath);
		below.getChildren().addAll(new Node[] { toolBar, output_text });
		sp1.getChildren().addAll(new Node[] { browser });
		sp2.getChildren().addAll(new Node[] { below });
		sp.setOrientation(Orientation.VERTICAL);
		sp.getItems().addAll(new Node[] { sp1, sp2 });
		sp.setDividerPositions(new double[] { 0.82 });
		getChildren().add(sp);

		webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
			public void changed(ObservableValue<? extends Worker.State> ov, Worker.State oldState,
					Worker.State newState) {
				if (newState == Worker.State.SUCCEEDED) {
					try {
						if (!cleared.booleanValue()) {
							webEngine.executeScript("Blockly.mainWorkspace.clear();renderContent();");
							cleared = Boolean.valueOf(true);

							setInterruptByBoard(
									boardsComboBox.getSelectionModel().getSelectedItem().toString());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	private void setToolTips() {
		slider.setTooltip(new Tooltip("Drag the zoom interface."));
	}

	private void setStyle() {
		progress.setVisible(false);
		progress.setStyle("-fx-accent: red;");
		progress.setPrefSize(progressSize, progressSize);
		progress2.setVisible(false);
		progress2.setStyle("-fx-accent: red;");
		progress2.setPrefSize(progressSize, progressSize);

		clear.setFont(new Font(fontSize));
		save1.setFont(new Font(fontSize));
		save2.setFont(new Font(fontSize));
		load.setFont(new Font(fontSize));
		compile.setFont(new Font(fontSize));
		upload.setFont(new Font(fontSize));
		openSerialPort.setFont(new Font(fontSize));
		double minwidth = 55;
		clear.setMinWidth(minwidth);
		save1.setMinWidth(minwidth);
		save2.setMinWidth(minwidth);
		load.setMinWidth(minwidth);
		compile.setMinWidth(minwidth);
		upload.setMinWidth(minwidth);
		openSerialPort.setMinWidth(minwidth);
		clear.setPadding(new Insets(8, 5, 8, 5));
		save1.setPadding(new Insets(8, 5, 8, 5));
		save2.setPadding(new Insets(8, 5, 8, 5));
		load.setPadding(new Insets(8, 5, 8, 5));
		compile.setPadding(new Insets(8, 5, 8, 5));
		upload.setPadding(new Insets(8, 5, 8, 5));
		openSerialPort.setPadding(new Insets(8, 5, 8, 5));
		final Background bg = new Background(new BackgroundFill[] {
				new BackgroundFill(Color.rgb(153, 153, 153, 1), new CornerRadii(0), null) });
		final Background bg_dark_gray = new Background(new BackgroundFill[] {
				new BackgroundFill(Color.rgb(100, 100, 100, 1), new CornerRadii(0), null) });
		Background bg_gray = new Background(new BackgroundFill[] {
				new BackgroundFill(Color.rgb(200, 200, 200, 1), new CornerRadii(6), null) });
		toolBar.setPadding(new Insets(0));
		toolBar.setBackground(bg);
		clear.setBackground(bg);
		save1.setBackground(bg);
		save2.setBackground(bg);
		load.setBackground(bg);
		compile.setBackground(bg);
		upload.setBackground(bg);
		openSerialPort.setBackground(bg);
		boardsComboBox.setBackground(bg_gray);
		comboBox.setBackground(bg_gray);
		clear.setTextFill(Color.WHITE);
		save1.setTextFill(Color.WHITE);
		save2.setTextFill(Color.WHITE);
		load.setTextFill(Color.WHITE);
		compile.setTextFill(Color.WHITE);
		upload.setTextFill(Color.WHITE);
		openSerialPort.setTextFill(Color.WHITE);

		Image image = new Image("hardware.png", 23, 23, false, false);
		ImageView imageview = new ImageView(image);
		btn_hardware_test.setGraphic(imageview);
		btn_hardware_test.setBackground(bg);
		btn_hardware_test.setTooltip(new Tooltip("Hardware test"));
		btn_hardware_test.setPadding(new Insets(4, 5, 4, 5));

		final DropShadow shadow = new DropShadow();
		
		clear.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				clear.setBackground(bg_dark_gray);
			}
		});
		
		save1.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				save1.setBackground(bg_dark_gray);
			}
		});
		
		save2.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				save2.setBackground(bg_dark_gray);
			}
		});
		
		load.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				load.setBackground(bg_dark_gray);
			}
		});
		
		compile.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				compile.setBackground(bg_dark_gray);
			}
		});
		
		upload.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				upload.setBackground(bg_dark_gray);
			}
		});
		
		openSerialPort.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				openSerialPort.setBackground(bg_dark_gray);
			}
		});
		
		btn_hardware_test.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				btn_hardware_test.setBackground(bg_dark_gray);
			}
		});
		
		boardsComboBox.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				boardsComboBox.setEffect(shadow);
			}
		});
		
		comboBox.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				comboBox.setEffect(shadow);
			}

		});
		
		clear.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				clear.setBackground(bg);
			}
		});
		
		save1.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				save1.setBackground(bg);
			}
		});
		
		save2.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				save2.setBackground(bg);
			}
		});
		
		load.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				load.setBackground(bg);
			}
		});
		
		compile.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				compile.setBackground(bg);
			}
		});
		
		upload.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				upload.setBackground(bg);
			}
		});
		
		openSerialPort.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				openSerialPort.setBackground(bg);
			}
		});
		
		btn_hardware_test.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				btn_hardware_test.setBackground(bg);
			}
		});
		
		boardsComboBox.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				boardsComboBox.setEffect(null);
			}
		});
		
		comboBox.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				comboBox.setEffect(null);
			}
		});
		
	}

	private void initToolBar() {
		toolBar = new HBox();
		toolBar.setAlignment(Pos.CENTER);

		toolBar.getChildren().add(clear);
		toolBar.getChildren().add(createSpacer(1));
		toolBar.getChildren().add(load);
		toolBar.getChildren().add(createSpacer(1));
		toolBar.getChildren().add(save1);
		toolBar.getChildren().add(createSpacer(1));
		toolBar.getChildren().add(save2);
		toolBar.getChildren().add(createSpacer(1));

		slider.setMin(50);
		slider.setMax(150);
		slider.setValue(100);
		slider.setPrefWidth(80);
		slider.setOrientation(Orientation.HORIZONTAL);
		toolBar.getChildren().add(createSpacer2());

		toolBar.getChildren().add(progress);
		toolBar.getChildren().add(createSpacer3(5));
		toolBar.getChildren().add(createSpacer(1));
		toolBar.getChildren().add(compile);
		toolBar.getChildren().add(createSpacer(1));
		toolBar.getChildren().add(createSpacer3(5));
		toolBar.getChildren().add(progress2);
		toolBar.getChildren().add(createSpacer3(5));
		toolBar.getChildren().add(createSpacer(1));
		toolBar.getChildren().add(upload);
		toolBar.getChildren().add(createSpacer(1));

		List<String> boardList = null;
		if (arduinoPath.contains(".app")) {
			String tmp = arduinoPath.substring(0, arduinoPath.indexOf(".app") + 4) + "/Contents/Java/";
			boardList = MyMethod.getBoardsList(tmp + "hardware/");
		} else {
			boardList = MyMethod.getBoardsList(arduinoPath + "hardware/");
		}
		for (int i = 0; i < boardList.size(); i++) {
			boardsComboBox.getItems().addAll(MyMethod.getBoards((String) boardList.get(i)));
		}
		boardsComboBox.setPrefWidth(200.0D);
		String currentBoard = MyMethod.getProp(cofig, "currentBoard").trim();
		if ((currentBoard == null) || (currentBoard.equals(""))
				|| (!boardsComboBox.getItems().contains(currentBoard))) {
			boardsComboBox.getSelectionModel().selectFirst();
		} else {
			boardsComboBox.setValue(currentBoard);
		}

		boardsComboBox.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				try {
					saveCurrentBoard();
					String board = boardsComboBox.getSelectionModel().getSelectedItem().toString();

					setInterruptByBoard(board);

				} catch (Exception localException) {
				}
			}
		});

		toolBar.getChildren().add(createSpacer3(10));
		toolBar.getChildren().add(boardsComboBox);
		toolBar.getChildren().add(createSpacer3(10));
		toolBar.getChildren().add(comboBox);
		toolBar.getChildren().add(createSpacer3(10));
		toolBar.getChildren().add(createSpacer(1));
		toolBar.getChildren().add(openSerialPort);
		toolBar.getChildren().add(createSpacer(1));
		toolBar.getChildren().add(btn_hardware_test);
		toolBar.getChildren().add(slider);

		new Thread(new Runnable() {

			public void run() {
				while (isalive) {
					cur = org.blockly.util.MyCom.getSerialNames();
					if (!MyMethod.compareStrArray(pre, cur)) {
						setCom(cur);
						pre = cur;
					}
					try {
						Thread.sleep(300L);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();

		slider.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				float value = new_val.floatValue();
				browser.setZoom(value / 100.0);
			}
		});

		openSerialPort.setOnMouseClicked(new EventHandler<Event>() {
			public void handle(Event arg0) {
				try {
					if ((serialPortCom != null) && (serialPortCom.isOpened())) {
						serialPortCom.closePort();
					}
				} catch (SerialPortException e) {
					e.printStackTrace();
				}

				String titile = ((String) comboBox.getValue()).toString();

				if ((titile != null) && (!titile.equals(""))) {
					portDialog.setTitle(titile);
					SerialPortDialog.textArea.setText("");
					SerialPortDialog.com = titile;
					portDialog.setLocationRelativeTo(null);
					portDialog.setVisible(true);
					portDialog.toFront();

					setUploadDisable(Boolean.valueOf(true));
					setHardWareTestDisable(Boolean.valueOf(true));

					if ((SerialPortDialog.serialPortCom != null) && (SerialPortDialog.serialPortCom.isOpened())) {
						try {
							SerialPortDialog.serialPortCom.closePort();
						} catch (SerialPortException e) {
							e.printStackTrace();
						}
						SerialPortDialog.serialPortCom = null;
					}

					SerialPortDialog.resetSerial();
				} else {
					updateTextArea("No serial port is available.");
				}

			}
		});

		compile.setOnMouseClicked(new EventHandler<Event>() {
			public void handle(Event arg0) {
				sd = new StateDialog(stage, LanguageProvider.getLocalString("msg_compiling"));
				cmdIsKilled = false;
				doCompile();
			}

		});

		upload.setOnMouseClicked(new EventHandler<Event>() {
			public void handle(Event arg0) {
				if (((String) comboBox.getValue()).trim().equals("")) {
					updateTextArea("No serial port.");
					return;
				}
				sd = new StateDialog(stage, LanguageProvider.getLocalString("msg_uploading"));
				cmdIsKilled = false;
				doUpLoad();
			}

		});

		clear.setOnMouseClicked(new EventHandler<Event>() {
			public void handle(Event arg0) {
				if (isXmlSaved()) {
					webEngine.executeScript("Blockly.mainWorkspace.clear();renderContent();");
					xmlFileName = null;
					stage.setTitle(getMyTitle());
				} else {
					unSaveWarningDialog.setYesOrNo(false);
					unSaveWarningDialog.setLocationRelativeTo(null);
					unSaveWarningDialog.setVisible(true);
					unSaveWarningDialog.toFront();

					new Thread() {
						public void run() {
							while (unSaveWarningDialog.isVisible()) {
								try {
									Thread.sleep(100L);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							if (unSaveWarningDialog.isYesOrNo()) {
								Platform.runLater(new Runnable() {
									public void run() {
										webEngine.executeScript("Blockly.mainWorkspace.clear();renderContent();");
										xmlFileName = null;
										stage.setTitle(getMyTitle());
									}

								});
							}
						}
					}.start();
				}
			}
		});

		save1.setOnMouseClicked(new EventHandler<Event>() {
			public void handle(Event arg0) {
				String currentBoard = boardsComboBox.getSelectionModel().getSelectedItem().toString();
				String xml = (String) webEngine.executeScript("Blockly.Xml.domToText(Blockly.Xml.workspaceToDom(Blockly.mainWorkspace))");
				String version = MyMethod.getProp(cofig, "version");
				xml = xml.replaceFirst("<xml", "<xml version=\"" + version + "\" board=\"" + currentBoard + "\"");
				if (xmlFileName == null) {
					FileChooser fd = new FileChooser();
					fd.setInitialFileName("arduino.blo");
					fd.setTitle("Save as");
					if (xmlFileName != null) {
						File defaultPath = new File(xmlFileName).getParentFile().getAbsoluteFile();
						fd.setInitialDirectory(defaultPath);
					}
					fd.getExtensionFilters().add(new FileChooser.ExtensionFilter("blo File", new String[] { "*.blo" }));
					File xmlFile = fd.showSaveDialog(stage);
					if (xmlFile != null) {
						String filePath = xmlFile.getAbsolutePath();
						if ((!filePath.endsWith(".blo")) && (!filePath.endsWith(".BLO"))) {

							filePath = filePath + ".blo";
						}
						MyMethod.saveXML(xml, filePath);
						xmlFileName = filePath;
						stage.setTitle(getMyTitle() + "(" + xmlFileName + ")");
					}
				} else {
					MyMethod.saveXML(xml, xmlFileName);
				}

			}
		});

		save2.setOnMouseClicked(new EventHandler<Event>() {
			public void handle(Event arg0) {
				String currentBoard = boardsComboBox.getSelectionModel().getSelectedItem().toString();
				String xml = (String) webEngine
						.executeScript("Blockly.Xml.domToText(Blockly.Xml.workspaceToDom(Blockly.mainWorkspace))");
				String version = MyMethod.getProp(cofig, "version");
				xml = xml.replaceFirst("<xml", "<xml version=\"" + version + "\" board=\"" + currentBoard + "\"");

				FileChooser fd = new FileChooser();
				fd.setInitialFileName("arduino.blo");
				fd.setTitle("Save as");
				if (xmlFileName != null) {
					File defaultPath = new File(xmlFileName).getParentFile().getAbsoluteFile();
					fd.setInitialDirectory(defaultPath);
				}
				fd.getExtensionFilters().add(new FileChooser.ExtensionFilter("blo File", new String[] { "*.blo" }));
				File xmlFile = fd.showSaveDialog(stage);
				if (xmlFile != null) {
					String filePath = xmlFile.getAbsolutePath();
					if ((!filePath.endsWith(".blo")) && (!filePath.endsWith(".BLO"))) {

						filePath = filePath + ".blo";
					}
					MyMethod.saveXML(xml, filePath);
					xmlFileName = filePath;
					stage.setTitle(getMyTitle() + "(" + xmlFileName + ")");
				}

			}
		});

		load.setOnMouseClicked(new EventHandler<Event>() {
			public void handle(Event arg0) {
				FileChooser fd = new FileChooser();
				fd.setTitle("Open");
				if (xmlFileName != null) {
					File defaultPath = new File(xmlFileName).getParentFile().getAbsoluteFile();
					fd.setInitialDirectory(defaultPath);
				} else {
					File defaultPath = new File("sample");
					if ((!defaultPath.exists()) && (!defaultPath.isDirectory())) {
						defaultPath.mkdir();
					}
					fd.setInitialDirectory(defaultPath);
				}
				fd.getExtensionFilters()
						.add(new FileChooser.ExtensionFilter("xml/blo File", new String[] { "*.xml", "*.blo" }));
				final File xmlFile = fd.showOpenDialog(stage);
				if (xmlFile != null) {
					String result = MyMethod.readXml(xmlFile);

					final String boardName = MyMethod.readBoardNameFromXml(xmlFile);

					if (result != null) {
						try {
							result = result.replaceAll("\"", "\\\\\"");

							final String tmp = result;
							Platform.runLater(new Runnable() {
								public void run() {
									try {
										webEngine.executeScript("var xml = Blockly.Xml.textToDom(\"" + tmp + "\");");
										webEngine.executeScript("Blockly.mainWorkspace.clear();");
										webEngine.executeScript("Blockly.Xml.domToWorkspace(Blockly.mainWorkspace, xml);");
										webEngine.executeScript("renderContent();");
										if ((boardName != null) && (!boardName.equals(""))) {
											boardName.equals("mylib");
										}
									} catch (Exception e) {
										e.printStackTrace();
									}

									xmlFileName = xmlFile.getAbsolutePath();
									stage.setTitle(getMyTitle() + "(" + xmlFileName + ")");
								}
							});
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

				}
			}
		});

		btn_hardware_test.setOnMouseClicked(new EventHandler<Event>() {
			public void handle(Event arg0) {
				if (((String) comboBox.getValue()).trim().equals("")) {
					updateTextArea("No Serial Port.");
					return;
				}

				int analogNum = HardWareUtil.getNumOfAnalogPin(webEngine);
				int digitalNum = HardWareUtil.getNumOfDigitalPin(webEngine) - analogNum;
				String code = HardWareUtil.getHardWareTestCode(digitalNum, analogNum);

				MyMethod.saveCode(code, "testArduino/testArduino.ino");

				String board = boardsComboBox.getSelectionModel().getSelectedItem().toString();
				String com = ((String) comboBox.getSelectionModel().getSelectedItem()).toString();
				hardwareDialog = new HardWareTestDialog(stage,
						LanguageProvider.getLocalString("hardwaretest") + "(" + com + ")：" + board, digitalNum,
						analogNum, com);
				hardwareDialog.setMsg("Uploading hardware test program, Please wait for a minute.");

				doUpLoadForHardWareTest();
			}
		});
	}

	private void doCompile() {
		try {
			String code = (String) webEngine.executeScript("Blockly.Arduino.workspaceToCode()");

			if (((Boolean) webEngine.executeScript("document.getElementById('tab_arduino').className == 'tabon'"))
					.booleanValue()) {
				code = (String) webEngine.executeScript("document.getElementById('content_arduino').value");
			}
			MyMethod.saveYuanma(code);

			new Thread(new Runnable() {
				public void run() {
					setProgressBar(true, 1);
					setButtonDisable(true);

					try {
						compileSuccess = true;
						compile();
						if (cmdIsKilled) {
							updateTextArea(String.valueOf(LanguageProvider.getLocalString("compile"))
									+ LanguageProvider.getLocalString("canceled") + "\n");
						} else if (compileSuccess) {
							updateTextArea(
									String.valueOf(LanguageProvider.getLocalString("compile_success")) + "\n");
						} else {
							updateTextArea(
									String.valueOf(LanguageProvider.getLocalString("compile_failed")) + "\n");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					setProgressBar(false, 1);
					setButtonDisable(false);
					sd.hide();
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void doUpLoad() {
		try {
			String code = (String) webEngine.executeScript("Blockly.Arduino.workspaceToCode()");

			if (((Boolean) webEngine.executeScript("document.getElementById('tab_arduino').className == 'tabon'"))
					.booleanValue()) {
				code = (String) webEngine.executeScript("document.getElementById('content_arduino').value");
			}
			MyMethod.saveYuanma(code);

			new Thread(new Runnable() {
				public void run() {
					try {
						if (serialPortCom != null && serialPortCom.isOpened()) {
							serialPortCom.closePort();
						}
					} catch (SerialPortException e) {
						e.printStackTrace();
					}

					setProgressBar(true, 2);
					setButtonDisable(true);
					try {
						uploadSuccess = true;
						upload();
						if (cmdIsKilled) {
							updateTextArea(String.valueOf(LanguageProvider.getLocalString("upload"))
									+ LanguageProvider.getLocalString("canceled") + "\n");
						} else if (uploadSuccess) {
							updateTextArea(String.valueOf(LanguageProvider.getLocalString("upload_success")) + "\n");
							if (MyMethod.getOsName().toLowerCase().startsWith("mac")
									|| MyMethod.getOsName().toLowerCase().startsWith("linux")) {
								serialPortCom = new SerialPort(serialPort);
								try {
									serialPortCom.openPort();
								} catch (SerialPortException e) {
									e.printStackTrace();
								}
							}
						} else {
							updateTextArea(
									String.valueOf(LanguageProvider.getLocalString("upload_failed")) + "\n");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					setProgressBar(false, 2);
					setButtonDisable(false);
					sd.hide();
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void doUpLoadForHardWareTest() {
		cmdIsKilled = false;

		try {
			new Thread(new Runnable() {
				public void run() {
					try {
						if (serialPortCom != null && serialPortCom.isOpened()) {
							serialPortCom.closePort();
						}
					} catch (SerialPortException e) {
						e.printStackTrace();
					}
					setProgressBar(true, 2);
					setButtonDisable(true);
					try {
						uploadSuccess = true;
						upload();
						if (cmdIsKilled) {
							updateTextArea(String.valueOf(LanguageProvider.getLocalString("upload"))
									+ LanguageProvider.getLocalString("canceled") + "\n");
						} else if (uploadSuccess) {
							updateTextArea(
									String.valueOf(LanguageProvider.getLocalString("upload_success")) + "\n");
							hardwareDialog
									.setMsg("Hardware test program upload success and is monitoring the pin state!");
							hardwareDialog.resetSerial();
						} else {
							updateTextArea(
									String.valueOf(LanguageProvider.getLocalString("upload_failed")) + "\n");
							hardwareDialog.setMsg(
									"ERROR: Hardware test program upload failed, unable to monitor the pin state!");
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

					setProgressBar(false, 2);
					setButtonDisable(false);
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void layoutChildren() {
		double w = getWidth();
		double h = getHeight();
		double toolHeight = this.toolBar.getHeight();
		double div = this.sp.getDividerPositions()[0] * h;
		layoutInArea(this.sp, 0, 0, w, h, 0, javafx.geometry.HPos.CENTER, javafx.geometry.VPos.CENTER);
		output_text.setPrefHeight(h - div - toolHeight);
	}

	private Button createSpacer(double w) {
		Button spacer = new Button();
		spacer.setPrefWidth(w);
		spacer.setPadding(new Insets(8, 0, 8, 0));
		Background bg = new Background(
				new BackgroundFill[] { new BackgroundFill(Color.rgb(255, 255, 255, 0), null, null) });
		spacer.setBackground(bg);
		return spacer;
	}

	private Node createSpacer2() {
		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		return spacer;
	}

	private Node createSpacer3(double w) {
		Region spacer = new Region();
		spacer.setPrefWidth(w);
		return spacer;
	}

	private static void init() {
		String curDir = new File("").getAbsolutePath();

		arduinoPath = MyMethod.getProp(cofig, "arduinoPath");
		arduino_save = MyMethod.getProp(cofig, "arduino_save");
		if (MyMethod.getOsName().toLowerCase().startsWith("win")) {
			arduino_complie = MyMethod.getProp(cofig, "arduino_complie") + " --verify \"" + curDir
					+ "/testArduino/testArduino.ino\"";
			arduino_upload = MyMethod.getProp(cofig, "arduino_upload") + " \"" + curDir
					+ "/testArduino/testArduino.ino\"";
		} else {
			arduino_complie = MyMethod.getProp(cofig, "arduino_complie") + " --verify " + curDir
					+ "/testArduino/testArduino.ino";
			arduino_upload = MyMethod.getProp(cofig, "arduino_upload") + " " + curDir + "/testArduino/testArduino.ino";
		}
		projectPath = "testArduino";
		serialPort = "COM0";
		releasePath = projectPath + "/Release/";
		webPath = MyMethod.getProp(cofig, "webPath") + "?lang=" + MyMethod.getProp(cofig, "language");

		if ((!webPath.startsWith("http://")) && (!webPath.startsWith("file:///"))) {

			File directory = new File("");
			webPath = "file:///" + directory.getAbsolutePath() + "/" + webPath;
		}
	}

	private static void upload() {
		try {
			serialPort = ((String) comboBox.getSelectionModel().getSelectedItem()).toString();
			if ((serialPort != null) && (!serialPort.equals(""))) {
				curCom = serialPort;
			}

			String tmp = boardsComboBox.getSelectionModel().getSelectedItem().toString();
			String cpu = "";
			String tmp2 = tmp;
			if (tmp.contains("[")) {
				tmp2 = tmp.substring(0, tmp.indexOf("["));

				cpu = ":cpu=" + tmp.substring(tmp.indexOf("[") + 1, tmp.length() - 1);
			}

			String board = null;

			String tmpBoardPath = (String) MyMethod.boardName_Path.get(tmp);
			String tmpPreStr = tmpBoardPath
					.substring(tmpBoardPath.indexOf("hardware") + 9,
							tmpBoardPath.lastIndexOf(System.getProperty("file.separator")))
					.replaceAll("\\\\", ":").replaceAll("/", ":") + ":";
			board = tmpPreStr + MyMethod.getCompileKeyByBoards(tmpBoardPath, tmp2);

			String cmd_upload = arduinoPath
					+ arduino_upload.replace("{board}", board).replace("{cpu}", cpu).replace("{port}", serialPort);
			System.out.println(cmd_upload);
			clearTextArea();
			String cmd_save = arduinoPath + arduino_save.replace("{board}", board).replace("{cpu}", cpu);
			if (shouldSaveConfig) {
				execcmd(cmd_save);
				shouldSaveConfig = false;
			}
			execcmd(cmd_upload);
		} catch (Exception e) {
			e.printStackTrace();
			uploadSuccess = false;
		}
	}

	private static void compile() {
		String tmp = boardsComboBox.getSelectionModel().getSelectedItem().toString();
		String cpu = "";
		String tmp2 = tmp;
		if (tmp.contains("[")) {
			tmp2 = tmp.substring(0, tmp.indexOf("["));

			cpu = ":cpu=" + tmp.substring(tmp.indexOf("[") + 1, tmp.length() - 1);
		}
		String board = null;

		String tmpBoardPath = (String) MyMethod.boardName_Path.get(tmp);
		String tmpPreStr = tmpBoardPath
				.substring(tmpBoardPath.indexOf("hardware") + 9,
						tmpBoardPath.lastIndexOf(System.getProperty("file.separator")))
				.replaceAll("\\\\", ":").replaceAll("/", ":") + ":";
		board = tmpPreStr + MyMethod.getCompileKeyByBoards(tmpBoardPath, tmp2);

		String cmd_compile = arduinoPath + arduino_complie.replace("{board}", board).replace("{cpu}", cpu);
		clearTextArea();
		String cmd_save = arduinoPath + arduino_save.replace("{board}", board).replace("{cpu}", cpu);
		if (shouldSaveConfig) {
			execcmd(cmd_save);
			shouldSaveConfig = false;
		}
		execcmd(cmd_compile);
	}

	public static void execcmd(String cmd) {
		updateTextArea(cmd);
		try {
			process = Runtime.getRuntime().exec(cmd);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			OutputStream stdOutput = process.getOutputStream();
			String line = null;
			while (!cmdIsKilled && (line = stdInput.readLine()) != null) {
				if ((line.contains("error ")) || (line.contains("Error ")) || (line.contains("ERROR "))
						|| (line.contains("error:")) || (line.contains("Error:")) || (line.contains("ERROR:"))
						|| (line.contains("error=")) || (line.contains("出错"))
						|| (line.contains("timeout communicating with programmer")) || (line.contains("can't"))
						|| (line.contains("not in sync"))) {
					compileSuccess = false;
					uploadSuccess = false;
				}
				if (line.contains("SUCCESS")) {
					compileSuccess = true;
					uploadSuccess = true;
				}
				updateTextArea(line);

				if (line.contains("avrdude")) {
					stdOutput.flush();
					stdOutput.close();
					break;
				}
			}

			while (!cmdIsKilled && (line = stdError.readLine()) != null) {
				if ((line.contains("error ")) || (line.contains("Error ")) || (line.contains("ERROR "))
						|| (line.contains("error:")) || (line.contains("Error:")) || (line.contains("ERROR:"))
						|| (line.contains("error=")) || (line.contains("出错"))
						|| (line.contains("timeout communicating with programmer")) || (line.contains("can't"))
						|| (line.contains("not in sync"))) {
					compileSuccess = false;
					uploadSuccess = false;
				}
				if (line.contains("SUCCESS")) {
					compileSuccess = true;
					uploadSuccess = true;
				}
				updateTextArea(line);
			}
			if (cmdIsKilled) {
				process.destroy();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static LinkedBlockingQueue<String> textQueue = new LinkedBlockingQueue<String>();

	static AtomicInteger textAreaRunnableState = new AtomicInteger();

	static Runnable textAreaRunnable = new Runnable() {
		public void run() {
			while (true) {
				updateTextArea0();
				try {
					Thread.sleep(50L);
				} catch (InterruptedException localInterruptedException) {
					return;
				}
			}
		}
	};

	static void updateTextArea0() {
		int count = textQueue.size();
		int len = 0;
		List<String> texts = new ArrayList<String>(count);
		for (int i = 0; i < count; i++) {
			String text = (String) textQueue.poll();
			if (text == null)
				break;
			len += text.length();
			texts.add(text);
		}

		StringBuilder sb = new StringBuilder(len + count * 2);
		for (String text : texts) {
			sb.append(text + System.lineSeparator());
		}

		if ((sb == null) || (sb.toString().equals(""))) {
			return;
		}

		Platform.runLater(new Runnable() {
			public void run() {
				output_text.appendText(sb.toString());
			}
		});
	}

	private static void updateTextArea(String s) {
		textQueue.offer(Coder.getChineseString(s));

		if (textAreaRunnableState.compareAndSet(0, 1)) {
			Thread t = new Thread(textAreaRunnable);
			t.setDaemon(true);
			t.start();
		}
	}

	private static void clearTextArea() {
		Platform.runLater(new Runnable() {
			public void run() {
				output_text.setText("");
			}
		});
	}

	private static void setProgressBar(final boolean visible, int k) {
		Platform.runLater(new Runnable() {
			public void run() {
				if (k == 1) {
					progress.setVisible(visible);
				} else if (k == 2) {
					progress2.setVisible(visible);
				}
			}
		});
	}

	private static void setCom(String[] s) {
		Platform.runLater(new Runnable() {
			public void run() {
				comboBox.getItems().clear();
				comboBox.setValue("");
				if ((s != null) && (s.length > 0)) {
					comboBox.getItems().addAll(s);
					if ((curCom != null) && (!curCom.equals(""))
							&& (arrayContain(cur, curCom))) {
						comboBox.setValue(curCom);
					} else {
						comboBox.getSelectionModel().selectFirst();
						comboBox.setValue(s[0]);
					}
				}
			}
		});
	}

	private static boolean arrayContain(String[] array, String value) {
		if ((array != null) && (array.length > 0)) {
			for (int i = 0; i < array.length; i++) {
				if (array[i].equals(value)) {
					return true;
				}
			}
		}
		return false;
	}

	private void saveCurrentBoard() {
		shouldSaveConfig = true;
		String str = boardsComboBox.getSelectionModel().getSelectedItem().toString();
		MyMethod.changeCurrentBoard(cofig, "currentBoard", str);
	}

	private void setInterruptByBoard(String board) {
		String str = "";

		String board_new = board.replaceAll("\\[.*\\]", "");

		if (webEngine.executeScript("profile[\"" + board_new + "\"]").toString().equals("undefined")) {
			str = str + "profile[\"default\"] = profile[\"arduino_standard\"];";
		} else {
			str = str + "profile[\"default\"] = profile[\"" + board_new + "\"];";
		}

		MyMethod.saveDigitalPinToInterrupt(str);
		webEngine.executeScript(str);

		webEngine.executeScript("renderContent();");
	}

	public static String getMyTitle() {
		 String version = MyMethod.getProp(cofig, "version");
		 return "BlocklyDuino " + version;
	}

	public static void refreshWeb() {
		Platform.runLater(new Runnable() {
			public void run() {
				webEngine.reload();
			}
		});
	}

	public static boolean isXmlSaved() {
		try {
			String xml = (String) webEngine
					.executeScript("Blockly.Xml.domToText(Blockly.Xml.workspaceToDom(Blockly.mainWorkspace))");
			xml = xml.replaceFirst("<xml(.*?)>", "<xml>");
			if ((xmlFileName == null) || (xmlFileName.equals(""))) {
				if (xml.equals("<xml></xml>")) {
					return true;
				}
				return false;
			}

			String save_xml = MyMethod.readXml(new File(xmlFileName)).toString().replaceFirst("<xml(.*?)>", "<xml>");

			if (xml.equals(save_xml)) {
				return true;
			}
			return false;

		} catch (Exception e) {

			e.printStackTrace();
		}
		return false;
	}
}