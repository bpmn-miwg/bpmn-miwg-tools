package org.omg.bpmn.miwg.xpath.checks;

import org.omg.bpmn.miwg.xpath.common.AbstractXpathCheck;
import org.w3c.dom.Node;

public class C_1_1_Check extends AbstractXpathCheck {

	@Override
	public String getName() {
		return "C.1.1";
	}




	@Override
	public void doExecute() throws Throwable {
		navigateElementX("/bpmn:definitions");
		checkAttributeValue("expressionLanguage", "http://www.w3.org/1999/XPath");
		

		navigateElement("bpmn:resource", "Team Assistant");
		checkAttributeValue("w4:type", "User");

		navigateElement("bpmn:resource", "Approver");
		checkAttributeValue("w4:type", "User");

		navigateElement("bpmn:resource", "Accountant");
		checkAttributeValue("w4:type", "User");

		selectElement("bpmn:process", "Invoice Handling (OMG BPMN MIWG Demo)");
		{
			checkAttributeValue("isExecutable", "true");
			checkExtensionElements();

			navigateElement("bpmn:startEvent", "Invoice received");
			checkExtensionElements();

			navigateFollowingElement("bpmn:userTask", "Assign Approver");
			checkExtensionElements();

			navigateFollowingElement("bpmn:userTask", "Approve Invoice");
			checkExtensionElements();

			Node n1 = navigateFollowingElement("bpmn:exclusiveGateway",
					"Invoice approved?");
			checkExtensionElements();
			

			{
				navigateFollowingElement("bpmn:userTask", "Rechnung klären",
						"no");
				checkExtensionElements();

				Node n2 = navigateFollowingElement("bpmn:exclusiveGateway",
						"Review successful?");
				checkExtensionElements();

				navigateFollowingElement("bpmn:userTask", "Approve Invoice",
						"yes");
				checkExtensionElements();

				navigateElement(n2);
				checkExtensionElements();

				navigateFollowingElement("bpmn:endEvent",
						"Invoice not processed", "no");
				checkExtensionElements();

			}

			navigateElement(n1);

			{
				navigateFollowingElement("bpmn:userTask",
						"Prepare Bank Transfer", "yes");
				checkExtensionElements();

				navigateFollowingElement("bpmn:serviceTask", "Archive Invoice");
				checkNonBPMNAttributes();
				checkExtensionElements();

				navigateFollowingElement("bpmn:endEvent", "Invoice processed");
				checkExtensionElements();

			}
		}
		pop();
	}

}
