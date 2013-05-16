package org.omg.bpmn.miwg.xpathTestRunner.base;

import java.io.*;

public class TestOutput {

	private PrintWriter fileWriter;

	public TestOutput(TestInfo info, String outputFolder) throws IOException {
		fileWriter = new PrintWriter(new BufferedWriter(new FileWriter(
				new File(outputFolder, info.getApplication() + "-"
						+ info.getTest() + ".txt"))));
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
