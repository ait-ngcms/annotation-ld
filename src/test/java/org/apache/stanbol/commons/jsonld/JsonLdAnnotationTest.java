/*
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.stanbol.commons.jsonld;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 *
 */
public class JsonLdAnnotationTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSerializationToJsonLd() {
    	
        JsonLd jsonLd = createJsonLdObject();

        String actual = jsonLd.toString();
        toConsole(actual);
        String expected = "{\"@context\":{\"oa\":\"http://www.w3.org/ns/oa-context-20130208.json\"},\"@type\":\"oa:Annotation\",\"annotatedAt\":\"2012-11-10T09:08:07\",\"annotatedBy\":{\"@type\":\"http://xmlns.com/foaf/0.1/person\",\"name\":\"annonymous web user\"},\"body\":[{\"chars\":\"Vlad Tepes\",\"dc:language\":\"ro\",\"format\":\"text/plain\"},{\"foaf:page\":\"https://www.freebase.com/m/035br4\"}],\"motivatedBy\":\"oa:tagging\",\"serializedAt\":\"2012-11-10T09:08:07\",\"serializedBy\":{\"@type\":\"prov:SoftwareAgent\",\"foaf:homepage\":\"http://annotorious.github.io/\",\"name\":\"Annotorious\"},\"styledBy\":{\"@type\":\"oa:CssStyle\",\"source\":\"http://annotorious.github.io/latest/themes/dark/annotorious-dark.css\",\"styleClass\":\"annotorious-popup\"},\"target\":{\"selector\":{\"@type\":\"\"},\"source\":{\"@id\":\"http://europeana.eu/portal/record//15502/GG_8285.html\",\"@type\":\"dctypes:Text\",\"format\":\"text/html\"}}}";
        toConsole(expected);
        assertEquals(expected, actual);
        
        String actualIndent = jsonLd.toString(4);
        toConsole(actualIndent);
        String expectedIndent = "{\n    \"@context\": {\n        \"oa\": \"http://www.w3.org/ns/oa-context-20130208.json\"\n    },\n    \"@type\": \"oa:Annotation\",\n    \"annotatedAt\": \"2012-11-10T09:08:07\",\n    \"annotatedBy\": {\n        \"@type\": \"http://xmlns.com/foaf/0.1/person\",\n        \"name\": \"annonymous web user\"\n    },\n    \"body\": [\n        {\n            \"chars\": \"Vlad Tepes\",\n            \"dc:language\": \"ro\",\n            \"format\": \"text/plain\"\n        },\n        {\n            \"foaf:page\": \"https://www.freebase.com/m/035br4\"\n        }\n    ],\n    \"motivatedBy\": \"oa:tagging\",\n    \"serializedAt\": \"2012-11-10T09:08:07\",\n    \"serializedBy\": {\n        \"@type\": \"prov:SoftwareAgent\",\n        \"foaf:homepage\": \"http://annotorious.github.io/\",\n        \"name\": \"Annotorious\"\n    },\n    \"styledBy\": {\n        \"@type\": \"oa:CssStyle\",\n        \"source\": \"http://annotorious.github.io/latest/themes/dark/annotorious-dark.css\",\n        \"styleClass\": \"annotorious-popup\"\n    },\n    \"target\": {\n        \"selector\": {\n            \"@type\": \"\"\n        },\n        \"source\": {\n            \"@id\": \"http://europeana.eu/portal/record//15502/GG_8285.html\",\n            \"@type\": \"dctypes:Text\",\n            \"format\": \"text/html\"\n        }\n    }\n}";
        toConsole(expectedIndent);
        
        assertEquals(expectedIndent, actualIndent);
    }

	private JsonLd createJsonLdObject() {
		JsonLd jsonLd = new JsonLd();
        jsonLd.setUseTypeCoercion(false);
        jsonLd.setUseCuries(true);
        jsonLd.addNamespacePrefix("http://www.w3.org/ns/oa-context-20130208.json", "oa");

        JsonLdResource jsonLdResource = new JsonLdResource();
        jsonLdResource.setSubject("");
        jsonLdResource.addType("oa:Annotation");
        jsonLdResource.putProperty("annotatedAt", "2012-11-10T09:08:07");
        JsonLdProperty annotatedByProperty = new JsonLdProperty("annotatedBy");
        JsonLdPropertyValue v1 = new JsonLdPropertyValue();
        
        v1.setType("http://xmlns.com/foaf/0.1/person");
        v1.getValues().put("name", "annonymous web user");
        annotatedByProperty.addValue(v1);        
        
        jsonLdResource.putProperty(annotatedByProperty);
        
        jsonLdResource.putProperty("serializedAt", "2012-11-10T09:08:07");
        
        JsonLdProperty serializedByProperty = new JsonLdProperty("serializedBy");
        v1 = new JsonLdPropertyValue();
        
        v1.setType("prov:SoftwareAgent");
        v1.getValues().put("name", "Annotorious");
        v1.getValues().put("foaf:homepage", "http://annotorious.github.io/");
        serializedByProperty.addValue(v1);        
        
        jsonLdResource.putProperty(serializedByProperty);
        
        jsonLdResource.putProperty("motivatedBy", "oa:tagging");

        JsonLdProperty styledByProperty = new JsonLdProperty("styledBy");
        v1 = new JsonLdPropertyValue();
        
        v1.setType("oa:CssStyle");
        v1.getValues().put("source", "http://annotorious.github.io/latest/themes/dark/annotorious-dark.css");
        v1.getValues().put("styleClass", "annotorious-popup");
        styledByProperty.addValue(v1);        
        
        jsonLdResource.putProperty(styledByProperty);
        			
        JsonLdProperty bodyProperty = new JsonLdProperty("body");
        v1 = new JsonLdPropertyValue();
        
        v1.addType("oa:Tag");
        v1.addType("cnt:ContentAsText");
        v1.addType("dctypes:Text");
        v1.getValues().put("chars", "Vlad Tepes");
        v1.getValues().put("dc:language", "ro");
        v1.getValues().put("format", "text/plain");
        bodyProperty.addValue(v1);        

        JsonLdPropertyValue v2 = new JsonLdPropertyValue();
        
        v2.addType("oa:SemanticTag");
        v2.getValues().put("foaf:page", "https://www.freebase.com/m/035br4");
        bodyProperty.addValue(v2);        
        
        jsonLdResource.putProperty(bodyProperty);

        JsonLdProperty targetProperty = new JsonLdProperty("target");
        v1 = new JsonLdPropertyValue();
        
        v1.addType("oa:SpecificResource");

        JsonLdProperty sourceProperty = new JsonLdProperty("source");
        JsonLdPropertyValue v3 = new JsonLdPropertyValue();
        
        v3.setType("dctypes:Text");
        v3.getValues().put("@id", "http://europeana.eu/portal/record//15502/GG_8285.html");
        v3.getValues().put("format", "text/html");
        sourceProperty.addValue(v3);        
        v1.putProperty(sourceProperty);
        
        JsonLdProperty selectorProperty = new JsonLdProperty("selector");
        JsonLdPropertyValue v4 = new JsonLdPropertyValue();
        v4.setType(""); // if property is empty - set empty type
        
        selectorProperty.addValue(v4);        
        v1.putProperty(selectorProperty);
        
        targetProperty.addValue(v1);        
        
        jsonLdResource.putProperty(targetProperty);

        
        jsonLd.put(jsonLdResource);
		return jsonLd;
	}
    
    @Test
    public void testGsonSerializationToJsonLd() {
    	
        JsonLd jsonLd = createJsonLdObject();

        System.out.println("### jsonLdOriginal ###");
        toConsole(jsonLd.toString());
        
        //quickFix for the test
        ExclusionStrategy excludeLogger = new ExclusionStrategy() {
			
//			@Override
			public boolean shouldSkipField(FieldAttributes f) {
				if("logger".equals(f.getName()))
					return true;
				return false;
			}
			
//			@Override
			public boolean shouldSkipClass(Class<?> clazz) {
				if(LogManager.class.equals(clazz))
					return true;
					
				return false;
			}
		};
		
		//fix for deserialization (Comparator)
		ExclusionStrategy excludeComparator = new ExclusionStrategy() {
			
//			@Override
			public boolean shouldSkipField(FieldAttributes f) {
				if("propOrderComparator".equals(f.getName()))
					return true;
				return false;
			}
			
//			@Override
			public boolean shouldSkipClass(Class<?> clazz) {
				if(LogManager.class.equals(clazz))
					return true;
					
				return false;
			}
		};
		
        Gson gson = new GsonBuilder().addDeserializationExclusionStrategy(excludeComparator).addSerializationExclusionStrategy(excludeLogger).create();
        
        String jsonLdSerializedString = gson.toJson(jsonLd, JsonLd.class);
        System.out.println("### jsonLdSerializedString ###");
        toConsole(jsonLdSerializedString);
        
        JsonLd jsonLdDeserializedObject = gson.fromJson(jsonLdSerializedString, JsonLd.class);
        String jsonLdDeserializedString = jsonLdDeserializedObject.toString();
        System.out.println("### jsonLdDeserializedString ###");
        toConsole(jsonLdDeserializedString);
             
        assertEquals(jsonLd.toString(), jsonLdDeserializedString);  
        
        System.out.println("### jsonLdDeserializedString with indent ###");
        toConsole(jsonLdDeserializedObject.toString(4));
    }
        
    @Test
    public void testParseToJsonLd() {
    	
        JsonLd jsonLd = createJsonLdObject();
        String jsonLdStr = jsonLd.toString();

        System.out.println("### jsonLdStr ###");
        toConsole(jsonLd.toString());
        
        JsonLd parsedJsonLd = null;
        try {
			parsedJsonLd = JsonLdParser.parseExt(jsonLdStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
        parsedJsonLd.setUseTypeCoercion(false);
        parsedJsonLd.setUseCuries(true);
        parsedJsonLd.setApplyNamespaces(true);
//        parsedJsonLd.setUseTypeCoercion(true);
        
        Map<String, String> usedNamameSpaces = new HashMap<String, String>();
        usedNamameSpaces.put("http://www.w3.org/ns/oa-context-20130208.json","oa");
        parsedJsonLd.setNamespacePrefixMap(usedNamameSpaces);
        
        
        String parsedJsonLdStr = parsedJsonLd.toString();        
        System.out.println("### parsedJsonLdStr ###");
        toConsole(parsedJsonLdStr);
        toConsole(parsedJsonLd.toString(4));

        assertEquals(jsonLdStr, parsedJsonLdStr);
        assertNotNull(parsedJsonLd);
    }
        
    private void toConsole(String actual) {
        System.out.println(actual);
        String s = actual;
        s = s.replaceAll("\\\\", "\\\\\\\\");
        s = s.replace("\"", "\\\"");
        s = s.replace("\n", "\\n");
        System.out.println("Java String representation as code");
        System.out.println(s);
    }
}
