package com.iver.cit.gvsig.project.documents.view.snapping.gui;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.iver.cit.gvsig.project.documents.view.snapping.ISnapper;

public class PropertySnapCellRenderer extends JButton implements TableCellRenderer{
	private JPanel panel;
	private ArrayList snappers;
	public PropertySnapCellRenderer(ArrayList snappers) {
		this.snappers=snappers;
		this.setText("...");
	}
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		panel=(JPanel)((ISnapper)snappers.get(row)).getConfigurator();
		if (panel!=null) {
			this.setEnabled(true);
		}else {
			this.setEnabled(false);
			this.setBackground(Color.white);
		}
		return this;
	}

}
