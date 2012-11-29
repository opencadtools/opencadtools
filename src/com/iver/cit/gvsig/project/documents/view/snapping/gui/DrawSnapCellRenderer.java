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
package com.iver.cit.gvsig.project.documents.view.snapping.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.iver.cit.gvsig.project.documents.view.snapping.ISnapper;

/**
 * Cell Renderer del símbolo de cada uno de los ISnapper.
 * 
 * @author Vicente Caballero Navarro
 */
public class DrawSnapCellRenderer extends JPanel implements TableCellRenderer {
    private static Point2D position = new Point2D.Double(7, 7);
    private ArrayList snappers;
    private int row;

    public DrawSnapCellRenderer(ArrayList snappers) {
	this.snappers = snappers;
	this.setBackground(Color.white);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
	    boolean isSelected, boolean hasFocus, int row, int column) {
	this.row = row;
	return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
	super.paintComponent(g);

	((ISnapper) snappers.get(row)).draw(g, position);
	// g.drawString("prueba",7,10);
    }

}
