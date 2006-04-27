package com.iver.cit.gvsig;

import java.awt.Component;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import com.hardcode.driverManager.Driver;
import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.DriverException;
import com.iver.cit.gvsig.fmap.FMap;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.edition.EditionException;
import com.iver.cit.gvsig.fmap.edition.ISpatialWriter;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;
//import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.VectorialAdapter;
import com.iver.cit.gvsig.fmap.layers.VectorialFileAdapter;
import com.iver.cit.gvsig.gui.View;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;
import com.iver.cit.gvsig.project.ProjectView;
import com.iver.utiles.SimpleFileFilter;

/**
 * @author Francisco José
 *
 * Cuando un tema se pone en edición, puede que su driver implemente
 * ISpatialWriter. En ese caso, es capaz de guardarse sobre sí mismo. Si no lo
 * implementa, esta opción estará deshabilitada y la única posibilidad de
 * guardar este tema será "Guardando como..."
 */
public class StopEditing implements Extension {
	private View vista;

	/**
	 * @see com.iver.andami.plugins.Extension#inicializar()
	 */
	public void inicializar() {
	}

	/**
	 * @see com.iver.andami.plugins.Extension#execute(java.lang.String)
	 */
	public void execute(String s) {
		com.iver.andami.ui.mdiManager.View f = PluginServices.getMDIManager()
				.getActiveView();

		vista = (View) f;

		ProjectView model = vista.getModel();
		FMap mapa = model.getMapContext();
		FLayers layers = mapa.getLayers();
		EditionManager edMan = CADExtension.getEditionManager();
		if (s.equals("STOPEDITING")) {
			FLayer[] actives = layers.getActives();
			// TODO: Comprobar que solo hay una activa, o al menos
			// que solo hay una en edición que esté activa, etc, etc
			for (int i = 0; i < actives.length; i++) {
				if (actives[i] instanceof FLyrVect && actives[i].isEditing()) {
					FLyrVect lv = (FLyrVect) actives[i];
					MapControl mapControl = (MapControl) vista.getMapControl();
					VectorialLayerEdited lyrEd = (VectorialLayerEdited) edMan.getActiveLayerEdited();
					lyrEd.clearSelection();
					stopEditing(lv, mapControl);

					return;
				}
			}
		}
		PluginServices.getMainFrame().enableControls();
	}

	/**
	 * @see com.iver.andami.plugins.Extension#isEnabled()
	 */
public boolean isEnabled() {
		FLayer[] lyrs = EditionUtilities.getActiveAndEditedLayers();
		if (lyrs == null) return false;
		FLyrVect lyrVect = (FLyrVect) lyrs[0];
		if (lyrVect.getSource() instanceof VectorialEditableAdapter){
			VectorialEditableAdapter vea = (VectorialEditableAdapter) lyrVect.getSource();
		if (vea.getDriver() instanceof ISpatialWriter)
			return true;
		}
		return false;
    }
	/**
	 * DOCUMENT ME!
	 */
public void stopEditing(FLyrVect layer,MapControl mapControl) {
    	VectorialEditableAdapter vea = (VectorialEditableAdapter) layer.getSource();

		ISpatialWriter writer = (ISpatialWriter) vea.getDriver();

		try {
			writer.initialize(layer);
	        vea.stopEdition(writer);
	        vea.getCommandRecord().removeCommandListener(mapControl);

	        layer.setEditing(false);
	        mapControl.setTool("zoomIn");
	        vista.hideConsole();
		} catch (EditionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
	/**
	 * @see com.iver.andami.plugins.Extension#isVisible()
	 */
	public boolean isVisible() {
		if (EditionUtilities.getEditionStatus() == EditionUtilities.EDITION_STATUS_ONE_VECTORIAL_LAYER_ACTIVE_AND_EDITABLE)
			return true;
		else
			return false;

	}
}
