package org.omg.bpmn.miwg.xpath.util;

import org.omg.bpmn.miwg.api.AnalysisJob;
import org.omg.bpmn.miwg.api.AnalysisOutput;
import org.w3c.dom.Document;

public interface DOMCheck {

	String getName();

	void execute(Document actualBpmnXml, Document referenceBpmnXMl,
			AnalysisJob job, AnalysisOutput output) throws Throwable;

}
