package org.omg.bpmn.miwg.api;

import java.io.File;

import org.w3c.dom.Document;

public interface DOMAnalysisTool {

	String getName();
	
    AnalysisResult analyzeDOM(AnalysisJob job, Document referenceDocument, 
            Document actualDocument, File logDir) throws Exception; 
}
