package com.iver.cit.gvsig;

import java.awt.Component;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.DriverException;
import com.iver.cit.gvsig.fmap.FMap;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.FShape;
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
import com.iver.cit.gvsig.fmap.edition.writers.dxf.DxfFieldsMapping;
import com.iver.cit.gvsig.fmap.edition.writers.dxf.DxfWriter;
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

public class ExportTo extends Extension {

	private class WriterTask extends AbstractMonitorableTask
	{
		FLyrVect lyrVect;
		IWriter writer;
		int rowCount;
		ReadableVectorial va;
		SelectableDataSource sds;
		FBitSet bitSet;
		public WriterTask(FLyrVect lyr, IWriter writer) throws DriverException, DriverIOException
		{
			this.lyrVect = lyr;
			this.writer = writer;
			
			setInitialStep(0);
			setDeterminatedProcess(true);
			setStatusMessage(PluginServices.getText(this, "exportando_features"));
			
			va = lyrVect.getSource();
			sds = lyrVect.getRecordset();

			bitSet = sds.getSelection();

			if (bitSet.cardinality() == 0)
				rowCount = va.getShapeCount();
			else
				rowCount = bitSet.cardinality();

			setFinalStep(rowCount);
			
		}
		public void run() throws Exception {

			// Creamos la tabla.
			writer.preProcess();

			if (bitSet.cardinality() == 0) {
				rowCount = va.getShapeCount();
				for (int i = 0; i < rowCount; i++) {
					IGeometry geom = va.getShape(i);

					reportStep();
					setNote(PluginServices.getText(this, "exporting_") + i);
					if (isCanceled())
						break;

					if (geom != null) {
						Value[] values = sds.getRow(i);
						IFeature feat = new DefaultFeature(geom, values, "" + i);
						DefaultRowEdited edRow = new DefaultRowEdited(feat,
								DefaultRowEdited.STATUS_ADDED, i);
						writer.process(edRow);
					}
				}
			} else {
				int counter = 0;
				for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet
						.nextSetBit(i + 1)) {
					IGeometry geom = va.getShape(i);

					reportStep();
					setNote(PluginServices.getText(this, "exporting_") + counter);
					if (isCanceled())
						break;

					if (geom != null) {
						Value[] values = sds.getRow(i);
						IFeature feat = new DefaultFeature(geom, values, "" + i);
						DefaultRowEdited edRow = new DefaultRowEdited(feat,
								DefaultRowEdited.STATUS_ADDED, i);

						writer.process(edRow);
					}
				}

			}

			writer.postProcess();
						
			JOptionPane.showMessageDialog(
					(JComponent) PluginServices.getMDIManager().getActiveView()
					, PluginServices.getText(this, "capa_exportada"), "Export",
					JOptionPane.INFORMATION_MESSAGE);

		}
		
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#initialize()
	 */
	public void initialize() {
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
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
						int numSelec = lv.getRecordset().getSelection()
								.cardinality();
						if (numSelec > 0) {
							int resp = JOptionPane.showConfirmDialog(
									(JComponent) PluginServices.getMDIManager().getActiveView(),
									"se_van_a_guardar_" + numSelec
											+ " features_desea_continuar",
									"Export", JOptionPane.YES_NO_OPTION);
							if (resp == JOptionPane.NO_OPTION) {
								continue;
							}
						} // if numSelec > 0
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
			} catch (DriverException e) {
				e.printStackTrace();
				NotificationManager.addError(e.getMessage(), e);
			} catch (DriverIOException e) {
				e.printStackTrace();
				NotificationManager.addError(e.getMessage(), e);
			}

		}
	}

	public void saveToPostGIS(FLyrVect layer) throws EditionException, DriverIOException {
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
		} catch (com.hardcode.gdbms.engine.data.driver.DriverException e) {
			e.printStackTrace();
			throw new EditionException(e);
		}

	}
	
	private void writeFeatures(FLyrVect layer, IWriter writer) throws DriverException, DriverIOException
	{
		PluginServices.cancelableBackgroundExecution(new WriterTask(layer, writer));
	}

	/**
	 * @param layer
	 *            FLyrVect to obtain features. If selection, only selected
	 *            features will be precessed.
	 * @param writer
	 *            (Must be already initialized)
	 * @throws EditionException
	 * @throws DriverException
	 * @throws DriverIOException
	 * @throws com.hardcode.gdbms.engine.data.driver.DriverException
	 */
	public void writeFeaturesNoThread(FLyrVect layer, IWriter writer)
			throws EditionException, DriverException, DriverIOException,
			com.hardcode.gdbms.engine.data.driver.DriverException {
		ReadableVectorial va = layer.getSource();
		SelectableDataSource sds = layer.getRecordset();

		// Creamos la tabla.
		writer.preProcess();

		int rowCount;
		FBitSet bitSet = layer.getRecordset().getSelection();

		if (bitSet.cardinality() == 0)
			rowCount = va.getShapeCount();
		else
			rowCount = bitSet.cardinality();

		ProgressMonitor progress = new ProgressMonitor(
				(JComponent) PluginServices.getMDIManager().getActiveView(),
				PluginServices.getText(this, "exportando_features"),
				PluginServices.getText(this, "exportando_features"), 0,
				rowCount);

		progress.setMillisToDecideToPopup(200);
		progress.setMillisToPopup(500);

		if (bitSet.cardinality() == 0) {
			rowCount = va.getShapeCount();
			for (int i = 0; i < rowCount; i++) {
				IGeometry geom = va.getShape(i);

				progress.setProgress(i);
				if (progress.isCanceled())
					break;

				if (geom != null) {
					Value[] values = sds.getRow(i);
					IFeature feat = new DefaultFeature(geom, values, "" + i);
					DefaultRowEdited edRow = new DefaultRowEdited(feat,
							DefaultRowEdited.STATUS_ADDED, i);
					writer.process(edRow);
				}
			}
		} else {
			int counter = 0;
			for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet
					.nextSetBit(i + 1)) {
				IGeometry geom = va.getShape(i);

				progress.setProgress(counter++);
				if (progress.isCanceled())
					break;

				if (geom != null) {
					Value[] values = sds.getRow(i);
					IFeature feat = new DefaultFeature(geom, values, "" + i);
					DefaultRowEdited edRow = new DefaultRowEdited(feat,
							DefaultRowEdited.STATUS_ADDED, i);

					writer.process(edRow);
				}
			}

		}

		writer.postProcess();
		progress.close();
	}

	public void saveToDxf(FLyrVect layer) throws EditionException, DriverIOException {
		try {
			JFileChooser jfc = new JFileChooser();
			SimpleFileFilter filterShp = new SimpleFileFilter("dxf",
					PluginServices.getText(this, "dxf_files"));
			jfc.setFileFilter(filterShp);
			if (jfc.showSaveDialog((Component) PluginServices.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
				File newFile = jfc.getSelectedFile();
				String path = newFile.getAbsolutePath();
				if (!(path.toLowerCase().endsWith(".dxf"))) {
					path = path + ".dxf";
				}
				newFile = new File(path);

				DxfWriter writer = (DxfWriter) LayerFactory.getWM().getWriter(
						"DXF Writer");
				SHPLayerDefinition lyrDef = new SHPLayerDefinition();
				SelectableDataSource sds = layer.getRecordset();
				FieldDescription[] fieldsDescrip = sds.getFieldsDescription();
				lyrDef.setFieldsDesc(fieldsDescrip);
				lyrDef.setFile(newFile);
				lyrDef.setName(newFile.getName());
				lyrDef.setShapeType(layer.getShapeType());
				writer.setFile(newFile);
				writer.initialize(lyrDef);
				writer.setProjection(layer.getProjection());
				DxfFieldsMapping fieldsMapping = new DxfFieldsMapping();
				// TODO: Recuperar aquí los campos del cuadro de diálogo.
				writer.setFieldMapping(fieldsMapping);

				writeFeatures(layer, writer);
			}

		} catch (DriverException e) {
			e.printStackTrace();
			throw new EditionException(e);
		} catch (com.hardcode.gdbms.engine.data.driver.DriverException e) {
			e.printStackTrace();
			throw new EditionException(e);
		}

	}

	public void saveToShp(FLyrVect layer) throws EditionException, DriverIOException {
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
				if (layer.getShapeType() == FShape.MULTI) // Exportamos a 3
				// ficheros
				{
					// puntos
					String aux = path.replaceFirst(".shp", "_points.shp");
					File filePoints = new File(aux);
					lyrDef.setFile(filePoints);
					lyrDef.setName(filePoints.getName());
					lyrDef.setShapeType(FShape.POINT);
					writer.setFile(filePoints);
					lyrDef.setFile(filePoints);
					writer.initialize(lyrDef);
					writeFeatures(layer, writer);

					// Lineas
					aux = path.replaceFirst(".shp", "_line.shp");
					File fileLines = new File(aux);
					lyrDef.setFile(fileLines);
					lyrDef.setName(fileLines.getName());
					lyrDef.setShapeType(FShape.LINE);
					writer.setFile(fileLines);
					lyrDef.setFile(fileLines);
					writer.initialize(lyrDef);
					writeFeatures(layer, writer);

					// Polígonos
					aux = path.replaceFirst(".shp", "_polygons.shp");
					File filePolygons = new File(aux);
					lyrDef.setFile(filePolygons);
					lyrDef.setName(filePolygons.getName());
					lyrDef.setShapeType(FShape.POLYGON);
					writer.setFile(filePolygons);
					lyrDef.setFile(filePolygons);
					writer.initialize(lyrDef);
					writeFeatures(layer, writer);
				} else {
					lyrDef.setFile(newFile);
					lyrDef.setName(newFile.getName());
					lyrDef.setShapeType(layer.getShapeType());
					writer.setFile(newFile);
					writer.initialize(lyrDef);

					writeFeatures(layer, writer);

				}
			}
		} catch (DriverException e) {
			e.printStackTrace();
			throw new EditionException(e);
		} catch (com.hardcode.gdbms.engine.data.driver.DriverException e) {
			e.printStackTrace();
			throw new EditionException(e);
		}

	}

	/**
	 * @see com.iver.andami.plugins.IExtension#isEnabled()
	 */
	public boolean isEnabled() {
		View f = (View) PluginServices.getMDIManager().getActiveView();

		if (f == null) {
			return false;
		} else
			return true;
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#isVisible()
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
