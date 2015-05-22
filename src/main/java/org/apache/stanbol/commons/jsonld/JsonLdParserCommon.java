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

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public abstract class JsonLdParserCommon {

	private static final Logger logger = Logger.getLogger(JsonLdParserCommon.class);
    
    /**
     * Uses the underlying Jettison to parse a JSON object.
     * 
     * @param jsonString
     *            JSON String representation.
     * @return
     */
    protected static JSONObject parseJson(String jsonString) throws JSONException {
        JSONObject jo = null;
        try {
            jo = new JSONObject(jsonString);
        } catch (JSONException e) {
            logger.info("Could not parse JSON string: " + jsonString, e);
            throw new JSONException("Could not parse JSON string: " + jsonString + e);
        }

        return jo;
    }
}
