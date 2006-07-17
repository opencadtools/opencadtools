package com.iver.cit.gvsig.gui.cad.panels.options;

import java.awt.event.ActionListener;

import javax.swing.JPanel;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.View;
import com.iver.andami.ui.mdiManager.ViewInfo;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;

import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;

import org.gvsig.gui.beans.AcceptCancelPanel;
import javax.swing.JTextArea;

public class OptionsPanel extends JPanel implements View{

	private JPanel jPanel = null;
	private JTextField jTextField = null;
	private VectorialEditableAdapter vea;
	private JLabel jLabel = null;
	private AcceptCancelPanel jPanel1;
	private JTextArea jTextArea = null;
	/**
	 * This is the default constructor
	 */
	public OptionsPanel(VectorialEditableAdapter vea) {
		super();
		this.vea=vea;
		initialize();
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {
		this.setLayout(null);
		this.setSize(389, 164);
		this.add(getJPanel(), null);
		this.add(getJPanel1(), null);
		this.add(getJTextArea(), null);
	}

	public ViewInfo getViewInfo() {
		ViewInfo viewInfo=new ViewInfo(ViewInfo.MODALDIALOG);
		viewInfo.setWidth(getWidth());
		viewInfo.setHeight(getHeight());
		viewInfo.setTitle(PluginServices.getText(this,"options"));
		return viewInfo;
	}

	/**
	 * This method initializes jPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jLabel = new JLabel();
			jLabel.setBounds(new java.awt.Rectangle(12,11,119,22));
			jLabel.setText(PluginServices.getText(this,"densityfication"));
			jPanel = new JPanel();
			jPanel.setLayout(null);
			jPanel.setBounds(new java.awt.Rectangle(15,73,284,39));
			jPanel.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.gray,1));
			jPanel.add(getJTextField(), null);
			jPanel.add(jLabel, null);
		}
		return jPanel;
	}

	/**
	 * This method initializes jTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setBounds(new java.awt.Rectangle(147,12,126,20));
			jTextField.setText(String.valueOf(vea.getFlatness()));
		}
		return jTextField;
	}

	/**
	 * This method initializes jPanel1
	 *
	 * @return javax.swing.JPanel
	 */
	private AcceptCancelPanel getJPanel1() {
		if (jPanel1 == null) {
			ActionListener okAction, cancelAction;
			okAction = new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					vea.setFlatness(Double.parseDouble(getJTextField().getText()));
					PluginServices.getMDIManager().closeView(OptionsPanel.this);
				}
			};
			cancelAction = new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					PluginServices.getMDIManager().closeView(OptionsPanel.this);
				}
			};
			jPanel1 = new AcceptCancelPanel(okAction, cancelAction);
			jPanel1.setBounds(new java.awt.Rectangle(15,120,284,39));
		}
		return jPanel1;
	}

	/**
	 * This method initializes jTextArea
	 *
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getJTextArea() {
		if (jTextArea == null) {
			jTextArea = new JTextArea();
			jTextArea.setBounds(new java.awt.Rectangle(13,7,285,57));
			jTextArea.setForeground(java.awt.Color.black);
			jTextArea.setBackground(java.awt.SystemColor.control);
			jTextArea.setRows(3);
			jTextArea.setWrapStyleWord(true);
			jTextArea.setLineWrap(true);
			jTextArea.setEditable(false);
			jTextArea.setText(PluginServices.getText(this,"specifies_the_minimum_size_of_the_lines_that_will_form_the_curves"));
		}
		return jTextArea;
	}

}  //  @jve:decl-index=0:visual-constraint="10,12"
