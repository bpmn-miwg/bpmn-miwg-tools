package org.omg.bpmn.miwg.xpath.checks;

import org.omg.bpmn.miwg.xpath.pluggableAssertions.Assertion;
import org.omg.bpmn.miwg.xpath.pluggableAssertions.SequenceFlowCondition;
import org.omg.bpmn.miwg.xpath.util.AbstractXpathCheck;
import org.omg.bpmn.miwg.xpath.util.ArtifactType;
import org.omg.bpmn.miwg.xpath.util.Direction;
import org.w3c.dom.Node;

public class C_4_0_Check extends AbstractXpathCheck {

	@Override
	public String getName() {
		return "C.4.0";
	}

	@Override
	public void doExecute() throws Throwable {

		{
			selectCollaborationByName("Onboarding employee");
			selectProcessByParticipant("Money Bank");
			checkAttributeValue("name", "Onboarding employee - Money Bank - Process");

			selectProcessByLane("HR Department");
			pop();
			selectProcessByLane("Responsible Department");
			pop();

			navigateElement("bpmn:startEvent", "Candidate accepted offer");

			navigateFollowingElement("bpmn:userTask", "Send candidate Contract");

			Node gateway = navigateFollowingElement("bpmn:exclusiveGateway", "Contract terms accepted?");

			navigateFollowingElement("bpmn:userTask", "Review terms of Contract", "No");
			navigateFollowingElement("bpmn:userTask", "Send candidate Contract");

			navigateBookmarkedElement(gateway);

			navigateFollowingElement("bpmn:userTask", "Get signature on contract and notify responsible department");
			checkDataAssociation(ArtifactType.DataStoreReference, "Employee Details", Direction.Output);

			gateway = navigateFollowingElement("bpmn:parallelGateway", "Non-exclusive Gateway");

			navigateFollowingElement("bpmn:userTask", "Inform employee of company policies");

			navigateFollowingElement("bpmn:userTask", "Introduce employee to company Mission, Vision and Values");

			navigateFollowingElement("bpmn:userTask", "Perform training for time reports, sick leave and holidays");

			navigateFollowingElement("bpmn:userTask", "Register for medical insurance");

			navigateFollowingElement("bpmn:parallelGateway", "Non-exclusive Gateway");

			navigateBookmarkedElement(gateway);

			navigateFollowingElement("bpmn:userTask", "Request preparations for a new employee");

			navigateFollowingElement("bpmn:intermediateThrowEvent", "New employee in department X");
			// POTENTIAL BUG IN THE REFERENCE: cf. issue #779,
			// https://github.com/bpmn-miwg/bpmn-miwg-test-suite/issues/779
			// checkDataAssociation(ArtifactType.DataStoreReference, "Employee Details",
			// Direction.Input);

			navigateFollowingElement("bpmn:parallelGateway", "Non-exclusive Gateway");

			navigateFollowingElement("bpmn:userTask", "Introduce new employee to the team");

			navigateFollowingElement("bpmn:userTask", "Perform training for position");

			gateway = navigateFollowingElement("bpmn:parallelGateway", "Non-exclusive Gateway");

			navigateFollowingElement("bpmn:intermediateCatchEvent", "Input from IT ready");
			navigateFollowingElement("bpmn:parallelGateway", "Non-exclusive Gateway");

			navigateBookmarkedElement(gateway);
			navigateFollowingElement("bpmn:intermediateCatchEvent", "Input from Payroll ready");
			navigateFollowingElement("bpmn:parallelGateway", "Non-exclusive Gateway");

			navigateBookmarkedElement(gateway);
			navigateFollowingElement("bpmn:intermediateCatchEvent", "Input from Facilities ready");
			navigateFollowingElement("bpmn:parallelGateway", "Non-exclusive Gateway");

			navigateFollowingElement("bpmn:userTask", "Compile welcome package");

			navigateFollowingElement("bpmn:userTask", "Give employee welcome package");

			navigateFollowingElement("bpmn:endEvent", "End Event");

			pop();
			pop();
		}

		{
			selectCollaborationByName("IT");
			selectProcessByParticipant("IT");

			navigateElement("bpmn:startEvent", "New employee hired");
			checkSignalEvent();
			// POTENTIAL BUG IN THE REFERENCE: cf. issue #779,
			// https://github.com/bpmn-miwg/bpmn-miwg-test-suite/issues/779
			// checkDataAssociation(ArtifactType.DataStoreReference, "Employee Details",
			// Direction.Output);

			navigateFollowingElement("bpmn:userTask", "Create domain account");
			checkDataAssociation(ArtifactType.DataStoreReference, "Employee Details", Direction.Input);
			checkDataAssociation(ArtifactType.DataStoreReference, "User Management", Direction.Output);

			navigateFollowingElement("bpmn:manualTask", "Prepare workstation");

			navigateFollowingElement("bpmn:userTask", "Assign required applications and permissions");
			checkDataAssociation(ArtifactType.DataStoreReference, "User Management", Direction.Output);

			navigateFollowingElement("bpmn:serviceTask", "Configure workstation");
			checkDataAssociation(ArtifactType.DataStoreReference, "User Management", Direction.Input);
			checkTextAssociation("With PowerShell");

			navigateFollowingElement("bpmn:userTask", "Prepare IT part of welcome package");

			navigateFollowingElement("bpmn:endEvent", "Workstation and permissions ready");
			checkMessageEvent();

			pop();
			pop();
		}

		{
			selectCollaborationByName("Payroll");
			selectProcessByParticipant("Payroll");

			navigateElement("bpmn:startEvent", "New employee hired");
			checkSignalEvent();
			// POTENTIAL BUG IN THE REFERENCE: cf. issue #779,
			// https://github.com/bpmn-miwg/bpmn-miwg-test-suite/issues/779
			// checkDataAssociation(ArtifactType.DataStoreReference, "Employee Details",
			// Direction.Output);
			
			navigateFollowingElement("bpmn:userTask", "Validate provided information");
			checkDataAssociation(ArtifactType.DataStoreReference, "Employee Details", Direction.Input);
			
			Node gateway = navigateFollowingElement("bpmn:exclusiveGateway", "All necessary data available?");
			
			navigateFollowingElement("bpmn:manualTask", "Clarify missing points", "No");
			checkStandardLoopCharacteristics();
			
			navigateFollowingElement("bpmn:userTask", "Update payroll system");
			
			navigateBookmarkedElement(gateway);
			
			navigateFollowingElement("bpmn:userTask", "Update payroll system");
			checkDataAssociation(ArtifactType.DataStoreReference, "Payroll system", Direction.Output);
			
			navigateFollowingElement("bpmn:endEvent", "Payroll ready");
			checkMessageEvent();
			
			pop();
			pop();
		}

		{
			selectCollaborationByName("Facilities");
			selectProcessByParticipant("Facilities");

			navigateElement("bpmn:startEvent", "New employee hired");
			checkSignalEvent();
			// POTENTIAL BUG IN THE REFERENCE: cf. issue #779,
			// https://github.com/bpmn-miwg/bpmn-miwg-test-suite/issues/779
			// checkDataAssociation(ArtifactType.DataStoreReference, "Employee Details",
			// Direction.Output);
			
			navigateFollowingElement("bpmn:manualTask", "Prepare access card");
			
			navigateFollowingElement("bpmn:userTask", "Configure access details");
			checkDataAssociation(ArtifactType.DataStoreReference, "Employee Details", Direction.Input);
			
			navigateFollowingElement("bpmn:endEvent", "Access card ready");
			checkMessageEvent();
			
			pop();
			pop();
		}

	}

}
