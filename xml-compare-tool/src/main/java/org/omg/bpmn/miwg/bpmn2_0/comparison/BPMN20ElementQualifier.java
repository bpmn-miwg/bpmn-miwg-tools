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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.custommonkey.xmlunit.ElementNameAndAttributeQualifier;
import org.custommonkey.xmlunit.ElementNameQualifier;
import org.w3c.dom.Element;

public class BPMN20ElementQualifier extends ElementNameAndAttributeQualifier {

	private ElementNameQualifier nameQualifier = new ElementNameQualifier();
	private Set<String> generatedIdElems;

	public BPMN20ElementQualifier(Collection<String> elementsWithGeneratedIds) {
		super("id");
		generatedIdElems = new HashSet<String>(elementsWithGeneratedIds);
	}

	public BPMN20ElementQualifier(String... elementsWithGeneratedIds) {
		super("id");
		generatedIdElems = new HashSet<String>();
		for (String elem : elementsWithGeneratedIds) {
			generatedIdElems.add(elem);
		}
	}

	/**
	 * Exceptions from super class implementation: - definitions element always are considered as equal.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see org.custommonkey.xmlunit.ElementNameAndAttributeQualifier#qualifyForComparison(org.w3c.dom.Element,
	 *      org.w3c.dom.Element)
	 */
	@Override
	public boolean qualifyForComparison(Element control, Element test) {
		boolean result;
		if (generatedIdElems.contains(control.getNodeName())) {
			// do not use ids as they might be generated
			result = nameQualifier.qualifyForComparison(control, test);
		} else {
			result = super.qualifyForComparison(control, test);
		}

		return result;
	}
}
