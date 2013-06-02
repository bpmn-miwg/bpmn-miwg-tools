package org.omg.bpmn.miwg.xpathTestRunner.tests;

import java.io.File;

import org.omg.bpmn.miwg.xpathTestRunner.testBase.AbstractXpathTest;



public class B_1_0_Test extends AbstractXpathTest {

	@Override
	public String getName() {
		return "B.1.0";
	}

	@Override
	public boolean isApplicable(File file) {
		String fn = file.getName();
		return fn.equals("B.1.0-export.bpmn")
				|| fn.equals("B.1.0-roundtrip.bpmn") || fn.equals("B.1.0.bpmn");
	}

	@Override
	public void execute(File file) throws Throwable {

		{
			loadFile(file);

			selectElement("//bpmn:collaboration");

			navigateElementX("bpmn:messageFlow[@name='Message Flow 1']");
			navigateElementX("bpmn:messageFlow[@name='Message Flow 2']");

			{
				selectProcess("//bpmn:process[@id=//bpmn:participant[@name='Participant']/@processRef]");
				navigateElementX("bpmn:startEvent[@name='Start Event Timer']");
				checkTimerEventL1();
				navigateElementX("bpmn:userTask[@name='User Task 2']");
				navigateElementX("bpmn:serviceTask[@name='Service Task 3']");
				navigateElementX("bpmn:endEvent[@name='End Event None 1']");
				pop();
			}

			{
				selectProcess("//bpmn:process[@id=//bpmn:participant[@name='Pool']/@processRef]");
				navigateElementX(".//bpmn:lane[@name='Lane 1']");
				navigateElementX(".//bpmn:lane[@name='Lane 2']");
				navigateElementX("bpmn:startEvent[@name='Start Event Message']");
				checkMessageEventL1();
				navigateElementX("bpmn:parallelGateway[@name='Parallel Gateway Divergence']");
				navigateElementX("bpmn:exclusiveGateway[@name='Exclusive Gateway Divergence 1']");
				navigateElementX("bpmn:exclusiveGateway[@name='Exclusive Gateway Divergence 2']");
				navigateElementX("bpmn:callActivity[@name='Call Activity Collapsed']");

				{
					selectElement("bpmn:callActivity[@name='Call Activity - Expanded']");
					{
						selectProcessOfCallActivity();
						navigateElementX("bpmn:startEvent[@name='Start Event None 1']");
						navigateElementX("bpmn:task[@name='Abstract Task 4']");
						navigateElementX("bpmn:endEvent[@name='End Event None 2']");
						pop();
					}

					pop();
				}

				navigateElementX("bpmn:exclusiveGateway[@name='Exclusive Gateway Convergence 1']");
				navigateElementX("bpmn:endEvent[@name='End Event Message']");
				checkMessageEventL1();

				navigateElementX("bpmn:textAnnotation/bpmn:text[contains(text(), 'Text Annotation')]");
				navigateElementX("bpmn:subProcess[@name='Collapsed Sub-Process']");
				navigateElementX("bpmn:exclusiveGateway[@name='Exclusive Gateway Convergence 2']");
				navigateElementX("bpmn:endEvent[@name='End Event Terminate']");
				checkTerminateEventL1();
				
				navigateElementX("bpmn:serviceTask[@name='Service Task 7']");
				navigateElementX("bpmn:dataObjectReference[@name='Data Object']");
				navigateElementX("bpmn:dataStoreReference[@name='Data Store Reference']");
				navigateElementX("//bpmn:categoryValue[@value='Group']");

				{
					selectElement("bpmn:subProcess[@name='Sub Process - Expanded']");

					navigateElementX("bpmn:startEvent[@name='Start Event None 2']");
					navigateElementX("bpmn:task[@name='Abstract Task 6']");
					navigateElementX("bpmn:endEvent[@name='End Event None 3']");

					pop();
				}

				pop();
			}

			pop();
		}

	}

}
