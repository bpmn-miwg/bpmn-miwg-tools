package org.omg.bpmn.miwg.xpath.checks;

import org.omg.bpmn.miwg.xpath.common.AbstractXpathCheck;
import org.omg.bpmn.miwg.xpath.common.Direction;
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

				selectElementNoID("bpmn:extensionElements");
				{

					navigateElement("signavio:signavioMetaData", "metaKey",
							"bgcolor");
					checkAttributeValue("metaValue", "#ffffff");

					navigateElement("signavio:signavioMetaData", "metaKey",
							"rolle");
					checkAttributeValue("metaValue", "[]");

					navigateElement("signavio:signavioMetaData", "metaKey",
							"userstory");
					checkAttributeValue("metaValue", "[]");

					selectElementNoID("w4graph:graphStyle");
					{

						navigateElement("w4graph:basic", "background",
								"192,192,192");
						checkAttributeValue("foreground", "0,0,0");
						checkAttributeValue("autoResize", "false");
						checkAttributeValue("borderColor", "100,100,100");
						checkAttributeValue("collapsed", "false");
					}
					pop();

				}
				pop();

				selectReferencedProcess();
				{
					checkAttributeValue("isExecutable", "false");

					navigateElement("bpmn:startEvent", "Invoice received");
					checkMessageEvent();

					navigateFollowingElement("bpmn:task",
							"Scan camunda-invoice-en");
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

					navigateElement(n1);

					navigateFollowingElement("bpmn:intermediateCatchEvent",
							"Invoice review needed");
					checkMessageEvent();

					navigateElement("bpmn:task", "Review and document result");

					navigateElement("bpmn:endEvent", null);

					selectElementNoID("bpmn:extensionElements");
					{

						selectElementNoID("w4graph:graphStyle");
						{

							navigateElement("w4graph:basic", "autoResize",
									"false");
							checkAttributeValue("foreground", "0,0,0");
							checkAttributeValue("background", "255,255,255");
							checkAttributeValue("autoResize", "false");
							checkAttributeValue("borderColor", "100,100,100");
							checkAttributeValue("collapsed", "false");
						}
						pop();

					}
					pop();

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
						"Scan camunda-invoice-en");

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
							"Rechnung klären", "no");
					// checkMessageFlow("", Direction.Output,
					// "bpmn:intermediateCatchEvent", "Invoice review needed");
					// checkMessageFlow("", Direction.Input, "bpmn:task",
					// "Review and document result");

					Node n2 = navigateFollowingElement("bpmn:exclusiveGateway",
							"Review successful?");

					navigateFollowingElement("bpmn:endEvent",
							"Invoice not processed", "no");

					navigateElement(n2);

					navigateFollowingElement("bpmn:userTask",
							"Approve Invoice", "yes");

				}

				{
					navigateElement(n1);

					navigateFollowingElement("bpmn:userTask",
							"Prepare Bank Transfer", "yes");

					navigateFollowingElement("bpmn:serviceTask",
							"Archive Invoice");

					checkAttributeValue("camunda:delegateExpression",
							"#{archiveService}");

					selectElementNoID("bpmn:extensionElements");
					{
						//navigateElement("field", "name", "text0");

					}
					pop();

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
