package org.omg.bpmn.miwg.common;

import org.omg.bpmn.miwg.api.AnalysisResult;
import org.w3c.dom.Document;

public interface Check2 {

	void init(CheckOutput out);

	String getName();

	int resultsOK();

	int resultsFinding();

   
    AnalysisResult execute2(Document actualBpmnXml) throws Throwable;

}
