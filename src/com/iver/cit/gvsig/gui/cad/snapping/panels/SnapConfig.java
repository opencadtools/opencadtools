/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2004 IVER T.I. and Generalitat Valenciana.
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
package com.iver.cit.gvsig.gui.cad.snapping.panels;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;

import com.iver.utiles.DefaultListModel;
;

public class SnapConfig extends JPanel {

	private JCheckBox jChkBoxRefentActive = null;
	private JList jListSnappers = null;
	private JPanel jPanel = null;
	private JScrollPane jScrollPane = null;
	
	private ArrayList snappers;
	
	 class MyCellRenderer extends JCheckBox implements ListCellRenderer {

	     // This is the only method defined by ListCellRenderer.
	     // We just reconfigure the JLabel each time we're called.

	     public Component getListCellRendererComponent(
	       JList list,
	       Object value,            // value to display
	       int index,               // cell index
	       boolean isSelected,      // is the cell selected
	       boolean cellHasFocus)    // the list and the cell have the focus
	     {
	         String s = value.toString();
	         setText(s);
	         
	   	   if (isSelected) {
	             setBackground(list.getSelectionBackground());
		       setForeground(list.getSelectionForeground());
		   }
	         else {
		       setBackground(list.getBackground());
		       setForeground(list.getForeground());
		   }
		   setEnabled(list.isEnabled());
		   setFont(list.getFont());
	         setOpaque(true);
	         return this;
	     }
	 }

	
	/**
	 * This method initializes 
	 * 
	 */
	public SnapConfig() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        this.setLayout(null);
        this.setSize(new java.awt.Dimension(463,239));
        this.setPreferredSize(new java.awt.Dimension(463,239));
        this.add(getJChkBoxRefentActive(), null);
        this.add(getJPanel(), null);
			
	}

	/**
	 * This method initializes jChkBoxRefentActive	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getJChkBoxRefentActive() {
		if (jChkBoxRefentActive == null) {
			jChkBoxRefentActive = new JCheckBox();
			jChkBoxRefentActive.setText("Referencia a Objetos Activada:");
			jChkBoxRefentActive.setBounds(new java.awt.Rectangle(26,10,418,23));
		}
		return jChkBoxRefentActive;
	}

	/**
	 * This method initializes jListSnappers	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getJListSnappers() {
		if (jListSnappers == null) {
			jListSnappers = new JList();
			jListSnappers.setCellRenderer(new MyCellRenderer());
		}
		return jListSnappers;
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
			jPanel.setBounds(new java.awt.Rectangle(19,40,423,181));
			jPanel.add(getJScrollPane(), null);
		}
		return jPanel;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setBounds(new java.awt.Rectangle(9,9,402,163));
			jScrollPane.setViewportView(getJListSnappers());
		}
		return jScrollPane;
	}

	public ArrayList getSnappers() {
		return snappers;
	}

	public void setSnappers(ArrayList snappers) {
		this.snappers = snappers;
		DefaultListModel listModel = new DefaultListModel(snappers);
		getJListSnappers().setModel(listModel);
	}
	
	
}  //  @jve:decl-index=0:visual-constraint="10,10"


