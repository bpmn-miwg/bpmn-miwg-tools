package org.omg.bpmn.miwg.submission;

import static org.junit.Assume.assumeNoException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.junit.Test;

public class RepoScannerTest {

    @Test
    public void testRepoScanner() throws IOException {
        try {
            JSONObject submissions = new RepoScanner()
                    .getSubmissionsFromRepo("bpmn-miwg/bpmn-miwg-test-suite");
            System.out.println(submissions);
            FileUtils.writeStringToFile(new File("target/submissions.json"),
                    submissions.toJSONString(), Charset.forName("UTF-8"));
        } catch (Throwable t) {
            assumeNoException("Assume no GitHub token provided, continuing", t);
        }
    }

}
