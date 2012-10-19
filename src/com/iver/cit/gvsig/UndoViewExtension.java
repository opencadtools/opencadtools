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
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.exceptions.commands.EditionCommandException;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.gui.cad.CADTool;
import com.iver.cit.gvsig.project.documents.view.gui.View;

/**
 * Extensión encargada de gestionar el deshacer los comandos anteriormente
 * aplicados.
 * 
 * @author Vicente Caballero Navarro
 */
public class UndoViewExtension extends Extension {
    /**
     * @see com.iver.andami.plugins.IExtension#initialize()
     */
    public void initialize() {
	PluginServices.getIconTheme()
		.registerDefault(
			"view-undo",
			this.getClass().getClassLoader()
				.getResource("images/Undo.png"));
    }

    /**
     * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
     */
    public void execute(String s) {
	View vista = (View) PluginServices.getMDIManager().getActiveWindow();

	if (s.compareTo("UNDO") == 0) {
	    undo(vista);

	}
    }

    private void undo(View vista) {
	MapControl mapControl = vista.getMapControl();
	try {
	    FLayers layers = mapControl.getMapContext().getLayers();
	    FLayer[] activeLayers = layers.getActives();
	    if (activeLayers.length == 1) {
		FLayer activeLayer = activeLayers[0];
		if (activeLayer instanceof FLyrVect
			&& ((FLyrVect) activeLayer).getSource() instanceof VectorialEditableAdapter
			&& activeLayer.isEditing() && activeLayer.isActive()) {
		    VectorialEditableAdapter vea = (VectorialEditableAdapter) ((FLyrVect) activeLayer)
			    .getSource();
		    vea.undo();
		    vea.getCommandRecord().fireCommandsRepaint(null);
		    vea.getSelection().clear();
		    CADTool cadTool = CADExtension.getCADTool();
		    if (cadTool != null) {
			cadTool.clearSelection();
		    }
		}
	    }
	} catch (EditionCommandException e) {
	    NotificationManager.addError(e.getMessage(), e);
	} catch (ReadDriverException e) {
	    NotificationManager.addError(e.getMessage(), e);
	}

    }

    /**
     * @see com.iver.andami.plugins.IExtension#isEnabled()
     */
    public boolean isEnabled() {
	View vista = (View) PluginServices.getMDIManager().getActiveWindow();
	MapControl mapControl = vista.getMapControl();
	FLayers layers = mapControl.getMapContext().getLayers();
	FLayer[] activeLayers = layers.getActives();
	if (activeLayers.length == 1) {
	    FLayer activeLayer = activeLayers[0];
	    if (activeLayer instanceof FLyrVect
		    && ((FLyrVect) activeLayer).getSource() instanceof VectorialEditableAdapter
		    && activeLayer.isEditing() && activeLayer.isActive()) {
		VectorialEditableAdapter vea = (VectorialEditableAdapter) ((FLyrVect) activeLayer)
			.getSource();
		if (vea == null) {
		    return false;
		}
		return vea.getCommandRecord().moreUndoCommands();
	    }

	}
	return false;
    }

    /**
     * @see com.iver.andami.plugins.IExtension#isVisible()
     */
    public boolean isVisible() {
	com.iver.andami.ui.mdiManager.IWindow f = PluginServices
		.getMDIManager().getActiveWindow();

	if (f == null) {
	    return false;
	}

	if (f instanceof View) {
	    MapControl mapControl = ((View) f).getMapControl();
	    FLayers layers = mapControl.getMapContext().getLayers();
	    FLayer[] activeLayers = layers.getActives();
	    if (activeLayers.length == 1) {
		FLayer activeLayer = activeLayers[0];
		if (activeLayer instanceof FLyrVect
			&& ((FLyrVect) activeLayer).getSource() instanceof VectorialEditableAdapter
			&& activeLayer.isEditing() && activeLayer.isActive()) {
		    VectorialEditableAdapter vea = (VectorialEditableAdapter) ((FLyrVect) activeLayer)
			    .getSource();
		    if (vea == null) {
			return false;
		    }
		    return true;
		}
	    }
	}
	return false;
    }
}
