package org.omg.bpmn.miwg.xpath.checks;

import org.omg.bpmn.miwg.xpath.common.AbstractXpathCheck;
import org.w3c.dom.Node;

public class C_3_0_Check extends AbstractXpathCheck {

	@Override
	public String getName() {
		return "C.3.0";
	}

	@Override
	public void doExecute() throws Throwable {
		navigateElement("bpmn:resource", "User");
		String resourceID = checkAttributeValue("id");

		selectFirstProcess();
		checkAttributeValue("isExecutable", "true");
		
		
		navigateElement("bpmn:startEvent", "Receive customer request");
		checkMessageEvent(true, "Service Level");

		Node taskAnalyseCustomerRequest = navigateFollowingElement("bpmn:userTask", "Analyse customer request");
		checkAttributeValue("startQuantity", "2");
		checkAttributeValue("completionQuantity", "2");
		
		navigateChildElement("bpmn:potentialOwner", "Potential Owner");
		checkChildElementValue("bpmn:resourceRef", null, resourceID);

		
		navigateElement(taskAnalyseCustomerRequest);
		Node gateway = navigateFollowingElement("bpmn:exclusiveGateway",
				"Service type");

		navigateFollowingElement("bpmn:userTask", "Replace fridge", "Warranty");
		navigateFollowingElement("bpmn:endEvent", "Fridge replaced");

		navigateElement(gateway);
		navigateFollowingElement("bpmn:subProcess", "Perform emergency repair",
				"Emergency service");
		Node gateway2 = navigateFollowingElement("bpmn:exclusiveGateway",
				"Successful?");
		navigateFollowingElement("bpmn:endEvent", "Emergency repair completed",
				"yes");

		navigateElement(gateway2);
		navigateFollowingElement("bpmn:userTask", "Replace fridge", "no");

		navigateElement(gateway);

		Node gateway3 = navigateFollowingElement("bpmn:exclusiveGateway",
				"Service level", "Regular repair service");

		Node task2 = navigateFollowingElement("bpmn:userTask",
				"Perform repair (premium level)", "Premium");
		
		navigateBoundaryEvent("2 hours");
		checkTimerEvent();
		navigateFollowingElement("bpmn:subProcess", "Perform emergency repair");
		
		navigateElement(task2);
		navigateFollowingElement("bpmn:endEvent", "Repair completed");
		
		navigateElement(gateway3);
		Node task3 = 
				navigateFollowingElement("bpmn:userTask", "Perform repair (standard level)", "Standard");
		navigateBoundaryEvent("");
		checkMessageEvent(true, "Service Level");
		
		navigateFollowingElement("bpmn:userTask", "Perform repair (premium level)");
		
		navigateElement(task3);
		navigateFollowingElement("bpmn:endEvent", "Repair completed");

		pop();

	}

}
