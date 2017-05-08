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

import java.util.Comparator;
import java.util.HashMap;

/**
 * A comparator for JSON-LD maps to ensure the order of certain key elements
 * like '#', '@', 'a' in JSON-LD output.
 *
 * @author Fabian Christ
 */
public class JsonComparator implements Comparator<Object> {
	
	static final HashMap<String, Integer> propOrderMap = new HashMap<String, Integer>();
	static {
		propOrderMap.put(JsonLdCommon.CONTEXT, 10);
		propOrderMap.put(JsonLdCommon.ID, 20);
		propOrderMap.put(JsonLdCommon.TYPE, 30);
		propOrderMap.put(JsonLdCommon.CREATED, 40);
		propOrderMap.put(JsonLdCommon.CREATOR, 50);
		propOrderMap.put(JsonLdCommon.GENERATED, 60);
		propOrderMap.put(JsonLdCommon.GENERATOR, 70);
		propOrderMap.put(JsonLdCommon.BODY, 80);
		propOrderMap.put(JsonLdCommon.TARGET, 90);
		propOrderMap.put(JsonLdCommon.VIA, 100);
	}

    @Override
    public int compare(Object arg0, Object arg1) {
    	Integer leftOrder = propOrderMap.get(arg0);
    	Integer rightOrder = propOrderMap.get(arg1);
		if(leftOrder == null)
			leftOrder = Math.abs(arg0.hashCode());
    	if(rightOrder == null)
    		rightOrder = Math.abs(arg1.hashCode());
		
    	return Integer.compare(leftOrder, rightOrder);
    }

}
