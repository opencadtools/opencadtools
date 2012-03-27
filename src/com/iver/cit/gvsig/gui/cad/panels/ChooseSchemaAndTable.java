package com.iver.cit.gvsig.gui.cad.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.gvsig.gui.beans.swing.JButton;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiFrame.MDIFrame;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.drivers.ConnectionJDBC;
import com.iver.cit.gvsig.fmap.drivers.db.utils.ConnectionWithParams;

public class ChooseSchemaAndTable extends JPanel implements IWindow,
	ActionListener {


    private JComboBox schemaCB;
    private String schema;
    private JTextField tableTF;
    private String table;

    private ConnectionWithParams cwp;
    private WindowInfo windowInfo;
    private JButton okBT;
    private JButton cancelBT;
    private boolean okPressed = false;


    private final CharSequence charSequence = "\\/=.:,;¿?*{}´$%&()@#|!¬";

    public ChooseSchemaAndTable(ConnectionWithParams cwp) {
	this.cwp = cwp;

	Vector<String> avaliableSchemas = new Vector<String>();
	Vector<String> avaliableS = new Vector<String>();
	try {
	    ResultSet rs = ((ConnectionJDBC) cwp.getConnection())
		    .getConnection().getMetaData().getSchemas();
	    while (rs.next()) {
		avaliableSchemas.add(rs.getString("TABLE_SCHEM"));
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	}


	GridBagLayout gridBagLayout = new GridBagLayout();
	setLayout(gridBagLayout);

	JLabel schemaLB = new JLabel(PluginServices.getText(this, "schema")
		+ ":");
	GridBagConstraints gbc_schemaLB = new GridBagConstraints();
	gbc_schemaLB.insets = new Insets(5, 5, 5, 5);
	gbc_schemaLB.gridx = 0;
	gbc_schemaLB.gridy = 0;
	add(schemaLB, gbc_schemaLB);

	schemaCB = new JComboBox(avaliableSchemas);
	GridBagConstraints gbc_schemaCB = new GridBagConstraints();
	gbc_schemaCB.fill = GridBagConstraints.HORIZONTAL;
	gbc_schemaCB.insets = new Insets(5, 5, 5, 0);
	gbc_schemaCB.gridx = 1;
	gbc_schemaCB.gridy = 0;
	add(schemaCB, gbc_schemaCB);

	JLabel tableLB = new JLabel(PluginServices.getText(this,
		"intro_tablename"));
	GridBagConstraints gbc_tableLB = new GridBagConstraints();
	gbc_tableLB.insets = new Insets(5, 5, 5, 5);
	gbc_tableLB.gridx = 0;
	gbc_tableLB.gridy = 1;
	add(tableLB, gbc_tableLB);

	tableTF = new JTextField();
	GridBagConstraints gbc_tableTF = new GridBagConstraints();
	gbc_tableTF.insets = new Insets(5, 5, 5, 0);
	gbc_tableTF.fill = GridBagConstraints.HORIZONTAL;
	gbc_tableTF.gridx = 1;
	gbc_tableTF.gridy = 1;
	add(tableTF, gbc_tableTF);

	okBT = new JButton(PluginServices.getText(this, "Accept"));
	okBT.addActionListener(this);
	GridBagConstraints gbc_okBT = new GridBagConstraints();
	gbc_okBT.insets = new Insets(5, 5, 5, 0);
	gbc_okBT.fill = GridBagConstraints.NONE;
	;

	gbc_okBT.gridx = 0;
	gbc_okBT.gridy = 2;
	add(okBT, gbc_okBT);

	cancelBT = new JButton(PluginServices.getText(this, "Cancel"));
	cancelBT.addActionListener(this);
	GridBagConstraints gbc_cancelBT = new GridBagConstraints();
	gbc_cancelBT.insets = new Insets(5, 5, 5, 0);
	gbc_cancelBT.fill = GridBagConstraints.NONE;
	;
	gbc_cancelBT.gridx = 1;
	gbc_cancelBT.gridy = 2;
	add(cancelBT, gbc_cancelBT);

    }

    public WindowInfo getWindowInfo() {

	if (windowInfo == null) {
	    windowInfo = new WindowInfo(WindowInfo.MODALDIALOG
		    | WindowInfo.RESIZABLE);
	    // windowInfo
	    // .setTitle(PluginServices.getText(this, "intro_tablename"));
	    Dimension dim = getPreferredSize();
	    MDIFrame a = (MDIFrame) PluginServices.getMainFrame();
	    int maxHeight = a.getHeight() - 175;
	    int maxWidth = a.getWidth() - 15;

	    int width, heigth = 0;
	    if (dim.getHeight() > maxHeight) {
		heigth = maxHeight;
	    } else {
		heigth = new Double(dim.getHeight()).intValue();
	    }
	    if (dim.getWidth() > maxWidth) {
		width = maxWidth;
	    } else {
		width = new Double(dim.getWidth()).intValue();
	    }
	    windowInfo.setWidth(width + 20);
	    windowInfo.setHeight(heigth + 15);

	}
	return windowInfo;
    }

    public Object getWindowProfile() {
	return WindowInfo.DIALOG_PROFILE;
    }


    public String getTable() {
	return table;
    }

    public String getSchema() {
	return schema;
    }

    public boolean isOKPressed() {
	return okPressed;
    }

    public void actionPerformed(ActionEvent e) {
	if (e.getSource() == okBT) {
	    String tableName = tableTF.getText().trim();

	    if (tableName.length() == 0) {
		JOptionPane.showMessageDialog(null, PluginServices.getText(this,"intro_tablename_blank"), PluginServices.getText(this,"warning"), JOptionPane.WARNING_MESSAGE);
		return;
	    }

	    for (int i = 0; i < charSequence.length(); i++) {
		char c = charSequence.charAt(i);
		if (tableName != null && tableName.indexOf(c) != -1) {
		    JOptionPane.showMessageDialog(null,
			    PluginServices.getText(this, "wrong_characters"),
			    PluginServices.getText(this, "warning"),
			    JOptionPane.WARNING_MESSAGE);
		    return;
		}
		}

	    table = tableName;
	    schema = schemaCB.getSelectedItem().toString();
	    okPressed = true;
	    PluginServices.getMDIManager().closeWindow(this);

	} else if (e.getSource() == cancelBT) {
	    okPressed = false;
	    table = null;
	    schema = null;
	    PluginServices.getMDIManager().closeWindow(this);
	}

    }

}
