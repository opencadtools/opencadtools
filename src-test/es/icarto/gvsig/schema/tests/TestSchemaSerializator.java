package es.icarto.gvsig.schema.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Test;

import com.thoughtworks.xstream.XStreamException;

import es.icarto.gvsig.schema.FieldDefinition;
import es.icarto.gvsig.schema.SchemaSerializator;


public class TestSchemaSerializator {

    private static List<FieldDefinition> getFields() {
	List<FieldDefinition> fields = new ArrayList<FieldDefinition>();

	FieldDefinition f1 = new FieldDefinition();
	f1.setName("test_varchar");
	f1.setType("String");
	f1.setLength("2");
	fields.add(f1);

	FieldDefinition f2 = new FieldDefinition();
	f2.setName("test_double");
	f2.setType("Double");
	f2.setLength("3");
	fields.add(f2);

	FieldDefinition f3 = new FieldDefinition();
	f3.setName("test_integer");
	f3.setType("Integer");
	f3.setLength("1");
	fields.add(f3);

	FieldDefinition f4 = new FieldDefinition();
	f4.setName("test_boolean");
	f4.setType("Boolean");
	f4.setLength("3");
	fields.add(f4);

	FieldDefinition f5 = new FieldDefinition();
	f5.setName("test_date");
	f5.setType("Date");
	f5.setLength("3");
	fields.add(f5);

	return fields;
    }

    @Test
    public void testToXML() {
	SchemaSerializator serializator = new SchemaSerializator();
	String xml = serializator.toXML(getFields());

	// should have all these tags
	assertTrue(xml instanceof String);
	assertTrue(xml.contains("<gvsig-layer-schema>"));
	assertTrue(xml.contains("<field>"));
	assertTrue(xml.contains("<name>"));
	assertTrue(xml.contains("<type>"));
	assertTrue(xml.contains("<length>"));
    }

    @Test
    public void testFromXML() {
	SchemaSerializator serializator = new SchemaSerializator();
	List<FieldDefinition> schema = serializator.fromXML(new File(
		"data/schema.xml"));
	assertTrue(5 == schema.size());
    }

    @Test
    public void testFieldsAreRetrievedInTheSameOrderThanTheyAreInXML() {
	SchemaSerializator serializator = new SchemaSerializator();
	List<FieldDefinition> fields = serializator.fromXML(new File(
		"data/schema.xml"));
	List<FieldDefinition> fieldsOriginal = getFields();
	for (int i = 0; i < fieldsOriginal.size(); i++) {
	    assertEquals(fieldsOriginal.get(i).getName(),
		    fields.get(i).getName());
	    assertEquals(fieldsOriginal.get(i).getType(),
		    fields.get(i).getType());
	    assertEquals(fieldsOriginal.get(i).getLength(),
		    fields.get(i).getLength());
	}
    }

    @Test
    public void testFromXMLWithoutNameIsParsed() {
	SchemaSerializator serializator = new SchemaSerializator();
	try {
	    List<FieldDefinition> fields = serializator.fromXML(new File(
		    "data/schema-without-name.xml"));
	    List<FieldDefinition> fieldsOriginal = getFields();
	    assertEquals(null, fields.get(0).getName());
	    assertEquals(fieldsOriginal.get(0).getType(),
		    fields.get(0).getType());
	    assertEquals(fieldsOriginal.get(0).getLength(),
		    fields.get(0).getLength());
	} catch (XStreamException e) {
	    fail("exception is thrown");
	}
    }

    @Test
    public void testFromXMLWithoutTypeIsParsed() {
	SchemaSerializator serializator = new SchemaSerializator();
	try {
	    List<FieldDefinition> fields = serializator.fromXML(new File(
		    "data/schema-without-type.xml"));
	    List<FieldDefinition> fieldsOriginal = getFields();
	    assertEquals(fieldsOriginal.get(0).getName(),
		    fields.get(0).getName());
	    assertEquals(null, fields.get(0).getType());
	    assertEquals(fieldsOriginal.get(0).getLength(),
		    fields.get(0).getLength());
	} catch (XStreamException e) {
	    fail("exception is thrown");
	}
    }

    @Test
    public void testFromXMLWithoutLengthIsParsed() {
	SchemaSerializator serializator = new SchemaSerializator();
	try {
	    List<FieldDefinition> fields = serializator.fromXML(new File(
		    "data/schema-without-length.xml"));
	    List<FieldDefinition> fieldsOriginal = getFields();
	    assertEquals(fieldsOriginal.get(0).getName(),
		    fields.get(0).getName());
	    assertEquals(fieldsOriginal.get(0).getType(),
		    fields.get(0).getType());
	    assertEquals(null, fields.get(0).getLength());
	} catch (XStreamException e) {
	    fail("exception is thrown");
	}
    }

    @Test(expected = XStreamException.class)
    public void testFromXMLWithoutFieldThrowsException() {
	SchemaSerializator serializator = new SchemaSerializator();
	List<FieldDefinition> fields = serializator.fromXML(new File(
		"data/schema-invalid-without-field.xml"));
    }

    @Test(expected = XStreamException.class)
    public void testFromXMLWithHalfFieldTagThrowsException() {
	SchemaSerializator serializator = new SchemaSerializator();
	List<FieldDefinition> fields = serializator.fromXML(new File(
		"data/schema-invalid-with-half-field.xml"));
    }

    @Test(expected = XStreamException.class)
    public void testFromXMLSomeFieldHasHalfFieldTagThrowsException() {
	SchemaSerializator serializator = new SchemaSerializator();
	List<FieldDefinition> fields = serializator.fromXML(new File(
		"data/schema-invalid-some-field-has-half-field.xml"));
    }

    @AfterClass
    public static void printXML() {
	SchemaSerializator serializator = new SchemaSerializator();
	String xml = serializator.toXML(getFields());
	System.out.println(xml);
    }


}
