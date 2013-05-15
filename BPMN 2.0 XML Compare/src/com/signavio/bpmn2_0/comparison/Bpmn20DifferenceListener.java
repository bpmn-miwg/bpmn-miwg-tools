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

package com.signavio.bpmn2_0.comparison;

import org.apache.commons.lang3.StringUtils;
import org.custommonkey.xmlunit.Difference;

import com.signavio.util.xml.diff.AbstractXmlDifferenceListener;

public class Bpmn20DifferenceListener extends AbstractXmlDifferenceListener {

	@Override
	protected boolean canDifferenceBeIgnored(Difference difference) {
		return false;
	}

	@Override
	protected boolean canDifferingIdBeIgnored(Difference difference, String controlId, String testId) {
		return StringUtils.isEmpty(controlId) == StringUtils.isEmpty(testId);
	}

	@Override
	protected boolean canMissingIdBeIgnored(Difference difference, String controlId) {
		return false;
	}

}
