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
 * Revision 1.4  2007-09-19 16:01:06  jaume
 * removed unnecessary imports
 *
 * Revision 1.3  2007/05/02 16:55:13  caballero
 * Editing colors
 *
 * Revision 1.1.2.2  2007/05/02 15:49:56  caballero
 * Editing colors
 *
 * Revision 1.1.2.1  2007/05/02 07:50:56  caballero
 * Editing colors
 *
 * Revision 1.4.4.9  2007/02/16 10:30:36  caballero
 * factor 0 incorrecto
 *
 * Revision 1.4.4.8  2006/11/22 01:45:47  luisw2
 * Recuperados cambios de la RC2 que normalizan la gestión de CRSs
 *
 * Revision 1.4.4.7  2006/11/15 00:08:09  jjdelcerro
 * *** empty log message ***
 *
 * Revision 1.21  2006/11/08 10:57:55  jaume
 * remove unecessary imports
 *
 * Revision 1.20  2006/10/25 08:34:06  jmvivo
 * LLamado al PluginServices.getText para las unidades de medida del los combo
 *
 * Revision 1.19  2006/10/04 07:23:31  jaume
 * refactored ambiguous methods and field names and added some more features for preference pages
 *
 * Revision 1.18  2006/10/03 09:52:38  jaume
 * restores to meters
 *
 * Revision 1.17  2006/10/03 09:19:12  jaume
 * *** empty log message ***
 *
 * Revision 1.16  2006/10/03 07:26:08  jaume
 * *** empty log message ***
 *
 * Revision 1.15  2006/10/02 15:30:29  jaume
 * *** empty log message ***
 *
 * Revision 1.14  2006/10/02 13:52:34  jaume
 * organize impots
 *
 * Revision 1.13  2006/10/02 13:38:23  jaume
 * *** empty log message ***
 *
 * Revision 1.12  2006/10/02 11:49:23  jaume
 * *** empty log message ***
 *
 * Revision 1.11  2006/09/28 12:04:21  jaume
 * default selection color now configurable
 *
 * Revision 1.10  2006/09/25 10:17:15  caballero
 * Projection
 *
 * Revision 1.9  2006/09/15 10:41:30  caballero
 * extensibilidad de documentos
 *
 * Revision 1.8  2006/09/14 15:43:48  jaume
 * *** empty log message ***
 *
 * Revision 1.7  2006/09/14 15:42:38  jaume
 * *** empty log message ***
 *
 * Revision 1.6  2006/09/14 06:57:18  jaume
 * *** empty log message ***
 *
 * Revision 1.5  2006/09/12 15:56:50  jaume
 * Default Projection now customizable
 *
 * Revision 1.4  2006/08/29 07:21:08  cesar
 * Rename com.iver.cit.gvsig.fmap.Fmap class to com.iver.cit.gvsig.fmap.MapContext
 *
 * Revision 1.3  2006/08/22 12:30:59  jaume
 * *** empty log message ***
 *
 * Revision 1.2  2006/08/22 07:36:04  jaume
 * *** empty log message ***
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.iver.andami.PluginServices;
import com.iver.andami.preferences.AbstractPreferencePage;
import com.iver.andami.preferences.StoreException;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.symbols.ILineSymbol;
import com.iver.cit.gvsig.gui.cad.DefaultCADTool;
import com.iver.cit.gvsig.gui.panels.ColorChooserPanel;
import com.iver.utiles.StringUtilities;
import com.iver.utiles.XMLEntity;

/**
 * Editing configuration page. <b><b> Here the user can establish what settings
 * wants to use by default regarding to editing.
 * 
 * 
 * @author Vicente Caballero Navarro
 */
public class EditingPage extends AbstractPreferencePage {

    public static String DEFAULT_HANDLER_COLOR = "default_editing_handler_color";
    public static String DEFAULT_HANDLER_OUTLINE_COLOR = "default_editing_handler_outline_color";

    public static String DEFAULT_SELECTION_COLOR = "default_editing_selection_color";
    // public static String DEFAULT_SELECTION_OUTLINE_COLOR =
    // "default_editing_selection_outline_color";

    public static String DEFAULT_AXIS_REFERENCES_COLOR = "default_editing_axis_references_color";
    // public static String DEFAULT_AXIS_REFERENCES_OUTLINE_COLOR =
    // "default_editing_axis_references_outline_color";

    public static String DEFAULT_RECTANGLE_SELECTION_COLOR = "default_editing_rectangle_selection_color";
    // public static String DEFAULT_GEOMETRY_SELECTION_OUTLINE_COLOR =
    // "default_editing_geometry_selection_outline_color";

    protected String id;
    private ImageIcon icon;
    private ColorChooserPanel jccDefaultSelectionColor;
    // private ColorChooserPanel jccDefaultSelectionOutLineColor;
    private ColorChooserPanel jccDefaultAxisReferencesColor;
    // private ColorChooserPanel jccDefaultAxisReferencesOutLineColor;
    private ColorChooserPanel jccDefaultGeometrySelectionColor;
    // private ColorChooserPanel jccDefaultGeometrySelectionOutLineColor;

    private ColorChooserPanel jccDefaultHandlerColor;
    private ColorChooserPanel jccDefaultHandlerOutLineColor;

    private boolean panelStarted = false;
    private JSlider jsDefaultSelectionAlpha;
    private JSlider jsDefaultAxisReferencesAlpha;
    private JSlider jsDefaultGeometrySelectionAlpha;
    private JSlider jsDefaultHandlerAlpha;

    /**
     * Creates a new panel containing View preferences settings.
     * 
     */
    public EditingPage() {
	super();
	id = this.getClass().getName();
    }

    @Override
    public void initializeValues() {
	if (!panelStarted)
	    getPanel();

	PluginServices ps = PluginServices.getPluginServices(this);
	XMLEntity xml = ps.getPersistentXML();

	// Default selection color
	if (xml.contains(DEFAULT_SELECTION_COLOR)) {
	    Color color = StringUtilities.string2Color(xml
		    .getStringProperty(DEFAULT_SELECTION_COLOR));
	    jccDefaultSelectionColor.setColor(color);
	    jccDefaultSelectionColor.setAlpha(color.getAlpha());
	    // Color
	    // colorOutLine=StringUtilities.string2Color(xml.getStringProperty(DEFAULT_SELECTION_OUTLINE_COLOR));
	    // jccDefaultSelectionOutLineColor.setColor(colorOutLine);
	    // jccDefaultSelectionOutLineColor.setAlpha(color.getAlpha());
	    jsDefaultSelectionAlpha.setValue(color.getAlpha());
	    DefaultCADTool.selectionSymbol = SymbologyFactory
		    .createDefaultSymbolByShapeType(FShape.MULTI, color);
	    // DefaultCADTool.selectionSymbol.setOutlineColor(colorOutLine);
	} else {
	    Color color = Color.RED;
	    jccDefaultSelectionColor.setColor(color);
	    jccDefaultSelectionColor.setAlpha(color.getAlpha());
	    // jccDefaultSelectionOutLineColor.setColor(color.darker());
	    // jccDefaultSelectionOutLineColor.setAlpha(color.getAlpha());
	    jsDefaultSelectionAlpha.setValue(color.getAlpha());
	    DefaultCADTool.selectionSymbol = SymbologyFactory
		    .createDefaultSymbolByShapeType(FShape.MULTI, color);
	    // DefaultCADTool.selectionSymbol.setOutlineColor(color.darker());
	}

	// Default axis references color
	if (xml.contains(DEFAULT_AXIS_REFERENCES_COLOR)) {
	    Color color = StringUtilities.string2Color(xml
		    .getStringProperty(DEFAULT_AXIS_REFERENCES_COLOR));
	    jccDefaultAxisReferencesColor.setColor(color);
	    jccDefaultAxisReferencesColor.setAlpha(color.getAlpha());
	    // Color
	    // colorOutLine=StringUtilities.string2Color(xml.getStringProperty(DEFAULT_AXIS_REFERENCES_OUTLINE_COLOR));
	    // jccDefaultAxisReferencesOutLineColor.setColor(colorOutLine);
	    // jccDefaultAxisReferencesOutLineColor.setAlpha(color.getAlpha());
	    jsDefaultAxisReferencesAlpha.setValue(color.getAlpha());
	    DefaultCADTool.axisReferencesSymbol = SymbologyFactory
		    .createDefaultSymbolByShapeType(FShape.MULTI, color);
	} else {
	    Color color = new Color(100, 100, 100, 100);
	    jccDefaultAxisReferencesColor.setColor(color);
	    jccDefaultAxisReferencesColor.setAlpha(color.getAlpha());
	    // jccDefaultAxisReferencesOutLineColor.setColor(color.darker());
	    // jccDefaultAxisReferencesOutLineColor.setAlpha(color.getAlpha());
	    jsDefaultAxisReferencesAlpha.setValue(color.getAlpha());
	    DefaultCADTool.axisReferencesSymbol = SymbologyFactory
		    .createDefaultSymbolByShapeType(FShape.MULTI, color);
	}

	// Default geometry selection color
	if (xml.contains(DEFAULT_RECTANGLE_SELECTION_COLOR)) {
	    Color color = StringUtilities.string2Color(xml
		    .getStringProperty(DEFAULT_RECTANGLE_SELECTION_COLOR));
	    jccDefaultGeometrySelectionColor.setColor(color);
	    jccDefaultGeometrySelectionColor.setAlpha(color.getAlpha());
	    // Color
	    // colorOutLine=StringUtilities.string2Color(xml.getStringProperty(DEFAULT_GEOMETRY_SELECTION_OUTLINE_COLOR));
	    // jccDefaultGeometrySelectionOutLineColor.setColor(colorOutLine);
	    // jccDefaultGeometrySelectionOutLineColor.setAlpha(color.getAlpha());
	    jsDefaultGeometrySelectionAlpha.setValue(color.getAlpha());
	    DefaultCADTool.geometrySelectSymbol = SymbologyFactory
		    .createDefaultSymbolByShapeType(FShape.MULTI, color);
	} else {
	    Color color = new Color(255, 0, 0, 100);
	    jccDefaultGeometrySelectionColor.setColor(color);
	    jccDefaultGeometrySelectionColor.setAlpha(color.getAlpha());
	    // jccDefaultGeometrySelectionOutLineColor.setColor(color.darker());
	    // jccDefaultGeometrySelectionOutLineColor.setAlpha(color.getAlpha());
	    jsDefaultGeometrySelectionAlpha.setValue(color.getAlpha());
	    DefaultCADTool.geometrySelectSymbol = SymbologyFactory
		    .createDefaultSymbolByShapeType(FShape.MULTI, color);
	}

	// Default handler color
	if (xml.contains(DEFAULT_HANDLER_COLOR)) {
	    Color color = StringUtilities.string2Color(xml
		    .getStringProperty(DEFAULT_HANDLER_COLOR));
	    jccDefaultHandlerColor.setColor(color);
	    jccDefaultHandlerColor.setAlpha(color.getAlpha());
	    Color colorOutLine = StringUtilities.string2Color(xml
		    .getStringProperty(DEFAULT_HANDLER_OUTLINE_COLOR));
	    jccDefaultHandlerOutLineColor.setColor(colorOutLine);
	    jccDefaultHandlerOutLineColor.setAlpha(color.getAlpha());
	    jsDefaultGeometrySelectionAlpha.setValue(color.getAlpha());
	    DefaultCADTool.handlerSymbol = SymbologyFactory
		    .createDefaultSymbolByShapeType(FShape.MULTI, color);
	    if (DefaultCADTool.handlerSymbol instanceof ILineSymbol) {
		((ILineSymbol) DefaultCADTool.handlerSymbol)
			.setLineColor(colorOutLine);
	    }
	} else {
	    Color color = Color.ORANGE;
	    jccDefaultHandlerColor.setColor(color);
	    jccDefaultHandlerColor.setAlpha(color.getAlpha());
	    jccDefaultHandlerOutLineColor.setColor(color.darker());
	    jccDefaultHandlerOutLineColor.setAlpha(color.getAlpha());
	    jsDefaultHandlerAlpha.setValue(color.getAlpha());
	    DefaultCADTool.handlerSymbol = SymbologyFactory
		    .createDefaultSymbolByShapeType(FShape.MULTI, color);
	    if (DefaultCADTool.handlerSymbol instanceof ILineSymbol) {
		((ILineSymbol) DefaultCADTool.handlerSymbol).setLineColor(color
			.darker());
	    }
	}
    }

    @Override
    public String getID() {
	return id;
    }

    @Override
    public String getTitle() {
	return PluginServices.getText(this, "editing");
    }

    @Override
    public JPanel getPanel() {
	if (panelStarted)
	    return this;
	panelStarted = true;

	// just a separator
	addComponent(new JLabel(" "));

	addComponent(new JLabel(PluginServices.getText(this,
		"change_the_editing_colors")));
	// default selection color chooser
	JPanel selectionPanel = new JPanel();
	selectionPanel.setBorder(new TitledBorder(PluginServices.getText(this,
		"options.editing.default_selection_color")));
	selectionPanel.setLayout(new GridBagLayout());
	selectionPanel.add(new JLabel(PluginServices.getText(this, "fill")));
	selectionPanel.add(jccDefaultSelectionColor = new ColorChooserPanel());
	// selectionPanel.add(new
	// JLabel(PluginServices.getText(this,"outline")));
	// selectionPanel.add(jccDefaultSelectionOutLineColor=new
	// ColorChooserPanel());

	// JPanel alphaSelectionPanel= new JPanel();
	selectionPanel.add(new JLabel(PluginServices.getText(this, "alpha")));
	selectionPanel.add(jsDefaultSelectionAlpha = new JSlider(0, 255));
	jsDefaultSelectionAlpha.setPreferredSize(new Dimension(100, 30));

	jsDefaultSelectionAlpha.addChangeListener(new ChangeListener() {
	    @Override
	    public void stateChanged(ChangeEvent e) {
		jccDefaultSelectionColor.setAlpha(((JSlider) e.getSource())
			.getValue());
		// jccDefaultSelectionOutLineColor.setAlpha(((JSlider)e.getSource()).getValue());

	    }
	});

	addComponent(selectionPanel);
	addComponent(new JLabel(" "));

	// default selection color chooser
	JPanel modifyPanel = new JPanel();
	modifyPanel.setBorder(new TitledBorder(PluginServices.getText(this,
		"options.editing.default_axis_references_color")));
	modifyPanel.setLayout(new GridBagLayout());
	modifyPanel.add(new JLabel(PluginServices.getText(this, "fill")));
	modifyPanel
		.add(jccDefaultAxisReferencesColor = new ColorChooserPanel());
	// modifyPanel.add(new JLabel(PluginServices.getText(this,"outline")));
	// modifyPanel.add(jccDefaultAxisReferencesOutLineColor=new
	// ColorChooserPanel());

	// JPanel alphaModifyPanel= new JPanel();
	// alphaModifyPanel.setPreferredSize(new Dimension(120,30));
	modifyPanel.add(new JLabel(PluginServices.getText(this, "alpha")));
	modifyPanel.add(jsDefaultAxisReferencesAlpha = new JSlider(0, 255));
	jsDefaultAxisReferencesAlpha.setPreferredSize(new Dimension(100, 30));

	jsDefaultAxisReferencesAlpha.addChangeListener(new ChangeListener() {
	    @Override
	    public void stateChanged(ChangeEvent e) {
		jccDefaultAxisReferencesColor.setAlpha(((JSlider) e.getSource())
			.getValue());
		// jccDefaultAxisReferencesOutLineColor.setAlpha(((JSlider)e.getSource()).getValue());

	    }
	});

	addComponent(modifyPanel);
	addComponent(new JLabel(" "));

	// default drawing color chooser
	JPanel drawingPanel = new JPanel();
	drawingPanel.setBorder(new TitledBorder(PluginServices.getText(this,
		"options.editing.default_rectangle_selection_color")));
	drawingPanel.setLayout(new GridBagLayout());
	drawingPanel.add(new JLabel(PluginServices.getText(this, "fill")));
	drawingPanel
		.add(jccDefaultGeometrySelectionColor = new ColorChooserPanel());
	// drawingPanel.add(new JLabel(PluginServices.getText(this,"outline")));
	// drawingPanel.add(jccDefaultGeometrySelectionOutLineColor=new
	// ColorChooserPanel());

	// JPanel alphaDrawingPanel= new JPanel();
	// alphaDrawingPanel.setPreferredSize(new Dimension(120,30));
	drawingPanel.add(new JLabel(PluginServices.getText(this, "alpha")));
	drawingPanel.add(jsDefaultGeometrySelectionAlpha = new JSlider(0, 255));
	jsDefaultGeometrySelectionAlpha
		.setPreferredSize(new Dimension(100, 30));

	jsDefaultGeometrySelectionAlpha.addChangeListener(new ChangeListener() {
	    @Override
	    public void stateChanged(ChangeEvent e) {
		jccDefaultGeometrySelectionColor.setAlpha(((JSlider) e
			.getSource()).getValue());
		// jccDefaultGeometrySelectionOutLineColor.setAlpha(((JSlider)e.getSource()).getValue());

	    }
	});

	addComponent(drawingPanel);
	addComponent(new JLabel(" "));

	// default selection color chooser
	JPanel handlerPanel = new JPanel();
	handlerPanel.setBorder(new TitledBorder(PluginServices.getText(this,
		"options.editing.default_handler_color")));
	handlerPanel.setLayout(new GridBagLayout());
	handlerPanel.add(new JLabel(PluginServices.getText(this, "fill")));
	handlerPanel.add(jccDefaultHandlerColor = new ColorChooserPanel());
	handlerPanel.add(new JLabel(PluginServices.getText(this, "outline")));
	handlerPanel
		.add(jccDefaultHandlerOutLineColor = new ColorChooserPanel());

	// JPanel alphaModifyPanel= new JPanel();
	// alphaModifyPanel.setPreferredSize(new Dimension(120,30));
	handlerPanel.add(new JLabel(PluginServices.getText(this, "alpha")));
	handlerPanel.add(jsDefaultHandlerAlpha = new JSlider(0, 255));
	jsDefaultHandlerAlpha.setPreferredSize(new Dimension(100, 30));

	jsDefaultHandlerAlpha.addChangeListener(new ChangeListener() {
	    @Override
	    public void stateChanged(ChangeEvent e) {
		jccDefaultHandlerColor.setAlpha(((JSlider) e.getSource())
			.getValue());
		jccDefaultHandlerOutLineColor.setAlpha(((JSlider) e.getSource())
			.getValue());

	    }
	});

	addComponent(handlerPanel);
	addComponent(new JLabel(" "));

	initializeValues();
	return this;
    }

    @Override
    public void storeValues() throws StoreException {
	Color selectionColor, modifyColor, drawingColor, handlerColor, handlerOutLineColor;// selectionOutLineColor,
											   // modifyOutLineColor,
											   // drawingOutLineColor;
	selectionColor = jccDefaultSelectionColor.getColor();
	// selectionOutLineColor = jccDefaultSelectionOutLineColor.getColor();
	modifyColor = jccDefaultAxisReferencesColor.getColor();
	// modifyOutLineColor = jccDefaultAxisReferencesOutLineColor.getColor();
	drawingColor = jccDefaultGeometrySelectionColor.getColor();
	// drawingOutLineColor =
	// jccDefaultGeometrySelectionOutLineColor.getColor();

	handlerColor = jccDefaultHandlerColor.getColor();
	handlerOutLineColor = jccDefaultHandlerOutLineColor.getColor();

	PluginServices ps = PluginServices.getPluginServices(this);
	XMLEntity xml = ps.getPersistentXML();
	xml.putProperty(DEFAULT_SELECTION_COLOR,
		StringUtilities.color2String(selectionColor));
	// xml.putProperty(DEFAULT_SELECTION_OUTLINE_COLOR,
	// StringUtilities.color2String(selectionOutLineColor));
	DefaultCADTool.selectionSymbol = SymbologyFactory
		.createDefaultSymbolByShapeType(FShape.MULTI, selectionColor);
	// DefaultCADTool.selectionSymbol.setOutlineColor(selectionOutLineColor);

	xml.putProperty(DEFAULT_AXIS_REFERENCES_COLOR,
		StringUtilities.color2String(modifyColor));
	// xml.putProperty(DEFAULT_AXIS_REFERENCES_OUTLINE_COLOR,
	// StringUtilities.color2String(modifyOutLineColor));
	DefaultCADTool.axisReferencesSymbol = SymbologyFactory
		.createDefaultSymbolByShapeType(FShape.MULTI, modifyColor);
	// DefaultCADTool.axisReferencesSymbol.setOutlineColor(modifyOutLineColor);

	xml.putProperty(DEFAULT_RECTANGLE_SELECTION_COLOR,
		StringUtilities.color2String(drawingColor));
	// xml.putProperty(DEFAULT_GEOMETRY_SELECTION_OUTLINE_COLOR,
	// StringUtilities.color2String(drawingOutLineColor));
	DefaultCADTool.geometrySelectSymbol = SymbologyFactory
		.createDefaultSymbolByShapeType(FShape.MULTI, drawingColor);
	// DefaultCADTool.geometrySelectSymbol.setOutlineColor(drawingOutLineColor);

	xml.putProperty(DEFAULT_HANDLER_COLOR,
		StringUtilities.color2String(handlerColor));
	xml.putProperty(DEFAULT_HANDLER_OUTLINE_COLOR,
		StringUtilities.color2String(handlerOutLineColor));
	DefaultCADTool.handlerSymbol = SymbologyFactory
		.createDefaultSymbolByShapeType(FShape.MULTI, handlerColor);
	if (DefaultCADTool.handlerSymbol instanceof ILineSymbol) {
	    ((ILineSymbol) DefaultCADTool.handlerSymbol)
		    .setLineColor(handlerOutLineColor);
	}
    }

    @Override
    public void initializeDefaults() {
	jccDefaultSelectionColor.setColor(Color.ORANGE);
	// jccDefaultSelectionOutLineColor.setColor(Color.ORANGE.darker());
	jsDefaultSelectionAlpha.setValue(255);

	jccDefaultAxisReferencesColor.setColor(new Color(100, 100, 100, 100));
	// jccDefaultAxisReferencesOutLineColor.setColor(new Color(100, 100,
	// 100, 100).darker());
	jsDefaultAxisReferencesAlpha.setValue(100);

	jccDefaultGeometrySelectionColor.setColor(new Color(255, 0, 0, 100));
	// jccDefaultGeometrySelectionOutLineColor.setColor(new Color(255, 0, 0,
	// 100).darker());
	jsDefaultGeometrySelectionAlpha.setValue(100);

	jccDefaultHandlerColor.setColor(new Color(255, 0, 0, 100));
	jccDefaultHandlerOutLineColor.setColor(new Color(255, 0, 0, 100)
		.darker());
	jsDefaultHandlerAlpha.setValue(100);

    }

    @Override
    public ImageIcon getIcon() {
	if (icon == null) {
	    icon = PluginServices.getIconTheme().get("edition-properties");
	}

	return icon;
    }

    @Override
    public boolean isValueChanged() {
	return super.hasChanged();
    }

    @Override
    public void setChangesApplied() {
	setChanged(false);
    }
}
