package org.omg.bpmn.miwg.xpathTestRunner.base;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.omg.bpmn.miwg.xpathTestRunner.testBase.Test;

public class TestOutput {

	private PrintWriter fileWriter;

	public TestOutput(TestInfo info, String outputFolder) throws IOException {
		File file = new File(outputFolder, info.getApplication() + "-"
				+ info.getTest() + ".txt");
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		fileWriter = new PrintWriter(new BufferedWriter(new FileWriter(
				file)));
	}

	public TestOutput(Test test, TestInfo info, String outputFolder)
			throws IOException {
		File file = new File(outputFolder, test.getName() + "-"
				+ info.getApplication() + "-"
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
