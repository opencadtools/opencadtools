/*
 * 
 * Copyright 2009 Deputación Provincial de Pontevedra
 * Copyright 2010 CartoLab, Universidad de A Coruña
 *
 * This file is part of openCADTools, developed by the Cartography
 * Engineering Laboratory of the University of A Coruña (CartoLab).
 * http://www.cartolab.es
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
 */

package com.iver.cit.gvsig;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.gui.cad.tools.InsertVertexCADTool;
import com.iver.cit.gvsig.project.documents.view.gui.View;

/**
 * Extension to insert a new vertex on a geometry of a layer in edition. Layer
 * must have line or poligon geometry.
 * 
 * @author Nacho Uve [Cartolab]
 */
public class InsertVertexExtension extends BaseCADExtension {
    private View view;
    private MapControl mapControl;
    private InsertVertexCADTool insertVertex;

    /**
     * @see com.iver.andami.plugins.IExtension#initialize()
     */
    @Override
    public void initialize() {
	insertVertex = new InsertVertexCADTool();
	CADExtension.addCADTool("_insertVertex", insertVertex);
	PluginServices.getIconTheme().registerDefault(
		"edition-geometry-insert-vertex",
		this.getClass().getClassLoader()
			.getResource("images/icons/anhadir_vertice.png"));
    }

    /**
     * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
     */
    @Override
    public void execute(String s) {
	CADExtension.initFocus();

	if (s.equals("_insertVertex")) {
	    CADExtension.setCADTool("_insertVertex", true);
	    CADExtension.getEditionManager().setMapControl(mapControl);
	}
	CADExtension.getCADToolAdapter().configureMenu();
    }

    /**
     * @see com.iver.andami.plugins.IExtension#isEnabled()
     */
    @Override
    public boolean isEnabled() {

	if (EditionUtilities.getEditionStatus() == EditionUtilities.EDITION_STATUS_ONE_VECTORIAL_LAYER_ACTIVE_AND_EDITABLE) {
	    view = (View) PluginServices.getMDIManager().getActiveWindow();
	    mapControl = view.getMapControl();
	    if (CADExtension.getEditionManager().getActiveLayerEdited() == null) {
		return false;
	    }
	    FLyrVect lv = (FLyrVect) CADExtension.getEditionManager()
		    .getActiveLayerEdited().getLayer();
	    try {
		if (insertVertex.isApplicable(lv.getShapeType())) {
		    return true;
		}
	    } catch (ReadDriverException e) {
		NotificationManager.addError(e.getMessage(), e);
	    }
	}
	return false;
    }

}
