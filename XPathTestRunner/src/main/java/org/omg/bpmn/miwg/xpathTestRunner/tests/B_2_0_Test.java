package org.omg.bpmn.miwg.xpathTestRunner.tests;

import java.io.File;

import org.omg.bpmn.miwg.xpathTestRunner.testBase.AbstractXpathTest;
import org.omg.bpmn.miwg.xpathTestRunner.testBase.ArtifactType;
import org.omg.bpmn.miwg.xpathTestRunner.testBase.Direction;
import org.w3c.dom.Node;

public class B_2_0_Test extends AbstractXpathTest {

	@Override
	public String getName() {
		return "B.2.0";
	}

	@Override
	public boolean isApplicable(File file) {
		String fn = file.getName();
		return fn.equals("B.2.0-export.bpmn")
				|| fn.equals("B.2.0-roundtrip.bpmn") || fn.equals("B.2.0.bpmn");
	}

	@Override
	public void execute(File file) throws Throwable {

		Node n;
		{
			loadFile(file);

			selectElement("//bpmn:collaboration");

			navigateElementX("bpmn:messageFlow[@name='Message Flow 1']",
					"messageFlowL2");
			navigateElementX("bpmn:messageFlow[@name='Message Flow 2']");

			{
				selectProcess("//bpmn:process[@id=//bpmn:participant[@name='Participant']/@processRef]");

				navigateElementX("bpmn:startEvent[@name='Start Event 1 Timer']");
				checkTimerEventL1();

				navigateElementX("bpmn:task[@name='Abstract Task 1']");

				navigateElementX("bpmn:sendTask[@name='Send Task 2']");
				checkMessageFlow("Message Flow 1", Direction.Output);

				navigateElementX("bpmn:userTask[@name='User Task 3']");
				checkAssociation(ArtifactType.DataObject, "Data Object",
						Direction.Input);

				navigateElementX("bpmn:dataObjectReference[@name='Data Object']");

				{
					selectElement("bpmn:inclusiveGateway[@name='Inclusive Gateway 1']");
					navigateGatewaySequenceFlow("Conditional Sequence Flow");
					navigateGatewaySequenceFlow("Default Sequence Flow 1");
					checkDefaultSequenceFlow();
					pop();
				}
				navigateElementX("bpmn:serviceTask[@name='Service Task 4']");

				navigateElementX("bpmn:intermediateThrowEvent[@name='Intermediate Event Signal Throw 1']");
				checkSignalEventL1();

				{
					selectElement("bpmn:subProcess[@name='Collapsed Sub-Process 1 Multi-Instances']");
					navigateElementX("bpmn:multiInstanceLoopCharacteristics[@isSequential='false']");
					pop();
				}

				navigateElementX("bpmn:task[@name='Task 5']");

				navigateBoundaryEvent("Boundary Intermediate Event Non-Interrupting Conditional");
				checkCancelActivity(false);
				checkParallelMultiple(false);
				checkConditionalEventL1();

				navigateElementX("bpmn:parallelGateway[@name='Parallel Gateway 2']");
				navigateElementX("bpmn:endEvent[@name='End Event 1 Message']");
				checkMessageEventL1();
				{
					selectElement("//bpmn:globalUserTask[@name='Call Activity calling a Global User Task']");
					navigateElementXByParam(
							"//bpmn:callActivity[@calledElement='%s' and @name='Call Activity calling a Global User Task']",
							"@id");
					pop();
				}

				{
					selectElement("bpmn:subProcess[@name='Expanded Sub-Process 1']");
					navigateElementX("bpmn:startEvent[@name='Start Event 2']");
					{
						selectElement("bpmn:userTask[@name='User Task 7 Standard Loop']");
						navigateElementX("bpmn:standardLoopCharacteristics");
						pop();
					}
					navigateElementX("bpmn:endEvent[@name='End Event 2']");
					pop();
				}

				navigateElementX("bpmn:userTask[@name='User Task 8']");
				navigateBoundaryEvent("");
				checkEscalationEventL1();

				navigateFollowingElement("bpmn:task", "Task 9");

				navigateFollowingElement("bpmn:intermediateCatchEvent",
						"Intermediate Event Conditional Catch");
				checkConditionalEventL1();

				navigateFollowingElement("bpmn:task", "Task 10");
				checkMessageFlow("Message Flow 2", Direction.Input);

				navigateFollowingElement("bpmn:endEvent", "End Event 3 Signal");
				checkSignalEventL1();

				pop();
			}

			{
				selectProcess("//bpmn:process[@id=//bpmn:participant[@name='Pool']/@processRef]");

				navigateElementX("bpmn:laneSet/bpmn:lane[@name='Lane 1']");
				navigateElementX("bpmn:laneSet/bpmn:lane[@name='Lane 2']");

				navigateElement("bpmn:startEvent", "Start Event 2 Message");
				checkMessageEventL1();
				checkMessageFlow("Message Flow 1", Direction.Input);

				navigateFollowingElement("bpmn:task", "Task 11");

				n = navigateFollowingElement("bpmn:eventBasedGateway",
						"Event Base Gateway 3");
				checkEventGatewayExclusive(true);

				navigateFollowingElement(n, "bpmn:intermediateCatchEvent",
						"Intermediate Event Timer Catch");

				navigateFollowingElement(n, "bpmn:intermediateCatchEvent",
						"Intermediate Event Message Catch");
				checkMessageEventL1();

				navigateFollowingElement(n, "bpmn:receiveTask",
						"Receive Task 20");

				{
					// // FLOW following the event message catch

					navigateElementX(
							"bpmn:subProcess[@name='Collapsed Sub-Process 2']",
							"L2CollapsedSubProcess");
					checkAssociation(ArtifactType.DataStoreReference,
							"Data Store Reference", Direction.Input);

					{
						selectElement("bpmn:exclusiveGateway[@name='Exclusive Gateway 4']");
						navigateGatewaySequenceFlow("Default Sequence Flow 2");
						checkDefaultSequenceFlow();
						pop();
					}

					navigateElementX("bpmn:intermediateThrowEvent[@name='Intermediate Event Message Throw']");
					checkMessageEventL1();

					n = navigateElementX("bpmn:callActivity[@name='Collapsed Call Activity']");

					navigateBoundaryEvent("Boundary Intermediate Event Non-Interrupting Escalation");
					checkEscalationEventL1();

					navigateFollowingElement("bpmn:intermediateThrowEvent",
							"Intermediate Event Link Throw");
					checkLinkEventL1();

					n = navigateFollowingElement(n, "bpmn:task", "Task 17");
					navigateBoundaryEvent("Boundary Intermediate Event Non-Interrupting Message");
					checkCancelActivity(false);
					checkParallelMultiple(false);
					checkMessageEventL1();

					navigateFollowingElement("bpmn:task", "Task 19");

					navigateFollowingElement("bpmn:endEvent",
							"End Event 7 None");

					navigateFollowingElement(n, "bpmn:endEvent",
							"End Event 7 None");

				}

				navigateElementX("bpmn:task[@name='Task 18']");

				navigateFollowingElement("bpmn:intermediateThrowEvent", "");
				checkEscalationEventL1();

				navigateFollowingElement("bpmn:task", "Task 23");

				navigateElementX("bpmn:receiveTask[@name='Receive Task 20']");

				n = navigateFollowingElement("bpmn:task", "Task 21");
				navigateBoundaryEvent("Boundary Intermediate Event Interrupting Timer");
				checkCancelActivity(true);
				checkTimerEventL1();

				navigateFollowingElement("bpmn:task", "Task 27");

				navigateFollowingElement("bpmn:inclusiveGateway",
						"Inclusive Gateway 6");

				{
					selectFollowingElement(n, "bpmn:subProcess",
							"Expanded Sub-Process 2");
					navigateElement("bpmn:startEvent", "Start Event 5 None");

					navigateFollowingElement("bpmn:serviceTask",
							"Service Task 22");
					checkMultiInstanceParallel();

					navigateFollowingElement("bpmn:endEvent",
							"End Event 8 None");

					pop();
				}

				navigateFollowingElement("bpmn:task", "Task 23");
				navigateBoundaryEvent("Boundary Intermediate Event Non-Interrupting Signal");
				checkSignalEventL1();
				checkCancelActivity(false);

				navigateFollowingElement("bpmn:task", "Task 24");

				navigateFollowingElement("bpmn:endEvent",
						"End Event 11 Escatation");

				pop();
			}

			pop();
		}

	}
}
