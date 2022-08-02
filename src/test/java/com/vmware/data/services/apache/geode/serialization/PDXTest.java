package com.vmware.data.services.apache.geode.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.data.services.apache.geode.demo.ComplexObject;
import com.vmware.data.services.apache.geode.demo.SimpleObject;
import com.vmware.data.services.apache.geode.serialization.exception.InvalidSerializationKeyException;
import nyla.solutions.core.security.user.data.UserProfile;
import nyla.solutions.core.util.Organizer;
import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheClosedException;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Region;
import org.apache.geode.pdx.PdxInstance;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

@Disabled
public class PDXTest
{
	private PDX subject;
	private ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	 void beforeAll()
	{
		this.subject = new PDX();
	}

	@BeforeEach
	public void setUp()
	throws Exception
	{
		Cache cache = null;


		try {
			cache = CacheFactory.getAnyInstance();

		}
		catch(CacheClosedException e) {
			if(cache == null)
				cache = new CacheFactory().create();
		}
		catch(Exception e)
		{
			//should already have factory
		}
	}//-------------------------------------------

	@Test
	public void test_convert_from_json_to_pdxInstance()
	throws Exception
	{
		UserProfile expected = new UserProfile();
		expected.setEmail("user");

		String json = subject.toJsonFromNonPdxObject(expected);

		System.out.println("json:"+json);

		assertTrue(json != null && json.length() > 0);

		assertTrue(json.contains("@type"));

		PdxInstance pdx = this.subject.fromJSON(json);

		UserProfile actual = (UserProfile)pdx.getObject();
		assertEquals(expected,actual);


	}
	@Test
	void assignJsonType_empty()
	{
		String type = Object.class.getName();
		assertNull(subject.addTypeToJson(null,type));
		assertNull(subject.addTypeToJson("",type));
		assertNull(subject.addTypeToJson(" \n",type));
	}
	@Test
	void assignJsonType_emptyObject() throws IOException
	{
		String json = "{}";
		String expected = "{\"@type\":\"java.lang.Object\"}";

		String type = Object.class.getName();
		String actual = subject.addTypeToJson(json,type);
		System.out.println(actual);
		assertTrue(actual.contains(type));
		assertEquals(expected,actual);

		objectMapper.readTree(actual);

	}
	@Test
	void assignJsonType_emptyArray() throws IOException
	{
		String json = "{\"items\": []}";
		String expected = "{\"@type\":\"java.lang.Object\",\"items\": []}";

		String type = Object.class.getName();
		String actual = subject.addTypeToJson(json,type);
		System.out.println(actual);
		assertTrue(actual.contains(type));
		assertEquals(expected,actual);
		objectMapper.readTree(actual);

	}
	@Test
	void assignJsonType_ObjectArray() throws IOException
	{
		String json = "{\n" +
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
		String expected = "{\"@type\":\"java.lang.Object\",[]}";

		String type = Object.class.getName();
		String actual = subject.addTypeToJson(json,type);
		System.out.println(actual);
		assertTrue(actual.contains(type));
		objectMapper.readTree(actual);

	}
	@Test
	public void reject_invalid_json()
	throws IOException
	{
		String invalid = "{\"sdsdfdfsf "+ subject.JSON_TYPE_ATTRIBUTE+"}";


		try
		{
			PdxInstance instance = subject.fromJSON(invalid);
			fail("invalid JSON");
		}
		catch(IllegalArgumentException e){
			System.out.println("exception:"+e);
			assertTrue(e.getMessage().contains(invalid));
		}

	}//-------------------------------------------

	@Test
	public void reject_valid_json_with_missing_type()
	throws IOException
	{
		String invalid = "{\"email\": 1223, \"firstName\": \"testFirst\"}";


		try
			{
			PdxInstance instance = subject.fromJSON(invalid);
			fail("Must catch missing type");
		}
		catch(IllegalArgumentException e){
			e.printStackTrace();
			System.out.println("exception:"+e);
			assertTrue(e.getMessage().contains(subject.JSON_TYPE_ATTRIBUTE));
		}

	}//-------------------------------------------
	@Test
	public void testFromJSON_Complex()
	throws Exception
	{

		ComplexObject expected = new ComplexObject();
		SimpleObject simpleObject = new SimpleObject();
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
		simpleObject.setFieldClass(PDXTest.class);
		simpleObject.setFieldDate(Calendar.getInstance().getTime());
		simpleObject.setFieldTimestamp(new Timestamp(Calendar.getInstance().getTimeInMillis()));

		expected.setSimpleObject((SimpleObject)simpleObject.clone());
		expected.setMap(new HashMap<String,String>());
		expected.getMap().put("sdsd","Sdsds");
		ComplexObject[] arrays = {(ComplexObject)expected.clone()};
		expected.setComplexArray(arrays);
		expected.setComplexObject((ComplexObject)expected.clone());


		ComplexObject duplicate = (ComplexObject)expected.clone();
		assertEquals(duplicate,expected);

		String json = subject.toJsonFromNonPdxObject(expected);


		PdxInstance pdx = this.subject.fromJSON(json);
		
		assertNotNull(pdx);

		ComplexObject actual = (ComplexObject)pdx.getObject();

		assertEquals(expected,actual);
	}//-------------------------------------------
	@Test
	public void test_PDX_instance_to_json()
	throws Exception {
		UserProfile userProfile = new UserProfile();
		userProfile.setEmail("a@pivotal.io");

		String json = subject.toJsonFromNonPdxObject(userProfile);
		System.out.println("JSON:"+json);

		PdxInstance pdx = this.subject.fromJSON(json);

		String actual = this.subject.toJSON(
				pdx,UserProfile.class.getName());
		assertTrue(actual.contains("a@pivotal.io"));
		assertTrue(actual.contains("@type"));
		assertTrue(actual.contains(userProfile.getClass().getName()));
	}//-------------------------------------------
	@Test
	public void test_region_to_jsonMapEntry()
	throws Exception
	{
		BigDecimal expectedKey = new BigDecimal("25");
		PdxInstance pdxInstance = subject.fromObject(new ComplexObject());

		Set<Serializable> expectedKeys = Organizer.toSet(expectedKey);
		Region<Serializable,PdxInstance> region = Mockito.mock(Region.class);
		Mockito.when(region.keySetOnServer()).thenReturn(expectedKeys);
		Mockito.when(region.get(Mockito.any())).thenReturn(pdxInstance);

		Collection<Serializable> keys = region.keySetOnServer();

		SerializationPdxEntryWrapper wrapper;

		for (Serializable key: keys)
		{
			wrapper = subject.toSerializePdxEntryWrapper(key,ComplexObject.class.getName(),region.get(key));
			assertNotNull(wrapper);
			assertEquals(expectedKey.getClass().getName(),wrapper.getKeyClassName());
			assertEquals(key,wrapper.deserializeKey());
			assertEquals(region.get(key),wrapper.toPdxInstance());

		}
	}

	@Test
	public void wrapper_json()
	throws IOException
	{
		Long keyLong = 12L;
		PdxInstance pdx = this.subject.fromObject(new UserProfile());
		SerializationPdxEntryWrapper expected = new SerializationPdxEntryWrapper(keyLong,
				UserProfile.class.getName(),pdx);

		String json = this.subject.toJsonFromNonPdxObject(expected);
		assertTrue(!json.contains(SerializationPdxEntryWrapper.class.getName()));

		SerializationPdxEntryWrapper actual = this.subject.toSerializePdxEntryWrapperFromJson(json);
		assertEquals(expected,actual);

		Double keyDouble = 12.0;
		expected = new SerializationPdxEntryWrapper(keyLong,UserProfile.class.getName(),pdx);
		json = this.subject.toJsonFromNonPdxObject(expected);
		actual = this.subject.toSerializePdxEntryWrapperFromJson(json);
		assertEquals(expected,actual);

		BigDecimal keyBigDecimal = BigDecimal.TEN;
		expected = new SerializationPdxEntryWrapper(keyBigDecimal,UserProfile.class.getName(),pdx);
		json = this.subject.toJsonFromNonPdxObject(expected);
		actual = this.subject.toSerializePdxEntryWrapperFromJson(json);
		assertEquals(expected,actual);


		String keystring = "sdsdsd";
		expected = new SerializationPdxEntryWrapper(keystring,UserProfile.class.getName(),pdx);
		json = this.subject.toJsonFromNonPdxObject(expected);
		actual = this.subject.toSerializePdxEntryWrapperFromJson(json);
		assertEquals(expected,actual);


		try
		{

			UserProfile invalid = new UserProfile();
			expected = new SerializationPdxEntryWrapper(invalid,UserProfile.class.getName(),pdx);
			fail("Invalid key");
		}
		catch(InvalidSerializationKeyException e){

		}



	}
}
