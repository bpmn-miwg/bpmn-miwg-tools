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

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.omg.bpmn.miwg.testresult.IndexWriter;
import org.omg.bpmn.miwg.xpathTestRunner.base.TestInstance;
import org.omg.bpmn.miwg.xpathTestRunner.base.TestManager;
import org.omg.bpmn.miwg.xpathTestRunner.base.TestOutput;

/**
 * 
 * @author Tim Stephenson
 * 
 */
@RunWith(value = Parameterized.class)
public class XPathTest {

	private static final String S = File.separator;
	private static final String RESOURCE_BASE_DIR = ".." + S + ".." + S
			+ "bpmn-miwg-test-suite";

	private static final String RPT_DIR = "target" + S + "site";

	private static File baseDir;

    private static List<File> files = new ArrayList<File>();

	private TestInstance instance;

	public XPathTest(TestInstance instance) {
		this.instance = instance;
	}

    @AfterClass
    public static void tearDown() {
        File idx = new File(RPT_DIR, "overview.html");
        System.out.println("writing index to " + idx);
        IndexWriter.write(XPathTest.class.getSimpleName(), idx,
                files);
    }

	@Parameters //(name= "{index}: {0}")
	public static Collection<Object[]> data() throws Throwable {
		baseDir = new File(RESOURCE_BASE_DIR).getCanonicalFile();
		TestManager manager = new TestManager();
		List<TestInstance> testInstances = TestInstance.buildTestInstances(
				manager, baseDir.getCanonicalPath());

		List<Object[]> l = new LinkedList<Object[]>();

		for (TestInstance ti : testInstances) {
			l.add(new Object[] { ti });
		}

		return l;
	}

	@Test
	public void testRun() throws IOException {
		TestManager manager = new TestManager();
		TestOutput out = new TestOutput(instance, RPT_DIR);
		
		manager.executeTests(instance, RPT_DIR);
		
		//Assert.assertEquals(0, instance.getFindings());
		
		out.close();
        assertNotNull(out.getFile());
        files.add(out.getFile());
	}

}
