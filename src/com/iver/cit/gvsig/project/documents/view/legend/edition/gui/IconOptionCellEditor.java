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
package com.iver.cit.gvsig.project.documents.view.legend.edition.gui;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.EventObject;

import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;

import com.iver.cit.gvsig.fmap.rendering.EditionManagerLegend;

/**
 * Cell Editor de iconos. Controla los eventos de edición que se realicen sobre
 * la columna de iconos.
 * 
 * @author Vicente Caballero Navarro
 */
public class IconOptionCellEditor extends PreviewIcon implements
	TableCellEditor {
    private ArrayList listeners = new ArrayList();
    private ImageIcon sel;
    private ImageIcon notSel;
    protected EditionManagerLegend eml;
    protected JTable table;

    public IconOptionCellEditor(EditionManagerLegend el, JTable tab,
	    ImageIcon sel, ImageIcon notSel) {
	this.eml = el;
	this.table = tab;
	this.sel = sel;
	this.notSel = notSel;
	addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
		    stopCellEditing();
		} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
		    cancelCellEditing();
		}
	    }
	});
    }

    // Implement the one CellEditor method that AbstractCellEditor doesn't.
    public Object getCellEditorValue() {
	return getIcon();
    }

    // Implement the one method defined by TableCellEditor.
    public Component getTableCellEditorComponent(JTable table, Object value,
	    boolean isSelected, int row, int column) {
	return this;
    }

    /**
     * DOCUMENT ME!
     */
    public void cancelCellEditing() {
	for (int i = 0; i < listeners.size(); i++) {
	    CellEditorListener l = (CellEditorListener) listeners.get(i);
	    ChangeEvent evt = new ChangeEvent(this);
	    l.editingCanceled(evt);
	}
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public boolean stopCellEditing() {
	// for (int i = 0; i < listeners.size(); i++) {
	// CellEditorListener l = (CellEditorListener) listeners.get(i);
	// ChangeEvent evt = new ChangeEvent(this);
	// l.editingStopped(evt);
	// }

	return true;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param anEvent
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public boolean isCellEditable(EventObject anEvent) {
	return true;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param anEvent
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public boolean shouldSelectCell(EventObject anEvent) {
	return true;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param l
     *            DOCUMENT ME!
     */
    public void addCellEditorListener(CellEditorListener l) {
	listeners.add(l);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param l
     *            DOCUMENT ME!
     */
    public void removeCellEditorListener(CellEditorListener l) {
	listeners.remove(l);
    }

    public ImageIcon getSel() {
	return sel;
    }

    public ImageIcon getNotSel() {
	return notSel;
    }
}
