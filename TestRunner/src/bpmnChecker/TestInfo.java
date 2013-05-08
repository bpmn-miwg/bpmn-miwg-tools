package bpmnChecker;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class TestInfo {

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

	public TestInfo() {
	}

	public static List<TestInfo> findTestFiles(String rootDir)
			throws IOException {
		System.out.println("Test files in " + rootDir);

		File root = new File(rootDir);
		File[] applicationFolders = root.listFiles(new FileFilter() {

			@Override
			public boolean accept(File f) {
				return f.isDirectory();
			}

		});

		List<TestInfo> allTestFiles = new LinkedList<TestInfo>();

		for (File applicationFolder : applicationFolders) {
			File[] testFiles = applicationFolder
					.listFiles(new FilenameFilter() {

						@Override
						public boolean accept(File folder, String name) {
							return name.equals("B.1.0-export.bpmn")
									|| name.equals("B.1.0-roundtrip.bpmn")
											|| name.equals("B.1.0.bpmn");
						}

					});

			for (File f : testFiles) {
				TestInfo tfi = new TestInfo();
				tfi.root = f.getParentFile().getParentFile().getCanonicalPath();
				tfi.application = f.getParentFile().getName();
				tfi.testFile = f.getName();

				allTestFiles.add(tfi);
			}
		}

		for (TestInfo testFile : allTestFiles) {
			System.out.println(" - " + testFile);
		}
		System.out.println();
		return allTestFiles;
	}

	public File getFile() {
		File f1 = new File(root, application);
		File f2 = new File(f1, testFile);
		return f2;
	}
	
	public void printTestFileInfo(TestOutput out) {
		out.println("Root       : " + root);
		out.println("Application: " + application);
		out.println("File       : " + testFile);
	}

	@Override
	public String toString() {
		try {
			return getFile().getCanonicalPath();
		} catch (IOException e) {
			return null;
		}
	}

}
