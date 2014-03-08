package com.iver.cit.gvsig.export;

import java.sql.Types;

import org.apache.log4j.Logger;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.drivers.DBLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.IConnection;
import com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver;
import com.iver.cit.gvsig.fmap.drivers.db.utils.ConnectionWithParams;
import com.iver.cit.gvsig.fmap.drivers.jdbc.postgis.PostGISWriter;
import com.iver.cit.gvsig.fmap.drivers.jdbc.postgis.PostGisDriver;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.gui.cad.panels.ChooseSchemaAndTable;
import com.iver.utiles.PostProcessSupport;
import com.prodevelop.cit.gvsig.vectorialdb.wizard.ConnectionChooserPanel;

public class PostGISExporter extends AbstractLayerExporter {
    private static final Logger logger = Logger
	    .getLogger(PostGISExporter.class);

    private static final PostGISExporter instance = new PostGISExporter();

    private PostGISExporter() {
    }

    public static PostGISExporter getInstance() {
	return instance;
    }

    @Override
    public void export(MapContext mapContext, FLyrVect layer) {
	try {
	    ConnectionWithParams cwp = getConnectionWithParams();

	    if (cwp == null) {
		logger.error("Selected CWP is null (?) Export canceled.");
		return;
	    }

	    ChooseSchemaAndTable chooseSchemaAndTable = new ChooseSchemaAndTable(
		    cwp);
	    PluginServices.getMDIManager().addWindow(chooseSchemaAndTable);
	    if (!chooseSchemaAndTable.isOKPressed()) {
		return;
	    }

	    IConnection _conex = cwp.getConnection();

	    DBLayerDefinition originalDef = null;
	    if (layer.getSource().getDriver() instanceof IVectorialDatabaseDriver) {
		originalDef = ((IVectorialDatabaseDriver) layer.getSource()
			.getDriver()).getLyrDef();
	    }

	    DBLayerDefinition dbLayerDef = new DBLayerDefinition();
	    // Fjp:
	    // Cambio: En Postgis, el nombre de cat�logo est� siempre vac�o. Es
	    // algo heredado de Oracle, que no se usa.
	    // dbLayerDef.setCatalogName(cs.getDb());
	    dbLayerDef.setCatalogName("");

	    // A�adimos el schema dentro del layer definition para poder tenerlo
	    // en cuenta.
	    dbLayerDef.setSchema(chooseSchemaAndTable.getSchema());

	    dbLayerDef.setTableName(chooseSchemaAndTable.getTable());
	    dbLayerDef.setName(chooseSchemaAndTable.getTable());
	    dbLayerDef.setShapeType(layer.getShapeType());
	    SelectableDataSource sds = layer.getRecordset();

	    FieldDescription[] fieldsDescrip = sds.getFieldsDescription();
	    dbLayerDef.setFieldsDesc(fieldsDescrip);
	    // Creamos el driver. OJO: Hay que a�adir el campo ID a la
	    // definici�n de campos.

	    if (originalDef != null) {
		dbLayerDef.setFieldID(originalDef.getFieldID());
		dbLayerDef.setFieldGeometry(originalDef.getFieldGeometry());

	    } else {
		// Search for id field name
		int index = 0;
		String fieldName = "gid";
		while (findFileByName(fieldsDescrip, fieldName) != -1) {
		    index++;
		    fieldName = "gid" + index;
		}
		dbLayerDef.setFieldID(fieldName);

		// search for geom field name
		index = 0;
		fieldName = "the_geom";
		while (findFileByName(fieldsDescrip, fieldName) != -1) {
		    index++;
		    fieldName = "the_geom" + index;
		}
		dbLayerDef.setFieldGeometry(fieldName);

	    }

	    // if id field dosen't exist we add it
	    if (findFileByName(fieldsDescrip, dbLayerDef.getFieldID()) == -1) {
		int numFieldsAnt = fieldsDescrip.length;
		FieldDescription[] newFields = new FieldDescription[dbLayerDef
			.getFieldsDesc().length + 1];
		for (int i = 0; i < numFieldsAnt; i++) {
		    newFields[i] = fieldsDescrip[i];
		}
		newFields[numFieldsAnt] = new FieldDescription();
		newFields[numFieldsAnt].setFieldDecimalCount(0);
		newFields[numFieldsAnt].setFieldType(Types.INTEGER);
		newFields[numFieldsAnt].setFieldLength(7);
		newFields[numFieldsAnt].setFieldName(dbLayerDef.getFieldID());
		dbLayerDef.setFieldsDesc(newFields);

	    }

	    // all fields to lowerCase
	    FieldDescription field;
	    for (int i = 0; i < dbLayerDef.getFieldsDesc().length; i++) {
		field = dbLayerDef.getFieldsDesc()[i];
		field.setFieldName(field.getFieldName().toLowerCase());
	    }
	    dbLayerDef.setFieldID(dbLayerDef.getFieldID().toLowerCase());
	    dbLayerDef.setFieldGeometry(dbLayerDef.getFieldGeometry()
		    .toLowerCase());

	    dbLayerDef.setWhereClause("");
	    String strSRID = layer.getProjection().getAbrev();
	    dbLayerDef.setSRID_EPSG(strSRID);
	    dbLayerDef.setConnection(_conex);

	    PostGISWriter writer = (PostGISWriter) LayerFactory.getWM()
		    .getWriter("PostGIS Writer");
	    writer.setWriteAll(true);
	    writer.setCreateTable(true);
	    writer.initialize(dbLayerDef);
	    PostGisDriver postGISDriver = new PostGisDriver();
	    postGISDriver.setLyrDef(dbLayerDef);
	    postGISDriver.open();
	    PostProcessSupport.clearList();
	    Object[] params = new Object[2];
	    params[0] = _conex;
	    params[1] = dbLayerDef;
	    PostProcessSupport.addToPostProcess(postGISDriver, "setData",
		    params, 1);

	    writeFeatures(mapContext, layer, writer, postGISDriver);

	} catch (DriverLoadException e) {
	    NotificationManager.addError(e.getMessage(), e);
	} catch (InitializeWriterException e) {
	    NotificationManager.showMessageError(e.getMessage(), e);
	} catch (ReadDriverException e) {
	    NotificationManager.addError(e.getMessage(), e);
	}
    }

    private ConnectionWithParams getConnectionWithParams() {
	ConnectionChooserPanel ccp = new ConnectionChooserPanel(
		PostGisDriver.NAME);
	PluginServices.getMDIManager().addWindow(ccp);

	if (!ccp.isOkPressed()) {
	    return null;
	}
	return ccp.getSelectedCWP();
    }

    private int findFileByName(FieldDescription[] fields, String fieldName) {
	for (int i = 0; i < fields.length; i++) {
	    FieldDescription f = fields[i];
	    if (f.getFieldName().equalsIgnoreCase(fieldName)) {
		return i;
	    }
	}

	return -1;
    }
}
