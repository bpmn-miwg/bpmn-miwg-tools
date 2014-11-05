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

package org.omg.bpmn.miwg.xmlCompare.util.xml;

import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionException;
import javax.xml.xpath.XPathFunctionResolver;

import org.apache.commons.lang3.StringUtils;

public class SignavioXPathFunctionResolver implements XPathFunctionResolver {

	private static final String FUNCTION_IS_EMPTY = "isEmpty";

	private static final class SignavioIsEmptyFunction implements XPathFunction {

		@Override
		public Object evaluate(@SuppressWarnings("rawtypes") List args) throws XPathFunctionException {
			if (args.size() == 1) {
				String s = (String) args.get(0);
				return StringUtils.isBlank(s);
			}
			return null;
		}
	}

	@Override
	public XPathFunction resolveFunction(QName functionName, int arity) {
		if (functionName == null) {
			throw new NullPointerException("function name cannot be null");
		}

		if (XmlNamespace.Signavio.getUri().equals(functionName.getNamespaceURI())) {
			if (FUNCTION_IS_EMPTY.equals(functionName.getLocalPart())) {
				return new SignavioIsEmptyFunction();

				// } else if (<your function here>.equals(functionName.getLocalPart())) {

			}
		}

		return null;
	}

}
