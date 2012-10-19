package com.iver.cit.gvsig.gui.cad;

import java.awt.Component;
import java.io.File;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import jwizardcomponent.FinishAction;
import jwizardcomponent.JWizardComponents;

import org.cresques.cts.IProjection;

import com.hardcode.driverManager.Driver;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.CADExtension;
import com.iver.cit.gvsig.StartEditing;
import com.iver.cit.gvsig.exceptions.layers.StartEditionLayerException;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.ICanReproject;
import com.iver.cit.gvsig.fmap.crs.CRSFactory;
import com.iver.cit.gvsig.fmap.drivers.ConnectionFactory;
import com.iver.cit.gvsig.fmap.drivers.ConnectionJDBC;
import com.iver.cit.gvsig.fmap.drivers.DBLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.DXFLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.IConnection;
import com.iver.cit.gvsig.fmap.drivers.ITableDefinition;
import com.iver.cit.gvsig.fmap.drivers.IVectorialJDBCDriver;
import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver;
import com.iver.cit.gvsig.fmap.drivers.db.utils.ConnectionWithParams;
import com.iver.cit.gvsig.fmap.drivers.dbf.DbaseFile;
import com.iver.cit.gvsig.fmap.drivers.jdbc.postgis.PostGISWriter;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.edition.rules.IRule;
import com.iver.cit.gvsig.fmap.edition.rules.RulePolygon;
import com.iver.cit.gvsig.fmap.edition.writers.dxf.DxfFieldsMapping;
import com.iver.cit.gvsig.fmap.edition.writers.dxf.DxfWriter;
import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.gui.cad.panels.ChooseGeometryType;
import com.iver.cit.gvsig.gui.cad.panels.FileBasedPanel;
import com.iver.cit.gvsig.gui.cad.panels.JPanelFieldDefinition;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.prodevelop.cit.gvsig.vectorialdb.wizard.NewVectorDBConnectionPanel;

public class MyFinishAction extends FinishAction {
    private static Preferences prefs = Preferences.userRoot().node(
	    "gvSIG.encoding.dbf");
    JWizardComponents myWizardComponents;
    FinishAction oldAction;
    ITableDefinition lyrDef = null;
    View view;
    String actionComand;

    public MyFinishAction(JWizardComponents wizardComponents, View view,
	    String actionComand) {
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
	    // ChooseWriteDriver driverPanel = (ChooseWriteDriver)
	    // myWizardComponents.getWizardPanel(0);
	    mapCtrl.getMapContext().beginAtomicEvent();
	    if (actionComand.equals("SHP")) {
		FileBasedPanel shpPanel = (FileBasedPanel) myWizardComponents
			.getWizardPanel(2);
		String path = shpPanel.getPath();
		if (!path.toLowerCase().endsWith(".shp")) {
		    path += ".shp";
		}
		File newFile = new File(path);
		if (newFile.exists()) {
		    int resp = JOptionPane
			    .showConfirmDialog(
				    (Component) PluginServices.getMainFrame(),
				    PluginServices
					    .getText(this,
						    "fichero_ya_existe_seguro_desea_guardarlo"),
				    PluginServices.getText(this, "guardar"),
				    JOptionPane.YES_NO_OPTION);
		    if (resp != JOptionPane.YES_OPTION) {
			return;
		    }
		}
		ChooseGeometryType geometryTypePanel = (ChooseGeometryType) myWizardComponents
			.getWizardPanel(0);
		JPanelFieldDefinition fieldDefinitionPanel = (JPanelFieldDefinition) myWizardComponents
			.getWizardPanel(1);

		String layerName = geometryTypePanel.getLayerName();
		String selectedDriver = geometryTypePanel.getSelectedDriver();
		int geometryType = geometryTypePanel.getSelectedGeometryType();
		FieldDescription[] fieldsDesc = fieldDefinitionPanel
			.getFieldsDescription();

		Driver drv = LayerFactory.getDM().getDriver(selectedDriver);

		SHPLayerDefinition lyrDef = new SHPLayerDefinition();
		lyrDef.setFieldsDesc(fieldsDesc);
		lyrDef.setFile(newFile);
		lyrDef.setName(layerName);
		lyrDef.setShapeType(geometryType);
		ShpWriter writer = (ShpWriter) LayerFactory.getWM().getWriter(
			"Shape Writer");
		String charSetName = prefs.get("dbf_encoding", DbaseFile
			.getDefaultCharset().toString());
		writer.loadDbfEncoding(newFile.getAbsolutePath(),
			Charset.forName(charSetName));
		writer.setCharset(Charset.forName(charSetName));
		writer.setFile(newFile);
		writer.initialize(lyrDef);
		writer.preProcess();
		writer.postProcess();

		lyr = (FLyrVect) LayerFactory.createLayer(layerName,
			(VectorialFileDriver) drv, newFile,
			mapCtrl.getProjection());

	    } else if (actionComand.equals("DXF")) {
		FileBasedPanel dxfPanel = (FileBasedPanel) myWizardComponents
			.getWizardPanel(0);
		String path = dxfPanel.getPath();
		if (!path.toLowerCase().endsWith(".dxf")) {
		    path += ".dxf";
		}
		File newFile = new File(path);
		if (newFile.exists()) {
		    int resp = JOptionPane
			    .showConfirmDialog(
				    (Component) PluginServices.getMainFrame(),
				    PluginServices
					    .getText(this,
						    "fichero_ya_existe_seguro_desea_guardarlo"),
				    PluginServices.getText(this, "guardar"),
				    JOptionPane.YES_NO_OPTION);
		    if (resp != JOptionPane.YES_OPTION) {
			return;
		    }
		}
		DXFLayerDefinition lyrDef = new DXFLayerDefinition();
		lyrDef.setFile(newFile);
		String layerName = newFile.getName();
		lyrDef.setName(layerName);
		DxfWriter writer = (DxfWriter) LayerFactory.getWM().getWriter(
			"DXF Writer");
		writer.setFile(newFile);
		DxfFieldsMapping fieldsMapping = new DxfFieldsMapping();
		fieldsMapping.setLayerField("Layer");
		fieldsMapping.setColorField("Color");
		fieldsMapping.setElevationField("Elevation");
		fieldsMapping.setThicknessField("Thickness");
		fieldsMapping.setTextField("Text");
		fieldsMapping.setHeightText("HeightText");
		fieldsMapping.setRotationText("RotationText");
		writer.setFieldMapping(fieldsMapping);
		writer.setProjection(mapCtrl.getProjection());
		writer.initialize(lyrDef);
		writer.preProcess();
		writer.postProcess();
		Driver drv = LayerFactory.getDM().getDriver(
			"gvSIG DXF Memory Driver");

		lyr = (FLyrVect) LayerFactory.createLayer(layerName,
			(VectorialFileDriver) drv, newFile,
			mapCtrl.getProjection());

	    } else if (actionComand.equals("POSTGIS")) {
		ChooseGeometryType geometryTypePanel = (ChooseGeometryType) myWizardComponents
			.getWizardPanel(0);
		JPanelFieldDefinition fieldDefinitionPanel = (JPanelFieldDefinition) myWizardComponents
			.getWizardPanel(1);
		NewVectorDBConnectionPanel conn_pan = (NewVectorDBConnectionPanel) myWizardComponents
			.getWizardPanel(2);

		String _layerName = geometryTypePanel.getLayerName();
		String _tableName = conn_pan.getTableName();
		String selectedDriver = geometryTypePanel.getSelectedDriver();
		int geometryType = geometryTypePanel.getSelectedGeometryType();
		FieldDescription[] fieldsDesc = fieldDefinitionPanel
			.getFieldsDescription();

		Driver drv = LayerFactory.getDM().getDriver(selectedDriver);

		IVectorialJDBCDriver dbDriver = (IVectorialJDBCDriver) drv;
		NewVectorDBConnectionPanel postgisPanel = (NewVectorDBConnectionPanel) myWizardComponents
			.getWizardPanel(2);
		ConnectionWithParams cwp = postgisPanel
			.getConnectionWithParams();
		if (cwp == null) {
		    return;
		}

		IConnection conex = ConnectionFactory.createConnection(
			cwp.getConnectionStr(), cwp.getUser(), cwp.getPw());

		PostGISWriter writer = new PostGISWriter(); // (PostGISWriter)LayerFactory.getWM().getWriter("PostGIS Writer");
		if (!existTable(conex, cwp.getSchema(), _tableName)) {

		    DBLayerDefinition dbLayerDef = new DBLayerDefinition();
		    dbLayerDef.setCatalogName(cwp.getDb());
		    dbLayerDef.setSchema(cwp.getSchema());
		    dbLayerDef.setTableName(_tableName);
		    dbLayerDef.setShapeType(geometryType);
		    dbLayerDef.setFieldsDesc(fieldsDesc);
		    dbLayerDef.setFieldGeometry("the_geom");

		    // create gid & add it to FieldDescription array
		    dbLayerDef.setNewFieldID();

		    dbLayerDef.setWhereClause("");
		    String strSRID = mapCtrl.getProjection().getAbrev()
			    .substring(5);
		    dbLayerDef.setSRID_EPSG(strSRID);
		    dbLayerDef.setConnection(conex);

		    writer.setWriteAll(true);
		    writer.setCreateTable(true);
		    writer.initialize(dbLayerDef);

		    // Creamos la tabla.
		    writer.preProcess();
		    writer.postProcess();

		    if (dbDriver instanceof ICanReproject) {
			((ICanReproject) dbDriver).setDestProjection(strSRID);
		    }

		    dbDriver.setData(conex, dbLayerDef);
		    IProjection proj = null;
		    if (drv instanceof ICanReproject) {
			proj = CRSFactory.getCRS("EPSG:"
				+ ((ICanReproject) dbDriver)
					.getSourceProjection(null, null));
		    }

		    lyr = (FLyrVect) LayerFactory.createDBLayer(dbDriver,
			    _layerName, proj);
		    // postgisPanel.saveConnectionSettings();

		} else {
		    JOptionPane.showMessageDialog(null, PluginServices.getText(
			    this, "table_already_exists_in_database"),
			    PluginServices.getText(this, "warning_title"),
			    JOptionPane.WARNING_MESSAGE);
		    return;
		}

	    } else // Si no es ni lo uno ni lo otro,
	    {

	    }
	} catch (Exception e) {
	    NotificationManager.showMessageError(e.getLocalizedMessage(), e);
	    return;
	}
	lyr.setVisible(true);

	mapCtrl.getMapContext().getLayers().addLayer(lyr);

	mapCtrl.getMapContext().endAtomicEvent();
	lyr.addLayerListener(CADExtension.getEditionManager());
	lyr.setActive(true);

	try {
	    lyr.setEditing(true);
	    VectorialEditableAdapter vea = (VectorialEditableAdapter) lyr
		    .getSource();
	    vea.getRules().clear();
	    // TODO: ESTO ES PROVISIONAL, DESCOMENTAR LUEGO
	    if (vea.getShapeType() == FShape.POLYGON) {
		IRule rulePol = new RulePolygon();
		vea.getRules().add(rulePol);
	    }
	    StartEditing.startCommandsApplicable(view, lyr);
	    vea.getCommandRecord().addCommandListener(mapCtrl);
	    view.showConsole();

	    // Para cerrar el cuadro de di�logo.
	    oldAction.performAction();
	} catch (ReadDriverException e) {
	    NotificationManager.addError(e);
	} catch (StartEditionLayerException e) {
	    NotificationManager.addError(e);
	}

    }

    private boolean existTable(IConnection conex, String schema,
	    String tableName) throws SQLException {

	Statement st = null;
	boolean exists = false;

	if (schema == null || schema.equals("")) {
	    schema = " current_schema()::Varchar ";
	} else {
	    schema = "'" + schema + "'";
	}

	String sql = "select relname,nspname "
		+ "from pg_class inner join pg_namespace "
		+ "on relnamespace = pg_namespace.oid where "
		+ " relkind = 'r' and relname = '" + tableName
		+ "' and nspname = " + schema;

	st = ((ConnectionJDBC) conex).getConnection().createStatement();
	ResultSet rs = st.executeQuery(sql);
	if (rs.next()) {
	    exists = true;
	}
	rs.close();
	st.close();

	return exists;
    }

}

// [eiel-gestion-conexiones]

