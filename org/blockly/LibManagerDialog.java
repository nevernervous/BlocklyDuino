package org.blockly;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.blockly.util.LanguageProvider;
import org.blockly.util.MyMethod;

public class LibManagerDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private JTextField textField;
	private JList list;
	private DefaultListModel<String> listModel = new DefaultListModel();
	private List<String> listdata;
	String a_new_name = LanguageProvider.getLocalString("input_new_name");

	public static void main(String[] args) {
		try {
			LibManagerDialog dialog = new LibManagerDialog();
			dialog.setDefaultCloseOperation(2);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public LibManagerDialog() {
		setAlwaysOnTop(true);
		addWindowListener(new WindowListener() {
			public void windowOpened(WindowEvent e) {
			}

			public void windowIconified(WindowEvent e) {
			}

			public void windowDeiconified(WindowEvent e) {
			}

			public void windowDeactivated(WindowEvent e) {
			}

			public void windowClosing(WindowEvent e) {
			}

			public void windowClosed(WindowEvent e) {
			}

			public void windowActivated(WindowEvent e) {
				updataDialog();
			}
		});
		setTitle(LanguageProvider.getLocalString("lib_manager"));
		setBounds(100, 100, 500, 400);
		getContentPane().setLayout(new BorderLayout());

		JPanel buttonPane = new JPanel();
		getContentPane().add(buttonPane, "South");

		textField = new JTextField(this.a_new_name);
		textField.setToolTipText(this.a_new_name);
		textField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				if (textField.getText().isEmpty()) {
					textField.setText(a_new_name);
				}
			}

			public void focusGained(FocusEvent e) {
				if (textField.getText().equals(a_new_name)) {
					textField.setText("");
				}
			}
		});
		buttonPane.setLayout(new FlowLayout(1, 5, 5));
		buttonPane.add(this.textField);
		textField.setColumns(15);

		JButton renameButton = new JButton(LanguageProvider.getLocalString("rename"));
		renameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		renameButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if ((list.getSelectedValue() != null)
						&& (!textField.getText().equals(""))
						&& (!textField.getText().equals(a_new_name))) {
					String fullpath = list.getSelectedValue().toString();

					String oldname = fullpath.substring(fullpath.lastIndexOf(System.getProperty("file.separator")) + 1,
							fullpath.length());

					if (fullpath.contains("company" + System.getProperty("file.separator"))) {
						if (MyMethod.renameFile("company", oldname,
								textField.getText() + ".xml") == 2) {
							updatalist();
						}
					} else if (MyMethod.renameFile("mylib", oldname,
							textField.getText() + ".xml") == 2) {
						updatalist();
					}

				}

			}
		});
		renameButton.setActionCommand("OK");
		buttonPane.add(renameButton);
		getRootPane().setDefaultButton(renameButton);

		JButton deleteButton = new JButton(LanguageProvider.getLocalString("delete"));
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		deleteButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				if (list.getSelectedValue() != null) {
					String name = list.getSelectedValue().toString();

					File xmlFile = new File(name);
					final String result = MyMethod.readXml(xmlFile);
					String folder = xmlFile.getParent();
					if (folder.endsWith("company")) {
						new Thread(new Runnable() {
							public void run() {
								try {
									deleteAllLib(result);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}).start();
					}

					MyMethod.deleteFile(name);
					updatalist();
				}
			}

			private void deleteAllLib(String result) {
				Pattern p1 = Pattern.compile("block=\"(.*?)\"");

				Pattern p2 = Pattern.compile("generator=\"(.*?)\"");

				Pattern p3 = Pattern.compile("lib=\"(.*?)\"");

				Pattern p4 = Pattern.compile("hardware=\"(.*?)\"");

				Pattern p5 = Pattern.compile("media=\"(.*?)\"");

				Pattern p6 = Pattern.compile("language=\"(.*?)\"");

				Pattern p7 = Pattern.compile("pin=\"(.*?)\"");

				Matcher m1 = p1.matcher(result);
				Matcher m2 = p2.matcher(result);
				Matcher m3 = p3.matcher(result);
				Matcher m4 = p4.matcher(result);
				Matcher m5 = p5.matcher(result);
				Matcher m6 = p6.matcher(result);
				Matcher m7 = p7.matcher(result);
				String str_block = null;
				String str_generator = null;
				String str_lib = null;
				String str_hardware = null;
				String str_media = null;
				String str_lang = null;
				String str_pin = null;
				if (m1.find()) {
					str_block = m1.group(1);
				}

				if (m2.find()) {
					str_generator = m2.group(1);
				}

				if (m3.find()) {
					str_lib = m3.group(1);
				}

				if (m4.find()) {
					str_hardware = m4.group(1);
				}

				if (m5.find()) {
					str_media = m5.group(1);
				}

				if (m6.find()) {
					str_lang = m6.group(1);
				}

				if (m7.find()) {
					str_pin = m7.group(1);
				}

				if (str_lib != null) {
					str_lib.equals("");
				}

				if ((str_hardware != null) && (!str_hardware.equals("")) && (str_hardware.startsWith("hardware"))) {
					MyMethod.deleteDirectory(Browser.arduinoPath + str_hardware);
				}

				if ((str_media != null) && (!str_media.equals("")) && (str_media.startsWith("media"))) {
					MyMethod.deleteDirectory("blockly/" + str_media);
				}

				if ((str_lang != null) && (!str_lang.equals("")) && (str_lang.startsWith("language"))) {
					MyMethod.deleteDirectory("blockly/msg/js/company/" + str_lang);
					MyMethod.setCompanyLanguage();
				}

				if ((str_block != null) && (!str_block.equals("")) && (str_block.startsWith("block"))) {
					MyMethod.deleteFile("blockly/blocks/company" + str_block.substring(str_block.lastIndexOf("/")));
				}
				if ((str_generator != null) && (!str_generator.equals("")) && (str_generator.startsWith("generator"))) {
					MyMethod.deleteFile("blockly/generators/arduino/company"
							+ str_generator.substring(str_generator.lastIndexOf("/")));
				}

				if ((str_pin != null) && (!str_pin.equals("")) && (str_pin.startsWith("companypin"))) {
					MyMethod.deleteDirectory("blockly/apps/mixly/" + str_pin);
					MyMethod.setCompanyPin();
				}
			}
		});
		deleteButton.setActionCommand("Cancel");
		buttonPane.add(deleteButton);

		JButton openlibButton = new JButton(LanguageProvider.getLocalString("open_directory"));
		openlibButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				try {
					if (list.getSelectedValue() != null) {
						String fullpath = list.getSelectedValue().toString();

						if (fullpath.contains("company" + System.getProperty("file.separator"))) {
							Runtime.getRuntime().exec("explorer company");
						} else {
							Runtime.getRuntime().exec("explorer mylib");
						}
					} else {
						if ((listdata != null) && (listdata.size() > 0)) {

							if (((String) listdata.get(0))
									.contains("company" + System.getProperty("file.separator"))) {
								Runtime.getRuntime().exec("explorer company");
								return;
							}
						}
						Runtime.getRuntime().exec("explorer mylib");
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		buttonPane.add(openlibButton);

		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, "Center");

		listdata = MyMethod.getXmlMilList("mylib");
		listdata.addAll(MyMethod.getXmlMilList("company"));
		if ((listdata != null) && (listdata.size() > 0)) {
			for (int i = 0; i < this.listdata.size(); i++) {
				String fullpath = (String) listdata.get(i);

				this.listModel.addElement(fullpath);
			}
		}
		list = new JList(listModel);
		list.setSelectionMode(0);
		scrollPane.setViewportView(list);
	}

	private void updatalist() {
		updataDialog();

		MyMethod.setCustom();
		Browser.refreshWeb();
	}

	private void updataDialog() {
		listdata = MyMethod.getXmlMilList("mylib");
		listdata.addAll(MyMethod.getXmlMilList("company"));
		listModel.clear();
		if ((listdata != null) && (listdata.size() > 0)) {
			for (int i = 0; i < listdata.size(); i++) {
				String fullpath = (String) listdata.get(i);

				listModel.addElement(fullpath);
			}
		}
		if (list != null) {
			list.updateUI();
		}
	}
}