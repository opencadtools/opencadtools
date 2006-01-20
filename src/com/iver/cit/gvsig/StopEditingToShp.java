package com.iver.cit.gvsig;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.FMap;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.VectorialAdapter;
import com.iver.cit.gvsig.gui.View;
import com.iver.cit.gvsig.project.ProjectView;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class StopEditingToShp implements Extension {
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

        View vista = (View) f;
        ProjectView model = vista.getModel();
        FMap mapa = model.getMapContext();
            FLayers layers = mapa.getLayers();
            if (s.equals("STOPEDITING")){
            for (int i = 0; i < layers.getLayersCount(); i++) {
                if (layers.getLayer(i) instanceof FLyrVect &&
                        layers.getLayer(i).isEditing()) {
                    FLyrVect lv = (FLyrVect) layers.getLayer(i);
                    stopEditing(lv);

                    return;
                }
            }
            }else if (s.equals("CANCELEDITING")){
            	for (int i = 0; i < layers.getLayersCount(); i++) {
                    if (layers.getLayer(i) instanceof FLyrVect &&
                            layers.getLayer(i).isEditing()) {
                        FLyrVect lv = (FLyrVect) layers.getLayer(i);
                        //stopEditing(lv);
                        VectorialEditableAdapter vea = (VectorialEditableAdapter) ((FLyrVect) layers.getLayer(i)).getSource();
                        lv.setSource(vea.getOriginalAdapter());
                        lv.setEditing(false);
                        return;
                    }
                }
            }
            PluginServices.getMainFrame().enableControls();
        /*          try {
           if (actionCommand.equals("STOP")){
                   File file = null;
                   if (((FLyrVect)capa).getFile()==null){
                   JFileChooser jfc = new JFileChooser();
                   jfc.addChoosableFileFilter(new GenericFileFilter("dxf",
                   PluginServices.getText(this, "DxfFiles")));
                   if (jfc.showSaveDialog((Component) PluginServices.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
                           file = jfc.getSelectedFile();
                           if (!(file.getPath().endsWith(".dxf") ||
                                           file.getPath().endsWith(".DXF"))) {
                                   file = new File(file.getPath() + ".dxf");
                           }
                           DxfWriter dxfwriter = new DxfWriter();
                           try {
                                   dxfwriter.write(new IGeometry[0], file);
                           } catch (Exception e3) {
                                   e3.printStackTrace();
                           }
                   }
                   }else{
                           file=((FLyrVect)capa).getFile();
                   }
                           capa.stopEdition(file);
                           vista.getMapControl().getMapControl().setTool("zoomIn");
                           vista.getMapControl().getMapControl().drawMap(false);
           }else if (actionCommand.equals("CANCELEDITION")){
                   capa.cancelEdition();
                   vista.getMapControl().getMapControl().setTool("zoomIn");
                   vista.getMapControl().getMapControl().drawMap(false);
           }else if (actionCommand.equals("SHOWGRID")){
                   vista.getMapControl().getCadToolAdapter().setGrid(true);
           }else if (actionCommand.equals("HIDEGRID")){
                   vista.getMapControl().getCadToolAdapter().setGrid(false);
           }else if (actionCommand.equals("SETADJUSTGRID")){
                   vista.getMapControl().getCadToolAdapter().setAdjustGrid(true);
           }else if (actionCommand.equals("NOTADJUSTGRID")){
                   vista.getMapControl().getCadToolAdapter().setAdjustGrid(false);
           }
           } catch (EditionException e) {
                   e.printStackTrace();
           }
           }*/
    }

    /**
     * @see com.iver.andami.plugins.Extension#isEnabled()
     */
    public boolean isEnabled() {
        return true;
    }

    /**
     * DOCUMENT ME!
     */
    public void stopEditing(FLyrVect layer) {
        try {
            JFileChooser jfc = new JFileChooser();

           // if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            /*   FLyrVect layer = (FLyrVect) test.createLayer("prueba",
                        (VectorialFileDriver) driverManager.getDriver(
                            "gvSIG shp driver"), original,
                        ProjectionPool.get("EPSG:23030"));
*/
            if (jfc.showSaveDialog((Component) PluginServices.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
        		    File newFile = jfc.getSelectedFile();

                    ShpWriter writer = new ShpWriter(newFile, layer);
                /*    writer.preProcess();

                    VectorialAdapter adapter = layer.getSource();

                    for (int i = 0; i < adapter.getShapeCount(); i++) {
                        IFeature feat = adapter.getFeature(i);
                        IRowEdited editFeat = new DefaultRowEdited(feat,
                                IRowEdited.STATUS_MODIFIED);
                        writer.process(editFeat);
                    }

                    writer.postProcess();
*/
                    VectorialEditableAdapter vea = (VectorialEditableAdapter) layer.getSource();
                    vea.stopEdition(writer);
                    layer.setSource(vea.getOriginalAdapter());
                    layer.setEditing(false);
                }
            //}
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            View vista = (View) f;
            ProjectView model = vista.getModel();
            FMap mapa = model.getMapContext();

            FLayers capas = mapa.getLayers();

            for (int i = 0; i < capas.getLayersCount(); i++) {
                if (capas.getLayer(i) instanceof FLyrVect &&
                        capas.getLayer(i).isEditing()) {
                    return true;
                }
            }

            return false;
        }

        return false;
    }
}
