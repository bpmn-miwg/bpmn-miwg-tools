package org.omg.bpmn.miwg.HtmlOutput.Pojos;

import java.util.LinkedList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name="span")
public class DetailedOutput {

	@Attribute(name = "class", required = true)
	private String clazz = DetailedOutput.class.getSimpleName().toLowerCase();
	@Element(name = "p", required = false)
	private String description;
	@ElementList(inline = true)
	private List<Detail> details;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void addDetail(Detail detail) {
		this.getDetails().add(detail);
	}

	private List<Detail> getDetails() {
		if (details == null) {
			details = new LinkedList<Detail>();
		}

		return details;
	}

}
