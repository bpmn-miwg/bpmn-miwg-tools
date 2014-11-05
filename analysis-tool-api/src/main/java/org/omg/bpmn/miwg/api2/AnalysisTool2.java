package org.omg.bpmn.miwg.api2;

import java.io.File;

import org.omg.bpmn.miwg.api.AnalysisResult;
import org.w3c.dom.Document;

public interface AnalysisTool2 {

    AnalysisResult runAnalysis2(AnalysisJob2 job, Document referenceDocument, 
            Document actualDocument, File logDir) throws Exception; 
}
