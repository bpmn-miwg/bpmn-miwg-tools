package org.omg.bpmn.miwg.HtmlOutput.Pojos;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

@Root(name = "span")
public class Detail {

	@Attribute(name = "class")
	private String type;
	@Attribute(name = "data-xpath", required = false)
	private String xpath;
	@Text
	private String message;

	public Detail() {
		super();
	}

	public Detail(String type, String xpath, String message) {
		this.setType(type);
		this.setXpath(xpath);
		this.setMessage(message);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getXpath() {
		return xpath;
	}

	public void setXpath(String xpath) {
		this.xpath = xpath;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
