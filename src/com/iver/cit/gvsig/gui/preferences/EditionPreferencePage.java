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
package com.iver.cit.gvsig.gui.preferences;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BoxLayout;

public class EditionPreferencePage extends AbstractPreferencePage {
	private JLabel jLabel = null;
	private JTextField jTxtTolerance = null;
	private JLabel jLabel1 = null;
	private JSeparator jSeparator = null;
	private JScrollPane jScrollPane = null;
	private JTable jTableSnapping = null;
	private JLabel jLabelCache = null;
	private JPanel jPanelNord = null;
	private JPanel jPanelCache = null;
	private FLayers layers;
	private class MyRecord
	{
		public Boolean bSelec = new Boolean(false);
		public String layerName;
		public Integer maxFeat = new Integer(1000);
	}
	
	private class MyTableModel extends AbstractTableModel
	{
		private ArrayList records = new ArrayList();
		
		public MyTableModel(FLayers layers)
		{
			addLayer(layers);
		}

		private void addLayer(FLayer lyr)
		{
			if (lyr instanceof FLayers)
			{
				FLayers lyrGroup = (FLayers) lyr;
				for (int i=0; i < lyrGroup.getLayersCount(); i++)
				{
					FLayer lyr2 = lyrGroup.getLayer(i); 
					addLayer(lyr2);
				}
			}
			else
			{
				if (lyr instanceof FLyrVect)
				{
					MyRecord rec = new MyRecord();
					rec.layerName = lyr.getName();
					records.add(rec);	
				}
			}
				
			

		}
		public int getColumnCount() {
			return 3;
		}

		public int getRowCount() {
			return records.size();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			MyRecord rec = (MyRecord) records.get(rowIndex);
			if (columnIndex == 0)
				return rec.bSelec;
			if (columnIndex == 1)
				return rec.layerName;
			if (columnIndex == 2)
				return rec.maxFeat;
			return null;
			
		}

		public Class getColumnClass(int c) {
			if (c == 0)
				return Boolean.class;
			if (c == 2)
				return Integer.class;
			return String.class; 
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			MyRecord rec = (MyRecord) records.get(rowIndex);
			if (columnIndex == 0)
				rec.bSelec = (Boolean) aValue;
			if (columnIndex == 2)
			{
				if (aValue != null)
					rec.maxFeat = (Integer) aValue;
				else
					rec.maxFeat = new Integer(0);
			}
			
			super.setValueAt(aValue, rowIndex, columnIndex);
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if (columnIndex == 0)
				return true;
			if (columnIndex == 2)
				return true;

			return false;
		}

		public String getColumnName(int column) {
			if (column == 0)
				return PluginServices.getText(this, "Selected");
			if (column == 1)
				return PluginServices.getText(this, "LayerName");
			if (column == 2)
				return PluginServices.getText(this, "MaxFeaturesEditionCache");
			return "You shouldn't reach this point";

		}
		
	}

	/**
	 * This method initializes 
	 * 
	 */
	public EditionPreferencePage() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		BorderLayout layout = new BorderLayout();
		layout.setHgap(20);
		
        this.setLayout(layout);
        
        jLabelCache = new JLabel();
        jLabelCache.setText(PluginServices.getText(this,"capas_edition_cache"));
        jLabelCache.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabelCache.setPreferredSize(new java.awt.Dimension(303,15));
        jLabelCache.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1 = new JLabel();
        jLabel1.setText("pixels");
        jLabel1.setBounds(new java.awt.Rectangle(195,8,207,15));
        jLabel1.setPreferredSize(new java.awt.Dimension(28,20));
        jLabel1.setName("jLabel1");
        jLabel = new JLabel();
        jLabel.setText("Snap Tolerance:");
        jLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel.setName("jLabel");
        jLabel.setBounds(new java.awt.Rectangle(15,8,122,15));
        jLabel.setPreferredSize(new java.awt.Dimension(28,20));
        jLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        
        this.setSize(new java.awt.Dimension(426,239));
        this.setPreferredSize(this.getSize());
        this.add(getJPanelNord(), BorderLayout.NORTH);
        
        this.add(getJSeparator(), BorderLayout.CENTER);
        

        this.add(getJPanelCache(), BorderLayout.CENTER);
			
	}

	public String getID() {
		return this.getClass().getName();
	}

	public String getTitle() {
		return PluginServices.getText(this,"Edition");
	}

	public JPanel getPanel() {
		return this;
	}

	public String getParentID() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.gui.preferences.IPreference#initializeValues()
	 */
	public void initializeValues() {
		/* Vamos a usar esto por ahora así:
		 * Al abrir el dialogo, miramos las capas que hay
		 * en edición y las capas activas.
		 * Las capas en edición nos las guardamos para
		 * fijarles las propiedades, y las que están activas
		 * las metemos en la tabla de configuración de
		 * snapping.
		 */
		TableModel tm = getJTableSnapping().getModel();
		for (int i=0; i < tm.getRowCount(); i++)
		{
			String layerName = (String) tm.getValueAt(i, 1);
			FLyrVect lyr = (FLyrVect) layers.getLayer(layerName);
			Boolean bUseCache = (Boolean) tm.getValueAt(i,0);
			Integer maxFeat = (Integer) tm.getValueAt(i,2);
			lyr.setSpatialCacheEnabled(bUseCache.booleanValue());
			lyr.setMaxFeaturesInEditionCache(maxFeat.intValue());
		}

		
	}

	public boolean storeValues() {
		TableModel tm = getJTableSnapping().getModel();
		for (int i=0; i < tm.getRowCount(); i++)
		{
			String layerName = (String) tm.getValueAt(i, 1);
			FLyrVect lyr = (FLyrVect) layers.getLayer(layerName);
			Boolean bUseCache = (Boolean) tm.getValueAt(i,0);
			Integer maxFeat = (Integer) tm.getValueAt(i,2);
			lyr.setSpatialCacheEnabled(bUseCache.booleanValue());
			lyr.setMaxFeaturesInEditionCache(maxFeat.intValue());
		}

		return true;
	}

	public void initializeDefaults() {
		TableModel tm = getJTableSnapping().getModel();
		for (int i=0; i < tm.getRowCount(); i++)
		{
			String layerName = (String) tm.getValueAt(i, 1);
			FLyrVect lyr = (FLyrVect) layers.getLayer(layerName);
			Boolean bUseCache = (Boolean) tm.getValueAt(i,0);
			Integer maxFeat = (Integer) tm.getValueAt(i,2);
			lyr.setSpatialCacheEnabled(bUseCache.booleanValue());
			lyr.setMaxFeaturesInEditionCache(maxFeat.intValue());
		}
		
	}

	public ImageIcon getIcon() {
		return null;
	}
	
	public void setLayers(FLayers layers)
	{
		this.layers = layers;
		MyTableModel tm = new MyTableModel(layers);
		getJTableSnapping().setModel(tm);
	}

	/**
	 * This method initializes jTxtTolerance	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTxtTolerance() {
		if (jTxtTolerance == null) {
			jTxtTolerance = new JTextField();
			jTxtTolerance.setPreferredSize(new java.awt.Dimension(28,20));
			jTxtTolerance.setName("jTxtTolerance");
			jTxtTolerance.setBounds(new java.awt.Rectangle(142,8,39,15));
			jTxtTolerance.setHorizontalAlignment(javax.swing.JTextField.LEFT);
		}
		return jTxtTolerance;
	}

	/**
	 * This method initializes jSeparator	
	 * 	
	 * @return javax.swing.JSeparator	
	 */
	private JSeparator getJSeparator() {
		if (jSeparator == null) {
			jSeparator = new JSeparator();
		}
		return jSeparator;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJTableSnapping());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jTableSnapping	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getJTableSnapping() {
		if (jTableSnapping == null) {
			jTableSnapping = new JTable();
//			TableColumnModel cm = new DefaultTableColumnModel();
//			TableColumn checkCol = new TableColumn(0, 50);
//			cm.addColumn(checkCol);
//			
//			TableColumn layerCol = new TableColumn(1, 250);
//			cm.addColumn(layerCol);
//			
//			TableColumn maxFeatCol = new TableColumn(2, 50);
//			cm.addColumn(maxFeatCol);
//
//			JTableHeader head = new JTableHeader(cm);
//			head.setVisible(true);
//			
//			
//			TableModel tm = new DefaultTableModel(4,3);
//			jTableSnapping.setModel(tm);
//			jTableSnapping.setTableHeader(head);
		}
		return jTableSnapping;
	}

	/**
	 * This method initializes jPanelNord	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelNord() {
		if (jPanelNord == null) {
			jPanelNord = new JPanel();
			jPanelNord.setLayout(null);
			jPanelNord.setComponentOrientation(java.awt.ComponentOrientation.UNKNOWN);
			jPanelNord.setPreferredSize(new java.awt.Dimension(30,30));
			jPanelNord.add(jLabel, null);
			jPanelNord.add(getJTxtTolerance(), null);
			jPanelNord.add(jLabel1, null);
			        

		}
		return jPanelNord;
	}

	/**
	 * This method initializes jPanelCache	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelCache() {
		if (jPanelCache == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.weighty = 1.0;
			gridBagConstraints1.gridy = 1;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.insets = new java.awt.Insets(5,10,5,10);
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.insets = new java.awt.Insets(5,10,2,75);
			gridBagConstraints.gridy = 0;
			gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
			gridBagConstraints.ipadx = 14;
			gridBagConstraints.gridwidth = 3;
			gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints.gridx = 0;
			jPanelCache = new JPanel();
			jPanelCache.setLayout(new GridBagLayout());
			jPanelCache.add(jLabelCache, gridBagConstraints);
			jPanelCache.add(getJScrollPane(), gridBagConstraints1);
		}
		return jPanelCache;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"


