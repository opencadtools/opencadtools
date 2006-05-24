package com.iver.cit.gvsig.gui.cad.panels.options;

import javax.swing.JPanel;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.View;
import com.iver.andami.ui.mdiManager.ViewInfo;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;

import javax.swing.JTextField;
import javax.swing.JButton;

public class OptionsPanel extends JPanel implements View{

	private JPanel jPanel = null;
	private JTextField jTextField = null;
	private JButton bOk = null;
	private JButton bCancel = null;
	private VectorialEditableAdapter vea;
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
		this.setSize(330, 112);
		this.add(getJPanel(), null);
		this.add(getBOk(), null);
		this.add(getBCancel(), null);
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
			jPanel = new JPanel();
			jPanel.setLayout(null);
			jPanel.setBounds(new java.awt.Rectangle(15,22,145,67));
			jPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null,PluginServices.getText(this, "densityfication"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
			jPanel.add(getJTextField(), null);
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
			jTextField.setBounds(new java.awt.Rectangle(7,20,126,20));
			jTextField.setText(String.valueOf(vea.getFlatness()));
		}
		return jTextField;
	}

	/**
	 * This method initializes bOk
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getBOk() {
		if (bOk == null) {
			bOk = new JButton();
			bOk.setBounds(new java.awt.Rectangle(178,22,116,29));
			bOk.setText(PluginServices.getText(this,"ok"));
			bOk.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					vea.setFlatness(Double.parseDouble(getJTextField().getText()));
					PluginServices.getMDIManager().closeView(OptionsPanel.this);
				}
			});
		}
		return bOk;
	}

	/**
	 * This method initializes bCancel
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getBCancel() {
		if (bCancel == null) {
			bCancel = new JButton();
			bCancel.setBounds(new java.awt.Rectangle(178,59,116,29));
			bCancel.setText(PluginServices.getText(this,"cancel"));
			bCancel.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					PluginServices.getMDIManager().closeView(OptionsPanel.this);
				}
			});
		}
		return bCancel;
	}

}  //  @jve:decl-index=0:visual-constraint="10,12"
