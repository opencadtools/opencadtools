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

/* CVS MESSAGES:
*
* $Id$
* $Log$
* Revision 1.1  2006-08-10 08:18:35  caballero
* configurar grid
*
* Revision 1.1  2006/08/04 11:41:05  caballero
* poder especificar el zoom a aplicar en las vistas
*
* Revision 1.3  2006/07/31 10:02:31  jaume
* *** empty log message ***
*
* Revision 1.2  2006/06/13 07:43:08  fjp
* Ajustes sobre los cuadros de dialogos de preferencias
*
* Revision 1.1  2006/06/12 16:04:28  caballero
* Preferencias
*
* Revision 1.11  2006/06/06 10:26:31  jaume
* *** empty log message ***
*
* Revision 1.10  2006/06/05 17:07:17  jaume
* *** empty log message ***
*
* Revision 1.9  2006/06/05 17:00:44  jaume
* *** empty log message ***
*
* Revision 1.8  2006/06/05 16:57:59  jaume
* *** empty log message ***
*
* Revision 1.7  2006/06/05 14:45:06  jaume
* *** empty log message ***
*
* Revision 1.6  2006/06/05 11:00:09  jaume
* *** empty log message ***
*
* Revision 1.5  2006/06/05 10:39:02  jaume
* *** empty log message ***
*
* Revision 1.4  2006/06/05 10:13:40  jaume
* *** empty log message ***
*
* Revision 1.3  2006/06/05 10:06:08  jaume
* *** empty log message ***
*
* Revision 1.2  2006/06/05 09:51:56  jaume
* *** empty log message ***
*
* Revision 1.1  2006/06/02 10:50:18  jaume
* *** empty log message ***
*
*
*/
package com.iver.cit.gvsig.gui.preferences;

import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.iver.andami.PluginServices;
import com.iver.andami.preferences.AbstractPreferencePage;
import com.iver.andami.preferences.StoreException;
import com.iver.cit.gvsig.CADExtension;
import com.iver.cit.gvsig.gui.cad.CADGrid;
import com.iver.cit.gvsig.gui.cad.CADToolAdapter;

public class GridPage extends AbstractPreferencePage {
	private static Preferences prefs = Preferences.userRoot().node( "cadtooladapter" );
	private CADToolAdapter cta;
	private JCheckBox chkShowGrid;
	private JCheckBox chkAdjustGrid;
	private JTextField txtDistanceX;
	private JTextField txtDistanceY;
	private JLabel lblUnits=new JLabel();
	private ImageIcon icon;

	public GridPage() {
		super();
		icon = new ImageIcon(this.getClass().getClassLoader().getResource("images/Grid.png"));
		chkShowGrid=new JCheckBox(PluginServices.getText(this,"mostrar_rejilla"));
		addComponent(chkShowGrid);
		chkAdjustGrid=new JCheckBox(PluginServices.getText(this,"ajustar_rejilla"));
		addComponent(chkAdjustGrid);
		// distance x
		addComponent(lblUnits);
		addComponent(PluginServices.getText(this, "distance_x") + ":",
			txtDistanceX = new JTextField("", 15));
		// distance y
		addComponent(PluginServices.getText(this, "distance_y") + ":",
			txtDistanceY = new JTextField("", 15));

	}

	public void initializeValues() {
		cta=CADExtension.getCADToolAdapter();
		cta.initializeGrid();
//		boolean showGrid = prefs.getBoolean("grid.showgrid",cta.getGrid().isShowGrid());
//		boolean adjustGrid = prefs.getBoolean("grid.adjustgrid",cta.getGrid().isAdjustGrid());
//
//		double dx = prefs.getDouble("grid.distancex",cta.getGrid().getGridSizeX());
//		double dy = prefs.getDouble("grid.distancey",cta.getGrid().getGridSizeY());
		CADGrid cg=cta.getGrid();
		chkShowGrid.setSelected(cg.isShowGrid());
		chkAdjustGrid.setSelected(cg.isAdjustGrid());
		txtDistanceX.setText(String.valueOf(cg.getGridSizeX()));
		txtDistanceY.setText(String.valueOf(cg.getGridSizeY()));
		lblUnits.setText(PluginServices.getText(this,"Unidades"));
//		cta.setGridVisibility(showGrid);
//		cta.setAdjustGrid(adjustGrid);
//		cta.getGrid().setGridSizeX(dx);
//		cta.getGrid().setGridSizeY(dy);

	}

	public String getID() {
		return this.getClass().getName();
	}

	public String getTitle() {
		return PluginServices.getText(this, "Grid");
	}

	public JPanel getPanel() {
		return this;
	}

	public void storeValues() throws StoreException {
		boolean showGrid;
		boolean adjustGrid;
		double dx;
		double dy;

			showGrid=chkShowGrid.isSelected();
			adjustGrid=chkAdjustGrid.isSelected();
		try{
			dx=Double.parseDouble(txtDistanceX.getText());
			dy=Double.parseDouble(txtDistanceY.getText());
		}catch (Exception e) {
			throw new StoreException(PluginServices.getText(this,"distancia_malla_incorrecta"));
		}
		prefs.putBoolean("grid.showgrid",showGrid);
		prefs.putBoolean("grid.adjustgrid",adjustGrid);
		prefs.putDouble("grid.distancex", dx);
		prefs.putDouble("grid.distancey", dy);

		cta.setGridVisibility(showGrid);
		cta.setAdjustGrid(adjustGrid);
		cta.getGrid().setGridSizeX(dx);
		cta.getGrid().setGridSizeY(dy);
	}

	public void cancelAction() {
		initializeValues();
	}

	public void initializeDefaults() {
		chkShowGrid.setSelected(cta.getGrid().isShowGrid());
		chkAdjustGrid.setSelected(cta.getGrid().isAdjustGrid());
		txtDistanceX.setText(String.valueOf(cta.getGrid().getGridSizeX()));
		txtDistanceY.setText(String.valueOf(cta.getGrid().getGridSizeY()));
	}

	public ImageIcon getIcon() {
		return icon;
	}
}
