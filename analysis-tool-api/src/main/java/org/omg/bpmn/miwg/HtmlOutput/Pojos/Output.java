package org.omg.bpmn.miwg.HtmlOutput.Pojos;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.omg.bpmn.miwg.output.DetailedOutput;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "div")
public class Output {

	/* Fields */

	@Attribute(name = "class", required = true)
	private OutputType outputType;
	@Element(name = "h4", required = false)
	private String description;
	@ElementList(inline = true, required = false)
	private List<Output> suboutputs;
	@ElementList(inline = true, required = false, empty=true)
	private List<DetailedOutput> details;

	/* Constructors */

	public Output() {
	}

	public Output(OutputType outputType, String description) {
		this.setOutputType(outputType);
		this.setDescription(description);
	}

	public Output(OutputType outputType, DetailedOutput detail) {
		this.setOutputType(outputType);
		this.addDetail(detail);
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

	public boolean equals(Object obj) {
		return (obj instanceof Output) && ((Output) obj).getDescription() != null
				&& ((Output) obj).getDescription().equals(this.getDescription());
	}

	/* Getter & Setter */
	private List<Output> getSuboutputs() {
		if (this.suboutputs == null) {
			this.suboutputs = new LinkedList<Output>();
		}
		return this.suboutputs;
	}

	private List<DetailedOutput> getDetail() {
		if (this.details == null) {
			this.details = new LinkedList<DetailedOutput>();
		}
		return this.details;
	}

	public void addDetail(DetailedOutput detailedOutput) {
		this.getDetail().add(detailedOutput);
	}
}
