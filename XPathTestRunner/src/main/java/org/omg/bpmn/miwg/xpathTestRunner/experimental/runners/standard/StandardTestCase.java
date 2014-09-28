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

package org.omg.bpmn.miwg.xpathTestRunner.experimental.runners.standard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.omg.bpmn.miwg.xpathTestRunner.experimental.InstanceParameter;
import org.omg.bpmn.miwg.xpathTestRunner.experimental.ScanParameters;
import org.omg.bpmn.miwg.xpathTestRunner.experimental.ScanUtil;

@RunWith(Parameterized.class)
public class StandardTestCase {

	private int i, j;

	private InstanceParameter param;
	
	public StandardTestCase(InstanceParameter parameter) {
		this.param = parameter;
	}

	@Before
	public void setUp() throws Exception {
		System.out.println("Running test:");
		System.out.println(param);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		assertEquals(i, j);
		fail("Not yet implemented");
	}
	
	protected static ScanParameters getParameterConfiguration() {
		return new StandardScanParameters();
	}

	@Parameters
	public static List<Object[]> data() throws IOException {
		return ScanUtil.data(new StandardScanParameters());
	}

}
