package com.iver.cit.gvsig;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.FMap;
import com.iver.cit.gvsig.fmap.edition.EditionException;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.gui.View;
import com.iver.cit.gvsig.project.ProjectTable;
import com.iver.cit.gvsig.project.ProjectView;





/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class StartEditing implements Extension {
    /**
     * @see com.iver.andami.plugins.Extension#inicializar()
     */
    public void inicializar() {
    }

    /**
     * @see com.iver.andami.plugins.Extension#execute(java.lang.String)
     */
    public void execute(String actionCommand) {
        com.iver.andami.ui.mdiManager.View f = PluginServices.getMDIManager()
                                                             .getActiveView();

        if (f instanceof View) {
            View vista = (View) f;
            vista.showConsole();
            ProjectView model = vista.getModel();
            FMap mapa = model.getMapContext();
            FLayers layers = mapa.getLayers();

            for (int i = 0; i < layers.getLayersCount(); i++) {
                if (layers.getLayer(i) instanceof FLyrVect &&
                        layers.getLayer(i).isActive()) {
                    /* for (int j = 0; j < i; j++) {
                        layers.getLayer(j).setVisible(false);
                    } */

                    FLyrVect lv = (FLyrVect) layers.getLayer(i);
                    // lv.setVisible(true);

                    VectorialEditableAdapter vea = new VectorialEditableAdapter();
                    vea.setOriginalVectorialAdapter(lv.getSource());
                    try{
                    vea.startEdition();
                    }catch (EditionException e) {
						// TODO: handle exception
					}
                    lv.setSource(vea);
                    lv.setEditing(true);

                    //Si existe una tabla asociada a esta capa se cambia su modelo por el VectorialEditableAdapter.
                    ProjectExtension pe=(ProjectExtension)PluginServices.getExtension(ProjectExtension.class);
                    ProjectTable pt=pe.getProject().getTable(lv);
                    if (pt!=null)
                    pt.setModel(vea);

                    return;
                }
            }

            /*
               PluginServices.getMDIManager().setWaitCursor();
               try {
                   if (((FLyrVect) capa).getSource().getDriver().getClass() == DXFCadDriver.class) {
                       if (JOptionPane.showConfirmDialog(
                                   (Component) PluginServices.getMainFrame(),
                                   "Todas las geometrías del formato DXF no se pueden editar, de momento podemos editar: Line, Point, Polyline, Arc, Circle y Ellipse. \n El resto de geometrías se perderán con la edición. \n ¿Desea continuar?") == JOptionPane.YES_OPTION) {
                           capa.startEdition();
                           vista.getMapControl().setCadTool("selection");
                       } else {
                       }
                   } else {
                       capa.startEdition();
                       vista.getMapControl().setCadTool("selection");
                   }
               } catch (EditionException e) {
                   // TODO Auto-generated catch block
                   e.printStackTrace();
               }
               PluginServices.getMDIManager().restoreCursor();
             */
            // vista.getMapControl().drawMap(false);
        }
    }

    /**
     * @see com.iver.andami.plugins.Extension#isEnabled()
     */
    public boolean isEnabled() {
		View f = (View) PluginServices.getMDIManager().getActiveView();

		if (f == null) {
			return false;
		}

		FLayer[] selected = f.getModel().getMapContext().getLayers().getActives();
		if (selected.length == 1 && selected[0] instanceof FLyrVect){
			if (selected[0].isEditing())
				return false;
			else
				return true;
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
