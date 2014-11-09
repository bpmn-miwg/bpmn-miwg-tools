package org.omg.bpmn.miwg.common;

import org.omg.bpmn.miwg.api.AnalysisResult;
import org.w3c.dom.Document;

public interface DOMCheck {

	void init(CheckOutput out);

	String getName();

    AnalysisResult execute2(Document actualBpmnXml) throws Throwable;

}
