package org.omg.bpmn.miwg.xpath.checks;

import org.omg.bpmn.miwg.xpath.util.AbstractXpathCheck;
import org.omg.bpmn.miwg.xpath.util.Direction;
import org.w3c.dom.Node;


public class C_1_0_Check extends AbstractXpathCheck {

	@Override
	public String getName() {
		return "C.1.0";
	}

	@Override
	public void doExecute() throws Throwable {

		navigateElement("bpmn:resource", "Team Assistant");
		checkAttributeValue("w4:type", "User");

		navigateElement("bpmn:resource", "Approver");
		checkAttributeValue("w4:type", "User");

		navigateElement("bpmn:resource", "Accountant");
		checkAttributeValue("w4:type", "User");

		selectCollaboration();
		{

			selectPool("Team-Assistant");
			{

				selectReferencedProcess();
				{
					checkAttributeValue("isExecutable", "false");

					navigateElement("bpmn:startEvent", "Invoice received");
					checkMessageEvent();

					navigateFollowingElement("bpmn:task", "Scan Invoice");
					checkMessageFlow("", Direction.Output, "bpmn:startEvent",
							"Invoice received");

					navigateFollowingElement("bpmn:task", "Archive original");

					navigateFollowingElement("bpmn:intermediateCatchEvent",
							"Approver to be assigned");
					// checkMessageFlow("", Direction.Input, "bpmn:userTask",
					// "Assign Approver");

					navigateFollowingElement("bpmn:task", "Assign approver");
					// checkMessageFlow("", Direction.Output, "bpmn:userTask",
					// "Assign Approver");

					Node n1 = navigateFollowingElement(
							"bpmn:eventBasedGateway", "");

					navigateFollowingElement("bpmn:intermediateCatchEvent",
							"7 days");
					checkTimerEvent();

					navigateFollowingElement("bpmn:endEvent", null);

					navigateBookmarkedElement(n1);

					navigateFollowingElement("bpmn:intermediateCatchEvent",
							"Invoice review needed");
					checkMessageEvent();

					navigateElement("bpmn:task", "Review and document result");

					navigateElement("bpmn:endEvent", null);

				}
				pop();

			}
			pop();

			selectPool("Process Engine - Invoice Receipt");
			selectReferencedProcess();
			{

				checkAttributeValue("isExecutable", "true");

				navigateLane("Team Assistant");
				navigateLane("Approver");
				navigateLane("Accountant");

				navigateElement("bpmn:startEvent", "Invoice received");
				checkMessageEvent();
				checkMessageFlow("", Direction.Input, "bpmn:task",
						"Scan Invoice");

				navigateFollowingElement("bpmn:userTask", "Assign Approver");
				// checkMessageFlow("", Direction.Output,
				// "bpmn:intermediateCatchEvent", "Approver to be assigned");
				// checkMessageFlow("", Direction.Input, "bpmn:task",
				// "Assign approver");

				navigateFollowingElement("bpmn:userTask", "Approve Invoice");
				checkAttributeValue("camunda:assignee", "${approver}");
				checkAttributeValue("camunda:formKey", "app:approveInvoice.jsf");

				Node n1 = navigateFollowingElement("bpmn:exclusiveGateway",
						"Invoice approved?");

				{
					navigateFollowingElement("bpmn:userTask",
							"Rechnung kl\u00e4ren", "no");
					// checkMessageFlow("", Direction.Output,
					// "bpmn:intermediateCatchEvent", "Invoice review needed");
					// checkMessageFlow("", Direction.Input, "bpmn:task",
					// "Review and document result");

					Node n2 = navigateFollowingElement("bpmn:exclusiveGateway",
							"Review successful?");

					navigateFollowingElement("bpmn:endEvent",
							"Invoice not processed", "no");

					navigateBookmarkedElement(n2);

					navigateFollowingElement("bpmn:userTask",
							"Approve Invoice", "yes");

				}

				{
					navigateBookmarkedElement(n1);

					navigateFollowingElement("bpmn:userTask",
							"Prepare Bank Transfer", "yes");

					navigateFollowingElement("bpmn:serviceTask",
							"Archive Invoice");

					checkAttributeValue("camunda:delegateExpression",
							"#{archiveService}");

					navigateFollowingElement("bpmn:endEvent",
							"Invoice processed");

				}

			}
			pop();
			pop();

		}
		pop();
	}

}
