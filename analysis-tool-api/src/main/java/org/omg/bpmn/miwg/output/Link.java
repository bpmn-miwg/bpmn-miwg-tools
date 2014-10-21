package org.omg.bpmn.miwg.output;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

@Root(name = "a")
public class Link {

	@Attribute(name = "class")
	private String type;

	@Text
	private String text;

	public Link() {
		super();
	}

	public Link(String type, String text) {
		this.type = type;
		this.text = text;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
