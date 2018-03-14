package org.omg.bpmn.miwg.facade;

import org.omg.bpmn.miwg.api.tools.AnalysisTool;

/**
 * This is a pseudo tool which is used during the initialization of a job.
 *
 */
public class InitAnalysisTool implements AnalysisTool {

	@Override
	public String getName() {
		return "init";
	}
	
}