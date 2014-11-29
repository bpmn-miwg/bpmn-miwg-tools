package org.omg.bpmn.miwg.HtmlOutput.Pojos;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "div")
public class Test {
	/* Fields */

	@Attribute(name = "class", required = true)
	private String clazz = Test.class.getSimpleName().toLowerCase();
	// + "well span12";

	@Attribute(name = "data-test", required = true)
	private String testName;

    @Attribute(name = "data-variant", required = true)
    private String testVariant;

	@Element(name = "h3", required = true)
	private String name;

	@ElementList(inline = true, required = false, empty = true)
	private List<Output> output;

	/* Constructors */

	public Test() {
	}

	public Test(String name, String variant) {
		this.name = name;
		// TODO is it possible to set both h3 and data-test with a single field?
		this.testName = name;
        this.testVariant = variant;
	}

	/**
	 * Constructor setting the name property (e.g. Signavio Process Editor 7.0)
	 * and adds a collection of output elements
	 * 
	 * @param name
	 */
	public Test(String name, Collection<Output> outputs) {
		this.setName(name);
		this.addOutput(outputs);
	}

	/* Business methods */

	/**
	 * Adds an output element.
	 * 
	 * @param output
	 */
	public Output addOutput(Output output) {
		Output o = output;
		if(this.getOutputs().contains(output)) {
			o = this.getOutputs().get(this.getOutputs().indexOf(o));
		} else {
			this.getOutputs().add(o);
		}
		
		return o;
	}
	
	public Output addOutput(OutputType outputType, String description) {
		Output o = new Output(outputType, description);
		return this.addOutput(o);
	}

	/**
	 * Adds all entries in outputs.
	 * 
	 * @param output
	 */
	public void addOutput(Collection<Output> output) {
		this.getOutputs().addAll(output);
	}

	/**
	 * Returns a shadow copy of all output.
	 * 
	 * @return {@link List} of {@link Output}
	 */
	public List<Output> getOutputCopy() {
		List<Output> i = new LinkedList<Output>();
		i.addAll(getOutputs());
		return i;
	}

	public boolean equals(Object obj) {
		return (obj instanceof Test)
				&& ((Test) obj).getName().equalsIgnoreCase(this.getName());
	}

	/* Getters & Setters */

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private List<Output> getOutputs() {
		if (this.output == null) {
			this.output = new LinkedList<Output>();
		}
		return this.output;
	}

    public void addAll(Collection<? extends Output> significantDifferences) {
        getOutputs().addAll(significantDifferences);
    }
}
