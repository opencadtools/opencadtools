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

import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.iver.cit.gvsig.project.documents.view.snapping.snappers.FinalPointSnapper;

public class testDialog {
    public static void main(String[] args) {

	try {
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} catch (ClassNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (InstantiationException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IllegalAccessException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (UnsupportedLookAndFeelException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	JDialog dlg = new JDialog();

	ArrayList list = new ArrayList();
	for (int i = 0; i < 20; i++)
	    list.add(new FinalPointSnapper());
	SnapConfig panel = new SnapConfig();
	panel.setSnappers(list);
	Action act = new AbstractAction() {
	    @Override
	    public void actionPerformed(java.awt.event.ActionEvent arg0) {
		System.out.println("HOla");
		// HashPrintRequestAttributeSet att = new
		// HashPrintRequestAttributeSet();
		//
		// PrinterJob job = PrinterJob.getPrinterJob();
		// PageFormat defaulFormat = job.defaultPage();
		// // PageFormat selectedFormat = job.pageDialog(defaulFormat);
		// Paper paper = new Paper();
		// paper.setSize(MediaSize.ISO.A5.getX(MediaSize.INCH),MediaSize.ISO.A5.getY(MediaSize.INCH));
		// defaulFormat.setPaper(paper);
		// job.defaultPage(defaulFormat);
		// if (job.printDialog()) {
		// // System.out.println(job.)
		// }
	    };
	};
	JButton btnPrint = new JButton(act);

	dlg.getContentPane().add(panel);
	// dlg.getContentPane().add(btnPrint);
	// dlg.getContentPane().setSize(panel.getSize());
	dlg.pack();
	dlg.show(true);
    }

}
