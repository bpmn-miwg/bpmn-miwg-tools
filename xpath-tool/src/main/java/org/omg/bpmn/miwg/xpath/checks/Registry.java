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

package org.omg.bpmn.miwg.xpath.checks;

import java.util.LinkedList;
import java.util.List;

import org.omg.bpmn.miwg.xpath.util.AbstractXpathCheck;

public class Registry {
	
	private static List<AbstractXpathCheck> registeredChecks = null;
	
	private static void createRegistry() {
		registeredChecks = new LinkedList<AbstractXpathCheck>();
		registeredChecks.add(new A_1_0_Check());
		registeredChecks.add(new A_1_1_Check());
		registeredChecks.add(new A_1_2_Check());
		registeredChecks.add(new A_2_0_Check());
		registeredChecks.add(new A_3_0_Check());
		registeredChecks.add(new A_4_0_Check());
		registeredChecks.add(new A_4_1_Check());
		registeredChecks.add(new B_1_0_Check());
		registeredChecks.add(new B_2_0_Check());
		registeredChecks.add(new C_1_0_Check());
		registeredChecks.add(new C_1_1_Check());
		registeredChecks.add(new C_2_0_Check());
		registeredChecks.add(new C_3_0_Check());
		registeredChecks.add(new C_4_0_Check());
		registeredChecks.add(new DemoTechnicalSupportCheck());
	}

	public static List<AbstractXpathCheck> getChecks() {
		if (registeredChecks == null)
			createRegistry();
		return registeredChecks;
	}
	
}
