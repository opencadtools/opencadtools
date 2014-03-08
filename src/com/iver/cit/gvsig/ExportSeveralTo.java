/*
 * Copyright 2008 Deputación Provincial de A Coruña
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

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.gvsig.exceptions.BaseException;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.export.SHPExporter;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.project.documents.view.gui.View;

public class ExportSeveralTo extends ExportTo {
    private static final Logger logger = Logger
	    .getLogger(ExportSeveralTo.class);

    @Override
    public void execute(String actionCommand) {
	if (!actionCommand.equals("SHP")) {
	    super.execute(actionCommand);
	    return;
	}

	JFileChooser fileChooser = new JFileChooser();
	fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	if (fileChooser.showSaveDialog((Component) PluginServices
		.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
	    File newFile = fileChooser.getSelectedFile();
	    String path = newFile.getAbsolutePath();
	    if (!path.endsWith(File.separator)) {
		path = path + File.separator;
	    }

	    View view = (View) PluginServices.getMDIManager().getActiveWindow();
	    FLayer[] activeLayers = view.getMapControl().getMapContext()
		    .getLayers().getActives();
	    for (FLayer fLayer : activeLayers) {
		if (!(fLayer instanceof FLyrVect)) {
		    continue;
		}

		FLyrVect layer = (FLyrVect) fLayer;
		if (confirmExport(layer)) {
		    String newFilePath = path
			    + layer.getName().replaceAll(" ", "_");
		    if (!newFilePath.toLowerCase().endsWith(".shp")) {
			newFilePath = newFilePath + ".shp";
		    }

		    newFile = new File(newFilePath);
		    int r = 0;
		    while (newFile.exists()) {
			newFile = new File(newFilePath.substring(0,
				newFilePath.length() - 4)
				+ "_" + r + ".shp");
			r++;
		    }

		    try {
			SHPExporter.getInstance().export(
				view.getMapControl().getMapContext(), layer,
				newFile.getAbsolutePath());
		    } catch (BaseException e) {
			logger.error("Cannot export layer", e);
			JOptionPane.showMessageDialog(null,
				PluginServices.getText(this, "cannot_export"),
				PluginServices.getText(this, "error"),
				JOptionPane.ERROR_MESSAGE);
		    }
		}
	    }
	}
    }

    @Override
    public boolean isVisible() {
	IWindow window = PluginServices.getMDIManager().getActiveWindow();

	if (window instanceof View) {
	    View view = (View) window;
	    MapContext mapContext = view.getMapControl().getMapContext();
	    FLayer[] layers = mapContext.getLayers().getActives();
	    return layers.length > 1;
	} else {
	    return false;
	}
    }
}
