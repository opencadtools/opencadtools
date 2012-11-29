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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import com.iver.cit.gvsig.fmap.rendering.EditionManagerLegend;

/**
 * Cell Editor de iconos. Controla los eventos de edición que se realicen sobre
 * la columna de iconos.
 * 
 * @author Vicente Caballero Navarro
 */
public class FilledCellEditor extends IconOptionCellEditor implements
	TableCellEditor {
    public FilledCellEditor(EditionManagerLegend el, JTable tab, ImageIcon sel,
	    ImageIcon notSel) {
	super(el, tab, sel, notSel);
	addMouseListener(new MouseListener() {

	    @Override
	    public void mouseClicked(MouseEvent e) {
		// if (e.getClickCount()==2){
		int index = table.getSelectedRow();
		if (eml.isFilled(index)) {
		    setIcon(getNotSel());
		    eml.setFilled(index, false);

		} else {
		    eml.setFilled(index, true);
		    setIcon(getSel());
		}
		stopCellEditing();
		// }
		table.repaint();
	    }

	    @Override
	    public void mouseEntered(MouseEvent e) {
	    }

	    @Override
	    public void mouseExited(MouseEvent e) {
	    }

	    @Override
	    public void mousePressed(MouseEvent e) {
	    }

	    @Override
	    public void mouseReleased(MouseEvent e) {
	    }

	});
    }

    // Implement the one method defined by TableCellEditor.
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
	    boolean isSelected, int row, int column) {
	// if (table.getSelectedRow()==row)
	// setSelected(true);
	// else
	setSelected(isSelected);
	if (eml.isFilled(row)) {
	    setIcon(getSel());
	} else {
	    setIcon(getNotSel());
	}
	return this;
    }
}
