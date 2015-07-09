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

package org.omg.bpmn.miwg.common;

import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceConstants;
import org.custommonkey.xmlunit.DifferenceListener;
import org.w3c.dom.Node;

public class DomSubtreeCompareDifferenceListener implements DifferenceListener {

	/*
	 * Receive notification that 2 nodes are different.
	 * 
	 * @param difference a Difference instance as defined in {@link
	 * DifferenceConstants DifferenceConstants } describing the cause of the
	 * difference and containing the detail of the nodes that differ
	 * 
	 * @return int one of the RETURN_ ... constants describing how this
	 * difference was interpreted
	 */
	@Override
	public int differenceFound(Difference difference) {

		/*
		 * if (!isChildOf(difference.getTestNodeDetail().getNode(), testRoot) ||
		 * !isChildOf(difference.getControlNodeDetail().getNode(), refRoot)) {
		 * return RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL; }
		 */

		switch (difference.getId()) {

		case DifferenceConstants.TEXT_VALUE_ID:

			String controlString = WhitespaceUtil
					.normalizeWhitespace(difference.getControlNodeDetail()
							.getValue());

			String testString = WhitespaceUtil.normalizeWhitespace(difference
					.getTestNodeDetail().getValue());

			if (controlString.equals(testString)) // Text nodes only differ
													// in whitespaces
				return RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
			else
				return RETURN_ACCEPT_DIFFERENCE;

		case DifferenceConstants.CHILD_NODELIST_LENGTH_ID:
		case DifferenceConstants.ATTR_SEQUENCE_ID:
		case DifferenceConstants.CHILD_NODELIST_SEQUENCE_ID:
			return RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;

		case DifferenceConstants.CHILD_NODE_NOT_FOUND_ID:
			// Something has been added in the test document
			if (difference.getControlNodeDetail().getNode() == null)
				return RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
			else
				return RETURN_ACCEPT_DIFFERENCE;

		default:

			return RETURN_ACCEPT_DIFFERENCE;
		}
	}

	/*
	 * Receive notification that a comparison between 2 nodes has been skipped
	 * because the node types are not comparable by the DifferenceEngine
	 * 
	 * @param control the control node being compared
	 * 
	 * @param test the test node being compared
	 * 
	 * @see DifferenceEngine
	 */
	@Override
	public void skippedComparison(Node control, Node test) {

	}
}
