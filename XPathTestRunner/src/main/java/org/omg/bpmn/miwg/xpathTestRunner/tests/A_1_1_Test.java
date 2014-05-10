package org.omg.bpmn.miwg.xpathTestRunner.tests;

import org.omg.bpmn.miwg.xpathTestRunner.testBase.OwnerType;


public class A_1_1_Test extends A_1_0_Test {

	@Override
	public String getName() {
		return "A.1.1";
	}

    @Override
    protected void execute() throws Throwable {
        selectElementX("/bpmn:definitions/bpmn:process");

        navigateElement("bpmn:startEvent", "Start Event");

        navigateFollowingElement("bpmn:userTask", "Task 1");
        checkOwner(OwnerType.PotentialOwner, "Performer 1");       
        
        navigateFollowingElement("bpmn:serviceTask", "Task 2");
        checkOperation("Operation 1");

        navigateFollowingElement("bpmn:userTask", "Task 3");
        checkOwner(OwnerType.PotentialOwner, "Performer 2");      

        navigateFollowingElement("bpmn:endEvent", "End Event");

        pop();
    }	
	
}
