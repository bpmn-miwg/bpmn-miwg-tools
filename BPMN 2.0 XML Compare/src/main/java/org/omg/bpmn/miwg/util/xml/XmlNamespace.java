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

package org.omg.bpmn.miwg.util.xml;

import javax.xml.XMLConstants;

/**
 * Enumeration of all relevant namespaces in our projects and corresponding prefixes
 * 
 * @author philipp.maschke
 * 
 */
public enum XmlNamespace {

	// @formatter:off
	BPMN20(					"bpmn",						"http://www.omg.org/spec/BPMN/20100524/MODEL"), 
	BPMN20DI(				"bpmndi",					"http://www.omg.org/spec/BPMN/20100524/DI"), 
	BPMN20OMGDC(			"omgdc",					"http://www.omg.org/spec/DD/20100524/DC"),
	BPMN20OMGDI( 			"omgdi",					"http://www.omg.org/spec/DD/20100524/DI"),	
	Signavio(				"signavio",					"http://www.signavio.com"),
	
	XML(					XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI),
	XMLNS(					XMLConstants.XMLNS_ATTRIBUTE, XMLConstants.XMLNS_ATTRIBUTE_NS_URI),
	DEFAULT(				XMLConstants.DEFAULT_NS_PREFIX, BPMN20.getUri());
	// @formatter:on

	private String prefix;
	private String uri;

	XmlNamespace(String prefix, String uri) {
		this.prefix = prefix;
		this.uri = uri;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getUri() {
		return uri;
	}
}
