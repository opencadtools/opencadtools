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

import java.io.IOException;

import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.plugins.Extension;

import com.iver.cit.gvsig.fmap.FMap;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.gui.View;



/**
 * Extensión dedicada a controlar las diferentes operaciones sobre el editado
 * de una capa.
 *
 * @author Vicente Caballero Navarro
 */
public class GeometryExtension implements Extension {
	/**
	 * @see com.iver.andami.plugins.Extension#inicializar()
	 */
	public void inicializar() {
	}

	/**
	 * @see com.iver.andami.plugins.Extension#execute(java.lang.String)
	 */
	public void execute(String s) {
		View vista = (View) PluginServices.getMDIManager().getActiveView();
		//PluginServices.getMainFrame().showConsole();
		MapControl cadmap=(MapControl)vista.getMapControl();
		if (s.compareTo("SPLINE") == 0) {
			//Spline.png
			///vista.getMapControl().setCadTool("spline");
		} else if (s.compareTo("COPY") == 0) {
			//Copy.png
			///vista.getMapControl().setCadTool("copy");
		} else if (s.compareTo("EQUIDISTANCE") == 0) {
			//Equidistance.png
		} else if (s.compareTo("MATRIZ") == 0) {
			//Matriz.png
		} else if (s.compareTo("SYMMETRY") == 0) {
			//Symmetry.png
			///vista.getMapControl().setCadTool("symmetry");
		} else if (s.compareTo("ROTATION") == 0) {
			//Rotation.png
			///vista.getMapControl().setCadTool("rotate");
		} else if (s.compareTo("STRETCHING") == 0) {
			//Stretching.png
			///vista.getMapControl().setCadTool("stretching");
		} else if (s.compareTo("SCALE") == 0) {
			//Scale.png
			///vista.getMapControl().setCadTool("scale");
		} else if (s.compareTo("EXTEND") == 0) {
			//Extend.png
		} else if (s.compareTo("TRIM") == 0) {
			//Trim.png
		} else if (s.compareTo("UNIT") == 0) {
			//Unit.png
		} else if (s.compareTo("EXPLOIT") == 0) {
			//Exploit.png
			///vista.getMapControl().setCadTool("exploit");
		} else if (s.compareTo("CHAFLAN") == 0) {
			//Chaflan.png
		} else if (s.compareTo("JOIN") == 0) {
			//Join.png
		} else if (s.compareTo("SELECT") == 0) {
			///vista.getMapControl().setCadTool("selection");
		} else if (s.compareTo("POINT") == 0) {
			///vista.getMapControl().setCadTool("point");
		} else if (s.compareTo("LINE") == 0) {
			///vista.getMapControl().setCadTool("line");
		} else if (s.compareTo("POLYLINE") == 0) {
			///vista.getMapControl().setCadTool("polyline");
		} else if (s.compareTo("CIRCLE") == 0) {
			///vista.getMapControl().setCadTool("circle");
		} else if (s.compareTo("ARC") == 0) {
			///vista.getMapControl().setCadTool("arc");
		} else if (s.compareTo("ELLIPSE") == 0) {
			///vista.getMapControl().setCadTool("ellipse");
		} else if (s.compareTo("RECTANGLE") == 0) {
			///vista.getMapControl().setCadTool("rectangle");
		} else if (s.compareTo("POLYGON") == 0) {
			///vista.getMapControl().setCadTool("polygon");
		}
		//ViewControls.CANCELED=false;
	}

	/**
	 * @see com.iver.andami.plugins.Extension#isEnabled()
	 */
	public boolean isEnabled() {
		com.iver.andami.ui.mdiManager.View f = PluginServices.getMDIManager()
															 .getActiveView();

		if (f == null) {
			return false;
		}

		if (f.getClass() == View.class) {
			FLayer[] l = ((View) f).getModel().getMapContext().getLayers()
						  .getActives();

			for (int i = 0; i < l.length; i++) {
				if (l[i] instanceof FLyrVect && ((FLyrVect)l[i]).isEditing()) {
					return true;
				}
			}
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
