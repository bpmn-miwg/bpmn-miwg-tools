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

			selectElement("//bpmn:collaboration");

			navigateElement("bpmn:messageFlow[@name='Message Flow 1']");
			navigateElement("bpmn:messageFlow[@name='Message Flow 2']");

			{
				selectProcess("//bpmn:process[@id=//bpmn:participant[@name='Participant']/@processRef]");
				navigateElement("bpmn:startEvent[@name='Start Event Timer']",
						"timerStartEvent");
				navigateElement("bpmn:userTask[@name='User Task 2']");
				navigateElement("bpmn:serviceTask[@name='Service Task 3']");
				navigateElement("bpmn:endEvent[@name='End Event None 1']");
				pop();
			}

			{
				selectProcess("//bpmn:process[@id=//bpmn:participant[@name='Pool']/@processRef]");
				navigateElement(".//bpmn:lane[@name='Lane 1']");
				navigateElement(".//bpmn:lane[@name='Lane 2']");
				navigateElement("bpmn:startEvent[@name='Start Event Message']",
						"messageStartEvent");
				navigateElement("bpmn:parallelGateway[@name='Parallel Gateway Divergence']");
				navigateElement("bpmn:exclusiveGateway[@name='Exclusive Gateway Divergence 1']");
				navigateElement("bpmn:exclusiveGateway[@name='Exclusive Gateway Divergence 2']");
				navigateElement("bpmn:callActivity[@name='Call Activity Collapsed']");

				{
					selectElement("bpmn:callActivity[@name='Call Activity - Expanded']");
					{
						selectProcessOfCallActivity();
						navigateElement("bpmn:startEvent[@name='Start Event None 1']");
						navigateElement("bpmn:task[@name='Abstract Task 4']");
						navigateElement("bpmn:endEvent[@name='End Event None 2']");
						pop();
					}

					pop();
				}

				navigateElement("bpmn:exclusiveGateway[@name='Exclusive Gateway Convergence 1']");
				navigateElement("bpmn:endEvent[@name='End Event Message']",
						"messageEndEvent");

				navigateElement("bpmn:textAnnotation/bpmn:text[contains(text(), 'Text Annotation')]");
				navigateElement("bpmn:subProcess[@name='Collapsed Sub-Process']");
				navigateElement("bpmn:exclusiveGateway[@name='Exclusive Gateway Convergence 2']");
				navigateElement("bpmn:endEvent[@name='End Event Terminate']",
						"terminateEvent");
				navigateElement("bpmn:serviceTask[@name='Service Task 7']");
				navigateElement("bpmn:dataObjectReference[@name='Data Object']");
				navigateElement("bpmn:dataStoreReference[@name='Data Store Reference']");
				navigateElement("//bpmn:categoryValue[@value='Group']");

				{
					selectElement("bpmn:subProcess[@name='Sub Process - Expanded']");

					navigateElement("bpmn:startEvent[@name='Start Event None 2']");
					navigateElement("bpmn:task[@name='Abstract Task 6']");
					navigateElement("bpmn:endEvent[@name='End Event None 3']");

					pop();
				}

				pop();
			}

			pop();
		}

	}

}
