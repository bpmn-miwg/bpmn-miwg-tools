package org.omg.bpmn.miwg.testresult;

import java.util.Collection;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root (name="div")
public class AnalysisOutputFragment {
	
	@Attribute(name="class")
	private String className="analysisresult";
	
	@ElementList(inline=true, entry="div")
	public Collection<? extends Output> output;
	
}