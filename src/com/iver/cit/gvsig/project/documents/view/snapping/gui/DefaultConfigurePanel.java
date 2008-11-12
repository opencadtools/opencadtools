package com.iver.cit.gvsig.project.documents.view.snapping.gui;

import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.gvsig.gui.beans.AcceptCancelPanel;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.project.documents.view.snapping.ISnapper;

public class DefaultConfigurePanel extends JPanel implements IWindow {
	private ISnapper snapper;
	private JLabel lblColor = null;
	private JButton bColor = null;
	private WindowInfo wi=null;
	private AcceptCancelPanel acceptCancelPanel;
	/**
	 * This is the default constructor
	 */
	public DefaultConfigurePanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {
		lblColor = new JLabel();
		lblColor.setText("color");
		this.setSize(269, 91);
		this.add(lblColor, null);
		this.add(getBColor(), null);
		this.add(getPAcceptCancel(),null);
	}
	public void setSnapper(ISnapper snapper) {
		this.snapper=snapper;
		//((AbstractSnapper)this.snapper).setColor(Color.blue);
	}

	/**
	 * This method initializes bColor
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getBColor() {
		if (bColor == null) {
			bColor = new JButton();
			bColor.setText("...");
		}
		return bColor;
	}

	protected AcceptCancelPanel getPAcceptCancel() {
		if (acceptCancelPanel==null) {
			ActionListener okAction = new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {

					if (PluginServices.getMainFrame() == null) {
                        ((JDialog) (getParent().getParent().getParent()
                                .getParent())).dispose();
                    } else {
                        PluginServices.getMDIManager().closeWindow(DefaultConfigurePanel.this);
                    }
				}
			};
			ActionListener cancelAction = new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (PluginServices.getMainFrame() != null) {
                        PluginServices.getMDIManager().closeWindow(DefaultConfigurePanel.this);
                    } else {
                        ((JDialog) (getParent().getParent().getParent()
                                        .getParent())).dispose();
                    }
				}
			};
			acceptCancelPanel=new AcceptCancelPanel(okAction,cancelAction);

		}
		return acceptCancelPanel;
	}

	public WindowInfo getWindowInfo() {
		if (wi==null) {
			wi=new WindowInfo(WindowInfo.MODALDIALOG|WindowInfo.RESIZABLE);
			wi.setWidth(this.getWidth());
			wi.setHeight(this.getHeight());
			wi.setTitle(PluginServices.getText(this,"propiedades"));
		}
		return wi;
	}

	@Override
	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
