package bpmnChecker.tests;

import java.io.File;

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

			navigateElement(
					"bpmn:messageFlow[@name='Message Flow 1']");
			navigateElement(
					"bpmn:messageFlow[@name='Message Flow 2']");

			{
				selectProcess(
						"//bpmn:process[@id=//bpmn:participant[@name='Participant']/@processRef]");
				navigateElement(
						"bpmn:startEvent[@name='Start Event 1 Timer']",
						"timerStartEvent");
				navigateElement("bpmn:task[@name='Abstract Task 1']");
				navigateElement("bpmn:dataObjectReference[@name='Data Object']");
				pop();
			}

			pop();
		}

	}

}
