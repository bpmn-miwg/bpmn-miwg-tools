package org.omg.bpmn.miwg.common;

import org.omg.bpmn.miwg.api.AnalysisResult;
import org.omg.bpmn.miwg.api.tools.AnalysisTool;
import org.w3c.dom.Document;

public interface DOMCheck {

	void init(CheckOutput out);

	String getName();

    AnalysisResult execute(Document actualBpmnXml, AnalysisTool analysisTool) throws Throwable;

}
