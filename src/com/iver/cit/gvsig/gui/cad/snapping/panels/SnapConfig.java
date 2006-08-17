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
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

import com.iver.cit.gvsig.gui.cad.snapping.ISnapper;
;

/**
 * @author fjp
 *
 * Necesitamos un sitio donde estén registrados todos los snappers que 
 * se pueden usar. ExtensionPoints es el sitio adecuado.
 * Este diálogo recuperará esa lista para que el usuario marque los
 * snappers con los que desea trabajar.
 */
public class SnapConfig extends JPanel {

	private JCheckBox jChkBoxRefentActive = null;
	private JTable jListSnappers = null;
	private JPanel jPanel = null;
	private JScrollPane jScrollPane = null;
	
	private ArrayList snappers;
	
	/**
	 * @author fjp
	 * primera columna editable con un check box para habilitar/deshabilitar el snapper
	 * segunda columna con el símbolo del snapper
	 * tercera con el tooltip
	 * cuarta con un botón para configurar el snapper si es necesario.
	 */
	class MyTableModel extends AbstractTableModel {
		
		public ArrayList mySnappers;
		
		public MyTableModel(ArrayList snappers)
		{
			this.mySnappers = snappers;
		}

		public int getColumnCount() {
			return 4;
		}

		public int getRowCount() {
			return mySnappers.size();
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if (columnIndex == 0)
				return true;
			else
				return false;
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			ISnapper snap = (ISnapper) mySnappers.get(rowIndex);
			switch (columnIndex)
			{
			case 0:
				snap.setEnabled(((Boolean)aValue).booleanValue());
			}

		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			ISnapper snap = (ISnapper) mySnappers.get(rowIndex);
			switch (columnIndex)
			{
			case 0:
				return new Boolean(snap.isEnabled());
			case 1:
				return snap;				
			case 2:
				return snap.getToolTipText();
			}
			return null;
		}

		public Class getColumnClass(int columnIndex) {
			switch (columnIndex)
			{
			case 0:
				return Boolean.class;
			case 1:
				return String.class;
			case 2:
				return String.class;
			case 3:
				return String.class;
			}
			return null;
		}

		public String getColumnName(int column) {
			// TODO Auto-generated method stub
			return super.getColumnName(column);
		}
		
	}
	
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
	    	 ISnapper snapper = (ISnapper) value;
	         String s = snapper.getToolTipText();
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

		public void doClick() {
			super.doClick();
			System.out.println("Click");
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
	private JTable getJListSnappers() {
		if (jListSnappers == null) {
			jListSnappers = new JTable();
			// jListSnappers.setCellRenderer(new MyCellRenderer());
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
		MyTableModel listModel = new MyTableModel(snappers);
		getJListSnappers().setModel(listModel);
	}
	
	
}  //  @jve:decl-index=0:visual-constraint="10,10"


