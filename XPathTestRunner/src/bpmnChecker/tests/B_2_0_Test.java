package bpmnChecker.tests;

import java.io.File;

import bpmnChecker.testBase.AbstractXpathTest;

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
				navigateElement("bpmn:startEvent[@name='Start Event 1 Timer']",
						"timerStartEvent");
				navigateElement("bpmn:task[@name='Abstract Task 1']");

				navigateElement("bpmn:sendTask[@name='Send Task 2']");

				{
					selectElement("bpmn:userTask[@name='User Task 3']");
					navigateElementByParam(
							"//bpmn:dataObjectReference[@id='%s']",
							"bpmn:dataInputAssociation/bpmn:sourceRef");
					pop();
				}

				navigateElement("bpmn:dataObjectReference[@name='Data Object']");

				{
					selectElement("bpmn:inclusiveGateway[@name='Inclusive Gateway 1']");
					navigateGatewaySequenceFlow("Conditional Sequence Flow");
					navigateGatewaySequenceFlow("Default Sequence Flow 1");
					checkDefaultSequenceFlow();
					pop();
				}
				navigateElement("bpmn:serviceTask[@name='Service Task 4']");

				navigateElement(
						"bpmn:intermediateThrowEvent[@name='Intermediate Event Signal Throw 1']",
						"signalEvent");

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
				navigateElement("bpmn:endEvent[@name='End Event 1 Message']",
						"messageEvent");
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

			pop();
		}

	}

}
