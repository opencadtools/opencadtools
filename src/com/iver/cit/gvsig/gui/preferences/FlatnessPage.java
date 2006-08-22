/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2005 IVER T.I. and Generalitat Valenciana.
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

/* CVS MESSAGES:
*
* $Id$
* $Log$
* Revision 1.2  2006-08-22 07:37:32  jaume
* *** empty log message ***
*
* Revision 1.1  2006/08/10 08:18:35  caballero
* configurar grid
*
* Revision 1.1  2006/08/04 11:41:05  caballero
* poder especificar el zoom a aplicar en las vistas
*
* Revision 1.3  2006/07/31 10:02:31  jaume
* *** empty log message ***
*
* Revision 1.2  2006/06/13 07:43:08  fjp
* Ajustes sobre los cuadros de dialogos de preferencias
*
* Revision 1.1  2006/06/12 16:04:28  caballero
* Preferencias
*
* Revision 1.11  2006/06/06 10:26:31  jaume
* *** empty log message ***
*
* Revision 1.10  2006/06/05 17:07:17  jaume
* *** empty log message ***
*
* Revision 1.9  2006/06/05 17:00:44  jaume
* *** empty log message ***
*
* Revision 1.8  2006/06/05 16:57:59  jaume
* *** empty log message ***
*
* Revision 1.7  2006/06/05 14:45:06  jaume
* *** empty log message ***
*
* Revision 1.6  2006/06/05 11:00:09  jaume
* *** empty log message ***
*
* Revision 1.5  2006/06/05 10:39:02  jaume
* *** empty log message ***
*
* Revision 1.4  2006/06/05 10:13:40  jaume
* *** empty log message ***
*
* Revision 1.3  2006/06/05 10:06:08  jaume
* *** empty log message ***
*
* Revision 1.2  2006/06/05 09:51:56  jaume
* *** empty log message ***
*
* Revision 1.1  2006/06/02 10:50:18  jaume
* *** empty log message ***
*
*
*/
package com.iver.cit.gvsig.gui.preferences;

import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.iver.andami.PluginServices;
import com.iver.andami.preferences.AbstractPreferencePage;
import com.iver.andami.preferences.StoreException;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;

public class FlatnessPage extends AbstractPreferencePage {
	private static Preferences prefs = Preferences.userRoot().node( "cadtooladapter" );
	private JTextArea jTextArea = null;
	private JTextField txtFlatness;

	private ImageIcon icon;

	public FlatnessPage() {
		super();
		icon = new ImageIcon(this.getClass().getClassLoader().getResource("images/Flatness.png"));
		addComponent(getJTextArea());

		addComponent(PluginServices.getText(this, "densityfication") + ":",
			txtFlatness = new JTextField("", 15));
	}

	public void initializeValues() {
		double flatness = prefs.getDouble("flatness",FConverter.flatness);

		txtFlatness.setText(String.valueOf(flatness));

		FConverter.flatness=flatness;
	}

	public String getID() {
		return this.getClass().getName();
	}

	public String getTitle() {
		return PluginServices.getText(this, "Flatness");
	}

	public JPanel getPanel() {
		return this;
	}

	public void storeValues() throws StoreException {
		double flatness;
		try{
			flatness=Double.parseDouble(txtFlatness.getText());
		}catch (Exception e) {
			throw new StoreException(PluginServices.getText(this,"minimum_size_of_line_incorrect"));
		}
		prefs.putDouble("flatness", flatness);

		FConverter.flatness=flatness;
	}

	public void initializeDefaults() {
		txtFlatness.setText(String.valueOf(FConverter.flatness));
	}

	public ImageIcon getIcon() {
		return icon;
	}
	/**
	 * This method initializes jTextArea
	 *
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getJTextArea() {
		if (jTextArea == null) {
			jTextArea = new JTextArea();
			jTextArea.setBounds(new java.awt.Rectangle(13,7,285,57));
			jTextArea.setForeground(java.awt.Color.black);
			jTextArea.setBackground(java.awt.SystemColor.control);
			jTextArea.setRows(3);
			jTextArea.setWrapStyleWord(true);
			jTextArea.setLineWrap(true);
			jTextArea.setEditable(false);
			jTextArea.setText(PluginServices.getText(this,"specifies_the_minimum_size_of_the_lines_that_will_form_the_curves"));
		}
		return jTextArea;
	}

	public boolean isValueChanged() {
		return super.hasChanged();
	}
}
