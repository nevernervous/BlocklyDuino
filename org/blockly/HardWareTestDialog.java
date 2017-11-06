package org.blockly;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class HardWareTestDialog {
	final Stage stage = new Stage(javafx.stage.StageStyle.UTILITY);
	private String msg = "Uploading hardware test program, Please wait for a minute.";
	Canvas canvas;
	private GraphicsContext gc;
	private double width = 500;
	private double height = 400;
	private double minWidth = 500;
	private double maxWidth = 1000;
	private int digitalNum;
	private int analogNum;
	private static int[] digitalArray = null;
	private static int[] analogArray = null;
	private List<ArrayList<Integer>> analogValues = new ArrayList<ArrayList<Integer>>();
	private int points = 0;
	private double xDelta = 20;
	private String com = "";
	private SerialPort serialPortCom;
	private StringBuffer strBuffer = new StringBuffer("");

	double leftpadding = 5;
	double rightpadding = 40;

	private Color[] myColors = { Color.RED, Color.GREEN, Color.BLUE, Color.ORANGE, Color.MAGENTA, Color.CYAN,
			Color.PINK, Color.YELLOW, Color.AQUAMARINE, Color.BROWN, Color.DARKGREY, Color.DEEPPINK,
			Color.DARKGOLDENROD, Color.SKYBLUE, Color.INDIGO, Color.GREENYELLOW };

	public HardWareTestDialog(Stage stg, String title, int digitalNum, final int analogNum, String com) {
		initData(digitalNum, analogNum, com);

		stage.initModality(Modality.APPLICATION_MODAL);

		stage.initOwner(stg);

		stage.setTitle(title);

		stage.setMinWidth(width + 18);
		stage.setMinHeight(height + 40);

		Group root = new Group();
		canvas = new Canvas(width, height);
		gc = canvas.getGraphicsContext2D();
		root.getChildren().add(canvas);
		Scene scene = new Scene(root, minWidth, height);
		stage.setScene(scene);
		stage.show();

		Timeline timeline = new Timeline();
		timeline.getKeyFrames().add(new KeyFrame(javafx.util.Duration.millis(100), new EventHandler<ActionEvent>() {
			public void handle(ActionEvent actionEvent) {
				for (int i = 0; i < analogNum; i++) {
					analogArray[i] = ((int) (Math.random() * 1024));
				}
				addValues(analogArray);
			}
		}, new KeyValue[0]));

		timeline.setCycleCount(-1);

		final AnimationTimer timer = new AnimationTimer() {
			public void handle(long now) {
				drawShapes(gc);
			}
		};
		timer.start();

		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent event) {

				timer.stop();

				MyBrowser.cmdIsKilled = true;

				strBuffer = new StringBuffer("");

				if ((serialPortCom != null)
						&& (serialPortCom.isOpened())) {
					try {
						serialPortCom.closePort();
						SerialPortDialog.serialPortCom = null;
					} catch (SerialPortException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	private void initData(int digitalNum, int analogNum, String com) {
		this.digitalNum = digitalNum;
		this.analogNum = analogNum;
		this.com = com;
		digitalArray = new int[digitalNum];
		analogArray = new int[analogNum];
		int max = Math.max(digitalNum, analogNum);
		double width = 40 + max * 30 + 15;

		if (width < minWidth) {
			this.width = minWidth;
		} else if (width > maxWidth) {
			this.width = maxWidth;
		} else {
			this.width = width;
		}

		for (int i = 0; i < this.analogNum; i++) {
			analogValues.add(new ArrayList());
		}
		points = ((int) ((width - 40 - 15) / xDelta + 1));
	}

	private void drawShapes(GraphicsContext gc) {
		double w = stage.getWidth() - 18;
		double h = stage.getHeight() - 40;
		canvas.setWidth(w);
		canvas.setHeight(h);

		gc.setFill(Color.WHITE);
		gc.clearRect(0, 0, w, h);
		gc.fillRect(0, 0, w, h);

		gc.setFill(Color.RED);
		gc.fillText(msg, 5, 15);
		gc.setFill(Color.BLACK);

		gc.setStroke(Color.GREEN);
		gc.setLineWidth(1);
		gc.strokeLine(0 + leftpadding, 80, w - rightpadding, 80);
		gc.setStroke(Color.BLACK);
		gc.strokeLine(0 + leftpadding, h - 50, w - rightpadding, h - 50);
		gc.setLineWidth(0.1D);
		gc.fillText("0", w - rightpadding + 3, h - 50 + 5);
		double deltaY = (h - 50 - 150) / 4;
		for (int i = 0; i < 4; i++) {
			gc.strokeLine(0 + leftpadding, h - 50 - deltaY * (i + 1), w - rightpadding,
					h - 50 - deltaY * (i + 1));
			gc.fillText("" + 256 * (i + 1), w - rightpadding + 3, h - 50 - deltaY * (i + 1) + 5);
		}

		gc.setStroke(Color.GREEN);
		gc.setFill(Color.GREEN);
		for (int i = 0; i < digitalNum; i++) {
			gc.setFill(Color.GREEN);
			gc.fillText("" + i, leftpadding + 5 + i * 30, 95);

			int data = digitalArray[i];
			if (data == 1) {
				gc.setFill(Color.GREEN);
			} else {
				gc.setFill(Color.LIGHTGREY);
			}
			gc.fillOval(leftpadding + 5 + i * 30 - 5, 50, 20, 20);
		}

		for (int i = 0; i < analogNum; i++) {
			gc.setStroke(myColors[(i % myColors.length)]);
			gc.setFill(myColors[(i % myColors.length)]);
			gc.setLineWidth(1);
			gc.fillText("A" + i, leftpadding + 5 + i * 30, h - 30);
			gc.fillRect(leftpadding + 5 + i * 30, h - 27, 15, 6);

			xDelta = ((w - 5 - 40) / (points - 1));
			if ((analogValues != null) && (analogValues.size() > 0) && (analogValues.get(i) != null)) {
				int length = ((ArrayList) analogValues.get(i)).size();
				for (int j = 0; j < length - 1; j++) {
					double x1 = xDelta * (points - length + j) + leftpadding;
					double y1 = ((Integer) ((ArrayList) analogValues.get(i)).get(j)).intValue();
					double x2 = xDelta * (points - length + j + 1) + leftpadding;
					double y2 = ((Integer) ((ArrayList) analogValues.get(i)).get(j + 1)).intValue();
					y1 = h - 50 - y1 / 1024 * (h - 150 - 50);
					y2 = h - 50 - y2 / 1024 * (h - 150 - 50);

					gc.strokeLine(x1, y1, x2, y2);
				}
			}
		}

		gc.setStroke(Color.BLACK);
	}

	public void hide() {
		Platform.runLater(new Runnable() {
			public void run() {
				stage.hide();
			}
		});
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public void resetSerial() {
		String name = com;
		try {
			serialPortCom = new SerialPort(name);
			if (!serialPortCom.isOpened()) {
				serialPortCom.openPort();
			}
			serialPortCom.setParams(9600, 8, 1, 0);
			int mask = 25;

			serialPortCom.setEventsMask(mask);
			serialPortCom.addEventListener(new SerialPortReader());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	class SerialPortReader implements SerialPortEventListener {
		SerialPortReader() {
		}

		public void serialEvent(SerialPortEvent event) {
			if ((event.isRXCHAR()) && (event.getEventValue() > 0)) {
				try {
					String msg = serialPortCom.readString();
					transfer(msg);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	private void transfer(String msg) {
		try {
			strBuffer.append(msg);
			String str = strBuffer.toString();
			int lastIndex = str.lastIndexOf("\r\n");
			if (str.contains("\r\n")) {
				String[] s = str.split("(\r\n)+");
				int new_length = str.endsWith("\r\n") ? s.length : s.length - 1;
				for (int i = 0; i < new_length; i++) {
					String tmp = s[i].trim();

					if (tmp.startsWith("digital")) {
						String[] valueStr = tmp.substring(7).split(" ");
						int num = valueStr.length;
						for (int k = 0; k < num && k < digitalArray.length; k++) {
							float f = 0.0F;
							f = Float.parseFloat(valueStr[k].trim());
							digitalArray[k] = (int) f;
						}
					} else if (tmp.startsWith("analog")) {
						String[] valueStr = tmp.substring(6).split(" ");
						int num = valueStr.length;
						for (int k = 0; k < num && k < analogArray.length; k++) {
							float f = 0.0F;
							f = Float.parseFloat(valueStr[k].trim());
							analogArray[k] = ((int) f);
						}

						addValues(analogArray);
					}
				}
				strBuffer = new StringBuffer(strBuffer.substring(lastIndex + 2));
			}
		} catch (Exception e) {
			strBuffer = new StringBuffer("");
		}
	}

	private void addValues(int[] analogArray) {
		if (analogArray == null)
			return;
		for (int i = 0; i < analogNum; i++) {
			while (((ArrayList) analogValues.get(i)).size() >= points) {
				((ArrayList) analogValues.get(i)).remove(0);
			}
			((ArrayList) analogValues.get(i)).add(Integer.valueOf(analogArray[i]));
		}
	}

	private void printData() {
		for (int i = 0; i < analogValues.size(); i++) {
			System.out.print(i + "-" + ((ArrayList) analogValues.get(i)).size() + " ");
			System.out.println(((ArrayList) analogValues.get(i)).toString());
		}
	}
}