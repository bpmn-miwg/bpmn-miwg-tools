package org.omg.bpmn.miwg.submission;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.junit.Test;
import org.omg.bpmn.miwg.submission.RepoScanner;


public class RepoScannerTest {

  @Test
  public void testRepoScanner() throws IOException {
    JSONObject submissions = new RepoScanner().getSubmissionsFromRepo("bpmn-miwg/bpmn-miwg-test-suite");
    System.out.println(submissions);
    FileUtils.writeStringToFile(new File("target/submissions.json"), submissions.toJSONString());
  }

}
