package org.omg.bpmn.miwg.xpath.checks;

import org.omg.bpmn.miwg.xpath.util.AbstractXpathCheck;
import org.omg.bpmn.miwg.xpath.util.ArtifactType;
import org.omg.bpmn.miwg.xpath.util.Direction;
import org.w3c.dom.Node;

public class B_1_0_Check extends AbstractXpathCheck {

	@Override
	public String getName() {
		return "B.1.0";
	}

	@Override
	public void doExecute() throws Throwable {

		{

			selectCollaboration();

			navigateElement("bpmn:messageFlow", "Message Flow 1");
			navigateElement("bpmn:messageFlow", "Message Flow 2");

			{
				selectProcessByParticipant("Participant");
				checkXORMarkersForProcess(false);
				navigateElement("bpmn:startEvent", "Start Event Timer");
				checkTimerEvent();
				navigateFollowingElement("bpmn:task", "Abstract Task 1");
				checkMessageFlow("Message Flow 1", Direction.Output);
				navigateFollowingElement("bpmn:userTask", "User Task 2");
				navigateFollowingElement("bpmn:serviceTask", "Service Task 3");
				navigateFollowingElement("bpmn:endEvent", "End Event None 1");
				pop();
			}

			{
				selectProcessByParticipant("Pool");
				checkXORMarkersForProcess(false);
				this.navigateLane("Lane 1");
				this.navigateLane("Lane 2");
				navigateElement("bpmn:startEvent", "Start Event Message");
				checkMessageEvent();
				Node n1 = navigateFollowingElement("bpmn:parallelGateway",
						"Parallel Gateway Divergence");
				Node n2 = navigateFollowingElement("bpmn:exclusiveGateway",
						"Exclusive Gateway Divergence 1");
				navigateFollowingElement("bpmn:callActivity",
						"Call Activity Collapsed");
				checkTextAssociation("Text Annotation");

				{
					selectFollowingElement("bpmn:callActivity",
							"Call Activity - Expanded");
					selectProcessOfCallActivity();

					navigateElement("bpmn:startEvent", "Start Event None 1");
					navigateFollowingElement("bpmn:task", "Abstract Task 4");
					navigateFollowingElement("bpmn:endEvent",
							"End Event None 2");

					pop();
					pop();
				}

				navigateFollowingElement("bpmn:exclusiveGateway",
						"Exclusive Gateway Convergence 1");
				navigateFollowingElement("bpmn:endEvent", "End Event Message");
				checkMessageEvent();
				checkMessageFlow("Message Flow 2", Direction.Output);

				navigateBookmarkedElement(n2);

				navigateFollowingElement("bpmn:callActivity",
						"Call Activity Calling a Global Task");
				checkGlobalTask(false);
				navigateFollowingElement("bpmn:exclusiveGateway",
						"Exclusive Gateway Convergence 1");

				navigateBookmarkedElement(n1);

				navigateFollowingElement("bpmn:userTask", "User Task 5");
				n1 = navigateFollowingElement("bpmn:exclusiveGateway",
						"Exclusive Gateway Divergence 2");

				navigateFollowingElement("bpmn:subProcess",
						"Collapsed Sub-Process");

				{
					selectFollowingElement("bpmn:subProcess",
							"Sub Process - Expanded");

					navigateElement("bpmn:startEvent", "Start Event None 2");
					navigateFollowingElement("bpmn:task", "Abstract Task 6");
					navigateFollowingElement("bpmn:endEvent",
							"End Event None 3");

					pop();
				}

				navigateFollowingElement("bpmn:exclusiveGateway",
						"Exclusive Gateway Convergence 2");

				navigateFollowingElement("bpmn:endEvent", "End Event Terminate");

				navigateBookmarkedElement(n1);

				navigateFollowingElement("bpmn:serviceTask", "Service Task 7");
				checkDataAssociation(ArtifactType.DataObject, "Data Object",
						Direction.Input);
				checkDataAssociation(ArtifactType.DataStoreReference,
						"Data Store Reference", Direction.Output);

				navigateFollowingElement("bpmn:exclusiveGateway",
						"Exclusive Gateway Convergence 2");

				pop();
			}

			pop();
		}

	}

}
