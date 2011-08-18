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
 *
*/

package com.iver.cit.gvsig.gui.cad.tools;

import java.awt.Graphics;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.util.ArrayList;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;

import com.iver.cit.gvsig.gui.cad.DefaultCADTool;
import com.iver.cit.gvsig.gui.cad.exception.CommandException;
import com.iver.cit.gvsig.gui.cad.tools.smc.EIELPointCADToolContext;
import com.iver.cit.gvsig.gui.cad.tools.smc.EIELPointCADToolContext.PointCADToolState;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 * @author Laboratorio de Bases de Datos. Universidad de A Coruña
 * @author Cartolab. Universidad de A Coruña
 */
public class EIELPointCADTool extends InsertionCADTool {
	private EIELPointCADToolContext _fsm;


	/**
	 * Método de incio, para poner el código de todo lo que se requiera de una
	 * carga previa a la utilización de la herramienta.
	 * Se ejecuta cada vez que se selecciona la herramienta
	 */
	public void init() {
		super.init();
		_fsm = new EIELPointCADToolContext(this);
	}

	/**
	 * DOCUMENT ME!
	 * @param x DOCUMENT ME!
	 * @param y DOCUMENT ME!
	 * @param sel DOCUMENT ME!
	 */
	public void transition(double x, double y, InputEvent event) {
		_fsm.addPoint(x, y, event);
	}

	public void transition(InputEvent event){
		_fsm.removePoint(event);
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap.layers.FBitSet, double)
	 */
	public void transition(double d) {
		_fsm.addValue(d);
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap.layers.FBitSet, java.lang.String)
	 */
	public void transition(String s) throws CommandException {
		if (!super.changeCommand(s)){
			_fsm.addOption(s);
		}else
			System.out.println("ChangeCommand devuelve true");
	}

	/**
	 * Equivale al transition del prototipo pero sin pasarle como pará metro el
	 * editableFeatureSource que ya estará creado.
	 *
	 * @param sel Bitset con las geometrías que estén seleccionadas.
	 * @param x parámetro x del punto que se pase en esta transición.
	 * @param y parámetro y del punto que se pase en esta transición.
	 */
	public void addPoint(double x, double y,InputEvent event) {
		PointCADToolState actualState = (PointCADToolState) _fsm.getPreviousState();
		String status = actualState.getName();
		if (status.equals("Point.FirstPoint")) {
			try {
				VectorialEditableAdapter vea = getVLE().getVEA();			
				addGeometry(ShapeFactory.createPoint2D(x, y));
				virtualIndex = new Integer(vea.getRowCount()-1);
			} catch (ReadDriverException e) {
					NotificationManager.addError(e.getMessage(),e);
			}
		}
	}


	public void removePoint(InputEvent event) {
		PointCADToolState actualState = (PointCADToolState) _fsm.getPreviousState();
		String status = actualState.getName();

		if (status.equals("Point.PointPainted")) {
			getCadToolAdapter().delete();
		}
	}



//	/**
//	 * Acción que abre el formulario de edición de las propiedades para
//	 * el punto introducido
//	 */
//	public void openForm(){
//		keys = openInsertEntityForm();
//		if (keys.size() == 0){
//			setFormState(InsertionCADTool.FORM_CANCELLED);
//		}else{
//			setFormState(InsertionCADTool.FORM_ACCEPTED);
//		}
//	}

	/**
	 * Acción que guarda en base de datos la geometría del punto
	 * en la tupla creada por el módulo de formularios
	 */
	public void save(){
		//insertGeometry(keys);
		initialize();
	}

	public void cancel(){
		if (virtualIndex != null){
			getCadToolAdapter().delete(virtualIndex.intValue());							
		}
	}


	/**
	 * Método que prepara la clase para una nueva digitalización.
	 * Inicializa todas las variables
	 * */
	private void initialize(){
		keys.clear();
		virtualIndex = null;
		initializeFormState();
	}

	/**
	 * Obtiene la geometria actual a partir de los puntos introducidos
	 * */
	public IGeometry getCurrentGeom(){
		VectorialEditableAdapter vea = getVLE().getVEA();
		IGeometry currentGeom = null;
		int num;
		try {
			num = vea.getRowCount()-1;
			currentGeom = vea.getFeature(num).getGeometry();
		} catch (ReadDriverException e) {
			NotificationManager.addError(e.getMessage(),e);
		}	
		return currentGeom;
	}

	/**
	 * Método para dibujar la lo necesario para el estado en el que nos
	 * encontremos.
	 *
	 * @param g Graphics sobre el que dibujar.
	 * @param selectedGeometries BitSet con las geometrías seleccionadas.
	 * @param x parámetro x del punto que se pase para dibujar.
	 * @param y parámetro x del punto que se pase para dibujar.
	 */
	public void drawOperation(Graphics g, double x,
			double y) {
	}


	/**
	 * Add a diferent option.
	 *
	 * @param sel DOCUMENT ME!
	 * @param s Diferent option.
	 */
	public void addOption(String s) {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.gui.cad.CADTool#addvalue(double)
	 */
	public void addValue(double d) {
		// TODO Auto-generated method stub
	}

	public String getName() {
		return PluginServices.getText(this,"point_");
	}

	public String toString() {
		return "_point";
	}

	public boolean isApplicable(int shapeType) {
		switch (shapeType) {
		case FShape.POINT:
		case FShape.MULTIPOINT:			
			return true;
		}
		return false;
	}

	public void setPreviousTool(DefaultCADTool tool) {
		// TODO Auto-generated method stub
		
	}

	public void drawOperation(Graphics g, ArrayList pointList) {
		// TODO Auto-generated method stub
		
	}

	public boolean isMultiTransition() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setMultiTransition(boolean condicion) {
		// TODO Auto-generated method stub
		
	}


}
