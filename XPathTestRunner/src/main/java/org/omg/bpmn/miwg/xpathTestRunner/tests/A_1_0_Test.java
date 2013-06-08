package org.omg.bpmn.miwg.xpathTestRunner.tests;

import org.omg.bpmn.miwg.xpathTestRunner.base.TestInstance;
import org.omg.bpmn.miwg.xpathTestRunner.testBase.AbstractXpathTest;

public class A_1_0_Test extends AbstractXpathTest {

	@Override
	public String getName() {
		return "A.1.0";
	}

	@Override
	public void execute(TestInstance instance) throws Throwable {

		{
			loadFile(instance.getFile());

			selectElementX("/bpmn:definitions/bpmn:process");
			
			navigateElement("bpmn:startEvent", "Start Event");
			
			navigateFollowingElement("bpmn:task", "Task 1");
			
			navigateFollowingElement("bpmn:task", "Task 2");
			
			navigateFollowingElement("bpmn:task", "Task 3");
			
			navigateFollowingElement("bpmn:endEvent", "End Event");

			pop();
			
			instance.addFindings(resultsFinding());
			instance.addOK(resultsOK());

		}
	}

}
