package org.omg.bpmn.miwg.xpath.checks;

import org.omg.bpmn.miwg.xpath.util.AbstractXpathCheck;
import org.omg.bpmn.miwg.xpath.util.ArtifactType;
import org.omg.bpmn.miwg.xpath.util.Direction;
import org.w3c.dom.Node;

public class B_2_0_Check extends AbstractXpathCheck {

	@Override
	public String getName() {
		return "B.2.0";
	}

	@Override
	public void doExecute() throws Throwable {

		Node n;
		{
			selectCollaboration();

			{
				selectProcessByParticipant("Participant");

				checkXORMarkersForProcess(true);

				navigateElement("bpmn:startEvent", "Start Event 1 Timer");
				checkTimerEvent();

				navigateFollowingElement("bpmn:task", "Abstract Task 1");

				navigateFollowingElement("bpmn:sendTask", "Send Task 2");
				checkMessageFlow("Message Flow 1", Direction.Output);

				navigateFollowingElement("bpmn:userTask", "User Task 3");
				checkDataAssociation(ArtifactType.DataObject, "Data Object",
						Direction.Input);

				n = navigateFollowingElement("bpmn:inclusiveGateway",
						"Inclusive Gateway 1");

				navigateFollowingElement("bpmn:serviceTask", "Service Task 4",
						"Conditional Sequence Flow");

				navigateFollowingElement("bpmn:intermediateThrowEvent",
						"Intermediate Event Signal Throw 1");
				checkSignalEvent();

				{
					selectFollowingElement("bpmn:subProcess",
							"Collapsed Sub-Process 1 Multi-Instances");
					checkMultiInstanceParallel();
					checkXORMarkersForProcess(true);
					pop();
				}

				navigateFollowingElement("bpmn:task", "Task 5");

				navigateBoundaryEvent("Boundary Intermediate Event Non-Interrupting Conditional");
				checkCancelActivity(false);
				checkParallelMultiple(false);
				checkConditionalEvent();

				navigateElement("bpmn:parallelGateway", "Parallel Gateway 2");
				navigateElement("bpmn:endEvent", "End Event 1 Message");
				checkMessageEvent();

				navigateBookmarkedElement(n);

				navigateFollowingElement("bpmn:callActivity",
						"Call Activity calling a Global User Task");
				checkGlobalTask(true);

				{
					selectElement("bpmn:subProcess", "Expanded Sub-Process 1");
					checkXORMarkersForProcess(true);

					navigateElement("bpmn:startEvent", "Start Event 2");

					navigateFollowingElement("bpmn:userTask",
							"User Task 7 Standard Loop");
					// TODO:
					// navigateElementX("bpmn:standardLoopCharacteristics");

					navigateFollowingElement("bpmn:endEvent", "End Event 2");
					pop();
				}

				navigateElement("bpmn:userTask", "User Task 8");
				navigateBoundaryEvent(null);
				checkEscalationEvent();

				navigateFollowingElement("bpmn:task", "Task 9");

				navigateFollowingElement("bpmn:intermediateCatchEvent",
						"Intermediate Event Conditional Catch");
				checkConditionalEvent();

				navigateFollowingElement("bpmn:task", "Task 10");
				checkMessageFlow("Message Flow 2", Direction.Input);

				navigateFollowingElement("bpmn:endEvent", "End Event 3 Signal");
				checkSignalEvent();

				pop();
			}

			{
				selectPool("Pool");
				selectReferencedProcess();

				checkXORMarkersForProcess(true);

				navigateLane("Lane 1");
				navigateLane("Lane 2");

				navigateElement("bpmn:startEvent", "Start Event 2 Message");
				checkMessageEvent();
				checkMessageFlow("Message Flow 1", Direction.Input);

				navigateFollowingElement("bpmn:task", "Task 11");

				n = navigateFollowingElement("bpmn:eventBasedGateway",
						"Event Base Gateway 3");
				checkEventGatewayExclusive(true);

				navigateBookmarkedElement(n);
				navigateFollowingElement("bpmn:intermediateCatchEvent",
						"Intermediate Event Message Catch");
				checkMessageEvent();

				navigateBookmarkedElement(n);
				navigateFollowingElement("bpmn:intermediateCatchEvent",
						"Intermediate Event Message Catch 2");

				navigateBookmarkedElement(n);
				navigateFollowingElement("bpmn:intermediateCatchEvent",
						"Intermediate Event Timer Catch");

				{
					// Flow following the intermediate timer event

					navigateFollowingElement("bpmn:callActivity",
							"Expanded Call Activity");

					selectCallActivityProcess();
					{

						navigateElement("bpmn:startEvent", "Start Event 3");

						navigateElement("bpmn:startEvent",
								"Start Event 4 Conditional");

						navigateFollowingElement("bpmn:userTask",
								"User Task 12 Muti-Inst. Seq.");
						checkMultiInstanceSequential();

						n = navigateFollowingElement("bpmn:userTask",
								"User Task 13");
						navigateBoundaryEvent("Boundary Intermediate Event Interrupting Message");
						checkCancelActivity(true);
						checkMessageEvent();

						navigateFollowingElement("bpmn:endEvent",
								"End Event 5 Terminate");
						checkTerminateEvent();

						navigateBookmarkedElement(n);

						navigateFollowingElement("bpmn:serviceTask",
								"Service Task 14");

						navigateFollowingElement("bpmn:endEvent", "End Event 4");

					}
					pop();

					n = navigateFollowingElement("bpmn:serviceTask",
							"Service Task 15");
					navigateBoundaryEvent("Boundary Intermediate Event Interrupting Conditional");
					checkConditionalEvent();
					checkCancelActivity(true);

					navigateFollowingElement("bpmn:intermediateThrowEvent",
							"Intermediate Event Link");
					checkLinkEvent();

					navigateBookmarkedElement(n);

					navigateFollowingElement("bpmn:receiveTask",
							"Receive Task 16");

					navigateFollowingElement("bpmn:endEvent",
							"End Event 6 Message");
					checkMessageFlow("Message Flow 2", Direction.Output);

				}

				{
					// FLOW following the event message catch

					navigateElement("bpmn:subProcess",
							"Collapsed Sub-Process 2");
					checkDataAssociation(ArtifactType.DataStoreReference,
							"Data Store Reference", Direction.Input);

					{
						selectElement("bpmn:exclusiveGateway",
								"Exclusive Gateway 4");
						navigateGatewaySequenceFlowStack("Default Sequence Flow 2");
						checkDefaultSequenceFlow();
						pop();
					}

					navigateElement("bpmn:intermediateThrowEvent",
							"Intermediate Event Message Throw");
					checkMessageEvent();

					n = navigateElement("bpmn:callActivity",
							"Collapsed Call Activity");

					navigateBoundaryEvent("Boundary Intermediate Event Non-Interrupting Escalation");
					checkEscalationEvent();

					navigateFollowingElement("bpmn:task", "Task 18");

					navigateFollowingElement("bpmn:intermediateThrowEvent", "");
					checkEscalationEvent();

					navigateFollowingElement("bpmn:task", "Task 23");

					navigateBookmarkedElement(n);

					n = navigateFollowingElement("bpmn:task", "Task 17");
					navigateBoundaryEvent("Boundary Intermediate Event Non-Interrupting Message");
					checkCancelActivity(false);
					checkParallelMultiple(false);
					checkMessageEvent();

					navigateFollowingElement("bpmn:task", "Task 19");

					navigateFollowingElement("bpmn:endEvent",
							"End Event 7 None");

					navigateBookmarkedElement(n);

					navigateFollowingElement("bpmn:endEvent",
							"End Event 7 None");

				}

				{
					// FLOW following the Intermediate Event Message Catch 2

					navigateElement("bpmn:intermediateCatchEvent",
							"Intermediate Event Message Catch 2");

					n = navigateFollowingElement("bpmn:task", "Task 21");
					navigateBoundaryEvent("Boundary Intermediate Event Interrupting Timer");
					checkCancelActivity(true);
					checkTimerEvent();

					navigateFollowingElement("bpmn:task", "Task 27");

					navigateFollowingElement("bpmn:inclusiveGateway",
							"Inclusive Gateway 6");

					navigateBookmarkedElement(n);

					{
						selectFollowingElement("bpmn:subProcess",
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
					checkSignalEvent();
					checkCancelActivity(false);

					navigateFollowingElement("bpmn:task", "Task 24");

					navigateFollowingElement("bpmn:endEvent",
							"End Event 11 Escalation");

				}

				{
					navigateElement("bpmn:startEvent", "Start Event 6 Signal");

					navigateFollowingElement("bpmn:task", "Task 25");

					navigateFollowingElement("bpmn:parallelGateway",
							"Parallel Gateway 5");

					navigateFollowingElement("bpmn:task", "Task 26");

					navigateFollowingElement("bpmn:intermediateThrowEvent",
							"Intermediate Event Signal Throw 2");

					navigateFollowingElement("bpmn:inclusiveGateway",
							"Inclusive Gateway 6");

					navigateFollowingElement("bpmn:task", "Task 28");

					navigateFollowingElement("bpmn:parallelGateway",
							"Parallel Gateway 7");

					navigateFollowingElement("bpmn:endEvent",
							"End Event 11 Escalation");

					navigateElement("bpmn:parallelGateway",
							"Parallel Gateway 5");

					navigateFollowingElement("bpmn:subProcess",
							"Expanded Sub-Process 3");

					navigateElement("bpmn:intermediateCatchEvent",
							"Intermediate Event Link");

					{
						selectFollowingElement("bpmn:subProcess",
								"Expanded Sub-Process 3");

						navigateElement("bpmn:startEvent", "Start Event 7");

						navigateFollowingElement("bpmn:intermediateCatchEvent",
								"Intermediate Event Signal Catch");
						checkSignalEvent();

						navigateFollowingElement("bpmn:task", "Task 31");

						n = navigateFollowingElement("bpmn:exclusiveGateway",
								"Exclusive Gateway 7");

						navigateFollowingElement("bpmn:endEvent",
								"End Event 12");

						navigateBookmarkedElement(n);
						navigateFollowingElement("bpmn:endEvent",
								"End Event 13 Error");
						checkErrorEvent();

						pop();
					}

					navigateFollowingElement("bpmn:task", "Task 32");
					navigateBoundaryEvent(null);

					navigateFollowingElement("bpmn:task", "Task 33");

					navigateFollowingElement("bpmn:endEvent", "End Event 14");

				}

				pop();
				pop();
			}

			pop();
		}

	}
}
