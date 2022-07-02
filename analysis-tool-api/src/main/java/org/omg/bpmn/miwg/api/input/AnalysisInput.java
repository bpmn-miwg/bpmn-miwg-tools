package org.omg.bpmn.miwg.api.input;

import java.io.IOException;
import java.io.InputStream;

public interface AnalysisInput {

	public InputStream getInputStream() throws IOException;
	
}
