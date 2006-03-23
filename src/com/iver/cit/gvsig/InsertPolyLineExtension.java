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
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.DriverException;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.gui.View;
import com.iver.cit.gvsig.gui.cad.tools.PolylineCADTool;

/**
 * Extensión que gestiona la inserción de polilíneas en edición.
 *
 * @author Vicente Caballero Navarro
 */
public class InsertPolyLineExtension implements Extension {
	private View view;

	private MapControl mapControl;

	/**
	 * @see com.iver.andami.plugins.Extension#inicializar()
	 */
	public void inicializar() {
		PolylineCADTool polyline = new PolylineCADTool();
		CADExtension.addCADTool("polyline", polyline);
	}

	/**
	 * @see com.iver.andami.plugins.Extension#execute(java.lang.String)
	 */
	public void execute(String s) {
		mapControl.addMapTool("cadtooladapter",CADExtension.getCADToolAdapter());
		mapControl.setTool("cadtooladapter");
		if (s.equals("POLYLINE")) {
			CADExtension.setCADTool("polyline");
			CADExtension.getEditionManager().setMapControl(mapControl);
		}
		CADExtension.getCADToolAdapter().configureMenu();
	}

	/**
	 * @see com.iver.andami.plugins.Extension#isEnabled()
	 */
	public boolean isEnabled() {

		try {
			if (EditionUtilities.getEditionStatus() == EditionUtilities.EDITION_STATUS_ONE_VECTORIAL_LAYER_ACTIVE_AND_EDITABLE) {
				view = (View) PluginServices.getMDIManager().getActiveView();
				mapControl = (MapControl) view.getMapControl();
				FLayer[] layers = mapControl.getMapContext().getLayers()
						.getActives();
				if (((FLyrVect) layers[0]).getShapeType() == FShape.LINE
						|| ((FLyrVect) layers[0]).getShapeType() == FShape.POLYGON
						|| ((FLyrVect) layers[0]).getShapeType() == FShape.MULTI) {
					return true;
				}
			}
		} catch (DriverException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @see com.iver.andami.plugins.Extension#isVisible()
	 */
	public boolean isVisible() {
		com.iver.andami.ui.mdiManager.View f = PluginServices.getMDIManager()
				.getActiveView();

		if (f == null) {
			return false;
		}

		if (f.getClass() == View.class) {
			return true;
		} else {
			return false;
		}
	}
}
