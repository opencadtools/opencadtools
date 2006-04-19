package com.iver.cit.gvsig;

import java.awt.Component;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.DriverException;
import com.iver.cit.gvsig.fmap.FMap;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.DBLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.jdbc.postgis.PostGISWriter;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.EditionException;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.gui.View;
import com.iver.cit.gvsig.jdbc_spatial.DlgConnection;
import com.iver.cit.gvsig.jdbc_spatial.gui.jdbcwizard.ConnectionSettings;
import com.iver.cit.gvsig.project.ProjectView;
import com.iver.utiles.SimpleFileFilter;

public class SaveAs implements Extension {

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
			ProjectView model = vista.getModel();
			FMap mapa = model.getMapContext();
			FLayers layers = mapa.getLayers();
			FLayer[] actives = layers.getActives();
			try {
				// NOTA: SI HAY UNA SELECCIÓN, SOLO SE SALVAN LOS SELECCIONADOS
				for (int i = 0; i < actives.length; i++) {
					if (actives[i] instanceof FLyrVect) {
						FLyrVect lv = (FLyrVect) actives[i];

						if (actionCommand.equals("SHP")) {

							saveToShp(lv);
						}
						if (actionCommand.equals("DXF")) {
							saveToDxf(lv);
						}
						if (actionCommand.equals("POSTGIS")) {
							saveToPostGIS(lv);
						}
					} // actives[i]
				} // for
			} catch (EditionException e) {
				e.printStackTrace();
				NotificationManager.addError(e.getMessage(), e);
			}

		}
	}

	public void saveToPostGIS(FLyrVect layer) throws EditionException {
		try {
			String tableName = JOptionPane.showInputDialog(PluginServices
					.getText(this, "intro_tablename"));
			if (tableName == null)
				return;
			DlgConnection dlg = new DlgConnection();
			dlg.setModal(true);
			dlg.setVisible(true);
			ConnectionSettings cs = dlg.getConnSettings();
			if (cs == null)
				return;
			Connection conex = DriverManager.getConnection(cs
					.getConnectionString(), cs.getUser(), cs.getPassw());

			DBLayerDefinition dbLayerDef = new DBLayerDefinition();
			dbLayerDef.setCatalogName(cs.getDb());
			dbLayerDef.setTableName(tableName);
			dbLayerDef.setShapeType(layer.getShapeType());
		    SelectableDataSource sds = layer.getRecordset();
		    FieldDescription[] fieldsDescrip = sds.getFieldsDescription();
			dbLayerDef.setFieldsDesc(fieldsDescrip);
			dbLayerDef.setFieldGeometry("the_geom");
			dbLayerDef.setFieldID("gid");

			dbLayerDef.setWhereClause("");
			String strSRID = layer.getProjection().getAbrev().substring(5);
			dbLayerDef.setSRID_EPSG(strSRID);
			dbLayerDef.setConnection(conex);

			PostGISWriter writer = new PostGISWriter(); // (PostGISWriter)LayerFactory.getWM().getWriter("PostGIS
														// Writer");
			writer.setWriteAll(true);
			writer.setCreateTable(true);
			writer.initialize(dbLayerDef);
			writeFeatures(layer, writer);

		} catch (DriverException e) {
			e.printStackTrace();
			throw new EditionException(e);
		} catch (DriverLoadException e) {
			throw new EditionException(e);
		} catch (SQLException e) {
			throw new EditionException(e);
		} catch (DriverIOException e) {
			throw new EditionException(e);
		} catch (com.hardcode.gdbms.engine.data.driver.DriverException e) {
			e.printStackTrace();
			throw new EditionException(e);
		}

	}

	/**
	 * @param layer FLyrVect to obtain features. If selection, only selected features will be precessed.
	 * @param writer (Must be already initialized)
	 * @throws EditionException
	 * @throws DriverException
	 * @throws DriverIOException
	 * @throws com.hardcode.gdbms.engine.data.driver.DriverException 
	 */
	public void writeFeatures(FLyrVect layer, IWriter writer) throws EditionException, DriverException, DriverIOException, com.hardcode.gdbms.engine.data.driver.DriverException {
		ReadableVectorial va = layer.getSource();
		SelectableDataSource sds = layer.getRecordset();
		
		// Creamos la tabla.
		writer.preProcess();

		int rowCount;
		FBitSet bitSet = layer.getRecordset().getSelection(); 
		if (bitSet.cardinality() == 0)
		{
			rowCount = va.getShapeCount();
			for (int i = 0; i < rowCount; i++) {
				IGeometry geom = va.getShape(i);

				if (geom != null) {
					Value[] values = sds.getRow(i);
					IFeature feat = new DefaultFeature(geom, values, ""+i);
					DefaultRowEdited edRow = new DefaultRowEdited(feat,
							DefaultRowEdited.STATUS_ADDED, i);
					writer.process(edRow);
				}
			}
		}
		else
		{
			for(int i=bitSet.nextSetBit(0); i>=0; i=bitSet.nextSetBit(i+1)) {
				IGeometry geom = va.getShape(i);

				if (geom != null) {
					Value[] values = sds.getRow(i);
					IFeature feat = new DefaultFeature(geom, values, ""+i);
					DefaultRowEdited edRow = new DefaultRowEdited(feat,
							DefaultRowEdited.STATUS_ADDED, i);

					writer.process(edRow);
				}
			}
			
		}

		writer.postProcess();
	}

	public void saveToDxf(FLyrVect lv) {
		// TODO Auto-generated method stub
		System.err.println("Not implemented yet");

	}

	public void saveToShp(FLyrVect layer) throws EditionException {
		try {
			JFileChooser jfc = new JFileChooser();
			SimpleFileFilter filterShp = new SimpleFileFilter("shp",
					PluginServices.getText(this, "shp_files"));
			jfc.setFileFilter(filterShp);
			if (jfc.showSaveDialog((Component) PluginServices.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
				File newFile = jfc.getSelectedFile();
				String path = newFile.getAbsolutePath();
				if (!(path.toLowerCase().endsWith(".shp"))) {
					path = path + ".shp";
				}
				newFile = new File(path);

				ShpWriter writer = (ShpWriter) LayerFactory.getWM().getWriter(
						"Shape Writer");
    		    SHPLayerDefinition lyrDef = new SHPLayerDefinition();
    		    SelectableDataSource sds = layer.getRecordset();
    		    FieldDescription[] fieldsDescrip = sds.getFieldsDescription();
    		    lyrDef.setFieldsDesc(fieldsDescrip);
    		    lyrDef.setFile(newFile);
    		    lyrDef.setName(newFile.getName());
    		    lyrDef.setShapeType(layer.getShapeType());
    			writer.setFile(newFile);
    			writer.initialize(lyrDef);
				
				writeFeatures(layer, writer);

			}
		} catch (DriverIOException e) {
			e.printStackTrace();
			throw new EditionException(e);
		} catch (DriverException e) {
			e.printStackTrace();
			throw new EditionException(e);
		} catch (com.hardcode.gdbms.engine.data.driver.DriverException e) {
			e.printStackTrace();
			throw new EditionException(e);
		}

	}

	/**
	 * @see com.iver.andami.plugins.Extension#isEnabled()
	 */
	public boolean isEnabled() {
		View f = (View) PluginServices.getMDIManager().getActiveView();

		if (f == null) {
			return false;
		} else
			return true;
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
