package org.omg.bpmn.miwg.xpath.checks;

import org.omg.bpmn.miwg.xpath.common.AbstractXpathCheck;
import org.w3c.dom.Node;

public class A_3_0_Check extends AbstractXpathCheck {

	@Override
	public String getName() {
		return "A.3.0";
	}

	@Override
	public void doExecute() throws Throwable {

		{

			selectFirstProcess();

			navigateElement("bpmn:startEvent", "Start Event");

			navigateFollowingElement("bpmn:task", "Task 1");

			Node n1 = navigateFollowingElement("bpmn:subProcess",
					"Collapsed Sub-Process");

			navigateBoundaryEvent("Boundary Intermediate Event Non-Interrupting Message");
			checkMessageEvent();
			checkCancelActivity(false);

			navigateFollowingElement("bpmn:task", "Task 3");

			navigateFollowingElement("bpmn:endEvent", "End Event 1");

			navigateElement(n1);

			navigateFollowingElement("bpmn:task", "Task 2");

			navigateFollowingElement("bpmn:endEvent", "End Event 1");

			navigateElement(n1);

			navigateBoundaryEvent("Boundary Intermediate Event Interrupting Escalation");
			checkEscalationEvent();
			checkCancelActivity(true);

			navigateFollowingElement("bpmn:task", "Task 4");

			navigateFollowingElement("bpmn:endEvent", "End Event 2");

			pop();

		}
	}

}
