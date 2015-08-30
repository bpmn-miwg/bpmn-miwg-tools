/**
 * The MIT License (MIT)
 * Copyright (c) 2013 OMG BPMN Model Interchange Working Group
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

package org.omg.bpmn.miwg.xpath.pluggableAssertions;

import org.omg.bpmn.miwg.xpath.common.AbstractXpathCheck;
import org.omg.bpmn.miwg.xpath.common.NameSpaceContexts;
import org.w3c.dom.Node;

public class SequenceFlowCondition implements Assertion {

	private String expectedExpression;
	private String expectedType;

	public SequenceFlowCondition(String expectedType, String expectedExpression) {
		this.expectedType = expectedType;
		this.expectedExpression = expectedExpression;
	}
	
	@Override
	public void check(Node node, AbstractXpathCheck check) throws Throwable {
		
		Node conditionExpressionNode = AssertionUtil.findNode(node, "bpmn:conditionExpression");
		if (conditionExpressionNode == null) {
			check.finding(null, "Could not find a conditionExpression element");
			return;
		}
		
		String actualType = AssertionUtil.getAttributeValue(conditionExpressionNode, "xsi:type");
		String actualExpression = AssertionUtil.getTextContent(node);

		if (!expectedType.equals(actualType)) {
			check.finding(null, String.format("The type should be '%s', but it is '%s'", expectedType, actualType));
		}
		
		if (!expectedExpression.equals(actualExpression)) {
			check.finding(null, String.format("The expression should be '%s', but it is '%s'", expectedExpression, actualExpression));
		}
		
	}

}
