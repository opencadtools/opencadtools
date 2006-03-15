package com.iver.cit.gvsig.gui.cad;

import java.io.File;

import jwizardcomponent.FinishAction;
import jwizardcomponent.JWizardComponents;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.CADExtension;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.ITableDefinition;
import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.VectorialDatabaseDriver;
import com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver;
import com.iver.cit.gvsig.fmap.edition.ISpatialWriter;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.gui.cad.panels.ChooseGeometryType;
import com.iver.cit.gvsig.gui.cad.panels.JPanelFieldDefinition;
import com.iver.cit.gvsig.gui.cad.panels.ShpPanel;
import com.iver.cit.gvsig.project.ProjectTable;

public class MyFinishAction extends FinishAction
{
	JWizardComponents myWizardComponents;
	FinishAction oldAction;
	ITableDefinition lyrDef = null;
	MapControl mapCtrl;
	String actionComand;
	public MyFinishAction(JWizardComponents wizardComponents, MapControl mapCtrl, String actionComand) {		
		super(wizardComponents);
		oldAction = wizardComponents.getFinishAction();
		myWizardComponents = wizardComponents;
		this.mapCtrl = mapCtrl;
		this.actionComand = actionComand;
		// TODO Auto-generated constructor stub
	}

	public void performAction() {
		// TODO Auto-generated method stub
		try {
			// ChooseWriteDriver driverPanel = (ChooseWriteDriver) myWizardComponents.getWizardPanel(0);
			ChooseGeometryType geometryTypePanel = (ChooseGeometryType) myWizardComponents.getWizardPanel(0);
			JPanelFieldDefinition fieldDefinitionPanel = (JPanelFieldDefinition) myWizardComponents.getWizardPanel(1);
			
			 
			String layerName = geometryTypePanel.getLayerName();
			String selectedDriver = geometryTypePanel.getSelectedDriver();
			int geometryType = geometryTypePanel.getSelectedGeometryType();
			FieldDescription[] fieldsDesc = fieldDefinitionPanel.getFieldsDescription();
			
			ISpatialWriter drv = (ISpatialWriter) LayerFactory.getDM().getDriver(selectedDriver);
			if (actionComand.equals("SHP"))
			{
	    		FLyrVect lyr = null;
	    		ShpPanel shpPanel = (ShpPanel) myWizardComponents.getWizardPanel(2);
    		    File newFile = new File(shpPanel.getPath());
    		    SHPLayerDefinition lyrDef = new SHPLayerDefinition();
    		    lyrDef.setFieldsDesc(fieldsDesc);
    		    lyrDef.setFile(newFile);
    		    lyrDef.setName(layerName);
    		    lyrDef.setShapeType(geometryType);
    			ShpWriter writer= (ShpWriter)LayerFactory.getWM().getWriter("Shape Writer");
    			writer.setFile(newFile);
    			writer.initialize(lyrDef);
    			writer.preProcess();
    			writer.postProcess();
	    		
				mapCtrl.getMapContext().beginAtomicEvent();
                lyr = (FLyrVect) LayerFactory.createLayer(layerName,
                        (VectorialFileDriver) drv, newFile, mapCtrl.getProjection());
                                
                lyr.setVisible(true);
				mapCtrl.getMapContext().getLayers().addLayer(lyr);
				
				mapCtrl.getMapContext().endAtomicEvent();
				lyr.setEditing(true);
                VectorialEditableAdapter vea = (VectorialEditableAdapter) lyr.getSource();
                // TODO: Provisional, para que al poner
                // un tema en edición el CADToolAdapter se entere
                CADExtension.getCADToolAdapter().setVectorialAdapter(vea);
                vea.getCommandRecord().addCommandListener(mapCtrl);
                //Si existe una tabla asociada a esta capa se cambia su modelo por el VectorialEditableAdapter.
                ProjectExtension pe=(ProjectExtension)PluginServices.getExtension(ProjectExtension.class);
                ProjectTable pt=pe.getProject().getTable(lyr);
                if (pt!=null)
                pt.setModel(vea);

				
	            
			}
			else if (drv instanceof VectorialDatabaseDriver)
			{
				// ConnectionPanel connectionPanel = (ConnectionPanel) myWizardComponents.getWizardPanel(3);
				/* ConnectionSettings cs = dlg.getConnSettings();
				if (cs == null)
					return;
				conex = DriverManager.getConnection(cs.getConnectionString(),
						cs.getUser(), cs.getPassw());

				st = conex.createStatement();

				dbLayerDef.setCatalogName(cs.getDb());
				dbLayerDef.setTableName(tableName);
				String strGeometryFieldType = "GEOMETRY";

				switch (lyrVect.getShapeType()) {
				case FShape.POINT:
					strGeometryFieldType = XTypes
							.fieldTypeToString(XTypes.POINT2D);
					break;
				case FShape.LINE:
					strGeometryFieldType = XTypes
							.fieldTypeToString(XTypes.LINE2D);
					break;
				case FShape.POLYGON:
					strGeometryFieldType = XTypes
							.fieldTypeToString(XTypes.POLYGON2D);
					break;
				case FShape.MULTI:
					strGeometryFieldType = XTypes
							.fieldTypeToString(XTypes.MULTI2D);
					break;
				}

				dbLayerDef.setFieldGeometry("the_geom");
				FieldDescription[] fieldsDescrip = new FieldDescription[rsSel
						.getFieldNames().length];
				dbLayerDef.setFieldNames(rsSel.getFieldNames());
				for (int i = 0; i < rsSel.getFieldNames().length; i++) {
					fieldsDescrip[i] = new FieldDescription();
					fieldsDescrip[i].setFieldType(rsSel.getFieldType(i));
					fieldsDescrip[i].setFieldName(rsSel.getFieldName(i));
					// TODO: Por ahora le ponemos 200, a falta
					// de recompilar GDBMS con la posibilidad
					// de obtener el ancho de un campo.
					fieldsDescrip[i].setFieldLength(200);
					/*
					 * if (fieldsDescrip[i].getFieldName().equals("gid")) { int
					 * resp = JOptionPane.showConfirmDialog(null,
					 * PluginServices.getText(this, "confirm_gid"), "Field GID",
					 * JOptionPane.YES_NO_OPTION); if (resp ==
					 * JOptionPane.NO_OPTION) return; else { // Quitamos el GID
					 * original, y lo sustituiremos por el nuestro } }
					 
				}
				String strSRID = lyrVect.getProjection().getAbrev()
						.substring(5);
				dbLayerDef.setSRID_EPSG(strSRID); */
				
			}
			else // Si no es ni lo uno ni lo otro, 
			{
				
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Para cerrar el cuadro de diálogo.
		oldAction.performAction();
		
	}
	  
}

