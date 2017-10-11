package org.omg.bpmn.miwg.submission;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

public class RepoScanner {

  protected static final Logger LOGGER = Logger.getLogger(RepoScanner.class.getName());

  List<String> testCases;

  private String githubUser;

  private String githubToken;

    public RepoScanner() {
        githubUser = System.getProperty("githubUser");
        githubToken = System.getProperty("githubToken");

        if (githubUser == null || githubToken == null) {
            String msg = ("To scan GitHub for submissions please provide your username and a valid Personal Access Token.\n"
                    + "Without this the submissions table will be out of date.\n"
                    + "JVM parameters are githubUser and githubToken");
            throw new IllegalStateException(msg);
        }
    }

	public JSONObject getSubmissionsFromRepo(String repoName) {
    LOGGER.info("Scanning Repo '" + repoName + "'.\n\n This might take some time!\n\n");
		JSONObject submissions;
    try {
            GitHub github = GitHub.connect(githubUser, githubToken);
			GHRepository repository = github.getRepository(repoName);
			testCases = getReferences(repository);
			submissions = getSubmissionsFromRootDir(repository);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		LOGGER.info("done with scanning Repo '" + repoName + "'.");
		return submissions;
	}

  private List<String> getReferences(GHRepository repository) throws IOException {
    ArrayList<String> references = new ArrayList<String>();
    List<GHContent> directoryContent = repository.getDirectoryContent("Reference");
    for (GHContent content : directoryContent) {
      String name = content.getName();
      String type = content.getType();
      if ("file".equals(type) && name.matches("[A-C]\\.[1-9][0-9]*\\.[0-9]+.bpmn")) {
        references.add(name.replace(".bpmn", ""));
      }
    }
    LOGGER.info("References: " + references);
    return references;
  }

  private JSONObject getSubmissionsFromRootDir(GHRepository repository) throws IOException {
    LOGGER.info("Scanning: " + repository.getName());
    JSONObject submissions = new JSONObject();
    List<GHContent> directoryContent = repository.getDirectoryContent("/");
    for (GHContent content : directoryContent) {
      String name = content.getName();
      String type = content.getType();
      if ("dir".equals(type) && !"Reference".equals(name) && !"Work in Progress".equals(name)) {
        getSubmissionsFromToolDir(submissions, repository, content.getPath());
      }
    }
    return submissions;
  }

  @SuppressWarnings("unchecked")
  private void getSubmissionsFromToolDir(JSONObject submissions, GHRepository repository,
      String tool) throws IOException {
    LOGGER.info("Scanning: " + repository.getName() + "/" + tool);
    int count = 0;
    int countA = 0;
    int countB = 0;
    int countC = 0;
    JSONArray files = new JSONArray();
    List<GHContent> directoryContent = repository.getDirectoryContent(tool.replace(" ", "%20"));
    for (GHContent content : directoryContent) {
      String name = content.getName();
      String type = content.getType();
      for (String testCase : testCases) {
        if ("file".equals(type)) {
          if (name.matches(testCase + "-(import.png|roundtrip.bpmn|export.png|export.bpmn)")) {
            count++;
            if (name.startsWith("A")) countA++;
            if (name.startsWith("B")) countB++;
            if (name.startsWith("C")) countC++;
            files.add(name);
            break;
          } else if (name.startsWith(testCase)) {
            LOGGER.warning("[" + tool + "] File '" + name + "' does not match the naming conventions.");
          }
        }
      }
    }
    JSONObject submission = new JSONObject();
    submission.put("count", count);
    submission.put("countA", countA);
    submission.put("countB", countB);
    submission.put("countC", countC);
    submission.put("files", files);
    submissions.put(tool, submission);
  }

}
