package com.iver.cit.gvsig;

import java.io.IOException;

import javax.swing.JOptionPane;

import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.FMap;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.edition.EditionEvent;
import com.iver.cit.gvsig.fmap.edition.EditionException;
import com.iver.cit.gvsig.fmap.edition.ISpatialWriter;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.gui.Table;
import com.iver.cit.gvsig.gui.View;
import com.iver.cit.gvsig.project.ProjectView;

/**
 * @author Francisco José
 * 
 * Cuando un tema se pone en edición, puede que su driver implemente
 * ISpatialWriter. En ese caso, es capaz de guardarse sobre sí mismo. Si no lo
 * implementa, esta opción estará deshabilitada y la única posibilidad de
 * guardar este tema será "Guardando como..."
 */
public class StopEditing extends Extension {
	private View vista;

	/**
	 * @see com.iver.andami.plugins.IExtension#initialize()
	 */
	public void initialize() {
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
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
					// VectorialLayerEdited lyrEd = (VectorialLayerEdited)
					// edMan.getActiveLayerEdited();
					// lyrEd.clearSelection();
					stopEditing(lv, mapControl);

					// return;
				}
			}
			vista.getMapControl().setTool("zoomIn");
			vista.hideConsole();			
		}
		PluginServices.getMainFrame().enableControls();
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#isEnabled()
	 */
	public boolean isEnabled() {
		FLayer[] lyrs = EditionUtilities.getActiveAndEditedLayers();
		if (lyrs == null)
			return false;
		FLyrVect lyrVect = (FLyrVect) lyrs[0];
		if (lyrVect.getSource() instanceof VectorialEditableAdapter) {
			VectorialEditableAdapter vea = (VectorialEditableAdapter) lyrVect
					.getSource();
			if (vea.getDriver() instanceof ISpatialWriter)
				return true;
		}
		return false;
	}

	/**
	 * DOCUMENT ME!
	 */
	public void stopEditing(FLyrVect layer, MapControl mapControl) {
		VectorialEditableAdapter vea = (VectorialEditableAdapter) layer
				.getSource();

		ISpatialWriter writer = (ISpatialWriter) vea.getDriver();

		int resp = JOptionPane
				.showConfirmDialog(null, PluginServices.getText(this,
						"realmente_desea_guardar_features_de_capa") + layer.getName(),
						PluginServices.getText(this,"Guardar"),
						JOptionPane.YES_NO_OPTION);
		try {
			if (resp == JOptionPane.NO_OPTION) { // CANCEL EDITING
				com.iver.andami.ui.mdiManager.View[] views = PluginServices
						.getMDIManager().getAllViews();
				for (int j = 0; j < views.length; j++) {
					if (views[j] instanceof Table) {
						Table table = (Table) views[j];
						if (table.getModel().getAssociatedTable() != null
								&& table.getModel().getAssociatedTable()
										.equals(layer)) {
							table.cancelEditing();
						}
					}
				}
			} else { // GUARDAMOS EL TEMA
				writer.initialize(layer);
				vea.stopEdition(writer, EditionEvent.GRAPHIC);				
			}
			vea.getCommandRecord().removeCommandListener(mapControl);
			layer.setEditing(false);
		} catch (EditionException e) {
			e.printStackTrace();
			NotificationManager.addError(e);

		} catch (IOException e) {
			e.printStackTrace();
			NotificationManager.addError(e);
		}

	}

	/**
	 * @see com.iver.andami.plugins.IExtension#isVisible()
	 */
	public boolean isVisible() {
		if (EditionUtilities.getEditionStatus() == EditionUtilities.EDITION_STATUS_ONE_VECTORIAL_LAYER_ACTIVE_AND_EDITABLE)
			return true;
		else
			return false;

	}
}
