package org.omg.bpmn.miwg.xpathTestRunner.tests;

import org.omg.bpmn.miwg.xpathTestRunner.testBase.AbstractXpathTest;

public class A_1_0_Test extends AbstractXpathTest {

	@Override
	public String getName() {
		return "A.1.0";
	}

    @Override
    protected void execute() throws Throwable {
        selectElementX("/bpmn:definitions/bpmn:process");

        navigateElement("bpmn:startEvent", "Start Event");

        navigateFollowingElement("bpmn:task", "Task 1");
       
        navigateFollowingElement("bpmn:task", "Task 2");

        navigateFollowingElement("bpmn:task", "Task 3");

        navigateFollowingElement("bpmn:endEvent", "End Event");

        pop();
    }

}
