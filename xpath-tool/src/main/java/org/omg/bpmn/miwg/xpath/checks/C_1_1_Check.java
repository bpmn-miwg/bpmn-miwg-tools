package org.omg.bpmn.miwg.xpath.checks;


import org.omg.bpmn.miwg.xpath.util.AbstractXpathCheck;
import org.w3c.dom.Node;

public class C_1_1_Check extends AbstractXpathCheck {

	@Override
	public String getName() {
		return "C.1.1";
	}

	@Override
	public void doExecute() throws Throwable {
		navigateDefinitions();
		checkAttributeValue("expressionLanguage",
				"http://www.w3.org/1999/XPath");

		navigateElement("bpmn:resource", "Team Assistant");
		checkAttributeValue("w4:type", "User");

		navigateElement("bpmn:resource", "Approver");
		checkAttributeValue("w4:type", "User");

		navigateElement("bpmn:resource", "Accountant");
		checkAttributeValue("w4:type", "User");

		selectElement("bpmn:process", "Invoice Handling (OMG BPMN MIWG Demo)");
		{
			checkAttributeValue("isExecutable", "true");

			navigateElement("bpmn:startEvent", "Invoice received");

			navigateFollowingElement("bpmn:userTask", "Assign Approver");

			navigateFollowingElement("bpmn:userTask", "Approve Invoice");

			Node n1 = navigateFollowingElement("bpmn:exclusiveGateway",
					"Invoice approved?");

			{
				navigateFollowingElement("bpmn:userTask", "Rechnung klären",
						"no");

				Node n2 = navigateFollowingElement("bpmn:exclusiveGateway",
						"Review successful?");

				navigateFollowingElement("bpmn:userTask", "Approve Invoice",
						"yes");

				navigateBookmarkedElement(n2);

				navigateFollowingElement("bpmn:endEvent",
						"Invoice not processed", "no");

			}

			navigateBookmarkedElement(n1);

			{
				navigateFollowingElement("bpmn:userTask",
						"Prepare Bank Transfer", "yes");

				navigateFollowingElement("bpmn:serviceTask", "Archive Invoice");

				navigateFollowingElement("bpmn:endEvent", "Invoice processed");

			}
		}
		pop();
	}

}
