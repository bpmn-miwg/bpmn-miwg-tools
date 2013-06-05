package org.omg.bpmn.miwg.testresult;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name="div")
public class Test {
	/* Fields */
	
	@Attribute(name = "class", required = true)
	private String clazz = Test.class.getSimpleName().toLowerCase();
	
	@Element(name = "h3", required = true)
	private String name;

	@ElementList(inline = true, required = true)
	private List<Issue> issues;

	/* Constructors */

	public Test() {
	}

	/**
	 * Constructor setting the name property (e.g. Signavio Process Editor 7.0)
	 * and adds a collection of issue elements
	 * 
	 * @param name
	 */
	public Test(String name, Collection<Issue> issues) {
		this.setName(name);
		this.addIssues(issues);
	}

	/* Business methods */

	/**
	 * Adds an issue element.
	 * 
	 * @param issue
	 */
	public void addIssue(Issue issue) {
		this.getIssues().add(issue);
	}

	/**
	 * Adds all entries in issues.
	 * 
	 * @param issues
	 */
	public void addIssues(Collection<Issue> issues) {
		this.getIssues().addAll(issues);
	}
	
	/**
	 * Returns a shadow copy of all issues.
	 * 
	 * @return {@link List} of {@link Issue}
	 */
	public List<Issue> getIssuesCopy() {
		List<Issue> i = new LinkedList<Issue>();
		i.addAll(getIssues());
		return i;
	}

	/* Getters & Setters */

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private List<Issue> getIssues() {
		if (this.issues == null) {
			this.issues = new LinkedList<Issue>();
		}
		return this.issues;
	}
}
