package com.iver.cit.gvsig.gui.cad.createTable;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.gvsig.gui.beans.AcceptCancelPanel;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;

public class NewTableChoosePanel extends JPanel implements IWindow {
    private WindowInfo info;
    private JList<String> list;
    private String selected;

    public NewTableChoosePanel(String[] types) {

	setLayout(new BorderLayout());

	JLabel label = new JLabel(PluginServices.getText(this, "choose_format")
		+ ":");
	label.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));

	list = new JList<String>(types);
	list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	JScrollPane pane = new JScrollPane(list);
	JPanel center = new JPanel(new BorderLayout());
	center.add(pane, BorderLayout.CENTER);
	center.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

	ActionListener cancelAction = new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		close();
	    }
	};
	ActionListener okAction = new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		close();
		selected = list.getSelectedValue();
	    }
	};

	add(label, BorderLayout.NORTH);
	add(center, BorderLayout.CENTER);
	add(new AcceptCancelPanel(okAction, cancelAction), BorderLayout.SOUTH);
    }

    private void close() {
	PluginServices.getMDIManager().closeWindow(this);

    }

    public String getSelectedFormat() {
	return selected;
    }

    @Override
    public WindowInfo getWindowInfo() {
	if (info == null) {
	    info = new WindowInfo(WindowInfo.MODALDIALOG);
	    info.setWidth(350);
	    info.setHeight(120);
	    info.setTitle(PluginServices.getText(this, "new_table"));
	}
	return info;
    }

    @Override
    public Object getWindowProfile() {
	return WindowInfo.DIALOG_PROFILE;
    }
}
