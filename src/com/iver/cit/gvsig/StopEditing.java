package com.iver.cit.gvsig;

import java.awt.Component;
import java.io.IOException;

import javax.swing.JOptionPane;

import com.hardcode.gdbms.engine.instruction.FieldNotFoundException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.DriverException;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.ILayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.VectorialDriver;
import com.iver.cit.gvsig.fmap.drivers.shp.IndexedShpDriver;
import com.iver.cit.gvsig.fmap.edition.EditionEvent;
import com.iver.cit.gvsig.fmap.edition.EditionException;
import com.iver.cit.gvsig.fmap.edition.ISpatialWriter;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.rendering.VectorialLegend;
import com.iver.cit.gvsig.gui.Table;
import com.iver.cit.gvsig.gui.View;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;
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
		com.iver.andami.ui.mdiManager.IWindow f = PluginServices.getMDIManager()
				.getActiveView();

		vista = (View) f;
		boolean isStop=false;
		ProjectView model = vista.getModel();
		MapContext mapa = model.getMapContext();
		FLayers layers = mapa.getLayers();
		EditionManager edMan = CADExtension.getEditionManager();
		if (s.equals("STOPEDITING")) {
			FLayer[] actives = layers.getActives();
			// TODO: Comprobar que solo hay una activa, o al menos
			// que solo hay una en edición que esté activa, etc, etc
			for (int i = 0; i < actives.length; i++) {
				if (actives[i] instanceof FLyrVect && actives[i].isEditing()) {
					FLyrVect lv = (FLyrVect) actives[i];
					MapControl mapControl = vista.getMapControl();
					VectorialLayerEdited lyrEd = (VectorialLayerEdited)	edMan.getActiveLayerEdited();
					lyrEd.clearSelection();
					isStop=stopEditing(lv, mapControl);

					// return;
				}
			}
			if (isStop) {
				vista.getMapControl().setTool("zoomIn");
				vista.hideConsole();
				vista.repaintMap();
			}
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
			return true;
		}
		return false;
	}
	/**
	 * DOCUMENT ME!
	 */
	public boolean stopEditing(FLyrVect layer, MapControl mapControl) {
		VectorialEditableAdapter vea = (VectorialEditableAdapter) layer
				.getSource();
		int resp = JOptionPane.NO_OPTION;

		try {
			if (layer.isWritable()) {
				resp = JOptionPane.showConfirmDialog((Component) PluginServices
						.getMainFrame(), PluginServices.getText(this,
						"realmente_desea_guardar_la_capa")
						+ " : " + layer.getName(), PluginServices.getText(this,
						"guardar"), JOptionPane.YES_NO_OPTION);
				if (resp != JOptionPane.YES_OPTION) { // CANCEL EDITING
					cancelEdition(layer);
				} else { // GUARDAMOS EL TEMA
					saveLayer(layer);
				}

				vea.getCommandRecord().removeCommandListener(mapControl);
				layer.setEditing(false);
				return true;
			}
			// Si no existe writer para la capa que tenemos en edición
				resp = JOptionPane
						.showConfirmDialog(
								(Component) PluginServices.getMainFrame(),
								PluginServices
										.getText(
												this,
												"no_existe_writer_para_este_formato_de_capa_o_no_tiene_permisos_de_escritura_los_datos_no_se_guardaran_desea_continuar")
										+ " : " + layer.getName(),
								PluginServices.getText(this, "cancelar_edicion"),
								JOptionPane.YES_NO_OPTION);
				if (resp == JOptionPane.YES_OPTION) { // CANCEL EDITING
					cancelEdition(layer);
					vea.getCommandRecord().removeCommandListener(mapControl);
					if (!(layer.getSource().getDriver() instanceof IndexedShpDriver)){
						VectorialLayerEdited vle=(VectorialLayerEdited)CADExtension.getEditionManager().getLayerEdited(layer);
						layer.setLegend((VectorialLegend)vle.getLegend());
					}
					layer.setEditing(false);
					return true;
				}

		} catch (EditionException e) {
			NotificationManager.addError(e);
		} catch (IOException e) {
			NotificationManager.addError(e);
		} catch (DriverException e) {
			NotificationManager.addError(e);
		} catch (FieldNotFoundException e) {
			e.printStackTrace();
		}
		return false;

	}


	private void saveLayer(FLyrVect layer) throws DriverException,
			EditionException {
		VectorialEditableAdapter vea = (VectorialEditableAdapter) layer
				.getSource();

		ISpatialWriter writer = (ISpatialWriter) vea.getWriter();
		com.iver.andami.ui.mdiManager.IWindow[] views = PluginServices
				.getMDIManager().getAllViews();
		for (int j = 0; j < views.length; j++) {
			if (views[j] instanceof Table) {
				Table table = (Table) views[j];
				if (table.getModel().getAssociatedTable() != null
						&& table.getModel().getAssociatedTable().equals(layer)) {
					table.stopEditingCell();
				}
			}
		}
		vea.cleanSelectableDatasource();
		layer.setRecordset(vea.getRecordset()); // Queremos que el recordset del layer
		// refleje los cambios en los campos.
		ILayerDefinition lyrDef = EditionUtilities.createLayerDefinition(layer);
		String aux="FIELDS:";
		FieldDescription[] flds = lyrDef.getFieldsDesc();
		for (int i=0; i < flds.length; i++)
		{
			aux = aux + ", " + flds[i].getFieldAlias();
		}
		System.err.println("Escribiendo la capa " + lyrDef.getName() +
				" con los campos " + aux);
		writer.initialize(lyrDef);
		vea.stopEdition(writer, EditionEvent.GRAPHIC);

	}

	private void cancelEdition(FLyrVect layer) throws IOException {
		com.iver.andami.ui.mdiManager.IWindow[] views = PluginServices
				.getMDIManager().getAllViews();
		for (int j = 0; j < views.length; j++) {
			if (views[j] instanceof Table) {
				Table table = (Table) views[j];
				if (table.getModel().getAssociatedTable() != null
						&& table.getModel().getAssociatedTable().equals(layer)) {
					table.cancelEditing();
				}
			}
		}
	}
	/**
	 * @see com.iver.andami.plugins.IExtension#isVisible()
	 */
	public boolean isVisible() {
		if (EditionUtilities.getEditionStatus() == EditionUtilities.EDITION_STATUS_ONE_VECTORIAL_LAYER_ACTIVE_AND_EDITABLE)
			return true;
		return false;

	}
}
