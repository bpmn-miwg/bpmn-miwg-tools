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
package org.omg.bpmn.miwg;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.custommonkey.xmlunit.Difference;
import org.junit.BeforeClass;
import org.junit.Test;
import org.omg.bpmn.miwg.bpmn2_0.comparison.Bpmn20ConformanceChecker;

/**
 * 
 * @author Tim Stephenson
 * 
 */
public class XmlCompareTest {

	private static final String S = File.separator;
	private static final String RESOURCE_BASE_DIR = ".." + S + ".." + S
			+ "bpmn-miwg-test-suite" + S;

	private static final String A_DIR = "A - Fixed Digrams with Variations of Attributes";

	private static final String B_DIR = "B - Validate that tool covers conformance class set";

	private static File baseDir;

	private static File testADir;

	private static File testBDir;

	private static List<String> tools;

	private static Bpmn20ConformanceChecker checker;

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

		checker = new Bpmn20ConformanceChecker();
	}

	@Test
	public void testA10() {
		String testName = "A.1.0";
		reportSummary(testName, doTest(testName));
	}

	@Test
	public void testA20() {
		String testName = "A.2.0";
		reportSummary(testName, doTest(testName));
	}

	@Test
	public void testA30() {
		String testName = "A.3.0";
		reportSummary(testName, doTest(testName));
	}

	@Test
	public void testA40() {
		String testName = "A.4.0";
		reportSummary(testName, doTest(testName));
	}

	@Test
	public void testB10() {
		String testName = "B.1.0";
		reportSummary(testName, doTest(testName));
	}

	@Test
	public void testB20() {
		String testName = "B.2.0";
		reportSummary(testName, doTest(testName));
	}

	private Map<String, List<Difference>> doTest(String testName) {
		Map<String, List<Difference>> results = new HashMap<String, List<Difference>>();
		for (String tool : tools) {
			try {
				List<Difference> diffs = checker.getSignificantDifferences(
						getReferenceFile(testName + ".bpmn"),
						getBpmnFile(tool, testName + "-roundtrip.bpmn"));
				results.put(tool, diffs);
			} catch (FileNotFoundException e) {
				// Not a test failure but missing the file to compare
				results.put(tool, convertToDiff(e));
			} catch (Exception e) {
				e.printStackTrace();
				fail(e.getClass().getName() + ":" + e.getMessage());
			}
		}
		return results;
	}

	private List<Difference> convertToDiff(FileNotFoundException e) {
		List<Difference> diffs = new ArrayList<Difference>();
		diffs.add(new SimpleDifference(1000, e.getMessage()));
		return diffs;
	}

	private File getReferenceFile(String filename) {
		return getBpmnFile("Reference", filename);
	}

	private File getBpmnFile(String dir, String filename) {
		if (filename.startsWith("A")) {
			return new File(testADir, dir + S + filename);
		} else {
			return new File(testBDir, dir + S + filename);
		}
	}

	private void reportSummary(String testName,
			Map<String, List<Difference>> results) {
		for (Map.Entry<String, List<Difference>> result : results.entrySet()) {
			String msg = "Issues found in implementation of " + testName
					+ " by " + result.getKey();
			System.out.println(msg + ": " + result.getValue().size());
			// assertEquals(msg, 0, result.getValue().size());
		}
	}

}
