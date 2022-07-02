package org.omg.bpmn.miwg.api.input;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileAnalysisInput implements AnalysisInput {

	private File file;

	public FileAnalysisInput(File file) {
		this.file = file;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new FileInputStream(file);
	}

}
