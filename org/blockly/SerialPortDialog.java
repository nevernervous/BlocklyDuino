package org.blockly;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import org.blockly.util.LanguageProvider;
import org.blockly.view.DataReceiverPanel;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class SerialPortDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private JTextField textField;
	private static JCheckBox chckbxNewCheckBox;
	public static JTextArea textArea;
	static SerialPort serialPortCom;
	static String com = "";
	private static JComboBox comboBox;
	private static DataReceiverPanel panel_huatu = new DataReceiverPanel(1);
	private CardLayout card = new CardLayout(0, 0);
	private JTextField textField_1;
	private JTextField textField_2;
	private static final String HEAD = "mixly";
	private static StringBuffer strBuffer = new StringBuffer("mixly\r\n");
	private static boolean isSetNum = false;

	public static void main(String[] args) {
		try {
			SerialPortDialog dialog = new SerialPortDialog();
			dialog.setDefaultCloseOperation(2);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SerialPortDialog() {
		setAlwaysOnTop(true);
		addWindowListener(new WindowListener() {
			public void windowOpened(WindowEvent e) {

				panel_huatu.begin();
			}

			public void windowIconified(WindowEvent e) {
			}

			public void windowDeiconified(WindowEvent e) {
			}

			public void windowDeactivated(WindowEvent e) {
			}

			public void windowClosing(WindowEvent e) {
				panel_huatu.clear();
				strBuffer = new StringBuffer("mixly\r\n");
				isSetNum = false;
				card.first(getContentPane());

				Browser.setUploadDisable(Boolean.valueOf(false));
				Browser.setHardWareTestDisable(Boolean.valueOf(false));

				if ((serialPortCom != null) && (serialPortCom.isOpened())) {
					try {
						serialPortCom.closePort();
						serialPortCom = null;
					} catch (SerialPortException e1) {
						e1.printStackTrace();
					}
				}
			}

			public void windowClosed(WindowEvent e) {
				panel_huatu.setAlive(false);
			}

			public void windowActivated(WindowEvent e) {
			}
		});
		setTitle("Serial monitor");
		setBounds(100, 100, 600, 400);
		getContentPane().setLayout(card);

		JPanel panel = new JPanel();
		getContentPane().add(panel, "name_39102077189140");
		panel.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		panel.add(scrollPane, "Center");

		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);

		JPanel panel_1 = new JPanel();
		panel.add(panel_1, "North");
		panel_1.setLayout(new BorderLayout(0, 0));

		textField = new JTextField();
		panel_1.add(textField);
		textField.setColumns(10);

		JButton button = new JButton(LanguageProvider.getLocalString("send"));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		button.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				String str = textField.getText();
				serialWriteData(str);
				textField.setText("");
			}
		});
		panel_1.add(button, "East");

		JPanel buttonPane = new JPanel();
		panel.add(buttonPane, "South");
		buttonPane.setLayout(new BorderLayout(0, 0));

		chckbxNewCheckBox = new JCheckBox(LanguageProvider.getLocalString("auto_scroll"));
		chckbxNewCheckBox.setSelected(true);
		buttonPane.add(chckbxNewCheckBox, "West");

		JButton button_1 = new JButton(LanguageProvider.getLocalString("clear"));
		button_1.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				textArea.setText("");
			}

		});
		panel_1 = new JPanel();
		buttonPane.add(panel_1, "Center");

		JLabel lblNewLabel = new JLabel(
				LanguageProvider.getLocalString("baud_rate"));
		panel_1.add(lblNewLabel);

		comboBox = new JComboBox();
		comboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if ((e.getStateChange() == 1) && (isVisible())) {
					resetSerialBaudRate();
				}

			}
		});
		comboBox.setModel(new DefaultComboBoxModel(new String[] { "110", "300", "600", "1200", "2400", "4800", "9600",
				"19200", "28800", "38400", "43000", "56000", "57600", "115200", "128000", "256000" }));
		comboBox.setSelectedIndex(6);
		panel_1.add(comboBox);

		JButton btnNewButton_1 = new JButton(

				LanguageProvider.getLocalString("btn_huitu"));
		btnNewButton_1.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				card.next(getContentPane());
			}
		});
		panel_1.add(btnNewButton_1);

		buttonPane.add(button_1, "East");

		panel = new JPanel();
		getContentPane().add(panel, "name_39409381508270");
		panel.setLayout(new BorderLayout(0, 0));
		panel.add(panel_huatu, "Center");

		panel_1 = new JPanel();
		panel.add(panel_1, "North");
		panel_1.setLayout(new BorderLayout(0, 0));

		JPanel panel_2 = new JPanel();
		panel_1.add(panel_2);

		JLabel lblNewLabel_1 = new JLabel(LanguageProvider.getLocalString("txt_max"));
		panel_2.add(lblNewLabel_1);

		textField_1 = new JTextField();
		textField_1.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent arg0) {
			}

			public void insertUpdate(DocumentEvent arg0) {
				setMaxAndMin();
			}

			public void removeUpdate(DocumentEvent arg0) {
			}
		});
		textField_1.setText("1024");
		panel_2.add(this.textField_1);
		textField_1.setColumns(10);

		JLabel lblNewLabel_2 = new JLabel(LanguageProvider.getLocalString("txt_min"));
		panel_2.add(lblNewLabel_2);

		textField_2 = new JTextField();
		textField_2.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent arg0) {
			}

			public void insertUpdate(DocumentEvent arg0) {
				setMaxAndMin();
			}

			public void removeUpdate(DocumentEvent arg0) {
			}
		});
		textField_2.setText("0");
		panel_2.add(textField_2);
		textField_2.setColumns(10);

		JButton btnNewButton = new JButton(LanguageProvider.getLocalString("btn_txt_mode"));
		btnNewButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				card.previous(getContentPane());
			}
		});
		panel_1.add(btnNewButton, "East");
	}

	protected void setMaxAndMin() {
		if ((this.textField_1 != null) && (this.textField_2 != null)) {
			String max = this.textField_1.getText();
			String min = this.textField_2.getText();
			try {
				int ma = Integer.parseInt(max);
				int mi = Integer.parseInt(min);
				panel_huatu.setMaxAndMin(ma, mi);
			} catch (Exception localException) {
			}
		}
	}

	public static void resetSerial() {
		String name = com;
		try {
			serialPortCom = new SerialPort(name);
			if (!serialPortCom.isOpened()) {
				serialPortCom.openPort();
			}
			resetSerialBaudRate();
			int mask = 25;

			serialPortCom.setEventsMask(mask);
			serialPortCom.addEventListener(new SerialPortReader());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void resetSerialBaudRate() {
		try {
			int baudRate = Integer.parseInt(comboBox.getSelectedItem().toString());

			serialPortCom.setParams(baudRate, 8, 1, 0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	static class SerialPortReader implements SerialPortEventListener {
		public void serialEvent(SerialPortEvent event) {
			if ((event.isRXCHAR()) && (event.getEventValue() > 0)) {
				try {
					String msg = SerialPortDialog.serialPortCom.readString();
					SerialPortDialog.updateTextArea(msg);
					SerialPortDialog.transferToGraph(msg);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	private static void serialWriteData(String data) {
		if (serialPortCom.isOpened()) {
			try {
				serialPortCom.writeString(data);
			} catch (SerialPortException e) {
				e.printStackTrace();
			}
		}
	}

	private static void transferToGraph(String msg) {
		try {
			strBuffer.append(msg);
			String str = strBuffer.toString();
			int lastIndex = str.lastIndexOf("\r\n");
			if (str.contains("\r\n")) {
				String[] s = str.split("(\r\n)+");
				int new_length = str.endsWith("\r\n") ? s.length : s.length - 1;
				for (int i = 0; i < new_length; i++) {
					String tmp = s[i].trim();

					if (!tmp.startsWith("mixly")) {
						ArrayList<Float> value = new ArrayList();
						String[] valueStr = tmp.split("[,ï¼Œ;ï¼›]+");
						int num = valueStr.length;
						for (int k = 0; k < num; k++) {
							float f = 0.0F;
							f = Float.parseFloat(valueStr[k].trim());
							value.add(Float.valueOf(f));
						}

						if (!isSetNum) {
							panel_huatu.setNum(num);
							isSetNum = true;
						}
						panel_huatu.addValue(value);
					}
				}

				strBuffer = new StringBuffer(strBuffer.substring(lastIndex + 2));
			}
		} catch (Exception e) {
			strBuffer = new StringBuffer("mixly");
		}
	}

	private static void updateTextArea(String msg) {
		textArea.append(msg);
		if (chckbxNewCheckBox.isSelected()) {
			int length = textArea.getText().length();
			textArea.setCaretPosition(length);
		}
	}
}