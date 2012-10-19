/*
 * Created on 27-abr-2004
 *
 */
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

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

/**
 * Renderer para iconos.
 * 
 * @author Vicente Caballero Navarro
 */
public class CellIconOptionRenderer extends JPanel implements TableCellRenderer {
    Border unselectedBorder = null;
    Border selectedBorder = null;
    boolean isBordered = true;
    private PreviewIcon preview = new PreviewIcon();

    /**
     * Crea un nuevo FCellRenderer.
     * 
     * @param isBordered
     *            DOCUMENT ME!
     */
    public CellIconOptionRenderer(boolean isBordered) {
	this.isBordered = isBordered;
	setOpaque(true); // MUST do this for background to show up.
    }

    /**
     * DOCUMENT ME!
     * 
     * @param table
     *            DOCUMENT ME!
     * @param value
     *            DOCUMENT ME!
     * @param isSelected
     *            DOCUMENT ME!
     * @param hasFocus
     *            DOCUMENT ME!
     * @param row
     *            DOCUMENT ME!
     * @param column
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public Component getTableCellRendererComponent(JTable table, Object value,
	    boolean isSelected, boolean hasFocus, int row, int column) {
	preview.setIcon((ImageIcon) value);
	if (table.getSelectedRow() == row) {
	    preview.setSelected(true);
	} else {
	    preview.setSelected(false);
	}
	return preview;
    }
}
