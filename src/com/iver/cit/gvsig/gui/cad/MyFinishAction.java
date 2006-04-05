package com.iver.cit.gvsig.gui.cad;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import jwizardcomponent.FinishAction;
import jwizardcomponent.JWizardComponents;

import org.cresques.cts.IProjection;
import org.cresques.cts.ProjectionPool;

import com.hardcode.driverManager.Driver;
import com.iver.cit.gvsig.CADExtension;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.core.ICanReproject;
import com.iver.cit.gvsig.fmap.drivers.DBLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.ITableDefinition;
import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver;
import com.iver.cit.gvsig.fmap.drivers.VectorialJDBCDriver;
import com.iver.cit.gvsig.fmap.drivers.jdbc.postgis.PostGISWriter;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.gui.View;
import com.iver.cit.gvsig.gui.cad.panels.ChooseGeometryType;
import com.iver.cit.gvsig.gui.cad.panels.JPanelFieldDefinition;
import com.iver.cit.gvsig.gui.cad.panels.PostGISpanel;
import com.iver.cit.gvsig.gui.cad.panels.ShpPanel;
import com.iver.cit.gvsig.jdbc_spatial.gui.jdbcwizard.ConnectionSettings;

public class MyFinishAction extends FinishAction
{
	JWizardComponents myWizardComponents;
	FinishAction oldAction;
	ITableDefinition lyrDef = null;
	View view;
	String actionComand;
	public MyFinishAction(JWizardComponents wizardComponents, View view, String actionComand) {		
		super(wizardComponents);
		oldAction = wizardComponents.getFinishAction();
		myWizardComponents = wizardComponents;
		this.view = view;
		this.actionComand = actionComand;
		// TODO Auto-generated constructor stub
	}

	public void performAction() {
		FLyrVect lyr = null;
		MapControl mapCtrl = view.getMapControl();
		try {			
			// ChooseWriteDriver driverPanel = (ChooseWriteDriver) myWizardComponents.getWizardPanel(0);
			ChooseGeometryType geometryTypePanel = (ChooseGeometryType) myWizardComponents.getWizardPanel(0);
			JPanelFieldDefinition fieldDefinitionPanel = (JPanelFieldDefinition) myWizardComponents.getWizardPanel(1);
			
			 
			String layerName = geometryTypePanel.getLayerName();
			String selectedDriver = geometryTypePanel.getSelectedDriver();
			int geometryType = geometryTypePanel.getSelectedGeometryType();
			FieldDescription[] fieldsDesc = fieldDefinitionPanel.getFieldsDescription();
			
			Driver drv = LayerFactory.getDM().getDriver(selectedDriver);    		
			mapCtrl.getMapContext().beginAtomicEvent();
			if (actionComand.equals("SHP"))
			{
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
	    		
				
                lyr = (FLyrVect) LayerFactory.createLayer(layerName,
                        (VectorialFileDriver) drv, newFile, mapCtrl.getProjection());
                                
			}
			else if (drv instanceof VectorialJDBCDriver)
			{
				VectorialJDBCDriver dbDriver = (VectorialJDBCDriver) drv;
	    		PostGISpanel postgisPanel = (PostGISpanel) myWizardComponents.getWizardPanel(2);
				ConnectionSettings cs = postgisPanel.getConnSettings();
				if (cs == null)
					return;
				Connection conex = DriverManager.getConnection(cs.getConnectionString(),
						cs.getUser(), cs.getPassw());

				Statement st = conex.createStatement();

				DBLayerDefinition dbLayerDef = new DBLayerDefinition();
				dbLayerDef.setCatalogName(cs.getDb());
				dbLayerDef.setTableName(layerName);
				dbLayerDef.setShapeType(geometryType);
				dbLayerDef.setFieldGeometry("the_geom");
				dbLayerDef.setFieldID("gid");
				dbLayerDef.setFieldsDesc(fieldsDesc);
				dbLayerDef.setWhereClause("");
				String strSRID = mapCtrl.getProjection().getAbrev()
						.substring(5);
				dbLayerDef.setSRID_EPSG(strSRID); 
				dbLayerDef.setConnection(conex);

    			PostGISWriter writer= new PostGISWriter(); //(PostGISWriter)LayerFactory.getWM().getWriter("PostGIS Writer");
    			writer.setWriteAll(true);
    			writer.setCreateTable(true);
    			writer.initialize(dbLayerDef);

    			writer.preProcess();
    			writer.postProcess();
	    		
    	        if (dbDriver instanceof ICanReproject)
    	        {                    
    	            ((ICanReproject)dbDriver).setDestProjection(strSRID);
    	        }
    	        dbDriver.setData(conex, dbLayerDef);
    	        IProjection proj = null; 
    	        if (drv instanceof ICanReproject)
    	        {                                        
    	            proj = ProjectionPool.get("EPSG:" + ((ICanReproject)dbDriver).getSourceProjection()); 
    	        }
				
    			lyr = (FLyrVect) LayerFactory.createDBLayer(dbDriver, layerName, proj);
				
			}
			else // Si no es ni lo uno ni lo otro, 
			{
				
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        lyr.setVisible(true);
		mapCtrl.getMapContext().getLayers().addLayer(lyr);
		
		mapCtrl.getMapContext().endAtomicEvent();
		lyr.addLayerListener(CADExtension.getEditionManager());
		lyr.setActive(true);
		lyr.setEditing(true);
        VectorialEditableAdapter vea = (VectorialEditableAdapter) lyr.getSource();
        // TODO: Provisional, para que al poner
        // un tema en edición el CADToolAdapter se entere
        CADExtension.getCADToolAdapter().setVectorialAdapter(vea);
        vea.getCommandRecord().addCommandListener(mapCtrl);
        view.showConsole();
		
		// Para cerrar el cuadro de diálogo.
		oldAction.performAction();
		
	}
	  
}

