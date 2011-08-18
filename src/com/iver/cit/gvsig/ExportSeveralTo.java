/*
 * Copyright 2008 Deputación Provincial de A Coruña
 * Copyright 2009 Deputación Provincial de Pontevedra
 * Copyright 2010 CartoLab, Universidad de A Coruña
 *
 * This file is part of openCADTools, developed by the Cartography
 * Engineering Laboratory of the University of A Coruña (CartoLab).
 * http://www.cartolab.es
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 */

package com.iver.cit.gvsig;

import java.awt.Component;
import java.awt.geom.Point2D;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.cresques.cts.ICoordTrans;

import com.hardcode.driverManager.Driver;
import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.driver.exceptions.OpenDriverException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.v02.FLabel;
import com.iver.cit.gvsig.fmap.drivers.DriverAttributes;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.ILayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.VectorialDriver;
import com.iver.cit.gvsig.fmap.drivers.dbf.DbaseFile;
import com.iver.cit.gvsig.fmap.drivers.shp.IndexedShpDriver;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrAnnotation;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.layers.VectorialFileAdapter;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.utiles.PostProcessSupport;
import com.iver.utiles.SimpleFileFilter;
import com.iver.utiles.swing.threads.AbstractMonitorableTask;

public class ExportSeveralTo extends ExportTo {
	
	private boolean overwrite = false;
	
	private class WriterTask extends AbstractMonitorableTask
	{
		FLyrVect lyrVect;
		IWriter writer;
		int rowCount;
		ReadableVectorial va;
		SelectableDataSource sds;
		FBitSet bitSet;
		MapContext mapContext;
		VectorialDriver reader;

		public WriterTask(MapContext mapContext, FLyrVect lyr, IWriter writer, Driver reader) throws ReadDriverException
		{
			this.mapContext = mapContext;
			this.lyrVect = lyr;
			this.writer = writer;
			this.reader = (VectorialDriver) reader;

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
			lyrVect.setWaitTodraw(true);
			va.start();
			ICoordTrans ct = lyrVect.getCoordTrans();
			DriverAttributes attr = va.getDriverAttributes();
			boolean bMustClone = false;
			if (attr != null) {
				if (attr.isLoadedInMemory()) {
					bMustClone = attr.isLoadedInMemory();
				}
			}
			if (lyrVect instanceof FLyrAnnotation && lyrVect.getShapeType()!=FShape.POINT) {
				SHPLayerDefinition lyrDef=(SHPLayerDefinition)writer.getTableDefinition();
				lyrDef.setShapeType(FShape.POINT);
				writer.initialize(lyrDef);
			}

			 if(writer instanceof ShpWriter) {
				 String charSetName = prefs.get("dbf_encoding", DbaseFile.getDefaultCharset().toString());
				 if(lyrVect.getSource() instanceof VectorialFileAdapter) {
					 ((ShpWriter)writer).loadDbfEncoding(((VectorialFileAdapter)lyrVect.getSource()).getFile().getAbsolutePath(), Charset.forName(charSetName));
				 } else {
						Object s = lyrVect.getProperty("DBFFile");
						if(s != null && s instanceof String)
							((ShpWriter)writer).loadDbfEncoding((String)s, Charset.forName(charSetName));
				 }
			 }

			// Creamos la tabla.
			writer.preProcess();

			if (bitSet.cardinality() == 0) {
				rowCount = va.getShapeCount();
				for (int i = 0; i < rowCount; i++) {
					if (isCanceled())
						break;
					IGeometry geom = va.getShape(i);
					if (geom == null) {
						reportStep();
						continue;
					}
					if (lyrVect instanceof FLyrAnnotation && geom.getGeometryType()!=FShape.POINT) {
						Point2D p=FLabel.createLabelPoint((FShape)geom.getInternalShape());
						geom=ShapeFactory.createPoint2D(p.getX(),p.getY());
					}
					if (isCanceled())
						break;
					if (ct != null) {
						if (bMustClone)
							geom = geom.cloneGeometry();
						geom.reProject(ct);
					}
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
					if (isCanceled())
						break;
					IGeometry geom = va.getShape(i);
					if (geom == null) {
						reportStep();
						continue;
					}
					if (lyrVect instanceof FLyrAnnotation && geom.getGeometryType()!=FShape.POINT) {
						Point2D p=FLabel.createLabelPoint((FShape)geom.getInternalShape());
						geom=ShapeFactory.createPoint2D(p.getX(),p.getY());
					}
					if (isCanceled())
						break;
					if (ct != null) {
						if (bMustClone)
							geom = geom.cloneGeometry();
						geom.reProject(ct);
					}
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
			va.stop();
			lyrVect.setWaitTodraw(false);

		}
		/* (non-Javadoc)
		 * @see com.iver.utiles.swing.threads.IMonitorableTask#finished()
		 */
		public void finished() {
			try {
				executeCommand(lyrVect);
			} catch (Exception e) {
				NotificationManager.addError(e);
			}
		}

	}
	private class MultiWriterTask extends AbstractMonitorableTask{
		Vector tasks=new Vector();
		public void addTask(WriterTask wt) {
			tasks.add(wt);
		}
		public void run() throws Exception {
			for (int i = 0; i < tasks.size(); i++) {
				((WriterTask)tasks.get(i)).run();
			}
			tasks.clear();
		}
		/* (non-Javadoc)
		 * @see com.iver.utiles.swing.threads.IMonitorableTask#finished()
		 */
		public void finished() {
			for (int i = 0; i < tasks.size(); i++) {
				((WriterTask)tasks.get(i)).finished();
			}
			tasks.clear();
		}


	}

	
	public void execute(String actionCommand) {
		try {
		if (!actionCommand.equals("SHP")) {
			super.execute(actionCommand);
		} else {
			overwrite = false;
			JFileChooser jfc = new JFileChooser();
			SimpleFileFilter filterShp = new SimpleFileFilter("shp",
					PluginServices.getText(this, "shp_files"));
			jfc.setFileFilter(filterShp);
			if (jfc.showSaveDialog((Component) PluginServices.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
				File newFile = jfc.getSelectedFile();
				String path = newFile.getAbsolutePath();
				if (path.endsWith(".shp")) {
					path = path.substring(0, path.lastIndexOf(".shp"));
				}

				View view = (View) PluginServices.getMDIManager().getActiveWindow();
				FLayer[] activeLayers = view.getMapControl().getMapContext().getLayers().getActives();
				for (int i=0; i<activeLayers.length; i++) {
					if (activeLayers[i] instanceof FLyrVect) {
						FLyrVect lv = (FLyrVect) activeLayers[i];
						int numSelec;
							numSelec = lv.getRecordset().getSelection()
							.cardinality();
						if (numSelec > 0) {
							String message = String.format(PluginServices.getText(this, "se_van_a_guardar_de_la_capa"), 
									numSelec, lv.getName());
							int resp = JOptionPane.showConfirmDialog(
									(Component) PluginServices.getMainFrame(),
									message,
									PluginServices.getText(this,"export_to"), JOptionPane.YES_NO_OPTION);
							if (resp != JOptionPane.YES_OPTION) {
								continue;
							}
						}
						String newFilePath = path + "_" + PluginServices.getText(this, "layer") 
						 + "_" + i + "_" + activeLayers[i].getName();
						if (!newFilePath.toLowerCase().endsWith(".shp")) {
							newFilePath = newFilePath + ".shp";
						}
						newFile = new File(newFilePath);
						if (!overwrite && newFile.exists()) {
							int resp = JOptionPane.showConfirmDialog(
									(Component) PluginServices.getMainFrame(),PluginServices.getText(this,"fichero_ya_existe_seguro_desea_guardarlo"),
									PluginServices.getText(this,"guardar"), JOptionPane.YES_NO_OPTION);
							if (resp != JOptionPane.YES_OPTION) {
								return;
							}
							overwrite = true;
						}
							saveToShp(view.getMapControl().getMapContext(), 
									(FLyrVect) activeLayers[i], newFilePath);
					}
				}
			}
		}
		} catch (ReadDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean isVisible() {
		com.iver.andami.ui.mdiManager.IWindow f = PluginServices.getMDIManager()
		.getActiveWindow();

		if (f == null) {
			return false;
		}

		if (f instanceof View) {
			FLayer[] layers = ((View) f).getMapControl().getMapContext().getLayers().getActives();
			if (layers.length > 1) {
				return true;
			}
		}
		return false;
	}
	
	public void saveToShp(MapContext mapContext, FLyrVect layer, String path) {
		try {

				if (!(path.toLowerCase().endsWith(".shp"))) {
					path = path + ".shp";
				}
				File newFile = new File(path);



				SelectableDataSource sds = layer.getRecordset();
				FieldDescription[] fieldsDescrip = sds.getFieldsDescription();

				if (layer.getShapeType() == FShape.MULTI) // Exportamos a 3
				// ficheros
				{
					ShpWriter writer1 = (ShpWriter) LayerFactory.getWM().getWriter(
					"Shape Writer");
					Driver[] drivers=new Driver[3];
					ShpWriter[] writers=new ShpWriter[3];

					// puntos
					String auxPoint = path.replaceFirst("\\.shp", "_points.shp");

					SHPLayerDefinition lyrDefPoint = new SHPLayerDefinition();
					lyrDefPoint.setFieldsDesc(fieldsDescrip);
					File filePoints = new File(auxPoint);
					lyrDefPoint.setFile(filePoints);
					lyrDefPoint.setName(filePoints.getName());
					lyrDefPoint.setShapeType(FShape.POINT);
					loadEnconding(layer, writer1);
					writer1.setFile(filePoints);
					writer1.initialize(lyrDefPoint);
					writers[0]=writer1;
					drivers[0]=getOpenShpDriver(filePoints);
					//drivers[0]=null;



					ShpWriter writer2 = (ShpWriter) LayerFactory.getWM().getWriter(
					"Shape Writer");
					// Lineas
					String auxLine = path.replaceFirst("\\.shp", "_line.shp");
					SHPLayerDefinition lyrDefLine = new SHPLayerDefinition();
					lyrDefLine.setFieldsDesc(fieldsDescrip);

					File fileLines = new File(auxLine);
					lyrDefLine.setFile(fileLines);
					lyrDefLine.setName(fileLines.getName());
					lyrDefLine.setShapeType(FShape.LINE);
					loadEnconding(layer, writer2);
					writer2.setFile(fileLines);
					writer2.initialize(lyrDefLine);
					writers[1]=writer2;
					drivers[1]=getOpenShpDriver(fileLines);
					//drivers[1]=null;

					ShpWriter writer3 = (ShpWriter) LayerFactory.getWM().getWriter(
					"Shape Writer");
					// Polígonos
					String auxPolygon = path.replaceFirst("\\.shp", "_polygons.shp");
					SHPLayerDefinition lyrDefPolygon = new SHPLayerDefinition();
					lyrDefPolygon.setFieldsDesc(fieldsDescrip);
					File filePolygons = new File(auxPolygon);
					lyrDefPolygon.setFile(filePolygons);
					lyrDefPolygon.setName(filePolygons.getName());
					lyrDefPolygon.setShapeType(FShape.POLYGON);
					loadEnconding(layer, writer3);
					writer3.setFile(filePolygons);
					writer3.initialize(lyrDefPolygon);
					writers[2]=writer3;
					drivers[2]=getOpenShpDriver(filePolygons);
					//drivers[2]=null;

					writeMultiFeatures(mapContext,layer, writers, drivers);
				} else {
					ShpWriter writer = (ShpWriter) LayerFactory.getWM().getWriter(
						"Shape Writer");
					loadEnconding(layer, writer);
					IndexedShpDriver drv = getOpenShpDriver(newFile);
					SHPLayerDefinition lyrDef = new SHPLayerDefinition();
					lyrDef.setFieldsDesc(fieldsDescrip);
					lyrDef.setFile(newFile);
					lyrDef.setName(newFile.getName());
					lyrDef.setShapeType(layer.getTypeIntVectorLayer());
					writer.setFile(newFile);
					writer.initialize(lyrDef);
					// CODIGO PARA EXPORTAR UN SHP A UN CHARSET DETERMINADO
					// ES UTIL PARA QUE UN DBF SE VEA CORRECTAMENTE EN EXCEL, POR EJEMPLO
//					Charset resul = (Charset) JOptionPane.showInputDialog((Component)PluginServices.getMDIManager().getActiveWindow(),
//								PluginServices.getText(ExportTo.class, "select_charset_for_writing"),
//								"Charset", JOptionPane.QUESTION_MESSAGE, null,
//								Charset.availableCharsets().values().toArray(),
//								writer.getCharsetForWriting().displayName());
//					if (resul == null)
//						return;
//					Charset charset = resul;
//					writer.setCharsetForWriting(charset);
					writeFeatures(mapContext, layer, writer, drv);

				}
			
		} catch (InitializeWriterException e) {
			NotificationManager.addError(e.getMessage(),e);
		} catch (OpenDriverException e) {
			NotificationManager.addError(e.getMessage(),e);
		} catch (ReadDriverException e) {
			NotificationManager.addError(e.getMessage(),e);
		} catch (DriverLoadException e) {
			NotificationManager.addError(e.getMessage(),e);
		}

	}
	
	/**
	 * Lanza un thread en background que escribe las features. Cuando termina, pregunta al usuario si quiere
	 * añadir la nueva capa a la vista. Para eso necesita un driver de lectura ya configurado.
	 * @param mapContext
	 * @param layer
	 * @param writer
	 * @param reader
	 * @throws ReadDriverException
	 * @throws DriverException
	 * @throws DriverIOException
	 */
	private void writeFeatures(MapContext mapContext, FLyrVect layer, IWriter writer, Driver reader) throws ReadDriverException
	{
		PluginServices.cancelableBackgroundExecution(new WriterTask(mapContext, layer, writer, reader));
	}
	private void writeMultiFeatures(MapContext mapContext, FLyrVect layers, IWriter[] writers, Driver[] readers) throws ReadDriverException{
		MultiWriterTask mwt=new MultiWriterTask();
		for (int i=0;i<writers.length;i++) {
			mwt.addTask(new WriterTask(mapContext, layers, writers[i], readers[i]));
		}
		PluginServices.cancelableBackgroundExecution(mwt);
	}

}
