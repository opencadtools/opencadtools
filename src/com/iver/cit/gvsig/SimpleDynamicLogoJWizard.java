package com.iver.cit.gvsig;

import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import jwizardcomponent.FinishAction;
import jwizardcomponent.JWizardComponents;
import jwizardcomponent.Utilities;
import jwizardcomponent.example.SimpleDynamicWizardPanel;
import jwizardcomponent.example.SimpleLabelWizardPanel;
import jwizardcomponent.frame.SimpleLogoJWizardFrame;

import com.hardcode.driverManager.Driver;
import com.hardcode.driverManager.DriverManager;
import com.hardcode.driverManager.WriterManager;
import com.iver.cit.gvsig.fmap.edition.ISpatialWriter;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.gui.cad.MyFinishAction;
import com.iver.cit.gvsig.gui.cad.panels.ChooseGeometryType;
import com.iver.cit.gvsig.gui.cad.panels.ChooseWriteDriver;
import com.iver.cit.gvsig.gui.cad.panels.JPanelFieldDefinition;

/**
 * <p>Title: JWizardComponent</p>
 * <p>Description: Swing-Based Wizard Framework for Wizards</p>
 * <p>Copyright (C) 2003 William Ready
 * 
 * <br>This library is free software; you can redistribute it and/or
 * <br>modify it under the terms of the GNU Lesser General Public
 * <br>License as published by the Free Software Foundation; either
 * <br>version 2.1 of the License, or (at your option) any later version.
 *
 * <br>This library is distributed in the hope that it will be useful,
 * <br>but WITHOUT ANY WARRANTY; without even the implied warranty of
 * <br>MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * <br>See the GNU Lesser General Public License for more details.
 *
 * <br>To receive a copy of the GNU Lesser General Public License 
 * <br>write to:  The Free Software Foundation, Inc., 
 * <br>59 Temple Place, Suite 330 
 * <br>Boston, MA 02111-1307 USA</p>
 * @author William Ready
 * @version 1.0
 */

public class SimpleDynamicLogoJWizard {

  static ImageIcon LOGO;
  

  public static void main(String [] args) {
    try {
    	
    	LOGO = new javax.swing.ImageIcon("images/package_graphics.png");
			// new ImageIcon(DefaultJWizardComponents.class.getResource("images/logo.jpeg"));

      SimpleLogoJWizardFrame wizardFrame = new SimpleLogoJWizardFrame(
          LOGO);
      wizardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      SwingUtilities.updateComponentTreeUI(wizardFrame);
      
      wizardFrame.setTitle("Nuevo tema");
      
      MyFinishAction myFinish = new MyFinishAction(wizardFrame.getWizardComponents()); 
      
	  wizardFrame.getWizardComponents().setFinishAction(myFinish);
	  

      /* wizardFrame.getWizardComponents().addWizardPanel(
          new SimpleLabelWizardPanel(wizardFrame.getWizardComponents(),
          new JLabel("Dynamic Test"))); */
      LayerFactory.setDriversPath("c:/workspace/_fwAndami/gvSIG/extensiones/com.iver.cit.gvsig/drivers");
      
      DriverManager writerManager = LayerFactory.getDM(); 
      ArrayList spatialDrivers = new ArrayList();
      String[] writerNames = writerManager.getDriverNames();
      for (int i=0; i<writerNames.length; i++)
      {
    	  Driver drv = writerManager.getDriver(writerNames[i]);
    	  if (drv instanceof ISpatialWriter)
    		  spatialDrivers.add(drv.getName());
      }
      
      

      wizardFrame.getWizardComponents().addWizardPanel(
    		  new ChooseWriteDriver(wizardFrame.getWizardComponents(),
              "Dynamic Test", (String[]) spatialDrivers.toArray(new String[0]))); 
      
      wizardFrame.getWizardComponents().addWizardPanel(
              new ChooseGeometryType(wizardFrame.getWizardComponents()));

      
      wizardFrame.getWizardComponents().addWizardPanel(
          new JPanelFieldDefinition(wizardFrame.getWizardComponents()));

      wizardFrame.getWizardComponents().addWizardPanel(
          new SimpleLabelWizardPanel(wizardFrame.getWizardComponents(),
          new JLabel("Done!")));
      wizardFrame.setSize(500, 300);
      
      Utilities.centerComponentOnScreen(wizardFrame);
      wizardFrame.show();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}