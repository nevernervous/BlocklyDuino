package org.blockly;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import org.blockly.util.LanguageProvider;

public class UnSaveWarningDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	private boolean yesOrNo;

	public static void main(String[] args) {
		try {
			UnSaveWarningDialog dialog = new UnSaveWarningDialog();
			dialog.setDefaultCloseOperation(2);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public UnSaveWarningDialog() {
		setTitle(LanguageProvider.getLocalString("warning"));
		setAlwaysOnTop(true);
		setBounds(100, 100, 486, 129);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, "Center");
		contentPanel.setLayout(new BorderLayout(0, 0));

		JTextArea textArea = new JTextArea(LanguageProvider.getLocalString("warning_text"));
		textArea.setBackground(new Color(240, 240, 240));
		textArea.setFont(new Font("Monospaced", 0, 13));
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		contentPanel.add(textArea, "Center");

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(2));
		getContentPane().add(buttonPane, "South");

		JButton okButton = new JButton(LanguageProvider.getLocalString("btn_ok"));
		okButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				setYesOrNo(true);
				setVisible(false);
			}
		});
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);

		JButton cancelButton = new JButton(LanguageProvider.getLocalString("btn_cancel"));
		cancelButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				setYesOrNo(false);
				setVisible(false);
			}
		});
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);
	}

	public boolean isYesOrNo() {
		return this.yesOrNo;
	}

	public void setYesOrNo(boolean yesOrNo) {
		this.yesOrNo = yesOrNo;
	}
}