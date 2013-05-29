package org.omg.bpmn.miwg.xpathTestRunner.tests;

import java.io.File;

import org.omg.bpmn.miwg.xpathTestRunner.testBase.AbstractXpathTest;
import org.omg.bpmn.miwg.xpathTestRunner.testBase.ArtifactType;
import org.omg.bpmn.miwg.xpathTestRunner.testBase.AssociationDirection;

public class B_2_0_Test extends AbstractXpathTest {

	@Override
	public String getName() {
		return "B.2.0";
	}

	@Override
	public boolean isApplicable(String fileName) {
		File f = new File(fileName);
		String fn = f.getName();
		return fn.equals("B.2.0-export.bpmn")
				|| fn.equals("B.2.0-roundtrip.bpmn") || fn.equals("B.2.0.bpmn");
	}

	@Override
	public void execute(String fileName) throws Throwable {

		{
			loadFile(fileName);

			selectElement("//bpmn:collaboration");

			navigateElement("bpmn:messageFlow[@name='Message Flow 1']",
					"messageFlowL2");
			navigateElement("bpmn:messageFlow[@name='Message Flow 2']");

			{
				selectProcess("//bpmn:process[@id=//bpmn:participant[@name='Participant']/@processRef]");

				navigateElement("bpmn:startEvent[@name='Start Event 1 Timer']");
				checkTimerEventL1();

				navigateElement("bpmn:task[@name='Abstract Task 1']");

				navigateElement("bpmn:sendTask[@name='Send Task 2']");

				navigateElement("bpmn:userTask[@name='User Task 3']");
				checkAssociation(ArtifactType.DataObject, "Data Object",
						AssociationDirection.Input);

				navigateElement("bpmn:dataObjectReference[@name='Data Object']");

				{
					selectElement("bpmn:inclusiveGateway[@name='Inclusive Gateway 1']");
					navigateGatewaySequenceFlow("Conditional Sequence Flow");
					navigateGatewaySequenceFlow("Default Sequence Flow 1");
					checkDefaultSequenceFlow();
					pop();
				}
				navigateElement("bpmn:serviceTask[@name='Service Task 4']");

				navigateElement("bpmn:intermediateThrowEvent[@name='Intermediate Event Signal Throw 1']");
				checkSignalEventL1();

				{
					selectElement("bpmn:subProcess[@name='Collapsed Sub-Process 1 Multi-Instances']");
					navigateElement("bpmn:multiInstanceLoopCharacteristics[@isSequential='false']");
					pop();
				}

				navigateElement("bpmn:task[@name='Task 5']");

				{
					selectElement("bpmn:boundaryEvent[@name='Boundary Intermediate Event Non-Interrupting Conditional']");
					navigateElementByParam(
							"../bpmn:task[@name='Task 5' and @id='%s']",
							"@attachedToRef");
					pop();
				}

				navigateElement("bpmn:parallelGateway[@name='Parallel Gateway 2']");
				navigateElement("bpmn:endEvent[@name='End Event 1 Message']");
				checkMessageEventL1();
				{
					selectElement("//bpmn:globalUserTask[@name='Call Activity calling a Global User Task']");
					navigateElementByParam(
							"//bpmn:callActivity[@calledElement='%s' and @name='Call Activity calling a Global User Task']",
							"@id");
					pop();
				}

				{
					selectElement("bpmn:subProcess[@name='Expanded Sub-Process 1']");
					navigateElement("bpmn:startEvent[@name='Start Event 2']");
					{
						selectElement("bpmn:userTask[@name='User Task 7 Standard Loop']");
						navigateElement("bpmn:standardLoopCharacteristics");
						pop();
					}
					navigateElement("bpmn:endEvent[@name='End Event 2']");
					pop();
				}

				pop();
			}

			{
				selectProcess("//bpmn:process[@id=//bpmn:participant[@name='Pool']/@processRef]");

				navigateElement("bpmn:laneSet/bpmn:lane[@name='Lane 1']");
				navigateElement("bpmn:laneSet/bpmn:lane[@name='Lane 2']");

				navigateElement("bpmn:startEvent[@name='Start Event 2 Message']");
				checkMessageEventL1();

				navigateElement("bpmn:task[@name='Task 11']");

				navigateElement(
						"bpmn:eventBasedGateway[@name='Event Base Gateway 3']",
						"exclusiveGateway");

				navigateElement("bpmn:intermediateCatchEvent[@name='Intermediate Event Timer Catch']");

				navigateElement("bpmn:intermediateCatchEvent[@name='Intermediate Event Message Catch']");
				checkMessageEventL1();

				navigateElement("bpmn:receiveTask[@name='Receive Task 20']");

				navigateElement(
						"bpmn:subProcess[@name='Collapsed Sub-Process 2']",
						"L2CollapsedSubProcess");
				checkAssociation(ArtifactType.DataStoreReference,
						"Data Store Reference", AssociationDirection.Input);

				{
					selectElement("bpmn:exclusiveGateway[@name='Exclusive Gateway 4']");
					navigateGatewaySequenceFlow("Default Sequence Flow 2");
					checkDefaultSequenceFlow();
					pop();
				}

				navigateElement("bpmn:intermediateThrowEvent[@name='Intermediate Event Message Throw']");
				checkMessageEventL1();

				pop();
			}

			pop();
		}

	}

}
