/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2005 IVER T.I. and Generalitat Valenciana.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *  Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibáñez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *      +34 963862235
 *   gvsig@gva.es
 *      www.gvsig.gva.es
 *
 *    or
 *
 *   IVER T.I. S.A
 *   Salamanca 50
 *   46005 Valencia
 *   Spain
 *
 *   +34 963163400
 *   dac@iver.es
 */

package com.iver.cit.gvsig.gui.preferences;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.iver.andami.PluginServices;
import com.iver.andami.preferences.AbstractPreferencePage;
import com.iver.andami.preferences.StoreException;

/**
 * @author Vicente Caballero Navarro
 **/
public class FieldExpresionPage extends AbstractPreferencePage {
    private static Preferences prefs = Preferences.userRoot().node(
	    "fieldExpresionOptions");
    private JTextArea jTextArea = null;
    private JTextField txtLimit;
    private JCheckBox ckLimit = null;
    private ImageIcon icon;

    public FieldExpresionPage() {
	super();
	icon = PluginServices.getIconTheme().get("field-expresion");
	addComponent(getJTextArea());

	addComponent(
		PluginServices.getText(this, "limit_rows_in_memory") + ":",
		txtLimit = new JTextField("", 15));
	addComponent(ckLimit = new JCheckBox(PluginServices.getText(this,
		"without_limit")));
	ckLimit.addMouseListener(new MouseListener() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
		if (ckLimit.isSelected()) {
		    txtLimit.setText(PluginServices.getText(this,
			    "without_limit"));
		} else {
		    if (txtLimit.getText().equals(
			    PluginServices.getText(this, "without_limit"))) {
			txtLimit.setText("500000");
		    }
		}

	    }

	    @Override
	    public void mouseEntered(MouseEvent e) {
	    }

	    @Override
	    public void mouseExited(MouseEvent e) {
	    }

	    @Override
	    public void mousePressed(MouseEvent e) {
	    }

	    @Override
	    public void mouseReleased(MouseEvent e) {
	    }

	});
    }

    @Override
    public void initializeValues() {
	int limit = prefs.getInt("limit_rows_in_memory", -1);
	if (limit == -1) {
	    ckLimit.setSelected(true);
	    txtLimit.setText(PluginServices.getText(this, "without_limit"));
	} else {
	    ckLimit.setSelected(false);
	    txtLimit.setText(String.valueOf(limit));
	}
    }

    @Override
    public String getID() {
	return this.getClass().getName();
    }

    @Override
    public String getTitle() {
	return PluginServices.getText(this, "limit_rows_in_memory");
    }

    @Override
    public JPanel getPanel() {
	return this;
    }

    @Override
    public void storeValues() throws StoreException {
	int limit;
	try {
	    if (ckLimit.isSelected()) {
		limit = -1;
	    } else {
		// if
		// (txtLimit.getText().equals(PluginServices.getText(this,"without_limit")))
		// {
		// limit=-1;
		// }else {
		limit = Integer.parseInt(txtLimit.getText());
	    }
	} catch (Exception e) {
	    throw new StoreException(PluginServices.getText(this,
		    "limit_rows_in_memory")
		    + PluginServices.getText(this, "error"));
	}
	prefs.putInt("limit_rows_in_memory", limit);
    }

    @Override
    public void initializeDefaults() {
	int limit = prefs.getInt("limit_rows_in_memory", -1);
	if (limit == -1) {
	    ckLimit.setSelected(true);
	    txtLimit.setText(PluginServices.getText(this, "without_limit"));
	} else {
	    ckLimit.setSelected(false);
	    txtLimit.setText(String.valueOf(limit));
	}
    }

    @Override
    public ImageIcon getIcon() {
	return icon;
    }

    /**
     * This method initializes jTextArea
     * 
     * @return javax.swing.JTextArea
     */
    private JTextArea getJTextArea() {
	if (jTextArea == null) {
	    jTextArea = new JTextArea();
	    jTextArea.setBounds(new java.awt.Rectangle(13, 7, 285, 57));
	    jTextArea.setForeground(java.awt.Color.black);
	    jTextArea.setBackground(java.awt.SystemColor.control);
	    jTextArea.setRows(3);
	    jTextArea.setWrapStyleWord(true);
	    jTextArea.setLineWrap(true);
	    jTextArea.setEditable(false);
	    jTextArea
		    .setText(PluginServices
			    .getText(this,
				    "specifies_the_limit_rows_in_memory_when_the_program_eval_the_expresion"));
	}
	return jTextArea;
    }

    @Override
    public boolean isValueChanged() {
	return super.hasChanged();
    }

    @Override
    public void setChangesApplied() {
	setChanged(false);
    }
}
