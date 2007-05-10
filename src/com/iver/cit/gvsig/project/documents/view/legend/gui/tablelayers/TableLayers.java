package com.iver.cit.gvsig.project.documents.view.legend.gui.tablelayers;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.v02.FSymbol;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.fmap.rendering.EditionManagerLegend;
import com.iver.cit.gvsig.fmap.rendering.IVectorialLegend;
import com.iver.cit.gvsig.project.documents.view.legend.edition.gui.ActivatedCellEditor;
import com.iver.cit.gvsig.project.documents.view.legend.edition.gui.BlockedCellEditor;
import com.iver.cit.gvsig.project.documents.view.legend.edition.gui.CellIconOptionRenderer;
import com.iver.cit.gvsig.project.documents.view.legend.edition.gui.DisabledCellEditor;
import com.iver.cit.gvsig.project.documents.view.legend.edition.gui.FCellSymbolRenderer;
import com.iver.cit.gvsig.project.documents.view.legend.edition.gui.FilledCellEditor;
import com.iver.cit.gvsig.project.documents.view.legend.edition.gui.PresentCellEditor;
import com.iver.cit.gvsig.project.documents.view.legend.edition.gui.SymbolCellEditor;
import com.iver.cit.gvsig.project.documents.view.legend.edition.gui.ValueCellEditor;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class TableLayers extends JPanel {
    private ImageIcon selected = new ImageIcon(this.getClass().getClassLoader()
                                                   .getResource("images/selected.png"));
    private ImageIcon notselected = new ImageIcon(this.getClass()
                                                      .getClassLoader()
                                                      .getResource("images/notSelected.png"));
    private ImageIcon blocked = new ImageIcon(this.getClass().getClassLoader()
                                                  .getResource("images/blocked.png"));
    private ImageIcon unblocked = new ImageIcon(this.getClass().getClassLoader()
                                                    .getResource("images/unblocked.png"));
    private ImageIcon active = new ImageIcon(this.getClass().getClassLoader()
                                                 .getResource("images/active.png"));
    private ImageIcon defuse = new ImageIcon(this.getClass().getClassLoader()
                                                 .getResource("images/defuse.png"));
    private ImageIcon disable = new ImageIcon(this.getClass().getClassLoader()
                                                  .getResource("images/disabled.png"));
    private ImageIcon notdisable = new ImageIcon(this.getClass().getClassLoader()
                                                     .getResource("images/notdisabled.png"));
    private ImageIcon fill = new ImageIcon(this.getClass().getClassLoader()
                                               .getResource("images/fill.png"));
    private ImageIcon notfill = new ImageIcon(this.getClass().getClassLoader()
                                                  .getResource("images/notfill.png"));
    private boolean DEBUG = false;
    private ReadableVectorial source;
    private EditionManagerLegend eml;
    private JTable table;
	private ArrayList statusListeners=new ArrayList();

    public TableLayers(ReadableVectorial source2, IVectorialLegend legend2) {
        super(new GridLayout(1, 0));
        this.source = source2;
        this.eml = new EditionManagerLegend(legend2);

        table = new JTable(new MyTableModel());
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setShowHorizontalLines(false);

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);

        //Set up column sizes.
        initColumnSizes(table);

        setUpStatusColumn(table, table.getColumnModel().getColumn(0));
        setUpValueColumn(table, table.getColumnModel().getColumn(1));
        setUpActivateColumn(table, table.getColumnModel().getColumn(2));
        setUpDisableColumn(table, table.getColumnModel().getColumn(3));
        setUpBlockColumn(table, table.getColumnModel().getColumn(4));
        setUpFillColumn(table, table.getColumnModel().getColumn(5));
        setUpSymbolColumn(table, table.getColumnModel().getColumn(6));

        //Add the scroll pane to this panel.
        add(scrollPane);
    }

    /*
     * This method picks good column sizes.
     * If all column heads are wider than the column's cells'
     * contents, then you can just use column.sizeWidthToFit().
     */
    private void initColumnSizes(JTable table) {
        MyTableModel model = (MyTableModel) table.getModel();
        TableColumn column = null;
        Component comp = null;
        int headerWidth = 0;
        int cellWidth = 0;
        Object[] longValues = model.longValues;
        TableCellRenderer headerRenderer = table.getTableHeader()
                                                .getDefaultRenderer();

        for (int i = 0; i < 7; i++) {
            column = table.getColumnModel().getColumn(i);

            comp = headerRenderer.getTableCellRendererComponent(null,
                    column.getHeaderValue(), false, false, 0, 0);
            headerWidth = comp.getPreferredSize().width;

            comp = table.getDefaultRenderer(model.getColumnClass(i))
                        .getTableCellRendererComponent(table, longValues[i],
                    false, false, 0, i);
            cellWidth = comp.getPreferredSize().width;

            if (DEBUG) {
                System.out.println("Initializing width of column " + i + ". " +
                    "headerWidth = " + headerWidth + "; cellWidth = " +
                    cellWidth);
            }

            ///column.setPreferredWidth(Math.max(headerWidth, cellWidth));
            column.setPreferredWidth((headerWidth + cellWidth) / 2);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param table DOCUMENT ME!
     * @param column DOCUMENT ME!
     */
    public void setUpSymbolColumn(JTable table, TableColumn column) {
        ///SymbolCellEditor symboleditor = new SymbolCellEditor();
        ///column.setCellEditor(symboleditor);

        FCellSymbolRenderer renderer = new FCellSymbolRenderer(true);
        column.setCellRenderer(renderer);
    }
    public void setUpValueColumn(JTable table, TableColumn column) {
        ValueCellEditor valueEditor = new ValueCellEditor();
        column.setCellEditor(valueEditor);
    }
    /**
     * DOCUMENT ME!
     *
     * @param table DOCUMENT ME!
     * @param column DOCUMENT ME!
     */
    public void setUpStatusColumn(JTable table, TableColumn column) {

        PresentCellEditor presenteditor = new PresentCellEditor(eml,table,selected,
                notselected);
        presenteditor.addMouseListener(new MouseListener(){

			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount()==2){
					for (int i=0;i<statusListeners.size();i++) {
						((StatusListener)statusListeners.get(i)).click();
					}
				}
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}

		});
        column.setCellEditor(presenteditor);

        CellIconOptionRenderer renderer = new CellIconOptionRenderer(true);
        column.setCellRenderer(renderer);
    }

    /**
     * DOCUMENT ME!
     *
     * @param table DOCUMENT ME!
     * @param column DOCUMENT ME!
     */
    public void setUpActivateColumn(JTable table, TableColumn column) {
        ActivatedCellEditor activatededitor = new ActivatedCellEditor(eml,table,active,
                defuse);
        column.setCellEditor(activatededitor);

        CellIconOptionRenderer renderer = new CellIconOptionRenderer(true);
        column.setCellRenderer(renderer);
    }

    /**
     * DOCUMENT ME!
     *
     * @param table DOCUMENT ME!
     * @param column DOCUMENT ME!
     */
    public void setUpDisableColumn(JTable table, TableColumn column) {
        DisabledCellEditor disablededitor = new DisabledCellEditor(eml,table,notdisable,
                disable);
        column.setCellEditor(disablededitor);

        CellIconOptionRenderer renderer = new CellIconOptionRenderer(true);
        column.setCellRenderer(renderer);
    }

    /**
     * DOCUMENT ME!
     *
     * @param table DOCUMENT ME!
     * @param column DOCUMENT ME!
     */
    public void setUpBlockColumn(JTable table, TableColumn column) {
        BlockedCellEditor blockeditor = new BlockedCellEditor(eml,table,blocked,
                unblocked);
        column.setCellEditor(blockeditor);

        CellIconOptionRenderer renderer = new CellIconOptionRenderer(true);
        column.setCellRenderer(renderer);
    }

    /**
     * DOCUMENT ME!
     *
     * @param table DOCUMENT ME!
     * @param column DOCUMENT ME!
     */
    public void setUpFillColumn(JTable table, TableColumn column) {
        FilledCellEditor fillededitor = new FilledCellEditor(eml,table,fill,
                notfill);
        column.setCellEditor(fillededitor);

        CellIconOptionRenderer renderer = new CellIconOptionRenderer(true);
        column.setCellRenderer(renderer);
    }

    /**
     * Create the GUI and show it.  For thread safety, this method should be
     * invoked from the event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("TableRenderDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        TableLayers newContentPane = new TableLayers(null, null);
        newContentPane.setOpaque(true); //content panes must be opaque
        newContentPane.DEBUG = true;
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    createAndShowGUI();
                }
            });
    }

    /**
     * DOCUMENT ME!
     *
     * @param source DOCUMENT ME!
     */
    public void setSource(ReadableVectorial source) {
        this.source = source;
    }

    /**
     * DOCUMENT ME!
     *
     * @param legend DOCUMENT ME!
     */
    public void setLegend(IVectorialLegend legend) {
        this.eml = new EditionManagerLegend(legend);
    }

    class MyTableModel extends AbstractTableModel {
        private String[] columnNames = {
                PluginServices.getText(this, "estado"),
                PluginServices.getText(this, "nombre"),
                PluginServices.getText(this, "activar"),
                PluginServices.getText(this, "inutilizar"),
                PluginServices.getText(this, "bloquear"),
                PluginServices.getText(this, "relleno"),
                PluginServices.getText(this, "simbolo")
            };

        //        private Object[][] data = {
        //            {new Boolean(true), "Nombre1",new Boolean(true) ,new Boolean(false),new Boolean(false),
        //            	"símbolo"},
        //            	{new Boolean(false), "Nombre2",new Boolean(true),new Boolean(false),new Boolean(false),
        //            	"símbolo"},
        //            	{new Boolean(false), "Nombre3",new Boolean(true),new Boolean(false),new Boolean(false),
        //            	"símbolo"},
        //            	{new Boolean(false), "Nombre4",new Boolean(true),new Boolean(false),new Boolean(false),
        //            	"símbolo"},
        //            	{new Boolean(false), "Nombre5",new Boolean(true),new Boolean(false),new Boolean(false),
        //            	"símbolo"}
        //        };
        public final Object[] longValues = {
                new ImageIcon(), "Nombre1", new ImageIcon(), new ImageIcon(),
                new ImageIcon(), new ImageIcon(),
                SymbologyFactory.createDefaultSymbolByShapeType(FShape.MULTI)
            };

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return eml.getRowCount();
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            switch (col) {
            case 0:

                if (eml.isPresent(row)) {
                    return selected;
                }

                return notselected;

            case 1:
                return eml.getValue(row);

            case 2:

                if (eml.isActived(row)) {
                    return active;
                }

                return defuse;

            case 3:

                if (eml.isDisable(row)) {
                    return notdisable;
                }

                return disable;

            case 4:

                if (eml.isBlocked(row)) {
                    return blocked;
                }

                return unblocked;

            case 5:

                if (eml.isFilled(row)) {
                    return fill;
                }

                return notfill;

            case 6:
            	return eml.getSymbol(row);

            default:
                return null;
            }
        }

        /*
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         */
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        /*
         * Don't need to implement this method unless your table's
         * editable.
         */
        public boolean isCellEditable(int row, int col) {
        	return true;
        }

        /*
         * Don't need to implement this method unless your table's
         * data can change.
         */
        public void setValueAt(Object value, int row, int col) {
//            if (DEBUG) {
//                System.out.println("Setting value at " + row + "," + col +
//                    " to " + value + " (an instance of " + value.getClass() +
//                    ")");
//            }
//
//            ///data[row][col] = value;
//            ////////////////
//            switch (col) {
//            case 0:
//            	if (value.equals(selected)) {
//                    eml.setPresent(row,true);
//                }else {
//                	eml.setPresent(row,false);
//                }
//            case 1:
//                eml.setValue(row,value);
//            case 2:
//            	if (value.equals(active)) {
//                    eml.setActived(row,true);
//                }else {
//                	eml.setActived(row,false);
//                }
//            case 3:
//            	if (value.equals(disable)) {
//                    eml.setDisable(row,true);
//                }else {
//                	eml.setDisable(row,false);
//                }
//            case 4:
//            	if (value.equals(blocked)) {
//                    eml.setBlocked(row,true);
//                }else {
//                	eml.setBlocked(row,false);
//                }
//            case 5:
//            	if (value.equals(fill)) {
//                    eml.setFilled(row,true);
//                }else {
//                	eml.setFilled(row,false);
//                }
//            case 6:
//               eml.setSymbol(row,value);
//            }
//            /////////////
//            fireTableCellUpdated(row, col);
//
//            if (DEBUG) {
//                System.out.println("New value of data:");
//                printDebugData();
//            }
        }

        private void printDebugData() {
            int numRows = getRowCount();
            int numCols = getColumnCount();

            for (int i = 0; i < numRows; i++) {
                System.out.print("    row " + i + ":");

                for (int j = 0; j < numCols; j++) {
                    /// System.out.print("  " + data[i][j]);
                }

                System.out.println();
            }

            System.out.println("--------------------------");
        }
    }

	public String getPresentSubLayer() {
		return eml.getPresentSubLayer();
	}
	public void addStatusListener(StatusListener listener) {
		statusListeners.add(listener);
	}
}
