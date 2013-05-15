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

package com.signavio.util.xml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

/**
 * Implementation of the namespace context interfaces of the JavaX and XmlUnit libraries, which contains all relevant
 * namespaces used in our projects.<br/>
 * Uses {@link XmlNamespace} to define this context.
 * 
 * @author Philipp Maschke
 * 
 */
public class SignavioNamespaceContext implements NamespaceContext, org.custommonkey.xmlunit.NamespaceContext {

	private Map<String, XmlNamespace> prefixMap = new HashMap<String, XmlNamespace>();
	private Map<String, XmlNamespace> uriMap = new HashMap<String, XmlNamespace>();

	public SignavioNamespaceContext() {
		for (XmlNamespace ns : XmlNamespace.values()) {
			prefixMap.put(ns.getPrefix(), ns);
			uriMap.put(ns.getUri(), ns);
		}
	}

	@Override
	public String getNamespaceURI(String prefix) {
		XmlNamespace ns = prefixMap.get(prefix);
		if (ns != null)
			return ns.getUri();
		else
			return XMLConstants.NULL_NS_URI;
	}

	@Override
	public String getPrefix(String namespaceURI) {
		XmlNamespace ns = uriMap.get(namespaceURI);
		if (ns != null)
			return ns.getPrefix();
		else
			return XMLConstants.DEFAULT_NS_PREFIX;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Iterator getPrefixes(String namespaceURI) {
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Iterator getPrefixes() {
		return prefixMap.keySet().iterator();
	}
}
