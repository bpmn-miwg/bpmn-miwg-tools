package org.omg.bpmn.miwg;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.xml.sax.SAXException;

/**
 * 
 * @author Sven Wagner-Boysen
 * 
 */
@RunWith(value = Parameterized.class)
public class XmlCompareTestXmlResult {

	private static final String S = File.separator;
	private static final String RESOURCE_BASE_DIR = ".." + S + ".." + S
			+ "bpmn-miwg-test-suite" + S;

	private static final String RPT_DIR = "target" + S + "site";
	private static final String REFERENCE_FOLDER_NAME = "Reference";

	private static File baseDir;

	private String tool;

	/**
	 * Several tests are run for each reference model. Valid values are
	 * "import", "-export", "-roundtrip".
	 */
	private Variant variant;
	/**
	 * The name of the test category's folder e.g.
	 * "A - Fixed Digrams with Variations of Attributes"
	 */
	private TestCategory testCategory;

	public XmlCompareTestXmlResult(String tool, TestCategory testCategory,
			Variant variant) {
		this.tool = tool;
		this.variant = variant;
		this.testCategory = testCategory;
	}

	@Parameters
	public static Collection<Object[]> data() throws Exception {
		setUp();
		ArrayList<Object[]> tools = new ArrayList<Object[]>();
		// Find all tools (Assume all did at least test category A)
		for (String dir : (new File(baseDir, TestCategory.A.toString())).list()) {
			if (!dir.equals("Reference") && !dir.startsWith(".")) {
				for (TestCategory c : TestCategory.values()) {
					for (Variant v : Variant.values()) {
						tools.add(new Object[] { dir, c, v });
					}
				}
			}
		}
		return tools;
	}

	public static void setUp() throws Exception {
		baseDir = new File(RESOURCE_BASE_DIR);
		System.out.println("Looking for MIWG resources in: "
				+ baseDir.getAbsolutePath());

		// Check resources availability
		for (TestCategory c : TestCategory.values()) {
			File testDir = new File(baseDir, c.toString());
			assertTrue(
					"Dir does not exist at "
							+ testDir.getAbsolutePath()
							+ ", please ensure you have checked out the MIWG resources.",
					testDir.exists());
		}

		new File(RPT_DIR).mkdirs();
	}

	/**
	 * 
	 * @param tool
	 *            The name of the tool folder
	 * @param testCategory
	 *            The name test category folder e.g.
	 *            "A - Fixed Digrams with Variations of Attributes"
	 * @param result
	 *            The test result xml structure
	 * @throws IOException
	 */
	private void reportTestResult(String tool, String testCategory,
			String result) throws IOException {
		File f = new File(RPT_DIR, getClass().getName() + "-" + tool + "-"
				+ variant + ".html");
		FileUtils.writeStringToFile(f, result);
	}

	private File getReferenceFolder() {
		return new File(new File(baseDir, testCategory.toString()),
				REFERENCE_FOLDER_NAME);
	}

	private File getToolFolder() {
		return new File(new File(baseDir, testCategory.toString()), tool);
	}

	@Test
	public void testXmlCompare() throws ParserConfigurationException,
			SAXException, IOException {

		String result = TestRunner.runXmlCompareTest(getReferenceFolder()
				.getAbsolutePath(), getToolFolder().getAbsolutePath(), variant);
		reportTestResult(this.tool, this.testCategory.toString(), result);
	}

}
