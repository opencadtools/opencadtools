package com.iver.cit.gvsig.gui.cad.panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import jwizardcomponent.JWizardComponents;
import jwizardcomponent.JWizardPanel;

import org.apache.log4j.Logger;
import org.gvsig.gui.beans.swing.JFileChooser;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.thoughtworks.xstream.XStreamException;

import es.icarto.gvsig.schema.FieldDefinition;
import es.icarto.gvsig.schema.SchemaSerializator;

/**
 * @author fjp
 * 
 *         Panel para que el usuario seleccione el driver que va a utilizar para
 *         crear un tema desde cero.
 * 
 */
public class JPanelFieldDefinition extends JWizardPanel implements
	ActionListener, TableModelListener, ListSelectionListener {
    private static final Logger logger = Logger
	    .getLogger(JPanelFieldDefinition.class);

    protected static final int MAX_FIELD_LENGTH = 254;

    protected static final int FIELD_NAME = 0;
    protected static final int FIELD_TYPE = 1;
    protected static final int FIELD_LENGTH = 2;

    protected JTable table;
    protected DefaultTableModel model;

    private JButton add, delete, load, save;

    protected IWriter writer = null;

    public JPanelFieldDefinition(JWizardComponents wizardComponents,
	    IWriter writer) {
	super(wizardComponents, null);

	this.writer = writer;

	JLabel title = new JLabel(PluginServices.getText(this, "define_fields"));

	this.model = createModel();
	this.table = new JTable(model);
	this.table.getColumnModel().getColumn(FIELD_NAME).setWidth(180);
	setCellEditorForFieldType();

	table.getModel().addTableModelListener(this);
	table.getSelectionModel().addListSelectionListener(this);

	JScrollPane scrollPane = new JScrollPane(table);

	JPanel buttons = new JPanel();
	buttons.setLayout(null);
	buttons.setPreferredSize(new Dimension(170, 100));

	this.add = createButton("add_field", 5, true);
	this.delete = createButton("delete_field", 33, false);
	this.load = createButton("load_schema", 61, true);
	this.save = createButton("save_schema", 89, false);

	buttons.add(add);
	buttons.add(delete);
	buttons.add(load);
	buttons.add(save);

	this.setLayout(new BorderLayout(5, 5));
	this.setSize(new Dimension(500, 250));
	this.add(title, BorderLayout.NORTH);
	this.add(scrollPane, BorderLayout.CENTER);
	this.add(buttons, BorderLayout.EAST);
    }

    /**
     * <p>
     * Creates the table model for the field definitions.
     * </P>
     * <p>
     * <b>IMPORTANT</b>: In case you override this method, you <b>must</b>
     * always add columns to the table model, but <b>never</b> replace/remove
     * the existing.
     * </p>
     * 
     * @return the table model
     */
    protected DefaultTableModel createModel() {
	DefaultTableModel model = new DefaultTableModel();
	model.addColumn(PluginServices.getText(this, "field"));
	model.addColumn(PluginServices.getText(this, "type"));
	model.addColumn(PluginServices.getText(this, "length"));
	return model;
    }

    @Override
    public void tableChanged(TableModelEvent e) {
	save.setEnabled(table.getRowCount() > 0);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
	Object source = e.getSource();
	if (!e.getValueIsAdjusting() && source instanceof ListSelectionModel) {
	    ListSelectionModel model = (ListSelectionModel) source;
	    delete.setEnabled(!model.isSelectionEmpty());
	}
    }

    protected void setCellEditorForFieldType() {
	JComboBox<String> comboBox = new JComboBox<String>();

	if (writer.canWriteAttribute(Types.BOOLEAN)) {
	    comboBox.addItem("Boolean");
	}
	if (writer.canWriteAttribute(Types.DATE)) {
	    comboBox.addItem("Date");
	}
	if (writer.canWriteAttribute(Types.INTEGER)) {
	    comboBox.addItem("Integer");
	}
	if (writer.canWriteAttribute(Types.DOUBLE)) {
	    comboBox.addItem("Double");
	}
	// We assume that string can always be written
	comboBox.addItem("String");

	TableColumn typeColumn = table.getColumnModel().getColumn(FIELD_TYPE);
	typeColumn.setCellEditor(new DefaultCellEditor(comboBox));
    }

    private JButton createButton(String text, int y, boolean enabled) {
	JButton button = new JButton(PluginServices.getText(this, text));
	button.setLocation(new Point(7, y));
	button.setSize(new Dimension(145, 23));
	button.setPreferredSize(new Dimension(100, 26));
	button.setEnabled(enabled);
	button.addActionListener(this);
	return button;
    }

    protected String getNewFieldName() {
	// Figure out a suitable field name
	String prefix = PluginServices.getText(this, "field").replaceAll(" +",
		"_");
	int index = 0;
	for (int i = 0; i < table.getRowCount(); i++) {
	    String fieldName = table.getModel().getValueAt(i, FIELD_NAME)
		    .toString();
	    if (fieldName.startsWith(prefix)) {
		try {
		    index = Integer.parseInt(fieldName.replaceAll(prefix, ""));
		} catch (Exception ex) {
		    // ignore
		}
	    }
	}
	return prefix + (++index);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	Object source = e.getSource();
	if (source == add) {
	    addField();
	} else if (source == delete) {
	    deleteField();
	} else if (source == load) {
	    loadSchema();
	} else if (source == save) {
	    saveSchema();
	}
    }

    protected void addField() {
	model.addRow(new Object[] { getNewFieldName(), "String", "20" });
    }

    protected void deleteField() {
	int[] selectedRows = table.getSelectedRows();
	for (int i = selectedRows.length - 1; i >= 0; i--) {
	    model.removeRow(selectedRows[i]);
	}
    }

    protected void loadSchema() {
	Preferences prefs = Preferences.userRoot().node("gvsig.foldering");
	JFileChooser fileChooser = new JFileChooser("LOAD_SCHEMA_ID",
		prefs.get("TemplatesFolder", null));
	if (fileChooser.showOpenDialog(null) == javax.swing.JFileChooser.APPROVE_OPTION) {
	    try {
		Object[][] rows = readSchema(fileChooser.getSelectedFile());
		for (Object[] row : rows) {
		    model.addRow(row);
		}
		setCellEditorForFieldType();
	    } catch (XStreamException e) {
		logger.error("Cannot load schema", e);
		JOptionPane.showMessageDialog(this,
			PluginServices.getText(this, "xstream_parsing_error"));
	    }
	}
    }

    /**
     * Returns the rows with the values to add to the table model
     * 
     * @param file
     *            The file to read
     * @return The 2D array, first index for rows, second for columns
     */
    protected Object[][] readSchema(File file) throws XStreamException {
	SchemaSerializator serializator = new SchemaSerializator();
	List<FieldDefinition> fields = serializator.fromXML(file);
	Object[][] ret = new Object[fields.size()][];
	for (int i = 0; i < ret.length; i++) {
	    FieldDefinition field = fields.get(i);
	    ret[i] = new Object[] { field.getName(), field.getType(),
		    field.getLength() };
	}

	return ret;
    }

    protected void saveSchema() {
	// in case the user is still editing the table
	TableCellEditor cellEditor = table.getCellEditor();
	if (table.isEditing() && cellEditor != null) {
	    cellEditor.stopCellEditing();
	}

	if (validateTableModel() && validateSchema()) {
	    Preferences prefs = Preferences.userRoot().node("gvsig.foldering");
	    JFileChooser jfc = new JFileChooser("SAVE_SCHEMA_ID", prefs.get(
		    "TemplatesFolder", null));
	    if (jfc.showSaveDialog(null) == javax.swing.JFileChooser.APPROVE_OPTION) {
		String xml = getSchemaXML();
		File schema = jfc.getSelectedFile();
		if (!schema.exists()) {
		    try {
			schema.createNewFile();
		    } catch (IOException e) {
			logger.error("Cannot create schema file", e);
			JOptionPane.showMessageDialog(this,
				PluginServices.getText(this, "error"));
			return;
		    }
		}
		try {
		    BufferedWriter bw = new BufferedWriter(new FileWriter(
			    schema.getAbsoluteFile()));
		    bw.write(xml);
		    bw.close();
		} catch (IOException e) {
		    logger.error("Cannot create schema file", e);
		    JOptionPane.showMessageDialog(this,
			    PluginServices.getText(this, "error"));
		    return;
		}
	    }
	}
    }

    protected String getSchemaXML() {
	return new SchemaSerializator().toXML(getFieldsFromModel());
    }

    @Override
    public void next() {
	if (validateTableModel()) {
	    super.next();
	}

	List panels = getWizardComponents().getWizardPanelList();
	if (panels.size() > 2 && panels.get(2) instanceof SpatialFileBasedPanel) {
	    SpatialFileBasedPanel filePanel = (SpatialFileBasedPanel) panels
		    .get(2);
	    setFinishButtonEnabled(!filePanel.getPath().equals(""));
	}
    }

    private boolean validateTableModel() {
	for (int i = 0; i < model.getRowCount(); i++) {
	    String name = model.getValueAt(i, FIELD_NAME).toString();
	    String type = model.getValueAt(i, FIELD_TYPE).toString();
	    String length = model.getValueAt(i, FIELD_LENGTH).toString();

	    if (!validateFieldName(name) || !validateInteger(length)
		    || !validateFieldType(type)) {
		return false;
	    }

	    if (type.equals("String")
		    && Integer.parseInt(length) > MAX_FIELD_LENGTH) {
		String message = PluginServices.getText(this, "max_length_is")
			+ ": " + MAX_FIELD_LENGTH + "\n"
			+ PluginServices.getText(this, "length_of_field")
			+ " '" + name + "' "
			+ PluginServices.getText(this, "will_be_truncated");
		JOptionPane.showMessageDialog(this, message);
		model.setValueAt(String.valueOf(MAX_FIELD_LENGTH), i,
			FIELD_LENGTH);
	    }

	}

	// ensure no field name is used more than once
	List<String> fieldNames = new ArrayList<String>();
	for (int i = 0; i < table.getRowCount(); i++) {
	    String name = model.getValueAt(i, FIELD_NAME).toString();
	    if (fieldNames.contains(name)) {
		JOptionPane.showMessageDialog(this, PluginServices.getText(
			this, "two_or_more_fields_with_the_same_name"));
		return false;
	    }
	    fieldNames.add(name);
	}

	return true;
    }

    private boolean validateFieldType(String fieldType) {
	if (fieldType == null) {
	    JOptionPane.showMessageDialog(
		    (Component) PluginServices.getMainFrame(),
		    PluginServices.getText(this, "no_puede_continuar")
			    + "\n"
			    + PluginServices.getText(this,
				    "field_type_cannot_be_null"));
	    return false;
	} else {
	    return true;
	}
    }

    private boolean validateSchema() {
	Pattern nonWordCharsPattern = Pattern.compile("[^\\w]");
	Pattern upperCaseCharsPattern = Pattern.compile("[A-Z]");

	String error = null;
	for (int i = 0; i < model.getRowCount(); i++) {
	    String fieldName = model.getValueAt(i, FIELD_NAME).toString();
	    List<String> reservedWords = readReservedWordsFromFile();
	    if (reservedWords.contains(fieldName.toUpperCase())) {
		error = PluginServices.getText(this, "no_puede_continuar")
			+ "\n" + PluginServices.getText(this, "field") + " : "
			+ fieldName + "\n"
			+ PluginServices.getText(this, "is_reserved_word");
	    } else if (nonWordCharsPattern.matcher(fieldName).find()) {
		error = PluginServices.getText(this, "no_puede_continuar")
			+ "\n"
			+ PluginServices.getText(this, "field")
			+ " : "
			+ fieldName
			+ "\n"
			+ PluginServices.getText(this,
				"has_non_word_characters");
	    } else if (upperCaseCharsPattern.matcher(fieldName).find()) {
		error = PluginServices.getText(this, "warning_title")
			+ "\n"
			+ PluginServices.getText(this, "field")
			+ " : "
			+ fieldName
			+ "\n"
			+ PluginServices.getText(this,
				"has_upper_case_characters");
	    }
	}

	if (error != null) {
	    JOptionPane.showMessageDialog(
		    (Component) PluginServices.getMainFrame(), error);
	    return false;
	} else {
	    return true;
	}
    }

    public IWriter getWriter() {
	return this.writer;
    }

    private boolean validateInteger(String s) {
	try {
	    Integer.parseInt(s);
	    return true;
	} catch (NumberFormatException e) {
	    JOptionPane.showMessageDialog(
		    (Component) PluginServices.getMainFrame(),
		    PluginServices.getText(this, "no_puede_continuar") + "\n"
			    + PluginServices.getText(this, "size") + " : " + s
			    + "\n"
			    + PluginServices.getText(this, "incorrect_value"));
	    return false;
	}
    }

    private boolean validateFieldName(String fieldName) {
	String error = null;
	if (fieldName == null) {
	    error = PluginServices.getText(this, "no_puede_continuar") + "\n"
		    + PluginServices.getText(this, "field_name_cannot_be_null");
	} else if (fieldName.equals("")) {
	    error = PluginServices.getText(this, "no_puede_continuar")
		    + "\n"
		    + PluginServices
			    .getText(this, "the_field_name_is_required");
	} else if (fieldName.indexOf(" ") != -1) {
	    error = PluginServices.getText(this, "no_puede_continuar")
		    + "\n"
		    + PluginServices.getText(this, "field")
		    + " : "
		    + fieldName
		    + "\n"
		    + PluginServices.getText(this,
			    "contiene_espacios_en_blanco");
	} else if (this.writer != null
		&& this.writer.getCapability("FieldNameMaxLength") != null) {
	    int intValue = 0;
	    try {
		intValue = Integer.parseInt(writer
			.getCapability("FieldNameMaxLength"));
	    } catch (NumberFormatException e) {
		// ignore
	    }

	    if (intValue > 0 && fieldName.length() > intValue) {
		error = PluginServices.getText(this, "no_puede_continuar")
			+ "\n" + PluginServices.getText(this, "field") + " : "
			+ fieldName + "\n"
			+ PluginServices.getText(this, "too_long_name") + "\n"
			+ PluginServices.getText(this, "maximun_name_size")
			+ " : " + intValue + "\n";
	    }
	} else if (fieldName.length() > MAX_FIELD_LENGTH) {
	    // if the writer has not defined the max length, truncate the field
	    // name to MAX_FIELD_LENGTH
	    error = PluginServices.getText(this, "no_puede_continuar") + "\n"
		    + PluginServices.getText(this, "field") + " : " + fieldName
		    + "\n" + PluginServices.getText(this, "too_long_name")
		    + "\n" + PluginServices.getText(this, "maximun_name_size")
		    + " : " + MAX_FIELD_LENGTH + "\n";
	}

	if (error != null) {
	    JOptionPane.showMessageDialog(
		    (Component) PluginServices.getMainFrame(), error);
	    return false;
	} else {
	    return true;
	}
    }

    private List<String> readReservedWordsFromFile() {
	List<String> list = new ArrayList<String>();
	try {
	    String file = "gvSIG/extensiones/com.iver.cit.gvsig.cad/restricted.txt";
	    BufferedReader reader = new BufferedReader(new FileReader(file));
	    String strLine;
	    while ((strLine = reader.readLine()) != null) {
		list.add(strLine.toUpperCase());
	    }
	    reader.close();
	} catch (Exception e) {
	    list.clear();
	}
	return list;
    }

    public List<FieldDefinition> getFieldsFromModel() {
	List<FieldDefinition> fields = new ArrayList<FieldDefinition>();

	for (int i = 0; i < model.getRowCount(); i++) {
	    FieldDefinition field = new FieldDefinition();
	    field.setName(model.getValueAt(i, FIELD_NAME).toString());
	    field.setType(model.getValueAt(i, FIELD_TYPE).toString());
	    field.setLength(model.getValueAt(i, FIELD_LENGTH).toString());
	    fields.add(field);
	}

	return fields;
    }

    /**
     * Converts the JTable contents into an array of {@link FieldDescription}s
     * 
     * @return the field descriptions
     */
    public FieldDescription[] getFieldsDescription() {
	FieldDescription[] fieldsDesc = new FieldDescription[model
		.getRowCount()];

	for (int i = 0; i < model.getRowCount(); i++) {
	    fieldsDesc[i] = new FieldDescription();
	    fieldsDesc[i].setFieldName((String) model.getValueAt(i, 0));
	    String strType = (String) model.getValueAt(i, 1);
	    if (strType.equals("String")) {
		fieldsDesc[i].setFieldType(Types.VARCHAR);
	    }
	    if (strType.equals("Double")) {
		fieldsDesc[i].setFieldType(Types.DOUBLE);
	    }
	    if (strType.equals("Integer")) {
		fieldsDesc[i].setFieldType(Types.INTEGER);
	    }
	    if (strType.equals("Boolean")) {
		fieldsDesc[i].setFieldType(Types.BOOLEAN);
	    }
	    if (strType.equals("Date")) {
		fieldsDesc[i].setFieldType(Types.DATE);
	    }
	    int fieldLength = Integer.parseInt((String) model.getValueAt(i, 2));
	    fieldsDesc[i].setFieldLength(fieldLength);

	    // TODO: HACERLO BIEN
	    if (strType.equals("Double")) {
		fieldsDesc[i].setFieldDecimalCount(5);
	    }

	}

	return fieldsDesc;
    }
}
