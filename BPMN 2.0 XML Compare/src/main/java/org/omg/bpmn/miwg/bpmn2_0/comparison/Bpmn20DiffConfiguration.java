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

package org.omg.bpmn.miwg.bpmn2_0.comparison;

import org.omg.bpmn.miwg.util.xml.diff.ElementsPrefixMatcher;
import org.omg.bpmn.miwg.util.xml.diff.XmlDiffConfiguration;

public class Bpmn20DiffConfiguration extends XmlDiffConfiguration {
	
	//@formatter:off
	private static final String[] ignoredNodes = { // "/bpmn:definitions/bpmn:globalTask",
		//"//extensionElements/signavio:signavioMetaData"
	};

	private static final String[] ignoredAttributes = {
		"/definitions/@exporterVersion"
	};
	
	private static final String[] idsAndIdRefs = {"id", "dataStoreRef", "bpmnElement"};
	
	private static final String[][]	defaultAttributeValueRegexes = {
		{"top", "false"},{"bottom", "false"},{"left", "false"},	{"right", "false"}
	};
	
	private static final String[] optionalAttributes = {};
	
	//@formatter:on
	
	/**
	 * Keep the matchiner up to date as the XPath expressions above will not match an XML element not listed there!!!
	 */
	private static final ElementsPrefixMatcher	prefixMatcher					= new Bpmn20ElementsPrefixMatcher();

	
	public Bpmn20DiffConfiguration() {
		super(ignoredNodes, ignoredAttributes, optionalAttributes,
				idsAndIdRefs, null, null, null, prefixMatcher,
				defaultAttributeValueRegexes);
	}
}
