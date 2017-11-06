package org.blockly.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.blockly.util.LanguageProvider;

public class DataReceiverPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private List<List> values = Collections.synchronizedList(new ArrayList());
	private int MAX_VALUE = 1024;
	private int MIN_VALUE = 0;
	private static final int MAX_COUNT_OF_VALUES = 50;
	private static final Color[] COLOR = { Color.RED, Color.GREEN, Color.BLUE, Color.ORANGE, Color.MAGENTA, Color.CYAN,
			Color.PINK, Color.YELLOW };
	private int num = 0;
	private int left = 30;
	private int right = 10;
	private int top = 10;
	private int bottom = 50;
	private boolean isAlive = true;

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}

	public DataReceiverPanel(int n) {
		setNum(n);
	}

	public void begin() {
		new Thread(new Runnable() {
			public void run() {
				while (isAlive) {
					repaint();
					try {
						Thread.sleep(50L);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public void setNum(int num) {
		this.num = num;
		values.clear();
		for (int i = 0; i < this.num; i++) {
			values.add(new ArrayList());
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		String[] txt = { "" + MAX_VALUE * 1.0D,
				"" + MIN_VALUE + 3.0D * (MAX_VALUE - MIN_VALUE) / 4.0D,
				"" + MIN_VALUE + 2.0D * (MAX_VALUE - MIN_VALUE) / 4.0D,
				"" + MIN_VALUE + 1.0D * (MAX_VALUE - MIN_VALUE) / 4.0D, "" + MIN_VALUE * 1.0D };
		int strMaxWidth = 0;
		for (int i = 0; i < 5; i++) {
			if (g.getFontMetrics().stringWidth(txt[i]) > strMaxWidth) {
				strMaxWidth = g.getFontMetrics().stringWidth(txt[i]);
			}
		}

		left = (strMaxWidth + 5);

		int w = getWidth() - left - right;
		int h = getHeight() - top - bottom;
		float xDelta = w * 1.0F / 50.0F;

		g2d.setPaint(Color.WHITE);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		g2d.setPaint(Color.LIGHT_GRAY);
		g2d.fillRect(left, top, w, h);

		g2d.setColor(Color.BLACK);
		for (int i = 0; i < 5; i++) {
			int x = strMaxWidth - g.getFontMetrics().stringWidth(txt[i]);
			g2d.drawString(txt[i], x + 2, top + i * h / 4 + 3);
		}
		g2d.setStroke(new BasicStroke(0.5F));
		g2d.setColor(Color.WHITE);
		for (int i = 1; i < 4; i++) {
			g2d.drawLine(left, top + i * h / 4, left + w, top + i * h / 4);
		}

		g2d.setStroke(new BasicStroke(3.0F));
		int tmp = num > 5 ? 7 : num + 1;
		for (int i = 0; i < (num < 5 ? num : 5); i++) {
			g2d.setColor(Color.BLACK);
			g2d.drawString(LanguageProvider.getLocalString("value") + (i + 1), w / tmp * (i + 1), top + h + 30);
			g2d.setColor(COLOR[(i % COLOR.length)]);
			g2d.drawLine(w / tmp * (i + 1) + 45, top + h + 25, w / tmp * (i + 1) + 55, top + h + 25);
		}
		if (num > 5) {
			g2d.setColor(Color.BLACK);
			g2d.drawString("…", w / tmp * 6, top + h + 30);
		}

		g2d.setStroke(new BasicStroke(1.0F));
		for (int i = 0; i < num; i++) {
			g2d.setColor(COLOR[(i % COLOR.length)]);
			if ((values != null) && (values.size() > 0) && (values.get(i) != null)) {
				int length = ((List) values.get(i)).size();
				for (int j = 0; j < length - 1; j++) {
					int x1 = (int) (xDelta * (50 - length + j)) + left;
					int x2 = (int) (xDelta * (50 - length + j + 1)) + left;
					int y1 = normalizeValueForYAxis(((Float) ((List) values.get(i)).get(j)).floatValue(), h)
							+ top;
					int y2 = normalizeValueForYAxis(((Float) ((List) values.get(i)).get(j + 1)).floatValue(), h)
							+ top;

					g2d.drawLine(x1, y1, x2, y2);

					g2d.fillOval(x1 - 3, y1 - 3, 6, 6);
				}
			}
		}
	}

	public void addValue(ArrayList<Float> value) {
		if ((value == null) || (value.size() != num)) {
			return;
		}
		for (int i = 0; i < num; i++) {
			while (((List) values.get(i)).size() >= 50) {
				((List) values.get(i)).remove(0);
			}
			((List) values.get(i)).add(value.get(i));
		}
	}

	public void setMaxAndMin(int max, int min) {
		if (max > min) {
			MAX_VALUE = max;
			MIN_VALUE = min;
		}
	}

	private int normalizeValueForYAxis(float value, int height) {
		int re = (int) (height * (value - MIN_VALUE) / (MAX_VALUE - MIN_VALUE));
		return height - re;
	}

	private static void createGuiAndShow() {
		JFrame frame = new JFrame("Serial data curve");
		DataReceiverPanel dataPanel = new DataReceiverPanel(3);
		frame.getContentPane().add(dataPanel);
		dataPanel.begin();
		frame.setDefaultCloseOperation(3);
		frame.setSize(600, 400);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
	}

	public void clear() {
		this.values.clear();
		for (int i = 0; i < this.num; i++) {
			this.values.add(new ArrayList());
		}
	}
}