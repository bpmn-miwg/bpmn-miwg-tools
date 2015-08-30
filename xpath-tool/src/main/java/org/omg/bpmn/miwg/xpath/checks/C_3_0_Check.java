package org.omg.bpmn.miwg.xpath.checks;

import org.omg.bpmn.miwg.xpath.common.AbstractXpathCheck;
import org.omg.bpmn.miwg.xpath.common.Direction;
import org.w3c.dom.Node;

public class C_3_0_Check extends AbstractXpathCheck {

	@Override
	public String getName() {
		return "C.3.0";
	}

	@Override
	public void doExecute() throws Throwable {
		selectFirstProcess();

		navigateElement("bpmn:startEvent", "Receive customer request");
		checkMessageEvent();

		navigateFollowingElement("bpmn:userTask", "Analyse customer request");

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
		checkMessageEvent();
		navigateFollowingElement("bpmn:userTask", "Perform repair (premium level)");
		
		navigateElement(task3);
		navigateFollowingElement("bpmn:endEvent", "Repair completed");

		pop();

	}

}
