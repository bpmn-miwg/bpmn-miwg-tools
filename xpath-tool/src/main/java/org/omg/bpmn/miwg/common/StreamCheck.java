package org.omg.bpmn.miwg.common;

import java.io.InputStream;

import org.omg.bpmn.miwg.api.AnalysisResult;

public interface StreamCheck {

	void init(CheckOutput out);

	String getName();

    AnalysisResult execute(InputStream actualBpmnXml) throws Throwable;
    
}
