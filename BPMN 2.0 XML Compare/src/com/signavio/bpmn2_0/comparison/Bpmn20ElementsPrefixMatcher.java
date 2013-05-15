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

import com.signavio.util.xml.XmlNamespace;
import com.signavio.util.xml.diff.ElementsPrefixMatcher;

public class Bpmn20ElementsPrefixMatcher extends ElementsPrefixMatcher {
	public Bpmn20ElementsPrefixMatcher() {
		// the elements are ordered alphabetically; please keep it that way
		addMatchInfo("/(collaboration|dataState|dataStore|dataStoreReference|definitions|documentation|"
				+ "extensionElements|ioSpecification|laneSet|lane|participant|process|sequenceFlow|startEvent|"
				+ "subProcess|task|transformation)", "/" + XmlNamespace.BPMN20.getPrefix() + ":$1");

		addMatchInfo("/(BPMNDiagram|BPMNPlane)", "/" + XmlNamespace.BPMN20DI.getPrefix() + ":$1");
	}
}
