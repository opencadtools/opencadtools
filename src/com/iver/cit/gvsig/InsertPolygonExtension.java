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

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.gui.cad.DefaultCADTool;
import com.iver.cit.gvsig.gui.cad.tools.CircleCADTool;
import com.iver.cit.gvsig.gui.cad.tools.EditVertexCADTool;
import com.iver.cit.gvsig.gui.cad.tools.EllipseCADTool;
import com.iver.cit.gvsig.gui.cad.tools.PolygonCADTool;
import com.iver.cit.gvsig.gui.cad.tools.RectangleCADTool;
import com.iver.cit.gvsig.project.documents.view.gui.View;

/**
 * Extensión que gestiona la inserción de poligonos en edición.
 * 
 * @author Vicente Caballero Navarro
 */
public class InsertPolygonExtension extends BaseCADExtension {
    protected View view;

    protected MapControl mapControl;
    protected PolygonCADTool polygon;

    @Override
    public void initialize() {
	polygon = new PolygonCADTool();
	CADExtension.addCADTool("_polygon", polygon);
	registerIcon("edition-insert-polygon", "images/icons/poligono.png");

	DefaultCADTool circle = new CircleCADTool();
	CADExtension.addCADTool("_circle", circle);
	registerIcon("edition-insert-circle", "images/icons/circulo.png");

	RectangleCADTool rectangle = new RectangleCADTool();
	CADExtension.addCADTool("_rectangle", rectangle);
	registerIcon("edition-insert-rectangle", "images/icons/rectangulo.png");

	EllipseCADTool ellipse = new EllipseCADTool();
	CADExtension.addCADTool("_ellipse", ellipse);
	registerIcon("edition-insert-ellipse", "images/icons/elipse.png");

	EditVertexCADTool editvertex = new EditVertexCADTool();
	CADExtension.addCADTool("_editvertex", editvertex);
	registerIcon("edition-geometry-edit-vertex", "images/EditVertex.png");
    }

    /**
     * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
     */
    @Override
    public void execute(String s) {
	CADExtension.initFocus();
	if (s.equals("_polygon") || s.equals("_circle") || s.equals("_ellipse")
		|| s.equals("_rectangle") || s.equals("_editvertex")) {
	    CADExtension.setCADTool(s, true);
	}
	CADExtension.getEditionManager().setMapControl(mapControl);
	CADExtension.getCADToolAdapter().configureMenu();
    }

    /**
     * @see com.iver.andami.plugins.IExtension#isEnabled()
     */
    @Override
    public boolean isEnabled() {

	try {
	    if (EditionUtilities.getEditionStatus() == EditionUtilities.EDITION_STATUS_ONE_VECTORIAL_LAYER_ACTIVE_AND_EDITABLE) {
		view = (View) PluginServices.getMDIManager().getActiveWindow();
		mapControl = view.getMapControl();
		if (CADExtension.getEditionManager().getActiveLayerEdited() == null) {
		    return false;
		}
		FLyrVect lv = (FLyrVect) CADExtension.getEditionManager()
			.getActiveLayerEdited().getLayer();
		if (polygon.isApplicable(lv.getShapeType())) {
		    return true;
		}
	    }
	} catch (ReadDriverException e) {
	    NotificationManager.addError(e.getMessage(), e);
	}
	return false;
    }

}
