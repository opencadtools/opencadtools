package com.iver.cit.gvsig.gui.cad.panels.matrix;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.gvsig.gui.beans.AcceptCancelPanel;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.gui.cad.tools.MatrixCADTool;
import com.iver.utiles.DoubleUtilities;

public class MatrixProperty extends JPanel implements IWindow {

    private JPanel jPanel = null;
    private JTabbedPane jTabbedPane = null; // @jve:decl-index=0:visual-constraint="10,10"
    private JPanel pRectangular = null;
    private JPanel pPolar = null;
    private JPanel pNorth = null;
    private JLabel lblRows = null;
    private JTextField txtRows = null;
    private JLabel lblColumns = null;
    private JTextField txtColumns = null;
    private JPanel pCenter = null;
    private JLabel lblDistRows = null;
    private JTextField txtDistRows = null;
    private JLabel lblDistColumns = null;
    private JTextField txtDistColumns = null;
    private JLabel lblRotation = null;
    private JTextField txtRotation = null;
    private AcceptCancelPanel jPanel1;
    private MatrixOperations operations;
    private JPanel pNorthPolar = null;
    private JPanel pCenterPolar = null;
    private JLabel lblCenter = null;
    private JLabel lblx = null;
    private JTextField txtX = null;
    private JLabel lblY = null;
    private JTextField txtY = null;
    private JLabel lblNum = null;
    private JTextField txtNum = null;
    private JCheckBox chbRotateElements = null;
    private JButton bLagXY = null;
    private JButton bLagY = null;
    private JButton bLagX = null;
    private JButton bRotation = null;
    private MatrixCADTool matrixCADTool;
    private static ImageIcon ilagXY = null;
    private static ImageIcon iaddpoint = null;

    /**
     * This is the default constructor
     */
    public MatrixProperty() {
	super();
	initialize();

    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
	ilagXY = PluginServices.getIconTheme().get(
		"edition-geometrymatrix-lagxy");
	iaddpoint = PluginServices.getIconTheme().get(
		"edition-geometrymatrix-addpoint");
	this.setLayout(new BorderLayout());
	this.setSize(369, 275);
	this.add(getJTabbedPane(), java.awt.BorderLayout.CENTER);
	this.add(getJPanel(), java.awt.BorderLayout.EAST);
	this.add(getJPanel1(), java.awt.BorderLayout.SOUTH);

    }

    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel() {
	if (jPanel == null) {
	    jPanel = new JPanel();
	}
	return jPanel;
    }

    /**
     * This method initializes jTabbedPane
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getJTabbedPane() {
	if (jTabbedPane == null) {
	    jTabbedPane = new JTabbedPane();
	    jTabbedPane.setSize(new java.awt.Dimension(323, 235));
	    jTabbedPane.addTab(
		    PluginServices.getText(this, "matriz_rectangular"), null,
		    getPRectangular(), null);
	    jTabbedPane.addTab(PluginServices.getText(this, "matriz_polar"),
		    null, getPPolar(), null);
	}
	return jTabbedPane;
    }

    /**
     * This method initializes pRectangular
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getPRectangular() {
	if (pRectangular == null) {
	    pRectangular = new JPanel();
	    pRectangular.setLayout(new BorderLayout());
	    pRectangular.setPreferredSize(new Dimension(300, 200));
	    pRectangular.add(getPNorth(), java.awt.BorderLayout.NORTH);
	    pRectangular.add(getPCenter(), java.awt.BorderLayout.CENTER);
	}
	return pRectangular;
    }

    /**
     * This method initializes pPolar
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getPPolar() {
	if (pPolar == null) {
	    pPolar = new JPanel();
	    pPolar.setLayout(new BorderLayout());
	    pPolar.add(getPNorthPolar(), java.awt.BorderLayout.NORTH);
	    pPolar.add(getPCenterPolar(), java.awt.BorderLayout.CENTER);
	}
	return pPolar;
    }

    /**
     * This method initializes pNorth
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getPNorth() {
	if (pNorth == null) {
	    lblColumns = new JLabel();
	    lblColumns.setText(PluginServices.getText(this, "columns"));
	    lblRows = new JLabel();
	    lblRows.setText(PluginServices.getText(this, "rows"));
	    pNorth = new JPanel();
	    pNorth.add(lblRows, null);
	    pNorth.add(getTxtRows(), null);
	    pNorth.add(lblColumns, null);
	    pNorth.add(getTxtColumns(), null);
	}
	return pNorth;
    }

    /**
     * This method initializes txtRows
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getTxtRows() {
	if (txtRows == null) {
	    txtRows = new JTextField();
	    txtRows.setPreferredSize(new Dimension(40, 20));
	}
	return txtRows;
    }

    /**
     * This method initializes txtColumns
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getTxtColumns() {
	if (txtColumns == null) {
	    txtColumns = new JTextField();
	    txtColumns.setPreferredSize(new Dimension(40, 20));
	}
	return txtColumns;
    }

    /**
     * This method initializes pCenter
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getPCenter() {
	if (pCenter == null) {
	    lblRotation = new JLabel();
	    lblRotation.setBounds(new java.awt.Rectangle(6, 73, 141, 22));
	    lblRotation.setText(PluginServices.getText(this, "rotation"));
	    lblDistColumns = new JLabel();
	    lblDistColumns.setBounds(new java.awt.Rectangle(6, 42, 141, 22));
	    lblDistColumns
		    .setText(PluginServices.getText(this, "disp_columns"));
	    lblDistRows = new JLabel();
	    lblDistRows.setBounds(new java.awt.Rectangle(6, 10, 141, 22));
	    lblDistRows.setText(PluginServices.getText(this, "disp_rows"));
	    pCenter = new JPanel();
	    pCenter.setPreferredSize(new Dimension(200, 150));
	    pCenter.setLayout(null);
	    pCenter.add(lblDistRows, null);
	    pCenter.add(getTxtDistRows(), null);
	    pCenter.add(lblDistColumns, null);
	    pCenter.add(getTxtDistColumns(), null);
	    pCenter.add(lblRotation, null);
	    pCenter.add(getTxtRotation(), null);
	    pCenter.add(getBLagXY(), null);
	    pCenter.add(getBLagY(), null);
	    pCenter.add(getBLagX(), null);
	    pCenter.add(getBRotation(), null);
	}
	return pCenter;
    }

    /**
     * This method initializes txtDistRows
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getTxtDistRows() {
	if (txtDistRows == null) {
	    txtDistRows = new JTextField();
	    txtDistRows.setBounds(new java.awt.Rectangle(153, 11, 75, 22));
	}
	return txtDistRows;
    }

    /**
     * This method initializes txtDistColumns
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getTxtDistColumns() {
	if (txtDistColumns == null) {
	    txtDistColumns = new JTextField();
	    txtDistColumns.setBounds(new java.awt.Rectangle(153, 43, 75, 22));
	}
	return txtDistColumns;
    }

    /**
     * This method initializes txtRotation
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getTxtRotation() {
	if (txtRotation == null) {
	    txtRotation = new JTextField();
	    txtRotation.setBounds(new java.awt.Rectangle(153, 72, 75, 22));
	}
	return txtRotation;
    }

    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private AcceptCancelPanel getJPanel1() {
	if (jPanel1 == null) {
	    ActionListener okAction = new java.awt.event.ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent e) {
		    if (getJTabbedPane().getSelectedIndex() == 0) {
			operations.setRectangular(true);
			operations.setNumRows(Integer.parseInt(getTxtRows()
				.getText()));
			operations.setNumColumns(Integer
				.parseInt(getTxtColumns().getText()));
			operations.setDistRows(Double
				.parseDouble(getTxtDistRows().getText()));
			operations.setDistColumns(Double
				.parseDouble(getTxtDistColumns().getText()));
			operations.setRotation(Double
				.parseDouble(getTxtRotation().getText()));
		    } else {
			operations.setRectangular(false);
			operations.setPositionX(Double.parseDouble(getTxtX()
				.getText()));
			operations.setPositionY(Double.parseDouble(getTxtY()
				.getText()));
			operations.setNum(Integer.parseInt(getTxtNum()
				.getText()));
			operations.setRotateElements(getChbRotateElements()
				.isSelected());
		    }
		    operations.setAccepted(true);

		    if (PluginServices.getMainFrame() == null) {
			((JDialog) (getParent().getParent().getParent()
				.getParent())).dispose();
		    } else {
			PluginServices.getMDIManager().closeWindow(
				MatrixProperty.this);
		    }
		}
	    };
	    ActionListener cancelAction = new java.awt.event.ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent e) {
		    operations.setAccepted(false);
		    if (PluginServices.getMainFrame() != null) {
			PluginServices.getMDIManager().closeWindow(
				MatrixProperty.this);
		    } else {
			((JDialog) (getParent().getParent().getParent()
				.getParent())).dispose();
		    }
		}
	    };
	    jPanel1 = new AcceptCancelPanel(okAction, cancelAction);
	}
	return jPanel1;
    }

    public WindowInfo getWindowInfo() {
	WindowInfo m_viewinfo = new WindowInfo(WindowInfo.MODALDIALOG);
	m_viewinfo.setTitle(PluginServices.getText(this, "matrix"));
	m_viewinfo.setHeight(this.getHeight());
	m_viewinfo.setWidth(this.getWidth());
	return m_viewinfo;
    }

    public Object getWindowProfile() {
	return WindowInfo.DIALOG_PROFILE;
    }

    public void setMatrixCADTool(MatrixCADTool tool) {
	operations = tool.getOperations();
	matrixCADTool = tool;
	refresh();
    }

    /**
     * This method initializes pNorthPolar
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getPNorthPolar() {
	if (pNorthPolar == null) {
	    lblY = new JLabel();
	    lblY.setText("Y: ");
	    lblx = new JLabel();
	    lblx.setText("X: ");
	    lblCenter = new JLabel();
	    lblCenter.setText("centro");
	    pNorthPolar = new JPanel();
	    pNorthPolar.add(lblCenter, null);
	    pNorthPolar.add(lblx, null);
	    pNorthPolar.add(getTxtX(), null);
	    pNorthPolar.add(lblY, null);
	    pNorthPolar.add(getTxtY(), null);
	}
	return pNorthPolar;
    }

    /**
     * This method initializes pCenterPolar
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getPCenterPolar() {
	if (pCenterPolar == null) {
	    lblNum = new JLabel();
	    lblNum.setText("num_elementos");
	    pCenterPolar = new JPanel();
	    pCenterPolar.add(lblNum, null);
	    pCenterPolar.add(getTxtNum(), null);
	    pCenterPolar.add(getChbRotateElements(), null);
	}
	return pCenterPolar;
    }

    /**
     * This method initializes txtX
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getTxtX() {
	if (txtX == null) {
	    txtX = new JTextField();
	    txtX.setPreferredSize(new java.awt.Dimension(50, 20));
	}
	return txtX;
    }

    /**
     * This method initializes txtY
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getTxtY() {
	if (txtY == null) {
	    txtY = new JTextField();
	    txtY.setPreferredSize(new java.awt.Dimension(50, 20));
	}
	return txtY;
    }

    /**
     * This method initializes txtNum
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getTxtNum() {
	if (txtNum == null) {
	    txtNum = new JTextField();
	    txtNum.setPreferredSize(new java.awt.Dimension(50, 20));
	}
	return txtNum;
    }

    /**
     * This method initializes chbRotateElements
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getChbRotateElements() {
	if (chbRotateElements == null) {
	    chbRotateElements = new JCheckBox();
	    chbRotateElements.setText("girar_elementos_a_medida_que_se_copian");
	}
	return chbRotateElements;
    }

    /**
     * This method initializes bLagXY
     * 
     * @return javax.swing.JButton
     */
    public JButton getBLagXY() {
	if (bLagXY == null) {
	    bLagXY = new JButton();
	    bLagXY.setBounds(new java.awt.Rectangle(232, 8, 30, 56));
	    bLagXY.setIcon(ilagXY);
	    bLagXY.addActionListener(new java.awt.event.ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent e) {
		    operations.setAccepted(false);
		    matrixCADTool.addOption("lagXY");
		}
	    });
	}
	return bLagXY;
    }

    /**
     * This method initializes bLagY
     * 
     * @return javax.swing.JButton
     */
    public JButton getBLagY() {
	if (bLagY == null) {
	    bLagY = new JButton();
	    bLagY.setBounds(new java.awt.Rectangle(270, 7, 28, 26));
	    bLagY.setIcon(iaddpoint);
	    bLagY.addActionListener(new java.awt.event.ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent e) {
		    operations.setAccepted(false);
		    matrixCADTool.addOption("lagY");
		}
	    });
	}
	return bLagY;
    }

    /**
     * This method initializes bLagX
     * 
     * @return javax.swing.JButton
     */
    public JButton getBLagX() {
	if (bLagX == null) {
	    bLagX = new JButton();
	    bLagX.setBounds(new java.awt.Rectangle(270, 38, 28, 26));
	    bLagX.setIcon(iaddpoint);
	    bLagX.addActionListener(new java.awt.event.ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent e) {
		    operations.setAccepted(false);
		    matrixCADTool.addOption("lagX");
		}
	    });
	}
	return bLagX;
    }

    /**
     * This method initializes bRotation
     * 
     * @return javax.swing.JButton
     */
    public JButton getBRotation() {
	if (bRotation == null) {
	    bRotation = new JButton();
	    bRotation.setBounds(new java.awt.Rectangle(232, 70, 28, 26));
	    bRotation.setIcon(iaddpoint);
	    bRotation.addActionListener(new java.awt.event.ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent e) {
		    operations.setAccepted(false);
		    matrixCADTool.addOption("rotation");
		}
	    });
	}
	return bRotation;
    }

    public void refresh() {
	refreshLagX();
	refreshLagY();
	refreshRotation();
	getTxtColumns().setText(String.valueOf(operations.getNumColumns()));
	getTxtRows().setText(String.valueOf(operations.getNumRows()));
	getTxtNum().setText(String.valueOf(operations.getNum()));
	getTxtX().setText(String.valueOf(operations.getPositionX()));
	getTxtY().setText(String.valueOf(operations.getPositionY()));
    }

    public void refreshLagX() {
	getTxtDistColumns().setText(
		String.valueOf(DoubleUtilities.format(
			operations.getDistColumns(), '.', 2)));

    }

    public void refreshLagY() {
	getTxtDistRows().setText(
		String.valueOf(DoubleUtilities.format(operations.getDistRows(),
			'.', 2)));

    }

    public void refreshRotation() {
	getTxtRotation().setText(
		String.valueOf(DoubleUtilities.format(operations.getRotation(),
			'.', 2)));

    }

} // @jve:decl-index=0:visual-constraint="10,10"
