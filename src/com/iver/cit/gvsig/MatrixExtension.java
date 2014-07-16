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
package com.iver.cit.gvsig;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.gui.cad.tools.MatrixCADTool;
import com.iver.cit.gvsig.project.documents.view.gui.View;

/**
 * Extensión que gestiona la creación de una matriz a partir de la geometría
 * seleccionada.
 * 
 * @author Vicente Caballero Navarro
 */
public class MatrixExtension extends BaseCADExtension {

    /**
     * @see com.iver.andami.plugins.IExtension#initialize()
     */
    @Override
    public void initialize() {
	registerIcons();
	tool = new MatrixCADTool();
	CADExtension.addCADTool("_matrix", tool);

    }

    private void registerIcons() {
	PluginServices.getIconTheme().registerDefault(
		"edition-geometry-matrix",
		this.getClass().getClassLoader()
			.getResource("images/Matriz.png"));
	PluginServices.getIconTheme().registerDefault(
		"edition-geometrymatrix-lagxy",
		this.getClass().getClassLoader()
			.getResource("images/lagxy.png"));
	PluginServices.getIconTheme().registerDefault(
		"edition-geometrymatrix-addpoint",
		this.getClass().getClassLoader()
			.getResource("images/addpoint.png"));
    }

    /**
     * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
     */
    @Override
    public void execute(String s) {
	CADExtension.initFocus();
	if (s.equals("_matrix")) {
	    CADExtension.setCADTool(s, true);
	}
	View view = (View) PluginServices.getMDIManager().getActiveWindow();
	MapControl mapControl = view.getMapControl();
	CADExtension.getEditionManager().setMapControl(mapControl);
	CADExtension.getCADToolAdapter().configureMenu();
    }
}
