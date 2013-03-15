package es.icarto.gvsig.schema.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.Types;

import org.junit.AfterClass;
import org.junit.Test;

import com.iver.cit.gvsig.fmap.drivers.FieldDescription;

import es.icarto.gvsig.schema.SchemaSerializator;


public class TestSchemaSerializator {

    private static FieldDescription[] getFields() {
	FieldDescription[] fields = new FieldDescription[5];

	fields[0] = new FieldDescription();
	fields[0].setFieldName("test_varchar");
	fields[0].setFieldType(Types.VARCHAR);
	fields[0].setFieldLength(2);

	fields[1] = new FieldDescription();
	fields[1].setFieldName("test_double");
	fields[1].setFieldType(Types.DOUBLE);
	fields[1].setFieldLength(3);

	fields[2] = new FieldDescription();
	fields[2].setFieldName("test_integer");
	fields[2].setFieldType(Types.INTEGER);
	fields[2].setFieldLength(1);

	fields[3] = new FieldDescription();
	fields[3].setFieldName("test_boolean");
	fields[3].setFieldType(Types.BOOLEAN);
	fields[3].setFieldLength(3);

	fields[4] = new FieldDescription();
	fields[4].setFieldName("test_date");
	fields[4].setFieldType(Types.DATE);
	fields[4].setFieldLength(3);

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

	// should have none of the following tags
	assertFalse(xml.contains("FieldDescription-array"));
	assertFalse(xml.contains("FieldDescription"));
	assertFalse(xml.contains("<fieldName>"));
	assertFalse(xml.contains("<fieldType>"));
	assertFalse(xml.contains("<fieldLength>"));
	assertFalse(xml.contains("<defaulValue"));
	assertFalse(xml.contains("<fieldDecimalCount>"));
	assertFalse(xml.contains("<fieldAlias>"));

    }

    @Test
    public void testFromXML() {
	SchemaSerializator serializator = new SchemaSerializator();
	FieldDescription[] fields = serializator.fromXML(new File(
		"data/schema.xml"));
	assertTrue(5 == fields.length);
    }

    @Test
    public void testFieldsAreRetrievedInTheSameOrderThanTheyAreInXML() {
	SchemaSerializator serializator = new SchemaSerializator();
	FieldDescription[] fields = serializator.fromXML(new File(
		"data/schema.xml"));
	FieldDescription[] fieldsOriginal = getFields();
	assertEquals(fieldsOriginal[0].getFieldName(), fields[0].getFieldName());
	assertTrue(fieldsOriginal[0].getFieldType() == fields[0].getFieldType());
	assertEquals(fieldsOriginal[1].getFieldName(), fields[1].getFieldName());
	assertTrue(fieldsOriginal[1].getFieldType() == fields[1].getFieldType());
	assertEquals(fieldsOriginal[2].getFieldName(), fields[2].getFieldName());
	assertTrue(fieldsOriginal[2].getFieldType() == fields[2].getFieldType());
	assertEquals(fieldsOriginal[3].getFieldName(), fields[3].getFieldName());
	assertTrue(fieldsOriginal[3].getFieldType() == fields[3].getFieldType());
	assertEquals(fieldsOriginal[4].getFieldName(), fields[4].getFieldName());
	assertTrue(fieldsOriginal[4].getFieldType() == fields[4].getFieldType());
    }

    @AfterClass
    public static void printXML() {
	SchemaSerializator serializator = new SchemaSerializator();
	String xml = serializator.toXML(getFields());

	System.out.println(xml);
    }


}
