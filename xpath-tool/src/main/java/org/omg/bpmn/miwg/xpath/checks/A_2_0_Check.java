package org.omg.bpmn.miwg.xpath.checks;

import org.omg.bpmn.miwg.xpath.util.AbstractXpathCheck;
import org.w3c.dom.Node;

public class A_2_0_Check extends AbstractXpathCheck {

	@Override
	public String getName() {
		return "A.2.0";
	}

	@Override
	public void doExecute() throws Throwable {

		{
			selectFirstProcess();

			navigateElement("bpmn:startEvent", "Start Event");

			navigateFollowingElement("bpmn:task", "Task 1");

			Node n1 = navigateFollowingElement("bpmn:exclusiveGateway",
					"Gateway (Split Flow)");

			navigateFollowingElement("bpmn:task", "Task 2");

			navigateBookmarkedElement(n1);

			Node n2 = navigateFollowingElement("bpmn:task", "Task 3");

			navigateBookmarkedElement(n1);

			navigateFollowingElement("bpmn:task", "Task 4");

			navigateFollowingElement("bpmn:exclusiveGateway",
					"Gateway (Merge Flows)");

			navigateBookmarkedElement(n2);

			navigateFollowingElement("bpmn:exclusiveGateway",
					"Gateway (Merge Flows)");

			navigateFollowingElement("bpmn:endEvent", "End Event");

			pop();

		}
	}

}
