package com.iver.cit.gvsig.gui.cad.panels;

import java.awt.BorderLayout;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import jwizardcomponent.JWizardComponents;
import jwizardcomponent.JWizardPanel;

/**
 * @author fjp
 * 
 * Panel para que el usuario seleccione el driver que va a utilizar para
 * crear un tema desde cero.
 *
 */
public class JPanelFieldDefinition extends JWizardPanel {


	private JLabel jLabel = null;
	private JScrollPane jScrollPane = null;
	private JTable jTable = null;
	private JPanel jPanelEast = null;
	private JButton jButtonAddField = null;
	private JButton jButtonDeleteField = null;


	public JPanelFieldDefinition(JWizardComponents wizardComponents) {
		super(wizardComponents, null);
		initialize();
		// TODO Auto-generated constructor stub
	}
	

	/* (non-Javadoc)
	 * @see jwizardcomponent.JWizardPanel#next()
	 */
	public void next() {
		// TODO Auto-generated method stub
		super.next();
		getWizardComponents().getFinishButton().setVisible(true);
		getWizardComponents().getFinishButton().setEnabled(true);
	}


	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        jLabel = new JLabel();
        jLabel.setText("define_fields");
        this.setLayout(new BorderLayout(5,5));
        this.setSize(new java.awt.Dimension(437,232));
        this.add(jLabel, java.awt.BorderLayout.NORTH);
        this.add(getJScrollPane(), java.awt.BorderLayout.CENTER);
        this.add(getJPanelEast(), java.awt.BorderLayout.EAST);
	}


	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJTable());
		}
		return jScrollPane;
	}


	/**
	 * This method initializes jTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getJTable() {
		if (jTable == null) {
			jTable = new JTable();
			DefaultTableModel tm = (DefaultTableModel) jTable.getModel();
			tm.addColumn("Field");
			
			// TableColumn fieldTypeColumn = new TableColumn(1);
			// fieldTypeColumn.setHeaderValue("Type");
			// jTable.addColumn(fieldTypeColumn);
			tm.addColumn("Type");
			// MIRAR EL CÓDIGO DEL BOTÓN DE AÑADIR CAMPO PARA VER EL CellEditor con comboBox

			
			/* TableColumn fieldLengthColumn = new TableColumn(2);
			fieldLengthColumn.setHeaderValue("Length");
			// fieldLengthColumn.setCellRenderer(new DefaultTableCellRenderer());
			jTable.addColumn(fieldLengthColumn); */
			tm.addColumn("Length");
			
//			Ask to be notified of selection changes.
			ListSelectionModel rowSM = jTable.getSelectionModel();
			rowSM.addListSelectionListener(new ListSelectionListener() {
			    public void valueChanged(ListSelectionEvent e) {
			        //Ignore extra messages.
			        if (e.getValueIsAdjusting()) return;

			        ListSelectionModel lsm =
			            (ListSelectionModel)e.getSource();
			        if (lsm.isSelectionEmpty()) {
			            //no rows are selected
			        	jButtonDeleteField.setEnabled(false);
			        } else {
			            // int selectedRow = lsm.getMinSelectionIndex();
			            //selectedRow is selected
			        	jButtonDeleteField.setEnabled(true);
			        }
			    }
			});

			
		}
		return jTable;
	}


	/**
	 * This method initializes jPanelWest	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelEast() {
		if (jPanelEast == null) {
			jPanelEast = new JPanel();
			jPanelEast.setLayout(null);
			jPanelEast.setPreferredSize(new java.awt.Dimension(100,100));
			jPanelEast.add(getJButtonAddField(), null);
			jPanelEast.add(getJButtonDeleteField(), null);
		}
		return jPanelEast;
	}


	/**
	 * This method initializes jButtonAddField	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonAddField() {
		if (jButtonAddField == null) {
			jButtonAddField = new JButton();
			jButtonAddField.setText("AddField");
			jButtonAddField.setBounds(new java.awt.Rectangle(7,5,85,23));
			jButtonAddField.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// Add a new row
					DefaultTableModel tm = (DefaultTableModel) jTable.getModel();
					Object[] newRow = new Object[tm.getColumnCount()];
					newRow[0] = "NewField";
					newRow[1] = "String";
					newRow[2] = new Integer(20);
					tm.addRow(newRow);
					
					// Esto lo añado aquí porque si no tiene registros, no hace caso. (Por eso no
					// lo pongo en getJTable()
					TableColumn typeColumn = jTable.getColumnModel().getColumn(1);
					JComboBox comboBox = new JComboBox();
					comboBox.addItem("Boolean");
					comboBox.addItem("Date");
					comboBox.addItem("Integer");
					comboBox.addItem("Double");
					comboBox.addItem("String");
					typeColumn.setCellEditor(new DefaultCellEditor(comboBox));
					
					// tm.setValueAt("NewField", tm.getRowCount()-1, 0);
				}
			});
			
		}
		return jButtonAddField;
	}


	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonDeleteField() {
		if (jButtonDeleteField == null) {
			jButtonDeleteField = new JButton();
			jButtonDeleteField.setText("DeleteField");
			jButtonDeleteField.setBounds(new java.awt.Rectangle(7,33,85,23));
			jButtonDeleteField.setEnabled(false);
			jButtonDeleteField.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int[] selecteds = jTable.getSelectedRows();
					DefaultTableModel tm = (DefaultTableModel) jTable.getModel();
					
					for (int i=selecteds.length-1; i >=0; i--)
						tm.removeRow(selecteds[i]);
				}
			});
		}
		return jButtonDeleteField;
	}



}  //  @jve:decl-index=0:visual-constraint="10,10"
