package org.omg.bpmn.miwg.testresult;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name="div")
public class Output {

	/* Fields */

	@Attribute(name = "class", required = true)
	private OutputType outputType;
	@Element(name = "p")
	private String description;
	@ElementList(inline = true, required = false)
	private List<Output> suboutputs;
	
	/* Constructors */
	
	public Output() {}
	
	public Output(OutputType outputType, String description) {
		this.setOutputType(outputType);
		this.setDescription(description);
	}

	/* Business methods */
	public OutputType getOutputType() {
		return outputType;
	}

	public void setOutputType(OutputType issueType) {
		this.outputType = issueType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Adds an output element.
	 * 
	 * @param output
	 */
	public void addSubissue(Output output) {
		this.getSuboutputs().add(output);
	}

	/**
	 * Adds all entries in outputs.
	 * 
	 * @param outputs
	 */
	public void addSuboutputs(Collection<Output> outputs) {
		this.getSuboutputs().addAll(outputs);
	}
	
	/**
	 * Returns a shadow copy of all suboutputs.
	 * 
	 * @return {@link List} of {@link Output}
	 */
	public List<Output> getSuboutputsCopy() {
		List<Output> i = new LinkedList<Output>();
		i.addAll(getSuboutputs());
		return i;
	}

	/* Getter & Setter */
	private List<Output> getSuboutputs() {
		if (this.suboutputs == null) {
			this.suboutputs = new LinkedList<Output>();
		}
		return this.suboutputs;
	}
}
