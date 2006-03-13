package com.iver.cit.gvsig.gui.cad.panels;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import jwizardcomponent.JWizardComponents;
import jwizardcomponent.JWizardPanel;

import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.edition.ISpatialWriter;

/**
 * @author fjp
 * 
 * Panel para que el usuario seleccione el driver que va a utilizar para
 * crear un tema desde cero.
 *
 */
public class ChooseGeometryType extends JWizardPanel {

	private JLabel lblSelecGeometryType = null;
	private JPanel jPanelGeometryTypeOptions = null;
	private JRadioButton jRadioButtonPoint = null;
	private JRadioButton jRadioButtonLine = null;
	private JRadioButton jRadioButtonPolygon = null;
	private JRadioButton jRadioButtonMulti = null;
	private JRadioButton jRadioButtonMultiPoint = null;

	public ChooseGeometryType(JWizardComponents wizardComponents) {
		super(wizardComponents);
		initialize();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        lblSelecGeometryType = new JLabel();
        lblSelecGeometryType.setText("please_select_geometry_type");
        lblSelecGeometryType.setBounds(new java.awt.Rectangle(33,10,145,15));
        this.setLayout(null);
        this.setSize(new java.awt.Dimension(434,232));
        this.add(lblSelecGeometryType, null);
        this.add(getJPanelGeometryTypeOptions(), null);
			
	}

	/**
	 * This method initializes jPanelGeometryTypeOptions	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelGeometryTypeOptions() {
		if (jPanelGeometryTypeOptions == null) {
			jPanelGeometryTypeOptions = new JPanel();
			jPanelGeometryTypeOptions.setLayout(null);
			jPanelGeometryTypeOptions.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Geometry_types", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
			jPanelGeometryTypeOptions.setPreferredSize(new java.awt.Dimension(300,400));
			jPanelGeometryTypeOptions.setBounds(new java.awt.Rectangle(33,37,355,172));
			jPanelGeometryTypeOptions.add(getJRadioButtonPoint(), null);
			jPanelGeometryTypeOptions.add(getJRadioButtonLine(), null);
			jPanelGeometryTypeOptions.add(getJRadioButtonPolygon(), null);
			jPanelGeometryTypeOptions.add(getJRadioButtonMulti(), null);
			jPanelGeometryTypeOptions.add(getJRadioButtonMultiPoint(), null);
			
		    //Group the radio buttons.
		    ButtonGroup group = new ButtonGroup();
		    group.add(getJRadioButtonPoint());
		    group.add(getJRadioButtonLine());
		    group.add(getJRadioButtonPolygon());
		    group.add(getJRadioButtonMulti());
		    group.add(getJRadioButtonMultiPoint());
			
		}
		return jPanelGeometryTypeOptions;
	}

	/**
	 * This method initializes jRadioButtonPoint	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButtonPoint() {
		if (jRadioButtonPoint == null) {
			jRadioButtonPoint = new JRadioButton();
			jRadioButtonPoint.setText("POINT_type");
			jRadioButtonPoint.setBounds(new java.awt.Rectangle(19,31,325,23));
		}
		return jRadioButtonPoint;
	}

	/**
	 * This method initializes jRadioButtonLine	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButtonLine() {
		if (jRadioButtonLine == null) {
			jRadioButtonLine = new JRadioButton();
			jRadioButtonLine.setText("LINE_type");
			jRadioButtonLine.setSelected(true);
			jRadioButtonLine.setBounds(new java.awt.Rectangle(19,81,325,23));
		}
		return jRadioButtonLine;
	}

	/**
	 * This method initializes jRadioButtonPolygon	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButtonPolygon() {
		if (jRadioButtonPolygon == null) {
			jRadioButtonPolygon = new JRadioButton();
			jRadioButtonPolygon.setText("POLYGON_type");
			jRadioButtonPolygon.setBounds(new java.awt.Rectangle(19,106,325,23));
		}
		return jRadioButtonPolygon;
	}

	/**
	 * This method initializes jRadioButtonMulti	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButtonMulti() {
		if (jRadioButtonMulti == null) {
			jRadioButtonMulti = new JRadioButton();
			jRadioButtonMulti.setText("MULTI_type");
			jRadioButtonMulti.setBounds(new java.awt.Rectangle(19,131,325,23));
		}
		return jRadioButtonMulti;
	}

	/**
	 * This method initializes jRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButtonMultiPoint() {
		if (jRadioButtonMultiPoint == null) {
			jRadioButtonMultiPoint = new JRadioButton();
			jRadioButtonMultiPoint.setText("MULTIPOINT_type");
			jRadioButtonMultiPoint.setBounds(new java.awt.Rectangle(19,56,325,23));
		}
		return jRadioButtonMultiPoint;
	}

	/**
	 * En función de qué tipo de driver sea, habilitamos o deshabilitamos
	 * las opciones. Por ejemplo, si es de tipo shp, deshabilitamos
	 * multi_type
	 * @param writer
	 */
	public void setDriver(ISpatialWriter writer) {
		// En función de qué tipo de driver sea, habilitamos o deshabilitamos
		// las opciones. Por ejemplo, si es de tipo shp, deshabilitamos
		// multi_type
		System.out.println("Writer seleccionado:" + writer.getName());
		getJRadioButtonPoint().setEnabled(writer.canWriteGeometry(FShape.POINT));
		getJRadioButtonMultiPoint().setEnabled(writer.canWriteGeometry(FShape.MULTIPOINT));
		getJRadioButtonLine().setEnabled(writer.canWriteGeometry(FShape.LINE));
		getJRadioButtonPolygon().setEnabled(writer.canWriteGeometry(FShape.POLYGON));
		getJRadioButtonMulti().setEnabled(writer.canWriteGeometry(FShape.MULTI));
	}
	
	public int getSelectedGeometryType()
	{
		if (getJRadioButtonPoint().isSelected())
			return FShape.POINT;
		if (getJRadioButtonMultiPoint().isSelected())
			return FShape.MULTIPOINT;
		if (getJRadioButtonLine().isSelected())
			return FShape.LINE;
		if (getJRadioButtonPolygon().isSelected())
			return FShape.POLYGON;
		if (getJRadioButtonMulti().isSelected())
			return FShape.MULTI;		
		
		return -1;
	}


}  //  @jve:decl-index=0:visual-constraint="10,10"
