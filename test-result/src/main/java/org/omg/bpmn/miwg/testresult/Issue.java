package org.omg.bpmn.miwg.testresult;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

public class Issue {

	/* Fields */

	@Attribute(name = "class", required = true)
	private IssueType issueType;
	@Element(name = "p")
	private String description;
	@ElementList(inline = true, required = false)
	private List<Issue> subissues;
	
	/* Constructors */
	
	public Issue() {}
	
	public Issue(IssueType issueType, String description) {
		this.setIssueType(issueType);
		this.setDescription(description);
	}

	/* Business methods */
	public IssueType getIssueType() {
		return issueType;
	}

	public void setIssueType(IssueType issueType) {
		this.issueType = issueType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Adds an issue element.
	 * 
	 * @param issue
	 */
	public void addSubissue(Issue issue) {
		this.getSubissuses().add(issue);
	}

	/**
	 * Adds all entries in issues.
	 * 
	 * @param issues
	 */
	public void addSubissues(Collection<Issue> issues) {
		this.getSubissuses().addAll(issues);
	}
	
	/**
	 * Returns a shadow copy of all subissues.
	 * 
	 * @return {@link List} of {@link Issue}
	 */
	public List<Issue> getSubissuesCopy() {
		List<Issue> i = new LinkedList<Issue>();
		i.addAll(getSubissuses());
		return i;
	}

	/* Getter & Setter */
	private List<Issue> getSubissuses() {
		if (this.subissues == null) {
			this.subissues = new LinkedList<Issue>();
		}
		return this.subissues;
	}
}
