package com.iver.cit.gvsig.project.documents.view.snapping.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.EventObject;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.project.documents.view.snapping.ISnapper;

public class PropertySnapCellEditor extends JButton implements TableCellEditor {
    private ArrayList snappers;
    private int row;
    private IWindow panel;

    public PropertySnapCellEditor(ArrayList snappers) {
	this.snappers = snappers;
	this.addMouseListener(new MouseListener() {

	    @Override
	    public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
		    if (panel != null) {
			openConfigurePanel();
		    }
		}
	    }

	    @Override
	    public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	    }

	    @Override
	    public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	    }

	    @Override
	    public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	    }

	    @Override
	    public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	    }

	});
    }

    // class WinConfigure extends JPanel implements IWindow {
    //
    // private WindowInfo wi=null;
    //
    // public WindowInfo getWindowInfo() {
    // if (wi==null) {
    // wi=new WindowInfo(WindowInfo.MODALDIALOG|WindowInfo.RESIZABLE);
    // wi.setWidth(panel.getWidth());
    // wi.setHeight(panel.getHeight());
    // wi.setTitle(PluginServices.getText(this,"propiedades"));
    // }
    // return wi;
    // }
    // }
    private void openConfigurePanel() {
	// IWindow window=new WinConfigure();
	((DefaultConfigurePanel) panel)
		.setSnapper((ISnapper) snappers.get(row));
	PluginServices.getMDIManager().addWindow(panel);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
	    boolean isSelected, int row, int column) {
	this.row = row;
	panel = ((ISnapper) snappers.get(row)).getConfigurator();
	if (panel != null) {
	    this.setEnabled(true);
	} else {
	    this.setEnabled(false);
	    this.setBackground(Color.white);
	}
	return this;
    }

    @Override
    public void cancelCellEditing() {
	// TODO Auto-generated method stub

    }

    @Override
    public boolean stopCellEditing() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public Object getCellEditorValue() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public boolean isCellEditable(EventObject anEvent) {
	return true;
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public void addCellEditorListener(CellEditorListener l) {
	// TODO Auto-generated method stub

    }

    @Override
    public void removeCellEditorListener(CellEditorListener l) {
	// TODO Auto-generated method stub

    }

}
