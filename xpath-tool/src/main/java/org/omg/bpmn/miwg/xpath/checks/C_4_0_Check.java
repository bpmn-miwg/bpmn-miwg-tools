package org.omg.bpmn.miwg.xpath.checks;

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
			this.navigateDefinitions();
			checkAttributeValue("name", "EmployeeOnboarding");
			// According to Falko's document, this is the expected value. However, it is not
			// retained.
			// checkAttributeValue("id", "definition_");
			// checkAttributeValue("targetNamespace", "http://www.boc-group.com");
			checkAttributeValue("expressionLanguage", "http://www.w3.org/1999/XPath");
			checkAttributeValue("typeLanguage", "http://www.w3.org/2001/XMLSchema");
			// unclear: What is "Version" referring to?

			navigateItemDefinition("itemDefEmployeeDetails");
			checkItemDefinition(ItemKind.Information, "semantic:string", false);

			navigateItemDefinition("itemDefUserManagement");
			checkItemDefinition(ItemKind.Information, "semantic:string", false);

			navigateItemDefinition("itemDefPayrollSystem");
			checkItemDefinition(ItemKind.Information, "semantic:string", false);

			navigateItemDefinition("itemDefAck");
			checkItemDefinition(ItemKind.Information, "semantic:boolean", false);

			navigateDataStore("User Management");
			checkDataStore(true, true);

			navigateDataStore("Payroll system");
			checkDataStore(true, true);

			navigateDataStore("Employee Details");
			checkDataStore(true, true);
			
			navigateSignal("Employee_hired_Dept_X");
			checkSignal("itemDefEmployeeDetails");
			
			navigateMessage("msgFacilites");
			checkMessage("itemDefAck");
			
			navigateMessage("msgIT");
			checkMessage("itemDefAck");

			navigateMessage("msgPayroll");
			checkMessage("itemDefAck");
		}

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
			checkSignalEvent("Employee_hired_Dept_X");

			navigateFollowingElement("bpmn:parallelGateway", "Non-exclusive Gateway");

			navigateFollowingElement("bpmn:userTask", "Introduce new employee to the team");

			navigateFollowingElement("bpmn:userTask", "Perform training for position");

			gateway = navigateFollowingElement("bpmn:parallelGateway", "Non-exclusive Gateway");

			navigateFollowingElement("bpmn:intermediateCatchEvent", "Input from IT ready");
			checkMessageEvent("msgIT");
			navigateFollowingElement("bpmn:parallelGateway", "Non-exclusive Gateway");

			navigateBookmarkedElement(gateway);
			navigateFollowingElement("bpmn:intermediateCatchEvent", "Input from Payroll ready");
			checkMessageEvent("msgPayroll");
			navigateFollowingElement("bpmn:parallelGateway", "Non-exclusive Gateway");

			navigateBookmarkedElement(gateway);
			navigateFollowingElement("bpmn:intermediateCatchEvent", "Input from Facilities ready");
			checkMessageEvent("msgFacilites");
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
			checkSignalEvent("Employee_hired_Dept_X");
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
			checkMessageEvent("msgIT");

			pop();
			pop();
		}

		{
			selectCollaborationByName("Payroll");
			selectProcessByParticipant("Payroll");

			navigateElement("bpmn:startEvent", "New employee hired");
			checkSignalEvent("Employee_hired_Dept_X");
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
			checkMessageEvent("msgPayroll");

			pop();
			pop();
		}

		{
			selectCollaborationByName("Facilities");
			selectProcessByParticipant("Facilities");

			navigateElement("bpmn:startEvent", "New employee hired");
			checkSignalEvent("Employee_hired_Dept_X");
			// POTENTIAL BUG IN THE REFERENCE: cf. issue #779,
			// https://github.com/bpmn-miwg/bpmn-miwg-test-suite/issues/779
			// checkDataAssociation(ArtifactType.DataStoreReference, "Employee Details",
			// Direction.Output);

			navigateFollowingElement("bpmn:manualTask", "Prepare access card");

			navigateFollowingElement("bpmn:userTask", "Configure access details");
			checkDataAssociation(ArtifactType.DataStoreReference, "Employee Details", Direction.Input);

			navigateFollowingElement("bpmn:endEvent", "Access card ready");
			checkMessageEvent("msgFacilites");

			pop();
			pop();
		}

	}

}
