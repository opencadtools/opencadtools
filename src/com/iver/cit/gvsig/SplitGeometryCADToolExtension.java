/*
 * Created on 10-abr-2006
 *
 * gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
/* CVS MESSAGES:
 *
 * $Id:
 * $Log:
 */
package com.iver.cit.gvsig;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.gui.cad.tools.SplitGeometryCADTool;
import com.iver.cit.gvsig.project.documents.view.gui.View;

/**
 * CAD extension to split geometries from a digitized linear geometry.
 * 
 * @author Alvaro Zabala
 * 
 */
public class SplitGeometryCADToolExtension extends BaseCADExtension {

    private View view;
    private MapControl mapControl;
    private SplitGeometryCADTool cadTool;

    @Override
    public void initialize() {
	cadTool = new SplitGeometryCADTool();
	CADExtension.addCADTool(SplitGeometryCADTool.SPLIT_GEOMETRY_TOOL_NAME,
		cadTool);
	registerIcon("split-geometry", "images/split-poly.png");
    }

    @Override
    public void execute(String s) {
	CADExtension.initFocus();
	if (s.equals(SplitGeometryCADTool.SPLIT_GEOMETRY_TOOL_NAME)) {
	    CADExtension.setCADTool(
		    SplitGeometryCADTool.SPLIT_GEOMETRY_TOOL_NAME, true);
	}
	CADExtension.getEditionManager().setMapControl(mapControl);
	CADExtension.getCADToolAdapter().configureMenu();
    }

    /**
     * Returns if this Edit CAD tool is visible. For this, there must be an
     * active vectorial editing lyr in the TOC, which geometries' dimension
     * would must be linear or polygonal, and with at least one selected
     * geometry.
     * 
     */
    @Override
    public boolean isEnabled() {
	try {
	    if (EditionUtilities.getEditionStatus() == EditionUtilities.EDITION_STATUS_ONE_VECTORIAL_LAYER_ACTIVE_AND_EDITABLE) {
		this.view = (View) PluginServices.getMDIManager()
			.getActiveWindow();
		mapControl = view.getMapControl();
		if (CADExtension.getEditionManager().getActiveLayerEdited() == null) {
		    return false;
		}
		FLyrVect lv = (FLyrVect) CADExtension.getEditionManager()
			.getActiveLayerEdited().getLayer();
		int geometryDimensions = getDimensions(lv.getShapeType());
		if (geometryDimensions <= 0) {
		    return false;
		}

		return lv.getRecordset().getSelection().cardinality() != 0;
	    }
	} catch (ReadDriverException e) {
	    NotificationManager.addError(e.getMessage(), e);
	    return false;
	}
	return true;
    }

    private static int getDimensions(int shapeType) {
	switch (shapeType) {
	case FShape.ARC:
	case FShape.LINE:
	    return 1;

	case FShape.CIRCLE:
	case FShape.ELLIPSE:
	case FShape.POLYGON:
	case FShape.MULTI:
	    return 2;

	case FShape.MULTIPOINT:
	case FShape.POINT:
	case FShape.TEXT:
	    return 0;
	default:
	    return -1;
	}
    }
}
