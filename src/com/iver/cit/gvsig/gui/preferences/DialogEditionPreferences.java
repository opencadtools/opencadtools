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
package com.iver.cit.gvsig.gui.preferences;

import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JPanel;

import com.iver.andami.preferences.AbstractPreferencePage;
import com.iver.andami.preferences.DlgPreferences;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;

public class DialogEditionPreferences  {
	public class TestPref extends AbstractPreferencePage
	{
		String id;
		public TestPref(String title)
		{
			super();
			id = title;
			setTitle(title);
		}
		public JPanel getPanel() {
			return this;
		}
		public void initializeValues() {
			// TODO Auto-generated method stub

		}
		public void storeValues() {
		}
		public void initializeDefaults() {
			// TODO Auto-generated method stub

		}
		public ImageIcon getIcon() {
			// TODO Auto-generated method stub
			return null;
		}
		public String getID() {
			// TODO Auto-generated method stub
			return null;
		}
		public String getTitle() {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean isValueChanged() {
			// TODO Auto-generated method stub
			return false;
		}
		public void setChangesApplied() {
			// TODO Auto-generated method stub

		}

	}
	public static void main(String[] args) {
		try {
			// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			// UIManager.setLookAndFeel(Options.JGOODIES_WINDOWS_NAME);
		} catch (Exception e) {
			e.printStackTrace();
		}

		DialogEditionPreferences test = new DialogEditionPreferences();
		// test.configureUI();
		test.test();

	}

	   /**
     * Configures the UI; tries to set the system look on Mac,
     * <code>WindowsLookAndFeel</code> on general Windows, and
     * <code>Plastic3DLookAndFeel</code> on Windows XP and all other OS.<p>
     *
     * The JGoodies Swing Suite's <code>ApplicationStarter</code>,
     * <code>ExtUIManager</code>, and <code>LookChoiceStrategies</code>
     * classes provide a much more fine grained algorithm to choose and
     * restore a look and theme.
     */
//    private void configureUI() {
//        UIManager.put(Options.USE_SYSTEM_FONTS_APP_KEY, Boolean.TRUE);
//        Options.setDefaultIconSize(new Dimension(18, 18));
//
//        String lafName =
//            LookUtils.IS_OS_WINDOWS_XP
//                ? Options.getCrossPlatformLookAndFeelClassName()
//                : Options.getSystemLookAndFeelClassName();
//
//        try {
//            UIManager.setLookAndFeel(lafName);
//        } catch (Exception e) {
//            System.err.println("Can't set look & feel:" + e);
//        }
//    }
	private void test() {
		JDialog dlg = new JDialog();
		dlg.setModal(true);
		dlg.setTitle("Preferences");

		DlgPreferences panel = DlgPreferences.getInstance();

		for (int i=0; i< 20; i++)
		{
			TestPref newPref = new TestPref("Titulo " + i);
			Random rnd = new Random();
			if (i> 5)
				newPref.setParentID("Titulo " + rnd.nextInt(i));
			//panel.addPreferencePage(newPref);

		}

		FLayers layers = new FLayers();//(null,null);
		FLyrVect lyrVect = new FLyrVect();
		lyrVect.setName("Hola");
		layers.addLayer(lyrVect);

		EditionPreferencePage edPref = new EditionPreferencePage();
		//panel.addPreferencePage(edPref);
		// edPref.setLayers(layers);

		dlg.getContentPane().add(panel);
		dlg.pack();
		dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		dlg.setVisible(true);



		System.exit(0);
	}

}
