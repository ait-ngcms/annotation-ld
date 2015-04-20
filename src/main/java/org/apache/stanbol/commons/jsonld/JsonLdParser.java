/*
* Licensed to the Apache Software Foundation (ASF) under one or more
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * The JsonLdParser can be used to parse a given JSON-LD String representation
 * into a JSON-LD data structure.
 * 
 * @author Fabian Christ
 */
public class JsonLdParser extends JsonLdParserCommon {

	private static final Logger logger = Logger.getLogger(JsonLdParser.class);

	/**
	 * Parse the given String into a JSON-LD data structure.
	 * 
	 * @param jsonLdString
	 *            A JSON-LD String.
	 * @return JSON-LD data structure.
	 */
	public static JsonLd parse(String jsonLdString) throws Exception {
		JsonLd jld = null;

		JSONObject jo = parseJson(jsonLdString);
		if (jo != null) {
			jld = new JsonLd();
			parseSubject(jo, jld, 1, null);
		}

		return jld;
	}

	/**
	 * Parse the given String into a JSON-LD data structure without subject.
	 * Will be replaced through the annotation-utils.AnnotatioParser
	 * @param jsonLdString
	 *            A JSON-LD String.
	 * @return JSON-LD data structure.
	 */
	@Deprecated
	public static JsonLd parseExt(String jsonLdString) throws Exception {
		JsonLd jld = null;

		JSONObject jo = parseJson(jsonLdString);
		if (jo != null) {
			jld = new JsonLd();
			parseSubjectExt(jo, jld, 1, null);
		}

		return jld;
	}

	/**
	 * Parses a single subject.
	 * 
	 * @param jo
	 *            JSON object that holds the subject's data.
	 * @param jld
	 *            JsonLd object to add the created subject resource.
	 */
	@SuppressWarnings("deprecation")
	private static void parseSubject(JSONObject jo, JsonLd jld, int bnodeCount,
			String profile) {

		// The root subject is used for cases where no explicit subject is
		// specified. We need
		// at least one dummy subject (bnode) to support type coercion because
		// types are assigned to
		// subjects.
		JsonLdResource subject = new JsonLdResource();

		try {
			if (jo.has(JsonLdCommon.CONTEXT)) {
				JSONObject context = jo.getJSONObject(JsonLdCommon.CONTEXT);
				for (int i = 0; i < context.names().length(); i++) {
					String name = context.names().getString(i).toLowerCase();
					if (name.equals(JsonLdCommon.COERCE)) {
						JSONObject typeObject = context.getJSONObject(name);
						for (int j = 0; j < typeObject.names().length(); j++) {
							String property = typeObject.names().getString(j);
							String type = typeObject.getString(property);
							subject.putPropertyType(property, type);
						}
					} else {
						jld.addNamespacePrefix(context.getString(name), name);
					}
				}

				jo.remove(JsonLdCommon.CONTEXT);
			}

			// If there is a local profile specified for this subject, we
			// use that one. Otherwise we assign the profile given by the
			// parameter.
			if (jo.has(JsonLdCommon.PROFILE)) {
				String localProfile = unCURIE(jo
						.getString(JsonLdCommon.PROFILE), jld
						.getNamespacePrefixMap());
				profile = localProfile;
				jo.remove(JsonLdCommon.PROFILE);
			}
			subject.setProfile(profile);

			if (jo.has(JsonLdCommon.ID)) {
				// Check for N subjects
				Object subjectObject = jo.get(JsonLdCommon.ID);
				if (subjectObject instanceof JSONArray) {
					// There is an array of subjects. We create all subjects
					// in sequence.
					JSONArray subjects = (JSONArray) subjectObject;
					for (int i = 0; i < subjects.length(); i++) {
						parseSubject(subjects.getJSONObject(i), jld,
								bnodeCount++, profile);
					}
				} else {
					String subjectName = unCURIE(jo
							.getString(JsonLdCommon.ID), jld
							.getNamespacePrefixMap());
					subject.setSubject(subjectName);
				}
				jo.remove(JsonLdCommon.ID);
			} else {
				// No subject specified. We create a dummy bnode
				// and add this subject.
				subject.setSubject("_:bnode" + bnodeCount);
				jld.put(subject.getSubject(), subject);
			}

			// Iterate through the rest of properties and unCURIE property
			// values
			// depending on their type
			if (jo.names() != null && jo.names().length() > 0) {
				for (int i = 0; i < jo.names().length(); i++) {
					String property = jo.names().getString(i);
					Object valueObject = jo.get(property);
					handleProperty(jld, subject, property, valueObject);
				}
			}

		} catch (JSONException e) {
			logger.error(
					"There were JSON problems when parsing the JSON-LD String",
					e);
			e.printStackTrace();
		}
	}

	/**
	 * Parses a single subject if subject is undefined.
	 * Will be replaced through the annotation-utils.AnnotatioParser
	 * 
	 * @param jo
	 *            JSON object that holds the subject's data.
	 * @param jld
	 *            JsonLd object to add the created subject resource.
	 */
	@SuppressWarnings("deprecation")
	@Deprecated  
	private static void parseSubjectExt(JSONObject jo, JsonLd jld, int bnodeCount,
			String profile) {

		// The root subject is used for cases where no explicit subject is
		// specified. We need
		// at least one dummy subject (bnode) to support type coercion because
		// types are assigned to
		// subjects.
		JsonLdResource subject = new JsonLdResource();

		try {
			if (jo.has(JsonLdCommon.CONTEXT)) {
				Object rawContextValue = jo.get(JsonLdCommon.CONTEXT);
				
				if(rawContextValue instanceof JSONObject){
					JSONObject context = jo.getJSONObject(JsonLdCommon.CONTEXT);
					for (int i = 0; i < context.names().length(); i++) {
						String name = context.names().getString(i).toLowerCase();
						if (name.equals(JsonLdCommon.COERCE)) {
							JSONObject typeObject = context.getJSONObject(name);
							for (int j = 0; j < typeObject.names().length(); j++) {
								String property = typeObject.names().getString(j);
								String type = typeObject.getString(property);
								subject.putPropertyType(property, type);
							}
	//					} else {
	//						jld.addNamespacePrefix(context.getString(name), name);
						}
					}
				}else if(rawContextValue instanceof String){
					subject.putPropertyType("oa", (String)rawContextValue);
				}

//				jo.remove(JsonLdCommon.CONTEXT);

			}

			// If there is a local profile specified for this subject, we
			// use that one. Otherwise we assign the profile given by the
			// parameter.
			if (jo.has(JsonLdCommon.PROFILE)) {
				String localProfile = unCURIE(jo
						.getString(JsonLdCommon.PROFILE), jld
						.getNamespacePrefixMap());
				profile = localProfile;
				jo.remove(JsonLdCommon.PROFILE);
			}
			subject.setProfile(profile);

			if (jo.has(JsonLdCommon.ID)) {
				// Check for N subjects
				Object subjectObject = jo.get(JsonLdCommon.ID);
				if (subjectObject instanceof JSONArray) {
					// There is an array of subjects. We create all subjects
					// in sequence.
					JSONArray subjects = (JSONArray) subjectObject;
					for (int i = 0; i < subjects.length(); i++) {
						parseSubject(subjects.getJSONObject(i), jld,
								bnodeCount++, profile);
					}
				} else {
					String subjectName = unCURIE(jo
							.getString(JsonLdCommon.ID), jld
							.getNamespacePrefixMap());
					subject.setSubject(subjectName);
				}
				jo.remove(JsonLdCommon.ID);
			} else {
				// No subject specified. We create a dummy bnode
				// and add this subject.
//				subject.setSubject("_:bnode" + bnodeCount);
//				jld.put(subject.getSubject(), subject);
				subject.setSubject("");
				jld.put(subject.getSubject(), subject);
			}

			// Iterate through the rest of properties and unCURIE property
			// values
			// depending on their type
			if (jo.names() != null && jo.names().length() > 0) {
				for (int i = 0; i < jo.names().length(); i++) {
					String property = jo.names().getString(i);
					Object valueObject = jo.get(property);
					handlePropertyExt(jld, subject, property, valueObject);
				}
			}
//			jld.put(subject.getSubject(), subject); //
			jld.put(subject); //

		} catch (JSONException e) {
			logger.error(
					"There were JSON problems when parsing the JSON-LD String",
					e);
			e.printStackTrace();
		}
	}

	private static void handleProperty(JsonLd jld, JsonLdResource subject,
			String property, Object valueObject) {
		if (valueObject instanceof JSONObject) {
			JSONObject jsonValue = (JSONObject) valueObject;
			subject.putProperty(property, convertToMapAndList(jsonValue, jld
					.getNamespacePrefixMap()));
		} else if (valueObject instanceof JSONArray) {
			JSONArray arrayValue = (JSONArray) valueObject;
			subject.putProperty(property, convertToMapAndList(arrayValue, jld
					.getNamespacePrefixMap()));
		} else if (valueObject instanceof String) {
			String stringValue = (String) valueObject;
			subject.putProperty(property, unCURIE(stringValue, jld
					.getNamespacePrefixMap()));
		} else {
			subject.putProperty(property, valueObject);
		}
	}

    /**
     * This method converts JSON string to map.
     * @param value The input string
     * @return resulting map
     */
    public static Map<String, String> splitToMap(String value) {
    	String reg = "\",\"|\\},\"";
        Map<String,String> res = new HashMap<String, String>();
//    	if (!value.isEmpty() && value.contains("#")) {
        if (!value.isEmpty()) {
//        	if (value.contains(",") && !value.contains("\",\"")) {
//	        	reg = ",|\\},\"";
//				value = value.substring(1, value.length() - 1); // remove braces
//		        String[] arrValue = value.split(reg);
//		        for (String string : arrValue) {
//		            String[] mapPair = string.split(":");
//		            res.put(mapPair[0], mapPair[1]);
//		        }
//	    	} else {
		        String[] arrValue = value.split(reg);
		        for (String string : arrValue) {
		            String[] mapPair = string.split("\":\"");
		            String right = "";
		            if (mapPair.length > 1 && mapPair[1] != null)
		            	right = mapPair[1].replace("\"", "");
		            res.put(mapPair[0], right);
//		            res.put(mapPair[0], mapPair[1]);
//		        }
	    	}
        }
        return res;
    }
    
    /**
     * This method converts JSON string to array.
     * @param value The input string
     * @return resulting map
     */
    public static String[] splitToArray(String value) {
        return value.split("\\},\\{");
    }
    
    /**
     * This method converts Annotation JSON list string to array.
     * @param value The input string
     * @return resulting map
     */
    public static String[] splitAnnotationListStringToArray(String value) {
        return value.split("\\}\\},\\{");
    }
    
    /**
     * This method calculates end position for current key area.
     * @param key
     * @param keyList
     * @param data
     * @return next area position
     */
    private static int findNextKey(String key, List<String> keyList, String data) {
    	int nextIdx = 0;
    	Iterator<String> itr = keyList.iterator();
    	int idx = 0;
    	if (data != null && data.length() > 0) {
	    	nextIdx = data.length();
	    	if (data.contains(key)) {
		    	idx = data.indexOf(key);
		    	while (itr.hasNext()) {
		    		String currentKey = itr.next();
		    		currentKey = "\"" + currentKey + "\"";
		        	if (data.contains(currentKey) && !currentKey.equals(key)) {
		        		int curNextIdx = data.indexOf(currentKey);
		        		if (curNextIdx > idx && curNextIdx < nextIdx) {
		        			nextIdx = curNextIdx;
		        		}
		        	}
		    	}
	    	}
    	}
    	return nextIdx;
    }
    
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Deprecated
	private static void handlePropertyExt(JsonLd jld, JsonLdResource subject,
			String property, Object valueObject) throws JSONException {
		
		if (valueObject instanceof JSONObject) {			
			JSONObject jsonValue = (JSONObject) valueObject;
			String jsonValueStr = jsonValue.toString();
			String jsonValueNormalized = normalize(jsonValueStr);
			
			JsonLdProperty jlp = new JsonLdProperty(property);
			String mapString = jsonValueNormalized.substring(1, jsonValueNormalized.length() - 1); // remove braces
			JsonLdPropertyValue jlpv = new JsonLdPropertyValue();
			
			if(JsonLd.CONTEXT.equals(property)){
				parseContext(jld, valueObject);
				return;
			}
			
			
			if (!mapString.contains("}")) {
				/**
				 * The property is a single map - without complex objects inside
				 */
				jlpv = parseJsonLdPropertyValue(mapString);
				jlp.addValue(jlpv);
			} else {
				Map<String, String> propMap = 
						(Map<String, String>) convertToMapAndList(jsonValue, jld.getNamespacePrefixMap());
				List<String> keyList = new ArrayList<String>();
				keyList.addAll(propMap.keySet());
				Iterator<?> it = propMap.entrySet().iterator();
				while (it.hasNext()) {
				    Map.Entry pairs = (Map.Entry)it.next();
				    String key = "\"" + pairs.getKey().toString() + "\"";
				    int nextPos = findNextKey(key, keyList, mapString);
				    int startPos = mapString.indexOf(key) + key.length() + 1; // +1 for ':'
				    String value = mapString.substring(startPos, nextPos);
				    if (value.lastIndexOf(",") == value.length() - 1) {
					    value = value.substring(0, value.length() - 1);
				    }
					value = value.substring(1, value.length() - 1); // remove braces
				    key = key.replace("\"", "");
				    if (!value.contains(",")) {
				    	if (!value.contains("\":\"")) {
				    		/**
				    		 * simple key value pair
				    		 */				    	
				    		jlpv.getValues().put(key, value);
				    	} else {
				    		/**
				    		 * complex value with ':'
				    		 */
					        JsonLdProperty subProperty = new JsonLdProperty(key);
							JsonLdPropertyValue sub_jlpv = new JsonLdPropertyValue();
							Map<String, String> propMap2 = splitToMap(value);
							Iterator<?> it2 = propMap2.entrySet().iterator();
							while (it2.hasNext()) {
							    Map.Entry pairs2 = (Map.Entry)it2.next();
							    String key2 = pairs2.getKey().toString().replace("\"", "").replace("{", "").replace("}", "");
							    String value2 = pairs2.getValue().toString();//.replace("\"", "").replace("{", "").replace("}", "");
							    if (value2.length() < 2) {
							    	value2 = "";
							    }
							    sub_jlpv.getValues().put(key2, value2);
								subProperty.addValue(sub_jlpv);
						        jlpv.putProperty(subProperty);
							}
				    	}
				    } else {
				    	if (value.contains(":") && value.contains("euType") && !key.equals("selector")) { // for the euType
//					    	if (value.contains(":") && value.contains("#")) { // for the euType
				    		jlpv.getValues().put(key, value);
				    	} else {
					        JsonLdProperty subProperty = new JsonLdProperty(key);
							JsonLdPropertyValue sub_jlpv = parseJsonLdPropertyValue(value);
							subProperty.addValue(sub_jlpv);
					        jlpv.putProperty(subProperty);
				    	}
				    }
				}
				jlp.addValue(jlpv);
			}
			subject.putProperty(jlp);
		} else if (valueObject instanceof JSONArray) {
			JSONArray arrayValue = (JSONArray) valueObject;
			String jsonValueStr = arrayValue.toString();
			String jsonValueNormalized = normalize(jsonValueStr);
			JsonLdProperty jlp = new JsonLdProperty(property);
			String arrayString = jsonValueNormalized.substring(1, jsonValueNormalized.length() - 1);
			String[] propArray = splitToArray(arrayString);
			Iterator<String> itr = Arrays.asList(propArray).iterator();
			while (itr.hasNext()) {
				String propString = normalizeArrayString(itr.next());
				JsonLdPropertyValue jlpv = parseJsonLdPropertyValue(propString);
				jlp.addValue(jlpv);
			}
			subject.putProperty(jlp);
		} else if (valueObject instanceof String) {
//			JsonLdPropertyValue jlpv = new JsonLdPropertyValue();
//			JsonLdProperty jlp = new JsonLdProperty(property);

			String stringValue = (String) valueObject;
			subject.putProperty(property, unCURIE(stringValue, jld
					.getNamespacePrefixMap()));
//    		jlpv.getValues().put(property, unCURIE(stringValue, jld
//					.getNamespacePrefixMap()));
//			jlp.addValue(jlpv);
		} else {
//			JsonLdPropertyValue jlpv = new JsonLdPropertyValue();
//			JsonLdProperty jlp = new JsonLdProperty(property);

			subject.putProperty(property, valueObject);
//    		jlpv.getValues().put(property, (String) valueObject);
//			jlp.addValue(jlpv);
		}
	}

	private static void parseContext(JsonLd jld, Object valueObject)
			throws JSONException {
		HashMap< String, String> namespaces = new HashMap<String, String>();
		String key;
		for (@SuppressWarnings("rawtypes")
		Iterator iterator = ((JSONObject) valueObject).keys(); iterator.hasNext();) {
			key = (String) iterator.next();
			namespaces.put(key, ((JSONObject) valueObject).getString(key));
		}
		jld.setNamespacePrefixMap(namespaces);
	}

	/**
	 * This method parses the JsonLdPropertyValue string to the object.
	 * @param mapString
	 */
	@SuppressWarnings("rawtypes")
	private static JsonLdPropertyValue parseJsonLdPropertyValue(String mapString) {
		JsonLdPropertyValue jlpv = new JsonLdPropertyValue();
		Map<String, String> propMap = splitToMap(mapString);
		Iterator<?> it = propMap.entrySet().iterator();
		while (it.hasNext()) {
		    Map.Entry pairs = (Map.Entry)it.next();
		    String key = pairs.getKey().toString().replace("\"", "").replace("{", "").replace("}", "");
		    String value = pairs.getValue().toString().replace("\"", "").replace("{", "").replace("}", "");
		    jlpv.getValues().put(key, value);
		}
		return jlpv;
	}

	/**
	 * Converts a JSON object into a Map or List data structure.
	 * 
	 * <p>
	 * The JSON-LD implementation is based on Map and List data types. If the
	 * input is a JSONObject, it will be converted into a Map&lt;String,
	 * Object>. If the input is a JSONArray, it will be converted into a
	 * List&lt;Object>. Otherwise the input will be returned untouched.
	 * 
	 * @param input
	 *            Object that will be converted.
	 * @return
	 */
	private static Object convertToMapAndList(Object input,
			Map<String, String> namespacePrefixMap) {
		if (input instanceof JSONObject) {
			JSONObject jo = (JSONObject) input;

			// Handle IRIs
			if (jo.has(JsonLdCommon.ID)) {
				try {
					return new JsonLdIRI(unCURIE(
							jo.getString(JsonLdCommon.ID), namespacePrefixMap));
				} catch (JSONException e) {
					return null;
				}
			} else {
				// Handle arbitrary JSON
				return convertToMap(jo, namespacePrefixMap);
			}
		} else if (input instanceof JSONArray) {
			JSONArray ao = (JSONArray) input;
			return convertToList(ao, namespacePrefixMap);
		} else if (input instanceof String) {
			return unCURIE((String) input, namespacePrefixMap);
		} else {
			return input;
		}
	}

	/**
	 * Converts a JSONOBject into a Map&lt;String, Object>.
	 * 
	 * @param jo
	 *            JSONOBject to be converted.
	 * @return A Map that represents the same information as the JSONOBject.
	 */
	private static Map<String, Object> convertToMap(JSONObject jo,
			Map<String, String> namespacePrefixMap) {
		Map<String, Object> jsonMap = null;
		try {
			if (jo.names() != null && jo.names().length() > 0) {
				jsonMap = new HashMap<String, Object>();
				for (int i = 0; i < jo.names().length(); i++) {
					String name = jo.names().getString(i);
					jsonMap.put(name, convertToMapAndList(jo.get(name),
							namespacePrefixMap));
				}
			}
		} catch (JSONException e) { /* ignored */
		}
		return jsonMap;
	}

	private static List<Object> convertToList(JSONArray arrayValue,
			Map<String, String> namespacePrefixMap) {

		List<Object> values = new ArrayList<Object>();
		for (int i=0; i<arrayValue.length(); i++) {
			try {
				values.add(convertToMapAndList(arrayValue.get(i), namespacePrefixMap));
			} catch (JSONException e) {
				logger.error("Error converting JSONArray to list", e);
			}
		}
		
		return values;
	}
	
	/**
	 * Replaces the CURIE prefixes with the namespace to create full qualified
	 * IRIs.
	 * 
	 * @param curie
	 *            The CURIE to create an IRI from.
	 * @param namespacePrefixMap
	 *            A Map with known namespaces.
	 * @return
	 */
	private static String unCURIE(String curie,
			Map<String, String> namespacePrefixMap) {
//		for (String namespace : namespacePrefixMap.keySet()) {
//			String prefix = namespacePrefixMap.get(namespace) + ":";
//			curie = curie.replaceAll(prefix, namespace);
//		}
		return curie;
	}

    private static String normalize(String value) {
        String s = value;
        s = s.replaceAll("\\\\\\\\", "\\\\");
        s = s.replace("\\\"", "\"");
        s = s.replace("\\", "");
        s = s.replace("\\n", "\n");
//        s = s.replace("\"", "");
        return s;
    }
    
    private static String normalizeArrayString(String value) {
        String s = value;
        s = s.replace("{", "");
        s = s.replace("}", "");
        return s;
    }
    
}
