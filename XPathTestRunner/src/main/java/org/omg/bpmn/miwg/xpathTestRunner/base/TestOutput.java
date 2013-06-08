package org.omg.bpmn.miwg.xpathTestRunner.base;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class TestOutput {

	private PrintWriter fileWriter;

	public TestOutput(String name, String outputFolder) throws IOException {
		File file = new File(outputFolder, name + ".txt");
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		fileWriter = new PrintWriter(new BufferedWriter(new FileWriter(file)));
	}

	public TestOutput(TestInstance info, String outputFolder)
			throws IOException {
		File file = new File(outputFolder, info.getApplication() + "-"
				+ info.getTest() + ".txt");
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		fileWriter = new PrintWriter(new BufferedWriter(new FileWriter(file)));
	}

	public void println(String line) {
		System.out.println(line);
		fileWriter.println(line);
	}

	public void println() {
		println("");
	}

	public void close() {
		if (fileWriter != null)
			fileWriter.close();
	}
}
