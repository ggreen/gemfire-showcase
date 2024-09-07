package com.vmware.data.services.gemfire.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.data.services.gemfire.demo.ComplexObject;
import com.vmware.data.services.gemfire.demo.SimpleObject;
import nyla.solutions.core.security.user.data.UserProfile;
import org.apache.geode.json.JsonDocument;
import org.apache.geode.json.JsonDocumentFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GemFireJsonTest
{
	private GemFireJson subject;
	@Mock
	private JsonDocumentFactory factory;

	@Mock
	private JsonDocument jsonDocument;

	private ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	 void beforeAll()
	{
		this.subject = new GemFireJson(factory);
	}

	@BeforeEach
	public void setUp()
	throws Exception
	{
	}


	@Test
	void assignJsonType_empty()
	{
		var type = Object.class.getName();
		assertNull(subject.addTypeToJson(null,type));
		assertNull(subject.addTypeToJson("",type));
		assertNull(subject.addTypeToJson(" \n",type));
	}
	@Test
	void assignJsonType_emptyObject() throws IOException
	{
		var json = "{}";
		var expected = "{\"@type\":\"java.lang.Object\"}";

		var type = Object.class.getName();
		var actual = subject.addTypeToJson(json,type);
		System.out.println(actual);
		assertTrue(actual.contains(type));
		assertEquals(expected,actual);

		objectMapper.readTree(actual);

	}
	@Test
	void assignJsonType_emptyArray() throws IOException
	{
		var json = "{\"items\": []}";
		var expected = "{\"@type\":\"java.lang.Object\",\"items\": []}";

		var type = Object.class.getName();
		var actual = subject.addTypeToJson(json,type);
		System.out.println(actual);
		assertTrue(actual.contains(type));
		assertEquals(expected,actual);
		objectMapper.readTree(actual);

	}
	@Test
	void assignJsonType_ObjectArray() throws IOException
	{
		var json = "{\n" +
				"    \"glossary\": {\n" +
				"        \"title\": \"example glossary\",\n" +
				"\t\t\"GlossDiv\": {\n" +
				"            \"title\": \"S\",\n" +
				"\t\t\t\"GlossList\": {\n" +
				"                \"GlossEntry\": {\n" +
				"                    \"ID\": \"SGML\",\n" +
				"\t\t\t\t\t\"SortAs\": \"SGML\",\n" +
				"\t\t\t\t\t\"GlossTerm\": \"Standard Generalized Markup Language\",\n" +
				"\t\t\t\t\t\"Acronym\": \"SGML\",\n" +
				"\t\t\t\t\t\"Abbrev\": \"ISO 8879:1986\",\n" +
				"\t\t\t\t\t\"GlossDef\": {\n" +
				"                        \"para\": \"A meta-markup language, used to create markup languages such as " +
				"DocBook.\",\n" +
				"\t\t\t\t\t\t\"GlossSeeAlso\": [\"GML\", \"XML\"]\n" +
				"                    },\n" +
				"\t\t\t\t\t\"GlossSee\": \"markup\"\n" +
				"                }\n" +
				"            }\n" +
				"        }\n" +
				"    }\n" +
				"}";
		var expected = "{\"@type\":\"java.lang.Object\",[]}";

		var type = Object.class.getName();
		var actual = subject.addTypeToJson(json,type);
		System.out.println(actual);
		assertTrue(actual.contains(type));
		objectMapper.readTree(actual);

	}
	@Test
	public void reject_invalid_json()
	throws IOException
	{
		var invalid = "{\"sdsdfdfsf "+ subject.JSON_TYPE_ATTRIBUTE+"}";


		try
		{
			var instance = subject.fromJSON(invalid);
			fail("invalid JSON");
		}
		catch(IllegalArgumentException e){
			System.out.println("exception:"+e);
			assertTrue(e.getMessage().contains(invalid));
		}

	}

	@Test
	public void reject_valid_json_with_missing_type()
	throws IOException
	{
		var invalid = "{\"email\": 1223, \"firstName\": \"testFirst\"}";


		try
			{
			var instance = subject.fromJSON(invalid);
			fail("Must catch missing type");
		}
		catch(IllegalArgumentException e){
			e.printStackTrace();
			System.out.println("exception:"+e);
			assertTrue(e.getMessage().contains(subject.JSON_TYPE_ATTRIBUTE));
		}

	}

	@Test
	public void testFromJSON_Complex()
	throws Exception
	{

		ComplexObject expected = new ComplexObject();
		var simpleObject = new SimpleObject();
		simpleObject.setFieldInt(1);
		simpleObject.setBigDecimal(new BigDecimal(232));
		simpleObject.setFiedByte( Byte.valueOf("2"));
		simpleObject.setFieldBoolean(true);
		simpleObject.setFieldChar('e');
		simpleObject.setFieldLongObject(Long.valueOf(223));
		simpleObject.setFieldShortObject(Short.valueOf("2"));
		//TODO: simpleObject.setLocalDateTime(LocalDateTime.now()); //DEFECT
		//TODO: simpleObject.setLocalDate(LocalDate.now()); //DEFECT
		//TODO: simpleObject.setLocalTime(LocalTime.now()); //DEFECT
		//TODO: simpleObject.setFieldTime(new Time(Calendar.getInstance().getTime().getTime())); //DEFECT
		//TODO: simpleObject.setException(new Exception("Sdsds")); //DEFECT
		//TODO: simpleObject.setError(new Error("Sdsd")); //DEFECT
		//TODO: simpleObject.setFieldCalendar(Calendar.getInstance()); //DEFECT
		simpleObject.setFieldClass(GemFireJsonTest.class);
		simpleObject.setFieldDate(Calendar.getInstance().getTime());
		simpleObject.setFieldTimestamp(new Timestamp(Calendar.getInstance().getTimeInMillis()));

		expected.setSimpleObject((SimpleObject)simpleObject.clone());
		expected.setMap(new HashMap<String,String>());
		expected.getMap().put("sdsd","Sdsds");
		ComplexObject[] arrays = {(ComplexObject)expected.clone()};
		expected.setComplexArray(arrays);
		expected.setComplexObject((ComplexObject)expected.clone());


		var duplicate = (ComplexObject)expected.clone();
		assertEquals(duplicate,expected);

		String json = subject.toJsonFromNonPdxObject(expected);


		var pdx = this.subject.fromJSON(json);
//
//		assertNotNull(pdx);
//
//		var actual = pdx.toJson();
//
//		assertEquals(expected,actual);
	}

	@Test
	public void test_PDX_instance_to_json()
	throws Exception {

		when(factory.create(anyString())).thenReturn(this.jsonDocument);
		var userProfile = new UserProfile();
		userProfile.setEmail("a@pivotal.io");

		var json = subject.toJsonFromNonPdxObject(userProfile);
		assertNotNull(json);
		System.out.println("JSON:"+json);

		when(jsonDocument.toJson()).thenReturn(json);

		var pdx = this.subject.fromJSON(json);
		assertNotNull(pdx);

		var actual = this.subject.toJsonFromNonPdxObject(pdx);
		assertTrue(actual.contains("a@pivotal.io"));
		assertTrue(actual.contains("@type"));
		assertTrue(actual.contains(userProfile.getClass().getName()));
	}

}
