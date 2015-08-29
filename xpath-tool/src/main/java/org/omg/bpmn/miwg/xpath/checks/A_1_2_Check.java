package org.omg.bpmn.miwg.xpath.checks;

import org.omg.bpmn.miwg.xpath.common.ArtifactType;
import org.omg.bpmn.miwg.xpath.common.Direction;
import org.omg.bpmn.miwg.xpath.common.OwnerType;

public class A_1_2_Check extends A_1_0_Check {

	@Override
	public String getName() {
		return "A.1.2";
	}

	@Override
	protected void doExecute() throws Throwable {
		selectFirstProcess();

		navigateElement("bpmn:startEvent", "Start Event");

		navigateFollowingElement("bpmn:userTask", "Task 1");
		checkOwner(OwnerType.Performer, "Performer 1");
		checkDataAssociation(ArtifactType.DataObject, "Data Object 1",
				Direction.Output);

		navigateFollowingElement("bpmn:serviceTask", "Task 2");
		checkOperation("Operation 1");
		checkDataAssociation(ArtifactType.DataObject, "Data Object 1",
				Direction.Input);
		checkDataAssociation(ArtifactType.DataObject, "Data Object 2",
				Direction.Output);

		navigateFollowingElement("bpmn:userTask", "Task 3");
		checkDataAssociation(ArtifactType.DataObject, "Data Object 2",
				Direction.Input);
		checkOwner(OwnerType.Performer, "Performer 2");

		navigateFollowingElement("bpmn:endEvent", "End Event");

		pop();
	}

}
