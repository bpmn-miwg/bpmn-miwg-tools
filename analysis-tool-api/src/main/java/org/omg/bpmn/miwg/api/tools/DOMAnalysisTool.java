package org.omg.bpmn.miwg.api.tools;

import java.io.File;

import org.omg.bpmn.miwg.api.AnalysisJob;
import org.omg.bpmn.miwg.api.AnalysisResult;
import org.w3c.dom.Document;

public interface DOMAnalysisTool extends AnalysisTool {

	String getName();

	AnalysisResult analyzeDOM(AnalysisJob job, Document referenceDocument,
			Document actualDocument, File logDir) throws Exception;
}
