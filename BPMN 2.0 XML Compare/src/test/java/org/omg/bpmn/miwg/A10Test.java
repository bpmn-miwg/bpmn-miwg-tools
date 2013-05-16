package org.omg.bpmn.miwg;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.omg.bpmn.miwg.TestRunner;


public class A10Test {

    private static final String RESOURCE_BASE_DIR = "../../";

    private static final String A10_DIR = "bpmn-miwg-test-suite/A - Fixed Digrams with Variations of Attributes/";
    
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testA10() {
        File baseDir = new File(RESOURCE_BASE_DIR);
        System.out.println("Looking for MIWG resources in: "
                + baseDir.getAbsolutePath());

        File testDir = new File(baseDir, A10_DIR);
        assertTrue("Dir does not exist at " + testDir.getAbsolutePath()
                + ", please ensure you have checked out the MIWG resources.",
                testDir.exists());
        for (String dir : testDir.list()) {
            if (!dir.equals("Reference") && !dir.startsWith(".")) {
                try {
                    TestRunner.main(new String[] { getDir("Reference"),
                            getDir(dir) });
                } catch (FileNotFoundException e) {
                    // Seems like this is not a failed test but just a file that
                    // has not been provided.
                    // TODO We'll need to revisit this once we get the results
                    // into an output file.
                    System.err.println(e.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                    fail(e.getClass().getName() + ": " + e.getMessage());
                }
            }
        }

    }

    private String getDir(String dir) {
        return RESOURCE_BASE_DIR + A10_DIR + dir;
    }

}
