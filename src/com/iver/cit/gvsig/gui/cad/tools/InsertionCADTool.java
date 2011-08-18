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

package com.iver.cit.gvsig.gui.cad.tools;

import java.awt.Component;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JOptionPane;

import com.hardcode.gdbms.engine.instruction.FieldNotFoundException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.XMLException;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;
import com.iver.cit.gvsig.project.documents.table.gui.Table;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.vividsolutions.jts.geom.Geometry;

import com.iver.cit.gvsig.gui.cad.DefaultCADTool;

//import com.iver.cit.gvsig.cad.DefaultCADTool;

//import es.udc.lbd.eiel.cad.CADExtension;
//import es.udc.lbd.eiel.cad.StartEditing;
//import es.udc.lbd.eiel.cad.StopEditing;
//import es.udc.lbd.eiel.cad.dao.GeometryEditionDAO;
//import es.udc.lbd.eiel.cad.dao.MetadataDAO;
//import es.udc.lbd.eiel.cad.util.GeometryTypeConverter;
//import es.udc.lbd.eiel.cad.util.GeometryTypes;
//import es.udc.lbd.eiel.formsmodule.facade.FormsModuleFacade;
//import es.udc.lbd.eiel.formsmodule.facade.FormsModuleFacadeFactory;
//import es.udc.lbd.eiel.giseiel.util.Sesion;
//import es.udc.lbd.eiel.giseiel.util.comprobaciones.Comprobacion;
//import es.udc.lbd.eiel.giseiel.util.layer_manager.LayerDescriptor;
//import es.udc.lbd.eiel.giseiel.util.layer_manager.LayerEditionDescriptor;
//import es.udc.lbd.eiel.giseiel.util.layer_manager.LayerManager;
//import es.udc.lbd.eiel.gui.cad.CompoundGeomInfo;
//import es.udc.lbd.eiel.gui.cad.DefaultCADTool;
//import es.udc.lbd.eiel.layers.VectorialLayerEdited;
//import es.udc.lbd.eiel.util.configuration.GlobalNames;
//import es.udc.lbd.eiel.util.exceptions.InternalErrorException;

/**
 * Clase que agrupa a las herramientas de inserción de nueva cartografía.
 * Al insertar se abre un formulario de edición que devuelve un conjunto
 * de claves para actualizar en BD la geometría digitalizada.
 * 
 * @author Isabel Pérez-Urria Lage [LBD]
 * @author Javier Estévez [Cartolab]
 */
public abstract class InsertionCADTool extends DefaultCADTool{

	/** El formulario de insercion no ha sido abierto todavia*/
	public final static int FORM_INITIAL = 0;
	/** El formulario de insercion ha sido aceptado*/
	public final static int FORM_ACCEPTED = 1;
	/** El formulario de insercion ha sido cancelado*/
	public final static int FORM_CANCELLED = 2;

	/** HashMap de claves para insertar la geometria en base de datos.*/
	protected HashMap keys = new HashMap();


	/**
	 * Estado del formulario: aceptado, cancelado, estado inicial
	 * */
	private int formState = FORM_INITIAL;

	/** Indice virtual (en memoria) de la ultima feature introducida. Este indice es la 
	 * forma de identificar a la feature en GIS-EIEL, y no coincide con la clave de la base de datos*/
	protected Integer virtualIndex;

	/**
	 * Método de incio, para poner el código de todo lo que se requiera de una
	 * carga previa a la utilización de la herramienta.
	 */
	public void init(){
		virtualIndex = null;
		keys.clear();
		formState = FORM_INITIAL;
	}

	public int getFormState(){
		return formState;
	}

	public void setFormState(int formState){
		this.formState = formState;
	}

	public void initializeFormState(){
		this.formState = FORM_INITIAL;
	}


//	/**
//	 * Abre un formulario en modo inserción
//	 * */
//	public HashMap openInsertEntityForm(){
//
//		//Conjunto atributo-valor que es clave en la tabla (generalmente el atributo geoId)
//		HashMap keys = null;
//		try {
//			FormsModuleFacade formManager = FormsModuleFacadeFactory.getDelegate(new Integer(5));
//			FLayer fLayer =  (FLayer)getActiveLayer();
//			LayerEditionDescriptor led = LayerManager.getLayerDescriptor(fLayer.getName()).getLayerEditionDescriptor();
//
//			//El formulario ya está inicializado (en StartEditing)
//			//Abre un formulario Modal: la ejecución se para en este punto hasta que el
//			//usuario acepta o cancela, en ese momento se devuelve el control al programa
//			getCadToolAdapter().setFormOpened(true);
//			if(led.getFormInvisible().booleanValue())
//				keys = formManager.insertEntityWithoutForm();
//			else{
//				keys = formManager.insertEntity();
//			}
//			getCadToolAdapter().setFormOpened(false);
//
//			System.out.println(formManager.getExecutedQueryString());
//
//		}catch(Exception e){
//			getCadToolAdapter().setFormOpened(false);
//			System.out.println("Error durante la generación del formulario");
//			e.printStackTrace();
//		}
//
//		return keys;
//	}
//	
//
//	public HashMap openInsertEntityForm(HashMap claves){
//
//		//Conjunto atributo-valor que es clave en la tabla (generalmente el atributo geoId)
//		HashMap keys = null;
//		try {
//			FormsModuleFacade formManager = FormsModuleFacadeFactory.getDelegate(new Integer(5));
//			FLayer fLayer =  (FLayer)getActiveLayer();
//			LayerEditionDescriptor led = LayerManager.getLayerDescriptor(fLayer.getName()).getLayerEditionDescriptor();
//
//			//El formulario ya está inicializado (en StartEditing)
//			//Abre un formulario Modal: la ejecución se para en este punto hasta que el
//			//usuario acepta o cancela, en ese momento se devuelve el control al programa
//			getCadToolAdapter().setFormOpened(true);
//			if(led.getFormInvisible().booleanValue())
//				keys = formManager.insertEntityWithoutForm();
//			else{
////				ahora sacare los campos de la entidad y creare el nuevo formulario con ellos
//				HashMap entityFields = formManager.getEntityFields(claves);
//				keys = formManager.insertEntity(entityFields);
//			}
//			getCadToolAdapter().setFormOpened(false);
//
//			System.out.println(formManager.getExecutedQueryString());
//
//		}catch(Exception e){
//			getCadToolAdapter().setFormOpened(false);
//			System.out.println("Error durante la generación del formulario");
//			e.printStackTrace();
//		}
//
//		return keys;
//	}
//
//
//	/**
//	 * Actualiza la BD con la gometria digitalizada
//	 * */
//	public void insertGeometry(HashMap keys){
//		GeometryEditionDAO geometryEditionDAO = null;
//		MetadataDAO metadataDAO = new MetadataDAO();
//		String geoId = "";
//		LayerDescriptor ld = null;
//		int rowid = -1;
//
//		VectorialEditableAdapter vea = getVLE().getVEA();
//
//		try{
//			//Obtenemos geometria
//			rowid = vea.getRowCount()-1;
//			IGeometry Igeom = vea.getFeature(rowid).getGeometry();
//			Geometry geom = Igeom.toJTSGeometry();
//
//			//Obtenemos geoId
//			geoId = (String)keys.get("geoid");
//			System.out.println("geoid = " + geoId);
//
//			//Obtenemos LayerDescriptor con informacion sobre la capa
//			FLayer fLayer =  (FLayer)getActiveLayer();
//			ld = LayerManager.getLayerDescriptor(fLayer.getName());
//
//			geometryEditionDAO = new GeometryEditionDAO(ld.getLayerSchema());
//
//			//SRID
//			String SRID = obtenerView().getMapControl().getViewPort()
//			.getProjection().getAbrev().substring(5);
//			System.out.println("SRID = " + SRID);
//
//			//TODO:Creo que esto va a sobrar porque hay herramientas especificas
//			//para punto, multipunto, etc. En todo caso el tipo de la geometria
//			//podra cogerse del LayerDescriptor
//			//Aseguramos que el tipo de dato coincide con el de la base
//			//de datos
//			int geometryType = metadataDAO.getGeometryType(ld.getLayerTable(),
//					ld.getLayerGeometryField());
//			geom = GeometryTypeConverter.convertToType(geom, geometryType);
//
//			//Por defecto le asigna el goid de la fila en la que se inserta. Lo cambiamos
//			//por el geoid generado en el módulo de formularios
//			vea.changeFID(rowid, new Integer(geoId).intValue());
//
//			//Actualizamos la geometría
//			geometryEditionDAO.setGeometry(ld.getLayerTable(), ld.getLayerGeometryField(),
//					geom, geoId, SRID);
//
//			//Recargamos los drivers y borramos la nueva Feature del fichero de expansión
//			vea.clearChanges();
//
//			//Almacenar centroide en caso de ser necesario
//			if(ld.getLayerEditionDescriptor().getCapaCentroide() != null){
//				saveCentroid(geoId, ld.getLayerTable(), ld.getLayerEditionDescriptor().getGeoCentroide(),
//						Igeom.toJTSGeometry(), geometryEditionDAO, metadataDAO, SRID);
//				//Si está cargada la capa del centroide la actualizamos
//				String capaCentroide = ld.getLayerEditionDescriptor().getCapaCentroide();
//				FLyrVect flayerCentroid = (FLyrVect)obtenerView().getMapControl().getMapContext().getLayers().getLayer(capaCentroide);
//				if(flayerCentroid != null){
//					flayerCentroid.getRecordset().reload();
//				}
//			}
//
//			getVLE().clearSelection();
//			//Nota: se podria recargar únicamente la fila que cambié??
//			getCadToolAdapter().refreshEditedLayer();
//			//CADExtension.getEditionManager().getMapControl().repaint();
//
//
//			//Si es geometria compuesta almacenamos la clave para insertar la siguiente geom.
//			if((hasNextGeometry()) && (getCadToolAdapter().getCompoundGeomInfo() != null)){
//				getCadToolAdapter().getCompoundGeomInfo().setKeys(keys);
//			}
//
//		}catch(Exception e){
//			deleteFeatureOnError(keys, rowid);
//			e.printStackTrace();
//		}
//
//	}
//


	/** Borra una feature en caso de que haya ocurrido algun error*/
	private void deleteFeatureOnError(HashMap keys, int rowid){
		if (keys.isEmpty())
			return;
		System.out.println("Error durante la actualización de la geometria con geoId = " + keys.get("geoid"));
		System.out.println("Eliminando la tupla ..");
		try{
			JOptionPane.showMessageDialog(
					(Component) PluginServices.getMDIManager().getActiveWindow(),
					PluginServices.getText(this, "error_inserting"),
					PluginServices.getText(this, "warning_title"),
					JOptionPane.ERROR_MESSAGE);
			//[NachoV comment]getCadToolAdapter().deleteRow(rowid);
//			[NachoV added by]
			getCadToolAdapter().delete(rowid);
		}catch(Exception e2){
			e2.printStackTrace();
		}
	}

//	/*
//	 * Las funciones que vienen a continuación son para implementar la digitalización
//	 * de geometrías compuestas, como tramos de carreteras 
//	 * */
//
//	/** En el caso de geometrias compuestas pone activa la primera capa*/
//	public void setActiveFirstLayer(){
//		CompoundGeomInfo compoundGeomInfo = getCadToolAdapter().getCompoundGeomInfo();
//		changeActiveLayer(compoundGeomInfo.getFirstLayer());
//	}
//
//	/** En el caso de geometrias compuestas pone activa la segunda capa*/
//	public void setActiveSecondLayer(){
//		CompoundGeomInfo compoundGeomInfo = getCadToolAdapter().getCompoundGeomInfo();
//		changeActiveLayer(compoundGeomInfo.getSecondLayer());
//	}
//
//
//	/**
//	 * A ejecutar por firstLayer:
//	 * Comprueba si es una geometria compuesta, es decir, a continuacion de
//	 * la geometria actual se digitaliza otra antes de editar el formulario.
//	 * (ej: tramos de carretera y su mediana)
//	 */
//	public boolean hasNextGeometry(){
//		FLayer fLayer = getActiveLayer();
//		LayerDescriptor ld = LayerManager.getLayerDescriptor(fLayer.getName());
//		if(ld.getLayerEditionDescriptor().getCapaEdicionSig() != null){
//			return true;
//		}
//		return false;
//	}
//
//	/**
//	 * En el caso de geometrías compuestas comprueba si la capa que 
//	 * está activa actualmente es la primera
//	 * */
//	public boolean isFirstGeometry(){
//		FLayer fLayer = getActiveLayer();
//		LayerDescriptor ld = LayerManager.getLayerDescriptor(fLayer.getName());
//		if(ld.getLayerEditionDescriptor().getOrdenEdicion().intValue() == 1){
//			return true;
//		}
//		return false;
//	}
//
//	/**
//	 * A ejecutar por firstLayer en el caso de geometrias compuestas. Termina la edición
//	 * de la primera capa y activa la edición de la segunda. 
//	 * */
//	public void digitalizeNextGeometry(){
//
//		FLayer fLayer = getActiveLayer();
//		LayerDescriptor ld = LayerManager.getLayerDescriptor(fLayer.getName());
//		String nextLayerName = ld.getLayerEditionDescriptor().getCapaEdicionSig();
//
//		View vista = (View) PluginServices.getMDIManager().getActiveWindow();
//		FLyrVect nextLayer = (FLyrVect) vista.getModel().getMapContext().getLayers().getLayer(nextLayerName);
//
//
//		changeActiveLayer(nextLayer);
//		StartEditing startEditing=(StartEditing)PluginServices.getExtension(StartEditing.class);
//		startEditing.execute("STARTEDITING");
//
//
//		getCadToolAdapter().setCompoundGeomInfo(
//				new CompoundGeomInfo(fLayer, nextLayer));
//		getCadToolAdapter().getCompoundGeomInfo().setFirstCADTool(this);
//
//		(LayerManager.getLayerDescriptor(nextLayerName)).getLayerEditionDescriptor().
//		setEditingAsSecond(new Boolean(true));
//
//		//System.out.println("Ponemos capa de " +  fLayer.getName() + " a dirty");
//		fLayer.setCachingDrawnLayers(false);
//		fLayer.setCacheImageDrawnLayers(null);
//		fLayer.setDirty(true);
//
//	}
//
//
//	/**
//	 * A ejecutar por secondLayer
//	 * Cambia de nuevo la capa activa a firstGeometry y abre su formulario de edición
//	 * */
//	public void endNextGeom(){
//		setActiveFirstLayer();
//		getCadToolAdapter().getCompoundGeomInfo().setSecondCADTool(this);
//		//Provocamos una transición en la CadTool de la primera capa digitalizada
//		//para abrir el formulario de edición
//		getCadToolAdapter().keyPressed("espacio");
//	}
//
//
//	/**
//	 * En el caso de geomtrias compuestas inserta la geometria digitalizada 
//	 * en segundo lugar 
//	 * */
//	public void saveNext(){
//		CompoundGeomInfo compoundGeomInfo = getCadToolAdapter().getCompoundGeomInfo();
//		setActiveSecondLayer();
//		InsertionCADTool nextCadTool = (InsertionCADTool) compoundGeomInfo.getSecondCADTool();
//		nextCadTool.insertGeometry(compoundGeomInfo.getKeys());
//
//	}
//
//
//	/**
//	 * Termina la edición de la segunda capa en una geometria compuesta
//	 * */
//	public void endEdition(){
//
//		setActiveSecondLayer();
//		StopEditing stopEditing = (StopEditing)PluginServices.getExtension(StopEditing.class);
//		stopEditing.execute("STOPEDITING");
//		setActiveFirstLayer();
//
//		CompoundGeomInfo compoundGeomInfo = getCadToolAdapter().getCompoundGeomInfo();
//		String secondLayerName = compoundGeomInfo.getSecondLayer().getName();
//		(LayerManager.getLayerDescriptor(secondLayerName)).getLayerEditionDescriptor().
//		setEditingAsSecond(new Boolean(false));
//
//	}
//
//
//
//	/** 
//	 * En el caso de geometrías compuestas cancela las herramientas de ambas capas 
//	 * (vuelve al estado inicial de ambas herramientas) 
//	 * */
//	public void cancel(){
//		//Cancelamos tambien la primera capa en edición, en caso de que estemos en la segunda
//		FLayer flyr = getActiveLayer();
//		CompoundGeomInfo compoundGeomInfo = getCadToolAdapter().getCompoundGeomInfo();
//		if((compoundGeomInfo != null)&&(flyr.getName().equals(compoundGeomInfo.getSecondLayer().getName()))){
//			//Si está activa la segunda capa terminamos la edición
//			endEdition();
//			//Cancelamos la primera capa
//			setActiveFirstLayer();
//			getCadToolAdapter().transition("C");
//		}
//
//	}
//
//
//	/**
//	 * En el caso de geomtrias compuestas elimina la información de las mismas
//	 * */
//	public void clearCompoundGeom() {
//		getCadToolAdapter().setCompoundGeomInfo(null);
//	}
//
//
//	/**
//	 * Realiza comprobaciones sobre la geomertria que vamos a insertar 
//	 * @param Igeom geometria digitalizada que pretendemos insertar
//	 * */
//	public boolean checksOnInsertion(IGeometry Igeom){
//		boolean checksOnInsertion = true;
//		FLayer flyr = getActiveLayer();
//		LayerDescriptor ld = LayerManager.getInstance().getLayerDescriptor(flyr.getName());
//		LayerEditionDescriptor led = ld.getLayerEditionDescriptor();
//		Collection comprobaciones = led.getComprobaciones();
//
//		if(led.hayComprobaciones()){
//			try{
//				for(Iterator it = comprobaciones.iterator(); it.hasNext(); ){
//					Comprobacion comp = (Comprobacion)it.next();
//					checksOnInsertion = comp.comprobarEnInsercion(Igeom.toJTSGeometry());
//					if(!checksOnInsertion)
//						break;
//				}
//
//			}catch(InternalErrorException e){
//				setErrorOnIntersection(true);
//				e.printStackTrace();
//				JOptionPane.showMessageDialog(
//						(Component) PluginServices.getMDIManager().getActiveWindow(),
//						PluginServices.getText(this, "error_during_check"),
//						PluginServices.getText(this, "error_title"),
//						JOptionPane.ERROR_MESSAGE);
//			}
//		}
//		return checksOnInsertion;
//
//	}
//
//
//
//	/**
//	 * Metodo que sustituye a isApplicable en nuestras herramientas. 
//	 * Si la capa es de sólo edición las herramientas de inserción no estarán activas.
//	 * */
//	public boolean newIsApplicable(LayerDescriptor ld){
//		if((ld.getLayerEditionDescriptor().getSoloEditable().booleanValue()) &&
//				(!ld.getLayerEditionDescriptor().isEditingAsSecond().booleanValue())){
//			return false;
//		}else{
//			String tipoGeom = ld.getLayerEditionDescriptor().getTipoGeom();
//			return isApplicable(GeometryTypes.getTypeFromString(tipoGeom));
//		}
//	}

}
