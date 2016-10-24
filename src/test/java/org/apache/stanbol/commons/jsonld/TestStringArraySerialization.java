package org.apache.stanbol.commons.jsonld;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestStringArraySerialization {

	String fieldName = "testprop";
	String val1 = "val1";
	String val2 = "val2";
	
	@Test
	public void testStringArraySize1Minimized() {
		String[] array = new String[]{val1};
		JsonLd jsonLd = createJsonLdObj(fieldName, array, true);
		
		String actual = jsonLd.toString();
		String expected = "{\"testprop\":\"val1\"}";
		
		assertEquals(actual, expected);
	}

	@Test
	public void testStringArraySize1Verbose() {
		String[] array = new String[]{val1};
		JsonLd jsonLd = createJsonLdObj(fieldName, array, false);
		
		String actual = jsonLd.toString();
		String expected = "{\"testprop\":[\"val1\"]}";
		
		assertEquals(actual, expected);
	}
	
	
	@Test
	public void testStringArraySize2() {
		String[] array = new String[]{val1, val2};
		JsonLd jsonLd = createJsonLdObj(fieldName, array, false);
		
		String actual = jsonLd.toString();
		String expected = "{\"testprop\":[\"val1\", \"val2\"]}";
		
		assertEquals(actual, expected);
	}
	
	private JsonLd createJsonLdObj(String fieldName, String[] array, boolean minimized) {
		JsonLd jsonLd = new JsonLd();
		jsonLd.setUseTypeCoercion(false);
		jsonLd.setUseCuries(false);
		
		JsonLdResource jsonLdResource = new JsonLdResource();
		jsonLd.putStringArrayProperty(fieldName, array, jsonLdResource, minimized);
		jsonLd.put(jsonLdResource);
		
		return jsonLd;
	}
	
	public void testStringArrayVerbose(String[] array) {
		JsonLd jsonLd = new JsonLd();
		jsonLd.setUseTypeCoercion(false);
		jsonLd.setUseCuries(false);
		
		JsonLdResource jsonLdResource = new JsonLdResource();
		jsonLd.putStringArrayProperty("testprop", array, jsonLdResource);		
	}
}
