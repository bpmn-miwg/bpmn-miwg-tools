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

package org.omg.bpmn.miwg.xmlCompare.bpmn2_0.comparison;

import java.io.IOException;

import org.omg.bpmn.miwg.xmlCompare.configuration.BpmnCompareConfiguration;
import org.omg.bpmn.miwg.xmlCompare.util.xml.diff.ElementsPrefixMatcher;
import org.omg.bpmn.miwg.xmlCompare.util.xml.diff.XmlDiffConfiguration;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class Bpmn20DiffConfiguration extends XmlDiffConfiguration {
	
	//@formatter:off
    private static String[] defaultAttributes;
	private static String[] ignoredNodes;
	private static String[] ignoredAttributes;
	private static String[] idsAndIdRefs;
	private static String[] optionalAttributes;
	
	private static final String[][]	defaultAttributeValueRegexes = {
		{"top", "false"},{"bottom", "false"},{"left", "false"},	{"right", "false"}
	};
	
	
	//@formatter:on
	
	static {
		try {
			BpmnCompareConfiguration conf = BpmnCompareConfiguration.loadConfiguration();
			
			setConf(conf);
			
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Keep the matchiner up to date as the XPath expressions above will not match an XML element not listed there!!!
	 */
	private static final ElementsPrefixMatcher	prefixMatcher					= new Bpmn20ElementsPrefixMatcher();
	
	public Bpmn20DiffConfiguration() {
        super(defaultAttributes, ignoredNodes, ignoredAttributes,
                optionalAttributes,
				idsAndIdRefs, null, null, null, prefixMatcher,
				defaultAttributeValueRegexes);
	}
	
	public static void setConf(BpmnCompareConfiguration conf) {
        defaultAttributes = conf.getDefaultAttributes();
		ignoredNodes = conf.getIgnoredNodes();
		ignoredAttributes = conf.getIgnoredAttributes();
		optionalAttributes = conf.getOptionalAttributes();
		
		idsAndIdRefs = conf.getIdsAndIdRefs();
	}
}
