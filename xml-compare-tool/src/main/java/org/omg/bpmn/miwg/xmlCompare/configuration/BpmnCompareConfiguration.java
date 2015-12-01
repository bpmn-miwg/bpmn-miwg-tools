/**
 * The MIT License (MIT)
 * Copyright (c) 2013 Signavio, OMG BPMN Model Interchange Working Group
 *
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */
package org.omg.bpmn.miwg.xmlCompare.configuration;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BpmnCompareConfiguration {

    private String[] defaultAttributes;
	private String[] ignoredNodes;
	private String[] ignoredAttributes;
	private String[] idsAndIdRefs;
	private String[] optionalAttributes;
	private String[] generatedNodeIds; 

	public static BpmnCompareConfiguration loadConfiguration(String confName)
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();

		if (confName == null) {
			confName = "/org/omg/bpmn/miwg/configuration/conf.json";
		}
		InputStream is = BpmnCompareConfiguration.class
				.getResourceAsStream(confName);

		BpmnCompareConfiguration conf = mapper.readValue(is,
				BpmnCompareConfiguration.class);

		return conf;
	}

	public static BpmnCompareConfiguration loadConfiguration()
			throws JsonParseException, JsonMappingException, IOException {
		return loadConfiguration(null);
	}

	/* Getter & Setter */
    public String[] getDefaultAttributes() {
        return defaultAttributes;
    }

    public void setDefaultAttributes(String[] defaultAttributes) {
        this.defaultAttributes = defaultAttributes;
    }

	public String[] getIgnoredNodes() {
		return ignoredNodes;
	}

	public void setIgnoredNodes(String[] ignoredNodes) {
		this.ignoredNodes = ignoredNodes;
	}

	public String[] getIgnoredAttributes() {
		return ignoredAttributes;
	}

	public void setIgnoredAttributes(String[] ignoredAttributes) {
		this.ignoredAttributes = ignoredAttributes;
	}

	public String[] getIdsAndIdRefs() {
		return idsAndIdRefs;
	}

	public void setIdsAndIdRefs(String[] idsAndIdRefs) {
		this.idsAndIdRefs = idsAndIdRefs;
	}

	public String[] getOptionalAttributes() {
		return optionalAttributes;
	}

	public void setOptionalAttributes(String[] optionalAttributes) {
		this.optionalAttributes = optionalAttributes;
	}

	public String[] getGeneratedNodeIds() {
		return generatedNodeIds;
	}

	public void setGeneratedNodeIds(String[] generatedNodeIds) {
		this.generatedNodeIds = generatedNodeIds;
	}
}
