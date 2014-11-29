package org.omg.bpmn.miwg.api.input;

import java.io.IOException;
import java.io.InputStream;

public class ResourceAnalysisInput implements AnalysisInput {

	private Class<?> resourceClass;
	private String resourceName;
	
	public ResourceAnalysisInput(Class<?> resourceClass, String resourceName) {
		this.resourceClass = resourceClass;
		this.resourceName = resourceName;
	}
	
	@Override
	public InputStream getInputStream() throws IOException {
		return resourceClass.getResourceAsStream(resourceName);
	}

}
