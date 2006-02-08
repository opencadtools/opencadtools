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
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.plugins.Extension;

import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.gui.View;

import java.io.IOException;


/**
 * Extensión encargada de gestionar el rehacer un comando anteriormente
 * deshecho.
 *
 * @author Vicente Caballero Navarro
 */
public class RedoCommandExtension implements Extension {
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
		MapControl mapControl = (MapControl) vista.getMapControl();

		if (s.compareTo("REDO") == 0) {
			try {
					FLayers layers=mapControl.getMapContext().getLayers();
					for (int i=0;i<layers.getLayersCount();i++){
						if (layers.getLayer(i) instanceof FLyrVect && layers.getLayer(i).isEditing()){
							VectorialEditableAdapter vea=(VectorialEditableAdapter)((FLyrVect)layers.getLayer(i)).getSource();
							vea.redo();
							vea.getSelection().clear();
						}

					}
			} catch (DriverIOException e) {
				NotificationManager.addError("Error accediendo a los Drivers para rehacer un comando",
					e);
			} catch (IOException e) {
				NotificationManager.addError("Error accediendo al fichero para rehacer un comando",
					e);
			}

			vista.getMapControl().drawMap(false);
		}
	}

	/**
	 * @see com.iver.andami.plugins.Extension#isEnabled()
	 */
	public boolean isEnabled() {
		View vista = (View) PluginServices.getMDIManager().getActiveView();
		MapControl mapControl = (MapControl) vista.getMapControl();
		FLayers layers=mapControl.getMapContext().getLayers();
		for (int i=0;i<layers.getLayersCount();i++){
			if (layers.getLayer(i) instanceof FLyrVect && ((FLyrVect)layers.getLayer(i)).getSource() instanceof VectorialEditableAdapter && layers.getLayer(i).isEditing()){
				VectorialEditableAdapter vea=(VectorialEditableAdapter)((FLyrVect)layers.getLayer(i)).getSource();
				if (vea==null)return false;
				return vea.moreRedoCommands();
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
