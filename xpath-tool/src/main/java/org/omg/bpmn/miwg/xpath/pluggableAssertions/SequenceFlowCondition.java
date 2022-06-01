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

import org.omg.bpmn.miwg.api.AnalysisOutput;
import org.omg.bpmn.miwg.xpath.util.AbstractXpathCheck;
import org.w3c.dom.Node;

public class SequenceFlowCondition implements Assertion {

	private String expectedExpression;
	private String expectedType;

	public SequenceFlowCondition(String expectedType, String expectedExpression) {
		this.expectedType = expectedType;
		this.expectedExpression = expectedExpression;
	}

	@Override
	public void check(Node node, AbstractXpathCheck check, AnalysisOutput output)
			throws Throwable {

		Node conditionExpressionNode = AssertionUtil.findNode(node,
				"bpmn:conditionExpression");
		if (conditionExpressionNode == null) {
			output.pluggableAssertionFinding(getClass().getSimpleName(),
					"Could not find a conditionExpression element", null);
			return;
		}

		String actualType = AssertionUtil.getAttributeValue(
				conditionExpressionNode, "xsi:type");
		String actualExpression = AssertionUtil.getTextContent(node);

		if (!expectedType.equals(actualType)) {
			output.pluggableAssertionFinding(getClass().getSimpleName(), String
					.format("The type should be '%s', but it is '%s'",
							expectedType, actualType), null);
		}

		if (!expectedExpression.equals(actualExpression)) {
			output.pluggableAssertionFinding(getClass().getSimpleName(), String
					.format("The expression should be '%s', but it is '%s'",
							expectedExpression, actualExpression), null);
		}

		output.pluggableAssertionOk(getClass().getSimpleName(),
				"Condition expression is correct");
	}

}
