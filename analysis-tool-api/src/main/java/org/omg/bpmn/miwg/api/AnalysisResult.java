package org.omg.bpmn.miwg.api;

import java.util.Collection;

import org.omg.bpmn.miwg.testresult.Output;


/***
 * 
 * Represents the findings of a check run.
 * 
 * Stores the number of successful assertions (numOK), findings (numFindings), and analysis output.
 * 
 * @author mk
 *
 */
public class AnalysisResult {
	
	public int numOK;
	
	public int numFindings;
	
	public Collection<? extends Output> output;
	
	public AnalysisResult(int numOK, int numFindings, Collection<? extends Output> output) {
		this.numOK = numOK;
		this.numFindings = numFindings;
		this.output = output;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("OK      : ");
		sb.append(numOK);
		sb.append("\n");
		sb.append("Findings: ");
		sb.append(numFindings);
		sb.append("\n");
		return sb.toString();
	}

}
