/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2004 IVER T.I. and Generalitat Valenciana.
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
 * For more information, contact:
 *
 *  Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibáñez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *      +34 963862235
 *   gvsig@gva.es
 *      www.gvsig.gva.es
 *
 *    or
 *
 *   IVER T.I. S.A
 *   Salamanca 50
 *   46005 Valencia
 *   Spain
 *
 *   +34 963163400
 *   dac@iver.es
 */
package com.iver.cit.gvsig.gui.cad.tools;

import java.awt.Graphics;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.CADExtension;
import com.iver.cit.gvsig.exceptions.validate.ValidateRowException;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.EditionEvent;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.gui.cad.DefaultCADTool;
import com.iver.cit.gvsig.gui.cad.exception.CommandException;
import com.iver.cit.gvsig.gui.cad.tools.smc.JoinCADToolContext;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;
import com.vividsolutions.jts.geom.Geometry;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class JoinCADTool extends DefaultCADTool {
    private IGeometry joinedGeometry;
    private IFeature joinedFeature;
    public static final String JOIN_ACTION_COMMAND = "_join";
    protected JoinCADToolContext _fsm;
    private TreeSet<DefaultRowEdited> shorted = new TreeSet<DefaultRowEdited>(new Comparator<DefaultRowEdited>(){
		public int compare(DefaultRowEdited o1, DefaultRowEdited o2) {
			return new Integer(o2.getIndex()).compareTo(new Integer(o1.getIndex()));
		}
	});
    /**
     * Crea un nuevo JoinCADTool.
     */
    public JoinCADTool() {
    }

    /**
     * Método de inicio, para poner el código de todo lo que se requiera de una
     * carga previa a la utilización de la herramienta.
     */
    public void init() {
        _fsm = new JoinCADToolContext(this);
    }

    public IGeometry getJoinedGeometry() {
	return joinedGeometry;
    }

    public IFeature getJoinedFeature(){
	return joinedFeature;
    }
    
    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap.layers.FBitSet, double, double)
     */
    public void transition(double x, double y, InputEvent event) {
        _fsm.addPoint(x, y, event);
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
    	}
    }

    /**
     * DOCUMENT ME!
     */
    public void selection() {
       ArrayList selectedRows=getSelectedRows();
        if (selectedRows.size() == 0 && !CADExtension.getCADTool().getClass().getName().equals("com.iver.cit.gvsig.gui.cad.tools.SelectionCADTool")) {
            CADExtension.setCADTool("_selection",false);
	    ((SelectionCADTool) CADExtension.getCADTool())
		    .setNextTool(JOIN_ACTION_COMMAND);
        }
    }

    /**
     * Equivale al transition del prototipo pero sin pasarle como parámetro el
     * editableFeatureSource que ya estará creado.
     *
     * @param x parámetro x del punto que se pase en esta transición.
     * @param y parámetro y del punto que se pase en esta transición.
     */
    public void addPoint(double x, double y,InputEvent event) {
    }

    /**
     * Método para dibujar la lo necesario para el estado en el que nos
     * encontremos.
     *
     * @param g Graphics sobre el que dibujar.
     * @param x parámetro x del punto que se pase para dibujar.
     * @param y parámetro x del punto que se pase para dibujar.
     */
    public void drawOperation(Graphics g, double x, double y) {
    }

    public void join() {
    	ArrayList selectedRow = getSelectedRows();
    	if (selectedRow.size()<2) {
    		return;
    	}
    	ArrayList selectedRowAux = new ArrayList();
    	VectorialLayerEdited vle = getVLE();
    	VectorialEditableAdapter vea = vle.getVEA();
    	try {
    		vea.startComplexRow();
    		Geometry geomTotal=null;
    		DefaultRowEdited[] dres=(DefaultRowEdited[])selectedRow.toArray(new DefaultRowEdited[0]);
    		for (int i = 0; i < dres.length; i++) {
        		shorted.add(dres[i]);
    		}
    		boolean first=true;
    		Value[] values=null;
    		String fid = null;
    		int index = -1;
    		Iterator<DefaultRowEdited> iterator=shorted.iterator();
        	while (iterator.hasNext()) {
    			DefaultRowEdited dre = (DefaultRowEdited) iterator.next();
    			DefaultFeature df = (DefaultFeature) dre.getLinkedRow()
    				.cloneRow();
    			IGeometry geom=df.getGeometry();
    			if (first){
    				values=df.getAttributes();
    				fid = df.getID();
    				index = dre.getIndex();
    				first=false;
    			}else{    			    
    			    vea.removeRow(dre.getIndex(), getName(), EditionEvent.GRAPHIC);
    			}

    			if (geomTotal==null){
    				geomTotal=geom.toJTSGeometry();
    			}else{
    				Geometry geomJTS=geom.toJTSGeometry();
    				geomTotal=geomTotal.union(geomJTS);
    			}
    		}
        	shorted.clear();
//    		String newFID = vea.getNewFID();
    		IGeometry geom = FConverter.jts_to_igeometry(geomTotal);
//    		DefaultFeature df1 = new DefaultFeature(geom, values, newFID);
    		DefaultFeature df1 = new DefaultFeature(geom, values, fid);
    		joinedGeometry = geom;
    		joinedFeature = (IFeature) df1.cloneRow();
//    		int index1 = vea.addRow(df1, PluginServices.getText(this, "join"),
//    				EditionEvent.GRAPHIC);
    		int index1 = vea.modifyRow(index, df1, PluginServices.getText(this, "join"), EditionEvent.GRAPHIC);
    		selectedRowAux.add(new DefaultRowEdited(df1, IRowEdited.STATUS_ADDED,
    				vea.getInversedIndex(index1)));
    		vea.endComplexRow(getName());
//    		vle.setSelectionCache(VectorialLayerEdited.NOTSAVEPREVIOUS, selectedRowAux);
    		refresh();
	    fireEndGeometry(JOIN_ACTION_COMMAND);
    	} catch (ReadDriverException e) {
    		NotificationManager.addError(e.getMessage(),e);
    	} catch (ValidateRowException e) {
    		NotificationManager.addError(e.getMessage(),e);
    	}
    }
       /**
	 * Add a diferent option.
	 *
	 * @param s
	 *            Diferent option.
	 */
    public void addOption(String s) {
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see com.iver.cit.gvsig.gui.cad.CADTool#addvalue(double)
	 */
    public void addValue(double d) {

    }

	public String getName() {
		return PluginServices.getText(this,"join_");
	}

	public String toString() {
		return "_join";
	}
	public boolean isApplicable(int shapeType) {
		switch (shapeType) {
			case FShape.POINT:
				return false;
		}
		return true;
	}

        public void drawOperation(Graphics g, ArrayList pointList) {
	    // TODO Auto-generated method stub
	}
        public boolean isMultiTransition() {
		// TODO Auto-generated method stub
		return false;
	}

	public void transition(InputEvent event) {
		// TODO Auto-generated method stub

	}
}
