package bpmnChecker.tests;

import java.io.File;

public class B_1_0_Test extends AbstractXpathTest {

	@Override
	public String getName() {
		return "B.1.0";
	}

	@Override
	public boolean isApplicable(String fileName) {
		File f = new File(fileName);
		String fn = f.getName();
		return fn.equals("B.1.0-export.bpmn")
				|| fn.equals("B.1.0-roundtrip.bpmn") || fn.equals("B.1.0.bpmn");
	}

	@Override
	public void execute(String fileName) throws Throwable {

		{
			loadFile(fileName);

			selectElement("Collaboration", "//bpmn:collaboration");

			navigateElement("Message Flow 1",
					"bpmn:messageFlow[@name='Message Flow 1']");
			navigateElement("Message Flow 2",
					"bpmn:messageFlow[@name='Message Flow 2']");

			{
				selectProcess("Process of 'Participant'",
						"//bpmn:process[@id=//bpmn:participant[@name='Participant']/@processRef]");
				navigateElement("Start Event timer",
						"bpmn:startEvent[@name='Start Event Timer']",
						"timerStartEvent");
				navigateElement("User Task 2",
						"bpmn:userTask[@name='User Task 2']");
				navigateElement("Service Task 3",
						"bpmn:serviceTask[@name='Service Task 3']");
				navigateElement("End Event None 1",
						"bpmn:endEvent[@name='End Event None 1']");
				pop();
			}

			{
				selectProcess("Process of 'Pool'",
						"//bpmn:process[@id=//bpmn:participant[@name='Pool']/@processRef]");
				navigateElement("Lane 1", ".//bpmn:lane[@name='Lane 1']");
				navigateElement("Lane 2", ".//bpmn:lane[@name='Lane 2']");
				navigateElement("Start Event Message",
						"bpmn:startEvent[@name='Start Event Message']",
						"messageStartEvent");
				navigateElement("Parallel Gateway Divergence",
						"bpmn:parallelGateway[@name='Parallel Gateway Divergence']");
				navigateElement("Exclusive Gateway Divergence 1",
						"bpmn:exclusiveGateway[@name='Exclusive Gateway Divergence 1']");
				navigateElement("Exclusive Gateway Divergence 2",
						"bpmn:exclusiveGateway[@name='Exclusive Gateway Divergence 2']");
				navigateElement("Call Activity Collapsed",
						"bpmn:callActivity[@name='Call Activity Collapsed']");

				{
					selectElement("Call Activity - Expanded",
							"bpmn:callActivity[@name='Call Activity - Expanded']");
					{
						selectProcessOfCallActivity("Sub Process For Call Activity'");
						navigateElement("Start Event None 1",
								"bpmn:startEvent[@name='Start Event None 1']");
						navigateElement("Abstract Task 4",
								"bpmn:task[@name='Abstract Task 4']");
						navigateElement("End Event None 2",
								"bpmn:endEvent[@name='End Event None 2']");
						pop();
					}

					pop();
				}

				navigateElement("Exclusive Gateway Convergence 1",
						"bpmn:exclusiveGateway[@name='Exclusive Gateway Convergence 1']");
				navigateElement("Start Event Message",
						"bpmn:endEvent[@name='End Event Message']",
						"messageEndEvent");

				navigateElement("Text Annotation",
						"bpmn:textAnnotation/bpmn:text[contains(text(), 'Text Annotation')]");
				navigateElement("Call Activity Collapsed",
						"bpmn:subProcess[@name='Collapsed Sub-Process']");
				navigateElement("Exclusive Gateway Convergence 2",
						"bpmn:exclusiveGateway[@name='Exclusive Gateway Convergence 2']");
				navigateElement("End Event Terminate",
						"bpmn:endEvent[@name='End Event Terminate']", "terminateEvent");
				navigateElement("Service Task 7",
						"bpmn:serviceTask[@name='Service Task 7']");
				navigateElement("Data Object",
						"bpmn:dataObjectReference[@name='Data Object']");
				navigateElement("Data Store Reference",
						"bpmn:dataStoreReference[@name='Data Store Reference']");
				navigateElement("Group", "//bpmn:categoryValue[@value='Group']");

				{
					selectElement("Sub Process - Expanded",
							"bpmn:subProcess[@name='Sub Process - Expanded']");

					navigateElement("Start Event None 2",
							"bpmn:startEvent[@name='Start Event None 2']");
					navigateElement("Abstract Task 6",
							"bpmn:task[@name='Abstract Task 6']");
					navigateElement("End Event None 3",
							"bpmn:endEvent[@name='End Event None 3']");

					pop();
				}

				pop();
			}

			pop();
		}

	}

}
