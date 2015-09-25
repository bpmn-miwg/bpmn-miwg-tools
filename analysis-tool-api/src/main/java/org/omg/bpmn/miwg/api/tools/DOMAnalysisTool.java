package org.omg.bpmn.miwg.api.tools;

import org.omg.bpmn.miwg.api.AnalysisJob;
import org.omg.bpmn.miwg.api.AnalysisOutput;
import org.w3c.dom.Document;

public interface DOMAnalysisTool extends AnalysisTool {

	String getName();

	AnalysisOutput analyzeDOM(AnalysisJob job, Document referenceDocument,
			Document actualDocument) throws Exception;
}
