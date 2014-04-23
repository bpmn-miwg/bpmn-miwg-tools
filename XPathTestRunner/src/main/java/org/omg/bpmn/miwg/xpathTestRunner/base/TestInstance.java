package org.omg.bpmn.miwg.xpathTestRunner.base;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.omg.bpmn.miwg.input.DirFilter;
import org.omg.bpmn.miwg.xpathTestRunner.base.testEntries.KeyValueEntry;
import org.omg.bpmn.miwg.xpathTestRunner.testBase.Test;

public class TestInstance {

	private String testFile;

	public String getTest() {
		return testFile;
	}

	public String getApplication() {
		return application;
	}

	public String getRoot() {
		return root;
	}

	private String application;
	private String root;
	private String category;

	private int findings = 0;
	private int oks = 0;

	public TestInstance() {
	}

	public TestInstance(String root, String application, String testFile) {
		this.root = root;
		this.application = application;
		this.testFile = testFile;
	}

	public static List<TestInstance> buildTestInstances(
			final TestManager testManager, String rootDir) throws IOException {
		System.out.println("Test files in " + rootDir);

		File root = new File(rootDir);

        // File[] categoryFolders = root.listFiles(new FileFilter() {
        //
        // @Override
        // public boolean accept(File f) {
        // return f.isDirectory();
        // }
        //
        // });

		List<File> applicationFolders = new LinkedList<File>();

        // for (File category : categoryFolders) {

        applicationFolders.addAll(Arrays.asList(root
.listFiles(new DirFilter())));
        // }

		List<TestInstance> allTestFiles = new LinkedList<TestInstance>();

		for (File applicationFolder : applicationFolders) {
			File[] testFiles = applicationFolder
					.listFiles(new FilenameFilter() {

						@Override
						public boolean accept(File folder, String name) {
							return testManager.isAnyTestApplicable(new File(
									folder, name));
						}

					});

			for (File f : testFiles) {
				TestInstance tfi = new TestInstance();
				tfi.root = f.getParentFile().getParentFile().getCanonicalPath();
				tfi.application = f.getParentFile().getName();
                // tfi.category = f.getParentFile().getParentFile().getName();
				tfi.testFile = f.getName();

				if (testManager.getApplication() == null
						|| testManager.getApplication().equals(tfi.application))
					allTestFiles.add(tfi);
			}
		}

		for (TestInstance testFile : allTestFiles) {
			System.out.println(" - " + testFile);
		}
		System.out.println();
		return allTestFiles;
	}

	public File getFile() {
		File f1 = new File(root, application);
		File f2 = new File(f1, testFile);
        System.out.println("test file sought: " + f2.getPath());
        System.out.println("... exists?: " + f2.exists());
		return f2;
	}

	public void printTestFileInfo(TestOutput out) {
		out.println(new KeyValueEntry("Root", root));
		out.println(new KeyValueEntry("Application", application));
		out.println(new KeyValueEntry("File", testFile));
	}

	@Override
	public String toString() {
		try {
			return getFile().getCanonicalPath();
		} catch (IOException e) {
			return null;
		}
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getFindings() {
		return findings;
	}

	public void addFindings(int findings) {
		this.findings += findings;
	}

	public int getOKs() {
		return oks;
	}

	public void addOK(int oks) {
		this.oks += oks;
	}

	public void addResults(Test test) {
		this.oks += test.resultsOK();
		this.findings += test.resultsFinding();
	}

}
