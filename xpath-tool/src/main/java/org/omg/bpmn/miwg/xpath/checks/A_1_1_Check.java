package org.omg.bpmn.miwg.xpath.checks;

import org.omg.bpmn.miwg.xpath.util.OwnerType;

public class A_1_1_Check extends A_1_0_Check {

	@Override
	public String getName() {
		return "A.1.1";
	}

	@Override
	protected void doExecute() throws Throwable {
		selectFirstProcess();

		navigateElement("bpmn:startEvent", "Start Event");

		navigateFollowingElement("bpmn:userTask", "Task 1");
		checkOwner(OwnerType.PotentialOwner, "Performer 1");

		navigateFollowingElement("bpmn:serviceTask", "Task 2");
		checkOperation("Operation 1");

		navigateFollowingElement("bpmn:userTask", "Task 3");
		checkOwner(OwnerType.PotentialOwner, "Performer 2");

		navigateFollowingElement("bpmn:endEvent", "End Event");

		pop();
	}

}
