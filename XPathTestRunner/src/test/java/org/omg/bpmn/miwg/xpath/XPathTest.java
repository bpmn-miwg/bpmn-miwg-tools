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
package org.omg.bpmn.miwg.xpath;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.omg.bpmn.miwg.xpathTestRunner.base.TestInfo;
import org.omg.bpmn.miwg.xpathTestRunner.base.TestOutput;
import org.omg.bpmn.miwg.xpathTestRunner.tests.B_1_0_Test;
import org.omg.bpmn.miwg.xpathTestRunner.tests.B_2_0_Test;
import org.omg.bpmn.miwg.xpathTestRunner.tests.ValidatorTest;

/**
 * 
 * @author Tim Stephenson
 * 
 */
public class XPathTest {

	private static final String S = File.separator;
	private static final String RESOURCE_BASE_DIR = ".." + S + ".." + S
			+ "bpmn-miwg-test-suite" + S;

	private static final String A_DIR = "A - Fixed Digrams with Variations of Attributes";

	private static final String B_DIR = "B - Validate that tool covers conformance class set";
	private static final String RPT_DIR = "target" + S + "surefire-reports";

	private static File baseDir;

	private static File testADir;

	private static File testBDir;

	private static List<String> tools;

	@BeforeClass
	public static void setUp() throws Exception {
		baseDir = new File(RESOURCE_BASE_DIR);
		System.out.println("Looking for MIWG resources in: "
				+ baseDir.getAbsolutePath());

		testADir = new File(baseDir, A_DIR);
		assertTrue("Dir does not exist at " + testADir.getAbsolutePath()
				+ ", please ensure you have checked out the MIWG resources.",
				testADir.exists());

		testBDir = new File(baseDir, B_DIR);
		assertTrue("Dir does not exist at " + testBDir.getAbsolutePath()
				+ ", please ensure you have checked out the MIWG resources.",
				testBDir.exists());

		tools = new ArrayList<String>();
		for (String dir : testADir.list()) {
			if (!dir.equals("Reference") && !dir.startsWith(".")) {
				tools.add(dir);
			}
		}
		new File(RPT_DIR).mkdirs();
	}

	@Test
	public void testSchemaValid() {
		doTest(new ValidatorTest(), "B.1.0");
		doTest(new ValidatorTest(), "B.2.0");
	}

	@Test
	public void testB10() {
		doTest(new B_1_0_Test(), "B.1.0");
	}

	@Test
	public void testB20() {
		doTest(new B_2_0_Test(), "B.2.0");
	}

	private void doTest(org.omg.bpmn.miwg.xpathTestRunner.testBase.Test test, String baseFileName) {
		for (String tool : tools) {
			TestInfo info = new TestInfo(baseDir.getAbsolutePath()
					+ testBDir.getName(), tool, baseFileName
							+ "-roundtrip.bpmn");
			TestOutput out = null;
			try {
				out = new TestOutput(test, info, RPT_DIR);
				out.println("Running Tests for " + test.getName() + ":");
				int numOK = 0;
				int numIssue = 0;
				if (test.isApplicable(info.getFile().getName())) {
					out.println("> TEST " + test.getName());
					try {
						test.init(out);
						test.execute(info.getFile().getAbsolutePath());
					} catch (FileNotFoundException e) {
						// Most likely this is because this tool has not
						// provided a test file
						out.println(e.getMessage());
					} catch (Exception e) {
						out.println("Exception during test execution of "
								+ test.getName());
						e.printStackTrace();
						fail(e.getMessage());
					} catch (Throwable e) {
						e.printStackTrace();
						fail(e.getMessage());
					}
					out.println();
					out.println("  TEST " + test.getName() + " results:");
					out.println("  * OK    : " + test.ResultsOK());
					out.println("  * ISSUES: " + test.ResultsIssue());
					out.println();

					numOK += test.ResultsOK();
					numIssue += test.ResultsIssue();
				} else {
					fail("File to test is not applicable: "
							+ info.getFile().getName());
				}
			} catch (IOException e) {
				fail(e.getMessage());
			} finally {
				out.close();
			}
		}
	}

}
